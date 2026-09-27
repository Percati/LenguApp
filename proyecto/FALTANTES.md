# Qué falta, a 22 de septiembre de 2026 (actualización 3)

Estado regenerado a partir de los archivos reales del repositorio (parche de Apariciones ya aplicado y verificado en un clon de prueba, no solo leído). Si este archivo y otro documento del proyecto se contradicen, este es el que hay que creer.

## 1. Núcleos — ✅ completo

293/293, con `contraste` y `erroresContrastivos` completos (5 claves de idioma de app cada uno) en las 10 combinaciones.

## 2. Packs 2027 — ✅ completo

480/480. Validado con `tools/validar_packs.py`: 0 problemas de continuidad. Hueco conocido: 8 packs de `en-C1` siguen calcados del alemán (T02-1/3, T07-1/2, T09-1/3, T13-2/4). Causa confirmada: se escribieron en el commit inicial del repo, antes de que existiera el flujo de conversaciones separadas — se tradujo el glosario alemán en vez de escribir vocabulario inglés idiomático, en los cuatro temas con conceptos suizos específicos (pensiones, vivienda, Lehre, burocracia). No es una limitación de material disponible en inglés — pendiente de reescritura por Packs, no bloquea.

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

**Total: 480/480 fichas regulares + 40 semanas de repaso (S10/22/34/46 × 10 combos), verificado componiendo: 522 fichas + 49 semanas especiales, 0 problemas.**

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

### 4.8 — ✅ Modelos Kotlin leen `{idioma: texto}` (resuelto por Code)
Los 13 campos bilingües son ahora `TextoBilingue` (string plano u objeto por
idioma, `ContenidoSemanal.kt`). Se resuelve con `resolver(idiomaBase,
idiomaAprendido)`: idioma base del usuario, si falta o está vacío el idioma que
se aprende, y si tampoco cualquier otro; nunca lanza. Pantalla y widget ya lo
usan; hay tests unitarios y de render. Ya se puede embeber A2/B1 traducido.
(Texto original del bloqueante:)

**Antes: 🔴 Modelos Kotlin no leen `{idioma: texto}` (bloqueante real para embeber A2/B1)**
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

## 5. Audio — ✅ completo

6368 archivos `.ogg` en el repo, cubren los 6312 registrados en
`audio-estado.json`. `tools/generar_audio.py` se retiró del repo (Fer genera
todo localmente con Piper); las referencias a él en `CLAUDE.md` (raíz y
proyecto/), `PROMPT-CLAUDE-CODE.md` y `manifiesto_audio.py` se actualizaron
para no apuntar a un archivo que ya no existe.

## 6. Traducciones — ✅ cerrado, con una armonización diferida

No queda contenido pendiente de traducir: prosa A2/B1 (95 núcleos y 192
apariciones), vocabulario y expresiones, y las 16 semanas de repaso A2/B1
(4 semanas × DE A2, DE B1, EN A2, EN B1). Verificado componiendo las tres
capas reales: "522 fichas compuestas, 49 semanas especiales, 0 con
problemas", con el exportador en cero. El vaivén de parches de longitudes
(alargar → revertir) no dejó ninguna celda corta.

### 6.1 — Armonizar el trato al lector: tuteo en todo el material (diferido)

El trato al lector quedó disparejo entre lotes. En el lote de prosa alemana
el español y el italiano usan usted ("Hable", "Si concentri"); en las
semanas de repaso usan tú ("habla", "parla"). El francés mantiene *vous* en
los dos y el alemán *Sie*. Cada idioma es coherente dentro de su lote, pero
no entre lotes, y con el switch de idioma siempre visible la diferencia se
puede ver comparando la misma consigna en dos idiomas.

**Decisión: unificar en tuteo**, tanto en el idioma que se aprende como en
las traducciones. Alcanza al contenido ya generado de alemán e inglés.

**Cuándo: al sumar el primer idioma nuevo que se aprende** (español,
italiano, francés o portugués). Ahí hay que fijar el registro del contenido
nuevo de todos modos, y conviene hacer los dos pases juntos en vez de tocar
dos veces contenido ya validado. No es un bloqueante para el piloto.

Alcance del pase, para dimensionarlo cuando llegue el momento: los campos de
texto de núcleos, apariciones y semanas especiales de A2/B1 en los seis
idiomas. El contenido B2/C1/C2 es monolingüe y entra solo por el lado del
idioma que se aprende.

## 7. Vocabulario atestiguado alemán — A1-C1 reconstruido, falta C2

`vocab_de.json` cubre A1/A2/B1/B2/C1: **7683 lemas**. El 27-09-2026 se corrió por
primera vez la **reconstrucción completa** (`build_wordlists.py` con las 14 fuentes
presentes), en lugar de la fusión parcial que se venía usando desde B2. Reparto:
A1 584, A2 604, B1 2054, B2 3172, C1 1269, más 699 con etiqueta doble.

Dos arreglos del extractor salieron de esta corrida:

- Las listas del Goethe abrevian el artículo (`r Dezember`, `e Minute, -n`). El lector
  solo reconocía `der/die/das`, así que perdía más de la mitad de A1: pasó de 293 a 584
  lemas. Se añadió `r|e|s` al patrón de sustantivo y a la normalización de clave; sin
  lo segundo, `r dezember` quedaba como lema aparte de `dezember`.
- Con eso, palabras básicas que estaban mal ubicadas volvieron a su nivel: *Dezember*,
  *Minute*, *Montag*, *Woche* y *Bruder* pasaron de B1 a A1.

Artefacto conocido que queda: **94 lemas del top-1000 de frecuencia siguen etiquetados
B2 o C1** (*drei*, *acht*, *dich*, *dein*, *davon*). Son numerales, pronombres y
partículas que las listas del Goethe no traen como entradas de la lista alfabética
—aparecen en tablas del tipo `3 = drei`— así que solo los atestigua algún manual B2.
No es un error de contenido y no afecta a los packs: se puede vivir con eso o
ampliar el extractor para leer esas tablas.

Reverificación de los 240 packs alemanes contra la lista reconstruida: 1024 verificados,
264 por encima del nivel, 1205 sin atestiguar y 801 expresiones. Las 264 alertas se
concentran en A2 (163) y B1 (86) y bajan respecto de las 290 de la corrida anterior;
C1 y C2 no tienen ninguna. No apareció nada nuevo: son palabras que los manuales de
nivel superior también recogen.

Falta **C2** como nivel atestiguado. En sept-2026 se probó *Deutsch üben: Wortschatz
& Grammatik C2* (Hueber, 129 págs., con capa de texto, sin OCR necesario) y **no
sirve como fuente de lemas**: es un cuaderno de ejercicios en prosa, no un
Lernwortschatz. Medido dos veces, sin y con el diccionario como lista blanca:

- 2215 lemas sin lista blanca, 2508 con ella; de esos, 1243 serían nuevos, dominados
  por ruido: adjetivos declinados (*gehobenen*, *psychischen*, *einzigen*), fragmentos
  de palabras cortadas por guion (*nitionen* por Definitionen, *schenleben* por
  Menschenleben) y metalenguaje del propio libro (*Satzteile*, *Modalwörter*).
- Sobre los 48 packs de alemán C2 cubre **3 ítems** en ambas mediciones
  (*kleinlich*, *Meisterschaft*, *Zugehörigkeit*). El aporte no compensa marcar como
  C2 más de mil lemas que en su mayoría no lo son.

Por eso **no se cargó**: `vocab_de.json` sigue sin nivel C2. En sept-2026 se probaron
además tres listas web (MindDory 554 lemas, ScoreUp 107, germanfluent 5475) y se
descartaron las tres: germanfluent etiqueta como C2 bandas de frecuencia, no
dificultad (*Aschenbecher*, *Klassenzimmer*, *Vanille* figuran como C2), y las otras
dos, juntas, atestiguan **un solo ítem** de los 48 packs (*Ritual*).

**Solución adoptada: verificación por banda de frecuencia** (`tools/verificar_frecuencia.py`).
En niveles altos, en vez de preguntar si una palabra está en una lista de nivel —que para
compuestos y colocaciones nunca va a estar— se pregunta si es lo bastante infrecuente
como para pertenecer a ese nivel, usando el rango del diccionario de frecuencia
(Jones/Tschirner, 5000 lemas). El PDF no se versiona; la herramienta lo lee de
`fuentes/` en cada corrida y solo usa el rango, en memoria.

Resultado de la primera corrida:

| Pack | Demasiado común (top 1000) | 1001-2500 | 2501-5000 | Fuera del top 5000 |
|---|---|---|---|---|
| DE B2 | 8 | 18 | 45 | 400 |
| DE C1 | 5 | 7 | 41 | 374 |
| DE C2 | 3 | 10 | 21 | 484 |

El perfil es el esperado: cuanto más alto el nivel, más contenido fuera del top 5000.
Las alertas de C2 son *das Miteinander* (rango 716) y *die Bildung* (850, en dos packs).
Limitación conocida: el rango es por forma, no por acepción, así que una nominalización
de nivel alto hereda la frecuencia del adverbio o del sustantivo básico homógrafo
(*miteinander*); las alertas se revisan a mano, no se aplican en automático.

*Cosmos C2 – Redemittel* (escaneado, 17 páginas) tampoco es fuente de lemas: son
fórmulas de discurso. **Decisión (sept-2026): no se transcribe.** El campo `redemittel`
ya existe en cada núcleo (1862 expresiones en los 293 núcleos) — esto no es un tipo
de contenido nuevo. Copiar una selección curada de una sección entera de un manual
con derechos a un repo CC BY-SA es un riesgo real, distinto al de las wordlists
(que solo se usan como lista blanca de verificación, sin copiar contenido). La ruta
segura: escribir Redemittel C2 nuevos desde cero, usando el manual solo como
referencia de registro/nivel, nunca transcribiendo. Los 9 núcleos `fluency` de
alemán C2 están todos en el mínimo (6); hay margen de 6 más por núcleo. Sin dueño.

## 7.5 Ortografía suiza (ss vs ß) — ✅ cerrado, dos excepciones documentadas

Regla (`contenido/nucleos/DE-V08-B1.json`): en alemán suizo se escribe
siempre `ss`, nunca `ß`. Se corrigió en tres tandas (núcleos por
Apariciones, packs/ocurrencias por Packs, semanas de repaso por
Apariciones) — 522/49/0 sin cambio de número en cada paso, cero `ß` fuera
de las dos excepciones siguientes:

- **`DE-V08-B1/B2/C1`** (43 instancias): la letra es el contenido
  pedagógico del núcleo (enseña la propia regla), no un error.
- **`contenido/nucleos/EN-*.json`, clave `de` de `contraste`/`erroresContrastivos`**
  (58 instancias): ahí el alemán es el idioma de interfaz para un usuario
  germanohablante (Alemania/Austria/Suiza indistintamente), no el alemán
  suizo que se enseña — corresponde ortografía estándar (`ß`), y unificarla
  a `ss` sería el error, no la corrección.

No queda ninguna instancia fuera de estas dos categorías. No reabrir salvo
que aparezca un caso genuinamente distinto a los dos de arriba.

## 8. Sin dueño todavía

- **Semanas especiales sin soporte multiidioma:** ✅ el lado de Code ya está resuelto. `semana-especial.schema.json` acepta `oneOf` string plano u objeto `{idioma: texto}` en sus 6 campos de texto (no obligatorio; B2/C1/C2 siguen monolingües), y los modelos Kotlin (`SemanaEspecial`) y la pantalla ya los resuelven. **Falta solo la traducción del contenido de las semanas de repaso A2/B1 (Traducciones).** Además el id de las semanas especiales ahora lleva año: `REVIEW-DE-B2-2027-S10`, `SURVIVAL-EN-B2-2026-S44` (los 49 archivos de `contenido/nucleos/` se renombraron y `componer.py` ahora los valida contra su schema). Verificado: 522 fichas, 49 semanas especiales, 0 problemas.

## 9. Piloto 2026

Alemán B2, inglés B2 e inglés C1 siguen completos bajo el esquema viejo. No tocar hasta que se resuelva el id con año (sección 4).

## 10. Features de app — ✅ implementadas por Code (sept 2026)

Tres features de UI nuevas, sin tocar `contenido/` ni ningún schema.

✅ **Switch de traducción en vivo (A2/B1):** dentro de la ficha, visible mientras
se scrollea (no adentro del scroll de la ficha). Solo en fichas con
`bilingue == true`. No persiste — arranca siempre mostrando `idiomaBase`, se
resetea al cambiar de ficha. Afecta todos los campos `TextoBilingue` de la
ficha, más `contraste`/`erroresContrastivos` (mismo criterio, aunque esos dos
son un mecanismo previo a `TextoBilingue`).

✅ **Desafío de fin de semana:** switch separado (chip, no el mismo componente
que el de traducción), visible solo sábado 00:00–domingo 23:59 hora local.
Arranca siempre en `off` — CLAUDE.md regla dura #13. Actívalo y "pasa al
desafío": reemplaza el contenido normal por la `mision` de esa ficha bajo el
encabezado "Desafío de fin de semana" — mecanismo nada más, el texto final de
la variación queda para una conversación de contenido aparte.

✅ **Guardados:** nueva sección para marcar vocabulario/expresiones desde
cualquier ficha (ícono de estrella, toggle sin confirmación). Persiste en
Room — **única excepción documentada a la regla dura #4**, ver `CLAUDE.md`.
Filtro por idioma (uno a la vez, no mezcla), nivel y tipo. Tocar una fila
lleva a la ficha de origen si esa edición sigue embebida; si no, se muestra
sin el link. Buscar la ficha de origen es por `(skillId, idioma, nivel)`, no
por id completo — `ItemGuardado` no guarda `order`/`anio`, así que si el
mismo skill aparece más de una vez elige la primera que encuentra. Es una
simplificación conocida, no un bug.

Tests: 211 en `testDebugUnitTest` (antes 197), 0 fallas. `compileDebugKotlin
compileDebugUnitTestKotlin test` limpio.

🟡 **Nota de entorno:** `jre-21.0.5.11-hotspot` (el que usa `gradlew` por
default en esta máquina) no trae `jlink`, necesario para el nuevo procesador
de anotaciones de Room (KSP). Hace falta `jdk-21.0.5.11-hotspot` (la carpeta
hermana, con JDK completo) — `$env:JAVA_HOME` apuntando ahí antes de compilar.
No es nada del código ni del repo, es la instalación de Java de esta máquina.
