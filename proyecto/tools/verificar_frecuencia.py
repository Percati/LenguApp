#!/usr/bin/env python3
"""Verifica los packs por banda de frecuencia, no por lista de nivel.

Pensado para los niveles altos de alemán (B2, C1, C2), donde no existe lista
oficial de vocabulario: en vez de preguntar "¿está esta palabra en una lista C2?"
pregunta "¿es lo bastante infrecuente como para ser de nivel alto?".

Fuente: A Frequency Dictionary of German (Jones/Tschirner, Routledge), los 5000
lemas más frecuentes del alemán con su rango. El PDF no se versiona (copyright);
se espera en fuentes/A_Frequency_Dictionary_of_German.pdf, igual que el resto de
las fuentes. Del libro solo se usa el rango, en memoria, nunca se escribe a disco.

Bandas:
  rango 1-1000     nucleo del idioma        -> demasiado comun para C1/C2
  rango 1001-2500  frecuente                -> justo para C1/C2
  rango 2501-5000  poco frecuente           -> coherente con nivel alto
  fuera del top 5000                        -> raro o compuesto, coherente con C2

Uso:  python3 proyecto/tools/verificar_frecuencia.py de-C2-2027 [--umbral 1000]
Sale con codigo 1 si hay items por debajo del umbral (demasiado comunes).
"""
import argparse, glob, json, os, re, subprocess, sys

sys.stdout.reconfigure(encoding="utf-8")
BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DICC = os.path.join(BASE, "fuentes", "A_Frequency_Dictionary_of_German.pdf")
PATRON = re.compile(r"(?:^|\s{2,})(\d{1,4})\s+([A-Za-zÄÖÜäöüß][A-Za-zÄÖÜäöüß\-]{1,})(?=\s)", re.M)


def rangos():
    if not os.path.exists(DICC):
        sys.exit(f"Falta {DICC} (no se versiona; ver tools/fuentes.json)")
    texto = subprocess.run(["pdftotext", "-layout", DICC, "-"], capture_output=True, text=True).stdout
    r, vistos = {}, set()
    for m in PATRON.finditer(texto):
        n, w = int(m.group(1)), m.group(2).lower()
        if 1 <= n <= 5000 and n not in vistos:
            r[w] = n
            vistos.add(n)
    return r


def nucleo(s):
    s = re.sub(r"\(.*?\)", "", s).strip().lower()
    return re.sub(r"^(der|die|das)\s+", "", s)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("prefijo", help="p.ej. de-C2-2027")
    ap.add_argument("--umbral", type=int, default=1000,
                    help="rango por debajo del cual un item se considera demasiado comun (default 1000)")
    args = ap.parse_args()

    r = rangos()
    bandas = {"1-1000 demasiado comun": 0, "1001-2500 frecuente": 0,
              "2501-5000 poco frecuente": 0, "fuera del top 5000": 0, "expresion": 0}
    alertas = []
    for ruta in sorted(glob.glob(os.path.join(BASE, "contenido", "packs", f"{args.prefijo}-*.json"))):
        with open(ruta, encoding="utf-8") as f:
            d = json.load(f)
        for it in d["vocabulario"]:
            s = nucleo(it["item"])
            if " " in s:
                bandas["expresion"] += 1
                continue
            n = r.get(s)
            if n is None:
                bandas["fuera del top 5000"] += 1
            elif n <= args.umbral:
                bandas["1-1000 demasiado comun"] += 1
                alertas.append((f"{d['topicId']}-{d['n']}", it["item"], n))
            elif n <= 2500:
                bandas["1001-2500 frecuente"] += 1
            else:
                bandas["2501-5000 poco frecuente"] += 1

    print(f"Diccionario: {len(r)} lemas con rango")
    for k, v in bandas.items():
        print(f"  {v:5d}  {k}")
    for pack, item, n in sorted(alertas, key=lambda x: x[2]):
        print(f"ALERTA  {pack}: {item} (rango {n})")
    print(f"Alertas: {len(alertas)}")
    sys.exit(1 if alertas else 0)


if __name__ == "__main__":
    main()
