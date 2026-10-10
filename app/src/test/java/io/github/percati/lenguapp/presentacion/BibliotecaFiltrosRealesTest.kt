package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearCategoriasUso
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Biblioteca (Ronda E, tarea 3 / REGLAS-PREVENCION P3), sobre los assets REALES: los chips de
 * topic y de categoria salen de los items del idioma de la pestana, y cada chip mostrado tiene
 * al menos un item sumando todos los niveles.
 */
class BibliotecaFiltrosRealesTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val todo: List<ContenidoSemanal> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.map { parsearContenido(it.readText()) }
    }
    private val items by lazy { construirBiblioteca(todo) }
    private val idiomas by lazy { todo.filterIsInstance<Ficha>().map { it.idioma }.toSet() }
    private val categorias by lazy { parsearCategoriasUso(File(assets(), "temas/categorias-uso.json").readText()) }

    @Test
    fun `cada chip de topic y de categoria mostrado tiene al menos un item sumando todos los niveles`() {
        assertTrue(idiomas.size >= 2)
        for (idioma in idiomas) {
            for (t in items.topicsDelIdioma(idioma)) {
                val n = items.filtrar(idioma, TipoGuardado.VOCABULARIO, emptySet(), t, emptySet(), "", Idioma.ES).size
                assertTrue("$idioma: el topic $t no tiene items", n >= 1)
                assertEquals("$idioma: contador del topic $t", n, items.contarTopics(idioma, emptySet())[t])
            }
            for (c in items.categoriasDelIdioma(idioma)) {
                val n = items.filtrar(idioma, TipoGuardado.EXPRESION, emptySet(), null, setOf(c), "", Idioma.ES).size
                assertTrue("$idioma: la categoria $c no tiene items", n >= 1)
                assertEquals("$idioma: contador de la categoria $c", n, items.contarCategorias(idioma, emptySet())[c])
            }
        }
    }

    @Test
    fun `no falta ningun chip, todo topic o categoria con items del idioma se ofrece`() {
        for (idioma in idiomas) {
            val fichas = todo.filterIsInstance<Ficha>().filter { it.idioma == idioma }
            // calculado directo de las fichas, sin pasar por la Biblioteca
            val topicsConVocabulario = fichas.filter { it.vocabulario.isNotEmpty() }.map { it.topicId }.toSet()
            assertEquals("$idioma topics", topicsConVocabulario, items.topicsDelIdioma(idioma))
            val categoriasEnFichas = fichas.flatMap { f -> f.redemittel.flatMap { it.categoriasUso.orEmpty() } }.toSet()
            assertEquals("$idioma categorias", categoriasEnFichas, items.categoriasDelIdioma(idioma) - SIN_CATEGORIA)
        }
    }

    @Test
    fun `las categorias que se ofrecen estan en la lista cerrada y ninguna lista es global`() {
        val cerrada = (categorias.funcionComunicativa + categorias.patronGramatical).toSet()
        for (idioma in idiomas) assertTrue(cerrada.containsAll(items.categoriasDelIdioma(idioma) - SIN_CATEGORIA))
        // la lista cerrada tiene valores que no existen en algun idioma (p.ej. Caso y declinacion en ingles)
        val porIdioma = idiomas.associateWith { items.categoriasDelIdioma(it) - SIN_CATEGORIA }
        assertTrue("alguna categoria de la lista cerrada falta en algun idioma", porIdioma.values.any { it.size < cerrada.size })
        // el conjunto del idioma A no se deriva del idioma B
        assertTrue("todos los idiomas ofrecen las mismas categorias", porIdioma.values.toSet().size > 1)
    }

    @Test
    fun `Caso y declinacion se ofrece en ingles solo si hay una expresion inglesa que la use`() {
        val hayItems = items.any { it.idioma == Idioma.EN && "Caso y declinación" in it.categorias }
        assertEquals(hayItems, "Caso y declinación" in items.categoriasDelIdioma(Idioma.EN))
    }

    @Test
    fun `propiedad, los chips de un idioma salen solo de sus items y no de los de otro`() {
        val base = items.first { it.tipo == TipoGuardado.EXPRESION }
        val a = base.copy(idioma = Idioma.DE, categorias = setOf("X-solo-de"), topics = setOf("T-solo-de"), niveles = setOf(Nivel.B2))
        val b = base.copy(idioma = Idioma.EN, texto = "otra", categorias = setOf("Y-solo-en"), topics = setOf("T-solo-en"), niveles = setOf(Nivel.B2))
        val v = base.copy(tipo = TipoGuardado.VOCABULARIO, idioma = Idioma.DE, texto = "palabra", topics = setOf("T-solo-de"), niveles = setOf(Nivel.B2))
        val lista = listOf(a, b, v)
        assertEquals(setOf("X-solo-de"), lista.categoriasDelIdioma(Idioma.DE))
        assertEquals(setOf("Y-solo-en"), lista.categoriasDelIdioma(Idioma.EN))
        assertEquals(setOf("T-solo-de"), lista.topicsDelIdioma(Idioma.DE))
        assertEquals(emptySet<String>(), lista.topicsDelIdioma(Idioma.EN)) // EN no tiene vocabulario
        assertNotEquals(lista.categoriasDelIdioma(Idioma.DE), lista.categoriasDelIdioma(Idioma.EN))
    }

    @Test
    fun `un chip puede dar 0 en un nivel concreto sin dejar de ofrecerse, y Sin categoria solo si hay expresiones sin categoria`() {
        val base = items.first { it.tipo == TipoGuardado.EXPRESION }
        val soloC1 = base.copy(idioma = Idioma.DE, categorias = setOf("Pedir"), niveles = setOf(Nivel.C1))
        val lista = listOf(soloC1)
        assertTrue("Pedir" in lista.categoriasDelIdioma(Idioma.DE))
        assertEquals(null, lista.contarCategorias(Idioma.DE, setOf(Nivel.A2))["Pedir"]) // 0 en A2: el chip muestra (0)
        assertEquals(1, lista.contarCategorias(Idioma.DE, setOf(Nivel.C1))["Pedir"])
        assertFalse(SIN_CATEGORIA in lista.categoriasDelIdioma(Idioma.DE))
        val conVacia = lista + soloC1.copy(texto = "sin cat", categorias = emptySet())
        assertTrue(SIN_CATEGORIA in conVacia.categoriasDelIdioma(Idioma.DE))
    }
}
