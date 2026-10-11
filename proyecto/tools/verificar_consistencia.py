#!/usr/bin/env python3
"""Verificador de consistencia del contenido (oct 2026).

Reune las reglas que Fer encontro violadas a mano en la app y que una
maquina puede comprobar. Corre sobre la salida de componer.py (no sobre los
nucleos crudos), asi ve lo mismo que la app.

  python3 tools/componer.py --contenido contenido --salida /tmp/b --schema schema/ficha.schema.json
  python3 tools/verificar_consistencia.py --build /tmp/b

Sale con codigo 1 si hay algun error. Reglas (ver REGLAS-PREVENCION.md):
  R1  Todo campo de prosa de A2/B1 es bilingue (objeto), incluidos los items
      de erroresContrastivos.
  R2  Los nombres de topic y de categoriasUso existen en los 6 idiomas.
  R3  Los textos fijos de la planilla existen en los 6 idiomas con las mismas
      claves.
  R4  Los « » estan balanceados en todo el contenido.
  R5  Todo categoriasUso usado pertenece a la lista cerrada.
  R6  El espanol del contenido es neutro (tuteo): ninguna cadena ESPANOLA
      lleva formas de voseo (vos, tenes, podes, proba, decime...).

      Dos precisiones, aprendidas al pasar el contenido a tuteo (oct 2026):

      a) Solo se revisan las cadenas cuyo camino TERMINA en la clave 'es'. En
         un campo bilingue de A2/B1 el valor de la clave 'es' es un objeto
         {idioma_aprendido: original, es: traduccion}, asi que recorrer todo
         lo que cuelga de 'es' tambien leia el aleman y el ingles, y marcaba
         como voseo palabras que no son espanol.

      b) La lista NO incluye las formas en -i que coinciden con el preterito
         de primera persona: elegi, corregi, describi, escribi, repeti,
         resumi, sali. 'Corregi' puede ser imperativo voseante o 'yo corregi',
         y los autochequeos estan escritos justamente en primera persona
         ('Corregi con claridad un error propio?'). Marcarlas daba falsos
         positivos sobre espanol correcto y, como esta regla corre en
         preBuild, dejaba el build en rojo sin arreglo posible. Si aparece
         voseo con esas formas hay que verlo a mano.

  R7  El espanol del contenido trata al lector de TU y usa lexico neutro:
      ninguna cadena espanola lleva un imperativo de usted en posicion de
      imperativo, ni los peninsularismos de la lista corta. Las excepciones
      son explicitas (EXENTAS_R7) y cada una dice por que. Ver FALTANTES 6.1.1.

  R9  El texto de INSTRUCCION de aleman, frances e italiano trata al
      aprendiente de TU (decision de Fer del 10-10-2026: tu en todos los
      idiomas). Se revisan solo los campos donde la app le habla al
      aprendiente (CAMPOS_R9) y NO las ramas de contenido ensenado
      (PROHIBIDAS_R9: redemittel, ejemplos, cuadroReferencia, contraste,
      erroresContrastivos, vocabulario), donde el registro formal puede ser
      justamente lo que se ensena. Tampoco se mira dentro de « » ni de *…*:
      ahi va el idioma que se aprende.

      Limite conocido: en italiano la cortesia usa la 3a persona del
      indicativo ('che cosa ha comprato?'), identica a una 3a persona real
      ('che cosa ha detto il medico?'). La regla no puede distinguirlas y no
      las marca; las 15 cadenas de esa clase se arreglaron a mano en oct 2026
      y hay que revisarlas a ojo al escribir contenido nuevo (FALTANTES 6.1).

  R10 Ortografia suiza: ninguna cadena lleva ß. Las excepciones son las
      cadenas donde la eszett es el objeto de la explicacion (la ficha
      DE-V08 y las notas sobre «ss/ß»), en EXENTAS_R10 con su motivo.
"""
import argparse, glob, json, os, re, sys

RAIZ = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
LANGS = ["es", "en", "de", "fr", "it", "pt"]
CAMPOS_BILINGUES = ["titulo", "subtitulo", "descripcion", "notas", "errores", "autochequeo", "promptCorreccion"]


def cargar(p):
    with open(p, encoding="utf-8") as f:
        return json.load(f)


def recorrer_textos(o):
    if isinstance(o, str):
        yield o
    elif isinstance(o, list):
        for x in o:
            yield from recorrer_textos(x)
    elif isinstance(o, dict):
        for v in o.values():
            yield from recorrer_textos(v)


VOSEO = re.compile(
    # pronombre: 'vos' pronombre, no la mencion metalinguistica «vos/tu»
    r"\b(vos(?!/)|sé vos"
    # presente de indicativo voseante (-as/-es/-is)
    r"|tenés|podés|querés|sabés|decís|hacés|usás|pensás|sentís|andás|basás|conocés|copás|creés|debés"
    r"|decidís|esperás|llevás|movés|opinás|preferís|probás|referís|repetís|venís|vivís|ponés|salís"
    # imperativo voseante de verbos en -ar (sin ambiguedad con el preterito)
    r"|probá|mirá|hablá|pensá|intentá|contá|usá|escuchá|completá|marcá|anotá|grabá|agregá|revisá"
    r"|preguntá|buscá|fijá|armá|tratá|empezá|ordená|explicá|contestá|practicá|andá|adiviná|entrá"
    r"|esperá|guardá|pará|sacá|cambiá|dejá|tomá|llamá|avisá|mandá|pasá|ayudá|prepará|imaginá|acercá"
    # imperativo voseante de verbos en -er/-ir sin colision con el preterito
    r"|decí|leé|respondé|vení|tené|hacé|poné|vé"
    # imperativo voseante con enclitico (el tuteo lleva tilde: dejame/dejame -> dejame)
    r"|decime|decilo|decile|dejame|dejalo|dejala|dejanos|avisame|avisanos|contame|contanos|mirame"
    r"|miralo|mirala|esperame|esperanos|ayudame|ayudanos|preguntale|preguntame|tomalo|tomala|leelo"
    r"|leela|hacelo|hacela|ponelo|ponela|tenelo|pensalo|probalo|fijate|ponete|acordate|sentate"
    r"|quedate|callate|levantate|apurate|animate|preparate|imaginate|acercate|movete|volvete"
    # regionalismos de un solo pais, sin ambiguedad. 'dale' NO entra: en
    # 'Dale consejos a un amigo' es el imperativo normal de dar + le, asi que
    # la particula rioplatense 'dale' hay que verla a mano.
    r"|nomás|laburo|laburar"
    # vosotros: tampoco es neutro. Aparecio una vez, en una cita de ejemplo
    # («como ya sabéis»), y el espanol neutro usa ustedes.
    r"|vosotros|vuestro|vuestra|vuestros|vuestras|habéis|tenéis|podéis|queréis|sabéis"
    r"|sois|estáis|hacéis|vais|veis|dais|coméis|vivís|escribís"
    r")\b", re.IGNORECASE)


# --------------------------------------------------------------------------
# R7: usted y lexico peninsular (decision de Fer del 10-10-2026: tu en todo,
# mas una lista CORTA de sustituciones de lexico).
# --------------------------------------------------------------------------
# Solo se marca el imperativo de usted en POSICION DE IMPERATIVO (arranque de
# la cadena o detras de . ! ? : ; ¡ ¿ — ) ). Un subjuntivo dentro de 'que ...'
# tiene la misma forma ('que explique por que', 'segun se mire', 'aunque se
# cuente') y es espanol correcto: marcarlo dejaria el build en rojo para
# siempre, porque esta regla corre en preBuild.
# La posicion de imperativo incluye el arranque de la cadena, el detras de un
# signo de puntuacion, y el detras de un cierre de *cursiva* o de '(N)', porque
# las micro-tareas escriben 'Cinco oraciones: *Ich stehe auf.* Subraya el
# prefijo.' y '60 segundos: compara precios'.
_POS = r"(?:^|[.!?:;¡¿—)*]\s+|^¡|—\s*|\bLuego\s+|\bDespués\s+)"
USTED_IMP = re.compile(
    _POS +
    r"(reescriba|compruebe|revise|escriba|hable|describa|haga|responda|anote|explique"
    r"|indique|compare|repita|ordene|subraye|marque|reformule|formule|elija|pruebe"
    r"|busque|complete|grabe|agregue|añada|diga|cuente|escuche|practique|intente"
    r"|corrija|resuma|empiece|cambie|trate|piense|pida|fíjese|acuérdese"
    r"|concéntrese|imagine|prepare|justifique|identifique|evite|incluya|exprese"
    r"|preste|termine|convierta|solicite|converse|preséntese|corríjase|limítese"
    r"|léalas|léalo|respóndale|explíquele|cuéntele|dígalo|hágalo"
    r"|escríbalo|anótelo|revíselo|repítalo|descríbalo|póngase"
    r"|sírvase|use|pase|vaya|llame|mire|tenga|deje|tome|avise|ponga|salga|venga"
    r"|siga|vuelva|observe|verifique|repase|adapte|transforme|decida|defina|aplique"
    r"|señale|enumere|sustituya|reemplace|simplifique|amplíe)\b",
    re.IGNORECASE)

# Lista CORTA. Deliberadamente NO estan:
#   vale     -> en todo el contenido es el verbo 'valer' ('vale la pena'), no
#               el '!vale!' de Espana.
#   billete  -> en los packs de finanzas traduce 'der Schein' / 'a note', o sea
#               dinero, y ahi sirve en toda America. Solo el billete de
#               transporte se paso a 'boleto', a mano.
#   camarero -> no hay termino neutro ('mesero' America, 'mozo' Rio de la
#               Plata, 'camarero' Espana). Se dejo y esta documentado en
#               FALTANTES 6.1.1.
#   tio/tia  -> en este contenido son el tio y la tia de la familia, no el
#               'tio' coloquial de Espana.
# 'telefonia movil' es un adjetivo normal en todo el espanol: solo el
# sustantivo 'el movil' (= el celular) es peninsular.
LEXICO_PENINSULAR = re.compile(
    r"\b(aparcar|aparcamiento|coches?|ordenador(?:es)?|(?<!telefonía )móvil(?:es)?"
    r"|coger confianza|pisos?)\b", re.IGNORECASE)

# Excepciones explicitas de R7, por cadena completa.
EXENTAS_R7 = {
    # 'der Stock': aqui 'piso' es la planta de un edificio, valido en todas partes
    "el piso, la planta",
    # nota que habla DE la variacion regional del espanol
    "El español conoce, incluso dentro de América Latina, muchas palabras que cambian "
    "según el país («computadora/ordenador», «plata/dinero») sin que una variante sea "
    "incorrecta. En ese mismo sentido, palabras suizas como «Velo» o «Znüni» no son jerga, "
    "sino formas del alemán estándar de Suiza.",
    # interjeccion, no imperativo de 'ir'
    "Vaya novedad.",
    # filas formales en los SEIS idiomas (aleman 'Rufen Sie', frances 'Appelez',
    # italiano 'Chiami'): el registro formal es el contenido que se ensena
    "¡Llame a una ambulancia!",
    "¡Llame a una ambulancia, por favor!",
    "¡Pase, por favor!",
}


# --------------------------------------------------------------------------
# R8: fragmentos del idioma que se aprende sin « » (P5 de REGLAS-PREVENCION).
# R4 solo mira que los « » esten balanceados; R8 mira que EXISTAN.
#
# Se revisan las fichas EN y DE de A2/B1, en cada valor de texto bajo una
# clave de idioma distinta del que se aprende. No se revisa el texto del
# idioma aprendido (ahi el fragmento ES el idioma del texto), ni el campo
# 'funcion' (son etiquetas semanticas casi identicas entre idiomas:
# 'opinion'/'opinion'/'opinione'), ni 'vocabulario[].traducciones' (si la
# glosa correcta es la misma palabra -- 'la pollution', 'le stress', 'la
# privacy' -- marcarla no aporta nada), ni 'titulo': el titulo no lleva
# marcado de ningun tipo (tampoco *cursiva*, ver reglas-fichas) y tiene un
# tope de 80 caracteres que los « » hacen saltar.
# --------------------------------------------------------------------------
FUNC = {
    "en": ["I think", "I believe", "I agree", "I disagree", "because", "however",
           "maybe", "perhaps", "should", "would", "could", "might", "going to",
           "will", "shall", "must", "may", "can", "cannot", "should have",
           "would have", "could have", "might have", "must be", "can't be",
           "had better", "would rather", "be going to", "be able to",
           "used to", "have to", "there is", "there are", "I'm", "you're", "it's",
           "the", "some", "any", "who", "which", "that", "whose", "whom",
           "much", "many", "was", "were", "always", "usually", "often", "never",
           "first", "then", "after that", "if I were you", "if I had known",
           "I would known", "she always arrives", "I found out that she moved",
           "make a decision", "have breakfast", "have lunch", "have dinner",
           "take a shower", "have a shower", "find out", "discover", "make",
           "have", "take", "get", "give", "Recht haben",
           "Simple Present", "Present Continuous", "Simple Past", "Present Perfect",
           "First Conditional", "Second Conditional", "Third Conditional",
           "don't", "doesn't", "didn't", "can't", "won't", "isn't", "aren't",
           "mustn't", "mustn't be", "needn't", "shouldn't", "couldn't",
           "wouldn't", "haven't", "hasn't", "hadn't", "wasn't", "weren't",

           "I was", "I have", "do you", "did you",
           "present perfect", "past simple", "simple past", "present simple",
           "present continuous", "past continuous", "past perfect", "phrasal verb",
           "phrasal verbs", "question tag", "question tags", "tag question",
           "first conditional", "second conditional", "third conditional",
           "what do you think", "how about", "what about", "by the way"],
    "de": ["ich denke", "ich finde", "ich glaube", "meiner Meinung nach", "weil",
           "deshalb", "deswegen", "darum", "obwohl", "trotzdem", "aber",
           "ich hätte", "ich würde", "ich möchte", "man muss", "es gibt",
           "zuerst", "dann", "danach", "als", "wenn", "wo", "wohin", "woher",
           "wie", "warum", "wieso", "seitdem", "nachdem", "während", "bevor",
           "damit", "sodass", "falls", "sobald", "solange", "entweder", "weder",
           "sowohl", "ausserdem", "trotzdem", "allerdings", "jedoch", "sondern",
           "Ich muss", "Ich kann", "Ich will", "Ich darf", "Ich soll",
           "Akkusativ", "Dativ", "zu Hause", "nach Hause",
           "Perfekt", "Präteritum", "Plusquamperfekt", "Konjunktiv", "Konjunktiv II",
           "Passiv", "Aktiv", "Nominalisierung", "Relativsatz", "Relativsätze",
           "Konnektoren", "Nebensatz", "Hauptsatz", "Satzbau", "Wortstellung",
           "Modalpartikel", "Modalpartikeln", "Futur", "Futur I", "Imperativ",
           "Dativ", "Akkusativ", "Nominativ", "Genitiv", "Komparativ", "Superlativ",
           "Umlaut", "Partizip", "Partizip II", "Infinitiv",
           "können", "müssen", "wollen", "sollen", "dürfen", "mögen", "möchten",
           "hätte", "wäre", "würde", "nicht müssen", "nicht dürfen",
           "könnten", "könnte", "solltest", "sollte", "möchte", "Recht haben",
           "Konjunktiv II", "ich arbeite", "er schläft"],
}

COLISIONES = {
    ("en", "es"): {"no", "a", "me", "sin", "con", "o", "e", "mi", "tu", "su", "la",
                   "el", "un", "una", "de", "en", "es", "si", "ya", "van", "son"},
    ("en", "it"): {"no", "a", "i", "e", "o", "me", "se", "da", "di", "la", "il",
                   "un", "una", "in", "non", "come", "sono", "van"},
    ("en", "fr"): {"a", "on", "en", "la", "le", "un", "une", "de", "des", "du",
                   "me", "se", "si", "ou", "et", "sur", "pour", "mais", "car",
                   # mismas palabras en frances: no son fragmento ingles
                   "minutes", "minute", "note", "notes", "plan", "train",
                   "important", "central", "nature", "sport", "service",
                   "arrive", "arrives", "question", "questions", "double"},
    ("en", "pt"): {"a", "o", "as", "os", "da", "das", "do", "dos", "no", "na",
                   "em", "me", "se", "um", "uma", "de", "e", "ou", "com", "sem"},
    ("en", "de"): {"die", "das", "der", "ein", "eine", "in", "an", "am", "im",
                   "so", "also", "man", "war", "ist", "hat", "will", "mit", "bei",
                   # 'was' en aleman es 'que/lo que', no el 'was' ingles; 'wer'
                   # y 'wen' son pronombres alemanes
                   "was", "wer", "wen", "wem", "wo", "wie", "den", "dem", "des",
                   "sie", "wir", "ihr", "uns", "nie", "halt", "eben", "doch"},
    ("de", "es"): {"no", "a", "me", "o", "e", "mi", "tu", "su", "la", "el", "un",
                   "una", "de", "en", "es", "si", "ya", "son", "va", "vas"},
    ("de", "it"): {"no", "a", "i", "e", "o", "me", "se", "da", "di", "la", "il",
                   "un", "una", "in", "non", "come", "sono", "stato", "mai"},
    ("de", "fr"): {"a", "on", "en", "la", "le", "un", "une", "de", "des", "du",
                   "me", "se", "si", "ou", "et", "sur", "pour", "mais", "car",
                   "ans", "bus", "pas",
                   # el frances tiene las mismas palabras: no son fragmento aleman
                   "futur", "imperatif", "nature", "important", "central",
                   "central", "mode", "note", "plan", "train", "rose", "sport"},
    ("de", "pt"): {"a", "o", "as", "os", "da", "das", "do", "dos", "no", "na",
                   "em", "me", "se", "um", "uma", "de", "e", "ou", "com", "sem",
                   "bem", "mais", "ja"},
    ("de", "en"): {"a", "an", "the", "in", "is", "it", "so", "also", "man", "war",
                   "will", "hat", "am", "bin", "ist", "was", "wall", "rat", "die",
                   "hast", "mit", "bald", "halt", "gift", "art", "bad", "fast"},
}

COMUNES = {
    # Cognados: misma grafia en el idioma de destino, asi que no son un
    # fragmento extranjero sino la palabra normal del destino. Cada uno se
    # dejo fuera tras verlo dar un falso positivo.
    "alternative", "routine", "problem", "plan", "sport", "obligation",
    "suggestion", "opinion", "information", "moment", "normal", "total",
    "international", "interesse", "interessant", "kontakt", "material",
    "person", "service", "standard", "system", "telefon", "text", "zentral",
    "position", "position 2", "position 1", "basis", "detail", "direkt",
    "ideal", "modern", "privat", "intern", "extern", "neutral", "idea",
    "proportional", "temporal", "lokal", "kausal", "konzessiv", "final",
    "positiv", "negativ", "pollution", "privacy", "stress", "wifi", "central",
    "important", "parents", "documents", "nature", "air", "roles", "dose",
    "reception", "part-time", "fake news", "training", "make-up",
    "superlative", "superlatives", "comparative", "comparatives", "futur",
    "imperativ", "infinitiv", "partizip", "umlaut",
}


# Excepciones de R8, por cadena completa.
EXENTAS_R8 = {
    # 'Velo' es la palabra suiza para la bicicleta y aqui es el objeto del que
    # habla la nota, no un fragmento suelto; ya va entre « » donde hace falta.
    "«Velo», «Billett» y «Trottoir» proceden originalmente del francés.",
}


def _enmascarar_r8(t):
    """Tapa lo que ya esta marcado: « » y *…*."""
    t = re.sub(r"«[^»]*»", lambda m: " " * len(m.group(0)), t)
    t = re.sub(r"\*\*?[^*]+\*\*?", lambda m: " " * len(m.group(0)), t)
    return t


def _limpiar_frag(s):
    s = re.sub(r"^[…\.,;:¡¿\"'\s]+", "", s.strip())
    return re.sub(r"[…\.,;:!?\"'\s]+$", "", s).strip()


def _fragmentos_ficha(d, ap):
    """Textos en el idioma aprendido que trae la propia ficha."""
    out = set()
    for r in d.get("redemittel", []) or []:
        if isinstance(r, dict) and isinstance(r.get("expresion"), str):
            out.add(r["expresion"])
    for v in d.get("vocabulario", []) or []:
        if isinstance(v, dict) and isinstance(v.get("item"), str):
            out.add(v["item"])
    for e in d.get("ejemplos", []) or []:
        tx = e.get("texto") if isinstance(e, dict) else None
        if isinstance(tx, dict) and isinstance(tx.get(ap), str):
            out.add(tx[ap])
    for fila in ((d.get("cuadroReferencia") or {}).get("filas") or []):
        for c in fila:
            if isinstance(c, dict) and isinstance(c.get(ap), str):
                out.add(c[ap])
            elif isinstance(c, str):
                out.add(c)
    return {f for f in (_limpiar_frag(x) for x in out) if 3 <= len(f) <= 48}


def _textos_con_ruta(o, ruta=None):
    ruta = ruta or []
    if isinstance(o, dict):
        for k, v in o.items():
            yield from _textos_con_ruta(v, ruta + [k])
    elif isinstance(o, list):
        for i, x in enumerate(o):
            yield from _textos_con_ruta(x, ruta + [i])
    elif isinstance(o, str):
        yield (ruta, o)


def revisar_r8(d, nom, errores):
    if d.get("nivel") not in ("A2", "B1") or d.get("idioma") not in ("en", "de"):
        return
    ap = d["idioma"]
    propios = _fragmentos_ficha(d, ap)
    cache = {}
    for ruta, t in _textos_con_ruta(d):
        if not ruta or ruta[-1] not in LANGS or ruta[-1] == ap:
            continue
        if "funcion" in ruta or "titulo" in ruta:
            continue
        if "vocabulario" in ruta and "traducciones" in ruta:
            continue
        if t in EXENTAS_R8:
            continue
        destino = ruta[-1]
        if destino not in cache:
            col = COLISIONES.get((ap, destino), set())
            fr = sorted({f for f in list(propios) + FUNC.get(ap, [])
                         if 3 <= len(f) <= 48
                         and f.lower() not in col and f.lower() not in COMUNES},
                        key=len, reverse=True)
            cache[destino] = re.compile(
                r"(?<!\w)(" + "|".join(re.escape(f) for f in fr) + r")(?!\w)",
                re.IGNORECASE) if fr else None
        rx = cache[destino]
        if rx is None:
            continue
        m = rx.search(_enmascarar_r8(t))
        if m:
            errores.append(f"R8 {nom}: '{m.group(1)}' en {ruta[-1]} sin « »: {t[:60]}")


# --------------------------------------------------------------------------
# R9: trato formal en el texto de instruccion de aleman, frances e italiano.
# --------------------------------------------------------------------------
# Campos donde la app le habla al aprendiente.
CAMPOS_R9 = ("mision", "microtareas", "autochequeo", "promptCorreccion", "notas",
             "descripcion", "subtitulo", "requisitos", "consigna", "entregable",
             "criterios", "texto", "pista")
# Ramas que NO se miran: ahi el registro formal es contenido ensenado.
PROHIBIDAS_R9 = ("redemittel", "ejemplos", "cuadroReferencia", "contraste",
                 "erroresContrastivos", "vocabulario")

# Aleman: pronombres y posesivos de cortesia.
R9_DE = re.compile(r"\b(Sie|Ihnen|Ihre[mnrs]?|Ihr)\b")

# Frances: vous/votre/vos y toda forma en -ez (imperativo o presente de
# cortesia). FR_NO_VERBO son las palabras en -ez que no son verbos.
R9_FR = re.compile(r"\b([Vv]ous|[Vv]otre|[Vv]os|[Vv]ôtres?)\b|\b(\w+ez)\b")
FR_NO_VERBO = {"chez", "assez", "nez"}

# Italiano, cuatro piezas:
# a) pronombre y posesivo de cortesia. El posesivo aparece en mayuscula
#    ('Sua madre') y en minuscula ('la sua opinione'); en minuscula coincide
#    con el posesivo de 3a persona, asi que se marca igual y las pocas
#    cadenas que de verdad hablan de otra persona van en EXENTAS_R9.
R9_IT_PRON = re.compile(r"\b(Lei|[Ss]uo|[Ss]ua|[Ss]uoi|[Ss]ue)\b")
# b) congiuntivo de cortesia inequivoco: la forma de tu seria otra.
IT_UNAMB = ("Scriva|Riscriva|Descriva|Risponda|Legga|Rilegga|Prenda|Scelga|Faccia|"
            "Metta|Vada|Aggiunga|Corregga|Ripeta|Chieda|Veda|Venga|Tenga|Ponga|"
            "Esponga|Proponga|Riassuma|Decida|Reagisca|Esegua|Traduca|Dia|"
            "Richieda|Concluda|Riprenda|Discuta|"
            "Esprima|Rifletta|Rimetta|Distingua|Deduca|Riconosca|Sostituisca|"
            "Inserisca|Costruisca|Finisca|Ammetta")
# c) congiuntivo de los verbos en -are: coincide con el presente de tu ('tu
#    parli'), asi que solo cuenta en posicion de imperativo.
IT_AMB = ("Parli|Controlli|Verifichi|Racconti|Usi|Annoti|Confronti|Segni|Immagini|"
          "Registri|Spieghi|Eviti|Noti|Pensi|Completi|Elenchi|Provi|Colleghi|"
          "Formuli|Cerchi|Guardi|Ascolti|Ordini|Termini|Inizi|Continui|Partecipi|"
          "Prepari|Confermi|Giustifichi|Trasformi|Riformuli|Compili|Osservi|"
          "Sottolinei|Ripassi|Cambi|Marchi|Indichi")
PRE_IMP_IT = (r"(?:^|(?<=[:.!?;—] )|(?<=, )|(?<=\be )|(?<=\bo )|(?<=\bpoi )"
              r"|(?<=\bPoi )|(?<=\bNon )|(?<=\bnon ))")
# 'non' no entra aqui: 'che non usi piu' es presente de 2a persona correcto.
PRE_IMP_AMB = (r"(?:^|(?<=[:.!?;—] )|(?<=, )|(?<=\be )|(?<=\bo )|(?<=\bpoi )"
               r"|(?<=\bPoi ))")
R9_IT_AMB = re.compile(PRE_IMP_AMB + r"(" + IT_AMB + r")\b", re.IGNORECASE)
# En minuscula la forma inequivoca solo es cortesia detras de 'non', de un
# pronombre atono ('Gli dia', 'si corregga') o al principio de la cadena.
R9_IT_UNAMB = re.compile(
    r"\b(" + IT_UNAMB + r")\b|" + PRE_IMP_IT + r"[Nn]on\s+(" + IT_UNAMB.lower() + r")\b|"
    r"\b(?:[GgMmTtSsCcVv]li|[Ll]e|[Ll]o|[Ll]a|mi|si|ci|ne)\s+(" + IT_UNAMB.lower() + r")\b|"
    r"^(" + IT_UNAMB.lower() + r")\b")
# d) reflexivo de cortesia.
R9_IT_REFL = re.compile(r"\b[Ss]i\s+(concentri|eserciti|assicuri|prepari|fermi|"
                        r"abitui|ricordi|informi|limiti|registri)\b")

EXENTAS_R9 = {
    "Können Sie…? ist die einfachste höfliche Bitte.": "DE-V08: la forma de cortesia es el contenido que se ensena",
    "Könnten Sie…? ist die höflichste einfache Bitte auf B1.": "DE-V08: la forma de cortesia es el contenido que se ensena",
    "Auf B1 wird die Wahl zwischen du und Sie sicher getroffen: Sie bei Fremden und in formellen Situationen, du bei Freunden und Familie. Feinere Register-Unterschiede kommen erst später — hier zählt vor allem, nicht mitten im Gespräch zu wechseln.": "DE-V08: describe el contraste du/Sie",
    "Sie wird immer grossgeschrieben, auch mitten im Satz.": "DE-V08: regla ortografica sobre el pronombre Sie",
    "Im Zweifel ist es höflicher, mit Sie zu beginnen, als zu früh zu duzen.": "DE-V08: consejo sobre cuando usar Sie",
    "Ist Sie durchgehend grossgeschrieben?": "DE-V08: autochequeo sobre la ortografia de Sie",
    "Habe ich nicht ohne Grund zwischen du und Sie gewechselt?": "DE-V08: autochequeo sobre el cambio de registro",
    "Habe ich mindestens ein Beispiel für du UND eines für Sie gegeben?": "DE-V08: autochequeo que pide un ejemplo de cada registro",
    "Wie geht's? ist informell; die formelle Version ist wie geht es Ihnen?": "contrasta la version informal con la formal",
    "Du oder Sie beim Thema Umwelt": "subtitulo de la ficha du/Sie",
    "Du oder Sie beim Thema Gesundheit": "subtitulo de la ficha du/Sie",
    "Toi et un ami, vous voulez cuisiner ensemble le week-end. Écris la conversation.": "'vous' con sujeto plural (toi et un ami), no es cortesia",
    "Toi et un ami, vous voulez faire quelque chose ensemble ce week-end. Écris la conversation.": "'vous' con sujeto plural (toi et un ami), no es cortesia",
    "Konzentrier dich ausschliesslich auf die Wahl zwischen du und Sie. Prüf die Grossschreibung von Sie und die Konsistenz im Text. Schreib den Text nicht neu.": "DE-V08: el prompt habla justamente de la eleccion du/Sie",
    "Schreib zwei kurze Nachrichten: eine an deine Nachbarin (Sie) und eine an einen Freund (du). Es geht um den Müll im Haus.": "DE-V08: '(Sie)' y '(du)' etiquetan el registro pedido en cada mensaje",
    "Eine Kollegin ist überfordert. Schreib ihr eine Nachricht (du) und eine E-Mail an den Chef (Sie).": "'(Sie)' y '(du)' etiquetan el registro pedido en cada mensaje",
    "Auf B1 wird die Wahl zwischen du und Sie sicher getroffen: Sie bei Fremden und in formellen Situationen, du bei Freunden und Familie. Feinere Register-Unterschiede kommen erst später — hier zählt vor allem, die Grundregel richtig anzuwenden.": "DE-V08: describe el contraste du/Sie (variante de la descripcion)",
    "Il genere e il numero del pronome relativo dipendono dall’antecedente; il caso dipende dalla sua funzione nella frase relativa.": "'la sua funzione' = del pronombre relativo, no del lector",
    "Ho descritto un luogo con la sua atmosfera e non solo con dei fatti?": "'la sua atmosfera' = del lugar",
    "Ho usato l’espressione per la sua funzione corretta e non semplicemente come un saluto qualsiasi?": "'la sua funzione' = de la expresion",
    "Concentrati solo sui «phrasal verbs» quotidiani. Controlla la preposizione usata in ciascuno e suggerisci un «phrasal verb» quando al suo posto è stato usato un verbo formale di una sola parola. Non riscrivere il testo.": "'al suo posto' = en lugar del verbo formal",
    "Scrivi un messaggio a un amico: il suo cellulare non funziona.": "'il suo cellulare' = del amigo",
    "Chiedi a qualcuno *What do you think?* e scrivi la sua risposta.": "'la sua risposta' = de la otra persona",
    "Tua nonna ha un nuovo smartphone ed è preoccupata per i suoi dati. Scrivile un breve messaggio con dei consigli.": "'i suoi dati' = de la abuela",
    "Un amico ha litigato con la sua compagna. Dagli un consiglio.": "'la sua compagna' = la pareja del amigo",
    "Un amico si trasferisce dalla Germania a Zurigo. Spiegagli, con un elenco di consigli, che cosa deve dire al suo operatore di telefonia mobile.": "'al suo operatore' = del amigo",
    "Auf A2 verwendest du feste Wendungen fürs Einkaufen, den Kauf von Fahrkarten und das Vereinbaren von Terminen. Es geht darum, diese alltäglichen Situationen zu bewältigen, nicht um freie Sprachproduktion. Sie gehören zu den häufigsten realen Situationen auf diesem Niveau.": "'Sie' de tercera persona del plural (las expresiones), no cortesia",
    "Auf A2 lernst du die grundlegendsten alltäglichen «Phrasal Verbs»: «get up, sit down, come in». Sie werden ständig in alltäglichen Anweisungen und Gesprächen verwendet. Vielfältigere «Phrasal Verbs» mit ihren Präpositionen kommen auf höheren Niveaus.": "'Sie' de tercera persona del plural (los phrasal verbs / los conectores / las expresiones), no cortesia",
    "Sie werden ständig zu Hause, in der Schule und bei der Arbeit verwendet.": "'Sie' de tercera persona del plural (los phrasal verbs / los conectores / las expresiones), no cortesia",
    "Auf B1-Niveau lernst du die häufigsten alltäglichen «phrasal verbs»: «get up, look for, find out». Sie werden im gesprochenen Englisch ständig verwendet; wer sie zugunsten formeller Einwortverben vermeidet, klingt im Alltag steif und unnatürlich.": "'Sie' de tercera persona del plural (los phrasal verbs / los conectores / las expresiones), no cortesia",
    "Auf B1-Niveau verknüpfst du Ideen mit einfachen Konnektoren: «and, but, so, because». Sie sind die grundlegenden Bausteine, aus denen später auf B2- und C1-Niveau ein breiteres Repertoire entsteht, bei dem Register und Nuancen wichtiger werden.": "'Sie' de tercera persona del plural (los phrasal verbs / los conectores / las expresiones), no cortesia",
    "Scrivi un breve dialogo: ordini del cibo. Con *order*.": "'ordini' es presente de 2a persona dentro de la escena (vos pedis comida), no imperativo de cortesia: la frase ya trata de tu"
}

EXENTAS_R10 = {
    "Im Schweizer Hochdeutsch schreibt man kein ß, immer ss.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Swiss Standard German does not use ß; it always uses ss.": "la eszett es el objeto de la explicacion (DE-V08)",
    "En el alemán estándar suizo no se escribe «ß», siempre se usa «ss».": "la eszett es el objeto de la explicacion (DE-V08)",
    "En suisse standard, on n’écrit pas « ß » ; on utilise toujours « ss ».": "la eszett es el objeto de la explicacion (DE-V08)",
    "Nel tedesco standard svizzero non si usa «ß»: si scrive sempre «ss».": "la eszett es el objeto de la explicacion (DE-V08)",
    "No alemão-padrão suíço não se usa «ß»; usa-se sempre «ss».": "la eszett es el objeto de la explicacion (DE-V08)",
    "Ein ß in einem Schweizer Text verwenden.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Using ß in a Swiss text.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Usar «ß» en un texto suizo.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Employer « ß » dans un texte suisse.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Usare «ß» in un testo svizzero.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Usar «ß» em um texto suíço.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Kein ß im Text?": "la eszett es el objeto de la explicacion (DE-V08)",
    "No ß in the text?": "la eszett es el objeto de la explicacion (DE-V08)",
    "¿No hay ningún «ß» en el texto?": "la eszett es el objeto de la explicacion (DE-V08)",
    "Pas de « ß » dans le texte ?": "la eszett es el objeto de la explicacion (DE-V08)",
    "Nessun «ß» nel testo?": "la eszett es el objeto de la explicacion (DE-V08)",
    "Nenhum «ß» no texto?": "la eszett es el objeto de la explicacion (DE-V08)",
    "Konzentrier dich ausschliesslich auf die Helvetismen. Prüf die ss/ß-Schreibung und ob die Wörter korrekt als Standard eingesetzt sind. Schreib den Text nicht neu.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Focus exclusively on the Helvetisms. Check the ss/ß spelling and whether the words are correctly used as standard language. Do not rewrite the text.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Concéntrate exclusivamente en los helvetismos. Revisa la escritura «ss/ß» y si las palabras se usan correctamente como lengua estándar. No reescribas el texto.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Concentre-toi exclusivement sur les helvétismes. Vérifie l’orthographe « ss/ß » et que les mots sont correctement employés comme langue standard. Ne réécris pas le texte.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Concentrati esclusivamente sugli elvetismi. Controlla la grafia «ss/ß» e che le parole siano usate correttamente come lingua standard. Non riscrivere il testo.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Concentre-se exclusivamente nos helvetismos. Verifique a grafia «ss/ß» e se as palavras são usadas corretamente como língua padrão. Não reescreva o texto.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Straße, groß, heißen": "columna alemana del cuadro, frente a la suiza: muestra la grafia con eszett",
    "**kein ß in CH**": "la eszett es el objeto de la explicacion (DE-V08)",
    "Das **ß existiert im Schweizer Standard nicht**. Immer *ss*. Das ist Norm, nicht Nachlässigkeit.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Ein ß in einem Schweizer Dokument.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Kein einziges ß im Text?": "la eszett es el objeto de la explicacion (DE-V08)",
    "Konzentrieren Sie sich ausschliesslich auf die Schweizer Standardvarietät. (1) Markieren Sie jeden Helvetismus und sagen Sie, ob er Standard oder Mundart ist. (2) Prüfen Sie die ss/ß-Schreibung. (3) Markieren Sie bundesdeutsche Formen, die in einem Schweizer Protokoll auffallen würden. (4) Ist das Register für ein Protokoll formell genug? Schreiben Sie den Text nicht neu.": "la eszett es el objeto de la explicacion (DE-V08)",
    "kein ß, immer ss": "la eszett es el objeto de la explicacion (DE-V08)",
    "ß in Standardtexten": "la eszett es el objeto de la explicacion (DE-V08)",
    "Das fehlende ß ist in der Schweiz Norm, nicht Nachlässigkeit, und gilt für alle Textsorten, auch sehr formelle.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Italienischsprachige aus dem Tessin kennen viele Schweizer Verwaltungsbegriffe bereits aus dem Schweizer Italienisch (trattanda – Traktandum). Für alle Italienischsprachigen gilt: Die Deutschschweizer Schreibkonventionen (ss statt ß, feste Formeln in Protokollen und Behördenbriefen) sind ein eigenes Register neben dem Wortschatz.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Gli italofoni del Ticino conoscono già molti termini amministrativi svizzeri dallo svizzero italiano, per esempio «trattanda» – «Traktandum». Per tutti gli italofoni vale però che le convenzioni scritte della Svizzera tedesca («ss» invece di «ß», formule fisse nei verbali e nelle lettere ufficiali) costituiscono un registro specifico oltre al lessico.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Französischsprachige aus der Romandie kennen die Schweizer Institutionen und ihre Begriffe bereits («votation» – Abstimmung, «Conseil fédéral» – Bundesrat). Neu sind die Deutschschweizer Schreibkonventionen (ss statt ß, feste Formeln in Protokollen) und Begriffe wie Traktandum oder Pendenz.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Les francophones de Suisse romande connaissent déjà les institutions suisses et leur terminologie («votation» – «Abstimmung», «Conseil fédéral» – «Bundesrat»). Ce qui est nouveau, ce sont les conventions écrites de Suisse alémanique («ss» au lieu de «ß», formules fixes dans les procès-verbaux) et des termes comme «Traktandum» ou «Pendenz».": "la eszett es el objeto de la explicacion (DE-V08)",
    "Portugiesischsprachige kennen durch das europäische und das brasilianische Portugiesisch zwei Standardvarietäten mit eigenem Wortschatz, das Prinzip einer nationalen Varietät ist also vertraut. Neu sind die konkreten Deutschschweizer Konventionen (ss statt ß, Traktandum, Pendenz, feste Formeln in Behördenbriefen).": "la eszett es el objeto de la explicacion (DE-V08)",
    "Falantes de português conhecem, pelo português europeu e brasileiro, duas variedades-padrão com vocabulário próprio; portanto, o princípio de uma variedade nacional é familiar. A novidade são as convenções concretas do alemão suíço («ss» em vez de «ß», «Traktandum», «Pendenz», fórmulas fixas em cartas administrativas).": "la eszett es el objeto de la explicacion (DE-V08)",
    "Englischsprachige kennen nationale Standardvarietäten (britisches und amerikanisches Englisch), die Idee eines eigenen Schweizer Standards ist also vertraut. Neu sind die konkreten Deutschschweizer Konventionen (ss statt ß, Traktandum, Pendenz, feste Formeln in Protokollen und Behördenbriefen).": "la eszett es el objeto de la explicacion (DE-V08)",
    "English speakers are familiar with national standard varieties (British and American English), so the idea of a distinct Swiss standard is familiar. What is new are the specific Swiss German conventions: «ss» instead of «ß», words such as «Traktandum» and «Pendenz», and fixed formulas in minutes and official correspondence.": "la eszett es el objeto de la explicacion (DE-V08)",
    "Kein ß im gesamten Text?": "la eszett es el objeto de la explicacion (DE-V08)"
}

R10 = re.compile("\u00df")


def _enmascarar_r9(t):
    """Tapa « » y *…*: ahi dentro va el idioma que se aprende."""
    t = re.sub("\u00ab[^\u00bb]*\u00bb", lambda m: " " * len(m.group(0)), t)
    return re.sub(r"\*\*?[^*]+\*\*?", lambda m: " " * len(m.group(0)), t)


def _es_instruccion(ruta):
    if any(isinstance(k, str) and k in PROHIBIDAS_R9 for k in ruta):
        return False
    return any(isinstance(k, str) and k in CAMPOS_R9 for k in ruta)


def marca_r9(idi, t):
    """Formas de cortesia halladas en un texto de instruccion."""
    s = _enmascarar_r9(t)
    out = []
    if idi == "de":
        out = [m.group(0) for m in R9_DE.finditer(s)]
    elif idi == "fr":
        for m in R9_FR.finditer(s):
            g = m.group(0)
            low = g.lower()
            if low in FR_NO_VERBO:
                continue
            # 'rendez-vous' es un sustantivo, no imperativo + pronombre
            if low == "rendez" and s[m.end():m.end() + 5].startswith("-vous"):
                continue
            if low == "vous" and s[max(0, m.start() - 7):m.start()].endswith("rendez-"):
                continue
            out.append(g)
    elif idi == "it":
        for rx in (R9_IT_PRON, R9_IT_UNAMB, R9_IT_AMB, R9_IT_REFL):
            out += [m.group(0).strip() for m in rx.finditer(s)]
    return out


def revisar_r9_r10(d, nom, errores):
    for ruta, t in _textos_con_ruta(d):
        idi = ruta[-1] if ruta and isinstance(ruta[-1], str) else None
        if idi in ("de", "fr", "it") and _es_instruccion(ruta) and t not in EXENTAS_R9:
            m = marca_r9(idi, t)
            if m:
                errores.append(f"R9 {nom}: trato formal '{m[0]}' en {'/'.join(str(x) for x in ruta)}: {t[:60]}")
        if R10.search(t) and t not in EXENTAS_R10:
            errores.append(f"R10 {nom}: eszett en {'/'.join(str(x) for x in ruta)}: {t[:60]}")


def textos_es(o, ruta=""):
    """Cadenas que son espanol: su camino TERMINA en la clave 'es'.

    En A2/B1 el valor de una clave 'es' puede ser el objeto bilingue
    {idioma_aprendido: original, es: traduccion}: ahi solo la clave interna
    'es' es espanol, y el original en aleman o ingles no debe revisarse.
    """
    if isinstance(o, dict):
        for k, v in o.items():
            if k == "es" and isinstance(v, str):
                yield v
            else:
                yield from textos_es(v, ruta + "/" + str(k))
    elif isinstance(o, list):
        for x in o:
            yield from textos_es(x, ruta)


# --------------------------------------------------------------------------
# Autotest de R9 y R10: casos que DEBEN marcarse y casos que NO.
# Se corre con --autotest y no toca el contenido.
# --------------------------------------------------------------------------
CASOS_R9 = [
    # (idioma, texto, debe_marcar)
    ("de", "Schreiben Sie fünf Sätze über Ihre Ferien.", True),
    ("de", "Erzählen Sie, wie es Ihnen gefallen hat.", True),
    ("de", "Schreib fünf Sätze über deine Ferien.", False),
    ("de", "Erzähl, wie es dir gefallen hat.", False),
    ("de", "Die Konnektoren sind einfach; «and, but» kommen zuerst.", False),
    ("fr", "Écrivez cinq phrases sur vos vacances.", True),
    ("fr", "Concentrez-vous sur l'ordre des mots.", True),
    ("fr", "Écris cinq phrases sur tes vacances.", False),
    ("fr", "Concentre-toi sur l'ordre des mots.", False),
    ("fr", "parle pendant 90 secondes d'un rendez-vous chez le médecin", False),
    ("fr", "Il y en a assez pour deux.", False),
    ("it", "Scriva cinque frasi sulle sue vacanze.", True),
    ("it", "Si concentri sull'ordine delle parole.", True),
    ("it", "Non riscriva il testo.", True),
    ("it", "Gli dia un consiglio.", True),
    ("it", "Scrivi cinque frasi sulle tue vacanze.", False),
    ("it", "Concentrati sull'ordine delle parole.", False),
    ("it", "Non riscrivere il testo.", False),
    ("it", "A livello A2 si esprime accordo con «das stimmt». Si tratta di una formula.", False),
    ("it", "Verifica che la formula sia chiara e cortese.", False),
    ("it", "60 secondi: una *Anwendung* che non usi più.", False),
    # ambiguedad conocida: 'ordini' en posicion de imperativo parece cortesia
    # pero aqui es presente de 2a persona dentro de la escena; se resuelve
    # con la excepcion explicita, no con la regla.
    ("it", "Scrivi un breve dialogo: ordini del cibo. Con *order*.", False),
]
CASOS_R10 = [
    ("Das Haus wurde vor dreißig Jahren gebaut.", True),
    ("Das Haus wurde vor dreissig Jahren gebaut.", False),
    ("Im Schweizer Hochdeutsch schreibt man kein ß, immer ss.", False),  # exenta
]


def autotest():
    fallos = []
    for idi, t, debe in CASOS_R9:
        hay = bool(marca_r9(idi, t)) and t not in EXENTAS_R9
        if hay != debe:
            fallos.append(f"R9 {idi}: esperaba {debe}, dio {hay}: {t[:60]}")
    for t, debe in CASOS_R10:
        hay = bool(R10.search(t)) and t not in EXENTAS_R10
        if hay != debe:
            fallos.append(f"R10: esperaba {debe}, dio {hay}: {t[:60]}")
    # ninguna excepcion puede estar de mas: todas tienen que disparar la regla
    for t in EXENTAS_R9:
        if not any(marca_r9(i, t) for i in ("de", "fr", "it")):
            fallos.append(f"EXENTAS_R9 sobrante (la regla ya no la marca): {t[:60]}")
    for t in EXENTAS_R10:
        if not R10.search(t):
            fallos.append(f"EXENTAS_R10 sobrante (no lleva eszett): {t[:60]}")
    print(f"autotest R9/R10: {len(CASOS_R9) + len(CASOS_R10)} casos, "
          f"{len(EXENTAS_R9)} + {len(EXENTAS_R10)} excepciones, {len(fallos)} fallos")
    for f in fallos:
        print(" -", f)
    return 1 if fallos else 0


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--build", help="salida de componer.py")
    ap.add_argument("--autotest", action="store_true",
                    help="prueba R9/R10 contra casos fijos y sale")
    a = ap.parse_args()
    if a.autotest:
        sys.exit(autotest())
    if not a.build:
        ap.error("hace falta --build (o --autotest)")
    errores = []

    # R1
    n_a2b1 = 0
    for f in sorted(glob.glob(os.path.join(a.build, "*.json"))):
        d = cargar(f)
        if "skillId" not in d or d.get("nivel") not in ("A2", "B1"):
            continue
        n_a2b1 += 1
        nom = os.path.basename(f)
        ap_ = d["idioma"]
        for k in CAMPOS_BILINGUES:
            v = d.get(k)
            if v is None:
                continue
            for it in (v if isinstance(v, list) else [v]):
                if not isinstance(it, dict):
                    errores.append(f"R1 {nom}: {k} no es bilingue (string plano)")
                    break
        for clave, lista in (d.get("erroresContrastivos") or {}).items():
            for i, it in enumerate(lista):
                if not isinstance(it, dict) or ap_ not in it or clave not in it:
                    errores.append(f"R1 {nom}: erroresContrastivos.{clave}[{i}] debe ser {{'{ap_}': original, '{clave}': traduccion}}")
                elif it[clave].strip() == it[ap_].strip():
                    errores.append(f"R1 {nom}: erroresContrastivos.{clave}[{i}] 'traducido' es identico al original")

    # R2
    banco = cargar(os.path.join(RAIZ, "data", "banco.json"))
    for t in banco["topics"]:
        nombres = banco.get("topicNombresI18n", {}).get(t, {})
        for l in LANGS:
            if not nombres.get(l, "").strip():
                errores.append(f"R2 topic {t}: falta nombre en '{l}'")
    cat = cargar(os.path.join(RAIZ, "data", "categorias-uso.json"))
    todas = cat["funcionComunicativa"] + cat["patronGramatical"]
    for c in todas:
        for l in LANGS:
            if not cat.get("nombres", {}).get(c, {}).get(l, "").strip():
                errores.append(f"R2 categoria '{c}': falta nombre en '{l}'")

    # R3
    pl = os.path.join(RAIZ, "contenido", "plantillas", "planilla-profesor.json")
    if os.path.exists(pl):
        tx = cargar(pl)["textos"]
        base = set(tx.get("es", {}))
        for l in LANGS:
            if set(tx.get(l, {})) != base:
                errores.append(f"R3 planilla-profesor.json: claves de '{l}' distintas de 'es'")

    # R4 y R5
    usadas = set()
    for f in sorted(glob.glob(os.path.join(a.build, "*.json"))):
        d = cargar(f)
        nom = os.path.basename(f)
        for s in recorrer_textos(d):
            if s.count("«") != s.count("»"):
                errores.append(f"R4 {nom}: « » desbalanceados en: {s[:60]}")
                break
        for t in textos_es(d):
            m = VOSEO.search(t)
            if m:
                errores.append(f"R6 {nom}: voseo '{m.group(0)}' en: {t[:70]}")
            if t in EXENTAS_R7:
                continue
            m = USTED_IMP.search(t)
            if m:
                errores.append(f"R7 {nom}: imperativo de usted '{m.group(2)}' en: {t[:70]}")
            m = LEXICO_PENINSULAR.search(t)
            if m:
                errores.append(f"R7 {nom}: lexico peninsular '{m.group(0)}' en: {t[:70]}")
        revisar_r8(d, nom, errores)
        revisar_r9_r10(d, nom, errores)
        for r in d.get("redemittel", []) if isinstance(d, dict) else []:
            for c in r.get("categoriasUso") or []:
                usadas.add(c)
    for c in sorted(usadas - set(todas)):
        errores.append(f"R5 categoriasUso fuera de la lista cerrada: {c}")

    print(f"{n_a2b1} fichas A2/B1 revisadas, {len(errores)} problemas")
    por_regla = {}
    for e in errores:
        por_regla[e.split()[0]] = por_regla.get(e.split()[0], 0) + 1
    for r in sorted(por_regla):
        print(f" {r}: {por_regla[r]}")
    for e in errores[:40]:
        print(" -", e)
    if len(errores) > 40:
        print(f" ... y {len(errores) - 40} mas")
    sys.exit(1 if errores else 0)


if __name__ == "__main__":
    main()
