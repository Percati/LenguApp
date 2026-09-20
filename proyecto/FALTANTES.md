# Qué falta, a 18 de septiembre de 2026

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
| EN | B1 | 30 | 30 | ✅ completo |
| EN | B2 | 34 | 34 | ✅ completo |
| EN | C1 | 33 | 33 | ✅ completo |
| EN | C2 | 26 | 26 | ✅ completo |

**Total núcleos: 293/293.**

El 18-09-2026 se agregó el skill `EN-G08` (Prepositional verbs and dependent prepositions) en B1, B2 y C1: el banco de inglés solo cubría verb + particle (phrasal verbs, `EN-V02`/`EN-V03`) y verb + noun (`EN-V04`/`EN-V05`), y no tenía nada para los verbos preposicionales. En alemán la combinación ya estaba cubierta por `DE-G08` y `DE-G26`, así que no se tocó.

### 1.1 — Contraste multiidioma dentro de cada núcleo

Cada núcleo tiene un campo `contraste` en forma de diccionario por idioma de app (ES, EN, DE, IT, FR, PT). **Hoy solo está escrita la clave `es` en todos los núcleos existentes.** Las claves `it`, `fr`, `pt` (y `en` cuando el idioma que se aprende no es inglés) son trabajo pendiente completo — es la tarea de la conversación de Contrastes.

### 1.2 — Errores típicos multiidioma

El campo `errores` hoy es una lista plana pensada para hispanohablante. Falta decidir si conviene una lista con matices por idioma de app o un diccionario como `contraste`, y completar los cuatro perfiles restantes — es la tarea de la conversación de Errores Típicos.

## Vocabulario atestiguado por nivel

| Lista | Lemas | Niveles | Estado |
|---|---|---|---|
| `data/vocab_en.json` | 10.284 | A1–C2 | completa |
| `data/vocab_de.json` | 4.959 | A1–B1, C1 | **faltan B2 y C2** |

El Goethe solo publica lista oficial gratuita hasta B1; por encima no existe
equivalente. El 20-09-2026 se cubrió C1 con el Lernwortschatz de tres manuales
(`sicher-c1`, `aspekte-c1`, `erkundungen-c1`, +1.716 lemas), pasados por OCR y
filtrados contra un diccionario de frecuencia que actúa de lista blanca. Ver
`tools/fuentes.json`.

Pendiente: **B2 y C2 siguen sin lista**. Para B2 sirve el Lernwortschatz de
*Sicher B2.1/B2.2* o *Erkundungen B2*; para C2, *Erkundungen C2*. El
*Goethe-Zertifikat B2 Prüfungsziele* NO trae lista de palabras: es la
especificación del examen y sirve para validar skills, no vocabulario.

## 2. Packs de vocabulario (por tema y aparición, año 2027)

**Cambio del 18-09-2026:** el año pasó de 43 a 48 semanas de contenido (el Survival
dejó de ocupar semanas enteras y los repasos bajaron de 6 a 4), así que los tres
pares que figuraban como completos con 43 packs ya no lo están. Además la
redistribución de temas dejó packs huérfanos: su vocabulario sigue siendo válido,
solo hay que renumerarlos al `-n` que ahora corresponde. Faltan 377 packs en total
para 2027 (antes 301).

| Idioma | Nivel | Calendario 2027 | Packs 2027 | Estado |
|---|---|---|---|---|
| DE | A2 | 48 semanas | 0 | faltan 48 |
| DE | B1 | 48 semanas | 0 | faltan 48 |
| DE | B2 | 48 semanas | 33 útiles | faltan 15, hay 10 huérfanos renumerables |
| DE | C1 | 48 semanas | 0 | faltan 48 |
| DE | C2 | 48 semanas | 0 | faltan 48 |
| EN | A2 | 48 semanas | 0 | faltan 48 |
| EN | B1 | 48 semanas | 0 | faltan 48 |
| EN | B2 | 48 semanas | 35 útiles | faltan 13, hay 8 huérfanos renumerables |
| EN | C1 | 48 semanas | 35 útiles | faltan 13, hay 8 huérfanos renumerables |
| EN | C2 | 48 semanas | 0 | faltan 48 |

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
