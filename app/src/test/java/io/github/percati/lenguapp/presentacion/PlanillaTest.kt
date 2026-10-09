package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.PlanillaTextos
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearPlanillaTextos
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.SemanaEspecial
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Contenido de la planilla del profesor (Ronda B, pieza 3): reutiliza la ficha, textos fijos en el idioma que se aprende. */
class PlanillaTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private fun contenido(id: String) = parsearContenido(File(assets(), "contenido/$id.json").readText())
    private fun textos(): Map<Idioma, PlanillaTextos> =
        parsearPlanillaTextos(File(assets(), "plantillas/planilla-profesor.json").readText())

    private fun titulos(p: PlanillaContenido) = p.secciones.map { it.titulo }

    @Test
    fun `una ficha A2 alemana arma la planilla en aleman con las secciones pedidas`() {
        val ficha = contenido("DE-F01-A2-2026-1") as Ficha
        val t = textos().getValue(Idioma.DE)
        val p = construirPlanilla(ficha, t, nombreTopic = "Trabajo y carrera")

        assertEquals(t.titulo, p.titulo)
        assertTrue(p.encabezado.contains("A2"))
        val titulos = titulos(p)
        assertTrue(titulos.containsAll(listOf(t.objetivo, t.tema, t.expresionesObjetivo, t.expresionesReparacion, t.situaciones, t.giro, t.criterios)))
        // objetivo = la consigna de la ficha, en el idioma que se aprende (no en el de app)
        val objetivo = p.secciones.first { it.titulo == t.objetivo }.lineas.single()
        assertEquals(ficha.mision.consigna.porIdioma.getValue("de").replace("*", ""), objetivo)
        // topic: nombre legible + enfoque de la aparicion
        val tema = p.secciones.first { it.titulo == t.tema }.lineas
        assertEquals("Trabajo y carrera", tema.first())
        assertEquals(ficha.subtitulo.porIdioma.getValue("de").replace("*", ""), tema.last())
    }

    @Test
    fun `como mucho 3 expresiones objetivo, 2 frases de reparacion y 3 criterios`() {
        val ficha = contenido("EN-F01-C1-2026-1") as Ficha
        val t = textos().getValue(Idioma.EN)
        val p = construirPlanilla(ficha, t, nombreTopic = null)

        assertEquals(3, p.secciones.first { it.titulo == t.expresionesObjetivo }.lineas.size)
        assertTrue(p.secciones.first { it.titulo == t.expresionesObjetivo }.lineas.first().startsWith(ficha.redemittel.first().expresion))
        assertEquals(t.frasesReparacion, p.secciones.first { it.titulo == t.expresionesReparacion }.lineas)
        assertEquals(3, p.secciones.first { it.titulo == t.criterios }.lineas.size)
    }

    @Test
    fun `sin nombre de topic se muestra el id del topic, no queda vacio`() {
        val ficha = contenido("EN-F01-C1-2026-1") as Ficha
        val t = textos().getValue(Idioma.EN)
        val tema = construirPlanilla(ficha, t, nombreTopic = null).secciones.first { it.titulo == t.tema }.lineas
        assertEquals(ficha.topicId, tema.first())
    }

    @Test
    fun `el giro inesperado es una instruccion generica, sin preguntas escritas`() {
        val t = textos().getValue(Idioma.EN)
        val p = construirPlanilla(contenido("EN-F01-C1-2026-1"), t, null)
        val giro = p.secciones.first { it.titulo == t.giro }
        assertEquals(listOf(t.giroTexto), giro.lineas)
        assertFalse(giro.lineas.any { it.contains("?") })
    }

    @Test
    fun `la planilla termina con el recordatorio de no reescribir lo que dice el alumno`() {
        val t = textos().getValue(Idioma.DE)
        val p = construirPlanilla(contenido("DE-F01-A2-2026-1"), t, null)
        assertEquals(listOf(t.recordatorio), p.secciones.last().lineas)
    }

    @Test
    fun `una semana de repaso tambien tiene planilla, con el objetivo de su consigna y sin topic`() {
        val especial = contenido("REVIEW-DE-B2-2026-S40") as SemanaEspecial
        val t = textos().getValue(Idioma.DE)
        val p = construirPlanilla(especial, t, null)
        assertTrue(p.encabezado.contains("${especial.semana}"))
        assertTrue(titulos(p).contains(t.objetivo))
        assertFalse(titulos(p).contains(t.tema))
        assertTrue(titulos(p).contains(t.criterios))
    }

    @Test
    fun `cada idioma aprendible arma su planilla con sus propios textos fijos`() {
        val ficha = contenido("EN-F01-C1-2026-1") as Ficha
        for ((idioma, t) in textos()) {
            val p = construirPlanilla(ficha, t, null)
            assertEquals("$idioma", t.titulo, p.titulo)
            assertTrue(p.secciones.size >= 8)
        }
    }
}
