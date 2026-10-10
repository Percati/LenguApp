# Prompt para Traducciones — registro informal en de/fr/it y ortografía suiza (R9, R10)

Leé `CLAUDE.md`, `REGLAS-PREVENCION.md`, `FALTANTES.md` 6.1, 14, 23 y 27, y tus informes de los prompts 12, 14 y 17. Trabajás sobre `proyecto/contenido/` (nunca `build/` ni `assets/`; Overview regenera los assets). No pushees. Partí del `origin/main` con tu parche R8 y el parche de assets ya aplicados.

## Qué se decidió
Fer decidió **«tú en todo»** (10-oct-2026) y la interfaz ya usa informal en los 6 idiomas (ES tú, EN you, DE **du**, FR **tu**, IT **tu**, PT **você**). El contenido quedó a medias: el español ya está en «tú», pero las instrucciones traducidas a **alemán, francés e italiano** siguen en registro formal. Medición de Overview sobre los compuestos (heurística, orden de magnitud): **~480 cadenas en alemán** («Erklären Sie…», «Sie haben…»), **~500 en francés** («Écrivez», «Concentrez-vous», «votre») y **~270 en italiano** («Spieghi…», «Ha ricevuto…»), casi todas en `mision`, `microtareas`, `promptCorreccion`, y algo en `notas` y `descripcion`. Portugués no es problema: «você explique» es el imperativo informal de Brasil.
Además, **la ortografía suiza** (`ss`, nunca `ß`) no se respeta en ~88 cadenas / 51 archivos del texto alemán (`heißt`, `Außerdem`, `äußern`, `größer`, `schließlich`…), sobre todo en `contraste`, `redemittel`, `cuadroReferencia`, `ejemplos` y `autochequeo` de las fichas de inglés.

## Tarea 1 — Detectores primero (R9 y R10)
Sin tocar R1-R8, en `tools/verificar_consistencia.py`:
- **R9 (registro):** en el texto de **instrucciones** (`mision`, `microtareas`, `autochequeo`, `promptCorreccion`, `notas`, `descripcion`, `subtitulo`) bajo las claves `de`, `fr`, `it`: marcar el trato formal: `Sie/Ihnen/Ihr/Ihre…` y los imperativos con `Sie` en alemán; `vous`, `votre/vos` y los imperativos en `-ez` en francés; `Lei`, `Suo/Sua` y los imperativos de cortesía (`Spieghi`, `Scriva`, `Ha ricevuto`…) en italiano. **No mirar** `redemittel`, `ejemplos`, `cuadroReferencia`, `contraste`, `erroresContrastivos` ni `vocabulario`: ahí el registro formal es **contenido que se enseña** (frases de cortesía, Sie/du, Redemittel de queja y negociación); y no marcar el registro dentro de « » ni de `*…*`. Lista de excepciones explícita (`EXENTAS_R9`) con motivo; cuidado con falsos positivos como «rendez-vous».
- **R10 (ortografía suiza):** `ß` en cualquier valor de texto bajo la clave `de` o en fichas alemanas, **excepto** cuando la `ß` se menciona como letra (ficha `DE-V08-B1`, las notas «ss/ß», `«ß»` entre comillas). Lista de excepciones explícita.
Probalos como en los prompts 12, 14 y 17: demostrá que marcan el 100 % de lo que corregís y **0 falsos positivos** sobre el resto.

## Tarea 2 — Corregir
- **Registro:** pasá a informal solo lo que R9 marca: alemán **du/dein**, francés **tu/ton/ta/tes**, italiano **tu/tuo/tua**. Reescribí la frase entera cuando haga falta (la persona del verbo, los pronombres, los posesivos y los reflexivos cambian: «Erklären Sie Ihrem Freund» → «Erkläre deinem Freund»; «Écrivez votre texte» → «Écris ton texte»; «Spieghi a un amico» → «Spiega a un amico»). Conservá el sentido, el nivel y el largo (los topes de campo existen). Arreglá en la **fuente**.
- **Ortografía:** `ß` → `ss` en todo el texto alemán que R10 marque (`heißt`→`heisst`, `Außerdem`→`Ausserdem`, `äußern`→`äussern`, `größer`→`grösser`). Conservá la `ß` cuando es el objeto de la explicación.
- Las filas formales que **se enseñan** (frases con Sie/vous/Lei dentro de `redemittel`/`ejemplos`/`contraste`) **no se tocan**.
- Los fragmentos en otro idioma siguen entre « » (R8 sigue en 0).

## Verificación obligatoria
1. `componer.py` → 606 fichas, 56 semanas especiales, 0 con problemas.
2. `verificar_consistencia.py`: R1-R10 en 0.
3. Script sobre el diff: todas las cadenas cambiadas cuelgan de una clave `de/fr/it` (registro) o contienen alemán (ortografía); mismos « » y `*`; estructura JSON idéntica; ningún archivo fuera de `contenido/` salvo el verificador y los docs; ningún tope de longitud superado (`validar_packs` y esquema).
4. Barrido independiente: 30 cadenas al azar por idioma, leídas a ojo. Reportá lo que R9/R10 no vean (por ejemplo `Man`/`Wir` impersonales, que NO se tocan, o el voseo/usted residual en otros campos).

## Informe
Conteo real por idioma y campo, excepciones, 15 antes/después por idioma, casos de duda. Actualizá `FALTANTES.md` 6.1 y 27, y `REGLAS-PREVENCION.md` (nueva regla de verificación de registro y ortografía).
