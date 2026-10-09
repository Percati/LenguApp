package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Biblioteca (Ronda B, pieza 4): agregacion deduplicada y filtros, sobre el contenido
 * embebido real (assets/contenido/).
 */
class BibliotecaTest {

    private fun carpetaAssets(): File =
        listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val todo: List<ContenidoSemanal> by lazy {
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .sortedBy { it.name }.map { parsearContenido(it.readText()) }
    }
    private val items by lazy { construirBiblioteca(todo) }

    @Test
    fun `no hay dos items con el mismo idioma, tipo y texto`() {
        val claves = items.map { Triple(it.idioma, it.tipo, it.texto) }
        assertEquals(claves.size, claves.toSet().size)
    }

    @Test
    fun `deduplica, hay menos items que apariciones en las fichas`() {
        val fichas = todo.filterIsInstance<Ficha>()
        val apariciones = fichas.sumOf { it.vocabulario.size + it.redemittel.size }
        assertTrue(items.isNotEmpty())
        assertTrue("items=${items.size} apariciones=$apariciones", items.size < apariciones)
    }

    @Test
    fun `un item que aparece en varios niveles es uno solo con todos sus niveles`() {
        val multi = items.firstOrNull { it.niveles.size > 1 }
        if (multi != null) {
            val mismos = items.count { it.idioma == multi.idioma && it.tipo == multi.tipo && it.texto == multi.texto }
            assertEquals(1, mismos)
        }
    }

    @Test
    fun `filtrar por varios niveles es un OR y un item multinivel sale en cada uno`() {
        val base = items.first { it.tipo == TipoGuardado.VOCABULARIO }
        val multi = base.copy(texto = "zzz-multinivel", niveles = setOf(Nivel.A2, Nivel.B2))
        val lista = listOf(multi)
        fun salen(niveles: Set<Nivel>) = lista.filtrar(multi.idioma, multi.tipo, niveles, null, emptySet(), "", Idioma.ES).size
        assertEquals(1, salen(setOf(Nivel.A2)))
        assertEquals(1, salen(setOf(Nivel.B2)))
        assertEquals(1, salen(setOf(Nivel.A2, Nivel.C1)))
        assertEquals(0, salen(setOf(Nivel.C1)))
    }

    @Test
    fun `filtrar por idioma no mezcla idiomas`() {
        val idiomas = items.map { it.idioma }.toSet()
        assertTrue(idiomas.size >= 2)
        for (i in idiomas) {
            val r = items.filtrar(i, TipoGuardado.VOCABULARIO, emptySet(), null, emptySet(), "", Idioma.ES)
            assertTrue(r.isNotEmpty())
            assertTrue(r.all { it.idioma == i && it.tipo == TipoGuardado.VOCABULARIO })
        }
    }

    @Test
    fun `una expresion con dos categorias sale con cualquiera de las dos, Sin categoria solo las vacias`() {
        val base = items.first { it.tipo == TipoGuardado.EXPRESION }
        val dos = base.copy(texto = "zzz-dos", categorias = setOf("pedir", "agradecer"))
        val ninguna = base.copy(texto = "zzz-ninguna", categorias = emptySet())
        val lista = listOf(dos, ninguna)
        fun textos(cat: Set<String>) =
            lista.filtrar(base.idioma, TipoGuardado.EXPRESION, emptySet(), null, cat, "", Idioma.ES).map { it.texto }
        assertEquals(listOf("zzz-dos"), textos(setOf("pedir")))
        assertEquals(listOf("zzz-dos"), textos(setOf("agradecer")))
        assertEquals(listOf("zzz-dos"), textos(setOf("pedir", "agradecer")))
        assertEquals(listOf("zzz-ninguna"), textos(setOf(SIN_CATEGORIA)))
        assertEquals(listOf("zzz-dos", "zzz-ninguna"), textos(emptySet()))
    }

    @Test
    fun `el filtro de topic aplica al vocabulario`() {
        val v = items.first { it.tipo == TipoGuardado.VOCABULARIO }
        val topic = v.topics.first()
        val r = items.filtrar(v.idioma, TipoGuardado.VOCABULARIO, emptySet(), topic, emptySet(), "", Idioma.ES)
        assertTrue(r.isNotEmpty())
        assertTrue(r.all { topic in it.topics })
    }

    @Test
    fun `la busqueda ignora mayusculas y encuentra por el texto aprendido`() {
        val v = items.first { it.tipo == TipoGuardado.VOCABULARIO && it.texto.length >= 4 }
        val q = v.texto.substring(1, 4).uppercase()
        val r = items.filtrar(v.idioma, TipoGuardado.VOCABULARIO, emptySet(), null, emptySet(), q, Idioma.ES)
        assertTrue(r.any { it.texto == v.texto })
        assertFalse(items.filtrar(v.idioma, TipoGuardado.VOCABULARIO, emptySet(), null, emptySet(), "qqqq-no-existe-xyz", Idioma.ES).isNotEmpty())
    }

    @Test
    fun `la clave de la estrella coincide con la de la ficha`() {
        val v = items.first()
        assertEquals("${v.tipo}|${v.texto}", v.claveGuardado())
    }

    /**
     * Contra los assets REALES (no fichas de prueba): si esto fallara de
     * nuevo por un desfasaje de assets/contenido con los nucleos (ver
     * verificarAssetsContenidoActualizados en app/build.gradle.kts), es acá
     * donde se notaría en el filtro que usa la Biblioteca, no solo en que el
     * JSON compila.
     */
    @Test
    fun `al menos una expresion de EN-C1 tiene categoriasUso no vacio`() {
        val expresionesEnC1 = items.filter { it.idioma == Idioma.EN && it.tipo == TipoGuardado.EXPRESION && Nivel.C1 in it.niveles }
        assertTrue("no hay expresiones EN-C1 en el contenido embebido", expresionesEnC1.isNotEmpty())
        assertTrue("ninguna expresion EN-C1 tiene categoriasUso", expresionesEnC1.any { it.categorias.isNotEmpty() })
    }

    @Test
    fun `filtrar expresiones EN por la categoria Dar razones devuelve resultados`() {
        val r = items.filtrar(Idioma.EN, TipoGuardado.EXPRESION, emptySet(), null, setOf("Dar razones"), "", Idioma.ES)
        assertTrue("filtrar por 'Dar razones' no devolvio nada", r.isNotEmpty())
        assertTrue(r.all { "Dar razones" in it.categorias })
    }
}
