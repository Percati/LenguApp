# Prompt para Code — Ronda K: pantalla completa y la consigna del desafío

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (**P4**, P7), `proyecto/FALTANTES.md` **40** y **41**. Partí del `origin/main` actual (Ronda J, 0.2.6 / versionCode 8). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets.**

Son dos cosas que encontró Fer usando la app. Ronda corta.

## Tarea 1 — La app usa toda la pantalla (edge-to-edge)
Hoy el contenido empieza **debajo** de la barra de estado y no llega a la zona del notch, así que queda una franja muerta arriba. Tiene que dibujarse de borde a borde, como cualquier app moderna de Android.
- Activá edge-to-edge (`enableEdgeToEdge()` en `MainActivity`, o `WindowCompat.setDecorFitsSystemWindows(window, false)` según lo que use el proyecto) y que el fondo del tema se vea **detrás** de la barra de estado y la de navegación.
- **Nada de contenido tapado:** aplicá los insets donde corresponde (`WindowInsets.safeDrawing` / `systemBars`), con cuidado en las listas, que tienen que poder **desplazarse por debajo** de las barras pero terminar con el relleno suficiente para que la última fila no quede escondida (`contentPadding`, no `padding` del contenedor).
- Los íconos de la barra de estado tienen que ser legibles **en los 4 temas y en claro y oscuro**: claros sobre fondo oscuro y oscuros sobre fondo claro, cambiando con el tema elegido, no fijos.
- Cuidá el **notch y la barra de gestos** en horizontal: el texto no puede quedar bajo el recorte de la cámara.
- Probalo con barra de navegación por botones **y** por gestos.

## Tarea 2 — El desafío del fin de semana necesita una consigna de verdad
El rediseño de la Ronda F2 quedó bien de aspecto, pero **se llevó puesta la tarea**: hoy el desafío dice cómo hacerlo (una toma de 6 min, sin notas, cambiá de interlocutor, grabate) pero **no dice qué hacer**. «Tema: Educación» y «Habilidad: Summarising Complex Information» no son una consigna: son etiquetas.
- **La ficha ya tiene la consigna.** `mision.consigna` es una situación concreta y accionable («Du ziehst in eine neue Wohnung. Beschreib, wohin du deine Möbel stellst und wo sie am Ende stehen»). Usala como **encabezado de la actividad**, arriba de todo, con el peso tipográfico más fuerte del bloque: es lo primero que se lee, antes de las condiciones.
- **Reencuadrada para hablar, no copiada.** Antes de la consigna va una línea con la tarea oral concreta, armada con los datos que ya existen en la ficha: los minutos de `evidencia.oralMin` y el tema. Algo del tipo «Hablá **{oralMin} minutos** sobre esto, en voz alta y de una sola vez:» y debajo la consigna. Redactalo bien en los 6 idiomas (P7), con el tuteo de siempre y sin inventar datos que la ficha no tenga.
- **P4 sigue valiendo y no la estás rompiendo:** el desafío no puede ser la misión repetida, pero *la misión completa* son consigna **más** requisitos **más** micro-tareas más la evidencia escrita. Reusar **solo la consigna**, con encuadre oral y sin los requisitos ni las micro-tareas, mantiene el desafío distinto de su fuente. El test de P4 («texto visible del desafío ≠ texto visible de la misión») tiene que seguir pasando: actualizalo para que compare el bloque completo, no la presencia de la consigna, y que falle si el desafío vuelve a mostrar requisitos o micro-tareas.
- `RETO_LINEA` («Dilo en voz alta, como en una conversación de verdad») queda **redundante** con la línea nueva: quitala o fundila en la nueva. No dejes las dos.
- El orden del bloque queda: **consigna oral** → Tema y Habilidad → Condiciones → Frases para usar. Lo demás del rediseño de F2 no se toca.
- Si alguna ficha no tuviera `mision.consigna`, el bloque no puede quedar sin encabezado: caé a la descripción de la ficha y dejalo cubierto por un test.

## Tarea 3 — Versión y cierre
**versionCode 9, versionName 0.2.7.** Guía de testers: la versión, y revisá si alguna fila habla del desafío o del aspecto de la pantalla y quedó desactualizada. **La guía tiene que seguir entrando en una hoja (2 carillas del PDF)**; el `.pdf` lo regenera Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
Contenido, esquema, assets, la vista Verbos, ícono y splash, planilla, audio, progreso.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets.
3. Tests: el desafío muestra la consigna como encabezado; sigue sin mostrar requisitos ni micro-tareas (P4 actualizado); la ficha sin `mision.consigna` no queda sin encabezado; los textos nuevos en los 6 idiomas; el contenido no queda tapado por las barras del sistema.
4. Manifest sin `INTERNET` ni permisos nuevos.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 62 935 447 B de la Ronda J.

## Informe
Un commit por tarea. Cómo resolviste los insets y dónde quedó el `contentPadding`, el texto final de la línea oral en los 6 idiomas, qué pasó con `RETO_LINEA`, el tamaño del APK y las decisiones donde el prompt no cerraba. **Avisá si al probar edge-to-edge alguna pantalla quedó con el contenido bajo las barras y no lo pudiste resolver**: es mejor saberlo que descubrirlo en el teléfono.
