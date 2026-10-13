# Prompt para Code — Ronda P: arreglos en Ajustes

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (P3, P7) y `proyecto/FALTANTES.md` **51**, **55** y **57**. Partí del `origin/main` actual (Ronda O, 0.3.1 / versionCode 13). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets.**

Ronda corta, todo en la pantalla de Ajustes. Sale de Fer usando la 0.3.1.

## Tarea 1 — Orden de la pantalla
Hoy el encabezado (nombre, línea fija, «Acerca de», «Ver la bienvenida otra vez») está **arriba de todo**. Pasa al **final**, y en este orden:
1. …todos los ajustes (idiomas que se practican, idioma de la app, tema, modo, tipografía, velocidad del audio);
2. **Ver la bienvenida otra vez**;
3. **Acerca de**, última fila de la pantalla.

Lo que se usa seguido queda arriba; lo que se consulta una vez, al fondo.

## Tarea 2 — Nada de deslizar a lo ancho
Todos los grupos de opciones de Ajustes usan hoy `horizontalScroll` (seis lugares: idiomas aprendidos, niveles, idioma de la app, tema, modo, tipografía, y ahora la velocidad). **Lo que no se ve, no existe**: una opción que queda fuera de pantalla es una opción que nadie elige.
- Pasalos a `FlowRow` (el que ya usás en `FiltroChips.kt`), para que **todas las opciones se vean a la vez**, repartidas en dos o tres filas según entren. **Ninguna opción de Ajustes puede quedar fuera de la pantalla a lo ancho.**
- No fuerces un número fijo de columnas: dejá que el `FlowRow` acomode según el ancho real y el tamaño de letra. Lo que importa es que **todo sea visible sin deslizar**, no que haya exactamente dos o tres columnas.
- Si alguna etiqueta es tan larga que ella sola no entra (el nombre de un idioma en alemán, con la letra al 160 %), que se corte con elegancia o se achique el chip, pero que **no reaparezca el desplazamiento horizontal**.
- **Probalo a 320 dp de ancho y con la letra al 100 % y al 160 %**, en los 6 idiomas. Los nombres largos de alemán y portugués son el caso difícil.
- **La misma revisión en la pantalla de bienvenida**, que usa `horizontalScroll` para los chips de idioma: ahí es todavía más importante, porque es lo primero que ve alguien que abre la app y no puede adivinar que hay más idiomas a la derecha.
- La Biblioteca **no se toca**: ahí el desplazamiento horizontal de los chips de filtro es deliberado (P3, muchas categorías) y ya está resuelto con su propio componente.

## Tarea 3 — Quitar la línea fija
Borrá de Ajustes la línea «LenguApp · Para fijar lo que ya aprendiste en tu curso» (la clave `LINEA_FIJA`). Lo dice la pantalla de bienvenida y lo dice «Acerca de»: repetirlo en Ajustes no agrega nada.
- Si al quitarla queda huérfana la clave `LINEA_FIJA` en `TextosInterfaz`, **quitá también la clave y su función**, no la dejes muerta. Comprobá antes que no la use nadie más.
- El **nombre de la app** sí se queda arriba de las dos filas finales.
- Actualizá `proyecto/publicacion/LEEME.md`, que menciona esa línea como uno de los tres lugares donde se dice que no es un curso: ahora los lugares son la tienda, la bienvenida y «Acerca de».

## Tarea 4 — Versión y cierre
**versionCode 14, versionName 0.3.2.** Guía de testers: la versión y, si alguna fila habla de dónde están «Acerca de» o la línea fija, corregila. **Actualizá solo el español**, que es el canónico y el que lee el test, y **decime exactamente qué filas cambiaste**: las otras cinco guías y los seis `.pdf` los hace Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
El tutorial con foco (va en la ronda siguiente, prompt 32), el contenido, el esquema, los assets, el audio, el ícono y la pantalla de carga, la Biblioteca.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets.
3. Tests: «Acerca de» es la última fila y «Ver la bienvenida otra vez» la anterior; **ningún grupo de opciones de Ajustes ni de la bienvenida usa `horizontalScroll`** (vigilalo sobre el código fuente, como hiciste con los `contentDescription`); a 320 dp y con la letra al 160 % todas las opciones de cada grupo están presentes en pantalla; la línea fija ya no aparece.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 63 153 420 B (debug) de la Ronda O.

## Informe
Un commit por tarea. Cómo quedaron los grupos a 320 dp con la letra al 160 % (cuántas filas usa cada uno), qué pasó con la clave `LINEA_FIJA`, qué filas cambiaste en la guía en español, el tamaño del APK y las decisiones donde el prompt no cerraba.
