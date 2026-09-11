package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma

/**
 * A diferencia de EtiquetasSeccion.kt (indexada por el idioma de la ficha
 * que se esta leyendo), esto es texto de la propia app -- hoy solo el toast
 * de "idioma no disponible" (AJUSTES-FASE-6.md, bloque B) -- e indexa por
 * los 6 idiomas soportados como idioma de interfaz, no solo los que tienen
 * contenido de ficha.
 */
private val IDIOMA_NO_DISPONIBLE = mapOf(
    Idioma.ES to "Este idioma todavía no tiene contenido.",
    Idioma.EN to "This language doesn't have content yet.",
    Idioma.DE to "Für diese Sprache gibt es noch keinen Inhalt.",
    Idioma.FR to "Ce contenu n'est pas encore disponible dans cette langue.",
    Idioma.IT to "Questa lingua non ha ancora contenuti.",
    Idioma.PT to "Este idioma ainda não tem conteúdo.",
)

fun mensajeIdiomaNoDisponible(idiomaInterfaz: Idioma): String =
    IDIOMA_NO_DISPONIBLE[idiomaInterfaz] ?: IDIOMA_NO_DISPONIBLE.getValue(Idioma.ES)
