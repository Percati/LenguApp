#!/usr/bin/env python3
"""
descomponer.py — parte las fichas en sus tres piezas reutilizables.

Hasta ahora cada semana era una ficha completa y autonoma. Eso obliga a
reescribir la gramatica cada vez que un skill reaparece, y con 53 semanas por
combinacion y diez combinaciones de ingles y aleman era inviable.

El contenido se factoriza en tres objetos, cada uno con su propia clave:

  NUCLEO   (skill, nivel)          descripcion, cuadro, ejemplos, notas,
                                   contraste, errores, expresiones, prompt,
                                   autochequeo. Fijo: no depende de la semana
                                   ni del tema de conversacion.

  PACK     (idioma, nivel, topic, n)  el vocabulario. Depende del tema y del
                                   nivel, no del skill. La "n" distingue las
                                   sucesivas apariciones del mismo tema, que
                                   cambian entre un 40 y un 75 % de sus items.

  APARICION (skill, nivel, orden)  subtitulo, tema asignado, pack, mision,
                                   microtareas, evidencia. Es lo unico que
                                   hay que escribir cuando un skill reaparece.

La ficha que ve el usuario es la union de los tres. Un anio de 53 semanas pasa
de 53 fichas completas a ~18 nucleos mas 53 apariciones ligeras.

Uso:
    python3 descomponer.py ../build/*.json --salida ../contenido
"""
import argparse, json, sys
from collections import defaultdict
from pathlib import Path

# Campos que pertenecen al NUCLEO: no cambian entre apariciones del skill.
NUCLEO = ["titulo", "categoria", "ancla", "descripcion", "cuadroReferencia",
          "ejemplos", "notas", "contraste", "errores", "erroresContrastivos",
          "redemittel", "autochequeo", "promptCorreccion", "skillsRelacionados",
          "topicBlocklist", "variante"]
# Campos que pertenecen a la APARICION: cambian cada vez.
APARICION = ["order", "topicId", "subtitulo", "challengeType", "evidencia",
             "mision", "microtareas", "bilingue"]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("fichas", nargs="+")
    ap.add_argument("--salida", default="../contenido")
    a = ap.parse_args()
    base = Path(a.salida)
    for sub in ("nucleos", "packs", "ocurrencias"):
        base.joinpath(sub).mkdir(parents=True, exist_ok=True)

    fichas, especiales = [], []
    for f in sorted(a.fichas):
        d = json.loads(Path(f).read_text(encoding="utf-8"))
        (especiales if d.get("_tipo") else fichas).append(d)

    # --- packs: numerar las apariciones del mismo tema por idioma y nivel ---
    orden_pack, contador = {}, defaultdict(int)
    for d in sorted(fichas, key=lambda x: (x["idioma"], x["nivel"], x["order"], x["id"])):
        k = (d["idioma"], d["nivel"], d["topicId"])
        contador[k] += 1
        orden_pack[d["id"]] = contador[k]

    n_nuc, n_pack, n_ocu = 0, 0, 0
    apariciones = defaultdict(list)
    nucleos_vistos = {}

    for d in fichas:
        # NUCLEO
        kn = f'{d["skillId"]}-{d["nivel"]}'
        nuc = {"skillId": d["skillId"], "nivel": d["nivel"], "idioma": d["idioma"]}
        nuc.update({c: d[c] for c in NUCLEO if c in d})
        if kn in nucleos_vistos and nucleos_vistos[kn] != nuc:
            print(f"  ! {kn}: dos apariciones con nucleo distinto", file=sys.stderr)
        nucleos_vistos[kn] = nuc

        # PACK
        n = orden_pack[d["id"]]
        kp = f'{d["idioma"]}-{d["nivel"]}-{d["topicId"]}-{n}'
        base.joinpath("packs", kp + ".json").write_text(json.dumps(
            {"idioma": d["idioma"], "nivel": d["nivel"], "topicId": d["topicId"],
             "n": n, "vocabulario": d["vocabulario"]},
            ensure_ascii=False, indent=1), encoding="utf-8")
        n_pack += 1

        # APARICION
        ocu = {"skillId": d["skillId"], "packId": kp}
        ocu.update({c: d[c] for c in APARICION if c in d})
        apariciones[(d["idioma"], d["nivel"])].append(ocu)

    for kn, nuc in nucleos_vistos.items():
        base.joinpath("nucleos", kn + ".json").write_text(
            json.dumps(nuc, ensure_ascii=False, indent=1), encoding="utf-8")
        n_nuc += 1

    for (idi, niv), lista in apariciones.items():
        lista.sort(key=lambda x: (x["skillId"], x["order"]))
        base.joinpath("ocurrencias", f"{idi}-{niv}.json").write_text(json.dumps(
            {"idioma": idi, "nivel": niv, "apariciones": lista},
            ensure_ascii=False, indent=1), encoding="utf-8")
        n_ocu += len(lista)

    for d in especiales:
        base.joinpath("nucleos", d["id"] + ".json").write_text(
            json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")

    print(f"{n_nuc} nucleos, {n_pack} packs, {n_ocu} apariciones, "
          f"{len(especiales)} semanas especiales", file=sys.stderr)


if __name__ == "__main__":
    main()
