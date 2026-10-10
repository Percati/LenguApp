package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Nombres de topic y de categoria en los 6 idiomas (Ronda E, tarea 1): sobre los assets
 * REALES generados por el build. Si falta un idioma en alguna clave, falla.
 */
class NombresI18nTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val topics = parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText())
    private val categorias = parsearCategoriasUso(File(assets(), "temas/categorias-uso.json").readText())

    @Test
    fun `cada topic tiene nombre en los 6 idiomas`() {
        assertEquals(14, topics.claves.size)
        assertEquals(emptyList<Pair<String, Idioma>>(), topics.faltantes())
    }

    @Test
    fun `cada categoria de la lista cerrada tiene nombre en los 6 idiomas`() {
        val lista = categorias.funcionComunicativa + categorias.patronGramatical
        assertTrue(lista.isNotEmpty())
        assertEquals("categorias sin entrada en nombres", emptyList<String>(), lista.filter { it !in categorias.nombres })
        assertEquals(emptyList<Pair<String, Idioma>>(), categorias.faltantes())
    }

    @Test
    fun `el nombre sale en el idioma pedido y no en espanol`() {
        assertEquals("Work and career", topics.nombre("T01", Idioma.EN))
        assertEquals("Arbeit und Karriere", topics.nombre("T01", Idioma.DE))
        assertEquals("Giving opinions", categorias.nombre("Opinar", Idioma.EN))
    }

    @Test
    fun `fallback a espanol y luego a la propia clave`() {
        val n = NombresI18n(mapOf("X" to mapOf("es" to "Equis")))
        assertEquals("Equis", n.nombre("X", Idioma.DE))
        assertEquals("Y", n.nombre("Y", Idioma.DE))
        assertTrue(n.faltantes().isNotEmpty())
    }
}
