#!/usr/bin/env python3
"""
build_wordlists.py — construye la base de vocabulario por nivel MCER.

Lee las listas oficiales gratuitas (Goethe/OeSD/telc, PDF o texto), extrae los
lemas y los fusiona en un unico JSON. Cada lema guarda TODOS los niveles y
fuentes en los que aparece, no solo uno.

Uso:
    python3 build_wordlists.py --config fuentes.json --out ../data/vocab_de.json

Las listas fuente NO se distribuyen con el proyecto (son propiedad del
Goethe-Institut / Hueber). Se descargan por separado; ver fuentes.json.
"""
import argparse, json, re, subprocess, sys, unicodedata
from pathlib import Path
from collections import defaultdict, Counter

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

# --- lectura -----------------------------------------------------------
def leer(path: Path) -> str:
    """Devuelve el texto de un PDF o de un .txt. Tolera PDF mal etiquetados."""
    raw = path.read_bytes()[:5]
    if raw.startswith(b"%PDF"):
        r = subprocess.run(["pdftotext", "-layout", str(path), "-"],
                           capture_output=True, text=True,
                           encoding="utf-8", errors="replace")
        return r.stdout
    return path.read_text(encoding="utf-8", errors="replace")

# --- lectores por formato ----------------------------------------------
RE_NIVEL = re.compile(r"\b([ABC][12])\b")
POS = r"(?:n|v|adj|adv|prep|conj|pron|det|exclam|number|modal|auxiliary|indefinite|definite)"
RE_CORTE_POS = re.compile(rf"\s+(?={POS}\b)")

def lemas_oxford(texto):
    """PDF alfabetico de Oxford: 'abandon v. B2' / 'alien n. B2, adj. C1'.

    El nivel viene en la propia linea, junto a cada acepcion. Cuando una
    palabra tiene varios niveles segun la clase (alien n. B2 / adj. C1) se
    toma el mas bajo, que es cuando el aprendiente la encuentra por primera vez.
    """
    entradas = {}
    for linea in texto.splitlines():
        for celda in re.split(r"\s{2,}", linea):
            celda = celda.strip()
            if not celda or len(celda) > 70:
                continue
            niveles = RE_NIVEL.findall(celda)
            if not niveles:
                continue
            cabeza = RE_CORTE_POS.split(celda)[0].strip(" ,.")
            cabeza = RE_NIVEL.sub("", cabeza).strip(" ,.")
            if not cabeza or not re.match(r"^[a-zA-Z]", cabeza) or len(cabeza) < 2:
                continue
            nivel = sorted(set(niveles), key=NIVELES.index)[0]
            k = cabeza.lower()
            if k not in entradas or NIVELES.index(nivel) < NIVELES.index(entradas[k][1]):
                entradas[k] = (cabeza, nivel)
    return entradas

def lemas_cambridge(texto):
    """Lista de vocabulario de Cambridge (A2 Key / B1 Preliminary).

    Formato de dos columnas: 'headword (pos)'. Es la unica fuente inglesa
    disponible que etiqueta expresiones multipalabra: 'phr v' = phrasal verb,
    'phr' = frase. Se conservan tal cual para poder verificarlas enteras.
    """
    entradas = {}
    RE = re.compile(r"([a-zA-Z][a-zA-Z'\- ]{1,34}?)\s*\(([^)]{1,24})\)")
    for linea in texto.splitlines():
        for seg in re.split(r"\s{3,}", linea):
            m = RE.match(seg.strip())
            if not m:
                continue
            lema = m.group(1).strip().lower()
            pos = m.group(2).strip()
            if len(lema) < 2:
                continue
            entradas[lema] = (lema, "B1", pos)
    return {k: (v[0], v[1]) for k, v in entradas.items()}

def lemas_cambridge_wordlist(texto, nivel):
    """Wordlist de las series Grammar & Vocabulary de Cambridge.

    Formato: varias columnas, agrupado por unidad y por clase de palabra
    (NOUNS / ADJECTIVES / VERBS), con transcripcion IPA entre barras. Incluye
    expresiones de varias palabras ("hustle and bustle", "commuter belt"), que
    es justo lo que faltaba para verificar colocaciones por encima de B2.

    Se parte cada linea por 3+ espacios, se quita la IPA y se descartan las
    lineas de continuacion (las que solo llevan transcripcion).
    """
    entradas = {}
    for linea in texto.splitlines():
        for seg in re.split(r"\s{3,}", linea):
            seg = re.sub(r"/[^/]*/", " ", seg)          # fuera la IPA
            seg = re.sub(r"\s+", " ", seg).strip(" ,;.")
            if not seg or len(seg) > 40:
                continue
            if seg.isupper():                            # cabecera NOUNS/VERBS
                continue
            if not re.fullmatch(r"[a-zA-Z][a-zA-Z'\- ]{1,38}", seg):
                continue
            if len(seg) < 3:
                continue
            entradas[seg.lower()] = (seg.lower(), nivel)
    return entradas

def lemas_xlsx_por_unidad(path, nivel):
    """Wordlist transcrita a Excel: una columna por unidad.

    Dentro de cada columna, las filas que dicen Noun / Adjective / Verb /
    Adverb / Phrase son cabeceras de clase de palabra, no vocabulario. Todo lo
    demas es un item. La seccion "Phrase" aporta expresiones de varias
    palabras, que es lo que las listas de palabra sueltas no cubren.
    """
    import openpyxl
    CLASES = {"noun", "nouns", "adjective", "adjectives", "verb", "verbs",
              "adverb", "adverbs", "phrase", "phrases", "expression",
              "expressions", "collocation", "collocations"}
    # read_only=True no expone iter_cols, y aqui hace falta recorrer por
    # columna porque cada unidad es una columna. Se carga completo.
    wb = openpyxl.load_workbook(path, data_only=True)
    entradas = {}
    for ws in wb.worksheets:
        for col in ws.iter_cols(values_only=True):
            for celda in col:
                if celda is None:
                    continue
                t = re.sub(r"\s+", " ", str(celda)).strip(" ,;.")
                if not t or len(t) > 45:
                    continue
                bajo = t.lower()
                if bajo in CLASES or re.match(r"^u?unit\s*\d", bajo):
                    continue
                if not re.fullmatch(r"[a-zA-Z][a-zA-Z'\-' ]{1,43}", t):
                    continue
                if len(t) < 3:
                    continue
                entradas[bajo] = (bajo, nivel)
    return entradas

def lemas_csv(path, col_lema="headword", col_nivel="CEFR"):
    """CSV de CEFR-J / Octanove. El nivel CEFR-J trae subniveles (A1.1) que
    se colapsan al nivel MCER (A1)."""
    import csv
    entradas = {}
    with open(path, newline="", encoding="utf-8-sig") as fh:
        for fila in csv.DictReader(fh):
            lema = (fila.get(col_lema) or "").strip()
            nivel = (fila.get(col_nivel) or "").strip()[:2].upper()
            if not lema or nivel not in NIVELES:
                continue
            k = lema.lower()
            if k not in entradas or NIVELES.index(nivel) < NIVELES.index(entradas[k][1]):
                entradas[k] = (lema, nivel)
    return entradas

# --- extraccion de lemas (formato Goethe, dos columnas) ----------------
RE_NOMBRE = re.compile(r"^\s*(der|die|das)\s+([A-ZÄÖÜ][\wÄÖÜäöüß-]{1,30})\b")
RE_VERBO  = re.compile(r"^\s*(sich\s+)?([a-zäöüß][\wäöüß]{2,30}(?:en|ern|eln))\s*,")
RE_SIMPLE = re.compile(r"^\s*([a-zäöüß][\wäöüß-]{1,25})\s*$")
RE_FRASE  = re.compile(r"[.!?]\s|\d\.\s")

def es_ejemplo(seg: str) -> bool:
    """Un segmento es ejemplo si tiene puntuacion de frase o es largo."""
    if RE_FRASE.search(seg) or seg.endswith((".", "!", "?", ":")):
        return True
    return len(seg.split()) > 4

def normalizar(lema: str) -> str:
    lema = lema.strip().strip(",.;:!?()\u00a0")
    lema = re.sub(r"\s+", " ", lema)
    return lema

def clave(lema: str) -> str:
    """Clave de comparacion: minusculas, sin articulo aleman, ss por eszett."""
    k = lema.lower().strip()
    for a in ART:
        if k.startswith(a + " "):
            k = k[len(a) + 1:]
    k = re.sub(r",.*$", "", k)          # quita ", -en" / ", faengt an"
    k = k.replace("\u00df", "ss").replace("-", "").strip()
    return unicodedata.normalize("NFC", k)

def candidato(seg: str):
    """Devuelve el lema si el segmento parece una entrada de diccionario."""
    seg = seg.strip()
    if not seg or es_ejemplo(seg):
        return None
    m = RE_NOMBRE.match(seg)
    if m:
        return normalizar(f"{m.group(1)} {m.group(2)}")
    m = RE_VERBO.match(seg)
    if m:
        return normalizar((m.group(1) or "") + m.group(2))
    m = RE_SIMPLE.match(seg)
    if m and len(m.group(1)) > 2:
        return normalizar(m.group(1))
    return None

def extraer_lemas(texto: str):
    """Devuelve (lemas_estrictos, tokens_flojos).

    Las listas del Goethe vienen en dos columnas (lema | ejemplo) y a veces
    dos bloques por pagina. Se parte cada linea por 3+ espacios y se prueba
    cada segmento como entrada; los ejemplos se descartan por puntuacion.
    """
    lemas, tokens = set(), set()
    for linea in texto.splitlines():
        linea = linea.replace("\u0002", "").rstrip()
        if not linea.strip():
            continue
        for t in re.findall(r"[A-Za-z\u00c4\u00d6\u00dc\u00e4\u00f6\u00fc\u00df][\w\u00c4\u00d6\u00dc\u00e4\u00f6\u00fc\u00df-]{2,}", linea):
            tokens.add(clave(t))
        for seg in re.split(r"\s{3,}", linea):
            lema = candidato(seg)
            if lema:
                lemas.add(lema)
    return lemas, tokens

# --- fusion -------------------------------------------------------------
def construir(fuentes):
    entradas = defaultdict(lambda: {"formas": set(), "apariciones": []})
    tokens_por_nivel = defaultdict(set)
    fuentes_por_nivel = defaultdict(set)
    lemas_por_fuente = {}

    for f in fuentes:
        path = Path(f["archivo"])
        if not path.exists():
            print(f"  ! falta {path} - se omite", file=sys.stderr)
            continue
        formato = f.get("formato", "goethe")
        tokens = set()
        if formato == "csv":
            pares = lemas_csv(path, f.get("colLema", "headword"),
                              f.get("colNivel", "CEFR"))
        elif formato == "xlsx-unidades":
            pares = lemas_xlsx_por_unidad(path, f["nivel"])
        elif formato == "cambridge-wordlist":
            pares = lemas_cambridge_wordlist(leer(path), f["nivel"])
        elif formato == "cambridge":
            pares = lemas_cambridge(leer(path))
        elif formato == "oxford":
            pares = lemas_oxford(leer(path))
        else:                                   # Goethe: nivel fijo por archivo
            lemas, tokens = extraer_lemas(leer(path))
            pares = {clave(l): (l, f["nivel"]) for l in lemas}

        reparto = Counter(n for _, n in pares.values())
        print(f"  {f['id']:26s} {f.get('anio','?'):>6}  {len(pares):5d} lemas  "
              f"{dict(sorted(reparto.items()))}", file=sys.stderr)

        tokens_por_nivel[f.get("nivel", "?")] |= tokens
        lemas_por_fuente[f["id"]] = set(pares)
        for k, (forma, nivel) in pares.items():
            fuentes_por_nivel[nivel].add(f["id"])
            e = entradas[k]
            e["formas"].add(forma)
            e["apariciones"].append({"nivel": nivel, "fuente": f["id"],
                                     "anio": f.get("anio"),
                                     "variante": f.get("variante")})

    salida = {}
    for k, e in entradas.items():
        niveles = sorted({a["nivel"] for a in e["apariciones"]}, key=NIVELES.index)
        vistos = {a["fuente"] for a in e["apariciones"]}

        # Desacuerdo real: entre fuentes que cubren EL MISMO nivel (distintas
        # versiones o distintos organismos), unas lo incluyen y otras no.
        desacuerdo = [n for n in niveles
                      if len(fuentes_por_nivel[n]) > 1
                      and not fuentes_por_nivel[n] <= vistos]
        variantes = sorted({a["variante"] for a in e["apariciones"]
                            if a.get("variante")})

        # Nivel de ensenanza: el mas bajo atestiguado.
        nivel = niveles[0]

        # Si el nivel mas bajo esta en disputa, el lema queda a caballo entre
        # ese nivel y el siguiente atestiguado -> etiqueta doble "A2/B1".
        if nivel in desacuerdo and len(niveles) > 1:
            etiqueta = f"{niveles[0]}/{niveles[1]}"
        else:
            etiqueta = nivel

        salida[k] = {
            "forma": sorted(e["formas"], key=len)[-1],
            "nivel": nivel,
            "etiqueta": etiqueta,
            "niveles_atestiguados": niveles,
            "desacuerdo": desacuerdo,
            "variantes": variantes,
            "apariciones": e["apariciones"],
        }
    return salida, {n: sorted(t) for n, t in tokens_por_nivel.items()}

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--config", required=True)
    ap.add_argument("--out", required=True)
    ap.add_argument("--out-tokens")
    a = ap.parse_args()

    cfg = json.loads(Path(a.config).read_text(encoding="utf-8"))
    print(f"Construyendo vocabulario [{cfg['idioma']}]", file=sys.stderr)
    vocab, tokens = construir(cfg["fuentes"])

    Path(a.out).write_text(json.dumps(
        {"idioma": cfg["idioma"], "generado_por": "build_wordlists.py",
         "n_lemas": len(vocab), "entradas": vocab},
        ensure_ascii=False, indent=1))
    print(f"\n  {len(vocab)} lemas -> {a.out}", file=sys.stderr)
    disp = [v for v in vocab.values() if v["desacuerdo"]]
    dobles = [v["etiqueta"] for v in vocab.values() if "/" in v["etiqueta"]]
    print(f"  {len(disp)} lemas en los que las fuentes de un mismo nivel no coinciden",
          file=sys.stderr)
    print(f"  {len(dobles)} lemas con etiqueta doble (p.ej. A2/B1)", file=sys.stderr)
    from collections import Counter
    print("  reparto:", dict(Counter(v["etiqueta"] for v in vocab.values()).most_common()),
          file=sys.stderr)

    if a.out_tokens:
        Path(a.out_tokens).write_text(json.dumps(tokens, ensure_ascii=False), encoding="utf-8")
        print(f"  tokens -> {a.out_tokens}", file=sys.stderr)

if __name__ == "__main__":
    main()
