package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.VocabularioItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * `claseVerbo` (Ronda G, tarea 0): el parser estricto de la app lee el campo nuevo de
 * `vocabulario[]` en fichas reales, y un item sin el campo sigue cargando (lista vacia).
 */
class ClaseVerboTest {

    private fun carpeta(): File =
        listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }

    private fun ficha(id: String): Ficha = parsearContenido(File(carpeta(), "$id.json").readText()) as Ficha

    private fun item(f: Ficha, texto: String): VocabularioItem = f.vocabulario.first { it.item == texto }

    @Test
    fun `una ficha alemana real con un verbo trennbar lo lee`() {
        assertEquals(listOf("trennbar"), item(ficha("DE-F01-A2-2026-1"), "aussteigen").claseVerbo)
    }

    @Test
    fun `una ficha inglesa real con un phrasal separable lo lee`() {
        assertEquals(listOf("phrasal-separable"), item(ficha("EN-F01-B1-2027-2"), "to pay back").claseVerbo)
    }

    @Test
    fun `un verbo puede tener varias clases y simple va solo`() {
        assertEquals(listOf("reflexivo", "mit-Praposition"), item(ficha("DE-F01-A2-2027-2"), "sich beschweren").claseVerbo)
        assertEquals(listOf("simple"), item(ficha("DE-F01-A2-2027-1"), "arbeiten").claseVerbo)
        assertEquals(listOf("mit-Dativ"), item(ficha("DE-F01-A2-2027-3"), "helfen").claseVerbo)
    }

    @Test
    fun `un item que no es verbo no trae clase y la lista queda vacia`() {
        val sinClase = ficha("DE-F01-A2-2026-1").vocabulario.filter { it.claseVerbo.isEmpty() }
        assertTrue(sinClase.isNotEmpty())
    }

    @Test
    fun `todas las clases de los assets pertenecen a la taxonomia cerrada`() {
        val permitidas = setOf(
            "simple", "reflexivo", "trennbar", "mit-Praposition", "mit-Dativ",
            "phrasal-separable", "phrasal-inseparable", "verbo-preposicion",
        )
        val usadas = carpeta().listFiles { f -> f.extension == "json" }!!
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .flatMap { f -> f.vocabulario.flatMap { it.claseVerbo } }.toSet()
        assertTrue(usadas.isNotEmpty())
        assertEquals(emptySet<String>(), usadas - permitidas)
    }
}
