# Ronda R — los siete pasos flojos del tutorial

Escrito por Overview el 13-oct-2026, sobre la Ronda Q (`36351011`). Responde punto por punto la lista «Pasos que me parecen flojos» de `FALTANTES.md` 60. **Los siete los reportaste vos y los siete tienen razón**: el guion se escribió sin ver la app y se nota. Acá va la reescritura y, donde hace falta, el cambio de ancla.

**Nada de contenido pedagógico, nada de assets, nada de esquema.** Solo `TutorialPasos.kt`, `TextosInterfaz.kt`, las anclas que se indican y `TutorialOverlay.kt` para la cuenta.

## Regla que ordena todo lo de abajo

**El foco manda sobre el texto.** Si el recuadro ilumina una cosa, el cuerpo del paso habla de esa cosa. Puede mencionar algo que está fuera del foco solo como indicación de dónde mirar después («debajo», «cuando cierres esto»), nunca como si estuviera a la vista. Y **ningún paso pide tocar algo durante el tour**: el recuadro se come los toques, así que un «toca X» es una instrucción imposible en el momento en que se lee.

## Tarea 1 — ficha, pasos 3, 4 y 5 (solo texto)

La ficha ya está en el tope de 8 pasos, así que **no se agregan pasos ni se cambian anclas**: se ajusta el texto a lo que el foco realmente muestra.

- **Paso 3**, ancla `tour-ficha-descripcion`. Título: **«La explicación corta»**. Cuerpo: **«De qué va la habilidad de esta semana. Debajo, si la ficha los trae, vienen el cuadro de referencia, los ejemplos y las notas. Es media página, no un capítulo de gramática.»** (El «si la ficha los trae» es literal: el cuadro es plegable y puede no existir.)
- **Paso 4**, ancla `tour-ficha-errores`. Título: **«Los errores típicos»**. Cuerpo: **«En qué se suele equivocar quien viene de tu idioma. Algunas fichas traen además un contraste con tu idioma, justo encima.»**
- **Paso 5**, ancla `tour-ficha-vocabulario` (el encabezado plegable). Título: **«Vocabulario y expresiones»**. Cuerpo: **«El encabezado abre la lista cuando termines con esto. Cada palabra y cada expresión tienen una estrella: lo que marques queda en Guardados.»**

## Tarea 2 — Biblioteca: el paso de Verbos necesita su propia ancla

El cuerpo del paso 4 está bien; el problema es que el foco repite el del paso 1. **Agregá `anclaTour("tour-biblioteca-vista-verbos")` a la opción «Verbos» del selector de vistas** (la opción sola, no el selector), y apuntá el paso 4 ahí. El paso 1 sigue con `tour-biblioteca-vistas`, el selector entero.

Si la opción no se puede anclar sin cambiar cómo se ve el selector (envolverla en un `Box` como hiciste con la estrella es aceptable; cambiar su aspecto no), **no fuerces nada: fusioná los pasos 1 y 4 en uno** con el selector entero como ancla, y la Biblioteca pasa a 4 pasos. En ese caso el cuerpo fusionado es: **«Vocabulario y expresiones, Habilidades y Verbos. En Verbos están todos los del idioma, de cualquier nivel, agrupados por verbo; busca la raíz («stellen») para ver la familia entera.»** Decidilo vos con el código delante y dejá escrito cuál elegiste.

## Tarea 3 — Guardados: dos pasos, no tres, y el primero asume la lista vacía

**Fusionar los pasos 2 y 3 en uno**, con el grupo de botones como ancla (`tour-guardados-archivo`, el que ya existe): señalaban los dos lo mismo y la diferencia entre exportar e importar la dice el botón. Guardados queda en **2 pasos**.

- **Paso 1**, ancla `tour-guardados-lista`. Título: **«Lo que marcaste con la estrella»**. Cuerpo: **«Aparece aquí, con el mismo aspecto que en la Biblioteca. Si todavía no marcaste nada, está vacío: es lo normal la primera vez.»**
- **Paso 2** (el fusionado), ancla `tour-guardados-archivo`. Título: **«Exportar e importar»**. Cuerpo: **«Puedes mandar tus guardados a Anki o guardar una copia de seguridad, y volver a importarla. El archivo lo eliges tú en el momento; al importar, lo que traes se suma a lo que ya tienes, sin duplicar.»**

La clave de texto que queda libre (el tercer par) **se borra**, no se deja muerta.

## Tarea 4 — desafío: primero el interruptor, después la consigna

El paso 1 solo tiene sentido con el interruptor encendido, y **el tour no debe encenderlo**: aceptar el desafío es del usuario, no de la app (regla dura 13). Así que **se invierte el orden**:

- **Paso 1**, ancla `tour-desafio-interruptor`. Título: **«El desafío del fin de semana»**. Cuerpo: **«Aparece sábado y domingo. Es para hablar en voz alta, no para escribir. Enciéndelo cuando quieras hacerlo: arranca apagado cada vez que abres la app y no se guarda nada.»**
- **Paso 2**, ancla `tour-desafio-consigna`. Título: **«La consigna»**. Cuerpo: **«Arriba, con los minutos que te pedimos; debajo, el tema, la habilidad, las condiciones y unas frases para empezar.»**

Si el interruptor está apagado, la consigna no existe y **el paso 2 se saltea por el mecanismo que ya tenés**. Eso es correcto: el usuario que no enciende nada no necesita que le expliquen algo que no ve. Con la tarea 5, además, el contador no se nota.

## Tarea 5 — «Paso n de total» no debe saltarse números

Hoy `n` es el índice en el catálogo y `total` el tamaño del catálogo, así que un paso salteado por falta de ancla hace saltar el número.

**Cambio:** `n` cuenta **los pasos efectivamente mostrados** (1, 2, 3… sin huecos) y `total` es **el tamaño del catálogo menos los pasos ya salteados**. En el caso normal (nada salteado) se ve exactamente igual que hoy. Cuando algo se saltea, `n` nunca salta y `total` puede bajar en el momento del salteo —preferible a que el usuario vea «Paso 5 de 8» después de «Paso 3 de 8»—. No inventes una cadena nueva ni ocultes el total: la cadena `TOUR_PASO` y sus 6 traducciones quedan como están.

Un test: un tour de 4 pasos donde el 2.º no tiene ancla muestra «1 de 3», «2 de 3», «3 de 3».

## Tarea 6 — versión y guía

- `versionName` **0.4.1**, `versionCode` **16**.
- La guía de testers en español: la fila **«Bienvenida y tutorial»** no cambia de sentido con nada de esto, así que **tocala solo si el conteo de pasos quedó distinto**; el título sí sube a 0.4.1. **Las otras cinco guías y los seis `.pdf` los hago yo**, como siempre: decime en el informe qué filas cambiaste, con el texto exacto, o decime «ninguna».
- `FALTANTES.md`: escribí la sección **62** (la 61 es mía, ya está).

## El español va en tuteo neutro

Todas las cadenas de arriba están ya en tuteo neutro (P9): «puedes», «eliges tú», «abres». Si reescribís alguna, mantenelo, y las otras cinco traducciones con el mismo registro que ya usan los pasos de la Ronda Q.

## Qué no hacer

- No reescribas los pasos que no están en esta lista: están bien.
- No agregues animaciones, ni un «anterior», ni puntos de progreso.
- No toques el mecanismo de lanzamiento, el estado persistente ni «Saltar».
- No agregues `INTERNET` ni ningún permiso.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-r/`. En el informe, lo que decidiste donde este prompt no cerraba, y lo que **no** pudiste comprobar sin un teléfono.
