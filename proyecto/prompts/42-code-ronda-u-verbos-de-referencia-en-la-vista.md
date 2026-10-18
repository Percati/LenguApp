# Ronda U — la vista Verbos toma la segunda fuente, y la fusión por infinitivo

Escrito por Overview el 18-oct-2026 sobre `cc7d949a` (ya en `origin/main`: la Ronda T y las dos tandas de Verbos están aplicadas).

**Alcance:** `app/build.gradle.kts`, `datos/` (un cargador nuevo), `presentacion/Verbos.kt`, `ui/BibliotecaScreen.kt` y los tests. **Nada de contenido, nada de `proyecto/tools/`, nada de assets versionados, ningún permiso nuevo.** Y ningún texto de paso del tutorial: Fer sigue mirándolos.

Esta ronda tiene **una sola tarea**, pero es la que desbloquea 499 entradas de contenido que ya están escritas y hoy no se ven.

## El estado, medido

`proyecto/contenido/verbos/{de,en}.json` tiene hoy **1164 entradas**, de las cuales **499 son de referencia** (`referencia: true`): DE 160, EN 339. Son verbos de consulta de A1 y A2 con traducción a los otros cinco idiomas, clase y régimen. **No anotan ningún ítem del contenido**, y por eso:

- `componer.py` **no menciona la tabla de verbos**: la tabla es entrada de `aplicar_verbos.py`, que escribe `claseVerbo` / `reccion` / `nucleoVerbal` **dentro de los packs**. Lo que llega a `assets/contenido/` son fichas, nunca la tabla.
- `verbosDe()` construye la vista desde `ItemBiblioteca`, o sea desde los ítems de las fichas. **Una entrada de referencia no existe para la app.**

Verificado: las 499 entradas no están en `assets/` de ninguna forma, y `diff -rq` entre `componer.py` y `assets/contenido` da 0 diferencias con ellas en el repo.

## Tarea 1 — la segunda fuente

### Cómo llega a la app: extracción en Gradle, no un archivo versionado

**Hay precedente exacto en tu propio `build.gradle.kts`:** `extraerTopicNombres` lee `banco.json` y escribe **solo la tabla que la app necesita** en `assets/temas/topic-nombres.json` en cada build, sin versionar la copia. Hacé lo mismo.

- **Una tarea de Gradle que extrae, no un `Copy`.** El archivo completo pesa 319 KB y lleva campos que **no pueden llegar a la app**: `nivelFuente` y `nota` son campos de trabajo (decidido en el prompt 40 y registrado en FALTANTES 72), y las 665 entradas de anotación no tienen traducción propia y no van a la vista. Extraído queda en **92 KB**. Un `Copy` del archivo entero pondría `nivelFuente` físicamente dentro del APK para después ignorarlo, y eso incumple la decisión en silencio.
- **`assets/verbos/referencia-de.json` y `referencia-en.json`.** Carpeta propia, y **nunca** `assets/contenido/`: `CargadorContenido` hace `assets.list("contenido").filter { endsWith(".json") }` y parsea **todos** como ficha o semana especial, así que un archivo con otra forma ahí **rompe el cargador**, y además aparecería como diferencia en el `diff -rq` contra `componer.py` y en la tarea `verificarAssetsContenido`.
- **Qué entra en cada entrada extraída, y nada más:** `item`, `base`, `claseVerbo`, `traducciones`, y `raiz` y `reccion` cuando están. **`raiz` tiene que viajar**: es lo que hace que buscar «stellen» traiga la familia entera, y es la función que Fer más usa de esta vista. `nivelFuente` y `nota` **no viajan**.
- La tarea se engancha donde ya están enganchadas `extraerTopicNombres` y `copiarCategoriasUsoAssets`, con `inputs`/`outputs` declarados para que no corra de más.

### Cómo se lee

Un cargador en `datos/`, con la forma de `CategoriasUso.kt` / `PlanillaTextos.kt`: abre el asset, parsea, y **la función pura de parseo separada** para que los tests JVM la ejerciten sin `Context`. Perezoso: solo cuando se entra a la vista Verbos, no al arrancar la app.

### Cómo se muestra

Una entrada de referencia es **un tercer tipo** de fila, y hay que decidir bien qué puede hacer:

- **Tiene traducción propia** (por eso se escribió) y la línea extra con clase y régimen, igual que un verbo propio.
- **NO es marcable.** Y el motivo es el mismo que ya documentaste para los núcleos puros: la clave de Guardados es el texto del ítem, y un verbo de referencia **no es un ítem de ninguna ficha**, así que daría una clave que ningún contenido puede volver a producir. Si la estrella apareciera ahí, un guardado quedaría colgado el día que el verbo entre a un pack y se le quite `referencia`. **No pongas estrella.**
- **No tiene semana ni nivel.** La vista ya dejó de mostrar el nivel en la Ronda T, así que eso ya está. Si en algún lugar se muestra la semana del verbo, acá no hay ninguna: que no se dibuje la línea, no que quede un espacio.
- **Entra al mismo orden alfabético** por infinitivo, mezclada con las otras, no en una sección aparte. El usuario busca un verbo, no una procedencia.

## Tarea 2 — la fusión por infinitivo, que ahora es obligatoria

`EntradaVerbo` **ya fusiona** núcleo y verbo propio cuando el infinitivo coincide exactamente — lo dice tu propio comentario de la Ronda J. Lo que falta es meter la tercera fuente en esa misma fusión.

**Y esto no es cosmético, está medido.** Comparando por infinitivo, normalizando el «to » del inglés:

| colisión | DE | EN |
|---|---|---|
| entrada de referencia cuyo infinitivo es un núcleo de colocación | 45 | 78 |
| entrada de referencia cuyo infinitivo es el `base` de una entrada de anotación | 0 | 1 (`deal with`) |

**124 verbos mostrarían dos filas** en cuanto la segunda fuente exista: una con glosa y sin colocaciones, otra con colocaciones y sin glosa. `aufstehen`, `kommen`, `machen`, `nehmen`, `take`, `make`, `go`, `put`. Son justo los verbos más buscados.

**Consecuencia de calendario, y es la razón por la que esta ronda tiene una sola tarea:** la fusión tiene que entrar **en el mismo commit que la segunda fuente**, no en una ronda posterior. Hoy la duplicación es invisible porque la fuente no existe; el día que exista, se ve en 124 verbos de golpe.

Cómo queda la fusión, que es lo natural con lo que ya tenés:

- **Un solo `EntradaVerbo` por infinitivo**, con `propio` (el ítem marcable, si lo hay), `colocaciones` (las del núcleo) y la **glosa de referencia** cuando el propio no la tiene.
- **Si hay verbo propio, su traducción manda**: viene del contenido, está revisada y es la que el usuario ya vio en una ficha. La de referencia es el relleno para cuando no hay propio.
- **La estrella sigue la regla de siempre:** está si hay `propio`, y si no, vive en cada colocación. Que la entrada tenga glosa de referencia **no** la vuelve marcable.
- **`clases`:** si el propio o el núcleo declaran clase, esa manda; la de referencia solo rellena. Hay **dos desacuerdos** hoy y los dos están anotados a propósito, así que el código no debe «arreglarlos»: el núcleo `hören` es `mit-Praposition` y su referencia dice `simple`; `deal with` es `phrasal-inseparable` como anotación y `verbo-preposicion` como referencia. Verbos los está mirando por separado — **no los resuelvas vos ni agregues una guarda que falle por ellos.**

Tests, y acá quiero varios porque es el punto donde esto se puede romper sin que se note:

1. Un verbo que es **solo referencia** aparece, con glosa y **sin estrella**.
2. Un verbo que es **referencia + núcleo** aparece **una vez**, con la glosa de la referencia y las colocaciones del núcleo.
3. Un verbo que es **referencia + propio** aparece **una vez**, con la traducción **del propio**, no la de referencia.
4. `nivelFuente` y `nota` **no están** en el asset extraído. Esto es un test del asset, no del modelo: leelo y comprobá que las claves no existan.
5. El conteo total de entradas de la vista **no crece en 499** sino en 499 − 124, por la fusión. Fijá el número que te dé con los datos reales, para que una regresión en la fusión lo mueva.
6. Los chips de clase siguen derivándose del contenido y **no quedan vacíos** (P3).

## Tarea 3 — `anioEnCurso` desde `fechaInicial`

Quedó pendiente de la Ronda T y es una línea: `anioEnCurso` se deriva de `fechaInicial` en vez de leer el reloj por segunda vez. Mismo valor, un solo punto de lectura de la fecha, y los tests pueden fijar el año sin tocar el reloj.

## Tarea 4 — versión, guía y FALTANTES

- `versionName` **0.4.4**, `versionCode` **19**.
- La guía de testers en español: el título a 0.4.4 y **una fila sola** — «Verbos en la Biblioteca»: ahora hay verbos de consulta de A1 y A2 que no vienen de ninguna ficha, con traducción, y los verbos que antes salían dos veces salen una. Pasame el texto exacto que uses. **Las otras cinco guías y los seis `.pdf` los hago yo** — y tenelo en cuenta: cada fila nueva empuja los seis PDF a tres carillas, así que **una fila, corta**.
- `FALTANTES.md`: la sección **74**. La 73 es mía y la escribo ahora.

## Qué no hacer

- No pongas el archivo en `assets/contenido/` ni lo versiones en `assets/`.
- No embarques `nivelFuente` ni `nota`.
- No le pongas estrella a una entrada de referencia.
- No resuelvas los dos desacuerdos de clase, ni agregues una guarda que falle por ellos.
- No metas la fusión en una ronda posterior a la segunda fuente.
- No toques `proyecto/contenido/` ni `proyecto/tools/`: los verbos los escribe otra conversación y vamos a chocar.
- No agregues `INTERNET` ni ningún permiso.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-u/`. En el informe: el número real de entradas de la vista antes y después, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono.
