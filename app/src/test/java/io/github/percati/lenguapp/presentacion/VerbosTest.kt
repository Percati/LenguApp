package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.ui.etiquetaClaseVerbo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Vista «Verbos» (Ronda H): universo, deduplicacion, chips por clase y linea extra, sobre los assets reales y casos sinteticos. */
class VerbosTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val fichas: List<Ficha> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .map { parsearContenido(it.readText()) as ContenidoSemanal }.filterIsInstance<Ficha>()
    }
    private val items by lazy { construirBiblioteca(fichas) }
    private val pares = listOf(Idioma.DE, Idioma.EN).flatMap { i -> Nivel.entries.map { n -> i to n } }

    @Test
    fun `los verbos de cada par son los items con claseVerbo, sin repetir el original`() {
        for ((idioma, nivel) in pares) {
            // calculado directo de las fichas, sin pasar por la Biblioteca
            val esperados = fichas.filter { it.idioma == idioma && it.nivel == nivel }
                .flatMap { f -> f.vocabulario.filter { it.claseVerbo.isNotEmpty() }.map { it.item.trim() } }.toSet()
            val verbos = items.verbosDe(idioma, nivel)
            assertTrue("$idioma $nivel: sin verbos", verbos.isNotEmpty())
            assertEquals("$idioma $nivel", esperados, verbos.map { it.texto }.toSet())
            assertEquals("$idioma $nivel: un verbo aparece una sola vez", verbos.size, verbos.map { it.texto }.toSet().size)
            assertEquals("orden alfabetico por el original", verbos.map { it.texto.lowercase() }, verbos.map { it.texto.lowercase() }.sorted())
        }
    }

    @Test
    fun `los chips son solo las clases presentes, simple nunca, y cada chip tiene al menos un verbo`() {
        for ((idioma, nivel) in pares) {
            val verbos = items.verbosDe(idioma, nivel)
            val conteo = verbos.contarClases()
            assertFalse("simple no es un chip", CLASE_VERBO_SIMPLE in conteo)
            assertTrue(conteo.keys.all { it in CLASES_VERBO_CONOCIDAS })
            for ((clase, n) in conteo) {
                assertTrue("$idioma $nivel $clase vacio", n >= 1)
                assertEquals(n, verbos.count { clase in it.vocabulario!!.claseVerbo })
            }
            // el aleman no ofrece clases del ingles y viceversa (los chips salen de los datos del idioma)
            val delOtro = if (idioma == Idioma.DE) setOf("phrasal-separable", "phrasal-inseparable", "verbo-preposicion") else setOf("reflexivo", "trennbar", "mit-Praposition", "mit-Dativ")
            assertTrue("$idioma $nivel: clases del otro idioma $conteo", conteo.keys.none { it in delOtro })
        }
    }

    @Test
    fun `sin chip elegido se ven todos los verbos, con chips los que tienen alguna de las clases`() {
        val verbos = items.verbosDe(Idioma.DE, Nivel.B1)
        assertEquals(verbos, verbos.filtrarPorClases(emptySet()))
        val trennbar = verbos.filtrarPorClases(setOf("trennbar"))
        assertTrue(trennbar.isNotEmpty() && trennbar.all { "trennbar" in it.vocabulario!!.claseVerbo })
        val union = verbos.filtrarPorClases(setOf("trennbar", "reflexivo"))
        assertTrue(union.size >= trennbar.size)
        assertTrue(union.all { v -> "trennbar" in v.vocabulario!!.claseVerbo || "reflexivo" in v.vocabulario!!.claseVerbo })
        // los `simple` entran en "todos" aunque no tengan chip
        assertTrue(verbos.any { it.vocabulario!!.claseVerbo == listOf("simple") })
    }

    @Test
    fun `una clase desconocida no rompe, el verbo se ve, sin chip para esa clase`() {
        val base = items.verbosDe(Idioma.DE, Nivel.A2).first { it.vocabulario!!.claseVerbo.contains("trennbar") }
        val raro = base.copy(
            texto = "zzz-verbo-raro",
            vocabulario = base.vocabulario!!.copy(item = "zzz-verbo-raro", claseVerbo = listOf("clase-del-futuro")),
        )
        val lista = items.filter { it.idioma == Idioma.DE } + raro
        val verbos = lista.verbosDe(Idioma.DE, Nivel.A2)
        assertTrue("el verbo de clase desconocida se muestra", verbos.any { it.texto == "zzz-verbo-raro" })
        assertFalse("y no genera chip", verbos.contarClases().keys.any { it == "clase-del-futuro" })
        assertEquals(emptyList<String>(), raro.clasesDeChip())
        assertNull(etiquetaClaseVerbo("clase-del-futuro", Idioma.ES))
        // con un chip elegido no entra (no tiene esa clase), con ninguno si
        assertTrue(verbos.filtrarPorClases(setOf("trennbar")).none { it.texto == "zzz-verbo-raro" })
    }

    @Test
    fun `un item de vocabulario sin claseVerbo y una expresion no son verbos`() {
        val v = items.first { it.tipo == TipoGuardado.VOCABULARIO && it.vocabulario!!.claseVerbo.isEmpty() }
        assertFalse(v.esVerbo())
        assertFalse(items.first { it.tipo == TipoGuardado.EXPRESION }.esVerbo())
    }

    @Test
    fun `las etiquetas de las 7 clases existen en los 6 idiomas`() {
        for (clase in CLASES_VERBO_CONOCIDAS) for (idioma in Idioma.entries) {
            val e = etiquetaClaseVerbo(clase, idioma)
            assertTrue("$clase en $idioma", !e.isNullOrBlank())
        }
        assertEquals("Con preposición", etiquetaClaseVerbo("mit-Praposition", Idioma.ES))
        assertEquals("Trennbares Phrasal Verb", etiquetaClaseVerbo("phrasal-separable", Idioma.DE))
        assertEquals("Pronominal", etiquetaClaseVerbo("reflexivo", Idioma.FR))
        assertEquals("Verbo + preposizione", etiquetaClaseVerbo("verbo-preposicion", Idioma.IT))
        assertEquals("Phrasal verb inseparável", etiquetaClaseVerbo("phrasal-inseparable", Idioma.PT))
        assertNull(etiquetaClaseVerbo(CLASE_VERBO_SIMPLE, Idioma.ES))
    }

    // --- la linea discreta de debajo del verbo ---

    @Test
    fun `en aleman la linea extra es el regimen cuando existe, y nada cuando no`() {
        val conReccion = items.verbosDe(Idioma.DE, Nivel.B1).first { !it.vocabulario!!.reccion.isNullOrBlank() }
        assertEquals(conReccion.vocabulario!!.reccion!!.trim(), conReccion.lineaExtraVerbo(emptySet()) { it })
        val sinReccion = items.verbosDe(Idioma.DE, Nivel.B1).first { it.vocabulario!!.reccion.isNullOrBlank() }
        assertNull(sinReccion.lineaExtraVerbo(emptySet()) { it })
    }

    @Test
    fun `en ingles la linea extra es la separabilidad, salvo que ya se desprenda del chip elegido`() {
        val sep = items.verbosDe(Idioma.EN, Nivel.B1).first { "phrasal-separable" in it.vocabulario!!.claseVerbo }
        assertEquals("phrasal-separable", sep.lineaExtraVerbo(emptySet()) { it })
        assertEquals("phrasal-separable", sep.lineaExtraVerbo(setOf("verbo-preposicion")) { it })
        assertNull("el chip elegido ya lo dice", sep.lineaExtraVerbo(setOf("phrasal-separable")) { it })
        val prep = items.verbosDe(Idioma.EN, Nivel.B1).first { it.vocabulario!!.claseVerbo == listOf("verbo-preposicion") }
        assertNull("verbo + preposicion no es separabilidad", prep.lineaExtraVerbo(emptySet()) { it })
        val simple = items.verbosDe(Idioma.EN, Nivel.B1).first { it.vocabulario!!.claseVerbo == listOf("simple") }
        assertNull(simple.lineaExtraVerbo(emptySet()) { it })
    }

    @Test
    fun `el verbo es el mismo item de Guardados que en las otras vistas, no una clave distinta`() {
        val v = items.verbosDe(Idioma.DE, Nivel.A2).first()
        // misma identidad (idioma, tipo, texto) que usa la vista de vocabulario y la ficha
        assertEquals(TipoGuardado.VOCABULARIO, v.tipo)
        assertTrue(items.any { it === v })
        assertEquals("${v.tipo}|${v.texto}", v.claveGuardado())
    }
}
