package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.TipoGuardado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Nucleos verbales en la vista Verbos (Ronda J, tareas 1 y 2): las dos clases de entrada (verbo propio y nucleo
 * de colocaciones), la fusion por infinitivo, las clases de los nucleos en los chips y la busqueda por colocacion.
 * Sobre los assets reales y casos sinteticos.
 */
class NucleosVerbosTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val fichas: List<Ficha> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .map { parsearContenido(it.readText()) as ContenidoSemanal }.filterIsInstance<Ficha>()
    }
    private val items by lazy { construirBiblioteca(fichas) }

    // --- totales y fusion (contados directo de las fichas, sin pasar por la Biblioteca) ---

    private fun propiosDe(idioma: Idioma) =
        fichas.filter { it.idioma == idioma }.flatMap { f -> f.vocabulario.filter { it.claseVerbo.isNotEmpty() }.map { it.item.trim() } }.toSet()

    private fun nucleosDe(idioma: Idioma) =
        fichas.filter { it.idioma == idioma }.flatMap { f -> f.vocabulario.mapNotNull { it.nucleoVerbal?.trim() } }.toSet()

    @Test
    fun `DE tiene 305 propios, 197 nucleos, 32 fusionados y 470 entradas`() {
        val propios = propiosDe(Idioma.DE)
        val nucleos = nucleosDe(Idioma.DE)
        assertEquals(305, propios.size)
        assertEquals(197, nucleos.size)
        assertEquals(32, nucleos.count { it in propios })
        assertEquals(470, items.entradasVerbo(Idioma.DE).size)
    }

    @Test
    fun `EN tiene 368 propios, 256 nucleos, 65 fusionados y 559 entradas`() {
        val propios = propiosDe(Idioma.EN)
        val nucleos = nucleosDe(Idioma.EN)
        assertEquals(368, propios.size)
        assertEquals(256, nucleos.size)
        assertEquals(65, nucleos.count { it in propios })
        assertEquals(559, items.entradasVerbo(Idioma.EN).size)
    }

    @Test
    fun `un infinitivo aparece una sola vez, en orden alfabetico, y la fusion deja la fila del verbo propio`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val entradas = items.entradasVerbo(idioma)
            assertEquals("$idioma: infinitivos repetidos", entradas.size, entradas.map { it.infinitivo }.toSet().size)
            assertEquals(entradas.map { it.infinitivo.lowercase() }, entradas.map { it.infinitivo.lowercase() }.sorted())
            val propios = propiosDe(idioma)
            val fusionadas = entradas.filter { it.propio != null && it.colocaciones.isNotEmpty() }
            assertEquals("$idioma: fusionadas", nucleosDe(idioma).count { it in propios }, fusionadas.size)
            for (e in entradas) {
                // verbo propio <=> el infinitivo es un item etiquetado; el nucleo puro nunca lo es
                assertEquals(e.infinitivo in propios, e.propio != null)
                if (e.propio == null) assertTrue("$idioma ${e.infinitivo}: un nucleo puro sin colocaciones", e.colocaciones.isNotEmpty())
            }
        }
    }

    @Test
    fun `la fusion de la que habla el prompt, afford y abgeben, es una sola fila marcable con sus colocaciones debajo`() {
        val abgeben = items.entradasVerbo(Idioma.DE).filter { it.infinitivo == "abgeben" }
        assertEquals(1, abgeben.size)
        assertNotNull(abgeben.single().propio)
        assertTrue(abgeben.single().colocaciones.any { it.texto.contains("Verantwortung abgeben") })
        val afford = items.entradasVerbo(Idioma.EN).filter { it.infinitivo == "to afford" }
        assertEquals(1, afford.size)
        assertNotNull(afford.single().propio)
        assertTrue(afford.single().colocaciones.any { it.texto == "to afford something" })
    }

    // --- el nucleo no es un item; la colocacion si, y aparece solo debajo de su entrada ---

    @Test
    fun `toda colocacion con nucleo aparece una vez, debajo de su entrada, y nunca suelta como entrada`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val entradas = items.entradasVerbo(idioma)
            val colocaciones = items.filter { it.idioma == idioma && it.esColocacionConNucleo() }
            val debajo = entradas.flatMap { e -> e.colocaciones.map { e.infinitivo to it } }
            assertEquals("$idioma: cada colocacion una vez", colocaciones.size, debajo.size)
            assertEquals(colocaciones.map { it.texto }.toSet(), debajo.map { it.second.texto }.toSet())
            for ((infinitivo, c) in debajo) assertEquals(infinitivo, c.vocabulario!!.nucleoVerbal!!.trim())
            // ninguna colocacion es una entrada por si misma (las entradas son verbos propios o nucleos)
            val infinitivos = entradas.map { it.infinitivo }.toSet()
            assertTrue(colocaciones.none { it.texto in infinitivos })
            // el nucleo no es un item de la Biblioteca: solo existe el item de la colocacion (la clave de Guardados)
            for (e in entradas.filter { it.propio == null }) {
                assertTrue("${e.infinitivo} no debe ser un item", items.none { it.idioma == idioma && it.tipo == TipoGuardado.VOCABULARIO && it.texto == e.infinitivo })
            }
        }
    }

    @Test
    fun `los verbos propios del lexico no cambian, y la colocacion sigue en el lexico`() {
        assertEquals(305, items.verbosDe(Idioma.DE).size)
        assertEquals(368, items.verbosDe(Idioma.EN).size)
        val colocacion = items.first { it.idioma == Idioma.DE && it.texto == "die Frage stellen" }
        val enLexico = items.filtrar(Idioma.DE, TipoGuardado.VOCABULARIO, emptySet(), null, emptySet(), "die Frage stellen", Idioma.ES)
        assertTrue(enLexico.any { it === colocacion })
        assertFalse(colocacion.esVerbo())
    }

    // --- chips ---

    @Test
    fun `un nucleo aporta su claseNucleo al conteo, y las reglas de los chips no cambian`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val entradas = items.entradasVerbo(idioma)
            val conteo = entradas.contarClases()
            assertFalse(CLASE_VERBO_SIMPLE in conteo)
            assertTrue(conteo.keys.all { it in CLASES_VERBO_CONOCIDAS })
            for ((clase, n) in conteo) assertEquals(n, entradas.count { clase in it.clasesDeChip() })
            // los nucleos puros cuentan: sin ellos las cuentas serian menores
            val soloPropios = entradas.filter { it.propio != null }.contarClases()
            assertTrue("$idioma: los nucleos suman", conteo.any { (clase, n) -> n > (soloPropios[clase] ?: 0) })
            assertEquals(if (idioma == Idioma.DE) 4 else 3, conteo.size)
        }
    }

    @Test
    fun `un nucleo puro con una clase de chip se encuentra al elegir ese chip, uno simple solo sin chip`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        val umstellen = entradas.first { it.infinitivo == "umstellen" }
        assertNull(umstellen.propio)
        assertTrue("trennbar" in umstellen.clases)
        assertTrue(entradas.filtrarPorClases(setOf("trennbar")).any { it.infinitivo == "umstellen" })
        assertFalse(entradas.filtrarPorClases(setOf("reflexivo")).any { it.infinitivo == "umstellen" })
        val stellen = entradas.first { it.infinitivo == "stellen" }
        assertEquals(listOf("simple"), stellen.clases)
        assertTrue(entradas.filtrarPorClases(emptySet()).any { it.infinitivo == "stellen" })
        assertFalse(entradas.filtrarPorClases(setOf("trennbar", "reflexivo", "mit-Praposition", "mit-Dativ")).any { it.infinitivo == "stellen" })
    }

    @Test
    fun `una clase desconocida en un nucleo no rompe ni genera chip`() {
        val base = items.first { it.idioma == Idioma.DE && it.esColocacionConNucleo() }
        val raro = base.copy(
            texto = "zzz colocacion rara",
            vocabulario = base.vocabulario!!.copy(item = "zzz colocacion rara", nucleoVerbal = "zzzen", claseNucleo = listOf("clase-del-futuro"), reccionNucleo = null),
        )
        val entradas = (items.filter { it.idioma == Idioma.DE } + raro).entradasVerbo(Idioma.DE)
        val e = entradas.first { it.infinitivo == "zzzen" }
        assertEquals(emptyList<String>(), e.clasesDeChip())
        assertFalse("clase-del-futuro" in entradas.contarClases())
        assertTrue(entradas.filtrarPorClases(emptySet()).contains(e))
    }

    // --- linea extra ---

    @Test
    fun `la linea extra de un nucleo puro lleva el nivel mas bajo de sus colocaciones y la reccionNucleo`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        val conReccion = entradas.first { it.propio == null && !it.reccionNucleo.isNullOrBlank() }
        val bajo = conReccion.colocaciones.flatMap { it.niveles }.min().name
        assertEquals("$bajo · ${conReccion.reccionNucleo!!.trim()}", conReccion.lineaExtra(emptySet()) { it })
        val sinReccion = entradas.first { it.propio == null && it.reccionNucleo.isNullOrBlank() }
        assertEquals(sinReccion.colocaciones.flatMap { it.niveles }.min().name, sinReccion.lineaExtra(emptySet()) { it })
    }

    @Test
    fun `la linea extra de un verbo propio no cambia, lleve o no colocaciones debajo`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        val fusionada = entradas.first { it.propio != null && it.colocaciones.isNotEmpty() }
        assertEquals(fusionada.propio!!.lineaExtraVerbo(emptySet()) { it }, fusionada.lineaExtra(emptySet()) { it })
    }

    @Test
    fun `en ingles la linea extra de un nucleo puro dice la separabilidad salvo que el chip ya la diga`() {
        val sep = items.entradasVerbo(Idioma.EN).first { it.propio == null && "phrasal-separable" in it.clases }
        val n = sep.colocaciones.flatMap { it.niveles }.min().name
        assertEquals("$n · phrasal-separable", sep.lineaExtra(emptySet()) { it })
        assertEquals(n, sep.lineaExtra(setOf("phrasal-separable")) { it })
    }

    // --- busqueda ---

    @Test
    fun `buscar Frage trae stellen, por el texto de su colocacion`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        val r = entradas.filtrarPorBusquedaVerbos("Frage", Idioma.ES)
        val stellen = r.firstOrNull { it.infinitivo == "stellen" }
        assertNotNull("Frage debe traer stellen", stellen)
        assertTrue("y la colocacion que coincidio esta entre las de la entrada", stellen!!.colocaciones.any { it.texto.contains("Frage") })
        assertFalse("«Frage» no esta en el infinitivo: lo encontro por la colocacion", "frage" in stellen.infinitivo)
    }

    @Test
    fun `buscar por la traduccion de una colocacion trae su nucleo`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        // la traduccion al idioma de app de «die Frage stellen»
        val colocacion = items.first { it.idioma == Idioma.DE && it.texto == "die Frage stellen" }
        val traduccion = colocacion.datosFila(Idioma.ES).traduccion!!
        val r = entradas.filtrarPorBusquedaVerbos(traduccion.uppercase(), Idioma.ES)
        assertTrue("`$traduccion` debe traer stellen", r.any { it.infinitivo == "stellen" })
    }

    @Test
    fun `el caso stellen en aleman devuelve la familia, con la fusion de bestellen en una sola fila`() {
        val entradas = items.entradasVerbo(Idioma.DE)
        val r = entradas.filtrarPorBusquedaVerbos("stellen", Idioma.ES)
        val propios = setOf("bestellen", "sich etwas bestellen", "sich vorstellen", "etwas nachbestellen", "etwas richtigstellen", "jemanden kaltstellen")
        val nucleos = setOf("stellen", "einstellen", "umstellen")
        assertTrue("faltan propios ${propios - r.filter { it.propio != null }.map { it.infinitivo }.toSet()}", r.filter { it.propio != null }.map { it.infinitivo }.containsAll(propios))
        assertTrue("faltan nucleos", r.filter { it.propio == null }.map { it.infinitivo }.containsAll(nucleos))
        assertEquals("bestellen es UNA sola fila (la del verbo propio)", 1, r.count { it.infinitivo == "bestellen" })
        val bestellen = r.single { it.infinitivo == "bestellen" }
        assertNotNull(bestellen.propio)
        assertTrue(bestellen.colocaciones.any { it.texto == "online bestellen" })
        // el prompt contaba 10 con bestellen dos veces (6 propios + 4 nucleos); fusionado son 9 + lo que traiga por colocaciones
        assertEquals(r.size, r.map { it.infinitivo }.toSet().size)
        assertEquals("stellen: ${r.map { it.infinitivo }}", propios.size + nucleos.size, r.size)
    }

    @Test
    fun `el buscador precalculado cubre colocaciones y da lo mismo que filtrar en cada tecla`() {
        val entradas = items.entradasVerbo(Idioma.EN)
        val buscador = BuscadorVerbos(entradas, Idioma.ES)
        for (q in listOf("take", "TAKE", "up", "a", "qqqq", "", "  ")) {
            assertEquals(q, entradas.filtrarPorBusquedaVerbos(q, Idioma.ES).map { it.infinitivo }, buscador.filtrar(entradas, q).map { it.infinitivo })
        }
        // no cruza campos: la consulta no puede coincidir a caballo del infinitivo y de una colocacion
        assertEquals(emptyList<EntradaVerbo>(), entradas.filtrarPorBusquedaVerbos("zzzz\u0000", Idioma.ES))
    }

    @Test
    fun `la busqueda sigue siendo sin diacriticos tambien sobre las colocaciones`() {
        val colocacion = items.first { it.idioma == Idioma.DE && it.esColocacionConNucleo() && Regex("[äöüß]").containsMatchIn(it.texto) }
        val q = normalizarBusqueda(colocacion.texto).take(10)
        val r = items.entradasVerbo(Idioma.DE).filtrarPorBusquedaVerbos(q, Idioma.ES)
        assertTrue(r.any { e -> e.colocaciones.any { it === colocacion } })
    }

    @Test
    fun `teclear no renormaliza, filtrar es una busqueda de subcadena sobre texto ya preparado`() {
        for (idioma in listOf(Idioma.DE, Idioma.EN)) {
            val entradas = items.entradasVerbo(idioma)
            repeat(3) { BuscadorVerbos(entradas, Idioma.ES).filtrar(entradas, "x") } // calentar
            val t0 = System.nanoTime()
            val buscador = BuscadorVerbos(entradas, Idioma.ES)
            val construir = (System.nanoTime() - t0) / 1e6
            val teclas = listOf("s", "st", "ste", "stel", "stell", "stelle", "stellen")
            val t1 = System.nanoTime()
            repeat(50) { for (q in teclas) buscador.filtrar(entradas, q) }
            val porTecla = (System.nanoTime() - t1) / 1e6 / (50 * teclas.size)
            println("BUSQUEDA-VERBOS $idioma: ${entradas.size} entradas, preparar ${"%.1f".format(construir)} ms (una vez), filtrar ${"%.3f".format(porTecla)} ms por tecla")
            assertTrue("$idioma: filtrar por tecla = $porTecla ms", porTecla < 20.0)
        }
    }
}
