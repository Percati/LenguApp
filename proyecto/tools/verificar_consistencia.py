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


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--build", required=True, help="salida de componer.py")
    a = ap.parse_args()
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
