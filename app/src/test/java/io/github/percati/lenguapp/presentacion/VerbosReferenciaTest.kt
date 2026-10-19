package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.VerboReferencia
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearVerbosReferencia
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * La segunda fuente de la vista Verbos (Ronda U): los verbos de REFERENCIA, extraidos por Gradle a assets/verbos/, y su fusion por
 * infinitivo con los verbos propios y los nucleos de colocacion. Sobre los assets reales y casos sinteticos.
 */
class VerbosReferenciaTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private fun proyecto(): File = listOf(File("../proyecto"), File("proyecto")).first { it.isDirectory }

    private val fichas: List<Ficha> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .map { parsearContenido(it.readText()) as ContenidoSemanal }.filterIsInstance<Ficha>()
    }
    private val items by lazy { construirBiblioteca(fichas) }

    private fun referencia(idioma: Idioma): List<VerboReferencia> =
        parsearVerbosReferencia(File(assets(), "verbos/referencia-${idioma.name.lowercase()}.json").readText())

    private fun ref(item: String, clase: String = "simple", es: String = "glosa de $item", reccion: String? = null, raiz: String? = null) =
        VerboReferencia(item = item, base = item.removePrefix("to "), claseVerbo = listOf(clase), traducciones = mapOf("es" to es), raiz = raiz, reccion = reccion)

    // --- 4. el asset extraido: lo que viaja y lo que no (se lee el archivo, no el modelo) ---

    private fun claves(e: JsonElement, acumulado: MutableSet<String> = mutableSetOf()): Set<String> {
        when (e) {
            is JsonObject -> e.forEach { (k, v) -> acumulado += k; claves(v, acumulado) }
            is JsonArray -> e.forEach { claves(it, acumulado) }
            else -> {}
        }
        return acumulado
    }

    @Test
    fun `el asset extraido no lleva nivelFuente ni nota ni la marca referencia, solo lo que la app necesita`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val archivo = File(assets(), "verbos/referencia-${idioma.name.lowercase()}.json")
            assertTrue("falta $archivo: lo escribe la tarea extraerVerbosReferenciaAssets", archivo.isFile)
            val raiz = Json.parseToJsonElement(archivo.readText())
            val todas = claves(raiz)
            assertFalse("nivelFuente viaja en $idioma", "nivelFuente" in todas)
            assertFalse("nota viaja en $idioma", "nota" in todas)
            assertFalse("referencia viaja en $idioma", "referencia" in todas)
            assertEquals(setOf("idioma", "verbos", "item", "base", "claseVerbo", "traducciones", "raiz", "reccion", "es", "en", "de", "fr", "it", "pt").containsAll(todas), true)
            // cada entrada trae lo imprescindible
            for (v in raiz.jsonObject.getValue("verbos").jsonArray) {
                val o = v.jsonObject
                assertTrue("sin item", "item" in o && "claseVerbo" in o && "traducciones" in o)
            }
        }
    }

    @Test
    fun `el asset extraido es exactamente las entradas referencia de la tabla maestra, con su raiz`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val fuente = Json.parseToJsonElement(File(proyecto(), "contenido/verbos/${idioma.name.lowercase()}.json").readText())
                .jsonObject.getValue("verbos").jsonArray.map { it.jsonObject }.filter { it["referencia"]?.toString() == "true" }
            val extraidas = referencia(idioma)
            assertEquals("$idioma: cuantas", fuente.size, extraidas.size)
            assertEquals(fuente.map { it.getValue("item").toString().trim('"') }, extraidas.map { it.item })
            // la raiz viaja (es lo que hace que buscar «stellen» traiga la familia)
            assertEquals(fuente.count { "raiz" in it }, extraidas.count { it.raiz != null })
        }
        assertEquals(160, referencia(Idioma.DE).size)
        assertEquals(339, referencia(Idioma.EN).size)
    }

    // --- 1. solo referencia ---

    @Test
    fun `un verbo que es solo referencia aparece, con glosa y sin estrella`() {
        val r = ref("to blorp", es = "blorpear")
        val e = items.entradasVerbo(Idioma.EN, listOf(r)).single { it.infinitivo == "to blorp" }
        assertNull("no es un item: no es marcable", e.propio)
        assertTrue(e.colocaciones.isEmpty())
        assertEquals("blorpear", e.traduccion(Idioma.ES))
        assertEquals("simple", e.clases.single())
        assertNull("sin glosa si la app esta en el idioma que se aprende", e.traduccion(Idioma.EN))
        // y la referencia real: aufstehen o similar, sin propio ni nucleo
        val solo = items.entradasVerbo(Idioma.DE, referencia(Idioma.DE)).filter { it.propio == null && it.colocaciones.isEmpty() }
        assertTrue("hay verbos de solo referencia", solo.size > 50)
        for (e2 in solo) assertNotNull("${e2.infinitivo}: sin glosa", e2.traduccion(Idioma.ES))
    }

    // --- 2. referencia + nucleo ---

    @Test
    fun `un verbo que es referencia y nucleo aparece una vez, con la glosa de referencia y las colocaciones del nucleo`() {
        val nucleo = items.entradasVerbo(Idioma.DE).first { it.propio == null && it.colocaciones.isNotEmpty() }
        val r = ref(nucleo.infinitivo, clase = "simple", es = "glosa del nucleo")
        val entradas = items.entradasVerbo(Idioma.DE, listOf(r))
        val e = entradas.filter { it.infinitivo == nucleo.infinitivo }.single()
        assertEquals("glosa del nucleo", e.traduccion(Idioma.ES))
        assertEquals(nucleo.colocaciones, e.colocaciones)
        assertNull("el propio sigue sin existir: la referencia no la vuelve marcable", e.propio)
        assertEquals("la clase del nucleo manda", nucleo.clases, e.clases)
        assertEquals(items.entradasVerbo(Idioma.DE).size, entradas.size)
    }

    // --- 3. referencia + propio ---

    @Test
    fun `un verbo que es referencia y propio aparece una vez y manda la traduccion del propio`() {
        val propio = items.entradasVerbo(Idioma.DE).first { it.propio != null && !it.propio!!.vocabulario!!.traducciones["es"].isNullOrBlank() }
        val r = ref(propio.infinitivo, es = "OTRA glosa, de referencia")
        val e = items.entradasVerbo(Idioma.DE, listOf(r)).filter { it.infinitivo == propio.infinitivo }.single()
        assertEquals(propio.propio!!.vocabulario!!.traducciones["es"]!!.trim(), e.traduccion(Idioma.ES))
        assertFalse(e.traduccion(Idioma.ES)!!.contains("OTRA"))
        assertEquals(propio.propio, e.propio)
        assertEquals(propio.clases, e.clases)
    }

    @Test
    fun `deal with se fusiona con su anotacion to deal with something, una sola fila`() {
        val r = referencia(Idioma.EN).first { it.item == "to deal with" }
        val e = items.entradasVerbo(Idioma.EN, listOf(r)).filter { it.infinitivo.contains("deal with") }
        assertEquals(e.map { it.infinitivo }.toString(), 1, e.size)
        assertEquals("to deal with something", e.single().infinitivo)
        assertNotNull(e.single().propio)
        // la clase de la anotacion manda: el desacuerdo con la referencia (verbo-preposicion) no se resuelve ni se vigila
        assertEquals(listOf("phrasal-inseparable"), e.single().clases)
        assertEquals("verbo-preposicion", r.claseVerbo.single())
    }

    @Test
    fun `la fusion en ingles ignora el to de delante`() {
        val propio = items.entradasVerbo(Idioma.EN).first { it.propio != null && it.infinitivo.startsWith("to ") }
        val sinTo = ref(propio.infinitivo.removePrefix("to "))
        val entradas = items.entradasVerbo(Idioma.EN, listOf(sinTo))
        assertEquals(1, entradas.count { it.infinitivo == propio.infinitivo || it.infinitivo == sinTo.item })
        assertEquals(items.entradasVerbo(Idioma.EN).size, entradas.size)
    }

    // --- 5. el conteo real ---

    @Test
    fun `la fusion mueve el conteo, la vista no crece en las 499 de referencia sino en esas menos las que coinciden`() {
        for ((idioma, esperado) in listOf(Idioma.DE to ESPERADO_DE, Idioma.EN to ESPERADO_EN)) {
            val refs = referencia(idioma)
            val sin = items.entradasVerbo(idioma)
            val con = items.entradasVerbo(idioma, refs)
            val claves = sin.map { claveFusionVerbo(it.infinitivo, idioma) }.toSet()
            // (en ingles tambien cuenta «to deal with» contra el item «to deal with something»: la tabla los une por `base`)
            val sinObjeto = claves.map { it.replace(Regex("""\s+(something|someone|somebody|oneself|yourself)$"""), "") }.toSet()
            val colisiones = refs.count {
                claveFusionVerbo(it.item, idioma) in claves || claveFusionVerbo(it.base, idioma) in claves ||
                    (idioma == Idioma.EN && claveFusionVerbo(it.item, idioma) in sinObjeto)
            }
            println("MEDIDA-U $idioma sin=${sin.size} referencia=${refs.size} colisiones=$colisiones con=${con.size}")
            assertEquals("$idioma: sin referencia", esperado.sin, sin.size)
            assertEquals("$idioma: colisiones", esperado.colisiones, colisiones)
            assertEquals("$idioma: con referencia", esperado.con, con.size)
            assertEquals(sin.size + refs.size - colisiones, con.size)
            // un infinitivo, una fila
            val k = con.map { claveFusionVerbo(it.infinitivo, idioma) }
            // la referencia no crea ningun duplicado: los que haya ya estaban (EN trae 7 de antes: change, help, queue, rent, taste, trust,
            // fact-check, entre un verbo propio y un nucleo; no son de esta ronda)
            fun repetidos(claves: List<String>) = claves.groupBy { it }.filter { it.value.size > 1 }.keys
            assertEquals("$idioma: infinitivos repetidos", repetidos(sin.map { claveFusionVerbo(it.infinitivo, idioma) }), repetidos(k))
        }
    }

    // --- 6. chips ---

    @Test
    fun `los chips de clase salen del contenido y ninguno queda vacio`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val con = items.entradasVerbo(idioma, referencia(idioma))
            val conteo = con.contarClases()
            assertTrue("$idioma: sin chips", conteo.isNotEmpty())
            for ((clase, n) in conteo) {
                assertTrue("$idioma: chip «$clase» vacio", n > 0)
                assertEquals(n, con.count { clase in it.clasesDeChip() })
                assertTrue(con.filtrarPorClases(setOf(clase)).isNotEmpty())
            }
            // ninguna entrada de solo referencia queda sin clase
            assertTrue(con.none { it.clases.isEmpty() })
        }
    }

    // --- la linea extra, la busqueda ---

    @Test
    fun `la referencia pone el regimen y la raiz en la busqueda, y no se come lo del propio`() {
        val r = ref("to blorp", clase = "phrasal-separable", reccion = null, raiz = "zzraiz")
        val e = items.entradasVerbo(Idioma.EN, listOf(r)).single { it.infinitivo == "to blorp" }
        assertEquals("phrasal-separable", e.lineaExtra(emptySet()) { it })
        assertEquals("", e.lineaExtra(setOf("phrasal-separable")) { it })
        val lista = items.entradasVerbo(Idioma.EN, listOf(r))
        assertEquals(listOf("to blorp"), lista.filtrarPorBusquedaVerbos("zzraiz", Idioma.ES).map { it.infinitivo })
        assertEquals(listOf("to blorp"), lista.filtrarPorBusquedaVerbos("glosa de to", Idioma.ES).map { it.infinitivo })
        val de = items.entradasVerbo(Idioma.DE, listOf(ref("blorpen", reccion = "auf + Akk")))
        assertEquals("auf + Akk", de.single { it.infinitivo == "blorpen" }.lineaExtra(emptySet()) { it })
    }

    @Test
    fun `los dos desacuerdos de clase anotados a proposito no se tocan ni se vigilan`() {
        // `hoeren`: el nucleo dice mit-Praposition, la referencia simple. Manda el nucleo; no hay ninguna guarda.
        val de = items.entradasVerbo(Idioma.DE, referencia(Idioma.DE))
        val horen = de.firstOrNull { it.infinitivo == "hören" && it.referencia != null }
        if (horen != null && horen.propio == null) assertEquals(horen.clases, items.entradasVerbo(Idioma.DE).first { it.infinitivo == "hören" }.clases)
        // `deal with`: anotacion phrasal-inseparable, referencia verbo-preposicion. Manda la anotacion (ver el test de deal with).
        val deal = items.entradasVerbo(Idioma.EN, referencia(Idioma.EN)).single { it.infinitivo.contains("deal with") }
        assertEquals(listOf("phrasal-inseparable"), deal.clases)
    }

    private data class Esperado(val sin: Int, val colisiones: Int, val con: Int)

    private companion object {
        // Medidos con los assets reales (ver MEDIDA-U): si la fusion se rompe, el segundo numero se mueve.
        val ESPERADO_DE = Esperado(470, 45, 585)
        val ESPERADO_EN = Esperado(559, 79, 819)
    }
}
