# Ronda S — margen de verdad, el agujero recortado, y las voces en «Acerca de»

Escrito por Overview el 14-oct-2026 sobre la Ronda R (`57ed1fe6`). **Este prompt reemplaza a los prompts 35 y 36**, que nunca llegaron a ejecutarse: todo lo que decían está acá, con dos cambios de fondo que vienen del feedback de Fer de hoy. Si tenés a mano el 35 o el 36, **ignoralos**: están en el repo solo como registro.

**Alcance:** `ui/Tutorial.kt`, `ui/TutorialOverlay.kt`, `ui/TutorialPasos.kt`, `ui/TextosInterfaz.kt`, `ui/AcercaDeScreen.kt`, `ui/BibliotecaScreen.kt`, `app/build.gradle.kts` y los tests. **Nada de contenido pedagógico, nada de assets, nada de esquema.** Y **ningún texto de paso del tutorial**: Fer sigue editando `docs/tutorial-textos.md`.

## Lo que cambió respecto del prompt 35

1. **El margen de 32 dp es insuficiente: Fer lo probó y sigue viéndolo pegado al borde.** Pasa a **96 dp**. La cuenta: 1 dp = 1/160 de pulgada = 0,159 mm, así que **1,5 cm ≈ 94 dp** y 2 cm ≈ 126 dp. Fer pidió «al menos 1,5/2 cm»; 96 dp es el extremo bajo de lo que pidió y ya es **tres veces** lo de hoy.
2. **Decisión de Fer sobre el audio inglés: se publica como está, atribuyendo. No se regeneran los clips.** Y confirmó el dato que faltaba: **los 4100 clips son todos de `en_US-ryan-high`**; `en_US-lessac` nunca se usó. Eso cambia dos cosas del prompt 36 y agrega una tarea nueva (la 6).

## Tarea 1 — el margen pasa a 96 dp

`MARGEN_AL_TRAER_DP` de **32 a 96**. Nada más del mecanismo cambia: `traerALaVista` ya pide el rectángulo inflado y `bringIntoView` desplaza **lo mínimo** necesario, así que el ancla queda exactamente a 96 dp del borde por el que entró, que es justo lo que se busca.

Lo que hay que mirar con el código delante, y decidir vos:

- **96 dp arriba más 96 abajo son ~190 dp.** En una pantalla de ~640 dp de alto eso es el 30 %, y la tarjeta puede ocupar hasta el 55 %. Para un ancla alta (**`tour-ajustes-tema`** es el caso: agrupa tema, modo y tipografía) los dos márgenes **no van a entrar**. No lo fuerces: el contenedor desplaza lo que puede, que es exactamente lo que ya hace y lo que Fer da por entendido para el primer y el último elemento del contenido.
- **Si los dos márgenes no entran, priorizá el de arriba.** Quien lee un paso empieza por el principio del elemento; perder el final es menos malo que perder el comienzo.
- Si **96 dp te resulta imposible de sostener** en pantallas chicas (probá con un `w360dp-h640dp`), **no bajes el número por tu cuenta**: implementalo, medilo en el test y decí en el informe qué pasa. Lo que no sirve es seguir con un margen que Fer ya rechazó dos veces.

Tests: que la constante sea 96; que un ancla lejana dentro de un scroll quede a ≥ 96 dp de los dos bordes cuando hay lugar; que un ancla ya visible y despegada **no** desplace nada; y uno en pantalla chica que documente qué pasa cuando no entra.

## Tarea 2 — el agujero del foco se recorta a la pantalla

La tarea 1 de la Ronda R cambió el registro de las anclas de `boundsInRoot()` (recortado contra los ancestros) a `positionInRoot()` + `size` (**sin recortar**). Era **necesario** para `bringIntoView` —con el rectángulo recortado, un nodo fuera de pantalla registraba 0 × 0 y el margen se inflaba sobre nada— pero `rectDe()` alimenta dos cosas más en el recuadro, y las dos se rompen con un rectángulo sin recortar:

1. **El agujero y su borde** (`drawRoundRect` con `BlendMode.Clear`): si el ancla no entra entera en la pantalla, el agujero se dibuja **también sobre lo que no se ve** — un rectángulo iluminado por encima de la barra de arriba o por debajo del borde de abajo, con el borde blanco colgando en el aire.
2. **De qué lado va la tarjeta** (`enLaMitadDeArriba`, que mira `rect.center.y`): el centro de un nodo alto puede caer **fuera** de la pantalla, y entonces la tarjeta se va al lado equivocado y **tapa el foco**.

Con el margen de 96 dp esto se vuelve **más** probable, no menos: cuanto más se desplaza, más seguido un ancla alta queda parcialmente fuera.

**Qué hacer:** en el recuadro, **recortar el rectángulo del ancla contra los límites del propio recuadro** (`tamano`, que ya tenés) antes de dibujar el agujero y antes de decidir el lado de la tarjeta. El rectángulo **sin recortar** se sigue usando para `traerALaVista`. Si de la intersección no queda nada, tratalo como **paso sin foco**: tarjeta abajo y ningún agujero — nunca un agujero en una esquina.

## Tarea 3 — el elemento con foco se puede tocar (SOLO si Fer lo autoriza)

**Fer todavía no contestó a esto. Si al mandarte este prompt no te dice nada, NO hagas esta tarea** y seguí con el resto. No la hagas «por si acaso»: cambia cómo se siente el tutorial entero.

El problema: el recuadro se come **todos** los toques, incluidos los del elemento que ilumina. Eso deja dos pasos sin nada que hacer — el del encabezado del vocabulario, que tiene que decir «el encabezado abre la lista **cuando termines con esto**», y el del interruptor del desafío, que no se puede encender, así que el paso de la consigna **se saltea casi siempre** y el tour dice «Paso 1 de 1».

Si se autoriza: el agujero **pasa los toques al elemento que ilumina** y el resto de la pantalla sigue sin recibir nada. **El tour no avanza solo** al tocar: sigue haciendo falta «Siguiente». Si la pantalla cambia debajo, el foco sigue al ancla (`onGloballyPositioned` ya reregistra) y si el ancla desaparece, el paso se saltea con el mecanismo que existe. **No lo habilites en anclas que navegan a otra pantalla** (`acerca-de` y `ver-bienvenida` en Ajustes, las filas de Habilidades): un toque ahí dejaría el tour corriendo sobre una pantalla que no es la suya. Lo mejor es un campo **`tocable` en `PasoTour`**, falso por omisión, verdadero en los dos pasos que lo necesitan (`tour-ficha-vocabulario` y `tour-desafio-interruptor`); una lista a mano de «anclas que navegan» envejece peor.

## Tarea 4 — un bloque «Voces» en «Acerca de»

Fer pidió **referenciar todas las voces, las de dominio público y las licenciadas igual**. Hoy la atribución del audio es una línea pegada al final del bloque de licencias (`ACERCA_CREDITOS_TEXTO`: «Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0.»), **sin la voz inglesa y sin un solo enlace**.

Pasa a ser un bloque propio, con el mismo `Bloque(titulo, texto)` que usan los demás, **entre «Licencias» y la versión**. Clave nueva `ACERCA_VOCES_TITULO` y `ACERCA_VOCES_TEXTO`; **se borra `ACERCA_CREDITOS_TEXTO`** con sus 6 traducciones y `textoAcercaCreditos`, que este bloque reemplaza — no la dejes muerta.

**Diferencia con el prompt 36:** ahí el bloque era condicional (`BuildConfig.AUDIO_INGLES`), porque el release salía sin audio inglés. Con la decisión de Fer **el audio inglés entra al release** (tarea 6), así que el texto es **uno solo, sin condicionales**, y nombra las dos voces siempre.

### `ACERCA_VOCES_TITULO`

| Idioma | Texto |
|---|---|
| ES | Voces |
| EN | Voices |
| DE | Stimmen |
| FR | Voix |
| IT | Voci |
| PT | Vozes |

### `ACERCA_VOCES_TEXTO`

Los saltos de línea son parte del texto (`\n`), igual que en otros textos de «Acerca de».

- **ES:** `El audio se generó con Piper (MIT), a partir de modelos de voz publicados en rhasspy/piper-voices.\n\nAlemán: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (dominio público).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nInglés: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper: https://github.com/rhasspy/piper\nModelos: https://huggingface.co/rhasspy/piper-voices`
- **EN:** `The audio was generated with Piper (MIT), from voice models published in rhasspy/piper-voices.\n\nGerman: de_DE-thorsten-high. Thorsten-Voice corpus, CC0 1.0 (public domain).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nEnglish: en_US-ryan-high. RyanSpeech corpus, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper: https://github.com/rhasspy/piper\nModels: https://huggingface.co/rhasspy/piper-voices`
- **DE:** `Das Audio wurde mit Piper (MIT) erzeugt, aus Stimmmodellen aus rhasspy/piper-voices.\n\nDeutsch: de_DE-thorsten-high. Korpus Thorsten-Voice, CC0 1.0 (gemeinfrei).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nEnglisch: en_US-ryan-high. Korpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper: https://github.com/rhasspy/piper\nModelle: https://huggingface.co/rhasspy/piper-voices`
- **FR:** `L'audio a été généré avec Piper (MIT), à partir de modèles de voix publiés dans rhasspy/piper-voices.\n\nAllemand : de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (domaine public).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nAnglais : en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper : https://github.com/rhasspy/piper\nModèles : https://huggingface.co/rhasspy/piper-voices`
- **IT:** `L'audio è stato generato con Piper (MIT), a partire da modelli di voce pubblicati in rhasspy/piper-voices.\n\nTedesco: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (dominio pubblico).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nInglese: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper: https://github.com/rhasspy/piper\nModelli: https://huggingface.co/rhasspy/piper-voices`
- **PT:** `O áudio foi gerado com o Piper (MIT), a partir de modelos de voz publicados em rhasspy/piper-voices.\n\nAlemão: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (domínio público).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nInglês: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\n\nPiper: https://github.com/rhasspy/piper\nModelos: https://huggingface.co/rhasspy/piper-voices`

### Los enlaces van como texto, no como enlaces tocables

**Decisión deliberada:** las URL se muestran **seleccionables y copiables**, no tocables. CC BY 4.0 se cumple con un URI en texto, no exige un enlace activo; y un enlace tocable es un `ACTION_VIEW` hacia afuera, que **no necesita el permiso `INTERNET`** y por lo tanto no rompe la regla dura 1, pero **sí es una puerta nueva hacia afuera y eso lo decide Fer**. Concretamente: el bloque va dentro de un `SelectionContainer` (el mismo recurso que la ficha) y **nada** de `LinkAnnotation`, `ClickableText` ni `Intent`. Si te parece que vale la pena el enlace tocable, decilo en el informe.

## Tarea 5 — la licencia, declarada por componente

`ACERCA_LICENCIAS_TEXTO` dice hoy «Código: GPL-3.0. Contenido: CC BY-SA 4.0.» y eso, con un audio inglés CC BY-NC-SA adentro, es **inexacto en bloque**: BY-SA y BY-NC-SA son incompatibles entre sí, y lo que las hace convivir es que el audio es un **componente aparte con su propia licencia**, no una obra derivada del contenido. La línea lo dice:

| Idioma | Texto |
|---|---|
| ES | `Código: GPL-3.0. Contenido: CC BY-SA 4.0. Audio: la licencia de cada voz, en «Voces».` |
| EN | `Code: GPL-3.0. Content: CC BY-SA 4.0. Audio: each voice's own licence, under «Voices».` |
| DE | `Code: GPL-3.0. Inhalt: CC BY-SA 4.0. Audio: die Lizenz der jeweiligen Stimme, unter «Stimmen».` |
| FR | `Code : GPL-3.0. Contenu : CC BY-SA 4.0. Audio : la licence de chaque voix, dans « Voix ».` |
| IT | `Codice: GPL-3.0. Contenuti: CC BY-SA 4.0. Audio: la licenza di ogni voce, in «Voci».` |
| PT | `Código: GPL-3.0. Conteúdo: CC BY-SA 4.0. Áudio: a licença de cada voz, em «Vozes».` |

## Tarea 6 — el audio inglés entra al release

**Esta es la tarea delicada de la ronda: desarma una guarda a propósito.** Leela entera antes de tocar nada.

Desde la Ronda N el release **no lleva** `assets/audio/EN/`, por `ignoreAssetsPatterns`, con `AUDIO_INGLES = false` en release y la tarea `verificarReleaseSinAudioIngles` haciendo **fallar** cualquier release que lleve un clip inglés. Eso se montó cuando creíamos que esas voces no se podían publicar. **Ya no es el caso:** `en_US-ryan` es CC BY-NC-SA 4.0 y se publica atribuyendo, que es lo que hacen las tareas 4 y 5, y `en_US-lessac` nunca se usó. Así que:

- `AUDIO_INGLES` pasa a **`true` en release** (en debug ya lo es).
- Se **quita** el bloque `androidComponents` con `ignoreAssetsPatterns.add("<dir>EN")`.
- Se **quita** la tarea `verificarReleaseSinAudioIngles` y su `dependsOn` en `packageRelease` / `packageReleaseBundle` / `bundleRelease`.
- Se **actualizan los comentarios** que dicen que las voces inglesas «no se pueden publicar» o «no se pueden redistribuir»: no es cierto, y era mi error. Lo que corresponde decir es qué licencia tiene cada voz y que la atribución está en «Acerca de» (`docs/licencias-voces.md` tiene la versión corregida).
- **La otra guarda de `preBuild` no se toca.** Y ningún otro `verificar*` se toca.

**Consecuencia medible que hay que informar:** el release pasa de ~37,8 MB a **~55 MB** (los clips ingleses son 17,6 MB). Dalo medido, no estimado.

**Si al quitar la tarea se te cae algún test que la nombraba, cambialo, no lo borres**, y decí cuál.

## Tarea 7 — dos cosas chicas de la Ronda R

1. **`BibliotecaScreen.kt` quedó con el `import` de `traduccionParaMostrar` sin usar** (tarea 8 de la Ronda R). Borralo.
2. **El `enum ClaveTexto` quedó desordenado:** `TOUR_FICHA_2` y `TOUR_FICHA_9` están declaradas al final, después de las del desafío, porque se agregaron en la ronda del renumerado. No cambia nada funcional, pero el renumerado existía justamente para que la lista se lea en orden. Ponelas en su lugar (1…10 seguidas) y dejá las de `TOUR_DESAFIO` en orden también.

## Tarea 8 — versión y guía

- `versionName` **0.4.2**, `versionCode` **17**.
- La guía de testers en español: **la fila del audio ya la arreglé yo** en las seis guías (decía «solo en alemán», pero en debug `AUDIO_INGLES` es `true` y el tester del APK de debug **sí oye inglés** — error mío, no tuyo). Vos cambiá **solo el título** a 0.4.2 y, si la tarea 3 se hace, mirá la fila «Bienvenida y tutorial». **Las otras cinco guías y los seis `.pdf` los hago yo.**
- `FALTANTES.md`: escribí la sección **65** (la 64, la 66 y la 67 son mías, ya están).

## Qué no hacer

- No reescribas ningún texto de paso del tutorial: Fer los está editando en `docs/tutorial-textos.md`.
- No hagas la tarea 3 sin autorización explícita de Fer.
- No agregues `INTERNET` ni ningún permiso, ni un `Intent` hacia el navegador.
- No pongas texto visible fuera de `TextosInterfaz` (regla de prevención P5).
- No inventes licencias: si una voz no está en la tabla de la tarea 4, no la nombres.
- No agregues animaciones, ni puntos de progreso, ni cambies cuándo se lanza cada tour.

## Lo de siempre

Un commit por tarea, nada de push, los parches en `outputs/ronda-s/`. En el informe: el tamaño del release **medido**, qué decidiste donde este prompt no cerraba, y lo que no pudiste comprobar sin un teléfono — de las tareas 1 y 2 **casi todo** es «cómo se ve», así que medí lo que puedas en el test y decí claramente qué no viste.
