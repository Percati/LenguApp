# Prompt para Code — Ronda H: vista «Verbos» en la Biblioteca

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (P3, P5, P7, P11), `proyecto/FALTANTES.md` secciones **25.1** (los 6 pasos para enchufar «Verbos», que los escribiste vos), **29**, **30** y **31**. Partí del `origin/main` actual: la Ronda G y la versión 0.2.3 (versionCode 5) ya están. No pushees: entregá un parche por tarea con `git format-patch`.

El contenido ya está listo: `vocabulario[].claseVerbo` (lista de clases) y `vocabulario[].reccion` están poblados en los packs y los assets están al día. **No toques el contenido, ni el esquema, ni `proyecto/contenido/`.** Si algo del contenido te falta, decilo en el informe y seguí; no lo arregles vos.

## Tarea 1 — Vista «Verbos» en la Biblioteca
Tercera vista, junto a «Vocabulario y expresiones» y «Habilidades», siguiendo los 6 pasos de FALTANTES 25.1.
- **Qué entra:** los ítems de `vocabulario` que tienen `claseVerbo` no vacío, del idioma y nivel que se está viendo, **deduplicados por el original** (un verbo aparece una vez aunque esté en varios packs). Orden alfabético por el original.
- **Fila:** `FilaPalabra` con el parámetro `extra` ya existente. En `extra` va, en una línea discreta: en alemán el `reccion` cuando existe (`von + Dat`), y la separabilidad en inglés si no se desprende ya del chip elegido. Si no hay nada que mostrar, no se dibuja la línea. La estrella funciona igual que en las otras vistas (son los mismos ítems de Guardados; no inventes una clave distinta).
- **Un solo grupo de chips: la clase.** **No hay filtro de nivel** (decisión de Fer, FALTANTES 29: el nivel que se podía derivar era el del tema donde el verbo apareció primero, no su dificultad). Ojo: FALTANTES 25.1 dice «dos grupos de chips (nivel y tipo)», y eso quedó obsoleto. Si ves cualquier otro resto de nivel en lo que preparaste en la Ronda G, saltalo.
- **Los chips se calculan por idioma desde los datos (P3):** solo las clases presentes en los ítems que se están mostrando. Nunca un chip vacío. La clase `simple` **no se muestra como chip**: existe solo para que todo verbo tenga clase. Con ningún chip elegido se ven todos los verbos, los `simple` incluidos.
- **Etiquetas visibles en 6 idiomas** en `TextosInterfaz` (P7), más el nombre de la vista. Usá esta tabla, que viene de la conversación «Verbos» (fr/it/pt no los revisó un nativo; anotalo en el informe, no lo arregles):

| clase | es | en | de | fr | it | pt |
|---|---|---|---|---|---|---|
| `reflexivo` | Reflexivo | Reflexive | Reflexiv | Pronominal | Riflessivo | Reflexivo |
| `trennbar` | Separable | Separable | Trennbar | Séparable | Separabile | Separável |
| `mit-Praposition` | Con preposición | With preposition | Mit Präposition | Avec préposition | Con preposizione | Com preposição |
| `mit-Dativ` | Con dativo | With dative | Mit Dativ | Avec datif | Con il dativo | Com dativo |
| `phrasal-separable` | Phrasal separable | Separable phrasal verb | Trennbares Phrasal Verb | Phrasal verb séparable | Phrasal verb separabile | Phrasal verb separável |
| `phrasal-inseparable` | Phrasal inseparable | Inseparable phrasal verb | Untrennbares Phrasal Verb | Phrasal verb inséparable | Phrasal verb inseparabile | Phrasal verb inseparável |
| `verbo-preposicion` | Verbo + preposición | Verb + preposition | Verb + Präposition | Verbe + préposition | Verbo + preposizione | Verbo + preposição |

- **Clase desconocida:** si un ítem trae una clase que no está en esta tabla, no rompas: mostrá el verbo sin ese chip y contalo en un test. El contenido puede sumar clases antes que la app.
- **Estado vacío:** si el par (idioma, nivel) no tiene verbos etiquetados, el mismo tono de invitación breve que usaste en la Ronda G, en 6 idiomas.

## Tarea 2 — Barra de búsqueda en la vista Verbos
Es lo que reemplaza a las «familias» de verbos separables: Fer escribe `machen` y quiere ver *machen, aufmachen, zumachen, abmachen*.
- Campo de búsqueda arriba de los chips, con botón de borrar. Filtra **mientras se escribe**, sobre los verbos ya filtrados por clase.
- Coincidencia por **subcadena**, insensible a mayúsculas **y a diacríticos** (buscar `uber` encuentra *übernehmen*; `UBER` también), sobre el **original** y sobre la **traducción al idioma de app** (buscar `abrir` encuentra *aufmachen*).
- Nada de datos nuevos: no hay campo de familia ni de raíz en el contenido, y no lo agregues.
- Si no hay resultados, un mensaje breve en 6 idiomas, distinto del estado vacío de la tarea 1.
- **No** pongas la búsqueda en las otras dos vistas en esta ronda. Si te parece que debería estar también en «Vocabulario y expresiones», proponelo en el informe con el costo.

## Tarea 3 — «Contraer/Expandir» en 6 idiomas
`ui/SeccionPlegable.kt` tiene el `contentDescription` fijo en español. No es texto a la vista, pero **TalkBack lo lee en voz alta**: con la app en alemán el lector dice una palabra en español. Pasalo a `TextosInterfaz` en 6 idiomas (P7) y buscá si quedó algún otro `contentDescription` o texto visible fijo en un idioma: si hay más, arreglalos en el mismo commit y listalos.

## Tarea 4 — Versión y cierre
Subí a **versionCode 6, versionName 0.2.4**. Actualizá la guía de testers (`proyecto/docs/guia-testers.md`): la versión y una fila nueva en «esto es así a propósito» que explique la vista Verbos y su búsqueda, en el tono de las demás filas. **No** regeneres el `.pdf` (lo hace Overview). Actualizá `FALTANTES.md` con una sección nueva.

## Fuera de alcance
Ícono del lanzador y arte de la pantalla de carga (Fer todavía no tiene los archivos; el lugar único ya está documentado en FALTANTES 25.1 y no se toca). Contenido, esquema, planilla, audio, estadísticas o cualquier forma de progreso. Ejemplos con el verbo separado (es contenido nuevo).

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en **cada** commit.
2. Los dos guardas de `preBuild` (`verificarAssetsContenidoActualizados`, `verificarConsistenciaContenido`) en verde, sin tocar los assets.
3. Tests nuevos: deduplicación por original; chips calculados solo con las clases presentes; `simple` nunca como chip; clase desconocida sin romper; búsqueda insensible a mayúsculas y diacríticos y sobre la traducción; estado vacío y sin resultados; el selector con **tres** vistas a 320 dp con la letra al 160 % en los 6 idiomas; ningún texto fijo en un idioma en las pantallas tocadas.
4. Manifest sin `INTERNET` ni permisos nuevos (regla dura 1).
5. Tamaño del APK con **empaquetado limpio** (borrá `app/build/outputs/apk` y `app/build/intermediates/apk`), con el delta sobre los 62 897 555 B de la Ronda G.

## Informe
Un commit por tarea. Qué tests agregaste, el tamaño del APK y su delta, las decisiones donde el prompt no cerraba, las pantallas tocadas, y **cuántos verbos quedan a la vista por (idioma, nivel)** con los datos reales: si algún par queda con muy pocos, decilo, porque es el dato que define si hace falta contenido nuevo.
