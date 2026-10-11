package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Busqueda de la vista Verbos (Ronda H, tarea 2; con nucleos y colocaciones desde la J), sobre los assets reales. */
class BusquedaVerbosTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val items by lazy {
        construirBiblioteca(
            File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!
                .map { parsearContenido(it.readText()) as ContenidoSemanal }.filterIsInstance<Ficha>(),
        )
    }

    private fun todosLosVerbosDe(idioma: Idioma) = items.entradasVerbo(idioma)

    @Test
    fun `la normalizacion ignora mayusculas y diacriticos`() {
        assertEquals("ubernehmen", normalizarBusqueda("übernehmen"))
        assertEquals("ubernehmen", normalizarBusqueda("ÜBERNEHMEN"))
        assertEquals("strasse", normalizarBusqueda("Straße"))
        assertEquals("cafe nino", normalizarBusqueda("  Café NIÑO "))
    }

    @Test
    fun `buscar uber o UBER encuentra los verbos con u dieresis, sin acentos ni mayusculas`() {
        val todos = todosLosVerbosDe(Idioma.DE)
        val conDieresis = todos.map { it.infinitivo }.filter { it.contains("über") }.toSet()
        assertTrue("no hay verbos con über en los datos", conDieresis.isNotEmpty())
        for (q in listOf("uber", "UBER", "über", "Über")) {
            val r = todos.filtrarPorBusquedaVerbos(q, Idioma.ES).map { it.infinitivo }.toSet()
            assertTrue("$q no encontro $conDieresis (encontro $r)", r.containsAll(conDieresis))
        }
    }

    @Test
    fun `buscar machen encuentra la familia por subcadena`() {
        val todos = todosLosVerbosDe(Idioma.DE)
        val esperados = todos.map { it.infinitivo }.filter { it.lowercase().contains("machen") }.toSet()
        assertTrue("necesito al menos 2 verbos con machen en los datos: $esperados", esperados.size >= 2)
        assertTrue(todos.filtrarPorBusquedaVerbos("machen", Idioma.ES).map { it.infinitivo }.toSet().containsAll(esperados))
        assertEquals(todos.filtrarPorBusquedaVerbos("machen", Idioma.ES), todos.filtrarPorBusquedaVerbos("MACHEN", Idioma.ES))
    }

    @Test
    fun `tambien busca en la traduccion al idioma de app, el de app y no otro`() {
        val verbos = todosLosVerbosDe(Idioma.DE)
        val v = items.verbosDe(Idioma.DE).first { (it.datosFila(Idioma.ES).traduccion?.length ?: 0) >= 6 }
        val fragmento = v.datosFila(Idioma.ES).traduccion!!.substring(1, 5).uppercase()
        assertTrue("`$fragmento` (traduccion de ${v.texto}) no lo encontro", verbos.filtrarPorBusquedaVerbos(fragmento, Idioma.ES).any { it.infinitivo == v.texto })
        val ingles = items.verbosDe(Idioma.DE).first { it.datosFila(Idioma.EN).traduccion != null }
        val f = ingles.datosFila(Idioma.EN).traduccion!!.take(4)
        assertTrue(verbos.filtrarPorBusquedaVerbos(f, Idioma.EN).any { it.infinitivo == ingles.texto })
    }

    @Test
    fun `una consulta vacia o en blanco no filtra, y una sin coincidencias da vacio`() {
        val verbos = items.entradasVerbo(Idioma.DE)
        assertEquals(verbos, verbos.filtrarPorBusquedaVerbos("", Idioma.ES))
        assertEquals(verbos, verbos.filtrarPorBusquedaVerbos("   ", Idioma.ES))
        assertEquals(emptyList<EntradaVerbo>(), verbos.filtrarPorBusquedaVerbos("qqqqzzzz", Idioma.ES))
    }

    @Test
    fun `la busqueda se aplica sobre los verbos ya filtrados por clase`() {
        val soloTrennbar = items.entradasVerbo(Idioma.DE).filtrarPorClases(setOf("trennbar"))
        val r = soloTrennbar.filtrarPorBusquedaVerbos("a", Idioma.ES)
        assertTrue(r.isNotEmpty() && r.all { "trennbar" in it.clases })
    }

    @Test
    fun `el buscador precalculado da lo mismo que filtrar en cada tecla, y cubre el conjunto entero`() {
        val verbos = todosLosVerbosDe(Idioma.DE)
        val buscador = BuscadorVerbos(verbos, Idioma.ES)
        for (q in listOf("stellen", "uber", "MACHEN", "a", "qqqq", "")) {
            assertEquals(q, verbos.filtrarPorBusquedaVerbos(q, Idioma.ES).map { it.infinitivo }, buscador.filtrar(verbos, q).map { it.infinitivo })
        }
        assertEquals("DE: 305 propios + 197 nucleos - 32 fusionados", 470, verbos.size)
    }
}
