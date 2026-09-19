# Ajustes para la Fase 5

Dos bloques: lo que ya está corregido en los datos (nada que hacer en la app salvo renderizarlo) y lo que hay que implementar.

---

## A. Corregido en el compilador — recompilar y listo

El problema de los asteriscos era mío, no de la app. El Markdown fuente lleva marcado en línea y el compilador lo estaba pasando crudo al JSON, mal formado y a veces anidado. Además había pérdida silenciosa de contenido.

| Problema | Estado |
|---|---|
| Asteriscos como comillas en el cuadro de referencia | **Resuelto.** El marcado que envolvía el campo entero se quita; la app estiliza por rol |
| Doble asterisco en los ejemplos del cuadro | **Resuelto.** Se conserva solo el destaque semántico, sin anidar |
| Asteriscos en Redemittel | **Resuelto.** `expresion` llega en texto plano |
| Asteriscos en las notas (50 campos, no reportado) | **Resuelto** |
| **Viñetas que se comían el asterisco inicial** (104 campos con marcado sin cerrar) | **Resuelto.** Era el peor: `strip("-* ")` quitaba el marcador de la cita |
| Daga `†` filtrándose en 10 ítems de vocabulario | **Resuelto.** Ahora es el campo booleano `bajoNivelJustificado` |
| `reccion: "—"` en 14 fichas | **Resuelto.** El campo se omite cuando no rige nada |
| Ítems de vocabulario en negrita, redundante con `prioridad` | **Resuelto.** Texto plano |
| **Notas al pie de los cuadros perdidas en 10 fichas** | **Resuelto.** Contenían reglas mnemotécnicas: *obwohl* introduce la concesión, *trotzdem* el resultado inesperado |

Verificación: **0 campos mal formados de 2.579.**

### Lo único que la app tiene que implementar de este bloque

Queda **una sola convención de marcado en línea**, plana y sin anidar:

- `*cita*` — una palabra o expresión citada dentro de la prosa
- `**destaque**` — el elemento sobre el que se llama la atención

El destaque no es decoración: en la ficha de Satzbau marca **dónde va el verbo**, que es todo el punto de la carta. No se puede eliminar.

Hace falta **un solo renderizador** que convierta esas dos marcas a `AnnotatedString`. Parser de un nivel, sin recursión — está garantizado por el compilador. La convención está documentada en el `$comment` del schema.

Campos que pueden traer marcado: `descripcion`, `notas`, `errores`, `contraste`, `autochequeo`, `mision.consigna`, `mision.requisitos`, `promptCorreccion`, `ejemplos[].texto`, `cuadroReferencia.filas/columnas/notaPie`, `redemittel[].funcion`, `vocabulario[].nota`.
Campos que llegan **siempre** en texto plano: `vocabulario[].item`, `redemittel[].expresion`, `titulo`, `subtitulo`.

**`cuadroReferencia.notaPie` es nuevo** y hay que renderizarlo: va debajo del cuadro, en tamaño menor.

---

## B. Para implementar en la app

### B1. Títulos de sección en el idioma de la ficha

Ahora los títulos están en español mientras el contenido está en alemán. Incoherente.

Los títulos de sección **siguen el idioma de la ficha**, no el de la interfaz. El Markdown fuente ya los tiene así: *Beschreibung*, *Übersichtskasten*, *Beispiele*, *Hinweise*, *Typische Fehler*, *Themenwortschatz*, *Redemittel*, *Wochenaufgabe*, *Mikroaufgaben*, *Selbstkontrolle*, *Korrekturprompt*.

Tabla de etiquetas por idioma, indexada por `ficha.idioma`:

| Sección | en | de |
|---|---|---|
| descripcion | Description | Beschreibung |
| cuadroReferencia | Reference | Übersichtskasten |
| ejemplos | Examples | Beispiele |
| notas | Notes | Hinweise |
| contraste | Contrast with Spanish | Kontrast zum Spanischen |
| errores | Common mistakes | Typische Fehler |
| vocabulario | Vocabulary | Themenwortschatz |
| redemittel | Expressions | Redemittel |
| mision | Mission | Wochenaufgabe |
| microtareas | Micro-tasks | Mikroaufgaben |
| autochequeo | Self-check | Selbstkontrolle |
| promptCorreccion | Correction prompt | Korrekturprompt |

**Excepción:** cuando `bilingue == true` (A2 y B1), el título va en los dos idiomas — *Beschreibung / Descripción*. Es la decisión de diseño ya tomada en el syllabus, sección 3.

La etiqueta de `contraste` se arma con el idioma base del usuario, no fija a español.

### B2. Navegación semana por semana

Flechas adelante y atrás sobre la semana actual, con la fecha visible. Requisitos:

- Debe quedar claro **cuál es la semana de hoy**, y poder volver a ella en un toque.
- Navegar no cambia nada: **no hay estado que guardar**, coherente con el modo revista.
- Las semanas sin contenido se muestran con el motivo (ya tenés `RazonSinContenido`), no en blanco.
- Debe llegar hasta las semanas 1–36 de 2026, que no tienen contenido porque la edición arranca en la 37.

### B3. Selector de idioma

Hoy solo se ve alemán y no hay forma de llegar al inglés. Es el hueco funcional más grave.

Con dos idiomas, un conmutador visible en la cabecera es mejor que enterrarlo en ajustes. Cambiar de idioma mantiene la semana.

### B4. Rediseño visual

Minimalista, no plano. Sugerencias concretas, todas rechazables:

1. **La cabecera es una línea corrida de metadatos** (`Topic · Type · Evidence · Time`). Convertirla en etiquetas discretas separadas, no en una frase.
2. **El cuadro de referencia como tarjetas "columna: valor"** ya está bien resuelto para pantalla angosta. Mantener.
3. **Jerarquía tipográfica en vez de líneas divisorias.** Lo que hace ver "plana" una pantalla suele ser exceso de separadores y falta de contraste de tamaño. Menos reglas horizontales, más diferencia entre título de sección y cuerpo.
4. **Vocabulario: distinguir los cuatro ítems núcleo.** El campo `prioridad` ya viene; hoy no se usa visualmente. Cuatro destacados y diez secundarios es información útil que está disponible y desperdiciada.
5. **El prompt de corrección merece tratamiento propio.** Es la única acción de la pantalla. Un bloque con el botón de copiar bien visible, no una sección más de la lista.
6. **`bajoNivelJustificado` y `variante`** son dos marcas sutiles que hoy no se muestran. Una insignia pequeña alcanza — en el caso de `variante: CH` es además un requisito, porque tiene que poder desactivarse.
7. **Espacio en blanco antes que bordes.** Para un texto de 1.000 palabras leído en el teléfono, el aire entre bloques hace más por la legibilidad que cualquier recuadro.

Una restricción que no debe perderse en el rediseño: **las secciones plegables siguen plegadas por defecto.**
