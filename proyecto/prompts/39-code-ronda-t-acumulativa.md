# Ronda T — foco tocable, Habilidades por semana, y el desafío en el idioma que se aprende

Escrito por Overview el 18-oct-2026 sobre la Ronda S (`cc59695c`). **Reemplaza al prompt 38**, que no llegó a ejecutarse: sus dos tareas están acá como 1 y 2, y se suman cuatro cosas nuevas del feedback de Fer.

**Alcance:** `ui/Tutorial.kt`, `ui/TutorialOverlay.kt`, `ui/TutorialPasos.kt`, `ui/AcercaDeScreen.kt`, `ui/ContenidoSemanalScreen.kt`, `ui/BibliotecaScreen.kt`, `presentacion/IndiceHabilidades.kt`, `presentacion/Verbos.kt`, `ui/TextosInterfaz.kt` y los tests. **Nada de contenido, nada de assets, nada de esquema, ningún permiso nuevo.** Y **ningún texto de paso del tutorial**: Fer todavía los está mirando.

**Aviso de un parche mío que va primero:** borré los **59 clips de audio sin referencia** (33 en inglés, 26 en alemán). Quedan 4043 alemanes y 4100 ingleses, el índice no cambia y tu `AudioInglesEnElReleaseTest` pide `> 4000`, así que sigue pasando. Si el parche no estuviera aplicado cuando arranques, avisá y no toques nada de audio.

## Tarea 1 — el elemento con foco se puede tocar

Autorizado por Fer. Hoy el recuadro se come **todos** los toques, incluidos los del elemento que ilumina, y eso deja dos pasos sin nada que hacer: el del encabezado del vocabulario, que tiene que decir «el encabezado abre la lista **cuando termines con esto**», y el del interruptor del desafío, que no se puede encender, así que el paso de la consigna se saltea casi siempre y el tour dice «Paso 1 de 1».

- El agujero **pasa los toques al elemento que ilumina**; **el resto de la pantalla sigue sin recibir nada**. Usá el **rectángulo recortado** de la tarea 2 de la Ronda S, el mismo con el que dibujás el agujero: si una parte del ancla no se ve, un toque ahí no debería llegarle.
- **El tour NO avanza solo** al tocar. El paso sigue necesitando «Siguiente».
- **Si la pantalla cambia debajo**, el foco sigue al ancla (`onGloballyPositioned` ya reregistra). Si el ancla desaparece, el paso **se saltea** con el mecanismo que existe. Un test.
- **Un campo `tocable` en `PasoTour`, falso por omisión** — no una lista a mano de «anclas que navegan», que envejece peor. Verdadero en **exactamente dos**: `tour-ficha-vocabulario` y `tour-desafio-interruptor`, con un test que compruebe que **solo** esos dos lo son.
- **No lo habilites en anclas que navegan a otra pantalla** (`acerca-de`, `ver-bienvenida`, las filas de Habilidades): un toque ahí dejaría el tour corriendo sobre una pantalla que no es la suya.
- Con el vocabulario, abrir la lista **mueve el ancla y crece el contenido debajo**: comprobá que el foco siga al encabezado y que el recorte de la Ronda S no deje un agujero colgado.

## Tarea 2 — las URL del bloque «Voces» en una pantalla angosta

Lo marcaste vos y no se comprobó. `https://www.kaggle.com/datasets/roholazandie/ryanspeech` son 52 caracteres **sin un solo espacio**: si el salto de línea no las parte, se cortan o se van de la pantalla, que es justo lo que Fer pidió eliminar en la Ronda P.

**Comprobalo primero** con un test a 360 dp y a 320 dp, midiendo que el bloque no se pase del ancho. Si ya salta bien, decilo y no toques nada. Si no: (1) que el salto parta la URL por caracteres; (2) oportunidades de corte invisibles después de cada `/`; (3) **último recurso**, acortar la URL mostrada. Lo que **no** se hace es desplazamiento horizontal ni truncar con puntos suspensivos: **una URL truncada no cumple la atribución de CC BY**, que es para lo que está ahí.

## Tarea 3 — Habilidades: orden por semana, y sin semanas futuras

Fer: el orden alfabético por título en el idioma que se aprende **está bien y le gusta, se queda como está por omisión**. Lo que falta es poder **ordenar por semana de aparición**, y que la lista **no muestre las semanas futuras**.

**Las dos cosas ya tienen los datos:** `construirIndiceHabilidades` recibe `semanaReferencia` y cada `FilaHabilidad` tiene `apariciones` con su número de semana.

- **Orden por semana:** por la **primera aparición** de cada habilidad. Un grupo de dos chips en la vista Habilidades — «Alfabético» (elegido por omisión) y «Por semana» —, con los mismos `OpcionFiltro`/`GrupoChips` que ya usa la pantalla. **Sin persistencia:** cada vez que se entra arranca en «Alfabético», igual que el selector de vistas (regla dura 4).
- **Sin semanas futuras:** se descartan las apariciones con `semana > semanaReferencia`, y una habilidad cuya **primera** aparición es futura **no aparece en la lista**.
- **Y acá está el caso que hay que decidir bien: el año.** `semanaReferencia` es la semana de la ficha que se está viendo, y la app permite moverse a otro año. La regla es: **si el año del índice es anterior al año en curso, se muestran todas las semanas** (una edición cerrada se consulta entera); **si es el año en curso, se corta en `semanaReferencia`**. No uses la fecha del sistema para nada más que derivar el año en curso — es un dato que sí se puede leer.
- Con las futuras descartadas, `fichaAAbrir` (que hoy es `firstOrNull { semana >= ref } ?: last()`) pasa a ser siempre **la última aparición pasada**. Dejalo explícito en el código, no como efecto colateral.
- **Lo que esto implica y hay que vivir con ello:** en la semana 2 de un año la lista tiene una o dos entradas. Es coherente con el «modo revista» y con que las ediciones anteriores se puedan consultar enteras, así que **no inventes un relleno** con el año pasado.

Tests: el orden por semana; que una habilidad futura no esté; que en un año anterior estén todas; que el chip no persista entre entradas.

## Tarea 4 — la planilla del profesor no va en el desafío

En `FichaContenido`, `onPlanilla?.let { BotonPlanilla(...) }` está **fuera** del `if (modoDesafio)`, así que el botón aparece también en el desafío. **Movelo dentro de la rama normal.** En el desafío del fin de semana se muestra el desafío y nada más.

El bloque de título (título, subtítulo, insignias) **se queda** en los dos modos: dice en qué semana estás y eso no estorba. Si Fer lo quiere fuera, lo pide aparte.

Y ojo con la tarea 1: el ancla `tour-ficha-planilla` vive en ese botón. En modo desafío el botón ya no existe, así que **ese paso del tour se saltea solo** — que es el comportamiento correcto, pero comprobalo con un test para que no quede un agujero colgado.

## Tarea 5 — el desafío va en el idioma que se aprende

**El problema, con los nombres exactos.** En `RetoFinDeSemana` todo el andamiaje se pide en `idiomaInterfaz`: `etiquetaDesafioFinde`, `textoRetoConsignaLinea`, `etiquetaBibliotecaTema`, `etiquetaRetoHabilidad`, `etiquetaRetoCondiciones`, `reglasReto`, `etiquetaRetoFrasesTitulo` y `textoRetoFrasesAyuda`. Mientras tanto, la ficha normal pide sus títulos con `etiquetaSeccion(it, idiomaMostrado)`, o sea **en el idioma que se está mostrando**. Fer notó la incoherencia y tiene razón: el desafío es una superficie para **practicar el idioma**, y su andamiaje está en el idioma de la app.

**Qué hacer:**

1. `RetoFinDeSemana` recibe **`idiomaMostrado`** y **`idiomaAprendido`** además de `idiomaInterfaz`, y **todo el andamiaje de la lista de arriba pasa a pedirse en `idiomaMostrado`**. `idiomaMostrado` ya se calcula en `FichaContenido` con `idiomaMostradoDeFicha(...)`: es el idioma que se aprende, y el de la app solo cuando el interruptor de traducción de A2/B1 está encendido.
2. **Esto no necesita cadenas nuevas:** alemán e inglés son dos de los seis idiomas de la interfaz, así que `reglasReto(Idioma.DE, …)` ya devuelve las reglas en alemán. Es un cambio de parámetro, no de tabla. **Comprobá que las seis claves existan en de y en** antes de dar la tarea por hecha.
3. **Las frases de arranque siguen SIEMPRE en el idioma que se aprende** (ya es así a propósito: son las mismas expresiones del prompt de voz). Entonces, cuando `idiomaMostrado != idiomaAprendido` —o sea, con el interruptor encendido— **van entre « »**. Hoy la línea es `frases.forEach { Vinieta(it) }`, **sin el `angulares`**: tiene que ser `Vinieta(it, angulares = idiomaMostrado != idiomaAprendido)`. Ese es el bug concreto.
4. `consigna`, `tema` y `habilidad` ya salen del contenido: comprobá que sigan el idioma mostrado como el resto de la ficha, y si no, arreglalo igual.
5. **Consecuencia que es correcta y conviene tener clara:** en B2 y arriba no hay interruptor, así que el desafío queda **entero en el idioma que se aprende** — igual que la ficha de B2, que ya es monolingüe. En A2 y B1, apagado el interruptor está todo en el idioma que se aprende, y encendido pasa al de la app con las frases entre « ».

Esto cambia la definición de la superficie «desafío» de la regla de prevención P1 («cada superficie tiene idioma de salida fijo»). **Anotalo en `REGLAS-PREVENCION.md`**: una regla que se cambia en silencio deja de ser una regla.

Tests: en B2 el andamiaje en alemán; en A2 con el interruptor apagado en alemán y encendido en el idioma de la app; las frases entre « » solo cuando está encendido.

## Tarea 6 — la vista Verbos deja de mostrar el nivel

Fer preguntó si conviene que los verbos tengan un nivel asociado si la vista no filtra por nivel. **No conviene, y hay un motivo que cierra la discusión:** ese nivel **no es una propiedad del verbo, es una propiedad de nuestra cobertura** — es el nivel más bajo de los packs donde el verbo aparece. La tabla maestra de verbos ni lo tiene (`contenido/verbos/de.json` dice textual «Sin nivel: la vista Verbos filtra solo por claseVerbo»). Y Fer quiere **ampliar** la biblioteca de verbos con verbos de referencia que no vienen de ningún pack: esos **no van a tener nivel**, así que la columna quedaría a medias justo cuando la sección crezca.

**Quitá el nivel de la línea extra de la vista Verbos.** Lo que se queda: la clase, y en alemán el régimen (`von + Dat`). El nivel **no se toca** en la vista de léxico ni en la ficha, donde sí significa algo (ahí el ítem pertenece de verdad a una semana de un nivel).

Si al quitarlo la línea extra queda vacía en algún caso, que **no se dibuje** la línea, no que quede un espacio.

## Tarea 7 — versión y guía

- `versionName` **0.4.3**, `versionCode` **18**.
- La guía de testers en español: el título a 0.4.3, y mirá **tres filas**: «Bienvenida y tutorial» (el elemento iluminado ahora se puede tocar), «Habilidades en la Biblioteca» (el orden nuevo y que no se ven las semanas futuras) y «Verbos en la Biblioteca» (ya no muestra el nivel más bajo). Pasame los textos exactos que uses. **Las otras cinco guías y los seis `.pdf` los hago yo.**
- `FALTANTES.md`: escribí la sección **69** (la 68 es mía, ya está).

## Qué no hacer

- No reescribas textos de pasos del tutorial.
- No hagas que el tour avance solo al tocar el elemento con foco, ni habilites `tocable` en más de dos pasos.
- No toques el nivel en la vista de léxico ni en la ficha.
- No agregues una fuente nueva de verbos a la vista: los verbos de referencia son una ronda posterior, cuando el contenido exista.
- No agregues `INTERNET` ni ningún permiso, ni un `Intent` hacia el navegador.
- No toques los clips de audio.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-t/`. En el informe: si las URL ya saltaban bien, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono.
