# Qué falta, a 22 de septiembre de 2026 (actualización 3)

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

## 4. Esquema — ✅ todo resuelto

Id con año, estructura bilingüe de los 13 campos de prosa, `titulo` y
`redemittel[].funcion` (commits `b64da7f`, `238fdf1`, `e1a1d4f`): todos
cerrados. Verificado componiendo las tres capas reales: "522 fichas
compuestas, 9 semanas especiales, 0 con problemas".

`validar_apariciones.py` ya no tiene el chequeo muerto de colisión entre
años — lo retiró Code de paso.

### 4.7 — `minLength` de los campos bilingües y marcado con asteriscos (Code, sept 2026)

✅ **`minLength`:** en los campos bilingües la rama objeto `{idioma: texto}` ya no
lleva `minLength` (solo `maxLength`, igual para todas las claves). El mínimo rige
únicamente sobre la clave del idioma que se aprende (`de` en fichas alemanas,
`en` en fichas inglesas), vía dos condicionales en `allOf`. Una traducción corta
al es/en/fr/it/pt ya no rompe la ficha.

🔴 **Para Traducciones:** hay que **revertir a su redacción original las 66 celdas
que se alargaron como parche temporal en el commit `29875fa`** — el schema ya no
las necesita alargadas. `maxLength` sigue aplicando a todas las claves.

✅ **Marcado `*cita*` / `**destaque**`:** el renderizador ya lo interpretaba en
`mision.consigna`, `mision.requisitos` y `microtareas[].texto`. Le faltaba
`subtitulo`: corregido (ahora se ve en cursiva/negrita, no como asteriscos
literales; en el widget se quita el marcado porque Glance no admite texto con
estilo). Por eso **no hace falta sacar** los 5 subtítulos ingleses con asteriscos.
`titulo` de la ficha NO interpreta marcado (no se pidió): no usar asteriscos ahí.

🟡 **Aviso para la app:** los modelos Kotlin (`Ficha.subtitulo`, etc.) siguen
siendo `String`. Cuando A2/B1 se embeba con campos `{idioma: texto}`, hay que
extender `ContenidoSemanal.kt` y el renderizador para resolver el idioma.
Hoy no rompe nada porque el contenido embebido es solo el piloto 2026 (B2/C1).

### 4.8 — 🔴 Modelos Kotlin no leen `{idioma: texto}` (bloqueante real para embeber A2/B1)
`Ficha.subtitulo` y el resto de los 13 campos bilingües siguen tipados como
`String` en Kotlin. Hoy no rompe nada porque solo el piloto 2026 (sin
traducir) está embebido en la app. Pero es un bloqueante real: antes de
meter cualquier ficha A2/B1 traducida en `app/src/main/assets/contenido/`,
alguien tiene que extender los modelos para que sepan resolver el objeto
por idioma. Asignado a Code, sin empezar.

**Nota de proceso:** la memoria de proyecto de Claude (`reglas-fichas.md`,
etc.) no es visible para Code — si una regla de ahí es relevante para una
tarea de Code, hay que copiarla en texto plano dentro del prompt, no asumir
que la va a leer sola.

## 5. Audio — 🟡 grabado y convertido, falta subir 4462 al repo

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

## 6. Traducciones — 🟡 en curso, la hace Fer a mano

**21 595 celdas pendientes** (13 campos de prosa de 95 núcleos + 192
apariciones, ya con `titulo` y `funcion` incluidos; el hueco viejo de
`de-C1` ya se corrigió). Excel entregado, ETA 1-2 días. El importador ya
está listo y probado de punta a punta (round-trip: 522/9/0 sin problemas).

## 7. Vocabulario atestiguado alemán — B2 resuelto, falta C2

`vocab_de.json` cubre A1/A2/B1/**B2**/C1: 7607 lemas. B2 entró en sept-2026 a
partir de seis manuales pasados por OCR (Sicher! B2.1 y B2.2, Aspekte neu B2,
Erkundungen B2, Einfach besser! 500, Deutsch intensiv B2). Dos consecuencias
que conviene tener presentes:

- 426 lemas que figuraban como C1 pasaron a B2, porque el nivel de enseñanza
  es el más bajo atestiguado y ahora aparecen también en manuales B2. C1 baja
  de 1716 a 1290 lemas; no se perdió nada, solo se reetiquetó.
- La construcción de B2 se hizo **sin el diccionario de frecuencia** como lista
  blanca (`fuentes/A_Frequency_Dictionary_of_German.pdf` no estaba disponible),
  así que hay ruido de OCR entre los 4085 lemas B2 extraídos: formas flexionadas
  (*gestanden*, *hergerissen*) y palabras muy básicas. Cuando se reconstruya con
  la lista blanca presente, el recuento B2 va a bajar y quedar más limpio.

Falta **C2**, y ahí no hay salida conocida: ningún instituto publica lista por
encima de B2 y tampoco hay manual C2 con Lernwortschatz conseguido. Los packs
de alemán C2 quedan sin verificación externa.

## 8. Sin dueño todavía

- **Semanas de repaso 2027** (S10, S22, S34, S46): no existe contenido en ninguna combinación. No es de Apariciones — nadie definió el formato.

## 9. Piloto 2026

Alemán B2, inglés B2 e inglés C1 siguen completos bajo el esquema viejo. No tocar hasta que se resuelva el id con año (sección 4).
