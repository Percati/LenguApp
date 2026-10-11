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

/** Busqueda de la vista Verbos (Ronda H, tarea 2), sobre los assets reales. */
class BusquedaVerbosTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val items by lazy {
        construirBiblioteca(
            File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!
                .map { parsearContenido(it.readText()) as ContenidoSemanal }.filterIsInstance<Ficha>(),
        )
    }

    private fun todosLosVerbosDe(idioma: Idioma) = items.verbosDe(idioma)

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
        val conDieresis = todos.map { it.texto }.filter { it.contains("über") }.toSet()
        assertTrue("no hay verbos con über en los datos", conDieresis.isNotEmpty())
        for (q in listOf("uber", "UBER", "über", "Über")) {
            val r = todos.filtrarPorBusquedaVerbos(q, Idioma.ES).map { it.texto }.toSet()
            assertTrue("$q no encontro $conDieresis (encontro $r)", r.containsAll(conDieresis))
        }
    }

    @Test
    fun `buscar machen encuentra la familia por subcadena`() {
        val todos = todosLosVerbosDe(Idioma.DE)
        val esperados = todos.map { it.texto }.filter { it.lowercase().contains("machen") }.toSet()
        assertTrue("necesito al menos 2 verbos con machen en los datos: $esperados", esperados.size >= 2)
        assertEquals(esperados, todos.filtrarPorBusquedaVerbos("machen", Idioma.ES).map { it.texto }.toSet())
        assertEquals(esperados, todos.filtrarPorBusquedaVerbos("MACHEN", Idioma.ES).map { it.texto }.toSet())
    }

    @Test
    fun `tambien busca en la traduccion al idioma de app, el de app y no otro`() {
        val verbos = todosLosVerbosDe(Idioma.DE)
        val v = verbos.first { (it.datosFila(Idioma.ES).traduccion?.length ?: 0) >= 6 }
        val fragmento = v.datosFila(Idioma.ES).traduccion!!.substring(1, 5).uppercase()
        assertTrue("`$fragmento` (traduccion de ${v.texto}) no lo encontro", verbos.filtrarPorBusquedaVerbos(fragmento, Idioma.ES).any { it.texto == v.texto })
        val ingles = verbos.first { it.datosFila(Idioma.EN).traduccion != null }
        val f = ingles.datosFila(Idioma.EN).traduccion!!.take(4)
        assertTrue(verbos.filtrarPorBusquedaVerbos(f, Idioma.EN).any { it.texto == ingles.texto })
    }

    @Test
    fun `una consulta vacia o en blanco no filtra, y una sin coincidencias da vacio`() {
        val verbos = items.verbosDe(Idioma.DE)
        assertEquals(verbos, verbos.filtrarPorBusquedaVerbos("", Idioma.ES))
        assertEquals(verbos, verbos.filtrarPorBusquedaVerbos("   ", Idioma.ES))
        assertEquals(emptyList<ItemBiblioteca>(), verbos.filtrarPorBusquedaVerbos("qqqqzzzz", Idioma.ES))
    }

    @Test
    fun `la busqueda se aplica sobre los verbos ya filtrados por clase`() {
        val soloTrennbar = items.verbosDe(Idioma.DE).filtrarPorClases(setOf("trennbar"))
        val r = soloTrennbar.filtrarPorBusquedaVerbos("a", Idioma.ES)
        assertTrue(r.isNotEmpty() && r.all { "trennbar" in it.vocabulario!!.claseVerbo })
    }

    @Test
    fun `el buscador precalculado da lo mismo que filtrar en cada tecla, y cubre el conjunto entero`() {
        val verbos = todosLosVerbosDe(Idioma.DE)
        val buscador = BuscadorVerbos(verbos, Idioma.ES)
        for (q in listOf("stellen", "uber", "MACHEN", "a", "qqqq", "")) {
            assertEquals(q, verbos.filtrarPorBusquedaVerbos(q, Idioma.ES).map { it.texto }, buscador.filtrar(verbos, q).map { it.texto })
        }
        assertTrue("DE tiene mas de 300 formas", verbos.size > 300)
    }

    @Test
    fun `stellen en aleman devuelve los 6 miembros de la familia, sea cual sea el nivel`() {
        val miembros = setOf("bestellen", "sich etwas bestellen", "sich vorstellen", "etwas nachbestellen", "jemanden kaltstellen", "etwas richtigstellen")
        // la busqueda es sobre todo el idioma: el nivel de Ajustes no entra en la cuenta (ver el test de pantalla)
        val r = todosLosVerbosDe(Idioma.DE).filtrarPorBusquedaVerbos("stellen", Idioma.ES).map { it.texto }.toSet()
        assertTrue("faltan ${miembros - r}", r.containsAll(miembros))
    }
}
