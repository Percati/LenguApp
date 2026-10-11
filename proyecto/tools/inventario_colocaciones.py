#!/usr/bin/env python3
"""Inventario de COLOCACIONES VERBALES del vocabulario (prompt 23, tarea 1). SOLO LECTURA.

Complementa a inventario_verbos.py (prompt 20), que cubrio los items cuyo nucleo ES
el verbo (opcion A). Aca se miran los items SIN `claseVerbo` de mas de una palabra para
separar tres cosas:

  1. colocacion verbal   -> tiene un verbo como nucleo (die Frage stellen, to keep a promise)
  2. no verbal           -> sustantivo, adjetivo, adverbio (die Amtssprache, a canteen)
  3. dudoso              -> las senales se contradicen: hay que leerlo a mano

De las colocaciones verbales extrae el NUCLEO en infinitivo y agrupa, marcando si ese
nucleo ya existe como verbo etiquetado o si seria nuevo en la vista Verbos.

  python3 -I proyecto/tools/inventario_colocaciones.py --contenido proyecto/contenido
  python3 -I proyecto/tools/inventario_colocaciones.py --contenido proyecto/contenido \
      --salida /ruta/dir     # escribe informe.md, nucleos.csv y dudosos.md ahi

No escribe nada en el contenido. Todo lo que sale de aca son CANDIDATOS: el etiquetado
real se hace leyendo item por item contra la tabla maestra (P11).
"""
import argparse, csv, io, json, re, sys, unicodedata
from collections import Counter, defaultdict
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from inventario_verbos import PREP_DE_REAL, cargar_packs, forma_de, norm   # noqa: E402

# ------------------------------------------------------------------ aleman
INF_DE = re.compile(r"(en|ern|eln)$")
INF_IRREG_DE = {"sein", "tun"}
# participios que terminan en -en y NO son infinitivo (lista cerrada, leida a ojo)
PARTICIPIOS_DE = {"gewachsen", "gerissen", "gefallen_", "geworfen", "geschickt_", "vergriffen",
                  "eingespielt_", "angeschlagen", "ausgebrannt_", "eingerostet_", "voreingenommen",
                  "ausgewogen", "erwachsen", "zusammen", "gestern", "modern", "draussen", "offen",
                  "eigen", "unterschrieben", "gegeben_", "genommen", "verloren", "geschrieben"}
# NUCLEOS EXCLUIDOS (propuesta del informe, lista cerrada): auxiliares, copulas y modales.
# No aportan un verbo a la vista: lo que se aprende en la colocacion es el adjetivo o el
# sustantivo, no el verbo.
EXCLUIDOS_DE = {"sein", "haben", "werden", "lassen", "können", "müssen", "sollen", "wollen",
                "dürfen", "mögen"}

# ------------------------------------------------------------------ ingles
# `let` NO se excluye: sus casos son phrasal (let down, let off), no causativos (correccion de
# Fer, 11-oct-2026). Los dos causativos que quedan («to let someone know», «to let something
# rest») no se indexan: el infinitivo incrustado (know, rest) no es lo que ensena el item.
EXCLUIDOS_EN = {"be", "have", "get", "do"}
# arranques que no son el verbo: articulos, pronombres y adverbios que se cuelan delante
NO_VERBO_EN = {"something", "someone", "somebody", "your", "yourself", "oneself", "one's", "it", "its",
               "a", "an", "the", "my", "their", "in", "on", "out", "really", "just", "never", "always",
               "still", "only", "even", "also", "again", "both", "all", "no", "not"}


def variantes(item):
    """'die Ruecklage / Ruecklagen bilden' -> las dos ramas; un item sin barra -> una sola."""
    if " / " not in item:
        return [item.strip()]
    return [v.strip() for v in item.split(" / ") if v.strip()]


def glosa_verbal_en(g):
    return (g or "").strip().lower().startswith(("to ", "not to "))


def glosa_verbal_es(g):
    g = (g or "").strip().lower()
    return bool(re.match(r"^(no |nunca )?\w+(ar|er|ir|arse|erse|irse)\b", g))


def nucleo_de(variante):
    """Ultimo token en infinitivo de una colocacion alemana, o '' si no hay.

    Antes de mirar el ultimo token se quitan los comodines de valencia y la preposicion
    que rige el verbo: 'Ruecksicht nehmen auf' -> nehmen, 'zustaendig sein fuer' -> sein.
    """
    toks = variante.replace("(sich)", "sich").split()
    while toks and (toks[-1] in ("etwas", "jemanden", "jemandem", "jemand", "sich")
                    or (len(toks) > 2 and toks[-1] in PREP_DE_REAL)):
        toks = toks[:-1]
    if not toks:
        return ""
    ult = toks[-1]
    if not ult[:1].islower() or len(ult) < 3:
        return ""
    if ult in PARTICIPIOS_DE:
        return ""
    if ult == "lassen" and len(toks) > 1:
        # causativo: el verbo que se aprende es el infinitivo incrustado («sich impfen lassen»
        # ensena impfen), no lassen. Decision de Fer, 11-oct-2026.
        return nucleo_de(" ".join(toks[:-1]))
    if INF_DE.search(ult) or ult in INF_IRREG_DE:
        return ult
    return ""


def nucleo_en(variante, primera=True):
    """Verbo de una rama inglesa. Una rama que no es la primera tiene que traer su propio
    «to»: en «to update / an update» la segunda rama es un sustantivo, no un verbo."""
    t = variante.strip().lower()
    con_to = t.startswith(("to ", "not to "))
    if not con_to and not primera:
        return ""
    t = re.sub(r"^(not )?to ", "", t)
    t = re.sub(r"\([^)]*\)", " ", t)
    toks = [w.strip(",.;:!?") for w in t.split()]
    toks = [w for w in toks if w]
    while toks and (toks[0].endswith("ly") or toks[0] in NO_VERBO_EN):
        toks = toks[1:]
    return toks[0] if toks else ""


def nucleos_etiquetados(filas):
    """Nucleo de cada verbo YA etiquetado, por idioma (para saber que seria nuevo)."""
    out = defaultdict(set)
    for idi, niv, anio, pack, it in filas:
        if not it.get("claseVerbo"):
            continue
        if idi == "de":
            f = forma_de(it["item"])
            n = f.get("nucleo") or nucleo_de(it["item"])
        else:
            n = nucleo_en(it["item"])
        if n:
            out[idi].add(n)
    return out


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--contenido", default="proyecto/contenido")
    ap.add_argument("--salida", help="directorio donde escribir informe.md, nucleos.csv y dudosos.md")
    ap.add_argument("--top", type=int, default=30)
    a = ap.parse_args()
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")

    filas = cargar_packs(a.contenido)
    etq = nucleos_etiquetados(filas)

    # item unico -> datos (el vocabulario se repite entre packs: se cuenta una vez)
    uni = {}
    for idi, niv, anio, pack, it in filas:
        k = (idi, it["item"])
        e = uni.setdefault(k, {"idioma": idi, "item": it["item"], "cv": it.get("claseVerbo"),
                               "tr": it["traducciones"], "apar": 0, "niveles": set()})
        e["apar"] += 1
        e["niveles"].add(niv)

    verbales, noverbales, dudosos = [], [], []
    for e in uni.values():
        if e["cv"] or len(e["item"].split()) < 2:
            continue
        idi = e["idioma"]
        gen, ges = e["tr"].get("en"), e["tr"].get("es")
        nucs, forma_ok = [], False
        for i, v in enumerate(variantes(e["item"])):
            n = nucleo_de(v) if idi == "de" else nucleo_en(v, primera=(i == 0))
            if n:
                forma_ok = True
                if n not in nucs:
                    nucs.append(n)
        if idi == "de":
            glosa_ok = glosa_verbal_en(gen) or glosa_verbal_es(ges)
        else:
            glosa_ok = e["item"].strip().lower().startswith(("to ", "not to "))
            if not glosa_ok:
                # sin «to» no es verbal; salvo que la glosa castellana sea un infinitivo
                # (gerundios sustantivados tipo «batch cooking»): eso va a dudosos.
                glosa_ok = glosa_verbal_es(ges)
                forma_ok = False
            else:
                forma_ok = forma_ok and bool(nucs)
        e["nucleos"] = nucs
        if forma_ok and glosa_ok:
            verbales.append(e)
        elif forma_ok or glosa_ok:
            dudosos.append(e)
        else:
            noverbales.append(e)

    # ---------------------------------------------------------------- agrupado
    grupos = defaultdict(list)          # (idioma, nucleo) -> colocaciones
    for e in verbales:
        for n in e["nucleos"]:
            grupos[(e["idioma"], n)].append(e)
    for k in grupos:
        grupos[k].sort(key=lambda e: norm(e["item"]))

    out = []
    P = out.append
    P("# Inventario de colocaciones verbales (prompt 23, tarea 1)\n")
    P("Items del vocabulario **sin `claseVerbo`** y de mas de una palabra, por idioma.\n")
    P("| idioma | items unicos | ya etiquetados | sin etiquetar | de >1 palabra | colocacion verbal | no verbal | dudoso |")
    P("|---|---|---|---|---|---|---|---|")
    for idi in ("de", "en"):
        tot = [e for e in uni.values() if e["idioma"] == idi]
        et = [e for e in tot if e["cv"]]
        sin = [e for e in tot if not e["cv"]]
        multi = [e for e in sin if len(e["item"].split()) > 1]
        P(f"| {idi.upper()} | {len(tot)} | {len(et)} | {len(sin)} | {len(multi)} | "
          f"{sum(1 for e in verbales if e['idioma']==idi)} | "
          f"{sum(1 for e in noverbales if e['idioma']==idi)} | "
          f"{sum(1 for e in dudosos if e['idioma']==idi)} |")
    P("")
    for idi, excl in (("de", EXCLUIDOS_DE), ("en", EXCLUIDOS_EN)):
        ns = {n for (i, n) in grupos if i == idi}
        nuevos = {n for n in ns if n not in etq[idi]}
        ns_u = {n for n in ns if n not in excl}
        nuevos_u = {n for n in nuevos if n not in excl}
        colo_excl = sum(len(grupos[(idi, n)]) for n in ns & excl)
        P(f"## {idi.upper()}\n")
        P(f"- nucleos distintos: **{len(ns)}**; ya etiquetados como verbo: {len(ns & etq[idi])}; "
          f"**nuevos: {len(nuevos)}**")
        P(f"- quitando la lista de exclusion ({', '.join(sorted(excl & ns)) or 'ninguno presente'}): "
          f"nucleos **{len(ns_u)}**, de los cuales **nuevos {len(nuevos_u)}**; "
          f"se van {colo_excl} colocaciones")
        P(f"- verbos de la vista hoy: {len(etq[idi])} nucleos etiquetados -> "
          f"**+{len(nuevos_u)}** con esta ronda")
        P(f"- nucleos con una sola colocacion: {sum(1 for n in ns_u if len(grupos[(idi,n)])==1)}\n")
        if idi == "de":
            P("- `lassen` esta en la lista de exclusion pero no aparece como nucleo: la regla del "
              "infinitivo incrustado lo reemplaza antes («sich impfen lassen» -> impfen).\n")
        P(f"### {idi.upper()}: los {a.top} nucleos mas frecuentes (sin los excluidos)\n")
        P("| nucleo | nuevo | colocaciones | las colocaciones |")
        P("|---|---|---|---|")
        for n in sorted(ns_u, key=lambda n: (-len(grupos[(idi, n)]), n))[:a.top]:
            g = grupos[(idi, n)]
            P(f"| `{n}` | {'si' if n in nuevos_u else 'no'} | {len(g)} | " +
              "; ".join(e["item"] for e in g[:6]) + ("; ..." if len(g) > 6 else "") + " |")
        P("")
        P(f"### {idi.upper()}: nucleos excluidos por ser auxiliar / copula / modal\n")
        P("| nucleo | colocaciones | ejemplos |")
        P("|---|---|---|")
        for n in sorted(ns & excl, key=lambda n: -len(grupos[(idi, n)])):
            g = grupos[(idi, n)]
            P(f"| `{n}` | {len(g)} | " + "; ".join(e["item"] for e in g[:4]) + " |")
        P("")
    sal = "\n".join(out)
    print(sal)

    if a.salida:
        d = Path(a.salida)
        d.mkdir(parents=True, exist_ok=True)
        (d / "informe-colocaciones.md").write_text(sal + "\n", encoding="utf-8")
        buf = io.StringIO()
        w = csv.writer(buf, lineterminator="\n")
        w.writerow(["idioma", "nucleo", "excluido", "ya_etiquetado", "colocaciones", "apariciones",
                    "niveles", "lista_colocaciones"])
        for (idi, n), g in sorted(grupos.items()):
            excl = n in (EXCLUIDOS_DE if idi == "de" else EXCLUIDOS_EN)
            w.writerow([idi, n, int(excl), int(n in etq[idi]), len(g), sum(e["apar"] for e in g),
                        "+".join(sorted({x for e in g for x in e["niveles"]})),
                        " | ".join(e["item"] for e in g)])
        (d / "nucleos.csv").write_text(buf.getvalue(), encoding="utf-8")
        du = ["# Dudosos del inventario de colocaciones (hay que leerlos a mano)\n",
              "Las senales se contradicen: la forma parece verbal y la glosa no, o al reves.\n"]
        for idi in ("de", "en"):
            es = sorted((e for e in dudosos if e["idioma"] == idi), key=lambda e: norm(e["item"]))
            du.append(f"\n## {idi.upper()} ({len(es)})\n")
            du += [f"- {e['item']} · {e['tr'].get('en') or ''} · {e['tr'].get('es') or ''}" for e in es]
        (d / "dudosos.md").write_text("\n".join(du) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
