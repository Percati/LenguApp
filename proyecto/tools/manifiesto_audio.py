#!/usr/bin/env python3
"""
manifiesto_audio.py — lista todos los textos que necesitan audio.

Produce dos salidas:

  audio-manifiesto.md    legible, para revisar antes de generar
  audio-manifiesto.json  entrada de generar_audio.py

Cada texto lleva su idioma explicito y el nombre de archivo determinista que
le va a corresponder, de modo que el manifiesto y los MP3 se puedan cotejar.

Se incluyen tres clases de texto:

  ejemplo      frases marcadas con "audio": true en la ficha
  vocabulario  cada item del Themenwortschatz
  redemittel   cada expresion

El idioma es siempre el de la ficha: se pronuncia la lengua que se aprende,
nunca la glosa. Por eso la voz se deduce del campo idioma y no hay que
declararla a mano.

Uso:
    python3 manifiesto_audio.py ../build/*.json --salida ..
"""
import argparse, hashlib, json, sys
from pathlib import Path
from collections import defaultdict

VOCES = {
    "en": ["en_US-ryan-high", "en_US-lessac-high"],
    "de": ["de_DE-thorsten-high"],
    "es": ["es_AR-daniela-high"],
    "it": ["it_IT-serena-high"],
    "pt": ["pt_BR-faber-medium"],
    "fr": ["fr_FR-upmc-medium"],
}
VELOCIDADES = {"normal": 1.0, "lento": 1.25}
IDIOMA_NOMBRE = {"en": "Inglés", "de": "Alemán", "es": "Español",
                 "it": "Italiano", "pt": "Portugués", "fr": "Francés"}


def nombre(texto, voz, vel):
    h = hashlib.sha1(f"{texto}|{voz}|{vel}".encode()).hexdigest()[:12]
    return f"{voz}_{vel}_{h}.ogg"


def textos_de(ficha):
    """Devuelve (clase, texto) por cada elemento que necesita audio."""
    out = []
    for e in ficha.get("ejemplos", []):
        if e.get("audio"):
            out.append(("ejemplo", e["texto"]))
    for v in ficha.get("vocabulario", []):
        out.append(("vocabulario", v["item"]))
    for r in ficha.get("redemittel", []):
        out.append(("redemittel", r["expresion"]))
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fichas", nargs="+")
    ap.add_argument("--salida", default="..")
    a = ap.parse_args()

    por_idioma = defaultdict(lambda: defaultdict(list))
    manifiesto = []
    vistos = set()

    for f in sorted(a.fichas):
        d = json.loads(Path(f).read_text(encoding="utf-8"))
        if d.get("_tipo") == "semana_especial":
            continue          # las semanas especiales no traen texto a pronunciar
        idioma = d["idioma"]
        for clase, texto in textos_de(d):
            clave = (idioma, texto)
            if clave in vistos:
                continue      # el mismo texto puede repetirse entre fichas
            vistos.add(clave)
            por_idioma[idioma][clase].append((texto, d["id"]))
            for voz in VOCES[idioma]:
                for vel in VELOCIDADES:
                    manifiesto.append({
                        "idioma": idioma, "clase": clase, "texto": texto,
                        "ficha": d["id"], "voz": voz, "velocidad": vel,
                        "archivo": nombre(texto, voz, vel)})

    salida = Path(a.salida)
    salida.joinpath("audio-manifiesto.json").write_text(
        json.dumps(manifiesto, ensure_ascii=False, indent=1), encoding="utf-8")

    # --- version legible ---
    L = ["# Manifiesto de audio", "",
         "Textos que necesitan grabación, agrupados por idioma. **El idioma de "
         "cada texto es el que se aprende**, nunca el de la glosa: se pronuncia "
         "siempre en la lengua de la ficha.", "",
         "Los nombres de archivo son deterministas (hash del texto, la voz y la "
         "velocidad), así que regenerar solo produce lo que falta y el manifiesto "
         "se puede cotejar con los archivos existentes.", ""]
    tot_clips = 0
    for idioma in sorted(por_idioma):
        clases = por_idioma[idioma]
        n_tex = sum(len(v) for v in clases.values())
        voces = VOCES[idioma]
        clips = n_tex * len(voces) * len(VELOCIDADES)
        tot_clips += clips
        L += [f"## {IDIOMA_NOMBRE[idioma]} (`{idioma}`)", "",
              f"**Voz:** {', '.join('`'+v+'`' for v in voces)}"
              + ("  — dos voces seleccionables, se genera con las dos"
                 if len(voces) > 1 else ""), "",
              f"**Velocidades:** normal y 80 % (para shadowing)", "",
              f"**{n_tex} textos → {clips} clips** "
              f"({n_tex} × {len(voces)} voz/voces × 2 velocidades)", ""]
        for clase, titulo in (("ejemplo", "Frases de ejemplo"),
                              ("vocabulario", "Vocabulario"),
                              ("redemittel", "Expresiones")):
            filas = clases.get(clase, [])
            if not filas:
                continue
            L += [f"### {titulo} — {len(filas)}", "",
                  "| Texto | Ficha |", "|---|---|"]
            for texto, fid in filas:
                t = texto.replace("|", "\\|").replace("**", "")
                L.append(f"| {t} | `{fid}` |")
            L.append("")
    L += ["---", "",
          f"**Total: {len(vistos)} textos, {tot_clips} clips.** "
          f"A unos 25 KB por clip, alrededor de {tot_clips*25//1024} MB.", "",
          "## Cómo generarlos", "",
          "```sh", "pip install piper-tts",
          "# voces desde https://huggingface.co/rhasspy/piper-voices",
          "python3 tools/generar_audio.py build/*.json --voces voces --salida app/src/main/assets/audio",
          "```", "",
          "Salida: Opus 24 kbps mono en .ogg. El script salta los que ya existen, así que se puede correr en tandas."]
    salida.joinpath("audio-manifiesto.md").write_text("\n".join(L), encoding="utf-8")
    print(f"{len(vistos)} textos, {tot_clips} clips -> audio-manifiesto.md / .json",
          file=sys.stderr)


if __name__ == "__main__":
    main()
