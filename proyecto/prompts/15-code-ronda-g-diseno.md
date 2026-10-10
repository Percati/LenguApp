# Prompt para Code — Ronda G (dirección visual: temas, tipografías, estilo)

Leé `CLAUDE.md` (reglas 1-14), `REGLAS-PREVENCION.md` (P1, P7), `docs/inspiracion-ui.md` y `FALTANTES.md` secciones 24 y 25. Partí de `origin/main` actualizado. **No empieces mientras haya otra ronda de Code sin aplicar** (Fer puede mandar arreglos de la prueba de la 0.2.1 antes; esos van primero). Commit por tarea, `compileDebugKotlin compileDebugUnitTestKotlin test` en verde antes de cada uno. No pushees.

## Dirección decidida por Fer (10-oct-2026)
- **Paletas y tipografías seleccionables:** inspiración Bear. **Diseño bien hecho:** Material 3 (componentes estándar, sin reinventarlos). **Fichas, Guardados y Biblioteca:** Things 3, Day One, Readwise Reader, en ese orden: tipografía de lectura cuidada, mucho aire, listas de filas con línea fina en vez de tarjetas, marcas discretas.
- **Lo que NO queremos:** Duolingo. Nada de mascota, racha, anillos de progreso, confeti ni colores saturados de «logro». La app no guarda progreso (regla 4) y el diseño no debe sugerirlo.
- Es una ronda de **presentación**: no cambia contenido, ni comportamiento, ni pantallas nuevas.

## Tarea 1 — Cuatro temas + «Sistema»
Hoy `Tema.kt` tiene dos familias (`ACADEMIA`, `EDITORIAL`) × modo claro/oscuro/sistema. Reemplazá las familias por **PAPEL (por defecto), TERRACOTA, MAR, GRAFITO** y agregá **SISTEMA** (color dinámico de Material 3, Android 12+/API 31+; si `minSdk` es menor, en esos equipos SISTEMA cae a PAPEL y no se ofrece). El selector de modo (claro/oscuro/sistema) sigue igual y es independiente.

Valores (sin retocar; ya verifiqué AA en las 16 combinaciones de texto/fondo/superficie/acento). `acento` es el color principal; `onAcento` es blanco en claro y el fondo en oscuro:

| Tema | Modo | fondo | superficie | texto | acento | secundario |
|---|---|---|---|---|---|---|
| Papel | claro | #F7F6F2 | #FFFFFF | #1F2328 | #2F5D8A | #5A6169 |
| Papel | oscuro | #14171A | #1E2226 | #E6E8EA | #8DB4DE | #A3ABB2 |
| Terracota | claro | #FAF5EF | #FFFFFF | #2B2320 | #B4542F | #6B5D55 |
| Terracota | oscuro | #1A1512 | #251E1A | #F0E7DF | #E59A78 | #BDADA3 |
| Mar | claro | #F2F7F7 | #FFFFFF | #172326 | #1F7A80 | #4F6568 |
| Mar | oscuro | #0F1A1C | #172426 | #DFEDEE | #6FC4C9 | #9FB8BB |
| Grafito | claro | #F5F5F5 | #FFFFFF | #212121 | #3A3A3A | #5C5C5C |
| Grafito | oscuro | #161616 | #212121 | #EAEAEA | #CFCFCF | #A8A8A8 |

- Mantené la regla 60-30-10 y el mapeo a `ColorScheme` ya documentado en `Tema.kt` (acento solo para lo accionable). La estrella de Guardados usa `acento`, no un color decorativo.
- Los nombres se muestran en el idioma de app (P7, 6 idiomas): ES Papel/Terracota/Mar/Grafito · EN Paper/Terracotta/Sea/Graphite · DE Papier/Terrakotta/Meer/Graphit · FR Papier/Terre cuite/Mer/Graphite · IT Carta/Terracotta/Mare/Grafite · PT Papel/Terracota/Mar/Grafite · «Sistema» ya existe.
- **Migración del ajuste guardado:** `ACADEMIA`→`PAPEL`, `EDITORIAL`→`MAR`, cualquier valor desconocido→`PAPEL`. Test de migración. No se pierde ningún otro ajuste.
- Actualizá `ContrasteWcagTest`: los 4 temas × 2 modos, todos los pares texto/fondo, texto/superficie, secundario/fondo, secundario/superficie, `onAcento`/acento y el chip seleccionado (`secondaryContainer`) con su texto, ≥ 4,5:1. Si algún par falla, reportalo, no lo corrijas en silencio.

## Tarea 2 — Tres tipografías seleccionables
Opciones: **«Sistema»** (por defecto, la del dispositivo), **«Lectura»** (Literata, serifa) y **«Clara»** (Atkinson Hyperlegible, sans de alta legibilidad). Nombres en 6 idiomas (P7).
- Solo fuentes con licencia **OFL**, de los repositorios oficiales (Google Fonts / Braille Institute). Agregá cada licencia a `LICENSES/` y mencioná las fuentes en «Acerca de» (licencias). Compatibles con GPL-3.0 y CC BY-SA 4.0.
- Embebidas en `res/font/`, **sin descarga ni proveedor en la nube** (regla 1: sin `INTERNET`; nada de Downloadable Fonts).
- Pesos: Regular, Italic (las fichas usan `*cursiva*`), Medium y Bold. **Subset a Latin + Latin Extended** (tildes, diéresis, ñ, ã, ç, « »). Presupuesto: **+1,5 MB como máximo** sobre el APK actual (62 578 922 bytes); reportá el tamaño real y el método de subset.
- Se aplican a toda la escala tipográfica de Material 3 del tema, no solo a las fichas. El ajuste es independiente del tema de color.
- Test: cada fuente tiene glifos para « » ‹ › á é í ó ú ñ ü ö ä ã õ ç à è ì ò ù â ê î ô û œ y los caracteres de las notas fonéticas que ya se usan; si una fuente no cubre alguno, reportalo.

## Tarea 3 — Ajustes: selectores con vista previa
En Ajustes, el selector de **tema** muestra por cada opción su nombre y 3 puntos de color (fondo, superficie, acento) en el modo actual; el de **tipografía** muestra el nombre escrito en su propia fuente con una muestra corta («Aa»). Material 3 estándar (`ListItem`/`FilterChip`/`SegmentedButton`), sin componentes propios. Orden de ajustes: idioma, tema, modo, tipografía.
Pendiente aparte, no ahora: los chips de idioma de Ajustes siguen mostrando `EN`/`DE`; ponelos con el nombre del idioma (como las pestañas) si queda en el mismo archivo y es una línea.

## Tarea 4 — Estilo de lectura y listas (Things 3 / Day One / Readwise)
- **Ficha:** columna de lectura con ancho máximo cómodo en pantallas anchas, interlineado de ~1,5 en el cuerpo, jerarquía clara (título, subtítulo, secciones plegables), más aire entre secciones, sin cajas anidadas. Los fragmentos en otro idioma siguen entre « » (P5).
- **Guardados y Biblioteca:** filas con separador de línea fina (`HorizontalDivider`), sin tarjetas con sombra; estrella vacía/llena discreta; chips de filtro Material 3 sin sobrecargar; estados vacíos como invitación breve, no como disculpa.
- **Movimiento:** transiciones sutiles; respetá «quitar animaciones» del sistema.
- Cuidá que nada de esto rompa la regla P8 (planilla en una hoja): la planilla PDF **no cambia** de aspecto en esta ronda.

## Tarea 5 — Pantalla de carga (preparación, sin ícono)
Integrá la API de pantalla de carga (`androidx.core:core-splashscreen`) con fondo = color de fondo del tema Papel (claro #F7F6F2 / oscuro #14171A) y **sin ícono propio**: Fer todavía no tiene el arte. No inventes un logo. Dejá el ícono de la pantalla de carga y el ícono del lanzador en un solo lugar documentado en `FALTANTES.md` para integrarlos en una ronda H cuando Fer entregue el archivo (adaptive icon con capa monocromática para íconos temáticos y glifo de la splash).

## Fuera de alcance
Ícono del lanzador, arte de la splash, contenido, textos de ficha, planilla, nuevas secciones, estadísticas o cualquier forma de progreso.

## Informe
Commit por tarea, tests, tamaño del APK y delta, fuentes y licencias usadas, decisiones donde el prompt no cerraba, lista de pantallas tocadas. Actualizá `FALTANTES.md` (sección 25).
