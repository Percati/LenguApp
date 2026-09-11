package io.github.percati.lenguapp.modelo

/**
 * Preferencias del usuario, no progreso: cambiar de idioma o nivel no mueve
 * ningun puntero de semana, la semana la sigue mandando la fecha (modo
 * revista). uiLanguage (idiomaInterfaz) y baseLanguage (idiomaBase) son
 * ajustes distintos a proposito -- CLAUDE.md, regla dura #6 -- aunque hoy
 * los dos arranquen en "es".
 */
data class Ajustes(
    val idiomaAprendido: Idioma = Idioma.DE,
    val nivel: Nivel = Nivel.B2,
    val idiomaBase: Idioma = Idioma.ES,
    val idiomaInterfaz: Idioma = Idioma.ES,
    val variantesDesactivadas: Set<String> = emptySet(),
)
