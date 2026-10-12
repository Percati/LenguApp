# Prompt para Code — Ronda N: sacar el audio inglés del release y la pantalla de bienvenida

Leé `CLAUDE.md` (reglas duras **1**, **2** y **4**), `proyecto/REGLAS-PREVENCION.md` (P7), `proyecto/FALTANTES.md` **44**, **48**, **49** y **50**, `proyecto/docs/licencias-voces.md` y `proyecto/docs/tutorial-guion.md` (punto 0). Partí del `origin/main` actual (Ronda M, 0.2.9 / versionCode 11). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets de contenido.**

Dos cosas independientes: una obligación legal que dejaste abierta vos mismo, y la pantalla que más le falta a la app.

## Tarea 1 — Los `.ogg` ingleses no pueden viajar en un release
Lo detectaste bien al cerrar la Ronda M: `BuildConfig.AUDIO_INGLES` evita que **suenen**, pero el problema de las licencias (`en_US-ryan`, CC BY-NC-SA 4.0; `en_US-lessac`, Blizzard 2013 — solo investigación, sin uso comercial ni redistribución) es de **redistribución**, y `assets/audio/EN/` (4133 archivos, ~26 MB) viaja igual.
- **Sacalos del empaquetado de release.** La forma que prefiero es mover la carpeta al `sourceSet` de debug (`app/src/debug/assets/audio/EN/`), porque es imposible que se cuele por accidente: un release ni siquiera la ve. Si encontrás una forma mejor (un `ignoreAssetsPattern` por variante, por ejemplo), usala y explicá por qué es igual de segura.
- **El índice también:** `indice.json` lleva las entradas inglesas aunque el parser las descarte al cargar. No es ilegal, pero son ~170 KB inútiles en el release y, sobre todo, es una lista de archivos que no están. Decidí vos si se filtra al empaquetar o se deja, y contá el motivo.
- **El debug no cambia:** Fer tiene que poder seguir probando el audio inglés.
- **Test:** que el APK de release no contenga ningún `assets/audio/EN/` y que el de debug sí. Si no podés comprobarlo desde un test, hacelo con una tarea de Gradle que falle, y decilo en el informe.
- Cuando se regeneren los clips con una voz de corpus limpio, esto se revierte junto con el interruptor. Dejá las dos cosas documentadas en el mismo lugar.

## Tarea 2 — Pantalla de bienvenida (primer arranque)
Es el punto 0 de `docs/tutorial-guion.md`. **No** es el tutorial con foco: eso va mucho después. Es **una pantalla**, sin anclajes, que aparece **solo la primera vez** y hace tres cosas, en este orden:
1. **Elegir el idioma de la app.** Esto es obligatorio y **no se puede saltear**: hoy el valor por omisión es el español (`idiomaInterfaz = ES`), así que un usuario alemán o inglés ve la app en español hasta que descubre Ajustes. Ofrecé los 6 idiomas, cada uno escrito **en su propio idioma**, más la opción «Idioma del sistema» que ya existe.
   **Importante (regla dura 2):** la app **no puede leer el idioma del sistema por su cuenta** para preseleccionar nada. El usuario elige. Si elige «Idioma del sistema», ahí sí se lee, porque es una elección explícita, que es la excepción que la regla ya contempla.
2. **Qué idioma practica y en qué nivel.** Lo mismo que hay en Ajustes; reusá ese componente en vez de duplicarlo.
3. **Qué es esto y qué no es.** El texto más importante de la app. En español es este, y hay que traducirlo bien a los otros 5 (P7):
   > LenguApp no enseña un idioma: te hace practicar el que ya estás aprendiendo. Cada semana te propone un tema, una habilidad y un desafío para producir, no para estudiar. Si estás empezando un nivel, hacé un curso de ese nivel; esta app es para que lo que aprendés ahí no se te quede en el cuaderno.

   Tiene que verse **en la misma pantalla**, no detrás de un «leer más». Es prosa de cuerpo, no un titular: acordate de la Ronda L.
- **Cuándo aparece:** solo si nunca se completó. Guardalo como **un ajuste más** (`bienvenidaCompletada`), que es lo que la regla dura 4 permite: ajustes sí, progreso no. **No** debe viajar en la copia de seguridad de Guardados; comprobalo con un test.
- **Volver a verla:** una fila en Ajustes, junto a «Acerca de». El guion dice que ese botón reiniciará además los tours cuando existan; por ahora solo rehace la bienvenida.
- El mismo texto de «qué es esto» **ya debería estar en «Acerca de»**: si no coincide, unificá y decí cuál dejaste.

## Tarea 3 — Versión y cierre
**versionCode 12, versionName 0.3.0** (la bienvenida y el audio justifican subir la menor). Guía de testers: la versión y una fila nueva sobre la bienvenida, en el tono de las demás. **La guía tiene que seguir entrando en una hoja**; si no entra, acortá otra fila y decí cuál. El `.pdf` lo regenera Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
El tutorial con foco (tours por sección), los clips lentos, el contenido, el esquema, el ícono y la pantalla de carga, la planilla, el progreso.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets de contenido.
3. Tests: la bienvenida aparece la primera vez y no la segunda; no se puede salir sin elegir idioma; el idioma elegido se aplica de inmediato; el texto de «qué es esto» está en los 6 idiomas; `bienvenidaCompletada` no viaja en el respaldo; el release no lleva `assets/audio/EN/`.
4. **Manifest sin `INTERNET` ni permisos nuevos.**
5. Tamaño del APK con empaquetado limpio, **de debug y de release**, con el delta sobre los 63 137 036 B de la Ronda M. El release tiene que bajar unos 26 MB.

## Informe
Un commit por tarea. Cómo sacaste el audio inglés del release y cómo lo comprobás, qué hiciste con las entradas inglesas del índice, cómo quedó la bienvenida (con el texto final en los 6 idiomas), dónde guardaste `bienvenidaCompletada`, los dos tamaños de APK, y las decisiones donde el prompt no cerraba.
