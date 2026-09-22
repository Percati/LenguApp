# Qué falta, a 21 de septiembre de 2026

Estado regenerado a partir de los archivos reales del repositorio, no de una foto anterior. Si este archivo y otro documento del proyecto se contradicen, este es el que hay que creer.

## 1. Núcleos (contenido pedagógico fijo, por skill+nivel)

| Idioma | Nivel | Hechos | Total |
|---|---|---|---|
| DE | A2 | 15 | 15 |
| DE | B1 | 32 | 32 |
| DE | B2 | 37 | 37 |
| DE | C1 | 41 | 41 |
| DE | C2 | 27 | 27 |
| EN | A2 | 18 | 18 |
| EN | B1 | 30 | 30 |
| EN | B2 | 34 | 34 |
| EN | C1 | 33 | 33 |
| EN | C2 | 26 | 26 |

**Total: 293/293 — ✅ completo.**

### 1.1 — Contraste multiidioma

✅ Completo: 293/293 núcleos con las 5 claves de app.

### 1.2 — Errores contrastivos

✅ Completo: 293/293 núcleos con `erroresContrastivos` poblado, mismas 5 claves que `contraste`. Aplicado y verificado.

## 2. Packs de vocabulario (2027, calendario de 48 semanas)

✅ Completo: 480/480 packs. Validado con `tools/validar_packs.py`: 0 problemas de continuidad entre apariciones del mismo tema.

## 3. Apariciones (fichas semanales armadas, `ocurrencias/`)

🔴 **0/10 combinaciones — sin empezar.** Es el único bloqueante real para que `tools/componer.py` produzca fichas para Code: núcleos, contraste, errores contrastivos y packs ya están completos para las 10 combinaciones.

## 4. Traducciones (campo `traducciones` en redemittel y vocabulario)

Prácticamente completo. Un solo hueco real, aislado:


- 🔴 **`en` en los 672 ítems de vocabulario de `packs/de-C1-2027-*.json` — 0% traducido.** Es una combinación entera (alemán C1 2027), no casos sueltos.

- 🟡 8 expresiones sueltas de `redemittel` sin traducción al inglés: 3 en `DE-G15-C1`, 2 en `DE-G15-B2`, 3 en `DE-V05-B2`. Volumen bajo, se puede resolver en el mismo lote que lo anterior.

- El resto (es/de/it/fr/pt en ambos campos, y `en` en el resto de las combinaciones de alemán) está completo.

## 5. Audio

✅ Los 1859 archivos que `audio-estado.json` espera están todos presentes en el repo (0 faltantes).


🟡 Hay **47 archivos de audio en el repo que NO están registrados** en `audio-estado.json` — se agregaron directo, sin pasar por el proceso de importación. No es un problema de cobertura (van adelantados, no atrasados), pero conviene correr `importar_audio_maestro.py` con el Excel correspondiente o revisar a mano para que el estado quede sincronizado con la realidad.

## 6. Vocabulario atestiguado alemán

🟡 `data/vocab_de.json` cubre A1–B1 y C1, pero **sigue sin B2 y C2**. Sin chat asignado todavía.

## 7. Piloto 2026 (arquitectura anterior, aún vigente como referencia)

Alemán B2, inglés B2 e inglés C1 tienen su año 2026 (semanas 37-53) completo bajo el esquema viejo. No hace falta tocarlo.
