#!/usr/bin/env python3
"""Indice de audio para la app (solo lectura sobre `proyecto/audio-estado.json`).

Los MP3/OGG ya estan en `app/src/main/assets/audio/{DE,EN}/`, y `audio-estado.json`
sabe que archivo corresponde a cada texto. Pero ese archivo es de trabajo y la app no
lo lee: por eso el audio nunca se pudo reproducir. Este script emite el indice que SI
viaja en los assets:

    app/src/main/assets/audio/indice.json

Forma: {"de": {"Vocabulario": {"<texto>": "<archivo.ogg>", ...}, "Expresión": {...},
"Ejemplo": {...}}, "en": {...}}

El texto es la clave EXACTA del contenido (el item de vocabulario, la expresion del
redemittel o el texto del ejemplo, en el idioma que se aprende). La app busca por ese
texto; si no esta, no hay audio y no se dibuja el boton.

    python3 -I proyecto/tools/indice_audio.py
    python3 -I proyecto/tools/indice_audio.py --comprobar   # no escribe, solo informa
"""
import argparse, json, os
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[2]
ESTADO = RAIZ / "proyecto" / "audio-estado.json"
DESTINO = RAIZ / "app" / "src" / "main" / "assets" / "audio" / "indice.json"
AUDIO = RAIZ / "app" / "src" / "main" / "assets" / "audio"


def construir():
    estado = json.loads(ESTADO.read_text(encoding="utf-8"))
    indice = defaultdict(lambda: defaultdict(dict))
    faltan, sin_grabar = [], 0
    for clave, dato in estado.items():
        idioma, clase, texto = clave.split("|", 2)
        if not dato.get("grabado"):
            sin_grabar += 1
            continue
        archivo = dato.get("archivo")
        if not archivo:
            continue
        if not (AUDIO / idioma.upper() / archivo).is_file():
            faltan.append(archivo)
            continue
        indice[idioma][clase][texto] = archivo
    return indice, faltan, sin_grabar


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--comprobar", action="store_true", help="no escribe: informa y devuelve 1 si hay diferencias")
    a = ap.parse_args()

    indice, faltan, sin_grabar = construir()
    total = sum(len(t) for i in indice.values() for t in i.values())
    salida = json.dumps({i: {c: dict(sorted(t.items())) for c, t in sorted(cl.items())}
                         for i, cl in sorted(indice.items())}, ensure_ascii=False, indent=1, sort_keys=False)

    huerfanos = []
    nombrados = {ar for cl in indice.values() for t in cl.values() for ar in t.values()}
    for carpeta in ("DE", "EN"):
        d = AUDIO / carpeta
        if d.is_dir():
            huerfanos += [f.name for f in d.iterdir() if f.suffix == ".ogg" and f.name not in nombrados]

    print(f"{total} textos con audio; sin grabar {sin_grabar}; archivos que faltan en assets {len(faltan)}; "
          f"ogg sin entrada en el indice {len(huerfanos)}")
    for f in faltan[:10]:
        print("  falta:", f)
    for f in huerfanos[:10]:
        print("  huerfano:", f)

    if a.comprobar:
        actual = DESTINO.read_text(encoding="utf-8") if DESTINO.is_file() else ""
        igual = actual == salida + "\n"
        print("indice al dia" if igual else "el indice esta desactualizado")
        raise SystemExit(0 if igual else 1)

    DESTINO.parent.mkdir(parents=True, exist_ok=True)
    DESTINO.write_text(salida + "\n", encoding="utf-8")
    print("escrito", DESTINO.relative_to(RAIZ), f"({DESTINO.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
