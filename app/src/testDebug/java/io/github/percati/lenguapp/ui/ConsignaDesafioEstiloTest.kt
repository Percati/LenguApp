package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.expresionesDeLaFicha
import io.github.percati.lenguapp.presentacion.oralMinRedondeado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate

/**
 * La consigna del desafio (Ronda L, tarea 1): es PROSA de dos a cuatro renglones, no un titular. Estilo de cuerpo
 * (bodyLarge, peso medio, sin negrita), con la letra del sistema al 100 % y al 160 %. El test mira el ESTILO que
 * recibe el texto (familia tipografica, peso, color), no pixeles.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class ConsignaDesafioEstiloTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sabado = LocalDate.of(2026, 9, 12)

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private val topics: NombresI18n by lazy { parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText()) }

    private fun fichaDe(nivel: Nivel): Ficha =
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.nivel == nivel && it.mision.requisitos.size >= 2 && it.redemittel.size >= 3 }

    private fun limpio(t: String) = t.replace("*", "").replace("«", "").replace("»", "")

    private lateinit var tipografia: Typography
    private lateinit var colores: ColorScheme

    private fun mostrarDesafio(ficha: Ficha) {
        composeTestRule.setContent {
            tipografia = MaterialTheme.typography
            colores = MaterialTheme.colorScheme
            ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado, topicNombres = topics)
        }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.waitForIdle()
    }

    private fun estiloDe(fragmento: String): TextStyle {
        val nodo = composeTestRule.onNode(hasText(fragmento, substring = true)).fetchSemanticsNode()
        val resultados = mutableListOf<TextLayoutResult>()
        nodo.config.getOrNull(SemanticsActions.GetTextLayoutResult)!!.action!!(resultados)
        return resultados.first().layoutInput.style
    }

    private fun lineasDe(fragmento: String): Int {
        val nodo = composeTestRule.onNode(hasText(fragmento, substring = true)).fetchSemanticsNode()
        val resultados = mutableListOf<TextLayoutResult>()
        nodo.config.getOrNull(SemanticsActions.GetTextLayoutResult)!!.action!!(resultados)
        return resultados.first().lineCount
    }

    private fun verificarEstilo(escala: Float) {
        val ficha = fichaDe(Nivel.B2)
        mostrarDesafio(ficha)
        val consigna = limpio(ficha.mision.consigna.resolver(ficha.idioma, ficha.idioma)).take(30)
        val estilo = estiloDe(consigna)

        // la escala de letra pedida llego al texto
        val nodo = composeTestRule.onNode(hasText(consigna, substring = true)).fetchSemanticsNode()
        val resultados = mutableListOf<TextLayoutResult>()
        nodo.config.getOrNull(SemanticsActions.GetTextLayoutResult)!!.action!!(resultados)
        assertEquals(escala, resultados.first().layoutInput.density.fontScale, 0.001f)

        // estilo de cuerpo, no de titular: bodyLarge, peso medio, sin negrita
        assertEquals("tamano de bodyLarge", tipografia.bodyLarge.fontSize, estilo.fontSize)
        assertNotEquals("no es headlineSmall", tipografia.headlineSmall.fontSize, estilo.fontSize)
        assertTrue("no mas grande que titleMedium", estilo.fontSize.value <= tipografia.titleMedium.fontSize.value)
        assertEquals(FontWeight.Medium, estilo.fontWeight)
        assertNotEquals(FontWeight.Bold, estilo.fontWeight)
        assertEquals("color pleno", colores.onSurface, estilo.color)

        // El andamiaje del reto va en el idioma MOSTRADO (Ronda T): en B2 es el que se aprende, no el de la app
        // la linea de encuadre que la precede es de apoyo: mas chica que la consigna
        val lineaOral = textoRetoConsignaLinea(ficha.idioma, oralMinRedondeado(ficha.evidencia.oralMin))
        val estiloLinea = estiloDe(lineaOral.take(20))
        assertEquals(tipografia.bodyMedium.fontSize, estiloLinea.fontSize)
        assertEquals("color de apoyo", colores.onSurfaceVariant, estiloLinea.color)
        assertTrue(estiloLinea.fontSize.value < estilo.fontSize.value)

        // el bloque entero sigue siendo usable: todo esta, en orden, y la consigna ocupa pocos renglones
        val textos = composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes().flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }
        val orden = listOf(
            lineaOral, ficha.mision.consigna.resolver(ficha.idioma, ficha.idioma).take(20).replace("*", ""),
            "${etiquetaBibliotecaTema(ficha.idioma)}:", "${etiquetaRetoHabilidad(ficha.idioma)}:",
            etiquetaRetoCondiciones(ficha.idioma), etiquetaRetoFrasesTitulo(ficha.idioma),
        ).map { fragmento -> textos.indexOfFirst { it.contains(fragmento) } }
        assertTrue("faltan partes del bloque: $orden de $textos", orden.all { it >= 0 })
        assertEquals("orden del bloque", orden.sorted(), orden)
        for (r in reglasReto(ficha.idioma, ficha.nivel, oralMinRedondeado(ficha.evidencia.oralMin))) assertTrue("falta la condicion $r", textos.contains(r))
        for (f in expresionesDeLaFicha(ficha, 3)) assertTrue("falta la frase $f", textos.contains(f))
        assertTrue("la consigna en ${lineasDe(consigna)} renglones", lineasDe(consigna) <= if (escala > 1f) 14 else 8)
    }

    @Test
    fun `la consigna es prosa de cuerpo con la letra al 100 por ciento`() = verificarEstilo(1.0f)

    @Test
    @Config(fontScale = 1.6f)
    fun `la consigna sigue siendo cuerpo y el bloque usable con la letra al 160 por ciento`() = verificarEstilo(1.6f)
}
