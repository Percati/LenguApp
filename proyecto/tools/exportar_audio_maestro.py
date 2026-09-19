#!/usr/bin/env python3
"""
exportar_audio_maestro.py — el maestro único de textos a grabar.

Una hoja por idioma (el que se aprende, nunca la glosa: el audio se pronuncia
siempre en la lengua de la ficha). Dentro de cada hoja, los textos van
agrupados y ordenados por nivel, y dentro de cada nivel por tipo: ejemplos,
vocabulario, expresiones.

Fuentes leidas:
  contenido/nucleos/*.json   -> ejemplos (audio:true) y redemittel (expresion)
  contenido/packs/*.json     -> vocabulario (item)

Es un maestro acumulativo: se vuelve a correr cada vez que hay contenido
nuevo y reemplaza el archivo entero. No hace falta fusionar a mano.

Uso:
    python3 exportar_audio_maestro.py --contenido ../contenido --salida ../auditoria-audio.xlsx
"""
import argparse, json, re
from pathlib import Path
from collections import defaultdict

import openpyxl
from openpyxl.styles import Font, PatternFill, Alignment, Border, Side
from openpyxl.utils import get_column_letter

NOMBRE = {"de": "Alemán", "en": "Inglés", "es": "Español",
          "fr": "Francés", "it": "Italiano", "pt": "Portugués"}
ORDEN_NIVEL = ["A2", "B1", "B2", "C1", "C2"]
CABEZA = PatternFill("solid", fgColor="1E293B")
NIVEL_FILL = PatternFill("solid", fgColor="DDE3EA")
BORDE = Border(*[Side(style="thin", color="CCCCCC")] * 4)


def estado_de(estado, k):
    """Lee una entrada de estado tolerando el formato viejo (booleano) y el
    nuevo (dict con 'grabado' y 'archivo'). Nunca se pierde lo que Fer ya
    cargo, sea cual sea el formato en que quedo guardado."""
    v = estado.get(k)
    if isinstance(v, dict):
        return bool(v.get("grabado")), v.get("archivo", "")
    return bool(v), ""


def limpio(t):
    return re.sub(r"\*+", "", str(t)).strip()


def recolectar(base):
    """Devuelve {idioma: {nivel: [(tipo, texto, origen)]}}."""
    datos = defaultdict(lambda: defaultdict(list))
    vistos = defaultdict(set)

    for p in sorted(base.joinpath("nucleos").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo"):
            continue
        idi, niv, sid = d["idioma"], d["nivel"], d["skillId"]
        for e in d.get("ejemplos", []):
            if e.get("audio"):
                t = limpio(e["texto"])
                k = (niv, "Ejemplo", t)
                if k not in vistos[idi]:
                    vistos[idi].add(k)
                    datos[idi][niv].append(("Ejemplo", t, sid))
        for r in d.get("redemittel", []):
            t = limpio(r["expresion"])
            k = (niv, "Expresión", t)
            if k not in vistos[idi]:
                vistos[idi].add(k)
                datos[idi][niv].append(("Expresión", t, sid))

    for p in sorted(base.joinpath("packs").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        idi, niv = d["idioma"], d["nivel"]
        for v in d.get("vocabulario", []):
            t = limpio(v["item"])
            k = (niv, "Vocabulario", t)
            if k not in vistos[idi]:
                vistos[idi].add(k)
                datos[idi][niv].append(("Vocabulario", t, d.get("topicId", "")))
    return datos


def hoja_idioma(wb, idioma, por_nivel, estado):
    ws = wb.create_sheet(NOMBRE[idioma][:31])
    ws.freeze_panes = "A3"
    ws["A1"] = f"Audio a grabar — {NOMBRE[idioma]}"
    ws["A1"].font = Font(bold=True, size=13)

    fila = 2
    for j, t in enumerate(["Nivel", "Tipo", "Texto", "Origen", "Grabado", "Archivo audio"], start=1):
        c = ws.cell(row=fila, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF"); c.fill = CABEZA
    ws.column_dimensions["A"].width = 8
    ws.column_dimensions["B"].width = 13
    ws.column_dimensions["C"].width = 60
    ws.column_dimensions["D"].width = 14
    ws.column_dimensions["E"].width = 10
    ws.column_dimensions["F"].width = 45

    for niv in ORDEN_NIVEL:
        filas = por_nivel.get(niv, [])
        if not filas:
            continue
        fila += 1
        c = ws.cell(row=fila, column=1, value=f"Nivel {niv} — {len(filas)} textos")
        c.font = Font(bold=True); c.fill = NIVEL_FILL
        for j in range(2, 6):
            ws.cell(row=fila, column=j).fill = NIVEL_FILL
        for tipo, texto, origen in sorted(filas, key=lambda x: (x[0], x[1])):
            fila += 1
            ws.cell(row=fila, column=1, value=niv).border = BORDE
            ws.cell(row=fila, column=2, value=tipo).border = BORDE
            c = ws.cell(row=fila, column=3, value=texto)
            c.border = BORDE; c.alignment = Alignment(wrap_text=True)
            ws.cell(row=fila, column=4, value=origen).border = BORDE
            k = f"{idioma}|{tipo}|{texto}"
            grabado, archivo = estado_de(estado, k)
            ws.cell(row=fila, column=5, value="Y" if grabado else "").border = BORDE
            ws.cell(row=fila, column=6, value=archivo).border = BORDE
    return fila - 2  # total de filas de datos


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--contenido", default="../contenido")
    ap.add_argument("--salida", default="../auditoria-audio.xlsx")
    ap.add_argument("--estado", default="../audio-estado.json")
    a = ap.parse_args()
    datos = recolectar(Path(a.contenido))
    estado_path = Path(a.estado)
    estado = json.loads(estado_path.read_text(encoding="utf-8")) if estado_path.exists() else {}

    wb = openpyxl.Workbook()
    idx = wb.active; idx.title = "ÍNDICE"
    idx["A1"] = "Maestro de audio a grabar"
    idx["A1"].font = Font(bold=True, size=14)
    idx["A2"] = ("Una hoja por idioma que se aprende (nunca la glosa). Dentro de cada hoja, "
                 "agrupado por nivel. Columna «Grabado»: marcar con una X al terminar.")
    idx["A2"].font = Font(size=9, color="555555")
    idx["A2"].alignment = Alignment(wrap_text=True)
    idx.column_dimensions["A"].width = 90

    for j, t in enumerate(["Idioma", "Total textos"], start=1):
        c = idx.cell(row=4, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF"); c.fill = CABEZA
    idx.column_dimensions["B"].width = 14

    fila = 5
    total = 0
    for idi in sorted(datos, key=lambda x: NOMBRE[x]):
        n = hoja_idioma(wb, idi, datos[idi], estado)
        idx.cell(row=fila, column=1, value=NOMBRE[idi])
        idx.cell(row=fila, column=2, value=n)
        total += n
        fila += 1
    idx.cell(row=fila, column=1, value="TOTAL").font = Font(bold=True)
    idx.cell(row=fila, column=2, value=total).font = Font(bold=True)

    wb.save(a.salida)
    print(f"{total} textos -> {a.salida}")


if __name__ == "__main__":
    main()
