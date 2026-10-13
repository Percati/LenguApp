# Ronda R — el foco, el orden de los pasos y el botón «Atrás»

Escrito por Overview el 13-oct-2026 sobre la Ronda Q (`36351011`), con el feedback de uso de Fer. **Reemplaza al prompt 33**, que quedó sin usar: las tareas estructurales de ese prompt están acá y **las reescrituras de texto se posponen** (ver «El texto no se toca en esta ronda»).

Veredicto de Fer sobre la Ronda Q: **el tour con foco está aprobado**. Lo que sigue son ajustes sobre algo que ya funciona.

**Alcance:** `ui/Tutorial.kt`, `ui/TutorialOverlay.kt`, `ui/TutorialPasos.kt`, `ui/TextosInterfaz.kt`, `ui/ContenidoSemanalScreen.kt`, `ui/AjustesScreen.kt`, `presentacion/IdiomaAplicacion.kt` y los tests. **Nada de contenido pedagógico, nada de assets, nada de esquema, ningún permiso nuevo.**

## El texto no se toca en esta ronda

Fer está editando a mano los títulos y los cuerpos de los 22 pasos, en `docs/tutorial-textos.md`. Esa edición vuelve en una ronda posterior, con las seis traducciones hechas por Overview de una sola vez.

Así que acá: **mové, fusioná, reordená y agregá pasos, pero no reescribas el texto de un paso que ya existe.** Un paso que se mueve se lleva su texto tal cual. Donde esta ronda crea un paso nuevo o fusiona dos, el texto que doy abajo es **provisional** y así hay que anotarlo en el informe; no lo pulas.

## Tarea 1 — margen al traer el ancla a la vista

**El problema (lo más molesto de los cinco):** cuando el paso tiene que desplazar la pantalla para mostrar su ancla, el ancla queda pegada al borde de arriba o de abajo. Hoy `traerALaVista(id)` pide el rectángulo del nodo pelado, así que el contenedor lo deja justo en el borde.

**Qué hacer:** pedir a la vista el rectángulo del ancla **inflado verticalmente 32 dp arriba y abajo**. Con eso el contenedor desplaza de más y el ancla queda despegada del borde. El agujero del foco **no cambia** (sigue con sus 6 dp de margen): esto es desplazamiento, no dibujo.

- **32 dp** es mi número; si con el código delante ves que 24 o 40 queda mejor, cambialo y decilo. Lo que no sirve es lo de hoy.
- **Cuando el ancla es lo primero o lo último del contenido, el margen no se puede cumplir** y el contenedor hace lo que puede. Es correcto y Fer lo da por entendido: no inventes relleno al principio ni al final de la ficha para conseguirlo.
- Si el ancla ya está entera a la vista y despegada de los bordes, **no desplaces nada**: el salto gratuito es peor que el margen.
- Un test que compruebe que se pide el rectángulo inflado y no el pelado.

## Tarea 2 — el tour de la ficha va en el orden de la pantalla

**El problema:** el orden actual arranca arriba, salta a la misión (que está en la mitad de abajo), vuelve a la descripción (arriba), y después baja. Zigzaguea.

**Orden nuevo**, que es el orden visual de arriba hacia abajo, con los dos pasos nuevos de la tarea 3:

| # | Ancla |
|---|---|
| 1 | `tour-ficha-titulo` |
| 2 | `tour-ficha-traducir` *(nueva, tarea 3)* |
| 3 | `tour-ficha-descripcion` |
| 4 | `tour-ficha-errores` |
| 5 | `tour-ficha-vocabulario` |
| 6 | `tour-ficha-mision` |
| 7 | `tour-ficha-autochequeo` |
| 8 | `tour-ficha-prompt` |
| 9 | `tour-ficha-planilla` *(nueva, tarea 3)* |
| 10 | `tour-barra-superior` *(cierre)* |

**Una excepción deliberada al orden visual:** el interruptor de traducción está **encima** del bloque de título (en la franja fija, que no se desplaza), pero va en el paso 2 y no en el 1. Abrir el tutorial con el foco en un interruptor que además es condicional (solo A2 y B1) deja al usuario sin saber dónde está; «Esta es tu semana» orienta. Es un salto corto hacia arriba, no un zigzag. Fer puede revertirlo si no le gusta; está anotado.

**El cierre en la barra de arriba se queda donde está** (paso 10, aunque la barra esté arriba): Fer lo aprobó explícitamente porque esa barra se ve siempre.

**`MAX_PASOS_POR_TOUR` sube de 8 a 10.** Es el tope nuevo y conviene que sea el último: diez pasos ya es un tour largo, y lo que lo hace tolerable es que «Atrás» y «Saltar» estén a mano. Si en alguna ronda hace falta un paso 11, lo que hay que hacer es partir el tour, no subir el tope otra vez.

**Renumerá las claves `TOUR_FICHA_n` para que sigan el orden nuevo** (1…10). Es un renombre mecánico y vale la pena: una clave `TOUR_FICHA_2` que es el paso 6 envejece mal. `docs/tutorial-textos.md` identifica las filas por ancla justamente para que este renombre no lo rompa.

## Tarea 3 — las dos anclas que faltan en la ficha

1. **El interruptor «Traducir al …»** (`SwitchTraduccion`, en la franja fija de arriba de `ContenidoSemanalScreen.kt`, **fuera** de la columna con `verticalScroll`): ancla `tour-ficha-traducir`. Al estar fuera del scroll, `traerALaVista` no tiene nada que hacer con él: se ve siempre. Solo existe en A2 y B1 y cuando los idiomas difieren; en B2+ **el paso se saltea** por el mecanismo que ya tenés, que es el comportamiento correcto. Texto **provisional**: título «Ficha en tu idioma», cuerpo «En A2 y B1 puedes ver la ficha traducida a tu idioma o en el idioma que estás aprendiendo. Desde B2 ya no aparece: la ficha va siempre en el idioma que aprendes.»
2. **El botón de la planilla del profesor** (`BotonPlanilla`, al final de la ficha): ancla `tour-ficha-planilla`. Si `onPlanilla` es `null` el botón no se muestra y el paso se saltea. Texto **provisional**: título «La planilla para tu profesor», cuerpo «Genera una hoja en PDF, en el idioma que aprendes y en una sola carilla, para imprimir y darle a quien te da clase.»

El paso 8 (`tour-ficha-prompt`) **conserva su texto actual** aunque mencione la planilla; corregirlo es parte de la ronda de textos.

## Tarea 4 — el botón «Atrás» en la tarjeta

Fer pidió poder volver un paso, por si apretó «Siguiente» sin leer.

- La tarjeta pasa a tener **«Atrás» · «Saltar» · «Siguiente»/«Listo»**. En el primer paso, «Atrás» **no se muestra** (no lo deshabilites: un botón gris que no hace nada confunde más que su ausencia).
- «Atrás» vuelve **al paso mostrado anterior**, no al índice anterior: si un paso se salteó por falta de ancla, se lo vuelve a saltear al retroceder. Llevá una pila de los índices mostrados.
- 48 dp y `contentDescription` en los 6 idiomas, como los otros dos. Clave nueva `TOUR_ATRAS` (+ su descripción), español **«Atrás»**.
- Si los tres botones no entran a lo ancho en la pantalla más angosta, **no los achiques**: usá `FlowRow`, el mismo recurso de la Ronda P.

**Y el botón atrás del sistema cambia de significado:** hoy abandona el tour y cuenta como visto. Pasa a **volver un paso**, y abandonar solo cuando ya estás en el primero. Es lo que espera cualquiera en Android y es incoherente que la flecha de la pantalla y el gesto del sistema hagan cosas distintas. Esto **revierte una decisión de la Ronda Q**; los tests que la fijaban hay que cambiarlos, no borrarlos.

## Tarea 5 — el cierre de la ficha sin el párrafo de «qué es esto»

Fer: ese párrafo ya se muestra en la bienvenida y en «Acerca de», y en el último paso del tour sobra.

**Quitá `cuerpoExtra = ACERCA_QUE_ES_TEXTO`** del paso de cierre. Esto deshace la tarea 4 de la Ronda Q: el campo `cuerpoExtra` queda **sin ningún uso**, así que **borralo también** (del modelo, del recuadro y de los tests), con todo lo que lo probaba. El texto no se toca: sigue en la bienvenida y en «Acerca de», donde ya estaba.

## Tarea 6 — las otras tres cosas del tutorial que ya estaban reportadas

De tu propia lista de «pasos flojos» quedan tres que son estructurales (las demás son de texto y van en la ronda de textos):

1. **Biblioteca, paso 4:** agregá `anclaTour("tour-biblioteca-vista-verbos")` a la **opción** «Verbos» del selector de vistas (la opción sola, no el selector) y apuntá ahí el paso 4; el paso 1 sigue con el selector entero. Si no se puede anclar la opción sin cambiar cómo se ve el selector —envolverla en un `Box` como hiciste con la estrella es aceptable; cambiarle el aspecto no—, **no fuerces nada**: dejá el paso como está y decilo en el informe, que lo resolvemos con el texto.
2. **Guardados, pasos 2 y 3:** fusionalos en uno, con `tour-guardados-archivo` como ancla. Guardados queda en **2 pasos** y la clave de texto que sobra **se borra**. Texto **provisional** del paso fusionado: título «Exportar e importar», cuerpo «Puedes mandar tus guardados a Anki o guardar una copia de seguridad, y volver a importarla. El archivo lo eliges tú en el momento; al importar, lo que traes se suma a lo que ya tienes, sin duplicar.»
3. **Desafío: invertí los dos pasos.** Primero `tour-desafio-interruptor`, después `tour-desafio-consigna`. La consigna solo existe con el interruptor encendido, y **el tour no lo enciende**: encenderlo sería la app aceptando el desafío en lugar del usuario (regla dura 13). Con el interruptor apagado el paso 2 se saltea, y está bien.

## Tarea 7 — «Paso n de total» no debe saltarse números

Hoy `n` es el índice en el catálogo y `total` el tamaño del catálogo, así que un paso salteado hace saltar el número.

**Cambio:** `n` cuenta **los pasos efectivamente mostrados** (1, 2, 3… sin huecos) y `total` es **el tamaño del catálogo menos los pasos ya salteados**. En el caso normal se ve exactamente igual que hoy. Al retroceder con «Atrás», `n` baja. No inventes una cadena nueva: `TOUR_PASO` y sus 6 traducciones quedan como están.

Un test: un tour de 4 pasos donde el 2.º no tiene ancla muestra «1 de 3», «2 de 3», «3 de 3».

## Tarea 8 — la nota «las traducciones solo existen en español» es falsa: borrala

En Ajustes, cuando el idioma de la app no es español, aparece «Las traducciones de vocabulario todavía solo existen en español.» (`AVISO_GLOSAS`, condicionada por `avisoGlosasSoloEnEspanol`, que es literalmente `idiomaAplicacion != Idioma.ES`).

**Ya no es verdad, y lo verifiqué sobre los assets, no de memoria:** de los **8154** ítems de vocabulario, **8154 tienen glosa en es, fr, it y pt**. En alemán hay 4088 y en inglés 4066, y la diferencia **no es un hueco**: a una palabra alemana no se le pone glosa en alemán. Sumados dan 8154 exactos. La cobertura es total.

**Qué hacer:** borrar el `Text`, la clave `AVISO_GLOSAS` con sus 6 traducciones, la función `avisoGlosasTexto`, el predicado `avisoGlosasSoloEnEspanol` y los dos tests de `AjustesScreenTest` que lo afirmaban. Nada de esconderlo con una condición más fina: es una afirmación falsa, no una afirmación mal condicionada.

**Y comprobá una cosa de paso:** con la app en alemán **aprendiendo alemán** (o inglés con inglés), no hay glosa que mostrar. Fijate que la ficha, la Biblioteca y Guardados **degraden limpio** —sin guión suelto, sin paréntesis vacío, sin una línea en blanco donde iba la traducción—. Si no degradan limpio, **arreglalo acá**: es el agujero que la nota tapaba. Un test por superficie.

## Tarea 9 — versión y guía

- `versionName` **0.4.1**, `versionCode` **16**.
- La guía de testers en español: el título sube a 0.4.1, y **mirá dos filas**: la de «Bienvenida y tutorial» (si el conteo de pasos quedó distinto) y la de Verbos. **Las otras cinco guías y los seis `.pdf` los hago yo**: decime en el informe qué filas cambiaste, con el texto exacto, o «ninguna».
- `FALTANTES.md`: escribí la sección **63** (la 61 y la 62 son mías, ya están).

## Qué no hacer

- No reescribas textos de pasos existentes (ver arriba).
- No agregues animaciones, puntos de progreso ni un indicador nuevo.
- No toques cuándo se lanza cada tour, el estado persistente, «Saltar» ni la bienvenida.
- No enciendas el interruptor del desafío desde el tour.
- No agregues `INTERNET` ni ningún permiso.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-r/`. En el informe: lo que decidiste donde este prompt no cerraba, qué textos quedaron **provisionales**, y lo que **no** pudiste comprobar sin un teléfono —el margen de la tarea 1 es justamente de eso: medilo en el test, pero decí que no lo viste.
