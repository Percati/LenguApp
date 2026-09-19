package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * AJUSTES-FASE-9.md, bloque B: traduccion completa del *chrome*, se levanta
 * el diferimiento de AJUSTES-FASE-7.md (bloque 2.2) que habia acotado esto
 * a cuatro cadenas. Con 20 claves x 6 idiomas mas 6 nombres de idioma x 6
 * idiomas, el olvido silencioso de una celda es el modo de fallo esperable
 * -- ya paso dos veces en uso -- asi que el primer test recorre la tabla
 * entera en vez de puntualear casos sueltos. Los que siguen son muestras
 * puntuales contra el texto exacto del documento, para atrapar un error de
 * transcripcion en una celda que si esta presente pero dice otra cosa (la
 * tabla entera no detectaria eso, solo la ausencia).
 */
class TextosInterfazTest {

    private val seis = Idioma.entries

    @Test
    fun `cada clave del chrome tiene traduccion no vacia en los seis idiomas`() {
        val tabla = tablaChromeCruda()
        assertEquals("faltan claves enteras en la tabla", ClaveTexto.entries.toSet(), tabla.keys)
        tabla.forEach { (clave, porIdioma) ->
            seis.forEach { idioma ->
                val valor = porIdioma[idioma]
                assertTrue("$clave sin traduccion en $idioma", !valor.isNullOrBlank())
            }
        }
    }

    @Test
    fun `cada nombre de idioma tiene traduccion no vacia en los seis idiomas`() {
        val tabla = tablaNombresIdiomaCruda()
        assertEquals("faltan idiomas enteros en la tabla de nombres", seis.toSet(), tabla.keys)
        tabla.forEach { (idiomaNombrado, porIdioma) ->
            seis.forEach { idiomaInterfaz ->
                val valor = porIdioma[idiomaInterfaz]
                assertTrue("nombre de $idiomaNombrado sin traduccion en $idiomaInterfaz", !valor.isNullOrBlank())
            }
        }
    }

    // --- Muestras puntuales contra el documento ---

    @Test
    fun `etiquetaSemana coincide con las seis columnas del documento`() {
        assertEquals("Semana", etiquetaSemana(Idioma.ES))
        assertEquals("Week", etiquetaSemana(Idioma.EN))
        assertEquals("Woche", etiquetaSemana(Idioma.DE))
        assertEquals("Semaine", etiquetaSemana(Idioma.FR))
        assertEquals("Settimana", etiquetaSemana(Idioma.IT))
        assertEquals("Semana", etiquetaSemana(Idioma.PT))
    }

    @Test
    fun `etiquetaHoy coincide con las seis columnas del documento`() {
        assertEquals("Hoy", etiquetaHoy(Idioma.ES))
        assertEquals("Today", etiquetaHoy(Idioma.EN))
        assertEquals("Heute", etiquetaHoy(Idioma.DE))
        assertEquals("Aujourd'hui", etiquetaHoy(Idioma.FR))
        assertEquals("Oggi", etiquetaHoy(Idioma.IT))
        assertEquals("Hoje", etiquetaHoy(Idioma.PT))
    }

    @Test
    fun `etiquetaAjustes coincide con las seis columnas del documento -- PT es Ajustes, no Configuracoes`() {
        assertEquals("Ajustes", etiquetaAjustes(Idioma.ES))
        assertEquals("Settings", etiquetaAjustes(Idioma.EN))
        assertEquals("Einstellungen", etiquetaAjustes(Idioma.DE))
        assertEquals("Paramètres", etiquetaAjustes(Idioma.FR))
        assertEquals("Impostazioni", etiquetaAjustes(Idioma.IT))
        assertEquals("Ajustes", etiquetaAjustes(Idioma.PT))
    }

    @Test
    fun `etiquetaVolver coincide con las seis columnas del documento`() {
        assertEquals("Volver", etiquetaVolver(Idioma.ES))
        assertEquals("Back", etiquetaVolver(Idioma.EN))
        assertEquals("Zurück", etiquetaVolver(Idioma.DE))
        assertEquals("Retour", etiquetaVolver(Idioma.FR))
        assertEquals("Indietro", etiquetaVolver(Idioma.IT))
        assertEquals("Voltar", etiquetaVolver(Idioma.PT))
    }

    @Test
    fun `mensajeDobleAtrasParaSalir tiene el texto nuevo del documento, no el improvisado de la Fase 7`() {
        assertEquals("Pulsá otra vez para salir", mensajeDobleAtrasParaSalir(Idioma.ES))
        assertEquals("Press again to exit", mensajeDobleAtrasParaSalir(Idioma.EN))
        assertEquals("Zum Beenden nochmals drücken", mensajeDobleAtrasParaSalir(Idioma.DE))
        assertEquals("Appuyez à nouveau pour quitter", mensajeDobleAtrasParaSalir(Idioma.FR))
        assertEquals("Premi di nuovo per uscire", mensajeDobleAtrasParaSalir(Idioma.IT))
        assertEquals("Pressione novamente para sair", mensajeDobleAtrasParaSalir(Idioma.PT))
    }

    @Test
    fun `etiquetas de Ajustes coinciden con el documento`() {
        assertEquals("Idioma que aprendés", etiquetaIdiomasAprendidos(Idioma.ES))
        assertEquals("Language you're learning", etiquetaIdiomasAprendidos(Idioma.EN))
        assertEquals("Sprache der App", etiquetaIdiomaApp(Idioma.DE))
        assertEquals("Selon le système", etiquetaSegunSistema(Idioma.FR))
        assertEquals("Stile visivo", etiquetaEstiloVisual(Idioma.IT))
        assertEquals("Modo", etiquetaModo(Idioma.PT))
        assertEquals("Hell", etiquetaModoClaro(Idioma.DE))
        assertEquals("Sombre", etiquetaModoOscuro(Idioma.FR))
        assertEquals("Livello", etiquetaNivel(Idioma.IT))
        assertEquals("Mudar de nível", etiquetaCambiarNivel(Idioma.PT))
    }

    // --- Plantillas: marcas con nombre, nunca String.format posicional ---
    // (el orden de los elementos cambia entre idiomas -- en aleman el nivel
    // va antes del verbo final)

    @Test
    fun `mensajeSinContenidoNivel interpola idioma y nivel por nombre`() {
        assertEquals(
            "Todavía no hay contenido para Alemán en nivel B2.",
            mensajeSinContenidoNivel(Idioma.ES, Idioma.DE, Nivel.B2),
        )
        assertEquals(
            "Für Englisch auf Niveau C1 gibt es noch keinen Inhalt.",
            mensajeSinContenidoNivel(Idioma.DE, Idioma.EN, Nivel.C1),
        )
    }

    @Test
    fun `mensajeSinContenidoSemana interpola el anio`() {
        assertEquals("Esta semana queda fuera de la edición 2026.", mensajeSinContenidoSemana(Idioma.ES, 2026))
        assertEquals("This week is outside the 2027 edition.", mensajeSinContenidoSemana(Idioma.EN, 2027))
    }

    @Test
    fun `mensajeSinCalendario interpola el anio`() {
        assertEquals("Todavía no hay calendario para 2027.", mensajeSinCalendario(Idioma.ES, 2027))
        assertEquals("Non c'è ancora un calendario per 2027.", mensajeSinCalendario(Idioma.IT, 2027))
    }

    @Test
    fun `mensajeIdiomaNoDisponible interpola el nombre del idioma sin contenido, no el de interfaz`() {
        assertEquals("Francés todavía no está disponible", mensajeIdiomaNoDisponible(Idioma.ES, Idioma.FR))
        assertEquals("German isn't available yet", mensajeIdiomaNoDisponible(Idioma.EN, Idioma.DE))
    }

    @Test
    fun `nombreIdioma da el nombre del idioma nombrado, no del idioma de interfaz`() {
        assertEquals("Alemán", nombreIdioma(Idioma.DE, Idioma.ES))
        assertEquals("German", nombreIdioma(Idioma.DE, Idioma.EN))
        assertEquals("Deutsch", nombreIdioma(Idioma.DE, Idioma.DE))
    }
}
