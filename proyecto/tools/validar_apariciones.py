#!/usr/bin/env python3
"""
validar_apariciones.py — coherencia cruzada de contenido/ocurrencias/ antes de componer.

Lo que componer.py NO mira y este si:
  1. Cada aparicion coincide, en orden, con una semana de contenido del
     calendario (skillId, order, topicId).
  2. El packId existe como archivo, es el n-esimo del tema en ese calendario y
     su topicId coincide con el de la aparicion.
  3. challengeType corresponde al nivel.
  4. La mision y las microtareas usan vocabulario real del pack: al menos
     MIN_VOCAB items de prioridad "nucleo" aparecen citados en el texto.
  5. Entre apariciones del mismo skill: subtitulos distintos y consignas que
     no sean casi identicas (ratio de difflib).
  6. Audio: todo ejemplo con "audio": true de los nucleos usados figura como
     grabado en audio-estado.json.

Uso:
    python3 tools/validar_apariciones.py --anio 2027 [--idioma de --nivel C1]
Sale con 1 si hay errores (1-5). 6 es aviso.
"""
import argparse, collections, difflib, json, re, sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

CHALLENGE = {"A2": "chunk_deployment", "B1": "guided_production",
             "B2": "constrained_production", "C1": "open_production",
             "C2": "adaptive_production"}
MIN_VOCAB = 2
SIMILITUD_MAX = 0.6


def variantes(item):
    """Formas buscables de un item: sin parentesis, sin 'to ', alternativas."""
    base = re.sub(r"\([^)]*\)", "", item).strip()
    partes = re.split(r"\s*/\s*|\s*,\s*", base)
    out = set()
    for p in partes + [base]:
        p = p.strip().lower()
        if not p:
            continue
        out.add(p)
        for pref in ("to ", "a ", "an ", "the ", "der ", "die ", "das ", "sich "):
            if p.startswith(pref):
                out.add(p[len(pref):])
        # separable/reflexivo aleman: 'etwas in Kauf nehmen' -> 'in kauf'
        out.add(re.sub(r"^(etwas|jemanden|jemandem|sich)\s+", "", p))
    return {v for v in out if len(v) >= 4}


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--raiz", default=str(Path(__file__).resolve().parent.parent))
    ap.add_argument("--anio", type=int, required=True)
    ap.add_argument("--idioma")
    ap.add_argument("--nivel")
    a = ap.parse_args()
    R = Path(a.raiz)
    leer = lambda p: json.loads(p.read_text(encoding="utf-8"))

    audio = leer(R / "audio-estado.json")
    errores, avisos = [], []

    for p in sorted((R / "contenido/ocurrencias").glob(f"*-{a.anio}.json")):
        d = leer(p)
        idi, niv = d["idioma"], d["nivel"]
        if (a.idioma and idi != a.idioma) or (a.nivel and niv != a.nivel):
            continue
        tag = f"{idi}-{niv}-{a.anio}"
        cal = [x for x in leer(R / f"data/calendarios/{a.anio}-{idi}-{niv}.json")
               if x["tipo"] == "content"]
        aps = d["apariciones"]
        if len(aps) != len(cal):
            errores.append(f"{tag}: {len(aps)} apariciones, calendario tiene {len(cal)}")
        cnt = collections.Counter()
        por_skill = collections.defaultdict(list)
        for i, (o, c) in enumerate(zip(aps, cal)):
            w = f"{tag} S{c['semana']}"
            for k in ("skillId", "order", "topicId"):
                if o.get(k) != c[k]:
                    errores.append(f"{w}: {k}={o.get(k)} y el calendario dice {c[k]}")
            cnt[c["topicId"]] += 1
            esperado = f"{idi}-{niv}-{a.anio}-{c['topicId']}-{cnt[c['topicId']]}"
            pp = R / f"contenido/packs/{o['packId']}.json"
            if not pp.exists():
                errores.append(f"{w}: pack {o['packId']} no existe"); continue
            if o["packId"] != esperado:
                errores.append(f"{w}: pack {o['packId']}, se esperaba {esperado}")
            pack = leer(pp)
            if pack.get("topicId") != o["topicId"]:
                errores.append(f"{w}: pack de {pack.get('topicId')} en semana de {o['topicId']}")
            if o.get("challengeType") != CHALLENGE[niv]:
                errores.append(f"{w}: challengeType {o.get('challengeType')}")
            # Los campos bilingues son {idioma: texto} desde la fase de
            # traducciones; el vocabulario del pack se busca en la version del
            # idioma que se aprende, que es la unica que lo cita sin traducir.
            def txt(v):
                return v[idi] if isinstance(v, dict) else v
            texto = " ".join([txt(o["mision"]["consigna"]),
                              *(txt(r) for r in o["mision"]["requisitos"]),
                              *(txt(m["texto"]) for m in o["microtareas"])]).lower()
            usados = [v["item"] for v in pack["vocabulario"] if v["prioridad"] == "nucleo"
                      and any(x in texto for x in variantes(v["item"]))]
            if len(usados) < MIN_VOCAB:
                errores.append(f"{w}: solo {len(usados)} items nucleo del pack en mision/microtareas")
            por_skill[o["skillId"]].append((c["semana"], o))
            nuc = leer(R / f"contenido/nucleos/{o['skillId']}-{niv}.json")
            for e in nuc["ejemplos"]:
                if e.get("audio"):
                    # audio-estado guarda el texto sin marcado en linea
                    t = e["texto"]
                    t = t[idi] if isinstance(t, dict) else t
                    k = f"{idi}|Ejemplo|{t.replace('*', '')}"
                    if not audio.get(k, {}).get("grabado"):
                        avisos.append(f"{w}: audio sin grabar: {e['texto'][:50]}")
        for sk, lst in por_skill.items():
            for (s1, o1), (s2, o2) in [(x, y) for i, x in enumerate(lst) for y in lst[i + 1:]]:
                sub1, sub2 = o1["subtitulo"], o2["subtitulo"]
                if isinstance(sub1, dict):
                    sub1, sub2 = sub1.get(idi), sub2.get(idi)
                if sub1 == sub2:
                    errores.append(f"{tag} {sk}: mismo subtitulo en S{s1} y S{s2}")
                c1, c2 = o1["mision"]["consigna"], o2["mision"]["consigna"]
                if isinstance(c1, dict):
                    c1, c2 = c1.get(idi, ""), c2.get(idi, "")
                r = difflib.SequenceMatcher(None, c1, c2).ratio()
                if r > SIMILITUD_MAX:
                    errores.append(f"{tag} {sk}: consignas S{s1}/S{s2} casi iguales ({r:.2f})")

    for e in errores: print("ERROR ", e)
    for v in sorted(set(avisos)): print("aviso ", v)
    print(f"{len(errores)} errores, {len(set(avisos))} avisos")
    sys.exit(1 if errores else 0)


if __name__ == "__main__":
    main()
