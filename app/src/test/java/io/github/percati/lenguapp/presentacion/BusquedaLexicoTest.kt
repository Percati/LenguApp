package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Busqueda de "Vocabulario y expresiones" (Ronda I, tarea 3): usa la misma normalizacion que la vista
 * Verbos (sin mayusculas ni diacriticos, ß=ss) y busca los mismos campos de siempre (original, traduccion
 * al idioma de app y funcion).
 */
class BusquedaLexicoTest {

    private val items by lazy {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        construirBiblioteca(
            File(assets, "contenido").listFiles { f -> f.extension == "json" }!!
                .sortedBy { it.name }.map { parsearContenido(it.readText()) },
        )
    }

    private fun buscar(lista: List<ItemBiblioteca>, tipo: TipoGuardado, q: String, base: Idioma = Idioma.ES, idioma: Idioma = Idioma.DE) =
        lista.filtrar(idioma, tipo, emptySet(), null, emptySet(), q, base)

    @Test
    fun `uber en el lexico aleman encuentra palabras con über, con cualquier mayuscula`() {
        val conUmlaut = items.filter { it.idioma == Idioma.DE && it.tipo == TipoGuardado.VOCABULARIO && it.texto.lowercase().contains("über") }
        assertTrue("el contenido real tiene vocabulario con «über»", conUmlaut.isNotEmpty())
        for (q in listOf("uber", "UBER", "Uber", "über", "ÜBER")) {
            val r = buscar(items, TipoGuardado.VOCABULARIO, q).map { it.texto }.toSet()
            for (esperado in conUmlaut) assertTrue("«$q» debe encontrar «${esperado.texto}»", esperado.texto in r)
        }
    }

    @Test
    fun `la busqueda sin tildes tambien funciona en expresiones`() {
        val e = items.first { it.idioma == Idioma.DE && it.tipo == TipoGuardado.EXPRESION && Regex("[äöüß]").containsMatchIn(it.texto.lowercase()) }
        val sinMarcas = normalizarBusqueda(e.texto).take(8)
        assertTrue(buscar(items, TipoGuardado.EXPRESION, sinMarcas).any { it.texto == e.texto })
    }

    @Test
    fun `ß se busca como ss y al reves`() {
        val v = items.first { it.idioma == Idioma.DE }.let { b ->
            b.copy(tipo = TipoGuardado.VOCABULARIO, texto = "die Straße", vocabulario = b.vocabulario?.copy(item = "die Straße") ?: items.first { x -> x.vocabulario != null && x.idioma == Idioma.DE }.vocabulario!!.copy(item = "die Straße"))
        }
        assertEquals(1, buscar(listOf(v), TipoGuardado.VOCABULARIO, "strasse").size)
        assertEquals(1, buscar(listOf(v), TipoGuardado.VOCABULARIO, "STRAßE").size)
        assertEquals(1, buscar(listOf(v), TipoGuardado.VOCABULARIO, "  stra ").size)
    }

    @Test
    fun `sigue buscando por la traduccion y por la funcion, y solo por esos campos`() {
        val base = items.first { it.idioma == Idioma.DE && it.tipo == TipoGuardado.VOCABULARIO && it.vocabulario != null }
        val traducido = base.copy(texto = "zzzwort", vocabulario = base.vocabulario!!.copy(item = "zzzwort", traducciones = mapOf("es" to "Cómodo y ÚTIL")))
        assertEquals("traduccion con diacriticos y mayusculas", 1, buscar(listOf(traducido), TipoGuardado.VOCABULARIO, "comodo").size)
        assertEquals(1, buscar(listOf(traducido), TipoGuardado.VOCABULARIO, "UTIL").size)
        assertEquals("el topic o el nivel no se buscan", 0, buscar(listOf(traducido), TipoGuardado.VOCABULARIO, base.skillIdOrigen).size)

        val expresion = items.first { it.idioma == Idioma.DE && it.tipo == TipoGuardado.EXPRESION && it.funcion != null }
        val funcion = expresion.funcion!!.resolver(Idioma.DE, Idioma.DE)
        val q = normalizarBusqueda(funcion).take(6)
        assertTrue("la funcion sigue siendo buscable", buscar(items, TipoGuardado.EXPRESION, q).any { it.texto == expresion.texto })
    }

    @Test
    fun `una busqueda vacia o en blanco no filtra, y una imposible no devuelve nada`() {
        val todos = buscar(items, TipoGuardado.VOCABULARIO, "")
        assertEquals(todos.size, buscar(items, TipoGuardado.VOCABULARIO, "   ").size)
        assertTrue(todos.isNotEmpty())
        assertFalse(buscar(items, TipoGuardado.VOCABULARIO, "qqqq-no-existe-xyz").isNotEmpty())
    }

    @Test
    fun `el cache de busqueda no cambia la igualdad de los items`() {
        val a = items.first()
        val b = a.copy()
        a.textoBuscable
        a.traduccionBuscable(Idioma.ES)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `la traduccion buscable depende del idioma de app`() {
        val base = items.first { it.idioma == Idioma.DE && it.tipo == TipoGuardado.VOCABULARIO && it.vocabulario != null }
        val v = base.copy(vocabulario = base.vocabulario!!.copy(traducciones = mapOf("es" to "alfa", "en" to "bravo")))
        assertTrue(v.traduccionBuscable(Idioma.ES).contains("alfa"))
        assertTrue(v.traduccionBuscable(Idioma.EN).contains("bravo"))
        assertFalse(v.traduccionBuscable(Idioma.ES).contains("bravo"))
    }
}
