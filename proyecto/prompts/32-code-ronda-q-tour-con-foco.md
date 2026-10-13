# Prompt para Code — Ronda Q: el tutorial con foco, por partes

Leé `CLAUDE.md` (reglas duras **2** y **4**), `proyecto/REGLAS-PREVENCION.md` (P7), `proyecto/docs/tutorial-guion.md` **entero** y `proyecto/FALTANTES.md` **44**, **51** y **58**. Partí del `origin/main` con la **Ronda P ya integrada** (0.3.2): esa ronda mueve «Acerca de» al final de Ajustes y cambia los grupos de opciones de desplazamiento horizontal a `FlowRow`, y el tour se ancla justo a esas cosas. **No empieces antes.** No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets.**

Es la ronda más grande desde la G: unos **25 pasos en cuatro tours**, con título y cuerpo en 6 idiomas (≈300 cadenas). El guion está escrito y **es la fuente**: `proyecto/docs/tutorial-guion.md`. Seguilo; si algo no cierra, decilo en el informe en vez de improvisar.

## Tarea 1 — El mecanismo del foco
Un recuadro que **oscurece toda la pantalla menos lo que señala**, con un texto y los botones «Siguiente», «Saltar» y, en el último paso de cada tour, «Listo».
- **Ancla por `testTag`**, no por coordenadas: cada paso nombra una etiqueta que ya existe o que agregás, y el foco se dibuja sobre los límites reales de ese nodo.
- **Degrada sin romperse, y esto es lo más importante de la tarea:** si un paso no encuentra su ancla, **se saltea y el tour sigue**. Nunca oscurece la pantalla sin nada señalado, nunca se queda esperando, nunca crashea.
- **Que la ruptura no sea silenciosa:** un test que recorra todos los pasos de todos los tours y falle si alguna ancla no existe en su pantalla. Es la red que evita que el tutorial se pudra cuando cambie la interfaz; sin ese test, esta ronda no sirve para nada dentro de tres meses.
- Respetá «Quitar animaciones» del sistema, como en la Ronda G. El foco tiene que ser legible en los 4 temas y en claro y oscuro.
- **Accesibilidad:** el texto del paso lo lee TalkBack, los botones tienen su `contentDescription` en 6 idiomas, y el área táctil de cada botón es de 48 dp. Con la letra al 160 % el recuadro se desplaza si hace falta, pero no tapa lo que está señalando.

## Tarea 2 — Los cuatro tours y cuándo se lanzan
Del guion, respetando el orden y el contenido de cada paso:
- **Ficha** (7 pasos + cierre), al primer arranque, después de la bienvenida.
- **Biblioteca** (5), **Guardados** (3) y **Ajustes** (4), **la primera vez que se entra a cada una**.
- **Desafío del fin de semana** (2), **la primera vez que el interruptor está visible** — no «al entrar», porque solo existe sábado y domingo y si no se perdería durante semanas.
- **«Saltar» saltea todos los tours restantes**, no solo el actual. Es seguro porque se pueden repetir.
- **Repetir:** la fila «Ver la bienvenida otra vez» de Ajustes pasa a reiniciar **también** los tours, y conviene que lo diga su rótulo. Proponé el texto en 6 idiomas.
- **Estado:** un ajuste por tour visto más uno de «salteado», guardados con los demás ajustes (regla dura 4: ajustes sí, progreso no) y **sin viajar en la copia de seguridad de Guardados**; comprobalo con un test, como con `bienvenidaCompletada`.
- **Si un tour se abandona a la mitad** (botón atrás), **cuenta como visto**: nadie quiere que le vuelva a saltar lo mismo al día siguiente. Está en «lo que falta decidir» del guion; esta es la decisión.

## Tarea 3 — Dos cosas del guion que hay que resolver mirando la app
1. **Las secciones plegadas de la ficha** (vocabulario y expresiones arrancan cerradas). Un foco sobre una caja cerrada no explica nada. **Abrilas al llegar a ese paso** y volvelas a dejar como estaban al terminar el tour; si eso resulta frágil, explicá el encabezado en vez de señalar el interior, y decí cuál elegiste.
2. **Primer arranque sin ficha** (si la semana en curso no tiene contenido para ese par de idioma y nivel, la pantalla está vacía). El tour de la ficha **no se lanza** y queda pendiente para la primera vez que haya ficha. No lo des por visto.

## Tarea 4 — El cierre del tour de la ficha
El último paso del tour de la ficha lleva **el mensaje de que esto no enseña un idioma, sino que te hace practicarlo** — el texto que ya existe como `ACERCA_QUE_ES_TEXTO`, sin reescribirlo.
- **Ese texto se queda también en la bienvenida y en «Acerca de».** No lo saques de ningún lado: quien saltea el tour lo tiene que haber leído igual. Es la única redundancia deliberada de la app y está bien que lo sea.
- Es el cierre del **tour de la ficha**, el primero que se ve, no del último tour: alguien puede no entrar nunca a Guardados.

## Tarea 5 — Versión y cierre
**versionCode 15, versionName 0.4.0** (el tutorial es una función nueva, no un retoque). Guía de testers: la versión y una fila sobre el tutorial, en el tono de las demás. **Actualizá solo el español** y decime qué filas cambiaste; las otras cinco y los seis `.pdf` los hace Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
Cambiar los textos del guion (si alguno no funciona en pantalla, decilo y lo reescribe Overview), el contenido, el esquema, los assets, el audio, el ícono y la pantalla de carga.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets.
3. Tests: **todas las anclas de todos los pasos existen** (el test que evita la podredumbre silenciosa); un paso sin ancla se saltea y el tour sigue; «Saltar» saltea todos; cada tour se lanza una sola vez; abandonar cuenta como visto; el estado no viaja en el respaldo; los textos en los 6 idiomas; el tour del desafío solo se lanza con el interruptor visible; sin ficha, el tour de la ficha no se da por visto.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre la Ronda P.

## Informe
Un commit por tarea. Cómo anclaste el foco y qué `testTag` tuviste que agregar, qué hiciste con las secciones plegadas, cuántas cadenas nuevas sumaste, qué pasos te parecieron flojos **leídos en pantalla** (el guion se escribió sin ver la app), el tamaño del APK y las decisiones donde el prompt no cerraba. **Si algún tour te quedó en más de 8 pasos, decilo:** el guion fija ese tope y conviene respetarlo.
