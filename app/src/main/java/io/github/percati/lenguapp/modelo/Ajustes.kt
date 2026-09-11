package io.github.percati.lenguapp.modelo

/**
 * Preferencias del usuario, no progreso: cambiar de idioma o nivel no mueve
 * ningun puntero de semana, la semana la sigue mandando la fecha (modo
 * revista).
 *
 * `idiomasAprendidos` reemplaza el par singular `idiomaAprendido` + `nivel`
 * de las Fases 3-5 (ver AJUSTES-FASE-6.md, bloque B): varios idiomas en
 * paralelo, cada uno con su propio nivel. Una pestaña por entrada en la
 * pantalla principal.
 *
 * `idiomaBase` (baseLanguage) e `idiomaInterfaz` (uiLanguage) son dos campos
 * distintos en los datos aunque la pantalla de ajustes los presente como un
 * solo "Idioma de la aplicación" -- CLAUDE.md, regla dura #6: el dia que
 * alguien quiera interfaz en un idioma y glosas en otro, se desdobla el
 * ajuste en pantalla sin migrar datos, porque el modelo ya los tenia
 * separados. `idiomaSegunSistema`: si esta activo, el idioma efectivo de la
 * aplicacion se resuelve contra el idioma del sistema en tiempo de uso (ver
 * presentacion/IdiomaAplicacion.kt), no se persiste un valor recalculado
 * encima de `idiomaBase`/`idiomaInterfaz` -- esos dos siguen siendo el
 * ultimo valor elegido a mano, y quedan como respaldo si el idioma del
 * sistema no es ninguno de los soportados.
 */
data class Ajustes(
    val idiomasAprendidos: Map<Idioma, Nivel> = mapOf(Idioma.DE to Nivel.B2),
    val idiomaBase: Idioma = Idioma.ES,
    val idiomaInterfaz: Idioma = Idioma.ES,
    val idiomaSegunSistema: Boolean = false,
)
