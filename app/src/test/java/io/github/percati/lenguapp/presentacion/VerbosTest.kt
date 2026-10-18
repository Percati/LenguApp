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
    private val idiomas = listOf(Idioma.DE, Idioma.EN)

    @Test
    fun `los verbos de un idioma son TODOS los items con claseVerbo, de cualquier nivel, sin repetir el original`() {
        for (idioma in idiomas) {
            // calculado directo de las fichas, sin pasar por la Biblioteca
            val esperados = fichas.filter { it.idioma == idioma }
                .flatMap { f -> f.vocabulario.filter { it.claseVerbo.isNotEmpty() }.map { it.item.trim() } }.toSet()
            val verbos = items.verbosDe(idioma)
            assertTrue("$idioma: sin verbos", verbos.isNotEmpty())
            assertEquals("$idioma", esperados, verbos.map { it.texto }.toSet())
            assertEquals("$idioma: un verbo aparece una sola vez", verbos.size, verbos.map { it.texto }.toSet().size)
            assertEquals("orden alfabetico por el original", verbos.map { it.texto.lowercase() }, verbos.map { it.texto.lowercase() }.sorted())
        }
    }

    @Test
    fun `el nivel no recorta, hay verbos de los 5 niveles y el conjunto del idioma contiene el de cada nivel`() {
        for (idioma in idiomas) {
            val todos = items.verbosDe(idioma).map { it.texto }.toSet()
            for (nivel in Nivel.entries) {
                val delNivel = items.filter { it.idioma == idioma && it.esVerbo() && nivel in it.niveles }.map { it.texto }.toSet()
                assertTrue("$idioma $nivel: sin verbos", delNivel.isNotEmpty())
                assertTrue("$idioma $nivel: el conjunto del idioma debe contener el del nivel", todos.containsAll(delNivel))
            }
            // y hay verbos que NO estan en un nivel concreto (el recorte por nivel escondia material)
            val enB1 = items.filter { it.idioma == idioma && it.esVerbo() && Nivel.B1 in it.niveles }.size
            assertTrue("$idioma: todos (${todos.size}) debe ser mucho mas que B1 ($enB1)", todos.size > enB1)
        }
    }

    @Test
    fun `la familia de stellen se encuentra entera con cualquier nivel, los 6 miembros`() {
        val miembros = setOf("bestellen", "sich etwas bestellen", "sich vorstellen", "etwas nachbestellen", "jemanden kaltstellen", "etwas richtigstellen")
        val verbos = items.verbosDe(Idioma.DE)
        val r = items.entradasVerbo(Idioma.DE).filtrarPorBusquedaVerbos("stellen", Idioma.ES).filter { it.propio != null }.map { it.infinitivo }.toSet()
        assertTrue("faltan ${miembros - r} (encontro $r)", r.containsAll(miembros))
        // viven repartidos entre niveles (ninguno en B2): con un solo nivel no se podia encontrar la familia
        val porNivel = miembros.associateWith { m -> verbos.first { it.texto == m }.niveles }
        assertTrue("alguno deberia estar en A2 y otro en C2: $porNivel", porNivel.values.any { Nivel.A2 in it } && porNivel.values.any { Nivel.C2 in it })
        assertTrue("ninguno en B2: $porNivel", porNivel.values.none { Nivel.B2 in it })
    }

    @Test
    fun `los chips son solo las clases presentes, simple nunca, y cada chip tiene al menos un verbo`() {
        for (idioma in idiomas) {
            val verbos = items.entradasVerbo(idioma)
            val conteo = verbos.contarClases()
            assertFalse("simple no es un chip", CLASE_VERBO_SIMPLE in conteo)
            assertTrue(conteo.keys.all { it in CLASES_VERBO_CONOCIDAS })
            for ((clase, n) in conteo) {
                assertTrue("$idioma $clase vacio", n >= 1)
                assertEquals(n, verbos.count { clase in it.clases })
            }
            // el aleman no ofrece clases del ingles y viceversa (los chips salen de los datos del idioma)
            val delOtro = if (idioma == Idioma.DE) setOf("phrasal-separable", "phrasal-inseparable", "verbo-preposicion") else setOf("reflexivo", "trennbar", "mit-Praposition", "mit-Dativ")
            assertTrue("$idioma: clases del otro idioma $conteo", conteo.keys.none { it in delOtro })
            // con todos los niveles hay de las 4 (DE) o 3 (EN) clases
            assertEquals(if (idioma == Idioma.DE) 4 else 3, conteo.size)
        }
    }

    @Test
    fun `sin chip elegido se ven todos los verbos, con chips los que tienen alguna de las clases`() {
        val verbos = items.entradasVerbo(Idioma.DE)
        assertEquals(verbos, verbos.filtrarPorClases(emptySet()))
        val trennbar = verbos.filtrarPorClases(setOf("trennbar"))
        assertTrue(trennbar.isNotEmpty() && trennbar.all { "trennbar" in it.clases })
        val union = verbos.filtrarPorClases(setOf("trennbar", "reflexivo"))
        assertTrue(union.size >= trennbar.size)
        assertTrue(union.all { v -> "trennbar" in v.clases || "reflexivo" in v.clases })
        // los `simple` entran en "todos" aunque no tengan chip
        assertTrue(verbos.any { it.clases == listOf("simple") })
    }

    @Test
    fun `una clase desconocida no rompe, el verbo se ve, sin chip para esa clase`() {
        val base = items.verbosDe(Idioma.DE).first { it.vocabulario!!.claseVerbo.contains("trennbar") }
        val raro = base.copy(
            texto = "zzz-verbo-raro",
            vocabulario = base.vocabulario!!.copy(item = "zzz-verbo-raro", claseVerbo = listOf("clase-del-futuro")),
        )
        val lista = items.filter { it.idioma == Idioma.DE } + raro
        val verbos = lista.entradasVerbo(Idioma.DE)
        assertTrue("el verbo de clase desconocida se muestra", verbos.any { it.infinitivo == "zzz-verbo-raro" })
        assertFalse("y no genera chip", verbos.contarClases().keys.any { it == "clase-del-futuro" })
        assertEquals(emptyList<String>(), raro.clasesDeChip())
        assertNull(etiquetaClaseVerbo("clase-del-futuro", Idioma.ES))
        // con un chip elegido no entra (no tiene esa clase), con ninguno si
        assertTrue(verbos.filtrarPorClases(setOf("trennbar")).none { it.infinitivo == "zzz-verbo-raro" })
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
    fun `la linea extra ya no lleva el nivel, solo en aleman el regimen`() {
        val conReccion = items.verbosDe(Idioma.DE).first { !it.vocabulario!!.reccion.isNullOrBlank() }
        assertEquals(conReccion.vocabulario!!.reccion!!.trim(), conReccion.lineaExtraVerbo(emptySet()) { it })
        val sinReccion = items.verbosDe(Idioma.DE).first { it.vocabulario!!.reccion.isNullOrBlank() }
        assertEquals("sin regimen no hay linea", "", sinReccion.lineaExtraVerbo(emptySet()) { it })
        // nunca un nivel (A1..C2), ni solo ni acompanando
        for (verbo in items.verbosDe(Idioma.DE).take(300)) {
            val linea = verbo.lineaExtraVerbo(emptySet()) { it }
            assertTrue("«$linea» lleva un nivel", Nivel.entries.none { n -> linea == n.name || linea.startsWith(n.name + " ·") })
        }
    }

    @Test
    fun `en ingles la linea extra es la separabilidad, salvo que ya se desprenda del chip elegido, y si no hay nada queda vacia`() {
        val sep = items.verbosDe(Idioma.EN).first { "phrasal-separable" in it.vocabulario!!.claseVerbo }
        assertEquals("phrasal-separable", sep.lineaExtraVerbo(emptySet()) { it })
        assertEquals("phrasal-separable", sep.lineaExtraVerbo(setOf("verbo-preposicion")) { it })
        assertEquals("el chip elegido ya lo dice: no queda nada", "", sep.lineaExtraVerbo(setOf("phrasal-separable")) { it })
        val prep = items.verbosDe(Idioma.EN).first { it.vocabulario!!.claseVerbo == listOf("verbo-preposicion") }
        assertEquals("verbo + preposicion no es separabilidad", "", prep.lineaExtraVerbo(emptySet()) { it })
        val simple = items.verbosDe(Idioma.EN).first { it.vocabulario!!.claseVerbo == listOf("simple") }
        assertEquals("", simple.lineaExtraVerbo(emptySet()) { it })
    }

    @Test
    fun `el verbo es el mismo item de Guardados que en las otras vistas, no una clave distinta`() {
        val v = items.verbosDe(Idioma.DE).first()
        // misma identidad (idioma, tipo, texto) que usa la vista de vocabulario y la ficha
        assertEquals(TipoGuardado.VOCABULARIO, v.tipo)
        assertTrue(items.any { it === v })
        assertEquals("${v.tipo}|${v.texto}", v.claveGuardado())
    }
}
