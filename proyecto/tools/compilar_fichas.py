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

def secciones(cuerpo, et):
    """Devuelve {clave: bloque de texto} localizando cada etiqueta en negrita."""
    marcas = []
    for clave, etiqueta in et.items():
        m = re.search(r"\*\*" + re.escape(etiqueta) + r"[^*]*\*\*", cuerpo)
        if m:
            marcas.append((m.start(), m.end(), clave))
    marcas.sort()
    out = {}
    for i, (ini, fin, clave) in enumerate(marcas):
        hasta = marcas[i + 1][0] if i + 1 < len(marcas) else len(cuerpo)
        out[clave] = cuerpo[fin:hasta].strip()
        out[clave + "__titulo"] = cuerpo[ini:fin].strip("* ")
    return out

def vinetas(bloque):
    return [re.sub(r"\s+", " ", l.strip("-* ").strip())
            for l in bloque.splitlines() if l.strip().startswith("-")]

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

def limpiar(t):
    return re.sub(r"\s+", " ", t.replace("**", "").replace("★", "").strip())

def semana_especial(semana, clase, cuerpo, idioma):
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
        "id": f"{'REVIEW' if 'Review' in clase else 'SURVIVAL'}-{idioma.upper()}-S{semana}",
        "semana": semana, "idioma": idioma,
        "clase": "review" if "Review" in clase else "survival",
        "titulo": clase,
        "minutosEstimados": max((int(x) for x in mtime), default=60),
        "consigna": limpiar(bloque_m.split("\n")[0]) if bloque_m else "",
        "requisitos": [limpiar(x) for x in vinetas(bloque_m)],
        "microtareas": [limpiar(x) for x in vinetas(s.get("microtareas", ""))],
        "autochequeo": [limpiar(x) for x in numeradas(s.get("autochequeo", ""))][:5],
        "promptCorreccion": limpiar(re.sub(r"^>\s*", "", s.get("prompt", ""), flags=re.M)),
    }

def compilar(bloque, idioma):
    et = ETIQUETAS[idioma]
    cab, cuerpo = bloque.split("\n", 1)

    m = re.match(r"(?:Week|Woche) (\d+).*?·\s*((?:EN|DE)-[A-Z]\d\d)\s*·\s*(.+)", cab)
    if not m:
        esp = re.match(r"(?:Week|Woche) (\d+).*?·\s*(Skill Review Week|Survival Week)", cab)
        if esp:
            return semana_especial(int(esp.group(1)), esp.group(2), cuerpo, idioma), None
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

    nivel = "C1" if idioma == "en" else "B2"
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
        "descripcion": limpiar(s["descripcion"])[:1590],
        "ejemplos": [], "notas": [], "errores": [], "vocabulario": [],
        "redemittel": [], "mision": {}, "microtareas": [], "autochequeo": [],
        "promptCorreccion": "",
    }

    if "cuadro" in s:
        c, f = tabla(s["cuadro"])
        if c and f:
            ficha["cuadroReferencia"] = {
                "titulo": s.get("cuadro__titulo", "").split("—")[-1].strip(),
                "columnas": c[:5], "filas": [x[:5] for x in f[:20]]}

    for l in vinetas(s.get("ejemplos", "")):
        ficha["ejemplos"].append({"texto": limpiar(l)})
    ficha["notas"] = [limpiar(x) for x in vinetas(s.get("notas", ""))]
    ficha["errores"] = [limpiar(x) for x in vinetas(s.get("errores", ""))]

    if "contraste" in s:
        ficha["contraste"] = {"es": limpiar(s["contraste"])}

    c, f = tabla(s.get("vocabulario", ""))
    if c:
        col = {n.upper(): i for i, n in enumerate(c)}
        for fila in f:
            item = fila[0]
            v = {"item": limpiar(item),
                 "prioridad": "nucleo" if "★" in item else "ampliacion",
                 "traducciones": {"es": fila[col["ES"]] if "ES" in col and
                                  len(fila) > col["ES"] else ""}}
            if idioma == "de":
                v["reccion"] = fila[1] if len(fila) > 1 else "—"
            if len(fila) > 2 and fila[2]:
                v["nota"] = fila[2]
            if "CH" in " ".join(fila):
                v["variante"] = "CH"
            ficha["vocabulario"].append(v)

    c, f = tabla(s.get("redemittel", ""))
    if c and len(c) >= 3:
        for fila in f:
            tr = fila[2].strip() if len(fila) > 2 else ""
            ficha["redemittel"].append({
                "expresion": limpiar(fila[0]), "funcion": limpiar(fila[1]),
                "traducciones": {"es": None if tr in ("—", "-", "") else tr}})
    else:  # formato en linea: "*X* · *Y* · *Z*"
        for e in re.findall(r"\*([^*]{4,60})\*", s.get("redemittel", "")):
            ficha["redemittel"].append({"expresion": e.strip(),
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
    ficha["mision"] = {"consigna": limpiar(consigna), "requisitos": requisitos[:8]}

    for l in vinetas(s.get("microtareas", "")):
        md = re.match(r"\*?(\w+)\*?\s*\((\d+)\s*(?:min|Min)[^)]*\)\.?\*?\s*(.+)", l)
        if md:
            ficha["microtareas"].append({
                "dia": DIAS.get(md.group(1), "lun"),
                "minutos": int(md.group(2)), "texto": limpiar(md.group(3))})
    ficha["autochequeo"] = [limpiar(x) for x in numeradas(s.get("autochequeo", ""))][:5]

    pr = s.get("prompt", "")
    ficha["promptCorreccion"] = limpiar(re.sub(r"^>\s*", "", pr, flags=re.M))

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
        val = jsonschema.Draft202012Validator(json.loads(Path(a.schema).read_text()))

    ok = fallos = 0
    for f in a.fichas:
        texto = Path(f).read_text()
        idioma = "de" if re.search(r"Deutsch|Woche", texto[:400]) else "en"
        for bloque in trocear(texto):
            ficha, err = compilar(bloque, idioma)
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
                    json.dumps(ficha, ensure_ascii=False, indent=1))
                marca = "~~" if especial else "ok"
                print(f"  {marca} {ficha['id']:18s} {ficha['titulo'][:42]}")
                ok += 1
    print(f"\n{ok} fichas compiladas, {fallos} con problemas")
    sys.exit(1 if fallos else 0)

if __name__ == "__main__":
    main()
