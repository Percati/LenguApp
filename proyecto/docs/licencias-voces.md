# Licencias de las voces (audio sintético)

Documentado por Overview el 12-oct-2026. Cierra la deuda abierta desde la Ronda F2 («Acerca de» nombraba las voces sin afirmar nada sobre su licencia).

El audio de la app se generó con **[Piper](https://github.com/rhasspy/piper)** (MIT) a partir de modelos de voz publicados en **[rhasspy/piper-voices](https://huggingface.co/rhasspy/piper-voices)**. El repositorio de modelos es MIT, **pero cada voz arrastra la licencia del corpus con el que se entrenó**, y esa es la que manda sobre el audio que distribuimos dentro del APK.

## Las tres voces que se usaron

| Voz | Corpus | Licencia del corpus | ¿Sirve para publicar? |
|---|---|---|---|
| `de_DE-thorsten-high` | [Thorsten-Voice](https://github.com/thorstenMueller/Thorsten-Voice) | **CC0** (dominio público) | **Sí.** Sin restricciones. |
| `en_US-ryan-high` | [RyanSpeech](https://www.kaggle.com/datasets/roholazandie/ryanspeech) | **CC BY-NC-SA 4.0** | **No.** Ver abajo. |
| `en_US-lessac-high` | Lessac / [Blizzard Challenge 2013](https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html) | Licencia propia de Blizzard | **No.** Ver abajo. |

## El problema, en claro

- **`en_US-ryan` es CC BY-NC-SA 4.0.** La cláusula **NC** (no comercial) y la **SA** (compartir igual) chocan de frente con las licencias del proyecto: el contenido es **CC BY-SA 4.0**, y BY-SA y BY-NC-SA son **incompatibles** — no se puede redistribuir material NC bajo BY-SA. Además, publicar en una tienda de aplicaciones es terreno discutible para «no comercial», aunque la app sea gratis y sin publicidad.
- **`en_US-lessac` es peor.** La licencia de Blizzard 2013 es **nominal** (se concede a una persona o institución en particular), **solo para investigación**, **prohíbe el uso comercial** y **prohíbe la redistribución** sin permiso escrito. El audio generado es material derivado del corpus.
- **`de_DE-thorsten` es CC0** y no tiene ningún problema: el alemán se puede publicar tal cual.

**Consecuencia:** los **4100 clips en inglés no se pueden publicar** como están. Los 4043 en alemán sí.

## Qué hacer con el inglés

Regenerar el inglés con una voz de corpus limpio. La candidata que encontré es **`en_GB-alba`**, cuyo corpus está bajo **[CC BY 4.0](https://datashare.ed.ac.uk/handle/10283/3270)**: compatible con CC BY-SA, sin cláusula NC y con atribución, que ya damos. El costo es regenerar los clips ingleses y **aceptar acento británico** en lugar del estadounidense actual. Para un proyecto cuyas listas de vocabulario salen de Cambridge e IELTS, el acento británico no es una pérdida.

Antes de decidir hay que **comprobar la licencia de cada voz candidata en su `MODEL_CARD`**, no en el README del repositorio: varias voces de Piper que parecen libres (`en_US-hfc_female`, `en_US-hfc_male`) también son CC BY-NC-SA, y otras (`en_US-amy`, `en_US-kusal`) remiten a un «See URL» que hay que seguir hasta el corpus.

## Hallazgo nuevo (13-oct-2026): casi todas las voces de Piper se afinaron sobre `lessac`

El `MODEL_CARD` de **`de_DE-thorsten-medium`** dice, textual: *«Finetuned from U.S. English lessac voice (medium quality)»*. Lo mismo dice el de **`fr_FR-siwis-medium`**. Es el patrón habitual de Piper: el autor entrenó un modelo base en inglés con el corpus **Lessac/Blizzard 2013** y de ahí afinó casi todas las voces de los demás idiomas.

**Qué significa y qué no:**
- El **corpus** de Thorsten sigue siendo CC0 y el audio alemán que distribuimos **no contiene habla de Lessac**: es la voz de Thorsten, generada por un modelo cuyos pesos iniciales vinieron de otro modelo.
- Pero la licencia de Blizzard **prohíbe la redistribución del corpus y limita el uso a investigación**, y nunca dice nada sobre *pesos derivados* de él. Que los pesos afinados sean o no «material derivado» del corpus es una pregunta jurídica **no resuelta** y que no vamos a resolver nosotros.
- La diferencia de riesgo entre el inglés y el alemán **sigue siendo grande**: en `en_US-lessac` distribuimos clips que son directamente la voz del corpus prohibido; en `de_DE-thorsten` distribuimos otra voz, con corpus CC0, producida por un modelo con ascendencia contaminada. Lo primero es un problema claro; lo segundo es un riesgo remoto que conviene **declarar y no esconder**.
- **No verifiqué** el `MODEL_CARD` de `thorsten/high`, que es la calidad que usa la app (el archivo existe, 281 B, pero no pude leerlo desde acá). Hay que leerlo antes de publicar: es un minuto de trabajo y cierra el punto.

**Recomendación:** publicar el alemán, y que «Acerca de» diga de dónde viene la voz (Piper, MIT; corpus Thorsten-Voice, CC0) sin afirmar nada sobre la cadena de entrenamiento. Si alguna vez hay que quedar impecable, la única salida limpia es reentrenar desde un modelo base cuyo corpus sea libre, que es un proyecto aparte y no un ajuste.

## Cómo elegir una voz para un idioma nuevo

El criterio, en orden, para cada idioma que se agregue (es, fr, it, pt):

1. **La licencia tiene que permitir redistribuir el audio generado.** No alcanza con que el modelo sea descargable gratis.
2. **Sin cláusula NC** (no comercial): es incompatible con CC BY-SA 4.0, con la que distribuimos el contenido, y publicar en una tienda es terreno discutible.
3. **CC0 y CC BY 4.0 sirven las dos.** CC0 no pide nada; CC BY pide atribución, que ya damos en «Acerca de». CC BY-SA del corpus también serviría (es la misma licencia del contenido), pero no hay candidatas.
4. **La licencia se lee en el `MODEL_CARD` de la voz y la calidad exactas**, nunca en el README del repositorio ni en una lista de terceros. Patrón de la URL:
   `https://huggingface.co/rhasspy/piper-voices/blob/main/<idioma>/<locale>/<voz>/<calidad>/MODEL_CARD`
5. **Si el `MODEL_CARD` remite a un «See URL»**, hay que seguirlo hasta el corpus y leer la licencia ahí. Varias voces que parecen libres (`en_US-hfc_female`, `en_US-hfc_male`) son CC BY-NC-SA.
6. **Anotar también la línea «Training»**, por el punto anterior de este documento.

### Candidatas por idioma (inventario, NO verificado)

Las voces que existen en `rhasspy/piper-voices` para los cuatro idiomas que faltan, con su calidad. **Ninguna licencia de esta tabla está verificada** salvo donde se indica: el proxy de este entorno solo permite leer páginas que aparecen en una búsqueda, y los `MODEL_CARD` de estas voces no aparecen. Hay que leerlos uno por uno antes de generar un solo clip.

| Idioma | Voces (calidad) | Licencia |
|---|---|---|
| Español (es_ES) | `carlfm` (x_low), `davefx` (medium), `sharvard` (medium), `mls_9972` (low), `mls_10246` (low) | sin verificar |
| Español (es_MX) | `ald` (medium), `claude` (high) | sin verificar |
| Francés (fr_FR) | `siwis` (low, medium), `gilles` (low), `tom` (medium), `upmc` (medium), `mls` (medium), `mls_1840` (low) | **`siwis`: CC BY 4.0, verificado** (corpus SIWIS, Edinburgh DataShare 10283/2353). El resto, sin verificar. |
| Italiano (it_IT) | `paola` (medium), `riccardo` (x_low) | sin verificar |
| Portugués (pt_BR) | `faber` (medium), `edresson` (low) | sin verificar |
| Portugués (pt_PT) | `tugao` (medium) | sin verificar |

**Pistas, no conclusiones:** las voces `mls_*` salen de **Multilingual LibriSpeech**, que es CC BY 4.0 — si se confirma, son las candidatas más seguras, al costo de calidad `low` y de ser voces de audiolibro. `fr_FR-siwis` es la **única confirmada hoy** y da una pauta: el francés ya tiene salida limpia. Para es, it y pt no hay nada decidido.

**Nota de alcance:** esto importa recién cuando haya contenido en esos idiomas, que no es el caso. Lo que **sí** está en el camino crítico es regenerar el inglés con `en_GB-alba`.

## Atribución que corresponde incluir en «Acerca de»

> Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0. Voz inglesa: *(a completar cuando se regenere)*.

## Fuentes
- Piper: https://github.com/rhasspy/piper
- Modelos: https://huggingface.co/rhasspy/piper-voices
- Thorsten (CC0): https://huggingface.co/rhasspy/piper-voices/blob/main/de/de_DE/thorsten/high/MODEL_CARD
- Ryan (CC BY-NC-SA 4.0): https://huggingface.co/rhasspy/piper-voices/blob/main/en/en_US/ryan/high/MODEL_CARD
- Lessac / Blizzard 2013: https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html
- Alba (CC BY 4.0): https://datashare.ed.ac.uk/handle/10283/3270
- Thorsten, calidad media (CC0, «finetuned from U.S. English lessac voice»): https://huggingface.co/rhasspy/piper-voices/blob/main/de/de_DE/thorsten/medium/MODEL_CARD
- Siwis, francés (CC BY 4.0, «finetuned from U.S. English lessac voice»): https://huggingface.co/rhasspy/piper-voices/blob/main/fr/fr_FR/siwis/medium/MODEL_CARD
- Corpus SIWIS: https://datashare.is.ed.ac.uk/handle/10283/2353
- Inventario de voces por idioma (lista de terceros, sin licencias): https://docs.gladecore.com/files/piper-voice-models
