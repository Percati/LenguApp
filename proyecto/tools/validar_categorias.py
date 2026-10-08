#!/usr/bin/env python3
"""
validar_categorias.py -- comprueba el campo `categoriasUso` de los redemittel.

El schema declara `categoriasUso` como array de strings libre a proposito, y
delega la lista cerrada en la conversacion de Nucleos "por convencion". Una
convencion que no se verifica se degrada: con 33 nombres repartidos en miles de
arrays, el primer "Opinion" en vez de "Opinar" no lo caza nadie. Esta
herramienta es la que la hace cumplir.

Uso:
    python3 tools/validar_categorias.py
    python3 tools/validar_categorias.py --sin-categoria    # lista el residuo
"""
import argparse, json, sys, collections
from pathlib import Path

def _utf8_io():
    for f in (sys.stdout, sys.stderr):
        try: f.reconfigure(encoding="utf-8", errors="replace")
        except (AttributeError, ValueError): pass
_utf8_io()

RAIZ = Path(__file__).resolve().parent.parent

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--contenido", default=str(RAIZ / "contenido"))
    ap.add_argument("--taxonomia", default=str(RAIZ / "data" / "categorias-uso.json"))
    ap.add_argument("--sin-categoria", action="store_true")
    a = ap.parse_args()

    tax = json.loads(Path(a.taxonomia).read_text(encoding="utf-8"))
    comun = set(tax["funcionComunicativa"])
    gram = set(tax["patronGramatical"])
    validas = comun | gram

    tot = con = 0
    fallos = []
    sin = []
    uso = collections.Counter()
    for p in sorted(Path(a.contenido).joinpath("nucleos").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo") == "semana_especial":
            continue
        for i, r in enumerate(d.get("redemittel", [])):
            tot += 1
            if "categoriasUso" not in r:
                fallos.append(f"{p.name} redemittel[{i}]: falta categoriasUso")
                continue
            cs = r["categoriasUso"]
            if not isinstance(cs, list):
                fallos.append(f"{p.name} redemittel[{i}]: no es un array")
                continue
            if len(cs) != len(set(cs)):
                fallos.append(f"{p.name} redemittel[{i}]: categorias repetidas {cs}")
            for c in cs:
                if c not in validas:
                    fallos.append(f"{p.name} redemittel[{i}]: '{c}' no esta en la taxonomia")
                else:
                    uso[c] += 1
            if cs:
                con += 1
            else:
                sin.append((d["skillId"], d["nivel"], r["expresion"][:50]))

    for f in fallos:
        print("ERROR:", f, file=sys.stderr)
    print(f"Numeros: {con}/{tot} expresiones tageadas, {len(sin)} sin categoria clara")
    nunca = sorted(validas - set(uso))
    if nunca:
        print(f"  aviso: {len(nunca)} categorias de la taxonomia no se usan nunca: {nunca}")
    if a.sin_categoria:
        print("\n--- sin categoria clara ---")
        for sk, nv, ex in sin:
            print(f"  {sk}-{nv}  {ex}")
    print(f"{len(fallos)} problemas")
    sys.exit(1 if fallos else 0)

if __name__ == "__main__":
    main()
