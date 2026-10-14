# Licencias de las voces (audio sintético)

Documentado por Overview el 12-oct-2026. **Revisado y corregido el 14-oct-2026** a pedido de Fer, que cuestionó la conclusión original. Tenía razón en lo principal: la versión anterior de este documento decía «no se pueden publicar» cuando lo correcto es «se pueden publicar atribuyendo, con un asterisco en un caso y una pregunta abierta en el otro». La corrección está abajo, en «Qué cambió en la revisión».

El audio de la app se generó con **[Piper](https://github.com/rhasspy/piper)** (MIT) a partir de modelos de voz publicados en **[rhasspy/piper-voices](https://huggingface.co/rhasspy/piper-voices)**. El repositorio de modelos es MIT, **pero cada voz arrastra la licencia del corpus con el que se entrenó**, y esa es la que hay que leer.

## Las voces, y qué pide cada licencia

| Voz | Corpus | Licencia del corpus | Qué obliga |
|---|---|---|---|
| `de_DE-thorsten-high` | [Thorsten-Voice](https://github.com/thorstenMueller/Thorsten-Voice) | **CC0 1.0** (dominio público) | Nada. Atribuir es cortesía, no obligación. |
| `en_US-ryan-high` | [RyanSpeech](https://www.kaggle.com/datasets/roholazandie/ryanspeech) | **CC BY-NC-SA 4.0** | Atribuir; **no comercial**; **compartir igual**. |
| `en_US-lessac-high` | Lessac / [Blizzard Challenge 2013](https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html) | Licencia propia de CSTR, **no es una licencia Creative Commons** | Caso aparte: ver abajo. |

## Qué cambió en la revisión del 14-oct-2026

**Lo que estaba mal en la versión anterior:** no distinguí tres cosas distintas y las traté todas como «no publicable».

1. **CC0 y CC BY no son un problema, en ningún caso.** La única obligación de CC BY es la atribución, y atribuir es exactamente lo que vamos a hacer: una sección de referencias en «Acerca de» con la voz, el corpus, la licencia y el enlace. Esto vale para `de_DE-thorsten` (CC0, que ni eso exige) y para cualquier voz CC BY que se use en el futuro, `en_GB-alba` incluida.
2. **La cláusula NC de `en_US-ryan` es discutible, no prohibitiva — y para esta app se inclina a nuestro favor.** La definición de Creative Commons es **de intención, no de dinero**: «NonCommercial means not primarily intended for or directed towards commercial advantage or monetary compensation», y la propia guía dice que «the context and purpose of the use is relevant». LenguApp es gratis, sin publicidad, sin compras, sin telemetría y de código abierto: el propósito no es ventaja comercial. Publicar en una tienda no convierte el uso en comercial por sí mismo. **Riesgo bajo.**
3. **La cláusula SA de `en_US-ryan` es el problema real, y no se arregla citando.** BY-NC-SA exige que las **obras derivadas** vayan también bajo BY-NC-SA. Nuestro contenido es **CC BY-SA 4.0**, y las dos son **incompatibles en un sentido**: no se puede relicenciar material NC como BY-SA (le quitaría el NC), ni meter material BY-SA dentro de una obra BY-NC-SA (BY-SA prohíbe agregar restricciones). **Pero los clips no son una obra derivada de nuestro contenido:** son un componente separado que viaja junto, y las dos licencias permiten eso en una «colección», con su licencia declarada por componente. Así que es viable, con una condición: **la app no puede decir «todo el contenido es CC BY-SA 4.0» en bloque** — tiene que decir «el contenido es CC BY-SA 4.0; el audio inglés es CC BY-NC-SA 4.0».
4. **El asterisco que queda no es legal, es de proyecto.** Un componente NC hace que la app deje de ser limpiamente reutilizable: quien la bifurque hereda la restricción, y un principio declarado del proyecto es ser abierto y auditable. Eso **es una decisión de Fer**, no un impedimento.

**Y una retractación:** el 13-oct anoté como hallazgo preocupante que `de_DE-thorsten` y `fr_FR-siwis` se afinaron sobre la voz `lessac` («Finetuned from U.S. English lessac voice»), y que por eso el alemán arrastraría la licencia de Blizzard. **Le di más peso del que merecía.** Es el mismo razonamiento de abajo (¿son los pesos y la salida obra derivada del corpus?), aplicado dos pasos más lejos, y **vale para casi todas las voces de Piper**. Si ese razonamiento se sostuviera, no se podría publicar nada generado con Piper en ningún idioma — y una conclusión tan fuerte es señal de que el razonamiento es demasiado fuerte. El dato queda anotado en «Fuentes» porque es cierto; la alarma, retirada.

## `en_US-lessac` no se usó: el caso no existe

**Confirmado por Fer el 14-oct-2026: los 4100 clips ingleses son todos de `en_US-ryan-high`.** El manifiesto decía «dos voces seleccionables, se genera con las dos», pero eso era el plan y quedó sin implementar. **Así que la licencia de Blizzard no toca ningún archivo que la app distribuya**, y todo el análisis de abajo queda como registro de por qué esa voz **no se va a usar nunca**, no como un problema abierto.

Vale la pena dejarlo escrito porque es el único criterio de descarte duro que encontramos, y sirve para el día que se evalúe una voz nueva.

## Por qué `en_US-lessac` quedaría descartada si alguna vez se evaluara

Acá la idea de «todas se pueden usar citando la fuente» **no aplica**, y conviene verlo con la licencia delante. La [licencia de Blizzard 2013](https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html), leída el 14-oct-2026:

- **No es Creative Commons y no tiene cláusula de atribución.** No hay nada que citar que la satisfaga.
- Se concede **solo a la persona u organización nombrada**, es **no exclusiva y no transferible** y **sin derecho a sublicenciar** (cl. 3.2).
- Solo para **Research Purposes**, y la definición **excluye expresamente** el desarrollo, la comercialización, la venta o el licenciamiento de síntesis de voz.
- El usuario se compromete a **«not to lend, hire, sell, distribute or otherwise part with the Materials»** (cl. 7), y tiene que derivar a los licenciantes a cualquier tercero que quiera usarlos (cl. 9).

**Pero hay dos cosas que juegan a nuestro favor y que tampoco había tenido en cuenta:**

1. **Esa licencia no obliga a Fer.** Es un contrato entre CSTR y quien la firmó — el autor de Piper, que entrenó el modelo. Fer descargó un modelo publicado bajo MIT de un repositorio público y nunca aceptó términos con CSTR. Si el autor de Piper se excedió, eso es entre él y CSTR.
2. **La licencia no dice nada sobre modelos ni sobre voz sintética.** Lo verifiqué: el texto no aborda si lo construido a partir de los datos puede distribuirse; solo tiene los límites generales de redistribución **de los Materials**. Que los pesos de un modelo o su salida sean «obra derivada» del corpus de entrenamiento es una **pregunta jurídica abierta**, y la lectura más común se inclina por que la salida de un modelo no es obra derivada de sus datos de entrenamiento.

Dicho de otro modo: por contrato, Fer no está obligado; por derecho de autor, depende de una pregunta sin respuesta firme. **No es un «no se puede». Es el único caso del inventario donde la única defensa es un argumento jurídico que preferiríamos no tener que hacer.**

**Esto no es asesoramiento legal:** no soy abogado, y lo de arriba es lectura de los textos de licencia con criterio de ingeniería. Para un proyecto gratuito y personal el riesgo real es bajísimo en los dos casos; lo que cambia según la decisión es cuánta explicación hay que dar si alguien pregunta.

## Recomendación

1. **Atribuir todas las voces en «Acerca de»**, las de dominio público y las licenciadas: voz, corpus, licencia y enlace. Es lo que pidió Fer y es correcto: cierra la obligación de CC BY, satisface la de CC BY-NC-SA en su parte de atribución, y para CC0 es simple honestidad.
2. **Declarar la licencia por componente**, no en bloque: código GPL-3.0, contenido CC BY-SA 4.0, **audio** con la licencia de cada voz. Si el audio inglés se queda como está, eso incluye decir que es CC BY-NC-SA 4.0.
3. **El alemán se publica tal cual.** CC0, sin asterisco de ningún tipo.
4. **DECIDIDO (Fer, 14-oct-2026): el inglés se publica como está, atribuyendo.** No se regeneran los clips. Consecuencias, que son las que se implementan en `prompts/37`: el bloque «Voces» nombra la voz inglesa **siempre** (no solo en debug), la línea de licencias se declara por componente, y **el audio inglés pasa a entrar en el release** — o sea que el interruptor `AUDIO_INGLES` se enciende y la guarda `verificarReleaseSinAudioIngles` se retira. El APK de release sube de ~37,8 MB a ~55 MB.

   Queda en pie el asterisco de la cláusula NC: la app deja de ser limpiamente reutilizable por quien la bifurque, y eso **hay que decirlo donde se publique el código**, no solo en «Acerca de». Fer lo sabe y lo acepta; mi preferencia era la otra y queda anotada abajo por si alguna vez cambia.

### Lo que no se hizo, y queda anotado por si alguna vez cambia

**Regenerar el inglés con `en_GB-alba`** ([CC BY 4.0](https://datashare.ed.ac.uk/handle/10283/3270)): costaba una tarde de generación por lote y eliminaba el asterisco NC de una vez — atribución y nada más, y la app limpiamente reutilizable. El costo era aceptar acento británico. **Mi preferencia era esa**, por una razón modesta: una tarde de cómputo compra no tener que explicar nada nunca. Fer prefirió no rehacer 4100 clips, que es una razón perfectamente buena. Si algún día aparece otro motivo para regenerar el audio inglés (una voz mejor, más velocidades, otro acento), **conviene aprovechar el viaje y usar una voz CC BY o CC0**: el trabajo de regenerar es el mismo y el asterisco se cae solo.

**Nota de mantenimiento:** `audio-manifiesto.md` sigue diciendo «`en_US-ryan-high`, `en_US-lessac-high` — dos voces seleccionables, se genera con las dos». Eso nunca se implementó y ahora se sabe que no se va a implementar: **hay que corregir esa línea** para que diga la verdad (una sola voz, `en_US-ryan-high`).

## Cómo elegir una voz para un idioma nuevo

El criterio, en orden, para cada idioma que se agregue (es, fr, it, pt):

1. **Leer la licencia en el `MODEL_CARD` de la voz y la calidad exactas**, nunca en el README del repositorio ni en una lista de terceros. Patrón de la URL:
   `https://huggingface.co/rhasspy/piper-voices/blob/main/<idioma>/<locale>/<voz>/<calidad>/MODEL_CARD`
2. **Si el `MODEL_CARD` remite a un «See URL»**, seguirlo hasta el corpus y leer la licencia ahí. Varias voces que parecen libres (`en_US-hfc_female`, `en_US-hfc_male`) son CC BY-NC-SA.
3. **Preferir CC0 o CC BY 4.0.** Las dos sirven sin asterisco: CC0 no pide nada, CC BY pide atribución, que ya damos. CC BY-SA del corpus también serviría (es la licencia de nuestro contenido).
4. **CC BY-NC-SA sirve con asterisco** (ver arriba): hay que declarar la licencia de ese componente aparte y aceptar que la app deja de ser limpiamente reutilizable.
5. **Descartar cualquier licencia nominal, de investigación o que prohíba la redistribución** — del estilo de Blizzard. No hay atribución que las cure.
6. **Anotar siempre la línea «Training»** del `MODEL_CARD`, por trazabilidad, sin sacar conclusiones de ella.

### Candidatas por idioma (inventario, casi todo SIN verificar)

Las voces que existen en `rhasspy/piper-voices` para los cuatro idiomas que faltan, con su calidad. **Ninguna licencia de esta tabla está verificada** salvo donde se indica: desde este entorno solo se pueden leer páginas que aparecen en una búsqueda, y los `MODEL_CARD` de estas voces no aparecen. Hay que leerlos uno por uno antes de generar un solo clip.

| Idioma | Voces (calidad) | Licencia |
|---|---|---|
| Español (es_ES) | `carlfm` (x_low), `davefx` (medium), `sharvard` (medium), `mls_9972` (low), `mls_10246` (low) | sin verificar |
| Español (es_MX) | `ald` (medium), `claude` (high) | sin verificar |
| Francés (fr_FR) | `siwis` (low, medium), `gilles` (low), `tom` (medium), `upmc` (medium), `mls` (medium), `mls_1840` (low) | **`siwis`: CC BY 4.0, verificado** (corpus SIWIS, Edinburgh DataShare 10283/2353). El resto, sin verificar. |
| Italiano (it_IT) | `paola` (medium), `riccardo` (x_low) | sin verificar |
| Portugués (pt_BR) | `faber` (medium), `edresson` (low) | sin verificar |
| Portugués (pt_PT) | `tugao` (medium) | sin verificar |

**Pistas, no conclusiones:** las voces `mls_*` salen de **Multilingual LibriSpeech**, que es CC BY 4.0 — si se confirma, serían candidatas sin asterisco, al costo de calidad `low` y de ser voces de audiolibro. `fr_FR-siwis` es la **única confirmada** y deja al francés resuelto.

**Nota de alcance:** esto importa recién cuando haya contenido en esos idiomas, que no es el caso.

## Atribución que corresponde incluir en «Acerca de»

El texto que va en la app (ver `prompts/36`), con la fila inglesa según lo que se decida:

> **Voces.** El audio se generó con Piper (MIT). Alemán: `de_DE-thorsten-high`, corpus Thorsten-Voice (CC0 1.0). Inglés: `en_US-ryan-high`, corpus RyanSpeech (CC BY-NC-SA 4.0).

## Fuentes
- Piper: https://github.com/rhasspy/piper
- Modelos: https://huggingface.co/rhasspy/piper-voices
- Thorsten (CC0): https://huggingface.co/rhasspy/piper-voices/blob/main/de/de_DE/thorsten/high/MODEL_CARD
- Thorsten, calidad media (CC0; «finetuned from U.S. English lessac voice»): https://huggingface.co/rhasspy/piper-voices/blob/main/de/de_DE/thorsten/medium/MODEL_CARD
- Ryan (CC BY-NC-SA 4.0): https://huggingface.co/rhasspy/piper-voices/blob/main/en/en_US/ryan/high/MODEL_CARD
- Lessac / Blizzard 2013, texto de la licencia: https://www.cstr.ed.ac.uk/projects/blizzard/2013/lessac_blizzard2013/license.html
- Siwis, francés (CC BY 4.0; «finetuned from U.S. English lessac voice»): https://huggingface.co/rhasspy/piper-voices/blob/main/fr/fr_FR/siwis/medium/MODEL_CARD
- Corpus SIWIS: https://datashare.is.ed.ac.uk/handle/10283/2353
- Alba (CC BY 4.0): https://datashare.ed.ac.uk/handle/10283/3270
- Creative Commons, interpretación de NonCommercial: https://wiki.creativecommons.org/NonCommercial_interpretation
- Inventario de voces por idioma (lista de terceros, sin licencias): https://docs.gladecore.com/files/piper-voice-models
