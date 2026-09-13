#!/usr/bin/env python3
"""
compilar_fichas.py — convierte las fichas en Markdown al JSON que consume la app.

El Markdown es la fuente unica: legible para un revisor que no programa, y
compilable a JSON para la app. Este script es el puente. Si falla, falla aqui
y no en el telefono del usuario.

Uso:
    python3 compilar_fichas.py ../fichas/*.md --salida ../build --schema ../schema/ficha.schema.json
"""
import argparse, json, re, sys
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


# Etiquetas de seccion por idioma. La ficha se escribe en el idioma que se
# aprende, asi que los encabezados cambian y hay que mapearlos.
ETIQUETAS = {
    "en": {"descripcion": "Description", "cuadro": "Reference box",
           "ejemplos": "Examples", "notas": "Notes", "contraste": "Contrast with",
           "errores": "Common mistakes", "vocabulario": "Topic vocabulary",
           "redemittel": "Skill expressions", "mision": "Mission",
           "microtareas": "Micro-tasks", "autochequeo": "Self-check",
           "prompt": "Correction prompt", "audio": "Audio lines"},
    "de": {"descripcion": "Beschreibung", "cuadro": "Übersichtskasten",
           "ejemplos": "Beispiele", "notas": "Hinweise", "contraste": "Kontrast zum",
           "errores": "Typische Fehler", "vocabulario": "Themenwortschatz",
           "redemittel": "Redemittel", "mision": "Wochenaufgabe",
           "microtareas": "Mikroaufgaben", "autochequeo": "Selbstkontrolle",
           "prompt": "Korrekturprompt", "audio": "Audiozeilen"},
}
DIAS = {"Mon": "lun", "Wed": "mie", "Fri": "vie",
        "Mo": "lun", "Mi": "mie", "Fr": "vie"}
CAT = {"F": "fluency", "G": "grammar", "V": "vocabulary"}
RETO = {"A2": "chunk_deployment", "B1": "guided_production",
        "B2": "constrained_production", "C1": "open_production",
        "C2": "adaptive_production"}

def trocear(texto):
    """Parte el archivo en fichas por el encabezado de nivel 2."""
    partes = re.split(r"\n## (?=(?:Week|Woche) \d)", texto)
    return [p for p in partes[1:]]

RE_LENGUA = re.compile(r"\((es|en|de|fr|it|pt)\)")

# Cualquier renglon en negrita solo (una etiqueta de seccion, con o sin
# codigo de lengua). bloques_por_lengua() lo necesita ancho: un sub-bloque
# "Typische Fehler (en)" termina en la siguiente etiqueta CUALQUIERA
# (Themenwortschatz, Redemittel...), no en la siguiente con codigo.
_CUALQUIER_MARCADOR = re.compile(r"\n\*\*[^*\n]+\*\*")

# En cambio, para acotar el bloque de una etiqueta NOMBRADA (secciones()) hay
# que ser mas estrictos: solo un sub-bloque por lengua base (con codigo entre
# parentesis) cuenta como limite. El patron ancho tambien encuentra enfasis
# sueltos dentro de la prosa de una seccion -- p.ej. "**Diese Woche kein
# neuer Korrekturdurchgang...**" dentro de promptCorreccion -- y cortaria el
# bloque ahi por error.
_SIGUIENTE_MARCADOR_CON_LENGUA = re.compile(r"\n\*\*[^*\n]+\(\w\w\)[^*\n]*\*\*")


def bloques_por_lengua(cuerpo, etiqueta):
    """Devuelve {codigo_de_lengua: texto} para las secciones marcadas con (xx).

    En el Markdown conviven "**Kontrast zum Spanischen (es).**" y
    "**Kontrast zum Englischen (en).**". El codigo entre parentesis es lo que
    manda: asi agregar frances es anadir un bloque, sin tocar el compilador ni
    depender del nombre de la lengua escrito en otro idioma.
    """
    out = {}
    patron = re.compile(r"\*\*(?:" + etiqueta + r")[^*]*?\((\w\w)\)[^*]*\*\*")
    marcas = [(m.start(), m.end(), m.group(1)) for m in patron.finditer(cuerpo)]
    for ini, fin, cod in marcas:
        m = _CUALQUIER_MARCADOR.search(cuerpo, fin)
        out[cod] = cuerpo[fin:(m.start() if m else len(cuerpo))].strip()
    return out


def secciones(cuerpo, et):
    """Devuelve {clave: bloque de texto} localizando cada etiqueta en negrita.

    El limite de cada bloque es el que este mas cerca: la siguiente etiqueta
    conocida (ETIQUETAS) o cualquier otra linea en negrita propia -- esto
    ultimo cubre los sub-bloques por lengua base ("Typische Fehler (en) —
    zusatzlich...", que bloques_por_lengua() extrae aparte) para que no
    queden adentro del bloque de la etiqueta universal. Sin este limite
    extra, "errores" se comia tambien los renglones de erroresContrastivos:
    quedaban duplicados en los dos campos.
    """
    marcas = []
    for clave, etiqueta in et.items():
        m = re.search(r"\*\*" + re.escape(etiqueta) + r"[^*]*\*\*", cuerpo)
        if m:
            marcas.append((m.start(), m.end(), clave))
    marcas.sort()
    out = {}
    for i, (ini, fin, clave) in enumerate(marcas):
        limite_nombrado = marcas[i + 1][0] if i + 1 < len(marcas) else len(cuerpo)
        m_sig = _SIGUIENTE_MARCADOR_CON_LENGUA.search(cuerpo, fin)
        limite_cualquiera = m_sig.start() if m_sig else len(cuerpo)
        hasta = min(limite_nombrado, limite_cualquiera)
        out[clave] = cuerpo[fin:hasta].strip()
        out[clave + "__titulo"] = cuerpo[ini:fin].strip("* ")
    return out

def vinetas(bloque):
    """Extrae las lineas de una lista con guiones.

    OJO: solo se quita el guion de la vinuela, NUNCA los asteriscos. Un
    strip("-* ") se come el asterisco inicial de una linea que empieza con una
    cita ("- *Zwar* steht meist...") y deja el marcado sin cerrar. Ese bug
    afectaba 104 campos y la app los habria mostrado con asteriscos huerfanos.
    """
    out = []
    for l in bloque.splitlines():
        t = l.strip()
        if not t.startswith("-"):
            continue
        out.append(re.sub(r"\s+", " ", t.lstrip("-").strip()))
    return out

def numeradas(bloque):
    return [re.sub(r"\s+", " ", re.sub(r"^\d+\.\s*", "", l.strip()))
            for l in bloque.splitlines() if re.match(r"^\s*\d+\.", l)]

def tabla(bloque):
    filas, cab = [], None
    for l in bloque.splitlines():
        if not l.strip().startswith("|"):
            continue
        celdas = [c.strip() for c in l.strip().strip("|").split("|")]
        if re.match(r"^:?-+:?$", celdas[0]):
            continue
        if cab is None:
            cab = celdas
        else:
            filas.append(celdas)
    return cab, filas

def capitalizar(t):
    """Mayuscula inicial sin tocar el resto.

    El titulo del cuadro sale de partir "Ubersichtskasten - die drei
    Deklinationstypen" por el guion, asi que llega en minuscula. No se puede
    usar .capitalize() porque bajaria el resto: en aleman los sustantivos van
    en mayuscula y "die drei Deklinationstypen" se volveria
    "Die drei deklinationstypen".
    """
    t = t.strip()
    return t[:1].upper() + t[1:] if t else t


def limpiar(t):
    """Normaliza espacios y quita los marcadores de prioridad y de nivel.

    NO toca el marcado en linea: "*cita*" y "**destaque**" son semanticos y
    los renderiza la app. Ver desenvolver() para el caso del campo entero.
    """
    t = t.replace("\u2605", "").replace("\u2020", "")
    return re.sub(r"\s+", " ", t.strip())

def aplanar_marcado(t):
    """Normaliza el marcado en linea a un solo nivel, sin anidar.

    El Markdown fuente mezcla cita (*x*) y destaque (**x**) y a veces los
    anida: "*Zwar* ist..." dentro de una cita, o "***Zwar** ist...*". Un regex
    no alcanza —lo intente y corrompia texto—, asi que esto tokeniza de
    verdad: parte el texto en tramos con sus estilos, resuelve el conflicto
    (si un tramo es cita Y destaque, gana el destaque, porque es el que dirige
    la mirada) y vuelve a emitir marcado plano.

    Garantia de salida: marcadores balanceados y sin anidamiento, de modo que
    la app necesita un parser de un solo nivel.
    """
    tramos, buf, cita, dest, i = [], [], False, False, 0
    while i < len(t):
        if t.startswith("**", i):
            if buf: tramos.append(("".join(buf), cita, dest)); buf = []
            dest = not dest; i += 2
        elif t[i] == "*":
            if buf: tramos.append(("".join(buf), cita, dest)); buf = []
            cita = not cita; i += 1
        else:
            buf.append(t[i]); i += 1
    if buf: tramos.append(("".join(buf), cita, dest))

    # Si algun tramo es cita Y destaque a la vez, la cita se cae en TODO el
    # campo. Cerrar la cita y abrir el destaque pegados produce "***", que es
    # marcado ambiguo; y conservar solo el destaque es justo la regla
    # documentada: es el que dirige la mirada.
    if any(c and d for _, c, d in tramos):
        tramos = [(txt, False, d) for txt, _, d in tramos]

    salida, estilo_prev = [], None
    for texto, c, d in tramos:
        estilo = "dest" if d else ("cita" if c else None)
        if estilo != estilo_prev:
            if estilo_prev == "dest": salida.append("**")
            elif estilo_prev == "cita": salida.append("*")
            if estilo == "dest": salida.append("**")
            elif estilo == "cita": salida.append("*")
            estilo_prev = estilo
        salida.append(texto)
    if estilo_prev == "dest": salida.append("**")
    elif estilo_prev == "cita": salida.append("*")
    return re.sub(r"\s+", " ", "".join(salida)).strip()

def tramos_de(t):
    """Parte el texto en (texto, es_cita, es_destaque).

    Base de todo el manejo de marcado. Tokenizador y no regex: con regex se
    corrompia el texto en los campos que mezclan cita y destaque.
    """
    out, buf, cita, dest, i = [], [], False, False, 0
    while i < len(t):
        if t.startswith("**", i):
            if buf: out.append(("".join(buf), cita, dest)); buf = []
            dest = not dest; i += 2
        elif t[i] == "*":
            if buf: out.append(("".join(buf), cita, dest)); buf = []
            cita = not cita; i += 1
        else:
            buf.append(t[i]); i += 1
    if buf: out.append(("".join(buf), cita, dest))
    return out


def desenvolver(t):
    """Quita el marcado que cubre el campo ENTERO.

    En el Markdown fuente los ejemplos van en cursiva de punta a punta. Esa
    cursiva no es semantica: significa "esto es una cita", y la app ya lo sabe
    por el rol del campo. Solo se quita si TODOS los tramos son cita y ninguno
    es destaque; si el campo mezcla los dos, se deja como esta.
    """
    t = aplanar_marcado(limpiar(t))
    tr = tramos_de(t)
    if tr and all(c for _, c, _ in tr) and not any(d for _, _, d in tr):
        return "".join(x for x, _, _ in tr).strip()
    return t


def sin_marcado(t):
    """Texto plano, sin marcadores.

    Para campos que la app estiliza por su rol y donde el marcado seria
    redundante: items de vocabulario (la prioridad viaja en su propio campo)
    y expresiones de Redemittel.
    """
    return re.sub(r"\s+", " ",
                  "".join(x for x, _, _ in tramos_de(limpiar(t)))).strip()


def semana_especial(semana, clase, cuerpo, idioma, nivel):
    """Las semanas de repaso y Survival no son fichas: no tienen skill ni topic.

    Se emiten con su propia forma (ver schema/semana-especial.schema.json).
    Meterlas en el schema de ficha exigiria hacer opcionales casi todos sus
    campos obligatorios, que es justo lo que el schema debe evitar.
    """
    et = ETIQUETAS[idioma]
    s = secciones(cuerpo, et)
    mtime = re.findall(r"\*\*(?:Time|Zeit):\*\*[^\n]*?(\d+)\s*(?:min|Min)", cuerpo)
    bloque_m = s.get("mision", "")
    return {
        "_tipo": "semana_especial",
        "id": f"{'REVIEW' if 'Review' in clase else 'SURVIVAL'}-{idioma.upper()}-{nivel}-S{semana}",
        "semana": semana, "idioma": idioma, "nivel": nivel,
        "clase": "review" if "Review" in clase else "survival",
        "titulo": clase,
        "minutosEstimados": max((int(x) for x in mtime), default=60),
        "consigna": limpiar(bloque_m.split("\n")[0]) if bloque_m else "",
        "requisitos": [limpiar(x) for x in vinetas(bloque_m)],
        "microtareas": [limpiar(x) for x in vinetas(s.get("microtareas", ""))],
        "autochequeo": [limpiar(x) for x in numeradas(s.get("autochequeo", ""))][:5],
        "promptCorreccion": limpiar(re.sub(r"^>\s*", "", s.get("prompt", ""), flags=re.M)),
    }

def compilar(bloque, idioma, nivel):
    et = ETIQUETAS[idioma]
    cab, cuerpo = bloque.split("\n", 1)

    m = re.match(r"(?:Week|Woche) (\d+).*?·\s*((?:EN|DE)-[A-Z]\d\d)\s*·\s*(.+)", cab)
    if not m:
        esp = re.match(r"(?:Week|Woche) (\d+).*?·\s*(Skill Review Week|Survival Week)", cab)
        if esp:
            return semana_especial(int(esp.group(1)), esp.group(2), cuerpo,
                                   idioma, nivel), None
        return None, f"encabezado no reconocido: {cab[:60]}"
    semana, sid, titulo = int(m.group(1)), m.group(2), m.group(3).strip()

    mo = re.search(r"\*(?:Occurrence|Vorkommen) (\d+) (?:of|von) \d+\s*[—–-]\s*(.+?)\*", cuerpo)
    order = int(mo.group(1)) if mo else 1
    subtitulo = mo.group(2).strip() if mo else titulo

    mt = re.search(r"\*\*(?:Topic|Thema):\*\*\s*(T\d\d)", cuerpo)
    topic = mt.group(1) if mt else None

    # La cabecera trae "Evidence: X words or Y min - Time: ~Z min + W min".
    # Hay que leer evidencia y tiempo de sus propios campos: buscar en todo el
    # cuerpo hace que el tiempo estimado se cuele como minutos de audio.
    cab_ev = re.search(r"\*\*(?:Evidence|Nachweis):\*\*(.*?)(?:\*\*|$)", cuerpo, re.S)
    cab_t  = re.search(r"\*\*(?:Time|Zeit):\*\*(.*?)$", cuerpo, re.M)
    ev  = cab_ev.group(1) if cab_ev else ""
    tt  = cab_t.group(1) if cab_t else ""
    me = re.search(r"(\d+)\s*(?:words|Wörter)", ev)
    ms = re.search(r"(\d+)[\s-]*(?:min|Min)", ev)
    mtime = re.findall(r"(\d+)\s*(?:min|Min)", tt)

    s = secciones(cuerpo, et)
    if "descripcion" not in s:
        return None, f"{sid}: falta la descripcion"

    ficha = {
        "id": f"{sid}-{nivel}-{order}", "skillId": sid, "order": order,
        "idioma": idioma, "nivel": nivel, "categoria": CAT[sid[3]],
        "topicId": topic, "titulo": titulo, "subtitulo": subtitulo,
        "challengeType": RETO[nivel],
        "evidencia": {"escritura": int(me.group(1)) if me else 250,
                      "oralMin": min(int(ms.group(1)), 30) if ms else 4,
                      "minutosEstimados": sum(int(x) for x in mtime[:2]) or 55},
        "descripcion": aplanar_marcado(limpiar(s["descripcion"]))[:1590],
        "ejemplos": [], "notas": [], "errores": [], "vocabulario": [],
        "redemittel": [], "mision": {}, "microtareas": [], "autochequeo": [],
        "promptCorreccion": "",
    }

    if "cuadro" in s:
        c, f = tabla(s["cuadro"])
        # Nota al pie: las lineas de prosa que van DESPUES de la tabla dentro
        # del bloque del cuadro. Llevan reglas mnemotecnicas que estaban
        # perdiendose en silencio ("Merke: und, aber, denn... no provocan
        # inversion"), que es peor que un problema estetico.
        pie = []
        vista_tabla = False
        for linea in s["cuadro"].splitlines():
            t = linea.strip()
            if t.startswith("|"):
                vista_tabla = True
            elif vista_tabla and t:
                pie.append(t)
        if c and f:
            ficha["cuadroReferencia"] = {
                "titulo": capitalizar(desenvolver(s.get("cuadro__titulo", "").split("—")[-1])),
                "columnas": [desenvolver(x) for x in c[:5]],
                "filas": [[desenvolver(y) for y in x[:5]] for x in f[:20]],
                **({"notaPie": desenvolver(" ".join(pie))} if pie else {})}

    for l in vinetas(s.get("ejemplos", "")):
        ficha["ejemplos"].append({"texto": aplanar_marcado(limpiar(l))})
    ficha["notas"] = [aplanar_marcado(limpiar(x)) for x in vinetas(s.get("notas", ""))]
    ficha["errores"] = [aplanar_marcado(limpiar(x)) for x in vinetas(s.get("errores", ""))]

    contr = bloques_por_lengua(cuerpo, "Kontrast|Contrast")
    if contr:
        ficha["contraste"] = {k: aplanar_marcado(limpiar(v)) for k, v in contr.items()}
    elif "contraste" in s:
        ficha["contraste"] = {"es": aplanar_marcado(limpiar(s["contraste"]))}

    # Errores adicionales por lengua base: los errores tipicos de un
    # anglohablante que aprende aleman no son los de un hispanohablante.
    errc = bloques_por_lengua(cuerpo, "Typische Fehler|Common mistakes")
    errc = {k: [aplanar_marcado(limpiar(x)) for x in vinetas(v)]
            for k, v in errc.items()}
    errc = {k: v for k, v in errc.items() if v}
    if errc:
        ficha["erroresContrastivos"] = errc

    c, f = tabla(s.get("vocabulario", ""))
    if c:
        col = {n.upper(): i for i, n in enumerate(c)}
        # Cualquier columna cuya cabecera sea un codigo de idioma es una glosa.
        # Asi agregar FR o IT no toca el compilador: basta anadir la columna.
        IDIOMAS = {"ES", "EN", "DE", "FR", "IT", "PT"}
        cols_glosa = {n.upper(): i for i, n in enumerate(c) if n.upper() in IDIOMAS}
        for fila in f:
            item = fila[0]
            v = {"item": sin_marcado(item),
                 "prioridad": "nucleo" if "\u2605" in item else "ampliacion",
                 "traducciones": {k.lower(): fila[i].strip()
                                  for k, i in cols_glosa.items()
                                  if len(fila) > i and fila[i].strip()}}
            if "\u2020" in item:
                # Declarado por debajo del nivel a proposito: lo que se ensena
                # es una distincion, un registro o un doble sentido que si
                # corresponde al nivel. La app debe poder mostrarlo como nota.
                v["bajoNivelJustificado"] = True
            if idioma == "de":
                r = limpiar(fila[1]) if len(fila) > 1 else ""
                if r and r not in ("—", "-"):      # "—" significa "sin rección"
                    v["reccion"] = r
            if len(fila) > 2 and fila[2].strip() and fila[2].strip() != "—":
                v["nota"] = limpiar(fila[2])
            if "CH" in " ".join(fila):
                v["variante"] = "CH"
            ficha["vocabulario"].append(v)

    c, f = tabla(s.get("redemittel", ""))
    if c and len(c) >= 3:
        IDIOMAS = {"ES", "EN", "DE", "FR", "IT", "PT"}
        cols_g = {n.upper(): i for i, n in enumerate(c) if n.upper() in IDIOMAS}
        for fila in f:
            tr = {}
            for k, i in cols_g.items():
                if len(fila) <= i:
                    continue
                v = fila[i].strip()
                # "—" es deliberado: la expresion no tiene equivalencia directa
                # y se aprende por situacion. Se guarda como null, no se omite.
                tr[k.lower()] = None if v in ("—", "-", "") else v
            ficha["redemittel"].append({
                "expresion": sin_marcado(fila[0]),
                "funcion": aplanar_marcado(limpiar(fila[1])),
                "traducciones": tr})
    else:  # formato en linea: "*X* · *Y* · *Z*"
        for e in re.findall(r"\*([^*]{4,60})\*", s.get("redemittel", "")):
            ficha["redemittel"].append({"expresion": sin_marcado(e),
                                        "funcion": "—", "traducciones": {"es": None}})

    bloque_m = s.get("mision", "")
    req = vinetas(bloque_m)
    consigna = bloque_m.split("\n")[0].strip()
    if req:
        requisitos = [limpiar(r) for r in req]
    else:
        # "Required: a, b, c and d." -> cuatro requisitos
        m_req = re.search(r"(?:Required|Vorgaben):\s*(.+)", consigna)
        crudo = m_req.group(1) if m_req else consigna
        requisitos = [limpiar(x) for x in re.split(r",\s*(?:and\s+)?|\.\s+", crudo)
                      if len(x.strip()) > 4]
        consigna = consigna.split("Required")[0].split("Vorgaben")[0].strip() or consigna
    ficha["mision"] = {"consigna": aplanar_marcado(limpiar(consigna)),
                       "requisitos": [aplanar_marcado(r) for r in requisitos[:8]]}

    for l in vinetas(s.get("microtareas", "")):
        md = re.match(r"\*?(\w+)\*?\s*\((\d+)\s*(?:min|Min)[^)]*\)\.?\*?\s*(.+)", l)
        if md:
            ficha["microtareas"].append({
                "dia": DIAS.get(md.group(1), "lun"),
                "minutos": int(md.group(2)), "texto": limpiar(md.group(3))})
    ficha["autochequeo"] = [aplanar_marcado(limpiar(x)) for x in numeradas(s.get("autochequeo", ""))][:5]

    pr = s.get("prompt", "")
    ficha["promptCorreccion"] = aplanar_marcado(limpiar(re.sub(r"^>\s*", "", pr, flags=re.M)))

    audio = numeradas(s.get("audio", ""))
    for i, a in enumerate(audio):
        txt = limpiar(a)
        for e in ficha["ejemplos"]:
            if txt[:28].lower() in e["texto"].lower():
                e["audio"] = True
                break
        else:
            ficha["ejemplos"].append({"texto": txt, "audio": True})
    return ficha, None

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fichas", nargs="+")
    ap.add_argument("--salida", default="../build")
    ap.add_argument("--schema")
    a = ap.parse_args()
    Path(a.salida).mkdir(parents=True, exist_ok=True)
    val = None
    if a.schema:
        import jsonschema
        val = jsonschema.Draft202012Validator(json.loads(Path(a.schema).read_text(encoding="utf-8")))

    ok = fallos = 0
    for f in a.fichas:
        texto = Path(f).read_text(encoding="utf-8")
        # El idioma y el nivel salen del NOMBRE DE ARCHIVO, no del contenido.
        # Asumir el nivel por idioma ("en es C1") se rompio en cuanto ingles
        # tuvo dos niveles: las 17 fichas de B2 se compilaron como C1.
        m = re.match(r"(EN|DE|ES|FR|IT|PT)-([ABC][12])-", Path(f).name, re.I)
        if not m:
            print(f"  ! {Path(f).name}: el nombre debe empezar por IDIOMA-NIVEL-, "
                  f"p.ej. EN-B2-2026-S37-S53.md", file=sys.stderr)
            fallos += 1
            continue
        idioma, nivel = m.group(1).lower(), m.group(2).upper()
        for bloque in trocear(texto):
            ficha, err = compilar(bloque, idioma, nivel)
            if err:
                print(f"  ! {err}", file=sys.stderr); fallos += 1; continue
            especial = ficha.get("_tipo") == "semana_especial"
            problemas = [] if especial else (
                [f"{list(e.path)}: {e.message[:90]}" for e in val.iter_errors(ficha)]
                if val else [])
            if problemas:
                print(f"  ! {ficha['id']} no valida:", file=sys.stderr)
                for p in problemas[:4]:
                    print(f"      {p}", file=sys.stderr)
                fallos += 1
            else:
                Path(a.salida, ficha["id"] + ".json").write_text(
                    json.dumps(ficha, ensure_ascii=False, indent=1), encoding="utf-8")
                marca = "~~" if especial else "ok"
                print(f"  {marca} {ficha['id']:18s} {ficha['titulo'][:42]}")
                ok += 1
    print(f"\n{ok} fichas compiladas, {fallos} con problemas")
    sys.exit(1 if fallos else 0)

if __name__ == "__main__":
    main()
