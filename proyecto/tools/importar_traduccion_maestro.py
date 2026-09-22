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

Cubre los dos bloques del maestro:

  - Hojas por idioma que se aprende: vocabulario y expresiones.
  - Hojas "Prosa XX NN": la prosa bilingue de A2/B1. Ahi el campo pasa de
    string plano al objeto {idioma: texto} que admite el schema, con el texto
    original guardado bajo la clave del idioma que se aprende. Como el
    exportador agrupa los textos repetidos, cada fila se vuelca en TODOS los
    lugares (nucleos y apariciones) donde aparece ese mismo texto.

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


# --------------------------------------------------------------------------
# Prosa bilingue A2/B1
# --------------------------------------------------------------------------
def leer_hoja_prosa(ws, idioma_aprendido):
    """Devuelve {texto_original: {codigo_destino: valor}} de una hoja de prosa."""
    bases = [i for i in IDIOMAS if i != idioma_aprendido]
    cab = [ws.cell(row=3, column=j).value for j in range(1, ws.max_column + 1)]
    col_base = {}
    for j, nombre in enumerate(cab[4:len(bases) + 4], start=5):
        for cod, nom in NOMBRE.items():
            if nombre == nom:
                col_base[j] = cod
    out = {}
    for r in range(4, ws.max_row + 1):
        ruta = ws.cell(row=r, column=2).value
        texto = ws.cell(row=r, column=4).value
        if not ruta or texto is None:
            continue
        vals = {}
        for j, cod in col_base.items():
            v = ws.cell(row=r, column=j).value
            if v is not None and str(v).strip():
                vals[cod] = str(v).strip()
        if vals:
            out[str(texto)] = vals
    return out


def _traducir(valor, idioma, datos, cuenta):
    """String plano u objeto -> objeto {idioma: texto, ...traducciones}.

    No pisa ninguna clave que ya exista. Devuelve el valor nuevo.
    """
    if isinstance(valor, str):
        vals = datos.get(valor)
        if not vals:
            return valor
        nuevo = {idioma: valor}
        for cod, v in vals.items():
            nuevo[cod] = v
            cuenta[0] += 1
        return nuevo
    if isinstance(valor, dict):
        original = valor.get(idioma)
        vals = datos.get(original) if original else None
        if not vals:
            return valor
        for cod, v in vals.items():
            if cod not in valor:
                valor[cod] = v
                cuenta[0] += 1
        return valor
    return valor


def _prosa_nucleo(d, idioma, datos, cuenta):
    for clave in ("descripcion", "promptCorreccion"):
        if clave in d:
            d[clave] = _traducir(d[clave], idioma, datos, cuenta)
    c = d.get("cuadroReferencia") or {}
    if "titulo" in c:
        c["titulo"] = _traducir(c["titulo"], idioma, datos, cuenta)
    if "columnas" in c:
        c["columnas"] = [_traducir(x, idioma, datos, cuenta) for x in c["columnas"]]
    if "filas" in c:
        c["filas"] = [[_traducir(x, idioma, datos, cuenta) for x in fila] for fila in c["filas"]]
    if c.get("notaPie"):
        c["notaPie"] = _traducir(c["notaPie"], idioma, datos, cuenta)
    for e in d.get("ejemplos", []):
        if "texto" in e:
            e["texto"] = _traducir(e["texto"], idioma, datos, cuenta)
    for clave in ("notas", "errores", "autochequeo"):
        if isinstance(d.get(clave), list):
            d[clave] = [_traducir(x, idioma, datos, cuenta) for x in d[clave]]


def _prosa_aparicion(a, idioma, datos, cuenta):
    if "subtitulo" in a:
        a["subtitulo"] = _traducir(a["subtitulo"], idioma, datos, cuenta)
    m = a.get("mision") or {}
    if "consigna" in m:
        m["consigna"] = _traducir(m["consigna"], idioma, datos, cuenta)
    if "requisitos" in m:
        m["requisitos"] = [_traducir(x, idioma, datos, cuenta) for x in m["requisitos"]]
    for mt in a.get("microtareas", []):
        if "texto" in mt:
            mt["texto"] = _traducir(mt["texto"], idioma, datos, cuenta)


def volcar_prosa(base, por_combo):
    """Escribe la prosa traducida en nucleos y ocurrencias. Devuelve (campos, celdas)."""
    campos = 0
    celdas = [0]
    for p in sorted(base.joinpath("nucleos").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo"):
            continue
        datos = por_combo.get((d["idioma"], d["nivel"]))
        if not datos:
            continue
        antes = celdas[0]
        _prosa_nucleo(d, d["idioma"], datos, celdas)
        if celdas[0] != antes:
            campos += 1
            p.write_text(json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")

    for p in sorted(base.joinpath("ocurrencias").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        datos = por_combo.get((d["idioma"], d["nivel"]))
        if not datos:
            continue
        antes = celdas[0]
        for a in d["apariciones"]:
            _prosa_aparicion(a, d["idioma"], datos, celdas)
        if celdas[0] != antes:
            campos += 1
            p.write_text(json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")
    return campos, celdas[0]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("excel")
    ap.add_argument("--contenido", default="../contenido")
    a = ap.parse_args()
    base = Path(a.contenido)
    wb = openpyxl.load_workbook(a.excel, data_only=True)

    por_idioma = {}
    por_combo = {}
    for hoja in wb.sheetnames:
        if hoja.upper().startswith("ÍNDICE") or hoja.upper().startswith("INDICE"):
            continue
        if hoja.startswith("Prosa "):
            partes = hoja.split()
            if len(partes) != 3:
                print(f"Hoja de prosa con nombre inesperado, se saltea: {hoja}", file=sys.stderr)
                continue
            idi, niv = partes[1].lower(), partes[2]
            por_combo[(idi, niv)] = leer_hoja_prosa(wb[hoja], idi)
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

    n_campos, n_celdas = volcar_prosa(base, por_combo)

    print(f"{n_red} expresiones y {n_voc} items de vocabulario actualizados en el contenido fuente.",
          file=sys.stderr)
    print(f"Prosa A2/B1: {n_celdas} traducciones escritas en {n_campos} archivos.",
          file=sys.stderr)
    print("Volvé a correr exportar_traduccion_maestro.py: esas celdas ya van a salir verdes.",
          file=sys.stderr)


if __name__ == "__main__":
    main()
