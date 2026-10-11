#!/usr/bin/env python3
"""Inventario de verbos del vocabulario (prompt 20, paso 1). SOLO LECTURA.

Lee contenido/packs/*.json (el vocabulario[] vive solo en los packs), extrae
los items verbales y los deduplica por (idioma, forma normalizada). No escribe
nada en el contenido: con --csv / --dudosos escribe informes en la ruta dada.

  python3 -I proyecto/tools/inventario_verbos.py --contenido proyecto/contenido
  python3 -I proyecto/tools/inventario_verbos.py --contenido proyecto/contenido \
      --csv /ruta/inventario.csv --dudosos /ruta/dudosos.md

Todo lo que sale de aca son CANDIDATOS por forma (listas cerradas, sin
diccionario): el etiquetado real del paso 3 se hace leyendo item por item.
"""
import argparse, csv, io, json, re, sys, unicodedata
from collections import Counter, defaultdict
from pathlib import Path


# ---------------------------------------------------------------- aleman
ART = re.compile(r"^(der|die|das|ein|eine|einen|einem|einer|den|dem|des|kein|keine|mein|meine|sein|seine|unser|unsere)\s", re.I)
PREP_DE = {"an", "auf", "aus", "bei", "durch", "für", "fuer", "gegen", "in", "mit", "nach", "über",
           "ueber", "um", "unter", "von", "vor", "zu", "zwischen", "hinter", "neben", "gegenüber",
           "wegen", "bis", "ohne", "als", "wie"}
PREP_DE_REAL = PREP_DE - {"bis", "ohne", "als", "wie", "wegen"}
VALENCIA = ["jemandem etwas", "jemanden etwas", "jemandem", "jemanden", "etwas", "jemand", "einer sache"]
# prefijos separables sin ambiguedad / ambiguos (separables o inseparables segun el verbo)
SEP_SEGURO = ["ab", "an", "auf", "aus", "bei", "ein", "fest", "los", "mit", "nach", "vor", "weg", "zu", "zurück",
              "zurueck", "zusammen", "weiter", "hoch", "herunter", "heraus", "hinaus", "herein", "hinein",
              "hervor", "voran", "vorbei", "vorher", "fern", "frei", "kennen", "statt", "teil", "auseinander",
              "aneinander", "zurecht", "hin", "her", "dar", "nieder", "los", "bereit", "klar", "offen",
              "fertig", "bekannt", "wach", "stand", "heim", "krank", "schön", "gut", "leer", "voll",
              "kaputt", "gross", "klein", "sicher", "wahr", "wieder"]
SEP_AMBIGUO = ["durch", "über", "ueber", "um", "unter", "hinter", "wider"]
# verbos que empiezan con un prefijo separable pero NO son separables (lista cerrada, revisada a ojo)
FALSO_SEP = {"antworten", "abonnieren", "absolvieren", "anonymisieren", "adressieren", "akzeptieren",
             "analysieren", "argumentieren", "agieren", "annektieren", "arbeiten", "ahnen", "anerkennen",
             "beeinflussen", "ausbooten", "inszenieren", "informieren", "investieren", "integrieren",
             "interessieren", "installieren", "interpretieren", "interviewen", "improvisieren", "impfen",
             "intensivieren", "isolieren", "initiieren", "inspirieren", "importieren", "indizieren",
             "ignorieren", "imitieren", "identifizieren", "illustrieren", "ahnden", "antizipieren",
             "abstrahieren", "abstimmen_"}
PLACEHOLDER = {"etwas", "jemanden", "jemandem", "jemand", "sich", "(sich)", "einer", "sache", "ihm", "ihn"}


def norm(s):
    s = unicodedata.normalize("NFC", s).strip()
    s = re.sub(r"\s+", " ", s)
    return s.casefold()


def forma_de(item):
    """Analiza un item aleman. Devuelve dict de rasgos (o None si no es verbal)."""
    t = item.strip()
    r = {"refl": False, "valencia": "", "prep_final": "", "nucleo": "", "resto": []}
    if " / " in t or "/" in t:
        r["tipo"] = "multi"
        return r
    if ART.match(t):
        r["tipo"] = "articulo"
        return r
    toks = t.replace("(sich)", "sich").split()
    if toks and toks[0] == "sich":
        r["refl"] = True
        toks = toks[1:]
    # valencia al inicio (etwas / jemandem / jemanden / jemandem etwas)
    txt = " ".join(toks)
    for v in VALENCIA:
        if txt.startswith(v + " "):
            r["valencia"] = v
            toks = txt[len(v):].split()
            break
    # "sich" en medio: "jemandem etwas ... " o "etwas sich ..." -> reflexivo dativo
    if not r["refl"] and toks and toks[0] == "sich":
        r["refl"] = True
        toks = toks[1:]
    # preposicion final (kuemmern um, achten auf, absichern gegen)
    if len(toks) > 1 and toks[-1] in PREP_DE_REAL:
        r["prep_final"] = toks[-1]
        toks = toks[:-1]
    # preposicion final doble: "sich streiten über / um" ya cae en multi
    if not toks:
        r["tipo"] = "nada"
        return r
    ult = toks[-1]
    if not (ult[:1].islower() and re.search(r"(en|ern|eln|n)$", ult)):
        r["tipo"] = "no_infinitivo"
        return r
    r["nucleo"] = ult
    r["resto"] = toks[:-1]
    if not r["resto"]:
        r["tipo"] = "V1"
    elif any(w[:1].isupper() for w in r["resto"]):
        r["tipo"] = "V_nom"          # nombre + verbo, frase hecha, objeto fijo
    elif all(w in PREP_DE or w in PLACEHOLDER or w in ("dem", "den", "der", "die", "das") for w in r["resto"]):
        r["tipo"] = "V_pp"           # "in etwas versinken": verbo con complemento preposicional dentro
    else:
        r["tipo"] = "V_adv"          # adverbio / adjetivo + verbo: "frueh aufstehen"
    return r


def sep_de(nucleo):
    """'si' / 'ambiguo' / '' segun el prefijo del infinitivo."""
    n = nucleo
    if n in FALSO_SEP:
        return ""
    for p in sorted(SEP_SEGURO, key=len, reverse=True):
        if n.startswith(p) and len(n) - len(p) >= 4:
            resto = n[len(p):]
            if resto.endswith("ieren") and p in ("an", "ab", "auf", "aus", "ein", "vor", "mit", "nach", "zu") and len(resto) < 9:
                return "ambiguo"
            return "si"
    for p in SEP_AMBIGUO:
        if n.startswith(p) and len(n) - len(p) >= 4:
            return "ambiguo"
    return ""


# ---------------------------------------------------------------- ingles
ADV_PART = {"up", "down", "out", "off", "away", "back", "apart", "ahead", "along", "aside", "together",
            "forward", "forth"}
AMB_PART = {"on", "in", "over", "through", "around", "round", "about", "across", "by", "after", "behind",
            "under", "into", "onto"}
PREP_ONLY = {"to", "for", "with", "at", "from", "of", "against", "towards", "upon", "without", "within"}
VERBOS_COP = {"be", "have", "get", "make", "do", "keep", "take", "give", "go", "come"}


def forma_en(item):
    t = item.strip()
    r = {"tipo": "", "verbo": "", "part": "", "obj_tras_part": False, "tres": ""}
    tl = t.lower()
    if tl.startswith("not to "):
        tl = tl[4:]
    if not tl.startswith("to "):
        r["tipo"] = "no_verbo"
        return r
    cuerpo = re.sub(r"\([^)]*\)", " ", tl[3:])
    if "/" in cuerpo:
        r["tipo"] = "multi"
        return r
    toks = cuerpo.split()
    if not toks:
        r["tipo"] = "no_verbo"
        return r
    r["verbo"] = toks[0]
    r["base"] = toks[0]
    if len(toks) == 1:
        r["tipo"] = "E1"
        return r
    if toks[0] == "be":
        r["tipo"] = "E_be"            # to be restless / to be up to speed: no es phrasal
        return r
    p = toks[1]
    if p in ADV_PART:
        r["part"], r["tipo"] = p, "E_part_adv"
    elif p in AMB_PART:
        r["part"], r["tipo"] = p, "E_part_amb"
    elif p in PREP_ONLY:
        r["part"], r["tipo"] = p, "E_part_prep"
    else:
        # verbo + objeto/complemento (to keep a promise). Puede haber particula mas adelante:
        r["tipo"] = "E_obj"
        for w in toks[2:]:
            if w in ADV_PART:
                r["part"] = w
                break
        return r
    r["base"] = " ".join(toks[:2])
    if len(toks) > 2 and toks[2] in PREP_ONLY and (p in ADV_PART or p in AMB_PART):
        r["base"] = " ".join(toks[:3])
    r["obj_tras_part"] = len(toks) > 2 and toks[2] not in PREP_ONLY and toks[2] not in ADV_PART
    if len(toks) >= 3 and toks[2] in PREP_ONLY and p in ADV_PART:
        r["tres"] = f"{toks[0]} {p} {toks[2]}"
    return r


# Primera pasada MANUAL del ingles (criterio del prompt 20: phrasal = particula no composicional;
# verbo-preposicion = el verbo solo selecciona la preposicion; separable = el objeto cabe entre verbo y
# particula). Se clasifica por BASE (verbo + particula [+ preposicion]). PS separable, PI inseparable,
# VP verbo-preposicion, X ni una ni otra (verbo + sintagma composicional, modismo, "to" + infinitivo),
# D duda PI|VP, D2 duda PS|PI. Es PROVISORIA: en el paso 3 la reemplaza la tabla maestra
# contenido/verbos/en.json, leida item por item.
PRIMERA_PASADA_EN = {}
for _c, _s in (
    ("PS", "back up;bottle up;bring up;build up;burn off;call back;calm down;carve out;chase up;cut out;"
           "follow up;give up;let off;make up;pass down;pay back;put off;ride out;roll out;scale up;send back;"
           "send off;set up;shake off;shrug off;stave off;switch off;take up;throw away;tidy up;top up;try out;"
           "turn down;turn off;wind down;work out;wrap up;fill in;hand in;pass on;put in;switch on;take on;try on"),
    ("PI", "break up;brush up;burn out;catch up;catch up with;cut back;cut down;drift apart;drop out;"
           "drop out of;eat out;fall out;fall out with;flare up;get along;get off;go off;grow apart;"
           "hang out with;hold out for;keep up with;lie down;log off;look forward to;look up to;meet up;"
           "meet up with;move out;open up;open up to;opt out;opt out of;push back;put up with;run out of;"
           "save up for;sign off;sign up;sign up for;splash out;stand out;stand up for;trade down;ask around;"
           "blend in;buy into;check in;clock in;come round;fall behind;fit in;get into;get on;get on with;"
           "get over;gloss over;lag behind;log in;look after;look into;move in;potter about;see through;"
           "settle in;shop around;sleep on;take after;cover for;do without;fall for;stick to;stick with"),
    ("VP", "complain about;confide in;dabble in;haggle over;pay into;rely on;report on;scroll through;"
           "acclimatise to;advocate for;agree with;apply for;ask for;bond with;cram for;excel at;hedge against;"
           "learn from;listen to;move to;recover from;register with;report to;search for;study for;switch to;"
           "talk to;tinker with;backtrack on"),
    ("D", "deal with;look for;call in;settle into"),
    ("D2", "find out;find out about;set off;while away"),
    ("X", "feel out of;go out for;keep up to;put down;put down roots;stay ahead of;book in;buy in;come under;"
          "cut through;err on;fall through;feel under;get around;get in;go into;jump through;learn by;pay by;"
          "pay in;pay on;shop on;wait in;arrange to;feel at;get to;go for;live within;season to;teach to;"
          "vote with;work from;keep on;keep in;stay on;walk on")):
    for _b in _s.split(";"):
        PRIMERA_PASADA_EN[_b.strip()] = _c

# Verbos que rigen SOLO dativo (lista cerrada de partida; en el paso 3 se lee item por item)
DAT_SOLO = {"antworten", "danken", "dienen", "drohen", "fehlen", "folgen", "gefallen", "gehören", "gelingen",
            "genügen", "glauben", "gratulieren", "helfen", "nützen", "passen", "raten", "schaden", "schmecken",
            "trauen", "vertrauen", "wehtun", "widersprechen", "zuhören", "zustimmen", "verzeihen", "kündigen",
            "nachgeben", "nachgehen", "entgegenwirken", "entsprechen", "begegnen", "beitreten", "ausweichen",
            "beistehen", "imponieren", "misstrauen", "schmeicheln", "vorbeugen", "zusehen", "zutrauen",
            "entkommen", "einfallen", "auffallen", "leidtun", "schwerfallen", "leichtfallen", "angehören",
            "widmen", "entziehen", "fehlen", "weitersagen_"}


def cargar_packs(base):
    filas = []
    for p in sorted(Path(base, "packs").glob("*.json")):
        d = json.loads(p.read_text(encoding="utf-8"))
        for it in d["vocabulario"]:
            filas.append((d["idioma"], d["nivel"], d["anio"], p.stem, it))
    return filas


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--contenido", default="proyecto/contenido")
    ap.add_argument("--csv")
    ap.add_argument("--dudosos")
    ap.add_argument("--bases", action="store_true", help="agrega el recuento por verbo base")
    a = ap.parse_args()
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")

    filas = cargar_packs(a.contenido)
    total = Counter(f[0] for f in filas)
    reg = {}                      # (idioma, forma) -> info
    descartados = {}              # parecen verbo por la forma, pero la glosa no es un verbo
    for idi, niv, anio, pack, it in filas:
        if idi == "de":
            f = forma_de(it["item"])
            verbal = f["tipo"] in ("V1", "V_nom", "V_pp", "V_adv", "multi")
            g = (it["traducciones"].get("en") or "").lower()
            if f["tipo"] == "articulo" and g.startswith("to ") and re.search(r"(en|n)$", it["item"].split()[-1]):
                f["tipo"] = "nom_verbo_art"    # "der Stress abbauen": colocacion con articulo
                verbal = True
            elif f["tipo"] in ("no_infinitivo", "nada") and g.startswith("to "):
                verbal = True
                f["tipo"] = "otro_verbal"
            elif f["tipo"] == "multi" and not (g.startswith("to ") or re.search(r"(en|n)\b", it["item"])):
                verbal = False
        else:
            f = forma_en(it["item"])
            verbal = f["tipo"] not in ("no_verbo",)
        if idi == "de" and f["tipo"] == "V1" and not g.startswith(("to ", "not to ")):
            descartados.setdefault(norm(it["item"]), (it["item"], niv, g))
            continue
        if not verbal:
            continue
        k = (idi, norm(it["item"]) if idi == "de" else re.sub(r"^(not )?to ", "", norm(it["item"])))
        e = reg.setdefault(k, {"idioma": idi, "forma": k[1], "ejemplo": it["item"], 
                               "apar": 0, "f": f, "reccion": set(), "glosa": it["traducciones"].get("es", ""),
                               "packs": set(), "bajo": False})
        e["apar"] += 1
        e["packs"].add(pack)
        if it.get("reccion"):
            e["reccion"].add(it["reccion"])
        if it.get("bajoNivelJustificado"):
            e["bajo"] = True
    # --------------------------------------------------------------- derivados
    for e in reg.values():
        f = e["f"]
        if e["idioma"] == "de":
            f["base"] = (("sich " if f.get("refl") else "") + f.get("nucleo", "")).strip() or e["forma"]
        else:
            f.setdefault("base", e["forma"])
        cand = []
        if e["idioma"] == "de":
            if f.get("refl"):
                cand.append("reflexivo")
            s = sep_de(f["nucleo"]) if f.get("nucleo") else ""
            if s == "si":
                cand.append("trennbar")
            elif s == "ambiguo":
                cand.append("trennbar?")
            if f.get("prep_final") or any(("+" in r or " " in r.strip()) and not r.startswith(("+ Akk", "+ Dat", "Akk", "Dat", "reflex")) for r in e["reccion"]):
                cand.append("mit-Praposition")
            if (f.get("nucleo") in DAT_SOLO and not f.get("prep_final")) or \
                    (f.get("valencia") == "jemandem" and not f.get("prep_final") and f.get("nucleo") in DAT_SOLO):
                cand.append("mit-Dativ?")
        else:
            c1 = PRIMERA_PASADA_EN.get(f.get("base", "")) if f["tipo"].startswith("E_part") else None
            if c1:
                cand.append({"PS": "phrasal-separable", "PI": "phrasal-inseparable", "VP": "verbo-preposicion",
                             "D": "dudoso PI|VP", "D2": "dudoso PS|PI", "X": "(ninguna: SP/modismo)"}[c1])
        e["cand"] = cand
        if e["idioma"] == "de":
            e["univ"] = f["tipo"] in ("V1", "V_pp")
        else:
            e["univ"] = f["tipo"] == "E1" or (f["tipo"].startswith("E_part") and
                                              PRIMERA_PASADA_EN.get(f.get("base", "")) in ("PS", "PI", "VP", "D", "D2"))
    # --------------------------------------------------------------- informe
    out = []
    P = out.append
    P("# Inventario de verbos — paso 1 (solo lectura)\n")
    P("Fuente: `contenido/packs/*.json` (%d packs). Hechos medidos: DE %d items, EN %d items (con repeticiones)." %
      (len({f[3] for f in filas}), total["de"], total["en"]))
    n_tipo = Counter(); n_red = Counter(); n_nv = Counter()
    for idi, niv, anio, pack, it in filas:
        n_tipo[idi] += "tipo" in it
        n_nv[idi] += "nivelVerificado" in it
        n_red[idi] += "reccion" in it
    P("Campos presentes: `tipo` DE %d / EN %d; `nivelVerificado` DE %d / EN %d; `reccion` DE %d / EN %d.\n" %
      (n_tipo["de"], n_tipo["en"], n_nv["de"], n_nv["en"], n_red["de"], n_red["en"]))
    for idi in ("de", "en"):
        es = [e for e in reg.values() if e["idioma"] == idi]
        occ = sum(e["apar"] for e in es)
        P(f"## {idi.upper()}: {len(es)} verbos únicos ({occ} apariciones de {total[idi]} items)\n")
        kinds = Counter(e["f"]["tipo"] for e in es)
        P("Forma (tipo): " + ", ".join(f"{k} {v}" for k, v in sorted(kinds.items())) + "\n")
        P("| candidato | únicos |")
        P("|---|---|")
        cl = Counter()
        for e in es:
            for c in e["cand"]:
                cl[c] += 1
        for c, n in sorted(cl.items(), key=lambda x: -x[1]):
            sel = [e for e in es if c in e["cand"]]
            P(f"| {c} | {n} |")
        P("")
        if idi == "en":
            for t in ("E_part_adv", "E_part_amb", "E_part_prep", "E_obj", "E_be", "E1", "multi"):
                P(f"- {t}: {kinds.get(t,0)}")
            P("")
    # verbos base: una frase de ejemplo no es otro verbo
    P("## Verbos base (colapsando las frases que comparten verbo)\n")
    P("| idioma | formas únicas | bases únicas |")
    P("|---|---|---|")
    for idi in ("de", "en"):
        es = [e for e in reg.values() if e["idioma"] == idi]
        if idi == "en":
            es = [e for e in es if e["f"]["tipo"] in ("E1", "E_part_adv", "E_part_amb", "E_part_prep")]
        bases = {e["f"]["base"] for e in es}
        P(f"| {idi.upper()}{' (E1+phrasal/prep)' if idi=='en' else ''} | {len(es)} | {len(bases)} |")
    P("")
    # universo propuesto (opcion A): solo items cuyo nucleo es el verbo, sin sustantivo/adverbio fijo
    P("## Universo propuesto de la vista «Verbos» (opción A: el verbo es el núcleo del item)\n")
    P("DE = V1 (verbo solo, con sich / etwas / jemandem / preposición final) + V_pp. "
      "EN = «to» + verbo solo, phrasal o verbo-preposición (primera pasada manual; se excluyen verbo + sintagma composicional, modismos, «to be» + adjetivo, verbo + objeto).\n")
    P("| idioma | medida | total |")
    P("|---|---|---|")
    for idi in ("de", "en"):
        es = [e for e in reg.values() if e["idioma"] == idi and e["univ"]]
        bases = {e["f"]["base"] for e in es}
        P(f"| {idi.upper()} | formas únicas | {len(es)} |")
        P(f"| {idi.upper()} | verbos base | {len(bases)} |")
    P("")
    P("Clases dentro del universo (formas únicas; un verbo puede tener varias):\n")
    P("| idioma | clase candidata | formas |")
    P("|---|---|---|")
    for idi in ("de", "en"):
        es = [e for e in reg.values() if e["idioma"] == idi and e["univ"]]
        cl = Counter()
        for e in es:
            for c in (e["cand"] or ["(sin clase: simple)"]):
                cl[c] += 1
        for c, n in sorted(cl.items(), key=lambda x: -x[1]):
            sel = [e for e in es if c in (e["cand"] or ["(sin clase: simple)"])]
            P(f"| {idi.upper()} | {c} | {n} |")
    P("")
    # reccion existente
    rc = Counter()
    for e in reg.values():
        for r in e["reccion"]:
            rc[(e["idioma"], r)] += 1
    P("## `reccion` existente en verbos (únicos)\n")
    for (idi, r), n in sorted(rc.items(), key=lambda x: -x[1]):
        P(f"- {idi} `{r}`: {n}")
    sal = "\n".join(out)
    print(sal)

    if a.csv:
        buf = io.StringIO()
        w = csv.writer(buf, lineterminator="\n")
        w.writerow(["idioma", "forma", "ejemplo", "apariciones", "tipo_forma",
                    "candidatos", "reccion_existente", "glosa_es", "bajoNivelJustificado", "base"])
        for e in sorted(reg.values(), key=lambda e: (e["idioma"], e["forma"])):
            w.writerow([e["idioma"], e["forma"], e["ejemplo"], e["apar"],
                        e["f"]["tipo"], "|".join(e["cand"]), "|".join(sorted(e["reccion"])), e["glosa"],
                        int(e["bajo"]), e["f"].get("base", "")])
        Path(a.csv).write_text(buf.getvalue(), encoding="utf-8")
    if a.dudosos:
        d = ["# Dudosos del inventario (¿verbo o frase hecha?)\n"]
        for idi, tipos, tit in (("de", ("V_nom", "nom_verbo_art", "V_adv", "V_pp", "multi", "otro_verbal"), "DE"),
                                ("en", ("E_obj", "E_be", "multi", "E_part_amb"), "EN")):
            for t in tipos:
                es = sorted((e for e in reg.values() if e["idioma"] == idi and e["f"]["tipo"] == t),
                            key=lambda e: e["forma"])
                d.append(f"\n## {tit} · {t} ({len(es)})\n")
                d += [f"- {e['ejemplo']} · {e['glosa']}" for e in es]
        d.append(f"\n## DE · descartados por glosa ({len(descartados)}): parecen verbo por la forma, pero no lo son\n")
        d += [f"- {v[0]} · {v[1]} · {v[2]}" for v in sorted(descartados.values())]
        Path(a.dudosos).write_text("\n".join(d) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
