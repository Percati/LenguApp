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

#### 6.1.1 — Voseo → tuteo en español: ✅ hecho (Traducciones, 10-oct-2026)

Ojo que son **dos ejes distintos** y solo uno está cerrado:

- **vos / tú (dentro del español): cerrado.** 138 cadenas en 77 archivos de
  `nucleos`, `packs` y `ocurrencias` pasaron de voseo rioplatense a tuteo
  neutro, en todos los niveles (no solo A2/B1). Incluye tres regionalismos de
  un solo país: `nomás` → «nada más», `el laburo` → «el trabajo» y
  `¿Te copás con…?` → «¿Te animas a…?». R6 pasó de 236 a 0.
- **usted / tú dentro del español: cerrado** (ver 6.1.2).
- **El trato en los otros idiomas (*vous*/*Sie*/*Lei*): sigue abierto**, tal
  como lo describe 6.1. Este pase fue solo del español.

#### 6.1.2 — «usted» → «tú» y léxico neutro en español: ✅ hecho (Traducciones, 10-oct-2026)

Decisión de Fer del 10-10-2026: **tú en todo**, también en la prosa de las
fichas de alemán, y **lista corta** de léxico (no se reescribe el léxico
entero). 319 cadenas en 94 archivos.

**Tratamiento.** 232 cadenas pasaron de usted a tú: 180 de misión,
micro-tareas y requisitos (casi todas en `ocurrencias/de-A2-2027.json` y
`de-B1-2027.json`), 48 de `promptCorreccion`, y 4 que se habían escapado por
tener el verbo detrás de una cursiva. No fue solo cambiar el imperativo: hubo
que pasar también los posesivos (`su` → `tu`), los pronombres (`le` → `te`),
los reflexivos de cortesía (`se perdió` → `te perdiste`) y los verbos del
resto de la oración (`¿Cómo paga?` → `¿Cómo pagas?`).

**Léxico aplicado:**

| Término | Sustituto | Por qué |
|---|---|---|
| `piso` (vivienda) | `apartamento` | `piso` como vivienda es solo de España |
| `compañero/a de piso` | `compañero/a de apartamento` | igual |
| `billete` (transporte) | `boleto` | el más entendido en América; **el `billete` de dinero se dejó** |
| `aparcar` | `estacionar` | `aparcar` es de España |
| `coche` | `auto` | `coche` es de España (y en México es el tren) |
| `ordenador` | `computadora` | `ordenador` es solo de España |
| `móvil` (sustantivo) | `celular` | `móvil` como teléfono es de España |
| `coger confianza` | `ganar confianza` | `coger` es vulgar en buena parte de América |

**Lo que se dejó, a propósito:**

- `camarero` (3): **no hay término neutro** — `mesero` en América, `mozo` en
  el Río de la Plata, `camarero` en España. Si se prefiere la forma mayoritaria
  de América, el cambio es a `mesero`; está sin hacer a propósito.
- `vale` (14): en todo el contenido es el verbo *valer* («vale la pena»), no el
  «¡vale!» de España.
- `tío`/`tía` (4): aquí son el tío y la tía de la familia, no el «tío»
  coloquial de España.
- `billete` (2): traduce `der Schein` y `a note`, o sea dinero, y ahí sirve en
  toda América.
- `el piso, la planta`: traduce `der Stock`, es la planta de un edificio.
- `acera`, `ascensor`, `ratón`: son los términos neutros.
- **27 `usted`/`ustedes` de registro formal**, que son el contenido que se
  enseña (Redemittel y ejemplos formales de queja, negociación y trato
  `du`/`Sie`), más tres filas que están en registro formal en los **seis**
  idiomas a la vez (alemán `Rufen Sie`, francés `Appelez`, italiano `Chiami`):
  «¡Llame a una ambulancia!», «¡Llame a una ambulancia, por favor!» y
  «¡Pase, por favor!». Y «Vaya novedad.», que es una interjección, no un
  imperativo.

**Regla R7** del verificador cubre las dos cosas (imperativo de usted en
posición de imperativo, y la lista corta de léxico), con las excepciones
explícitas en `EXENTAS_R7`. R6 además sumó las formas de *vosotros*, que
aparecían una vez («como ya sabéis» → «como ya saben»).

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

### 14.1 — Fragmentos sin « » y regla R8: ✅ hecho (Traducciones, 10-oct-2026)

P5 pedía que todo fragmento del idioma que se aprende fuera entre « », pero
R4 solo comprobaba que los « » estuvieran **balanceados**, no que existieran.
Resultado: 1370 cadenas de las fichas A2/B1 llevaban el fragmento sin marcar.
Ya están todas marcadas, y **R8** comprueba de ahora en más que existan.

**Conteo por idioma de destino** (cadenas corregidas): it 274, pt 267, fr 246,
es 232, de 189, en 162 — 1370 en total, en 75 archivos de `nucleos`, `packs` y
`ocurrencias`.
Campos más afectados: `notas`, `errores`, `autochequeo`, `promptCorreccion`,
`descripcion`, `subtitulo`, `mision/requisitos` y `microtareas/texto`.

**Qué NO mira R8, y por qué:**

- **`titulo`** — no lleva marcado de ningún tipo (tampoco `*cursiva*`, ver
  `reglas-fichas`) y tiene un tope de 80 caracteres que los « » hacen saltar:
  marcar «Temporal subordinate clauses (als, wenn, …)» lo llevaba a 87 y
  rompía el esquema. `cuadroReferencia.titulo` sí se marca (no tiene tope).
- **`redemittel[].funcion`** — son etiquetas semánticas casi idénticas entre
  idiomas («opinion»/«opinion»/«opinione»): todo lo que marcaba era cognado.
- **`vocabulario[].traducciones`** — si la glosa correcta es la misma palabra
  («la pollution», «le stress», «la privacy», «le wifi»), marcarla no aporta.
- **Fragmentos de menos de 3 caracteres** — `du`, `wo`, `ja`. Dan demasiado
  ruido; los que había se marcaron a mano.
- **Metalenguaje gramatical: sí se marca.** El repo ya lo marcaba 884 veces
  contra 470 sin marcar, así que se unificó marcándolo («present perfect»,
  «past simple», «Perfekt», «Präteritum», «Konjunktiv II», «phrasal verbs»).

**Lo que queda abierto:**

- **9558 fragmentos `*x*` sin « »** en los idiomas de destino. **No se
  tocaron a propósito**: en este repo `*x*` tiene dos usos, y la mayoría de
  esos 9558 **no** son fragmentos extranjeros sino énfasis o etiquetas del
  propio idioma de destino (`*Lun (10 min).*`, `*pour*` + infinitivo, el
  plural en `*-s*`). Convertirlos en bloque habría envuelto texto que no es
  extranjero. Si se quiere unificar `*x*` → `«*x*»` donde sí es un fragmento
  del idioma aprendido, hay que distinguir los dos usos primero.
- **140 `ß` en el contenido**, contra la ortografía suiza que usa el proyecto
  (p. ej. `auszuschließen` en `EN-G10-B1`). Es anterior a este pase y no entra
  en «solo agregar « »», pero conviene barrerlo.
- **Vouvoiement en francés** (`Écrivez`, `Concentrez-vous`, `Donnez-lui`): es
  el eje usted/tú en los otros idiomas, que sigue abierto en 6.1.

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
3. **Prompt de conversación de voz** (aprobado 8-oct): plantilla escrita en `contenido/plantillas/prompt-voz.json`, piloto solo EN B2 y C1, 2026; la app lo arma desde la ficha (pieza 2 de Ronda B). Fer lo prueba con voz real y itera la plantilla; recién después se decide extender a otros pares. El prompt completo de Code está en `prompts/9-code-ronda-b.md`.

**Diferido / largo plazo (sin cambios):** tuteo unificado (6.1) hasta el primer idioma nuevo a aprender; sumar español (roadmap PDF entregado); estética, ícono y splash (esperan referencias de Fer); app única vs. una por idioma (APK ~60 MB, 90% audio); 2028 y el resto de 2027 necesitan packs, apariciones, audio y traducciones nuevos (núcleos y calendario se reutilizan); piloto 2026 congelado con su deuda conocida.

## 16. Code Ronda B — ✅ implementada (Code, oct 2026), pendiente de revisión de Fer

Cuatro piezas, un commit cada una (`compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una; 890 tests al cierre):

1. **Textos fijos** (`9be2b39e`): `etiquetaClase()` ("Semana de repaso"/"Semana Survival"), "Copiar al portapapeles"/"Copiado" y el encabezado de repaso/Survival pasan por `TextosInterfaz` en los 6 idiomas; un test de cobertura por clave nueva.
2. **Prompt de voz** (`e6f07352`): `PromptVoz.kt` arma el prompt desde la ficha con la plantilla de `plantillas/prompt-voz.json` (sin tocar su texto); línea con placeholder sin datos se descarta; máx. 6 expresiones y 6 palabras núcleo; `oralMin` redondeado (default 6). Tarjeta debajo del prompt de corrección, solo si existe plantilla `{idioma}-{nivel}` (hoy en-B2 y en-C1). Título y ayuda en 6 idiomas.
3. **Planilla del profesor** — textos fijos en commit aparte (`646648a7`, **para revisión de Fer**: `contenido/plantillas/planilla-profesor.json`, 6 idiomas) y generador (`61aaec0e`): PDF A4 con `PdfDocument`, márgenes 20 mm, 1–2 páginas, escrito en `cacheDir/planillas/` y abierto por `FileProvider` + `ACTION_VIEW` (sin permisos de almacenamiento). Botón al final de toda ficha y semana especial.
4. **Biblioteca** (`0431f4e8`): botón "Biblioteca" en la barra superior. Pestañas por idioma aprendido, nivel multi-selección (por defecto el nivel actual del idioma; se cambia en la pantalla sin tocar Ajustes), Vocabulario por topic, Expresiones por `categoriasUso` agrupadas en funciones comunicativas / patrones gramaticales + "Sin categoría", buscador. Items deduplicados entre fichas. Sin persistencia de filtros (regla 4).

**Decisiones donde el prompt no cerraba:**
- **Identidad de Guardados = (idioma, tipo, texto)**, ya no (skill, tipo, texto): es lo que hace que la estrella de la ficha y la de la Biblioteca sean la misma fila. `skillIdOrigen`/`nivel` quedan como el origen de la primera vez que se guardó. Quitar borra todas las filas con ese texto (incluidas duplicados de versiones anteriores).
- `banco.json` no se embebe: `generarTopicNombresAssets` extrae solo `topicNombres`, y `categorias-uso.json` se copia a `assets/temas/` (ambos gitignored, como el calendario).

**Abierto / para Fer:**
- Los **nombres de topic y de categoría existen solo en español** (`banco.json`, `categorias-uso.json`): la Biblioteca los muestra tal cual en cualquier idioma de app. Traducirlos es decisión de contenido.
- Planilla: **sin subtemas** (no hay dato en la ficha; se usa solo el subtítulo) y **sin reciclaje pasivo de semanas anteriores** (opcional en el prompt, no implementado).
- **El PDF real solo se puede verificar en un dispositivo**: `PdfDocument` es nativo y no corre bajo Robolectric. Los tests cubren el layout (paginación contra `Bitmap`, ≤ 2 páginas en A2, C1, repaso y Survival) pero no los bytes del PDF.
- `etiquetaDia` en repaso/Survival: se corrigió el idioma del encabezado; los días dentro de las microtareas ya usaban el idioma correcto desde la Ronda A.

## 17. Code Ronda C — ✅ implementada (Code, oct 2026), pendiente de revisión de Fer

Tres commits (`compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada uno; 913 tests al cierre), más el parche de assets de Overview que ya estaba commiteado al empezar (`eec75739`, confirmado byte a byte contra `componer.py` antes de tocar nada — no hizo falta reaplicarlo).

0. **Que no vuelva a pasar** (`40b4db47`): tarea Gradle `verificarAssetsContenidoActualizados`, enganchada a `preBuild` (corre antes de compilar y de testear, como `generarCalendarioAssets` y las demás). Recompone con `componer.py` en un directorio de build y compara archivo por archivo contra `assets/contenido/`; si difieren en el set de archivos o en el contenido, el build falla con el comando exacto para regenerar. Elegida en vez de documentar el paso a mano: una regla dura que depende de que alguien se acuerde de correrla es el bug que la originó. Test nuevo en `BibliotecaTest` contra los assets reales: al menos una expresión de EN-C1 tiene `categoriasUso` no vacío y filtrar por "Dar razones" devuelve resultados.
1. **Biblioteca** (`f2212f20`): filtro de topic del Vocabulario pasado de `Row` con scroll horizontal a `FlowRow` (todos los chips visibles, en varias líneas). Botón "Quitar filtros" general (nivel al default del idioma, funciones comunicativas, patrones gramaticales y topic), visible solo si hay algo que quitar; botón corto "Quitar" junto a cada título de rama de `categoriasUso`, visible solo si esa rama tiene algo elegido. Guillemets: Biblioteca y Guardados mostraban la traducción y la función de un item con `Text()` plano, sin pasar por `textoConMarcado` — un `*cita*` se veía con asteriscos literales y una cita del idioma aprendido sin traducir dentro de una traducción nunca se envolvía en « » aunque la ficha sí lo hace. Corregido en las dos pantallas con el mismo criterio que ya usa `ContenidoSemanalScreen` (`angulares = idioma aprendido != idiomaBase`).
2. **Guardados** (`8653c21c`): reescrita. Lista vertical con la estrella al lado de cada fila (tocarla quita el item directamente, misma fila de Room que la Biblioteca). Orden — alfabético, nivel, fecha (el `id` de Room), tipo — cada uno ascendente o descendente, default fecha más reciente primero. Filtros en cascada — idioma (pestaña), tipo, nivel, topic (vocabulario), `categoriasUso` (expresiones) — donde cada faceta solo ofrece como clickeables los valores con contador > 0 calculado sobre las demás facetas activas, así que un click nuevo nunca puede llevar a una lista vacía. Topic/categorías se resuelven buscando cada guardado en la Biblioteca por (idioma, tipo, texto); Room no cambia. Sin persistencia de orden ni filtros (regla dura #4). Lógica pura en `presentacion/CascadaGuardados.kt`, testeada sin Robolectric en `CascadaGuardadosTest` (incluye un test de propiedad: ninguna combinación normalizada de filtros, sobre un set variado de niveles/tipos/topics/categorías, da una lista vacía).

**Decisiones donde el prompt no cerraba:**
- **El contraste de `ContenidoSemanalScreen` no se tocó.** El prompt pedía revisar dónde se omiten los guillemets, incluyendo "contraste". Al auditar con los assets regenerados: el campo `contraste` mezcla, bajo el mismo marcado `*cita*`, citas en el idioma de la ficha (que NO deben llevar «», porque son la misma lengua que el texto que las rodea) y citas en el idioma base (que SÍ). Ejemplo real, `EN-F01-C1-2026-1.json`, `contraste.es.en`: *"Spanish can hedge inside the verb (\*creo que sea\* vs. \*creo que es\*); English can't. \*I think\* is only a frame..."* — `*creo que sea*`/`*creo que es*` son español (ajeno al inglés de alrededor, deberían ir entre «») pero `*I think*` es inglés (la misma lengua del texto, NO debería). Las tres citas usan el mismo marcado `*…*`, así que no hay una señal en el dato para que el código distinga una de otra sin inventar una regla no pedida (y sin arriesgar envolver "I think" en «» por error). La solución real es de contenido, no de código: o separar la cita ajena con una convención propia al redactar `contraste`, o aceptar que ese campo nunca pasó por el mecanismo dinámico de `textoConMarcado` (de hecho buena parte de los campos bilingües de los nucleos, como `notas`/`errores` en A2/B1, ya resuelven esto distinto: el autor escribe la cita ajena con «» literales directamente en el texto traducido, sin depender de `angulares` en absoluto). Queda para que Fer decida el criterio de redacción; el código no cambia mientras tanto.
- **Orden de Guardados**: "cada uno con ascendente y descendente por separado" se interpretó como un selector de criterio (4 chips) más un toggle de dirección compartido (2 chips), no 8 combinaciones fijas por separado — es la UI estándar de orden y cubre literalmente "cada criterio se puede ver ascendente o descendente".
- **Autocorrección de filtros stale en Guardados**: cuando los `items` cambian debajo del filtro activo (se quita una estrella) y una combinación deja de tener soporte, no hay forma no ambigua de saber cuál de los filtros activos es "el culpable" sin una heurística arbitraria — se optó por volver TODA la combinación al default en vez de adivinar cuál filtro soltar. El mecanismo principal que impide una lista vacía por un click nuevo es otro: cada faceta solo muestra como clickeables los valores con contador > 0 ya calculado sobre las demás, así que un click nunca puede, por sí solo, producir una combinación vacía; `normalizarFiltros` es solo la red de seguridad para el caso ajeno a un click.
- Topic y categoría de un guardado no se multi-seleccionan (a diferencia de categorías en la Biblioteca): el prompt no lo pedía para Guardados y mantenerlo de selección única simplificó la cascada sin perder nada que se haya pedido.

## 18. Code Ronda D — ✅ implementada (Code, oct 2026), versión 0.2.0

El parche de Overview (`e131886f`, assets regenerados tras el marcado « ») ya estaba commiteado al empezar; `verificarAssetsContenidoActualizados` pasa. 917 tests, 0 fallas.

1. **Marcado « »** (`079a4457`). Test sobre los 662 JSON reales: ningún string, con y sin `angulares`, muestra asteriscos literales ni `««`/`»»` una vez renderizado (`MarcadoAssetsRealesTest`). Se encontraron y arreglaron en el render dos cosas: (a) una cita que ya trae « » adentro (`*«vorrei» – ich hätte gern, «potrebbe» – könnten Sie*`, en `DE-G09-B2` contraste it/de) daba `«««` con `angulares`; ahora no se envuelve; (b) el contraste y las líneas de `erroresContrastivos` se renderizaban con `angulares` del switch, lo que envolvía en « » un `*würde*` que es del mismo idioma que la línea; ahora van sin `angulares` (las citas ajenas ya vienen a mano). `angulares` se mantiene para el resto, incl. palabra objetivo de misión y microtareas. Pantalla real con EN C1, DE B2 y DE A2 (switch ON y OFF donde existe): sin asteriscos ni « » dobles.
2. **Planilla, tuteo** (`01f6d6a8`): `es` de `planilla-profesor.json` (introducí→introduce, Elegí→Elige, trabajá→trabaja); las otras cinco lenguas no se tocaron.
3. **Versión**: `versionCode 2`, `versionName 0.2.0`. APK debug 62 513 366 bytes (≈ 59,6 MiB). Manifest fusionado: sin `INTERNET` ni los cuatro permisos removidos (`WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE`, `ACCESS_NETWORK_STATE`); el único `uses-permission` es el permiso interno de AndroidX `…DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` (firma de la propia app).

**Observaciones (sin tocar, no eran de esta ronda):**
- El switch de traducción solo existe en fichas bilingües (A2/B1); en B2/C1 `angulares` nunca se activa, así que el marcado de esas fichas es 100 % el escrito a mano.
- Quedan voseos en contenido (p. ej. `¿Elegí …?` en autochequeos de `DE-F13-B1-2027-*`, `REVIEW-*-B1-2027-*`): es de Contrastes/Traducciones.
- Hay textos en español hardcodeados fuera de `TextosInterfaz`: `MainActivity.kt` (`SinIdiomaSeleccionado`) y el widget (`SemanaGlanceWidget.kt`, "Elegí un idioma en Ajustes.").
- Casos de datos que no se pueden decidir por regla: un `*x*` suelto en texto traducido podría ser ajeno al idioma de la línea sin « »; en las tres fichas muestreadas no aparece.

## 19. Revisión del 10-oct-2026 (feedback de uso de Fer) — reglas de prevención

Fer encontró cinco defectos en alemán B1 y la planilla. Causas verificadas:

| Defecto | Causa real | Dónde se arregla |
|---|---|---|
| Desafío del finde igual a la misión | La app reutilizaba la misión tal cual; el texto propio «era de otra conversación» y nunca se escribió | Code, tarea 4 de Ronda E (encuadre fijo, no 600 textos nuevos) |
| Último error común sin traducir (DE B1) | `erroresContrastivos` es string en el idioma aprendido en TODOS los niveles; en A2/B1 es el único campo de prosa sin forma bilingüe (481 ítems en 95 núcleos; el resto de A2/B1 está completo, verificado) | Esquema (hecho), Traducciones (prompt 10), Code tarea 5 |
| Filtros de Biblioteca vacíos | Los chips de topic y de categoría salían de listas fijas (14 topics, 33 categorías) en vez de los ítems del idioma. Hoy «Caso y declinación» está vacío en todo inglés | Code, tarea 3 |
| Topic de la planilla en español | `topicNombres` solo existe en español | Hecho en datos: `topicNombresI18n` y `categorias-uso.json › nombres`, 6 idiomas; Code tarea 1 y 2 los usa |
| Planilla a 2 carillas con 3 líneas | Sin regla de compactación | Code, tarea 2 |

**Decisión de Fer que se revierte:** los errores típicos ya no «no llevan traducción» en A2/B1 (decisión del 16-09-2026). En B2+ no cambia.

**Nuevo en el repo (parche de Overview):** `REGLAS-PREVENCION.md` (8 reglas, idioma de salida por superficie, checklist para sumar un idioma), regla dura 14 en ambos `CLAUDE.md`, `tools/verificar_consistencia.py` (R1 bilingüe A2/B1, R2 nombres en 6 idiomas, R3 textos de planilla, R4 « » balanceados, R5 categorías en la lista cerrada; hoy falla solo R1 con 1210 apariciones = la deuda de arriba), `publicacion/` (textos de tienda es/en/de en formato fastlane + LEEME), `docs/inspiracion-ui.md`, prompts 10 y 11.

**Orden de pasos (secuencial, uno por vez):**
1. Aplicar el parche de Overview.
2. Traducciones: `prompts/10-traducciones-errores-contrastivos.md`.
3. Avisar a Overview para regenerar assets.
4. Code: `prompts/11-code-ronda-e.md` (7 tareas, la última engancha el verificador al build, por eso va después del paso 2).

**Pendientes por cerrar:**
- Pasos 2 a 4 de arriba.
- Probar el APK 0.2.0 en el teléfono: planilla (PDF real), prompt de voz con Claude y ChatGPT, Guardados, instalación encima de la versión anterior.
- Dirección visual: ícono, pantalla de carga y UI (Fer elige referencias en `docs/inspiracion-ui.md`).
- Publicación: capturas, textos fr/it/pt, nombre definitivo, verificar que «A2 a C2» sea verdad para todos los pares.
- 273 expresiones sin categoría (221 por hueco de taxonomía léxica: decisión pendiente).
- Tuteo unificado (6.1) y sumar español: hacerlo con la checklist de la sección 3 de `REGLAS-PREVENCION.md`.
- Contenido: resto de 2027 y 2028 (packs, apariciones, audio, traducciones); app única o una por idioma (APK ~60 MB, 90 % audio).

## 20. `erroresContrastivos` bilingüe en A2/B1 — ✅ hecho (Traducciones, 10-oct-2026)

Los 481 ítems de `erroresContrastivos` de los 95 núcleos A2/B1 pasaron de string
plano a objeto `{idioma_aprendido: original, clave: traducción}`, cerrando P2 de
`REGLAS-PREVENCION.md`. El original quedó intacto byte a byte en los 481; B2, C1
y C2 siguen siendo strings, como manda el esquema. Verificado con `componer.py`
(606 fichas, 56 semanas especiales, 0 problemas) y `verificar_consistencia.py`
(240 fichas A2/B1, 0 problemas).

**Deuda que deja:** la conversión se hizo con un script de un solo uso, no por el
maestro de traducciones. `exportar_traduccion_maestro.py` e
`importar_traduccion_maestro.py` **no cubren `erroresContrastivos`**, así que
estos 481 ítems no aparecen en el Excel maestro ni en ámbar ni en verde: si Fer
quiere revisar o corregir una de estas traducciones, hoy tiene que editar el JSON
a mano. Agregar una hoja «Errores contrastivos» (idioma, nivel, skill, clave,
original, traducción) al par exportador/importador cuando haga falta ese
ida-y-vuelta, o antes de sumar un idioma nuevo que multiplique estos ítems.

**Recomendaciones (cortas):**
- Agregar integración continua en el repositorio (componer + verificador + tests en cada push): hoy depende de que alguien corra los comandos.
- Un test de «matriz de superficies» (idioma aprendido × idioma de app × pantalla) que falle ante texto en el idioma equivocado, para no depender de que Fer lo encuentre a mano.
- Antes de sumar español, ejecutar la checklist completa con un par de prueba (ES A2 o B1 con app en inglés) en una rama.

## 21. Code Ronda E — ✅ implementada (Code, oct 2026), pendiente de probar en el teléfono

Requisitos ya aplicados al empezar: parche de Overview (reglas de prevención, nombres en 6 idiomas, verificador) y de Traducciones (`erroresContrastivos` A2/B1) con los assets regenerados. 7 tareas + 1 chica, un commit cada una; `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una.

| Tarea | Qué se hizo | Regla |
|---|---|---|
| 5 (primero, por orden de dependencia) | `erroresContrastivos` pasa a `Map<String, List<TextoBilingue>>`: con los assets nuevos el modelo viejo (`List<String>`) ya no parseaba y no había build en verde sin esto. Con el switch activo sale la traducción, apagado el original, B2+ igual que antes. Test sobre las 481 filas reales | P2 |
| 1 | `NombresI18n` (clave → idioma → nombre, fallback español, luego la clave). Gradle extrae `topicNombresI18n`; `categorias-uso.json › nombres` se lee en la app. Biblioteca y Guardados muestran topic y categoría en el idioma de app; el valor guardado en `categoriasUso` sigue siendo el español. Test sobre los assets: falla si falta un idioma en alguna clave | P1 |
| 2 | Planilla: el topic sale en el idioma que se aprende. El layout se separó del dibujo (`maquetar`); si la 2.ª carilla queda con ≤ 3 líneas se prueba, en orden, interlineado/espacio → cuerpo hasta 9 pt → márgenes de 15 mm. Sobre las 662 fichas y semanas especiales reales: 659 entran en una hoja (406 gracias a la compactación), 3 quedan en dos con 4 líneas en la 2.ª (`DE-G25-C2-2027-1`, `DE-V05-B2-2026-1`, `DE-V05-B2-2027-1`: justo por encima del umbral, no se tocan), ninguna pasa de dos | P1, P8 |
| 3 | Biblioteca: los chips de topic y de categoría salen de los ítems del idioma de la pestaña, todos los niveles; con el filtro de nivel activo muestran su contador y un chip puede dar 0 en ese nivel pero nunca en todos. «Sin categoría» solo si hay expresiones sin categoría. Topic y categorías elegidos se vacían al cambiar de pestaña. Tests sobre los assets, por idioma, y de propiedad | P3 |
| 4 | Reto del fin de semana: encuadre fijo (título, línea, 4 reglas por nivel con `{oralMin}`, rótulo y la consigna en chico con solo 2 requisitos) en vez de repetir la misión; todo en `TextosInterfaz`. Test por nivel A2–C2 sobre fichas reales | P4, regla 13 |
| 6 | «Acerca de» (acceso desde la cabecera de la pantalla principal) y la línea fija bajo el nombre de la app; 6 idiomas; la versión sale del `versionName` del paquete | P7 |
| 7 | Tarea Gradle `verificarConsistenciaContenido`, enganchada a `preBuild`: corre `verificar_consistencia.py` sobre la recomposición de `componer.py` que ya hace `verificarAssetsContenidoActualizados` (se reutiliza ese directorio en vez de recomponer dos veces). Comprobado que falla al quitarle un idioma a un nombre de categoría | P6 |
| extra | Los dos únicos textos de pantalla que seguían hardcodeados en español (`SinIdiomaSeleccionado` y el widget) pasan a `TextosInterfaz` | P7 |

**Decisiones donde el prompt no cerraba:**
- **Orden**: la tarea 5 va antes que la 1 porque sin el modelo nuevo los assets regenerados no parsean y ningún commit habría tenido el build en verde.
- **Planilla, qué cuenta como «línea» de la 2.ª carilla**: líneas de texto ya envueltas (sin contar las viñetas «•», que acompañan a una línea). Si no entra ni con la compactación máxima, queda con el estilo normal en dos carillas (no con el más apretado).
- **«Acerca de»** se accede desde una cabecera nueva (nombre de la app + línea fija + botón), no desde la barra de cuatro botones, que a 150 % de fuente ya no tiene lugar para un quinto. La app no mostraba su nombre en la pantalla principal; ahora sí.
- **Texto de privacidad**: «solo lee la fecha» no era del todo cierto (idioma del sistema si el usuario lo elige; Guardados persiste lo marcado). Se dice con precisión, en los 6 idiomas.
- **Reglas del reto**: traduje yo las dos tandas de reglas, la línea y los rótulos a EN/DE/FR/IT/PT; conviene que Traducciones las revise (como el resto de `TextosInterfaz`). Redondeo de `oralMin` = el mismo que el prompt de voz (6 si no hay dato).

**Hallazgos (no tocados):**
- **Switch de traducción solo en A2/B1.** En B2+ el reto, los errores y el resto muestran siempre el idioma que se aprende (es lo que fija la tabla de superficies).
- **Pestañas de idioma** (Biblioteca, Guardados, principal) muestran el código (`EN`, `DE`…), no el nombre del idioma en el idioma de app. Es una elección de diseño, no un texto sin traducir, pero no es la tabla de superficies.
- **`widget_descripcion`** (`res/values/strings.xml`, el texto que muestra el selector de widgets de Android) sigue solo en español; requiere `values-xx/` por idioma y no pasa por `TextosInterfaz`.
- **Voseo en contenido** (`¿Elegí…?` en autochequeos de B1 2027, etc.): de Contrastes/Traducciones, como en la sección 18.
- **El PDF real** sigue sin poder probarse bajo Robolectric: hay que mirar la planilla compactada en el teléfono (cuerpo de 9 pt y márgenes de 15 mm se ven recién ahí).
- **Deuda de la sección 20** sigue abierta: `erroresContrastivos` no está en el maestro de traducciones.

## 22. Revisión de la Ronda E y siguiente tanda (10-oct-2026)

Verificado por Overview: el parche de Code (9 commits) aplica limpio sobre `ab355c92`; si `git am` dice que ya está, es porque Code commiteó en la carpeta local de Fer y falta solo el `git push`.

**Decisiones sobre los hallazgos de Code:**

| Hallazgo | Veredicto | Dónde |
|---|---|---|
| 3 planillas en 2 carillas con 4 líneas | No vale la pena (3 de 662); mirarlas en el teléfono | — |
| Pestañas de idioma con código `EN`/`DE` | Se arregla (nombre en el idioma de app) | Code, Ronda F, tarea 1 |
| `widget_descripcion` solo en español | Se arregla | Code, Ronda F, tarea 2 |
| Voseo en el español del contenido (~85 cadenas, sobre todo traducciones de Redemittel) | Se arregla ahora y queda cubierto por la regla R6 del verificador | Traducciones (prompt 12) |
| Maestro de traducciones sin `erroresContrastivos` | Diferido hasta sumar un idioma | sección 20 |
| Switch de traducción solo en A2/B1 | Es el diseño, no un defecto | — |

**Hallazgos propios de esta revisión:**
- `PRIVACY.md` (raíz y `proyecto/legal/`) decía «nada sobre vos… ni progreso guardado» y «solo lee la fecha»: ya no era cierto (Guardados persiste las estrellas; el idioma del sistema se lee si el usuario lo elige). Corregido, y en el mismo parche los textos de tienda en es/en/de.
- Los textos de interfaz que escribió Code en portugués son de Portugal («telemóvel», «a aprender»), pero la voz y el contenido son de Brasil. Además FR y PT usan trato de usted mientras ES/DE/IT/EN tutean. Se unifica en Ronda F, tarea 3.
- Nueva regla R6 en `verificar_consistencia.py`: el español del contenido es neutro (lista aproximada de formas de voseo; no reemplaza la revisión humana de -ás/-és/-ís).

**Orden de pasos (secuencial):**
1. Subir lo que Code ya commiteó (`git push`) y aplicar el parche de Overview de esta ronda.
2. Traducciones: `prompts/12-traducciones-tuteo.md`.
3. Avisar a Overview para regenerar assets.
4. Code: `prompts/13-code-ronda-f.md` (versión 0.2.1).
5. Probar el APK en el teléfono.


## 23. Verificación del 10-oct-2026 (push de Code Ronda E + parche de Overview) y cierre de menores
- Verificado en clon fresco de `origin/main` (`43034f4e`): 606 fichas, 56 semanas especiales, 0 con problemas; assets idénticos a la recomposición (0 diferencias); sin `INTERNET` en el manifest; versión aún 0.2.0 (sube a 0.2.1 en Ronda F).
- Verificador: R1-R5 en verde; R6 (voseo) falla con ~196+ avisos hasta que Traducciones termine `prompts/12`. Después: verificar su parche, regenerar assets (parche aparte de Overview) y recién ahí Code Ronda F.
- Cerrado ahora por Overview: textos de tienda fr-FR, it-IT, pt-BR (informales; pt de Brasil). Pendiente: revisión de un hablante antes de publicar.
- Sumado a Ronda F (`prompts/13`, tarea 6): test de la matriz de superficies.
- Sigue abierto: dirección visual (ícono, pantalla de carga, UI), nombre definitivo, capturas, 273 expresiones sin categoría, maestro de traducciones sin `erroresContrastivos` (sección 20), 2027/2028, CI en el repo.

### 23.1 Decisiones de Fer del 10-oct-2026 (español neutro)
- Léxico: **lista corta** de sustituciones (no revisión total). Tratamiento: **tú en todo**, también en la prosa de alemán.
- Va a Traducciones como `prompts/14-traducciones-neutro-lexico-usted.md`, **antes** de Code Ronda F. Incluye la regla R7 del verificador.
- Medido (assets, 10-oct): ~438 imperativos de «usted» en ~145 archivos; léxico peninsular: piso 40, billete 60 (mucho es dinero, válido), aparcar 9, coche 8, ordenador 6, móvil 6, camarero 3, tío/tía 4, coger confianza 1.
- Parche de assets tras el tuteo (`9cd4de7d`) aplicado y verificado: recomposición idéntica, 0 diferencias.

### 23.2 Español neutro parte 2 — ✅ hecho (Traducciones, verificado por Overview 10-oct-2026)
- 232 cadenas «usted»→«tú», lista corta de léxico aplicada (piso→apartamento, billete de transporte→boleto, aparcar→estacionar, coche→auto, ordenador→computadora, móvil→celular, coger confianza→ganar confianza), regla R7 y formas de «vosotros» en R6. R1-R7 en 0; assets regenerados.
- Excepciones aceptadas: 27 «usted/ustedes» y 3 filas formales en los 6 idiomas (contenido que se enseña), `billete` de dinero, `camarero` (sin término neutro), menciones metalingüísticas («computadora/ordenador»).
- Abierto: `camarero` → `mesero` si Fer lo decide antes de publicar (parche de dos líneas).

## 24. Code Ronda F — ✅ implementada (Code, oct 2026), versión 0.2.1

Requisitos ya aplicados al empezar: parche de Overview y tuteo de Traducciones con los assets regenerados (R1-R7 en 0). 6 tareas + 1 corrección, un commit cada una; `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una.

| Tarea | Qué se hizo |
|---|---|
| 1 | Pestañas de idioma (Biblioteca, Guardados, principal) con el **nombre en el idioma de app** (`nombreIdioma`), no `EN`/`DE`. Se corrigió además la tabla: `Italiano` en la columna francesa era `Italien` (error de transcripción heredado de AJUSTES-FASE-9). Test: ninguno de los 36 nombres es un código |
| 2 | `widget_descripcion` en `values/` (inglés, respaldo) + `values-es/de/fr/it/pt`. Test: todo string visible de `values/` existe en los otros cinco; el manifest y `xml/` solo apuntan a `@string` (`app_name`, nombre propio, queda único) |
| 3 | **Portugués de Brasil** en TextosInterfaz, EtiquetasSeccion y planilla (celular, aplicativo, **Configurações** en vez de Ajustes, **Salvos** en vez de Guardados —que además convivía con «Remover de Salvos»—, «em uma única gravação», «dá para entender?», Autoavaliação, Microtarefas, Buscar…). **Informal en los 6**: francés pasa de *vous* a *tu* en toda la interfaz; la planilla mantiene su registro (es para el profesor), salvo la variante PT, que pasa a Brasil. Tests: lista corta de portugués europeo en las cadenas PT, nada de *vous* en FR, sin trato de usted en ES/IT/DE/EN |
| 4 | Acerca de vs `PRIVACY.md`: **diferían**. El texto de la app decía «lo único que se guarda son las estrellas» y omitía los ajustes (que `PRIVACY.md` sí menciona). Se corrigió el texto de la app en los 6 idiomas (aunque el prompt decía que el de la app mandaba: era el que estaba incompleto). Test con las ideas clave por idioma y comprobación de `PRIVACY.md` |
| 5 | `versionName 0.2.1`, `versionCode 3` |
| 6 | `MatrizSuperficiesTest`: (en, de) × 6 idiomas de app × (A2, B1, B2) sobre fichas reales; afirma el idioma de salida de rótulos, reglas del reto, nombres de topic/categoría, prosa de la ficha con y sin switch, errores contrastivos, traducciones de vocabulario/expresiones, planilla y prompts |

**Decisiones donde el prompt no cerraba:**
- **Hallazgo de la matriz (cambia comportamiento):** el prompt de corrección seguía al switch: en A2/B1 con el switch activo se mostraba **y se copiaba** en el idioma de app, contra la tabla 2 («prompt de corrección → idioma que se aprende»). Ahora va siempre en el idioma que se aprende (el título de la tarjeta sigue a la ficha). Revertible en una línea por superficie (`promptEnIdiomaAprendido` en `Presentacion.kt`); Fer decide si prefiere ver el prompt traducido.
- **Voseo en el español de la interfaz** (que el prompt no mencionaba): `Pulsá`, `Idioma que aprendés`, `Tocá`, `Pegalo`/`Decile`, y `móvil` → tuteo neutro/celular. Test de la lista de formas.
- **Ajustes**: las pestañas de idioma de la pantalla de Ajustes (chips de idiomas aprendidos y de idioma de app) siguen mostrando el código `EN`/`DE`; no son pestañas, sus tests dependen del código y no estaba pedido. Queda anotado.
- **Pestañas de Guardados**: la fila de cada ítem sigue mostrando `DE · B2 · DE-V02` (origen técnico), no es una pestaña.

**No se puede probar sin UI** (anotado en el encabezado del test): el cableado de cada `Text` de `ContenidoSemanalScreen` a la función correcta, el switch como control y el copiado al portapapeles; lo cubren los tests de pantalla existentes.

**Sigue abierto:** las 3 planillas en dos carillas (a mirar en el teléfono), revisión humana de fr/it/pt (tienda y ahora la interfaz en pt-BR), `camarero`→`mesero` (23.2), dirección visual, 273 expresiones sin categoría, `erroresContrastivos` en el maestro de traducciones (sección 20), 2027/2028.

## 25. Dirección visual (decidida por Fer, 10-oct-2026) — Code Ronda G
- Temas: Papel (por defecto), Terracota, Mar, Grafito + «Sistema» (color dinámico M3); claro/oscuro/sistema aparte. Tipografías: Sistema, Lectura (Literata), Clara (Atkinson Hyperlegible), todas OFL y embebidas. Referencias: Bear (paletas y tipografías), Material 3 (calidad), Things 3 / Day One / Readwise Reader (fichas, Guardados, Biblioteca). Anti-referencia: Duolingo.
- Prompt: `prompts/15-code-ronda-g-diseno.md`. Ícono del lanzador y arte de la splash esperan el archivo de Fer (ronda H). Paleta para ícono/splash: la elige Fer entre las cuatro.
- Se descartó para la UI el segundo color decorativo de cada paleta: la estrella usa el acento (los acentos secundarios amarillo/coral no pasan 3:1 sobre el fondo claro). Solo sirve para ícono/splash.
- Pendiente de Ajustes: los chips de idioma muestran `EN`/`DE` (tarea 3 lo cierra si es trivial).

## 26. Feedback de uso del 10-oct-2026 (tarde) — qué hacer y en qué orden
| # | Hallazgo de Fer | Causa verificada por Overview | Dónde se arregla |
|---|---|---|---|
| 1 | Encabezado ocupa mucho alto | `EncabezadoApp` en la pantalla principal | Code F2, tarea 4 |
| 2 | Palabras en otro idioma sin « » en fichas traducidas (ej. `EN-F01-B1`: «I think es neutral…») | R4 comprueba balance, no existencia. ~100 cadenas solo en EN→es (heurística) | Traducciones, prompt 17 (regla R8) |
| 3 | Desafío = copia de la misión; viñetas con demasiado espacio | Reto muestra consigna y 2 requisitos de la misión; espacios en dp fijos | Code F2, tareas 2 y 3 |
| 4 | «Acerca de» corto | — | Code F2, tarea 5 |
| 5 | **Guardados muestra solo la traducción** | `FilaGuardado` usa `texto.resolver(idiomaBase, …)` y la función en idioma de app; Biblioteca muestra original + traducción + función en idioma aprendido | Code F2, tarea 1 (un composable compartido) |
- Orden: Traducciones (17) → Overview regenera assets → Code F2 (18, versión 0.2.2) → [si Fer lo confirma: exportar/importar Guardados e índice de habilidades] → Code Ronda G (15) → ronda H (ícono y splash).
- Guía para testers (qué es decisión de diseño y qué es un error): `docs/guia-testers.md` y `docs/guia-testers-lenguapp.pdf`. Actualizar con cada versión.
- 10-oct-2026: Fer confirmó exportar/importar Guardados (TSV Anki + JSON de respaldo) e índice de habilidades (vista dentro de la Biblioteca): tareas 6 y 7 del prompt 18.
