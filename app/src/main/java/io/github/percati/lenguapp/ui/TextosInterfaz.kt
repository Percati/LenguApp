package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma

/**
 * A diferencia de EtiquetasSeccion.kt (indexada por el idioma de la ficha
 * que se esta leyendo), esto es texto de la propia app -- indexa por los 6
 * idiomas soportados como idioma de interfaz, no solo los que tienen
 * contenido de ficha. El resto del chrome (mensajes de "sin contenido",
 * etc.) sigue diferido a proposito -- AJUSTES-FASE-7.md, bloque 2.2: solo
 * "Semana", "Ajustes", "Hoy" y el aviso de doble atras son las que se ven
 * en la pantalla principal.
 */
private fun <T> textoPara(idioma: Idioma, tabla: Map<Idioma, T>, porDefecto: T): T =
    tabla[idioma] ?: porDefecto

private val IDIOMA_NO_DISPONIBLE = mapOf(
    Idioma.ES to "Este idioma todavía no tiene contenido.",
    Idioma.EN to "This language doesn't have content yet.",
    Idioma.DE to "Für diese Sprache gibt es noch keinen Inhalt.",
    Idioma.FR to "Ce contenu n'est pas encore disponible dans cette langue.",
    Idioma.IT to "Questa lingua non ha ancora contenuti.",
    Idioma.PT to "Este idioma ainda não tem conteúdo.",
)

fun mensajeIdiomaNoDisponible(idiomaInterfaz: Idioma): String =
    textoPara(idiomaInterfaz, IDIOMA_NO_DISPONIBLE, IDIOMA_NO_DISPONIBLE.getValue(Idioma.ES))

private val PALABRA_SEMANA = mapOf(
    Idioma.ES to "Semana", Idioma.EN to "Week", Idioma.DE to "Woche",
    Idioma.FR to "Semaine", Idioma.IT to "Settimana", Idioma.PT to "Semana",
)

fun etiquetaSemana(idiomaInterfaz: Idioma): String =
    textoPara(idiomaInterfaz, PALABRA_SEMANA, PALABRA_SEMANA.getValue(Idioma.ES))

private val PALABRA_AJUSTES = mapOf(
    Idioma.ES to "Ajustes", Idioma.EN to "Settings", Idioma.DE to "Einstellungen",
    Idioma.FR to "Paramètres", Idioma.IT to "Impostazioni", Idioma.PT to "Configurações",
)

fun etiquetaAjustes(idiomaInterfaz: Idioma): String =
    textoPara(idiomaInterfaz, PALABRA_AJUSTES, PALABRA_AJUSTES.getValue(Idioma.ES))

private val PALABRA_HOY = mapOf(
    Idioma.ES to "Hoy", Idioma.EN to "Today", Idioma.DE to "Heute",
    Idioma.FR to "Aujourd'hui", Idioma.IT to "Oggi", Idioma.PT to "Hoje",
)

fun etiquetaHoy(idiomaInterfaz: Idioma): String =
    textoPara(idiomaInterfaz, PALABRA_HOY, PALABRA_HOY.getValue(Idioma.ES))

private val MENSAJE_DOBLE_ATRAS = mapOf(
    Idioma.ES to "Tocá atrás de nuevo para salir",
    Idioma.EN to "Tap back again to exit",
    Idioma.DE to "Nochmal zurück tippen zum Beenden",
    Idioma.FR to "Appuyez à nouveau sur retour pour quitter",
    Idioma.IT to "Tocca di nuovo indietro per uscire",
    Idioma.PT to "Toque voltar de novo para sair",
)

/** AJUSTES-FASE-7.md, bloque 2.4: "del mismo tipo que el del idioma no disponible" -- un toast, no una ventana. */
fun mensajeDobleAtrasParaSalir(idiomaInterfaz: Idioma): String =
    textoPara(idiomaInterfaz, MENSAJE_DOBLE_ATRAS, MENSAJE_DOBLE_ATRAS.getValue(Idioma.ES))
