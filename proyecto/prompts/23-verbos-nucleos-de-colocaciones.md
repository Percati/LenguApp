# Prompt para la conversación «Verbos» — extraer el núcleo verbal de las colocaciones

> **Requisito previo:** necesitás el repo conectado. Si no ves la carpeta, PARÁ y avisá: no inventes números.

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (P3, P5, P11), `proyecto/FALTANTES.md` **29**, **32**, **33** y sobre todo **34**, y tu propio informe del prompt 20. Trabajás sobre `proyecto/contenido/`, `proyecto/schema/` y `proyecto/tools/`. Nunca `build/` ni `app/src/main/assets/` (Overview regenera los assets). No pushees: entregá parches sobre el `origin/main` actual.

## Por qué existe esta ronda
Fer probó la app con alemán B2 y **buscar «stellen» en la vista Verbos no devolvió nada**. Parte era un bug de código (ya en manos de Code). La otra parte es tuya y es más grave: con la **opción A** quedaron fuera de la vista los verbos que en el contenido solo existen **dentro de una colocación**. *stellen*, *einstellen* y *umstellen* —los tres que Fer nombró al pedir la función— no están como verbos: están en *die Frage stellen* (A2), *die Uhrzeit einstellen* (A2), *die Ernährung umstellen* (B1), *etwas in Frage stellen* (B2), *etwas auf den Prüfstand stellen* (C1).

Medición de Overview en alemán, para que la verifiques: **341 colocaciones verbales sin etiquetar**, con **191 núcleos verbales distintos** adentro, de los cuales **120 no aparecen en ningún verbo etiquetado** (*abbauen*, *einhalten*, *führen*, *behalten*, *nachgehen*, *widmen*…). Hay 1937 ítems DE y 1707 EN sin etiquetar de más de una palabra, así que el universo a revisar es grande y la mayoría **no** es verbal (*die Amtssprache*, *der Schlafmangel*).

**Decisión de Fer (11-oct-2026):** la opción A sigue decidiendo **qué se lista** (una vista de verbos no se llena de frases), pero el **núcleo verbal de una colocación sí se indexa como verbo**, con la colocación visible como contexto de uso.

## Tarea 1 — Inventario, y PARÁ para que lo apruebe
Script reproducible en `tools/`. Para cada idioma, de los ítems de `vocabulario` **sin** `claseVerbo`:
- Identificá los que son **colocación verbal** (tienen un verbo como núcleo: *die Frage stellen*, *to keep a promise*) y separalos de los que no lo son (sustantivos, adjetivos, *to be + adjetivo*).
- Extraé el **núcleo verbal en infinitivo** de cada una y agrupá: cuántos núcleos distintos, cuántos ya existen como verbo etiquetado y cuántos son nuevos.
- Reportá: núcleos nuevos por idioma, los 30 más frecuentes con sus colocaciones, los casos dudosos, y los que descartás por no ser verbales.
- **Casos que vas a encontrar y sobre los que quiero tu criterio en el informe, no una decisión silenciosa:** verbos auxiliares y de soporte muy frecuentes (*sein*, *haben*, *werden*, *lassen*, en inglés *to be*, *to have*, *to get*): 29 de las colocaciones DE tienen *sein* de núcleo, y *to be + adjetivo* es media lista en inglés. Mi posición: **un auxiliar no es un verbo que se aprenda**, así que *to be rusty* o *to be in remission* no aportan un verbo a la vista. Proponé la lista cerrada de núcleos a excluir y justificala.
- Lo mismo con los modismos cuyo verbo es intercambiable o vacío (*to take its toll*, *auf der Kippe stehen*): si el verbo no se usa fuera de la frase, no es un verbo de la vista.

## Tarea 2 — Esquema: la colocación como contexto del verbo
El verbo va a aparecer en la vista con su colocación visible debajo. Decidí y proponé **antes de implementar**:
- **Dónde vive el verbo nuevo.** No dupliques contenido: el verbo no es un ítem nuevo de `vocabulario` (rompería el conteo de ítems por pack y el tope del esquema). Mi propuesta: el verbo vive **solo en la tabla maestra** (`contenido/verbos/{de,en}.json`), con las colocaciones del contenido que lo contienen, y `aplicar_verbos.py` etiqueta la **colocación** en los packs con una marca nueva que diga «esta colocación tiene este núcleo verbal». Así el pack no gana ítems y la app puede construir la lista de verbos. Si ves una forma mejor, proponela con los costos.
- **Campos nuevos en el ítem de `vocabulario`:** lo mínimo para que la app arme la lista. Por ejemplo `nucleoVerbal` (el infinitivo) y la `claseVerbo` + `reccion` **de ese núcleo**. Un ítem con `nucleoVerbal` NO entra a la lista como colocación: entra su núcleo.
- Dejá claro en el esquema y en `FALTANTES` la diferencia entre los dos tipos de entrada: **verbo propio** (opción A, ya hecho) y **verbo con colocación** (esto).
- **Nada de nivel:** el nivel no se etiqueta (FALTANTES 33). La app lo deriva.

## Tarea 3 — Verificador (ampliar R11)
- `nucleoVerbal` solo en ítems **sin** `claseVerbo` propio y solo si está en la tabla maestra.
- El mismo núcleo verbal tiene **siempre** la misma clase y la misma `reccion`, venga de un verbo propio o de una colocación (es la misma garantía de P11, extendida).
- Ningún núcleo de la lista de exclusión (auxiliares) etiquetado.
- Autotest con casos negativos: 100 % de lo malo marcado, 0 falsos positivos.

## Tarea 4 — Etiquetar
- Leé **ítem por ítem** las colocaciones que el inventario marcó como verbales. No clasifiques por heurística: *die Ernährung umstellen* es *umstellen*, pero *etwas in Frage stellen* es un modismo cuyo núcleo (*stellen*) sí se usa suelto, y *to take its toll* no.
- Completá la clase y, en alemán, la `reccion` del núcleo. Si no estás seguro del régimen, dejalo vacío y listalo.
- **Un núcleo puede juntar varias colocaciones:** la tabla maestra las guarda todas; el orden es el alfabético de la colocación.
- No toques traducciones, ejemplos, ni nada fuera de los campos nuevos.

## Verificación obligatoria
1. `componer.py --contenido proyecto/contenido --salida <dir> --schema proyecto/schema/ficha.schema.json` → 606 fichas, 56 semanas especiales, 0 con problemas. `verificar_consistencia.py --build <dir>`: R1–R11 en 0.
2. `aplicar_verbos.py` sin `--escribir`: 0 diferencias, idempotente.
3. Script sobre el diff: solo cambian los campos nuevos; estructura JSON idéntica en lo demás; mismos « » y `*`; ningún tope de longitud superado; ningún pack gana ni pierde ítems de `vocabulario`.
4. **El caso que motivó la ronda:** mostrá que *stellen*, *einstellen* y *umstellen* quedan como verbos de la vista, con sus colocaciones, y que la familia de *stellen* queda completa. Guardá esa comprobación en un archivo del informe.
5. Muestra a ojo: 40 núcleos por idioma con su clase, su `reccion` y su colocación, contra un diccionario que cites.

## Informe
Inventario con números reales; la lista de exclusión de auxiliares y por qué; cuántos verbos gana la vista por idioma (antes 305 DE / 368 EN); los dudosos; y **la comprobación del caso *stellen***. Actualizá `FALTANTES.md` (sección 34) y `REGLAS-PREVENCION.md` (P11 ampliada). Avisá qué tiene que hacer Code después: los campos nuevos en el modelo de Kotlin (el parser es estricto) y cómo armar la lista con los núcleos.
