# Qué falta, a 8 de octubre de 2026 (actualización 4)

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

6368 archivos `.ogg` en el repo; el estado registra 8202 (ver abajo). `tools/generar_audio.py` se retiró del repo (Fer genera
todo localmente con Piper); las referencias a él en `CLAUDE.md` (raíz y
proyecto/), `PROMPT-CLAUDE-CODE.md` y `manifiesto_audio.py` se actualizaron
para no apuntar a un archivo que ya no existe.

✅ **Redemittel nuevos y packs 2026: 1834 audios generados** (28-09). `audio-estado.json` registra **8202 archivos: 4133 EN y 4069 DE**, todos `.ogg`. `exportar_audio_maestro.py --solo-pendientes` da 0 pendientes y `validar_apariciones.py` no da avisos de audio ni en 2026 ni en 2027.

🔴 **Los 1834 `.ogg` nuevos están solo en la copia local de Fer**: el repo tiene los 6368 anteriores y el estado registra 8202. Fer los sube con `git add`/`commit`/`push`; `sincronizar_audio_estado.py --dry-run` lista los que falten en un clon.

🟡 **Bug corregido en `exportar_audio_maestro.py`:** desde la fase de traducciones, `texto`, `expresion` e `item` de A2/B1 son `{idioma: texto}`. El exportador serializaba el diccionario entero como texto, con lo que 191 ejemplos ya grabados figuraban como pendientes y el nombre sugerido salía mal. Ahora toma la clave del idioma que se aprende, igual que `validar_apariciones.py`.

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

## 7.6 Categorías de uso de los redemittel (`categoriasUso`) — 91% tageado

Tageadas **2624 de 2897** expresiones contra `data/categorias-uso.json` (18 funciones
comunicativas + 15 patrones gramaticales). Validar con
`python3 tools/validar_categorias.py`.

Las **273 restantes** quedaron con `categoriasUso: []`, que significa *revisado y sin
categoría que encaje* — distinto del campo ausente, que sería *sin revisar*. No se
forzó ninguna etiqueta para vaciar la lista.

De esas 273, **221 son un hueco de la taxonomía, no casos raros**: caen en skills cuyo
contenido es léxico-semántico, y ninguna de las dos ramas (función comunicativa /
patrón gramatical) está pensada para describirlo.

| Expresiones | Skill | Qué describe su `funcion` |
|---|---|---|
| 43 | DE-V06 | formación de palabras (*Kompositum*, *un-* + adjetivo) |
| 29 | DE-V08 | helvetismos |
| 24 | DE-V07 | sinónimo coloquial frente al estándar |
| 22 | EN-F17 | cambio de registro formal/informal |
| 22 | EN-V08 | precisión léxica (qué reemplaza a *very*, *good*) |
| 18 | DE-F13 | registro du/Sie |
| 17 + 16 | EN-V09, DE-V09 | connotación |
| 16 | EN-F03 | describir personas y lugares |
| 14 | EN-G18 | determinantes y cuantificadores |

Cubrirlas exigiría decidir con Fer una tercera rama o 4-5 categorías más — del tipo
*Registro*, *Formación de palabras*, *Variante regional*, *Elección léxica y
connotación*, *Describir* y *Determinación*. **No se inventaron**: la taxonomía
vigente es la aprobada el 08-10-2026.

Las otras 52 son cola larga: descripciones de `funcion` únicas y muy específicas
(«plants a detail whose weight comes later»). Se resuelven a mano o ampliando el
léxico, sin decisión de diseño de por medio.

## 8. Sin dueño todavía

- **Semanas especiales sin soporte multiidioma:** ✅ el lado de Code ya está resuelto. `semana-especial.schema.json` acepta `oneOf` string plano u objeto `{idioma: texto}` en sus 6 campos de texto (no obligatorio; B2/C1/C2 siguen monolingües), y los modelos Kotlin (`SemanaEspecial`) y la pantalla ya los resuelven. **Falta solo la traducción del contenido de las semanas de repaso A2/B1 (Traducciones).** Además el id de las semanas especiales ahora lleva año: `REVIEW-DE-B2-2027-S10`, `SURVIVAL-EN-B2-2026-S44` (los 49 archivos de `contenido/nucleos/` se renombraron y `componer.py` ahora los valida contra su schema). Verificado: 522 fichas, 49 semanas especiales, 0 problemas.

## 9. Piloto 2026

Alemán B2, inglés B2 e inglés C1 siguen completos bajo el esquema viejo. No tocar hasta
que se resuelva el id con año (sección 4).

Dos transiciones de continuidad del piloto quedan fuera del rango 30-80 % y no se pueden
arreglar sin tocar contenido congelado: `de-B2-2026-T01 1->2` (7 %) y `en-C1-2026-T03
1->2` (93 %). Desde sept-2026 `validar_packs.py` las lista como aviso, con la constante
`DEUDA_PILOTO`, para que no tapen problemas reales del resto del calendario. Si algún día
se descongela el piloto, hay que quitarlas de esa constante y rehacer esas dos cadenas.

🔴 **`de-B2-2026`: los packs `T02-1` y `T02-2` están intercambiados respecto al orden real
del calendario** — verificado con los datos crudos (no es un falso positivo del
validador). El calendario dice semana 38 = `DE-G02` (debería usar el pack cronológicamente
primero, `T02-1`) y semana 52 = `DE-F10` (debería usar `T02-2`); las `ocurrencias/` reales
tienen exactamente al revés: `DE-G02`→`T02-2` y `DE-F10`→`T02-1`. Efecto real: un usuario
en esas dos semanas del piloto ve un vocabulario pensado para la otra. Congelado por regla
dura #11, no se toca — queda documentado para si el piloto se descongela alguna vez.

Packs 2026 de los 7 pares parciales (de-A2/B1/C1/C2, en-A2/B1/C2): 84 packs escritos en
sept-2026, 12 por par, uno por semana de contenido. Cobertura 126/126 en `--anio 2026`.

## 12. Contenido 2026 completo (sept-2026) — ✅ verificado de punta a punta

Apariciones (84 + 7 repasos), audio (1834 clips nuevos, 0 pendientes) y traducciones
(1815 celdas, 4 pares bilingües) de los 7 pares parciales están cerrados. Verificado
independientemente, no solo por los reportes:

- `componer.py`: **606 fichas compuestas, 56 semanas especiales, 0 con problemas**.
- `validar_apariciones.py --anio 2026`: 0 errores, 44 avisos (todos del piloto congelado,
  sección 9). `--anio 2027`: 0 errores, 0 avisos, intacto.
- Exportador de traducciones `--solo-pendientes`: 0 celdas. Exportador de audio
  `--solo-pendientes` (con `--estado audio-estado.json` explícito — el default relativo
  `../audio-estado.json` apunta mal si no se corre desde `proyecto/`): 0 textos.
- 8202 `.ogg` reales en el repo, 8143 registrados en `audio-estado.json`, 0 registrados
  sin archivo físico (los 59 de más son audio grabado por adelantado, no un hueco).
- `ß` suelto en packs/ocurrencias 2026: 1 instancia, en `en-B1-2026.json`, dentro de una
  traducción al alemán — cae bajo la misma excepción ya documentada en la sección 7.5
  (alemán como idioma de interfaz, no como alemán suizo aprendido). No es un error.

✅ **Embebido por Code (sept 2026):** `app/src/main/assets/contenido/` se
regeneró componiendo TODO 2026 (piloto + los 7 pares parciales) y TODO 2027,
sin separar por año — ya no hay motivo para embeber un subconjunto (ver
sección 11 para los calendarios). 606 fichas + 56 semanas especiales (53
repaso, 3 Survival — el Survival trimestral de 2027 no es semana especial,
vive en el fin de semana, ver regla dura #11) confirmadas en los assets.

Audio re-verificado de forma independiente, no solo por el reporte de la
sección: `exportar_audio_maestro.py --solo-pendientes` (sin filtrar por año,
cubre Ejemplo+Expresión+Vocabulario) da **0 textos pendientes**, y de los
8202 archivos que `audio-estado.json` marca como grabados, **los 8202
tienen su `.ogg` físico** en `assets/audio/` (cotejado por nombre de
archivo, recursivo — están organizados en subcarpetas `DE/`/`EN/`, no
sueltos). Nada falta.

`ContenidoAssetsTest.kt` tenía los conteos del piloto solo (51/42/9):
actualizado a 662/606/56. `ContenidoSemanalScreenRenderTest.kt` (pantallazo
por cada archivo de `assets/contenido/`) pasa de 51 a 662 casos — sigue
siendo rápido (toda la suite completa en ~1m15s), no hizo falta acotarlo.
Dos tests de `ResolutorSemanaTest.kt` de la sección 11 asumían que las
fichas de los 7 pares parciales todavía no estaban embebidas (correcto en
ese momento); ahora que sí lo están, uno pasó a verificar que la semana 41
resuelve de verdad (antes: "sin contenido"), y el otro se reescribió con un
`contenidoPorId` sintético para seguir probando ese camino del resolutor
sin depender de que a los assets les falte algo.

Primer build con el año completo real: **APK debug 59,1 MB** (61 921 178
bytes), `assets/audio/` 54 MB (8202 `.ogg`), `assets/contenido/` 13 MB,
`assets/calendario/` 44 KB (10 archivos, sin cambios — 2027 sigue sin
calendario embebido, semanas de repaso 2027 sin dueño, sección 8).

`./gradlew compileDebugKotlin compileDebugUnitTestKotlin test assembleDebug`
limpio: 825 tests, 0 fallas.

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

## 11. Calendarios parciales 2026 (semana 41 en adelante) — ✅ 7 pares, por Code (sept 2026)

`generar_calendario.py` tiene un modo `--parcial` nuevo, para calendarios que
no cubren el año entero. Sin tocar el comportamiento de año completo ni el
piloto (de-B2, en-B2, en-C1, semanas 37-53, congelado por regla dura #11):

- **Skills:** con `--parcial`, si el banco no entra en las semanas libres se
  eligen tantos como semanas haya (uno cada uno, sin machaque). Prioridad
  fundacional > núcleo (`repiteEn` incluye el nivel) > resto, desempate por
  id. Los que no entran se imprimen por stderr, no se descartan en silencio.
- **Temas:** se exige `min(14, semanas de contenido)` temas distintos, no los
  14 — con 12 semanas de contenido, 14 es imposible por diseño.
- **Debut fundacional:** relativo al inicio del calendario (`w - desde + 1`),
  no a la semana ISO absoluta — con `desde=1` es exactamente lo mismo de
  siempre.

Generados los 7 pares que faltaban de 2026 (de-A2/B1/C1/C2, en-A2/B1/C2),
semanas 41-53, repaso en la 46, en
`proyecto/data/calendarios/2026-{idioma}-{nivel}.json` (mismo formato que ya
usan el piloto y los 10 pares de 2027). Skills elegidos y descartados por
par, tal cual imprimió el script:

| Par | Elegidos (12) | Afuera (para 2027) |
|---|---|---|
| de-A2 | F01,G01,F03,F04,G05,F05,G02,G07,G04,G03,F12,F02 | G06, G18, V01 (3) |
| de-B1 | F01,F08,F04,F05,G03,F06,F11,G07,G02,G01,F07,F02 | F12,F13,V02,V03,V05,V06,V08,G04,G05,G06,G08,G09,G10,G11,G13,G18,G19,G22,G23,G24 (20) |
| de-C1 | F01,F08,F04,F05,K02,F06,F09,K03,K01,G02,F07,F02 | F10,F11,F12,F13,F14,G05,G09,G10,G11,G12,G13,G14,G15,G16,G21,G26,G27,K04,K05,K06,K08,V02,V03,V04,V05,V06,V07,V08,V09 (29) |
| de-C2 | F01,F11,F07,F08,K02,F09,F13,K03,K01,F14,F10,F02 | K04,K05,K06,V02,V05,V06,V07,V08,V09,G12,G14,G25,G26,K07,K08 (15) |
| en-A2 | F01,F07,F03,F04,G03,F05,F08,G04,G02,G01,F06,F02 | G05,G09,G14,G18,V01,V02 (6) |
| en-B1 | F01,F09,F03,F04,F16,F05,F10,G02,F15,F11,F08,F02 | G03,G04,G25,V02,V04,V07,G05,G06,G07,G08,G09,G10,G11,G13,G14,G17,G18,G19 (18) |
| en-C2 | F01,F13,F04,F10,F18,F11,F14,K01,F17,F15,F12,F02 | K02,K03,K04,K05,V03,V05,V06,V07,V08,V09,G15,G16,G20,K06 (14) |

(Prefijo del idioma omitido en la tabla por espacio: `de-A2` "F01" es
`DE-F01`, etc.)

`generarCalendarioAssets` (`app/build.gradle.kts`) ya no lee `semanas-fijas.json`
para decidir qué combos embeber: ahora deriva la lista de qué archivos
`2026-*.json` existen en `data/calendarios/` (regla dura #10, nunca a mano).
Con esto los 10 pares de 2026 (3 piloto + 7 parciales) quedan embebidos en
`assets/calendario/`; **2027 sigue sin embeberse** (las semanas de repaso
2027 siguen sin dueño, sección 8).

🔴 **`contenido/` todavía no tiene fichas para estos 7 pares** — tarea
explícitamente diferida a los pasos siguientes. Efecto visible mientras
tanto: como `nivelesConContenido` deriva disponibilidad solo de
`assets/calendario/` (regla dura #10), Ajustes ya ofrece los cinco niveles en
alemán e inglés, pero las semanas 41-53 de los 7 pares nuevos muestran "sin
contenido" (no crashean: verificado con test — `EntradaCalendario` no tenía
el campo `desafioFinde` que trae todo calendario nuevo desde el Survival
trimestral, se agregó como opcional para que el parser estricto no rompiera
al cargar ninguno de los 10).

(Nota: esto se resolvió después — ver sección 12, "Embebido por Code".)

## 13. Transiciones lentas y etiqueta "Sistema" — ✅ arreglado (Code, sept 2026)

Fer reportó lentitud al navegar entre semanas, cambiar el idioma que se
aprende y volver de Ajustes a la pantalla principal. Perfilado antes de
tocar nada (no se puso ningún spinner): la causa real era
`resolverSemana()` (`datos/RepositorioSemana.kt`), que releía y reparseaba
los **662 JSON de `assets/contenido/` (13 MB)** en cada una de esas tres
transiciones — I/O sincrónico sobre `AssetManager`, en el hilo principal,
porque el resultado vive en un `remember()` de `MainActivity.kt` que se
descarta cada vez que esa composición se destruye (al navegar a Ajustes y
volver, por ejemplo). El `remember` de `PantallaPrincipal` ya estaba bien
keyeado (`fechaVista`, `idiomaActivo`, `nivelActivo`) — el problema no era
recomposición de más, era el costo real de lo que se recomputaba cada vez.

Arreglo: `contenidoPorId` se carga y parsea **una sola vez**, en
`MainActivity.onCreate()` (mismo lugar donde ya se cargaba una vez para
Guardados), y se pasa ya armado a `resolverSemana()`. El widget
(`SemanaGlanceWidget.kt`) usa el mismo cambio de firma.

**Medido, no "se siente más rápido"** (`RepositorioSemanaPerfTest.kt`,
nuevo — parsea los 662 JSON reales embebidos, no un mock): 20 transiciones
simuladas reparseando todo cada vez, **2444 ms**; las mismas 20 con el mapa
ya cacheado, **10 ms** — **239x**. El test queda como guarda de regresión
(falla si el parseo vuelve a colarse en el camino de resolución).

Además, en la misma pasada: `ClaveTexto.SEGUN_SISTEMA` (Ajustes) pasó de
una frase que no entraba en el ancho de pantalla en varios idiomas
("Según el sistema", "Selon le système"…) a una palabra por idioma
("Sistema", "System", "Système"…). Y `idiomaAplicacionEfectivo()`
(`presentacion/IdiomaAplicacion.kt`): cuando "según el sistema" está activo
pero el locale del dispositivo no es ninguno de los 6 idiomas soportados,
el fallback ahora es **inglés fijo** (`Idioma.EN`, decisión de Fer — el más
universal de los seis), no el último idioma elegido a mano como antes
(eso seguía siendo `idiomaInterfaz`, que arranca en español por defecto).
El caso "la opción está apagada" no cambió: ese sigue siendo `idiomaInterfaz`,
una elección explícita del usuario, no un fallback.

`compileDebugKotlin compileDebugUnitTestKotlin test`: limpio, 826 tests
(antes 825), 0 fallas.

## 14. Contraste bilingüe completo, `categoriasUso` y 7 fixes de UI — ✅ por Code (oct 2026)

**Schema (para Traducciones y Núcleos):**

- `contraste` — cada VALOR (no el campo entero) es ahora `oneOf` string plano
  (formato viejo) u objeto `{idioma: texto}` con dos claves: la del idioma que
  se aprende (el original, **obligatoria** en forma objeto y con `minLength` 20)
  y la del idioma de app que nombra la entrada (su traducción, sin `minLength`).
  Las entradas string y objeto conviven mientras Traducciones no termine.
  Verificado: 606/56/0 con los datos de hoy; forma objeto, sin clave del
  aprendido, original corto e idioma inválido probados con jsonschema.
- `redemittel[].categoriasUso` — array de strings libres, opcional, sin
  estructura bilingüe. La lista cerrada de categorías la aplica Núcleos por
  convención, no el schema.

**App:**

- El renderizado del contraste sigue el switch como el resto de los campos
  bilingües: la ENTRADA la elige el idioma de app; el IDIOMA (original o
  traducción, con fallback al original si falta la traducción) lo elige el
  switch, y el título también ("Kontrast zum Spanischen" / "Contraste con el
  español"). La sección ya no depende del switch para aparecer.
  Decisión a revisar: en fichas **no** bilingües (B2+, sin switch) el contraste
  se muestra en el idioma que se aprende, igual que antes.
- (a) `EtiquetasSeccion.kt`: tabla de títulos en los 6 idiomas ("redemittel" =
  "Expresiones" en español). Ya no se concatenan dos idiomas.
- (b) `*palabra*` se muestra «palabra» (cursiva + comillas angulares) cuando la
  ficha muestra la traducción; no duplica si el texto ya trae « ».
- (c) El switch se oculta si idioma de app == idioma que se aprende.
- (d) Ejemplos: el original siempre; la traducción debajo, tipografía chica,
  solo con el switch activo.
- (e) Interlineado: palabra+traducción pegadas, más aire entre ítems; en
  expresiones la función va debajo de la traducción.
- (f) El día de las microtareas sigue el idioma mostrado.
- (g) Etiqueta del switch: "Traducir al {idioma}" ("Translate to…", "Auf …
  übersetzen", "Traduire en…", "Traduci in…", "Traduzir para…"), fija: dice qué
  hace el switch, no qué idioma se ve. El switch ON = traducido (por defecto).

Además se corrigió un bug latente de la primera versión del switch: las glosas
de vocabulario/expresiones se resolvían con el idioma *mostrado*, así que con
el switch en el idioma aprendido caían al español fijo; ahora siempre usan el
idioma de app.

`compileDebugKotlin compileDebugUnitTestKotlin test`: 844 tests, 0 fallas.

## 15. Estado a 8-oct-2026 (verificado en clon fresco) — falta SOLO Code Ronda B

Verificado tras los commits aa953bd, 790f118, 5f9e1c2 y df6cf9e:

| Chequeo | Resultado |
|---|---|
| `componer.py` | 606 fichas, 56 semanas especiales, 0 con problemas |
| `categoriasUso` | 2624 de 2897 redemittel (91%); 273 con lista vacía = revisadas sin categoría (ver 7.6) |
| Contraste bilingüe | 1465 entradas en 293 núcleos, 0 strings planos restantes |
| Taxonomía | 33 valores en uso = 18 funciones + 15 patrones (los +3/+8 aprobados el 08-10); ninguno fuera de `data/categorias-uso.json` |
| Code Ronda A | Los 7 fixes de la sección 14 aplicados, 844 tests en verde |

**Pendiente, en este orden:**

1. **Code Ronda B** (única pieza que bloquea el cierre de esta tanda): Planilla del profesor (PDF local, al final de cada ficha) y Biblioteca (Vocabulario / Expresiones, pestañas por idioma, filtros de nivel/topic/uso, estrella sincronizada con Guardados). Notas para ese prompt:
   - `banco.json` no se embebe en assets: la Biblioteca necesita un JSON chico con `topicNombres` (T01-T14).
   - La Biblioteca de Expresiones filtra por `categoriasUso`; con 33 valores conviene agrupar los chips por rama (función comunicativa / patrón gramatical).
   - **Textos que siguen hardcodeados en español, fuera del alcance de Ronda A** (a incluir en Ronda B): `etiquetaClase()` en `ContenidoSemanalScreen.kt` ("Semana de repaso", "Semana Survival"; ~línea 676) y el botón "Copiar al portapapeles"/"Copiado" del prompt de corrección. Deben pasar por `TextosInterfaz` en los 6 idiomas.
   - Además `etiquetaDia` usa el idioma equivocado en repaso/Survival (ver nota previa de la sección 14, punto f solo cubrió microtareas).
2. 273 redemittel sin categoría: 221 son hueco de taxonomía léxica (decisión de Fer: tercera rama o 4-5 categorías: Registro, Formación de palabras, Variante regional, Elección léxica y connotación, Describir, Determinación); 52 son cola larga. No bloquea nada.
3. Idea nueva, sin decidir: prompt de conversación de voz para IAs externas (análogo al de corrección). Opinión en el chat; si se aprueba, es contenido nuevo dentro del 10-15%.

**Diferido / largo plazo (sin cambios):** tuteo unificado (6.1) hasta el primer idioma nuevo a aprender; sumar español (roadmap PDF entregado); estética, ícono y splash (esperan referencias de Fer); app única vs. una por idioma (APK ~60 MB, 90% audio); 2028 y el resto de 2027 necesitan packs, apariciones, audio y traducciones nuevos (núcleos y calendario se reutilizan); piloto 2026 congelado con su deuda conocida.
