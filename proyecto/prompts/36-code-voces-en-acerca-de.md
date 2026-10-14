# Ronda S, tarea 5 — las voces, referenciadas en «Acerca de»

Escrito por Overview el 14-oct-2026. **Se suma al prompt 35 (Ronda S) como una tarea más**; si ya mandaste la Ronda S, esto va como ronda aparte y entonces `versionName` es **0.4.3** / `versionCode` **18** en vez de lo que dice abajo.

Fer cuestionó la conclusión de `docs/licencias-voces.md` («el audio inglés no se puede publicar») y tenía razón en lo principal: una licencia de atribución se cumple **atribuyendo**. El documento está corregido y ahora pide lo que corresponde: **referenciar todas las voces en «Acerca de», las de dominio público y las licenciadas igual**. Esta tarea es eso.

**Alcance:** `ui/AcercaDeScreen.kt`, `ui/TextosInterfaz.kt` y sus tests. **Nada de contenido, nada de assets, nada de esquema, ningún permiso nuevo.**

## Tarea 5.1 — un bloque «Voces» en «Acerca de»

Hoy la atribución del audio es una línea pegada al final del bloque de licencias (`ACERCA_CREDITOS_TEXTO`: «Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0.»), **sin la voz inglesa y sin un solo enlace**. Pasa a ser un bloque propio, con el mismo `Bloque(titulo, texto)` que ya usan los demás, **entre «Licencias» y la versión**.

**Claves nuevas:** `ACERCA_VOCES_TITULO`, `ACERCA_VOCES_TEXTO` y `ACERCA_VOCES_INGLES`.
**Clave que se borra:** `ACERCA_CREDITOS_TEXTO` (con sus 6 traducciones y `textoAcercaCreditos`), que este bloque reemplaza. No la dejes muerta.

**El bloque depende de `BuildConfig.AUDIO_INGLES`**, que ya existe: en debug el audio inglés suena, en release no viaja. Así que `ACERCA_VOCES_TEXTO` va siempre y `ACERCA_VOCES_INGLES` se **agrega** (con una línea en blanco de separación) **solo si `BuildConfig.AUDIO_INGLES` es `true``**. No atribuyas una voz que ese APK no lleva: el texto tiene que ser cierto en las dos variantes. Un test por variante.

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

- **ES:** `El audio se generó con Piper (MIT), a partir de modelos de voz publicados en rhasspy/piper-voices.\n\nAlemán: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (dominio público).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper: https://github.com/rhasspy/piper\nModelos: https://huggingface.co/rhasspy/piper-voices`
- **EN:** `The audio was generated with Piper (MIT), from voice models published in rhasspy/piper-voices.\n\nGerman: de_DE-thorsten-high. Thorsten-Voice corpus, CC0 1.0 (public domain).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper: https://github.com/rhasspy/piper\nModels: https://huggingface.co/rhasspy/piper-voices`
- **DE:** `Das Audio wurde mit Piper (MIT) erzeugt, aus Stimmmodellen aus rhasspy/piper-voices.\n\nDeutsch: de_DE-thorsten-high. Korpus Thorsten-Voice, CC0 1.0 (gemeinfrei).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper: https://github.com/rhasspy/piper\nModelle: https://huggingface.co/rhasspy/piper-voices`
- **FR:** `L'audio a été généré avec Piper (MIT), à partir de modèles de voix publiés dans rhasspy/piper-voices.\n\nAllemand : de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (domaine public).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper : https://github.com/rhasspy/piper\nModèles : https://huggingface.co/rhasspy/piper-voices`
- **IT:** `L'audio è stato generato con Piper (MIT), a partire da modelli di voce pubblicati in rhasspy/piper-voices.\n\nTedesco: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (dominio pubblico).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper: https://github.com/rhasspy/piper\nModelli: https://huggingface.co/rhasspy/piper-voices`
- **PT:** `O áudio foi gerado com o Piper (MIT), a partir de modelos de voz publicados em rhasspy/piper-voices.\n\nAlemão: de_DE-thorsten-high. Corpus Thorsten-Voice, CC0 1.0 (domínio público).\nhttps://github.com/thorstenMueller/Thorsten-Voice\n\nPiper: https://github.com/rhasspy/piper\nModelos: https://huggingface.co/rhasspy/piper-voices`

### `ACERCA_VOCES_INGLES` (solo si `AUDIO_INGLES`)

- **ES:** `Inglés: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nEn esta versión de prueba el audio en inglés suena; en la versión publicada todavía no se incluye.`
- **EN:** `English: en_US-ryan-high. RyanSpeech corpus, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nIn this test version the English audio plays; it is not included in the published version yet.`
- **DE:** `Englisch: en_US-ryan-high. Korpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nIn dieser Testversion ist das englische Audio zu hören; in der veröffentlichten Version ist es noch nicht enthalten.`
- **FR:** `Anglais : en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nDans cette version de test, l'audio en anglais fonctionne ; il n'est pas encore inclus dans la version publiée.`
- **IT:** `Inglese: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nIn questa versione di prova l'audio in inglese si sente; nella versione pubblicata non è ancora incluso.`
- **PT:** `Inglês: en_US-ryan-high. Corpus RyanSpeech, CC BY-NC-SA 4.0.\nhttps://www.kaggle.com/datasets/roholazandie/ryanspeech\nNesta versão de teste o áudio em inglês funciona; na versão publicada ainda não é incluído.`

**Un dato que tengo que poder confirmar y no puedo:** el manifiesto de audio (`proyecto/audio-manifiesto.md`) dice «`en_US-ryan-high`, `en_US-lessac-high` — dos voces seleccionables, se genera con las dos», pero la app reproduce **un clip por texto** y hay **4100 clips ingleses para 4100 textos**, o sea una sola voz. Los textos de arriba dicen `ryan`. **Si los clips son de `lessac`, decímelo en el informe y no publiques el bloque con la fila inglesa mal**: cambia la atribución entera. Fer generó los clips localmente y lo sabe él; si no está claro, dejá la tarea 5.1 sin la parte inglesa y avisá.

## Tarea 5.2 — la licencia, declarada por componente

`ACERCA_LICENCIAS_TEXTO` dice hoy «Código: GPL-3.0. Contenido: CC BY-SA 4.0.» y eso, con un audio inglés CC BY-NC-SA, sería **inexacto en bloque**: BY-SA y BY-NC-SA son incompatibles entre sí, y lo que las hace convivir es que el audio es un componente aparte con su propia licencia, no una obra derivada del contenido. Así que la línea lo dice:

| Idioma | Texto |
|---|---|
| ES | `Código: GPL-3.0. Contenido: CC BY-SA 4.0. Audio: la licencia de cada voz, en «Voces».` |
| EN | `Code: GPL-3.0. Content: CC BY-SA 4.0. Audio: each voice's own licence, under «Voices».` |
| DE | `Code: GPL-3.0. Inhalt: CC BY-SA 4.0. Audio: die Lizenz der jeweiligen Stimme, unter «Stimmen».` |
| FR | `Code : GPL-3.0. Contenu : CC BY-SA 4.0. Audio : la licence de chaque voix, dans « Voix ».` |
| IT | `Codice: GPL-3.0. Contenuti: CC BY-SA 4.0. Audio: la licenza di ogni voce, in «Voci».` |
| PT | `Código: GPL-3.0. Conteúdo: CC BY-SA 4.0. Áudio: a licença de cada voz, em «Vozes».` |

## Los enlaces van como texto, no como enlaces tocables

**Decisión, y es deliberada:** las URL se muestran **como texto seleccionable y copiable**, no como algo que se pueda tocar para abrir el navegador. Dos razones: CC BY 4.0 se cumple con un URI en texto (no exige un enlace activo), y un enlace tocable es un `ACTION_VIEW` hacia afuera de la app — que **no necesita el permiso `INTERNET`** y por lo tanto no rompe la regla dura 1, pero sí es una puerta nueva hacia afuera y eso **lo decide Fer, no vos ni yo**.

Concretamente: el bloque va dentro de un `SelectionContainer` (el mismo recurso que la ficha) para que se pueda seleccionar y copiar una URL, y **no** se usa `AnnotatedString` con `LinkAnnotation` ni `ClickableText` ni ningún `Intent`. Si en el informe te parece que vale la pena el enlace tocable, decilo y lo decide Fer; no lo implementes de prepo.

## Tarea 5.3 — el comentario del `build.gradle.kts` quedó desactualizado

El bloque de `AUDIO_INGLES` y el de `androidComponents` dicen que las voces inglesas **«no se pueden redistribuir»** y **«no se pueden publicar»**. Con el documento corregido eso es más fuerte de lo que corresponde: `en_US-ryan` es CC BY-NC-SA 4.0, que **sí** permite redistribuir atribuyendo, con una cláusula NC discutible y una SA que obliga a declarar la licencia por componente. **Ajustá los dos comentarios** para que digan lo que hoy dice `docs/licencias-voces.md`: que el audio inglés queda fuera del release **por decisión del proyecto, hasta que Fer decida entre regenerarlo con una voz CC BY o publicarlo atribuyendo**, y que el guarda sigue en pie mientras tanto. **La guarda y el interruptor no se tocan**: solo los comentarios.

## Tarea 5.4 — versión y guía

- Si esto entra en la Ronda S: `versionName` **0.4.2**, `versionCode` **17** (lo mismo que ya pide el prompt 35, no lo subas dos veces). Si va como ronda aparte: **0.4.3** / **18**.
- La guía de testers en español: **hay una fila mal y es culpa mía, no tuya.** Dice «El audio suena, solo en alemán … En inglés aún no», pero en **debug** `AUDIO_INGLES` es `true`, así que el tester que prueba el APK de debug **sí oye inglés**. **No la toques vos**: la arreglo yo en las seis guías de una sola vez, y en el informe solo decime si cambiaste algo más.
- `FALTANTES.md`: la sección de la Ronda S (la **65**) cubre también esta tarea. La **66** es mía.

## Qué no hacer

- No agregues `INTERNET` ni ningún permiso, ni un `Intent` hacia el navegador.
- No toques el interruptor `AUDIO_INGLES` ni la tarea `verificarReleaseSinAudioIngles`.
- No pongas texto visible fuera de `TextosInterfaz` (regla de prevención P5).
- No inventes licencias: si una voz no está en las tablas de arriba, no la nombres.

## Lo de siempre

Un commit, nada de push, el parche en `outputs/ronda-s/`. En el informe: si los clips ingleses son de `ryan` o de `lessac` por lo que puedas ver en el repo, y qué decidiste donde este prompt no cerraba.
