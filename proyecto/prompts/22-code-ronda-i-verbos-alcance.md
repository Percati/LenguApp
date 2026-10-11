# Prompt para Code — Ronda I: alcance de la vista Verbos y búsqueda unificada

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (P3, P7), `proyecto/FALTANTES.md` **32** y **33**. Partí del `origin/main` actual (Ronda H, 0.2.4 / versionCode 6). No pushees: un parche por tarea. No toques contenido, esquema ni assets.

Es una ronda corta: tres arreglos sobre lo que ya hiciste en la Ronda H, más la versión.

## Tarea 1 — La vista Verbos muestra **todos los verbos del idioma**, sin recortar por nivel
En la Ronda H la vista quedó atada al **nivel actual del idioma en Ajustes** (`idiomasAprendidos[idioma]`). Eso no era lo que se decidió: «sin filtro de nivel» significa que **el nivel no es una dimensión de esta vista**, no que se fije en uno. Con el recorte actual la vista esconde la mayor parte del material y, sobre todo, **rompe la tarea 2 de la Ronda H**: las familias de verbos separables viven repartidas entre niveles. Medido por Overview sobre el contenido real: la familia de *stellen* tiene 6 miembros en A2, B1, C1 y C2 (*bestellen* A2, *sich bestellen* y *sich vorstellen* B1, *nachbestellen* C1, *kaltstellen* y *richtigstellen* C2); la de *steigen* está en A2 y B1. Buscar «stellen» con el nivel en B1 muestra 2 de 6, y el usuario no tiene forma de saber que faltan cuatro.
- **Qué hacer:** `verbosDe(idioma)` sin parámetro de nivel: todos los verbos etiquetados de ese idioma, deduplicados por el original, alfabéticos. Los chips de clase y sus cuentas se calculan sobre ese conjunto (P3). DE pasa de 124 a 305 formas y EN a 360; es lo esperado.
- **El nivel no desaparece de la vista, pero deja de filtrar:** en la línea `extra` de cada fila, además del régimen (alemán) o la separabilidad (inglés), agregá el nivel más bajo en que aparece ese verbo, como dato y no como filtro (por ejemplo `A2 · von + Dat`). Es información útil y evita que la lista parezca plana.
- Sacá el rótulo «Nivel B2» que hoy encabeza la vista: ya no describe lo que se ve.
- **No** agregues un filtro de nivel (ni chips, ni selector). Es una decisión cerrada de Fer.
- Ojo con el rendimiento: son unos 300-360 ítems en una `LazyColumn`, con la búsqueda filtrando mientras se escribe. Si hace falta, memorizá el conjunto normalizado para la búsqueda en lugar de normalizar en cada tecla.

## Tarea 2 — Arreglar los textos que quedaron mintiendo
- El estado vacío dice «Prueba con otro idioma o nivel», pero en esta vista **el nivel no se puede cambiar**. Reescribilo en los 6 idiomas sin mandar al usuario a algo que no puede hacer ahí. Con el cambio de la tarea 1 este estado solo aparece si el idioma no tiene **ningún** verbo etiquetado, así que el texto tiene que decir eso.
- Revisá por el mismo motivo el mensaje de «sin resultados» y la fila de la guía de testers que escribiste en la Ronda H: la guía dice «los verbos del idioma y del nivel que estás viendo» y deja de ser cierto.

## Tarea 3 — Unificar la búsqueda de «Vocabulario y expresiones» con la de Verbos
Es tu propia propuesta de la Ronda H y la acepto: `coincide()` de `Biblioteca.kt` usa `lowercase()` plano, así que ahí `uber` **no** encuentra *übernehmen*, y en Verbos sí. Dos vistas de la misma pantalla que buscan distinto es un error de uso esperando a pasar. Pasá esa búsqueda a `normalizarBusqueda` y agregá sus tests. No cambies **qué** campos busca cada vista (la de léxico busca también la función; dejala así).

## Tarea 4 — Versión y cierre
**versionCode 7, versionName 0.2.5.** Guía de testers con la versión y la fila de Verbos corregida. **No** regeneres el `.pdf` (lo hace Overview). Sección nueva en `FALTANTES.md`.

## Fuera de alcance
Ícono y splash (Fer todavía no tiene los archivos). Contenido, esquema, assets, planilla, audio, progreso. Los `contentDescription` de las flechas de semana (`"< Semana"`): quedan como están, es cosmético y lo anotamos.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit. Los tests de la Ronda H que daban por buena la atadura al nivel **hay que actualizarlos**, no borrarlos: que pasen a comprobar lo contrario.
2. Test explícito de lo que motivó la ronda: buscar `stellen` en alemán devuelve los 6 miembros de la familia, con cualquier nivel en Ajustes. Y buscar `uber` en la vista de léxico encuentra *über…*.
3. Los dos guardas de `preBuild` en verde, sin tocar los assets.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 62 913 939 B de la Ronda H.

## Informe
Un commit por tarea. Cuántos verbos quedan a la vista por idioma (sin cortar por nivel) y cuántos por clase, los tests que cambiaste y por qué, el tamaño del APK, y las decisiones donde el prompt no cerraba.
