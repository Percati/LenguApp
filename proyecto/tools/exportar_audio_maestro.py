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

    # seguimiento: todo lo grabado + los ejemplos pendientes, por semana 2027:
    python3 exportar_audio_maestro.py --pendientes-tipos Ejemplo --anio 2027 --salida ../audio-a-grabar.xlsx

    # solo lo que falta grabar, y solo ejemplos:
    python3 exportar_audio_maestro.py --solo-pendientes --tipos Ejemplo --salida ../audio-a-grabar.xlsx

Con --anio AAAA se agrega la columna H "Primera semana": la primera semana
ISO de ese anio en que el texto aparece en la app (por el calendario del
par). Dentro de cada nivel las filas se ordenan por esa semana, para grabar
primero lo que se necesita antes.

La columna G "Archivo sugerido" propone el nombre con la convencion de los
.wav ya grabados: IDIOMA_NIVEL_<primeras 6 palabras>_<origen>.ogg, con sufijo
_02, _03... si choca con un archivo existente. El importador no la lee: solo
cuenta lo que Fer escriba en la columna F.
"""
import argparse, json, re, sys, unicodedata
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


def celda_de(estado, idioma, niv, k):
    """(grabado, marca, archivo) para mostrar en una fila de nivel `niv`.
    marca: lo que Fer escribio en la columna Grabado (p.ej. "Y (14.09.26)"),
    tal cual, para no perder la fecha; "Y" si el estado es viejo y no la tiene.
    archivo: si el texto se grabo tambien en este nivel (otrosArchivos), el de
    este nivel; si no, el principal."""
    grabado, archivo = estado_de(estado, k)
    v = estado.get(k)
    if not grabado:
        return False, "", archivo
    marca = v.get("marca") if isinstance(v, dict) else None
    pref = f"{idioma.upper()}_{niv}_"
    if isinstance(v, dict) and not archivo.startswith(pref):
        propio = [f for f in v.get("otrosArchivos", []) if f.startswith(pref)]
        if propio:
            archivo = propio[0]
    return True, marca or "Y", archivo


def limpio(t):
    return re.sub(r"\*+", "", str(t)).strip()


def slug(t, n=6):
    t = t.lower()
    for a, b in (("ä", "ae"), ("ö", "oe"), ("ü", "ue"), ("ß", "ss"), ("’", ""), ("'", "")):
        t = t.replace(a, b)
    t = unicodedata.normalize("NFKD", t).encode("ascii", "ignore").decode()
    return "_".join([w for w in re.split(r"[^a-z0-9]+", t) if w][:n])


def archivo_sugerido(idioma, niv, texto, origen, ocupados):
    base = f"{idioma.upper()}_{niv}_{slug(texto)}_{origen}"
    nombre, i = base, 1
    while nombre in ocupados:
        i += 1
        nombre = f"{base}_{i:02d}"
    ocupados.add(nombre)
    return nombre + ".ogg"


# Todos los origenes de cada texto (un texto repetido en dos skills del mismo
# nivel aparece una sola vez, con el primero). Sirve para mostrar el origen
# que coincide con el archivo ya grabado.
ORIGENES = defaultdict(set)


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
                ORIGENES[(idi,) + k].add(sid)
                if k not in vistos[idi]:
                    vistos[idi].add(k)
                    datos[idi][niv].append(("Ejemplo", t, sid))
        for r in d.get("redemittel", []):
            t = limpio(r["expresion"])
            k = (niv, "Expresión", t)
            ORIGENES[(idi,) + k].add(sid)
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


def primeras_semanas(raiz, anio):
    """{(idioma, nivel, tipo, texto): semana ISO de la primera aparicion}."""
    sem = {}
    for p in sorted(raiz.joinpath("contenido/ocurrencias").glob(f"*-{anio}.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        idi, niv = d["idioma"], d["nivel"]
        cal = json.loads(raiz.joinpath(f"data/calendarios/{anio}-{idi}-{niv}.json")
                         .read_text(encoding="utf-8"))
        cal = [c for c in cal if c["tipo"] == "content"]
        for o, c in zip(d["apariciones"], cal):
            s = c["semana"]
            nuc = json.loads(raiz.joinpath(f"contenido/nucleos/{o['skillId']}-{niv}.json")
                             .read_text(encoding="utf-8"))
            textos = [("Ejemplo", e["texto"]) for e in nuc.get("ejemplos", []) if e.get("audio")]
            textos += [("Expresión", r["expresion"]) for r in nuc.get("redemittel", [])]
            pk = raiz.joinpath(f"contenido/packs/{o['packId']}.json")
            if pk.exists():
                pack = json.loads(pk.read_text(encoding="utf-8"))
                textos += [("Vocabulario", v["item"]) for v in pack.get("vocabulario", [])]
            for tipo, t in textos:
                k = (idi, niv, tipo, limpio(t))
                sem[k] = min(sem.get(k, 99), s)
    return sem


def hoja_idioma(wb, idioma, por_nivel, estado, ocupados, semanas=None):
    ws = wb.create_sheet(NOMBRE[idioma][:31])
    ws.freeze_panes = "A3"
    cuenta = {"grabados": 0, "a_grabar": 0}
    # Un texto pendiente que aparece en varios niveles se graba UNA vez, en el
    # nivel mas bajo; los demas niveles lo reusan (igual que la app, que busca
    # el audio por texto). Asi no se generan duplicados por nivel.
    nivel_de_grabacion = {}
    for niv in ORDEN_NIVEL:
        for tipo, texto, _ in por_nivel.get(niv, []):
            if not estado_de(estado, f"{idioma}|{tipo}|{texto}")[0]:
                nivel_de_grabacion.setdefault((tipo, texto), niv)
    ws["A1"] = f"Audio a grabar — {NOMBRE[idioma]}"
    ws["A1"].font = Font(bold=True, size=13)

    fila = 2
    cols = ["Nivel", "Tipo", "Texto", "Origen", "Grabado", "Archivo audio",
            "Archivo sugerido / nota"]
    if semanas is not None:
        cols.append("Primera semana")
    for j, t in enumerate(cols, start=1):
        c = ws.cell(row=fila, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF"); c.fill = CABEZA
    ws.column_dimensions["A"].width = 8
    ws.column_dimensions["B"].width = 13
    ws.column_dimensions["C"].width = 60
    ws.column_dimensions["D"].width = 14
    ws.column_dimensions["E"].width = 10
    ws.column_dimensions["F"].width = 45
    ws.column_dimensions["G"].width = 50
    ws.column_dimensions["H"].width = 15
    sem = (lambda niv, tipo, t: semanas.get((idioma, niv, tipo, t))) if semanas is not None \
        else (lambda *x: None)

    for niv in ORDEN_NIVEL:
        filas = por_nivel.get(niv, [])
        if not filas:
            continue
        fila += 1
        c = ws.cell(row=fila, column=1, value=f"Nivel {niv} — {len(filas)} textos")
        c.font = Font(bold=True); c.fill = NIVEL_FILL
        for j in range(2, 6):
            ws.cell(row=fila, column=j).fill = NIVEL_FILL
        # pendientes arriba (por semana de necesidad), grabados abajo
        orden = lambda x: (celda_de(estado, idioma, niv, f"{idioma}|{x[0]}|{x[1]}")[0],
                           sem(niv, x[0], x[1]) or 99, x[0], x[1])
        for tipo, texto, origen in sorted(filas, key=orden):
            fila += 1
            ws.cell(row=fila, column=1, value=niv).border = BORDE
            ws.cell(row=fila, column=2, value=tipo).border = BORDE
            c = ws.cell(row=fila, column=3, value=texto)
            c.border = BORDE; c.alignment = Alignment(wrap_text=True)
            ws.cell(row=fila, column=4, value=origen).border = BORDE
            k = f"{idioma}|{tipo}|{texto}"
            grabado, marca, archivo = celda_de(estado, idioma, niv, k)
            m = re.search(r"_((?:DE|EN)-[A-Z]\d+)(?:_\d+)?\.(?:ogg|wav)$", archivo or "")
            if m and m.group(1) in ORIGENES[(idioma, niv, tipo, texto)]:
                origen = m.group(1)
                ws.cell(row=fila, column=4, value=origen)
            if grabado:
                cuenta["grabados"] += 1
            ws.cell(row=fila, column=5, value=marca).border = BORDE
            ws.cell(row=fila, column=6, value=archivo).border = BORDE
            if not grabado and nivel_de_grabacion[(tipo, texto)] != niv:
                c = ws.cell(row=fila, column=7,
                            value=f"(no grabar: se graba en {nivel_de_grabacion[(tipo, texto)]})")
                c.border = BORDE; c.font = Font(color="777777", italic=True)
            elif not grabado:
                cuenta["a_grabar"] += 1
                c = ws.cell(row=fila, column=7,
                            value=archivo_sugerido(idioma, niv, texto, origen, ocupados))
                c.border = BORDE; c.font = Font(color="777777")
            elif archivo and not archivo.startswith(f"{idioma.upper()}_{niv}_"):
                # el texto no se grabo en este nivel: la app usa el audio de otro
                c = ws.cell(row=fila, column=7,
                            value=f"(reusa el audio de {archivo.split('_')[1]})")
                c.border = BORDE; c.font = Font(color="777777", italic=True)
            if semanas is not None:
                ws.cell(row=fila, column=8, value=sem(niv, tipo, texto)).border = BORDE
    cuenta["total"] = sum(len(v) for v in por_nivel.values())  # sin separadores de nivel
    return cuenta


def main():
    sys.stdout.reconfigure(encoding="utf-8")
    ap = argparse.ArgumentParser()
    ap.add_argument("--contenido", default="../contenido")
    ap.add_argument("--salida", default="../auditoria-audio.xlsx")
    ap.add_argument("--estado", default="../audio-estado.json")
    ap.add_argument("--audio", default="../../app/src/main/assets/audio",
                    help="carpeta de audio, para que el nombre sugerido no choque")
    ap.add_argument("--solo-pendientes", action="store_true",
                    help="omitir lo ya grabado segun audio-estado.json")
    ap.add_argument("--anio", type=int,
                    help="agrega la primera semana de aparicion en ese anio y ordena por ella")
    ap.add_argument("--tipos", nargs="+", choices=["Ejemplo", "Expresión", "Vocabulario"],
                    help="limitar a estos tipos (por defecto, todos)")
    ap.add_argument("--pendientes-tipos", nargs="+",
                    choices=["Ejemplo", "Expresión", "Vocabulario"],
                    help="de lo NO grabado, listar solo estos tipos; lo grabado va siempre. "
                         "Es el modo seguimiento: todo lo hecho + la tanda en curso")
    a = ap.parse_args()
    datos = recolectar(Path(a.contenido))
    estado_path = Path(a.estado)
    estado = json.loads(estado_path.read_text(encoding="utf-8")) if estado_path.exists() else {}

    for idi in datos:
        for niv in list(datos[idi]):
            datos[idi][niv] = [x for x in datos[idi][niv]
                               if (not a.tipos or x[0] in a.tipos)
                               and not (a.solo_pendientes
                                        and estado_de(estado, f"{idi}|{x[0]}|{x[1]}")[0])
                               and not (a.pendientes_tipos
                                        and x[0] not in a.pendientes_tipos
                                        and not estado_de(estado, f"{idi}|{x[0]}|{x[1]}")[0])]
    ocupados = {p.stem for p in Path(a.audio).rglob("*") if p.suffix in (".ogg", ".wav")}
    for v in estado.values():
        if isinstance(v, dict):
            for f in [v.get("archivo", "")] + v.get("otrosArchivos", []):
                if f:
                    ocupados.add(Path(f).stem)

    semanas = primeras_semanas(Path(a.contenido).resolve().parent, a.anio) if a.anio else None

    wb = openpyxl.Workbook()
    idx = wb.active; idx.title = "ÍNDICE"
    idx["A1"] = "Maestro de audio a grabar"
    idx["A1"].font = Font(bold=True, size=14)
    idx["A2"] = ("Una hoja por idioma que se aprende (nunca la glosa). Dentro de cada hoja, "
                 "agrupado por nivel, pendientes arriba. Columna «Grabado»: al terminar, marcar "
                 "(p.ej. «Y (14.09.26)», se conserva tal cual) y poner el nombre del .ogg en "
                 "«Archivo audio» (la columna «Archivo sugerido» lo propone).")
    idx["A2"].font = Font(size=9, color="555555")
    idx["A2"].alignment = Alignment(wrap_text=True)
    idx.column_dimensions["A"].width = 90

    for j, t in enumerate(["Idioma", "Total filas", "Grabados", "Pendientes",
                                "A grabar (únicos)"], start=1):
        c = idx.cell(row=4, column=j, value=t)
        c.font = Font(bold=True, color="FFFFFF"); c.fill = CABEZA
    for col in "BCDE":
        idx.column_dimensions[col].width = 14

    fila = 5
    total = grab = agr = 0
    for idi in sorted(datos, key=lambda x: NOMBRE[x]):
        if not any(datos[idi].values()):
            continue
        c = hoja_idioma(wb, idi, datos[idi], estado, ocupados, semanas)
        idx.cell(row=fila, column=1, value=NOMBRE[idi])
        for j, v in ((2, c["total"]), (3, c["grabados"]), (4, c["total"] - c["grabados"]),
                     (5, c["a_grabar"])):
            idx.cell(row=fila, column=j, value=v)
        total += c["total"]; grab += c["grabados"]; agr += c["a_grabar"]
        fila += 1
    idx.cell(row=fila, column=1, value="TOTAL").font = Font(bold=True)
    for j, v in ((2, total), (3, grab), (4, total - grab), (5, agr)):
        idx.cell(row=fila, column=j, value=v).font = Font(bold=True)

    wb.save(a.salida)
    print(f"{total} textos ({grab} grabados, {total - grab} pendientes, "
          f"{agr} a grabar sin repetir niveles) -> {a.salida}")


if __name__ == "__main__":
    main()
