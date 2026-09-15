#!/usr/bin/env python3
"""
importar_traduccion_maestro.py — vuelca las traducciones devueltas al contenido fuente.

Esto es lo que hace que la persistencia funcione sola: las traducciones que
Fer escribe en el Excel no se guardan aparte, se escriben DENTRO de
contenido/nucleos/*.json y contenido/packs/*.json. La proxima vez que se
corra exportar_traduccion_maestro.py, esas celdas ya salen verdes porque el
dato ya esta en la fuente — no hace falta fusionar el Excel viejo con el
nuevo, el contenido mismo es la memoria.

Empareja por (idioma, nivel, tipo, texto original), no por posicion de fila:
si Fer reordena o filtra el Excel, sigue encontrando cada fila.

Uso:
    python3 importar_traduccion_maestro.py ../traducciones-a-completar.xlsx --contenido ../contenido
"""
import argparse, json, re, sys
from pathlib import Path
import openpyxl

IDIOMAS = ["de", "en", "es", "fr", "it", "pt"]
NOMBRE = {"de": "Alemán", "en": "Inglés", "es": "Español",
          "fr": "Francés", "it": "Italiano", "pt": "Portugués"}
COD = {v: k for k, v in NOMBRE.items()}


def limpio(t):
    return re.sub(r"\*+", "", str(t or "")).strip()


def leer_hoja(ws, idioma_aprendido):
    """Devuelve {(nivel, tipo, texto): {codigo_destino: valor}}."""
    bases = [i for i in IDIOMAS if i != idioma_aprendido]
    cab = [ws.cell(row=3, column=j).value for j in range(1, ws.max_column + 1)]
    col_base = {}
    for j, nombre in enumerate(cab[3:len(bases) + 3], start=4):
        for cod, nom in NOMBRE.items():
            if nombre == nom:
                col_base[j] = cod
    out = {}
    nivel_actual = None
    for r in range(4, ws.max_row + 1):
        a = ws.cell(row=r, column=1).value
        if a and "Nivel" in str(a):
            nivel_actual = str(a).split()[1]
            continue
        tipo = ws.cell(row=r, column=2).value
        texto = ws.cell(row=r, column=3).value
        if not tipo or not texto or not nivel_actual:
            continue
        k = (nivel_actual, tipo, limpio(texto))
        vals = {}
        for j, cod in col_base.items():
            v = ws.cell(row=r, column=j).value
            if v is not None and str(v).strip():
                vals[cod] = None if str(v).strip() == "—" else str(v).strip()
        if vals:
            out[k] = vals
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("excel")
    ap.add_argument("--contenido", default="../contenido")
    a = ap.parse_args()
    base = Path(a.contenido)
    wb = openpyxl.load_workbook(a.excel, data_only=True)

    por_idioma = {}
    for hoja in wb.sheetnames:
        if hoja.upper().startswith("ÍNDICE") or hoja.upper().startswith("INDICE"):
            continue
        idi = COD.get(hoja)
        if not idi:
            continue
        por_idioma[idi] = leer_hoja(wb[hoja], idi)

    n_red = n_voc = 0
    for p in base.joinpath("nucleos").glob("*.json"):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo"):
            continue
        idi, niv = d["idioma"], d["nivel"]
        datos = por_idioma.get(idi, {})
        cambiado = False
        for r in d.get("redemittel", []):
            k = (niv, "Expresión", limpio(r["expresion"]))
            if k in datos:
                for cod, val in datos[k].items():
                    if cod not in r["traducciones"]:
                        r["traducciones"][cod] = val
                        cambiado = True; n_red += 1
        if cambiado:
            p.write_text(json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")

    for p in base.joinpath("packs").glob("*.json"):
        d = json.loads(p.read_text(encoding="utf-8"))
        idi, niv = d["idioma"], d["nivel"]
        datos = por_idioma.get(idi, {})
        cambiado = False
        for v in d.get("vocabulario", []):
            k = (niv, "Vocabulario", limpio(v["item"]))
            if k in datos:
                for cod, val in datos[k].items():
                    if cod not in v["traducciones"]:
                        v["traducciones"][cod] = val
                        cambiado = True; n_voc += 1
        if cambiado:
            p.write_text(json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")

    print(f"{n_red} expresiones y {n_voc} items de vocabulario actualizados en el contenido fuente.",
          file=sys.stderr)
    print("Volvé a correr exportar_traduccion_maestro.py: esas celdas ya van a salir verdes.",
          file=sys.stderr)


if __name__ == "__main__":
    main()
