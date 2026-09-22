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

## 5. Audio — ✅ contenido completo

✅ **7348/7348 filas del Excel grabadas** (6312 textos distintos). `audio-estado.json` registra **6368 archivos: 3298 EN y 3070 DE**. `validar_apariciones.py --anio 2027` no da avisos de audio.

✅ **Formato final decidido (sept 2026): Opus, 24 kbps, mono, contenedor `.ogg`.** Opus rinde muy por encima de AAC y MP3 en voz a bitrates bajos, y Android y ExoPlayer lo soportan sin librerías extra; `.ogg` porque la extensión `.opus` recién anda desde API 29. Medido sobre los 1906 archivos del repo: **134 MB de WAV → 9,0 MB de Opus** (−93 %). Los 6368 completos quedan en unos 30 MB.

🔴 **La conversión la tiene que correr Fer sobre su carpeta local**, que es la única que tiene los 6368 archivos (el repo solo tiene 1906):

```sh
python3 proyecto/tools/convertir_audio.py --audio app/src/main/assets/audio \
    --estado proyecto/audio-estado.json --borrar-wav
```

Es idempotente y salta lo ya convertido. Los nombres en `audio-estado.json` ya apuntan a `.ogg` en este parche, así que al correrlo con `--estado` no va a cambiar nada más.

🔴 **Faltan 4462 archivos en el repo.** El estado registra 6368 y `assets/audio/` tiene 1906 commiteados; el resto existe solo en la copia local de Fer. Sin ellos, un clon del repo no compila la app completa y no hay respaldo. `sincronizar_audio_estado.py --dry-run` los lista. Ya comprimidos son ~30 MB en total, así que subirlos al repo dejó de ser un problema de peso.

✅ **Duplicados por nivel:** se conservan (decisión de Fer, sept 2026). 56 textos con una segunda grabación en otro nivel, campo `otrosArchivos`.

🟡 **`tools/generar_audio.py` está desfasado del pipeline real:** nombra los archivos por hash del texto (`voz_velocidad_hash.ogg`), no con la convención `IDIOMA_NIVEL_palabras_origen.ogg` que usan los 6368 archivos reales, y espera fichas compiladas en `build/`. Se actualizó su salida a Opus/`.ogg`, pero hoy no lo usa nadie: hay que decidir si se alinea con la convención o se retira.

`audio-a-grabar.xlsx` es el **Excel de seguimiento**: se regenera con `exportar_audio_maestro.py --anio 2027`.

## 6. Traducciones

🟡 Packs de `de-C1-2027`: 672 ítems de vocabulario sin la clave `en`, más 8 expresiones sueltas de `redemittel` (`DE-G15-B2`, `DE-G15-C1`, `DE-V05-B2`). El resto (es/de/it/fr/pt en ambos campos, y `en` en el resto de alemán) está completo. No bloquea composición, solo deja esa clave vacía en el JSON final.

## 7. Sin dueño todavía

- **Semanas de repaso 2027** (S10, S22, S34, S46): no existe contenido en ninguna combinación. No es de Apariciones — nadie definió el formato.

## 8. Piloto 2026

Alemán B2, inglés B2 e inglés C1 siguen completos bajo el esquema viejo. No tocar hasta que se resuelva el id con año (sección 4).
