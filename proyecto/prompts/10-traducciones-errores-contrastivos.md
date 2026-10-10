# Prompt para Traducciones — `erroresContrastivos` bilingüe en A2/B1

Leé `proyecto/CLAUDE.md` (regla 14), `proyecto/REGLAS-PREVENCION.md` (P2) y `proyecto/prompts/5-traducciones.md`. Clon fresco de `origin/main`.

## Problema
En una ficha A2/B1, al activar el switch de traducción, el último error común de DE B1 seguía en alemán: «*Para mí*» als *Für mich* in jedem Satz wiederholen statt… Los ítems de `erroresContrastivos` son strings en el idioma que se aprende, y en A2/B1 toda la prosa debe tener su traducción. Fer revierte para A2/B1 la regla del 16-09-2026 «los errores no llevan traducción».

## Qué hacer
En los núcleos **A2 y B1** (95 núcleos, unos 481 ítems), convertí cada ítem de `erroresContrastivos.<clave>` de string a objeto de dos claves:

```json
"es": [
  { "de": "«*Para mí*» als *Für mich* in jedem Satz wiederholen statt *Ich finde* oder *Meiner Meinung nach* abzuwechseln.",
    "es": "Repetir «*Für mich*» en cada frase, calcando «*Para mí*», en lugar de alternar «*Ich finde*» o «*Meiner Meinung nach*»." }
]
```

- Las claves son: el idioma que se aprende (el texto original, **sin tocarlo**) y la clave del diccionario (`es`, `en`, `fr`, `it`, `pt`: **la traducción va a ese idioma**, que es el idioma de app que corresponde a esa entrada).
- B2, C1 y C2 **no** se tocan: siguen siendo strings.
- Conservá el marcado: `*x*` y `**x**` se mantienen alrededor de la misma palabra; todo fragmento en el idioma que se aprende, dentro de la traducción, va entre « » (ver P5 y `prompts/3-contrastes.md`); la palabra objetivo no se traduce.
- La traducción debe leerse natural en el idioma destino, no calcada. Es un error típico: explicalo como lo explicaría un profesor de ese idioma. Misma extensión aproximada (máx. 400 caracteres).
- Ortografía suiza (`ss`, nunca `ß`) en toda palabra alemana, incluidas las citadas dentro de la traducción.
- No agregues ni quites ítems ni cambies su orden.

## Herramientas
El exportador/importador del maestro (`tools/exportar_traduccion_maestro.py`, `tools/importar_traduccion_maestro.py`) no cubre este campo: agregá una hoja «Errores contrastivos» (idioma, nivel, skill, clave, original, traducción) y el importado correspondiente con propagación a duplicados, o trabajá directo sobre los JSON con un script; elegí lo más simple y avisá cuál.

## Verificación obligatoria
1. `python3 proyecto/tools/componer.py --contenido proyecto/contenido --salida <tmp> --schema proyecto/schema/ficha.schema.json` → «606 fichas compuestas, 56 semanas especiales, 0 con problemas». (El esquema ya acepta string u objeto en estos ítems.)
2. `python3 proyecto/tools/verificar_consistencia.py --build <tmp>` → 0 problemas de la regla R1 (y ningún otro).
3. Para cada ítem modificado, la clave del idioma aprendido es idéntica al string viejo (script que lo compruebe sobre todo el diff).

## Entrega
Parche `git format-patch` desde `origin/main`, probado con `git am` en clon fresco; informe corto: cuántos ítems por idioma de app, casos dudosos. No pushees ni toques `assets/` (los regenera Overview).
