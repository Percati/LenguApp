#!/usr/bin/env python3
"""
componer.py — arma las fichas a partir de nucleo + pack + aparicion.

Contraparte de descomponer.py. Produce exactamente el mismo JSON que antes
generaba el compilador desde una ficha completa, de modo que la app no se
entera del cambio de formato de autoria.

Regla de composicion, que es lo unico importante:
  - el NUCLEO aporta todo lo que no depende de la semana
  - la APARICION aporta lo que si depende: tema, subtitulo, mision, microtareas
  - el PACK aporta el vocabulario, elegido por (idioma, nivel, tema, n)

Si un campo estuviera en dos sitios, gana la aparicion: es la mas especifica.

Uso:
    python3 componer.py --contenido ../contenido --salida ../build
"""
import argparse, json, sys
from pathlib import Path

ORDEN = ["id", "skillId", "order", "idioma", "nivel", "categoria", "topicId",
         "titulo", "subtitulo", "ancla", "challengeType", "bilingue", "variante",
         "evidencia", "descripcion", "cuadroReferencia", "ejemplos", "notas",
         "contraste", "errores", "erroresContrastivos", "vocabulario",
         "redemittel", "mision", "microtareas", "autochequeo",
         "promptCorreccion", "skillsRelacionados", "topicBlocklist"]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--contenido", default="../contenido")
    ap.add_argument("--salida", default="../build")
    ap.add_argument("--schema")
    a = ap.parse_args()
    base, out = Path(a.contenido), Path(a.salida)
    out.mkdir(parents=True, exist_ok=True)

    val = None
    if a.schema:
        import jsonschema
        val = jsonschema.Draft202012Validator(
            json.loads(Path(a.schema).read_text(encoding="utf-8")))

    nucleos, packs = {}, {}
    especiales = []
    for p in base.joinpath("nucleos").glob("*.json"):
        d = json.loads(p.read_text(encoding="utf-8"))
        if d.get("_tipo") == "semana_especial":
            especiales.append(d)
        else:
            nucleos[f'{d["skillId"]}-{d["nivel"]}'] = d
    for p in base.joinpath("packs").glob("*.json"):
        packs[p.stem] = json.loads(p.read_text(encoding="utf-8"))

    n, fallos = 0, 0
    for p in sorted(base.joinpath("ocurrencias").glob("*.json")):
        doc = json.loads(p.read_text(encoding="utf-8"))
        for ocu in doc["apariciones"]:
            kn = f'{ocu["skillId"]}-{doc["nivel"]}'
            if kn not in nucleos:
                print(f"  ! falta el nucleo {kn}", file=sys.stderr); fallos += 1; continue
            if ocu["packId"] not in packs:
                print(f"  ! falta el pack {ocu['packId']}", file=sys.stderr); fallos += 1; continue
            nuc, pack = nucleos[kn], packs[ocu["packId"]]

            f = {"idioma": doc["idioma"], "nivel": doc["nivel"]}
            f.update({k: v for k, v in nuc.items()
                      if k not in ("skillId", "nivel", "idioma")})
            f.update({k: v for k, v in ocu.items() if k != "packId"})
            f["skillId"] = ocu["skillId"]
            f["vocabulario"] = pack["vocabulario"]
            f["id"] = f'{ocu["skillId"]}-{doc["nivel"]}-{ocu["order"]}'
            f = {k: f[k] for k in ORDEN if k in f}

            if val:
                errs = list(val.iter_errors(f))
                if errs:
                    print(f"  ! {f['id']}: {list(errs[0].path)} {errs[0].message[:70]}",
                          file=sys.stderr)
                    fallos += 1
                    continue
            out.joinpath(f["id"] + ".json").write_text(
                json.dumps(f, ensure_ascii=False, indent=1), encoding="utf-8")
            n += 1

    for d in especiales:
        out.joinpath(d["id"] + ".json").write_text(
            json.dumps(d, ensure_ascii=False, indent=1), encoding="utf-8")

    print(f"{n} fichas compuestas, {len(especiales)} semanas especiales, "
          f"{fallos} con problemas", file=sys.stderr)
    sys.exit(1 if fallos else 0)


if __name__ == "__main__":
    main()
