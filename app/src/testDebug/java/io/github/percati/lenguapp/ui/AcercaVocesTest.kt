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
 * (Ronda T, tarea 2) Las URL se miden con graficos NATIVE de Robolectric: con el modo legacy el texto mide casi cero y no demuestra nada.
 * Resultado: ya saltan bien (parten en la «/» y en el «-», nunca se cortan ni se salen), asi que no se toco el bloque.
 *
 * El bloque «Voces» de «Acerca de» (Ronda S, tareas 4 y 5): las dos voces, siempre y en los 6 idiomas, con la licencia de cada corpus y las
 * URL como texto (seleccionable, nunca un enlace tocable), entre «Licencias» y la version; y la linea de licencias por componente.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
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

    // --- Ronda T, T2: las URL del bloque en una pantalla angosta ---

    private fun medirVoces(idioma: Idioma): List<String> {
        composeTestRule.setContent { AcercaDeScreen(idiomaInterfaz = idioma, version = "0.4.3", onVolver = {}) }
        composeTestRule.waitForIdle()
        val nodo = composeTestRule.onNode(hasText(textoAcercaVoces(idioma).lines().first(), substring = true)).fetchSemanticsNode()
        val resultados = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
        nodo.config[androidx.compose.ui.semantics.SemanticsActions.GetTextLayoutResult].action?.invoke(resultados)
        val r = resultados.single()
        val ancho = r.layoutInput.constraints.maxWidth.toFloat()
        val problemas = mutableListOf<String>()
        println("MEDIDA-URL ${idioma.name} anchoDisponible=$ancho nodo=${nodo.size.width} lineas=${r.lineCount} desborda=${r.didOverflowWidth}")
        if (r.didOverflowWidth) problemas += "didOverflowWidth"
        for (i in 0 until r.lineCount) if (r.getLineRight(i) > ancho + 0.5f) problemas += "linea $i: ${r.getLineRight(i)} > $ancho"
        if (nodo.size.width > ancho + 0.5f) problemas += "el nodo es mas ancho que lo disponible"
        // y todo el texto sigue ahi, sin puntos suspensivos
        assertEquals(textoAcercaVoces(idioma), r.layoutInput.text.text)
        assertFalse("sin truncar", r.hasVisualOverflow && r.layoutInput.maxLines != Int.MAX_VALUE)
        return problemas
    }

    @Test
    @Config(qualifiers = "w360dp-h4000dp")
    fun `a 360 dp las URL largas del bloque Voces saltan de linea y no se pasan del ancho, en espanol`() {
        val p = medirVoces(Idioma.ES)
        assertTrue("$p", p.isEmpty())
    }

    @Test
    @Config(qualifiers = "w360dp-h4000dp")
    fun `a 360 dp las URL largas del bloque Voces saltan de linea y no se pasan del ancho, en aleman`() {
        val p = medirVoces(Idioma.DE)
        assertTrue("$p", p.isEmpty())
    }

    @Test
    @Config(qualifiers = "w320dp-h4000dp", fontScale = 1.3f)
    fun `a 320 dp con letra al 130 por ciento las URL tampoco se pasan del ancho`() {
        val p = medirVoces(Idioma.ES)
        assertTrue("$p", p.isEmpty())
    }
}
