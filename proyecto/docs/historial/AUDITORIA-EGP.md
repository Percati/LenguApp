# Auditoría de los skills de gramática inglesa contra el English Grammar Profile

Fuente: EGP, **1.222 estructuras** (versión completa) con nivel MCER, categoría, guideword, can-do statement y ejemplos del Cambridge Learner Corpus. Cierra la deuda de verificación de nivel del banco de inglés.

**Método.** El EGP da el nivel en que una estructura se **adquiere**. Nuestro banco declara los niveles en que se **practica**, y practicar por encima del nivel de adquisición es correcto (consolidación). El error real es declarar un nivel **por debajo** del de adquisición.

> **Revisión del 9/9/2026.** La primera auditoría se hizo sobre un archivo **truncado a 999 entradas**: el conversor de conocimiento de proyecto corta los `.xlsx` en 1.000 filas por hoja, y el corte cayó en `PRONOUNS`. Faltaban tres supercategorías enteras: **QUESTIONS, REPORTED SPEECH y VERBS**. Rehecha sobre las 1.222 reales (subidas como CSV, que no se truncan). Los resultados de abajo son los definitivos.

**Advertencia metodológica.** El script `tools/audit_skills_egp.py` hace una primera pasada por palabras clave y dio "0 problemas". Ese resultado es engañoso: al buscar `conditional` trae también las entradas A2 de condicionales básicos, y el mínimo se hunde. **Los cuatro hallazgos reales salieron de consultas dirigidas a la estructura canónica de cada skill, no de la pasada automática.** El script sirve como red de seguridad, no como veredicto.

---

## 1. Correcciones que hay que aplicar

### EN-G07 · Third conditional — declarado **B2**, el EGP lo pone en **B1**

Dos entradas B1 lo cubren: la forma `'IF' + PAST PERFECT + 'WOULD HAVE' + '-ED'` para situación imaginada y arrepentimiento (CLAUSES), y el uso del past perfect tras cláusulas con *if* (PAST). No hay ninguna entrada B2.

→ **Cambiar a `B1 B2`.** El usuario es C1, así que no le afecta; afecta a las fichas B1 cuando se escriban.

### EN-G08 · Mixed conditionals — **no existe en el EGP** · CONFIRMADO

Cero coincidencias con `mixed conditional` en las **1.222** estructuras completas. La duda que quedaba tras el truncamiento está resuelta: no aparece tampoco en las categorías que faltaban. Sí hay 35 entradas de condicionales repartidas de A2 a C2, pero ninguna define el condicional mixto como categoría. No es un olvido del EGP: es que "condicional mixto" es una categoría de manual de texto, no una *criterial feature* atestiguada en corpus. El EGP describe lo que los examinandos producen, y aparentemente nadie produce esto como categoría separada.

→ **RESUELTO: eliminado.** El skill se retira del banco de inglés, que pasa de 47 a 46. Su contenido se absorbe en EN-G07 (Third conditional), cuya tercera aparición puede tratar combinaciones de tiempos. Motivo: un skill sin ancla empírica en un documento que presume de auditable es una grieta.

### EN-G15 · Reduced relative / participle clauses — declarado **C1 C2**, empieza antes

Distribución EGP: A2 (1), B1 (4), B2 (4), C1 (5), C2 (4). Las cláusulas no finitas con `-ing` ya son B1; las de comparación con `than` son B2. Lo genuinamente C1/C2 es solo una parte.

→ **Cambiar a `B2 C1 C2`.**

### EN-G20 · Unreal past (*I wish*, *if only*) — declarado **B2 C1**, el EGP dice **B2 y C2**

Solo dos entradas: `AFTER 'IF ONLY' AND 'WISH', IMAGINED PAST` en B2, y `AFTER 'IF ONLY', IMAGINED PAST` en C2. No hay nada en C1.

→ **Cambiar a `B2 C2`.**

---

## 2. Confirmados sin cambios (16 de 20)

EN-G01, G02, G03, G04, G05, G06, G09, G10, G11, G12, G13, G14, G16, G17, G18, G19.

### Corrección de la primera versión: dos skills quedaban cortos por arriba

Con las categorías VERBS y ADVERBS completas aparece material que antes no se veía:

- **EN-G17 Gerunds and infinitives** — declarado `B1 B2`. El EGP tiene 31 entradas, de A1 a C2, con **2 en C1 y 5 en C2**. → **Extender a `B1 B2 C1`**: hay material de nivel del usuario que hoy le queda inaccesible.
- **EN-G19 Word order and adverb placement** — declarado `B1 B2`. 34 entradas, con **4 en C1 y 4 en C2**. → **Extender a `B1 B2 C1`**, por el mismo motivo.
- **EN-G13 Reported speech** — declarado `B1 B2`. Ahora sí hay categoría propia: 17 entradas, A2 (2), B1 (8), B2 (7). Las de A2 son solo desplazamiento de pronombre con *say* y *tell*; lo sustantivo (desplazamiento de tiempo, preguntas indirectas) es B1. **La declaración se mantiene.**

Casos que merecen una nota:

- **EN-G12 Impersonal/advanced passive (C1)**: solo 7 entradas coincidentes, la más baja en B2. La asignación C1 es correcta pero el skill es estrecho; el EGP tiene poco material ahí.
- **EN-G16 Inversion/cleft/fronting (C1/C2)**: la masa está en C1 (11) y B2 (7), pero el *fronting* de sintagmas preposicionales ya es A2 y los clefts con *it* son B1. La asignación C1/C2 es correcta para las formas marcadas, que es lo que la ficha practica.
- **EN-G18 Articles and quantifiers**: 126 coincidencias, la categoría más poblada del EGP. Confirma que es el terreno donde más se distingue nivel a nivel.

---

## 3. Lo que esto no cubre

El EGP es gramática. Los **18 skills de fluidez** y los **9 de léxico** siguen sin ancla verificable: hoy dicen "MCER" o "EVP" sin número de escala. Se anclan con el Companion Volume, que ya está subido.
