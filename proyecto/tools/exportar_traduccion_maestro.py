#!/usr/bin/env python3
"""
exportar_traduccion_maestro.py — el maestro único de traducciones pendientes.

Misma filosofia que exportar_audio_maestro.py: una hoja por idioma que se
aprende, agrupado por nivel dentro de cada hoja, formato identico entre
hojas. La diferencia es que aqui cada fila lleva las columnas de destino que
todavia faltan, marcadas con el idioma exacto al que hay que traducir.

Cubre vocabulario y expresiones (de nucleos/packs). Deliberadamente NO cubre
el contraste ni los errores contrastivos: no son traduccion, son analisis
nuevo por cada par de idiomas, y los escribe el diseñador aparte.

Uso:
    python3 exportar_traduccion_maestro.py --contenido ../contenido --salida ../traducciones-maestro.xlsx
"""
import argparse, json, re
from pathlib import Path
from collections import defaultdict

import openpyxl
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter

IDIOMAS = ["de", "en", "es", "fr", "it", "pt"]
NOMBRE = {"de": "Alemán", "en": "Inglés", "es": "Español",
          "fr": "Francés", "it": "Italiano", "pt": "Portugués"}
ORDEN_NIVEL = ["A2", "B1", "B2", "C1", "C2"]

VERDE  = PatternFill("solid", fgColor="D9EAD3")
AMBAR  = PatternFill("solid", fgColor="FFF2CC")
GRIS   = PatternFill("solid", fgColor="EFEFEF")
CABEZA = PatternFill("solid", fgColor="1E293B")
NIVEL_FILL = PatternFill("solid", fgColor="DDE3EA")
BORDE  = Border(*[Side(style="thin", color="CCCCCC")] * 4)


def limpio(t):
    return re.sub(r"\*+", "", str(t)).strip()


def recolectar(base):
    """{idioma: {nivel: [(tipo, texto, dict_traducciones, origen)]}}, deduplicado."""
    datos = defaultdict(lambda: defaultdict(list))
    vistos = defaultdict(set)

    for p in sorted(base.joinpath("nucleos").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo"):
            continue
        idi, niv, sid = d["idioma"], d["nivel"], d["skillId"]
        for r in d.get("redemittel", []):
            t = limpio(r["expresion"])
            k = (niv, "Expresión", t)
            if k not in vistos[idi]:
                vistos[idi].add(k)
                datos[idi][niv].append(("Expresión", t, dict(r.get("traducciones", {})), sid))

    for p in sorted(base.joinpath("packs").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        idi, niv = d["idioma"], d["nivel"]
        for v in d.get("vocabulario", []):
            t = limpio(v["item"])
            k = (niv, "Vocabulario", t)
            if k not in vistos[idi]:
                vistos[idi].add(k)
                datos[idi][niv].append(("Vocabulario", t, dict(v.get("traducciones", {})),
                                        d.get("topicId", "")))
    return datos


def hoja_idioma(wb, idioma, por_nivel):
    bases = [i for i in IDIOMAS if i != idioma]
    ws = wb.create_sheet(NOMBRE[idioma][:31])
    ws.freeze_panes = "A4"
    ws["A1"] = f"Traducciones — {NOMBRE[idioma]} (idioma que se aprende)"
    ws["A1"].font = Font(bold=True, size=13)
    ws["A2"] = "Verde = ya existe, no tocar. Ámbar = falta traducir."
    ws["A2"].font = Font(size=9, color="7F6000")

    fila = 3
    cab = ["Nivel", "Tipo", f"{NOMBRE[idioma]} — original"] + [NOMBRE[b] for b in bases] + ["Origen"]
    for j, t in enumerate(cab, start=1):
        c = ws.cell(row=fila, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF", size=9); c.fill = CABEZA
        c.alignment = Alignment(wrap_text=True, horizontal="center")
    anchos = [8, 12, 34] + [22] * len(bases) + [12]
    for i, a_ in enumerate(anchos, start=1):
        ws.column_dimensions[get_column_letter(i)].width = a_

    tot_celdas = 0
    for niv in ORDEN_NIVEL:
        filas = por_nivel.get(niv, [])
        if not filas:
            continue
        fila += 1
        c = ws.cell(row=fila, column=1, value=f"Nivel {niv} — {len(filas)} textos")
        c.font = Font(bold=True); c.fill = NIVEL_FILL
        for j in range(2, len(cab) + 1):
            ws.cell(row=fila, column=j).fill = NIVEL_FILL
        for tipo, texto, trads, origen in sorted(filas, key=lambda x: (x[0], x[1])):
            fila += 1
            ws.cell(row=fila, column=1, value=niv).border = BORDE
            ws.cell(row=fila, column=2, value=tipo).border = BORDE
            c = ws.cell(row=fila, column=3, value=texto)
            c.fill = GRIS; c.border = BORDE; c.alignment = Alignment(wrap_text=True)
            for j, b in enumerate(bases, start=4):
                cel = ws.cell(row=fila, column=j)
                if b in trads:
                    v = trads[b]
                    cel.value = v if v is not None else "—"
                    cel.fill = VERDE
                else:
                    cel.fill = AMBAR
                    tot_celdas += 1
                cel.border = BORDE
                cel.alignment = Alignment(wrap_text=True)
            ws.cell(row=fila, column=len(cab), value=origen).border = BORDE
    return tot_celdas


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--contenido", default="../contenido")
    ap.add_argument("--salida", default="../traducciones-maestro.xlsx")
    a = ap.parse_args()
    datos = recolectar(Path(a.contenido))

    wb = openpyxl.Workbook()
    idx = wb.active; idx.title = "ÍNDICE"
    idx["A1"] = "Maestro de traducciones pendientes"
    idx["A1"].font = Font(bold=True, size=14)
    idx["A2"] = ("Una hoja por idioma que se aprende. Rellenar solo las celdas en ámbar. "
                 "No incluye contraste ni errores contrastivos: eso no es traducción, es "
                 "análisis nuevo por cada par de idiomas y lo escribe el diseñador aparte.")
    idx["A2"].font = Font(size=9, color="555555")
    idx["A2"].alignment = Alignment(wrap_text=True)
    idx.column_dimensions["A"].width = 95

    for j, t in enumerate(["Idioma que se aprende", "Celdas por traducir"], start=1):
        c = idx.cell(row=4, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF"); c.fill = CABEZA
    idx.column_dimensions["B"].width = 18

    fila = 5
    total = 0
    for idi in sorted(datos, key=lambda x: NOMBRE[x]):
        n = hoja_idioma(wb, idi, datos[idi])
        idx.cell(row=fila, column=1, value=NOMBRE[idi])
        idx.cell(row=fila, column=2, value=n)
        total += n
        fila += 1
    idx.cell(row=fila, column=1, value="TOTAL").font = Font(bold=True)
    idx.cell(row=fila, column=2, value=total).font = Font(bold=True)

    wb.save(a.salida)
    print(f"{total} celdas por traducir -> {a.salida}")


if __name__ == "__main__":
    main()
