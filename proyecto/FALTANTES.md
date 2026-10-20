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
- **El trato en los otros idiomas (*vous*/*Sie*/*Lei*): cerrado** (ver 6.1.3,
  11-oct-2026). Este pase fue solo del español.

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

#### 6.1.3 — *Sie* / *vous* / *Lei* → tú en alemán, francés e italiano: ✅ hecho (Traducciones, 11-oct-2026)

Cierra 6.1 para el contenido ya generado de alemán e inglés. **1.207 cadenas
de instrucción** en 122 archivos: **alemán 423**, **francés 515**,
**italiano 269**. No fue cambiar el imperativo: cambió la persona del verbo
en toda la oración, los pronombres, los posesivos y los reflexivos
(`Schreiben Sie … Ihre` → `Schreib … deine`; `Concentrez-vous … votre
famille` → `Concentre-toi … ta famille`; `Si concentri … la sua opinione` →
`Concentrati … la tua opinione`).

**Dónde sí y dónde no.** Solo los campos donde la app le habla al
aprendiente: `mision`, `microtareas`, `autochequeo`, `promptCorreccion`,
`notas`, `descripcion`, `subtitulo`, `requisitos`. Quedan fuera a propósito
`redemittel`, `ejemplos`, `cuadroReferencia`, `contraste`,
`erroresContrastivos` y `vocabulario`: ahí el registro formal puede ser el
contenido que se enseña. Tampoco se toca lo que va entre « » ni entre
`*…*`, salvo en alemán cuando el idioma de la instrucción es también el que
se aprende: ahí la palabra marcada forma parte de la gramática de la frase y
hay que conjugarla (`Was *bestellen* Sie gern?` → `Was *bestellst* du
gern?`).

**Cómo se comprueba: R9** en `verificar_consistencia.py` (regla P9), con
`EXENTAS_R9` — 32 excepciones, cada una con su motivo — y `--autotest`. La
regla detecta el 100% de lo que se corrigió, con 0 falsos positivos sobre el
contenido actual.

**Los tres detalles del francés que hubo que decidir:**

- `votre` → `ton` o `ta` según el género del sustantivo, pero `ton` también
  delante de vocal aunque el sustantivo sea femenino (`ton opinion`, `ton
  école`, `ton équipe`).
- Los sustantivos alemanes dentro de `*…*` se tratan como **masculinos**
  (`ton *Schule*`, `ton *Arbeitszeit*`), que es el uso francés para palabras
  extranjeras no asimiladas. Si se prefiere el género del alemán, es un pase
  aparte de ~13 cadenas.
- `rendez-vous` es un sustantivo, no un imperativo con pronombre: R9 lo
  exceptúa en las dos posiciones del token.

**Lo que R9 no puede ver (leer a ojo al escribir contenido nuevo).** En
italiano la cortesía usa la 3.ª persona del indicativo, idéntica a una 3.ª
persona real: `Scriva che cosa ha comprato ieri` (usted compró) y `che cosa
ha detto il medico?` (lo dijo el médico) tienen la misma forma. **15 cadenas**
de esa clase se arreglaron a mano porque ninguna regla las marcaba. Las que
de verdad hablan de otra persona se dejaron como estaban y están en
`EXENTAS_R9` con su motivo.

**Otros dos casos que quedaron a propósito:**

- `vous` de plural real (`Toi et un ami, vous voulez cuisiner…`): no es
  cortesía, es sujeto plural. En `EXENTAS_R9`.
- `Sie` como contenido en la ficha `DE-V08` (que enseña justamente `du`
  frente a `Sie`) y las etiquetas `(du)` / `(Sie)` que marcan el registro
  pedido en cada mensaje. En `EXENTAS_R9`.

**Efecto colateral del schema.** El imperativo informal alemán es más corto
que el de cortesía, y **11 `promptCorreccion` de núcleos `de` quedaron bajo
el mínimo de 150 caracteres** que el schema le exige al original. Se
alargaron en estilo de la casa, en los seis idiomas del campo, con una frase
útil para un prompt de corrección (`Markier jeden Fehler einzeln.` /
`Nenn zu jedem Fehler die Regel.`). Son los núcleos `DE-F01-B1`,
`DE-F02-B1`, `DE-F07-B1`, `DE-F12-B1`, `DE-G01-A2`, `DE-G02-A2`,
`DE-G04-A2`, `DE-G13-B1`, `DE-V01-A2`, `DE-V03-B1` y `DE-V05-B1`. Si se
prefiere bajar el mínimo del schema en vez de alargar el texto, es una
decisión de Code y estas 11 frases se pueden quitar.

**Lo que sigue abierto:** los fragmentos alemanes marcados con `*…*` dentro
de una instrucción en otro idioma siguen en formal (`*interessieren Sie
sich*`, `*beschweren Sie sich*`, `*entscheiden Sie sich*`, `*fühlen Sie
sich*`, `*treffen Sie sich*`, `*leisten Sie sich*`, `*informieren Sie
sich*`). Son la expresión que se enseña, no instrucción, así que R9 no mira
ahí. Si el criterio es que el aprendiente vea siempre `du`, hay que tocar
`redemittel` y `vocabulario`, no las instrucciones — y eso cambia el lema
que se enseña, así que es una decisión aparte.

#### 6.1.4 — El tuteo que R9 nunca miró: las cadenas planas de B2/C1/C2: ✅ hecho (Traducciones, 11-oct-2026)

El prompt 19 pidió marcar el registro «bajo las claves `de`, `fr`, `it`», y
eso dejó afuera **todas las cadenas planas**: en B2/C1/C2 la prosa de
instrucción es monolingüe, así que el campo es un `string` y R9 no lo
miraba. **458 cadenas** en 128 archivos pasaron a `du`.

**Conteo real por campo y nivel** (solo alemán; el inglés no distingue trato
y `fr`/`it` no tienen ni una cadena plana — comprobado):

| campo | B2 | C1 | C2 | total |
|---|---|---|---|---|
| `apariciones/mision/consigna` | 62 | 60 | 60 | 182 |
| `promptCorreccion` | 39 | 41 | 27 | 107 |
| `apariciones/microtareas/texto` | 35 | 41 | 17 | 93 |
| `apariciones/mision/requisitos` | 6 | 16 | 18 | 40 |
| `microtareas` (semanas especiales) | 16 | 6 | 1 | 23 |
| `consigna` (semanas especiales) | 3 | 5 | 5 | 13 |
| **total convertido** | **161** | **169** | **128** | **458** |
| `notas` / `descripcion` / `autochequeo` / `subtitulo` (exentas) | 11 | 5 | 3 | 19 |

**Dónde difiere de la tabla del prompt 26.** Las filas de **A2 y B1 de esa
tabla no existen**: en A2/B1 los campos de instrucción son objetos bilingües
y el prompt 19 ya los había pasado a `du`. Las únicas cadenas planas de
A2/B1 son las 360 de `microtareas/dia` (`lun`, `mie`, `vie`), que no son
prosa. Eso explica la diferencia entre el «~699» del encabezado y las 477
que mide R9: 477 − 19 exentas = 458 convertidas. Por campo, en B2/C1/C2 las
dos mediciones coinciden salvo tres celdas de B2 (micro-tareas 51 contra 54,
notas 6 contra 5, descripción/subtítulo 3 contra 4), diferencia de
convención al contar, no de contenido.

**Las 19 exentas, en dos clases** (todas en `EXENTAS_R9` con su motivo):

- **`Sie`/`Ihr` de tercera persona** (8): la prosa de los núcleos habla *de*
  las formas gramaticales. «Modalpartikeln … **Sie** sind der Hauptgrund
  dafür, dass …», «Ein Funktionsverbgefüge … **Sie** sind das Kennzeichen des
  deutschen Bürokratiestils», «**Ihr** völliges Fehlen wirkt steif». La forma
  es idéntica a la de cortesía y ninguna regla puede distinguirlas.
- **Contenido enseñado** (11): el contraste du/Sie de las fichas de registro
  y los ejemplos citados en formal. «**Sie** und du dürfen innerhalb eines
  Textes nicht gemischt werden», «Ich hätte gern, dass… ist höflicher als
  **Sie müssen das reparieren**», «Ist die Anrede (du oder **Sie**) im ganzen
  Text konsequent?», «Du oder **Sie** — beim Amt und unter Kollegen».

**Lo que hubo que arreglar a mano: 30 cadenas.** 27 por el residuo que el
transformador no resolvía (`Sie` como objeto — «was Sie daran hält» →
«was dich daran hält»; sujeto compuesto — «Sie und ein Kollege sollen» →
«Du und ein Kollege sollt»; recíprocos — «wie Sie sich … annähern» → «wie
ihr euch … annähert») y 3 defectos que encontró el barrido a ojo y los
chequeos mecánicos no veían:

- `*Bildungsideal* vertreten Sie?` quedaba como `*Bildungsideal* vertritt?`,
  sin sujeto: tras una marca `*…*` o un guion, un verbo en **minúscula** es
  una inversión interrogativa, no un imperativo. La regla del transformador
  ahora exige mayúscula en esa posición.
- `— hören Sie den Unterschied?` → `hörst du den Unterschied?`, mismo caso.
- Dos auxiliares coordinados al final de la oración que se quedaron en plural
  («und später erfahren **haben**» → «erfahren **hast**»).

**Efecto colateral del schema, otra vez.** El imperativo informal es más
corto, y **14 `promptCorreccion` de núcleos `de` de B2/C1/C2** quedaron bajo
el mínimo de 150 caracteres. Se alargaron una frase en estilo de la casa
(`Markier jeden Fehler einzeln.` / `Nenn zu jedem Fehler die Regel.`); son
monolingües, así que no hubo nada que sincronizar en los otros cinco
idiomas. Núcleos: `DE-F01-C2`, `DE-F07-C2`, `DE-F08-B2`, `DE-F08-C2`,
`DE-G15-C1`, `DE-G17-B2`, `DE-G19-B2`, `DE-G26-C1`, `DE-G27-C1`,
`DE-V03-C1`, `DE-V04-B2`, `DE-V06-C1`, `DE-V07-B2`, `DE-V08-C1`. Con los 11
del prompt 19 son **25 en total**; si se prefiere bajar el mínimo del schema,
es decisión de Code y las 25 frases se pueden quitar.

**Una excepción de R10 que hubo que reescribir:** el `promptCorreccion` de
`DE-V08-B2` está en `EXENTAS_R10` porque habla de la grafía «ss/ß», y el
cambio de registro le cambió el texto. Las excepciones van por texto exacto,
así que cualquier pase que toque una cadena exenta tiene que actualizar la
excepción en el mismo commit; el `--autotest` avisa cuando una queda de más.

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

## 7.5 Ortografía suiza (ss vs ß) — ✅ cerrado, una sola excepción (revisado 11-oct-2026)

Regla (`contenido/nucleos/DE-V08-B1.json`): en alemán suizo se escribe
siempre `ss`, nunca `ß`.

**Cambio del 11-oct-2026 — ojo, revierte la segunda excepción que decía
antes esta sección.** El pase R10 de Traducciones (prompt 19) pasó a `ss`
las **90 cadenas** con eszett que quedaban (98 marcas, 53 archivos), y
`verificar_consistencia.py` **R10** lo comprueba de acá en adelante. Queda
una sola excepción, en `EXENTAS_R10` con su motivo:

- **La eszett como objeto de la explicación** (42 cadenas, fichas
  `DE-V08-B1/B2/C1` en los seis idiomas): «Im Schweizer Hochdeutsch
  schreibt man kein ß», «Kein ß im Text?», la grafía «ss/ß», y la columna
  alemana del cuadro de `DE-V08-B2` (`Straße, groß, heißen`) que existe
  justamente para contrastar con la suiza.

**Lo que cambió respecto de la versión anterior de esta sección.** Antes
había una segunda excepción: la clave `de` de `contraste` y
`erroresContrastivos` en los núcleos `EN-*` conservaba `ß` con el argumento
de que ahí el alemán es el **idioma de interfaz** para un usuario
germanohablante (Alemania/Austria/Suiza), no el alemán suizo que se enseña.
Ese argumento sigue siendo razonable, pero choca con dos cosas más nuevas:
la sección 27 registró esas cadenas como deuda a corregir, y el prompt 19
pidió `ss` en **todo** texto alemán salvo la letra como objeto. Se aplicó lo
pedido: una sola regla, sin depender de qué papel cumple el alemán en cada
campo.

**Si se prefiere volver al criterio anterior**, son **29 cadenas** (28 de
`contraste`, 1 de `erroresContrastivos`, todas en núcleos `EN-*`): hay que
devolverles el `ß` y agregarlas a `EXENTAS_R10` con ese motivo. El resto de
las 90 (notas, autochequeo, ejemplos, cuadros, descripciones, prompts y
redemittel) no estaba exceptuado por la versión vieja de esta sección, así
que ahí no hay nada que decidir.

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

### 25.1 Code Ronda G — ✅ implementada (Code, 11-oct-2026)

Seis tareas, un commit cada una; `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una (1057 tests al cierre).

- **Tarea 0 (`claseVerbo`)**: `VocabularioItem.claseVerbo: List<String>` (sin mostrar), assets regenerados (606 / 56 / 0), tests con fichas reales (`trennbar`, `phrasal-separable`, varias clases, `simple`, taxonomía cerrada). Los dos guardas de `preBuild` en verde.
- **Tarea 1 (temas)**: PAPEL (defecto), TERRACOTA, MAR, GRAFITO + SISTEMA (color dinámico M3, solo API 31+; `minSdk` es 26: en 26-30 no se ofrece y, si estaba guardado, se ve como Papel). Valores del documento sin retocar. Migración `ACADEMIA→PAPEL`, `EDITORIAL→MAR`, desconocido→PAPEL, sin perder otros ajustes. `ContrasteWcagTest` mide los `ColorScheme` reales: los 4 temas × 2 modos, **todos los pares ≥ 4,5:1, ninguno falló** (incluido el acento como texto sobre fondo y superficie). Se completó el `ColorScheme` (`surfaceContainer*`, `outlineVariant`, `inverse*`) con tonos de la paleta para que no se filtren los violetas por defecto de M3. **Enmienda de la regla 2** (CLAUDE.md ×2): el color dinámico es presentación local, como el modo oscuro del sistema.
- **Tarea 2 (tipografías)**: Sistema / Lectura (Literata) / Clara (**Atkinson Hyperlegible Next**, la sucesora de 2024 en el repo oficial de Google Fonts; la original solo tiene Regular y Bold, sin Medium). Variables OFL 1.1, eje `wght` recortado a 400-700, un archivo redondo y otro cursiva por familia, subset Latin + Latin Extended (U+0020-024F) + combinantes + puntuación (« » ‹ ›); sin hinting; **4 archivos, 380 KB** (presupuesto +1,5 MB). Licencias y receta reproducible en `LICENSES/`. Se aplican a los 15 estilos de la escala. Glifos que **ninguna** de las dos trae de origen y el contenido usa: `↔` y `✗` (Literata); `→ ↔ ₂ ✗` (Atkinson): Android los dibuja con la fuente del sistema; el test fija esa lista. No hay caracteres fonéticos (IPA) en el contenido.
- **Tarea 3 (Ajustes)**: orden idioma → tema → modo → tipografía. Tema: nombre + 3 puntos (fondo, superficie, acento) del modo actual; tipografía: el nombre en su propia fuente con «Aa». Los chips de idioma de Ajustes ya muestran el nombre (el idioma que se aprende en el idioma de app; el idioma de la aplicación, cada uno con su propio nombre), cerrando el pendiente de la sección 25.
- **Tarea 4 (lectura y listas)**: columna de lectura de 640 dp máx. centrada; interlineado del cuerpo 1,5; Guardados, Biblioteca e índice de habilidades con fila + línea fina (`HorizontalDivider`) en vez de tarjetas; estrella de contorno discreto; estados vacíos como invitación breve (6 idiomas); fundido de 180 ms entre pantallas (Compose respeta «Quitar animaciones» del sistema). La planilla PDF no cambió.
- **Tarea 5 (splash)**: `androidx.core:core-splashscreen:1.0.1`, fondo del tema Papel (claro/oscuro), sin ícono (drawable vacío). **Un solo lugar para la ronda H**: `res/values/themes.xml` (`Theme.LenguApp.Splash`) + `res/drawable/splash_vacio.xml` para el glifo de la splash, y en `AndroidManifest.xml` agregar `android:icon` / `android:roundIcon` con el adaptive icon (capa monocromática para íconos temáticos); hoy no hay `android:icon`.
- **Tarea 6 (tercera vista en la Biblioteca)**: `SelectorDeVistas` y `GrupoChips` reutilizables (`ui/FiltroChips.kt`); el selector aguanta tres vistas a 320 dp con la letra al 160 % en los 6 idiomas (test); `FilaPalabra` tiene un parámetro `extra` (línea opcional). **Dónde se enchufa «Verbos»** (ronda H): (1) `enum VistaBiblioteca` en `BibliotecaScreen.kt` → agregar `VERBOS`; (2) su `OpcionFiltro` en la lista de `SelectorDeVistas` (etiqueta nueva en `TextosInterfaz`, 6 idiomas); (3) una rama más en el `if/when` de vistas dentro del `LazyColumn` (hoy `if (indiceHabilidades != null) … return@LazyColumn` y luego la lista de palabras); (4) los dos grupos de chips (nivel y tipo) con `GrupoChips`, calculados por idioma desde los datos (P3); (5) cada fila con `FilaPalabra(extra = { Text(régimen / separabilidad) })`; (6) la búsqueda de familias trennbar es búsqueda de texto, sin campo nuevo.

**Tamaño del APK debug** (empaquetado limpio): antes de la Ronda G, 62 649 138 B (0.2.2); tras la tarea 0, 62 733 545 B (+84 407, los assets con `claseVerbo`); **al cierre, 62 897 555 B** (+248 417 sobre la 0.2.2; +318 633 sobre los 62 578 922 B de la 0.2.1, dentro del presupuesto de +1,5 MB). Las fuentes pesan 380 KB sin comprimir (~240 KB en el APK); el resto es código y recursos. **Ojo**: un `assembleDebug` incremental puede dejar huecos en el ZIP y mostrar +4,5 MB que no existen; para medir, borrar `app/build/outputs/apk` y `app/build/intermediates/apk` y reconstruir.

**Pantallas tocadas**: ficha y semana especial (columna, interlineado, tipografía y tema), Biblioteca (vistas, chips, filas, habilidades), Guardados (filas), Ajustes (selectores y chips de idioma), Acerca de (línea de fuentes), arranque (splash), todas por el tema y la tipografía. No se tocaron: planilla PDF, contenido, ícono del lanzador.

**Decisiones donde el prompt no cerraba**: Atkinson «Next» en vez de la original (por el peso Medium y por ser la que mantiene Google Fonts); fuentes variables en vez de estáticas (un archivo por estilo en lugar de ocho: 380 KB); `contentDescription` «Contraer/Expandir» de `SeccionPlegable` sigue hardcodeado en español (no es texto visible; P7 lo cubriría si se quiere); `surfaceContainer*` completados a la superficie del tema.

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
- ✅ Hecho en Code F2 (sección 28, versión 0.2.2).
- 10-oct-2026: Fer confirmó exportar/importar Guardados (TSV Anki + JSON de respaldo) e índice de habilidades (vista dentro de la Biblioteca): tareas 6 y 7 del prompt 18.

## 27. Verificación del parche R8 (Traducciones, 10-oct-2026) y siguiente deuda de contenido
- Parche R8 verificado por Overview en clon fresco: 606 fichas, 56 semanas especiales, 0 con problemas; R1-R8 en 0; las 1370 cadenas cambiadas solo agregan « » (quitándolos, idénticas a las anteriores), mismos `*`, estructura JSON idéntica; el caso de `EN-F01-B1` («I think es neutral…») quedó como «I think» es neutral…. Assets regenerados.
- Decisión de Traducciones aceptada: `titulo` no se marca (tope de 80 caracteres); `*x*` sin « » queda como está (en este repo `*x*` es también énfasis del idioma de destino; ~9558 casos, separar los dos usos queda anotado en 14.1).
- Deuda hallada: (a) trato formal en las instrucciones de alemán (~480 cadenas), francés (~500) e italiano (~270), contra la decisión «tú en todo»; (b) ~88 cadenas con `ß` en texto alemán, contra la ortografía suiza. Va a Traducciones como `prompts/19-traducciones-registro-y-ss.md` (R9 y R10). No bloquea la prueba con testers hispanohablantes.
- **Las dos deudas están cerradas (Traducciones, 11-oct-2026).** 1.207 cadenas de registro (de 423, fr 515, it 269) y 90 cadenas con eszett (98 marcas), 122 archivos de `contenido/`. `componer.py` da 606/56/0 y `verificar_consistencia.py` da R1-R10 en 0. Las reglas nuevas son **R9** (trato formal, con 32 excepciones motivadas) y **R10** (eszett, con 42), más `--autotest` para que no queden excepciones de más. Detalle en 6.1.3 y 7.5.
- Tres cosas que quedaron decididas en ese pase y conviene saber antes de revisarlo: (1) 11 `promptCorreccion` de núcleos `de` se alargaron una frase en los seis idiomas porque el imperativo informal los dejaba bajo el mínimo de 150 del schema (6.1.3); (2) el pase de eszett **revierte** la segunda excepción que tenía 7.5 — 29 cadenas de `contraste` en núcleos `EN-*` que antes conservaban `ß` a propósito; (3) los fragmentos alemanes marcados con `*…*` dentro de una instrucción en otro idioma siguen en formal porque son la expresión que se enseña, no instrucción (6.1.3).

## 28. Code Ronda F2 — ✅ implementada (Code, oct 2026), versión 0.2.2

Cierra la sección 26 (feedback de uso de Fer). Requisitos aplicados al empezar: parche R8 de Traducciones y assets regenerados (R1–R8 en 0). 7 tareas, un commit cada una; `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una.

| # | Tarea | Qué se hizo |
|---|---|---|
| 1 | Guardados = Biblioteca | Un solo composable `FilaPalabra` para las dos pantallas: original (idioma que se aprende) · traducción al idioma de app (se omite si no hay, sin repetir el original) · contexto/función en el idioma que se aprende (solo si existe). El snapshot de Room ya traía original y traducciones: **sin migración** (test por Room). El orden alfabético de Guardados pasó a ser por el original. Se quitó el pie técnico `DE · B2 · DE-V02` de la fila. `MatrizSuperficiesTest` suma la superficie «fila de Guardados» |
| 2 | Desafío | Ya no muestra consigna ni requisitos de la misión (ni el rótulo «La misión:»). Muestra: línea nueva («Dilo en voz alta…»), **Tema** (topic en idioma de app), **Habilidad** (título de la ficha), **Condiciones** (4 filas con ícono Mic/Timer/Group·SwapHoriz/Replay, una idea por fila) y **Frases para usar** (hasta 3, mismas expresiones que el prompt de voz: `expresionesDeLaFicha`). Se eliminaron `RETO_TITULO` y `RETO_ROTULO_MISION` |
| 3 | Espaciado | **Causa real**: `Seccion` usaba `spacedBy(8.dp)` entre viñetas, y 22.dp / 32.dp entre items y secciones: dp fijos que no siguen el `fontScale`. El interlineado ya era en sp (el tema usaba la tipografía por defecto de Material 3), solo que sin proporción explícita. Ahora todo es múltiplo del tamaño de letra (`ui/Espaciado.kt`): viñetas 0,35 × cuerpo, dentro del ítem 0,15, entre ítems 0,6, entre secciones 1 × cuerpo grande; interlineado del tema en sp (cuerpo 1,45). Test con fontScale 1,0/1,3/1,6. Las secciones pasaron de 32 dp a ~16 dp y las viñetas de 8 dp a ~5 dp: **más compacto que antes**, que es lo que se pidió |
| 4 | Encabezado | Fuera de la pantalla principal. Ajustes lleva arriba el nombre, la línea fija y la fila «Acerca de». `publicacion/LEEME.md` actualizado |
| 5 | Acerca de | Cinco bloques en 6 idiomas (qué es, cómo está pensada, privacidad, licencias y créditos, versión de prueba). Ver «Textos quitados» abajo |
| 6 | Exportar / importar Guardados | Anki (TSV) y copia de seguridad (JSON v1) con el selector del sistema; importar solo el JSON, sin duplicar por (idioma, tipo, original), con límites (5 MB, 50 000 ítems) y mensajes claros. **Regla dura 2 enmendada** en los dos `CLAUDE.md`; `PRIVACY.md` (raíz y legal) y «Acerca de» lo dicen |
| 7 | Índice de habilidades | Segunda vista de la Biblioteca («Vocabulario y expresiones» \| «Habilidades»): título + semanas del año que se está viendo; un toque abre la próxima aparición |

**Decisiones donde el prompt no cerraba:**
- **Exportar a Anki**: se exportan **todos** los guardados (no los filtrados): lo más simple. Reverso = traducción + « — » + contexto. Etiquetas `de B2` = dos etiquetas de Anki (idioma y nivel).
- **Índice, qué abre un toque**: la **próxima aparición** desde la semana que se está viendo (o la última si ya pasaron todas). El prompt decía «abre esa semana» sin precisar cuál cuando hay varias. Orden: alfabético por título.
- **Índice, qué año/qué calendario**: solo hay calendarios embebidos de **2026** (y parciales: desde la semana 37 o 41 según el par), así que el índice de hoy lista únicamente las semanas desde ahí; con un año sin calendario dice «todavía no hay calendario para {año}». Cuando se embeba el calendario 2027 el índice lo usará solo.
- **Primera vista de la Biblioteca** se llama «Vocabulario y expresiones» (no solo «Vocabulario»): ya existe el chip «Vocabulario» dentro de esa vista.
- **El desafío no puede mostrar el tema si falta `topicNombres`**: cae al id del tema (T01…); en la app real siempre están.
- **Guía de testers** (`docs/guia-testers.md`) corregida (el .pdf no se regeneró): decía «cada 8 y ~16 semanas» (son 4 repasos al año) y «el audio funciona sin conexión» (ver abajo).

**Textos de «Acerca de» que quité o corregí por no ser verdad:**
- «vocabulario y expresiones **con audio**»: **la app todavía no reproduce audio** (los .ogg están dentro del APK pero no hay reproductor; solo existe el campo `audio` en el modelo). Quitado.
- «**cada ocho semanas** se dedica una semana al repaso» → son **cuatro al año** (2027: semanas 10, 22, 34 y 46). «y, de forma periódica, a la supervivencia» → no hay Survival en 2027 (solo en el piloto 2026); se reemplazó por el desafío de cada fin de semana, que sí existe.
- «las habilidades se repiten **varias veces** al año» → solo repiten las marcadas como núcleo de cada nivel (regla 12); ahora dice «las más importantes de cada nivel».
- **Licencias de las voces: no están documentadas en el repo** (solo los nombres: `de_DE-thorsten-high`, `en_US-ryan-high`, `en_US-lessac-high`, en `PROYECTO.md` y `audio-manifiesto.md`). La línea de créditos nombra las voces y Piper pero **no afirma ninguna licencia**. Pendiente de Overview: documentar la licencia de cada voz antes de publicar.
- Se verificó contra el manifest (sin `INTERNET`, sin ubicación, sin cuentas ni analítica) y `PRIVACY.md` cada frase de privacidad; todas son verdad.
- **fr/it/pt de «Acerca de» y de los textos nuevos de esta ronda: no los revisó una persona nativa** (traducción mía, registro informal; FR «tu», PT «você» de Brasil).

**Para probar en el teléfono:** que el selector de archivos del sistema abra (crear `.tsv`/`.json` y abrir el `.json`) —los lanzadores no se pueden probar bajo Robolectric—, importar el TSV en Anki real, y mirar el espaciado nuevo con la letra del sistema al 130 % y al 160 %.

## 29. Verificación de Code F2 y de los patches 19 (11-oct-2026); idea «Verbos en la Biblioteca»

**Verificación.** El patch 19 de Traducciones (R9/R10) se aplicó limpio: 606/56/0, R1-R10 en 0, 1324 cambios solo de texto. Una cosa no pedida: 11 núcleos DE ganaron una frase en `promptCorreccion` (6 idiomas) porque el esquema exige ≥150 caracteres en `de` y la forma «du» queda más corta; aceptado. Assets regenerados en patch propio. Code F2: los 8 commits ya están en `origin/main` (el patch 17 que Fer reenvió es redundante). Manifest sin `INTERNET`. Sin verificar por Overview: la suite de 1016 tests (no se corrió acá).

**Deudas que deja F2:** (1) la guía de testers `.pdf` no se regeneró (el `.md` sí); (2) las licencias de las voces Piper no están documentadas en el repo y «Acerca de» solo las nombra: Overview las documenta antes de publicar; (3) los textos nuevos fr/it/pt de «Acerca de» no los revisó un nativo; (4) los calendarios embebidos son solo 2026 y parciales, por eso el índice de habilidades lista pocas semanas.

**Verbos en la Biblioteca (decidido por Fer):** tercera vista «Verbos», dentro de la Biblioteca. Taxonomía cerrada por idioma; solo **nivel y tipo** son obligatorios; se reutiliza el contenido existente; DE lleva el caso/preposición que rige; EN marca si el phrasal es separable; sin «sustantivo + verbo». Medición: `tipo` y `nivelVerificado` están vacíos en el 100 % de los ~8150 ítems de vocabulario y `reccion` existe solo en 70 ítems DE. Orden: prompt 20 (contenido) → Code Ronda G (prompt 15, con la tarea 6 que solo prepara la estructura) → Overview genera el índice de verbos en el compositor → Code ronda H (vista Verbos + ícono y splash).

**Vista «Verbos» — hecho en contenido (prompt 20, 11-oct-2026).** Campo nuevo `claseVerbo` (array de clases, opcional) en `vocabulario[]`, con la regla R11 en `verificar_consistencia.py` y las mismas reglas en el esquema. Tablas maestras en `contenido/verbos/{de,en}.json` (clave: idioma + forma normalizada), que `tools/aplicar_verbos.py` vuelca a **todas** las repeticiones de cada ítem (modo de comprobación por defecto; idempotente). Las columnas `base`, `raiz` y `nota` de la tabla son de trabajo: no están en el esquema ni en el contenido.

- **Universo (opción A):** solo ítems cuyo núcleo es el verbo. Fuera quedan colocaciones, modismos, «to be + adjetivo» y verbo + objeto. DE: 305 formas únicas (282 bases, 578 apariciones). EN: 360 formas únicas (310 bases, 651 apariciones). Total 1229 apariciones etiquetadas.
- **Taxonomía DE:** `simple` 100 formas, `reflexivo` 98, `trennbar` 119, `mit-Praposition` 52, `mit-Dativ` 18. Un verbo puede tener varias clases salvo `simple`, que es exclusiva. Se descartó `mit-Akkusativ` (es el caso por defecto, no distingue nada).
- **Taxonomía EN:** `simple` 161, `phrasal-separable` 57, `phrasal-inseparable` 106, `verbo-preposicion` 36; exactamente una clase por forma. No se fusionaron las clases phrasal.
- **`reccion`:** formato controlado `prep + Akk|Dat|Akk/Dat` (varias separadas por « / »; `Dat` solo en `mit-Dativ`). Lo reflexivo ya no va en `reccion` sino en `claseVerbo`. 211 `reccion` normalizados; en EN no hay `reccion`. Los `mit-Praposition` dudosos se dejaron **sin** clase antes que inventar la preposición.
- **`simple`** existe para que todo verbo tenga clase, pero la interfaz no la muestra como filtro.
- **Nivel descartado (decisión de Fer, 11-oct-2026):** la vista filtra solo por clase. El «nivel» de un verbo que se podía derivar era el del tema donde apareció primero, no su dificultad real; por eso no se calcula ni se informa, y `inventario_verbos.py` ya no lo emite. La regla de ≥15 ítems se aplica por clase e idioma.
- **Familias trennbar:** se resuelven con búsqueda de texto en la app, sin campo de familia. Dato que lo respalda: 108 verbos trennbar distintos, 62 con otro verbo de la misma raíz en el contenido, 40 familias de 2 o más miembros (94 miembros). Mayores: geben 4, steigen 4, stellen 4 (la raíz vive en `raiz` de la tabla maestra).
- **Ejemplos con el verbo separado:** solo 5 de los 108 trennbar lo muestran. Escribir los demás es contenido nuevo y queda pendiente (no se hizo).
- **Corrección «der → den»:** 6 colocaciones DE, 11 apariciones en packs (commit 6202d0de), más 12 líneas de prosa en dos microtareas de `ocurrencias/de-B1-2027.json`.
- **Aviso para Code:** el parser de Kotlin es estricto y `VocabularioItem` no tiene `claseVerbo`; hasta que Code lo agregue y Overview regenere los assets, la tarea `verificarAssetsContenidoActualizados` (P6) fallará.

## 30. Verificación del paso 2-3 de «Verbos» (11-oct-2026) y siguiente paso

Los 4 parches de «Verbos» ya estaban en `origin/main` (HEAD `8b5f0d54`). Verificado por Overview: 606 / 56 / 0 problemas, R1–R11 en 0, `aplicar_verbos.py` sin diferencias (1229 apariciones etiquetadas). Los assets quedan **desactualizados a propósito**: el parser de Kotlin es estricto y `VocabularioItem` no tiene `claseVerbo`, así que regenerarlos antes de que Code agregue el campo rompería la app. Por eso el prompt 15 (Ronda G) arranca con la **Tarea 0**: campo + assets, en un commit propio. Hasta entonces el build local falla en `verificarAssetsContenidoActualizados`; es esperado. Siguiente: Ronda G → Ronda H (vista «Verbos» con búsqueda de texto, filtro solo por clase, sin nivel; ícono y splash).

## 31. Verificación de Code Ronda G (11-oct-2026) y versión 0.2.3

Verificado por Overview sobre `origin/main` (HEAD `c2f71956`): compositor 606 / 56 / 0, R1–R11 en 0, **assets idénticos a la salida del compositor** (los dos guardas de `preBuild` pasan), `VocabularioItem.claseVerbo` presente, 4 fuentes OFL de 380 KB con licencia en `LICENSES/`, color dinámico tras `SDK_INT >= S`, migración `ACADEMIA→PAPEL` / `EDITORIAL→MAR` en `RepositorioAjustes.kt`, manifest sin `INTERNET`, enmienda de la regla 2 por el color dinámico en los dos `CLAUDE.md`.

**Corregido por Overview en este parche:** la Ronda G cambió temas, tipografías y toda la lectura sin subir la versión, así que la app de Fer se habría llamado igual que la anterior. **versionCode 5, versionName 0.2.3.** Guía de testers actualizada (versión, los dos selectores nuevos, el aviso de los signos que caen a la fuente del sistema, ícono y splash todavía provisorios) y **`.pdf` regenerado** (2 carillas), que era la deuda que dejó F2.

**Deuda abierta para la ronda H:** el `contentDescription` «Contraer/Expandir» de `ui/SeccionPlegable.kt` está en español fijo. No es texto a la vista, pero TalkBack **lo lee en voz alta**, así que con la app en alemán el lector dice una palabra en español: va a `TextosInterfaz` en 6 idiomas (P7). Siguen pendientes: nombres de las clases de verbo en fr/it/pt sin revisar por nativo, ejemplos con el verbo separado (5 de 108), licencias de las voces Piper.

## 32. Code Ronda H — ✅ implementada (Code, 11-oct-2026), versión 0.2.4

Cuatro tareas, un commit cada una; `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada una. Sin tocar contenido, esquema ni assets.

- **Vista «Verbos»** (`presentacion/Verbos.kt`, `ui/BibliotecaScreen.kt`): tercera vista de la Biblioteca. Entran los ítems de vocabulario con `claseVerbo` no vacío del idioma de la pestaña y de **su nivel actual**, deduplicados por el original (el agrupado de `construirBiblioteca` ya es por idioma + tipo + texto), alfabéticos. Fila = `FilaPalabra` con `extra`: en alemán el `reccion`; en inglés la separabilidad del phrasal, salvo que ya la diga el chip elegido. La estrella es la de las otras vistas (mismo ítem y misma clave de Guardados). **Un solo grupo de chips, la clase, sin filtro de nivel** (el «dos grupos, nivel y tipo» de 25.1 quedó obsoleto, no se implementó). Chips por idioma desde los datos con su cuenta; `simple` nunca es chip; clase desconocida: el verbo se ve, sin chip, y no rompe (test). Estado vacío en 6 idiomas.
- **Búsqueda** (solo en Verbos): subcadena insensible a mayúsculas y diacriticos (`uber` → *übernehmen*, ß = ss) sobre el original y la traducción al idioma de app, mientras se escribe, sobre lo ya filtrado por clase; botón de borrar; mensaje propio de «sin resultados». Nada de datos nuevos (sin campo de familia ni raíz).
- **TalkBack**: «Contraer/Expandir» de `SeccionPlegable` a `TextosInterfaz` (6 idiomas). Auditoría: no había otro `contentDescription` con texto fijo; un test lo vigila sobre el código fuente. Quedan los `contentDescription` de las flechas de semana (`"< Semana"`, `"> Semana"`): la palabra va traducida pero TalkBack dice «menor que»; cosmético, no se tocó (los tests de pantalla los usan).
- **Tamaño del APK** (empaquetado limpio): **62 913 939 B**, +16 384 sobre los 62 897 555 B de la Ronda G. Manifest sin `INTERNET` ni permisos nuevos; 1088 tests, 0 fallas; los dos guardas de `preBuild` en verde.
- **Versión** 0.2.4 (versionCode 6); guía de testers (`.md`) con la versión y la fila de Verbos; el `.pdf` lo regenera Overview.

**Elecciones donde el prompt no cerraba:** los chips de clase son **multi-selección con OR** (como las categorías de expresiones): con «Separable» y «Reflexivo» se ven los que tienen alguna de las dos; la cuenta de cada chip es sobre todos los verbos del par (no baja al escribir en la búsqueda); la búsqueda y las clases elegidas se vacían al cambiar de pestaña de idioma; el nivel mostrado es el nivel actual del idioma en Ajustes (no el multiselector de la vista de palabras).

**Verbos a la vista por (idioma, nivel)** (únicos por original, datos reales; entre paréntesis las clases con chip):

| | A2 | B1 | B2 | C1 | C2 |
|---|---|---|---|---|---|
| **DE** | 124 (trennbar 32, reflexivo 25, mit-Praposition 16, mit-Dativ 9) | 121 (48, 50, 28, 10) | 72 (40, 35, 16, **2**) | 54 (27, 25, 7, **2**) | **24** (13, 8, 3, **sin mit-Dativ**) |
| **EN** | 102 (phrasal-sep. 13, phrasal-insep. 12, verbo-prep. **4**) | 128 (25, 47, 13) | 72 (11, 32, 7) | 76 (13, 31, 12) | 65 (**6**, 9, **5**) |

Pares flojos: **DE C2 con 24 verbos** y sin ningún `mit-Dativ` (el chip no aparece); `mit-Dativ` con 2 en DE B2 y C1; EN A2 con solo 4 `verbo-preposicion` y EN C2 con 6 `phrasal-separable` y 5 `verbo-preposicion`. DE C2 y los chips de 2–6 ítems son los candidatos a contenido nuevo. `reccion` está poblado en DE en 25 / 38 / 18 / 9 / 3 verbos (A2…C2): la línea extra solo se dibuja ahí. En EN no hay `reccion`; la línea extra es la separabilidad.

**Sin revisar por nativo:** los nombres de las clases, de la vista, el estado vacío, el «sin resultados» y Contraer/Expandir en fr / it / pt.

**Búsqueda en «Vocabulario y expresiones» (propuesta, no hecha):** ya la tiene (campo «Buscar», sobre original, traducción y función, sin diacríticos no: usa `lowercase()` plano). Costo de unificarla con la de Verbos: reemplazar `coincide()` de `Biblioteca.kt` por `normalizarBusqueda` (una línea, más sus tests) para que `uber` también encuentre *über…* ahí; bajo, y de paso las dos vistas se comportan igual.

## 33. Verificación de Code Ronda H (11-oct-2026) y Ronda I

Verificado por Overview sobre `origin/main` (HEAD `c5b4f726`): los 4 commits están, versionCode 6 / 0.2.4, `VistaBiblioteca { LEXICO, HABILIDADES, VERBOS }`, manifest sin `INTERNET`, contenido y assets sin tocar. La lógica de la vista (`presentacion/Verbos.kt`) está separada de la pantalla y bien cubierta; el test que barre el código fuente buscando `contentDescription` con texto fijo es una buena idea y se queda. **Guía de testers: `.pdf` regenerado por Overview** (2 carillas).

**Defecto encontrado por Overview — motiva la Ronda I.** La vista quedó atada al **nivel actual del idioma en Ajustes**. «Sin filtro de nivel» se interpretó como «fijado en un nivel» en lugar de «el nivel no es una dimensión de esta vista», y eso **rompe la búsqueda de familias** (tarea 2 de la H), porque las familias viven repartidas entre niveles: *stellen* tiene 6 miembros en A2, B1, C1 y C2 (*bestellen* A2, *sich bestellen* / *sich vorstellen* B1, *nachbestellen* C1, *kaltstellen* / *richtigstellen* C2) y *steigen* está en A2 y B1. Buscar «stellen» en B1 muestra 2 de 6 sin avisar. Además la vista esconde la mayor parte del material (124 de 305 formas en DE A2) y el estado vacío manda a «probar con otro nivel», que en esa vista no se puede cambiar. **Ronda I (prompt 22):** verbos de todo el idioma, el nivel como dato en la línea extra y no como filtro, textos corregidos y la búsqueda del léxico unificada con `normalizarBusqueda` (propuesta de Code, aceptada). Versión 0.2.5.

**Decisiones de la H que se quedan como están:** chips multi-selección con OR; la cuenta del chip no baja al escribir en la búsqueda; búsqueda y chips se vacían al cambiar de pestaña de idioma; en inglés la fila repite la separabilidad mientras no haya chip elegido.

**Pares flojos de contenido (dato de Code, sin acción todavía):** DE C2 con 24 verbos y sin ningún `mit-Dativ`; `mit-Dativ` con 2 en B2 y 2 en C1; EN A2 con 4 `verbo-preposicion`; EN C2 con 6 `phrasal-separable` y 5 `verbo-preposicion`. Con la Ronda I el nivel deja de recortar la vista, así que esto deja de ser urgente: se decide si hace falta contenido nuevo recién si una **clase entera** de un idioma queda floja.

## 34. La búsqueda de familias de verbos choca con la opción A (11-oct-2026) — deuda de contenido

Fer probó la 0.2.4 con alemán B2 y **«stellen» no devolvió ningún resultado**. Dos causas, una de código y una de contenido.

**Código (la cubre la Ronda I, prompt 22):** la vista recorta por el nivel de Ajustes y ningún miembro de la familia *stellen* está en B2 (A2, B1, C1 y C2).

**Contenido (sin resolver).** Con el nivel arreglado la búsqueda devolverá 6 verbos, pero **ni *stellen*, ni *einstellen*, ni *umstellen*** — los tres que Fer nombró como ejemplo al pedir la función — porque en el contenido no existen como verbos: viven dentro de colocaciones que la **opción A** dejó fuera a propósito (*die Frage stellen* A2, *die Uhrzeit einstellen* A2, *die Ernährung umstellen* B1, *etwas in Frage stellen* B2, *etwas auf den Prüfstand stellen* C1). Medición de Overview sobre los packs DE (**corregida en la sección 37 por el inventario de «Verbos», que es el número bueno: 369 colocaciones, 199 núcleos, 149 nuevos**): 341 colocaciones verbales sin etiquetar, con 191 núcleos verbales distintos escondidos dentro, de los cuales 120 no aparecen en ningún verbo etiquetado (*abbauen*, *einhalten*, *führen*, *behalten*, *nachgehen*, *widmen*…). Es decir: la vista Verbos muestra 305 formas DE y deja afuera unos 120 verbos que el usuario sí estudió.

La opción A sigue siendo correcta para **qué se lista** (una vista de verbos no debe llenarse de frases), pero es insuficiente para **qué se indexa**: el núcleo verbal de una colocación es un verbo que el usuario aprendió. Opciones para decidir con Fer: (a) extraer el núcleo verbal de las colocaciones a la tabla maestra y listarlo como verbo, con la colocación como ejemplo de uso; (b) dejar que la búsqueda de la vista Verbos alcance también a las colocaciones, mostrándolas en una sección aparte; (c) aceptar la laguna y decirlo en la guía de testers. **Fer eligió (a)** (11-oct-2026): el núcleo verbal de la colocación se indexa como verbo y la colocación se ve como contexto de uso; la opción A sigue decidiendo qué se **lista**. Prompt 23 para la conversación «Verbos»; después una ronda de Code para los campos nuevos en el modelo (el parser es estricto) y para armar la lista con los núcleos. Universo a revisar: 1937 ítems DE y 1707 EN sin etiquetar de más de una palabra, la mayoría no verbales. Punto a cerrar en esa ronda: los auxiliares y verbos de soporte (*sein* es núcleo de 29 colocaciones DE; *to be + adjetivo* es media lista en EN) **no** aportan un verbo a la vista.

## 35. Code Ronda I — ✅ implementada (Code, 11-oct-2026), versión 0.2.5

Cuatro commits, uno por tarea, sobre la Ronda H; contenido, esquema y assets sin tocar.

- **Tarea 1 — la vista Verbos es del idioma, no del nivel** (`presentacion/Verbos.kt`, `ui/BibliotecaScreen.kt`): `verbosDe(idioma)` sin nivel; el nivel de Ajustes no interviene. Chips y cuentas sobre todo el idioma (P3). La línea discreta de cada fila lleva el **nivel más bajo** en que aparece el verbo como dato (`A2 · von + Dat`; en inglés `B1 · Phrasal separable`, y la separabilidad se omite si ya es el chip elegido). Se quitó el encabezado «Nivel B2». Sin filtro, chip ni selector de nivel. No se inventó ni etiquetó ningún verbo: *stellen*, *einstellen* y *umstellen* siguen siendo deuda de contenido (sección 34). La búsqueda precalcula una vez el texto normalizado de cada verbo (`BuscadorVerbos`), así que teclear no renormaliza ~300 verbos por pulsación.
- **Tarea 2 — textos que mentían** (`TextosInterfaz.kt`, `proyecto/docs/guia-testers.md`): el estado vacío ya no menciona «otro idioma o nivel» y solo aparece si el idioma no tiene verbos etiquetados; el «sin resultados» habla de «esta lista»; la fila de la guía dice «todos los niveles», muestra el nivel más bajo, explica que no hay filtro de nivel y avisa **«La lista no es exhaustiva»** (con el ejemplo stellen/einstellen/umstellen). Un test lee la guía y vigila que la fila no vuelva a decir «del nivel que estás viendo».
- **Tarea 3 — búsqueda unificada** (`presentacion/Biblioteca.kt`): `filtrar()` pasa consulta y textos por `normalizarBusqueda` (sin mayúsculas ni diacríticos, ß = ss) en lugar de `lowercase()`: `uber` encuentra *über…* en «Vocabulario y expresiones». **Mismos campos de siempre** (original, traducción al idioma de app, función); ninguno nuevo. El texto normalizado del original+función y el de la traducción (por idioma de app) se calculan una vez por ítem (cache fuera de la igualdad del `data class`; test de que `equals`/`hashCode` no cambian).
- **Tarea 4 — versión 0.2.5 (versionCode 7)**; guía con la versión y la fila corregida (el `.pdf` lo regenera Overview).

**Tests cambiados (no borrados) y por qué:** `VerbosTest` (el universo ya no depende del nivel: ahora verifica que el nivel no recorta, que la familia *stellen* da sus 6 miembros y que ninguno es B2, y `nivelMasBajo`), `VistaVerbosTest` (la línea extra lleva el nivel: `B2 · über + Akk`, `B1 · Phrasal separable`; el test «solo se ven los del nivel actual» pasó a verificar lo contrario, y se agregó que no existe ningún filtro de nivel en la vista), `BusquedaVerbosTest` (+ equivalencia del buscador precalculado), `BusquedaVerbosPantallaTest` (+ con alemán B2 en Ajustes, «stellen» muestra los 6 miembros aunque ninguno sea B2). Nuevos: `TextosVerbosTest` (estado vacío, sin resultados y guía) y `BusquedaLexicoTest` (`uber`/`UBER`/`über` encuentran *über…* en el léxico real; expresiones sin tildes; ß/ss; sigue buscando por traducción y por función y solo por esos campos; búsqueda vacía; cache sin efecto en la igualdad). **1105 tests, 0 fallas**; los dos guardas de `preBuild` (`verificarAssetsContenidoActualizados`, `verificarConsistenciaContenido`) en verde sin tocar assets.

**Verbos visibles por idioma** (únicos por original recortado, datos reales, sin corte por nivel): **DE 305**, **EN 368**.

| Clase | DE | EN |
|---|---|---|
| simple (sin chip) | 100 | 168 |
| trennbar / phrasal-separable | 119 | 58 |
| reflexivo / phrasal-inseparable | 98 | 106 |
| mit-Praposition / verbo-preposicion | 52 | 36 |
| mit-Dativ | 18 | — |

(Un verbo puede tener varias clases, por eso las filas no suman el total.) Nivel más bajo en que aparece, DE A2…C2: 124 / 83 / 51 / 31 / 16; EN: 102 / 111 / 47 / 56 / 52. Los «pares flojos» de la sección 33 dejan de ser urgentes: con la vista de todo el idioma, **DE `mit-Dativ` tiene 18** y **EN `verbo-preposicion` 36**; ya no hay un chip de 2–6 ítems.

**APK (empaquetado limpio):** **62 913 943 B**, +4 B sobre los 62 913 939 B de la Ronda H (solo cambian los textos y el `versionCode`/`versionName`). Manifest sin `INTERNET` ni permisos nuevos (solo el interno de AndroidX).

**Elecciones donde el prompt no cerraba:** (1) la línea extra muestra el **nivel más bajo** en que aparece el verbo (no todos); el orden sigue alfabético, no por nivel; (2) la cuenta de cada chip es sobre todo el idioma, no baja al escribir en la búsqueda (como en la H); (3) en la búsqueda del léxico, el texto normalizado se cachea por ítem en vez de calcularse por pulsación; (4) no se tocó el `contentDescription` `"< Semana"` de las flechas (cosmético, fuera de alcance); (5) la propuesta de la sección 32 sobre el léxico se hizo tal cual, sin cambiar qué campos busca.

**Sin revisar por nativo:** el nuevo estado vacío y «sin resultados» en fr / it / pt.

## 36. Verificación de Code Ronda I (11-oct-2026)

Verificado por Overview sobre `origin/main` (HEAD `b3b8a540`, 0.2.5 / versionCode 7): compositor 606 / 56 / 0, R1–R11 en 0, `aplicar_verbos.py` con 0 diferencias y 1229 apariciones, **assets idénticos a la salida del compositor** (el contenido no se tocó: no hizo falta regenerarlos), manifest sin `INTERNET`. Contados por Overview contra los packs, los números del informe de Code son exactos: **DE 305 formas, EN 368**; nivel más bajo en que aparece cada verbo, A2…C2, **DE 124 / 83 / 51 / 31 / 16** y **EN 102 / 111 / 47 / 56 / 52**. El caso que motivó la ronda queda cubierto por un test de pantalla con alemán B2 en Ajustes.

**Guía de testers: `.pdf` regenerado por Overview** (0.2.5). La fila de Verbos que escribió Code llevaba el `.pdf` a **3 carillas**; Overview la acortó conservando lo que el test `TextosVerbosTest` exige («todos los niveles» y «no es exhaustiva») y la guía volvió a **una hoja (2 carillas)**. Al tocar esa fila, revisar el largo del PDF. **Cuando aterrice el prompt 23 hay que quitar el aviso «la lista no es exhaustiva»**, porque deja de ser cierto.

**Nota menor sin arreglar:** el test `no hay filtro de nivel, ni chips ni selector de niveles en la vista` (`VistaVerbosTest`) comprueba que no aparecen los textos «A2», «B1», «C1» ni «C2», y pasa solo porque en su fixture todos los verbos son B2 — ahora la línea extra **sí** muestra el nivel de cada verbo. La conclusión del test es correcta (no hay filtro de nivel), pero no prueba lo que su nombre dice y se rompe en cuanto el fixture cambie. Que Code lo reescriba cuando vuelva a tocar esa pantalla: la forma correcta es comprobar que no existe un `GrupoChips` de niveles, no la ausencia de esas cadenas.

**Decisiones de la I que se quedan:** la línea extra muestra solo el nivel más bajo; el orden es alfabético y no por nivel; la cuenta del chip no baja con la búsqueda; el `contentDescription` `"< Semana"` sigue sin tocar. La vista de léxico conserva su multi-selector de nivel y Verbos no tiene ninguno: es deliberado, no una inconsistencia.

**Pendiente de nativo:** el estado vacío y el «sin resultados» en fr / it / pt, además de lo ya listado en 32 y 33.

## 37. Inventario de colocaciones verbales (prompt 23, tarea 1) — aprobado con dos correcciones

Números buenos, medidos por «Verbos» con `tools/inventario_colocaciones.py` y revisados por Overview: **DE 369 colocaciones verbales / 199 núcleos / 149 nuevos**; **EN 672 / 250 / 151**. Reemplazan la medición de la sección 34 (341 / 191 / 120), que no separaba las ramas de los ítems con barra ni quitaba la preposición regida antes de buscar el infinitivo. La vista pasaría de 305 a 454 verbos en DE y de 368 a 519 en EN; cerca de la mitad de los núcleos nuevos tiene una sola colocación.

**Decisiones de Fer (11-oct-2026):**
- **Lista de exclusión de núcleos** (auxiliares, cópulas, modales y verbos livianos): DE `sein`, `werden`, `haben`, `können`, `lassen` (ver abajo); EN `be`, `have`, `do`, `get`. Un núcleo excluido no genera entrada **ni recibe colocaciones**, aunque ya exista como verbo de la vista (caso de `get`): colgarle sus 32 colocaciones lo enterraría en ruido. Esas colocaciones siguen buscables en «Vocabulario y expresiones».
- **EN `let` NO se excluye.** Corrección de Overview sobre la propuesta: sus 4 casos (*to let someone down*, *to let off steam*, *to let someone know*, *to let something rest*) son **phrasal verbs**, no causativos; excluirlo perdería *let down* y *let off*, que son justamente material de la vista. El argumento del causativo vale para el alemán, no para el inglés.
- **DE `lassen`: se extrae el infinitivo incrustado**, no el núcleo sintáctico: *sich impfen lassen* aporta *impfen*; igual *untersuchen*, *beraten*, *verlängern*. Regla especial en el extractor y en R11, por unos 8 ítems.
- **Sin glosa inventada** para los núcleos nuevos. La entrada se muestra como infinitivo más sus colocaciones con la traducción que ya tienen. Consecuencia aceptada: la búsqueda por traducción no encuentra *stellen* escribiendo «poner». Darles glosa serían ~1800 cadenas nuevas en 6 idiomas con revisión de nativo: otra ronda, si alguna vez.
- **Tres campos propios** (`nucleoVerbal`, `claseNucleo`, `reccionNucleo`), **no** reusar `claseVerbo` en la colocación. El argumento de «Verbos» es correcto y evita un desastre: la vista de la 0.2.5 lista todo ítem con `claseVerbo` no vacío y muestra el texto del ítem, así que reusarlo llenaría la vista de ~450 frases en cuanto se regeneraran los assets, justo lo que la opción A prohíbe. R11 garantiza que un ítem nunca tenga los dos.

**Hueco de diseño que encontró Overview, a resolver antes de la ronda de Code.** La estrella de Guardados se identifica por el **texto del ítem**: `claveGuardado(tipo, textoOrigen)` y `claveBiblioteca(idioma, tipo, texto)`. Un núcleo que solo existe dentro de colocaciones **no es un ítem**, así que no tiene clave: si la entrada *stellen* fuera marcable, generaría una clave que ninguna ficha puede volver a producir (la estrella no se encendería al ver *die Frage stellen* en la ficha) y Guardados mostraría una fila con un verbo sin traducción, que además se exportaría a Anki. **Decisión: la entrada de un núcleo sin ítem propio no es marcable; la estrella vive en la colocación**, que sí es un ítem. En la vista, la fila del verbo es un encabezado y las colocaciones debajo llevan su estrella. La alternativa (una clave sintética `VERBO|stellen`) abriría un espacio de nombres nuevo en Guardados y obligaría a cambiar el respaldo y la exportación: no vale la pena.

## 38. Núcleos verbales de las colocaciones — ✅ hecho en contenido (prompt 23, 11-oct-2026)

Implementadas las tareas 2 a 4 sobre `origin/main`. **Verificación:** 606 / 56 / 0, R1–R11 en 0, autotest de R11 con **67 casos + 7 de coherencia** (41/41 malos marcados, 0/26 falsos positivos, el esquema rechaza los mismos 41), `aplicar_verbos.py` sin diferencias y **1104 colocaciones con núcleo** además de las 1229 apariciones de verbo propio, y un script sobre el diff: 492 packs cambiados, solo los campos nuevos, ningún pack gana ni pierde ítems de `vocabulario`, « » y * intactos.

**Esquema.** Tres campos nuevos en `vocabulario[]`: `nucleoVerbal` (infinitivo, con la ortografía de las entradas de la vista: «sich widmen», «to take back»), `claseNucleo` (misma lista cerrada que `claseVerbo`) y `reccionNucleo` (solo alemán, formato controlado). Un ítem **nunca** lleva `claseVerbo` y `nucleoVerbal` a la vez: lo impiden el esquema y R11. Listas de exclusión en `$defs` (`nucleosExcluidosDE`, `nucleosExcluidosEN`), así que un auxiliar etiquetado no compone. La tabla maestra `contenido/verbos/{idioma}.json` gana un array `nucleos[]` (núcleo, clase, `reccion`, `colocaciones`, `nota`) junto al `verbos[]` de la opción A; `colocaciones` es de trabajo y no llega al contenido.

**R11 ampliada** (P11 en `REGLAS-PREVENCION.md`): el mismo núcleo lleva siempre la misma clase y la misma `reccion`, **y coincide con el verbo propio del mismo infinitivo** si existe — la clase de un verbo no depende de por dónde entró a la vista.

**Números finales.** DE: **197 núcleos**, 441 apariciones de colocación marcadas. EN: **256 núcleos**, 663 apariciones. De esos núcleos, 39 en alemán y 69 en inglés son el **mismo verbo** que una entrada que la vista ya tenía (hay que fusionarlos, ver abajo), así que la vista gana **158 verbos en alemán** (305 → 463) y **187 en inglés** (368 → 555). Los dos conteos de la tabla maestra y de los packs coinciden: 197 y 256 núcleos distintos aparecen en el contenido.

**Lo que se dejó afuera, a propósito:** los 8 modismos cuyo verbo no se usa libre (`in die Ferne schweifen`, `alles in sich hineinfressen`, `den Gürtel enger schnallen`, `to fan the flames`, `to muddy the waters`, `to salve your conscience`, `to whet your appetite`, `to make up your mind`), las colocaciones de los núcleos excluidos (47 DE, 221 EN) y los dos causativos de `let` (`to let someone know`, `to let something rest`: el infinitivo incrustado sería *know* y *rest*, que no es lo que enseña el ítem).

**Corrección de clase encontrada con diccionario:** `hinterlegen` estaba como `trennbar` y es **inseparable** en el sentido de «depositar» (DWDS, acepción II: *hinterlegt, hat hinterlegt*); la separable es regional y significa «poner detrás». La colocación del contenido es *die Kaution hinterlegen*, así que va `simple`.

**Dos huecos que quedan abiertos (no se tocaron):**
1. **Ítems con dos verbos separados por barra solo indexan el primero**, porque `nucleoVerbal` es un string: *hochladen / herunterladen* (se pierde *herunterladen*), *to chop / to slice*, *to downplay / to overstate*, *to withdraw / to deposit*, *to quit / to fire*, *to open / close an account*, *to take out a loan / a policy*. Son 7 ítems y 6 verbos perdidos. Arreglarlo es pasar el campo a array, con el costo correspondiente en el esquema, en R11 y en el modelo de Kotlin.
2. **Error de caso en el contenido:** `ein Hobby nachgehen` debería ser `einem Hobby nachgehen` (*nachgehen* rige dativo; el ítem correcto también existe, en otro pack). No se corrigió porque esta ronda no toca nada fuera de los campos nuevos — es el mismo tipo de error que la tanda «der → den».

**Para Code (ronda siguiente).**
- Tres campos en `VocabularioItem` (el parser es estricto) y assets regenerados en un commit propio, como en la Ronda G: hasta entonces `verificarAssetsContenidoActualizados` falla y es esperado.
- La vista entra con **`claseVerbo` no vacío O `nucleoVerbal` presente**, agrupando por infinitivo.
- **Dos tipos de entrada, y se comportan distinto:** el **verbo propio** es un ítem, se lista con su texto y **es marcable** en Guardados; el **núcleo** no es un ítem, no tiene glosa propia y **no es marcable** — la fila del verbo es un encabezado y la estrella vive en cada colocación de abajo (que sí es un ítem y ya tiene su clave). Nada de claves sintéticas tipo `VERBO|stellen`.
- **Las colocaciones van visibles debajo del verbo, no escondidas tras un toque** (condición de diseño de Fer): un núcleo sin glosa no puede verse como una fila vacía.
- **Hay que fusionar por infinitivo:** 32 núcleos alemanes coinciden exactamente con un verbo propio (*bestellen*, *arbeiten*, *kaufen*, *sich anmelden*…). Sin fusión se ven dos filas del mismo verbo. En inglés no hay ninguna coincidencia exacta. R11 garantiza que las dos entradas traen la misma clase, así que fusionar es seguro.
- Quitar de la guía de testers el aviso «la lista no es exhaustiva» (FALTANTES 36), que deja de ser cierto.

**Comprobaciones en archivo** (`outputs/verbos-prompt23/`): `comprobacion-stellen.md` (la búsqueda «stellen» devuelve 10 verbos: 6 propios y 4 núcleos, con *stellen*, *einstellen* y *umstellen* entre ellos), `muestra-diccionario.md` (40 núcleos por idioma, con 12 verificados en DWDS y 12 contra los dos libros de Cambridge del proyecto; Cambridge Dictionary y Collins en línea no se pueden consultar desde el entorno, y eso queda dicho), `informe-colocaciones.md`, `nucleos.csv` y `dudosos.md`.

## 39. Verificación del prompt 23 (Overview, 11-oct-2026) y Code Ronda J

Verificado sobre `origin/main` (HEAD `d6571b1b`): compositor **606 / 56 / 0**; verificador **R1–R11 en 0**; autotest de R11 **41/41 malos marcados, 0/26 falsos positivos, y el esquema rechaza los mismos 41**; autotest de R9/R10 sin fallos; `aplicar_verbos.py` sin `--escribir` con **0 diferencias, 1229 apariciones de verbo propio y 1104 de colocación con núcleo**. Auditoría propia de Overview sobre los packs: **ningún ítem lleva `claseVerbo` y `nucleoVerbal` a la vez**, **ningún núcleo incoherente** (el mismo infinitivo siempre con la misma clase) y **ningún núcleo de la lista de exclusión** presente (`sein`, `haben`, `werden`, `können`, `lassen`, `be`, `have`, `get`, `do`). `let` quedó bien: entradas `to let down` y `to let off`, sin los causativos. Los tres de Fer están: `stellen`, `einstellen`, `umstellen`.

**Corrección a un número del informe de «Verbos».** Dijo que en inglés no hay ninguna coincidencia exacta entre un núcleo y un verbo propio. **Son 65** (*to afford* / *to afford something*, *to back up* / *to back something up*, *to book* / *to book an appointment*…), además de las **32** del alemán (*abgeben* / *Verantwortung abgeben*). La fusión por infinitivo es obligatoria **en los dos idiomas**, no solo en alemán. Totales de la vista con fusión: **DE 470** (305 + 197 − 32) y **EN 559** (368 + 256 − 65).

**Assets: NO regenerados a propósito.** El contenido cambió (1104 campos nuevos) pero el parser de Kotlin es estricto y `VocabularioItem` no tiene los tres campos, así que regenerar ahora rompería la carga. Lo hace Code en la **tarea 0** de la Ronda J, igual que en la G. Hasta entonces el build local falla en `verificarAssetsContenidoActualizados`: es esperado.

**Code Ronda J (prompt 24):** los tres campos y los assets, los núcleos en la vista con sus colocaciones visibles y su estrella, la fusión por infinitivo, la búsqueda que alcanza a las colocaciones y queda fija a la vista, quitar el aviso «la lista no es exhaustiva» y versión 0.2.6. Incluye el arreglo del test flojo que se anotó en la sección 36.

**Deudas que deja esta ronda:** los 7 ítems con dos verbos separados por barra solo indexan el primero (se pierden *herunterladen*, *to slice*, *to overstate*, *to deposit*, *to fire* y el *close* de *to open / close an account*): laguna aceptada, `nucleoVerbal` queda como string. `ein Hobby nachgehen` sigue en el contenido junto al correcto `einem Hobby nachgehen`: va en un parche aparte de «Verbos». La separabilidad de los phrasal que los dos libros de Cambridge del proyecto no listan es clasificación de «Verbos», no dato de diccionario, y está marcada «sin fuente citable». Los núcleos no tienen glosa propia.

## 40. Code Ronda J — ✅ implementada (Code, 11-oct-2026), versión 0.2.6

Cuatro commits, uno por tarea, sobre la Ronda I más el contenido del prompt 23. Contenido, esquema y `proyecto/contenido/` sin tocar; solo los assets, regenerados en la tarea 0.

- **Tarea 0 — campos y assets** (`modelo/ContenidoSemanal.kt`, `assets/contenido/`): `VocabularioItem` gana `nucleoVerbal: String? = null`, `claseNucleo: List<String> = emptyList()` y `reccionNucleo: String? = null`. Assets regenerados con `componer.py`: **606 fichas, 56 semanas especiales, 0 con problemas**; 493 archivos cambiados, solo líneas agregadas; los dos guardas de `preBuild` en verde. Test del parser sobre fichas reales (`NucleoVerbalTest`: 1104 colocaciones con núcleo, ninguna con `claseVerbo` a la vez, todo núcleo con su clase). APK **62 913 943 → 62 935 447 B (+21 504)** por los campos nuevos en los JSON.
- **Tarea 1 — núcleos en la vista** (`presentacion/Verbos.kt`, `ui/BibliotecaScreen.kt`, `ui/FilaPalabra.kt`): nuevo `EntradaVerbo` (`propio?`, `colocaciones`, `clases`, `reccionNucleo`) y `entradasVerbo(idioma)`. Una entrada es el **verbo propio** (fila de siempre, marcable, con traducción) o el **núcleo puro** (encabezado con el infinitivo, **sin estrella ni traducción**: `FilaPalabra` gana `conEstrella`), y en los dos casos las colocaciones van **visibles debajo**, con su traducción y su propia estrella (clave de Guardados = la de la colocación, sin claves sintéticas). **Fusión por infinitivo exacto**: una sola fila, la del verbo propio. Una colocación con núcleo no se lista suelta; en «Vocabulario y expresiones» sigue como siempre. Los chips cuentan `claseNucleo` igual que `claseVerbo` (mismas reglas: solo clases presentes, `simple` nunca chip, clase desconocida no rompe); la línea extra del núcleo es el nivel más bajo de sus colocaciones + `reccionNucleo`; la de un verbo propio no cambia.
- **Tarea 2 — búsqueda** (`BuscadorVerbos`, `stickyHeader`): una entrada se encuentra por infinitivo, traducción, o **texto y traducción de cualquiera de sus colocaciones** («Frage» trae *stellen*, con *die Frage stellen* visible debajo). Texto normalizado de entradas y colocaciones precalculado **una vez**: preparar 1,2 ms (DE, 470 entradas) / 2,1 ms (EN, 559), y **filtrar 0,03 ms por tecla** (medido en el test, JVM de escritorio; teclear no renormaliza). El campo es un `stickyHeader` con fondo opaco: **queda fijo arriba** al recorrer la lista (test en viewport de 600 dp: tras `performScrollToIndex(300)` la pestaña de idioma ya no está y el campo sí). Los **chips no son fijos**: se van con el scroll (la búsqueda tiene prioridad sobre el espacio; fijar también los chips come ~100 dp en un teléfono).
- **Tarea 3 — guía y versión 0.2.6 (versionCode 8)**: fila de Verbos reescrita **sin** el aviso «La lista no es exhaustiva», contando las frases debajo del verbo, la estrella solo en las frases cuando el verbo no existe suelto, y la búsqueda fija que mira traducciones y frases; **más corta que la anterior** (573 vs 616 caracteres), así que la guía sigue entrando en las 2 carillas (el `.pdf` lo regenera Overview). `TextosVerbosTest` ahora vigila lo contrario del aviso (que no vuelva a decir «exhaustiva» y que mencione las frases). Test flojo de `VistaVerbosTest` arreglado: con verbos de A2, B1, C1 y C2 comprueba que el nivel **sí está como dato** de la fila pero que **ningún nodo clicable** (chip) lo ofrece como filtro, y que no aparece el rótulo «Nivel».

**Entradas por idioma** (contadas sobre los assets reales, también por test):

| | propios | núcleos distintos | fusionados | núcleos puros | **total** | colocaciones distintas |
|---|---|---|---|---|---|---|
| **DE** | 305 | 197 | 32 | 165 | **470** | 319 |
| **EN** | 368 | 256 | 65 | 191 | **559** | 444 |

Coinciden con los esperados (470 / 559); no hubo que parar. Las 1104 apariciones de la tarea 0 son 319 + 444 = 763 colocaciones distintas (varias aparecen en más de un pack).

**Chips tras la ronda** (entradas por clase; un verbo puede tener varias): DE trennbar 166, reflexivo 106, mit-Praposition 56, mit-Dativ 21 (y 208 con `simple`, sin chip); EN phrasal-separable 79, phrasal-inseparable 106, verbo-preposicion 38 (y 336 `simple`). Antes de la ronda (solo propios): DE 119 / 98 / 52 / 18, EN 58 / 106 / 36. Los núcleos suman en todas las clases salvo `phrasal-inseparable`, que no cambia (106).

**Una cuenta del prompt que no cierra: el caso «stellen».** El prompt pide **10 entradas** (6 propios + 4 núcleos, «bestellen» entre los núcleos «fusionado con el propio»). Con la fusión obligatoria son **9**: los 6 propios (*bestellen*, *sich etwas bestellen*, *sich vorstellen*, *etwas nachbestellen*, *etwas richtigstellen*, *jemanden kaltstellen*) + los núcleos puros *stellen*, *einstellen*, *umstellen*; *bestellen* es **una** fila, no dos. El «10» de `comprobacion-stellen.md` contaba *bestellen* dos veces (antes de fusionar). El test (`NucleosVerbosTest`, «el caso stellen en aleman…») fija las 9 entradas, exige que *bestellen* aparezca una sola vez con su colocación *online bestellen* debajo, y que estén *stellen*, *einstellen* y *umstellen*.

**Decisiones donde el prompt no cerraba:** (1) una entrada fusionada usa la línea extra del verbo propio (nivel del propio + su `reccion`), no el mínimo con las colocaciones; (2) las colocaciones de cada entrada van ordenadas alfabéticamente y no se filtran con la búsqueda: se ven todas, la que coincidió entre ellas (no se resalta el fragmento); (3) en el nivel de `claseNucleo` se unen las clases de todas las colocaciones del núcleo (R11 garantiza que coinciden); (4) el núcleo puro es un `FilaPalabra` sin estrella y sin `contentDescription` de estrella, así que TalkBack no ofrece nada que marcar ahí; (5) la clave del `LazyColumn` es el infinitivo (único por idioma tras la fusión).

**APK final (empaquetado limpio):** **62 935 447 B**, **+21 504 B** sobre los 62 913 943 B de la Ronda I; es la misma cifra que tras la tarea 0 (el código de las tareas 1–3 está en el dex, verificado: `EntradaVerbo` en `classes6/7.dex`, y `versionName 0.2.6`/`versionCode 8` en el manifest). Manifest sin `INTERNET` ni permisos nuevos. **1134 tests, 0 fallas.**

**Sin revisar por nativo:** nada nuevo (no se agregaron cadenas a `TextosInterfaz`).

**Deudas que siguen** (de la sección 38, sin tocar): los 7 ítems con dos verbos separados por barra solo indexan el primero; `ein Hobby nachgehen`; los núcleos no tienen glosa propia, así que la búsqueda por traducción solo los encuentra a través de sus colocaciones.

## 41. Verificación de Code Ronda J y dos hallazgos de uso (11-oct-2026) — Ronda K

**Verificación de la Ronda J.** Sobre `origin/main` (HEAD `9c1eabc1`, 0.2.6 / versionCode 8): compositor 606 / 56 / 0, R1–R11 en 0, **assets idénticos a la salida del compositor** (la tarea 0 los regeneró bien y los dos guardas de `preBuild` pasan), los tres campos en `VocabularioItem`, manifest sin `INTERNET`. Los totales de la vista dan lo esperado: **DE 470** y **EN 559** con las 32 y 65 fusiones. **Code tiene razón en el caso «stellen»: son 9 entradas, no 10** — el 10 del prompt 24 contaba *bestellen* dos veces, antes de fusionar. Error de Overview al escribir el prompt, no de la implementación. **Guía de testers: `.pdf` regenerado por Overview** (0.2.6, una hoja).

**Hallazgo 1 — la app no usa toda la pantalla.** El contenido empieza debajo de la barra de estado y no llega a la zona del notch. Falta edge-to-edge. Ronda K, tarea 1.

**Hallazgo 2 — el desafío del fin de semana no tiene consigna.** El rediseño de la Ronda F2 (sección 28) quitó la consigna y los requisitos para no repetir la misión (P4), pero se llevó puesta **la tarea**: hoy el bloque dice *cómo* hacerlo (una toma de 6 min, sin notas, cambiá de interlocutor) y no dice *qué* hacer. «Tema: Educación» y «Habilidad: Summarising Complex Information» son etiquetas, no una consigna. **Decisión:** el desafío usa `mision.consigna` como encabezado de la actividad, reencuadrada como tarea oral con los minutos de `evidencia.oralMin`; sin requisitos ni micro-tareas. **P4 no cambia de criterio pero sí de lectura:** la misión completa es consigna + requisitos + micro-tareas + evidencia escrita; reusar solo la consigna con encuadre oral mantiene el desafío distinto de su fuente. El test de P4 pasa a comparar el bloque completo en vez de la presencia de la consigna. Ronda K, tarea 2. Versión 0.2.7.

**Nota de método:** este es el segundo hallazgo que sale de usar la app y no de revisar código (el primero fue el recorte por nivel de la vista Verbos). Conviene que Fer pruebe cada versión antes de encadenar otra ronda.

## 42. Code Ronda K — ✅ implementada (Code, 11-oct-2026), versión 0.2.7

Tres commits, uno por tarea, sobre la Ronda J. Contenido, esquema y assets sin tocar (los dos guardas de `preBuild` en verde sin regenerar nada).

- **Tarea 1 — pantalla completa (edge-to-edge)** (`MainActivity.kt`, `ui/BarrasSistema.kt`): `enableEdgeToEdge()` en `onCreate` y `layoutInDisplayCutoutMode = SHORT_EDGES` (API 28+): el fondo del tema se dibuja **detrás** de la barra de estado, la de navegación y la zona del notch. Los insets se resuelven en **un solo archivo**:
  - **Arriba y a los costados** (barra de estado, notch, recorte en horizontal): una vez, en la raíz — el `NavHost` lleva `margenesSuperiorYLateralesDelSistema()` (`safeDrawing` solo `Top + Horizontal`). Los encabezados nunca quedan bajo la barra ni bajo el recorte de la cámara.
  - **Abajo** (barra de gestos o de botones, y el teclado): **no** se aplica al contenedor, así el contenido pasa por **debajo** de la barra. Cada scroll termina con el relleno justo: `finDeScrollSobreBarras()` (dentro del scroll, entre `verticalScroll` y el `padding` propio) en **Ajustes, Acerca de, Guardados** (la lista) y las **dos fichas** (ficha y semana especial); en la **Biblioteca** es `contentPadding` del `LazyColumn` (`paddingInferiorDelSistema()` + 8 dp), no `padding` del contenedor. En Guardados, Acerca de y Biblioteca el `padding` inferior del contenedor se pasó al interior del scroll por la misma razón.
  - **Iconos de las barras:** `AjustarIconosDeBarras(fondo)` los pone oscuros sobre fondo claro y claros sobre fondo oscuro, decidido con la **luminancia del fondo real del tema elegido** (cruce de contraste blanco/negro, ~0,179), no con el modo del sistema: así cambian con el tema y el modo de Ajustes, en los 4 temas y en el dinámico.
  - **Tests:** `IconosBarrasSistemaTest` (4 temas × claro/oscuro, y que la elección sea siempre la de mayor contraste, ≥ 4,5:1) y `BarrasSistemaPantallaTest`, que **inyecta insets simulados** (estado 90 px arriba, navegación 150 px abajo) en `LenguAppApp` y comprueba que el botón de Ajustes, «Volver» de Ajustes y de Acerca de arrancan bajo la barra de estado, que al final del scroll de Acerca de la última línea queda **sobre** la barra de navegación, y que sin barras nada se corre.
- **Tarea 2 — consigna del desafío** (`ui/ContenidoSemanalScreen.kt`, `presentacion/Desafio.kt`, `TextosInterfaz.kt`): el bloque queda **título «Desafío de fin de semana» → línea oral → consigna → Tema y Habilidad → Condiciones → Frases**. La consigna es `mision.consigna` (en el idioma que se muestra, con el switch de traducción de A2/B1), en `headlineSmall` **negrita**, el peso más fuerte del bloque, justo debajo de la línea oral. La línea oral usa los minutos de `evidencia.oralMin` redondeados. **`RETO_LINEA` se fundió en la línea nueva** (`RETO_CONSIGNA_LINEA`; se quitó la clave vieja y `textoRetoLinea`). Sin `mision.consigna` el encabezado cae a la descripción y, como último recurso, al título (`consignaDelDesafio`, con test). **P4:** el test compara el bloque completo contra la misión (la misión trae consigna + requisitos + micro-tareas; el desafío solo la consigna, más textos propios) y falla si el desafío vuelve a mostrar un requisito o una micro-tarea.
- **Tarea 3 — versión 0.2.7 (versionCode 9)**; fila del Desafío de la guía actualizada (consigna con minutos arriba), **más corta que la anterior** (240 vs 242 caracteres) y sin otros cambios: la guía sigue entrando en una hoja (el `.pdf` lo regenera Overview). Ninguna otra fila hablaba del aspecto de la pantalla.

**Texto final de la línea oral** (`{oralMin}` = minutos redondeados de la ficha; «min» y no «minutos» para no depender del plural):

| | |
|---|---|
| ES | Habla {oralMin} min sobre esto, en voz alta y de una sola vez (aunque la consigna pida escribir): |
| EN | Speak for {oralMin} min about this, out loud and in one go (even if the task asks you to write): |
| DE | Sprich {oralMin} Min. lang darüber, laut und in einem Zug (auch wenn die Aufgabe ein Schreiben verlangt): |
| FR | Parle {oralMin} min de cela, à voix haute et d'un seul trait (même si la consigne demande d'écrire) : |
| IT | Parla per {oralMin} min di questo, ad alta voce e tutto d'un fiato (anche se la consegna chiede di scrivere): |
| PT | Fale por {oralMin} min sobre isso, em voz alta e de uma só vez (mesmo que o enunciado peça para escrever): |

**Hallazgo que el prompt no previó: muchas consignas piden ESCRIBIR.** `mision.consigna` es la consigna de la **evidencia escrita**, así que bastantes empiezan con «Escribe cinco oraciones sobre…», «Write a postcard…», «Schreib…». Una búsqueda gruesa de verbos de escritura (schreib/verfass/write/escribe/écris/scrivi/escreva…) las encuentra en **~276 de las 606 fichas** (a ojo, sobre-cuenta: incluye «Schreiben» como sustantivo). Con la línea del prompt tal cual («Habla N minutos sobre esto…:») el encabezado diría «Habla 6 min sobre esto: Escribe cinco oraciones…», incoherente. **Decisión de Code, fácil de revertir:** la línea trae la aclaración «(aunque la consigna pida escribir)» en los 6 idiomas, que no inventa ningún dato. **Lo que de verdad lo resuelve es contenido** (una consigna oral por ficha, o un campo `consignaOral` en el esquema) y queda para Overview/Fer decidir.

**Tamaño del APK** (empaquetado limpio): **62 951 831 B**, **+16 396 B** sobre los 62 935 447 B de la Ronda J. Manifest sin `INTERNET` ni permisos nuevos. **1143 tests, 0 fallas.**

**Lo que NO se pudo probar aquí (no hay dispositivo):** barra de navegación por botones vs. gestos, notch real y orientación horizontal. Está cubierto con insets simulados en Robolectric (arriba y abajo) y por construcción (`safeDrawing` incluye el recorte de la cámara y los costados), pero hay que mirarlo en el teléfono de Fer. **Pantallas donde el contenido no scrollea bajo la barra de ESTADO:** el inset superior se aplica una vez en la raíz, así que las listas se desplazan por debajo de la barra de **navegación** pero se cortan en el borde inferior de la de **estado** (detrás de ella se ve el fondo del tema). Es una decisión: dejar que el texto pase bajo los iconos de la barra de estado exige scrim o encabezado fijo por pantalla. En las pruebas con insets simulados (principal, Ajustes, Acerca de) no quedó contenido tapado; **Guardados, Biblioteca y las fichas llevan el mismo patrón pero sin test propio de insets** (el final de la lista de la Biblioteca y de Guardados es lo primero que conviene mirar en el teléfono).

**Elecciones donde el prompt no cerraba:** (1) el título «Desafío de fin de semana» se mantiene como encabezado del bloque (identifica la sección) y la consigna es lo primero del contenido; (2) la línea oral usa «min» y no la palabra «minutos» (sin plural), y no lleva el tema (va justo debajo en «Tema:»); (3) tuteo «Habla» (no «Hablá»), por P9; (4) el teclado entra en el relleno inferior (`safeDrawing` incluye `ime`); (5) no se tocó el `windowSoftInputMode`.

## 43. Verificación de Code Ronda K y el tuteo que R9 nunca miró (11-oct-2026)

**Verificación de la Ronda K.** Sobre `origin/main` (HEAD `47917c13`, 0.2.7 / versionCode 9): compositor 606 / 56 / 0, R1–R11 en 0, assets intactos e idénticos a la salida del compositor, manifest sin `INTERNET`. **Guía de testers: `.pdf` regenerado por Overview** (0.2.7, una hoja). La ronda **no se probó en ningún teléfono**: ni barra de gestos contra botones, ni notch real, ni horizontal. Lo cubren tests de Robolectric con barras simuladas. Lo primero que hay que mirar en el teléfono es **el final de las listas de Biblioteca y Guardados**, que no tienen test propio de insets.

**Hallazgo de Code, correcto: la consigna está escrita para escribir.** `mision.consigna` es la consigna de la evidencia **escrita**: medido por Overview, **221 de las 606** empiezan con un verbo de escritura («Escribe…», «Write…», «Schreiben…»), contra 81 que empiezan con uno de habla. Con la línea oral delante, el encabezado del desafío dice «Habla 6 min sobre esto: Escribe cinco oraciones…». Error de Overview al escribir el prompt 25: mandé usar ese campo sin leer cómo está redactado. Code puso el paréntesis «(aunque la consigna pida escribir)», que es honesto pero pobre para el texto más grande de la pantalla. **Arreglo propuesto, sin tocar contenido:** cambiar el encuadre de la línea oral para que la consigna se lea como **tema**, no como orden — «Esto es lo que vas a contar. En voz alta, {oralMin} min, de una sola vez, sin escribir nada:». El desajuste desaparece sin paréntesis y sin contenido nuevo. El arreglo de fondo (una consigna oral propia por ficha) son 606 textos nuevos y queda anotado, no decidido.

**Hallazgo de Overview, más grave: ~699 cadenas planas en alemán siguen en trato formal.** El prompt 19 pidió marcar el registro **«bajo las claves `de`, `fr`, `it`»** — frase mía — y eso dejó afuera **todas las cadenas planas**: los campos de instrucción de B2, C1 y C2 no son bilingües, así que son `string` y R9 nunca los miró; lo mismo con los campos de A2/B1 todavía sin traducir. Reparto por campo y nivel en el prompt 26. Ejemplos: «Nehmen Sie Stellung zu der Frage…», «Wie sicher sind Sie wirklich?». **Ahora es urgente porque la Ronda K puso la consigna como encabezado en negrita del desafío**: un usuario de alemán C2 abre la app un sábado y lo primero que lee, en el texto más grande de la pantalla, es «Nehmen Sie Stellung…». Incumple P9. **Prompt 26 para Traducciones:** ampliar R9 a las cadenas planas (con cuidado de los falsos positivos: `Sie` a principio de oración puede ser «ella/ellos») y corregir. Las dos exenciones legítimas de `DE-F13` (el contraste Sie/du que se enseña) tienen que seguir pasando.

**Orden:** prompt 26 (Traducciones) → Overview regenera los assets → Code arregla el encuadre de la línea oral en su próxima ronda, junto con lo que salga de probar la 0.2.7 en el teléfono.

**Prompt 26 cerrado (Traducciones, 11-oct-2026).** 458 cadenas planas de B2/C1/C2 pasadas a `du` en 128 archivos; R9 ahora mira los campos de instrucción tanto en objeto bilingüe como en `string` plano (en la cadena plana el idioma es el de la ficha). Compositor 606/56/0, R1–R11 en 0, los dos autotests sin fallos, estructura JSON idéntica, mismas marcas y ningún tope superado. El reparto real por campo y nivel, las 19 excepciones y los tres defectos que encontró el barrido están en **6.1.4**. Dos cosas para la lista de Code: (a) con el prompt 19 van **25 `promptCorreccion` alemanes alargados** porque el imperativo informal los dejaba bajo el mínimo de 150 del schema — si se prefiere bajar el mínimo, las 25 frases se quitan; (b) **la consigna de C2 ya no dice «Nehmen Sie Stellung…»**, así que el encabezado del desafío del fin de semana cumple P9; el desajuste que queda es el de la línea oral, no el del registro.

## 44. Tutorial por partes (decidido por Fer, 11-oct-2026) — guion listo, implementación diferida

Fer quiere un tutorial con foco (oscurece la pantalla menos lo que señala), **por partes**: cada sección lanza su propio tour la primera vez que se entra (ficha al primer arranque, después Biblioteca, Guardados y Ajustes), ninguno de más de 8 pasos, unos 25 en total. «Saltar» saltea **todos** los tours restantes; un botón en Ajustes reinicia los contadores y vuelven a lanzarse. Pocos anclajes por tour y **degradación sin romperse**: si un paso no encuentra lo que señala, se saltea; un test falla cuando un anclaje desaparece, para que la ruptura no sea silenciosa.

**Guion escrito:** `proyecto/docs/tutorial-guion.md` (español como idioma fuente; las otras 5 lenguas se traducen cuando el guion esté cerrado). Incluye la bienvenida, los cuatro tours, el del desafío y las decisiones abiertas.

**Regla dura 2, respetada sin enmienda.** La idea original era mostrar el primer tutorial en el idioma del sistema, pero hoy la app lee ese idioma **solo si el usuario elige** «Idioma del sistema» (y por defecto está apagado, con `idiomaInterfaz = ES`). Leerlo en el primer arranque sería una tercera enmienda a una regla ya enmendada dos veces. **Solución: el primer paso de la bienvenida es elegir el idioma.** De paso arregla algo que hoy está mal: un usuario alemán o inglés ve la app en español hasta que entra a Ajustes.

**Costo:** ~25 pasos + bienvenida = **~300 cadenas** en 6 idiomas, anclajes en 5 pantallas, un ajuste nuevo por sección más un «salteado» global (es ajuste, no progreso: la regla dura 4 lo permite, pero **no** viaja en la copia de seguridad de Guardados).

**Cuándo:** la **bienvenida** (idioma, nivel y el mensaje «esto no enseña el idioma, te hace practicarlo») no tiene anclajes, es lo más valioso y lo más barato: se puede hacer en cualquier momento. Los **tours con foco**, recién cuando la interfaz deje de moverse. **Al implementarlo, el tutorial entra en el checklist de cada ronda de Code**, como la guía de testers y la versión.

## 45. Verificación del prompt 26 (tuteo en cadenas planas) y Code Ronda L

**Verificado por Overview** sobre `origin/main` (HEAD `e34206bd`): compositor 606 / 56 / 0, R1–R11 en 0, autotest de R9/R10 con **39 casos y 0 fallos**, autotest de R11 sin fallos. **Auditoría independiente del residuo:** quedan 244 cadenas alemanas con `Sie`/`Ihnen`/`Ihr` en campos de instrucción, pero al descontar lo que va entre « » y entre `*…*` son **33**, y **las 33 son legítimas**, leídas una por una: o enseñan el contraste du/Sie (subtítulos «Du oder Sie…», autochequeos «Ist die Anrede konsequent?», notas «Sie wird immer grossgeschrieben»), o usan `Sie`/`sie` como pronombre de tercera persona («Sie zu verstehen ist…», «Sie funktioniert nur, wenn…»). **Ninguna trata de usted al usuario.** La corrección de Traducciones es correcta y completa.

**Su conteo es mejor que el mío.** Midió 458 convertidas y 19 exentas sobre 477 detectadas; mi «~699» incluía ~201 cadenas de A2/B1 que no eran planas (ya estaban en `du` desde el prompt 19) y las 360 de `microtareas/dia` (`lun`, `mie`, `vie`), que no llevan trato. En B2/C1/C2 las dos mediciones coinciden casi celda por celda.

**Lo que encontró leyendo a mano y ningún chequeo veía:** tres oraciones rotas por el transformador (`*Bildungsideal* vertritt?` sin sujeto, `hör den Unterschied?`, un auxiliar coordinado en plural). Ya no tenían `Sie`, así que ni R9 ni el script del diff podían marcarlas. Es el argumento para seguir pidiendo barrido a ojo en cada ronda de contenido.

**Aviso que conviene recordar:** las excepciones de R9 y R10 van **por texto exacto**, así que cualquier pase que toque una cadena exenta tiene que actualizar la excepción en el mismo commit (le pasó con el `promptCorreccion` de `DE-V08-B2`, exento de R10 por hablar de «ss/ß»). El `--autotest` avisa cuando una excepción queda de más.

**Assets regenerados por Overview** (el contenido cambió y el modelo de Kotlin no necesita nada nuevo: solo cambió texto).

**Hallazgo de uso de Fer en la 0.2.7: la consigna del desafío se ve enorme y en negrita, y queda feo.** Causa: el prompt 25 pedía «el peso tipográfico más fuerte del bloque» y Code lo resolvió con `headlineSmall` + `Bold`. Para un titular estaría bien; la consigna es prosa de dos a cuatro renglones. **Error de Overview al escribir el prompt.** Ronda L: tamaño de cuerpo con peso medio, jerarquía por posición y aire, no por tamaño; y el encuadre de la línea oral sin el paréntesis «(aunque la consigna pida escribir)», para que la consigna se lea como tema y no como una orden que la línea desmiente. Versión 0.2.8.

**Pendiente abierto, decisión de Code:** son ya **25 `promptCorreccion`** alemanes alargados (11 del prompt 19 y 14 del 26) porque el imperativo informal queda bajo el mínimo de 150 caracteres del esquema. Posición de Overview: dejarlo como está; el mínimo existe para que el prompt sea útil. Code opina en la Ronda L y se decide después.

## 46. Code Ronda L — ✅ implementada (Code, 12-oct-2026), versión 0.2.8

Tres commits, uno por tarea, sobre la Ronda K y el tuteo de Traducciones (assets ya regenerados por Overview). Contenido, esquema y assets sin tocar.

- **Tarea 1 — la consigna deja de ser un titular** (`ui/ContenidoSemanalScreen.kt`): de `headlineSmall` + `Bold` a **`bodyLarge` con `FontWeight.Medium`, sin negrita y en `onSurface` pleno**. La línea de encuadre que la precede pasa a **`bodyMedium` en `onSurfaceVariant`** (línea de apoyo), y la consigna lleva 6 dp de aire arriba y abajo. La jerarquía la dan **la posición** (primera cosa del bloque), **el color** (pleno contra el de apoyo) y **el aire**; no el tamaño. Test `ConsignaDesafioEstiloTest` (dos casos, **letra al 100 % y al 160 %** con `@Config(fontScale = 1.6f)`): lee el `TextStyle` que recibe el texto (no píxeles) y comprueba bodyLarge, peso medio, no Bold, no más grande que `titleMedium`, colores, que la escala de letra llegó al texto, y que el **bloque entero** (línea oral, consigna, Tema, Habilidad, Condiciones con sus 4 reglas, Frases con sus 3 expresiones) está presente y **en orden**, con la consigna en pocos renglones.
- **Tarea 2 — encuadre sin paréntesis** (`TextosInterfaz.kt`): la línea oral presenta la consigna como **el tema de lo que se va a contar**, no como una orden. El paréntesis «(aunque la consigna pida escribir)» se quitó en los 6 idiomas; los minutos siguen saliendo de `evidencia.oralMin`. Test: ningún idioma tiene paréntesis, ES y EN exactos, `{oralMin}` resuelto. P4 intacto: sus tests (sin requisitos ni micro-tareas, bloque completo ≠ misión) siguen pasando.
- **Tarea 3 — versión 0.2.8 (versionCode 10)**; la guía lleva la versión y **la fila del desafío no cambia** (sigue siendo cierta: «arriba la consigna con los minutos…»), así que no crece.

**Línea oral final** (`{oralMin}` = minutos redondeados de la ficha):

| | |
|---|---|
| ES | Esto es lo que vas a contar. En voz alta, {oralMin} min, de una sola vez y sin escribir nada: |
| EN | This is what you'll talk about. Out loud, {oralMin} min, in one go, with no writing: |
| DE | Davon erzählst du. Laut, {oralMin} Min. am Stück und ohne etwas aufzuschreiben: |
| FR | Voilà ce que tu vas raconter. À voix haute, {oralMin} min, d'un seul trait et sans rien écrire : |
| IT | Questo è ciò che racconterai. Ad alta voce, {oralMin} min, tutto d'un fiato e senza scrivere nulla: |
| PT | Isto é o que você vai contar. Em voz alta, {oralMin} min, de uma só vez e sem escrever nada: |

**Lo demás del bloque, con el mismo criterio.** Nada más quedó desproporcionado: las condiciones son `bodyMedium` con icono, las frases `bodyMedium`/viñetas y Tema/Habilidad `labelLarge` + `bodyMedium`. Lo único grande que queda son los **títulos de sección en `titleLarge` Bold** («Desafío de fin de semana», «Condiciones», «Frases para usar»), que son los mismos de toda la ficha (`Seccion`) y no los toqué; tres seguidos con la letra al 160 % pesan, pero el test al 160 % confirma que todo el bloque sigue presente y legible. Si se quiere aligerar, el candidato es `Seccion` entero (afecta a toda la app), no el desafío.

**Tamaño del APK** (empaquetado limpio): **62 950 571 B**, **−1 260 B** sobre los 62 951 831 B de la Ronda K (el tuteo de Traducciones acortó algo los JSON; el código nuevo pesa lo mismo). Manifest sin `INTERNET` ni permisos nuevos. **1145 tests, 0 fallas.**

**Opinión sobre el mínimo de 150 caracteres del `promptCorreccion` (no se tocó):** hay que **dejarlo**. Medido sobre los assets: la distribución de `promptCorreccion` en la lengua de cada ficha tiene su p10 entre 153 y 169 en 8 de los 10 pares (los otros dos, 188 y 165), **también en inglés** (EN A2 155, B1 150, C2 152), donde no hubo ningún cambio de registro; es decir, el mínimo funciona como piso de calidad parejo en los dos idiomas y los 25 alargados de alemán quedan dentro de esa misma banda baja (mediana de DE ~170 en A2/B1/C2). Bajar el piso no arregla nada, solo permitiría que cualquier prompt futuro se acorte hasta ser menos útil; alargar 25 frases en estilo de la casa fue el costo correcto. Lo que sí conviene vigilar es que los prompts más cortos (los 21 de DE A2 y 16 de DE C2 bajo 160) sigan siendo instrucciones completas y no solo piso cumplido.

## 47. Verificación de Code Ronda L y estado de las deudas (12-oct-2026)

**Verificación.** Sobre `origin/main` (HEAD `eec0fa7e`, 0.2.8 / versionCode 10): compositor 606 / 56 / 0, R1–R11 en 0, assets intactos e idénticos a la salida del compositor, manifest sin `INTERNET`. La ronda cierra los dos errores de los prompts 25 y 27 de Overview (el titular y el paréntesis). **Guía de testers: `.pdf` regenerado por Overview** (0.2.8, una hoja). `ConsignaDesafioEstiloTest` mira el `TextStyle` y no píxeles, y corre al 100 % y al 160 %: es la forma correcta de fijar una decisión de diseño sin atarla a un dispositivo.

**La opinión de Code sobre el mínimo de 150 caracteres se acepta y la decisión queda cerrada:** se deja. Su argumento es mejor que el mío porque está medido — el p10 está entre 153 y 169 en 8 de los 10 pares, **también en inglés**, donde no hubo cambio de registro, así que el piso no es un artefacto del tuteo alemán. Los 25 `promptCorreccion` alargados se quedan. Queda su recomendación: vigilar que los más cortos (21 en DE A2 y 16 en DE C2, bajo 160) sigan siendo instrucciones completas.

**Los títulos de sección en `titleLarge` negrita** (tres seguidos en el desafío) son lo único que queda pesado con la letra al 160 %. Code no los tocó porque son los de toda la app (`Seccion`). **Decisión de Overview: no se tocan por ahora**; si se aligeran, es una decisión de diseño para toda la app y va en una ronda propia, no colgada del desafío.

### Estado de las deudas, al 12-oct-2026

**Bloquean publicar:**
- **Revisión por hablante nativo de fr, it y pt**: interfaz, textos de la tienda, nombres de las clases de verbo, estados vacíos y «sin resultados». Es la deuda más vieja y la única que no puede resolver ninguna conversación: necesita personas.
- **Licencias de las voces Piper**: sin documentar en el repo. Las documenta Overview.
- **Ícono y pantalla de carga**: Fer tiene que generar el arte con el prompt 16. El lugar donde se enchufan ya está documentado (25.1).
- **Decisión léxica `camarero`** (`mesero` / `mozo`): abierta desde el prompt 14.

**Funcionalidad incompleta, conocida y declarada:**
- **Audio**: los `.ogg` están dentro del APK y no hay reproductor. «Acerca de» y la guía ya no lo prometen.
- **Calendarios**: solo 2026 (parcial) y 2027. El índice de habilidades y la app lo reflejan.
- **Tutorial y pantalla de bienvenida**: guion escrito (`docs/tutorial-guion.md`, sección 44), sin implementar.

**Contenido, sin urgencia:**
- Los ~9558 `*x*` sin « » (14.1), las 273 expresiones sin categoría, el maestro de traducción sin `erroresContrastivos` (20), los ejemplos con el verbo separado (5 de 108), los núcleos sin glosa propia (37), los 7 ítems con barra que pierden el segundo verbo (39), y `ein Hobby nachgehen` junto al correcto `einem Hobby nachgehen` (4 apariciones), que «Verbos» tiene que entregar en un parche aparte.

**Riesgo de fondo, sin plan todavía:** el contenido de 2028 en adelante y quién lo mantiene. Es el riesgo que Fer anotó al principio del proyecto («el proyecto muere en el año 2») y sigue sin respuesta.

**Probar en el teléfono**: la 0.2.7 trajo edge-to-edge y **no se probó en ningún dispositivo real** (ni gestos contra botones, ni notch, ni horizontal). Lo primero que hay que mirar es el final de las listas de Biblioteca y Guardados.
## 48. Audio, licencias de las voces y `mesero` (Overview, 12-oct-2026)

**Decisiones de Fer (12-oct-2026):** `camarero` → **`mesero`** (o `mozo`, indistintamente); **ícono y pantalla de carga se posponen** hasta que haya un diseño que le guste; **los calendarios quedan como están** (resto de 2026 y todo 2027; el 2028 se arma durante 2027); el **edge-to-edge de la 0.2.7 probado en el teléfono y correcto**.

**`mesero`:** hecho por Overview, 3 apariciones en 2 archivos (`de-A2-2027-T10-2`, `en-A2-2027` ×2). Cierra la decisión léxica abierta desde el prompt 14.

### Por qué el audio nunca sonó
No faltaba grabar: **los 8202 `.ogg` ya viajan en el APK** (51 MB) y `proyecto/audio-estado.json` sabía desde septiembre qué archivo corresponde a cada texto. Lo que faltaba era el **puente**: ese archivo es de trabajo y la app nunca lo leyó. Nadie lo notó porque el audio se produjo en una pista aparte y las rondas de Code siempre tuvieron otra cosa adelante.

**Puente hecho por Overview:** `proyecto/tools/indice_audio.py` emite **`app/src/main/assets/audio/indice.json`** (544 KB, 8143 textos), con forma `{idioma: {clase: {texto exacto: archivo}}}`. `--comprobar` dice si quedó desactualizado. **Cobertura medida:** vocabulario 4055/4066 (DE) y 4088/4088 (EN); expresiones **100 %** en los dos; ejemplos 548/1066 (DE) y 477/1071 (EN), que son los marcados para audio. 59 `.ogg` quedaron huérfanos (textos editados después de grabar): inofensivos, el índice los ignora.

### Licencias de las voces — deuda cerrada, con un problema
Documentadas en `proyecto/docs/licencias-voces.md`. Piper es MIT y el repositorio de modelos también, **pero cada voz arrastra la licencia de su corpus**:
- **`de_DE-thorsten-high` → CC0.** Limpia, se publica sin problema.
- **`en_US-ryan-high` → CC BY-NC-SA 4.0.** La cláusula NC y la SA son **incompatibles con el CC BY-SA 4.0** del contenido.
- **`en_US-lessac-high` → licencia de Blizzard 2013**: nominal, solo investigación, **sin uso comercial y sin redistribución**.

**Consecuencia: los 4100 clips en inglés no se pueden publicar; los 4043 alemanes sí.** Candidata para regenerar el inglés: **`en_GB-alba`**, corpus **CC BY 4.0**, a cambio de acento británico. Antes de elegir hay que leer el `MODEL_CARD` de cada voz candidata, no el README: varias que parecen libres (`en_US-hfc_*`) también son CC BY-NC-SA.

**Código Ronda M (prompt 28):** reproductor desde los assets sin permisos nuevos, botón de altavoz solo donde hay audio, y el **audio inglés detrás de un interruptor de compilación** (encendido en debug, apagado en release) hasta que se regeneren los clips. La velocidad lenta al 80 % existe pero **no está en el índice** (`audio-estado.json` guarda un archivo por texto): Code informa cuántos hay y Overview decide si amplía el índice.

**Sobre el riesgo del «año 2»:** Fer lo corrige — el contenido no se congela, se sigue desarrollando durante el año 1 para tener el 2 y el 3. Queda como trabajo continuo, no como riesgo abierto.

## 49. Code Ronda M — ✅ implementada (Code, 12-oct-2026), versión 0.2.9

Cuatro commits, uno por tarea, sobre la Ronda L y el índice de audio de Overview. Contenido, esquema y los `.ogg` sin tocar; los dos guardas de `preBuild` en verde. **Manifest sin `INTERNET` ni permisos nuevos** (solo el permiso interno de AndroidX), comprobado en el manifest fusionado de la build.

- **Tarea 1 — reproductor** (`audio/`): `IndiceAudio` lee `assets/audio/indice.json` con parser estricto (idioma o clase desconocidos = error) y da la ruta `audio/<IDIOMA>/<archivo>` de un texto exacto; `ControlAudio` es lo que ven las pantallas (un solo clip a la vez, estado «sonando» solo en memoria: **nada se guarda**, regla 4); `ReproductorAssets` reproduce con **`MediaPlayer` + `AssetFileDescriptor`** (viene con Android: **0 B de librería**; ExoPlayer habría sumado varios MB). Canal de **medios** (`USAGE_MEDIA`, voz), foco de audio «con duck» (la música baja, no se corta) que se devuelve al terminar; con el volumen de medios en cero no suena y no pasa nada; no se fuerza ningún otro canal. **Archivo faltante o ilegible: ni crash ni cartel**; el botón vuelve a «Escuchar» y se escribe una línea en el log de depuración. `MainActivity` carga el índice una vez, provee el control, **corta el audio en `onStop`** y lo libera en `onDestroy`. Los `.ogg` ya están guardados sin comprimir en el APK (`Stored`), que es lo que permite `openFd`.
- **Tarea 2 — el botón** (`ui/BotonAudio.kt`): altavoz discreto **solo si el texto tiene clip** (si no, no se dibuja nada); mientras suena pasa a «Detener» con el color de acento; `contentDescription` «Escuchar»/«Detener» en los 6 idiomas; área táctil de 48 dp (el mismo `IconButton` de 40/36 dp que la estrella, que ya amplía la zona). Dónde: **ficha** (cada fila de vocabulario y de expresiones, y los ejemplos con clip), **Biblioteca** y **Guardados** (`FilaPalabra`, la fila compartida) y **vista Verbos**. `DetenerAudioAlSalir` corta el audio al salir de la pantalla o al cambiar de ficha.
- **Tarea 3 — velocidad lenta: no hay ninguna en los assets.** `assets/audio/` tiene **8202 `.ogg` = 8143 indexados + 59 «otrosArchivos»** (versiones viejas de textos reeditados); no existe ningún clip al 80 %, ni con otro nombre. El manifiesto (`audio-manifiesto.md`) **planeó** los clips lentos («362 textos → 724 clips», «712 → 2848»), pero **nunca se generaron**: `audio-estado.json` guarda un `archivo` por texto y ningún campo de velocidad. Para ofrecerlos haría falta (1) **generar los clips lentos** (Piper con `--length_scale` ≈ 1,25, ~8143 clips más, ~51 MB), (2) un **campo más en el índice** (p. ej. `{"texto": {"normal": "a.ogg", "lento": "a_80.ogg"}}`, que agregaría Overview), y (3) un botón o gesto largo que elija la velocidad. No se implementó.
- **Tarea 4 — versión 0.2.9 (versionCode 11)**; la fila de la guía dice que el audio suena, que en los ejemplos es parcial y que por ahora solo en alemán (191 caracteres, contra 184 antes; la guía sigue en una hoja, el `.pdf` lo regenera Overview). «Acerca de» ahora nombra solo la voz alemana: «Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0.» en los 6 idiomas; cuando se regeneren los clips ingleses hay que sumar la voz inglesa en `ACERCA_CREDITOS_TEXTO`.

**Decisiones de Code.** (1) **El altavoz va ANTES de la estrella** (a su izquierda): la estrella queda en el borde derecho como ancla del pulgar, igual en todas las pantallas, y el altavoz es lo que cambia de una fila a otra. (2) **En la vista Verbos lo llevan el verbo propio y cada colocación** (todas son ítems reales con su clip, si lo tienen); el **encabezado del núcleo no** (no es un ítem, no tiene texto grabado). (3) En la ficha, los ejemplos sin clip conservan la viñeta de siempre. (4) La fila no crece: el área de 48 dp la amplía el propio `IconButton` sin cambiar el alto de la fila (igual que la estrella; en filas de una sola línea, sin traducción, puede sumar unos dp). Con la letra al 160 % estrella y altavoz suman 80 dp a la derecha y el texto se reparte el resto; **conviene mirarlo en el teléfono**.

**El interruptor del audio inglés** está en **`app/build.gradle.kts`, `buildTypes`** (`buildConfigField("boolean", "AUDIO_INGLES", …)`): `true` en `debug`, `false` en `release`. Para encenderlo en release (cuando se regeneren los clips con una voz de corpus limpio), cambiar ese `"false"`. Un test comprueba el invariante `AUDIO_INGLES == DEBUG` en las dos variantes.

**⚠ Lo que el interruptor NO resuelve: los `.ogg` ingleses siguen dentro del APK de release.** Apagar el reproductor evita que **suenen**, pero el problema de licencia (CC BY-NC-SA 4.0 y Blizzard 2013) es de **redistribución**, y `assets/audio/EN/` (4133 archivos, ~26 MB) viaja igual en cualquier release. Mientras no se regeneren, **un release no debería llevar esa carpeta**: el arreglo es excluirla del empaquetado de release (o moverla a un `sourceSet` de debug). Esta ronda no tocaba los assets y no lo hizo; hay que decidirlo antes de publicar.

**Tamaño del APK** (empaquetado limpio, **debug**, con el inglés encendido): **63 137 036 B**, **+186 465 B** sobre los 62 950 571 B de la Ronda L: el índice (552 713 B sin comprimir) suma **169 905 B comprimido** y el código de audio + botones, ~16,5 KB. Los 8202 `.ogg` ya estaban en el APK. **1170 tests, 0 fallas.**

## 50. Verificación de Code Ronda M y Ronda N (12-oct-2026)

**Verificación.** Sobre `origin/main` (HEAD `17d211b2`, 0.2.9 / versionCode 11): compositor 606 / 56 / 0, R1–R11 en 0, assets de contenido intactos, `indice_audio.py --comprobar` dice «índice al día», **manifest sin `INTERNET` ni permisos nuevos**. La ronda está bien resuelta: `MediaPlayer` + `AssetFileDescriptor` es la elección correcta (0 B de librería contra los varios MB de ExoPlayer), el foco de audio «con duck» y el canal de medios son lo que corresponde para clips de voz, y el fallo silencioso ante un archivo que falta cumple lo pedido. **Falta oírlo en un teléfono**: Robolectric no cubre `MediaPlayer` real.

**El aviso de Code es correcto y es lo más importante de la ronda:** el interruptor `AUDIO_INGLES` evita que el audio inglés **suene**, pero el problema de licencia es de **redistribución** y `assets/audio/EN/` (4133 archivos, ~26 MB) viaja igual en cualquier release. **Ronda N, tarea 1:** sacar esa carpeta del empaquetado de release (preferentemente moviéndola al `sourceSet` de debug, donde un release ni la ve), y decidir qué hacer con las entradas inglesas del `indice.json`. Se revierte junto con el interruptor cuando se regeneren los clips.

**Clips lentos: no existen.** El manifiesto los planeó pero nunca se generaron; `audio-estado.json` guarda un archivo por texto y ningún campo de velocidad. Para ofrecerlos haría falta generar ~8143 clips más (~51 MB), ampliar el índice con un campo de velocidad (Overview) y un control en la interfaz. **Decisión de Overview: no se hacen por ahora** — duplicarían el peso del APK, que ya está en 63 MB, para una función que nadie pidió todavía. Si Fer los quiere, se decide entonces y primero hay que resolver lo del inglés.

**Ronda N (prompt 29):** sacar el audio inglés del release y **la pantalla de bienvenida** (punto 0 de `docs/tutorial-guion.md`): elegir idioma de la app — hoy por omisión es español, así que un usuario alemán o inglés ve la app en español hasta que descubre Ajustes —, elegir qué practica y en qué nivel, y el mensaje de que la app **no enseña el idioma, hace practicarlo**. Sin anclajes, así que no se rompe cuando cambie la interfaz: es la parte del tutorial con más valor y la más barata. Versión **0.3.0**.

## 51. Code Ronda N — ✅ implementada (Code, 12-oct-2026), versión 0.3.0

Tres commits, uno por tarea, sobre la Ronda M. Contenido, esquema y assets de contenido sin tocar; los dos guardas de `preBuild` en verde; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes.

- **Tarea 1 — el audio inglés no viaja en un release** (`app/build.gradle.kts`): en vez de mover 4133 archivos al `sourceSet` de debug (lo que habría roto, en silencio, las herramientas de Overview que leen `assets/audio/{DE,EN}`: `indice_audio.py`, `sincronizar_audio_estado.py`, `exportar_audio_maestro.py`), el release **ignora la carpeta por variante**: `androidComponents { onVariants(selector().withBuildType("release")) { it.androidResources.ignoreAssetsPatterns.add("<dir>EN") } }`. **Es igual de segura** por una razón que no depende de la configuración: la tarea **`verificarReleaseSinAudioIngles`**, colgada de `packageRelease` / `packageReleaseBundle` / `bundleRelease`, mira los **assets ya fusionados del release** (lo que de verdad entra al APK/AAB) y **hace fallar el build** si hay un solo `.ogg` en `audio/EN`. Probado a mano: con la exclusión comentada falla con «El release lleva 4133 clips de audio ingles…»; con ella, el release no lleva ninguno. Los clips siguen en el repo y el **debug los empaqueta** (4133 en el APK de debug). Un test (`AudioInglesFueraDelReleaseTest`) vigila la configuración, el interruptor (`AUDIO_INGLES` falso en release, verdadero en debug) y que los clips sigan en el repo. **Cómo se comprueba el APK:** desde un test de JVM no se puede abrir un APK de release, así que la comprobación es la tarea de Gradle (corre en cada empaquetado) más un `unzip -l` al hacer el informe (0 entradas `assets/audio/EN/` en release, 4133 en debug).
  - **Entradas inglesas de `indice.json`: se dejan.** Son ~170 KB comprimidos (0,3 % del APK); el parser ya las descarta al cargar con `AUDIO_INGLES` apagado, así que no hay un botón que apunte a un archivo que no está. Filtrarlas al empaquetar exigiría partir el índice en dos archivos (`indice.json` + uno solo-debug), o sea tocar el generador de Overview (`indice_audio.py`): **propuesta para Overview**, no urgente.
  - **Para revertirlo, un solo lugar:** en `app/build.gradle.kts`, el bloque `androidComponents` (con su comentario), la tarea `verificarReleaseSinAudioIngles` y su `dependsOn`, más `AUDIO_INGLES = "true"` en `release`. Cuando se regeneren los clips con una voz de corpus limpio, también hay que sumar la voz inglesa a `ACERCA_CREDITOS_TEXTO`.
- **Tarea 2 — bienvenida** (`ui/BienvenidaScreen.kt`, `MainActivity.kt`, `AjustesScreen.kt`, `datos/RepositorioAjustes.kt`): una pantalla que **ocupa el lugar de toda la app** hasta que se completa. (1) **Idioma de la app, sin nada marcado de antemano** (los 6, cada uno en su idioma, más «Idioma del sistema»; solo si se toca esa se lee el idioma del dispositivo: regla dura 2 respetada sin enmienda). Hasta que se elige **no se ve nada más ni hay botón para seguir**; el rótulo previo es el mismo en los 6 idiomas («Idioma · Language · Sprache · Langue · Lingua · Idioma») porque la app aún no sabe en cuál hablar. La elección **se aplica de inmediato** (la pantalla ya sigue en ese idioma) y se guarda en `idiomaInterfaz` e `idiomaBase` como en Ajustes. (2) Qué idioma se practica y en qué nivel: **el mismo selector de Ajustes** (`SelectorIdiomasAprendidos`, ahora `internal`); hay que dejar al menos uno (si no, el botón se desactiva y un aviso lo dice). (3) **Qué es esto y qué no es**, a la vista y en cuerpo de texto (`bodyLarge`), no un titular. Botón «Empezar».
  - **Dónde vive `bienvenidaCompletada`:** campo nuevo de `Ajustes`, guardado en las mismas `SharedPreferences("ajustes")` que el resto de los ajustes (clave `bienvenida_completada`). Es un ajuste, no progreso (regla dura 4). **No viaja en la copia de seguridad ni en Anki** (test: el JSON del respaldo y el TSV no contienen «bienvenida» ni ningún otro ajuste). Valor por omisión del **modelo**: `true` (para que `Ajustes()` en pantallas y tests no abra la bienvenida); lo que decide el primer arranque es el **repositorio**: sin la clave guardada devuelve `false`.
  - **Quien ya tenía la app (testers de versiones anteriores) la ve una vez**, porque su `SharedPreferences` no tiene la clave. Es lo buscado: reciben el mensaje nuevo.
  - **Volver a verla:** fila «Ver la bienvenida otra vez» en Ajustes, justo bajo «Acerca de». En esa repetición **no se fuerza a elegir de nuevo el idioma** (ya se eligió): se ve todo de entrada. Por ahora solo rehace la bienvenida (los tours no existen).
  - **«Qué es esto» y «Acerca de»:** **una sola clave** (`ACERCA_QUE_ES_TEXTO`) para los dos lugares. Antes «Acerca de» tenía un párrafo distinto («ofrece cada semana una ficha de práctica de alemán o de inglés, entre A2 y C2… No es un curso»). **Dejé el texto de la bienvenida** y reemplacé el párrafo viejo: dice lo mismo en menos palabras y deja de haber dos versiones que se desfasan. Lo que se pierde (que hay fichas semanales A2–C2 en alemán e inglés, y que hay un texto para que una IA corrija) sigue en la guía de testers.
  - **Tuteo:** el texto del prompt venía en voseo («hacé»); está en tuteo en los 6 idiomas (P9): «haz un curso de ese nivel», «take a course», «besuche einen Kurs», «suis un cours», «segui un corso», «faça um curso».
- **Tarea 3 — versión 0.3.0 (versionCode 12)**; la guía lleva la versión y una fila nueva («Pantalla de bienvenida»); **no creció** (4248 → 4246 caracteres): la fila de Verbos se acortó para compensar. El `.pdf` lo regenera Overview.

**Textos nuevos** (los 6 idiomas están en `TextosInterfaz.kt`): título «Te damos la bienvenida / Welcome / Willkommen / Bienvenue / Ti diamo il benvenuto / Boas-vindas»; botón «Empezar / Start / Los geht's / Commencer / Inizia / Começar»; aviso «Elige al menos un idioma para practicar»; fila de Ajustes «Ver la bienvenida otra vez». **Texto de «qué es esto», en los 6 idiomas:**
- ES: LenguApp no enseña un idioma: te hace practicar el que ya estás aprendiendo. Cada semana te propone un tema, una habilidad y un desafío para producir, no para estudiar. Si estás empezando un nivel, haz un curso de ese nivel; esta app es para que lo que aprendes ahí no se te quede en el cuaderno.
- EN: LenguApp doesn't teach you a language: it makes you practise the one you're already learning. Every week it gives you a topic, a skill and a challenge to produce language, not to study it. If you're just starting a level, take a course at that level; this app is there so that what you learn in it doesn't stay in your notebook.
- DE: LenguApp bringt dir keine Sprache bei: Es lässt dich die Sprache üben, die du schon lernst. Jede Woche schlägt es dir ein Thema, eine Fähigkeit und eine Herausforderung vor, bei der du Sprache selbst hervorbringst, statt zu lernen. Wenn du ein Niveau gerade erst beginnst, besuche einen Kurs auf diesem Niveau; diese App sorgt dafür, dass das, was du dort lernst, nicht im Heft stehen bleibt.
- FR: LenguApp n'enseigne pas une langue : elle te fait pratiquer celle que tu apprends déjà. Chaque semaine, elle te propose un thème, une compétence et un défi pour produire, pas pour étudier. Si tu débutes un niveau, suis un cours de ce niveau ; cette appli sert à ce que ce que tu y apprends ne reste pas dans ton cahier.
- IT: LenguApp non insegna una lingua: ti fa praticare quella che stai già imparando. Ogni settimana ti propone un tema, un'abilità e una sfida per produrre, non per studiare. Se stai iniziando un livello, segui un corso di quel livello; questa app serve a fare in modo che quello che impari lì non resti sul quaderno.
- PT: O LenguApp não ensina um idioma: ele faz você praticar o que já está aprendendo. Toda semana propõe um tema, uma habilidade e um desafio para produzir, não para estudar. Se você está começando um nível, faça um curso desse nível; este app existe para que o que você aprende lá não fique no caderno.

**Tamaños de APK** (empaquetado limpio): **debug 63 153 420 B** (**+16 384 B** sobre los 63 137 036 B de la Ronda M; trae el audio inglés encendido) y **release (sin firmar) 37 731 702 B**. **El release baja unos 17,6 MB, no los ~26 MB del prompt:** los 4133 clips ingleses ocupan 17 643 412 B dentro del APK (están guardados sin comprimir; los «26 MB» salen de `du` sobre el disco, que redondea por bloques). No existe una medición «antes» del release (nunca se había armado uno), así que ese ahorro es el tamaño real de la carpeta excluida. **Tests:** 1185 en debug y 319 en release, 0 fallas.

**Elecciones donde el prompt no cerraba:** (1) la exclusión por variante en vez de mover los archivos, por las herramientas de Overview (arriba); (2) las entradas inglesas del índice se dejan; (3) el idioma de la repetición desde Ajustes no se vuelve a forzar; (4) al menos un idioma para practicar es obligatorio; (5) el modelo `Ajustes` trae `bienvenidaCompletada = true` por omisión y es el repositorio el que arranca en `false`; (6) la bienvenida reemplaza el párrafo anterior de «Acerca de».

**Para mirar en el teléfono:** la bienvenida con la letra al 160 % (el bloque entero es un scroll) y en horizontal; y que al elegir «Idioma del sistema» la pantalla pase al idioma del dispositivo.

## 52. Verificación de Code Ronda N y qué falta para publicar (12-oct-2026)

**Verificación.** Sobre `origin/main` (HEAD `156321e6`, 0.3.0 / versionCode 12): compositor 606 / 56 / 0, R1–R11 en 0, assets de contenido intactos, `indice_audio.py --comprobar` al día, manifest sin `INTERNET`. **Guía de testers: `.pdf` regenerado por Overview** (0.3.0, una hoja).

**La decisión de no mover los archivos fue la correcta**, y por un motivo que Overview no había visto: `indice_audio.py`, `sincronizar_audio_estado.py` y `exportar_audio_maestro.py` leen `app/src/main/assets/audio/{DE,EN}`, así que moverlos al `sourceSet` de debug las habría roto **en silencio**. La exclusión por variante más la tarea `verificarReleaseSinAudioIngles`, que mira los **assets ya fusionados** y hace fallar el empaquetado, es más segura que mover: no depende de que nadie toque la configuración. Probada quitando la exclusión.

**Corrección de Overview a su propia estimación:** el release ahorra **17,6 MB**, no 26. Los 26 salían de `du` sobre el disco, que redondea por bloques; dentro del APK los clips ocupan 17 643 412 B (van sin comprimir, que es lo que permite `openFd`). Release sin firmar: **37 731 702 B**.

**Textos de la tienda corregidos por Overview.** Las 6 descripciones prometían «vocabulario y expresiones **con audio**» sin más, y el audio inglés no se publica: ahora dicen «con audio en alemán». Era publicidad engañosa esperando a pasar.

### Qué falta para publicar (estado al 12-oct-2026)

**Bloqueantes:**
1. **Revisión por hablante nativo de fr, it y pt** (interfaz, tienda, clases de verbo, estados vacíos, el texto de la bienvenida). **Recomendación de Overview: publicar primero solo con es, en y de** — los tres que Fer puede juzgar él mismo — y sumar fr/it/pt cuando haya quien los revise. Desbloquea la publicación sin bajar la calidad y no tira nada: los textos quedan en el repo.
2. **Firma de la app**: no hay `signingConfig`. Hay que crear el almacén de claves, guardarlo **fuera del repo** y decidir si se usa la firma gestionada por Google.
3. **Ícono y pantalla de carga**: Fer los hará cuando tenga un diseño que le guste.
4. **Capturas de pantalla** para la ficha de la tienda (4 a 6, en el idioma de cada ficha).

**No bloqueantes, anotados:** regenerar el audio inglés con `en_GB-alba` (CC BY 4.0) y revertir entonces el interruptor y la exclusión; los tours del tutorial (`docs/tutorial-guion.md`); los clips lentos; las deudas de contenido de la sección 47.

**Formulario de seguridad de los datos (Play):** la app no recoge ni comparte nada; todo en «no», apuntando a `legal/PRIVACY.md`.

## 53. Guía de testers en los 6 idiomas (Overview, 12-oct-2026)

Verificado el parche anterior sobre `origin/main` (HEAD `d6d2435d`): las 6 descripciones de la tienda ya dicen «con audio en alemán», `FALTANTES` 52 está, y el `LEEME.md` de publicación lleva la firma de la app, el formulario de seguridad de los datos y el aviso del audio inglés.

**Guía traducida a los 6 idiomas de la app**, una por idioma, **dos carillas cada una** (una hoja a doble faz):

| Archivo | PDF |
|---|---|
| `docs/guia-testers.md` (español, el canónico) | `guia-testers-lenguapp.pdf` y `-es.pdf` |
| `docs/guia-testers-en.md` | `guia-testers-lenguapp-en.pdf` |
| `docs/guia-testers-de.md` | `guia-testers-lenguapp-de.pdf` |
| `docs/guia-testers-fr.md` | `guia-testers-lenguapp-fr.pdf` |
| `docs/guia-testers-it.md` | `guia-testers-lenguapp-it.pdf` |
| `docs/guia-testers-pt.md` | `guia-testers-lenguapp-pt.pdf` |

**El español sigue siendo el original y el canónico:** es el que lee el test `TextosVerbosTest` (que vigila que la fila de Verbos no vuelva a mentir), y es el que hay que editar primero. **Al cambiar la guía hay que retraducir las otras cinco**, o quedan desfasadas sin que nada avise. Los PDF se generan con márgenes de 10 mm; con 12 mm el alemán se iba a tres carillas.

**Alemán y francés están resumidos respecto del español** (son idiomas más largos): se acortaron las filas de Verbos, del desafío, de temas y tipografías, y de Guardados, sin perder ninguna información. El contenido es el mismo en las seis.

**Sin revisión de nativo: fr, it y pt**, igual que el resto de los textos de esos idiomas. El inglés y el alemán los puede juzgar Fer. Es la misma deuda de la sección 52, ahora también en la guía.

## 54. Velocidad del audio — la decisión de la sección 50 se revierte (12-oct-2026)

Fer probó la 0.3.0 y pidió escuchar más lento, **entre el 60 % y el 100 %**.

**La sección 50 decía que no se hacía, y estaba mal razonada.** El argumento era el costo: generar ~8143 clips lentos (~51 MB) sobre un APK de 63 MB. Eso vale para clips **pregrabados**, que es como lo planteó el manifiesto original, pero **no hace falta ninguno**: `MediaPlayer` cambia la velocidad en el momento con `setPlaybackParams(PlaybackParams().setSpeed(x))`, disponible desde **API 23**, y el `minSdk` del proyecto es **26**. Cero archivos nuevos, cero MB, sin tocar el índice ni el contenido. Ni Overview ni Code lo miraron en su momento: se discutió «generar clips» en vez de «cambiar la velocidad».

**Ronda O (prompt 30):** velocidad variable en `ReproductorAssets` (con `PlaybackParams` aplicado **dentro de `setOnPreparedListener`**, porque antes de preparar lanza `IllegalStateException`), **cuatro pasos fijos — 60 %, 75 %, 90 % y 100 %**, por omisión 100 %, elegidos en **Ajustes** y guardados como un ajuste más (regla dura 4; no viaja en la copia de seguridad). Si el dispositivo rechaza la velocidad, se reproduce al 100 % en vez de callar. Versión **0.3.1**.

**A confirmar en el informe de Code:** que el tono se conserve al bajar la velocidad, y **si al 60 % la voz sintética sigue siendo entendible** — si no, el paso más lento sube a 70 %.

**Nota de proceso para la guía de testers:** ahora son seis archivos (sección 53). Code actualiza **solo el español**, que es el canónico y el que lee el test; **Overview retraduce los otros cinco y regenera los seis `.pdf`**. Si esto se olvida una vez, las cinco traducciones quedan desfasadas sin que nada avise.

## 55. Code Ronda O — ✅ implementada (Code, 12-oct-2026), versión 0.3.1

Tres commits, uno por tarea. Contenido, esquema, assets e índice de audio sin tocar; los dos guardas de `preBuild` en verde; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes; el release sigue sin `assets/audio/EN/` (la guarda de la Ronda N pasa).

- **Tarea 1 — velocidad variable** (`audio/ReproductorAssets.kt`, `audio/ControlAudio.kt`): `ReproductorAudio.reproducir(ruta, velocidad, alTerminar)`; la velocidad se aplica con `PlaybackParams().setSpeed(x).setPitch(1.0f)` **dentro de `setOnPreparedListener`, antes de `start()`**. Ahí y no en `setDataSource` porque antes de preparar no cambia nada (el reproductor todavía no tiene decodificador) y en un estado inválido lanza `IllegalStateException`; una vez preparado es el primer momento en que es válido y todavía no se oyó nada. (Como documenta Android, un `setPlaybackParams` con velocidad distinta de cero sobre un reproductor ya preparado equivale a `start()`; el `start()` posterior es inocuo. No hay pausa en la app, así que la trampa del reproductor pausado no aplica; queda anotada en el código.) La lógica vive en `aplicarVelocidadSegura(velocidad, aplicar)`: a 100 % no toca nada. **Si el dispositivo la rechaza** (`IllegalArgumentException`, `IllegalStateException`, `UnsupportedOperationException`, cualquier excepción) **suena a velocidad normal**, con una línea en el log de depuración y sin cartel; testeado con las tres excepciones. `ControlAudio.alternar(ruta, velocidad)` la recibe en cada toque y **no la guarda**.
- **Tarea 2 — dónde se elige** (`modelo/Ajustes.kt`, `datos/RepositorioAjustes.kt`, `ui/AjustesScreen.kt`, `ui/BotonAudio.kt`): `velocidadAudio` es **un ajuste más** (enum `VelocidadAudio`: 60, 75, 90 y 100 %, por omisión 100 %), guardado en las mismas `SharedPreferences` (clave `velocidad_audio`; un valor desconocido o ausente vuelve a 100 %). **No viaja en la copia de seguridad ni en Anki** (test). Fila nueva en Ajustes **después de la tipografía**: «Velocidad del audio» con cuatro chips fijos y una línea de ayuda («Para escuchar las palabras y las frases más despacio»), rótulo, ayuda y valores en los 6 idiomas (el «%» sigue la costumbre de cada idioma: «75 %» con espacio duro en ES/DE/FR, «75%» en EN/IT/PT).
  - **Cómo se ve que no está al 100 %:** el botón de altavoz muestra un **numerito discreto** (9 sp, color de apoyo, esquina inferior derecha del icono: «75») y su descripción de TalkBack pasa a «Escuchar (75 %)». Al 100 % no se dibuja nada extra. Es un matiz, no un cartel: no cambia el tamaño del botón ni de la fila. **No pude verlo en un teléfono**; si ensucia, es una sola `if` en `BotonAudio.kt`.
- **Tarea 3 — versión 0.3.1 (versionCode 13)**; en la guía de testers **en español** cambió **solo la fila del audio**: antes «El audio suena, por ahora solo en alemán · Toca el altavoz junto a una palabra o expresión (en los ejemplos, solo algunos): es voz sintética. Suena una a la vez. En inglés todavía no hay.»; ahora «El audio suena, solo en alemán · Toca el altavoz junto a una palabra o expresión (en los ejemplos, solo algunos): voz sintética. **En Ajustes eliges la velocidad (60–100 %).** En inglés aún no.» (más la versión en el título). Quedó 2 caracteres más larga en total (4246 → 4248): irrelevante para la hoja, pero se dice. **Las otras cinco guías y los seis `.pdf` los retraduce/regenera Overview**; no los toqué.

**Tamaños de APK** (empaquetado limpio): **debug 63 153 420 B**, **idéntico a la Ronda N (delta 0)**: es código, no archivos; **release 37 731 698 B** (−4 B). **Tests:** 1198 en debug y 328 en release, 0 fallas.

**Lo que NO pude comprobar (no hay dispositivo ni oído):**
- **Que el tono se conserve.** El código pide `pitch = 1,0` explícito y `PlaybackParams` en `MediaPlayer` usa el estirado temporal del propio Android (lo mismo que ExoPlayer y los reproductores de podcast al 0,75×), que por diseño mantiene el tono. Pero no lo escuché: es lo primero que hay que verificar en el teléfono de Fer.
- **Si al 60 % la voz sintética sigue siendo entendible.** No puedo juzgar audio desde acá. Lo que se sabe: el estirado temporal digital suele ser limpio hasta ~0,75× y a 0,6× empieza a notarse un timbre «metálico» o con artefactos en consonantes, y las voces Piper ya son un poco planas; es un riesgo real, no una certeza. **Prueba concreta para Fer:** escuchar una frase larga alemana (p. ej. un ejemplo de B2) a 60 % y a 75 %. Si el 60 % cuesta, el paso más lento sube a 70 %: es **un solo número** (`VelocidadAudio.LENTA(60)` → 70) más el texto de ayuda si lo menciona.

**Decisiones donde el prompt no cerraba:** (1) la velocidad llega al botón por un `CompositionLocal` (`LocalVelocidadAudio`) provisto desde `LenguAppApp` con el ajuste vigente, en vez de pasarla por todas las pantallas; (2) el aviso es un numerito junto al icono y en la descripción, no un color ni un icono distinto (que ya se usa para «Detener»); (3) los chips de la fila de Ajustes llevan el porcentaje con la ortografía de cada idioma; (4) se aplica `setPitch(1.0f)` explícito, aunque es el valor por omisión, para que quede escrito qué se espera.

## 57. Feedback de uso de la 0.3.1 (Fer, 13-oct-2026) — Ronda P

- **«Acerca de» al final de Ajustes**, con «Ver la bienvenida otra vez» justo encima. Lo que se usa seguido arriba; lo que se consulta una vez, al fondo.
- **Nada de deslizar a lo ancho en Ajustes.** Los seis grupos de opciones usan `horizontalScroll` y las opciones que quedan fuera de pantalla no las elige nadie. Pasan a `FlowRow` (el que ya existe en `FiltroChips.kt`), repartidos en las filas que hagan falta. **Lo mismo en la pantalla de bienvenida**, donde es más grave todavía: es lo primero que ve alguien que abre la app y no puede adivinar que hay más idiomas a la derecha. La Biblioteca **no se toca**: ahí el desplazamiento es deliberado.
- **Quitar la línea «LenguApp · Para fijar lo que ya aprendiste en tu curso»** de Ajustes. Lo dicen la bienvenida y «Acerca de». Hay que actualizar `publicacion/LEEME.md`, que la nombra como uno de los tres lugares donde se aclara que no es un curso: ahora son la tienda, la bienvenida y «Acerca de».

Versión **0.3.2** (prompt 31).

## 58. El tutorial con foco se arranca ahora (Fer, 13-oct-2026) — Ronda Q

Fer decide **empezar los tours con foco** sin esperar más. Overview había recomendado esperar a que la interfaz se asentara; el riesgo sigue siendo real, así que se compensa con dos cosas, ambas obligatorias en el prompt 32: **anclar por `testTag` y no por coordenadas**, y **un test que recorra todos los pasos y falle si alguna ancla no existe**. Sin ese test el tutorial se pudre en silencio la primera vez que alguien mueva un botón.

**Orden: la Ronda P va primero.** Mueve «Acerca de» y cambia los grupos de opciones a `FlowRow`, que es justo lo que el tour de Ajustes tiene que señalar.

**Decisiones que cierran los puntos abiertos del guion** (`docs/tutorial-guion.md`):
- **Abandonar un tour cuenta como visto.** Nadie quiere que le vuelva a saltar lo mismo al día siguiente.
- **Secciones plegadas de la ficha:** el paso las abre y las deja como estaban al terminar; si resulta frágil, se explica el encabezado.
- **Primer arranque sin ficha:** el tour de la ficha no se lanza y **no se da por visto**; espera a la primera semana con contenido.
- **El mensaje «esto no enseña un idioma» cierra el tour de la ficha** (pedido de Fer), **y además se queda en la bienvenida y en «Acerca de»**. Es la única redundancia deliberada de la app: quien saltea el tour tiene que haberlo leído igual.

Versión **0.4.0**: el tutorial es una función nueva, no un retoque.

## 59. Code Ronda P — ✅ implementada (Code, 13-oct-2026), versión 0.3.2

Cuatro commits, uno por tarea, sobre la Ronda O. Contenido, esquema y assets sin tocar; los dos guardas de `preBuild` en verde; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes; el release sigue sin `assets/audio/EN/`.

- **Tarea 1 — orden de Ajustes** (`ui/AjustesScreen.kt`): los ajustes quedan arriba (idiomas que se practican, idioma de la app, tema, modo, tipografía, velocidad del audio) y **al final**, en este orden: el nombre de la app, **«Ver la bienvenida otra vez»** y, **última fila de la pantalla, «Acerca de»**. Test de orden vertical (`AjustesOrdenTest`: título < idiomas que se practican < idioma de la app < tipografía < velocidad < nombre < bienvenida < Acerca de).
- **Tarea 2 — sin deslizar a lo ancho** (`AjustesScreen.kt`, `BienvenidaScreen.kt`): los **6 grupos de Ajustes** (idiomas que se practican, idioma de la app, tema, modo, tipografía, velocidad) y los **chips de idioma de la bienvenida** pasan de `Row` + `horizontalScroll` a **`FlowRow`** (`horizontalArrangement` y `verticalArrangement` de 8 dp, sin número fijo de columnas). El selector de nivel de cada idioma es un menú desplegable, no una fila: no tenía scroll. **La Biblioteca no se tocó.** Ninguno de los dos archivos contiene ya `horizontalScroll` (un test lo vigila sobre el código fuente, junto con que usen `FlowRow`).
  - **Tests** (`SinDeslizarALoAnchoTest`, 8 casos): a **320 dp con la letra al 100 % y al 160 %**, **en los 6 idiomas**, para Ajustes y para la bienvenida (con y sin idioma ya elegido): cada grupo tiene **todas** sus opciones (se cuentan) y **ninguna queda fuera de la pantalla** (`left ≥ 0` y `right ≤ ancho`), y **ningún nodo se desplaza en horizontal**. Comprobé que los tests **fallan con el código anterior** (6 de 6 fallan: «hay 6 nodo(s) que se desplazan en horizontal»).
  - **Limitación honesta del entorno:** Robolectric mide el texto con una fuente sustituta **muy angosta** (un chip de «Español» mide ~40 dp, en un teléfono real unos 100), así que a 320 dp casi todo entra en una fila aunque el código fuera malo. Por eso **el conteo de filas por grupo que pidió el prompt no se puede medir fielmente acá** (Robolectric da 3/2/3/1/1/1 filas en los 6 idiomas y a 100 % y 160 %, sin diferencia entre escalas: no es un dato de un teléfono). Para que la prueba de «nada fuera de pantalla» apriete de verdad, se repite además en una pantalla de **140 dp**, donde cada grupo tiene que partirse en varias filas. **Estimación para un teléfono real a 320 dp** (ancho útil 288 dp, chips de ~90–150 dp con el 100 % de letra): idiomas que se practican ~3–4 filas, idioma de la app ~3–4, tema ~3, modo ~2, tipografía ~2, velocidad 1; con el 160 % cada grupo gana una o dos filas. **Lo que hay que mirar en el teléfono:** que los chips más largos (en alemán y portugués, y el de tema con sus tres puntos de color) se partan en dos líneas dentro del chip y no se corten.
  - Se actualizaron los 4 tests de `AjustesScreenTest` que se desplazaban hasta un chip a través de la fila con scroll propio (`filaConChip`): ahora el chip está directo en el scroll vertical.
- **Tarea 3 — sin la línea fija** (`AjustesScreen.kt`, `proyecto/publicacion/LEEME.md`): se borró de Ajustes «Para fijar lo que ya aprendiste en tu curso»; el **nombre de la app queda arriba de las dos filas finales**. **La clave `LINEA_FIJA` NO queda huérfana:** `AcercaDeScreen` la sigue mostrando (y los tests de «Acerca de» en los 6 idiomas la usan), así que **se conserva** con su función `textoLineaFija`; solo se quitó su uso en Ajustes. El LEEME de publicación ya no cuenta Ajustes entre los lugares donde se dice que no es un curso: son **la tienda, la bienvenida y «Acerca de»** (con una nota de que hasta la 0.3.1 estaba también en Ajustes).
- **Tarea 4 — versión 0.3.2 (versionCode 14).**

**Guía de testers en español — filas cambiadas** (las otras cinco y los seis `.pdf`, a cargo de Overview): (1) el título: «versión 0.3.1» → «versión 0.3.2»; (2) la fila **«El nombre de la app y «Acerca de» están en Ajustes»** pasa a **««Acerca de» está al final de Ajustes»** (la explicación «La pantalla principal queda libre para el contenido.» no cambia). Ninguna otra fila hablaba de la línea fija; la fila de la bienvenida («…Se repite desde Ajustes.») sigue siendo cierta. La guía quedó 14 caracteres más corta (4248 → 4234).

**Tamaño del APK** (empaquetado limpio): **debug 63 153 420 B, idéntico a la Ronda O (delta 0)** y **release 37 731 698 B** (igual que antes). **Tests:** 1209 en debug y 328 en release, 0 fallas.

**Decisiones donde el prompt no cerraba:** (1) la fila del conteo de filas es una **estimación** y no una medición, por la limitación de Robolectric (arriba); (2) `LINEA_FIJA` se conserva porque «Acerca de» la usa; (3) los chips de «Idioma del sistema» y de cada idioma de la app siguen dentro del mismo `FlowRow` (un solo grupo, 7 chips); (4) cada unidad «chip de idioma + botón de nivel» de «idiomas que se practican» es un solo elemento del `FlowRow`: el botón de nivel no se separa del chip al partir la fila.

## 60. Code Ronda Q — ✅ implementada (Code, 13-oct-2026), versión 0.4.0

Cuatro commits (las tareas 2 y 3 salieron juntas, ver abajo), sobre la Ronda P. Contenido, esquema y assets sin tocar; los dos guardas de `preBuild` en verde; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes; el release sigue sin `assets/audio/EN/`. **Nada de esto se vio en un teléfono**: el recuadro, las tarjetas y el scroll hasta cada ancla están probados con Robolectric (estructura, orden, textos, que las anclas existan), no a ojo.

- **Tarea 1 — el mecanismo** (`ui/Tutorial.kt`, `ui/TutorialOverlay.kt`): `GestorTours` (qué tour está en marcha y en qué paso; el estado persistente se lo inyecta `LenguAppApp`), `RegistroAnclas` y el modificador **`anclaTour(id)`**: pone `testTag(id)`, registra los **límites reales** del nodo (`onGloballyPositioned`, se actualizan al desplazarse) y un `BringIntoViewRequester` para traerlo a la vista si está dentro de un scroll. El recuadro (`TutorialOverlay`) oscurece **toda** la pantalla (negro al 66 %) menos un agujero redondeado con borde blanco sobre el ancla, y una **tarjeta en la mitad opuesta** al foco (con scroll propio si no entra, máx. 55 % de la pantalla): «Paso n de total», título, cuerpo, «Saltar» y «Siguiente» (en el último, «Listo»). **Se come los toques** de lo de abajo; el botón **atrás abandona** el tour. **Degrada sin romperse:** un paso cuya ancla no aparece en 600 ms **se saltea y el tour sigue**; mientras espera **no se dibuja nada** (nunca una pantalla oscura sin nada señalado). **Sin animaciones propias**, así que «Quitar animaciones» no hace falta atenderlo; el escrim es translúcido y la tarjeta usa `surface`/`onSurface` del tema (ya AA en los 4 temas, claro y oscuro). Botones de **48 dp** con `contentDescription` en 6 idiomas y `paneTitle` para TalkBack.
- **Tarea 2 — los tours y cuándo se lanzan** (`ui/TutorialPasos.kt`, `MainActivity.kt` y cada pantalla): **22 pasos**: ficha 8, Biblioteca 5, Guardados 3, Ajustes 4, desafío 2 (**ninguno pasa de 8**; la ficha llega justo al tope). Ficha: sola, tras la bienvenida, **solo desde la pantalla principal** y solo si hay ficha. Biblioteca, Guardados y Ajustes: **la primera vez que se entra**. Desafío: la primera vez que **el interruptor está visible** (fin de semana), **después** del tour de la ficha y **nunca encadenado en la misma visita** (un tour por pantalla y visita). **«Saltar»** deja **todos** los tours como vistos y marca «salteado». **Abandonar con atrás cuenta como visto** (decisión del prompt). **Repetir:** la fila de Ajustes pasa a **«Ver la bienvenida y el tutorial otra vez»** (6 idiomas) y reinicia bienvenida, tours vistos y «salteado». **Estado:** `Ajustes.toursVistos` (conjunto de `Tour`) y `Ajustes.toursSalteados`, en las mismas `SharedPreferences` (`tours_vistos`, `tours_salteados`); **no viajan en la copia de seguridad ni en Anki** (test). Como con `bienvenidaCompletada`: el modelo trae «todos vistos» por omisión (pantallas y tests sin tours) y el repositorio arranca **sin ninguno** en una instalación nueva; quien ya tenía la app ve los tours una vez.
- **Tarea 3 — las dos decisiones de la ficha:** (1) **secciones plegadas: el paso explica el ENCABEZADO, no abre la lista.** Abrirla exigía controlar el estado de `SeccionPlegable` desde fuera, traer la lista a la vista y señalar una caja de decenas de filas (el foco habría tapado media pantalla) para decir lo que ya dice el encabezado; el ancla es el encabezado (`anclaEncabezado`) y el texto empieza con «Toca el encabezado para abrir la lista». (2) **Sin ficha:** el tour de la ficha vive dentro de la composición de la ficha, así que cuando no hay contenido para el par (idioma, nivel) **no se lanza ni se da por visto**: queda pendiente para la primera vez que haya ficha (test con una semana de 2035).
- **Tarea 4 — el cierre del tour de la ficha:** el 8.º paso («Biblioteca, Guardados y Ajustes», con el foco en la barra de arriba) lleva, además de la línea de los tres botones, **`ACERCA_QUE_ES_TEXTO` tal cual** (clave `cuerpoExtra`). El texto **sigue** en la bienvenida y en «Acerca de» (misma clave, misma función).
- **Tarea 5 — versión 0.4.0 (versionCode 15).**

**Commits:** las tareas 2 y 3 son **un solo commit** (`e63efc84`): las dos decisiones de la 3 (ancla en el encabezado del vocabulario y «sin ficha») son parte del mecanismo de lanzamiento de la 2 y no se podían separar sin dejar el catálogo apuntando a un ancla inexistente.

**`testTag` / anclas agregadas:** `tour-ficha-titulo`, `tour-ficha-mision`, `tour-ficha-descripcion`, `tour-ficha-errores`, `tour-ficha-vocabulario` (encabezado de la sección plegable), `tour-ficha-autochequeo`, `tour-ficha-prompt`, `tour-barra-superior` (la fila de Biblioteca/Guardados/Ajustes), `tour-biblioteca-vistas`, `tour-biblioteca-filtros`, `tour-biblioteca-buscar`, `tour-biblioteca-estrella` (la estrella de la **primera** fila, envuelta en un `Box`: la estrella conserva su `testTag` de siempre), `tour-guardados-lista`, `tour-guardados-archivo`, `tour-ajustes-idiomas`, `tour-ajustes-tema`, `tour-desafio-consigna` y `tour-desafio-interruptor`. Las filas «Acerca de» y «Ver la bienvenida otra vez» de Ajustes **reusan** sus tags de siempre (`acerca-de`, `ver-bienvenida`), que pasaron a ser anclas. Para agrupar sin cambiar el aspecto, en Ajustes los dos selectores de idioma y los tres de tema/modo/tipografía van cada grupo en un `Column` (misma separación de 24 dp), y en Guardados todo lo de debajo de los botones de archivo va en un `Column` (misma separación de 16 dp).

**Cadenas nuevas:** **44 claves de pasos** (22 pasos × título y cuerpo) × 6 idiomas = **264**, más **7 claves del recuadro** (Siguiente, Saltar, Listo, «Paso n de total» y las tres descripciones de los botones) × 6 = **42**: **306 cadenas nuevas**, y un rótulo cambiado (el de «Ver la bienvenida…»). Español = el guion pasado a tuteo (P9); los otros cinco, traducidos por Code con los nombres que ya usa la app (Library/Saved/Settings, Bibliothek/Gespeichert/Einstellungen, Bibliothèque/Enregistrés/Paramètres, Biblioteca/Salvati/Impostazioni, Biblioteca/Salvos/Configurações; «Wochenaufgabe» para la misión). Pasos en portugués con «salvar» y no «guardar» (el test de registro lo exige). **Sin revisión de nativo** en fr / it / pt (ni en de / en: son de Code).

**Pasos que me parecen flojos, leídos contra lo que la app muestra** (el guion se escribió sin ver la app; si alguno hay que reescribirlo, lo hace Overview):
1. **Ficha, paso 3 («Para entender de qué va»)** nombra cuatro cosas (descripción, cuadro, ejemplos, notas) pero el foco cae **solo sobre la descripción**: el cuadro de referencia es plegable y puede no existir, y ejemplos y notas están lejos. Mejor un ancla que abarque el bloque, o partir el paso.
2. **Ficha, paso 4 («Lo que se compara y lo que falla»)** habla de contraste y errores, pero señala solo **Errores comunes**: el contraste está entre medio y falta en bastantes fichas.
3. **Ficha, paso 5** dice «Toca el encabezado… Con la estrella guardas…» pero la **estrella no se ve** (la lista está cerrada y, además, el recuadro bloquea los toques mientras dura el tour). Se lee bien como indicación para después, pero no hay nada que mostrar de la estrella.
4. **Biblioteca, pasos 1 y 4** señalan **el mismo selector de vistas**: el paso de Verbos no muestra la vista de Verbos (no se cambia de vista durante el tour); el cuerpo funciona, el foco repite.
5. **Guardados, pasos 2 y 3** (Exportar / Importar) señalan **el mismo grupo de botones**, no cada botón. Y en el **primer ingreso Guardados está vacío**: el paso 1 («Aquí está lo que marcaste. Se ve igual que en la Biblioteca») apunta al aviso de «no hay guardados». Sería mejor que el texto del paso lo prevea.
6. **Desafío, paso 1** («La consigna está arriba y las condiciones debajo») solo tiene sentido con el interruptor encendido: el tour lo enciende y lo deja apagado, pero un tester puede preguntarse por qué «se prendió solo».
7. **Cuenta de pasos:** «Paso n de total» usa el total del catálogo; si un paso se saltea por falta de ancla, el contador salta un número.

**Tamaño del APK** (empaquetado limpio): **debug 63 202 572 B** (**+49 152 B** sobre los 63 153 420 B de la Ronda P) y **release 37 764 466 B** (+32 768 B): son las ~306 cadenas y el código del recuadro. **Tests:** 1246 en debug y 338 en release, 0 fallas.

**Tests nuevos que sostienen la ronda:** `TutorialMecanismoTest` (11: ancla, orden, paso sin ancla, ninguna ancla, Saltar, atrás, una sola vez, toques bloqueados, 48 dp, textos), `TutorialPantallaTest` (16: **todas las anclas de los 5 tours existen** en la app real, lanzamiento, recorrido completo de la ficha, Saltar, atrás, sin ficha, Biblioteca una sola vez, desafío solo con interruptor visible y orden con la ficha, reinicio, respaldo), `TourCatalogoTest` (pasos por tour ≤ 8, textos en los 6 idiomas, el paso de cierre) y `ToursAjusteTest` (persistencia, instalación nueva, respaldo y Anki). **Un hallazgo del propio test de anclas:** en su primera versión fallaba para la Biblioteca porque `LenguAppApp` se había armado sin `contenidoTodos` (la Biblioteca salía vacía y no había primera fila): exactamente el tipo de ruptura que quiere atrapar.

**Guía de testers en español — filas cambiadas** (las otras cinco y los seis `.pdf`, a cargo de Overview): (1) el título: «versión 0.3.2» → «versión 0.4.0»; (2) la fila **«Pantalla de bienvenida»** pasa a **«Bienvenida y tutorial»** («La primera vez eliges idioma y nivel; cada sección muestra un recorrido que oscurece todo menos lo que explica. «Saltar» los saltea. Se repite desde Ajustes.»); (3) la fila **«Verbos en la Biblioteca»** se **acortó** para compensar (se quitó «Se filtra por clase, no por nivel» → «Se filtra por clase» y se condensó): no cambia de sentido. La guía quedó **3 caracteres más corta** (4234 → 4231).

**Decisiones donde el prompt no cerraba:** (1) plegadas: explicar el encabezado (arriba); (2) las tareas 2 y 3 en un commit; (3) un tour por pantalla y visita, el del desafío nunca encadenado al de la ficha; (4) los pasos del guion sin cuerpo propio para el cierre: el 8.º paso lleva la línea de los tres botones **más** `ACERCA_QUE_ES_TEXTO` (dos párrafos), con foco en la barra de arriba; (5) el título de un paso es la frase corta del guion (sin el punto final); (6) la tarjeta va en la mitad opuesta al foco, no flotando junto a él.

## 61. Verificación de Code Ronda Q, guías 0.4.0 y licencias de voces (Overview, 13-oct-2026)

**Qué está en `origin/main`:** los **cuatro commits de la Ronda Q** (`47713cd1`, `e63efc84`, `fc68c102`, `36351011`) y **nada mío**. El último commit de Overview es `decab5d3` (prompts 31 y 32, secciones 57 y 58). **El parche que mandé con las guías traducidas a 0.3.2, el criterio de elección de voces y la sección 60 nunca se aplicó** y su sección 60 chocaba con la que escribió Code: queda descartado y lo que valía de él se rehace acá, renumerado a 61. Es la **segunda vez** que pasa (la primera, la 0.3.1 — sección 53): mientras las guías traducidas y los `.pdf` los haga Overview y la guía en español la haga Code en el mismo commit que la versión, cada parche mío que se saltea obliga a acumular saltos de versión. Opción para cerrarlo de verdad, si se repite: que Code **no** toque ninguna guía y las seis salgan siempre de un solo parche mío.

**Verificación independiente de la Ronda Q (todo corrido sobre `36351011`, no sobre el informe):**

| Comprobación | Resultado |
|---|---|
| `componer.py` + `verificar_consistencia.py` | **606 fichas, 56 semanas especiales, 0 con problemas**; 240 fichas A2/B1, 0 problemas |
| `diff -rq build assets/contenido` | **0 diferencias** (la ronda no tocó contenido, como dice el informe) |
| `aplicar_verbos.py` | 0 packs con diferencias; 1229 apariciones etiquetadas, 1104 colocaciones con núcleo, 0 errores |
| `indice_audio.py --comprobar` | índice al día (los dos huérfanos de siempre) |
| `grep -c INTERNET` en el manifest | **1** = solo el comentario |
| `versionCode` / `versionName` | **15 / 0.4.0**, como dice el informe |
| Pasos del catálogo | **22**: ficha 8, Biblioteca 5, Guardados 3, Ajustes 4, desafío 2. Ninguno pasa de 8 |
| Cadenas nuevas | **51 claves `TOUR_*` × 6 idiomas = 306**. Coincide |
| Anclas repetidas | Confirmadas las dos que reporta: `tour-biblioteca-vistas` en los pasos 1 y 4, `tour-guardados-archivo` en los pasos 2 y 3 |
| El contador salta números | Confirmado leyendo `TutorialOverlay.kt`: `n = indice + 1` y `total = pasos(tour).size`, y un paso sin ancla llama `siguiente()`, que avanza el índice |

**Los siete pasos flojos: los siete tienen razón.** No encontré ninguno que fuera falsa alarma ni ninguno más. Reescritura y cambios de ancla en `prompts/33-code-ronda-r-pasos-del-tutorial.md` (Ronda R): texto nuevo para los tres pasos de la ficha (el foco manda sobre el texto, y ningún paso pide tocar algo mientras el recuadro se come los toques), ancla propia para la opción «Verbos» o fusión de los pasos 1 y 4 de la Biblioteca, fusión de los dos pasos de archivo de Guardados (quedan 2 pasos y el primero asume la lista vacía), inversión de los dos pasos del desafío —**el tour no enciende el interruptor**, que sería la app aceptando el desafío por el usuario— y la cuenta de pasos sobre los pasos mostrados en vez del índice del catálogo.

**Guías de testers:** las cinco traducidas estaban en **0.3.0** (se habían saltado 0.3.1 y 0.3.2), así que fueron de 0.3.0 a **0.4.0** de una vez, con las cuatro filas que cambiaron en esos tres saltos: «Acerca de» al final de Ajustes, «Bienvenida y tutorial», la fila de Verbos acortada y la velocidad del audio (60–100 %) en la de audio. Los **seis `.pdf` regenerados y los seis en dos carillas**. **En alemán hubo que acortar tres filas** (bienvenida, verbos e «Símbolo y pantalla de carga») porque el texto alemán es más largo y se iba a una tercera carilla: la versión alemana dice lo mismo con menos palabras, no menos cosas.

**Licencias de voces — hallazgo que no estaba en el radar.** El `MODEL_CARD` de `de_DE-thorsten-medium` dice textual «Finetuned from U.S. English lessac voice (medium quality)», y el de `fr_FR-siwis-medium` dice lo mismo: **casi todas las voces de Piper se afinaron sobre el modelo entrenado con el corpus Lessac/Blizzard 2013**, el mismo cuya licencia prohíbe redistribuir y limita el uso a investigación. El audio alemán que distribuimos **no contiene habla de Lessac** (es Thorsten, CC0) y el riesgo es mucho menor que el del inglés, pero si los pesos afinados cuentan como material derivado del corpus, no es cero. Detalle, matices y recomendación en `docs/licencias-voces.md`. **Pendiente de un minuto:** leer el `MODEL_CARD` de `thorsten/high`, que es la calidad que usa la app (existe, 281 B; desde este entorno no lo pude abrir).

**Voces para los cuatro idiomas que faltan (es, fr, it, pt):** quedó el inventario completo de voces y calidades en `docs/licencias-voces.md`, con el criterio de elección (redistribución permitida, sin NC, CC0 o CC BY sirven las dos, licencia leída en el `MODEL_CARD` de la voz y calidad exactas). **Solo una está verificada: `fr_FR-siwis`, CC BY 4.0** (corpus SIWIS, Edinburgh DataShare). Las demás, sin verificar: el proxy de este entorno solo deja leer páginas que aparecen en una búsqueda y los `MODEL_CARD` de esas voces no aparecen; hay que leerlos a mano. Pista: las voces `mls_*` vienen de Multilingual LibriSpeech, que es CC BY 4.0, pero son calidad `low`. **No está en el camino crítico** (no hay contenido en esos idiomas); lo que sí está es regenerar el inglés con `en_GB-alba`.

## 62. Feedback de uso del tutorial con foco (Fer, 13-oct-2026) — Ronda R

**Veredicto de Fer: el tour con foco está aprobado**, con cinco ajustes. El prompt 33 (reescritura de los siete pasos flojos) **queda reemplazado por el 34**: las partes estructurales pasaron ahí y las reescrituras de texto se posponen, porque Fer está editando los textos a mano.

1. **El foco queda pegado al borde de la pantalla** cuando el paso tiene que desplazar para mostrar su ancla. Es lo más molesto de los cinco. `traerALaVista` pide el rectángulo pelado del nodo; pasa a pedirlo **inflado 32 dp arriba y abajo**. Cuando el ancla es lo primero o lo último del contenido el margen no se puede cumplir, y eso Fer lo da por entendido: no se inventa relleno.
2. **El tour de la ficha zigzaguea.** Arranca arriba, salta a la misión (mitad de abajo), vuelve a la descripción (arriba) y después baja. Pasa a ir **en el orden de la pantalla, de arriba hacia abajo**. Una excepción deliberada: el interruptor de traducción está encima del título pero va en el paso 2, porque abrir el tutorial con el foco en un interruptor condicional no orienta a nadie. **El cierre se queda en la barra de arriba**: Fer lo aprobó explícitamente porque esa barra se ve siempre.
3. **Faltan dos anclas en la ficha:** el interruptor que traduce la ficha en A2 y B1, y el botón de la planilla del profesor. La ficha pasa a **10 pasos** y `MAX_PASOS_POR_TOUR` sube de 8 a 10 — **tope definitivo**: si alguna vez hace falta un paso 11, se parte el tour.
4. **Botón «Atrás» en la tarjeta**, por si se apretó «Siguiente» sin leer. Vuelve al **paso mostrado** anterior, no al índice anterior. Y el **botón atrás del sistema pasa a volver un paso** en vez de abandonar el tour (abandona solo en el primero): esto **revierte una decisión de la Ronda Q**, porque es incoherente que la flecha de la tarjeta y el gesto del sistema hagan cosas distintas.
5. **El último paso pierde el párrafo de «qué es esto y qué no es»**: ya está en la bienvenida y en «Acerca de». Esto deshace la tarea 4 de la Ronda Q y deja `cuerpoExtra` sin uso, así que el campo se borra.

**Los textos de los pasos pasan a `docs/tutorial-textos.md`:** los 22 títulos y cuerpos en español, identificados **por ancla y no por clave** (para que el renumerado de `TOUR_FICHA_n` no lo rompa), con el texto actual y mi propuesta de reescritura al lado. Fer edita el español y Overview traduce a los otros cinco idiomas de una sola vez, en una ronda posterior. Mientras eso pasa, **Code no reescribe textos**: mueve, fusiona y agrega pasos llevándose el texto tal cual, y los dos pasos nuevos y el fusionado de Guardados llevan texto **provisional**.

**Bonus: la nota «las traducciones de vocabulario todavía solo existen en español» es falsa y se borra.** Medido sobre los assets, no de memoria: de **8154** ítems de vocabulario, **8154 tienen glosa en es, fr, it y pt**; en alemán hay 4088 y en inglés 4066, y la diferencia no es un hueco —a una palabra alemana no se le pone glosa en alemán— porque 4088 + 4066 = 8154 exactos. La cobertura es total. Se borra el aviso, su clave, su función y el predicado `avisoGlosasSoloEnEspanol` (que era `idiomaAplicacion != Idioma.ES`), no se condiciona más fino: es una afirmación falsa, no una mal condicionada. **Y queda una comprobación pendiente que la nota tapaba:** con la app en alemán aprendiendo alemán no hay glosa que mostrar, así que hay que verificar que la ficha, la Biblioteca y Guardados degraden limpio (sin guión suelto ni paréntesis vacío).

## 63. Code Ronda R — ✅ implementada (Code, 13-oct-2026), versión 0.4.1

Ocho commits, uno por tarea (la 2 y la 3 juntas, ver abajo), sobre la Ronda Q. Contenido, esquema y assets sin tocar; los dos guardas de `preBuild` en verde; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes; el release sigue sin `assets/audio/EN/`. **Nada de esto se vio en un teléfono.**

- **Tarea 1 — margen al traer el ancla** (`ui/Tutorial.kt`): `traerALaVista(id, margenPx)` pide ahora el rectángulo del ancla **inflado 32 dp arriba y abajo** (`rectanguloConMargen`) en vez del nodo pelado. **Dejé 32 dp** (no cambié el número: el test lo mide a ≥ 32 dp de los dos bordes y no vi motivo para 24 ni 40, pero **no lo vi en pantalla**). El agujero del foco no cambió. Si el ancla ya está entera y despegada de los bordes no se desplaza nada (test: scroll en 0 antes y después); si es lo primero o lo último del contenido el contenedor hace lo que puede, sin relleno inventado. **Hallazgo de paso, un error de la Ronda Q:** las anclas registraban `boundsInRoot()`, que **recorta contra los ancestros** y devuelve un rectángulo **vacío** para un nodo que todavía está fuera de la pantalla dentro de un scroll, justo el caso en que hay que traerlo; ahora registran `positionInRoot()` + `size` (sin recortar). Sin eso el margen se habría inflado sobre un rectángulo de 0 × 0.
- **Tarea 4 — «Atrás»** (`Tutorial.kt`, `TutorialOverlay.kt`): la tarjeta pasa a **Atrás · Saltar · Siguiente/Listo** en un `FlowRow` (en una pantalla angosta los botones bajan de línea, no se achican; test a 140 dp). En el primer paso «Atrás» **no se muestra** (no se deshabilita). Vuelve al **paso mostrado** anterior con una **pila de índices mostrados**: un paso salteado por falta de ancla nunca entra en la pila, así que al retroceder tampoco se vuelve a mostrar. Claves `TOUR_ATRAS` (Atrás / Back / Zurück / Retour / Indietro / Voltar) y `TOUR_ATRAS_CD`; 48 dp. **El botón atrás del sistema vuelve un paso y solo abandona en el primero** (cuenta como visto): revierte la decisión de la Ronda Q; los tests que la fijaban se **cambiaron**, no se borraron.
- **Tarea 7 — «Paso n de total»** (`Tutorial.kt`): `n` = pasos efectivamente mostrados (`pila.size + 1`, baja al retroceder); `total` = catálogo − pasos ya salteados − pasos por delante cuya ancla **no está en pantalla ahora**. Un tour de 4 pasos con el 2.º sin ancla muestra «1 de 3», «2 de 3», «3 de 3» (test). **Efecto visible en la ficha:** en B2+ (sin el interruptor de traducir) y sin planilla el tour dice «de 8», no «de 10». Si ya no queda ningún paso con ancla por delante, el paso actual dice «Listo». `TOUR_PASO` no cambió.
- **Tarea 5 — sin el párrafo de «qué es esto»**: `cuerpoExtra` borrado del modelo (`PasoTour`), del recuadro y de los tests; el cierre queda con su línea sobre Biblioteca, Guardados y Ajustes. `ACERCA_QUE_ES_TEXTO` sigue en la bienvenida y en «Acerca de».
- **Tareas 2 y 3 — orden de la ficha y dos anclas nuevas** (un solo commit: el orden nuevo incluye los dos pasos nuevos): orden de arriba hacia abajo **titulo → traducir → descripción → errores → vocabulario → misión → autochequeo → prompt → planilla → cierre (barra de arriba)**. La excepción deliberada (el interruptor de traducir, encima del título, va en el paso 2) quedó como pidió el prompt. Anclas nuevas: `tour-ficha-traducir` (el interruptor, en la franja fija; solo A2 y B1) y `tour-ficha-planilla` (solo si hay `onPlanilla`). **`MAX_PASOS_POR_TOUR` = 10** (tope definitivo; la ficha lo usa entero). Claves `TOUR_FICHA_n` **renumeradas** a 1…10 (mecánico: 2→6, 6→7, 7→8, 8→10; los textos que se mueven se llevan su cadena tal cual).
- **Tarea 6 — las tres estructurales**: (a) **Biblioteca, paso 4** → `tour-biblioteca-vista-verbos`, un ancla sobre la **opción «Verbos»** del selector (`OpcionFiltro.ancla`, un modificador en el chip: **el selector no cambió de aspecto**); el paso 1 sigue con el selector entero. (b) **Guardados**: «Exportar» e «Importar» fusionados en **un paso** (`tour-guardados-archivo`); Guardados queda en **2 pasos** y la clave `TOUR_GUARDADOS_3` se borró. (c) **Desafío**: primero el interruptor, después la consigna, y **el tour ya no enciende el interruptor** (regla dura 13): con el interruptor apagado el paso de la consigna **se saltea solo** y el tour dice «1 de 1». Se borró la lógica de la Ronda Q que lo encendía y lo dejaba apagado.
- **Tarea 8 — la nota falsa**: borrados el `Text` de Ajustes, la clave `AVISO_GLOSAS` (6 traducciones), `avisoGlosasTexto`, `avisoGlosasSoloEnEspanol` y los tests que la afirmaban (2 de `AjustesScreenTest`, 2 de `IdiomaAplicacionTest`). **Y la comprobación que la nota tapaba, que SÍ estaba rota:** con la app en alemán aprendiendo alemán (o inglés con inglés), la **Biblioteca y Guardados caían a la glosa ESPAÑOLA de respaldo** (una glosa en español bajo una palabra alemana en una app en alemán) y la **ficha** dibujaba esa misma glosa o un «—» / «Sin equivalencia directa» en las expresiones. Ahora, si el idioma de la app es el que se aprende, **no hay glosa**: `datosFila` devuelve `null`, la ficha no dibuja la línea y la búsqueda de la Biblioteca tampoco busca por una glosa que no se muestra. **Un test por superficie** (ficha, Biblioteca, Guardados) contra el español como control. `MatrizSuperficiesTest` esperaba la glosa de respaldo también para aprendido = app; se actualizó (no se borró).
- **Tarea 9 — versión 0.4.1 (versionCode 16).**

**Textos PROVISIONALES** (los escribió Overview en el prompt para el español; las otras cinco lenguas las tradujo Code para que compile y pase el test de cobertura; **todo se reemplaza en la ronda de textos**): `TOUR_FICHA_2` («Ficha en tu idioma»), `TOUR_FICHA_9` («La planilla para tu profesor») y `TOUR_GUARDADOS_2` («Exportar e importar», el paso fusionado). **Pasos que se movieron con su texto intacto:** misión, autochequeo, prompt y cierre de la ficha; interruptor y consigna del desafío (invertidos: sus claves se intercambiaron, no sus textos). En el alemán del paso del interruptor agregué «dieser Schalter» (y equivalentes) donde el texto español dice «ya no aparece», para que quede claro qué es lo que no aparece. **No se reescribió ningún texto existente.**

**Lo que no pude comprobar sin un teléfono:** que **32 dp** se vean bien (el test mide la posición del ancla tras el scroll en Robolectric, no cómo se ve); que los tres botones de la tarjeta queden cómodos con la letra al 160 % (probé 140 dp de ancho con la letra normal); y el aspecto del selector de vistas de la Biblioteca con el ancla en «Verbos».

**Pendiente que no es de esta ronda** (visto al hacer la tarea 1): con el desafío, si el usuario enciende el interruptor **a mano**, el tour (que ya no lo enciende) igual muestra los dos pasos la próxima vez que se lance; no hay forma de lanzarlo con el interruptor encendido porque el recuadro bloquea los toques, así que en la práctica el paso 2 de ese tour casi siempre se saltea. Si Fer lo quiere visible, la forma honesta es **cambiar el texto del paso 1** para que invite a encenderlo y que el tour se lance de nuevo con él encendido (decisión de contenido, no de código).

**Tamaño del APK** (empaquetado limpio): **debug 63 202 572 B** y **release 37 764 466 B**: **idénticos** a la Ronda Q (delta 0 B; el código nuevo y el viejo de los tests cambian el dex pero el empaquetado queda en el mismo tamaño, como en otras rondas). **Tests:** 1268 en debug y 341 en release, 0 fallas.

**Guía de testers en español — filas cambiadas** (las otras cinco y los seis `.pdf`, a cargo de Overview): **solo el título**, «versión 0.4.0» → «versión 0.4.1». La fila «Bienvenida y tutorial» no menciona cantidad de pasos y la de Verbos no cambió: **ninguna otra**.

**Decisiones donde el prompt no cerraba:** (1) la excepción del interruptor de traducir en el paso 2, tal cual; (2) el total de «Paso n de total» descuenta también los pasos cuya ancla **ya se ve ausente** (el prompt pedía «los ya salteados», pero para que «1 de 3» salga desde el primer paso hace falta mirar adelante); (3) `saltearSinAncla()` y `siguiente()` son funciones distintas del gestor: solo la segunda apila; (4) el ancla de la opción «Verbos» es un modificador en el chip, no un `Box`; (5) el paso fusionado de Guardados conserva la clave `TOUR_GUARDADOS_2` (la 3 se borró).

## 64. Verificación de Code Ronda R y guías 0.4.1 (Overview, 14-oct-2026)

**Todo lo que informó Code es cierto.** Verificado sobre `57ed1fe6`, corriendo las herramientas y leyendo los ocho parches, no el informe:

| Comprobación | Resultado |
|---|---|
| `componer.py` + `verificar_consistencia.py` | **606 fichas, 56 semanas especiales, 0 con problemas**; 240 A2/B1, 0 problemas |
| `diff -rq build assets/contenido` | **0 diferencias** |
| `aplicar_verbos.py` | 0 packs con diferencias; 1229 apariciones, 1104 núcleos, 0 errores |
| `indice_audio.py --comprobar` | índice al día |
| `grep -c INTERNET` | **1** = solo el comentario |
| `versionCode` / `versionName` | **16 / 0.4.1** |
| Orden de la ficha | título → traducir → descripción → errores → vocabulario → misión → autochequeo → prompt → planilla → barra de arriba; tope 10 |
| Guardados, desafío, Verbos | 2 pasos / interruptor antes que consigna / `tour-biblioteca-vista-verbos` en el chip, sin cambiar el aspecto |
| Glosa en los cuatro caminos | la vista Verbos usa el mismo `FilaPalabra`/`datosFila` que la Biblioteca, así que **también queda cubierta** (no hace falta un cuarto test) |

**El mejor hallazgo de la ronda es de Code y no estaba pedido:** las anclas registraban `boundsInRoot()`, que **recorta contra los ancestros** y devuelve un rectángulo **vacío** para un nodo que todavía está fuera de la pantalla dentro de un scroll — justo el caso en que hay que traerlo a la vista. Es decir: desde la Ronda Q, para un ancla lejos del scroll, el tour dibujaba un agujero de 0 × 0 y **nunca desplazaba**. Parte de «el foco queda pegado al borde» era esto, no el margen.

**Pero el arreglo dejó una regresión visual, y la encontré leyendo el parche:** `rectDe()` es también la fuente de (1) el agujero del foco y su borde y (2) de qué lado va la tarjeta (`rect.center.y`). Con el rectángulo **sin recortar**, un ancla que no entra entera en la pantalla deja el agujero dibujado **también sobre lo que no se ve** (un rectángulo iluminado por encima de la barra de arriba o por debajo del borde) y puede poner la tarjeta **encima del foco**. El caso realista es **`tour-ajustes-tema`**, que agrupa tema, modo y tipografía: en un teléfono chico es más alto que media pantalla. Arreglo en `prompts/35` (Ronda S): recortar el rectángulo contra el recuadro **para dibujar y para ubicar la tarjeta**, y seguir usando el sin recortar para `traerALaVista`.

**Lo que `totalEfectivo` supone, y por qué hoy funciona:** el total descuenta los pasos cuya ancla no está registrada **en este momento**. Eso es correcto porque la ficha, Ajustes y la cabecera de la Biblioteca son `verticalScroll`, que **compone todos sus hijos** estén visibles o no. El día que una pantalla del tour pase a `LazyColumn`, un ancla lejos de la ventana **no estará compuesta** y el total va a mentir (y `esUltimoPaso` puede decir «Listo» de más). No hay nada que arreglar hoy; queda anotado para el día que cambie una lista.

**La decisión que Code dejó a Fer, con mi recomendación:** el tour del desafío dice «Paso 1 de 1» porque el interruptor arranca apagado, el tour no lo enciende y el recuadro bloquea los toques. Code propone cambiar el texto del paso 1 para invitar a encenderlo. **Hay algo mejor y arregla dos pasos a la vez: que el elemento con foco se pueda tocar** (los toques pasan solo dentro del agujero; el resto de la pantalla sigue bloqueada, y el tour **no avanza solo**). Eso desbloquea el paso del interruptor y también el del vocabulario, que hoy tiene que decir «el encabezado abre la lista cuando termines con esto». Es el patrón habitual de los tutoriales con foco. Está como tarea 1 de `prompts/35`, **condicionada a que Fer diga sí**.

**Guías de testers:** las cinco traducidas y los seis `.pdf` a **0.4.1**. Code cambió solo el título en la española y ninguna otra fila, así que acá tampoco: **solo el título**. Los seis siguen en dos carillas.

**Lo que sigue sin moverse, y ya van seis rondas:** revisión de nativo de fr / it / pt, clave de firma, ícono y pantalla de carga, capturas para la tienda y regenerar el audio inglés con `en_GB-alba`. El tutorial está terminado a los fines prácticos; **el camino crítico para publicar es ese otro, y no avanzó nada desde la sección 52**.

## 65. Code Ronda S — ✅ implementada (Code, 14-oct-2026), versión 0.4.2

Siete commits, uno por tarea (la 3 **no se hizo**: Fer no la autorizó), sobre la Ronda R. Contenido, esquema, assets y textos de pasos del tutorial **sin tocar**; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes (comprobado en los manifiestos fusionados de debug y release); los dos guardas de `preBuild` y los demás `verificar*` intactos. **Nada de lo visual se vio en un teléfono.**

- **Tarea 1 — margen de 96 dp** (`ui/Tutorial.kt`, `ui/TutorialOverlay.kt`): `MARGEN_AL_TRAER_DP` = **96** (no lo bajé). `rectanguloConMargen` recibe un tope de alto: si arriba + ancla + abajo no entran, **se recorta el margen de abajo y el de arriba se conserva entero**. El recuadro pasa como tope el **80 % del alto de la pantalla** (`screenHeightDp`), porque desde el recuadro no se conoce el alto del visor real del scroll; sin tope, Compose trata un rectángulo más grande que el visor con su regla de «el borde más cercano», que no prioriza nada. **Medido en Robolectric** (`w360dp-h640dp`, dp = px): ancla lejana de 60 dp en 600 dp de alto → ≥ 96 de los dos bordes; ancla ya visible y despegada → no se desplaza; **visor de 440 dp** (pantalla de 640 menos barras) con ancla de **150** → 194 arriba y 96 abajo; con ancla de **400** (no entra con 96 + 96) → **96 arriba y el final del ancla cortado 56 dp**, o sea el comienzo queda con su margen completo, como pidió el prompt. **Un detalle que conviene saber:** en un visor «desnudo» de 640 dp sin barras, un ancla de 400 queda a 224 arriba y 16 abajo (el tope del 80 % ya la deja entrar y Compose hace el desplazamiento mínimo desde abajo); no es el caso de la app real, pero lo dejo medido como `MEDIDA-360x640`. **El número 96 en sí (si se ve bien o si en un teléfono chico ya es mucho) no lo vi.**
- **Tarea 2 — el agujero recortado** (`Tutorial.kt`, `TutorialOverlay.kt`): `recortarAlRecuadro(rect, ancho, alto)` (null si no queda área) y `focoEnLaMitadDeArriba(foco, alto)`. El agujero y su borde se dibujan con el rectángulo **recortado al `size` del DrawScope**; el lado de la tarjeta se decide con el centro del foco **recortado** y el `tamano` del recuadro. `traerALaVista` sigue usando el rectángulo **sin recortar**. Intersección vacía = paso sin foco: **sin agujero y con la tarjeta abajo** (no al centro: el centro queda para los pasos que no tienen ancla). Tests: recorte, nulos (arriba, abajo, justo en el borde), lado de la tarjeta, ancla que se pasa de la pantalla y ancla fuera de pantalla.
- **Tarea 3 — NO hecha** (esperaba la autorización de Fer). Consecuencias que siguen como estaban: el paso de la consigna del desafío se saltea casi siempre y el tour dice «Paso 1 de 1»; el encabezado del vocabulario sigue sin poder tocarse con el tour abierto. No se tocó la fila «Bienvenida y tutorial» de la guía.
- **Tarea 4 — bloque «Voces»** (`AcercaDeScreen.kt`, `TextosInterfaz.kt`): `ACERCA_VOCES_TITULO` y `ACERCA_VOCES_TEXTO` (los seis textos del prompt, tal cual, con sus `\n`), entre «Licencias» y la versión, en un `SelectionContainer` con el mismo `Bloque`. **Las URL son texto seleccionable y copiable**: sin `LinkAnnotation`, `ClickableText`, `Intent` ni `UriHandler` (un test lee el fuente y lo exige). Nombra siempre las dos voces. Borrados `ACERCA_CREDITOS_TEXTO` (6 traducciones) y `textoAcercaCreditos`; `LenguAppAppTest` («Acerca de en cada uno de los 6 idiomas») ahora usa los accesores nuevos y se **cambió**, no se borró. **Sobre el enlace tocable:** no lo hice; si Fer lo quiere, es un `ACTION_VIEW` hacia el navegador (sin `INTERNET`, pero una puerta nueva hacia afuera): decisión suya.
- **Tarea 5 — licencia por componente**: `ACERCA_LICENCIAS_TEXTO` con la línea del prompt en los 6 idiomas («… Audio: la licencia de cada voz, en «Voces».»); un test exige que cada idioma remita al nombre de su propio bloque.
- **Tarea 6 — el audio inglés entra al release**: `AUDIO_INGLES` = `true` en **release** (ya lo era en debug); **borrados** el bloque `androidComponents` con `ignoreAssetsPatterns.add("<dir>EN")`, la tarea `verificarReleaseSinAudioIngles` y su `dependsOn` en `packageRelease` / `packageReleaseBundle` / `bundleRelease`. Comentarios corregidos (`build.gradle.kts`, `MainActivity.kt`): dicen qué licencia tiene cada voz y que la atribución está en «Acerca de»; no queda ninguna frase de «no se pueden publicar». **Tests que nombraban lo quitado, cambiados (ninguno borrado a secas):** `AudioInglesFueraDelReleaseTest` → renombrado a `AudioInglesEnElReleaseTest` y vuelto del revés (exige que NO estén la exclusión, el bloque ni la guarda; que el interruptor valga `true` en las dos variantes; que sigan las otras guardas y los 4 133 clips en el repo); y `IndiceAudioTest` (`AudioTest.kt`), «el interruptor … encendido en debug y apagado en release» → «encendido, en debug y en release». **Medido (empaquetado limpio, `assembleRelease` sin firmar):** **release 56 145 643 B** (antes 37 764 466 B: **+18 381 177 B**, de los cuales 17 643 412 B son los 4 133 clips ingleses guardados en el APK) y **debug 63 218 956 B** (antes 63 202 572 B). Los dos APK llevan `assets/audio/EN/` con 4 133 clips y `assets/audio/DE/` con 4 069. El prompt estimaba ~55 MB; salen **56,1 MB**.
- **Tarea 7 — dos cosas chicas**: borrado el `import` sin usar de `traduccionParaMostrar` en `BibliotecaScreen.kt`; en el `enum ClaveTexto` las claves de los tours quedan **FICHA 1…10, BIBLIOTECA 1…5, GUARDADOS 1…2, AJUSTES 1…4, DESAFIO 1…2** (antes `TOUR_FICHA_2/9` iban al final y `TOUR_DESAFIO_2` antes de `_1`); un test fija el orden. Solo el enum: **las entradas de la tabla de textos siguen en el orden que tenían** (el orden del mapa no tiene efecto).
- **Tarea 8 — versión 0.4.2 (versionCode 17).**

**Lo que no pude comprobar sin un teléfono:** que 96 dp se vean bien (los tests miden la posición del ancla tras el scroll, no cómo se ve ni qué visor real tiene cada pantalla); cuánto de pantalla se come la tarjeta con el ancla alta de Ajustes (`tour-ajustes-tema`) en un teléfono de ~640 dp; cómo se ve el borde del agujero cuando se recorta contra el borde de la pantalla; y que el bloque «Voces» se lea bien con URL largas en 360 dp (el texto salta de línea en los `/`? Compose corta URL largas donde puede; no lo vi).

**Decisiones donde el prompt no cerraba:** (1) el tope de alto de la tarea 1 es el 80 % de la pantalla (no el visor real, que el recuadro no conoce); (2) intersección vacía → tarjeta **abajo** (no al centro); (3) `AudioInglesFueraDelReleaseTest` se renombró a `AudioInglesEnElReleaseTest` en vez de conservar un nombre que ahora miente; (4) el release medido es el **APK sin firmar** (`app-release-unsigned.apk`), igual que en rondas anteriores.

**Guía de testers en español — filas cambiadas:** **solo el título**, «versión 0.4.1» → «versión 0.4.2». Ninguna fila más (Overview ya arregló la del audio; la tarea 3 no se hizo). **Tests:** 1 283 en debug y 343 en release, 0 fallas.

## 66. Licencias de las voces, revisadas: Fer tenía razón (Overview, 14-oct-2026)

Fer cuestionó la conclusión de la sección 48 y de `docs/licencias-voces.md` («los 4100 clips en inglés no se pueden publicar») con el argumento de que una licencia de atribución se cumple **atribuyendo**, y que ninguno de los modelos que miramos es inusable si se cita la fuente. **Tenía razón en lo principal.** El documento está reescrito; esto es el resumen de qué cambió y qué queda por decidir.

**Lo que estaba mal:** no distinguí tres cosas y las traté todas como «no publicable».

1. **CC0 y CC BY no son un problema en ningún caso.** La única obligación de CC BY es atribuir, y atribuir es barato. Vale para `de_DE-thorsten` (CC0, que ni eso exige) y para cualquier CC BY futura, `en_GB-alba` incluida.
2. **La cláusula NC de `en_US-ryan` es discutible, no prohibitiva, y para esta app se inclina a nuestro favor.** La definición de Creative Commons es **de intención, no de dinero**: «not primarily intended for or directed towards commercial advantage or monetary compensation», y la guía oficial dice que el contexto y el propósito importan. LenguApp es gratis, sin publicidad, sin compras y sin telemetría: el propósito no es ventaja comercial, y publicar en una tienda no vuelve comercial el uso por sí mismo. Riesgo bajo.
3. **La cláusula SA de `en_US-ryan` es el problema real, y no se arregla citando** — pero es manejable. BY-NC-SA y BY-SA son incompatibles en un sentido, así que no se puede decir «todo el contenido de la app es CC BY-SA 4.0» en bloque con un audio BY-NC-SA adentro. Lo que las hace convivir es que **el audio es un componente aparte con su propia licencia**, no una obra derivada del contenido, y las dos licencias permiten eso en una colección. La condición es **declarar la licencia por componente**, que es lo que pasa a hacer «Acerca de».
4. **El asterisco que queda no es legal, es de proyecto:** un componente NC hace que la app deje de ser limpiamente reutilizable y quien la bifurque hereda la restricción. Eso choca con un principio declarado del proyecto, y es **decisión de Fer**, no un impedimento.

**Retractación:** el 13-oct anoté como hallazgo preocupante que `de_DE-thorsten` y `fr_FR-siwis` se afinaron sobre la voz `lessac`, y que por eso el alemán arrastraría la licencia de Blizzard. **Le di más peso del que merecía.** Es el mismo razonamiento (¿son los pesos y la salida obra derivada del corpus?) dos pasos más lejos, y vale para casi todas las voces de Piper: si se sostuviera, no se podría publicar nada generado con Piper en ningún idioma. Una conclusión tan fuerte es señal de que el razonamiento es demasiado fuerte. El dato queda; la alarma, retirada.

**Donde Fer no tiene razón, y lo verifiqué con la licencia delante:** `en_US-lessac` es el único caso distinto. La [licencia de Blizzard 2013](https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html), leída hoy, **no es Creative Commons y no tiene cláusula de atribución**: se concede «only to the person or organisation named», es no transferible y sin derecho a sublicenciar, solo para fines de investigación con el uso comercial excluido expresamente, y el usuario se compromete a «not to lend, hire, sell, distribute or otherwise part with the Materials». No hay cita que cure eso. **Pero dos cosas juegan a favor y tampoco las había tenido en cuenta:** (1) esa licencia **no obliga a Fer** — es un contrato entre CSTR y quien la firmó, el autor de Piper; Fer bajó un modelo MIT de un repositorio público y nunca aceptó términos con CSTR; (2) la licencia **no dice nada sobre modelos ni sobre voz sintética**, y que la salida de un modelo sea obra derivada de su corpus de entrenamiento es una pregunta abierta cuya lectura más común se inclina por el no. O sea: por contrato no está obligado, y por derecho de autor depende de algo sin respuesta firme. No es un «no se puede»; **es el único caso donde la única defensa es un argumento jurídico que preferiríamos no tener que hacer.**

**No es asesoramiento legal:** no soy abogado y esto es lectura de textos de licencia con criterio de ingeniería.

**Lo que se hace ahora (pedido de Fer):** un bloque **«Voces»** en «Acerca de» con **todas** las voces, las de dominio público y las licenciadas: voz, corpus, licencia y URL. Prompt: `prompts/36-code-voces-en-acerca-de.md`, que se suma a la Ronda S. Tres decisiones que dejé tomadas ahí: el bloque **depende de `BuildConfig.AUDIO_INGLES`** (no se atribuye una voz que ese APK no lleva, así el texto es cierto en debug y en release); la línea de licencias pasa a declararse **por componente**; y las URL van como **texto seleccionable y copiable, no como enlaces tocables** — un enlace sería un `ACTION_VIEW`, que no necesita el permiso `INTERNET` y por lo tanto no rompe la regla dura 1, pero es una puerta nueva hacia afuera y la decide Fer.

**Lo que queda por decidir, y ya no es urgente:** el release **ya sale sin audio inglés** (la guarda `verificarReleaseSinAudioIngles` de la Ronda N), así que esto **no bloquea publicar**. Cuando el audio inglés vaya a salir, Fer elige entre (a) publicarlo atribuyendo, que cuesta cero y deja el asterisco NC, o (b) regenerarlo con `en_GB-alba` (CC BY 4.0), que cuesta una tarde de generación y elimina los dos asteriscos. **Mi preferencia sigue siendo regenerar, pero por una razón más débil que la que di antes:** no porque lo otro sea ilegal, sino porque una tarde de cómputo compra no tener que explicar nada nunca.

**Un dato que hace falta y que no puedo ver desde acá:** `audio-manifiesto.md` dice «`en_US-ryan-high`, `en_US-lessac-high` — dos voces seleccionables, se genera con las dos», pero la app reproduce **un clip por texto** y hay **4100 clips ingleses para 4100 textos**: los clips que existen son de **una sola voz** y el plan de las dos quedó sin implementar. **Si son todos de `ryan`, el caso `lessac` no existe** y el único asterisco es el NC, que es el discutible. Fer generó los clips localmente: lo sabe él, y de eso depende la fila inglesa del bloque «Voces».

**Y un error mío en la guía de testers, encontrado de paso:** la fila del audio decía «solo en alemán … En inglés aún no», pero en **debug** `AUDIO_INGLES` es `true`, así que **el tester que prueba el APK de debug sí oye inglés**. La guía describía el release y la lee quien usa el debug. Corregida en las **seis** guías (también la española, que normalmente es de Code, para no partir el arreglo en dos parches) y los seis `.pdf` regenerados, los seis en dos carillas.

## 67. Decisiones de Fer del 14-oct-2026: audio inglés, margen del foco y la idea del texto semanal

**Estado de los commits, verificado:** en `origin/main` están las dos entregas de Overview (`32eead3e`, `4d7491a5`) y **Code no corrió la Ronda S**: los prompts 35 y 36 nunca se ejecutaron. No se salteó ninguna iteración; simplemente la ronda no empezó. Por eso **el prompt 37 es acumulativo y reemplaza al 35 y al 36**, que quedan en el repo solo como registro. La sección 65 sigue reservada para el informe de Code de esa ronda.

**1. Audio inglés: se publica como está, atribuyendo. DECIDIDO.** Fer no quiere rehacer 4100 clips, y es una razón perfectamente buena. Y confirmó el dato que faltaba: **los clips son todos de `en_US-ryan-high`**; `en_US-lessac` nunca se usó, así que la licencia de Blizzard **no toca ningún archivo que la app distribuya** y ese caso se cierra. Queda el asterisco de la cláusula NC, que es el discutible y que para una app gratis y sin publicidad se inclina a nuestro favor. Consecuencias, todas en el prompt 37: bloque «Voces» en «Acerca de» con las dos voces **siempre** (ya no condicionado a la variante), licencias declaradas **por componente**, y **el audio inglés entra al release** — `AUDIO_INGLES` a `true`, se quita `ignoreAssetsPatterns` y **se retira la guarda `verificarReleaseSinAudioIngles`**, que se montó cuando creíamos que esas voces no se podían publicar. El release pasa de ~37,8 MB a ~55 MB. `docs/licencias-voces.md` y `audio-manifiesto.md` (que seguía anunciando dos voces seleccionables, algo que nunca se implementó) quedaron corregidos.

**2. Margen del foco: 32 dp no alcanzó y pasa a 96 dp.** Fer lo probó dos veces y sigue viéndolo pegado al borde. Pidió «al menos 1,5/2 cm»: 1 dp = 1/160 de pulgada = 0,159 mm, así que **1,5 cm ≈ 94 dp** y 2 cm ≈ 126 dp. **96 dp** es el extremo bajo de lo pedido y ya es el triple de lo que hay. Lo que hay que tener presente, y está escrito en el prompt: 96 arriba más 96 abajo son ~190 dp, el 30 % de una pantalla de 640 dp, y la tarjeta puede ocupar el 55 %; para un ancla alta (`tour-ajustes-tema`) **los dos márgenes no van a entrar**, y entonces se prioriza el de arriba. Esto además **agrava** la regresión del agujero sin recortar que encontré en la sección 64: cuanto más se desplaza, más seguido un ancla alta queda parcialmente fuera de pantalla con el agujero dibujado sobre lo que no se ve. Las dos cosas van juntas en la misma ronda a propósito.

**3. Sigue sin respuesta** si el elemento con foco puede volverse tocable (tarea 3 del prompt 37, explícitamente condicionada a que Fer autorice). Mientras no se decida, el paso del interruptor del desafío sigue muerto («Paso 1 de 1») y el del vocabulario sigue teniendo que decir «cuando termines con esto».

**4. El «texto de la semana»: investigado, no empezado.** Informe completo en `docs/textos-por-nivel-investigacion.md`. Resumen del veredicto, que es favorable con una condición:

- **Vale la pena**, y es lo más útil que se le puede agregar al contenido ahora: tapa el paso que falta entre la lista de vocabulario y la misión (ver las palabras usadas juntas, en contexto), y es **la única parte de la ficha que entrena la boca sin un interlocutor** — lo que encaja exactamente con una app offline y sin cuentas.
- **El costo recurrente es chico: ~700 palabras por semana** para los dos pares que se usan de verdad. Lo que asusta es el total —**~181 000 palabras** para los 606 packs, dos novelas de prosa restringida en dos idiomas y cinco niveles— y es completamente evitable: el campo es **opcional**, «esta semana no tiene texto» ya es el caso normal de casi toda la matriz, y se escriben los pares que se usan. Un año de de-B2 y en-C1 son **~104 textos, ~36 000 palabras**.
- **El problema real es de densidad, no de volumen.** Con **13,5 ítems de vocabulario por pack** (6 `nucleo` + 7,5 `ampliacion`, medido) y un texto A2 de 110 palabras, la cobertura total da **un ítem cada 8 palabras**: eso no es un texto, es un listado con conectores, y es justo el nivel donde un texto artificial hace más daño. En B2 (un ítem cada 21) y arriba no hay problema. Dos salidas limpias: **(a)** obligatorios los `nucleo` y opcionales los `ampliacion`, que usa un campo **que ya existe** y da un ítem cada 18 en A2 — mi recomendación —; **(b)** dos textos cortos en A2 y B1 en lugar de uno, que mantiene la cobertura íntegra que pidió Fer y además es más fiel al nivel. Las expresiones, parciales en los dos casos.
- **Cinco cosas hay que decidir antes de escribir una línea:** cómo se marca la cobertura de modo verificable por máquina (un `contains()` no sirve: «der Arbeitstag» aparece como «des Arbeitstags» y los separables se parten — la salida elegante es que **la negrita sea también la anotación**, con el id del ítem, y una regla **R12** que lo verifique); dónde vive el texto (**no** en el pack, porque cubre vocabulario del pack **y** expresiones del núcleo: hace falta una **cuarta capa** `contenido/textos/`); qué pasa con la regla de bilingüismo P2 en A2/B1, que duplicaría el trabajo y pondría la traducción al lado justo cuando la gracia es leer sin ella (propongo exceptuar el texto, **explícitamente**); que **no va a tener audio** (606 textos son ~650 MB, no entra, y no hace falta porque la gracia es la voz de quien lee); y quién revisa, porque Fer puede juzgar de-B2 y en-C1 pero **no** alemán C1 ni C2 — el mismo agujero que fr/it/pt.
- **Lo que yo haría:** decidir esas cinco, escribir **tres textos de prueba** en de-B2 sobre semanas que ya existen, y que Fer los lea en voz alta y se grabe. Si con tres la experiencia no es buena, con 104 tampoco. Recién después: schema, `componer.py`, R12 y el prompt de la conversación que los escriba por lotes. **El riesgo no es el volumen: es que el texto se lea como un ejercicio**, y un texto que se nota construido para meter catorce palabras es peor que no tener texto.

**Guías de testers:** la fila del audio quedó en su forma final en las seis («suena en alemán y en inglés», con las licencias en «Acerca de») y los seis `.pdf` regenerados, los seis en dos carillas. El salto de versión a 0.4.2 lo hace Code en la española y yo en las otras cinco cuando cierre la Ronda S.

## 68. Verificación de Code Ronda S, foco tocable y el texto semanal decidido (Overview, 15 y 18-oct-2026)

**Estado de los commits:** en `origin/main` están mi entrega (`f4eaeca0`) y **los 7 commits de la Ronda S** de Code (`6dc79c83` a `cc59695c`). Nada pendiente de aplicar.

**Verificación independiente de la Ronda S** (corrida sobre `cc59695c`, no leída del informe):

| Comprobación | Resultado |
|---|---|
| `componer.py` + `verificar_consistencia.py` | **606 fichas, 56 semanas especiales, 0 con problemas**; 240 A2/B1, 0 problemas |
| `diff -rq build assets/contenido` | **0 diferencias** |
| `aplicar_verbos.py` / índice de audio | 0 diferencias / índice al día |
| `grep -c INTERNET` | **1** = solo el comentario |
| Versión | **17 / 0.4.2** |
| `MARGEN_AL_TRAER_DP` | **96** |
| Guarda del audio inglés | retirada (`ignoreAssetsPatterns` y `verificarReleaseSinAudioIngles` ya no existen); **quedan en pie** `verificarAssetsContenidoActualizados`, `verificarConsistenciaContenido` y `verificarPermisosApk` |
| `AUDIO_INGLES` | `true` en las dos variantes |
| Bloque «Voces» | las dos claves nuevas presentes; `ACERCA_CREDITOS_TEXTO` y `textoAcercaCreditos` **borradas** (0 referencias) |

**El informe no exageró nada.** Dos números que conviene registrar porque corrigen los míos: el release pasó a **56 145 643 B**, o sea **+18,4 MB** y no los ~17,6 MB que yo había estimado (mi `du` redondeaba por bloque); y en la carpeta hay **4133** `.ogg` ingleses, no 4100 — la diferencia son clips sin referencia, ver abajo.

**59 clips de audio no se usan nunca** (33 en inglés, 26 en alemán, ~350 KB): están en `assets/audio/` y **ninguna entrada del índice los nombra**, así que viajan en los dos APK y no suenan jamás. Son restos de textos que cambiaron en alguna ronda de contenido (`EN_B2_a_check_up_T04.ogg`, `DE_B2_aufgrund_DE-G16.ogg`…). `indice_audio.py` ya los informa en su línea de resumen («ogg sin entrada en el índice 59»); mi error fue leer solo las primeras diez líneas del informe durante varias rondas y creer que eran diez. **No es tarea de Code**: los clips los genera Fer localmente, así que la limpieza la decide él. Si se borran, el índice no cambia (no los nombra) y el release baja ~350 KB; si se dejan, no molestan. **Fer decide.**

**Lo que sigue sin verse en un teléfono**, y es lo único que falta de la Ronda S: que 96 dp se vea bien; cuánto tapa la tarjeta en el paso de `tour-ajustes-tema` en una pantalla de ~640 dp (el peor caso, porque ese ancla agrupa tema, modo y tipografía); y cómo se ve el borde del agujero recortado. Code lo midió en Robolectric: con un visor de 440 dp, un ancla de 150 dp queda a 194 arriba y 96 abajo, y un ancla de 400 dp queda a 96 arriba y pierde 56 dp del final — que es exactamente la prioridad al margen de arriba que pedía el prompt.

**Foco tocable: AUTORIZADO por Fer.** Va como Ronda T (`prompts/38`). Qué implica, en concreto, porque Fer preguntó: el agujero del foco **pasa los toques al elemento que ilumina** y el resto de la pantalla sigue bloqueado; **el tour no avanza solo** (sigue haciendo falta «Siguiente»); se habilita **paso por paso** con un campo `tocable` y **solo en dos**: el encabezado del vocabulario y el interruptor del desafío. Lo que desbloquea: se puede abrir la lista de vocabulario mientras el paso la explica, y se puede **encender el interruptor del fin de semana**, con lo cual el paso de la consigna deja de saltearse y el tour del desafío pasa a decir «Paso 1 de 2» en vez de «Paso 1 de 1». No se habilita en anclas que navegan a otra pantalla, porque un toque ahí dejaría el tour corriendo sobre una pantalla que no es la suya.

**Riesgo que Code marcó y pasa a tarea:** no se comprobó cómo saltan de línea las URL del bloque «Voces» en 360 dp. `https://www.kaggle.com/datasets/roholazandie/ryanspeech` son 52 caracteres **sin un espacio**, y si el salto de línea no las parte se cortan o se van de la pantalla — justo lo que Fer pidió eliminar en la Ronda P. Y una URL truncada **no cumple la atribución de CC BY**, que es para lo que está ahí.

### El texto semanal: respuestas y alcance

Detalle completo en `docs/textos-por-nivel-investigacion.md`. Lo decidible:

- **Un texto por combinación (skill + topic), no uno por topic, y está forzado:** un topic aparece **cuatro veces al año** y **cada aparición tiene su propio pack** de 13-14 ítems. Un texto por topic tendría que cubrir ~54 ítems en 285 palabras. La unidad es la aparición = la semana. Como beneficio, al ser por semana el texto puede apoyarse también en la gramática del skill y en algunas de sus expresiones, que es lo que lo hace servir de micro-tarea 0.
- **Alcance de «lo que queda de este año»: 97 textos, ~29 000 palabras.** Hoy es la semana ISO 42 y el contenido de 2026 llega a la 53; descontando las semanas especiales quedan 10 semanas de contenido para siete pares y 9 para de-B2, en-B2 y en-C1 (que tienen Survival en 44 y Repaso en 48). Con la semana 42 incluida serían 107 textos y ~32 000 palabras.
- **Objeción al alcance completo: 30 de esos 97 son de-C1, de-C2 y en-C2, que nadie del equipo puede revisar.** Fer juzga alemán B2 e inglés C1, y sin problema cualquier cosa por debajo, pero no un texto de alemán C2. Si entre los testers no hay quien pueda, esos tres pares convienen al final o para cuando haya revisor. Los otros 67 son revisables hoy.
- **Corrijo lo que dije ayer sobre dónde se guarda:** `mision` y `microtareas` ya viven dentro de `apariciones`, así que el texto «debería» ir ahí. Me quedo igual con una **capa aparte** `contenido/textos/{idioma}-{nivel}-{anio}.json`, y el motivo no es técnico: las ocurrencias las toca la conversación del calendario y los textos los escribiría otra conversación por lotes, y dos conversaciones en el mismo archivo es lo que ya hizo chocar dos parches acá. Además los packs llevan el año en el nombre, así que **no se reusan entre ediciones** y no hay nada que proteger atando el texto a la semana.
- **Los textos de un idioma NO son la traducción de los del otro. Pedido explícito de Fer**, con un ejemplo propio: leyó dos libros de relatos, uno en alemán y uno en inglés, con los mismos cuentos traducidos y los nombres germanizados, y se sintió «más de lo mismo». **La regla ya existe para los packs** (`proyecto/CLAUDE.md`: «Los packs de cada idioma se redactan desde ese idioma, nunca traduciendo el pack del otro idioma del mismo tema») y se extiende al texto, operativizada: mismo topic con **ángulo distinto** por idioma, **género declarado y distinto** entre los dos idiomas de la misma semana, **sin personajes paralelos**, escrito desde el idioma y no hacia él, y cada par de textos entregado con **una línea que diga en qué se diferencian** — si esa línea cuesta escribirla, son demasiado parecidos. No es verificable por máquina y no hay que pretenderlo. **Agrego algo que Fer no pidió y va en la misma dirección:** aunque no se traduzca nada, si todas las semanas son una entrada de diario también se siente «más de lo mismo» dentro del mismo idioma, así que la variedad de género hay que llevarla como registro.

### Las cinco decisiones, cerradas por Fer el 15-oct-2026

Ya no queda nada abierto antes de escribir. Especificación completa en `docs/textos-por-nivel-investigacion.md`; acá lo que decide cada una.

1. **Marcado: solo `**negrita**`, sin ids, igual para vocabulario y expresiones.** Fer dijo negrita sin id porque la definición ya está en el vocabulario de la semana, **y tiene razón por un motivo extra que él no sabía: es lo único que la app puede renderizar hoy.** `MarcadoEnLinea.kt` declara su parser «plana y sin anidar, garantizado por el compilador», y lo comprobé: `***x***` **no** da negrita-cursiva, da una negrita con dos asteriscos sueltos a los lados. Así que negrita-cursiva no existe sin tocar un parser que promete no anidar. Tampoco distingo vocabulario de expresiones con estilos distintos: el lector no necesita saber en qué lista está la palabra, y `*cursiva*` ya significa otra cosa en el proyecto (palabra citada, que en una traducción se envuelve en « »). **Costo para la app: cero** — `textoConMarcado` ya existe.
2. **La cobertura se declara al lado del texto, no dentro.** El id nunca fue para el lector: era para el verificador, y sin él no se puede comprobar nada buscando en el texto («der Arbeitstag» aparece como «des Arbeitstags», los separables se parten). Así que cada entrada lleva el texto limpio **más una lista `cubre`** con los ítems **tal como están escritos en el pack**, y **R12** compara por igualdad exacta de cadenas, sin lingüística: que cada ítem de `cubre` exista literalmente en el pack; que desde B1 estén todos y en A2 al menos los `nucleo`; que las `expresiones` existan en el núcleo del skill; que haya **al menos tantos tramos en negrita** como ítems declarados; que el largo caiga en la banda del nivel; y que el texto no use `*cursiva*`. **Lo que R12 no puede comprobar, y hay que decirlo: que el tramo en negrita *sea* el ítem declarado.** El conteo de tramos atrapa los casos groseros; el resto lo atrapa la revisión. R12 no es hermética.
3. **Cobertura total obligatoria desde B1; en A2 solo los 6 `nucleo`** «y alguno más de ser posible». Las **expresiones siguen parciales en todos los niveles** (3 o 4 de las ~8). **Consecuencia que traje yo: subí la banda de B1 de 150-200 a 200-240 palabras.** Con cobertura total obligatoria, 13,5 ítems en 175 palabras dan **un ítem cada 13** — el punto más apretado de toda la escala, más que B2. A 220 baja a uno cada 16, que es escribible, y los descriptores del CEFR para B1 aguantan 220 palabras sin problema: lo que no aguanta B1 es la complejidad, no la longitud.
4. **El texto no se traduce, por ahora.** Fer: el bilingüismo se mantiene como regla general de A2/B1, pero el texto no, por el costo y porque las palabras nuevas ya tienen su traducción en la misma ficha. **Queda abierto a cambiar según la UX.** Es una **excepción explícita y reversible a la regla de prevención P2**, y hay que anotarla en `REGLAS-PREVENCION.md` cuando el texto se implemente: una regla que se incumple en silencio deja de ser una regla. Coincido, y agrego que si en el uso resulta que en A2 el texto no se entiende sin ayuda, la salida barata no es traducirlo: es **acortarlo**.
5. **Los tres pares que Fer no puede revisar se escriben igual**, porque entre los testers hay nativos alemanes y angloparlantes. **Retiro la objeción** y el alcance vuelve a los **97 textos completos, ~30 000 palabras**. Única condición que agrego: que esos 30 **no lleguen a los testers sin que un nativo los lea primero**.

**El orden de ejecución, que importa más que las decisiones:** primero **tres textos a mano** —dos en de-B2 y uno en de-A2, el caso apretado—, sobre semanas que ya existen, para que Fer los lea en voz alta y se grabe. **Antes** del schema, de `componer.py` y de R12. Si el texto se lee como un ejercicio, la micro-tarea 0 no sirve y la tubería fue trabajo tirado; tres textos cuestan una hora y la tubería cuesta una ronda entera de Code. Después: schema y capa `contenido/textos/`, R12, la sección en la app; después el lote de 97, empezando por de-B2 y en-C1. Y el **registro de géneros desde el primer texto**, no desde el décimo: es lo único que evita que a la semana 6 todo sea una entrada de diario.

**Sigue sin escribirse ni un texto**, como pidió Fer.

**Guías de testers:** las cinco traducidas y los seis `.pdf` a **0.4.2**, los seis en dos carillas.

## 69. Code Ronda T — ✅ implementada (Code, 18-oct-2026), versión 0.4.3

Siete commits, uno por tarea (la 7 es la versión y la guía), sobre la Ronda S y el parche de limpieza de audio de Overview (que estaba aplicado). Contenido, esquema y assets sin tocar; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes; ningún clip de audio tocado. **Nada de lo visual se vio en un teléfono.** *(Entre mis commits entró en el repo el de Overview «Verbos: primera tanda…»: no es mío y los parches de `outputs/ronda-t/` no lo llevan.)*

- **Tarea 1 — el foco se puede tocar** (`Tutorial.kt`, `TutorialOverlay.kt`, `TutorialPasos.kt`): `PasoTour.tocable`, falso por omisión, verdadero **solo** en `tour-ficha-vocabulario` y `tour-desafio-interruptor` (un test fija que sean exactamente esos dos y que ninguna ancla que navega —`acerca-de`, `ver-bienvenida`, `tour-ajustes-idiomas`— lo sea). En un paso tocable el recuadro deja de comerse todos los toques y los bloquea con **cuatro franjas** (arriba, abajo, izquierda, derecha) alrededor del **mismo rectángulo recortado** del agujero, **sin** el margen de 6 dp del borde: el aro iluminado sigue bloqueado y un toque en la parte del ancla que no se ve no le llega. En los demás pasos, un solo bloqueo de pantalla completa, como siempre. El tour **no avanza solo**: sigue haciendo falta «Siguiente». **El ancla que desaparece con el paso ya mostrado:** antes solo se salteaba si no aparecía al empezar; ahora, si el registro la pierde, el paso se saltea con el mismo mecanismo tras `ESPERA_ANCLA_MS` (600 ms), así una recomposición que la saca y la vuelve a poner no lo saltea. Tests: toque dentro llega y afuera no (y el paso no avanza); sin `tocable` el elemento tampoco recibe; ancla que desaparece; **sobre la app real**, abrir la lista del vocabulario desde su encabezado (la lista se abre, el tour sigue en el mismo paso, el foco sigue al encabezado y su alto no crece con la lista, y el botón de otra sección sigue bloqueado), y el sábado encender el interruptor del desafío desde su paso (el contador pasa de «1 de 1» a «1 de 2» y «Siguiente» muestra la consigna). **Cambia algo que el prompt ya anticipaba:** el paso 2 del tour del desafío ya no se saltea «casi siempre»: se puede encender el interruptor *con el tour abierto*, pero **el tour sigue sin encenderlo por su cuenta** (regla dura 13).
- **Tarea 2 — las URL de «Voces»: ya saltaban bien, no toqué el código.** Medido a **360 dp** (español y alemán) y a **320 dp con la letra al 130 %**, con gráficos **NATIVE** de Robolectric (el modo legacy mide el texto en casi cero y no demuestra nada; por eso el test de esta tarea corre en NATIVE): ninguna línea pasa del ancho disponible, no hay desborde y el texto queda entero, sin puntos suspensivos. Las URL parten en la «/» y en el «-» (p. ej. `https://www.kaggle.com` / `/datasets/roholazandie` / `/ryanspeech`), nunca a mitad de una palabra. La URL se copia entera (el corte es solo de pantalla). Lo único que quedó: tests.
- **Tarea 3 — Habilidades por semana y sin semanas futuras** (`IndiceHabilidades.kt`, `BibliotecaScreen.kt`, `MainActivity.kt`): `OrdenHabilidades` (**Alfabético** por omisión / **Por semana**, por la primera aparición, con el título de desempate) con dos chips (`GrupoChips`/`OpcionFiltro`), `remember` simple: **cada entrada a la Biblioteca arranca en «Alfabético»** (test: salir y volver a entrar). `construirIndiceHabilidades` recibe `anioEnCurso`: si el año del índice es **anterior** al en curso se muestra entero; si es el en curso (**o uno posterior**) se descartan las apariciones con `semana > semanaReferencia` y una habilidad cuya primera aparición es futura no aparece. `anioEnCurso` sale de `semanaIsoDe(LocalDate.now()).anio` como valor por omisión de `LenguAppApp` y es **inyectable** (los tests pasan 2026/2027; si no, el test de la app real se rompería el 1 de enero de 2027). **`fichaAAbrir` es ahora siempre `apariciones.last()` de las que se muestran** (la última ya pasada), con el porqué en un comentario. Claves nuevas `BIBLIOTECA_ORDEN_ALFABETICO` y `BIBLIOTECA_ORDEN_SEMANA`, **traducidas por Code** (EN Alphabetical / By week, DE Alphabetisch / Nach Woche, FR Alphabétique / Par semaine, IT Alfabetico / Per settimana, PT Alfabética / Por semana): revisar. **Decisión:** un año *posterior* al en curso se corta igual en la semana vista (el prompt solo cerraba el anterior y el actual). En la semana 2 la lista tiene una o dos entradas; no se rellena. Tests nuevos: corte por semana (con la semana de referencia incluida y la semana 0 vacía), año anterior entero, orden por semana contra alfabético y chips en pantalla. El test de la app real se actualizó (el título de la primera fila se calcula con el mismo corte).
- **Tarea 4 — la planilla no va en el desafío** (`ContenidoSemanalScreen.kt`): el botón pasó **dentro de la rama normal**; el bloque de título (título, subtítulo, insignias) se queda en los dos modos. Tests: el botón está en la ficha y no en el desafío (y vuelve al apagar el interruptor); **el paso `tour-ficha-planilla` del tour se saltea solo en el desafío** y el tour termina sin dejar un recuadro colgado.
- **Tarea 5 — el desafío en el idioma que se aprende** (`ContenidoSemanalScreen.kt`): `RetoFinDeSemana` recibe `idiomaMostrado` e `idiomaAprendido` y pide en `idiomaMostrado` el rótulo, la línea de encuadre, tema, habilidad, condiciones (`reglasReto`) y el título y la ayuda de las frases. Sin cadenas nuevas; comprobé que las claves del andamiaje existen y no están vacías ni con llaves en **alemán y en inglés** (test). `consigna` y `habilidad` ya seguían el idioma mostrado; **`tema` no**: salía de `topicNombres.nombre(…, idiomaInterfaz)`, lo arreglé. **El bug concreto del prompt tenía un detalle que no cerraba:** `Vinieta(texto, angulares = true)` solo envuelve en « » las *citas marcadas con `*…*`* del texto, y **ninguna de las 6 172 expresiones** de los assets las trae, así que pasar `angulares` sin más no habría puesto ningún « ». Envolví **la frase entera** (`«frase»`) cuando `idiomaMostrado != idiomaAprendido` y a `Vinieta` no le paso `angulares` para no anidar. El interruptor del desafío (el de arriba de la ficha) **sigue en idioma de app**: es un control de la app, no parte del reto. `REGLAS-PREVENCION.md`: dos filas nuevas en la tabla 2 y un párrafo con el cambio (antes: andamiaje en idioma de app). **Tests cambiados (no borrados):** `RetoFinDeSemanaTest` (la verificación general ahora espera el idioma mostrado y las frases entre « » solo cuando corresponde; «no persiste», «sin consigna» y «sin expresiones» miraban rótulos en español), `ConsignaDesafioEstiloTest` (los rótulos del bloque en el idioma de la ficha) y `MatrizSuperficiesTest` (solo el nombre: `reglasReto` devuelve el idioma que se le pide). Nuevos: B2 con la app en español → andamiaje en el idioma que se aprende y frases sin « »; A2 con el interruptor de traducción encendido → idioma de app y frases entre « »; A2 con el interruptor apagado → idioma que se aprende y sin « ».
- **Tarea 6 — Verbos sin nivel**: la línea extra queda con **el régimen en alemán y la separabilidad del phrasal en inglés**; si no hay nada que decir es la cadena vacía y la fila no dibuja la línea (la pantalla ya hacía `ifEmpty { null }`). Se borraron `EntradaVerbo.nivelMasBajo` y `ItemBiblioteca.nivelMasBajo()` (sin otros usos). Léxico y ficha, intactos. **Tests cambiados (no borrados):** `VerbosTest` (2 reescritos y 1 retirado, el que probaba `nivelMasBajo`), `NucleosVerbosTest` (2), `VistaVerbosTest` (4: ahora afirman que el nivel NO se ve), `NucleosVerbosPantallaTest` (1); nuevo en `BusquedaVerbosPantallaTest`.
- **Tarea 7 — versión 0.4.3 (versionCode 18).**

**Tamaño del APK** (empaquetado limpio): **release 55 890 727 B** y **debug 62 964 036 B** (Ronda S: 56 145 643 y 63 218 956; **−254 916 y −254 920 B** por los 59 clips que Overview borró; el código nuevo no cambia el empaquetado de forma visible). Los dos llevan 4 100 clips ingleses y 4 043 alemanes. **Tests:** 1 304 en debug y 345 en release, 0 fallas.

**Guía de testers en español — filas cambiadas, con el texto exacto:**
1. **Título:** «versión 0.4.2» → «versión 0.4.3».
2. **Bienvenida y tutorial:** «La primera vez eliges idioma y nivel; cada sección muestra un recorrido que oscurece todo menos lo que explica. En dos pasos (el encabezado del vocabulario y el interruptor del desafío) lo iluminado se puede tocar; el recorrido no avanza solo: sigue con «Siguiente». «Saltar» los saltea. Se repite desde Ajustes.»
3. **Habilidades en la Biblioteca:** «Es una lista de solo lectura: toca una habilidad para abrir su semana. Se ordena por título (por omisión) o «Por semana», según su primera aparición. Solo se ven las semanas que ya llegaron; un año anterior se ve completo.»
4. **Verbos en la Biblioteca:** cambia solo el comienzo: «Lista los verbos del idioma, de todos los niveles; debajo de cada uno, en alemán, el régimen (von + Dat) y, en inglés, si el phrasal verb es separable.» (antes: «… con su nivel más bajo y, en alemán, el régimen (von + Dat).»). El resto de la fila igual.
5. **No toqué, pero conviene mirar** (el prompt pedía tres filas): la del **Desafío de fin de semana** dice «arriba la consigna con los minutos; debajo, tema, habilidad, condiciones y frases», que sigue siendo cierto, pero ahora todo eso sale **en el idioma que aprendes** (o en el de la app con el interruptor de traducción encendido), y la planilla ya no aparece en el desafío.

**Lo que no pude comprobar sin un teléfono:** que el borde del agujero y las franjas de bloqueo coincidan al píxel (el test mide que el toque dentro llega y afuera no, no cómo se ve); que tocar el encabezado del vocabulario con el recuadro puesto se sienta natural; que la fila de dos chips de orden se vea bien al lado del selector de vistas; y los textos de las frases entre « » en pantalla chica.

**Decisiones donde el prompt no cerraba:** (1) las franjas bloquean el aro de 6 dp del borde del agujero; (2) un paso cuyo ancla desaparece se saltea tras 600 ms, no al instante; (3) año posterior al en curso → se corta como el en curso; (4) `anioEnCurso` inyectable, con la fecha del sistema solo como valor por omisión; (5) la frase entera entre « » en vez de `angulares` (ver tarea 5); (6) los chips de orden van como un grupo propio arriba de la lista, sin título.

## 70. Textos piloto, limpieza de audio y el feedback del 18-oct (Overview, 18-oct-2026)

La sección 69 queda reservada para el informe de Code de la Ronda T.

**Tres textos piloto escritos** (`docs/textos-piloto.md`): de-B2 semana 43 (Subjektive Modalverben, Salud, nota de voz transcrita, **253 palabras**), de-B2 semana 45 (Verben mit festen Präpositionen, Hobbies, aviso de un club, **246**) y de-A2 semana 43 (Alltag bewältigen, Entorno, mensaje a una vecina, **119**). Van en `docs/` y **no** en `contenido/`: el schema y la capa `textos/` no existen todavía, y la idea es justamente no construirlos antes de saber si el texto se lee bien en voz alta. Los tres sobre semanas reales de 2026, ortografía suiza.

**Lo que ya se aprendió escribiéndolos, antes de que Fer los lea:**

1. **La negrita es más de la que yo había estimado, por un motivo que no había previsto.** Conté los tramos: **27 en el texto 1 y 22 en el texto 2**, no 14 y 18. **Un ítem de varias palabras se parte en varios tramos** cuando la frase lo separa: «Das muss etwas sein» sale como `**Das muss** etwas **sein**`. Así que en B2 queda **una negrita cada nueve palabras** y el texto puede verse manchado. Opciones si molesta: marcar solo el vocabulario y no las expresiones (~16 tramos), no partir los ítems discontinuos, o no marcar nada. **Se decide leyendo en el teléfono.**
2. **El género hace casi todo el trabajo.** En el texto 1 las **ocho** expresiones del skill entraron solas, sin planearlo, porque especular *es* el género de una nota de voz y los modales subjetivos son especulación; en el texto 2 hubo que adaptarlas al plural. Cuando el género y la gramática de la semana coinciden, el texto se escribe solo. **Eso es un criterio para elegir el género, no un accidente**, y es lo primero que tiene que anotar el registro de géneros.
3. **A2 es el que más costó y salió el más natural de los tres.** La regla de los 6 núcleo obligatorios funciona: entraron **10 de 12** ítems y quedaron afuera `der See` y `der Wald`, que en un mensaje sobre el jardín de al lado no tenían dónde ir. Pedir los 12 en 119 palabras habría obligado a inventar un lago y un bosque — exactamente la costura que arruina la idea.

**Los 59 clips de audio sin referencia, borrados** (33 ingleses, 26 alemanes). Quedan **4043** alemanes y **4100** ingleses, que es exactamente lo que dice `audio-estado.json`; `indice_audio.py --comprobar` informa ahora **0 ogg sin entrada en el índice** y el índice sigue al día sin regenerarse (no los nombraba). El `AudioInglesEnElReleaseTest` de Code pide `> 4000`, así que sigue pasando. La carpeta `assets/audio` bajó a 51 MB.

**Feedback de Fer del 18-oct, todo en `prompts/39` (Ronda T, que reemplaza al 38):**

- **El foco tocable** (autorizado) y **las URL del bloque «Voces» en 360 dp** (riesgo que marcó Code): las dos tareas del prompt 38, sin cambios.
- **Habilidades: orden por semana de aparición, y sin semanas futuras.** El alfabético **se queda por omisión** — a Fer le gusta. Los datos ya están: `construirIndiceHabilidades` recibe `semanaReferencia` y cada fila tiene sus `apariciones`. El caso que hay que decidir bien es **el año**: si el índice es de un año anterior se muestran todas las semanas (una edición cerrada se consulta entera) y solo el año en curso se corta en la semana vista. Consecuencia que hay que aceptar: **en la semana 2 de un año la lista tiene una o dos entradas**, que es coherente con el modo revista, así que no se inventa relleno.
- **La planilla del profesor sale del desafío.** Hoy `onPlanilla?.let { BotonPlanilla(...) }` está **fuera** del `if (modoDesafio)`, así que aparece en los dos modos; se mueve adentro de la rama normal. El bloque de título se queda en los dos. Y el ancla `tour-ficha-planilla` deja de existir en modo desafío, así que ese paso del tour **se saltea solo** — correcto, pero con test.
- **El desafío pasa al idioma que se aprende.** Fer notó que el andamiaje está siempre en el idioma de la app mientras la ficha normal usa el idioma mostrado. Es cierto y son ocho funciones: `etiquetaDesafioFinde`, `textoRetoConsignaLinea`, `etiquetaBibliotecaTema`, `etiquetaRetoHabilidad`, `etiquetaRetoCondiciones`, `reglasReto`, `etiquetaRetoFrasesTitulo` y `textoRetoFrasesAyuda`. **No hacen falta cadenas nuevas:** alemán e inglés son dos de los seis idiomas de la interfaz, así que es un cambio de parámetro (`idiomaMostrado` en vez de `idiomaInterfaz`), no de tabla. **Y hay un bug concreto además:** las frases de arranque van siempre en el idioma que se aprende y la línea es `frases.forEach { Vinieta(it) }` **sin `angulares`**, así que con el interruptor encendido quedan sin « ». Esto **cambia la definición de la superficie «desafío» de la regla P1** y hay que anotarlo en `REGLAS-PREVENCION.md`.
- **La vista Verbos deja de mostrar el nivel.** Fer preguntó si conviene que los verbos tengan nivel si la vista no filtra por nivel. **No conviene, y hay un motivo que cierra la discusión:** ese nivel **no es una propiedad del verbo sino de nuestra cobertura** (es el nivel más bajo de los packs donde aparece), la tabla maestra ni lo tiene («Sin nivel: la vista Verbos filtra solo por claseVerbo», dice el propio archivo), y los verbos de referencia que se van a agregar **no van a tener nivel**: la columna quedaría a medias justo cuando la sección crezca. El nivel **se queda** en la vista de léxico y en la ficha, donde el ítem sí pertenece a una semana de un nivel.

**Ampliar la biblioteca de verbos: el hallazgo que define el trabajo** (`prompts/40`). Medido: la tabla maestra tiene **exactamente** los verbos que ya están en los packs — **0** entradas que no aparezcan en contenido —, porque se construyó a partir del contenido. **No es un diccionario, es una tabla de anotación**, y la vista Verbos se construye desde los ítems del contenido, no desde la tabla: **un verbo que no está en ningún pack no puede aparecer en la app hoy**. Hoy la vista muestra ~470 entradas en alemán y ~617 en inglés.

Así que ampliarla son **dos trabajos**: las entradas de referencia (Verbos, ahora) y una **segunda fuente** en la vista (Code, una ronda posterior). Como la segunda no existe, la primera tanda es chica a propósito: **A1 y A2, ~200 verbos en alemán** y el equivalente en inglés, con `referencia: true`, `traducciones` en los cinco idiomas que no son el que se aprende, `claseVerbo`, `reccion` en alemán, y `nivelFuente` como **campo de trabajo que no llega al contenido**. La fuente es `proyecto/data/vocab_{de,en}.json`, las listas que ya se armaron con los perfiles de Goethe y CEFR-J: en alemán hay **2540** candidatos a verbo que no están en la tabla (79 de A1, 123 de A2, 414 de B1, 1382 de B2, 542 de C1). **Los 1382 de B2 no entran nunca**: ése es el diccionario que Fer dijo que no quiere, y además los verbos de B2 y C1 ya están cubiertos por el contenido de esos niveles.

**En hold, a pedido de Fer:** los textos del tutorial (`docs/tutorial-textos.md`), que todavía está mirando. Code sigue con la instrucción de no reescribir ninguno.

## 71. Verificación de Code Ronda T y de la primera tanda de Verbos (Overview, 18-oct-2026)

**La Ronda T está bien.** Verificada aplicando los siete parches sobre `73234e83` y corriendo las herramientas, no leyendo el informe: **606 fichas, 56 semanas especiales, 0 con problemas**; 240 A2/B1, 0 problemas; **0 diferencias** entre `build` y los assets; `aplicar_verbos.py` 0 packs con diferencias y 0 errores; **0 ogg sin entrada en el índice** (los 59 borrados ya no están); `grep -c INTERNET` = 1; versión **18 / 0.4.3**.

**Donde me equivoqué yo y Code tenía razón:** el prompt 39 pedía `Vinieta(it, angulares = idiomaMostrado != idiomaAprendido)` como «el bug concreto». Code contestó que `angulares` solo envuelve en « » las *citas marcadas con `*…*`* y que ninguna expresión las trae, así que mi instrucción no habría puesto ni un « ». **Lo medí: 6172 expresiones en los assets, 0 con `*cita*`.** Tenía razón, y envolver la frase entera era la única forma de cumplir lo que pedía Fer. Mi error fue leer el nombre del parámetro y no lo que hace.

**Pero la solución merece una mirada en el teléfono, y no es una objeción al código:** la regla P1 dice «fragmento en otro idioma **dentro de un texto** → entre « »», y acá cada viñeta está **entera** en el otro idioma. Una lista donde las tres viñetas van entre comillas angulares puede verse cargada. Si molesta, la alternativa es una sola línea de aviso arriba de la sección en vez de comillas en cada viñeta. **Que lo mire Fer; el código no está mal.**

**Dos cosas que no vio nadie y encontré yo:**

1. **Las tres filas nuevas de la guía rompieron las dos carillas, en los seis idiomas.** Code no genera los `.pdf`, así que no podía verlo: con sus textos exactos los seis se iban a **tres carillas**. Los reescribí más cortos sin perder nada, quité la fila de los signos raros (↔, ✗, →, ₂: es cosmética y ya se conoce) y **acorté las filas más largas en alemán y francés**, que son los dos idiomas al borde. **Los seis volvieron a dos carillas.** Y de paso actualicé la fila del Desafío, que Code marcó como «conviene revisarla» y seguía diciendo lo de antes: ahora dice que sale en el idioma que se aprende y ya no menciona la planilla.
2. **`anioEnCurso` es un segundo `LocalDate.now()`.** `LenguAppApp` ya tenía `fechaInicial = LocalDate.now()`, y la Ronda T agregó `anioEnCurso = semanaIsoDe(LocalDate.now()).anio`: **dos lecturas independientes de «ahora»** que un test puede dejar en desacuerdo (poner `fechaInicial` en 2026 y olvidarse del año). En producción coinciden y Code pasó el valor en los tests, así que hoy no hay bug; pero es una trampa para el que venga. **Arreglo de una línea:** que el valor por omisión sea `semanaIsoDe(fechaInicial).anio`, que en Kotlin puede referirse a un parámetro anterior. Queda para la ronda siguiente, no vale una propia.

**Decisiones de Code que apruebo, con el motivo:** (a) las franjas de bloqueo cubren el aro de 6 dp del borde del agujero — correcto, porque ese aro está **fuera** del elemento y un toque ahí no le llegaría igual, así que dejarlo pasar solo serviría para tocar lo que hay detrás; (b) un año **posterior** al en curso se corta como el actual — coherente: no se ve más de lo que ya se vería navegando hasta esa semana, y «entero» es privilegio de una edición cerrada; (c) medir las URL con gráficos **NATIVE** de Robolectric porque el modo por omisión mide el texto en casi cero — sin eso el test no probaba nada, y es el tipo de detalle que convierte un test verde en un test inútil.

### La primera tanda de Verbos: la cuenta no cierra, y hace falta el parche

**No tengo el parche**, solo el informe, así que esto es lo que se puede comprobar desde el repo.

**Lo que está bien:** tocar `aplicar_verbos.py` **no era opcional y el diagnóstico es exacto.** La comprobación final de la herramienta recorre `list(tabla) + list(otros) + list(cols)` y marca error por cada clave que no se vio en ningún pack; una entrada `referencia: true` cae ahí sí o sí. Mi requisito 3 («que siga dando 0 errores sin tocar la herramienta») era imposible, y excluirlas de `tabla` es el cambio mínimo correcto.

**Pero ese cambio deja una trampa silenciosa, y hay que cerrarla en la ronda siguiente.** Si una entrada queda fuera de `tabla`, también queda fuera del mapa que **aplica** `claseVerbo` y `reccion`. Hoy no pasa nada; el día que un pack nuevo use un verbo que ya está en la lista de referencia —y va a pasar cada vez más, porque la lista cubre justamente los verbos comunes— esa aparición **no recibe anotación y nada avisa**. Lo correcto es que la herramienta **informe** «la entrada de referencia X ya aparece en un pack: promovela» en vez de ignorarla.

**Y la cuenta no cierra.** El informe dice que de 124 candidatos A1 alemanes **70 ya estaban en la tabla**, y de 153 de A2, **58**: 128 en total. Lo medí comparando contra `forma`, `item`, `base` y los núcleos, que es lo que el informe dice haber hecho: **solo ~76 estaban**. De los 201 que no están, **26 aparecen como `raiz` de otra entrada** (`stehen` es la raíz de `aufstehen`) y **5 como `sich X`**. Eso explica la mitad de la diferencia, y **cuenta como presente algo que no lo está**: que `stehen` esté en la tabla no pone `aufstehen`, y `aufstehen` es exactamente el verbo A1 por el que existe una lista de consulta.

Descontando las 41 descartadas a mano —bien descartadas: `draussen`, `geboren`, `Kosten`, los modales—, quedan **~160 verbos A1/A2 alemanes legítimamente nuevos** y se escribieron **108**. Faltarían **~52**. **Antes de aprobar la tanda y pasar a B1 hace falta el parche y la lista de los que quedaron afuera, con el motivo de cada uno.** No es un error grave ni hay que rehacer nada: es terminar de contar.

**Lo demás de Verbos, anotado para cuando llegue el parche:** la selección inglesa fue **a ojo sobre 1064 y 1243 lemas**, sin filtro, así que ahí no hay forma de saber qué quedó afuera — conviene cruzarla contra las listas de phrasal verbs e irregulares que ya están en los documentos del proyecto. Las decisiones sobre preposición regida (siete con la preposición en la `forma`, cinco como `simple` con la preposición en `nota`) y acepciones separadas por punto y coma me parecen bien y son las que yo habría tomado. Y sí: **que escriba una sección de FALTANTES** con la cuenta y la decisión de `referencia: true`.

### El problema de proceso, que es el más urgente de los tres

**Las dos conversaciones trabajaron sobre el mismo clon y se vieron.** Code informa «en el repo apareció un commit *Verbos: primera tanda…* que no es mío»; Verbos informa «hay otra sesión trabajando en `app/`… la rama tiene cuatro commits T1–T4 por delante de `origin/main`» y que commiteó **por ruta explícita** para no arrastrar nada ajeno. Las dos hicieron lo correcto y las dos entregaron parches limpios, pero **eso fue cuidado de ellas, no del montaje**. Mi plan del 18-oct decía «en paralelo, no se pisan» porque pensaba en archivos distintos; el archivo no era el problema, **el clon sí**.

Para la próxima: o **una carpeta por conversación**, o **de a una por vez**. Y mientras eso no esté, la regla mínima es la que aplicaron solas: **commitear por ruta explícita, nunca `git add -A`**.

## 72. Verbos de referencia: primera tanda cerrada y la cuenta reconciliada (19-oct-2026)

Tandas 1 y 1b de la biblioteca de verbos de consulta (prompts 40 y 41). Entradas nuevas en `contenido/verbos/{de,en}.json` con `referencia: true`, traducción a los otros cinco idiomas, clase y régimen. **No anotan ningún ítem del contenido**, así que no se ven en la app hasta que una ronda de Code haga que la vista Verbos tome esta segunda fuente.

**Total: 498 entradas de referencia.** DE 160 (A1 60, A2 100) y EN 338 (A1 141, A2 197). La tabla pasó de 305 a 465 entradas en alemán y de 360 a 698 en inglés. (Eran 499: la entrada de referencia `deal with` se quitó el 19-oct, ver el final de esta sección.)

### La cuenta reconciliada (277 candidatos alemanes de A1 y A2)

El filtro es verificable: lema de `data/vocab_de.json` con nivel A1 o A2, sin artículo delante, terminado en `-en`, `-rn` o `-ln`.

| resultado | cuántos | A1 | A2 |
|---|---|---|---|
| escritos en la tanda 1 | 108 | 35 | 73 |
| ya estaban en `verbos[]` (por `forma`, `item` o `base`) | 76 | 45 | 31 |
| **solo existían como núcleo de una colocación** (prompt 23, **sin glosa propia**) | **45** | 24 | 21 |
| basura del filtro (adverbios, participios, sustantivos, modales y auxiliares) | 41 | 19 | 22 |
| solo coincidían tras quitar «sich» o «etwas» de una entrada reflexiva | 6 | 1 | 5 |
| no figuraban de ninguna forma | 1 | 0 | 1 |
| **suma** | **277** | 124 | 153 |

**De dónde venía la discrepancia.** El informe de la tanda 1 dijo «128 ya en la tabla» y Overview midió 76. Las dos son correctas y miden cosas distintas: 76 + 45 + 6 + 1 = 128. Mi chequeo contaba como presente al que figuraba **como núcleo de colocación** (45) y al que coincidía **tras quitar el pronombre** de una entrada reflexiva (6). No era `raiz`: `raiz` nunca entró en el chequeo, y ningún candidato coincide solo por ahí. Overview atribuyó esos 45 a `raiz` porque su medición no miró `nucleos[]`, y esos verbos (*kommen*, *geben*, *halten*, *hören*, *fahren*) son además raíz de algún separable.

**Pero la conclusión de Overview era la correcta, por otro camino:** esos 45 son entradas **sin traducción propia** —en el prompt 23 se decidió no inventar glosas para los núcleos—, y una lista de consulta existe justamente para contestar «¿qué quería decir esta palabra?». Así que la tanda 1b los escribe: **45 núcleos + 6 formas desnudas de verbos reflexivos** (*entschuldigen*, *aussprechen*, *informieren*, *organisieren*, *versuchen*, *öffnen*, que son otro verbo que su forma con «sich») **+ 1 que faltaba** (*sich umziehen*) = **52 entradas alemanas nuevas**.

**El mismo agujero en inglés, y es más grande:** de los 147 lemas ingleses que la tanda 1 descartó por «ya en la tabla», **78 estaban solo como núcleo** (*take*, *make*, *go*, *keep*, *see*, *put*, *say*…) y por lo tanto sin glosa. Escritos también en la 1b. Más 2 de la tarea 3, abajo: **80 entradas inglesas nuevas**.

**Consecuencia para Code, que crece:** FALTANTES 38 ya pedía fusionar por infinitivo los 32 casos en que un núcleo coincide con un verbo propio. Ahora son **77 en alemán y 80 en inglés**, porque cada una de estas entradas de referencia es el mismo infinitivo que un núcleo existente. La fusión deja de ser cosmética: sin ella la vista muestra dos filas del mismo verbo, una con glosa (la de referencia) y otra con las colocaciones (el núcleo). **Un solo desacuerdo de clase**, deliberado y anotado en la entrada: el núcleo *hören* quedó `mit-Praposition` por *auf seinen Körper hören*, y la entrada de referencia dice `simple`, que es la clase del verbo; `hören auf` es una valencia secundaria. R11 no lo toca porque solo verifica los packs.

### La selección inglesa, cruzada contra las listas del proyecto (tarea 3)

| lista | verbos | sin lema en `vocab_en.json` | nivel B1 o más | A1/A2 ya cubiertos | A1/A2 que faltaban |
|---|---|---|---|---|---|
| phrasal verbs (Cambridge G&V First, pág. 1: *burst out* … *set off*) | 111 | 104 | 7 | 0 | **0** |
| irregulares (Cambridge G&V First, *broadcast* … *teach*) | 80 | 1 | 15 | 60 | **4** |

Los 104 phrasal sin lema **no son una limitación de la lematización**, y la explicación que escribí primero («CEFR-J lematiza por palabra») era falsa: `vocab_en.json` tiene **1021 lemas con espacio**, entre ellos *according to*, *bus stop* y quince phrasal. El motivo real es más acotado: **los phrasal están poco representados en la lista, y los que están son todos de nivel alto.** La diferencia importa porque la versión equivocada implicaría que un cruce contra una lista de phrasal nunca puede dar nada, y en B1 podría darlo. De los 4 irregulares que faltaban se escribieron 2: **`to light`** (el lema A1 de la lista de vocabulario es el sustantivo, el verbo entra por la lista de irregulares) y **`to deal`** (con nota de que el uso habitual, *deal with*, ya estaba). Los otros dos quedan fuera a propósito: **`to do`** es auxiliar, y **`spring`** es A1 como sustantivo (la estación), no como verbo.

**El hueco de los `.pdf` está cerrado (Overview, 18-oct-2026), y no hace falta repetir nada.** La capa de texto de los dos archivos está incompleta —la de phrasal llega hasta *set off* y la de irregulares va de *broadcast* a *teach*—, así que Overview cruzó el resto contra la lista canónica:

- **Irregulares, la mitad que el `.pdf` no muestra:** 29 verbos. Presentes en la tabla 18 (*bear*, *become*, *begin*, *bend*, *bite*, *blow*, *break*, *bring*, *tear*, *tell*, *think*, *throw*, *understand*, *wake*, *wear*, *weep*, *win*, *write*); de nivel B1 o más, 7 (*arise*, *beat*, *bet*, *bind*, *bleed*, *breed*, *weave*); ausentes de A1/A2 solo 3, y los tres por buen motivo: **`be`** es auxiliar, **`wind`** es A1 como sustantivo (el viento, el mismo caso que *spring*) y **`awake`** es marginal con *wake* ya en la tabla.
- **Phrasal, las dos páginas:** 111 de la página 1 y las 42 canónicas de la página 2. Con lema en `vocab_en.json`, 7 y 8; **los quince son B2 o C1** (*cut down*, *give up*, *run down*, *settle down*, *think over*, *wear off*, *wear out*; *fall for*, *fall out*, *pick up*, *set off*, *stand by*, *stand for*, *stand out*, *stand up for*).

**Cero huecos de A1/A2 en las dos listas.** Queda como deuda solo si alguna vez hace falta cruzar B1 contra el material completo.

### `referencia: true` y por qué hubo que tocar `aplicar_verbos.py`

La comprobación final de la herramienta recorre `list(tabla) + list(otros) + list(cols)` y marca error por cada clave que no apareció en ningún pack. Una entrada de referencia cae ahí por definición —no anota ningún ítem—, así que **el requisito de «0 errores» era imposible** hasta sacarlas del mapa. El cambio es una condición en `cargar_tablas`.

**La trampa que eso abría, y cómo quedó cerrada.** Al quedar fuera del mapa, el día que un pack nuevo use un verbo que ya está en la lista de referencia, esa aparición **no se anotaría y nada avisaría**. Ahora la herramienta lo trata como **error**, con el criterio del resto de las guardas del proyecto: una regla que se incumple en silencio deja de ser una regla. El mensaje dice qué hacer (quitarle `referencia: true` y completar la anotación). Lo fija `aplicar_verbos.py --autotest`: dos casos, con y sin el ítem de referencia en el pack.

**La guarda comparó mal en el primer intento y la primera corrida lo mostró:** la clave inglesa quita el «to», así que el verbo *to delay* chocaba con el sustantivo *delay*, y salieron 6 falsos positivos (*delay*, *rain*, *snow*, *exercise*, *clean*, *support*: los seis son sustantivos o adjetivos en el pack). La guarda compara ahora el **texto del ítem**, no la clave. Es el ejemplo de por qué la guarda tenía que ser error y no aviso: un aviso entre 600 líneas no se mira.

### Decisiones donde el prompt 40 no cerraba

- **Preposición regida en la `forma` frente a `nota`:** la lleva en la forma el verbo que sin ella significa otra cosa (`belong to`, `consist of`, `depend on`, `refer to`, `deal with`, `focus on`, `worry about`, clase `verbo-preposicion`); va en `nota` cuando el verbo existe solo (`care`, `point`, `apply`, `click`, `search`, clase `simple`). No se uniforma.
- **Acepciones múltiples:** separadas por punto y coma, en el mismo orden en las cinco lenguas (*wählen* → «elegir; votar; marcar un número»). No se abre una entrada por acepción.
- **`nota` es campo de trabajo**, como `base`, `raiz` y `nivelFuente`: no llega al contenido ni a la app. Se usa para el régimen secundario (*danken … für + Akk*), para avisar de inseparables que parecen separables (*unternehmen*, *übernachten*, *übersetzen*, *bekommen*, *beraten*, *untersuchen*, *versuchen*) y para las variantes ortográficas (se eligió la británica: *criticise*, *organise*, *realise*).
- **`raiz` solo en los separables alemanes** (27 de las 160): es lo que hace que buscar «stellen» traiga la familia. En inglés no se usa.
- **Ortografía suiza también en las glosas:** 0 `ß` en las 498 entradas, incluidas las glosas alemanas de los verbos ingleses (P10).
- **Ninguna glosa al idioma que se aprende:** un verbo alemán lleva es/en/fr/it/pt y uno inglés es/de/fr/it/pt, porque desde la Ronda R la app no muestra glosa cuando el idioma de la app es el que se aprende.

### Qué queda para la segunda tanda

- **B1**: 414 candidatos alemanes sin cubrir, de los cuales habrá que descontar los que estén solo como núcleo. Nada de B2 ni C1: son 1924 y los cubre el contenido de esos niveles.
- **Fusión por infinitivo en la app** (arriba): sin eso, estas 498 entradas se ven como filas duplicadas.
- **La vista todavía no las muestra**: hace falta la ronda de Code que tome la tabla como segunda fuente de la vista Verbos.
- **Nadie revisó fr / it / pt** de las 498 entradas. Alemán e inglés los juzga Fer.

### Dos arreglos del 19-oct-2026 (prompt 43)

- **`deal with` quedó una sola vez.** Era el único caso en que una entrada de referencia chocaba con una **entrada de anotación**: `deal with something` ya anota un ítem del contenido y por lo tanto ya se ve en la app con su traducción, que es justo lo que la lista de referencia viene a resolver. Se quitó la entrada de referencia `deal with`; queda la de anotación con su clase **`phrasal-inseparable`**, que es la correcta: *deal* solo significa «repartir», así que la partícula no es composicional, y la lista de phrasal verbs de Cambridge trae *deal with*. La entrada `deal` se queda, con la nota corregida (antes decía que *deal with* estaba como `verbo-preposicion`). **Las otras 123 colisiones son contra núcleos y las resuelve la fusión por infinitivo de Code, no se tocan.**
- **El desacuerdo de `hören` quedó escrito en la entrada**, no solo acá: la `nota` de la entrada de referencia dice que el núcleo lleva `mit-Praposition` con `auf + Akk`, que esta entrada dice `simple` a propósito porque esa es la clase del verbo, y que no se uniforme sin decidirlo antes para todos los verbos con valencia secundaria. Era el único desacuerdo de clase entre una entrada de referencia y un núcleo.

## 73. Verificación de la tanda 1b de Verbos y la ruta de la segunda fuente (Overview, 18-oct-2026)

Verificación independiente de `cc7d949a` (ya en `origin/main`), más la decisión de **cómo** llegan a la app las 499 entradas de referencia, que es lo que estaba sin resolver.

### La reconciliación, rehecha candidato por candidato

Reconstruí la clasificación de los 277 candidatos alemanes contra la tabla anterior a la 1b (`92d09866`), por mi cuenta y sin mirar la del informe: **las 277 filas coinciden**. Mis tres discrepancias iniciales (`heissen`, `abschliessen`, `schliessen`) eran mías: busqué con `ß` contra una tabla que por P10 escribe `ss`.

**Mi error de la ronda anterior, cerrado.** Atribuí 26 candidatos a «existen solo como `raiz` de otra entrada». Medidos los doce que nombré: **diez están en `nucleos[]`** —y además son raíz de un separable, que es lo que me confundió— y dos, `braten` y `kennen`, ya estaban escritos en la tanda 1. **Ninguno coincide solo por `raiz`.** Mi medición no miró `nucleos[]`. Es el segundo error del mismo tipo en dos rondas: leí el nombre de un campo en vez de medir qué contiene (el otro fue `angulares` en la Ronda T, FALTANTES 71).

### La guarda de referencia, probada fuera de su propio autotest

Inyecté a mano un ítem igual a una entrada de referencia (`abschliessen`) en un pack real: `aplicar_verbos.py` sale con **`rc=1`** y el mensaje accionable. Que sea error y no aviso es lo que hizo aparecer los 6 falsos positivos de la primera corrida.

### El estado completo, desde el repo

606 fichas / 56 semanas especiales / 0 con problemas; R1–R11 con 0 problemas en 240 fichas A2/B1; `diff -rq` 0 diferencias; `aplicar_verbos.py` 0 packs con diferencias, 1229 apariciones, 1104 colocaciones, 0 errores; `--autotest` 2 casos 0 fallos; packs 2026 y 2027 en verde; índice de audio al día; `INTERNET` = 1.

Las 499 entradas de referencia, auditadas: traducciones completas en las cinco lenguas y **ninguna** glosa al idioma que se aprende; 0 `ß`; 0 clases desconocidas; 0 `mit-Praposition` sin `reccion`; 0 `trennbar` sin `raiz`; 0 formas duplicadas en `verbos[]`.

### Por qué las 499 no se ven, con el mecanismo exacto

`componer.py` **no menciona** la tabla de verbos: la tabla es entrada de `aplicar_verbos.py`, que escribe `claseVerbo` / `reccion` / `nucleoVerbal` **dentro de los packs**. A `assets/contenido/` llegan fichas. Y `verbosDe()` construye la vista desde `ItemBiblioteca`, o sea desde los ítems de las fichas. **Una entrada que no anota ningún ítem no existe para la app.**

### La decisión: extracción en Gradle, no un archivo versionado ni un emisor nuevo

No hace falta ninguna herramienta nueva en `proyecto/tools/`. **El patrón ya está en `app/build.gradle.kts`:** `extraerTopicNombres` lee `banco.json` y escribe solo la tabla que la app necesita en `assets/temas/topic-nombres.json` en cada build, sin versionar la copia. La segunda fuente es lo mismo, y queda **entera del lado de Code** (prompt 42, Ronda U).

- **Extracción, no `Copy`.** El archivo completo pesa 319 KB y lleva `nivelFuente` y `nota`, que son campos de trabajo que por decisión del prompt 40 **no llegan a la app**. Un `Copy` los pondría físicamente dentro del APK para después ignorarlos: una decisión incumplida en silencio. Extraído son **92 KB** (`item`, `base`, `claseVerbo`, `traducciones`, y `raiz` y `reccion` cuando están). **`raiz` tiene que viajar**: es lo que hace que buscar «stellen» traiga la familia, la función que Fer más usa de la vista.
- **`assets/verbos/`, nunca `assets/contenido/`.** `CargadorContenido` hace `assets.list("contenido").filter { endsWith(".json") }` y parsea **todos** como ficha o semana especial: un archivo con otra forma ahí rompe el cargador, y además aparecería como diferencia en el `diff -rq` y en la tarea `verificarAssetsContenido`.
- **Sin estrella.** Una entrada de referencia no es ítem de ninguna ficha, así que un guardado daría una clave que ningún contenido puede volver a producir — el mismo motivo ya documentado para los núcleos puros, y además quedaría colgado el día que el verbo entre a un pack y se le quite `referencia`.

### La fusión por infinitivo va en el mismo commit que la segunda fuente

Medido, normalizando el «to » del inglés: **124 verbos** mostrarían dos filas en cuanto la fuente exista — 45 DE y 78 EN contra núcleos, más `deal with` contra una entrada de anotación. Son `aufstehen`, `kommen`, `machen`, `nehmen`, `take`, `make`, `go`, `put`: los más buscados. Hoy la duplicación es invisible porque la fuente no existe; el día que exista se ve de golpe. Por eso la Ronda U tiene una sola tarea grande y no se parte en dos.

### El cruce de los PDF: deuda cerrada, no pendiente

FALTANTES 72 la deja abierta («los dos `.pdf` con la capa de texto completa»). **La cerré con la lista canónica y no hay ningún hueco.**

- **Irregulares, la mitad que el PDF no muestra** (antes de `broadcast`, después de `teach`): 29 cruzados. Ausentes de nivel A1/A2: **tres, los tres correctos** — `be` es auxiliar (regla del prompt 40), `wind` es A1 como sustantivo (igual que el caso `spring`) y `awake` es marginal con `wake` ya en la tabla.
- **Phrasal, las dos páginas:** 111 + 42 cruzados, 15 con lema en `vocab_en.json` y **todos B2 o C1**. Cero de A1/A2.

**Y el motivo que da FALTANTES 72 es errado:** no es que «CEFR-J lematiza por palabra» — `vocab_en.json` tiene **1021 lemas con espacio**, entre ellos `according to`, `bus stop` y quince phrasal verbs. El motivo real es más acotado: los phrasal están poco representados y los que están son de nivel alto. Importa porque la versión errada implicaría que un cruce contra una lista de phrasal nunca puede dar nada, y en B1 sí podría.

### Dos desacuerdos de clase, no uno

El informe declara uno (`hören`: núcleo `mit-Praposition` frente a referencia `simple`) y el criterio es correcto. **Hay un segundo que no se declaró:** `deal with` es `phrasal-inseparable` como anotación y `verbo-preposicion` como referencia, y además la entrada de referencia es redundante — `deal with something` ya se ve en la app con su traducción. Van al prompt 43 junto con la corrección de FALTANTES 72. A Code se le pidió explícitamente **no** resolverlos ni agregar una guarda que falle por ellos, así que no bloquean la Ronda U.

### B1 espera, con un motivo nuevo

El argumento del prompt 40 («si la forma está mal, se corrige con 200 y no con 600») se agotó: la forma está validada. El que queda es más fuerte: **nadie vio una de estas entradas en la pantalla**. Cómo se lee una glosa de referencia al lado de una con colocaciones, si la falta de estrella se entiende o parece un error, y si 499 entradas nuevas vuelven incómoda la lista alfabética — las tres cambian cómo se escriben los 414 candidatos de B1, y las tres se contestan recién con la Ronda U aplicada.

## 74. Code Ronda U — ✅ implementada (Code, 19-oct-2026), versión 0.4.4

Tres commits (las tareas 1 y 2 van juntas, como pedía el prompt; la 3 y la 4 sueltas) sobre `8d050ff7`. Contenido, `proyecto/contenido/`, `proyecto/tools/` y assets versionados **sin tocar**; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes. **Nada de esto se vio en un teléfono.**

- **Tareas 1 y 2 — la segunda fuente y la fusión, en un solo commit.**
  - **Extracción en Gradle** (`app/build.gradle.kts`, tarea `extraerVerbosReferenciaAssets`, enganchada a `preBuild` junto a `generarTopicNombresAssets`, con `inputs`/`outputs` declarados): lee `proyecto/contenido/verbos/{de,en}.json`, se queda con las entradas `referencia: true` y escribe `assets/verbos/referencia-{de,en}.json` con **solo** `item`, `base`, `claseVerbo`, `traducciones` y, cuando están, `raiz` y `reccion`. **`nivelFuente`, `nota` y la marca `referencia` no viajan**; las 665 entradas de anotación tampoco. Carpeta propia (`assets/verbos/`, nunca `assets/contenido/`) y **no versionada** (`.gitignore`). Medido en el APK: 29 272 B (DE, 160 verbos) + 58 540 B (EN, 339) = **87,8 KB** sin comprimir, en las dos variantes; `assets/contenido/` no tiene nada de verbos.
  - **Lectura:** `datos/VerbosReferencia.kt` con `VerboReferencia`, la función **pura** `parsearVerbosReferencia(texto)` y `cargarVerbosReferenciaDesdeAssets(context, idioma)` (vacío si no hay archivo o no se puede leer). **Perezoso:** `BibliotecaScreen` recibe `verbosReferencia: (Idioma) -> List<VerboReferencia>` y lo pide **solo al mirar la vista Verbos**; `MainActivity` guarda el resultado por idioma. De paso, las entradas de la vista Verbos ya **no se arman mientras se mira el léxico** (antes se calculaban igual).
  - **Fusión:** `entradasVerbo(idioma, referencia)` mete la referencia en la misma fusión por infinitivo (clave: minúsculas y, en inglés, sin el «to » de delante). Un `EntradaVerbo` por infinitivo, con `propio`, `colocaciones` y `referencia`. **Con propio manda su traducción** (la de referencia es el relleno cuando no hay propio); **la clase del propio o del núcleo manda** y la de referencia solo rellena; el régimen se toma del propio, del núcleo o, por último, de la referencia. **La estrella no cambia:** está si hay `propio`, y si no vive en cada colocación; un verbo de solo referencia tiene glosa y línea extra y **no tiene estrella**. Entra en el mismo orden alfabético, sin sección aparte. La búsqueda mira también la glosa de referencia y la **`raiz`** (que viaja en el asset).
  - **Los dos desacuerdos de clase no se tocaron:** `hören` (núcleo `mit-Praposition`, referencia `simple`) y `deal with` (`phrasal-inseparable` como anotación, `verbo-preposicion` como referencia): manda el núcleo/anotación, sin guarda que falle (un test los documenta).
  - **Entradas de la vista, medidas con los assets reales** (antes → después): **DE 470 → 585** (+115 = 160 − 45 fusionadas) y **EN 559 → 819** (+260 = 339 − 79 fusionadas): **+375 en total = 499 − 124**, exactamente la cuenta del prompt. Los tests fijan los seis números (antes, colisiones, después) por idioma y comprueban que la referencia no crea ningún infinitivo repetido.
  - **Dónde el prompt no cerraba (decisión mía): `deal with`.** La colisión que el prompt cuenta como «`base` de una entrada de anotación» no se ve desde la app: el ítem de la ficha es `to deal with something` y su `base` (`deal with`) es un campo de trabajo que no viaja. Sin hacer nada, `to deal with` (referencia) y `to deal with something` (ficha) habrían salido en dos filas, que es justo lo que la tarea 2 quiere evitar. Agregué **una segunda clave, solo en inglés y solo cuando la primera no encuentra nada**: el infinitivo del ítem sin el objeto de relleno final (`something`, `someone`, `somebody`, `oneself`, `yourself`). Medido contra los 339 verbos de referencia: **solo `to deal with` se activa**, ninguna otra referencia coincide por esa vía. La fila resultante es `to deal with something` (el ítem manda), con su clase.
  - **Hallazgo, no de esta ronda:** en inglés **7 infinitivos ya salían dos veces antes de la referencia** (`change`, `fact-check`, `help`, `queue`, `rent`, `taste`, `trust`: un verbo propio y un núcleo de colocación con el mismo infinitivo, uno con «to » y otro sin él). Es el mismo defecto que esta ronda cierra para la referencia, pero ya estaba. **No lo arreglé** (el prompt pedía fusionar la tercera fuente, no tocar la fusión entre las dos primeras); si la referencia coincide con uno de ellos se cuelga de la última de las dos filas. El test fija que la referencia no suma duplicados nuevos y deja registrados esos siete. Si Fer quiere la fusión también ahí, es normalizar el «to » en la clave de `entradasVerbo`.
  - **Tests:** solo referencia (con glosa y sin estrella), referencia + núcleo (una vez, glosa de la referencia, colocaciones del núcleo), referencia + propio (una vez, **traducción del propio**), el `to` del inglés, `deal with`, el conteo real, los chips (derivados del contenido, ninguno vacío, ninguna entrada sin clase), el asset extraído (se lee el archivo y se comprueba que **no existan las claves** `nivelFuente`, `nota` ni `referencia`, y que sea exactamente lo marcado `referencia` en la tabla maestra, con su `raiz`), y en pantalla: la fila de referencia con glosa y régimen, sin estrella (una estrella por verbo propio), hallable por glosa y por raíz, pedida solo al entrar a Verbos, y la de mismo infinitivo que un propio, una sola vez.
- **Tarea 3 — `anioEnCurso` desde `fechaInicial`:** `anioEnCursoDe(fechaInicial)` (año **ISO**, en `semana/ResolutorSemana.kt`) reemplaza al segundo `LocalDate.now()`; un test fija la frontera (1-ene-2027 es la semana 53 de 2026) y el test de la app real ya no pasa el año a mano.
- **Tarea 4 — versión 0.4.4 (versionCode 19).**

**Tamaño del APK** (empaquetado limpio): **release 55 910 623 B** y **debug 63 003 776 B** (Ronda T: 55 890 727 y 62 964 036; **+19 896 y +39 740 B**: el JSON se comprime). **Tests:** 1 319 en debug y 358 en release, 0 fallas.

**Guía de testers en español — filas cambiadas:** (1) el **título**, «versión 0.4.3» → «versión 0.4.4»; (2) **Verbos en la Biblioteca**, con una frase corta nueva, justo después de «si el phrasal verb es separable.»: «Incluye verbos de consulta (A1–A2) que no salen en ninguna ficha: con traducción, sin estrella.» (el resto de la fila igual). No agregué «cada verbo sale una sola vez»: la duplicación nunca llegó a verse en una versión, así que no hay nada que avisar. Ninguna fila más.

**Lo que no pude comprobar sin un teléfono (y el prompt lo pedía):** cómo se lee una glosa de referencia al lado de una fila con colocaciones; si la falta de estrella en un verbo de consulta se entiende o parece un error (la fila de núcleo ya no tenía estrella, pero ahí las colocaciones de abajo sí); y si 375 entradas más vuelven incómoda la lista alfabética (el desplazamiento y la búsqueda con hasta ~820 filas (inglés) en una sola `LazyColumn`: la búsqueda se calcula una vez por lista, pero no medí la fluidez).

**Decisiones donde el prompt no cerraba:** (1) la segunda clave de `deal with` (arriba); (2) los 7 duplicados de inglés quedan como estaban; (3) la referencia nueva con el mismo infinitivo que un propio sin traducción propia **no** rellena la fila del propio (la fila del propio la dibuja `FilaBiblioteca`, que no sabe de la referencia; el prompt dice que la de referencia rellena «cuando no hay propio»); (4) el `base` de la referencia también se prueba como clave (sin diferencia hoy en los datos).

## 75. La Ronda U y el parche de Verbos se rompen entre sí; icono y splash resueltos como bitmap (Overview, 19-oct-2026)

> **Esta sección se reescribió el mismo día.** La primera versión (commit `a6465c72`) describía tres *vector drawables* verificados píxel a píxel, y estaba hecha sobre los `.svg` equivocados: eran una vectorización de los renders, no el arte. Lo que sigue reemplaza aquello; los tres `.xml` de arte que entraron con `a6465c72` se borran en este mismo commit. Lo del build rojo no cambió.

### El build está rojo en `main`, y ninguna de las dos conversaciones hizo nada mal

`778c577e` (Ronda U) y `62047119` (Verbos) se escribieron sobre la misma base, sin verse. Code corrió su suite sobre `778c577e` —1319 tests en debug, 0 fallas— y Verbos quitó después la entrada de referencia `to deal with`, que es exactamente lo que tres de esos tests fijan. **El prompt 43 le prohibía a Verbos tocar `app/` y Code no podía saber que la entrada iba a desaparecer.** El riesgo que se venía avisando desde la Ronda S (dos conversaciones sobre el mismo clon) se materializó acá, y no por descuido de nadie.

En `app/src/test/java/io/github/percati/lenguapp/presentacion/VerbosReferenciaTest.kt`:

| línea | qué fija | estado |
|---|---|---|
| 86 | `assertEquals(339, referencia(Idioma.EN).size)` | **rojo** — ahora son 338 |
| 135 | `deal with se fusiona con su anotacion…`, que hace `.first { it.item == "to deal with" }` | **rojo** — `NoSuchElementException` |
| 233 | `ESPERADO_EN = Esperado(559, 79, 819)` | **rojo en `colisiones`** — ahora son 78 |
| 222 | `los dos desacuerdos de clase…`, que busca `single { contains("deal with") }` | pasa, pero quedó **vacío**: ya no hay referencia con la que desacordar |

**El total de la vista no se mueve:** quitar una entrada que además fusionaba resta una referencia y una colisión a la vez, así que inglés sigue en 819 (559 + 338 − 78) y alemán en 585. Lo único que cambió son los dos números intermedios.

**Y hay una consecuencia de diseño:** `OBJETO_FINAL_EN` (la segunda clave de fusión que quita `something`/`someone` del final) se agregó en la Ronda U **para un solo caso**, `to deal with`, y Code lo dijo así: «la probé contra los 339 verbos de referencia y solo `to deal with` coincide». Ese caso ya no existe, así que hoy es una rama sin ningún caso vivo. No es dañina —es un fallback detrás de dos claves que sí aciertan— pero no puede quedar sin un test que la ejerza con datos armados: en B1 va a volver a haber `base` que difieran del ítem de la ficha.

### El arte: lo que llegó y lo que se podía hacer con ello

Llegaron dos tandas de archivos. La segunda, que es la buena según Fer, **no son SVG**: son tres PNG metidos dentro de una etiqueta `<svg>` con un solo `<image>` y los datos en base64. Cero geometría vectorial. Y no son assets, son los renders de presentación:

| archivo | qué es de verdad |
|---|---|
| `icono.svg` | PNG de 1254×1254 con un **render de un icono**: un squircle azul de 553×554 px flotando sobre fondo gris claro con sombra proyectada. El arte ocupa el 44 % del archivo |
| `splash_1.svg` | PNG de 941×1672: el render **a pantalla completa** sobre papel blanco, solo la L |
| `splash_2.svg` | PNG de 941×1672: el render **a pantalla completa** sobre azul noche, con las trece marcas de puntuación en órbita |

(Ojo: respecto de la primera tanda, `splash_1` y `splash_2` quedaron **intercambiados** — ahora el 1 es el blanco y el 2 el azul.)

**Por qué el vector queda descartado.** El arte vive de degradados, sombreado suave y textura de papel. `VectorDrawable` soporta degradados lineales y radiales, pero no este tipo de iluminación; la primera tanda de `.svg` era precisamente un intento de vectorizar estos renders, y es lo que Fer vio mal. **La decisión es bitmap**, que para un icono de launcher es práctica normal de Android.

### Lo que se midió, y por qué el render no se puede usar tal cual

- **El icono adaptativo recorta.** Si el squircle del render se toma como el lienzo de 108 dp, la L queda con un **radio máximo de 46.3 dp sobre una zona segura de 36**: se recortaría en cualquier launcher de máscara circular. El mockup está dibujado como un icono terminado, y un icono adaptativo necesita el arte más chico porque la máscara la pone el sistema. **La L tiene que ir al 78 % de lo que muestra el mockup**, y el squircle y su sombra se descartan: los dibuja el launcher.
- **El icono del splash se recorta a un círculo** de 192 dp sobre un lienzo de 288, y **el splash no admite imagen de fondo**, solo un color sólido. Así que de los renders a pantalla completa lo único que llega es la L centrada.
- **Las marcas en órbita no entran a un precio razonable.** Medido sobre `splash_2`: la L sola tiene radio 249 px y el conjunto con las trece marcas, 361. Llevar el conjunto al círculo útil deja la L en **91 × 103 dp**; llevar solo la L la deja en **132 × 149 dp**. La órbita cuesta el 30 % del tamaño del glifo, que es lo único que tiene que leerse en un arranque de medio segundo.
- **La paleta es para fondo oscuro.** Contraste del glifo contra los dos valores de `fondo_papel`: el asta (`#B4D8F4`) **1.38** en claro contra 12.05 en oscuro; la cola (`#F4D6A9`) **1.29** contra 12.88. Con `fondo_papel` el splash en modo claro mostraría una L casi invisible. Por eso lleva **`#253C52` fijo** en los dos temas: es el azul del render del icono (media medida del squircle `[48 66 85]`, del fondo de `splash_2` `[27 48 72]`, con `#253C52` = `[37 60 82]` entre los dos), y hace que el splash y el icono del launcher sean la misma marca.

### Los assets que van en este commit

**La L es una sola, la de `splash_2`**, en el icono y en el splash. Los dos renders traen L distintas —la del squircle es más cuadrada, 299×363; la del splash tiene la cola larga, 343×386— y conviene que la marca sea una. Recortada del fondo con un *matte* por distancia al navy y el color desmultiplicado; comprobado componiéndola sobre `#253C52` y sobre blanco: sobre el navy no deja halo, sobre blanco sí un fleco gris tenue. **Eso significa que esta L solo puede mostrarse sobre el navy**, que es el único sitio donde se usa.

| archivo | lienzo | la L mide | escala desde la fuente |
|---|---|---|---|
| `mipmap-xxxhdpi/ic_launcher_foreground.webp` | 432 px (108 dp) | 175×197 px, radio 33 de 36 | 0.511, reducción |
| `mipmap-xxhdpi/ic_launcher_foreground.webp` | 324 px (108 dp) | 132×148 px | 0.383, reducción |
| `drawable-xxhdpi/splash_l.webp` | 864 px (288 dp) | 351×395 px, radio 88 de 96 | 1.023, prácticamente nativo |
| `drawable/ic_launcher_background.xml` | 108 dp | — | `#253C52` plano, vector |

WebP sin pérdida porque es un 35 % más chico que PNG con el mismo resultado (119 KB contra 187 KB en total). `minSdk = 26`, así que los iconos adaptativos son nativos y **no hace falta ningún respaldo de PNG por densidad**.

**El techo de calidad y de dónde viene.** El splash sale a xxhdpi (864 px) porque a esa densidad la escala es 1.023 —resolución nativa, sin detalle inventado—; en un teléfono xxxhdpi el sistema lo amplía 1.33× en tiempo de ejecución, que no es peor que ampliarlo acá. La fuente son 343×386 px útiles de un render de 941×1672, y eso es todo lo que hay. **Si alguna vez se quiere arte sin ese techo, lo que hace falta no es «un SVG» sino el glifo a 1024 px o más con fondo transparente**, o el archivo vectorial de origen si existe.

### Lo del splash aleatorio: no se puede, y además no hay dos

El tema del splash lo resuelve el sistema **antes de que corra una sola línea de la app** —por eso existe `postSplashScreenTheme`, al que `installSplashScreen()` salta *después*—, así que no hay dónde elegir. El rodeo conocido (varios `activity-alias` y `setComponentEnabledSetting`) persiste estado fuera de la app y hace parpadear el icono del launcher: no va en este proyecto. Una segunda pantalla de carga dibujada por Compose sí se podría alternar, pero agrega demora a una app cuyo punto es abrirla y ver la semana.

Y por lo de arriba **no hay dos splashes, hay un glifo y dos composiciones a pantalla completa**, y las composiciones son justo lo que el splash no puede mostrar. El render blanco y el de la órbita tienen su lugar como ilustración —la cabecera de «Acerca de» es donde se los mira de verdad y no cuestan arranque—, no como icono. Si alguna vez hace falta variedad en el splash, el camino honesto es derivarla de la **semana ISO**, el único dato que la app ya lee, y no de un azar; pero eso necesita primero una pantalla de carga propia, que hoy no conviene.

### Deuda menor, anotada

`docs/guia-testers-lenguapp.pdf` y `docs/guia-testers-lenguapp-es.pdf` son **el mismo documento con dos nombres** (FALTANTES 1537 lo documenta). En la Ronda U el primero quedó en 0.4.3 un día entero mientras el segundo ya estaba en 0.4.4. Conviene dejar uno solo; por ahora se regeneran los dos.

## 76. Code Ronda V — ✅ implementada (Code, 19-oct-2026), versión 0.4.5

Cinco commits sobre `1059dfe3` (el parche de Overview con los cuatro assets estaba aplicado). Contenido, `proyecto/contenido/`, `proyecto/tools/` y los textos del tutorial **sin tocar**; **manifest sin `INTERNET` ni permisos nuevos** en las dos variantes (mismos manifiestos fusionados de debug y release: `android:icon`, `android:roundIcon`, versionCode 20, versionName 0.4.5). **Lo visual de esta ronda (el icono en el launcher y el splash en los dos temas) no se vio: ver abajo.**

- **Tarea 1 — el build.** `VerbosReferenciaTest` tras quitar `to deal with` de la tabla. **Medido con los assets reales:** referencias **DE 160, EN 338**; colisiones **DE 45, EN 78**; entradas de la vista **DE 470 → 585, EN 559 → 819**. Los tres números que el prompt pedía (338, 78, 819) coinciden, y el total no se movió (EN 559 + 338 − 78 = 819; DE 585). Los dos tests de `deal with` se reescribieron **con datos armados a mano**, sin buscar otro verbo real: (a) una referencia inventada `to blorp with` contra un ítem inventado `to blorp with something` → una sola fila, manda la clase del ítem, y lo mismo con los cinco objetos de relleno (`something`, `someone`, `somebody`, `oneself`, `yourself`); (b) la segunda clave no inventa fusiones (una referencia exacta acierta por la primera, una ajena es fila nueva, y en alemán no existe); (c) una referencia inventada que desacuerda en clase con un núcleo real no le cambia la clase. **El desacuerdo de `hören` sigue contra el dato real, y ahora el test es estricto** (antes tenía un `if` que lo dejaba pasar en silencio si el dato cambiaba): el núcleo sin propio dice `mit-Praposition`, la referencia `simple`, y la fila fusionada conserva `mit-Praposition`.
- **Tarea 2 — `OBJETO_FINAL_EN`.** No se sacó. El KDoc dice **«HOY NO TIENE NINGÚN CASO VIVO»**, por qué se agregó (`to deal with`), por qué se queda (tercer intento detrás de dos claves que aciertan; en B1 volverá a haber `base` distintos del ítem) y dónde se ejerce (`VerbosReferenciaTest`, con datos armados). También se corrigieron el comentario del sitio de uso y el de `fusionarReferencia`, que nombraban `deal with` como desacuerdo vivo.
- **Tarea 3 — el icono adaptativo.** `res/mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml` (`<adaptive-icon>` con `@drawable/ic_launcher_background` y `@mipmap/ic_launcher_foreground`) y `android:icon` / `android:roundIcon` en `<application>`. **Sin** PNG de respaldo, **sin** densidades nuevas, **sin** `<monochrome>`: el hueco queda documentado en el comentario de los dos XML («HUECO DOCUMENTADO») y un test lo exige. `IconoAdaptativoTest` fija: manifest, los dos XML, fondo `#253C52`, solo existen `mipmap-anydpi-v26`, `-xxhdpi` y `-xxxhdpi`, ningún `ic_launcher` bitmap suelto y `minSdk = 26`. **Si falta alguna densidad** (el prompt pedía decirlo en vez de generarla): con `anydpi-v26` y minSdk 26 no hace falta ninguna para que el icono se vea; xxhdpi/xxxhdpi cubren los teléfonos actuales y el sistema reduce para los de abajo. Lo único que se podría discutir es **xhdpi** para pantallas de 320 dpi, que reducirían desde xxhdpi (324 px → 216 px, con calidad perfectamente buena): no la generé.
- **Tarea 4 — la pantalla de carga.** `windowSplashScreenBackground` → **`@color/fondo_splash` = `#253C52`** (nombre propio, en `values/colors.xml`, **no redefinido en `values-night`**), `windowSplashScreenAnimatedIcon` → **`@drawable/splash_l`**. Borrado `splash_vacio.xml`. **No** hay `windowSplashScreenIconBackgroundColor` ni animación. `SplashRecursosTest` se reescribió (no se borró): el estilo de la splash **no contiene `fondo_papel`** (con el motivo en el mensaje: contraste 1.38 del asta y 1.29 de la cola sobre el papel claro), el color es el mismo en claro y en oscuro, el icono es `splash_l`, no existe `IconBackgroundColor` y el placeholder ya no está. El comentario viejo de `themes.xml` («cuando llegue el icono, reemplazar…») se reescribió con lo que no se puede (imagen de fondo, alternar el splash al azar por `activity-alias` o `setComponentEnabledSetting`).
- **Tarea 5 — las dos composiciones a «Acerca de»: solo propuesta, ningún drawable agregado.** Ver «Propuesta» abajo.
- **Tarea 6 — versión 0.4.5 (versionCode 20).**

**Propuesta para la tarea 5 (lo decide Fer).** La cabecera de «Acerca de» (`AcercaDeScreen.kt`) es una fila fija con «< Volver» y el título, y debajo una columna que **se desplaza** y empieza con el nombre «LenguApp» y la línea fija. **El lugar razonable es el comienzo de esa columna que se desplaza, encima del nombre**: una ilustración de ~160 dp de alto, en el ancho de la columna (328 dp en un teléfono de 360), con `ContentScale.Crop` y esquinas redondeadas del tema. Como se desplaza con el contenido, no le quita pantalla a los textos de abajo ni empuja el título; sí hace que la primera pantalla de «Acerca de» sea más ilustración que texto (160 dp son un cuarto de un teléfono de 640 dp de alto), y es una decisión de Fer si eso le gusta. Lo que habría que resolver al hacerlo: (1) **los dos renders encajan con los dos temas**: el de papel blanco para el tema claro y el azul con las marcas en órbita para el oscuro (elegido por el tema, no al azar), que además evita un bloque blanco brillante en un tema oscuro; (2) **peso:** cada render es un bitmap de fondo completo; a 328 × 160 dp hace falta recortarlo ya al aspecto 2:1 (no cargar la composición vertical entera) y guardarlo en WebP, del orden de decenas de KB por tema; (3) **texto alternativo:** decorativa (`contentDescription = null`) o con una cadena nueva en `TextosInterfaz` (regla P7: seis idiomas); la decorativa es la honesta, el nombre «LenguApp» ya está debajo; (4) **tests:** el de «Acerca de» en los seis idiomas busca textos y no se ve afectado, pero el de «Voces» entre «Licencias» y la versión mide posiciones y seguirá bien. No agregué nada.

**Tamaño del APK** (empaquetado limpio): **release 56 031 894 B** y **debug 63 125 549 B** (Ronda U: 55 910 623 y 63 003 776; **+121 271 y +121 773 B**, o sea los tres bitmaps: 22 632 + 13 482 + 83 422 = 119 536 B, más los XML). **Tests:** 1 325 en debug y 364 en release, 0 fallas.

**Guía de testers en español:** solo el **número de versión del título**, «versión 0.4.4» → «versión 0.4.5» (lo aviso para que las otras cinco guías y los seis `.pdf` los rehaga Overview). **No toqué ninguna fila.** No propongo una: nadie reporta «la app tiene icono». Si Fer quiere una, la única candidata sería avisar al tester de que el icono y el splash son nuevos, y la dejaría fuera por el costo en carillas.

**Lo que no pude comprobar sin un teléfono (el APK de debug va justamente para eso):** (1) el icono en el launcher bajo **cada máscara** real (círculo, squircle, cuadrado, gota) y la L sin recorte; (2) el splash **en los dos temas**: que el fondo `#253C52` sea el mismo, que la L se vea entera dentro del círculo de 192 dp y su tamaño; (3) que no se vea un **halo claro** alrededor de la L sobre el azul: al abrir `splash_l.webp` sobre fondo blanco se nota un borde claro muy fino en el contorno del glifo (el render original traía un borde); sobre `#253C52` podría verse como un filo luminoso; (4) en Android 11 y anteriores el splash sale por el camino de compatibilidad de `core-splashscreen` (no por el nativo de Android 12) y la L puede verse distinta de tamaño; (5) que el icono temático de Android 13 (sin capa monocromática) no quede feo: el sistema usa el adaptativo tal cual.

**Decisiones donde el prompt no cerraba:** (1) el test de `hören` pasa a estricto; (2) `SplashRecursosTest` se reescribió entero porque sus tres tests describían el placeholder; (3) la Tarea 5 es solo la propuesta de arriba; (4) las capas `<monochrome>` y las densidades de más, ni generadas ni preparadas.

## 77. Verificación de la Ronda V, el halo descartado y un test que fija un pendiente (Overview, 20-oct-2026)

Verificación independiente de los cinco commits de la Ronda V, más las seis guías a 0.4.5.

### Lo que está bien

**Los tres números de la tarea 1 coinciden con mi propia medición**, hechos sobre los assets reales: DE 160 referencias / 45 colisiones / 470 → 585, EN 338 / 78 / 559 → 819. El build está destrabado.

**Los tests reescritos son del tipo correcto.** Los dos de `deal with` pasaron a datos armados (`to blorp with` contra el ítem `to blorp with something`, y una referencia que desacuerda en clase con su núcleo), que es lo que prueba el mecanismo sin depender de qué verbos haya en la tabla este mes. Y el de `hören` dejó de tener el `if` que lo hacía pasar en silencio si el dato cambiaba: ahora exige que el núcleo sea puro, que su clase sea `mit-Praposition`, que la referencia diga `simple`, que al fusionar mande la del núcleo y que las colocaciones no se pierdan. Es más estricto que antes de la ronda.

**El splash y el icono están cableados como se pidió.** `fondo_splash` (`#253C52`) existe solo en `values/colors.xml` y `Theme.LenguApp.Splash` solo en `values/themes.xml`, así que es el mismo en claro y en oscuro por construcción, no por repetición. Sin `windowSplashScreenIconBackgroundColor`, sin animación, `splash_vacio.xml` borrado. El icono en `mipmap-anydpi-v26` con `icon` y `roundIcon`, sin respaldo heredado y sin densidades de más.

**El resto, en verde:** 606 fichas / 0 problemas, R1–R11 con 0 problemas en 240 fichas A2/B1, `diff -rq` 0 diferencias, `aplicar_verbos.py` 0 packs con diferencias y 0 errores, `--autotest` 2 casos 0 fallos, índice de audio al día, `INTERNET` = 1, `versionName` 0.4.5 / `versionCode` 20.

### El halo del splash: descartado, medido

Code marcó como no comprobable «un posible halo claro fino alrededor de la L sobre el azul», que había visto al abrir `splash_l.webp` sobre blanco. **No hay halo sobre el azul, y es medible.** Compuesto el asset sobre `#253C52` exacto, el anillo de 3 px inmediatamente por fuera del glifo da **`[37.1 60.0 82.0]`** contra el `[37 60 82]` del fondo: una desviación por debajo de 0.1/255.

Lo que Code vio es lo que FALTANTES 75 ya documenta: el *matte* se hizo por distancia al navy y el color se desmultiplicó, así que los píxeles del borde solo reconstruyen bien sobre ese fondo. **Sobre blanco deja un fleco gris tenue y sobre el navy no deja nada**, y el navy es el único sitio donde el asset se usa. No hay nada que arreglar; lo que no hay que hacer es poner esta L sobre cualquier otro fondo.

### Un test que fija un pendiente en vez de vigilar una regresión

`IconoAdaptativoTest` tiene dos afirmaciones que, en los dos `.xml` del icono, exigen que **el hueco del monocromo siga abierto**:

```kotlin
assertFalse("$nombre: sin <monochrome> (hueco documentado, lo decide Fer)", x.contains("<monochrome"))
assertTrue("$nombre: el hueco del monocromo esta documentado", x.contains("HUECO DOCUMENTADO"))
```

El día que Fer decida el arte monocromo, quien lo agregue se encuentra con **dos tests rojos cuyos mensajes dicen que la capa no va**, y el segundo además obliga a dejar el comentario tal cual, así que tampoco se puede limpiar. Un test sirve para impedir una regresión, no para impedir que se complete algo que está anotado como pendiente; el comentario de los dos `.xml` ya documenta el hueco, que es donde corresponde. **Las dos afirmaciones se sacan** en la próxima ronda que toque `app/`; no justifica una ronda propia.

La afirmación del conjunto de carpetas `mipmap` tiene la misma forma pero es defendible —vigila que no aparezcan densidades sueltas ni un respaldo heredado—; lo único que convendría es que el mensaje diga «si agregás una densidad, actualizá esta lista» en vez de dar a entender que está prohibido.

### Lo que queda sin comprobar, y cuál importa

De las cuatro cosas que Code no pudo mirar sin teléfono, tres las contesta el APK en cuanto Fer lo instale (el icono bajo su launcher, el splash en los dos temas, el tamaño de la L en el círculo) y el halo queda contestado arriba. Queda una abierta de verdad:

**El splash en Android 8 a 11 (API 26–30), que sale por el camino de compatibilidad de `core-splashscreen`.** La documentación de Android no especifica cómo se dibuja el icono en ese camino —ni si se enmascara, ni si se escala—, y recomienda explícitamente comprobarlo en un dispositivo. Lo que sí se puede decir es que **el arte es seguro bajo las dos lecturas de la especificación**: los docs dan 288 dp de lienzo con 192 de círculo útil para un icono estático y 432 con 288 visible para un AVD, y las dos dejan el arte en los **dos tercios interiores** del lienzo, que es el invariante que `splash_l` respeta (radio 88 de 96). Si en un emulador de API 30 se viera mal igual, el arreglo es partir el tema del splash en `values-v31`, no tocar el asset. **Es un rato de emulador, no una ronda de trabajo.**

### Deudas, con los números puestos al día

Varias cifras que se venían repitiendo estaban viejas o no eran deudas:

- **549 `redemittel` sin `categoriasUso`** de 6172, no 273: la cifra vieja era de antes del contenido de 2027.
- **Los `*x*` sin « » NO son una deuda.** Son 31 601 en los assets de hoy, pero FALTANTES 780 y 1056 ya cierran la discusión: en este repo `*x*` es también énfasis en el idioma de destino, así que la mayoría no son fragmentos extranjeros, y la decisión tomada fue dejarlo. Sacarlo de la lista de pendientes.
- **`ein Hobby nachgehen` sigue mal, y son cinco apariciones**, no una: `nachgehen` rige dativo, así que va `einem Hobby nachgehen`. Está mal en `de-B2-2027-T12-1`, `-2`, `-3`, `-4` y en `contenido/verbos/de.json`; la forma correcta ya existe en otros tres sitios, así que el contenido se contradice a sí mismo. Es trabajo de Verbos o de Packs, y es de minutos.
- **Falta el icono de 512×512 de la tienda** y las capturas. Ahora es barato: el render de 1254×1254 sirve de origen.
- **Nadie revisó fr / it / pt** de las 498 entradas de referencia de verbos, ni de buena parte del contenido.
- `docs/guia-testers-lenguapp.pdf` sigue duplicando a `-es.pdf` (ver FALTANTES 75 y 1537).
- **`roundIcon` y el icono adaptativo:** `mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml` son **byte a byte idénticos**, lo que es un riesgo de desincronización barato (se cambia uno y se olvida el otro; por ahora el test mira los dos). Antes de simplificarlo hay que comprobar si algún launcher todavía necesita `roundIcon` cuando ya hay icono adaptativo — no lo verifiqué, así que queda anotado y no propuesto.

### El proceso, que ya costó un build rojo

El choque entre la Ronda U y el parche de Verbos (FALTANTES 75) no fue un descuido: las dos conversaciones trabajaron sobre el mismo clon, cada una respetando su alcance, y el resultado fue rojo. **Mientras sigan compartiendo carpeta va a volver a pasar**, y la próxima vez puede no ser un test que falla ruidosamente sino contenido que se pisa en silencio. Lo que hace falta es una carpeta por conversación, o una conversación por vez. Es la tercera vez que queda anotado.
