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


REVIEW   = {8, 16, 24, 32, 40, 48}
SURVIVAL = {12, 28, 44}
SEP_MIN  = 10          # semanas minimas entre apariciones de un mismo skill
SEP_MAX  = 30          # semanas maximas: mas separacion no es repeticion espaciada
                       # sino dos clases sueltas. Con 43 semanas y dos
                       # apariciones, el ideal ronda las 21.
FUNDACIONAL_MAX = 20   # un skill fundacional no puede debutar despues de aqui

def semanas_iso(anio: int) -> int:
    """52 o 53 segun el anio ISO. 2026 tiene 53."""
    return date(anio, 12, 28).isocalendar()[1]

def rango_fechas(anio, semana):
    lun = date.fromisocalendar(anio, semana, 1)
    dom = date.fromisocalendar(anio, semana, 7)
    return lun, dom

def apariciones(skill, n=None):
    """Lista de (skillId, order) segun cuantas veces aparece en el anio."""
    return [(skill["id"], o + 1) for o in range(n or skill.get("apariciones", 2))]


def repartir(skills, libres):
    """Decide cuantas veces aparece cada skill para llenar exactamente el anio.

    Todos los skills aparecen al menos dos veces: una sola aparicion no es
    repeticion espaciada, es una clase suelta. Las apariciones sobrantes van
    primero a los skills fundacionales y despues en orden de declaracion, que
    es el del banco y por tanto explicable.

    Si hay mas skills que semanas, algunos quedan fuera del anio en vez de
    aparecer una sola vez: es preferible cubrir menos temas bien que muchos
    una vez.
    """
    n = len(skills)
    if n == 0:
        return {}
    if 2 * n > libres:
        # No entran todos dos veces, asi que hay que descartar algunos. Cortar
        # por orden de declaracion es lo peor posible: el banco lista primero
        # fluidez, luego gramatica y al final lexico, de modo que el corte se
        # come el lexico entero. Aleman C1 quedaba sin una sola Redewendung.
        # Se recorta proporcionalmente dentro de cada categoria.
        cabe = libres // 2
        por_cat = {}
        for sk in skills:
            por_cat.setdefault(sk["id"][3], []).append(sk)   # F, G o V
        elegidos, resto = [], []
        for cat, lista in sorted(por_cat.items()):
            cuota = round(cabe * len(lista) / n)
            elegidos += lista[:cuota]
            resto += lista[cuota:]
        # el redondeo puede dejar hueco o pasarse
        elegidos = (elegidos + resto)[:cabe]
        skills = [sk for sk in skills if sk in elegidos]
        n = len(skills)
    plan = {s["id"]: 2 for s in skills}
    sobran = libres - 2 * n
    prioridad = ([s for s in skills if s.get("fundacional")] +
                 [s for s in skills if not s.get("fundacional")])
    i = 0
    while sobran > 0 and prioridad:
        sid = prioridad[i % len(prioridad)]["id"]
        if plan[sid] < 4:                   # tope: mas de cuatro es machaque
            plan[sid] += 1
            sobran -= 1
        i += 1
        if i > 4 * len(prioridad):
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
    libres = [w for w in range(desde, total + 1)
              if w not in REVIEW and w not in SURVIVAL]
    skills = [s for s in banco if s["idioma"] == idioma and nivel in s["niveles"]]
    if not skills:
        print(f"El banco no tiene skills de {idioma} en {nivel}.", file=sys.stderr)
        sys.exit(2)

    fijas = fijas or {}
    fijadas = {int(w): (tuple(v["tarea"]), v["topic"]) for w, v in fijas.items()}
    rnd = random.Random(anio)

    plan_ap = repartir(skills, len(libres) - len(fijadas))
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
        if w in REVIEW:
            filas.append((w, lun, dom, "review", None, None, None))
        elif w in SURVIVAL:
            filas.append((w, lun, dom, "survival", None, None, None))
        else:
            (sid, order), topic = plan[w]
            filas.append((w, lun, dom, "content", sid, order, topic))
    return filas


def verificar(filas):
    """Comprueba las reglas duras sobre el resultado. Nunca confiar sin esto."""
    errores = []
    prev = None
    vistos = {}
    for w, _, _, tipo, sid, order, topic in filas:
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
                           "tipo": t, "skillId": s, "order": o, "topicId": tp}
                          for w, l, f, t, s, o, tp in filas],
                         ensure_ascii=False, indent=1))
    else:
        print(f"# Calendario {a.anio} · {a.idioma} · {a.nivel}\n")
        print("| Semana | Fechas | Tipo | Skill | Ap. | Topic |")
        print("|---|---|---|---|---|---|")
        for w, l, f, t, s, o, tp in filas:
            print(f"| {w} | {l:%d %b}–{f:%d %b} | {t} | {s or '—'} "
                  f"| {o or '—'} | {tp or '—'} |")
    sys.exit(1 if errs else 0)

if __name__ == "__main__":
    main()
