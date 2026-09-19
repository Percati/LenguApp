# Plan de estudios — App de retos lingüísticos

**Edición 2.4 — septiembre de 2026**
Documento auditable. Fuente única del contenido pedagógico de la aplicación.

En este documento intervienen cuatro roles: el **usuario** (quien practica), el **diseñador** (quien fija skills, niveles y anclajes), el **revisor** (hablante nativo o docente que valida) y el **programador** (quien implementa la aplicación). No se usan nombres propios.

---

## 0. Cómo leer este documento

Este documento cumple la misma función que el programa de una materia universitaria: declara qué se enseña, en qué nivel, con qué criterio se asignó ese nivel y con qué vocabulario se trabaja. No contiene las clases (las fichas semanales) sino el plan.

Está pensado para tres lectores distintos:

| Lector | Qué debería mirar |
|---|---|
| **El usuario** | Secciones 3, 4, 5 y 6: qué se practica y en qué orden |
| **El revisor** (nativo o docente) | Secciones 1, 4, 5 y 7: si las asignaciones de nivel y el vocabulario son correctos |
| **El programador** | Secciones 2 y 3: estructura de datos, tipos de reto y reglas de generación |
| **El contribuyente** | Secciones 2 y 8: cómo está estructurado el contenido y bajo qué licencia |

**Cómo se genera.** Este documento y el JSON que consume la aplicación se compilan del mismo archivo fuente en Markdown. No se redactan por separado, de modo que no pueden discrepar.

**Qué NO es.** No es un curso. No reemplaza clases ni un método. Es un sistema de práctica deliberada, dirigido a usuarios que ya han estudiado la teoría y necesitan forzarse a emplearla de forma activa.

---

## 1. Fuentes de referencia y método de asignación de nivel

La pregunta que este documento tiene que poder responder es: *¿por qué "Hedging" está en B2 y no en C1?* Si la respuesta fuera un criterio no declarado del diseñador, el documento no sería auditable. Cada asignación de nivel se ancla a una fuente pública.

### 1.1 Inglés

**English Grammar Profile (EGP)**, del programa English Profile de Cambridge. Es la referencia principal. Su lógica no es prescriptiva sino empírica: identifica *criterial features*, estructuras que distinguen un nivel del anterior porque aparecen usadas correctamente a partir de ahí y no antes.

Detalles relevantes del método del EGP:

- Se construyó sobre el **Cambridge Learner Corpus**, un corpus de más de 55 millones de palabras de textos producidos por examinandos reales, con más de 140 lenguas maternas representadas.
- Contiene más de **1.200 descriptores** de estructuras gramaticales, cada uno mapeado al nivel MCER en que se considera adquirido.
- El criterio de adquisición incluye un umbral de uso correcto (se elevó al 60% de precisión para compensar el efecto de la lengua materna).
- Los descriptores son de dos tipos: **form-based** (gramaticales) y **use-based** (pragmáticos). Esta distinción es importante para nosotros: justifica que un skill como "Hedging" tenga nivel asignado aunque no sea una estructura gramatical.

Ejemplo del propio EGP: las preguntas indirectas (*can you tell me…?*) son criterial feature de B1, porque un A2 típicamente no las produce con corrección y un B1 sí.

**English Vocabulary Profile (EVP)**, del mismo programa, para el nivel de los ítems léxicos.

### 1.2 Alemán

**Profile deutsch** (Glaboniat, Müller, Rusch et al., Langenscheidt, versión 2.0 de 2005), la implementación oficial del MCER para alemán como lengua extranjera. Es la referencia que citan los propios manuales de objetivos de examen del Goethe-Institut.

**Hallazgo metodológico importante, y que cambia el diseño del banco alemán:**

Profile deutsch describe la gramática alemana solo hasta B2. La razón declarada es que más allá de B2 los medios lingüísticos ya no son determinables: hay demasiadas situaciones, variedades y léxicos especializados posibles según el ámbito de cada usuario. La primera edición de Profile deutsch llegaba solo hasta B2 por ese motivo; en la segunda se integró un diccionario para cubrir C1 y C2.

El supuesto explícito de la obra es que **el aprendiente conoce todos los fenómenos gramaticales al llegar a B2**, y que C1 y C2 tratan del *uso activo* de esos medios, no de estructuras nuevas.

Consecuencia directa para nuestro banco: **el alemán C1 y C2 no lleva gramática nueva.** Lleva registro, pragmática, estilística, precisión léxica y variación. Cualquier banco de alemán C1 que ofrezca "estructuras avanzadas nuevas" está inventando. Esto también reduce el riesgo que señalamos antes sobre contenido C1/C2 no validable: hay menos que inventar de lo que parecía.

**Goethe-Zertifikat, manuales de Prüfungsziele / Testbeschreibung** (A1 a C2), para los inventarios de objetivos y el vocabulario temático.

**Hallazgos verificados de primera mano (septiembre 2026):**

1. El manual gratuito del **Goethe B2** no contiene la lista de estructuras: remite explícitamente al CD-ROM de Profile deutsch. El manual del **C1** va más lejos y declara que **no existen inventarios de vocabulario ni de gramática para ese nivel**, porque se trabaja con textos auténticos y se espera que el aprendiente deduzca por formación de palabras.
2. Los manuales de **Cambridge B2 First, C1 Advanced y C2 Proficiency no traen lista de vocabulario**: cero menciones en 264 páginas. Solo el de B1 Preliminary la tiene.
3. **Dos organismos independientes, en dos idiomas, llegan a la misma conclusión.** Que no haya inventario por encima de B1 deja de ser una limitación de nuestras fuentes y pasa a ser un dato sobre cómo funciona el nivel avanzado. Refuerza que en C1/C2 el skill sea formación de palabras y estrategia, no listas.
4. El ejemplar de **Profile deutsch** conseguido es el manual del libro, no el CD-ROM: contiene el índice de la gramática sistemática pero **sin etiquetas de nivel**. No sirve para cerrar la deuda de las asignaciones alemanas C1/C2.

### 1.3 Ambas lenguas

**Corpus de lectura graduada** (20 historias inglesas escritas a tres niveles), como confirmación cruzada receptiva. Ver `AUDITORIA-LECTURA.md`: confirma seis asignaciones y corrigió dos.

**Marco Común Europeo de Referencia (MCER/GER)**, y en particular el *Companion Volume*, para los descriptores funcionales: los skills de fluidez (opinar, discrepar, negociar, mediar) se anclan a escalas del MCER, no a gramática.

### 1.4 Cómo se lee la columna "Ancla" de las tablas

- `EGP` — estructura registrada en el English Grammar Profile en ese nivel.
- `EGP-use` — descriptor de tipo pragmático del EGP.
- `MCER` — escala funcional del Marco, no estructura gramatical.
- `PD` — Profile deutsch.
- `PD>B2` — no cubierto por Profile deutsch por diseño (C1/C2 alemán): asignación por criterio propio, **marcado explícitamente como deuda de validación**.
- `Goethe` — inventario de objetivos de examen del Goethe-Institut.

### 1.5 Honestidad sobre los límites de este anclaje

Tres advertencias que un revisor debe tener presentes:

1. **El EGP y Profile deutsch son obras de acceso restringido o de pago.** Las asignaciones de este documento se apoyan en su metodología pública y en la literatura derivada, no en una copia verificada línea por línea de sus bases de datos. Todo revisor con acceso directo debería corregir lo que encuentre.
2. **El MCER describe niveles de desempeño, no un temario.** Cualquier "lista de gramática por nivel" es una interpretación. La de este documento también.
3. **Las asignaciones marcadas `PD>B2` son las más débiles.** Constituyen criterio del diseñador, no fuente. Están señaladas para que el revisor las examine primero.

---

## 2. Arquitectura de contenido

### 2.1 Los cinco objetos

| Objeto | Qué es | Cuántos |
|---|---|---|
| **Skill** | Tema gramatical, funcional, léxico o combinatorio | 53 en inglés, 58 en alemán |
| **Topic** | Tema de conversación | 14, compartidos entre idiomas |
| **VocabPack** | Vocabulario de un Topic en un idioma y nivel | hasta 140 |
| **Occurrence** | Una aparición concreta de un Skill en un nivel, con su profundidad | variable |
| **Week** | Asignación calculada de (Skill, Topic, Occurrence) a una semana ISO | 52-53 por año |

Regla de oro: **los cuatro primeros son permanentes, el quinto se calcula.** El calendario no se escribe a mano, se deriva. Por eso no hay mantenimiento anual obligatorio.

### 2.2 Un banco por idioma, no uno por nivel

Decisión tomada: existe un único banco de Skills por idioma. Cada Skill declara en qué niveles aplica. El nivel del usuario filtra qué Skills ve y ajusta tres cosas:

1. **La profundidad de la ocurrencia** (subtítulo, foco).
2. **El pack de vocabulario** del Topic asociado.
3. **El tipo de reto y la evidencia** exigida.

Lo que *no* cambia es el Skill. Konjunktiv II es Konjunktiv II en B1 y en C1; lo que cambia es qué se hace con él.

Esto evita el error de mantener cinco bancos paralelos que se desincronizan.

### 2.3 Indexación temporal

Semana ISO 8601. Contenido indexado por `(añoISO, semanaISO)`. El año ISO puede no coincidir con el calendario en los bordes de diciembre/enero. Se resuelve con la función nativa del lenguaje, nunca a mano.

### 2.4 Modo "revista"

Manda la fecha del dispositivo. La semana 14 es la semana 14 haya o no haya hecho el usuario la 13. No hay puntero de progreso, no hay estado guardado, no hay recuperación de semanas perdidas. Es la única variante compatible con "el único dato que se lee del dispositivo es la fecha".

Consecuencia aceptada: si el usuario se va tres semanas de viaje, esas tres semanas se perdieron. A cambio, la app no lo persigue ni lo culpabiliza, y no guarda nada sobre él.

### 2.5 Sin estructura de fichas por semana

Ningún archivo del proyecto se llama "semana 12". El contenido vive indexado por `(skill, nivel, order)`. El calendario solo referencia IDs. Reordenar el año es cambiar una función, no reescribir contenido.
---

## 3. Tipos de reto y evidencia por nivel

Esta es la sección que hace que A2 funcione dentro de una app de práctica libre. Sin ella, A2 sería "lo mismo pero más fácil", que no funciona: un A2 no puede escribir 150 palabras libres usando una estructura y además no tiene cómo saber si le salió bien.

La solución es que **el tipo de reto cambie con el nivel**, no solo su dificultad.

| Nivel | Tipo de reto (`challengeType`) | Lógica | Evidencia |
|---|---|---|---|
| **A2** | `chunk_deployment` | Repertorio cerrado de frases hechas, desplegado en un contexto nuevo cada vez | 5-8 frases escritas o 60-90 s de audio |
| **B1** | `guided_production` | Andamiaje: se da el esqueleto, el usuario completa y extiende | 100-150 palabras o 2 min de audio |
| **B2** | `constrained_production` | Producción libre con restricción estructural declarada | 250 palabras o 4 min de audio |
| **C1** | `open_production` | Producción libre, la restricción es de registro y precisión, no de forma | 400 palabras o 6 min de audio |
| **C2** | `adaptive_production` | Misma idea reformulada para tres audiencias o registros distintos | 400-600 palabras o 8 min de audio |

### 3.1 Por qué A2 sí encaja

El argumento en contra era que la app pide producción libre y un A2 no puede producir libremente. El argumento a favor, que es el correcto: en A2 uno aprende un repertorio acotado de frases armadas que resuelven situaciones concretas, y el problema no es aprenderlas sino **animarse a usarlas y sostenerlas en contextos que cambian**.

Entonces en A2:

- El repertorio es **cerrado y visible**. La ficha da 10-12 chunks, no una regla.
- Lo que rota es el **contexto**, no el contenido. Los mismos chunks de "pedir algo" se usan en la panadería, en la farmacia y en la oficina de correos.
- El reto es de despliegue, no de creación.
- El vocabulario nuevo entra de a poco y siempre pegado a un chunk que ya se domina.

Esto invierte la jerarquía respecto de los niveles altos:

| | Qué manda | Qué acompaña |
|---|---|---|
| A2 – B1 | El **Topic** (la situación) | El Skill |
| B2 – C2 | El **Skill** (la estructura) | El Topic |

Es una diferencia real de diseño, no un matiz. La ficha A2 se ve distinta de la ficha C1.

### 3.2 Micro-tareas

Cada ocurrencia trae **3 micro-tareas** (lunes / miércoles / viernes), de una línea cada una, derivadas del mismo Skill. No son contenido nuevo: son tres ángulos del mismo reto. Existen porque una app puramente semanal se abre el lunes y se olvida los otros seis días.

### 3.3 Prompt de corrección

Cada ocurrencia trae un prompt pre-escrito, copiable al portapapeles, para pegar en cualquier asistente de IA. Es lo que cierra el circuito de retroalimentación sin que la app se conecte a nada.

Reglas del prompt:
- Foco **exclusivo** en el Skill de la semana. No "corregime todo": eso devuelve una lista inútil de comas.
- Pide marcar aciertos, no solo errores.
- Pide una alternativa más natural, no solo la corrección.
- En B2+ pide además una valoración de registro.

### 3.4 Semanas de integración

- **Skill Review Week** cada 8 semanas (semanas ISO 8, 16, 24, 32, 40, 48). Sin Skill nuevo: se recombinan los últimos ocho.
- **Survival Week** cada ~16 semanas (semanas 12, 28, 44), desfasada. Sin Skill: una tarea comunicativa larga usando lo que sea.
- Ambos idiomas alinean estas semanas. El resto del año corre en tracks independientes.
---

## 4. Banco de Skills — Inglés (53)

Los niveles indicados son aquellos en los que el Skill **se practica activamente** en la app, no aquellos en los que se introduce por primera vez en un curso. Un Skill puede seguir apareciendo por encima del nivel en que se adquiere: `Second Conditional` se adquiere en B1 pero se sigue trabajando en B2 porque la fluidez con él tarda años.

### 4.1 Función y fluidez (18)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| EN-F01 | Giving and asking for opinions | A2 B1 B2 C1 C2 | CV Informal/Formal discussion | A2: repertorio cerrado (*I think…*). C1: opinión matizada bajo presión |
| EN-F02 | Agreeing and disagreeing | A2 B1 B2 C1 C2 | CV Formal discussion (meetings) | A2: acuerdo simple. C1: desacuerdo diplomático |
| EN-F03 | Describing people, places and routines | A2 B1 | CV Sustained monologue: describing experience | Base descriptiva; desaparece en B2 |
| EN-F04 | Telling a story / recounting experiences | A2 B1 B2 C1 C2 | CV Sustained monologue: describing experience | Cambia el eje: A2 secuencia, C1 estructura narrativa y tensión |
| EN-F05 | Making and responding to suggestions | A2 B1 B2 | CV Goal-oriented co-operation | |
| EN-F06 | Survival: directions, help, emergencies | A2 | CV Information exchange | Solo A2. Repertorio cerrado |
| EN-F07 | Survival: transactions (shops, tickets, appointments) | A2 | CV Goal-oriented co-operation | Solo A2. Repertorio cerrado |
| EN-F08 | Comparing and contrasting | A2 B1 B2 C1 | CV Sustained monologue: giving information | A2: comparativos. C1: contraste matizado y concesivo |
| EN-F09 | Giving advice | B1 B2 C1 | CV Goal-oriented co-operation | Cruza con EN-G06 y EN-G20 |
| EN-F10 | Speculating and predicting | B1 B2 C1 C2 | CV Propositional precision | Base gramatical en EN-G10 |
| EN-F11 | Summarising complex information | B1 B2 C1 C2 | CV Relaying specific information / Processing text | Escala de mediación del Companion Volume |
| EN-F12 | Persuading and negotiating | B2 C1 C2 | CV Sustained monologue: putting a case | |
| EN-F13 | Hedging and softening claims | B2 C1 C2 | CV Propositional precision | Descriptor pragmático, no gramatical. Núcleo del inglés profesional |
| EN-F14 | Giving constructive feedback | B2 C1 C2 | CV Facilitating collaborative interaction | Alto valor laboral, poco cubierto por los cursos |
| EN-F15 | Managing a conversation: turn-taking, interrupting, repair | B1 B2 C1 C2 | CV Turntaking / Taking the floor | El skill más subestimado para producción oral |
| EN-F16 | Complaining and resolving problems | B1 B2 C1 | CV Goal-oriented co-operation | |
| EN-F17 | Register shifting: formal ↔ informal | B2 C1 C2 | CV Sociolinguistic appropriateness | |
| EN-F18 | Irony, understatement and humour | C1 C2 | CV Sociolinguistic appropriateness | Marcadamente cultural. Riesgo alto de contenido no validable |

### 4.2 Gramática (20)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| EN-G01 | Present simple vs. present continuous | A2 | EGP A2 | |
| EN-G02 | Past simple and past continuous | A2 B1 | EGP A2 | |
| EN-G03 | Present perfect vs. past simple | A2 B1 B2 | EGP A2/B1 | Error fosilizado clásico en hispanohablantes |
| EN-G25 | Past perfect and narrative time reference | B1 B2 | EGP B1 + corpus | **Hueco detectado**: 37 entradas en el EGP y ausencia total en textos A2 |
| EN-G04 | Future forms: will / going to / present continuous | A2 B1 | EGP A2/B1 | |
| EN-G05 | First conditional | A2 B1 | EGP A2 | |
| EN-G06 | Second conditional | B1 B2 | EGP B1 | |
| EN-G07 | Third conditional | B1 B2 | EGP B1 | |
| EN-G09 | Modals of obligation, permission and ability | A2 B1 | EGP A2 | |
| EN-G10 | Modals of deduction (*must have*, *might have*, *can't have*) | B1 B2 C1 | EGP B1/B2 | |
| EN-G11 | Passive voice | B1 B2 | EGP B1 | |
| EN-G12 | Impersonal and advanced passive (*it is said that…*) | C1 | EGP C1 | |
| EN-G13 | Reported speech | B1 B2 | EGP B1 | |
| EN-G14 | Relative clauses: defining and non-defining | A2 B1 B2 | EGP A2 | Las no restrictivas con *who*/*which* ya son A2 |
| EN-G15 | Reduced relative and participle clauses | B2 C1 C2 | EGP B1+ | Gran salto de densidad escrita |
| EN-G16 | Inversion, cleft and emphatic structures | C1 C2 | EGP C1 | |
| EN-G17 | Gerunds and infinitives | B1 B2 C1 | EGP A1–C2 | |
| EN-G18 | Articles and quantifiers | A2 B1 | EGP A2 | Error persistente hasta C1 en hispanohablantes |
| EN-G19 | Word order and adverb placement | B1 B2 C1 | EGP A1–C2 | |
| EN-G20 | Unreal past: *I wish*, *if only*, *it's time* | B2 C2 | EGP B2/C2 | |

### 4.2bis Combinaciones estructurales (6)

Misma categoría `K` y misma lógica: estructuras que coocurren en la lengua real y que producen oraciones Frankenstein cuando se aprenden por separado.

| ID | Combinación | Niveles | Ancla | Nota |
|---|---|---|---|---|
| EN-K01 | Inversion + cleft + fronting for emphasis | C1 C2 | EGP C1 | Tres formas de mover el énfasis, usadas juntas y fáciles de sobrecargar |
| EN-K02 | Reduced relatives + participle clauses + nominalisation | C1 C2 | EGP B2 | La densidad del inglés escrito formal sale de estas tres a la vez |
| EN-K03 | Unreal past + modal perfect + third conditional | B2 C1 C2 | EGP B2 | El bloque del arrepentimiento: *I wish I'd known — we could have avoided it* |
| EN-K04 | Hedging + passive + impersonal reporting | B2 C1 C2 | CV Propositional precision | La cautela académica y profesional: *it has been suggested that this may…* |
| EN-K05 | Phrasal verbs + collocation + register shift | C1 C2 | CV Sociolinguistic appropriateness | Elegir entre *look into*, *investigate* y *conduct an inquiry* según el interlocutor |
| EN-K06 | Reported speech + backshift + hedged attribution | C1 C2 | Objective Proficiency | *She reportedly claimed she had been unaware*: tres capas de distancia |

### 4.3 Léxico (9)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| EN-V01 | Fixed chunks for daily survival | A2 | CV Vocabulary range | Núcleo del modelo A2. Repertorio cerrado por Topic |
| EN-V02 | Everyday phrasal verbs | A2 B1 | CV Vocabulary range | |
| EN-V03 | Advanced phrasal verbs | B2 C1 C2 | CV Vocabulary range | |
| EN-V04 | Everyday collocations | B1 B2 | CV Vocabulary control | |
| EN-V05 | Professional and academic collocations | C1 C2 | CV Vocabulary control | |
| EN-V06 | Idioms in context | B2 C1 C2 | CV Vocabulary control | Riesgo de registro anticuado. Revisión nativa obligatoria |
| EN-V07 | Discourse markers and linking | B1 B2 C1 C2 | CV Coherence and cohesion | |
| EN-V08 | Precision: avoiding overused words | B2 C1 C2 | CV Propositional precision | *very*, *thing*, *good*, *a lot* |
| EN-V09 | Connotation and nuance | C1 C2 | CV Vocabulary control | |

### 4.4 Distribución resultante

| Nivel | Skills disponibles | Fluidez | Gramática | Léxico |
|---|---|---|---|---|
| A2 | 18 | 8 | 8 | 2 |
| B1 | 27 | 10 | 12 | 5 |
| B2 | 30 | 12 | 12 | 6 |
| C1 | 29 | 13 | 8 | 8 |
| C2 | 17 | 9 | 2 | 6 |

Lectura: el peso se corre de gramática a fluidez y léxico a medida que sube el nivel, que es exactamente lo que debe pasar. En C2 quedan solo dos ítems gramaticales porque ya no hay gramática nueva que aprender, solo que dominar.
---

## 5. Banco de Skills — Alemán (58)

Recordatorio de la sección 1.2: **por encima de B2 no hay gramática nueva.** Profile deutsch describe la gramática solo hasta B2 y asume que en C1/C2 el trabajo es de uso activo, no de adquisición de estructuras. El banco lo refleja: los niveles C llevan registro, precisión, estilística y variación.

### 5.1 Función y fluidez / Kommunikation (14)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| DE-F01 | Meinung äußern und begründen | A2 B1 B2 C1 C2 | PD / MCER | A2: *Ich finde…* cerrado. C1: argumentación matizada |
| DE-F02 | Zustimmen und widersprechen | A2 B1 B2 C1 C2 | PD | C1: höflicher Widerspruch, clave en entorno laboral suizo |
| DE-F03 | Alltag bewältigen: Einkauf, Termine, Behörden | A2 | Goethe A2 | Solo A2. Repertorio cerrado |
| DE-F04 | Erfahrungen und Geschichten erzählen | A2 B1 B2 C1 | PD | |
| DE-F05 | Vorschläge machen und aushandeln | A2 B1 B2 C1 | PD | |
| DE-F06 | Ratschläge geben | B1 B2 C1 | PD | Cruza con DE-G09 |
| DE-F07 | Vermutungen ausdrücken | B1 B2 C1 C2 | PD | Cruza con DE-G05 (subjetiver Modalgebrauch) |
| DE-F08 | Zusammenfassen | B1 B2 C1 C2 | MCER | Escala de mediación |
| DE-F09 | Argumentieren und überzeugen | B2 C1 C2 | PD B2 | |
| DE-F10 | Abschwächen und relativieren | B2 C1 C2 | PD>B2 | Equivalente alemán del hedging. Se apoya mucho en Modalpartikeln |
| DE-F11 | Gespräch steuern: einwerfen, nachfragen, korrigieren | B1 B2 C1 C2 | MCER | |
| DE-F12 | Sich beschweren und reklamieren | A2 B1 B2 C1 | PD | |
| DE-F13 | Register: du/Sie, formell/informell | B1 B2 C1 C2 | PD | |
| DE-F14 | Ironie, Untertreibung und Humor | C1 C2 | PD>B2 | Deuda de validación alta |

### 5.2 Gramática / Grammatik (27)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| DE-G01 | Satzbau: Haupt- und Nebensatz, Verbstellung | A2 B1 B2 | PD A2 | El error estructural más caro para hispanohablantes |
| DE-G02 | Konnektoren (*weil, obwohl, trotzdem, deshalb, während*) | A2 B1 B2 C1 | PD A2/B1 | C1: konzessive und konsekutive Feinheiten |
| DE-G03 | Präpositionen: Dativ, Akkusativ, Wechselpräpositionen | A2 B1 | PD A2 | |
| DE-G04 | Trennbare und untrennbare Verben | A2 B1 | PD A2 | |
| DE-G05 | Modalverben | A2 B1 B2 C1 | PD A2 / Aspekte C1 | El uso subjetivo (*Er soll reich sein*) es **C1**, no B2 |
| DE-G06 | Perfekt vs. Präteritum im Gespräch | A2 B1 | PD A2/B1 | |
| DE-G07 | Adjektivdeklination | A2 B1 B2 | PD A2 | Error que sobrevive hasta C1 |
| DE-G08 | Verben mit festen Präpositionen + Präpositionaladverbien | B1 B2 | PD B1 | *worauf, darauf, damit* |
| DE-G09 | Konjunktiv II | B1 B2 C1 | PD B1 | |
| DE-G10 | Passiv und Passiversatzformen | B1 B2 C1 | PD B1/B2 | C1: *lassen sich*, *-bar*, *ist zu + Inf.* |
| DE-G11 | Relativsätze | B1 B2 C1 | PD B1 | C1: Genitivrelativsätze, *wo-/was-* |
| DE-G12 | Nominalisierung und Nominalstil | B2 C1 C2 | PD B2 / Erkundungen C2 | Puerta de entrada al alemán técnico y administrativo. En C2 es bloque propio |
| DE-G13 | Infinitivsätze mit *zu* | B1 B2 C1 | PD B1 / Aspekte C1 | C1: presente y pasado |
| DE-G14 | Erweiterte Partizipialkonstruktionen | B2 C1 C2 | Aspekte B2/C1 · Erkundungen C2 | Empieza en B2 (*Partizipien als Adjektive*); en C2, participios nominalizados |
| DE-G15 | Konjunktiv I und indirekte Rede | B2 C1 | Aspekte B2 | Aspekte lo introduce en B2, no en C1 |
| DE-G16 | Genitiv und Genitivpräpositionen | B2 C1 | PD B2 | |
| DE-G17 | Negation und Fokuspartikeln | B2 | Aspekte B2 | Aspekte lo pone en B2, no en B1+ |
| DE-G22 | Reflexive Verben | B1 B2 | Aspekte B1+ | **Hueco grave**: el juego de verbos reflexivos alemán no coincide con el español |
| DE-G23 | n-Deklination und Pluralbildung der Nomen | B1 | Aspekte B1+ | La n-Deklination no tiene equivalente en español |
| DE-G24 | Zukünftiges ausdrücken (Präsens vs. Futur I) | B1 | Aspekte B1+ | |
| DE-G25 | Appositionen | C2 | Erkundungen C2 | Refinamiento sintáctico, no estructura nueva |
| DE-G26 | Valenz: Verben und Nomen mit Ergänzungen | C1 C2 | Erkundungen C1/C2 · Sicher! C1 | Qué complementos exige cada verbo y cada nombre |
| DE-G27 | Adjektive mit präpositionalem Kasus | C1 | Erkundungen C1 | *stolz auf*, *fähig zu*: paralelo adjetival de DE-G08 |
| DE-G19 | Vergleichssätze (*als, wie, je … desto*) | B1 B2 | Aspekte B2 | Hueco detectado: no había skill de comparación en alemán |
| DE-G20 | Das Wort *es* (posicional y correlativo) | B2 | Aspekte B2 | Error frecuente, poco enseñado |
| DE-G21 | Modalitätsverben (*scheinen, pflegen, drohen*) | C1 | Aspekte C1 | C1 puro |
| DE-G18 | Temporale Nebensätze (*als, wenn, während, nachdem, seitdem*) | A2 B1 B2 | PD A2/B1 | *als* vs *wenn*: error clásico |

### 5.2bis Combinaciones estructurales / Strukturbündel (8)

Categoría propia, con la letra `K`. No son estructuras nuevas: son **combinaciones de estructuras ya conocidas que en la lengua real aparecen juntas**.

La razón de que existan es un problema concreto del nivel avanzado. Quien aprende Konjunktiv II en una unidad y Modalverben en otra, y nunca las ve combinadas, produce oraciones correctas por partes y monstruosas como conjunto. *Hätte man das früher wissen können* no se aprende sumando Konjunktiv II y verbos modales: se aprende como bloque.

Encaja con lo que declara Profile deutsch para C1 y C2 —que allí no hay gramática nueva sino uso activo de medios ya disponibles— y da a C2 un contenido gramatical que no consiste en inventar estructuras.

| ID | Combinación | Niveles | Ancla | Nota |
|---|---|---|---|---|
| DE-K01 | Konjunktiv II + Konditionalsatz + Modalverb im Perfekt | C1 C2 | Erkundungen C1 | *Hätte man das früher wissen können, wäre…* El reproche irreal con modal es el bloque más frecuente y el que peor se improvisa |
| DE-K02 | Passiv + Partizip I und II als Attribut | C1 C2 | Erkundungen C2 | *die zu erwartenden Auswirkungen*, *das anzuwendende Verfahren*. El participio modal solo tiene sentido junto al pasivo |
| DE-K03 | Nominalisierung + Genitivkette + Präposition mit Genitiv | C1 C2 | Erkundungen C2 | *im Hinblick auf die Umsetzung der Richtlinie*. El alemán administrativo entero es este bloque |
| DE-K04 | Konjunktiv I + Relativsatz + Nominalstil | C1 C2 | Erkundungen C1 | El registro de prensa y actas: *die Maßnahme, die bereits beschlossen sei, werde…* |
| DE-K05 | Subjektive Modalverben + Modalpartikeln + Abschwächung | C1 C2 | Aspekte C1 | *Das dürfte ja wohl eher nicht…* Tres recursos de atenuación apilados, que es como suena el alemán hablado culto |
| DE-K06 | Erweiterte Partizipialattribute und Relativsätze | C1 C2 | Erkundungen C1 | Convertir en las dos direcciones. Es la palanca de densidad del alemán escrito |
| DE-K07 | Funktionsverbgefüge + Nominalstil + Passiversatzformen | C2 | Erkundungen C2 | *Das lässt sich in Betracht ziehen.* El registro técnico y jurídico |
| DE-K08 | Zweiteilige Konnektoren + Vergleichs- und Konzessivsätze | C1 C2 | Aspekte C1 | *nicht nur… sondern auch*, *je… desto*, *zwar… allerdings* dentro de un mismo argumento |

### 5.3 Léxico / Wortschatz (9)

| ID | Skill | Niveles | Ancla | Nota |
|---|---|---|---|---|
| DE-V01 | Alltags-Chunks | A2 | Goethe A2 | Núcleo del modelo A2 |
| DE-V02 | Redewendungen | B1 B2 C1 C2 | PD B1+ | |
| DE-V03 | Kollokationen | B1 B2 C1 | PD B1+ | |
| DE-V04 | Funktionsverbgefüge | B2 C1 | Aspekte B2 | Aspekte lo llama *Nomen-Verb-Verbindungen*, en B2 |
| DE-V05 | Modalpartikeln (*doch, mal, ja, eben, halt, wohl*) | B1 B2 C1 C2 | PD B1 | **El skill de mayor impacto en sonar natural.** Casi no se enseña |
| DE-V06 | Wortbildung: Präfixe, Suffixe, Komposita | B1 B2 C1 C2 | PD B1 / Erkundungen C2 | Multiplica vocabulario sin memorizar listas. Es la estrategia que el Goethe declara esperar en C1 |
| DE-V07 | Umgangssprache vs. Standardsprache | B2 C1 C2 | PD>B2 | |
| DE-V08 | Helvetismen und Deutschschweizer Kontext | B1 B2 C1 C2 | — | Añadido propio, fuera del MCER. Ver nota 5.5 |
| DE-V09 | Feine Bedeutungsunterschiede und Konnotation | C1 C2 | PD>B2 | |

### 5.4 Distribución resultante

| Nivel | Skills disponibles | Fluidez | Gramática | Léxico |
|---|---|---|---|---|
| A2 | 14 | 6 | 8 | 1 |
| B1 | 28 | 9 | 14 | 5 |
| B2 | 31 | 11 | 14 | 6 |
| C1 | 27 | 12 | 8 | 7 |
| C2 | 18 | 8 | 4 | 6 |

**C2 alemán tiene cuatro ítems gramaticales, todos de refinamiento.** La versión anterior de este documento declaraba cero, apoyada en que Profile deutsch describe gramática solo hasta B2.

El índice de **Erkundungen C2** obligó a corregirlo, y la distinción es fina pero importante: de sus ocho bloques gramaticales, seis son retratamiento de material B2 —Zeitformen, Konjunktiv I y II, Modalverben, Relativsätze, Adjektivdeklination—, lo que confirma que **no hay estructuras nuevas**. Pero hay cuatro que sí son de C2 y no son estructuras nuevas sino **fenómenos de borde de estructuras conocidas**: Appositionen, participios nominalizados, valencia y formación de nombres.

Leer "no hay gramática nueva" como "no hay gramática" fue un error de interpretación. En C2 no se aprenden estructuras: se aprenden los bordes de las que ya se conocen, y eso sí es enseñable y sí tiene nivel.

### 5.5 Nota sobre DE-V08 (Helvetismen)

Este Skill no tiene ancla en ninguna referencia MCER porque el MCER describe el alemán estándar. Está incluido por una razón práctica: el usuario de referencia de esta edición reside y trabaja en la Suiza alemana, donde el Hochdeutsch escrito convive con el suizo alemán hablado y con helvetismos plenamente estándar en el registro formal (*Velo*, *parkieren*, *Traktandum*, *Unterlagen einreichen*, ausencia de *ß*).

Es contenido **regionalmente marcado**. En una distribución pública este Skill debe llevar etiqueta de variante y ser desactivable — el campo `variante` del schema cumple esa función —, o inducirá a error a un aprendiente de otra área germanófona.

### 5.6 Solapamiento entre bancos

Doce Skills tienen equivalente casi exacto en ambos idiomas (opinar, discrepar, resumir, narrar, aconsejar, especular, negociar, gestionar la conversación, registro, pasiva, relativas, condicional/Konjunktiv II). El solapamiento es deseable: permite que el usuario reconozca la función comunicativa y concentre el esfuerzo en la forma.

Nueve son específicos de una lengua y no deben forzarse a la otra: Modalpartikeln, Funktionsverbgefüge, Nominalisierung, Adjektivdeklination y Helvetismen no tienen equivalente inglés útil; phrasal verbs, articles/quantifiers y unreal past no tienen equivalente alemán útil.
---

## 6. Banco de Topics (14) y escalera de profundidad

Los 14 Topics son compartidos entre idiomas y entre niveles. Lo que cambia con el nivel es **el ángulo desde el que se aborda el tema** y el vocabulario asociado, no el tema.

Regla de diseño: el ángulo A2 es siempre **transaccional** (resolver una situación), el B1 **descriptivo-personal**, el B2 **argumentativo**, el C1 **analítico-profesional** y el C2 **crítico-abstracto**.

### T01 — Work & Career / Arbeit und Beruf

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Puesto, horario, lugar de trabajo | Describir un día laboral |
| B1 | Cambios de trabajo, entrevistas, condiciones | Contar un cambio de empleo |
| B2 | Conciliación, teletrabajo, jerarquías | Defender una postura sobre semana de 4 días |
| C1 | Cultura organizacional, conflicto, liderazgo | Dar feedback a un colega difícil |
| C2 | El sentido del trabajo, automatización, clase | Evaluar el discurso público sobre productividad |

### T02 — Personal Finance & Banking / Finanzen und Bankwesen

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Pagar, cobrar, abrir una cuenta | Resolver un trámite en el banco |
| B1 | Presupuesto, ahorro, gastos fijos | Explicar la organización de los gastos propios |
| B2 | Deuda, seguros, jubilación | Argumentar sobre alquilar vs. comprar |
| C1 | Inversión, riesgo, sistemas de pensiones | Explicar un sistema de pensiones a un extranjero |
| C2 | Desigualdad, política monetaria, especulación | Criticar un argumento económico público |

### T03 — Technology & AI / Technologie und KI

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Aparatos, apps, problemas simples | Pedir ayuda con un móvil que no anda |
| B1 | Hábitos digitales, redes, privacidad básica | Contar cómo cambió mi uso del teléfono |
| B2 | Automatización, empleo, regulación | Debatir si la IA debe reemplazar tareas repetitivas |
| C1 | Sesgo algorítmico, gobernanza, dependencia | Explicar un riesgo técnico a alguien no técnico |
| C2 | Determinismo tecnológico, soberanía digital | Evaluar críticamente una promesa tecnológica |

### T04 — Health & Wellbeing / Gesundheit und Wohlbefinden

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Síntomas, médico, farmacia, cita | Pedir un turno y explicar un malestar |
| B1 | Hábitos, sueño, deporte, alimentación | Describir un cambio de hábito |
| B2 | Sistemas de salud, prevención, salud mental | Argumentar sobre cobertura sanitaria |
| C1 | Ética médica, envejecimiento, evidencia | Resumir un estudio para alguien lego |
| C2 | Medicalización, autonomía, límites de la ciencia | Evaluar un debate bioético |

### T05 — Travel & Culture Shock / Reisen und Kulturschock

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Billetes, hotel, direcciones, equipaje | Resolver un problema en una estación |
| B1 | Viajes realizados, comparación de lugares | Narrar un viaje con un imprevisto |
| B2 | Turismo masivo, integración, prejuicios | Defender una postura sobre turismo |
| C1 | Identidad, migración, choque cultural | Explicar un malentendido cultural propio |
| C2 | Exotismo, poscolonialismo, pertenencia | Criticar una narrativa turística |

### T06 — Environment & Sustainability / Umwelt und Nachhaltigkeit

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Reciclaje, transporte, clima diario | Explicar la separación de residuos |
| B1 | Hábitos sostenibles, consumo | Contar un cambio de hábito y su motivo |
| B2 | Política climática, costos, responsabilidad | Argumentar sobre impuestos al CO2 |
| C1 | Transición energética, compensaciones, evidencia | Explicar un compromiso técnico real |
| C2 | Decrecimiento, justicia climática, discurso verde | Evaluar el greenwashing de una campaña |

### T07 — Housing & City Life / Wohnen und Stadtleben

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Vivienda, barrio, alquiler, mudanza | Reportar una avería al propietario |
| B1 | Buscar piso, convivencia, transporte | Comparar dos barrios de residencia previa |
| B2 | Precios, especulación, urbanismo | Argumentar sobre control de alquileres |
| C1 | Gentrificación, planificación, densidad | Presentar un proyecto urbano y sus contras |
| C2 | Derecho a la ciudad, propiedad, exclusión | Criticar un plan urbano desde su lenguaje |

### T08 — Relationships & Family / Beziehungen und Familie

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Familia, amistades, planes | Invitar a alguien y coordinar |
| B1 | Amistad a distancia, conflictos leves | Contar cómo conocí a alguien |
| B2 | Roles, expectativas, crianza | Argumentar sobre reparto de tareas |
| C1 | Conflicto, límites, cuidado | Mediar en un desacuerdo ajeno |
| C2 | Modelos de familia, intimidad, cambio social | Analizar un cambio generacional |

### T09 — Education & Lifelong Learning / Bildung und Weiterbildung

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Estudios, horario, docentes | Preguntar por un curso e inscribirse |
| B1 | Estrategias de aprendizaje, dificultades | Describir el propio método de estudio |
| B2 | Sistemas educativos, exámenes, acceso | Argumentar sobre educación gratuita |
| C1 | Formación continua, reconversión, mérito | Diseñar y defender un plan formativo |
| C2 | Credencialismo, desigualdad, autonomía | Criticar el discurso del talento |

### T10 — Food & Cooking / Essen und Kochen

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Pedir, comprar, cocinar algo simple | Pedir en un restaurante con una restricción |
| B1 | Recetas, gustos, cocina de origen | Explicar un plato típico paso a paso |
| B2 | Dieta, industria, etiquetado | Argumentar sobre carne y sostenibilidad |
| C1 | Cadena de suministro, cultura culinaria | Explicar una polémica alimentaria |
| C2 | Autenticidad, apropiación, gastronomía y clase | Evaluar el discurso de la comida "auténtica" |

### T11 — Current Events & News / Aktuelles und Nachrichten

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Titulares simples, tiempo, sucesos | Contar algo que pasó esta semana |
| B1 | Resumir una noticia, dar mi reacción | Resumir una noticia en 5 frases |
| B2 | Contrastar coberturas, opinar | Comparar dos versiones de un mismo hecho |
| C1 | Encuadre, fuentes, sesgo | Analizar cómo un medio construye un relato |
| C2 | Desinformación, agenda, retórica política | Desmontar un argumento público |

**Advertencia de diseño:** T11 es el único Topic que envejece. Las tareas deben formularse de modo genérico ("una noticia de esta semana"), nunca con hechos concretos, o la edición 2026 queda inservible en 2028.

### T12 — Hobbies & Free Time / Hobbys und Freizeit

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Actividades, frecuencia, compañía | Proponer un plan y acordar hora |
| B1 | Inicio de la afición, motivos | Contar cómo se llegó a una afición |
| B2 | Tiempo libre, productividad, ocio digital | Argumentar sobre el ocio "útil" |
| C1 | Comunidad, identidad, amateurismo | Explicar una subcultura desde adentro |
| C2 | Ocio y clase, mercantilización del tiempo libre | Analizar la profesionalización de un hobby |


### T13 — Services & Bureaucracy / Dienstleistungen und Behörden

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Correos, banco, peluquería, cita previa | Resolver un trámite presencial |
| B1 | Contratos, reclamos, atención al cliente | Relatar un trámite que salió mal |
| B2 | Burocracia, digitalización, acceso | Argumentar sobre servicios públicos vs. privados |
| C1 | Administración, derechos, mediación | Explicar un procedimiento a alguien recién llegado |
| C2 | Estado, ciudadanía, opacidad institucional | Criticar el diseño de un sistema administrativo |

### T14 — Consumption & Commerce / Konsum und Handel

| Nivel | Ángulo | Tarea tipo |
|---|---|---|
| A2 | Comprar, devolver, comparar precios | Devolver algo defectuoso |
| B1 | Compra online, garantías, publicidad | Describir una compra de la que se arrepiente |
| B2 | Consumo responsable, obsolescencia | Argumentar sobre reparar vs. reemplazar |
| C1 | Cadenas de suministro, poder de mercado | Explicar por qué algo cuesta lo que cuesta |
| C2 | Consumismo, deseo fabricado, valor | Analizar críticamente una campaña publicitaria |


### 6.1 Origen de T13 y T14

Ambos se añadieron al contrastar el banco con el catálogo temático que publica el Goethe-Institut en el manual del B2, tomado del MCER: 14 áreas, entre ellas *Dienstleistungen* y *Konsum und Handel*, que faltaban. **T03 (Technology & AI) y T11 (Current Events) no están en ese catálogo**: son adiciones propias, justificadas porque el catálogo del MCER es de 2001 y no previó la IA ni el consumo informativo actual. Queda declarado para que un revisor sepa cuáles tienen ancla y cuáles no.

### 6.2 Nota sobre universalidad para la expansión a ES/FR/IT/PT

Doce de los catorce Topics son razonablemente universales. Dos no lo son y hay que vigilarlos al traducir:

- **T02 (Finanzas):** los sistemas de pensiones, seguros y banca difieren tanto entre países que el vocabulario B2+ es casi intraducible sin variantes locales. El pilar suizo no existe en Argentina.
- **T07 (Vivienda):** alquiler, garantías, propiedad y regulación son radicalmente distintos por país.

- **T13 (Servicios y burocracia):** es el más dependiente del país de todos. Los trámites suizos no se parecen a los argentinos ni a los alemanes.

Recomendación: cuando se añada un idioma, estos tres Topics necesitan pack de vocabulario propio, no traducido.
---

## 7. Packs de vocabulario

### 7.0 Criterio de selección

Los packs **no** intentan cubrir el campo semántico de un tema. Un diccionario hace eso mejor y gratis. Se seleccionan ítems que cumplen al menos uno de estos criterios:

1. **No deducibles** desde el español (colocaciones, verbos con preposición fija).
2. **Falsos amigos** o trampas de registro.
3. **Alta frecuencia real** en conversación de ese tema, baja presencia en manuales.
4. **Chunks** listos para usar, no palabras sueltas.

Tamaño objetivo: **14 ítems por pack**. Un pack más grande no se usa, se hojea.

### 7.1 Estado de cobertura

| | A2 | B1 | B2 | C1 | C2 |
|---|---|---|---|---|---|
| Inglés | especificado | especificado | especificado | **completo (14/14)** | especificado |
| Alemán | especificado | especificado | **completo (14/14)** | especificado | especificado |

Los 28 packs completos corresponden a los niveles del usuario (EN C1, DE B2), que son los únicos que puede validar en el uso propio y los únicos que se necesitan para el piloto de 2026. Los demás niveles tienen especificado el ángulo y el criterio (sección 6), y sus packs se escriben cuando haya un revisor que pueda validarlos. Escribir ahora 112 packs no validables sería exactamente el error que este proyecto decidió no cometer.

---

## 7.2 Inglés C1 — packs completos

### T01 · Work & Career — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to take on (a role, responsibility) | phrasal | No *to assume a role* salvo en registro muy formal |
| a steep learning curve | colocación | Significa difícil, no lento. Error frecuente |
| to be up to speed (on) | chunk | *Let me get you up to speed* — imprescindible en reuniones |
| to have a lot on your plate | idiom | Informal pero aceptable en oficina |
| pushback | sustantivo | *I got some pushback on that.* Resistencia, sin connotación agresiva |
| bandwidth | metáfora | *I don't have the bandwidth for this.* Muy frecuente, algo jergal |
| to escalate (an issue) | verbo | Subir jerárquicamente, no "empeorar" |
| a stopgap (solution) | sustantivo | Solución provisoria |
| to be siloed | verbo | Equipos que no se comunican entre sí |
| to sign off on something | phrasal | Aprobar formalmente. No *to sign* |
| a stretch assignment | colocación | Tarea por encima del nivel actual, deliberadamente |
| to be spread too thin | idiom | |
| turnover (staff) | sustantivo | **Falso amigo**: no es "facturación" en este contexto (eso es *revenue* en EE.UU.) |
| to hand in your notice | chunk | Renunciar. *To resign* es más formal |

### T02 · Personal Finance & Banking — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to put money aside | phrasal | Más natural que *to save* en habla |
| a nest egg | idiom | Ahorro acumulado para el futuro |
| to be in the red / in the black | idiom | En números rojos / con superávit |
| to service a debt | colocación | Pagar los intereses, no cancelarla |
| a standing order / direct debit | término | Orden permanente / domiciliación. Distinción real en UK |
| to take out a loan / a policy | colocación | *Take out*, no *make* ni *do* |
| to be over-leveraged | término | Demasiado endeudado respecto a activos |
| disposable income | colocación | Ingreso tras impuestos y gastos fijos |
| to hedge against (inflation) | verbo | Cubrirse. Ojo: mismo verbo que el hedging discursivo |
| a windfall | sustantivo | Ingreso inesperado |
| to live beyond your means | idiom | |
| compound interest | colocación | |
| to write something off | phrasal | Amortizar o dar por perdido, según contexto |
| a rainy day fund | idiom | Fondo de emergencia. Muy usado |

### T03 · Technology & AI — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to roll something out | phrasal | Desplegar gradualmente |
| a workaround | sustantivo | Solución que esquiva el problema sin resolverlo |
| to be locked in (vendor lock-in) | término | Dependencia de un proveedor |
| technical debt | metáfora | Ya se usa fuera de la informática |
| to scale (intransitivo) | verbo | *It doesn't scale.* Sin objeto |
| a black box | metáfora | Sistema cuyo funcionamiento interno no se ve |
| to future-proof something | verbo | |
| edge case | término | Caso límite |
| to deprecate | verbo | Marcar como obsoleto sin eliminar aún |
| unintended consequences | colocación | Clave en debates sobre IA |
| to opt out (of) | phrasal | Y *opt in*. Distinción central en privacidad |
| a single point of failure | colocación | |
| to be over-engineered | adjetivo | |
| guardrails | metáfora | Límites de seguridad. Muy frecuente en discurso sobre IA |

### T04 · Health & Wellbeing — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to be run down | phrasal adj. | Agotado, con defensas bajas |
| to flare up | phrasal | Brote de una condición crónica |
| a referral | sustantivo | Derivación a un especialista |
| to be on the mend | idiom | Recuperándose |
| underlying condition | colocación | |
| to manage (a condition) | verbo | Convivir con ella, no curarla |
| evidence-based | adjetivo | |
| a side effect vs. an adverse reaction | distinción | La segunda es grave y notificable |
| preventative care | colocación | Ojo: *preventive* también correcto |
| to burn out | phrasal | Y *burnout* sustantivo |
| coping mechanism | colocación | |
| a check-up | sustantivo | **Falso amigo parcial**: no es *control* |
| to be discharged | verbo | Recibir el alta. No *to be released* |
| watchful waiting | término | Estrategia médica de observar sin intervenir |

### T05 · Travel & Culture Shock — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to get your bearings | idiom | Orientarse, literal y figurado |
| off the beaten track | idiom | |
| a culture clash | colocación | Choque, más fuerte que *culture shock* |
| to settle in | phrasal | Adaptarse a un lugar nuevo |
| homesick / homesickness | adjetivo | |
| an expat vs. an immigrant | distinción | Distinción cargada políticamente. Vale discutirla |
| red tape | idiom | Burocracia. Muy frecuente |
| to be a tourist trap | colocación | |
| to acclimatise (to) | verbo | Físico y cultural |
| a layover / stopover | término | |
| to travel light | colocación | |
| the done thing | chunk | *That's not the done thing here.* Muy británico y muy útil |
| to give someone the cold shoulder | idiom | |
| to blend in | phrasal | |

### T06 · Environment & Sustainability — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| a carbon footprint | colocación | |
| to offset (emissions) | verbo | Compensar. Término técnico y polémico |
| greenwashing | término | |
| a trade-off | sustantivo | Central para hablar de política ambiental con honestidad |
| to phase something out | phrasal | Eliminar gradualmente. Y *phase in* |
| single-use | adjetivo | |
| a tipping point | metáfora | Punto de no retorno |
| renewables (sustantivo plural) | término | Se usa sin *energy* |
| the grid | sustantivo | La red eléctrica |
| baseload | término | Carga base. Pertinente en perfiles de ingeniería |
| to curb (emissions) | verbo | Frenar. Registro periodístico |
| net zero | término | Ojo: no es lo mismo que *zero emissions* |
| stranded assets | colocación | Activos que pierden valor por la transición |
| virtue signalling | término | Cargado. Usar con conciencia del registro |

### T07 · Housing & City Life — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| a deposit | sustantivo | Fianza. **Falso amigo**: no es "depósito" de almacenaje |
| a landlord / a tenant | término | |
| to be priced out (of a market) | phrasal | Quedar fuera por precio |
| gentrification | término | |
| a fixer-upper | sustantivo | Vivienda a reformar. Informal |
| wear and tear | chunk | Desgaste normal. Clave en disputas de fianza |
| to give notice | colocación | Preavisar rescisión |
| a viewing | sustantivo | Visita a una vivienda. No *a visit* |
| utilities (bills) | término | Suministros |
| commutable | adjetivo | Desde donde se puede ir a trabajar a diario |
| urban sprawl | colocación | |
| a communal area | colocación | Zona común |
| to be up to code | chunk | Cumplir la normativa técnica |
| mixed-use development | colocación | |

### T08 · Relationships & Family — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to drift apart | phrasal | Distanciarse sin conflicto |
| to fall out (with someone) | phrasal | Pelearse. Y *a falling-out* |
| to patch things up | phrasal | Reconciliarse |
| to set boundaries | colocación | Muy frecuente hoy. Registro terapéutico ya normalizado |
| to take something the wrong way | chunk | |
| a sounding board | idiom | Persona con quien pensar en voz alta |
| to walk on eggshells | idiom | |
| to be there for someone | chunk | Difícil de traducir literalmente |
| an in-joke | sustantivo | |
| to bottle something up | phrasal | Guardarse emociones |
| tough love | colocación | |
| to grow apart vs. to grow up | distinción | |
| a support network | colocación | |
| to clear the air | idiom | Aclarar un malentendido |

### T09 · Education & Lifelong Learning — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to brush up on something | phrasal | Repasar algo sabido |
| to get to grips with something | idiom | Dominar algo difícil. Muy británico |
| a steep learning curve | colocación | Repetido a propósito desde T01: alta frecuencia |
| rote learning | colocación | Memorización mecánica |
| to fall behind / to catch up | phrasal | |
| a transferable skill | colocación | |
| to retrain / reskilling | término | |
| hands-on | adjetivo | |
| to sit an exam | colocación | Británico. EE.UU.: *to take* |
| a plateau (to plateau) | metáfora | Estancamiento en el aprendizaje |
| self-directed learning | colocación | |
| to cram | verbo | Estudiar a último momento |
| credentialism | término | Crítico, útil en C1/C2 |
| deliberate practice | término | Concepto que sustenta este sistema |

### T10 · Food & Cooking — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to be an acquired taste | idiom | |
| to whip something up | phrasal | Preparar algo rápido |
| a staple (food) | sustantivo | Alimento básico de una dieta |
| to season / seasoning | verbo | No *to spice* como genérico |
| use-by vs. best-before | distinción | Seguridad vs. calidad. Distinción legal real |
| to be over/underdone | adjetivo | |
| comfort food | colocación | |
| to source (ingredients) | verbo | Conseguir de un origen concreto |
| food miles | término | |
| a hearty meal | colocación | Contundente, positivo |
| to go off (milk, meat) | phrasal | Echarse a perder. Británico |
| bland | adjetivo | Soso. Muy usado, poco enseñado |
| batch cooking | término | |
| ultra-processed | adjetivo | |

### T11 · Current Events & News — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to break (a story) | verbo | Publicar primero. Y *breaking news* |
| framing | término | Cómo se encuadra un hecho. Central en C1 |
| a talking point | colocación | Argumento repetido de una postura |
| to downplay / to overstate | verbo | Par imprescindible |
| an outlet (news outlet) | sustantivo | Medio. No *media* en singular |
| unsubstantiated | adjetivo | Sin pruebas |
| to backtrack (on a statement) | phrasal | |
| a U-turn | metáfora | Cambio radical de postura. Muy periodístico |
| to come under fire | idiom | Ser criticado |
| an op-ed | término | Columna de opinión |
| the fallout (from) | sustantivo | Consecuencias. No literal, radiactivo |
| to gain traction | colocación | |
| a false equivalence | término | Falacia. Muy útil para C1 |
| to be misconstrued | verbo | Ser malinterpretado. Registro formal |

### T12 · Hobbies & Free Time — EN C1

| Ítem | Tipo | Nota de uso |
|---|---|---|
| to get into something | phrasal | Empezar a interesarse. Muy frecuente |
| to be hooked on something | idiom | |
| to dabble in something | verbo | Hacer algo sin comprometerse |
| a gateway (hobby, drug, etc.) | metáfora | |
| to tinker with something | verbo | Trastear, arreglar por gusto |
| a busman's holiday | idiom | Ocio parecido al propio trabajo. Muy británico |
| to be rusty | adjetivo | Fuera de práctica |
| downtime | sustantivo | |
| to unwind | verbo | Desconectar. Más natural que *to relax* |
| a completionist | sustantivo | Jerga de videojuegos, ya generalizada |
| to lose track of time | chunk | |
| a hobbyist vs. an amateur | distinción | El segundo puede sonar despectivo |
| to scratch an itch | idiom | Satisfacer una curiosidad |
| flow state | término | |
---

## 7.3 Alemán B2 — packs completos

Convención: se indica artículo y, cuando es relevante, la preposición que rige el verbo. Los helvetismos van marcados con **(CH)**.

### T01 · Arbeit und Beruf — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Einarbeitung | sustantivo | Período de incorporación. *sich einarbeiten in + Akk.* |
| sich um etwas kümmern | verbo + prep. | Encargarse. Uno de los verbos más útiles del alemán |
| die Zuständigkeit / zuständig sein für + Akk. | colocación | Competencia, responsabilidad formal |
| etwas in Angriff nehmen | Funktionsverbgefüge | Poner manos a la obra. Registro neutro-formal |
| die Absprache / etwas absprechen mit + Dat. | sustantivo/verbo | Acuerdo informal previo. Muy frecuente en oficina |
| der Termindruck | compuesto | Presión de plazos |
| die Überstunden (pl.) | sustantivo | Solo plural |
| sich einbringen | verbo reflexivo | Aportar, implicarse. Difícil de traducir |
| die Weiterbildung | sustantivo | Formación continua. Concepto central en el mundo laboral DACH |
| das Traktandum **(CH)** | helvetismo | Punto del orden del día. En Alemania: *der Tagesordnungspunkt* |
| kündigen (jemandem / selbst) | verbo | Ojo a quién despide a quién: *Ich kündige* vs. *Mir wurde gekündigt* |
| die Rückmeldung | sustantivo | Feedback. Preferible a *das Feedback* en registro formal |
| etwas nachvollziehen können | verbo | Entender el razonamiento de alguien. Altísima frecuencia |
| die Belastung / belastend | sustantivo/adj. | Carga, no solo física |

### T02 · Finanzen und Bankwesen — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Rücklage / Rücklagen bilden | colocación | Reservas, ahorro |
| der Dauerauftrag | sustantivo | Orden permanente |
| die Lastschrift | sustantivo | Domiciliación |
| sich verschulden | verbo reflexivo | Endeudarse |
| die Rendite | sustantivo | Rentabilidad |
| die Altersvorsorge | sustantivo | Previsión para la vejez. Concepto clave en DACH |
| die Säule **(CH)** | helvetismo | *Erste/zweite/dritte Säule* — los tres pilares del sistema suizo |
| über die Runden kommen | Redewendung | Llegar a fin de mes |
| etwas abbuchen | verbo | Cargar en cuenta |
| die Nebenkosten (pl.) | sustantivo | Gastos accesorios. Aparece también en T07 |
| die Steuererklärung | sustantivo | Declaración de impuestos |
| in Raten zahlen | colocación | Pagar a plazos |
| sich etwas leisten können | verbo reflexivo | Poder permitirse. Muy frecuente |
| die Bonität | sustantivo | Solvencia crediticia |

### T03 · Technologie und KI — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Anwendung | sustantivo | Aplicación, en sentido amplio |
| etwas voraussetzen | verbo | Dar por supuesto, requerir. Doble sentido útil |
| die Schnittstelle | sustantivo | Interfaz. Literal y figurado ("punto de contacto entre áreas") |
| ausgereift / nicht ausgereift | adjetivo | Maduro, terminado. Muy usado sobre tecnología |
| der Datenschutz | sustantivo | Protección de datos. Concepto cultural fuerte en DACH |
| etwas in Frage stellen | Funktionsverbgefüge | Cuestionar |
| die Auswirkung auf + Akk. | sustantivo + prep. | Efecto, impacto |
| menschenähnlich / maschinell | adjetivo | |
| die Verlässlichkeit | sustantivo | Fiabilidad. Prefiere -keit sobre *Reliabilität* |
| etwas nachrüsten | verbo | Actualizar añadiendo. Muy técnico y muy alemán |
| die Fehleranfälligkeit | compuesto | Propensión a fallos. Ejemplo de Nominalisierung productiva |
| voreingenommen / die Voreingenommenheit | adjetivo | Sesgado. Alternativa nativa a *der Bias* |
| etwas abwägen | verbo | Sopesar. Central para argumentar en B2 |
| der Umgang mit + Dat. | colocación | El trato con algo. Fórmula altísimamente frecuente |

### T04 · Gesundheit und Wohlbefinden — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Beschwerden (pl.) | sustantivo | Molestias, síntomas. Casi siempre plural |
| die Überweisung | sustantivo | **Doble sentido**: derivación médica y transferencia bancaria |
| sich schonen | verbo reflexivo | Cuidarse, no forzar |
| die Vorsorgeuntersuchung | sustantivo | Revisión preventiva |
| ausgebrannt sein | adjetivo | Quemado. Y *das Burnout* |
| die Krankenkasse | sustantivo | Seguro de salud. En CH: obligatorio y privado |
| der Selbstbehalt **(CH)** / die Franchise **(CH)** | helvetismo | Franquicia del seguro suizo. No existe igual en DE |
| die Nebenwirkung | sustantivo | Efecto secundario |
| sich krankmelden | verbo reflexivo | Dar parte de baja |
| die Genesung / genesen | sustantivo/verbo | Recuperación. Registro formal |
| auf etwas achten | verbo + prep. | Prestar atención a, cuidar de |
| die Belastungsgrenze | compuesto | Límite de tolerancia |
| chronisch / akut | adjetivo | |
| das Wohlbefinden | sustantivo | Bienestar. Más natural que *das Wellbeing* |

### T05 · Reisen und Kulturschock — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| sich einleben | verbo reflexivo | Adaptarse a vivir en un sitio |
| das Heimweh / das Fernweh | sustantivo | Nostalgia del hogar / ansia de irse. El segundo no tiene equivalente |
| die Unterkunft | sustantivo | Alojamiento. Genérico, muy útil |
| der Aufenthalt | sustantivo | Estancia |
| sich zurechtfinden | verbo reflexivo | Orientarse, arreglárselas |
| die Behörde / der Behördengang | sustantivo | Administración / trámite administrativo |
| die Anmeldung | sustantivo | Registro de domicilio. Trámite real en DACH |
| eingespielt sein | adjetivo | Rodado, ya funcionando bien |
| die Gepflogenheiten (pl.) | sustantivo | Usos y costumbres. Registro culto |
| befremdlich | adjetivo | Que resulta extraño o chocante |
| die Verständigung | sustantivo | Entendimiento, comunicación lograda |
| das Velo **(CH)** | helvetismo | Bicicleta. En DE: *das Fahrrad* |
| jemandem etwas übelnehmen | verbo | Tomarse algo a mal |
| auf Anhieb | chunk | A la primera. *Das hat auf Anhieb geklappt* |

### T06 · Umwelt und Nachhaltigkeit — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| der ökologische Fußabdruck | colocación | Huella ecológica |
| die Energiewende | sustantivo | Transición energética. Término político concreto |
| etwas eindämmen | verbo | Contener, frenar |
| der Ausstoß / ausstoßen | sustantivo/verbo | Emisión. Más común que *die Emission* en habla |
| die Kreislaufwirtschaft | compuesto | Economía circular |
| der Verzicht auf + Akk. | sustantivo | Renuncia. Concepto muy presente en el debate DACH |
| die Grundlast | término | Carga base. Léxico de ingeniería energética |
| das Stromnetz | compuesto | Red eléctrica |
| nachhaltig / die Nachhaltigkeit | adjetivo | Ojo: también significa "duradero" en sentido general |
| die Mülltrennung | compuesto | Separación de residuos. Práctica cultural fuerte |
| etwas in Kauf nehmen | Funktionsverbgefüge | Aceptar como precio a pagar. Muy útil para argumentar |
| der Zielkonflikt | compuesto | Conflicto de objetivos. Equivalente de *trade-off* |
| die Abhängigkeit von + Dat. | sustantivo + prep. | |
| schönreden | verbo | Endulzar, maquillar un problema. Equivalente parcial de *greenwashing* |

### T07 · Wohnen und Stadtleben — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Kaution | sustantivo | Fianza |
| die Nebenkosten (pl.) | sustantivo | Gastos aparte del alquiler. Fuente clásica de conflicto |
| der Mietvertrag / die Kündigungsfrist | sustantivo | Contrato / plazo de preaviso |
| die Besichtigung | sustantivo | Visita a una vivienda |
| der Eigenbedarf | sustantivo | Necesidad propia del propietario. Causa legal de desahucio |
| die Hausordnung | sustantivo | Normas de la comunidad. En CH se aplican en serio |
| die Wohnungsnot | compuesto | Escasez de vivienda |
| die Verdrängung | sustantivo | Desplazamiento de población. Término de la gentrificación |
| instand halten / die Instandhaltung | verbo | Mantener en estado |
| der Mangel / einen Mangel melden | sustantivo | Desperfecto |
| die Genossenschaft **(CH/DE)** | sustantivo | Cooperativa de vivienda. Muy relevante en Suiza |
| die Anbindung | sustantivo | Conexión con el transporte público |
| beengt / großzügig (Wohnung) | adjetivo | Par de adjetivos para describir espacio |
| die Zwischennutzung | compuesto | Uso temporal de un edificio vacío. Muy suizo |

### T08 · Beziehungen und Familie — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| sich auseinanderleben | verbo reflexivo | Distanciarse con el tiempo |
| jemandem etwas anvertrauen | verbo | Confiar algo a alguien |
| die Rücksicht / Rücksicht nehmen auf + Akk. | colocación | Consideración hacia alguien. Concepto cultural fuerte |
| sich streiten über + Akk. / um + Akk. | verbo + prep. | La preposición cambia el matiz: sobre qué / por qué |
| die Zuneigung | sustantivo | Afecto. Registro algo formal |
| jemandem aus dem Weg gehen | Redewendung | Evitar a alguien |
| die Erziehung | sustantivo | Crianza y educación en casa. Distinto de *Bildung* |
| sich vertragen | verbo reflexivo | Llevarse bien, reconciliarse. Muy coloquial y muy útil |
| die Verpflichtung gegenüber + Dat. | colocación | |
| etwas ansprechen | verbo | Sacar un tema. Clave para hablar de conflictos |
| nachtragend sein | adjetivo | Rencoroso |
| die Aufgabenteilung | compuesto | Reparto de tareas |
| jemandem den Rücken stärken | Redewendung | Apoyar a alguien |
| das Verhältnis zu + Dat. | sustantivo | La relación con alguien. Más neutro que *die Beziehung* |

### T09 · Bildung und Weiterbildung — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| sich etwas aneignen | verbo reflexivo | Adquirir un conocimiento por cuenta propia |
| die Lehre **(CH/DE)** | sustantivo | Formación dual/aprendizaje. Institución central en DACH |
| der Abschluss | sustantivo | Título obtenido |
| etwas vertiefen | verbo | Profundizar |
| die Auffassungsgabe | sustantivo | Capacidad de comprensión. Registro culto |
| auf dem Laufenden bleiben | Redewendung | Mantenerse al día |
| die Prüfung ablegen / bestehen / durchfallen | colocación | Los tres verbos correctos |
| das Fachwissen | compuesto | Conocimiento especializado |
| die Lernkurve | sustantivo | Calco reciente del inglés, ya establecido |
| sich weiterbilden | verbo reflexivo | |
| etwas verinnerlichen | verbo | Interiorizar. Preciso y poco enseñado |
| der Nachholbedarf | compuesto | Déficit por recuperar. Muy alemán como concepto |
| berufsbegleitend | adjetivo | Compatible con el trabajo (estudios) |
| die Durchlässigkeit (Bildungssystem) | sustantivo | Permeabilidad entre vías formativas. Debate real en CH |

### T10 · Essen und Kochen — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Zutat (-en) | sustantivo | Ingrediente |
| etwas anbraten / anschwitzen | verbo | Sofreír, pochar. Distinción real de técnica |
| abschmecken | verbo | Rectificar de sal. Sin equivalente exacto |
| deftig | adjetivo | Contundente, sustancioso. Muy usado, poco enseñado |
| die Haltbarkeit / das Mindesthaltbarkeitsdatum | sustantivo | Conservación / fecha de consumo preferente |
| verderben | verbo | Echarse a perder |
| die Unverträglichkeit | sustantivo | Intolerancia alimentaria |
| das Znüni / das Zvieri **(CH)** | helvetismo | Tentempié de media mañana / media tarde |
| regional und saisonal | colocación | Par fijo en el discurso alimentario |
| die Massentierhaltung | compuesto | Ganadería intensiva. Término del debate ético |
| fade | adjetivo | Soso. Registro neutro |
| etwas auf den Tisch bringen | colocación | Literal y figurado |
| die Zubereitung | sustantivo | Preparación |
| der Verzehr | sustantivo | Consumo alimentario. Registro formal/administrativo |

### T11 · Aktuelles und Nachrichten — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| die Berichterstattung | sustantivo | Cobertura informativa |
| etwas hinterfragen | verbo | Cuestionar críticamente. Verbo clave en B2+ |
| die Schlagzeile | sustantivo | Titular |
| jemanden zu Wort kommen lassen | chunk | Dar voz a alguien |
| die Quelle / aus zuverlässiger Quelle | colocación | |
| etwas relativieren | verbo | Matizar, poner en perspectiva |
| der Vorwurf / jemandem etwas vorwerfen | sustantivo/verbo | Reproche, acusación |
| in die Kritik geraten | colocación | Ser objeto de críticas |
| die Meinungsmache | compuesto | Manipulación de la opinión. Peyorativo |
| sich distanzieren von + Dat. | verbo + prep. | |
| die Tragweite | sustantivo | Alcance, trascendencia |
| etwas dementieren | verbo | Desmentir. Registro periodístico |
| einseitig / ausgewogen | adjetivo | Sesgado / equilibrado. Par fijo |
| der Sachverhalt | sustantivo | Los hechos del caso. Formal, muy útil para resumir |

### T12 · Hobbys und Freizeit — DE B2

| Ítem | Tipo | Nota de uso |
|---|---|---|
| sich mit etwas beschäftigen | verbo + prep. | Dedicarse a algo. Altísima frecuencia |
| etwas ausprobieren | verbo | Probar algo nuevo |
| der Ausgleich | sustantivo | Contrapeso al trabajo. Concepto cultural: *Sport als Ausgleich* |
| basteln / tüfteln | verbo | Manualidades / trastear con ingenio. *Tüfteln* es más técnico |
| aus der Übung sein | chunk | Estar desentrenado |
| die Vereinsmeierei | sustantivo | Burocracia asociativa. Irónico, muy cultural |
| der Verein | sustantivo | Asociación. Institución social central en DACH |
| abschalten | verbo | Desconectar mentalmente |
| sich austoben | verbo reflexivo | Desfogarse |
| die Leidenschaft für + Akk. | sustantivo | |
| gemütlich / die Gemütlichkeit | adjetivo | Sin equivalente en español. Merece una ficha propia |
| das Wandern / eine Tour machen | colocación | Senderismo. Actividad social central en CH |
| in etwas versinken | verbo | Sumergirse en una actividad |
| der Zeitvertreib | sustantivo | Pasatiempo. Ligeramente peyorativo |

### T13 · Services & Bureaucracy — EN C1

| Ítem | Tipo | Nota | ES |
|---|---|---|---|
| **red tape** ★ | idiom | burocracia innecesaria | trámites engorrosos |
| **to chase something up** ★ | phrasal | insistir para que avance | reclamar el seguimiento |
| **to be passed from pillar to post** ★ | idiom | de ventanilla en ventanilla | mandar de acá para allá |
| **a grievance / to file a grievance** ★ | colocación | reclamo formal | queja formal |
| an ombudsman | término | mediador institucional | defensor del pueblo |
| to escalate a complaint | colocación | | elevar un reclamo |
| a proof of address | término | trámite habitual en DACH | comprobante de domicilio |
| to opt out of a service | phrasal | | darse de baja |
| a grace period | colocación | | período de gracia |
| to be means-tested | verbo | según ingresos | sujeto a comprobación de ingresos |
| a backlog | sustantivo | trabajo acumulado | atraso acumulado |
| to streamline a process | verbo | | agilizar un proceso |
| a point of contact | colocación | | persona de contacto |
| to fall through the cracks | idiom | quedar sin atender | quedar en el limbo |

### T14 · Consumption & Commerce — EN C1

| Ítem | Tipo | Nota | ES |
|---|---|---|---|
| **planned obsolescence** ★ | término | | obsolescencia programada |
| **to be worth the outlay** ★ | colocación | *outlay* = desembolso | valer el desembolso |
| **a markup** ★ | sustantivo | margen sobre el costo | margen comercial |
| **to haggle over** ★ | verbo | regatear | regatear |
| a loss leader | término | producto gancho | producto gancho |
| to be locked into a contract | verbo | | quedar atado a un contrato |
| an impulse buy | colocación | | compra impulsiva |
| to shop around | phrasal | | comparar antes de comprar |
| a knock-off | sustantivo | informal | imitación |
| a chargeback | sustantivo | reversión de un cargo | contracargo |
| to be a false economy | idiom | barato sale caro | ahorro engañoso |
| consumer surplus | término | | excedente del consumidor |
| to undercut a competitor | verbo | | vender por debajo de |
| built to last | chunk | | hecho para durar |

### T13 · Dienstleistungen und Behörden — DE B2

| Ausdruck | Rektion | Hinweis | ES |
|---|---|---|---|
| **der Antrag / einen Antrag stellen** ★ | Akk. | Grundwort der Verwaltung | la solicitud / presentar una solicitud |
| **die Frist einhalten / versäumen** ★ | Akk. | | cumplir / dejar pasar el plazo |
| **der Bescheid** ★ | — | Entscheid der Behörde | la resolución |
| **zuständig sein für** ★ | + Akk. | erste Frage jedes Behördengangs | ser competente para |
| die Anmeldung / die Abmeldung | — | Wohnsitz | el alta / la baja de domicilio |
| die Beglaubigung | — | amtlich beglaubigte Kopie | la compulsa, la legalización |
| der Nachweis über | + Akk. | | el comprobante de |
| die Sachbearbeiterin | — | die Person am Schalter | la persona encargada del expediente |
| Widerspruch einlegen gegen | + Akk. | | presentar recurso contra |
| die Auflage | — | Bedingung der Behörde | la condición impuesta |
| der Termin / einen Termin vereinbaren | Akk. | | la cita / concertar una cita |
| das Formular ausfüllen | Akk. | | rellenar el formulario |
| die Bearbeitungszeit | — | | el plazo de tramitación |
| die Vernehmlassung | — | **CH**, politisches Verfahren | la consulta pública |

### T14 · Konsum und Handel — DE B2

| Ausdruck | Rektion | Hinweis | ES |
|---|---|---|---|
| **die Gewährleistung / die Garantie** ★ | — | gesetzlich vs. freiwillig | la garantía legal / comercial |
| **etwas reklamieren** ★ | Akk. | | reclamar por un defecto |
| **das Preis-Leistungs-Verhältnis** ★ | — | sehr häufig | la relación calidad-precio |
| **die geplante Obsoleszenz** ★ | — | | la obsolescencia programada |
| das Widerrufsrecht | — | Onlinekauf | el derecho de desistimiento |
| der Kaufvertrag | — | | el contrato de compraventa |
| die Ratenzahlung | — | | el pago a plazos |
| der Mangel / mangelhaft | — | juristischer Begriff | el defecto |
| etwas umtauschen | Akk. | | cambiar un producto |
| der Verbraucherschutz | — | | la protección al consumidor |
| die Lieferkette | — | | la cadena de suministro |
| etwas in Anspruch nehmen | Akk. | Funktionsverbgefüge | hacer uso de algo |
| der Schnäppchenpreis | — | umgangssprachlich | el precio de ganga |
| sich etwas gut überlegen | Akk. | | pensárselo bien |

---

## 8. Licencias

### 8.1 Aclaración previa: una licencia no certifica nada

El pedido era "la licencia que no permita copiarla y venderla con anuncios, y que certifique que es libre de trackers y compras". Hay que separar dos cosas que no son lo mismo:

| Lo que se quiere | Qué lo consigue |
|---|---|
| Que nadie haga una versión cerrada con anuncios | **La licencia** (copyleft) |
| Que se pueda demostrar que no hay trackers | **La distribución y la auditoría**, no la licencia |

Ninguna licencia de software libre prohíbe cobrar por el software. Lo que hace el copyleft fuerte es obligar a que cualquier derivado se publique también con el código abierto y la misma licencia. Eso no impide vender, pero **destruye el modelo de negocio del fork con anuncios**: quien lo intente tiene que publicar el código, y cualquiera puede recompilarlo sin los anuncios en cinco minutos.

### 8.2 Código de la app: **GPL-3.0-only**

Razones:

- Copyleft fuerte: cualquier derivado distribuido debe publicar su código bajo GPL-3.0.
- Incluye cláusulas anti-tivoización y de patentes que AGPL comparte pero MIT no.
- Es la licencia dominante en el ecosistema de apps libres de Android, lo que facilita reutilizar componentes.

Se descarta AGPL: su valor añadido es cubrir el uso como servicio de red, y esta app no tiene red por diseño. Añadiría fricción sin beneficio.

Se descarta MIT: permitiría exactamente el fork cerrado con anuncios que se quiere evitar.

### 8.3 Contenido pedagógico: **CC BY-SA 4.0**

El contenido no es código y la GPL le sienta mal. CC BY-SA 4.0:

- Exige atribución y que las obras derivadas mantengan la misma licencia.
- Es la licencia estándar de contenido educativo abierto, lo que importa cuando lleguen traducciones a francés o italiano: obliga a que vuelvan al procomún.
- Es compatible en la práctica con distribuir el contenido dentro de una app GPL.

**Consecuencia a asumir:** el vocabulario y las descripciones deben ser originales. No se pueden copiar listas del Goethe-Institut ni entradas del English Vocabulary Profile, que están protegidas. Se pueden usar como referencia metodológica y citarlas, que es lo que hace este documento.

### 8.4 Cómo se demuestra que no hay trackers

Esto no lo hace la licencia sino la publicación en **F-Droid**, que mantiene un sistema público de *Anti-Features*: etiquetas visibles en la ficha de cada app que señalan si contiene publicidad, si rastrea la actividad del usuario, si depende de servicios de red no libres, si promueve complementos privativos, y varias más.

La etiqueta de *Tracking* se aplica a apps que registran o reportan la actividad del usuario sin permiso o por defecto, incluyendo casos tan leves como enviar informes de fallos o comprobar actualizaciones sin avisar. F-Droid además compila desde el código fuente, así que la etiqueta no depende de la palabra del autor.

El objetivo declarado del proyecto es entonces concreto y verificable: **cero Anti-Features en F-Droid**. Eso se consigue así:

- Sin permiso `INTERNET` en el manifiesto. Es la prueba más fuerte que existe: si la app no puede abrir un socket, no puede rastrear nada. Vale más que cualquier política de privacidad.
- Sin Google Play Services, sin Firebase, sin ninguna librería de analítica o de informes de fallos.
- Compilación reproducible, para que el APK publicado se pueda verificar contra el código.
- Complementariamente, un informe de **Exodus Privacy** con cero rastreadores.

### 8.5 Lo que hay que escribir antes del primer commit público

| Archivo | Contenido |
|---|---|
| `LICENSE` | Texto completo GPL-3.0 |
| `LICENSE-CONTENT` | Texto completo CC BY-SA 4.0, con alcance explícito (todo lo que esté bajo `/content`) |
| `CONTRIBUTING.md` | Que quien aporte contenido acepta CC BY-SA 4.0 y código GPL-3.0 |
| `PRIVACY.md` | Una frase: la app no tiene permiso de red y no recoge ningún dato |

El punto sobre `CONTRIBUTING.md` es el que más urge: si se acepta la primera traducción al francés sin dejar clara la licencia, después hay que pedir permiso individualmente a cada contribuyente para cualquier cambio.

---

## 9. Deudas conocidas

Este documento declara sus propias debilidades. Ordenadas por gravedad.

| # | Deuda | Riesgo | Cuándo resolver |
|---|---|---|---|
| 0 | Auditoría alemana | **CERRADA para gramática.** Cruce contra Aspekte neu B1+/B2/C1 y Netzwerk neu A2/B1: 6 correcciones, 6 skills nuevos, A2 sin errores. Faltan los 23 skills de fluidez y léxico, que ningún índice cubre | No bloqueante |
| 1 | Ningún nativo revisó el banco de inglés | Registro anticuado o poco natural, sobre todo en EN-V06 (idioms) y EN-F18 (ironía) | Antes de publicar |
| 2 | Revisión por nativo o docente de alemán | Mejora de calidad deseable | **Plazo indefinido. NO bloquea la app** |
| 3 | Packs de vocabulario: 28 de 140 escritos | Los niveles A2, B1, B2-EN y C1-DE no son usables todavía | Cuando haya revisor por nivel |
| 4 | DE-V08 (Helvetismen) no tiene ancla MCER | Confunde a usuarios fuera de Suiza | Antes de publicar: etiquetar como variante y hacerlo desactivable |
| 5 | ~~Verificación contra el EGP~~ | **CERRADA.** Auditoría completa contra las 1.222 estructuras del EGP; 4 correcciones aplicadas y 1 skill eliminado | Hecho |
| 6 | T11 (Actualidad) envejece | La edición de un año queda obsoleta | Regla ya fijada: nunca hechos concretos en las tareas |
| 7 | Sin decisión sobre voz TTS | El audio es un compromiso todavía no evaluado | Tras escuchar las muestras de Piper |

### 9.1 Lo que este documento deliberadamente NO hace

- No asigna semanas concretas. Eso lo calcula el generador de calendario.
- No escribe fichas. Eso es el paso siguiente, y empieza por dos de muestra.
- No define UI, stack ni widget.
- No incluye contenido para niveles que el usuario no pueda validar ni conseguir que alguien valide.
