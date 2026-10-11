#!/usr/bin/env python3
"""Aplica la tabla maestra de verbos (contenido/verbos/{de,en}.json) a TODAS las
repeticiones del item en contenido/packs/*.json (prompt 20, paso 3).

Solo toca dos campos de vocabulario[]: `claseVerbo` y `reccion`. Todo lo demas
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
  - cualquier otro item con claseVerbo o reccion es un ERROR (nada se decide por heuristica).
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
        t[idi] = ({e["forma"]: e for e in d["verbos"]}, d.get("reccionOtros", {}))
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


def reordenar(it, clase, reccion):
    """item, prioridad, [reccion], [claseVerbo], resto en su orden original."""
    nuevo = {}
    for k, v in it.items():
        if k in ("reccion", "claseVerbo"):
            continue
        nuevo[k] = v
        if k == "prioridad":
            if reccion is not None:
                nuevo["reccion"] = reccion
            if clase is not None:
                nuevo["claseVerbo"] = clase
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
        t = re.sub(r',\s*"reccion":\s*"(?:[^"\\]|\\.)*"', "", t)
        t = re.sub(r',\s*"claseVerbo":\s*\[[^\]]*\]', "", t)
        ins = ""
        if "reccion" in nuevo:
            ins += ", " + '"reccion": ' + dj(nuevo["reccion"])
        if "claseVerbo" in nuevo:
            ins += ", " + '"claseVerbo": ' + dj(nuevo["claseVerbo"])
        t = re.sub(r'("prioridad":\s*"[a-z]+")', lambda m: m.group(1) + ins, t, count=1)
        if list(json.loads(t).items()) != list(nuevo.items()):
            raise ValueError("la edicion de linea no reproduce el item: " + t[:80])
        lineas[i] = t + coma
    return eol.join(lineas)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--contenido", default="proyecto/contenido")
    ap.add_argument("--escribir", action="store_true")
    a = ap.parse_args()
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    tablas = cargar_tablas(a.contenido)
    vistos = {"de": set(), "en": set()}
    errores, cambiados, items_tag = [], 0, 0
    for p in sorted(Path(a.contenido, "packs").glob("*.json")):
        raw = p.read_bytes().decode("utf-8")
        eol = "\r\n" if "\r\n" in raw else "\n"
        final = raw.endswith(eol)
        d = json.loads(raw)
        modo = "std"
        if serializar(d, eol, final) != raw:
            modo = "compacto" if serializar(d, eol, final, True) == raw else "lineas"
        idi = d["idioma"]
        tabla, otros = tablas[idi]
        nuevos = []
        for it in d["vocabulario"]:
            k = clave(idi, it["item"])
            if k in tabla:
                e = tabla[k]
                vistos[idi].add(k)
                nuevo = reordenar(it, e["claseVerbo"], e.get("reccion"))
                items_tag += 1
            elif k in otros:
                vistos[idi].add(k)
                nuevo = reordenar(it, None, otros[k])
            else:
                if "claseVerbo" in it or "reccion" in it:
                    errores.append(f"{p.name}: «{it['item']}» tiene claseVerbo/reccion y no esta en la tabla")
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
        tabla, otros = tablas[idi]
        for k in list(tabla) + list(otros):
            if k not in vistos[idi]:
                errores.append(f"{idi}: «{k}» esta en la tabla y no aparece en ningun pack")
    print(f"packs con diferencias: {cambiados}; apariciones etiquetadas: {items_tag}; errores: {len(errores)}")
    for e in errores:
        print("  !", e)
    if errores or (cambiados and not a.escribir):
        sys.exit(1)


if __name__ == "__main__":
    main()
