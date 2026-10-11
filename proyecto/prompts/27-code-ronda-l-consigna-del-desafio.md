# Prompt para Code — Ronda L: la consigna del desafío, legible y bien encuadrada

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (**P4**, P7), `proyecto/FALTANTES.md` **42**, **43** y **45**. Partí del `origin/main` actual (Ronda K, 0.2.7 / versionCode 9, más el parche de tuteo de Traducciones y los assets regenerados por Overview). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets.**

Ronda corta: dos arreglos sobre la Ronda K, los dos de la misma pantalla, los dos por errores de **mi** prompt anterior.

## Tarea 1 — La consigna no va en tamaño titular
Fer la probó en el teléfono: **se ve enorme y en negrita, y queda feo.** La causa está en el prompt 25, donde pedí «el peso tipográfico más fuerte del bloque», y lo resolviste con `headlineSmall` + `FontWeight.Bold` (`ContenidoSemanalScreen.kt:538`). Para una o dos líneas de titular estaría bien; la consigna es **prosa de dos a cuatro renglones** («Du ziehst in eine neue Wohnung. Beschreib, wohin du deine Möbel stellst und wo sie am Ende stehen.»), y a ese tamaño grita y empuja todo lo demás fuera de la pantalla.
- La consigna pasa a **tamaño de cuerpo, no de titular**: `bodyLarge` o `titleMedium`, con **peso medio** (`FontWeight.Medium`), **sin negrita**. Tiene que leerse como lo primero y lo más importante del bloque, pero por **posición y por aire alrededor**, no por tamaño.
- Si con eso te parece que pierde jerarquía, ganala con espacio arriba y abajo o con color (`onSurface` pleno contra el `onSurfaceVariant` de las líneas de apoyo), **no** agrandando la letra.
- Comprobalo con la letra del sistema al **160 %**, que es donde el titular se vuelve ilegible: el bloque entero (consigna, tema, habilidad, condiciones y frases) tiene que seguir siendo usable.
- Mirá de paso el resto del bloque con ese mismo criterio y decime si algo más quedó desproporcionado.

## Tarea 2 — El encuadre de la línea oral, sin el paréntesis
Hoy dice «Habla 6 min sobre esto, en voz alta y de una sola vez **(aunque la consigna pida escribir)**». El paréntesis fue tu solución honesta a un problema que te dejé yo: `mision.consigna` está redactada para la evidencia **escrita** y en 221 de las 606 fichas empieza con «Escribe…», «Write…» o «Schreiben…», así que la línea y la consigna se contradecían.
- **El arreglo es cambiar el encuadre, no aclarar la contradicción.** La consigna tiene que leerse como **el tema de lo que vas a contar**, no como una orden que la línea desmiente. En español, algo de este tenor (ajustá la redacción si encontrás una mejor, y hacé lo propio en los otros 5 idiomas con el tuteo de siempre, P7):
  > Esto es lo que vas a contar. En voz alta, {oralMin} min, de una sola vez y sin escribir nada:
- **Quitá el paréntesis** en los 6 idiomas. Con el encuadre nuevo sobra, y una consigna que empieza con «Escribe cinco oraciones sobre…» se lee bien como tema.
- Los minutos siguen saliendo de `evidencia.oralMin`, redondeados como ahora.
- P4 no se toca y su test tiene que seguir pasando: el desafío sigue sin mostrar requisitos ni micro-tareas.

## Tarea 3 — Versión y cierre
**versionCode 10, versionName 0.2.8.** Guía de testers: la versión, y revisá la fila del desafío por si el texto nuevo la deja desactualizada. **La guía tiene que seguir entrando en una hoja (2 carillas del PDF)**; el `.pdf` lo regenera Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
Contenido, esquema, assets, la vista Verbos, ícono y splash, planilla, audio, progreso, el tutorial (su guion está en `proyecto/docs/tutorial-guion.md` y se implementa más adelante).

## Un pendiente que es decisión tuya, no mía
Traducciones alargó **25 `promptCorreccion` alemanes** (11 en el prompt 19 y 14 en el 26) porque el imperativo informal es más corto y los dejaba bajo el mínimo de 150 caracteres del esquema. Son frases en estilo de la casa y ya están integradas. Si preferís **bajar el mínimo** del esquema y que esas 25 frases se quiten, decilo con el motivo y lo coordinamos; mi posición es dejarlo como está, porque el mínimo existe para que el prompt sea útil. **No hagas el cambio en esta ronda**, solo opiná.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets.
3. Tests: el bloque del desafío con la letra al 100 % y al 160 %; la consigna ya no usa estilo de titular (que el test mire el estilo, no el tamaño en píxeles); los textos nuevos en los 6 idiomas y sin el paréntesis; P4 sigue pasando.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 62 951 831 B de la Ronda K.

## Informe
Un commit por tarea. El texto final de la línea oral en los 6 idiomas, qué estilo le pusiste a la consigna y por qué, si algo más del bloque te pareció desproporcionado, el tamaño del APK y tu opinión sobre el mínimo de 150 caracteres.
