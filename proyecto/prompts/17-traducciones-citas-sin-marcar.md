# Prompt para Traducciones — fragmentos en otro idioma sin « » (R8)

Leé `CLAUDE.md`, `REGLAS-PREVENCION.md` (P2, P5), `FALTANTES.md` 14 y 23, y tus informes de los prompts 10, 12 y 14. Trabajás sobre `proyecto/contenido/` (nunca `build/` ni `assets/`; Overview regenera los assets). No pushees.

## Problema (feedback de Fer, 10-oct-2026)
La regla P5 dice que todo fragmento en un idioma distinto al del texto que lo rodea va escrito a mano entre « ». La verificación R4 solo comprueba que los « » estén **balanceados**, no que **existan**. Resultado: en las fichas A2/B1 traducidas hay fragmentos del idioma que se aprende sin marcar. Caso real de la semana actual, `EN-F01-B1.json`:
- `"es": "I think es neutral y funciona en casi cualquier situación."` → debe ser `«I think» es neutral…` (la nota siguiente, `«In my opinion» es un poco más formal…`, ya está bien).
- Otros del mismo tipo: `Because permite añadir una razón breve.`, `I agree es la forma más sencilla…`, `Maybe funciona bien…`, `Confundir like y think.`, subtítulos como `I think… sobre el dinero`, autochequeos como `¿Sigue una razón con because?`.

Medición de Overview (heurística gruesa con listas de palabras, con falsos positivos: tomala como orden de magnitud, no como conteo): ~100 cadenas únicas en español por inglés A2/B1 fuera de `contraste` y `erroresContrastivos`, y cifras parecidas en fr/it/pt/de; alemán→otros idiomas, bastante menos. Campos más afectados: `notas`, `subtitulo`, `errores`, `autochequeo`, `promptCorreccion`, `descripcion`, `microtareas/texto`.

## Tarea 1 — Detector R8 primero (antes de tocar contenido)
Agregá la regla **R8** a `tools/verificar_consistencia.py`, sin tocar R1-R7. Para fichas EN y DE de A2/B1, en cada valor de texto bajo una clave de idioma **distinta del que se aprende** (en una ficha EN: `de`, `es`, `fr`, `it`, `pt`; en una DE: `en`, `es`, `fr`, `it`, `pt`):
- (a) **Fragmentos propios de la ficha:** reunir los textos en el idioma que se aprende que están en esa misma ficha (`redemittel[].expresion`, `vocabulario[].item`, `ejemplos[].texto` en el idioma aprendido, y los de `cuadroReferencia`) y marcar cualquier aparición de ellos, sin distinción de mayúsculas, **fuera de « » y de `*…*`**.
- (b) **Palabras funcionales del idioma aprendido** con una lista corta y explícita **por par de idiomas**, fuera de « » y de `*…*`. Cuidado con los falsos amigos (it. «i», de. «also», pt. «das», fr. «on»/«a», es. «no»…): dejalos fuera de la lista, y documentá cada exclusión.
- No mirar: `erroresContrastivos` en B2+ y la clave del idioma aprendido dentro de `contraste`/`erroresContrastivos` (ahí el texto **es** del idioma aprendido por diseño); el texto del idioma aprendido en sí.
- Lista de excepciones explícita (`EXENTAS_R8`), cada una con motivo.
Probalo con el método de los prompts 12 y 14: demostrá que marca el 100 % de lo que corregís, **sin falsos positivos** sobre el resto. Un chequeo que da 0 porque está ciego no sirve.

## Tarea 2 — Corregir el contenido
Con R8 marcando, corregí **todas** las cadenas, en los 5 idiomas de destino:
- Solo se **agregan « »** alrededor del fragmento que ya está (o se pasa `*x*` a `«*x*»`; no se duplica `«*x*»`). No se cambia el texto del fragmento ni el resto de la frase, salvo que la puntuación obligue a un ajuste mínimo (los puntos suspensivos: `«I think»… sobre el dinero`).
- Francés lleva espacio interior por convención: `« I think »`. Alemán, español, italiano y portugués, sin espacio. Seguí lo que ya usa cada idioma en el repo.
- Ante la duda de si una palabra es un fragmento del idioma aprendido o una palabra común del idioma de destino, **no la marques** y listala en el informe.
- Arreglá en la **fuente** (`nucleos/`, `packs/`, `ocurrencias/`, `plantillas/`), no en los compuestos.

## Verificación obligatoria
1. `componer.py` → 606 fichas, 56 semanas especiales, 0 con problemas.
2. `verificar_consistencia.py`: R1-R8 en 0.
3. Script sobre todo el diff: todas las cadenas cambiadas cuelgan de una clave de idioma distinta del aprendido; cada cambio solo **agrega** « » (quitando los « » nuevos, la cadena es idéntica a la anterior, salvo el espacio francés y la puntuación documentada); mismos `*`; sin `ß`; estructura JSON idéntica; ningún archivo cambiado salvo contenido y el verificador. `validar_packs` sin problemas.
4. Barrido independiente, distinto de R8: tomá 40 cadenas al azar de cada idioma de destino de fichas A2/B1 y leelas a ojo; reportá lo que R8 no vio.

## Informe
Conteo real por idioma y campo, excepciones, casos de duda, 15 antes/después, hallazgos del barrido. Actualizá `FALTANTES.md` 14 y `REGLAS-PREVENCION.md` P5 (ahora hay verificación de que existan, no solo de que balanceen).
