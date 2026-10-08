# Prompt para Code — Ronda B (Planilla del profesor + Biblioteca + prompt de voz + textos fijos)

Leé primero `CLAUDE.md` (reglas duras) y `FALTANTES.md` secciones 14 y 15. Ronda A ya está hecha y pusheada. Esta ronda tiene CUATRO piezas independientes: hacelas en este orden y commiteá una por una, con `compileDebugKotlin test` en verde antes de cada commit. Si algo contradice una regla dura, frená y avisá antes de escribir código.

Restricciones comunes: sin permiso INTERNET, sin librerías de terceros nuevas con red o telemetría (para el PDF usá `android.graphics.pdf.PdfDocument`, ya incluido en Android), todo texto visible en los 6 idiomas vía `TextosInterfaz`/`EtiquetasSeccion` (nunca hardcodeado en español), nombres de campo nunca visibles (regla 9). Contenido nuevo: solo lo mínimo indicado abajo.

## Pieza 1 — Textos fijos que quedaron en español (chica, hacela primero)
- `etiquetaClase()` en `ContenidoSemanalScreen.kt` ("Semana de repaso", "Semana Survival"): pasar por `TextosInterfaz` en los 6 idiomas, según el idioma de app (o el mostrado, si el switch aplica: seguí el criterio de Ronda A).
- Botón "Copiar al portapapeles" / "Copiado" de `TarjetaPrompt`: ídem.
- `etiquetaDia(...)` en repaso/Survival usa el idioma equivocado (Ronda A solo arregló microtareas): corregir.
- Tests: uno por clave nueva que verifique los 6 idiomas no vacíos.

## Pieza 2 — Prompt de conversación de voz (piloto: inglés B2 y C1, año 2026)
Qué es: un texto que el usuario copia y pega en una IA externa con modo voz (Claude, ChatGPT…). Es el mismo mecanismo que `promptCorreccion`: copiar al portapapeles, la app no habla con ninguna IA, cero red.
- La plantilla ya está escrita: `proyecto/contenido/plantillas/prompt-voz.json` (claves `en-B2`, `en-C1`). Embebela en assets y registrá su carga con el resto del contenido. NO reescribas su texto: Fer la va a probar y la iterará él, por eso vive en un JSON y no en Kotlin.
- Placeholders: `{titulo} {consigna} {requisitos} {expresiones} {vocabulario} {oralMin}`. Se resuelven desde la ficha **en el idioma que se aprende** (B2/C1 no son bilingües, son strings planos): `titulo`; `mision.consigna`; `mision.requisitos` unidos con "; "; `expresiones` = hasta 6 `redemittel[].expresion` en orden; `vocabulario` = hasta 6 `vocabulario[].item` con `prioridad == "nucleo"`; `oralMin` = `challengeType`/`evidencia.oralMin` redondeado (si falta, 6). Placeholder sin dato: quitá la línea entera, no dejes llaves.
- UI: tarjeta con el mismo componente que `TarjetaPrompt`, título "Prompt para conversar por voz" (6 idiomas) y debajo una ayuda corta (6 idiomas): "Pegalo al empezar una conversación de voz con tu IA. Decile *feedback* cuando quieras la devolución." Ubicación: justo después del prompt de corrección.
- Visibilidad: solo si existe plantilla para `{idioma}-{nivel}` de la ficha. Derivalo del contenido (regla 10), no con un `if` por idioma. Hoy eso significa inglés B2 y C1; las fichas 2026 de esos pares ya están en assets.
- No aparece en semanas de repaso/Survival ni en otros pares. Sin switch de traducción (el prompt va siempre en el idioma que se aprende, porque la IA debe hablarlo).
- Tests: renderizado de placeholders con una ficha EN C1 y una EN B2 reales; línea omitida si falta dato; no visible en DE-B2.

## Pieza 3 — Planilla del profesor (PDF local)
Decisiones cerradas por Fer:
- Botón/link al final de cada ficha (también repaso/Survival; todos los idiomas y niveles A2-C2; diseño genérico). Abre un PDF generado localmente en el idioma que se aprende y lo entrega al lector de PDF del sistema (guardar/compartir) vía `FileProvider` o `ACTION_VIEW` sobre caché; sin permisos de almacenamiento.
- Se genera siempre (haya o no tareas hechas) y también para semanas pasadas.
- Estética cuidada y sobria, respetando márgenes de impresión (A4, ≥ 15 mm).
- Contenido (reutilizá ≥ 85-90% de lo existente en la ficha): objetivo comunicativo; topic + hasta 2 subtemas; gramática de repaso sin explicación extensa; 3 Redemittel objetivo + 2 "de reparación" (frases para pedir ayuda/reformular); una situación cotidiana y otra profesional; un giro inesperado SOLO como instrucción genérica al profesor ("introducí una complicación a mitad", sin preguntas escritas); 3 criterios de feedback; recordatorio de no reescribir todo lo que dice el alumno. Puede incluir 1-2 semanas anteriores como reciclaje pasivo (foco en la actual).
- Si necesitás textos fijos de la planilla (títulos de sección, criterios), van en el idioma que se aprende y en un JSON en assets, no en Kotlin. Proponé el JSON y los textos para los 6 idiomas aprendibles en un commit aparte para que Fer los revise antes de dar la pieza por cerrada.
- Tests: generación sin crash para un par A2, uno C1 y una semana de repaso; PDF de una página o dos como máximo.

## Pieza 4 — Biblioteca
Decisiones cerradas por Fer:
- Pantalla de consulta pura, solo en pantalla (sin exportar, sin ida y vuelta a la ficha). Dos secciones separadas: **Vocabulario** y **Expresiones**.
- Pestañas de idioma (de `idiomasAprendidos`); el filtro de idioma FILTRA, no mezcla.
- Filtro de nivel: por defecto el nivel actual del idioma, cambiable dentro de la Biblioteca sin tocar Ajustes; multi-selección; un ítem que aparece en varios niveles sale en cada filtro. Ítems deduplicados.
- Vocabulario: filtro por topic. Los nombres legibles están en `data/banco.json` → `topicNombres` (T01-T14, en español). `banco.json` NO está en assets: generá en el build un JSON chico con `topicNombres` y embebelo (si hace falta traducir los nombres a los otros idiomas de app, avisá y no inventes: dejá español como fallback y reportá).
- Expresiones: filtro por `categoriasUso` (array; una expresión puede tener varias). Lista cerrada en `data/categorias-uso.json` (18 funciones comunicativas + 15 patrones gramaticales): agrupá los chips por rama. 273 expresiones tienen `categoriasUso: []`; mostralas igual, solo que no aparecen bajo ningún chip de categoría (agregá "Sin categoría" solo si resulta barato). Los nombres de las categorías están en español: mostralos tal cual y avisá en el informe si ves que hace falta traducirlos.
- Buscador de texto libre. La traducción se muestra siempre en el idioma de app de Ajustes (no depende del switch de la ficha).
- La estrella es la misma de Guardados: mismo backing Room, sincronizada en ambos sentidos. No crear tabla nueva.
- Sin persistencia nueva (regla 4): filtros y búsqueda arrancan en default en cada apertura.
- Entrada desde la pantalla principal, con el estilo de los demás accesos.
- Tests: deduplicado, filtro multi-nivel, filtro por categoría con ítem multi-categoría, estrella sincronizada con Guardados.

## Al terminar
Informe corto: qué commit por pieza, tests, qué decisiones tomaste donde el prompt no cerraba, y cualquier regla dura que hayas tenido que rozar. Actualizá la sección correspondiente de `FALTANTES.md`. No pushees: Fer aplica los parches.
