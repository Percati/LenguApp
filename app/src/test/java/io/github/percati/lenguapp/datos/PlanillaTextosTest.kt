package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Textos fijos de la planilla (propuesta para revision de Fer): completos en los 6 idiomas aprendibles. */
class PlanillaTextosTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    @Test
    fun `los textos de la planilla estan completos en los seis idiomas`() {
        val textos = parsearPlanillaTextos(File(assets(), "plantillas/planilla-profesor.json").readText())
        assertEquals(Idioma.entries.toSet(), textos.keys)
        for ((idioma, t) in textos) {
            val simples = listOf(
                t.titulo, t.nivel, t.objetivo, t.tema, t.enfoque, t.gramatica, t.expresionesObjetivo,
                t.expresionesReparacion, t.situaciones, t.situacionCotidiana, t.situacionProfesional,
                t.giro, t.giroTexto, t.criterios, t.recordatorio, t.semana,
            )
            assertTrue("$idioma tiene un texto vacio", simples.all { it.isNotBlank() })
            assertEquals("$idioma: 2 frases de reparacion", 2, t.frasesReparacion.size)
            assertEquals("$idioma: 3 criterios de feedback", 3, t.criteriosLista.size)
            assertTrue(t.frasesReparacion.all { it.isNotBlank() } && t.criteriosLista.all { it.isNotBlank() })
        }
    }

    @Test
    fun `los nombres de topic generados en el build cubren T01 a T14`() {
        val nombres = parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText())
        assertEquals((1..14).map { "T%02d".format(it) }.toSet(), nombres.claves)
    }
}
