package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel

/**
 * Traduccion completa del *chrome* de la interfaz a los seis idiomas
 * soportados como idioma de interfaz -- AJUSTES-FASE-9.md, bloque B. Se
 * levanta el diferimiento de AJUSTES-FASE-7.md (bloque 2.2), que habia
 * acotado esto a "Semana", "Ajustes", "Hoy" y el aviso de doble atras.
 *
 * A diferencia de EtiquetasSeccion.kt (indexada por el idioma de la ficha
 * que se esta leyendo), esto indexa por los 6 idiomas soportados como
 * idioma de interfaz.
 *
 * `TABLA_CHROME` es la tabla resuelta del documento, palabra por palabra:
 * ninguna cadena de aca se inventa. `TextosInterfazTest.kt` la recorre
 * entera para detectar un idioma sin traducir en alguna clave -- con 6
 * idiomas x ~30 claves el olvido silencioso es el modo de fallo esperable,
 * y ya paso dos veces en uso.
 *
 * Los textos con `{marca}` son plantillas con marcas con nombre, nunca
 * `String.format` posicional: el orden de los elementos cambia entre
 * idiomas (en aleman el nivel va antes del verbo final). `interpolar()`
 * reemplaza por nombre.
 */
enum class ClaveTexto {
    SEMANA, HOY, AJUSTES, VOLVER,
    SIN_CONTENIDO_TITULO, SIN_CONTENIDO_NIVEL, SIN_CONTENIDO_SEMANA, SIN_CALENDARIO,
    SALIR_CONFIRMAR,
    IDIOMAS_APRENDIDOS, IDIOMA_APP, SEGUN_SISTEMA, ESTILO_VISUAL, MODO, MODO_CLARO, MODO_OSCURO,
    NIVEL, CAMBIAR_NIVEL, IDIOMA_NO_DISPONIBLE, AVISO_GLOSAS,
    GUARDADOS, GUARDADOS_VACIO, FILTRO_IDIOMA, FILTRO_TIPO, FILTRO_TODOS,
    TIPO_VOCABULARIO, TIPO_EXPRESION, DESAFIO_FINDE, VER_EN_IDIOMA,
}

private val TABLA_CHROME: Map<ClaveTexto, Map<Idioma, String>> = mapOf(
    ClaveTexto.SEMANA to mapOf(
        Idioma.ES to "Semana", Idioma.EN to "Week", Idioma.DE to "Woche",
        Idioma.FR to "Semaine", Idioma.IT to "Settimana", Idioma.PT to "Semana",
    ),
    ClaveTexto.HOY to mapOf(
        Idioma.ES to "Hoy", Idioma.EN to "Today", Idioma.DE to "Heute",
        Idioma.FR to "Aujourd'hui", Idioma.IT to "Oggi", Idioma.PT to "Hoje",
    ),
    ClaveTexto.AJUSTES to mapOf(
        Idioma.ES to "Ajustes", Idioma.EN to "Settings", Idioma.DE to "Einstellungen",
        Idioma.FR to "Paramètres", Idioma.IT to "Impostazioni", Idioma.PT to "Ajustes",
    ),
    ClaveTexto.VOLVER to mapOf(
        Idioma.ES to "Volver", Idioma.EN to "Back", Idioma.DE to "Zurück",
        Idioma.FR to "Retour", Idioma.IT to "Indietro", Idioma.PT to "Voltar",
    ),
    ClaveTexto.SIN_CONTENIDO_TITULO to mapOf(
        Idioma.ES to "Sin contenido esta semana",
        Idioma.EN to "No content this week",
        Idioma.DE to "Diese Woche kein Inhalt",
        Idioma.FR to "Pas de contenu cette semaine",
        Idioma.IT to "Nessun contenuto questa settimana",
        Idioma.PT to "Sem conteúdo esta semana",
    ),
    ClaveTexto.SIN_CONTENIDO_NIVEL to mapOf(
        Idioma.ES to "Todavía no hay contenido para {idioma} en nivel {nivel}.",
        Idioma.EN to "There is no content yet for {idioma} at level {nivel}.",
        Idioma.DE to "Für {idioma} auf Niveau {nivel} gibt es noch keinen Inhalt.",
        Idioma.FR to "Il n'y a pas encore de contenu pour {idioma} au niveau {nivel}.",
        Idioma.IT to "Non c'è ancora contenuto per {idioma} al livello {nivel}.",
        Idioma.PT to "Ainda não há conteúdo para {idioma} no nível {nivel}.",
    ),
    ClaveTexto.SIN_CONTENIDO_SEMANA to mapOf(
        Idioma.ES to "Esta semana queda fuera de la edición {anio}.",
        Idioma.EN to "This week is outside the {anio} edition.",
        Idioma.DE to "Diese Woche liegt außerhalb der Ausgabe {anio}.",
        Idioma.FR to "Cette semaine est en dehors de l'édition {anio}.",
        Idioma.IT to "Questa settimana è fuori dall'edizione {anio}.",
        Idioma.PT to "Esta semana está fora da edição {anio}.",
    ),
    ClaveTexto.SIN_CALENDARIO to mapOf(
        Idioma.ES to "Todavía no hay calendario para {anio}.",
        Idioma.EN to "There is no calendar for {anio} yet.",
        Idioma.DE to "Für {anio} gibt es noch keinen Kalender.",
        Idioma.FR to "Il n'y a pas encore de calendrier pour {anio}.",
        Idioma.IT to "Non c'è ancora un calendario per {anio}.",
        Idioma.PT to "Ainda não há calendário para {anio}.",
    ),
    ClaveTexto.SALIR_CONFIRMAR to mapOf(
        Idioma.ES to "Pulsá otra vez para salir",
        Idioma.EN to "Press again to exit",
        Idioma.DE to "Zum Beenden nochmals drücken",
        Idioma.FR to "Appuyez à nouveau pour quitter",
        Idioma.IT to "Premi di nuovo per uscire",
        Idioma.PT to "Pressione novamente para sair",
    ),
    ClaveTexto.IDIOMAS_APRENDIDOS to mapOf(
        Idioma.ES to "Idioma que aprendés",
        Idioma.EN to "Language you're learning",
        Idioma.DE to "Sprache, die du lernst",
        Idioma.FR to "Langue que vous apprenez",
        Idioma.IT to "Lingua che stai imparando",
        Idioma.PT to "Idioma que está a aprender",
    ),
    ClaveTexto.IDIOMA_APP to mapOf(
        Idioma.ES to "Idioma de la aplicación",
        Idioma.EN to "App language",
        Idioma.DE to "Sprache der App",
        Idioma.FR to "Langue de l'application",
        Idioma.IT to "Lingua dell'applicazione",
        Idioma.PT to "Idioma da aplicação",
    ),
    // Una sola palabra por idioma: "Según el sistema" y sus equivalentes no
    // entraban en el ancho de pantalla en varios de los 6 idiomas y obligaban
    // a deslizar para leer la opcion completa.
    ClaveTexto.SEGUN_SISTEMA to mapOf(
        Idioma.ES to "Sistema",
        Idioma.EN to "System",
        Idioma.DE to "System",
        Idioma.FR to "Système",
        Idioma.IT to "Sistema",
        Idioma.PT to "Sistema",
    ),
    ClaveTexto.ESTILO_VISUAL to mapOf(
        Idioma.ES to "Estilo visual", Idioma.EN to "Visual style", Idioma.DE to "Erscheinungsbild",
        Idioma.FR to "Style visuel", Idioma.IT to "Stile visivo", Idioma.PT to "Estilo visual",
    ),
    ClaveTexto.MODO to mapOf(
        Idioma.ES to "Modo", Idioma.EN to "Mode", Idioma.DE to "Modus",
        Idioma.FR to "Mode", Idioma.IT to "Modalità", Idioma.PT to "Modo",
    ),
    ClaveTexto.MODO_CLARO to mapOf(
        Idioma.ES to "Claro", Idioma.EN to "Light", Idioma.DE to "Hell",
        Idioma.FR to "Clair", Idioma.IT to "Chiaro", Idioma.PT to "Claro",
    ),
    ClaveTexto.MODO_OSCURO to mapOf(
        Idioma.ES to "Oscuro", Idioma.EN to "Dark", Idioma.DE to "Dunkel",
        Idioma.FR to "Sombre", Idioma.IT to "Scuro", Idioma.PT to "Escuro",
    ),
    ClaveTexto.NIVEL to mapOf(
        Idioma.ES to "Nivel", Idioma.EN to "Level", Idioma.DE to "Niveau",
        Idioma.FR to "Niveau", Idioma.IT to "Livello", Idioma.PT to "Nível",
    ),
    ClaveTexto.CAMBIAR_NIVEL to mapOf(
        Idioma.ES to "Cambiar nivel", Idioma.EN to "Change level", Idioma.DE to "Niveau ändern",
        Idioma.FR to "Changer de niveau", Idioma.IT to "Cambia livello", Idioma.PT to "Mudar de nível",
    ),
    ClaveTexto.IDIOMA_NO_DISPONIBLE to mapOf(
        Idioma.ES to "{idioma} todavía no está disponible",
        Idioma.EN to "{idioma} isn't available yet",
        Idioma.DE to "{idioma} ist noch nicht verfügbar",
        Idioma.FR to "{idioma} n'est pas encore disponible",
        Idioma.IT to "{idioma} non è ancora disponibile",
        Idioma.PT to "{idioma} ainda não está disponível",
    ),
    ClaveTexto.AVISO_GLOSAS to mapOf(
        Idioma.ES to "Las traducciones de vocabulario todavía solo existen en español.",
        Idioma.EN to "Vocabulary translations only exist in Spanish so far.",
        Idioma.DE to "Die Wortschatzübersetzungen liegen bisher nur auf Spanisch vor.",
        Idioma.FR to "Les traductions du vocabulaire n'existent pour l'instant qu'en espagnol.",
        Idioma.IT to "Le traduzioni del lessico esistono per ora solo in spagnolo.",
        Idioma.PT to "As traduções de vocabulário só existem em espanhol por agora.",
    ),
    // --- Guardados (feature 3) ---
    ClaveTexto.GUARDADOS to mapOf(
        Idioma.ES to "Guardados", Idioma.EN to "Saved", Idioma.DE to "Gespeichert",
        Idioma.FR to "Enregistrés", Idioma.IT to "Salvati", Idioma.PT to "Guardados",
    ),
    ClaveTexto.GUARDADOS_VACIO to mapOf(
        Idioma.ES to "Todavía no guardaste nada. Tocá la estrella junto a una palabra o expresión para guardarla.",
        Idioma.EN to "You haven't saved anything yet. Tap the star next to a word or expression to save it.",
        Idioma.DE to "Du hast noch nichts gespeichert. Tippe auf den Stern neben einem Wort oder Ausdruck, um ihn zu speichern.",
        Idioma.FR to "Vous n'avez encore rien enregistré. Appuyez sur l'étoile à côté d'un mot ou d'une expression pour l'enregistrer.",
        Idioma.IT to "Non hai ancora salvato nulla. Tocca la stella accanto a una parola o espressione per salvarla.",
        Idioma.PT to "Ainda não guardou nada. Toque na estrela junto a uma palavra ou expressão para a guardar.",
    ),
    ClaveTexto.FILTRO_IDIOMA to mapOf(
        Idioma.ES to "Idioma", Idioma.EN to "Language", Idioma.DE to "Sprache",
        Idioma.FR to "Langue", Idioma.IT to "Lingua", Idioma.PT to "Idioma",
    ),
    ClaveTexto.FILTRO_TIPO to mapOf(
        Idioma.ES to "Tipo", Idioma.EN to "Type", Idioma.DE to "Art",
        Idioma.FR to "Type", Idioma.IT to "Tipo", Idioma.PT to "Tipo",
    ),
    ClaveTexto.FILTRO_TODOS to mapOf(
        Idioma.ES to "Todos", Idioma.EN to "All", Idioma.DE to "Alle",
        Idioma.FR to "Tous", Idioma.IT to "Tutti", Idioma.PT to "Todos",
    ),
    ClaveTexto.TIPO_VOCABULARIO to mapOf(
        Idioma.ES to "Vocabulario", Idioma.EN to "Vocabulary", Idioma.DE to "Wortschatz",
        Idioma.FR to "Vocabulaire", Idioma.IT to "Lessico", Idioma.PT to "Vocabulário",
    ),
    ClaveTexto.TIPO_EXPRESION to mapOf(
        Idioma.ES to "Expresiones", Idioma.EN to "Expressions", Idioma.DE to "Redemittel",
        Idioma.FR to "Expressions", Idioma.IT to "Espressioni", Idioma.PT to "Expressões",
    ),
    // --- Desafio de fin de semana (feature 2) ---
    ClaveTexto.DESAFIO_FINDE to mapOf(
        Idioma.ES to "Desafío de fin de semana", Idioma.EN to "Weekend challenge", Idioma.DE to "Wochenend-Herausforderung",
        Idioma.FR to "Défi du week-end", Idioma.IT to "Sfida del weekend", Idioma.PT to "Desafio de fim de semana",
    ),
    // --- Switch de traduccion en vivo (feature 1) ---
    ClaveTexto.VER_EN_IDIOMA to mapOf(
        Idioma.ES to "Ver en {idioma}",
        Idioma.EN to "View in {idioma}",
        Idioma.DE to "Auf {idioma} anzeigen",
        Idioma.FR to "Voir en {idioma}",
        Idioma.IT to "Vedi in {idioma}",
        Idioma.PT to "Ver em {idioma}",
    ),
)

/**
 * Nombres de idioma para interpolar en las plantillas ({idioma}), no para
 * mostrar solos: los chips de idioma siguen con el codigo corto (DE, EN),
 * igual que los de nivel (B2, C1) -- eso no es parte de esta tabla.
 */
private val TABLA_NOMBRES_IDIOMA: Map<Idioma, Map<Idioma, String>> = mapOf(
    Idioma.EN to mapOf(
        Idioma.ES to "Inglés", Idioma.EN to "English", Idioma.DE to "Englisch",
        Idioma.FR to "Anglais", Idioma.IT to "Inglese", Idioma.PT to "Inglês",
    ),
    Idioma.DE to mapOf(
        Idioma.ES to "Alemán", Idioma.EN to "German", Idioma.DE to "Deutsch",
        Idioma.FR to "Allemand", Idioma.IT to "Tedesco", Idioma.PT to "Alemão",
    ),
    Idioma.ES to mapOf(
        Idioma.ES to "Español", Idioma.EN to "Spanish", Idioma.DE to "Spanisch",
        Idioma.FR to "Espagnol", Idioma.IT to "Spagnolo", Idioma.PT to "Espanhol",
    ),
    Idioma.FR to mapOf(
        Idioma.ES to "Francés", Idioma.EN to "French", Idioma.DE to "Französisch",
        Idioma.FR to "Français", Idioma.IT to "Francese", Idioma.PT to "Francês",
    ),
    Idioma.IT to mapOf(
        Idioma.ES to "Italiano", Idioma.EN to "Italian", Idioma.DE to "Italienisch",
        // "Italiano" en la columna fr es lo que dice la tabla resuelta de
        // AJUSTES-FASE-9.md -- probable error de transcripcion en el
        // documento (se esperaria "Italien"), pero la instruccion fue no
        // inventar ninguna cadena. Reportado, no corregido en silencio.
        Idioma.FR to "Italiano", Idioma.IT to "Italiano", Idioma.PT to "Italiano",
    ),
    Idioma.PT to mapOf(
        Idioma.ES to "Portugués", Idioma.EN to "Portuguese", Idioma.DE to "Portugiesisch",
        Idioma.FR to "Portugais", Idioma.IT to "Portoghese", Idioma.PT to "Português",
    ),
)

/**
 * Visibilidad de modulo solo para TextosInterfazTest.kt: el test de
 * cobertura recorre la tabla cruda, sin pasar por el fallback a español de
 * texto()/nombreIdioma() -- ese fallback es justo lo que le permitiria a
 * una clave faltante pasar desapercibida en un test que solo mirara la
 * salida de las funciones publicas.
 */
internal fun tablaChromeCruda(): Map<ClaveTexto, Map<Idioma, String>> = TABLA_CHROME
internal fun tablaNombresIdiomaCruda(): Map<Idioma, Map<Idioma, String>> = TABLA_NOMBRES_IDIOMA

private fun texto(clave: ClaveTexto, idiomaInterfaz: Idioma): String {
    val porIdioma = TABLA_CHROME.getValue(clave)
    return porIdioma[idiomaInterfaz] ?: porIdioma.getValue(Idioma.ES)
}

private fun interpolar(plantilla: String, vararg pares: Pair<String, String>): String =
    pares.fold(plantilla) { texto, (clave, valor) -> texto.replace("{$clave}", valor) }

/** Nombre de [idioma] en el idioma de interfaz [idiomaInterfaz], para interpolar en una plantilla. */
fun nombreIdioma(idioma: Idioma, idiomaInterfaz: Idioma): String {
    val porIdioma = TABLA_NOMBRES_IDIOMA.getValue(idioma)
    return porIdioma[idiomaInterfaz] ?: porIdioma.getValue(Idioma.ES)
}

fun etiquetaSemana(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SEMANA, idiomaInterfaz)
fun etiquetaHoy(idiomaInterfaz: Idioma): String = texto(ClaveTexto.HOY, idiomaInterfaz)
fun etiquetaAjustes(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AJUSTES, idiomaInterfaz)
fun etiquetaVolver(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VOLVER, idiomaInterfaz)

fun mensajeSinContenidoTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_CONTENIDO_TITULO, idiomaInterfaz)

fun mensajeSinContenidoNivel(idiomaInterfaz: Idioma, idiomaAprendido: Idioma, nivel: Nivel): String = interpolar(
    texto(ClaveTexto.SIN_CONTENIDO_NIVEL, idiomaInterfaz),
    "idioma" to nombreIdioma(idiomaAprendido, idiomaInterfaz),
    "nivel" to nivel.name,
)

fun mensajeSinContenidoSemana(idiomaInterfaz: Idioma, anio: Int): String =
    interpolar(texto(ClaveTexto.SIN_CONTENIDO_SEMANA, idiomaInterfaz), "anio" to anio.toString())

fun mensajeSinCalendario(idiomaInterfaz: Idioma, anio: Int): String =
    interpolar(texto(ClaveTexto.SIN_CALENDARIO, idiomaInterfaz), "anio" to anio.toString())

/** AJUSTES-FASE-7.md, bloque 2.4: "del mismo tipo que el del idioma no disponible" -- un toast, no una ventana. */
fun mensajeDobleAtrasParaSalir(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SALIR_CONFIRMAR, idiomaInterfaz)

fun etiquetaIdiomasAprendidos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.IDIOMAS_APRENDIDOS, idiomaInterfaz)
fun etiquetaIdiomaApp(idiomaInterfaz: Idioma): String = texto(ClaveTexto.IDIOMA_APP, idiomaInterfaz)
fun etiquetaSegunSistema(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SEGUN_SISTEMA, idiomaInterfaz)
fun etiquetaEstiloVisual(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ESTILO_VISUAL, idiomaInterfaz)
fun etiquetaModo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO, idiomaInterfaz)
fun etiquetaModoClaro(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO_CLARO, idiomaInterfaz)
fun etiquetaModoOscuro(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO_OSCURO, idiomaInterfaz)
fun etiquetaNivel(idiomaInterfaz: Idioma): String = texto(ClaveTexto.NIVEL, idiomaInterfaz)
fun etiquetaCambiarNivel(idiomaInterfaz: Idioma): String = texto(ClaveTexto.CAMBIAR_NIVEL, idiomaInterfaz)

/** [idiomaSinContenido] es el idioma que todavia no tiene contenido embebido, no el de interfaz. */
fun mensajeIdiomaNoDisponible(idiomaInterfaz: Idioma, idiomaSinContenido: Idioma): String = interpolar(
    texto(ClaveTexto.IDIOMA_NO_DISPONIBLE, idiomaInterfaz),
    "idioma" to nombreIdioma(idiomaSinContenido, idiomaInterfaz),
)

fun avisoGlosasTexto(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AVISO_GLOSAS, idiomaInterfaz)

fun etiquetaGuardados(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS, idiomaInterfaz)
fun mensajeGuardadosVacio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_VACIO, idiomaInterfaz)
fun etiquetaFiltroIdioma(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_IDIOMA, idiomaInterfaz)
fun etiquetaFiltroTipo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_TIPO, idiomaInterfaz)
fun etiquetaFiltroTodos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_TODOS, idiomaInterfaz)
fun etiquetaTipoVocabulario(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TIPO_VOCABULARIO, idiomaInterfaz)
fun etiquetaTipoExpresion(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TIPO_EXPRESION, idiomaInterfaz)
fun etiquetaDesafioFinde(idiomaInterfaz: Idioma): String = texto(ClaveTexto.DESAFIO_FINDE, idiomaInterfaz)

/** [idiomaMostrado] es el idioma que el switch de traduccion en vivo esta mostrando ahora, no el de interfaz. */
fun etiquetaVerEnIdioma(idiomaInterfaz: Idioma, idiomaMostrado: Idioma): String = interpolar(
    texto(ClaveTexto.VER_EN_IDIOMA, idiomaInterfaz),
    "idioma" to nombreIdioma(idiomaMostrado, idiomaInterfaz),
)
