# Qué falta, a 15 de septiembre de 2026

Estado regenerado a partir de los archivos reales del repositorio, no de una foto anterior. Si este archivo y otro documento del proyecto se contradicen, este es el que hay que creer — se actualiza seguido.

## 1. Núcleos (contenido pedagógico fijo, por skill+nivel)

| Idioma | Nivel | Hechos | Total | Estado |
|---|---|---|---|---|
| DE | A2 | 15 | 15 | ✅ completo |
| DE | B1 | 32 | 32 | ✅ completo |
| DE | B2 | 37 | 37 | ✅ completo |
| DE | C1 | 41 | 41 | ✅ completo |
| DE | C2 | 27 | 27 | ✅ completo |
| EN | A2 | 18 | 18 | ✅ completo |
| EN | B1 | 29 | 29 | ✅ completo |
| EN | B2 | 33 | 33 | ✅ completo |
| EN | C1 | 32 | 32 | ✅ completo |
| EN | C2 | 26 | 26 | ✅ completo |

**Total núcleos: 290/290.**

### 1.1 — Contraste multiidioma dentro de cada núcleo

Cada núcleo tiene un campo `contraste` en forma de diccionario por idioma de app (ES, EN, DE, IT, FR, PT). **Hoy solo está escrita la clave `es` en todos los núcleos existentes.** Las claves `it`, `fr`, `pt` (y `en` cuando el idioma que se aprende no es inglés) son trabajo pendiente completo — es la tarea de la conversación de Contrastes.

### 1.2 — Errores típicos multiidioma

El campo `errores` hoy es una lista plana pensada para hispanohablante. Falta decidir si conviene una lista con matices por idioma de app o un diccionario como `contraste`, y completar los cuatro perfiles restantes — es la tarea de la conversación de Errores Típicos.

## 2. Packs de vocabulario (por tema y aparición, año 2027)

| Idioma | Nivel | Calendario 2027 | Packs 2027 | Estado |
|---|---|---|---|---|
| DE | A2 | 43 semanas | 0 | faltan 43 |
| DE | B1 | 43 semanas | 0 | faltan 43 |
| DE | B2 | 43 semanas | 43 | ✅ completo (prioridad corregida 15-09) |
| DE | C1 | 43 semanas | 0 | faltan 43 |
| DE | C2 | 43 semanas | 0 | faltan 43 |
| EN | A2 | 43 semanas | 0 | faltan 43 |
| EN | B1 | 43 semanas | 0 | faltan 43 |
| EN | B2 | 43 semanas | 43 | ✅ completo (prioridad corregida 15-09) |
| EN | C1 | 43 semanas | 43 | ✅ completo (prioridad corregida 15-09) |
| EN | C2 | 43 semanas | 0 | faltan 43 |

## 3. Apariciones (fichas semanales armadas, año 2027)

| Idioma | Nivel | Estado |
|---|---|---|
| DE | A2 | no empezado |
| DE | B1 | no empezado |
| DE | B2 | no empezado |
| DE | C1 | no empezado |
| DE | C2 | no empezado |
| EN | A2 | no empezado |
| EN | B1 | no empezado |
| EN | B2 | no empezado |
| EN | C1 | no empezado |
| EN | C2 | no empezado |

## 4. Piloto 2026 (arquitectura anterior, aún vigente como referencia)

Alemán B2, inglés B2 e inglés C1 tienen su año 2026 (semanas 37-53) completo bajo el esquema viejo de packs/ocurrencias, migrado con año 2026 explícito para no chocar con 2027. No hace falta tocarlo.

## 5. Traducciones y audio

Ver el Excel maestro más reciente (`traducciones-a-completar.xlsx` y `audio-a-grabar.xlsx`) para el conteo exacto de celdas pendientes — cambian con cada ronda, no tiene sentido fijar un número acá.

## 6. Deuda conocida del piloto 2026 (no bloquea)

`tools/validar_packs.py --anio 2026` marca dos transiciones del piloto fuera del rango 30-80 %, anteriores a esa regla: `de-B2-2026-T01` 1→2 (7 %) y `en-C1-2026-T03` 1→2 (93 %). Se dejan como están salvo decisión explícita.
