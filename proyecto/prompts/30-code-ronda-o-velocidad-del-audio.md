# Prompt para Code — Ronda O: velocidad del audio (60 % a 100 %)

Leé `CLAUDE.md` (reglas duras **1**, **2** y **4**), `proyecto/REGLAS-PREVENCION.md` (P7) y `proyecto/FALTANTES.md` **49**, **50**, **51** y **54**. Partí del `origin/main` actual (Ronda N, 0.3.0 / versionCode 12). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema ni los assets.**

## Por qué esta ronda existe, y por qué es barata
Fer probó la 0.3.0 y pidió poder escuchar más lento, entre el **60 % y el 100 %**. En la Ronda M informaste que los clips lentos **no existen** y que ofrecerlos costaría generar ~8143 clips más (~51 MB) más un campo nuevo en el índice. Eso era cierto **para clips pregrabados**, y por eso Overview los había descartado.

**No hacen falta.** `MediaPlayer` cambia la velocidad en el momento con `setPlaybackParams(PlaybackParams().setSpeed(x))`, disponible desde API 23 — y `minSdk` es **26**. Cero archivos nuevos, cero MB, cero cambios en el índice ni en el contenido. La decisión de la sección 50 queda **revertida por un dato nuevo**, no por capricho.

## Tarea 1 — Velocidad variable en el reproductor
En `audio/ReproductorAssets.kt`:
- Aplicá la velocidad con `PlaybackParams`. **Cuidado con dos trampas conocidas:**
  1. `setPlaybackParams` lanza `IllegalStateException` si el reproductor no está preparado. Aplicala **dentro de `setOnPreparedListener`, antes de `start()`**, no en `setDataSource`.
  2. En varias versiones de Android, llamar a `setPlaybackParams` sobre un reproductor **pausado lo hace arrancar**. Acá no hay pausa (solo reproducir y detener), pero si lo agregás alguna vez, acordate.
- **El tono se conserva**: la velocidad baja sin que la voz suene grave. Comprobalo y decilo en el informe; si en algún dispositivo o versión no fuera así, avisá en vez de taparlo.
- **Si el dispositivo rechaza la velocidad** (`IllegalArgumentException` o similar), reproducí a velocidad normal en vez de no sonar: un clip al 100 % es mejor que el silencio. Línea en el log de depuración, sin cartel.
- `ControlAudio` recibe la velocidad vigente al reproducir; no la guarda él.

## Tarea 2 — Dónde se elige
**Un ajuste**, no un control por fila: la velocidad es una preferencia de uso, no una decisión que se tome palabra por palabra.
- Fila nueva en **Ajustes**, junto a la tipografía: **cuatro pasos fijos — 60 %, 75 %, 90 % y 100 %** (por omisión **100 %**). Pasos y no un deslizador: se toca una vez, se lee de un vistazo y no hay que afinar nada con el pulgar.
- Es **un ajuste más** (`velocidadAudio`), guardado con los demás en las mismas `SharedPreferences`. La regla dura 4 lo permite (ajustes sí, progreso no) y **no viaja en la copia de seguridad de Guardados**: comprobalo con un test, como hiciste con `bienvenidaCompletada`.
- **Etiqueta y valores en los 6 idiomas** (P7). El rótulo tiene que decir para qué sirve sin tecnicismos; algo como «Velocidad del audio».
- **Que se note dónde está:** si el ajuste no está al 100 %, el botón de altavoz tiene que dejarlo ver de algún modo discreto (un matiz, no un cartel), para que nadie crea que el audio «suena raro» sin saber por qué. Proponé cómo y mostralo en el informe; si te parece que ensucia, decilo y lo discutimos.

## Tarea 3 — Versión y cierre
**versionCode 13, versionName 0.3.1.** Guía de testers: la versión y la fila del audio, que ahora tiene que mencionar la velocidad. **La guía está ahora en 6 idiomas** (`docs/guia-testers.md` más `-en`, `-de`, `-fr`, `-it`, `-pt`): el **español es el canónico y el que lee el test**; actualizá el español y decime qué cambió, que **las otras cinco las retraduce Overview**. No las toques vos. Los `.pdf` también los regenera Overview. Sección nueva en `FALTANTES.md`.

## Fuera de alcance
Generar clips lentos (ya no hacen falta), el contenido, el esquema, los assets, el índice de audio, los tours del tutorial, el ícono y la pantalla de carga.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets.
3. Tests: la velocidad elegida llega al reproductor; el valor por omisión es 100 %; se guarda y se recuerda; **no viaja en el respaldo ni en Anki**; una velocidad rechazada por el dispositivo cae a 100 % sin romper; la etiqueta está en los 6 idiomas.
4. **Manifest sin `INTERNET` ni permisos nuevos.**
5. Tamaño del APK con empaquetado limpio y el delta sobre los 63 153 420 B (debug) de la Ronda N. Tiene que ser casi cero: es código, no archivos.

## Informe
Un commit por tarea. Dónde aplicaste `PlaybackParams` y por qué ahí, si el tono se conserva, qué pasa si el dispositivo rechaza la velocidad, cómo se ve que el ajuste no está al 100 %, qué cambió en el español de la guía, el tamaño del APK y las decisiones donde el prompt no cerraba. **Decime también si al 60 % la voz sintética sigue siendo entendible**: si no lo es, el paso más lento tendría que subir a 70 %.
