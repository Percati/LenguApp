# Prompt para Code — Ronda J: los núcleos verbales en la vista Verbos

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (P3, P7, P11), `proyecto/FALTANTES.md` **34**, **37**, **38** y **39**, y el esquema `proyecto/schema/ficha.schema.json` (campos `nucleoVerbal`, `claseNucleo`, `reccionNucleo`). Partí del `origin/main` actual (Ronda I, 0.2.5 / versionCode 7, más el contenido del prompt 23 ya integrado). No pushees: un parche por tarea. **No toques `proyecto/contenido/` ni el esquema.**

El contenido ya trae 1104 colocaciones marcadas con su núcleo verbal. Hoy esos campos **no están en el modelo de Kotlin y los assets están desactualizados a propósito**: regenerarlos antes de que agregues los campos rompería la carga, igual que en la Ronda G.

## Qué son las dos clases de entrada (esto decide todo lo demás)
- **Verbo propio** (ya en la app): un ítem de `vocabulario` con `claseVerbo`. Se lista con su texto, tiene traducción y **es marcable** con la estrella.
- **Núcleo verbal** (nuevo): el infinitivo que es núcleo de una o más colocaciones (*die Frage stellen* → *stellen*). **No es un ítem**: no tiene traducción propia y **NO es marcable**, porque la clave de Guardados es el texto del ítem (`claveGuardado(tipo, textoOrigen)`) y un núcleo sin ítem generaría una clave que ninguna ficha puede volver a producir. La estrella vive en **la colocación**, que sí es un ítem.

## Tarea 0 — PRIMERA: los tres campos y los assets al día
En su propio commit, antes de todo lo demás:
1. Agregá a `VocabularioItem` (`modelo/ContenidoSemanal.kt`): `nucleoVerbal: String? = null`, `claseNucleo: List<String> = emptyList()`, `reccionNucleo: String? = null`. Sin mostrarlos todavía.
2. Regenerá los assets: `python3 proyecto/tools/componer.py --contenido proyecto/contenido --salida <tmp> --schema proyecto/schema/ficha.schema.json` (esperado: 606 fichas, 56 semanas especiales, 0 con problemas) y reemplazá `app/src/main/assets/contenido/` por la salida (`rm -rf` + `cp -r`). Los dos guardas de `preBuild` tienen que quedar en verde.
3. Test: cargar una ficha real con `nucleoVerbal` y comprobar que el parser la lee.
4. Informá el tamaño del APK antes y después.

## Tarea 1 — Los núcleos en la vista Verbos
La vista lista **las dos clases de entrada juntas**, en un solo orden alfabético por el infinitivo.
- **Núcleo:** una fila con el infinitivo, sin estrella, y **debajo sus colocaciones visibles** (no detrás de un toque), cada una con su traducción al idioma de app y **su propia estrella**. Son los `nucleoVerbal` distintos del idioma, agrupando todas las colocaciones que los comparten.
- **Fusión obligatoria por infinitivo.** Cuando un núcleo coincide **exactamente** con el texto de un verbo propio, es **una sola fila**: la del verbo propio (marcable, con su traducción) y las colocaciones del núcleo debajo. Medido por Overview sobre el contenido: **32 coincidencias en alemán y 65 en inglés** (*to afford* propio y núcleo de *to afford something*; *abgeben* propio y núcleo de *Verantwortung abgeben*). **Ojo:** el informe de «Verbos» dijo que en inglés no había ninguna; es incorrecto, son 65. Sin fusionar se ven dos filas del mismo verbo, una marcable y otra no. R11 garantiza que las dos traen la misma clase, así que fusionar es seguro.
- **Totales esperados, fusión incluida:** **DE 470 entradas** (305 propios + 197 núcleos − 32) y **EN 559** (368 + 256 − 65). Si te dan otros números, pará y reportalo antes de seguir.
- **Chips:** un núcleo aporta su `claseNucleo` al conteo, igual que un verbo propio aporta su `claseVerbo`. Las reglas no cambian: solo las clases presentes (P3), `simple` nunca es chip, una clase desconocida no rompe.
- **Línea extra del núcleo:** el `reccionNucleo` cuando existe, y el nivel más bajo de sus colocaciones (el mismo criterio que ya usás para los verbos propios).
- Un ítem con `nucleoVerbal` **no** se lista como colocación suelta en esta vista: aparece solo debajo de su núcleo. En «Vocabulario y expresiones» sigue apareciendo como siempre, sin cambios.

## Tarea 2 — Búsqueda
Con ~470 y ~559 entradas más 1104 filas de colocación, la búsqueda deja de ser una comodidad y pasa a ser **la forma principal de usar la vista**. Por eso:
- La búsqueda debe encontrar un núcleo tanto por el infinitivo como **por el texto o la traducción de cualquiera de sus colocaciones**: escribir «Frage» tiene que traer *stellen*, y al traerlo se ve la colocación que coincidió.
- **El campo de búsqueda queda siempre a la vista** mientras se recorre la lista (fijo arriba de la vista, no perdido al hacer scroll). Lo mismo para los chips, si se puede sin comerse la pantalla; si hay que elegir, la búsqueda tiene prioridad.
- Cuidá el rendimiento: precalculá el texto normalizado de cada entrada **y de sus colocaciones** una sola vez, como ya hiciste en la Ronda I. Medí y reportá que teclear no renormaliza en cada pulsación.
- **El caso de prueba obligatorio:** buscar `stellen` en alemán devuelve **10 entradas** — 6 verbos propios (*bestellen*, *sich etwas bestellen*, *sich vorstellen*, *etwas nachbestellen*, *etwas richtigstellen*, *jemanden kaltstellen*) y 4 núcleos (*stellen*, *einstellen*, *umstellen* y *bestellen*, este último fusionado con el propio). Dejalo como test.

## Tarea 3 — Guía de testers y versión
- **Quitá el aviso «La lista no es exhaustiva»** de la fila de Verbos: con esta ronda deja de ser cierto. Reescribí la fila contando que debajo de un verbo pueden aparecer las frases donde se usa. **La guía tiene que seguir entrando en una hoja (2 carillas del PDF), así que no la alargues**: si sumás, recortá en otra parte. El `.pdf` lo regenera Overview.
- **versionCode 8, versionName 0.2.6.** Sección nueva en `FALTANTES.md`.
- De paso, arreglá el test `no hay filtro de nivel, ni chips ni selector de niveles en la vista` (`VistaVerbosTest`): hoy comprueba que no aparecen los textos «A2», «B1», «C1» ni «C2» y pasa solo porque en su fixture todos los verbos son B2, cuando la línea extra **sí** muestra el nivel. Lo correcto es comprobar que no existe un `GrupoChips` de niveles.

## Fuera de alcance
Contenido, esquema, assets más allá de la tarea 0, ícono y splash, planilla, audio, progreso. No inventes glosas para los núcleos: la decisión está tomada (FALTANTES 37) y un núcleo se muestra sin traducción propia.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde después de la tarea 0.
3. Tests: fusión por infinitivo (con los 32 DE y 65 EN contados sobre los assets reales); el núcleo no es marcable y la colocación sí; la colocación no aparece suelta en la vista; chips con `claseNucleo`; búsqueda por colocación y por su traducción; el caso `stellen` con 10 entradas; el campo de búsqueda visible tras hacer scroll.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 62 913 943 B de la Ronda I.

## Informe
Un commit por tarea. Entradas por idioma (propias, núcleos, fusionadas y total), tamaño del APK, qué tests agregaste, decisiones donde el prompt no cerraba, y si los totales no dan 470 / 559, por qué.
