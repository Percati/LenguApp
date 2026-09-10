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

REVIEW   = {8, 16, 24, 32, 40, 48}
SURVIVAL = {12, 28, 44}
SEP_MIN  = 10          # semanas minimas entre apariciones de un mismo skill
FUNDACIONAL_MAX = 20   # un skill fundacional no puede debutar despues de aqui

def semanas_iso(anio: int) -> int:
    """52 o 53 segun el anio ISO. 2026 tiene 53."""
    return date(anio, 12, 28).isocalendar()[1]

def rango_fechas(anio, semana):
    lun = date.fromisocalendar(anio, semana, 1)
    dom = date.fromisocalendar(anio, semana, 7)
    return lun, dom

def apariciones(skill):
    """Lista de (skillId, order) segun cuantas veces aparece en el anio."""
    return [(skill["id"], o + 1) for o in range(skill.get("apariciones", 2))]

def generar(anio, idioma, nivel, banco, topics, desde=1, intentos=400, fijas=None):
    """Greedy aleatorizado con reintentos.

    El backtracking completo es innecesario y explota combinatoriamente: con
    ~90 tareas y 14 topics hay demasiadas ramas. Un greedy que recorre las
    semanas en orden y prueba pares (tarea, topic) barajados encuentra
    solucion en milisegundos; si se atasca, se reintenta con otro barajado.
    Todo con Random(anio), asi que el resultado sigue siendo reproducible.
    """
    total = semanas_iso(anio)
    libres = [w for w in range(desde, total + 1)
              if w not in REVIEW and w not in SURVIVAL]
    skills = [s for s in banco if s["idioma"] == idioma and nivel in s["niveles"]]
    if not skills:
        print(f"El banco no tiene skills de {idioma} en {nivel}.", file=sys.stderr)
        sys.exit(2)
    idx = {s["id"]: s for s in skills}
    tareas_base = [t for s in skills for t in apariciones(s)]
    rnd = random.Random(anio)

    # Semanas fijadas a mano. Sirven para ediciones curadas (el piloto 2026) y
    # para anclar un skill fundacional a una fecha concreta. El generador las
    # respeta y rellena el resto. Sin esto, el calendario escrito a mano y el
    # calculado divergen, que es exactamente el fallo que este soporte evita.
    fijas = fijas or {}
    fijadas = {int(w): (tuple(v["tarea"]), v["topic"]) for w, v in fijas.items()}

    for intento in range(intentos):
        tareas = list(tareas_base)
        rnd.shuffle(tareas)
        plan, usados_skill, usados_topic = {}, {}, {}
        for w, (tarea, topic) in fijadas.items():
            plan[w] = (tarea, topic)
            usados_skill.setdefault(tarea[0], []).append(w)
            usados_topic.setdefault(tarea[0], set()).add(topic)
        pendientes = [t for t in tareas if t not in [v[0] for v in plan.values()]]
        ok = True
        for semana in libres:
            if semana in plan:
                continue
            colocada = False
            tops = list(topics); rnd.shuffle(tops)
            for tarea in list(pendientes):
                sid = tarea[0]
                if any(abs(w - semana) < SEP_MIN for w in usados_skill.get(sid, [])):
                    continue
                if idx[sid].get("fundacional") and not usados_skill.get(sid) \
                   and semana > FUNDACIONAL_MAX:
                    continue
                bloq = set(idx[sid].get("topicBlocklist", []))
                for topic in tops:
                    if topic in usados_topic.get(sid, set()) or topic in bloq:
                        continue
                    if plan.get(semana - 1, (None, None))[1] == topic:
                        continue
                    plan[semana] = (tarea, topic)
                    usados_skill.setdefault(sid, []).append(semana)
                    usados_topic.setdefault(sid, set()).add(topic)
                    pendientes.remove(tarea)
                    colocada = True
                    break
                if colocada:
                    break
            if not colocada:
                ok = False
                break
        if ok:
            break
    else:
        print("Sin solucion tras {} intentos. Bajar SEP_MIN o ampliar el banco."
              .format(intentos), file=sys.stderr)
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
            for w2, t2 in vistos.get(sid, []):
                if abs(w2 - w) < SEP_MIN:
                    errores.append(f"S{w}: {sid} a menos de {SEP_MIN} semanas de S{w2}")
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

    d = json.loads(Path(a.banco).read_text())
    fijas = {}
    if a.fijas:
        fijas = json.loads(Path(a.fijas).read_text()).get(f"{a.anio}-{a.idioma}-{a.nivel}", {})
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
