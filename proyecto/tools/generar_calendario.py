#!/usr/bin/env python3
"""
generar_calendario.py — deriva el calendario anual a partir de las reglas.

El calendario NO se escribe a mano: se calcula. Misma semilla (el anio ISO)
produce siempre el mismo resultado, asi que un anio pasado se puede reproducir
exactamente sin guardarlo.

Uso:
    python3 generar_calendario.py --anio 2027 --idioma de --nivel B2 \
        --banco ../data/banco.json
    python3 generar_calendario.py --anio 2026 --desde 37 --formato md
"""
import argparse, json, random, sys
from datetime import date
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


REVIEW   = {10, 22, 34, 46}
# El Survival ya NO consume semanas de dias de semana. El desafio vive en el fin
# de semana (sabado 00:00 a domingo 23:59, hora local) de CADA semana, y cuatro
# veces al año ese desafio es el Survival largo en vez del semanal. Por eso
# SURVIVAL_FINDE no se resta de las semanas disponibles: es una etiqueta sobre
# el fin de semana de una semana que igual tiene contenido de lunes a viernes.
# Resultado: 52 - 4 repasos = 48 semanas de contenido, contra las 43 del esquema
# anterior (6 repasos + 3 semanas Survival enteras).
def survival_finde(total):
    """Cuatro por año, al cierre de cada trimestre, desfasadas de los repasos."""
    return {13, 26, 39, total}

SEP_MIN  = 10          # semanas minimas entre apariciones de un mismo skill
SEP_MAX  = 30          # semanas maximas: mas separacion no es repeticion espaciada
                       # sino dos clases sueltas. Con 48 semanas y dos
                       # apariciones, el ideal ronda las 24.
FUNDACIONAL_MAX = 20   # un skill fundacional no puede debutar despues de aqui
TOPE_APARICIONES = 4   # mas de cuatro veces en un año es machaque

def semanas_iso(anio: int) -> int:
    """52 o 53 segun el anio ISO. 2026 tiene 53."""
    return date(anio, 12, 28).isocalendar()[1]

def rango_fechas(anio, semana):
    lun = date.fromisocalendar(anio, semana, 1)
    dom = date.fromisocalendar(anio, semana, 7)
    return lun, dom

def repartir(skills, libres, nivel):
    """Decide cuantas veces aparece cada skill para llenar exactamente el anio.

    Regla vigente: **todo skill del par (idioma, nivel) aparece al menos una
    vez**, y solo los marcados como nucleo de ESE nivel repiten.

    Antes la regla era el minimo de dos apariciones para todos, lo que obligaba
    a descartar skills: con 43 semanas cabian 21, y aleman C1 tiene 41, asi que
    20 skills no aparecian nunca en el anio. Cubrir el banco entero una vez y
    machacar solo lo importante cubre mas y explica mejor por que se repite lo
    que se repite.

    Un skill es nucleo de un nivel si ese nivel figura en su campo `repiteEn`.
    La importancia depende del nivel, no del skill: un tema puede ser central en
    B1 y periferico en C1, asi que `fundacional` (booleano por skill) no servia
    para esto y se mantiene solo para su otro uso, el de no debutar tarde.

    Los slots que sobran despues de dar la segunda aparicion a los nucleo se
    reparten entre ellos primero y despues entre el resto, con tope de
    TOPE_APARICIONES. En los niveles bajos, donde hay muchas menos skills que
    semanas, esto hace que todo el banco aparezca tres o cuatro veces, que es
    exactamente lo que se quiere en A2.
    """
    n = len(skills)
    if n == 0:
        return {}
    if n > libres:
        # Ni una vez cada uno. No se puede resolver repartiendo: el banco de ese
        # par es mas grande que el anio y hay que achicarlo o partirlo en dos
        # ediciones. Fallar aca es mejor que descartar skills en silencio.
        raise ValueError(
            f"{nivel}: {n} skills no entran en {libres} semanas ni una vez cada uno. "
            f"Achicar el banco de este par o repartirlo en dos anios.")

    plan = {s["id"]: 1 for s in skills}
    nucleo = [s for s in skills if nivel in s.get("repiteEn", [])]
    resto  = [s for s in skills if nivel not in s.get("repiteEn", [])]

    sobran = libres - n
    # primero la segunda aparicion de cada nucleo, que es la razon de marcarlos
    for s in nucleo:
        if sobran <= 0:
            break
        plan[s["id"]] += 1
        sobran -= 1
    # despues, si todavia sobran semanas, se reparten por rondas
    prioridad = nucleo + resto
    i = 0
    while sobran > 0 and prioridad:
        sid = prioridad[i % len(prioridad)]["id"]
        if plan[sid] < TOPE_APARICIONES:
            plan[sid] += 1
            sobran -= 1
        i += 1
        if i > TOPE_APARICIONES * len(prioridad):
            break
    return plan

def generar(anio, idioma, nivel, banco, topics, desde=1, intentos=400, fijas=None):
    """Reparte skills y temas en el anio.

    El algoritmo es constructivo, no un greedy que rellena semana a semana.
    Aquel fallaba: al llegar a las ultimas semanas solo le quedaban skills cuya
    ventana de repeticion ya habia vencido, y se quedaba sin solucion.

    La construccion es directa. Si un skill aparece k veces y hay L semanas
    libres, sus apariciones van cerca de las posiciones L*j/k con un desfase
    propio. Eso reparte cada skill a lo largo del anio por diseno y hace que
    los intervalos caigan solos dentro de [SEP_MIN, SEP_MAX], sin buscar.

    Los intervalos NO son uniformes entre skills: cada uno arranca en un
    desfase distinto, asi que uno puede caer en las semanas 1, 25 y 34 y otro
    en la 2, 20 y 40. La regularidad seria un efecto no deseado.

    Despues se asignan los temas, que es donde si hace falta buscar.
    """
    total = semanas_iso(anio)
    finde_largo = survival_finde(total)
    libres = [w for w in range(desde, total + 1) if w not in REVIEW]
    skills = [s for s in banco if s["idioma"] == idioma and nivel in s["niveles"]]
    if not skills:
        print(f"El banco no tiene skills de {idioma} en {nivel}.", file=sys.stderr)
        sys.exit(2)

    fijas = fijas or {}
    fijadas = {int(w): (tuple(v["tarea"]), v["topic"]) for w, v in fijas.items()}
    rnd = random.Random(anio)

    plan_ap = repartir(skills, len(libres) - len(fijadas), nivel)
    skills = [s for s in skills if s["id"] in plan_ap]
    idx = {s["id"]: s for s in skills}

    for intento in range(intentos):
        orden = list(skills); rnd.shuffle(orden)
        libres_disp = [w for w in libres if w not in fijadas]
        L = len(libres_disp)
        ranura = {}                      # semana -> (skillId, order)
        ok = True

        for i, sk in enumerate(orden):
            k = plan_ap[sk["id"]]
            base = (i * L // max(len(orden), 1))
            for j in range(k):
                objetivo = (base + j * L // k) % L
                # primera ranura libre a partir del objetivo, dando la vuelta
                for d in range(L):
                    pos = (objetivo + d) % L
                    w = libres_disp[pos]
                    if w in ranura:
                        continue
                    previas = [x for x, (s2, _) in ranura.items() if s2 == sk["id"]]
                    if any(abs(x - w) < SEP_MIN for x in previas):
                        continue
                    if previas and abs(w - min(previas, key=lambda y: abs(y - w))) > SEP_MAX:
                        continue
                    if idx[sk["id"]].get("fundacional") and not previas \
                       and w > FUNDACIONAL_MAX:
                        continue
                    ranura[w] = (sk["id"], j + 1)
                    break
                else:
                    ok = False; break
            if not ok:
                break
        if not ok or len(ranura) != L:
            continue

        # --- order: renumerar por semana ---
        # La colocacion de arriba calcula la posicion objetivo con "% L", asi que
        # un skill cuya base cae al final del anio da la vuelta y su segunda
        # aparicion aterriza ANTES que la primera. El order quedaba sellado como
        # j+1, el indice del bucle, no la posicion real: aparecia order 2 en la
        # semana 6 y order 1 en la 33. Como el order es lo que elige el subtitulo
        # y la profundidad creciente de cada reaparicion, eso corria la escalera
        # de repeticion espaciada al reves.
        # Renumerar no mueve ninguna semana, solo reetiqueta, asi que no toca las
        # separaciones ya validadas. Los skills con una aparicion fijada a mano en
        # semanas-fijas.json se dejan intactos: ahi el order es una decision del
        # autor, no un calculo.
        fijados_sid = {tarea[0] for tarea, _ in fijadas.values()}
        por_skill = {}
        for w, (sid, _) in ranura.items():
            por_skill.setdefault(sid, []).append(w)
        for sid, semanas in por_skill.items():
            if sid in fijados_sid:
                continue
            for nuevo, w in enumerate(sorted(semanas), start=1):
                ranura[w] = (sid, nuevo)

        # --- temas: ahora si, busqueda ---
        plan = dict(fijadas)
        usados_topic = {}
        for w, (sid, order) in plan.items() if False else []:
            pass
        for w, (tarea, topic) in fijadas.items():
            usados_topic.setdefault(tarea[0], set()).add(topic)
        fallo = False
        for w in sorted(ranura):
            sid, order = ranura[w]
            bloq = set(idx[sid].get("topicBlocklist", []))
            cands = [t for t in topics
                     if t not in usados_topic.get(sid, set()) and t not in bloq
                     and plan.get(w - 1, (None, None))[1] != t
                     and plan.get(w + 1, (None, None))[1] != t]
            if not cands:
                fallo = True; break
            rnd.shuffle(cands)
            topic = cands[0]
            plan[w] = ((sid, order), topic)
            usados_topic.setdefault(sid, set()).add(topic)
        if fallo:
            continue
        break
    else:
        print(f"Sin solucion tras {intentos} intentos.", file=sys.stderr)
        sys.exit(2)

    filas = []
    for w in range(desde, total + 1):
        lun, dom = rango_fechas(anio, w)
        # El desafio del fin de semana: el Survival largo cuatro veces al anio,
        # el semanal el resto. En una semana de repaso el desafio es el repaso.
        if w in REVIEW:
            desafio = "repaso"
        elif w in finde_largo:
            desafio = "survival"
        else:
            desafio = "semanal"
        if w in REVIEW:
            filas.append((w, lun, dom, "review", None, None, None, desafio))
        else:
            (sid, order), topic = plan[w]
            filas.append((w, lun, dom, "content", sid, order, topic, desafio))
    return filas


def verificar(filas):
    """Comprueba las reglas duras sobre el resultado. Nunca confiar sin esto."""
    errores = []
    prev = None
    vistos = {}
    for w, _, _, tipo, sid, order, topic, _des in filas:
        if tipo != "content":
            prev = None
            continue
        if prev and prev == topic:
            errores.append(f"S{w}: topic {topic} repetido en semanas consecutivas")
        prev = topic
        if sid:
            anteriores = [w2 for w2, _ in vistos.get(sid, [])]
            for w2, t2 in vistos.get(sid, []):
                if abs(w2 - w) < SEP_MIN:
                    errores.append(f"S{w}: {sid} a menos de {SEP_MIN} semanas de S{w2}")
            if anteriores and w - max(anteriores) > SEP_MAX:
                errores.append(f"S{w}: {sid} a mas de {SEP_MAX} semanas de S{max(anteriores)}")
            for w2, t2 in vistos.get(sid, []):
                if t2 == topic:
                    errores.append(f"S{w}: {sid} repite topic {topic} (ya en S{w2})")
            vistos.setdefault(sid, []).append((w, topic))

    # El order tiene que crecer con la semana: la aparicion n-esima de un skill
    # es la n-esima del anio, porque de ahi sale la profundidad creciente. Sin
    # esta comprobacion el wrap-around del "% L" pasaba desapercibido.
    ordenes = {}
    for w, _, _, tipo, sid, order, _, _des in filas:
        if tipo == "content" and sid:
            ordenes.setdefault(sid, []).append((w, order))
    for sid, pares in ordenes.items():
        pares.sort()
        secuencia = [o for _, o in pares]
        if secuencia != sorted(secuencia):
            errores.append(f"{sid}: order no creciente con la semana ({pares})")
        if secuencia != list(range(1, len(secuencia) + 1)):
            errores.append(f"{sid}: orders no consecutivos desde 1 ({secuencia})")
    return errores

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--anio", type=int, required=True)
    ap.add_argument("--idioma", default="de")
    ap.add_argument("--nivel", default="B2")
    ap.add_argument("--banco", default="../data/banco.json")
    ap.add_argument("--desde", type=int, default=1)
    ap.add_argument("--formato", choices=["md", "json"], default="md")
    ap.add_argument("--fijas", help="JSON con semanas fijadas a mano")
    a = ap.parse_args()

    d = json.loads(Path(a.banco).read_text(encoding="utf-8"))
    fijas = {}
    if a.fijas:
        fijas = json.loads(Path(a.fijas).read_text(encoding="utf-8")).get(f"{a.anio}-{a.idioma}-{a.nivel}", {})
    filas = generar(a.anio, a.idioma, a.nivel, d["skills"], d["topics"], a.desde,
                    fijas=fijas)

    errs = verificar(filas)
    for e in errs:
        print("REGLA VIOLADA:", e, file=sys.stderr)

    if a.formato == "json":
        print(json.dumps([{"semana": w, "inicio": str(l), "fin": str(f),
                           "tipo": t, "skillId": s, "order": o, "topicId": tp,
                           "desafioFinde": ds}
                          for w, l, f, t, s, o, tp, ds in filas],
                         ensure_ascii=False, indent=1))
    else:
        print(f"# Calendario {a.anio} · {a.idioma} · {a.nivel}\n")
        print("| Semana | Fechas | Tipo | Skill | Ap. | Topic | Finde |")
        print("|---|---|---|---|---|---|---|")
        for w, l, f, t, s, o, tp, ds in filas:
            print(f"| {w} | {l:%d %b}–{f:%d %b} | {t} | {s or '—'} "
                  f"| {o or '—'} | {tp or '—'} | {ds} |")
    sys.exit(1 if errs else 0)

if __name__ == "__main__":
    main()
