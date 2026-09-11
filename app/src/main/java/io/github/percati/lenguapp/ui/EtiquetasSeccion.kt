package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma

/**
 * Los titulos de seccion siguen el idioma de la ficha, no el de la
 * interfaz: mostrar "Vocabulario" en una ficha alemana mientras el resto
 * esta en aleman es incoherente. Tabla indexada por ficha.idioma -- ver
 * proyecto/AJUSTES-FASE-5.md, B1. Solo en/de por ahora: son los dos unicos
 * idiomas con contenido.
 */
private val ETIQUETAS_EN = mapOf(
    "descripcion" to "Description",
    "cuadroReferencia" to "Reference",
    "ejemplos" to "Examples",
    "notas" to "Notes",
    "errores" to "Common mistakes",
    "vocabulario" to "Vocabulary",
    "redemittel" to "Expressions",
    "mision" to "Mission",
    // No esta en la tabla de AJUSTES-FASE-5.md (esa cubre solo la ficha):
    // SemanaEspecial tiene su propia seccion "requisitos", sin "mision".
    "requisitos" to "Requirements",
    "microtareas" to "Micro-tasks",
    "autochequeo" to "Self-check",
    "promptCorreccion" to "Correction prompt",
)

private val ETIQUETAS_DE = mapOf(
    "descripcion" to "Beschreibung",
    "cuadroReferencia" to "Übersichtskasten",
    "ejemplos" to "Beispiele",
    "notas" to "Hinweise",
    "errores" to "Typische Fehler",
    "vocabulario" to "Themenwortschatz",
    "redemittel" to "Redemittel",
    "mision" to "Wochenaufgabe",
    "requisitos" to "Anforderungen",
    "microtareas" to "Mikroaufgaben",
    "autochequeo" to "Selbstkontrolle",
    "promptCorreccion" to "Korrekturprompt",
)

/**
 * Espanol para la mitad bilingue (A2/B1): CLAUDE.md dice bilingue = tambien
 * en la lengua base, pero en 2026 "es" es la unica lengua base con datos, y
 * A2/B1 todavia no tienen fichas. Cuando haya otras lenguas base con
 * contenido bilingue real, esto se generaliza igual que contraste.
 */
private val ETIQUETAS_ES_BILINGUE = mapOf(
    "descripcion" to "Descripción",
    "cuadroReferencia" to "Cuadro de referencia",
    "ejemplos" to "Ejemplos",
    "notas" to "Notas",
    "errores" to "Errores comunes",
    "vocabulario" to "Vocabulario",
    "redemittel" to "Redemittel",
    "mision" to "Misión",
    "microtareas" to "Micro-tareas",
    "autochequeo" to "Autochequeo",
    "promptCorreccion" to "Prompt de corrección",
)

private fun etiquetasDe(idioma: Idioma): Map<String, String> = if (idioma == Idioma.DE) ETIQUETAS_DE else ETIQUETAS_EN

/**
 * `bilingue == true` (A2/B1): el titulo va en los dos idiomas, p.ej.
 * "Beschreibung / Descripción" -- decision ya tomada en el syllabus, sección 3.
 */
fun etiquetaSeccion(clave: String, idioma: Idioma, bilingue: Boolean): String {
    val propia = etiquetasDe(idioma)[clave] ?: clave
    if (!bilingue) return propia
    val espanol = ETIQUETAS_ES_BILINGUE[clave] ?: clave
    return "$propia / $espanol"
}

/**
 * Nombres de idioma para armar "Kontrast zum Spanischen" / "Contrast with
 * Spanish" con el idioma base real del usuario, no fijo a español --
 * AJUSTES-FASE-5.md, B1.
 */
private val NOMBRE_IDIOMA_EN = mapOf(
    Idioma.ES to "Spanish", Idioma.EN to "English", Idioma.DE to "German",
    Idioma.FR to "French", Idioma.IT to "Italian", Idioma.PT to "Portuguese",
)

private val NOMBRE_IDIOMA_DE_DATIVO = mapOf(
    Idioma.ES to "Spanischen", Idioma.EN to "Englischen", Idioma.DE to "Deutschen",
    Idioma.FR to "Französischen", Idioma.IT to "Italienischen", Idioma.PT to "Portugiesischen",
)

fun etiquetaContraste(idioma: Idioma, idiomaBase: Idioma): String = when (idioma) {
    Idioma.DE -> "Kontrast zum ${NOMBRE_IDIOMA_DE_DATIVO[idiomaBase] ?: idiomaBase.name}"
    else -> "Contrast with ${NOMBRE_IDIOMA_EN[idiomaBase] ?: idiomaBase.name}"
}
