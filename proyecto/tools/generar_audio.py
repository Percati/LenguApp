#!/usr/bin/env python3
"""
generar_audio.py — genera los MP3 de las fichas con Piper, en tiempo de compilacion.

El telefono NUNCA genera ni descarga audio: los archivos se empaquetan en el
APK. Este script corre en la maquina de quien compila.

Requisitos (una vez):
    pip install piper-tts
    # descargar las voces en voces/ desde
    # https://huggingface.co/rhasspy/piper-voices
    # muestras: https://rhasspy.github.io/piper-samples/

Uso:
    python3 generar_audio.py ../build/*.json --voces voces --salida ../assets/audio
    python3 generar_audio.py ../build/*.json --dry-run     # solo listar lo que falta

Para cada frase marcada se generan dos archivos: velocidad normal y 80 %
(shadowing). El nombre es determinista (hash del texto + voz + velocidad), asi
que regenerar solo produce lo que falta.
"""
import argparse, hashlib, json, shutil, subprocess, sys
from pathlib import Path

VOCES = {
    "en": ["en_US-ryan-high", "en_US-lessac-high"],   # el usuario elige
    "de": ["de_DE-thorsten-high"],
    "es": ["es_AR-daniela-high"],
    "it": ["it_IT-serena-high"],
    "pt": ["pt_BR-faber-medium"],
    "fr": ["fr_FR-upmc-medium"],
}
VELOCIDADES = {"normal": 1.0, "lento": 1.25}   # length_scale: mayor = mas lento

def nombre(texto, voz, vel):
    h = hashlib.sha1(f"{texto}|{voz}|{vel}".encode()).hexdigest()[:12]
    return f"{voz}_{vel}_{h}.mp3"

def frases(ficha):
    """Solo los ejemplos marcados con audio: true."""
    return [e["texto"] for e in ficha.get("ejemplos", []) if e.get("audio")]

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fichas", nargs="+")
    ap.add_argument("--voces", default="voces")
    ap.add_argument("--salida", default="../assets/audio")
    ap.add_argument("--dry-run", action="store_true")
    a = ap.parse_args()

    tiene_piper = shutil.which("piper") is not None
    tiene_ffmpeg = shutil.which("ffmpeg") is not None
    if not a.dry_run and not tiene_piper:
        print("piper no esta instalado. `pip install piper-tts`, o usar --dry-run.",
              file=sys.stderr)
        sys.exit(2)

    Path(a.salida).mkdir(parents=True, exist_ok=True)
    indice, pendientes, hechos = {}, 0, 0

    for f in a.fichas:
        ficha = json.loads(Path(f).read_text())
        idioma = ficha.get("idioma")
        if not idioma or idioma not in VOCES:
            continue
        for texto in frases(ficha):
            for voz in VOCES[idioma]:
                for vel, escala in VELOCIDADES.items():
                    dest = Path(a.salida, nombre(texto, voz, vel))
                    indice.setdefault(ficha["id"], []).append(
                        {"texto": texto, "voz": voz, "velocidad": vel,
                         "archivo": dest.name})
                    if dest.exists():
                        continue
                    pendientes += 1
                    if a.dry_run:
                        continue
                    modelo = Path(a.voces, voz + ".onnx")
                    if not modelo.exists():
                        print(f"  ! falta la voz {modelo}", file=sys.stderr)
                        continue
                    wav = dest.with_suffix(".wav")
                    subprocess.run(["piper", "--model", str(modelo),
                                    "--length_scale", str(escala),
                                    "--output_file", str(wav)],
                                   input=texto, text=True, check=True)
                    if tiene_ffmpeg:
                        subprocess.run(["ffmpeg", "-y", "-loglevel", "error",
                                        "-i", str(wav), "-ac", "1", "-b:a", "48k",
                                        str(dest)], check=True)
                        wav.unlink()
                    hechos += 1

    Path(a.salida, "indice.json").write_text(
        json.dumps(indice, ensure_ascii=False, indent=1))
    total = sum(len(v) for v in indice.values())
    print(f"{total} clips referenciados por {len(indice)} fichas")
    if a.dry_run:
        print(f"{pendientes} por generar. Peso estimado: ~{pendientes * 25 // 1024} MB")
        if not tiene_ffmpeg:
            print("Nota: sin ffmpeg el audio queda en WAV (unas 10 veces mas pesado).")
    else:
        print(f"{hechos} generados, {total - pendientes} ya existian")

if __name__ == "__main__":
    main()
