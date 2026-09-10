#!/usr/bin/env python3
"""
check_level.py — verificador de nivel del vocabulario de una ficha.

Toma una ficha (.json validada contra ficha.schema.json, o el .md de trabajo)
y compara cada item de vocabulario contra la base construida por
build_wordlists.py.

Reglas:
  - Si el lema esta atestiguado en un nivel POR DEBAJO del nivel de la ficha,
    se marca DEMASIADO FACIL: el usuario ya deberia conocerlo.
  - Si esta atestiguado en el MISMO nivel, se marca LIMITE (aceptable, pero
    no aporta vocabulario nuevo).
  - Si no aparece en ninguna lista, se marca OK: esta por encima del ultimo
    nivel cubierto por las fuentes, que es lo que se busca.
  - Si las fuentes de un mismo nivel no coinciden, se avisa.

Uso:
    python3 check_level.py ../fichas/DE-G01.json --vocab ../data/vocab_de.json
    python3 check_level.py ../fichas/*.md --vocab ../data/vocab_de.json --umbral 0.25
Salida distinta de 0 si se supera el umbral de items demasiado faciles.
"""
import argparse, json, re, sys, unicodedata
from pathlib import Path

# --- Portabilidad Windows -------------------------------------------------
# En Linux el locale suele ser UTF-8 y todo funciona por accidente. En Windows
# Python cae a cp1252 y rompe cualquier caracter no ASCII: los "·" de las
# cabeceras de las fichas, las dieresis, la marca "†". Por eso:
#   1. toda lectura y escritura de archivos declara encoding="utf-8"
#   2. stdout y stderr se reconfiguran para no fallar al imprimir
def _utf8_io():
    for flujo in (sys.stdout, sys.stderr):
        try:
            flujo.reconfigure(encoding="utf-8", errors="replace")
        except (AttributeError, ValueError):
            pass  # entorno sin reconfigure: no es critico

_utf8_io()


NIVELES = ["A1", "A2", "B1", "B2", "C1", "C2"]
ART = ("der", "die", "das")
VERDE, AMBAR, ROJO, FIN = "\033[32m", "\033[33m", "\033[31m", "\033[0m"

EN_PREF = ("to ", "a ", "an ", "the ")

def clave(lema: str) -> str:
    k = lema.lower().strip()
    k = re.sub(r"\(.*?\)", "", k)
    k = re.sub(r"\s*\+.*$", "", k)          # quita "+ Akk."
    k = k.replace("\u2019", "'").strip()
    for a in ART:                            # articulo aleman
        if k.startswith(a + " "):
            k = k[len(a) + 1:]
    for a in EN_PREF:                        # "to ", articulo ingles
        if k.startswith(a):
            k = k[len(a):]
    if re.search(r",\s*(-|\u00a8|[a-zäöü]+e?[nrs]?$)", k) and len(k.split()) <= 3:
        k = re.sub(r",.*$", "", k)           # marca de plural/conjugacion alemana
    k = k.replace("ß", "ss").replace("-", "").strip()
    return unicodedata.normalize("NFC", k)

def es_multipalabra(expr: str) -> bool:
    """True si es colocacion, phrasal verb, verbo con preposicion o modismo."""
    limpio = re.sub(r"\(.*?\)|\+\s*\w+\.?", "", expr)
    limpio = re.sub(r"^(der|die|das|to|a|an|the)\s+", "", limpio.strip(), flags=re.I)
    limpio = re.split(r"\s*/\s*", limpio)[0]
    return len([w for w in limpio.split() if w not in ("sich",)]) > 1

CABECERA_VOCAB = re.compile(r"\b(ES|EN|FR|IT|PT)\b")

def items_de_md(texto: str):
    """Extrae la 1a celda de las tablas de VOCABULARIO.

    Solo se leen las tablas cuya cabecera tiene una columna de traduccion
    (ES/EN/...); asi se ignoran los cuadros de referencia gramatical, que no
    son vocabulario y no deben verificarse.
    """
    items, estado = [], "fuera"
    cabecera = []
    for linea in texto.splitlines():
        if re.match(r"\s*\|", linea):
            celdas = [c.strip() for c in linea.strip().strip("|").split("|")]
            if len(celdas) < 3:
                continue
            if re.match(r"^:?-+:?$", celdas[0]):
                estado = "dentro" if any(CABECERA_VOCAB.fullmatch(c)
                                         for c in cabecera) else "ignorar"
                continue
            if estado == "dentro" and celdas[0] and not celdas[0].startswith("**"):
                items.append(celdas[0].replace("*", "").strip())
            elif estado == "fuera":
                cabecera = celdas
        else:
            estado, cabecera = "fuera", []
    return items

def items_de_json(d):
    return [v["item"] for v in d.get("vocabulario", [])]

def var(e):
    """Avisa si el lema solo esta atestiguado en una variante."""
    v = e.get("variantes") or []
    return f"solo {v[0]}" if len(v) == 1 else ""

def revisar(items, nivel_ficha, vocab):
    idx = NIVELES.index(nivel_ficha)
    bajos = {n for n in NIVELES[:idx]}
    res = []
    for it in items:
        # "\u2020" = item por debajo del nivel a proposito: lo que se ensena no
        # es la palabra sino una distincion, un registro o un doble sentido que
        # si corresponde al nivel. El verificador compara lemas, no lo que se
        # ensena sobre ellos, asi que necesita esta valvula.
        if "\u2020" in it:
            res.append((it.replace("\u2020", "").strip(), None, "OK",
                        "bajo nivel a proposito"))
            continue
        multi = es_multipalabra(it)
        k = clave(it)
        e = vocab["entradas"].get(k)
        if multi and (e is None or len(k.split()) < len(it.split()) - 1):
            e = vocab["entradas"].get(it.lower().strip().rstrip(".…"))
        # Una colocacion o Redewendung NO es facil solo porque sus palabras
        # lo sean: "uber die Runden kommen" son cuatro palabras A1 y la
        # expresion es B1+. Solo se juzga la expresion entera.
        if e is None and multi:
            res.append((it, None, "OK", "expresion no atestiguada"))
            continue
        if e is None:
            res.append((it, None, "OK", ""))
        elif e["nivel"] in bajos:
            nota = "fuentes en desacuerdo" if e["desacuerdo"] else ""
            res.append((it, e["etiqueta"], "DEMASIADO FACIL", nota))
        elif e["nivel"] == nivel_ficha:
            res.append((it, e["etiqueta"], "LIMITE", var(e)))
        else:
            res.append((it, e["etiqueta"], "OK", var(e)))
    return res

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fichas", nargs="+")
    ap.add_argument("--vocab", required=True)
    ap.add_argument("--nivel", help="fuerza el nivel si la ficha es .md")
    ap.add_argument("--umbral", type=float, default=0.20,
                    help="fraccion maxima tolerada de items demasiado faciles")
    a = ap.parse_args()

    vocab = json.loads(Path(a.vocab).read_text(encoding="utf-8"))
    total_malos = total_items = 0

    for f in a.fichas:
        p = Path(f)
        if p.suffix == ".json":
            d = json.loads(p.read_text(encoding="utf-8"))
            items, nivel = items_de_json(d), d["nivel"]
        else:
            txt = p.read_text(encoding="utf-8")
            items = items_de_md(txt)
            m = re.search(r"\bnivel:\s*([ABC][12])\b", txt, re.I) or \
                re.search(r"\b([ABC][12])\b", txt)
            nivel = a.nivel or (m.group(1).upper() if m else "B2")
        if not items:
            continue
        res = revisar(items, nivel, vocab)
        malos = [r for r in res if r[2] == "DEMASIADO FACIL"]
        total_malos += len(malos); total_items += len(res)

        print(f"\n== {p.name}  (nivel {nivel}, {len(res)} items)")
        for it, etq, estado, nota in res:
            col = {"OK": VERDE, "LIMITE": AMBAR, "DEMASIADO FACIL": ROJO}[estado]
            marca = {"OK": "ok", "LIMITE": "~~", "DEMASIADO FACIL": "!!"}[estado]
            extra = f"  [{etq}]" if etq else ""
            extra += f"  ({nota})" if nota else ""
            print(f"  {col}{marca}{FIN} {it[:44]:46s}{extra}")
        if malos:
            print(f"  -> {len(malos)}/{len(res)} por debajo del nivel de la ficha")

    if total_items:
        frac = total_malos / total_items
        print(f"\nTOTAL: {total_malos}/{total_items} demasiado faciles "
              f"({frac:.0%}, umbral {a.umbral:.0%})")
        sys.exit(1 if frac > a.umbral else 0)

if __name__ == "__main__":
    main()
