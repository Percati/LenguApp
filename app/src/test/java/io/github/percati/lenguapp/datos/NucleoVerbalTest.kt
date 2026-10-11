package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Ficha
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * `nucleoVerbal`, `claseNucleo` y `reccionNucleo` (Ronda J, tarea 0): el parser estricto lee los campos
 * nuevos de `vocabulario[]` en fichas reales, y un item sin ellos sigue cargando con los valores por defecto.
 */
class NucleoVerbalTest {

    private fun carpeta(): File =
        listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }

    private val fichas: List<Ficha> by lazy {
        carpeta().listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
    }

    @Test
    fun `una ficha alemana real con una colocacion lee su nucleo y su clase`() {
        val ficha = parsearContenido(File(carpeta(), "DE-F01-A2-2027-3.json").readText()) as Ficha
        val colocacion = ficha.vocabulario.first { it.item == "die Frage stellen" }
        assertEquals("stellen", colocacion.nucleoVerbal)
        assertEquals(listOf("simple"), colocacion.claseNucleo)
        assertTrue("una colocacion no es un verbo propio", colocacion.claseVerbo.isEmpty())
    }

    @Test
    fun `la reccion del nucleo se lee cuando existe y es null cuando no`() {
        val todos = fichas.flatMap { it.vocabulario }.filter { it.nucleoVerbal != null }
        assertTrue(todos.any { it.reccionNucleo != null })
        assertTrue(todos.any { it.reccionNucleo == null })
    }

    @Test
    fun `un item sin nucleo carga con los valores por defecto`() {
        val sin = fichas.flatMap { it.vocabulario }.first { it.nucleoVerbal == null }
        assertTrue(sin.claseNucleo.isEmpty())
        assertEquals(null, sin.reccionNucleo)
    }

    @Test
    fun `nunca hay un item con claseVerbo y nucleoVerbal a la vez, y todo nucleo trae su clase`() {
        val items = fichas.flatMap { it.vocabulario }
        assertTrue(items.none { it.nucleoVerbal != null && it.claseVerbo.isNotEmpty() })
        assertTrue(items.filter { it.nucleoVerbal != null }.all { it.claseNucleo.isNotEmpty() })
        assertTrue(items.filter { it.nucleoVerbal == null }.all { it.claseNucleo.isEmpty() && it.reccionNucleo == null })
    }

    @Test
    fun `los assets reales traen las 1104 colocaciones con nucleo por aparicion`() {
        // 441 apariciones en alemán + 663 en inglés (FALTANTES 38)
        val apariciones = fichas.flatMap { f -> f.vocabulario.filter { it.nucleoVerbal != null }.map { f.idioma } }
        assertEquals(1104, apariciones.size)
    }
}
