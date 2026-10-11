package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Textos de la vista Verbos que no pueden mentir (Ronda I, tarea 2): el nivel no se puede cambiar ahi, y
 * la lista ya no se declara incompleta desde que los nucleos de las colocaciones entran (Ronda J).
 */
class TextosVerbosTest {

    private val palabrasDeNivel = Regex("""nivel|level|niveau|livello|nível|niveaus""", RegexOption.IGNORE_CASE)

    @Test
    fun `el estado vacio no manda a cambiar de nivel ni de idioma en los 6 idiomas`() {
        for (idioma in Idioma.entries) {
            val t = mensajeVerbosVacio(idioma)
            assertTrue("$idioma vacio", t.isNotBlank())
            assertFalse("$idioma: el nivel no se puede cambiar en esa vista: $t", palabrasDeNivel.containsMatchIn(t))
            assertFalse("$idioma: no hay otra cosa que hacer ahi: $t", Regex("""otro idioma|another language|andere Sprache|autre langue|altra lingua|outro idioma""", RegexOption.IGNORE_CASE).containsMatchIn(t))
        }
        assertEquals("Todavía no hay verbos etiquetados para este idioma.", mensajeVerbosVacio(Idioma.ES))
    }

    @Test
    fun `el sin resultados habla de esta lista, no de todos los verbos del idioma`() {
        for (idioma in Idioma.entries) {
            val t = mensajeVerbosSinResultados(idioma)
            assertTrue(t.isNotBlank())
            assertFalse("$idioma: $t", palabrasDeNivel.containsMatchIn(t))
        }
        assertTrue(mensajeVerbosSinResultados(Idioma.ES).contains("de esta lista"))
        assertTrue(mensajeVerbosSinResultados(Idioma.DE).contains("dieser Liste"))
        assertTrue(mensajeVerbosSinResultados(Idioma.EN).contains("this list"))
    }

    @Test
    fun `la guia de testers ya no dice que los verbos son del nivel que se esta viendo`() {
        val guia = listOf(File("../proyecto/docs/guia-testers.md"), File("proyecto/docs/guia-testers.md")).first { it.isFile }.readText()
        val fila = guia.lines().first { it.startsWith("| Verbos en la Biblioteca") }
        assertFalse(fila.contains("del nivel que estás viendo"))
        assertTrue(fila.contains("todos los niveles"))
        assertFalse("con los nucleos verbales la lista ya no se declara incompleta", fila.contains("exhaustiva"))
        assertTrue("cuenta que debajo de un verbo salen las frases donde se usa", fila.contains("frases"))
        assertTrue("y que la busqueda mira las frases", fila.contains("«Frage» encuentra stellen"))
    }
}
