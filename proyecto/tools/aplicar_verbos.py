#!/usr/bin/env python3
"""Aplica la tabla maestra de verbos (contenido/verbos/{de,en}.json) a TODAS las
repeticiones del item en contenido/packs/*.json (prompt 20, paso 3).

Toca cinco campos de vocabulario[]: `claseVerbo` y `reccion` (el verbo propio, opcion A) y
Las entradas con `referencia: true` quedan fuera: son verbos de consulta de la biblioteca, no
anotaciones de items del contenido, asi que no se aplican a ningun pack.

`nucleoVerbal` + `claseNucleo` + `reccionNucleo` (el nucleo verbal de una colocacion,
prompt 23). Todo lo demas
(traducciones, notas, orden de los items, formato del archivo) queda igual byte
a byte: se conserva el fin de linea y la presencia/ausencia de salto final de cada
pack, y antes de escribir se comprueba que reserializar el pack SIN cambios da
exactamente el mismo texto.

  python3 -I proyecto/tools/aplicar_verbos.py --contenido proyecto/contenido           # comprueba (exit 1 si hay diferencias)
  python3 -I proyecto/tools/aplicar_verbos.py --contenido proyecto/contenido --escribir # aplica

Reglas:
  - item en la tabla     -> claseVerbo = la de la tabla; reccion = la de la tabla o se quita.
  - item en reccionOtros -> (sustantivos y frases fuera de la vista Verbos) reccion normalizada o se quita;
                            sin claseVerbo.
  - item en `nucleos[].colocaciones` -> nucleoVerbal = el nucleo; claseNucleo y reccionNucleo = los
                            del nucleo. Nunca junto con claseVerbo: una colocacion no se lista. Si
                            ademas esta en reccionOtros, conserva esa reccion (es la del sintagma).
  - cualquier otro item con alguno de los cinco campos es un ERROR (nada se decide por heuristica).
  - item que coincide con una entrada `referencia: true` -> ERROR: la entrada dejo de ser de consulta
                            y hay que promoverla (quitarle `referencia` y completar su anotacion).
                            Sin esta guarda la aparicion se quedaria sin anotar en silencio, porque
                            la entrada de referencia no esta en el mapa que se aplica.

  python3 -I proyecto/tools/aplicar_verbos.py --autotest   # prueba de la guarda de referencia
"""
import argparse, json, re, sys, unicodedata
from pathlib import Path


def norm(s):
    return " ".join(unicodedata.normalize("NFC", s).split()).casefold()


def clave(idioma, item):
    k = norm(item)
    if idioma == "en":
        k = k[7:] if k.startswith("not to ") else (k[3:] if k.startswith("to ") else k)
    return k


def cargar_tablas(base):
    t = {}
    for idi in ("de", "en"):
        d = json.loads(Path(base, "verbos", f"{idi}.json").read_text(encoding="utf-8"))
        cols = {}
        for e in d.get("nucleos", []):
            for c in e["colocaciones"]:
                k = clave(idi, c)
                if k in cols:
                    raise SystemExit(f"{idi}: «{c}» figura en dos nucleos ({cols[k]['nucleo']} y "
                                     f"{e['nucleo']}); un item lleva un solo nucleoVerbal")
                cols[k] = e
        # Las entradas de referencia (`referencia: true`) son verbos de consulta que NO anotan
        # ningun item del contenido: no se aplican a los packs ni se exige que aparezcan en uno.
        t[idi] = ({e["forma"]: e for e in d["verbos"] if not e.get("referencia")},
                  d.get("reccionOtros", {}), cols,
                  # La guarda de referencia compara el TEXTO del item, no la clave: la clave
                  # inglesa quita el «to», asi que el verbo «to delay» chocaria con el
                  # sustantivo «delay», que es otro item.
                  {norm(e["item"]): e for e in d["verbos"] if e.get("referencia")})
    return t


def serializar(d, eol, final, compacto=False):
    """Los packs tienen dos estilos: indent=1 estandar, o compacto (cabecera en una linea
    y un item por linea). Se respeta el de cada archivo."""
    if not compacto:
        s = json.dumps(d, indent=1, ensure_ascii=False).replace("\n", eol)
    else:
        dj = lambda o: json.dumps(o, ensure_ascii=False)
        cab = ", ".join(f"{dj(k)}: {dj(v)}" for k, v in d.items() if k != "vocabulario")
        n = len(d["vocabulario"])
        filas = ["  " + dj(it) + ("," if i < n - 1 else "") for i, it in enumerate(d["vocabulario"])]
        s = eol.join(["{", " " + cab + ",", ' "vocabulario": ['] + filas + [" ]", "}"])
    return s + (eol if final else "")


CAMPOS = ("reccion", "claseVerbo", "nucleoVerbal", "claseNucleo", "reccionNucleo")


def reordenar(it, valores):
    """item, prioridad, los campos de CAMPOS que correspondan, resto en su orden original."""
    nuevo = {}
    for k, v in it.items():
        if k in CAMPOS:
            continue
        nuevo[k] = v
        if k == "prioridad":
            for c in CAMPOS:
                if valores.get(c) is not None:
                    nuevo[c] = valores[c]
    return nuevo


def cirugia_lineas(raw, eol, viejos, nuevos):
    """Para los packs escritos a mano (un item por linea, con separadores propios): se edita
    solo el texto de cada linea que cambia, sin reserializar el resto."""
    lineas = raw.split(eol)
    idx = [i for i, l in enumerate(lineas) if l.startswith('  {"item"')]
    if len(idx) != len(viejos):
        raise ValueError("no coincide el numero de lineas de item")
    dj = lambda o: json.dumps(o, ensure_ascii=False)
    for i, viejo, nuevo in zip(idx, viejos, nuevos):
        if list(viejo.items()) == list(nuevo.items()):
            continue
        coma = "," if lineas[i].endswith(",") else ""
        t = lineas[i][:len(lineas[i]) - len(coma)]
        for c in ("reccion", "nucleoVerbal", "reccionNucleo"):
            t = re.sub(r',\s*"%s":\s*"(?:[^"\\]|\\.)*"' % c, "", t)
        for c in ("claseVerbo", "claseNucleo"):
            t = re.sub(r',\s*"%s":\s*\[[^\]]*\]' % c, "", t)
        ins = ""
        for c in CAMPOS:
            if c in nuevo:
                ins += ", " + dj(c) + ": " + dj(nuevo[c])
        t = re.sub(r'("prioridad":\s*"[a-z]+")', lambda m: m.group(1) + ins, t, count=1)
        if list(json.loads(t).items()) != list(nuevo.items()):
            raise ValueError("la edicion de linea no reproduce el item: " + t[:80])
        lineas[i] = t + coma
    return eol.join(lineas)


def revisar(contenido, escribir):
    """Recorre los packs. Devuelve (packs con diferencias, items anotados, colocaciones, errores)."""
    class _A:
        pass
    a = _A()
    a.contenido, a.escribir = contenido, escribir
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    tablas = cargar_tablas(a.contenido)
    vistos = {"de": set(), "en": set()}
    errores, cambiados, items_tag, items_nuc = [], 0, 0, 0
    for p in sorted(Path(a.contenido, "packs").glob("*.json")):
        raw = p.read_bytes().decode("utf-8")
        eol = "\r\n" if "\r\n" in raw else "\n"
        final = raw.endswith(eol)
        d = json.loads(raw)
        modo = "std"
        if serializar(d, eol, final) != raw:
            modo = "compacto" if serializar(d, eol, final, True) == raw else "lineas"
        idi = d["idioma"]
        tabla, otros, cols, refs = tablas[idi]
        nuevos = []
        for it in d["vocabulario"]:
            k = clave(idi, it["item"])
            if k in tabla:
                e = tabla[k]
                vistos[idi].add(k)
                if k in cols:
                    errores.append(f"{p.name}: «{it['item']}» esta en `verbos` y en `nucleos`: "
                                   "un item es verbo propio o colocacion, no las dos cosas")
                nuevo = reordenar(it, {"claseVerbo": e["claseVerbo"], "reccion": e.get("reccion")})
                items_tag += 1
            elif k in cols:
                e = cols[k]
                vistos[idi].add(k)
                # una colocacion puede tener su propia reccion en reccionOtros («Rücksicht nehmen
                # auf»): esa es del sintagma y se conserva, aparte de la del nucleo.
                nuevo = reordenar(it, {"reccion": otros.get(k), "nucleoVerbal": e["nucleo"],
                                       "claseNucleo": e["claseVerbo"],
                                       "reccionNucleo": e.get("reccion")})
                items_nuc += 1
            elif k in otros:
                vistos[idi].add(k)
                nuevo = reordenar(it, {"reccion": otros[k]})
            elif norm(it["item"]) in refs:
                errores.append(f"{p.name}: «{it['item']}» es una entrada de referencia "
                               f"(«{refs[norm(it['item'])]['forma']}») y ya aparece en un pack: quitale "
                               "`referencia: true` y completa su anotacion")
                nuevo = it
            else:
                if any(c in it for c in CAMPOS):
                    errores.append(f"{p.name}: «{it['item']}» tiene {'/'.join(c for c in CAMPOS if c in it)} "
                                   "y no esta en la tabla")
                nuevo = it
            nuevos.append(nuevo)
        if nuevos != d["vocabulario"] or any(list(x) != list(y) for x, y in zip(nuevos, d["vocabulario"])):
            cambiados += 1
            if a.escribir:
                if modo == "lineas":
                    try:
                        texto = cirugia_lineas(raw, eol, d["vocabulario"], nuevos)
                    except ValueError as ex:
                        errores.append(f"{p.name}: {ex}")
                        continue
                else:
                    d["vocabulario"] = nuevos
                    texto = serializar(d, eol, final, modo == "compacto")
                p.write_bytes(texto.encode("utf-8"))
    for idi in ("de", "en"):
        tabla, otros, cols, refs = tablas[idi]
        for k in list(tabla) + list(otros) + list(cols):
            if k not in vistos[idi]:
                errores.append(f"{idi}: «{k}» esta en la tabla y no aparece en ningun pack")
    return cambiados, items_tag, items_nuc, errores


def autotest():
    """La guarda de la tarea 2: un pack cuyo item coincide con una entrada de referencia tiene
    que hacer saltar el error. Se arma un contenido minimo en un directorio temporal."""
    import tempfile
    fallos = []
    tabla = {"idioma": "de", "version": 1, "verbos": [
        {"forma": "aufstehen", "item": "aufstehen", "base": "aufstehen",
         "claseVerbo": ["trennbar"], "raiz": "stehen", "referencia": True, "nivelFuente": "A1",
         "traducciones": {"es": "levantarse", "en": "to get up", "fr": "se lever",
                          "it": "alzarsi", "pt": "levantar-se"}},
        {"forma": "abholen", "item": "abholen", "base": "abholen", "claseVerbo": ["trennbar"]},
    ]}
    vacia = {"idioma": "en", "version": 1, "verbos": []}
    pack = {"idioma": "de", "nivel": "A2", "anio": 2027, "vocabulario": [
        {"item": "abholen", "prioridad": "nucleo", "traducciones": {"es": "recoger"}}]}
    for con_referencia, espera in ((True, 1), (False, 0)):
        with tempfile.TemporaryDirectory() as d:
            base = Path(d)
            (base / "verbos").mkdir(); (base / "packs").mkdir()
            voc = list(pack["vocabulario"])
            if con_referencia:
                voc.append({"item": "aufstehen", "prioridad": "ampliacion",
                            "traducciones": {"es": "levantarse"}})
            p = dict(pack, vocabulario=voc)
            (base / "verbos" / "de.json").write_text(json.dumps(tabla, ensure_ascii=False, indent=1),
                                                     encoding="utf-8")
            (base / "verbos" / "en.json").write_text(json.dumps(vacia, ensure_ascii=False, indent=1),
                                                     encoding="utf-8")
            (base / "packs" / "de-A2-2027-T01-1.json").write_text(
                json.dumps(p, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
            cambiados, tag, nuc, errores = revisar(str(base), False)
            refs = [e for e in errores if "entrada de referencia" in e]
            if len(refs) != espera:
                fallos.append(f"con_referencia={con_referencia}: esperaba {espera} errores de "
                              f"referencia y hubo {len(refs)}: {errores}")
            if not con_referencia and errores:
                fallos.append(f"sin el item de referencia no deberia haber errores: {errores}")
    print(f"autotest de la guarda de referencia: 2 casos, {len(fallos)} fallos")
    for f in fallos:
        print("  !", f)
    return len(fallos)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--contenido", default="proyecto/contenido")
    ap.add_argument("--escribir", action="store_true")
    ap.add_argument("--autotest", action="store_true", help="prueba la guarda de `referencia: true`")
    a = ap.parse_args()
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    if a.autotest:
        sys.exit(1 if autotest() else 0)
    cambiados, items_tag, items_nuc, errores = revisar(a.contenido, a.escribir)
    print(f"packs con diferencias: {cambiados}; apariciones etiquetadas: {items_tag}; "
          f"colocaciones con nucleo: {items_nuc}; errores: {len(errores)}")
    for e in errores:
        print("  !", e)
    if errores or (cambiados and not a.escribir):
        sys.exit(1)


if __name__ == "__main__":
    main()
