package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearPlantillasPromptVoz
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.TextoBilingue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Prompt de conversacion de voz (Ronda B, pieza 2): se arma desde la ficha en el
 * idioma que se aprende, con la plantilla real de proyecto/contenido/plantillas/
 * (copiada a assets/plantillas/ por copiarPlantillasAssets antes de testear).
 */
class PromptVozTest {

    private fun carpetaAssets(): File =
        listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private fun plantillas(): Map<String, String> =
        parsearPlantillasPromptVoz(File(carpetaAssets(), "plantillas/prompt-voz.json").readText())

    private fun ficha(id: String): Ficha =
        parsearContenido(File(carpetaAssets(), "contenido/$id.json").readText()) as Ficha

    @Test
    fun `las plantillas reales cargan y hoy son solo ingles B2 y C1`() {
        assertEquals(setOf("en-B2", "en-C1"), plantillas().keys)
    }

    @Test
    fun `una ficha EN C1 real resuelve todos los placeholders`() {
        val f = ficha("EN-F01-C1-2026-1")
        val prompt = promptVozPara(f, plantillas())!!
        assertFalse("quedaron llaves sin resolver", Regex("\\{[A-Za-z]+\\}").containsMatchIn(prompt))
        assertTrue(prompt.contains("Skill: " + (f.titulo.plano ?: f.titulo.porIdioma.getValue("en"))))
        assertTrue(prompt.contains("LEVEL: C1"))
        assertTrue(prompt.contains("Mission: " + (f.mision.consigna.plano ?: "")))
        assertTrue(prompt.contains(f.mision.requisitos.joinToString("; ") { it.plano ?: "" }))
        assertTrue(prompt.contains(f.redemittel.first().expresion))
    }

    @Test
    fun `una ficha EN B2 real usa la plantilla B2 y como mucho 6 expresiones y 6 palabras nucleo`() {
        val f = ficha("EN-F11-B2-2026-1")
        val prompt = promptVozPara(f, plantillas())!!
        assertTrue(prompt.contains("LEVEL: B2"))
        val lineaExpresiones = prompt.lines().first { it.startsWith("Target expressions") }
        val esperadas = f.redemittel.take(6).map { it.expresion }
        esperadas.forEach { assertTrue("falta $it", lineaExpresiones.contains(it)) }
        f.redemittel.drop(6).forEach { assertFalse(lineaExpresiones.contains(it.expresion)) }
        val lineaVocab = prompt.lines().first { it.startsWith("Target vocabulary") }
        val nucleo = f.vocabulario.filter { it.prioridad == io.github.percati.lenguapp.modelo.Prioridad.NUCLEO }.take(6)
        nucleo.forEach { assertTrue(lineaVocab.contains(it.item)) }
    }

    @Test
    fun `un placeholder sin dato quita la linea entera, no deja llaves ni renglon vacio`() {
        val f = ficha("EN-F01-C1-2026-1")
        val sinRequisitos = f.copy(mision = f.mision.copy(requisitos = emptyList()))
        val prompt = promptVozPara(sinRequisitos, plantillas())!!
        assertFalse(prompt.lines().any { it.startsWith("Requirements:") })
        assertFalse(prompt.contains("{"))
        assertTrue(prompt.lines().any { it.startsWith("Mission:") })

        val sinExpresiones = f.copy(redemittel = emptyList())
        assertFalse(promptVozPara(sinExpresiones, plantillas())!!.lines().any { it.startsWith("Target expressions") })
    }

    @Test
    fun `oralMin se redondea, y si falta (cero) cae a 6`() {
        val f = ficha("EN-F01-C1-2026-1")
        val con = promptVozPara(f.copy(evidencia = f.evidencia.copy(oralMin = 7.6)), plantillas())!!
        assertTrue(con.contains("about 8 minutes"))
        val sin = promptVozPara(f.copy(evidencia = f.evidencia.copy(oralMin = 0.0)), plantillas())!!
        assertTrue(sin.contains("about 6 minutes"))
    }

    @Test
    fun `sin plantilla para el par no hay prompt -- alemán B2 no lo tiene`() {
        assertNull(promptVozPara(ficha("DE-G01-B2-2026-1"), plantillas()))
    }

    @Test
    fun `la clave de plantilla sale del idioma y el nivel de la ficha, sin un if por idioma`() {
        val f = ficha("EN-F01-C1-2026-1")
        assertEquals("en-C1", clavePlantillaVoz(f))
        val inventada = mapOf("en-C1" to "Hola {titulo}")
        val prompt = promptVozPara(f, inventada)
        assertNotNull(prompt)
        assertEquals("Hola " + (f.titulo.plano ?: f.titulo.porIdioma["en"]), prompt)
    }

    @Test
    fun `las claves de documentacion no son plantillas`() {
        val json = """{"plantillas": {"_nota": "x", "en-B2": "t"}}"""
        assertEquals(setOf("en-B2"), parsearPlantillasPromptVoz(json).keys)
    }

    @Test
    fun `resuelve desde el idioma que se aprende aunque la ficha traiga objetos bilingues`() {
        val f = ficha("EN-F01-C1-2026-1")
        val bil = f.copy(titulo = TextoBilingue(porIdioma = mapOf("en" to "Original title", "es" to "Titulo en espanol")))
        val prompt = promptVozPara(bil, mapOf("en-C1" to "{titulo}"))
        assertEquals("Original title", prompt)
    }
}
