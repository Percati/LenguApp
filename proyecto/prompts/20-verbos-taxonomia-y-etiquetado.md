# Prompt para la conversación «Verbos» — taxonomía cerrada, esquema y etiquetado de verbos para la Biblioteca

Leé `CLAUDE.md`, `REGLAS-PREVENCION.md` (P3, P5, P7), `FALTANTES.md` 14, 20, 28 y 29, y el esquema `schema/ficha.schema.json` (campo `vocabulario[]` y `redemittel[].categoriasUso`). Trabajás sobre `proyecto/contenido/`, `proyecto/schema/` y `proyecto/tools/verificar_consistencia.py` (nunca `build/` ni `assets/`; Overview regenera los assets). No pushees. Partí del `origin/main` actual (Code F2 y patch 19 ya integrados).

## Qué se decidió (Fer, 11-oct-2026)
La Biblioteca va a tener una **tercera vista «Verbos»**, junto a «Vocabulario y expresiones» y «Habilidades». Cada idioma muestra **solo las subdivisiones que le corresponden**. Reglas cerradas:
1. **Taxonomía cerrada por idioma**, validada como `categoriasUso` (R5): una etiqueta fuera de la lista rompe el build.
2. **Etiquetado mínimo:** nivel y tipo obligatorios. **Ningún otro criterio de filtro.**
3. **Se reutiliza lo que ya existe** en `vocabulario` y `redemittel`. No se agrega contenido nuevo en esta ronda; al final se decide si hace falta.
4. Además del tipo: en alemán, **el caso o la preposición que rige** cada verbo; en inglés, **si el phrasal es separable**.
5. **Sin** la subdivisión «sustantivo + verbo» (ni Nomen-Verb-Verbindung ni collocation): se solapa con otras y es difusa. Esos ítems siguen donde están, como redemittel o vocabulario, sin etiqueta de verbo.

## Hechos medidos por Overview (verificalos, no los tomes por buenos)
- `vocabulario[]`: 4066 ítems en DE y 4088 en EN (contando repeticiones entre fichas). Los campos `tipo` y `nivelVerificado` **existen en el esquema pero están vacíos en el 100 %**. `reccion` existe en solo 70 ítems DE y en ninguno EN.
- Heurística grosera: ~129 ítems DE que empiezan por `sich` y ~229 EN con forma de phrasal. Es orden de magnitud, no inventario.
- El **nivel** ya está implícito: cada ítem cuelga de una ficha con nivel. Propuesta de Overview: **no etiquetarlo a mano**; se deriva del nivel más bajo de las fichas donde aparece el verbo (lo calcula el compositor). Etiquetarlo a mano duplicaría el dato y se desincronizaría. Si ves una razón para etiquetarlo igual, decilo con el costo.

## Tarea 1 — Inventario (solo lectura, informe antes de tocar nada)
Con un script reproducible (guardalo en `tools/`): extraé de `vocabulario` y `redemittel` todos los ítems que son verbos o locuciones verbales, **deduplicados** por (idioma, forma normalizada). Por idioma: cuántos únicos, cuántos por nivel (el más bajo en que aparecen) y cuántos candidatos a cada clase de la propuesta de abajo. Reportá además los ítems dudosos (¿verbo o frase hecha?).

## Tarea 2 — Taxonomía (propuesta; la cerrás vos con los números)
- **DE:** `reflexivo`, `trennbar`, `mit-Praposition` (verbo con preposición fija; la preposición y el caso van en `reccion`), `mit-Dativ`/`mit-Akkusativ` solo si el caso no es el esperable y no hay preposición. Un verbo puede tener varias clases.
- **EN:** `phrasal-separable`, `phrasal-inseparable`, `verbo-preposicion` (p. ej. *depend on*, *look at*). Un phrasal tiene exactamente una de las dos primeras.
- **Regla de cupo:** una clase solo entra a la lista si tiene al menos **15 ítems únicos** en ese idioma. Las más chicas se fusionan o se descartan; decidilo con los números del inventario y justificalo. El filtro de la app se calcula por idioma desde los datos (P3), así que no debe aparecer ninguna clase vacía en ningún idioma.
- Nombres de clase: cadenas internas estables (sin traducir). Las **etiquetas visibles** en 6 idiomas las agrega Code en `TextosInterfaz` (P7); dejá en el informe una tabla clase → nombre sugerido en los 6.

## Tarea 3 — Esquema y verificador
- Esquema: en `vocabulario[].items` agregá `claseVerbo` (array de strings, opcional, `minItems` 1 cuando está) y reutilizá `reccion`. En EN la separabilidad **es** la clase (`phrasal-separable` / `phrasal-inseparable`), no un booleano aparte. Para `redemittel` decidí si hace falta el mismo campo o si los verbos solo viven en `vocabulario` (recomendación de Overview: solo `vocabulario`, y los redemittel verbales se quedan como están).
- Verificador (R11, nueva): `claseVerbo` solo con valores de la lista cerrada de **ese idioma**; en DE, `reccion` obligatoria para `mit-Praposition`, con formato controlado (`auf + Akk`, `mit + Dat`, `an + Akk/Dat`…) y lista cerrada de preposiciones; en EN, `phrasal-*` solo si la forma tiene partícula. Demostrá con casos negativos que marca el 100 % de lo malo y **0 falsos positivos** sobre lo bueno.
- Actualizá `componer.py` solo si hace falta para que los campos nuevos pasen; **no** generes todavía el índice de verbos (lo hace Overview después).

## Tarea 4 — Etiquetar
- Etiquetá **todos** los ítems verbales reutilizables, en la **fuente** (nucleos/packs/ocurrencias). Leélos uno por uno: no clasifiques por heurística sola. Un `sich` suelto no es reflexivo si es recíproco o fijo (anotá los casos).
- DE `reccion`: completala en **todos** los verbos de clase `mit-Praposition` y en los casos/preposiciones evidentes. Si no estás seguro de un caso, dejalo sin `reccion` y listalo en el informe; no inventes.
- No toques traducciones, ejemplos ni nada fuera de estos campos. Ningún tope de longitud superado.

## Verificación obligatoria
1. `componer.py` → 606 fichas, 56 semanas especiales, 0 con problemas. `verificar_consistencia.py`: R1-R11 en 0.
2. Script sobre el diff: solo cambian `claseVerbo` y `reccion`; estructura JSON idéntica en lo demás; mismos « » y `*`.
3. Muestra a ojo: 40 verbos por idioma, con su clase y su `reccion`, contra un diccionario que cites.

## Informe
Inventario con números reales, taxonomía final y por qué, cuántos ítems quedaron etiquetados por clase y nivel, los dudosos, y un conteo de cuántos verbos únicos hay por (idioma, nivel): si algún nivel queda por debajo de ~25 verbos, decilo, porque define si hace falta contenido nuevo. Actualizá `FALTANTES.md` (sección 29) y `REGLAS-PREVENCION.md`.
