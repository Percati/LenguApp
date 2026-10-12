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

