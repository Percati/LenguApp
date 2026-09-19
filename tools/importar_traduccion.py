#!/usr/bin/env python3
"""
importar_traduccion.py — vuelca el Excel traducido a las fichas Markdown.

Contraparte de exportar_traduccion.py. Lee una hoja por ficha, empareja cada
fila por el texto de la primera columna (el idioma que se aprende) y anade una
columna por idioma en las tablas de vocabulario y de Redemittel.

Empareja por texto, no por posicion: si una fila se movio en el Excel, sigue
encontrandola; si el texto se edito, avisa en vez de escribir en la fila
equivocada.

Uso:
    python3 importar_traduccion.py ../traducciones_completado.xlsx --fichas ../fichas
"""
import argparse, re, sys
from pathlib import Path
import openpyxl

IDIOMAS = {"ALEMÁN": "DE", "INGLÉS": "EN", "ESPAÑOL": "ES",
           "FRANCÉS": "FR", "ITALIANO": "IT", "PORTUGUÉS": "PT"}


def clave(t):
    # Se quitan TODOS los asteriscos, no solo los de los extremos: hay celdas
    # como "*eye-watering* (price)" donde el marcado queda a mitad de texto y
    # el emparejamiento fallaba en silencio.
    t = str(t or "").replace("★", "").replace("†", "").replace("*", "").strip()
    return re.sub(r"\s+", " ", t)


def leer_excel(path):
    """Devuelve {clave_de_texto: {codigo_idioma: glosa}} de todas las hojas."""
    wb = openpyxl.load_workbook(path, data_only=True)
    glosas, cols_por_hoja = {}, {}
    for h in wb.sheetnames:
        if h.upper().startswith("ÍNDICE") or h.upper().startswith("INDICE"):
            continue
        ws = wb[h]
        cab = {}
        for j in range(2, 8):
            v = str(ws.cell(row=4, column=j).value or "")
            for nombre, cod in IDIOMAS.items():
                if v.upper().startswith(nombre):
                    cab[j] = cod
        cols_por_hoja[h] = cab
        for r in range(5, ws.max_row + 1):
            orig = ws.cell(row=r, column=1).value
            if not orig or str(orig).strip() in ("VOCABULARIO", "EXPRESIONES"):
                continue
            k = clave(orig)
            dest = glosas.setdefault(k, {})
            for j, cod in cab.items():
                v = ws.cell(row=r, column=j).value
                if v is not None and str(v).strip():
                    dest[cod] = re.sub(r"\s+", " ", str(v).strip())
    return glosas, cols_por_hoja


def actualizar_ficha(path, glosas, orden_deseado):
    """Reescribe las tablas con cabecera de idioma, anadiendo las columnas que falten."""
    lineas = path.read_text(encoding="utf-8").splitlines()
    out, cab, sin = [], None, []
    for l in lineas:
        t = l.strip()
        if not t.startswith("|"):
            cab = None
            out.append(l)
            continue
        celdas = [c.strip() for c in t.strip("|").split("|")]
        codigos = [c.upper() for c in celdas if c.upper() in IDIOMAS.values()]
        if codigos:                                   # fila de cabecera
            fijas = [c for c in celdas if c.upper() not in IDIOMAS.values()]
            nuevos = [c for c in orden_deseado if c not in codigos]
            cab = {"fijas": len(fijas), "cols": codigos + nuevos}
            out.append("| " + " | ".join(fijas + cab["cols"]) + " |")
            continue
        if cab and re.match(r"^:?-+:?$", celdas[0]):
            out.append("|" + "---|" * (cab["fijas"] + len(cab["cols"])))
            continue
        if cab:
            k = clave(celdas[0])
            g = glosas.get(k, {})
            fijas = celdas[:cab["fijas"]]
            vals = []
            for i, cod in enumerate(cab["cols"]):
                if cab["fijas"] + i < len(celdas) and celdas[cab["fijas"] + i]:
                    vals.append(celdas[cab["fijas"] + i])     # ya estaba
                else:
                    v = g.get(cod, "")
                    if not v:
                        sin.append((k, cod))
                    vals.append(v)
            out.append("| " + " | ".join(fijas + vals) + " |")
            continue
        out.append(l)
    path.write_text("\n".join(out) + "\n", encoding="utf-8")
    return sin


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("excel")
    ap.add_argument("--fichas", default="../fichas")
    a = ap.parse_args()

    glosas, _ = leer_excel(a.excel)
    print(f"{len(glosas)} textos con glosa en el Excel", file=sys.stderr)

    total_sin = 0
    for p in sorted(Path(a.fichas).glob("*.md")):
        m = re.match(r"(EN|DE|ES|FR|IT|PT)-", p.name, re.I)
        if not m:
            continue
        aprende = m.group(1).upper()
        orden = [c for c in ("ES", "EN", "DE", "FR", "IT", "PT") if c != aprende]
        sin = actualizar_ficha(p, glosas, orden)
        total_sin += len(sin)
        print(f"  {p.name}: {'ok' if not sin else str(len(sin)) + ' sin glosa'}",
              file=sys.stderr)
        for k, cod in sin[:3]:
            print(f"      falta {cod}: {k}", file=sys.stderr)
    print(f"\n{total_sin} celdas quedaron sin glosa", file=sys.stderr)


if __name__ == "__main__":
    main()
