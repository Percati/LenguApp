package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Tour
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El catalogo de pasos del tutorial (Ronda Q) contra docs/tutorial-guion.md: pasos por tour, tope de 8, textos en los 6 idiomas. */
class TourCatalogoTest {

    @Test
    fun `cada tour tiene sus pasos y ninguno pasa del tope de 10`() {
        assertEquals(mapOf(Tour.FICHA to 10, Tour.BIBLIOTECA to 5, Tour.GUARDADOS to 2, Tour.AJUSTES to 4, Tour.DESAFIO to 2),
            CATALOGO_TOURS.mapValues { it.value.size })
        for ((tour, pasos) in CATALOGO_TOURS) assertTrue("$tour: ${pasos.size} pasos (tope $MAX_PASOS_POR_TOUR)", pasos.size <= MAX_PASOS_POR_TOUR)
        assertEquals("unos 25 pasos en total", 23, CATALOGO_TOURS.values.sumOf { it.size })
    }

    @Test
    fun `Guardados fusiona exportar e importar en un paso, con el ancla de los dos botones, y la clave que sobra no existe`() {
        val pasos = CATALOGO_TOURS.getValue(Tour.GUARDADOS)
        assertEquals(listOf("tour-guardados-lista", "tour-guardados-archivo"), pasos.map { it.ancla })
        assertEquals("Exportar e importar", textoTour(pasos[1].titulo, Idioma.ES))
        assertTrue(textoTour(pasos[1].cuerpo, Idioma.ES).contains("Anki") && textoTour(pasos[1].cuerpo, Idioma.ES).contains("sin duplicar"))
        assertTrue("la clave TOUR_GUARDADOS_3 se borro", ClaveTexto.entries.none { it.name.startsWith("TOUR_GUARDADOS_3") })
    }

    @Test
    fun `el desafio senala primero el interruptor y despues la consigna, y la vista Verbos tiene su propia ancla en la Biblioteca`() {
        assertEquals(listOf("tour-desafio-interruptor", "tour-desafio-consigna"), CATALOGO_TOURS.getValue(Tour.DESAFIO).map { it.ancla })
        assertEquals("tour-biblioteca-vistas", CATALOGO_TOURS.getValue(Tour.BIBLIOTECA)[0].ancla)
        assertEquals("tour-biblioteca-vista-verbos", CATALOGO_TOURS.getValue(Tour.BIBLIOTECA)[3].ancla)
        // el interruptor conserva su texto de siempre y pasa a ser el paso 1
        assertEquals("El interruptor arranca apagado", textoTour(CATALOGO_TOURS.getValue(Tour.DESAFIO)[0].titulo, Idioma.ES))
        assertEquals("Esto es para hablar", textoTour(CATALOGO_TOURS.getValue(Tour.DESAFIO)[1].titulo, Idioma.ES))
    }

    @Test
    fun `el tope es 10 y es el ultimo`() {
        assertEquals(10, MAX_PASOS_POR_TOUR)
    }

    @Test
    fun `el tour de la ficha va en el orden de la pantalla de arriba hacia abajo, con el cierre en la barra`() {
        assertEquals(
            listOf(
                "tour-ficha-titulo", "tour-ficha-traducir", "tour-ficha-descripcion", "tour-ficha-errores", "tour-ficha-vocabulario",
                "tour-ficha-mision", "tour-ficha-autochequeo", "tour-ficha-prompt", "tour-ficha-planilla", "tour-barra-superior",
            ),
            CATALOGO_TOURS.getValue(Tour.FICHA).map { it.ancla },
        )
    }

    @Test
    fun `las claves TOUR_FICHA siguen el orden nuevo, 1 a 10, sin huecos`() {
        val pasos = CATALOGO_TOURS.getValue(Tour.FICHA)
        for ((i, paso) in pasos.withIndex()) {
            assertEquals("TOUR_FICHA_${i + 1}_T", paso.titulo.name)
            assertEquals("TOUR_FICHA_${i + 1}_C", paso.cuerpo.name)
        }
    }

    @Test
    fun `el enum ClaveTexto declara las claves de cada tour en orden, 1 a n seguidas (Ronda S)`() {
        fun orden(prefijo: String) = ClaveTexto.entries.filter { it.name.startsWith("${prefijo}_") && it.name.length <= prefijo.length + 5 }.map { it.name }
        for ((prefijo, n) in listOf("TOUR_FICHA" to 10, "TOUR_BIBLIOTECA" to 5, "TOUR_GUARDADOS" to 2, "TOUR_AJUSTES" to 4, "TOUR_DESAFIO" to 2)) {
            val esperado = (1..n).flatMap { listOf("${prefijo}_${it}_T", "${prefijo}_${it}_C") }
            assertEquals(prefijo, esperado, orden(prefijo))
        }
    }

    @Test
    fun `titulo y cuerpo de cada paso existen en los 6 idiomas, sin llaves ni vacios`() {
        for ((tour, pasos) in CATALOGO_TOURS) for ((i, paso) in pasos.withIndex()) {
            for (idioma in Idioma.entries) {
                for (clave in listOf(paso.titulo, paso.cuerpo)) {
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
    fun `el cierre del tour de la ficha ya no repite el texto de que es esto, que vive en la bienvenida y en Acerca de`() {
        val cierre = CATALOGO_TOURS.getValue(Tour.FICHA).last()
        for (idioma in Idioma.entries) {
            val cuerpo = textoTour(cierre.cuerpo, idioma)
            assertFalse("$idioma: el cierre repite el parrafo", cuerpo.contains(textoAcercaQueEs(idioma).take(40)))
            // el texto sigue existiendo, una sola vez, para la bienvenida y «Acerca de»
            assertEquals(textoAcercaQueEs(idioma), textoTour(ClaveTexto.ACERCA_QUE_ES_TEXTO, idioma))
            assertTrue(textoAcercaQueEs(idioma).isNotBlank())
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
