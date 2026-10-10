# Prompt para Code — Ronda E (reglas de prevención, Biblioteca, planilla, desafío, Acerca de)

Leé `CLAUDE.md` (reglas 1-14), `proyecto/REGLAS-PREVENCION.md` y `FALTANTES.md` secciones 18 y 19. **Esperá a que estén aplicados**: (a) el parche de Overview de esta ronda (nombres en 6 idiomas, esquema, verificador, publicación) y (b) el parche de Traducciones de `erroresContrastivos` A2/B1 (y los assets regenerados por Overview después). Commit por tarea, `compileDebugKotlin compileDebugUnitTestKotlin test` en verde antes de cada uno. No pushees.

## Tarea 1 — Nombres de topic y de categoría en el idioma de app
`proyecto/data/banco.json` ahora trae `topicNombresI18n` ({T01: {es, en, de, fr, it, pt}, …}) y `proyecto/data/categorias-uso.json` trae `nombres` ({categoría en español: {es, en, de, fr, it, pt}}). El valor guardado en `categoriasUso` sigue siendo el español (es la clave canónica; no cambia).
- Gradle: `generarTopicNombresAssets` pasa a extraer `topicNombresI18n`; el cargador lee el mapa por idioma. `topicNombres` (solo español) queda en el JSON para las herramientas, no lo uses en la app.
- Biblioteca y Guardados muestran el nombre de topic y de categoría **en el idioma de app** (hoy salen en español). Fallback: español, y un test que falle si falta algún idioma en alguna clave.

## Tarea 2 — Planilla del profesor
a. **Idioma**: toda la planilla, **incluido el nombre del topic**, en el idioma que se aprende (`topicNombresI18n[topic][idiomaAprendido]`). Hoy el topic sale en español en la planilla de DE B1 y en la de EN C1 (el primero: «Educación y formación continua»).
b. **Una hoja (dos carillas) como máximo.** Si la segunda carilla queda con 3 líneas o menos, compactar para que todo entre en una: probá en orden (1) reducir interlineado y espacio entre secciones, (2) bajar el cuerpo hasta un mínimo legible (9 pt), (3) acortar márgenes hasta 15 mm. Si aun así no entra, dejarlo en dos carillas. Nunca más de dos.
c. Tests sobre las fichas **reales** de assets, todos los pares (idioma × nivel A2-C2) y un repaso y un Survival de cada uno: cantidad de carillas ≤ 2; ningún caso con ≤ 3 líneas en la carilla 2 sin haber agotado los pasos de compactación; ningún texto de la planilla en idioma distinto al aprendido (comprobá que cada cadena fija viene de `planilla-profesor.json[idiomaAprendido]` y el topic de `topicNombresI18n`).

## Tarea 3 — Biblioteca: filtros solo con valores que existen
- Los chips de **topic** (Vocabulario) y de **categoría** (Expresiones) se calculan en ejecución a partir de los ítems del **idioma de la pestaña**, sobre todos sus niveles. Un valor sin ningún ítem en ningún nivel de ese idioma **no se muestra**. Hoy, p. ej., «Caso y declinación» aparece en inglés sin tener ni una expresión.
- Que un chip dé vacío para un nivel concreto sí está permitido. Con el filtro de nivel activo, mostrá junto a cada chip su contador (con ese filtro); nunca ocultes el chip por dar 0 en el nivel elegido, pero nunca lo muestres si da 0 en todos.
- Los botones «Quitar filtros» y el resto de Ronda C no cambian.
- Tests contra los assets reales, para cada idioma aprendido que tenga fichas: cada chip mostrado tiene ≥ 1 ítem sumando todos los niveles. Test de propiedad: el conjunto de chips del idioma A no se deriva del idioma B.

## Tarea 4 — Desafío del fin de semana distinto de la misión
Hoy muestra la misión tal cual (`ContenidoSemanalScreen.kt`, `modoDesafio`). Decisión de Overview (revertible): **no escribir un desafío nuevo por semana (serían ~600 textos), sino un encuadre fijo que hace de la misión una versión «en vivo»**.
Con el desafío activo la pantalla muestra, en este orden, bajo el título «Reto del fin de semana» (clave nueva, 6 idiomas):
1. Una línea: «La misión de la semana, pero en modo real.»
2. Reglas (en idioma de app, texto de `TextosInterfaz`, 6 idiomas):
   - A2/B1: «1. De corrido, sin leer. 2. Una sola toma. 3. Díselo a una persona o grábate en audio. 4. Escúchalo: ¿se entiende? Repítelo una vez.»
   - B2/C1/C2: «1. Sin notas, una sola toma de {oralMin} min. 2. Cambia de interlocutor a mitad (de un amigo a tu jefe, o al revés). 3. Grábate y apunta una cosa que mejorarías. 4. Repite con ese cambio.»
3. «La misión:» (rótulo) + la consigna de la ficha en pequeño y **solo los dos primeros requisitos**.
`{oralMin}` sale de `evidencia.oralMin`. Test: el texto visible del desafío ≠ el texto visible de la misión, en todos los niveles; el desafío no persiste nada (regla 13).

## Tarea 5 — `erroresContrastivos` bilingüe en A2/B1 (modelo y UI)
Ver el esquema (`ficha.schema.json`): en A2/B1 cada ítem es `{<aprendido>: texto, <claveIdiomaApp>: traducción}`; en B2+ sigue siendo un string.
- Modelo: `Map<String, List<TextoBilingue>>` (un string plano se lee como `TextoBilingue` de una sola clave).
- UI: con el switch activo se muestra la traducción (la clave del idioma de app); con él apagado, el original. Sin switch (B2+): igual que hoy. Fallback: original.
- Test sobre assets reales: en una ficha DE B1 con app en español, ningún ítem de errores muestra el texto alemán con el switch activo.

## Tarea 6 — Acerca de y línea fija
- Pantalla estática «Acerca de» (acceso desde la barra superior), 6 idiomas: qué es y qué no es («No es un curso: fija lo que ya aprendiste en un curso de tu nivel»), privacidad (sin Internet, sin cuenta, solo lee la fecha), licencias (GPL-3.0 y CC BY-SA 4.0), versión. Sin persistencia.
- Bajo el nombre de la app en la pantalla principal, una línea fija: «Para fijar lo que ya aprendiste en tu curso.» (6 idiomas).

## Tarea 7 — Que las reglas se verifiquen solas
Agregá una tarea Gradle `verificarConsistenciaContenido` enganchada a `preBuild` que recompone con `componer.py` y corre `proyecto/tools/verificar_consistencia.py --build <dir>`; falla el build si hay problemas.

## Informe
Commit por tarea, tests, decisiones donde el prompt no cerraba (en especial: cualquier chip o texto que encuentres sin resolver al idioma correcto en otros pares). Actualizá `FALTANTES.md`.
