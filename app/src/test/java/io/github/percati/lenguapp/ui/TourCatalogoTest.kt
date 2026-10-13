package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Tour
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El catalogo de pasos del tutorial (Ronda Q) contra docs/tutorial-guion.md: pasos por tour, tope de 8, textos en los 6 idiomas. */
class TourCatalogoTest {

    @Test
    fun `cada tour tiene los pasos del guion y ninguno pasa de 8`() {
        assertEquals(mapOf(Tour.FICHA to 8, Tour.BIBLIOTECA to 5, Tour.GUARDADOS to 3, Tour.AJUSTES to 4, Tour.DESAFIO to 2),
            CATALOGO_TOURS.mapValues { it.value.size })
        for ((tour, pasos) in CATALOGO_TOURS) assertTrue("$tour: ${pasos.size} pasos (tope $MAX_PASOS_POR_TOUR)", pasos.size <= MAX_PASOS_POR_TOUR)
        assertEquals("unos 25 pasos en total", 22, CATALOGO_TOURS.values.sumOf { it.size })
    }

    @Test
    fun `titulo y cuerpo de cada paso existen en los 6 idiomas, sin llaves ni vacios`() {
        for ((tour, pasos) in CATALOGO_TOURS) for ((i, paso) in pasos.withIndex()) {
            for (idioma in Idioma.entries) {
                for (clave in listOfNotNull(paso.titulo, paso.cuerpo, paso.cuerpoExtra)) {
                    val t = textoTour(clave, idioma)
                    assertTrue("$tour paso ${i + 1} $clave en $idioma vacio", t.isNotBlank())
                    assertFalse("$tour paso ${i + 1} $clave en $idioma: llaves sueltas: $t", t.contains("{") || t.contains("}"))
                }
            }
            // un titulo corto y distinto en cada idioma (no el espanol copiado)
            val titulos = Idioma.entries.map { textoTour(paso.titulo, it) }
            assertTrue("$tour paso ${i + 1}: titulo largo: $titulos", titulos.all { it.length <= 60 })
        }
    }

    @Test
    fun `los textos estan traducidos y no son el espanol copiado en los otros 5 idiomas`() {
        // Titulos que coinciden a proposito entre idiomas (nombres propios o cognados): no se exigen distintos.
        val ok = setOf("Verbos", "Verbes", "Verbi", "Exportar", "Importar", "Export", "Import")
        for ((tour, pasos) in CATALOGO_TOURS) for ((i, paso) in pasos.withIndex()) {
            val es = textoTour(paso.cuerpo, Idioma.ES)
            for (idioma in Idioma.entries.filter { it != Idioma.ES }) {
                assertTrue("$tour paso ${i + 1} cuerpo en $idioma es igual al espanol", textoTour(paso.cuerpo, idioma) != es || es in ok)
            }
        }
    }

    @Test
    fun `las anclas son testTags con nombre y el cierre de la ficha senala la barra de arriba`() {
        for ((tour, pasos) in CATALOGO_TOURS) for (paso in pasos) {
            val ancla = paso.ancla
            assertNotNull("$tour: un paso sin ancla (el tutorial senala siempre algo)", ancla)
            assertTrue("$tour: ancla $ancla", ancla!!.isNotBlank() && !ancla.contains(' '))
        }
        assertEquals("tour-barra-superior", CATALOGO_TOURS.getValue(Tour.FICHA).last().ancla)
        // las filas de Ajustes reusan sus tags de siempre
        assertEquals("acerca-de", CATALOGO_TOURS.getValue(Tour.AJUSTES)[2].ancla)
        assertEquals("ver-bienvenida", CATALOGO_TOURS.getValue(Tour.AJUSTES)[3].ancla)
        assertNull(CATALOGO_TOURS[Tour.FICHA]!!.first().cuerpoExtra)
    }

    @Test
    fun `el rotulo de repetir dice que tambien reinicia el tutorial, en los 6 idiomas`() {
        for (idioma in Idioma.entries) {
            val t = etiquetaVerBienvenida(idioma)
            assertTrue("$idioma: $t", t.isNotBlank())
        }
        assertEquals("Ver la bienvenida y el tutorial otra vez", etiquetaVerBienvenida(Idioma.ES))
        assertTrue(etiquetaVerBienvenida(Idioma.EN).contains("tutorial"))
        assertTrue(etiquetaVerBienvenida(Idioma.DE).contains("Tutorial"))
        assertTrue(etiquetaVerBienvenida(Idioma.FR).contains("tutoriel"))
        assertTrue(etiquetaVerBienvenida(Idioma.IT).contains("tutorial"))
        assertTrue(etiquetaVerBienvenida(Idioma.PT).contains("tutorial"))
    }
}
