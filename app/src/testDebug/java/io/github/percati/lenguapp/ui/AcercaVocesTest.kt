package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * El bloque «Voces» de «Acerca de» (Ronda S, tareas 4 y 5): las dos voces, siempre y en los 6 idiomas, con la licencia de cada corpus y las
 * URL como texto (seleccionable, nunca un enlace tocable), entre «Licencias» y la version; y la linea de licencias por componente.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class AcercaVocesTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val urls = listOf(
        "https://github.com/thorstenMueller/Thorsten-Voice", "https://www.kaggle.com/datasets/roholazandie/ryanspeech",
        "https://github.com/rhasspy/piper", "https://huggingface.co/rhasspy/piper-voices",
    )

    @Test
    fun `el bloque Voces nombra las dos voces con su licencia y las cuatro URL, en los 6 idiomas`() {
        for (idioma in Idioma.entries) {
            val t = textoAcercaVoces(idioma)
            assertTrue("$idioma: Piper", t.contains("Piper (MIT)"))
            assertTrue("$idioma: voz alemana", t.contains("de_DE-thorsten-high") && t.contains("CC0 1.0"))
            assertTrue("$idioma: voz inglesa", t.contains("en_US-ryan-high") && t.contains("CC BY-NC-SA 4.0"))
            for (u in urls) assertTrue("$idioma: $u", t.contains(u))
            assertFalse("$idioma: ninguna voz que no este en la tabla", t.contains("lessac", ignoreCase = true))
            assertTrue("$idioma: titulo", etiquetaAcercaVocesTitulo(idioma).isNotBlank())
            assertFalse("$idioma: llaves sueltas", t.contains("{") || t.contains("}"))
        }
        val esperado = mapOf(Idioma.ES to "Voces", Idioma.EN to "Voices", Idioma.DE to "Stimmen", Idioma.FR to "Voix", Idioma.IT to "Voci", Idioma.PT to "Vozes")
        for ((idioma, titulo) in esperado) assertEquals(titulo, etiquetaAcercaVocesTitulo(idioma))
    }

    @Test
    fun `la linea de licencias declara el audio aparte y remite al bloque Voces, con el nombre del bloque en cada idioma`() {
        for (idioma in Idioma.entries) {
            val t = textoAcercaLicencias(idioma)
            assertTrue("$idioma: $t", t.contains("GPL-3.0") && t.contains("CC BY-SA 4.0"))
            assertTrue("$idioma: remite a «${etiquetaAcercaVocesTitulo(idioma)}»: $t", t.contains(etiquetaAcercaVocesTitulo(idioma)))
        }
        assertEquals("Código: GPL-3.0. Contenido: CC BY-SA 4.0. Audio: la licencia de cada voz, en «Voces».", textoAcercaLicencias(Idioma.ES))
        assertEquals("Code: GPL-3.0. Content: CC BY-SA 4.0. Audio: each voice's own licence, under «Voices».", textoAcercaLicencias(Idioma.EN))
        assertEquals("Code : GPL-3.0. Contenu : CC BY-SA 4.0. Audio : la licence de chaque voix, dans « Voix ».", textoAcercaLicencias(Idioma.FR))
    }

    @Test
    fun `Acerca de muestra Voces entre Licencias y la version`() {
        composeTestRule.setContent { AcercaDeScreen(idiomaInterfaz = Idioma.ES, version = "0.4.2", onVolver = {}) }
        composeTestRule.waitForIdle()
        fun top(texto: String) = composeTestRule.onNode(hasText(texto, substring = true)).fetchSemanticsNode().boundsInRoot.top
        val licencias = top("Código: GPL-3.0")
        val voces = top("Alemán: de_DE-thorsten-high")
        val version = top("Versión 0.4.2")
        assertTrue("Licencias ($licencias) < Voces ($voces) < version ($version)", licencias < voces && voces < version)
        composeTestRule.onNodeWithText("Voces").assertExists()
        composeTestRule.onNode(hasText("en_US-ryan-high", substring = true)).assertExists()
    }

    @Test
    fun `la pantalla no tiene enlaces tocables ni Intent, y el bloque va en un SelectionContainer`() {
        val f = listOf(File("src/main/java/io/github/percati/lenguapp/ui/AcercaDeScreen.kt"), File("app/src/main/java/io/github/percati/lenguapp/ui/AcercaDeScreen.kt"))
            .first { it.isFile }.readText()
        assertTrue(f.contains("SelectionContainer"))
        for (prohibido in listOf("LinkAnnotation", "ClickableText", "Intent", "ACTION_VIEW", "LocalUriHandler")) assertFalse(prohibido, f.contains(prohibido))
    }

    @Test
    fun `ya no existe el texto de creditos viejo`() {
        assertTrue(ClaveTexto.entries.none { it.name == "ACERCA_CREDITOS_TEXTO" })
    }
}
