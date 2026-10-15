# Ronda T — el elemento con foco se puede tocar, y las URL del bloque «Voces»

Escrito por Overview el 15-oct-2026 sobre la Ronda S (`cc59695c`). Ronda corta: **Fer autorizó la tarea 3 que quedó sin hacer**, más un riesgo que vos mismo marcaste en tu informe.

Verifiqué la Ronda S entera y **está bien**: 606 fichas y 0 problemas, 0 diferencias entre `build` y los assets, índice de audio al día, manifest sin `INTERNET` en las dos variantes, versión 17 / 0.4.2, margen en 96, agujero recortado, bloque «Voces» con las dos voces, `ACERCA_CREDITOS_TEXTO` borrada, y la guarda del audio inglés retirada dejando en pie las otras tres. El informe no exageró nada.

**Alcance:** `ui/Tutorial.kt`, `ui/TutorialOverlay.kt`, `ui/TutorialPasos.kt`, `ui/AcercaDeScreen.kt`, `ui/TextosInterfaz.kt` y los tests. **Nada de contenido, nada de assets, nada de esquema, ningún permiso nuevo.** Y **ningún texto de paso del tutorial**: Fer sigue editando `docs/tutorial-textos.md`.

## Tarea 1 — el elemento con foco se puede tocar

Es la tarea 3 del prompt 37, ahora **autorizada por Fer**. El texto de abajo es el mismo, con un agregado al final.

Hoy el recuadro se come **todos** los toques, incluidos los del elemento que ilumina. Eso deja dos pasos sin nada que hacer: el del encabezado del vocabulario, que tiene que decir «el encabezado abre la lista **cuando termines con esto**», y el del interruptor del desafío, que no se puede encender, así que el paso de la consigna se saltea casi siempre y el tour dice «Paso 1 de 1».

- El agujero **pasa los toques al elemento que ilumina**; **todo el resto de la pantalla sigue sin recibir nada**. Usá el **rectángulo recortado** de la tarea 2 de la Ronda S, el mismo con el que dibujás el agujero: si una parte del ancla no se ve, un toque ahí no debería llegarle.
- **El tour NO avanza solo** al tocar. El paso sigue necesitando «Siguiente». Tocar el interruptor lo enciende y la pantalla cambia debajo del recuadro; el paso se queda donde está.
- **Si la pantalla cambia debajo**, el foco sigue al ancla (`onGloballyPositioned` ya reregistra los límites). Si el ancla desaparece, el paso **se saltea** con el mecanismo que ya existe. Un test de eso.
- **Un campo `tocable` en `PasoTour`, falso por omisión.** No una lista a mano de «anclas que navegan»: eso envejece peor. Verdadero en **exactamente dos pasos**: `tour-ficha-vocabulario` y `tour-desafio-interruptor`.
- **No lo habilites en ningún otro paso**, y menos en anclas que navegan a otra pantalla (`acerca-de` y `ver-bienvenida` en Ajustes, las filas de la vista Habilidades): un toque ahí dejaría el tour corriendo sobre una pantalla que no es la suya. Un test que compruebe que **solo esos dos** pasos son `tocable`.
- **Agregado:** con el vocabulario, abrir la lista **mueve el ancla y crece el contenido debajo**. Comprobá que el foco siga al encabezado y que el agujero no quede sobre la lista desplegada; si la lista empuja el encabezado fuera de pantalla, el recorte de la tarea 2 de la Ronda S tiene que hacer lo suyo y no dejar un agujero colgado.

**Lo que esto desbloquea, y por qué lo pedimos:** con el interruptor encendido a mano durante el tour, el paso 2 del desafío (la consigna) **aparece**, y el tour pasa a decir «Paso 1 de 2». Es la única forma de que ese paso exista sin que la app acepte el desafío en lugar del usuario (regla dura 13).

## Tarea 2 — las URL del bloque «Voces» en una pantalla angosta

Vos lo marcaste: no se comprobó **cómo salta de línea el bloque «Voces» con URL largas en 360 dp**. Es un riesgo real y concreto: `https://www.kaggle.com/datasets/roholazandie/ryanspeech` son 52 caracteres **sin un solo espacio**, y una pantalla de 360 dp con `bodyMedium` no da para eso en una línea. Si el salto de línea no las parte, se cortan o se van de la pantalla — y «nada de deslizar a lo ancho» fue justamente lo que Fer pidió en la Ronda P.

**Comprobalo primero** con un test a 360 dp (y a 320 dp, que es el piso razonable), midiendo que el bloque no se pase del ancho disponible. **Si se pasa, arreglalo**; si ya salta bien solo, decilo y no toques nada. Lo que no quiero es que quede sin mirar.

Si hay que arreglarlo, el orden de preferencia: (1) dejar que el salto de línea parta la URL por caracteres; (2) insertar oportunidades de corte invisibles después de cada `/`; (3) **último recurso**, acortar la URL mostrada. Lo que **no** se hace es poner la URL en una línea con desplazamiento horizontal propio, ni truncarla con puntos suspensivos: una URL cortada no cumple la atribución de CC BY, que es para lo que está ahí.

## Tarea 3 — versión y guía

- `versionName` **0.4.3**, `versionCode` **18**.
- La guía de testers en español: el título a 0.4.3 y **mirá la fila «Bienvenida y tutorial»**: que el elemento iluminado ahora se pueda tocar es algo que un tester nota, y la fila no lo dice. Si la cambiás, pasame el texto exacto. **Las otras cinco guías y los seis `.pdf` los hago yo** (ya las dejé en 0.4.2 con la fila del audio corregida).
- `FALTANTES.md`: escribí la sección **69** (la 68 es mía, ya está).

## Lo que NO es tarea tuya, para que no lo toques

- **Los 59 clips de audio sin referencia** (33 en inglés, 26 en alemán; ~350 KB) que viajan en los dos APK y no suenan nunca. Son restos de textos que cambiaron. Los genera Fer localmente, así que **la limpieza la decide él**, no vos: si borrás archivos de `assets/audio/` el índice cambia y la ronda se vuelve otra cosa.
- **Probar en teléfono** lo que dejaste anotado: que 96 dp se vea bien, cuánto tapa la tarjeta con `tour-ajustes-tema` en ~640 dp, y cómo se ve el borde del agujero recortado. Eso es de Fer.

## Qué no hacer

- No reescribas ningún texto de paso del tutorial.
- No hagas que el tour avance solo al tocar el elemento con foco.
- No habilites `tocable` en más de los dos pasos indicados.
- No agregues `INTERNET` ni ningún permiso, ni un `Intent` hacia el navegador (el enlace tocable en «Voces» sigue descartado: lo decidiría Fer, no nosotros).
- No agregues animaciones ni cambies cuándo se lanza cada tour.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-t/`. En el informe: si las URL ya saltaban bien o hubo que arreglarlas, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono.
