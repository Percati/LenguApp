#!/usr/bin/env python3
"""Datos para decidir si la busqueda alcanza como «familia» de verbos separables (prompt 20, puntos 8 y 9).

  python3 -I proyecto/tools/familias_trennbar.py --contenido proyecto/contenido [--detalle]

(8) Familias: para cada raiz de un trennbar (columna `raiz` de contenido/verbos/de.json) cuenta los verbos DE
    del universo de la vista cuyo infinitivo es la raiz o TERMINA en ella (lo que devolveria buscar la raiz
    como texto: «machen» -> machen, mitmachen, weitermachen). Una familia tiene 2 o mas miembros distintos.
(9) Ejemplos con el verbo separado: de los trennbar, cuantos tienen en `ejemplos[].texto` de algun nucleo DE una
    oracion con una forma conjugada de la raiz y, despues, el prefijo suelto al final de la clausula
    (heuristica por cadenas; con --detalle imprime cada acierto para revisarlo a ojo). NO crea contenido.
"""
import argparse, json, re, sys, unicodedata
from collections import defaultdict
from difflib import SequenceMatcher
from pathlib import Path


def sin_um(s):
    return unicodedata.normalize("NFC", s).lower().replace("ä", "a").replace("ö", "o").replace("ü", "u").replace("ß", "ss")


# Presentes y pasados con cambio de vocal de las raices frecuentes (sin umlaut): nimmt, gibt, fuhr...
IRREGULARES = {"nehmen": ("nimm", "nahm"), "geben": ("gib", "gab"), "sprechen": ("sprich", "sprach"),
               "lesen": ("lies", "las"), "sehen": ("sieh", "sah"), "fahren": ("fahr", "fuhr"), "laufen": ("lauf", "lief"),
               "halten": ("halt", "hielt"), "lassen": ("lass", "liess"), "tragen": ("trag", "trug"),
               "schlafen": ("schlaf", "schlief"), "ziehen": ("zieh", "zog"), "bringen": ("bring", "brachte"),
               "kommen": ("komm", "kam"), "gehen": ("geh", "ging"), "stehen": ("steh", "stand"), "treffen": ("triff", "traf"),
               "fallen": ("fall", "fiel"), "tun": ("tu", "tat"), "bleiben": ("bleib", "blieb"), "schreiben": ("schreib", "schrieb"),
               "rufen": ("ruf",), "steigen": ("steig", "stieg"), "werfen": ("wirf", "warf"), "finden": ("find", "fand"),
               "schneiden": ("schneid", "schnitt"), "denken": ("denk", "dach"), "laden": ("lad", "lud")}


INSEPARABLES = {"be", "ge", "er", "ver", "ent", "emp", "zer", "miss", "über", "unter", "um", "durch", "hinter", "wider", "wieder", "voll"}


def textos_ejemplos(base):
    for p in sorted(Path(base, "nucleos").glob("DE-*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        for e in d.get("ejemplos", []):
            t = e["texto"]
            t = t.get("de") if isinstance(t, dict) else t
            if t:
                yield p.stem, t.replace("*", "")


def separado(oracion, prefijo, raiz):
    """True si hay una forma conjugada de `raiz` y luego `prefijo` suelto al final de la clausula."""
    toks = oracion.split()
    r = sin_um(raiz)
    stem = r[:-2] if r.endswith("en") else r[:-1]
    for i, t in enumerate(toks):
        w = sin_um(t.strip(".,;:!?«»\"'()"))
        if not w or w == r:
            continue
        irr = IRREGULARES.get(r, ())
        if not (w.startswith(stem[:min(len(stem), 4)]) or any(w.startswith(x) for x in irr)
                or (w[:2] == stem[:2] and SequenceMatcher(None, w, stem).ratio() >= 0.78)):
            continue
        for u in toks[i + 1:i + 9]:
            if sin_um(u.strip(".,;:!?«»\"'()")) == sin_um(prefijo) and (u[-1] in ".,;:!?" or u is toks[-1]):
                return True
    return False


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--contenido", default="proyecto/contenido")
    ap.add_argument("--detalle", action="store_true")
    a = ap.parse_args()
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    de = json.loads(Path(a.contenido, "verbos", "de.json").read_text(encoding="utf-8"))["verbos"]
    nucleos = {}
    for e in de:
        n = re.sub(r"^sich ", "", e["base"])
        nucleos.setdefault(n, []).append(e)
    tren = {n: es for n, es in nucleos.items() if any("trennbar" in e["claseVerbo"] for e in es)}
    raiz_de = {}
    for n, es in tren.items():
        for e in es:
            if "trennbar" in e["claseVerbo"]:
                raiz_de[n] = e["raiz"]
    prefijos = {n[:len(n) - r_] for n, r_ in ((n, len(r)) for n, r in raiz_de.items())} | INSEPARABLES
    fam, fam_texto = defaultdict(set), defaultdict(set)
    for n, r in raiz_de.items():
        for m in nucleos:
            if m.endswith(r):
                fam_texto[r].add(m)                     # lo que devolveria la busqueda por texto
                if m == r or m[:-len(r)] in prefijos:    # familia real: raiz + prefijo
                    fam[r].add(m)
    grandes = {r: ms for r, ms in fam.items() if len(ms) >= 2}
    con_otro = [n for n, r in raiz_de.items() if len(fam[r] - {n}) >= 1]
    print("## Familias de raiz (busqueda por texto)\n")
    print(f"- formas trennbar: {sum(1 for e in de if 'trennbar' in e['claseVerbo'])}; verbos trennbar distintos (infinitivo): {len(raiz_de)}; raices distintas: {len(fam)}")
    print(f"- trennbar con al menos otro verbo de la misma raiz en el contenido: {len(con_otro)} de {len(raiz_de)}")
    print(f"- familias de 2 o mas miembros: {len(grandes)} (miembros en total: {len(set().union(*grandes.values())) if grandes else 0})")
    print(f"- por busqueda de texto (cualquier verbo que TERMINE en la raiz, incl. falsos amigos como schalten/halten): "
          f"{sum(1 for r, ms in fam_texto.items() if len(ms) >= 2)} raices con 2 o mas coincidencias")
    print("- las 10 mas grandes (familia real = raiz + prefijo):")
    for r, ms in sorted(grandes.items(), key=lambda x: (-len(x[1]), x[0]))[:10]:
        print(f"  - {r} ({len(ms)}): " + ", ".join(sorted(ms)))
    textos = list(textos_ejemplos(a.contenido))
    con_ej, sin_ej = [], []
    for n in sorted(raiz_de):
        pref = n[:len(n) - len(raiz_de[n])]
        hit = next(((src, t) for src, t in textos if separado(t, pref, raiz_de[n])), None)
        (con_ej if hit else sin_ej).append((n, hit))
    print("\n## Ejemplos con el verbo separado (ejemplos[] de los nucleos DE)\n")
    print(f"- trennbar (verbos distintos) con un ejemplo que lo muestra separado: {len(con_ej)} de {len(raiz_de)}")
    print(f"- sin ejemplo: {len(sin_ej)}")
    if a.detalle:
        for n, (src, t) in con_ej:
            print(f"  + {n}: [{src}] {t}")


if __name__ == "__main__":
    main()
