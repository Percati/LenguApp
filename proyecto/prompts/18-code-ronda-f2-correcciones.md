# Prompt para Code — Ronda F2 (correcciones de la prueba de la 0.2.1)

Leé `CLAUDE.md` (reglas 1-14), `REGLAS-PREVENCION.md`, `FALTANTES.md` 24 y 26. **Esperá a que estén aplicados**: el parche de Traducciones del prompt 17 (R8) y el parche de assets de Overview que lo sigue; si no, el verificador de `preBuild` falla. Commit por tarea, `compileDebugKotlin compileDebugUnitTestKotlin test` en verde antes de cada uno. No pushees. Versión nueva: `0.2.2`, `versionCode` `4`.

## Tarea 1 — Guardados: mostrar igual que la Biblioteca (error grave)
**Hoy:** `FilaGuardado` (`GuardadosScreen.kt`) muestra `item.texto.resolver(idiomaBase, item.idioma)`: solo la **traducción** al idioma de app, y la función también resuelta en idioma de app. El usuario guarda una palabra de inglés y en Guardados ve únicamente su traducción.
**Debe ser:** exactamente lo que muestra `FilaBiblioteca`, **independientemente del idioma de app y del switch de traducción de A2/B1**:
1. Línea 1: la palabra o expresión **en el idioma que se aprende** (el original).
2. Línea 2: su **traducción al idioma de app** (`idiomaBase`), con la misma regla que la Biblioteca (`traduccionParaMostrar`/clave ausente: si no hay traducción, **se omite**, no se repite el original ni se muestra un hueco).
3. Línea 3: el **contexto/función**, **solo si existe y no está en blanco**, y **siempre en el idioma que se aprende** (`resolver(item.idioma, item.idioma)`). Vocabulario no la tiene; muchas expresiones tampoco.
Los snapshots ya guardados traen la clave del idioma aprendido y las traducciones (`aTextoBilingue`), así que **no hace falta migración** de Room: confirmalo con un test.
Para que no vuelvan a divergir: **un solo composable compartido** para la fila de vocabulario/expresión que usen la Biblioteca y Guardados (la estrella es lo único que cambia). Los « » del texto de la traducción siguen igual (`textoConMarcado`).
Tests: Guardados × (en, de) × 6 idiomas de app × (A2, B1, B2) × switch on/off → las tres líneas con los idiomas correctos; ampliá `MatrizSuperficiesTest` con la superficie «fila de Guardados». Un guardado sin traducción y otro sin función.

## Tarea 2 — Desafío de fin de semana: distinto de la misión, condiciones legibles
**Hoy:** el desafío es una línea («La misión de la semana, pero en modo real»), reglas en texto corrido, y la **consigna y 2 requisitos de la misión copiados**. Fer: sigue siendo una copia de la misión.
**Debe ser** (el resto de la ficha sigue oculto mientras el reto está activo; regla 13: el switch arranca apagado y no se guarda):
- **No se muestra la consigna ni los requisitos de la misión**, ni el rótulo «La misión:». Se elimina `RETO_ROTULO_MISION` y su uso.
- Encabezado «Desafío de fin de semana» + una línea nueva (`RETO_LINEA`, 6 idiomas) que lo describe como **hablar en voz alta**, no como repetir la tarea: ES «Dilo en voz alta, como en una conversación de verdad.»
- Una fila «**Tema**» con el nombre del topic en el idioma de app (ya existe en `TopicNombres`) y una fila «**Habilidad**» con el título de la ficha.
- «**Condiciones**»: 3 o 4 filas **con ícono** (Material Icons: `Mic`, `Timer`, `Group`/`SwapHoriz`, `Replay`; no inventes íconos), una idea por fila, texto corto. A2/B1: de corrido y sin leer · una sola toma · díselo a una persona o grábate · escúchalo y repítelo una vez. B2/C1/C2: sin notas · una toma de `{oralMin}` min · cambia de interlocutor a mitad · grábate y apunta una cosa que mejorarías. Reescribí `RETO_REGLAS_*` como textos cortos de una fila (los 6 idiomas, tuteo/informal, P7).
- «**Frases para usar**»: hasta 3 expresiones de la ficha **en el idioma que se aprende** (la misma selección que usa `PromptVoz` para `{expresiones}`; reutilizala, no dupliques), una por fila, con el encabezado en idioma de app: «Intenta usar al menos dos:». Si la ficha no tiene expresiones, la sección no aparece.
- Tests: ninguna de las strings de la consigna/requisitos aparece en el modo desafío; las filas por nivel; las expresiones en idioma aprendido; 6 idiomas.

## Tarea 3 — Interlineado y espaciado que escalan con la letra
Fer: el espacio entre viñetas es muy grande y parece no escalar con el tamaño de letra. Causa probable: `Vinieta`/`Seccion` usan `Arrangement.spacedBy(8.dp)` fijo en dp y/o los estilos del tema no definen `lineHeight` proporcional.
- Diagnosticá la causa real y reportala (no supongas).
- Definí el **interlineado en `sp`** con proporción al tamaño (cuerpo ≈ 1,4-1,5) en la tipografía del tema, y el **espacio entre viñetas y entre renglones de lista** como múltiplo del tamaño de letra (convertido con `LocalDensity`/`sp`), no en dp fijos. Espacio entre viñetas ≈ 0,3-0,4 × tamaño de letra; entre secciones ≈ 1 × tamaño.
- Aplicalo a `Vinieta`, `Seccion`, las listas de condiciones de la Tarea 2 y las filas de Biblioteca/Guardados.
- Test con `fontScale` 1,0 / 1,3 / 1,6 (por ejemplo vía `LocalDensity` con `fontScale`) que compruebe que el espacio crece con la escala y que nada se corta.
- Esta tarea fija las **proporciones**; el color y las tipografías seleccionables llegan en la Ronda G, que se apoya en esto.

## Tarea 4 — Encabezado fuera de la pantalla principal
La pantalla principal pierde mucho alto con el encabezado (nombre, línea fija, «Acerca de»). Sacalo de ahí:
- Quitá `EncabezadoApp` de la pantalla principal; el contenido sube y gana espacio.
- En **Ajustes**, arriba del todo: el nombre de la app, la línea fija («Para fijar lo que ya aprendiste en tu curso.», que ya existe en 6 idiomas) y una fila **«Acerca de»** que navega a la pantalla actual. Nada más cambia en Ajustes.
- Actualizá `publicacion/LEEME.md` (sección «Cómo dejar claro que NO es un curso»): la línea fija ya no está en la pantalla principal sino en Ajustes y «Acerca de».
- Tests: la pantalla principal no contiene el nombre ni la línea; Ajustes sí; la navegación a Acerca de funciona.

## Tarea 5 — «Acerca de» más completo, en registro formal
Registro **formal** (sin coloquialismos), pero seguí tuteando como en el resto de la app: ni «usted» ni voseo. Texto en español de partida (traducilo a los otros 5 idiomas con el mismo registro; FR «tu», PT «você» de Brasil; **marcá en el informe que fr/it/pt no los revisó un hablante nativo**). Secciones y texto:
1. **Qué es y qué no es:** «LenguApp ofrece cada semana una ficha de práctica de alemán o de inglés, entre los niveles A2 y C2. Cada ficha propone una habilidad y un tema, con vocabulario y expresiones con audio, una misión breve, microtareas y un texto para que una inteligencia artificial corrija tu producción. No es un curso: no enseña desde cero, sino que consolida lo que ya aprendiste en un curso de tu nivel.»
2. **Cómo está pensada** (sección nueva): «Las habilidades se repiten varias veces al año con distinta profundidad, porque la repetición espaciada es la base del método. Cada ocho semanas se dedica una semana al repaso y, de forma periódica, a la supervivencia. El contenido se publica en ediciones anuales. Puedes practicar alemán o inglés, y la interfaz está disponible en español, inglés, alemán, francés, italiano y portugués.»
3. **Privacidad:** el texto actual, más: «No utiliza cuentas ni estadísticas de uso, no solicita permiso de Internet ni de ubicación, y no envía información a terceros.» (Verificá contra el manifest y `PRIVACY.md` antes de dejar cada frase; lo que no sea verdad, se quita y se reporta.)
4. **Licencias y créditos:** «El código se distribuye bajo la licencia GPL-3.0 y el contenido bajo CC BY-SA 4.0.» + una línea con las voces sintéticas y sus licencias **tal como figuran en el repositorio** (audio/Piper); si no están documentadas, **no inventes**: reportalo.
5. **Versión** (como ahora) y una frase: «Esta es una versión de prueba; tus comentarios ayudan a mejorarla.»
Todas las strings en `TextosInterfaz` con 6 idiomas (P7). Test: ninguna clave nueva sin sus 6 idiomas; R6-equivalente de registro sobre las strings nuevas (sin voseo ni «usted»).

## Tarea 6 — Exportar e importar Guardados (confirmada por Fer, 10-oct-2026)
Dos archivos, con el selector de archivos del sistema (Storage Access Framework: `CreateDocument` / `OpenDocument`), **sin permisos nuevos y sin `INTERNET`**. Botones «Exportar» e «Importar» en la pantalla de Guardados (6 idiomas, P7).
- **Exportar a Anki (TSV):** UTF-8, cabeceras `#separator:tab` y `#html:false`; columnas: anverso = original en el idioma que se aprende, reverso = traducción al idioma de app + (si existe) el contexto en el idioma aprendido, etiquetas = `idioma nivel`. Escapá tabuladores y saltos de línea. Se exportan los items del idioma activo con los filtros vigentes o todos (elegí lo más simple y reportalo).
- **Copia de seguridad (JSON):** `lenguapp-guardados.json` con `version: 1` y los items tal cual (idioma, nivel, tipo, texto, función, skillIdOrigen).
- **Importar (solo el JSON):** validá versión y estructura, límite de tamaño razonable, rechazá lo que no cumpla con un mensaje claro; **fusioná sin duplicar** por la identidad (idioma, tipo, texto) y no pises lo existente. Informá cuántos se agregaron y cuántos ya estaban. Nada se ejecuta ni se interpreta del archivo más allá de esos campos.
- Regla dura 2: leer o escribir un archivo **que el usuario elige en ese momento** es una acción del usuario, no una lectura del dispositivo; documentalo en `CLAUDE.md` (enmienda como la del idioma del sistema) y en `PRIVACY.md`/«Acerca de» si cambia lo que decís.
- Tests: ida y vuelta (exportar→importar a una base vacía deja los mismos items), importación con duplicados, archivo corrupto, versión desconocida, TSV con tabuladores y saltos en el texto.

## Tarea 7 — Índice de habilidades (confirmada por Fer, 10-oct-2026)
Lista de solo lectura de las habilidades del idioma activo, para volver a una concreta sin recorrer semanas. **Dentro de la Biblioteca**, como segunda vista («Vocabulario» | «Habilidades»), para no sumar otro botón a la barra (Fer quiere más espacio para el contenido).
- Cada fila: el nombre de la habilidad (el título de la ficha en el nivel activo, con la regla de idioma de la ficha), y las **semanas del año en que aparece** (por ejemplo «sem. 6, 20, 33»), del año que se está viendo.
- Tocar una fila abre esa semana (la ficha). Si la habilidad no tiene ficha en el nivel activo, no aparece.
- Sin estado persistente (regla 4): nada de «vista» o «hecho». Los nombres y rótulos en idioma de app (P7, 6 idiomas); los títulos de ficha según la regla de la ficha.
- Tests: el índice de EN y DE en cada nivel lista las habilidades que existen en los assets, con sus semanas correctas según el calendario; abrir una fila navega a la semana.

## Fuera de alcance
Temas, tipografías y estilo visual (Ronda G); ícono y splash; contenido.

## Informe
Commit por tarea, tests, tamaño del APK, causa real del interlineado, decisiones donde el prompt no cerraba, textos de Acerca de que quitaste por no ser verdad. Actualizá `FALTANTES.md` (sección 26).
