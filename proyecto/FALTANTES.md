# Qué falta, a 22 de septiembre de 2026 (actualización 2)

Estado regenerado a partir de los archivos reales del repositorio (parche de Apariciones ya aplicado y verificado en un clon de prueba, no solo leído). Si este archivo y otro documento del proyecto se contradicen, este es el que hay que creer.

## 1. Núcleos — ✅ completo

293/293, con `contraste` y `erroresContrastivos` completos (5 claves de idioma de app cada uno) en las 10 combinaciones.

## 2. Packs 2027 — ✅ completo

480/480. Validado con `tools/validar_packs.py`: 0 problemas de continuidad. Hueco conocido: 8 packs de `en-C1` siguen calcados del alemán (T02-1/3, T07-1/2, T09-1/3, T13-2/4) — pendiente de reescritura, no bloquea.

## 3. Apariciones 2027 — ✅ completo, verificado

| Combo | Apariciones |
|---|---|
| DE A2 | 48/48 |
| DE B1 | 48/48 |
| DE B2 | 48/48 |
| DE C1 | 48/48 |
| DE C2 | 48/48 |
| EN A2 | 48/48 |
| EN B1 | 48/48 |
| EN B2 | 48/48 |
| EN C1 | 48/48 |
| EN C2 | 48/48 |

**Total: 480/480 — verificado componiendo de verdad, no solo leyendo el reporte.**

`tools/validar_apariciones.py` (nuevo, agregado por esta conversación) da 0 errores en las 10 combinaciones; los avisos restantes son todos de audio faltante, cubierto en la sección 5.

## 4. Bloqueantes para Code (dos, ambos de esquema, no de contenido)

### 4.1 — Id de ficha sin año
🔴 `componer.py` ahora falla explícitamente (antes pisaba en silencio) ante 42 colisiones de id entre 2026 y 2027: 14 de `de-B2`, 14 de `en-B2` y 14 de `en-C1`. Confirmado corriendo `componer.py` completo: "480 fichas compuestas, 9 semanas especiales, 42 con problemas".

Requiere tocar `schema/ficha.schema.json` (pattern del id), `tools/componer.py` y `ResolutorSemana.kt` de la app.

### 4.2 — Falta la estructura de traducción para A2/B1 (hallazgo nuevo, 22-09)
🔴 Según `reglas-fichas.md`, en A2 y B1 **toda la prosa de la ficha** debe ser bilingüe (idioma que se aprende + idioma de app, para el switch de la interfaz). Pero de los 14 campos de un núcleo, solo `redemittel` (y `vocabulario` en los packs) tienen un diccionario de traducciones (`{es,en,it,fr,pt}`). Verificado en `DE-G01-A2.json`: `descripcion`, `notas`, `ejemplos`, `errores`, `autochequeo`, `cuadroReferencia` y `promptCorreccion` son texto plano, sin ningún campo donde alojar la traducción.

Mismo problema en las apariciones A2/B1: `subtitulo`, `mision` y `microtareas` tampoco tienen estructura de traducción. Apariciones ya escribió ese texto en frases cortas pensando en esto, pero el campo para guardarlo no existe.

**Alcance real: 95 núcleos (A2+B1 de+en) y 192 apariciones (48 semanas × 4 combinaciones A2/B1).**

Antes de que Traducciones pueda trabajar acá, hace falta decidir la forma del campo (¿cada string se vuelve `{de: "...", es: "...", ...}`? ¿archivo paralelo?) — es una decisión de schema, igual que 4.1. Se recomienda resolver ambas en la misma conversación de Code.

## 5. Audio

✅ **Ejemplos con `"audio": true`: 716/716 grabados** (los 296 que faltaban, grabados el 22-09 e importados desde el Excel de seguimiento). `validar_apariciones.py --anio 2027` ya no da avisos de audio.

🔴 **Los 296 `.wav` nuevos no están en el repo todavía.** `audio-estado.json` registra 2202 archivos y `app/src/main/assets/audio/` tiene 1906. Hay que copiarlos a `assets/audio/DE/` y `assets/audio/EN/` y commitearlos; `sincronizar_audio_estado.py --dry-run` lista los que falten (tiene que dar 0).

🟡 **Peso:** los audios son WAV PCM 16 bit, 22 kHz mono: 139 MB con 1906 archivos, ~160 MB con los nuevos. La decisión tomada es comprimir a 24-32 kbps (del orden de 20 MB en total), pendiente de definir formato y si se renombran las extensiones en el estado.

🟡 **Duplicados por nivel:** 56 textos tienen una segunda grabación de otro nivel (`otrosArchivos`: 47 viejos B2/C1 y 9 nuevos A2/B1). La app reproduce por texto, así que sobran; sin decidir si se borran.

🟡 **Expresiones y vocabulario sin grabar: 4166 textos únicos** (4807 filas; 641 son el mismo texto en otro nivel y se marcan "no grabar: se graba en A2", para no crear más duplicados por nivel). Excel entregado a Fer el 22-09 para generarlos.

`audio-a-grabar.xlsx` es el **Excel de seguimiento**: se regenera con `exportar_audio_maestro.py --anio 2027` (todo) o `--pendientes-tipos Ejemplo` (solo ejemplos pendientes) y conserva la marca con fecha de cada grabación (campo `marca` de `audio-estado.json`).

## 6. Traducciones

🟡 Packs de `de-C1-2027`: 672 ítems de vocabulario sin la clave `en`, más 8 expresiones sueltas de `redemittel` (`DE-G15-B2`, `DE-G15-C1`, `DE-V05-B2`). El resto (es/de/it/fr/pt en ambos campos, y `en` en el resto de alemán) está completo. No bloquea composición, solo deja esa clave vacía en el JSON final.

## 7. Sin dueño todavía

- **Semanas de repaso 2027** (S10, S22, S34, S46): no existe contenido en ninguna combinación. No es de Apariciones — nadie definió el formato.

## 8. Piloto 2026

Alemán B2, inglés B2 e inglés C1 siguen completos bajo el esquema viejo. No tocar hasta que se resuelva el id con año (sección 4).
