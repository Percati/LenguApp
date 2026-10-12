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

## Atribución que corresponde incluir en «Acerca de»

> Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0. Voz inglesa: *(a completar cuando se regenere)*.

## Fuentes
- Piper: https://github.com/rhasspy/piper
- Modelos: https://huggingface.co/rhasspy/piper-voices
- Thorsten (CC0): https://huggingface.co/rhasspy/piper-voices/blob/main/de/de_DE/thorsten/high/MODEL_CARD
- Ryan (CC BY-NC-SA 4.0): https://huggingface.co/rhasspy/piper-voices/blob/main/en/en_US/ryan/high/MODEL_CARD
- Lessac / Blizzard 2013: https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html
- Alba (CC BY 4.0): https://datashare.ed.ac.uk/handle/10283/3270
