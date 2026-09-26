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
  - Hojas "Repaso XX NN": los seis campos de texto de las semanas especiales
    A2/B1 (contenido/nucleos/REVIEW-*), con el mismo criterio.
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
# Escritura que conserva el formato del archivo
# --------------------------------------------------------------------------
# Varios archivos de contenido estan formateados a mano (varias claves por
# linea, tablas alineadas). Volcarlos con json.dumps los reescribe enteros y
# el diff del commit tapa el cambio real. Por eso se reemplaza en el TEXTO
# solo el tramo de cada valor que cambio, y se comprueba que el JSON
# resultante sea identico al objeto ya modificado. Si algo no calza, se cae
# al volcado completo, que siempre es correcto aunque sea feo.

def _fin_string(t, i):
    i += 1
    while t[i] != '"':
        i += 2 if t[i] == "\\" else 1
    return i + 1


def _fin_valor(t, i):
    if t[i] == '"':
        return _fin_string(t, i)
    if t[i] in "{[":
        hondo = 0
        while True:
            if t[i] == '"':
                i = _fin_string(t, i); continue
            if t[i] in "{[":
                hondo += 1
            elif t[i] in "}]":
                hondo -= 1
                if hondo == 0:
                    return i + 1
            i += 1
    j = i
    while t[j] not in ",}]" and not t[j].isspace():
        j += 1
    return j


def _saltar(t, i):
    while t[i].isspace():
        i += 1
    return i


def _tramo(t, ruta, i=0):
    """(inicio, fin) del valor que vive en `ruta` dentro del texto JSON."""
    i = _saltar(t, i)
    if not ruta:
        return i, _fin_valor(t, i)
    paso, resto = ruta[0], ruta[1:]
    if isinstance(paso, str):
        assert t[i] == "{"
        i = _saltar(t, i + 1)
        while t[i] != "}":
            fin_clave = _fin_string(t, i)
            clave = json.loads(t[i:fin_clave])
            i = _saltar(t, fin_clave)
            assert t[i] == ":"
            ini_val = _saltar(t, i + 1)
            fin_val = _fin_valor(t, ini_val)
            if clave == paso:
                return _tramo(t, resto, ini_val)
            i = _saltar(t, fin_val)
            if t[i] == ",":
                i = _saltar(t, i + 1)
        raise KeyError(paso)
    assert t[i] == "["
    i = _saltar(t, i + 1)
    n = 0
    while t[i] != "]":
        fin_val = _fin_valor(t, i)
        if n == paso:
            return _tramo(t, resto, i)
        n += 1
        i = _saltar(t, fin_val)
        if t[i] == ",":
            i = _saltar(t, i + 1)
    raise IndexError(paso)


def _sangria(t, i):
    """Sangria de la linea donde empieza el valor (no la columna del valor)."""
    ini = t.rfind("\n", 0, i) + 1
    linea = t[ini:i]
    return linea[:len(linea) - len(linea.lstrip())]


def _serializar(valor, sangria):
    """Objeto {idioma: texto} en varias lineas, con la sangria del lugar.

    Un valor que no es objeto (un string plano, p. ej.) se serializa tal cual.
    """
    if not isinstance(valor, dict):
        return json.dumps(valor, ensure_ascii=False)
    dentro = sangria + " "
    cuerpo = ",\n".join(dentro + json.dumps(k, ensure_ascii=False) + ": "
                        + json.dumps(v, ensure_ascii=False) for k, v in valor.items())
    return "{\n" + cuerpo + "\n" + sangria + "}"


def escribir(p, original, destino, cambios):
    """Escribe `destino` en `p` tocando solo los tramos de `cambios`.

    cambios: lista de (ruta, valor_nuevo), ruta como lista de claves/indices.
    """
    texto = original
    try:
        tramos = []
        for ruta, valor in cambios:
            ini, fin = _tramo(texto, ruta)
            tramos.append((ini, fin, valor))
        for ini, fin, valor in sorted(tramos, reverse=True):
            texto = texto[:ini] + _serializar(valor, _sangria(texto, ini)) + texto[fin:]
        if json.loads(texto) != destino:
            raise ValueError("el texto reescrito no coincide con el contenido")
        p.write_text(texto, encoding="utf-8")
        return True
    except Exception as e:
        print(f"  formato: {p.name} se reescribe entero ({e})", file=sys.stderr)
        p.write_text(json.dumps(destino, ensure_ascii=False, indent=1), encoding="utf-8")
        return False


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


def _traducir(valor, idioma, datos):
    """String plano u objeto -> objeto {idioma: texto, ...traducciones}.

    Devuelve (valor_nuevo, cuantas_claves_se_agregaron) o (valor, 0) si no
    hay nada que aplicar. No pisa ninguna clave que ya exista.
    """
    if isinstance(valor, str):
        vals = datos.get(valor)
        if not vals:
            return valor, 0
        nuevo = {idioma: valor}
        nuevo.update(vals)
        return nuevo, len(vals)
    if isinstance(valor, dict):
        original = valor.get(idioma)
        vals = datos.get(original) if original else None
        if not vals:
            return valor, 0
        faltan = {c: v for c, v in vals.items() if c not in valor}
        valor.update(faltan)
        return valor, len(faltan)
    return valor, 0


def _aplicar(cont, clave, ruta, idioma, datos, cambios):
    """Traduce cont[clave] y anota el cambio. Devuelve cuantas claves agrego."""
    nuevo, n = _traducir(cont[clave], idioma, datos)
    if n:
        cont[clave] = nuevo
        cambios.append((ruta, nuevo))
    return n


def _prosa_nucleo(d, idioma, datos, cambios):
    n = 0
    for clave in ("titulo", "descripcion", "promptCorreccion"):
        if clave in d:
            n += _aplicar(d, clave, [clave], idioma, datos, cambios)
    c = d.get("cuadroReferencia") or {}
    for clave in ("titulo", "notaPie"):
        if c.get(clave):
            n += _aplicar(c, clave, ["cuadroReferencia", clave], idioma, datos, cambios)
    for i in range(len(c.get("columnas", []))):
        n += _aplicar(c["columnas"], i, ["cuadroReferencia", "columnas", i], idioma, datos, cambios)
    for i, fila in enumerate(c.get("filas", [])):
        for j in range(len(fila)):
            n += _aplicar(fila, j, ["cuadroReferencia", "filas", i, j], idioma, datos, cambios)
    for i, e in enumerate(d.get("ejemplos", [])):
        if "texto" in e:
            n += _aplicar(e, "texto", ["ejemplos", i, "texto"], idioma, datos, cambios)
    for clave in ("notas", "errores", "autochequeo"):
        if isinstance(d.get(clave), list):
            for i in range(len(d[clave])):
                n += _aplicar(d[clave], i, [clave, i], idioma, datos, cambios)
    for i, r in enumerate(d.get("redemittel", [])):
        if "funcion" in r:
            n += _aplicar(r, "funcion", ["redemittel", i, "funcion"], idioma, datos, cambios)
    return n


def _prosa_aparicion(a, ruta_base, idioma, datos, cambios):
    n = 0
    if "subtitulo" in a:
        n += _aplicar(a, "subtitulo", ruta_base + ["subtitulo"], idioma, datos, cambios)
    m = a.get("mision") or {}
    if "consigna" in m:
        n += _aplicar(m, "consigna", ruta_base + ["mision", "consigna"], idioma, datos, cambios)
    for i in range(len(m.get("requisitos", []))):
        n += _aplicar(m["requisitos"], i, ruta_base + ["mision", "requisitos", i],
                      idioma, datos, cambios)
    for i, mt in enumerate(a.get("microtareas", [])):
        if "texto" in mt:
            n += _aplicar(mt, "texto", ruta_base + ["microtareas", i, "texto"],
                          idioma, datos, cambios)
    return n


def _prosa_semana(d, idioma, datos, cambios):
    n = 0
    for clave in ("titulo", "consigna", "promptCorreccion"):
        if clave in d:
            n += _aplicar(d, clave, [clave], idioma, datos, cambios)
    for clave in ("requisitos", "microtareas", "autochequeo"):
        if isinstance(d.get(clave), list):
            for i in range(len(d[clave])):
                n += _aplicar(d[clave], i, [clave, i], idioma, datos, cambios)
    return n


def volcar_repaso(base, por_combo):
    """Escribe las traducciones en las semanas especiales. Devuelve (archivos, celdas)."""
    archivos = celdas = 0
    for p in sorted(base.joinpath("nucleos").glob("*.json")):
        original = p.read_text(encoding="utf-8")
        d = json.loads(original)
        if d.get("_tipo") != "semana_especial":
            continue
        datos = por_combo.get((d["idioma"], d.get("nivel")))
        if not datos:
            continue
        cambios = []
        n = _prosa_semana(d, d["idioma"], datos, cambios)
        if n:
            escribir(p, original, d, cambios)
            archivos += 1
            celdas += n
    return archivos, celdas


def volcar_prosa(base, por_combo):
    """Escribe la prosa traducida en nucleos y ocurrencias. Devuelve (archivos, celdas)."""
    archivos = celdas = 0
    for p in sorted(base.joinpath("nucleos").glob("*.json")):
        original = p.read_text(encoding="utf-8")
        d = json.loads(original)
        if d.get("_tipo"):
            continue
        datos = por_combo.get((d["idioma"], d["nivel"]))
        if not datos:
            continue
        cambios = []
        n = _prosa_nucleo(d, d["idioma"], datos, cambios)
        if n:
            escribir(p, original, d, cambios)
            archivos += 1
            celdas += n

    for p in sorted(base.joinpath("ocurrencias").glob("*.json")):
        original = p.read_text(encoding="utf-8")
        d = json.loads(original)
        datos = por_combo.get((d["idioma"], d["nivel"]))
        if not datos:
            continue
        cambios = []
        n = 0
        for i, a in enumerate(d["apariciones"]):
            n += _prosa_aparicion(a, ["apariciones", i], d["idioma"], datos, cambios)
        if n:
            escribir(p, original, d, cambios)
            archivos += 1
            celdas += n
    return archivos, celdas


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("excel")
    ap.add_argument("--contenido", default="../contenido")
    a = ap.parse_args()
    base = Path(a.contenido)
    wb = openpyxl.load_workbook(a.excel, data_only=True)

    por_idioma = {}
    por_combo = {}
    por_repaso = {}
    for hoja in wb.sheetnames:
        if hoja.upper().startswith("ÍNDICE") or hoja.upper().startswith("INDICE"):
            continue
        if hoja.startswith("Prosa ") or hoja.startswith("Repaso "):
            partes = hoja.split()
            if len(partes) != 3:
                print(f"Hoja de prosa con nombre inesperado, se saltea: {hoja}", file=sys.stderr)
                continue
            idi, niv = partes[1].lower(), partes[2]
            destino = por_repaso if hoja.startswith("Repaso ") else por_combo
            destino[(idi, niv)] = leer_hoja_prosa(wb[hoja], idi)
            continue
        idi = COD.get(hoja)
        if not idi:
            continue
        por_idioma[idi] = leer_hoja(wb[hoja], idi)

    n_red = n_voc = 0
    for p in base.joinpath("nucleos").glob("*.json"):
        original = p.read_text(encoding="utf-8")
        d = json.loads(original)
        if d.get("_tipo"):
            continue
        idi, niv = d["idioma"], d["nivel"]
        datos = por_idioma.get(idi, {})
        cambios = []
        for i, r in enumerate(d.get("redemittel", [])):
            k = (niv, "Expresión", limpio(r["expresion"]))
            if k in datos:
                nuevas = {cod: val for cod, val in datos[k].items()
                          if cod not in r["traducciones"]}
                if nuevas:
                    r["traducciones"].update(nuevas)
                    cambios.append((["redemittel", i, "traducciones"], r["traducciones"]))
                    n_red += len(nuevas)
        if cambios:
            escribir(p, original, d, cambios)

    for p in base.joinpath("packs").glob("*.json"):
        original = p.read_text(encoding="utf-8")
        d = json.loads(original)
        idi, niv = d["idioma"], d["nivel"]
        datos = por_idioma.get(idi, {})
        cambios = []
        for i, v in enumerate(d.get("vocabulario", [])):
            k = (niv, "Vocabulario", limpio(v["item"]))
            if k in datos:
                nuevas = {cod: val for cod, val in datos[k].items()
                          if cod not in v["traducciones"]}
                if nuevas:
                    v["traducciones"].update(nuevas)
                    cambios.append((["vocabulario", i, "traducciones"], v["traducciones"]))
                    n_voc += len(nuevas)
        if cambios:
            escribir(p, original, d, cambios)

    n_archivos, n_celdas = volcar_prosa(base, por_combo)
    n_sem, n_celdas_sem = volcar_repaso(base, por_repaso)

    print(f"{n_red} expresiones y {n_voc} items de vocabulario actualizados en el contenido fuente.",
          file=sys.stderr)
    print(f"Prosa A2/B1: {n_celdas} traducciones escritas en {n_archivos} archivos.",
          file=sys.stderr)
    print(f"Semanas de repaso A2/B1: {n_celdas_sem} traducciones escritas en {n_sem} archivos.",
          file=sys.stderr)
    print("Volvé a correr exportar_traduccion_maestro.py: esas celdas ya van a salir verdes.",
          file=sys.stderr)


if __name__ == "__main__":
    main()
