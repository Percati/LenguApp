#!/usr/bin/env python3
"""
sincronizar_audio_estado.py — registra en audio-estado.json los audios (.ogg o .wav) que estan
en assets/audio/ pero que ninguna entrada del estado nombra.

Empareja por la convencion de nombre de los audios grabados:
    IDIOMA_NIVEL_<primeras 6 palabras del texto>_<origen>[_NN].ogg
contra los textos del contenido (misma recoleccion que exportar_audio_maestro).
Si el nombre coincide con el texto completo de un item se prefiere ese; si
no, el item cuyas primeras palabras coinciden.

El estado esta indexado por idioma|tipo|texto, SIN nivel. Cuando un texto
existe en dos niveles y se grabo una vez por nivel, el segundo archivo no
tiene donde ir en "archivo". Se guarda en la lista "otrosArchivos" de esa
misma entrada: el exportador y el importador la ignoran, asi que el cambio
es compatible hacia atras.

Si un archivo no se puede emparejar sin ambiguedad, se informa y NO se toca.

Tambien informa el caso inverso: archivos que el estado registra pero que no
estan en assets/audio/ (grabados y anotados en el Excel, sin subir al repo).

Uso:
    python3 sincronizar_audio_estado.py --estado ../audio-estado.json \\
        --contenido ../contenido --audio ../../app/src/main/assets/audio [--dry-run]
"""
import argparse, json, re, sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent))
from exportar_audio_maestro import recolectar, slug  # noqa: E402

PATRON = re.compile(r"^([A-Z]{2})_([A-C][12])_(.+)_((?:DE|EN)-[A-Z]\d+|T\d+)(?:_\d+)?$")


def main():
    sys.stdout.reconfigure(encoding="utf-8")
    ap = argparse.ArgumentParser()
    ap.add_argument("--estado", default="../audio-estado.json")
    ap.add_argument("--contenido", default="../contenido")
    ap.add_argument("--audio", default="../../app/src/main/assets/audio")
    ap.add_argument("--dry-run", action="store_true")
    a = ap.parse_args()

    estado_path = Path(a.estado)
    estado = json.loads(estado_path.read_text(encoding="utf-8"))
    registrados = set()
    for v in estado.values():
        registrados.add(Path(v.get("archivo", "")).stem)
        registrados.update(Path(f).stem for f in v.get("otrosArchivos", []))

    datos = recolectar(Path(a.contenido))
    items = [(idi, niv, tipo, tx) for idi in datos for niv in datos[idi]
             for tipo, tx, _ in datos[idi][niv]]

    nuevos = sin_par = 0
    for wav in sorted(p for p in Path(a.audio).rglob("*") if p.suffix in (".ogg", ".wav")):
        if wav.stem in registrados:
            continue
        m = PATRON.match(wav.stem)
        if not m:
            print(f"  ? nombre fuera de convencion: {wav.name}")
            sin_par += 1
            continue
        idi, niv, palabras, origen = m.group(1).lower(), m.group(2), m.group(3), m.group(4)
        # origen T## = pack de vocabulario; origen de skill = ejemplo o expresion
        del_tipo = [x for x in items if x[0] == idi
                    and (x[2] == "Vocabulario") == origen.startswith("T")]
        cand = [x for x in del_tipo if slug(x[3], 99) == palabras]
        if not cand:
            cand = [x for x in del_tipo if slug(x[3]) == palabras]
        mismo_nivel = [x for x in cand if x[1] == niv]
        cand = mismo_nivel or cand
        claves = sorted({f"{x[0]}|{x[2]}|{x[3]}" for x in cand})
        if len(claves) != 1:
            print(f"  ? {wav.name}: {len(claves)} candidatos {claves[:3]}")
            sin_par += 1
            continue
        k = claves[0]
        e = estado.get(k)
        if isinstance(e, dict) and e.get("archivo"):
            e.setdefault("otrosArchivos", []).append(wav.name)
            destino = "otrosArchivos"
        else:
            estado[k] = {"grabado": True, "archivo": wav.name}
            destino = "archivo"
        print(f"  + {wav.name} -> {k} ({destino})")
        nuevos += 1

    en_disco = {w.name for w in Path(a.audio).rglob("*") if w.suffix in (".ogg", ".wav")}
    faltan = sorted(f for v in estado.values() if isinstance(v, dict)
                    for f in [v.get("archivo", "")] + v.get("otrosArchivos", [])
                    if f and f not in en_disco)
    for f in faltan[:20]:
        print(f"  - registrado pero ausente en el repo: {f}")
    if len(faltan) > 20:
        print(f"  - ... y {len(faltan) - 20} mas")
    print(f"{len(faltan)} archivos registrados que faltan en {a.audio}")

    if not a.dry_run:
        estado_path.write_text(json.dumps(estado, ensure_ascii=False, indent=1), encoding="utf-8")
    print(f"{nuevos} archivos registrados, {sin_par} sin emparejar"
          + (" (dry-run, nada escrito)" if a.dry_run else ""))


if __name__ == "__main__":
    main()
