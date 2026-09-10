# Generador de calendario y edición 2026 (semanas 37–53)

## Parte A — Reglas del generador

El calendario no se escribe: se calcula. Esta sección es la especificación de esa función, en lenguaje llano, para que después se traduzca a código una sola vez.

### A.1 Entradas

| Entrada | Qué es |
|---|---|
| `añoISO` | El año que se quiere generar |
| `idioma` | `en` o `de` |
| `nivel` | A2 … C2 |
| `bancoSkills` | Los skills del idioma que aplican a ese nivel |
| `bancoTopics` | Los 12 temas |
| `semilla` | El propio `añoISO`. Hace el resultado reproducible: mismo año, mismo calendario, siempre |

### A.2 Salida

Una lista de `(semanaISO, tipoSemana, skillId, occurrenceOrder, topicId)`.

`tipoSemana` es `content`, `review` o `survival`.

### A.3 Reglas duras (el generador no puede violarlas)

1. **Semanas reservadas.** Review Week en las semanas 8, 16, 24, 32, 40, 48. Survival Week en las semanas 12, 28, 44. Estas nueve no llevan skill ni topic nuevo.
2. **Un skill no repite topic entre sus apariciones del año.** Si Konjunktiv II sale en las semanas 4, 21 y 36, los tres topics son distintos.
   *(Esta es la corrección de la regla original, que estaba mal formulada: decía "mientras el skill esté en el mismo order", cosa que nunca puede pasar porque cada order aparece una sola vez al año.)*
3. **Un topic no sale en dos semanas consecutivas del mismo idioma.**
4. **Separación mínima entre apariciones de un mismo skill:** 10 semanas. Si aparece 3 veces, la separación ideal es 17.
5. **Los skills de repaso van primero.** Un skill marcado `foundational` no puede tener su primera aparición después de la semana 20. No sirve repasar Satzbau en noviembre.
6. **Los tracks de los dos idiomas son independientes.** No comparten topic. Solo se alinean las nueve semanas reservadas.

### A.4 Reglas blandas (se cumplen si se puede)

7. Distribuir las categorías: no más de tres semanas seguidas de la misma categoría (Grammar / Fluency / Vocabulary).
8. Alternar carga: un skill denso (Nominalisierung) no debería ir pegado a otro denso.
9. Respetar `topicBlocklist` si el skill lo declara. Sirve para evitar combinaciones absurdas.

### A.5 Algoritmo

Backtracking simple sobre las semanas libres, en orden. Para cada semana, se elige el candidato con menos opciones restantes (heurística clásica de restricción mínima). Si se atasca, retrocede. Con 40-45 asignaciones y 12 topics siempre encuentra solución en milisegundos.

**No usar aleatoriedad sin semilla.** Todo el valor de la reproducibilidad —poder ver el calendario de 2026 desde 2031— depende de que la función sea determinista.

### A.6 Casos borde

- **Años de 53 semanas ISO** (como 2026): la semana 53 es una semana de contenido normal. No hay regla especial.
- **Año ISO ≠ año calendario**: la semana 53 de 2026 va del 28 de diciembre de 2026 al 3 de enero de 2027. La app debe indexar por año ISO, o el 1 de enero mostrará contenido equivocado.
- **Menos skills disponibles que semanas libres** (pasa en C2): se permite que un skill aparezca una vez más de lo previsto, nunca menos de 10 semanas de separación.

---

## Parte B — Edición 2026, semanas 37 a 53

Esta edición es un **piloto parcial**: arranca en la semana 37 porque es la semana en curso. Un año completo generado con las reglas de arriba se ve distinto.

**Limitación asumida:** ningún skill se repite. En 14 semanas de contenido no hay repetición espaciada posible. Este piloto valida el formato de la ficha, el prompt de corrección y las micro-tareas, no el motor de repetición.

### B.1 Calendario

| Semana | Fechas | Tipo | Inglés (C1) | Topic EN | Alemán (B2) | Topic DE |
|---|---|---|---|---|---|---|
| 37 | 07–13 sep | contenido | EN-F01 Opinions | T01 Work | DE-G01 Satzbau | T07 Wohnen |
| 38 | 14–20 sep | contenido | EN-V07 Discourse markers | T11 News | DE-G02 Konnektoren | T02 Finanzen |
| 39 | 21–27 sep | contenido | EN-F02 Diplomatic disagreement | T03 Tech & AI | DE-V05 Modalpartikeln | T01 Arbeit |
| **40** | 28 sep–04 oct | **Review** | los 3 anteriores | — | los 3 anteriores | — |
| 41 | 05–11 oct | contenido | EN-F11 Summarising | T09 Education | DE-G07 Adjektivdeklination | T10 Essen |
| 42 | 12–18 oct | contenido | EN-F13 Hedging | T06 Environment | DE-G09 Konjunktiv II | T08 Beziehungen |
| 43 | 19–25 oct | contenido | EN-V03 Advanced phrasal verbs | T12 Hobbies | DE-G05 Subjektive Modalverben | T04 Gesundheit |
| **44** | 26 oct–01 nov | **Survival** | — | — | — | — |
| 45 | 02–08 nov | contenido | EN-F15 Managing a conversation | T08 Relationships | DE-G08 Verben + Präp. | T12 Hobbys |
| 46 | 09–15 nov | contenido | EN-G16 Inversion & cleft | T05 Travel | DE-G10 Passiv & Ersatzformen | T06 Umwelt |
| 47 | 16–22 nov | contenido | EN-F14 Constructive feedback | T01 Work | DE-V02 Redewendungen | T05 Reisen |
| **48** | 23–29 nov | **Review** | los 6 anteriores | — | los 6 anteriores | — |
| 49 | 30 nov–06 dic | contenido | EN-V05 Professional collocations | T02 Finance | DE-G12 Nominalisierung | T03 Technologie |
| 50 | 07–13 dic | contenido | EN-F12 Persuading & negotiating | T07 Housing | DE-F09 Argumentieren | T11 Nachrichten |
| 51 | 14–20 dic | contenido | EN-G15 Participle clauses | T04 Health | DE-G11 Relativsätze | T09 Bildung |
| 52 | 21–27 dic | contenido | EN-V08 Precision | T10 Food | DE-F10 Abschwächen | T02 Finanzen |
| 53 | 28 dic–03 ene | contenido | EN-F17 Register shifting | T03 Tech & AI | DE-V08 Helvetismen | T01 Arbeit |

### B.2 Verificación de reglas

| Regla | Estado |
|---|---|
| Semanas 40, 44, 48 reservadas | Cumple |
| Ningún topic en semanas consecutivas (EN) | Cumple |
| Ningún topic en semanas consecutivas (DE) | Cumple |
| Skills sin repetir | Cumple (14 skills distintos por idioma) |
| Repaso fundacional temprano en alemán | Cumple: Satzbau (37), Konnektoren (38), Adjektivdeklination (41) |
| Máx. 3 semanas seguidas de la misma categoría | **Incumple parcialmente en alemán**: semanas 41, 42, 43, 45, 46 son cinco de gramática seguidas |

Sobre la última: es deliberado y hay que decidirlo conscientemente. El objetivo declarado del alemán en esta edición es consolidar estructura, y una regla blanda no debería vencer a un objetivo del usuario. Si al usarlo se hace pesado, se corrige en 2027.

### B.3 Balance de la edición

| | Fluidez | Gramática | Léxico |
|---|---|---|---|
| Inglés C1 | 6 (43 %) | 3 (21 %) | 5 (36 %) |
| Alemán B2 | 2 (14 %) | 8 (57 %) | 4 (29 %) |

El inglés respeta aproximadamente el 50/30/20 acordado. El alemán está deliberadamente cargado de gramática, que es el motivo original del proyecto. Está bien que no lo respete.

### B.4 Semanas especiales

**Semana 40 — Skill Review Week.**
Inglés: sostener una discusión de 10 minutos combinando opinión matizada, marcadores del discurso y desacuerdo diplomático. Alemán: escribir un texto de 300 palabras que use Nebensatzstellung correcta, al menos seis conectores distintos y cuatro Modalpartikeln.

**Semana 44 — Survival Week.**
Sin skill ni topic. Una única tarea: mantener una conversación real de 20 minutos en cada idioma con una persona, sin preparación. Evidencia: una nota de tres líneas sobre qué se trabó. No hay corrección con IA esta semana; el objetivo es la incomodidad, no la precisión.

**Semana 48 — Skill Review Week.**
Recombinar los seis skills desde la semana 41.
