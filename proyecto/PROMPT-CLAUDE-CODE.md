# Brief para Claude Code — App Android de retos lingüísticos semanales

Pegar este documento completo como primer mensaje en Claude Code, dentro de un repositorio vacío que contenga la carpeta `proyecto/` de este paquete.

---

## 1. Qué construimos

Una app Android, **libre y sin conexión**, que cada semana muestra un tema gramatical o comunicativo (*Skill*) y un tema de conversación (*Topic*), con vocabulario, misión semanal, tres micro-tareas y un prompt copiable para pegar en un asistente de IA externo. El contenido es el mismo cada año para la misma semana ISO, y años anteriores se pueden consultar en modo lectura.

El contenido pedagógico **ya está diseñado, auditado y compilado**. No hay que inventar nada de contenido: hay que leer JSON y mostrarlo bien.

## 2. Requisitos no negociables

Estos no se discuten ni se optimizan. Un PR que los rompa se descarta.

1. **Sin permiso `INTERNET` en el manifiesto.** No es una recomendación: es la prueba verificable de que la app no puede rastrear nada. Si algo parece necesitar red, la respuesta es que no se hace.
2. **El único dato que se lee del dispositivo es la fecha.** Sin ubicación, sin cuentas, sin identificadores, sin sensores.
3. **Sin Google Play Services, sin Firebase, sin analítica, sin informes de fallos automáticos, sin SDK de terceros.**
4. **Compilación reproducible.** Objetivo declarado: publicar en F-Droid con cero Anti-Features.
5. **Modo "revista": manda la fecha.** La semana 14 es la semana 14 haya hecho el usuario la 13 o no. No hay puntero de progreso, ni estado guardado, ni recuperación de semanas perdidas. No implementar persistencia de progreso aunque parezca una mejora obvia — es una decisión de diseño, no un olvido.
6. **Indexación por semana ISO 8601**, con `(añoISO, semanaISO)`. Usar `java.time.temporal.WeekFields.ISO` o `LocalDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)`. **Nunca calcular semanas a mano.** El año ISO no coincide con el calendario en los bordes de diciembre y enero: la semana 53 de 2026 va del 28/12/2026 al 3/1/2027.
7. **Licencias:** código GPL-3.0, contenido CC BY-SA 4.0. Ver `proyecto/legal/`.

## 3. Stack

- Kotlin + Jetpack Compose
- `kotlinx.serialization` para leer el JSON. Sin Gson, sin Moshi, sin Retrofit.
- Contenido embebido en `assets/`, no en base de datos. Es de solo lectura y cabe en memoria.
- Widget con Glance (fase 4, no antes).
- `minSdk` 26 por `java.time`. Sin desugaring si se puede evitar.
- Sin inyección de dependencias, sin arquitectura en capas. Es una app de una pantalla que lee un JSON: mantenerla del tamaño del problema.

## 4. Modelo de datos

Autoridad: `proyecto/schema/ficha.schema.json` (27 campos) y `proyecto/schema/semana-especial.schema.json`.

**Dos formas distintas, a propósito.** Las semanas de repaso y Survival no tienen skill ni topic; forzarlas al schema de ficha exigiría hacer opcionales casi todos sus campos obligatorios. Modelar dos tipos, con una interfaz sellada:

```kotlin
sealed interface ContenidoSemanal
data class Ficha(...) : ContenidoSemanal
data class SemanaEspecial(...) : ContenidoSemanal
```

Campos que suelen malinterpretarse:

- `traducciones` es un mapa `{es, en, fr, it, pt}` con **valores vacíos a propósito** en los idiomas todavía sin traducir. Un valor vacío no es un error: mostrar el idioma base y caer a `es` si falta. Nunca mostrar la clave cruda ni "null".
- En `redemittel`, `traducciones.es` puede ser **`null` de forma deliberada**: significa "esta expresión no tiene equivalencia directa, se aprende por situación". Mostrar un guion o una marca, no un hueco.
- `variante` (opcional): si está presente — por ejemplo `"CH"` para helvetismos —, la app **debe** mostrar la etiqueta de variante y ofrecer un ajuste para desactivar ese contenido.
- `bilingue` es `true` en A2 y B1: en esos niveles la prosa se muestra también en la lengua base del usuario. De B2 en adelante, solo en la lengua que se aprende.
- `challengeType` determina el tipo de reto y por tanto la presentación. Cinco valores; ver sección 3 del syllabus.
- **`uiLanguage` y `baseLanguage` son ajustes distintos.** El idioma de la interfaz no es el idioma de las glosas. Un usuario italiano aprendiendo alemán puede querer menús en italiano y glosas en inglés. Separarlos desde el primer commit: unificarlos ahora obliga a rehacerlo después.

## 5. Pipeline de contenido

Ya existe y funciona. La app consume la salida; no hay que reescribirlo.

```
fichas/*.md  --compilar_fichas.py-->  build/*.json  --> assets/contenido/
banco.json + semanas-fijas.json  --generar_calendario.py-->  calendario_<año>_<idioma>_<nivel>.json
build/*.json  --generar_audio.py-->  assets/audio/*.mp3 + indice.json
```

Tareas Gradle a crear: que `assembleDebug` falle si el JSON de `assets/` no valida contra el schema. Es más barato fallar en el build que en el teléfono.

El generador de calendario acepta **semanas fijadas** (`data/semanas-fijas.json`). El piloto 2026 está fijado ahí porque las fichas se escribieron contra un calendario curado a mano; sin ese archivo el generador produce otra asignación y las fichas no encajarían.

## 6. Fases, con criterio de aceptación

**Fase 1 — Lectura y validación (2–3 días).**
Modelos Kotlin a partir de los dos schemas; lectura de `assets/`; test que cargue las 8 fichas del piloto y las 2 semanas especiales sin excepciones.
*Aceptación:* `./gradlew test` verde, y un test que falle a propósito si se borra un campo obligatorio.

**Fase 2 — Resolución de la semana (1–2 días).**
Dada la fecha del dispositivo, devolver el contenido correspondiente.
*Aceptación:* tests con fechas de borde: 28/12/2026 debe resolver a la semana ISO 53 de 2026; 1/1/2027 también; 4/1/2027 a la semana 1 de 2027.

**Fase 3 — Pantalla única (1–2 semanas).**
Una pantalla que muestre la ficha completa. Cuadro de referencia, vocabulario y Redemittel **plegados**, abiertos bajo demanda: la ficha completa ronda las 1.000 palabras y desplegada entera no se lee. Botón de copiar el prompt de corrección al portapapeles — la única función que toca el sistema.
*Aceptación:* las 8 fichas y las 2 semanas especiales se renderizan sin recortes ni desbordes, en pantalla estrecha y con fuente grande.

**Fase 4 — Navegación y ajustes (1 semana).**
Selector de idioma y nivel; navegación de años anteriores en modo lectura; ajustes de `uiLanguage`, `baseLanguage` y variantes regionales. Widget con Glance.
*Aceptación:* si un año o nivel no tiene contenido, la app lo dice con claridad y no se cierra. **Esto va a pasar de verdad**: la matriz idioma × nivel × año nunca estará completa, así que las celdas vacías son el caso normal, no el excepcional.

**Fase 5 — Publicación (depende de F-Droid).**
Build reproducible, metadatos, informe de Exodus. Antes del primer commit público, los cuatro archivos de `legal/`.

## 7. Advertencia sobre el contenido disponible

**El año 2026 está completo**: 34 fichas en `proyecto/build/`, semanas 37 a 53, inglés C1 y alemán B2, incluidas 4 semanas de repaso y 2 Survival. La app puede ser funcional de punta a punta para lo que queda del año.

Lo que **no** existe: el año 2027, y cualquier otro nivel o idioma. La matriz idioma × nivel × año va a estar casi siempre vacía, por diseño. Consecuencia: **la app tiene que manejar con elegancia el caso "esta combinación no tiene contenido" desde la fase 1.** No es un error, es el estado normal.

Las semanas 8, 12, 16, 24, 28 y 32 de 2026 quedan sin contenido porque la edición arranca en la 37. Si el usuario navega ahí, la app debe decirlo con claridad.

## 8. Qué leer, en este orden

| Archivo | Para qué |
|---|---|
| `proyecto/PROMPT-CLAUDE-CODE.md` | Este documento |
| `proyecto/schema/*.json` | La forma exacta del dato. Autoridad |
| `proyecto/build/*.json` | 10 ejemplos reales ya validados |
| `proyecto/PROYECTO.md` | Decisiones cerradas: no rediscutirlas |
| `proyecto/calendario-2026-S37-S53.md`, sección A | Reglas del generador, en prosa |
| `proyecto/tools/*.py` | El pipeline existente |
| `proyecto/syllabus-app-idiomas.md`, secciones 2 y 3 | Arquitectura de contenido y tipos de reto por nivel |
| `proyecto/legal/` | Licencias, privacidad, contribución |

No hace falta leer el syllabus completo: son 1.200 líneas y la mayoría es contenido pedagógico, no especificación.

## 9. Cosas que van a tentar y no se hacen

- Guardar el progreso del usuario, aunque sea local. Rompe el modo revista.
- Añadir estadísticas o rachas. Está fuera de alcance por decisión explícita.
- Descargar contenido o audio. Rompe el requisito 1.
- Unificar `uiLanguage` y `baseLanguage` "por simplicidad".
- Meter las semanas especiales en el schema de ficha.
- Generar el calendario en el dispositivo. Se precalcula y se embebe: así un año pasado es reproducible sin depender de la versión del algoritmo instalada.
