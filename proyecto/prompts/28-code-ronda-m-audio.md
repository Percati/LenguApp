# Prompt para Code — Ronda M: que el audio suene

Leé `CLAUDE.md` (reglas duras **1**, **2** y **4**), `proyecto/REGLAS-PREVENCION.md`, `proyecto/FALTANTES.md` **48**, y `proyecto/docs/licencias-voces.md`. Partí del `origin/main` actual (Ronda L, 0.2.8 / versionCode 10, más el parche de Overview con el índice de audio). No pushees: un parche por tarea. **No toques `proyecto/contenido/`, el esquema, ni los `.ogg`.**

## Por qué nunca sonó, y qué cambió
Los **8202 clips `.ogg` ya viajan en el APK** (51 MB, `assets/audio/DE/` y `assets/audio/EN/`) y `proyecto/audio-estado.json` sabía desde septiembre qué archivo corresponde a cada texto — pero ese archivo es **de trabajo** y la app nunca lo leyó. No faltaba grabar nada: faltaba el puente.

Overview ya puso el puente: **`app/src/main/assets/audio/indice.json`** (544 KB), generado por `proyecto/tools/indice_audio.py`. Forma:

```json
{"de": {"Vocabulario": {"<texto exacto>": "<archivo.ogg>"}, "Expresión": {…}, "Ejemplo": {…}}, "en": {…}}
```

La clave es el **texto exacto del contenido** en el idioma que se aprende: el `item` del vocabulario, la `expresion` del redemittel, o el `texto` del ejemplo. **Cobertura medida sobre los assets reales:** vocabulario **4055/4066 (DE)** y **4088/4088 (EN)**, expresiones **100 % en los dos**, ejemplos **548/1066 (DE)** y **477/1071 (EN)** — solo los marcados para audio. Si un texto no está en el índice, **no hay audio y no se dibuja ningún botón**: nunca un botón que no suena.

## Tarea 1 — El reproductor
- Leé `indice.json` una sola vez al arrancar (o perezosamente la primera vez que se necesita), igual que el resto del contenido, con el parser estricto de siempre.
- Reproducí el `.ogg` **desde los assets**, sin copiarlo a disco ni pedir permisos. `MediaPlayer` con `AssetFileDescriptor` alcanza; si preferís `ExoPlayer`, justificá el peso que suma al APK — y si suma más de 1 MB, usá `MediaPlayer`.
- **Un solo audio a la vez:** tocar otro corta el anterior. Soltá el reproductor al salir de la pantalla y cuando la app pasa a segundo plano.
- **Respetá el silencio del sistema** y el volumen de medios; no fuerces el canal de llamada ni el de alarma. Si el teléfono está en silencio, no hay sonido y no pasa nada raro.
- Ante un archivo que falta o no se puede abrir: **nada de crash y nada de cartel de error**; el botón queda inerte y se registra en el log de depuración.
- **Reglas duras:** no se agrega ningún permiso (regla 1: tampoco `INTERNET`), no se lee nada del dispositivo (regla 2) y **no se guarda qué se escuchó** (regla 4).

## Tarea 2 — Dónde va el botón
Un ícono de altavoz, discreto, **solo cuando hay audio**:
- En la **ficha**: en cada fila de vocabulario y de redemittel, y en los ejemplos que tengan clip.
- En la **Biblioteca** (vista de léxico y vista de Verbos) y en **Guardados**: en la misma fila donde ya está la estrella. Cuidá que el alto de la fila no crezca y que estrella y altavoz no se peleen por el espacio con la letra al 160 %.
- **Estado visible mientras suena** (el ícono cambia o se atenúa). Área táctil de 48 dp aunque el ícono sea chico, y `contentDescription` en los 6 idiomas (P7), que TalkBack lo lee: algo como «Escuchar» / «Detener».
- **Decidilo vos y contalo:** si el altavoz va antes o después de la estrella, y si en la vista de Verbos va en la fila del verbo, en la de sus colocaciones, o en las dos (la colocación es la que tiene texto con audio; el núcleo puede no tenerlo).

## Tarea 3 — La velocidad lenta, solo si sale gratis
Hay clips al **80 %** para *shadowing*, pero **no están en el índice**: `audio-estado.json` guarda un archivo por texto. **No** inventes un segundo índice ni adivines nombres de archivo. Mirá si los lentos están en los assets y con qué nombre, y **decí en el informe** cuántos hay y qué haría falta para ofrecerlos (un campo más en el índice, que lo agregaría Overview). **No lo implementes en esta ronda.**

## Tarea 4 — Versión y cierre
**versionCode 11, versionName 0.2.9.** En la guía de testers, la fila que dice que el audio todavía no suena **deja de ser cierta**: reescribila contando que hay audio en vocabulario y expresiones, que en los ejemplos es parcial, y **que por ahora solo el alemán** (ver abajo). La guía sigue entrando en una hoja; el `.pdf` lo regenera Overview. En «Acerca de», la atribución que corresponde está en `docs/licencias-voces.md`. Sección nueva en `FALTANTES.md`.

## Lo que NO se publica todavía: el audio en inglés
Overview documentó las licencias y encontró un problema: las dos voces inglesas (`en_US-ryan`, CC BY-NC-SA 4.0, y `en_US-lessac`, licencia de Blizzard 2013 — solo investigación, sin uso comercial ni redistribución) **no se pueden publicar** y son incompatibles con el CC BY-SA 4.0 del contenido. La alemana (`de_DE-thorsten`, **CC0**) está limpia.
- **Implementá el reproductor para los dos idiomas** — el código es el mismo y el índice ya los trae.
- **Pero dejá el audio inglés detrás de un interruptor de compilación** (una constante o un `buildConfigField`, no un ajuste visible para el usuario) que hoy esté **apagado para release y encendido para debug**, así Fer lo prueba y no se publica. Documentá dónde está esa constante en una sola línea de `FALTANTES.md`, para encenderla cuando se regeneren los clips.
- No borres los `.ogg` ingleses ni toques los assets de audio.

## Fuera de alcance
Contenido, esquema, los `.ogg`, la velocidad lenta, el tutorial y la bienvenida, el ícono y la pantalla de carga, la planilla, el progreso.

## Verificación obligatoria
1. `compileDebugKotlin compileDebugUnitTestKotlin test` en verde en cada commit.
2. Los dos guardas de `preBuild` en verde, sin tocar los assets de contenido.
3. Tests: el índice se parsea; un texto con audio muestra botón y uno sin audio no; un archivo inexistente no rompe; un solo audio a la vez; el reproductor se suelta al salir; `contentDescription` en los 6 idiomas; el interruptor del inglés apagado en release.
4. **Manifest sin `INTERNET` ni permisos nuevos** — esto es lo primero que hay que comprobar en esta ronda.
5. Tamaño del APK con empaquetado limpio y el delta sobre los 62 950 571 B de la Ronda L. El índice suma 544 KB; decí cuánto suma el código.

## Informe
Un commit por tarea. Qué reproductor elegiste y por qué, dónde pusiste el botón y con qué criterio, qué pasa con el silencio del sistema y con un archivo faltante, el recuento de clips lentos y qué haría falta para ofrecerlos, el tamaño del APK, y dónde quedó el interruptor del audio inglés.
