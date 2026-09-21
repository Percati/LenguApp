#!/usr/bin/env python3
"""Valida los packs de vocabulario contra las reglas del proyecto.

Chequea, para cada pack de contenido/packs/:
  - `prioridad` de cada item dentro del enum de schema/ficha.schema.json
    ("nucleo" | "ampliacion"; NO "variante", que es otro campo: regionalismo, p.ej. "CH");
  - entre 10 y 18 items;
  - continuidad: entre packs n y n+1 del mismo idioma/nivel/año/tema,
    30-80 % de items compartidos (sobre el tamaño del pack n+1);
  - calco entre idiomas: >=60 % de coincidencia entre los items de un pack y las
    traducciones del pack del otro idioma (mismo nivel/tema/n) cuenta como problema
    de B2 en adelante; en A2/B1 solo se informa, porque el vocabulario núcleo de esos
    niveles es el mismo en cualquier idioma;
  - con --anio, solo se validan los packs de ese año, y se reporta la cobertura contra data/calendarios/{anio}-{idioma}-{nivel}.json.

Uso:  python3 proyecto/tools/validar_packs.py [--anio 2027]
Sale con código 1 si hay algún problema.
"""
import argparse, collections, glob, json, os, sys

sys.stdout.reconfigure(encoding="utf-8")
BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def cargar(ruta):
    with open(ruta, encoding="utf-8") as f:
        return json.load(f)


def enum_prioridad():
    encontrado = []

    def buscar(o):
        if isinstance(o, dict):
            p = o.get("prioridad")
            if isinstance(p, dict) and "enum" in p:
                encontrado.append(p["enum"])
            for v in o.values():
                buscar(v)
        elif isinstance(o, list):
            for v in o:
                buscar(v)

    buscar(cargar(os.path.join(BASE, "schema", "ficha.schema.json")))
    return set(encontrado[0])


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--anio", type=int, help="además, reportar cobertura contra los calendarios de ese año")
    args = ap.parse_args()

    validos = enum_prioridad()
    problemas = 0
    cadenas = collections.defaultdict(dict)
    for ruta in sorted(glob.glob(os.path.join(BASE, "contenido", "packs", "*.json"))):
        d = cargar(ruta)
        if args.anio and d["anio"] != args.anio:
            continue
        nombre = os.path.basename(ruta)
        voc = d["vocabulario"]
        if not 10 <= len(voc) <= 18:
            problemas += 1
            print(f"TAMAÑO  {nombre}: {len(voc)} items")
        for it in voc:
            if it.get("prioridad") not in validos:
                problemas += 1
                print(f"PRIORIDAD  {nombre}: {it.get('item')!r} -> {it.get('prioridad')!r}")
        clave = (d["idioma"], d["nivel"], d["anio"], d["topicId"])
        cadenas[clave][d["n"]] = {it["item"].strip().lower() for it in voc}

    # calco entre idiomas: items de un pack vs traducciones del pack del otro idioma
    # (mismo nivel, año, tema y n). >=60 % indica que uno se escribió traduciendo al otro.
    avisos_calco = 0
    por_clave = {}
    for ruta in sorted(glob.glob(os.path.join(BASE, "contenido", "packs", "*.json"))):
        d = cargar(ruta)
        if args.anio and d["anio"] != args.anio:
            continue
        por_clave[(d["idioma"], d["nivel"], d["anio"], d["topicId"], d["n"])] = d
    for (idi, niv, anio, top, n), d in sorted(por_clave.items()):
        otro = "de" if idi == "en" else "en"
        o = por_clave.get((otro, niv, anio, top, n))
        if not o:
            continue
        items = {it["item"].strip().lower() for it in o["vocabulario"]}
        trad = {it["traducciones"].get(otro, "").strip().lower() for it in d["vocabulario"]}
        pct = len(items & trad) / len(d["vocabulario"]) * 100
        if pct >= 60:
            if niv in ("A2", "B1"):
                # en niveles bajos el vocabulario núcleo es universal (bank, doctor, cash):
                # coincidir en conceptos es esperable y no es calco de estructura
                avisos_calco += 1
            else:
                problemas += 1
                print(f"CALCO  {idi}-{niv}-{anio}-{top}-{n} vs {otro}: {pct:.0f}%")

    if avisos_calco:
        print(f"Aviso: {avisos_calco} packs A2/B1 comparten >=60 % de conceptos con el otro idioma (esperable en niveles bajos; no bloquea)")

    transiciones = 0
    for clave, cadena in sorted(cadenas.items()):
        for n in sorted(cadena):
            if n + 1 in cadena:
                transiciones += 1
                a, b = cadena[n], cadena[n + 1]
                pct = len(a & b) / len(b) * 100
                if not 30 <= pct <= 80:
                    problemas += 1
                    print(f"CONTINUIDAD  {'-'.join(map(str, clave))} {n}->{n+1}: {pct:.0f}%")

    if args.anio:
        hechos = total = 0
        for cal in sorted(glob.glob(os.path.join(BASE, "data", "calendarios", f"{args.anio}-*.json"))):
            anio, idioma, nivel = os.path.basename(cal)[:-5].split("-")
            cuenta = collections.Counter()
            necesarios = []
            for semana in cargar(cal):
                t = semana.get("topicId")
                if t:
                    cuenta[t] += 1
                    necesarios.append(f"{idioma}-{nivel}-{anio}-{t}-{cuenta[t]}.json")
            h = sum(os.path.exists(os.path.join(BASE, "contenido", "packs", n)) for n in necesarios)
            hechos += h
            total += len(necesarios)
            print(f"{h}/{len(necesarios)} packs hechos en {idioma.upper()} {nivel}")
        print(f"Números: {hechos}/{total} packs")

    print(f"Transiciones revisadas: {transiciones}. Problemas: {problemas}")
    sys.exit(1 if problemas else 0)


if __name__ == "__main__":
    main()
