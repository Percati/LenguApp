#!/usr/bin/env python3
"""
convertir_audio.py — pasa los .wav de assets/audio a Opus en contenedor .ogg.

Formato final decidido (sept 2026): **Opus, 24 kbps, mono, contenedor .ogg**.
  - Opus rinde muy por encima de AAC y MP3 en voz a bitrates bajos.
  - Android y ExoPlayer lo soportan de fabrica, sin librerias extra. El
    contenedor .ogg anda desde API 21; la extension .opus recien desde la 29,
    por eso .ogg.
  - 24 kbps mono deja cada clip en ~2 KB (el WAV original pesa ~73 KB).

El nombre no cambia: solo la extension. `--estado` reescribe los nombres
guardados en audio-estado.json (campos `archivo` y `otrosArchivos`) para que
apunten al .ogg.

Es idempotente: salta los .ogg que ya existen, asi que se puede cortar y
retomar. Sin --borrar-wav deja los originales donde estan.

Uso:
    python3 convertir_audio.py --audio ../../app/src/main/assets/audio \\
        --estado ../audio-estado.json --borrar-wav
    python3 convertir_audio.py --audio ... --dry-run
"""
import argparse, json, shutil, subprocess, sys
from pathlib import Path

BITRATE = "24k"


def convertir(wav, ogg):
    subprocess.run(["ffmpeg", "-v", "error", "-y", "-i", str(wav),
                    "-c:a", "libopus", "-b:a", BITRATE, "-ac", "1",
                    "-application", "voip", str(ogg)], check=True)


def main():
    sys.stdout.reconfigure(encoding="utf-8")
    ap = argparse.ArgumentParser()
    ap.add_argument("--audio", default="../../app/src/main/assets/audio")
    ap.add_argument("--estado")
    ap.add_argument("--borrar-wav", action="store_true")
    ap.add_argument("--dry-run", action="store_true")
    a = ap.parse_args()

    if not a.dry_run and not shutil.which("ffmpeg"):
        sys.exit("ffmpeg no esta instalado.")

    base = Path(a.audio)
    wavs = sorted(base.rglob("*.wav"))
    hechos = saltados = 0
    bytes_wav = bytes_ogg = 0
    for wav in wavs:
        ogg = wav.with_suffix(".ogg")
        bytes_wav += wav.stat().st_size
        if ogg.exists():
            saltados += 1
        elif a.dry_run:
            hechos += 1
            continue
        else:
            convertir(wav, ogg)
            hechos += 1
        bytes_ogg += ogg.stat().st_size
        if a.borrar_wav and not a.dry_run:
            wav.unlink()

    mb = lambda n: f"{n / 1024 / 1024:.1f} MB"
    print(f"{len(wavs)} .wav ({mb(bytes_wav)}) -> {hechos} convertidos, "
          f"{saltados} ya estaban; .ogg: {mb(bytes_ogg)}"
          + (" [dry-run]" if a.dry_run else ""))

    if a.estado:
        p = Path(a.estado)
        estado = json.loads(p.read_text(encoding="utf-8"))
        cambiados = 0
        for v in estado.values():
            if not isinstance(v, dict):
                continue
            if v.get("archivo", "").endswith(".wav"):
                v["archivo"] = v["archivo"][:-4] + ".ogg"
                cambiados += 1
            otros = v.get("otrosArchivos")
            if otros:
                v["otrosArchivos"] = [f[:-4] + ".ogg" if f.endswith(".wav") else f
                                      for f in otros]
                cambiados += sum(1 for f in otros if f.endswith(".wav"))
        if not a.dry_run:
            p.write_text(json.dumps(estado, ensure_ascii=False, indent=1), encoding="utf-8")
        print(f"{cambiados} nombres de {p.name} ahora apuntan a .ogg"
              + (" [dry-run]" if a.dry_run else ""))


if __name__ == "__main__":
    main()
