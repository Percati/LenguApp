# Ronda S — el elemento con foco se puede tocar, y el agujero no se sale de la pantalla

Escrito por Overview el 14-oct-2026 sobre la Ronda R (`57ed1fe6`). Verifiqué la Ronda R entera: **todo lo que informaste es cierto** y el hallazgo de `boundsInRoot()` es el mejor de la ronda. Lo que sigue son dos cosas: una decisión de diseño que desbloquea dos pasos muertos, y una regresión visual que entró con la tarea 1.

**Alcance:** `ui/Tutorial.kt`, `ui/TutorialOverlay.kt`, `ui/TutorialPasos.kt` y los tests. **Nada de contenido, nada de assets, nada de esquema, ningún permiso nuevo.** Y **ningún texto de paso**: Fer sigue editando `docs/tutorial-textos.md`.

## Tarea 1 — el elemento con foco se puede tocar (si Fer la aprueba)

**El problema de fondo.** El recuadro se come **todos** los toques, incluidos los del elemento que está iluminando. Eso deja dos pasos sin nada que hacer y obliga a escribir en futuro:

- **Ficha, paso 5** (el encabezado del vocabulario) tiene que decir «el encabezado abre la lista **cuando termines con esto**»: iluminamos algo y pedimos que no se toque.
- **Desafío, paso 1** (el interruptor) no puede encenderse, así que el paso 2 (la consigna) **se saltea casi siempre** y el tour dice «Paso 1 de 1». Lo anotaste vos: hoy ese tour es de un paso en la práctica.

**El cambio.** El agujero del foco **pasa los toques al elemento que ilumina**; todo el resto de la pantalla sigue sin recibir nada. Es el patrón habitual de los tutoriales con foco y arregla los dos casos de arriba sin textos en futuro.

Cómo:
- El `pointerInput` que se come los toques **deja pasar** los que caen dentro del rectángulo del ancla (el mismo que se usa para dibujar el agujero, después del recorte de la tarea 2). Fuera de ahí, se los sigue comiendo.
- **El tour no avanza solo** al tocar: el paso sigue necesitando «Siguiente». Tocar el interruptor lo enciende y la pantalla cambia debajo del recuadro; el paso no se mueve.
- **Si la pantalla cambia debajo del tour**, el foco sigue al ancla (`onGloballyPositioned` ya reregistra los límites) y, si el ancla desaparece, el paso **se saltea** con el mecanismo que ya existe. Un test de eso.
- **No se habilita para un ancla que navega a otra pantalla.** Las que podrían hacerlo son `acerca-de` y `ver-bienvenida` en Ajustes, y las filas de la vista Habilidades. Dejalas sin paso-a-través: un toque ahí dejaría el tour corriendo sobre una pantalla que ya no es la suya. Si la lista de anclas «que navegan» se vuelve una lista a mano, mejor invertirlo: **un campo `tocable` en `PasoTour`**, falso por omisión, verdadero en los pasos que lo necesitan. Elegí vos la forma; la de la lista a mano envejece peor.
- Los pasos que lo necesitan hoy: **ficha paso 5** (`tour-ficha-vocabulario`) y **desafío paso 1** (`tour-desafio-interruptor`). Con eso alcanza.

**Esta tarea depende de que Fer diga sí.** Si dice que no, saltala entera y quedate con las tareas 2 y 3; el tour del desafío se arregla entonces por texto, en la ronda de textos.

## Tarea 2 — el agujero del foco se recorta a la pantalla

**La regresión.** La tarea 1 de la Ronda R cambió el registro de las anclas de `boundsInRoot()` (recortado contra los ancestros) a `positionInRoot()` + `size` (**sin recortar**). Era necesario para `bringIntoView`: con el rectángulo recortado, un nodo fuera de pantalla registraba 0 × 0 y el margen se inflaba sobre nada. Pero `rectDe()` **también** es la fuente de dos cosas más en el recuadro, y las dos se rompen con un rectángulo sin recortar:

1. **El agujero y su borde** (`drawRoundRect` con `BlendMode.Clear`): si el ancla no entra entera en la pantalla, el agujero se dibuja **también sobre la parte que no se ve** — un rectángulo iluminado por encima de la barra de arriba o por debajo del borde de abajo, con su borde blanco colgando en el aire.
2. **De qué lado va la tarjeta** (`enLaMitadDeArriba`, que mira `rect.center.y`): con un rectángulo sin recortar, el centro de un nodo alto puede caer **fuera** de la pantalla, así que la tarjeta se va al lado equivocado y **tapa el foco**.

El caso realista no es exótico: **`tour-ajustes-tema`** agrupa tema, modo y tipografía, tres `FlowRow` de chips. En un teléfono chico ese grupo es más alto que media pantalla y puede no entrar entero. `tour-ficha-mision` y `tour-ficha-autochequeo` son candidatos parecidos cuando la misión es larga.

**Qué hacer:** en el recuadro, **recortar el rectángulo del ancla contra los límites del propio recuadro** (`tamano`, que ya tenés) antes de dibujar el agujero y antes de decidir de qué lado va la tarjeta. El rectángulo **sin recortar** se sigue usando para `traerALaVista`, que es para lo que se cambió. Si de la intersección no queda nada (el ancla quedó entera fuera de pantalla, que no debería pasar después de traerla), tratalo como **sin foco**: tarjeta abajo y nada de agujero — nunca un agujero en una esquina.

**Y un caso que conviene decidir ahora:** un ancla **más alta que la pantalla**. Hoy `bringIntoView` con el rectángulo inflado la centra, así que se ve el medio y se pierden los dos extremos. Preferí **mostrar el principio**: traerla a la vista por su borde de arriba (con el margen de 32 dp) y dejar que el resto se corte abajo. Quien lee un paso empieza por arriba.

Tests: un ancla que sobra por abajo deja el agujero dentro de la pantalla; un ancla alta pone la tarjeta del lado donde hay lugar, no encima del foco.

## Tarea 3 — dos cosas chicas

1. **`BibliotecaScreen.kt` quedó con el `import` de `traduccionParaMostrar` sin usar** (tarea 8 de la Ronda R). Borralo.
2. **El `enum ClaveTexto` quedó desordenado:** `TOUR_FICHA_2` y `TOUR_FICHA_9` están declaradas al final de la lista, después de las del desafío, porque se agregaron en una ronda posterior al renumerado. No cambia nada funcional, pero el renumerado de la Ronda R existía justamente para que la lista se lea en orden. Ponelas en su lugar (1…10 seguidas) y aprovechá para dejar las de `TOUR_DESAFIO` en orden también.

## Tarea 4 — versión y guía

- `versionName` **0.4.2**, `versionCode` **17**.
- La guía de testers en español: el título sube a 0.4.2. **Si se hace la tarea 1**, mirá también la fila «Bienvenida y tutorial»: que el elemento iluminado se pueda tocar es algo que un tester va a notar. **Las otras cinco guías y los seis `.pdf` los hago yo**: decime qué filas cambiaste, con el texto exacto, o «ninguna».
- `FALTANTES.md`: escribí la sección **65** (la 64 es mía, ya está).

## Qué no hacer

- No reescribas ningún texto de paso: Fer los está editando.
- No agregues animaciones, ni un «anterior» más, ni un indicador nuevo.
- No cambies cuándo se lanza cada tour, el estado persistente ni «Saltar».
- No hagas que el tour avance solo al tocar el elemento con foco.
- No enciendas el interruptor del desafío desde el tour: que lo encienda el usuario es justamente el punto de la tarea 1.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-s/`. En el informe: qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono — de la tarea 2 **casi todo** es «cómo se ve», así que medí lo que puedas en el test y decí claramente qué no viste.
