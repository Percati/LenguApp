# Ronda V — los tests que rompió el parche de Verbos, y el icono y la pantalla de carga

Escrito por Overview el 19-oct-2026 sobre `62047119`. **Antes de nada: el build está rojo en `main` y no es culpa tuya** — tu suite pasó sobre `778c577e`, y el parche de Verbos entró después. La tarea 1 es destrabarlo.

**Alcance:** `app/src/test/.../VerbosReferenciaTest.kt`, `presentacion/Verbos.kt` (solo el comentario y un test), `AndroidManifest.xml`, `res/values/themes.xml`, `res/values-night/themes.xml`, `res/mipmap-anydpi-v26/`, y los tests. **Nada de contenido, nada de `proyecto/tools/`, nada de `proyecto/contenido/`.** Y ningún texto de paso del tutorial.

**Un parche mío va primero** y trae tres cosas que vas a necesitar: `res/drawable/splash_l.xml`, `res/drawable/ic_launcher_foreground.xml` y `res/drawable/ic_launcher_background.xml`, más las seis guías a 0.4.4 y FALTANTES 75. **Si no están cuando arranques, avisá y no los escribas vos.**

## Tarea 1 — destrabar el build

Verbos quitó la entrada de referencia `to deal with` (ahora son **498**: DE 160, EN **338**). Tres cosas de `VerbosReferenciaTest.kt` quedaron rojas y una quedó vacía:

| línea | qué pasa | qué hacer |
|---|---|---|
| 86 | `assertEquals(339, referencia(Idioma.EN).size)` | **338** |
| 135 | `.first { it.item == "to deal with" }` lanza `NoSuchElementException` | ver abajo |
| 233 | `ESPERADO_EN = Esperado(559, 79, 819)` | **`Esperado(559, 78, 819)`** |
| 222 | busca `single { contains("deal with") }` y ya no hay referencia que desacuerde | ver abajo |

**El total no se mueve:** quitar una entrada que además fusionaba resta una referencia y una colisión a la vez, así que inglés sigue en **819** (559 + 338 − 78) y alemán en **585**. Medilo y confirmalo, no lo des por bueno porque lo diga acá.

**Los dos tests de `deal with`.** El de la línea 135 probaba la fusión por `base` contra un ítem con objeto de relleno, y el de la 222 probaba un desacuerdo de clase. El caso real desapareció, así que:

- **No lo reescribas contra otro verbo real** buscando uno que se le parezca: no hay ninguno, y un test que se agarra del dato de hoy se vuelve a romper en B1.
- **Reescribilos con datos de prueba armados a mano**, como hacés en el resto del archivo: una referencia inventada `to X with` contra un ítem `to X with something`, y una referencia inventada que desacuerde en clase con su núcleo. Eso prueba el mecanismo, que es lo que importa, y no depende de qué verbos haya en la tabla este mes.
- El desacuerdo de `hören` **sigue siendo real**: ese dejalo como está, contra el dato real.

## Tarea 2 — `OBJETO_FINAL_EN` se quedó sin ningún caso vivo

La segunda clave de fusión que quita `something`/`someone`/`somebody`/`oneself`/`yourself` del final la agregaste para un solo caso, y lo dijiste vos: «la probé contra los 339 verbos de referencia y solo `to deal with` coincide así». **Ese caso ya no existe**, así que hoy es una rama que no se ejerce con los datos reales.

**No la saques.** Es correcta, es un tercer intento detrás de dos claves que aciertan, y en B1 va a volver a haber `base` que difieran del ítem de la ficha. Lo que sí hace falta:

1. **El test de la tarea 1 con datos armados** la ejerce — con eso alcanza, no hace falta nada más.
2. **Que el comentario diga que hoy no tiene casos vivos** y por qué se queda igual. Un comentario que dice «el ítem de la ficha `to deal with something` es la anotación de la referencia `to deal with`» describe un caso que ya no está en la tabla, y el que lo lea en marzo va a buscarlo y no lo va a encontrar.

## Tarea 3 — el icono adaptativo

Los dos drawables ya están en el parche mío, medidos: el arte tiene un radio máximo de **32.9** sobre una zona segura de radio 36, y lo simulé bajo las tres máscaras de launcher (círculo, squircle, cuadrado) sin recorte. Lo que falta es el cableado.

- `res/mipmap-anydpi-v26/ic_launcher.xml` e `ic_launcher_round.xml`, con `<adaptive-icon>` apuntando a `@drawable/ic_launcher_background` y `@drawable/ic_launcher_foreground`.
- `android:icon="@mipmap/ic_launcher"` y `android:roundIcon="@mipmap/ic_launcher_round"` en `<application>`. **Hoy el manifiesto no declara ninguno de los dos** — lo dice el comentario que vos mismo dejaste en `themes.xml` («cuando llegue el icono, reemplazar…»), y es el momento.
- **Nada de PNG por densidad.** El icono es vectorial y `anydpi-v26` cubre de Android 8 en adelante; `minSdk` del proyecto está por encima de eso, confirmalo y decilo en el informe. Si por `minSdk` hiciera falta un respaldo, **avisá antes de generar bitmaps**, no los hagas por las dudas.
- **Icono monocromo (`<monochrome>`, iconos temáticos de Android 13): no lo hagas en esta ronda.** La L vive de tres colores superpuestos; aplanada a una sola tinta hay que decidir qué forma sobrevive, y eso lo mira Fer. Dejá el hueco documentado.

## Tarea 4 — la pantalla de carga

`themes.xml` ya tiene el andamiaje: `Theme.LenguApp.Splash` con `windowSplashScreenBackground`, `windowSplashScreenAnimatedIcon` apuntando a `splash_vacio` y `postSplashScreenTheme`.

- **`windowSplashScreenAnimatedIcon` → `@drawable/splash_l`.** Borrá `splash_vacio.xml`, que era el placeholder; si algún test lo nombra, actualizalo.
- **`windowSplashScreenBackground` → un color propio `#253C52`, no `@color/fondo_papel`, y el mismo en claro y en oscuro.** Esto es lo que más importa de la ronda y es medido, no estético: con `fondo_papel` claro (`#F7F6F2`) el asta de la L contrasta **1.38** y la cola **1.29** — en modo claro se vería una L casi invisible, solo la astilla terracota. La paleta está hecha para fondo oscuro. El `#253C52` es el azul noche que `icono.svg` ya trae de fondo, así que el splash y el icono del launcher quedan siendo la misma marca. Agregá el color a `colors.xml` con nombre propio (`fondo_splash` o el que te guste) y **no** lo redefinas en `values-night`.
- **El icono se recorta a un círculo de 192 dp** sobre el lienzo de 288 — no a un cuadrado. `splash_l` entra con radio máximo 87.5 sobre 96, así que no hay que escalar nada. **No le pongas `windowSplashScreenIconBackgroundColor`:** eso cambia el lienzo a 240 con círculo útil de 160 y el arte dejaría de entrar.
- **Nada de animación.** El atributo se llama `AnimatedIcon` pero acepta un drawable estático, que es lo que corresponde acá: una animación alarga el arranque.
- Un test que compruebe que el tema del splash **no** usa `fondo_papel`, para que nadie lo «arregle» más adelante creyendo que es una inconsistencia de tema.

**Lo que NO se puede y conviene que lo tengas escrito:** el splash del sistema no admite imagen de fondo, solo color sólido más icono centrado, así que los dos renders a página completa que mandó Fer no son reproducibles. Y **no se puede alternar el splash al azar**: el tema lo resuelve el sistema antes de que corra una línea de la app — por eso existe `postSplashScreenTheme`, al que `installSplashScreen()` salta después. No lo intentes con `activity-alias` ni `setComponentEnabledSetting`.

## Tarea 5 — `splash_2` va a «Acerca de», no al splash

Simulé el recorte circular de `splash_2.svg` y **no sirve como icono de splash**: los dos pliegues pálidos son el fondo del render blanco continuado, no parte del glifo, así que sobre un color sólido se leen como dos cuchillas blancas atravesando la L, y los trazos exteriores quedan rebanados en el borde de la máscara (radio 94.4 sobre 96). Funciona como composición a página completa, que es justo lo que el splash no puede hacer.

**No lo pongas en esta ronda.** Decime si la cabecera de «Acerca de» tiene un lugar razonable para una ilustración de ~160 dp de alto y cómo lo harías, y lo decide Fer en la ronda siguiente con el dato en la mano. **No agregues el drawable todavía.**

## Tarea 6 — versión, guía y FALTANTES

- `versionName` **0.4.5**, `versionCode` **20**.
- **La guía de testers no la toques esta vez.** Las seis ya están a 0.4.4 en mi parche y el icono no es una fila de guía: nadie reporta «la app tiene icono». Subí solo el número de versión en el título de la española y **decímelo**, que las otras cinco y los seis `.pdf` los rehago yo. Si te parece que hace falta una fila, proponela en el informe en vez de escribirla: cada fila nueva empuja los seis `.pdf` a tres carillas.
- `FALTANTES.md`: la sección **76**. La 75 es mía y ya está en el parche.

## Qué no hacer

- No saques `OBJETO_FINAL_EN`.
- No reescribas los tests de `deal with` contra otro verbo real de la tabla.
- No pongas `windowSplashScreenIconBackgroundColor` ni animes el icono.
- No uses `fondo_papel` como fondo del splash.
- No generes PNG del icono por densidad sin avisar antes, ni el `<monochrome>`.
- No agregues `splash_2` a ningún lado todavía.
- No toques `proyecto/contenido/` ni `proyecto/tools/`.
- No agregues `INTERNET` ni ningún permiso.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-v/`. En el informe: los tres números de la tarea 1 medidos, el `minSdk` de la tarea 3, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono. **Y el APK de debug**, que esta vez hay algo que solo se ve en la pantalla: el icono en el launcher y el splash en los dos temas.
