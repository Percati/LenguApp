# Ronda V — los tests que rompió el parche de Verbos, y el icono y la pantalla de carga

Escrito por Overview el 19-oct-2026 sobre `62047119`. **Antes de nada: el build está rojo en `main` y no es culpa tuya** — tu suite pasó sobre `778c577e`, y el parche de Verbos entró después. La tarea 1 es destrabarlo.

**Alcance:** `app/src/test/.../VerbosReferenciaTest.kt`, `presentacion/Verbos.kt` (solo el comentario), `AndroidManifest.xml`, `res/values/themes.xml`, `res/values/colors.xml`, `res/mipmap-anydpi-v26/`, y los tests. **Nada de contenido, nada de `proyecto/tools/`, nada de `proyecto/contenido/`.** Y ningún texto de paso del tutorial.

**Un parche mío va primero** y trae los cuatro assets ya medidos y recortados, más las seis guías a 0.4.4 y FALTANTES 75. **Si no están cuando arranques, avisá y no los generes vos.**

| archivo | qué es |
|---|---|
| `res/mipmap-xxxhdpi/ic_launcher_foreground.webp` | primer plano del icono, lienzo 432 px (108 dp), la L a radio 33 de 36 |
| `res/mipmap-xxhdpi/ic_launcher_foreground.webp` | lo mismo a 324 px |
| `res/drawable/ic_launcher_background.xml` | el `#253C52` plano, vector |
| `res/drawable-xxhdpi/splash_l.webp` | icono del splash, lienzo 864 px (288 dp), la L a radio 88 de 96 |

Son **bitmap y no vector a propósito**: el arte original son tres PNG dentro de una etiqueta `<svg>` —renders con degradado, sombreado suave y textura de papel— y `VectorDrawable` no puede reproducir esa iluminación. FALTANTES 75 tiene las mediciones.

## Tarea 1 — destrabar el build

Verbos quitó la entrada de referencia `to deal with` (ahora son **498**: DE 160, EN **338**). Tres cosas de `VerbosReferenciaTest.kt` quedaron rojas y una quedó vacía:

| línea | qué pasa | qué hacer |
|---|---|---|
| 86 | `assertEquals(339, referencia(Idioma.EN).size)` | **338** |
| 135 | `.first { it.item == "to deal with" }` lanza `NoSuchElementException` | ver abajo |
| 233 | `ESPERADO_EN = Esperado(559, 79, 819)` | **`Esperado(559, 78, 819)`** |
| 222 | busca `single { contains("deal with") }` y ya no hay referencia que desacuerde | ver abajo |

**El total no se mueve:** quitar una entrada que además fusionaba resta una referencia y una colisión a la vez, así que inglés sigue en **819** (559 + 338 − 78) y alemán en **585**. Medilo y confirmalo, no lo des por bueno porque lo diga acá.

**Los dos tests de `deal with`.** El de la línea 135 probaba la fusión por `base` contra un ítem con objeto de relleno, y el de la 222 un desacuerdo de clase. El caso real desapareció, así que:

- **No los reescribas contra otro verbo real** buscando uno parecido: no hay ninguno, y un test que se agarra del dato de hoy se vuelve a romper en B1.
- **Reescribilos con datos armados a mano**, como hacés en el resto del archivo: una referencia inventada `to X with` contra un ítem `to X with something`, y una referencia inventada que desacuerde en clase con su núcleo. Eso prueba el mecanismo, que es lo que importa.
- El desacuerdo de `hören` **sigue siendo real**: ese dejalo contra el dato real.

## Tarea 2 — `OBJETO_FINAL_EN` se quedó sin ningún caso vivo

La segunda clave de fusión que quita `something`/`someone`/`somebody`/`oneself`/`yourself` del final la agregaste para un solo caso, y lo dijiste vos: «la probé contra los 339 verbos de referencia y solo `to deal with` coincide así». **Ese caso ya no existe.**

**No la saques.** Es correcta, es un tercer intento detrás de dos claves que aciertan, y en B1 va a volver a haber `base` que difieran del ítem de la ficha. Lo que hace falta:

1. **El test de la tarea 1 con datos armados** la ejerce — con eso alcanza.
2. **Que el comentario diga que hoy no tiene casos vivos** y por qué se queda. Hoy describe un caso que ya no está en la tabla, y el que lo lea en marzo lo va a buscar y no lo va a encontrar.

## Tarea 3 — el icono adaptativo

Los assets ya están medidos: la L queda con un radio máximo de 33 dp sobre una zona segura de 36, y la simulé bajo las tres máscaras de launcher (círculo, squircle, cuadrado) sin recorte. Falta el cableado.

- `res/mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml`, con `<adaptive-icon>` apuntando a `@drawable/ic_launcher_background` y `@mipmap/ic_launcher_foreground`.
- `android:icon="@mipmap/ic_launcher"` y `android:roundIcon="@mipmap/ic_launcher_round"` en `<application>`. **Hoy el manifiesto no declara ninguno de los dos** — lo dice el comentario que vos mismo dejaste en `themes.xml` («cuando llegue el icono, reemplazar…»), y es el momento.
- **Nada de respaldo heredado.** Confirmé `minSdk = 26`, así que los iconos adaptativos son nativos y `mipmap-anydpi-v26` cubre todo el rango: **no generes PNG cuadrados de respaldo** ni `ic_launcher` bitmap suelto.
- **No agregues más densidades.** Están xxhdpi y xxxhdpi; el sistema reduce solo para las de abajo. Si creés que falta alguna, decilo en el informe en vez de generarla, porque ampliar desde la fuente que hay no agrega detalle (ver el techo de calidad en FALTANTES 75).
- **Icono monocromo (`<monochrome>`, iconos temáticos de Android 13): no lo hagas.** La L vive de tres colores superpuestos con sombreado; aplanada a una tinta hay que decidir qué forma sobrevive, y eso lo mira Fer. Dejá el hueco documentado.

## Tarea 4 — la pantalla de carga

`themes.xml` ya tiene el andamiaje: `Theme.LenguApp.Splash` con `windowSplashScreenBackground`, `windowSplashScreenAnimatedIcon` apuntando a `splash_vacio` y `postSplashScreenTheme`.

- **`windowSplashScreenAnimatedIcon` → `@drawable/splash_l`.** Borrá `splash_vacio.xml`, que era el placeholder; si algún test lo nombra, actualizalo.
- **`windowSplashScreenBackground` → un color propio `#253C52`, no `@color/fondo_papel`, y el mismo en claro y en oscuro.** Esto es lo que más importa de la ronda y es medido, no estético: con `fondo_papel` claro (`#F7F6F2`) el asta de la L contrasta **1.38** y la cola **1.29** — en modo claro se vería una L casi invisible, solo la astilla terracota. La paleta está hecha para fondo oscuro, y `#253C52` es el azul del propio render, así que el splash y el icono del launcher quedan siendo la misma marca. Agregá el color a `colors.xml` con nombre propio (`fondo_splash` o el que prefieras) y **no** lo redefinas en `values-night`.
- **El icono se recorta a un círculo de 192 dp** sobre el lienzo de 288 — no a un cuadrado. `splash_l` ya entra con radio 88 sobre 96, así que no hay que escalar nada. **No le pongas `windowSplashScreenIconBackgroundColor`:** eso cambia el lienzo a 240 con círculo útil de 160 y el arte dejaría de entrar.
- **Nada de animación.** El atributo se llama `AnimatedIcon` pero acepta un drawable estático, que es lo que corresponde: una animación alarga el arranque.
- Un test que compruebe que el tema del splash **no** usa `fondo_papel`, para que nadie lo «arregle» más adelante creyendo que es una inconsistencia de tema.

**Lo que NO se puede y conviene que lo tengas escrito:** el splash del sistema no admite imagen de fondo, solo color sólido más icono centrado, así que los dos renders a pantalla completa no son reproducibles. Y **no se puede alternar el splash al azar**: el tema lo resuelve el sistema antes de que corra una línea de la app. No lo intentes con `activity-alias` ni `setComponentEnabledSetting`.

## Tarea 5 — las dos composiciones van a «Acerca de», no al splash

Los dos renders a pantalla completa (el de papel blanco y el del azul con las marcas de puntuación en órbita) no sirven como icono de splash: son composiciones de fondo completo, que es justo lo que el splash no puede mostrar, y llevar las trece marcas en órbita al círculo útil deja la L en 91 × 103 dp en vez de 132 × 149 — la órbita cuesta el 30 % del tamaño del glifo.

**No los pongas en esta ronda.** Decime si la cabecera de «Acerca de» tiene un lugar razonable para una ilustración de ~160 dp de alto y cómo lo harías, y lo decide Fer en la ronda siguiente con el dato en la mano. **No agregues ningún drawable todavía.**

## Tarea 6 — versión, guía y FALTANTES

- `versionName` **0.4.5**, `versionCode` **20**.
- **La guía de testers no la toques.** Las seis ya están a 0.4.4 en mi parche y el icono no es una fila de guía: nadie reporta «la app tiene icono». Subí solo el número de versión en el título de la española y **decímelo**, que las otras cinco y los seis `.pdf` los rehago yo. Si te parece que hace falta una fila, proponela en el informe en vez de escribirla: cada fila nueva empuja los seis `.pdf` a tres carillas.
- `FALTANTES.md`: la sección **76**. La 75 es mía y ya está en el parche.

## Qué no hacer

- No saques `OBJETO_FINAL_EN`.
- No reescribas los tests de `deal with` contra otro verbo real de la tabla.
- No pongas `windowSplashScreenIconBackgroundColor` ni animes el icono.
- No uses `fondo_papel` como fondo del splash.
- No generes PNG de respaldo, densidades nuevas ni el `<monochrome>`.
- No agregues las ilustraciones a pantalla completa a ningún lado todavía.
- No toques `proyecto/contenido/` ni `proyecto/tools/`.
- No agregues `INTERNET` ni ningún permiso.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-v/`. En el informe: los tres números de la tarea 1 medidos, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono. **Y el APK de debug**, que esta vez hay algo que solo se ve en la pantalla: el icono en el launcher y el splash en los dos temas.
