package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Preparacion de la Biblioteca para una tercera vista (Ronda G, tarea 6): el selector de vistas aguanta
 * TRES en pantalla angosta, con la letra al 160 % y en los 6 idiomas (aleman y portugues, los mas largos);
 * y el grupo de chips es reutilizable (se desplaza o se colapsa cuando hay muchos).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w320dp-h3000dp")
class SelectorVistasYChipsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // La tercera vista (todavia sin implementar) se llamara "Verbos" en cada idioma.
    private val verbos = mapOf(
        Idioma.ES to "Verbos", Idioma.EN to "Verbs", Idioma.DE to "Verben",
        Idioma.FR to "Verbes", Idioma.IT to "Verbi", Idioma.PT to "Verbos",
    )

    private fun probar(idioma: Idioma) {
        val vistas = listOf(
            OpcionFiltro("LEXICO", etiquetaBibliotecaVistaLexico(idioma)),
            OpcionFiltro("HABILIDADES", etiquetaBibliotecaVistaHabilidades(idioma)),
            OpcionFiltro("VERBOS", verbos.getValue(idioma)),
        )
        var elegida by mutableStateOf("LEXICO")
        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(2f, 1.6f)) {
                TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) {
                    SelectorDeVistas(vistas, elegida, onElegir = { elegida = it })
                }
            }
        }
        // las tres etiquetas existen y se puede llegar a cada una desplazando la fila
        for (v in vistas) {
            composeTestRule.onNodeWithText(v.etiqueta).performScrollTo().performClick()
            assertEquals("${idioma}: ${v.etiqueta}", v.clave, elegida)
        }
        // una sola fila: los tres chips tienen la misma altura y arriba igual (no se rompe en dos lineas)
        val tops = vistas.map { v ->
            composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
                .fetchSemanticsNodes().first { n -> n.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text == v.etiqueta }.boundsInRoot.top
        }
        assertEquals("${idioma}: los chips no estan en una sola fila: $tops", 1, tops.map { it.toInt() }.toSet().size)
    }

    @Test fun `tres vistas en espanol con la letra al 160`() = probar(Idioma.ES)
    @Test fun `tres vistas en ingles con la letra al 160`() = probar(Idioma.EN)
    @Test fun `tres vistas en aleman con la letra al 160`() = probar(Idioma.DE)
    @Test fun `tres vistas en frances con la letra al 160`() = probar(Idioma.FR)
    @Test fun `tres vistas en italiano con la letra al 160`() = probar(Idioma.IT)
    @Test fun `tres vistas en portugues con la letra al 160`() = probar(Idioma.PT)

    // --- GrupoChips ---

    @Test
    fun `GrupoChips desplazable muestra una sola fila y todos los chips son alcanzables`() {
        val opciones = (1..25).map { OpcionFiltro("k$it", "Opcion numero $it") }
        var tocada = ""
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) {
                GrupoChips(opciones, estaElegida = { it.clave == tocada }, onElegir = { tocada = it.clave })
            }
        }
        composeTestRule.onNodeWithText("Opcion numero 25").performScrollTo().performClick()
        assertEquals("k25", tocada)
    }

    @Test
    fun `GrupoChips ajustado y colapsable muestra ver mas, y expande y colapsa`() {
        val opciones = (1..40).map { OpcionFiltro("k$it", "Chip $it") }
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) {
                GrupoChips(
                    opciones, estaElegida = { false }, onElegir = {},
                    disposicion = DisposicionChips.AJUSTADA, maxLineasColapsado = 2,
                    etiquetaVerMas = "ver mas", etiquetaVerMenos = "ver menos",
                )
            }
        }
        composeTestRule.onNodeWithText("ver mas").assertExists()
        // colapsado: los chips de sobra no se ven (pueden estar compuestos pero no se colocan)
        composeTestRule.onNode(hasText("Chip 40")).assertIsNotDisplayed()
        composeTestRule.onNodeWithText("ver mas").performClick()
        composeTestRule.onNodeWithText("ver menos").assertExists()
        composeTestRule.onNode(hasText("Chip 40")).assertIsDisplayed()
        composeTestRule.onNodeWithText("ver menos").performClick()
        composeTestRule.onNodeWithText("ver mas").assertExists()
    }

    @Test
    fun `GrupoChips ajustado sin colapsar muestra todos`() {
        val opciones = (1..40).map { OpcionFiltro("k$it", "Chip $it") }
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) {
                GrupoChips(opciones, estaElegida = { false }, onElegir = {}, disposicion = DisposicionChips.AJUSTADA)
            }
        }
        assertTrue((1..40).all { composeTestRule.onAllNodesWithText("Chip $it").fetchSemanticsNodes().isNotEmpty() })
    }

    // --- FilaPalabra: el espacio extra para una linea mas ---

    @Test
    fun `FilaPalabra sin extra no cambia nada y con extra muestra la linea adicional`() {
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) {
                androidx.compose.foundation.layout.Column {
                    FilaPalabra("aussteigen", "bajarse", null, Idioma.DE, Idioma.ES, false, "t1", null, {})
                    FilaPalabra("helfen", "ayudar", null, Idioma.DE, Idioma.ES, false, "t2", null, {}, extra = { Text("mit Dativ") })
                }
            }
        }
        composeTestRule.onNodeWithText("aussteigen").assertExists()
        composeTestRule.onNodeWithText("helfen").assertExists()
        composeTestRule.onAllNodesWithText("mit Dativ").let { assertEquals(1, it.fetchSemanticsNodes().size) }
    }
}
