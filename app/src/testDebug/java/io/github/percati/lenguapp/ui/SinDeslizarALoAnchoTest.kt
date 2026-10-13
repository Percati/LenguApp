package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
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
 * Nada de deslizar a lo ancho (Ronda P, tarea 2): en Ajustes y en la bienvenida todas las opciones de cada grupo se ven a la vez,
 * repartidas en filas (FlowRow). A 320 dp de ancho, con la letra al 100 % y al 160 %, en los 6 idiomas: ninguna opcion queda fuera
 * de la pantalla y ningun nodo se desplaza en horizontal.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w320dp-h4000dp")
class SinDeslizarALoAnchoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // El ancho real de la pantalla de la prueba (320 dp salvo que el test lo cambie).
    private val anchoPantalla: Float get() = composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.right

    private val opciones = SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)

    private fun nodosDeOpcion(): List<SemanticsNode> =
        composeTestRule.onAllNodes(opciones).fetchSemanticsNodes()

    private fun verificarSinDesbordar(contexto: String) {
        for (nodo in nodosDeOpcion()) {
            val b = nodo.boundsInRoot
            assertTrue("$contexto: una opcion queda fuera de la pantalla a la izquierda ($b)", b.left >= -0.5f)
            assertTrue("$contexto: una opcion queda fuera de la pantalla a la derecha ($b)", b.right <= anchoPantalla + 0.5f)
        }
        val conDeslizamiento = composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.HorizontalScrollAxisRange))
            .fetchSemanticsNodes()
        assertTrue("$contexto: hay ${conDeslizamiento.size} nodo(s) que se desplazan en horizontal", conDeslizamiento.isEmpty())
    }

    private fun recorrerLosSeisIdiomas(mostrar: @androidx.compose.runtime.Composable (Idioma) -> Int) {
        var idioma by mutableStateOf(Idioma.ES)
        var esperadas = 0
        composeTestRule.setContent { esperadas = mostrar(idioma) }
        for (i in Idioma.entries) {
            composeTestRule.runOnIdle { idioma = i }
            composeTestRule.waitForIdle()
            assertEquals("$i: cantidad de opciones", esperadas, nodosDeOpcion().size)
            verificarSinDesbordar("$i")
        }
    }

    @androidx.compose.runtime.Composable
    private fun ajustes(idioma: Idioma) = AjustesScreenParaTest(idioma)

    @androidx.compose.runtime.Composable
    private fun AjustesScreenParaTest(idioma: Idioma): Int {
        AjustesScreen(
            ajustes = Ajustes(idiomaInterfaz = idioma, idiomaBase = idioma),
            idiomasConContenido = Idioma.entries.toSet(),
            nivelesConContenido = Idioma.entries.associateWith { Nivel.entries.toSet() },
            idiomaAplicacionEfectivo = idioma, onAjustesCambiados = {}, onVolver = {},
        )
        // idiomas que se practican (6) + idioma de la app (sistema + 6) + temas + modo (3) + tipografia (3) + velocidad (4)
        return 6 + 7 + familiasOfrecidas().size + 3 + 3 + 4
    }

    @androidx.compose.runtime.Composable
    private fun BienvenidaParaTest(idioma: Idioma): Int {
        BienvenidaScreen(
            ajustes = Ajustes(idiomaInterfaz = idioma, idiomaBase = idioma, bienvenidaCompletada = false),
            idiomasConContenido = Idioma.entries.toSet(),
            nivelesConContenido = Idioma.entries.associateWith { Nivel.entries.toSet() },
            idiomaAplicacionEfectivo = idioma, onAjustesCambiados = {}, onEmpezar = {},
            yaEligioIdioma = true,
        )
        // idioma de la app (sistema + 6) + idiomas que se practican (6)
        return 7 + 6
    }

    @Test
    fun `Ajustes a 320 dp y letra al 100 por ciento muestra todas las opciones, en los 6 idiomas`() =
        recorrerLosSeisIdiomas { idioma -> ajustes(idioma) }

    @Test
    @Config(fontScale = 1.6f)
    fun `Ajustes a 320 dp y letra al 160 por ciento muestra todas las opciones, en los 6 idiomas`() =
        recorrerLosSeisIdiomas { idioma -> ajustes(idioma) }

    @Test
    fun `la bienvenida a 320 dp y letra al 100 por ciento muestra todos los idiomas, en los 6 idiomas`() =
        recorrerLosSeisIdiomas { idioma -> BienvenidaParaTest(idioma) }

    @Test
    @Config(fontScale = 1.6f)
    fun `la bienvenida a 320 dp y letra al 160 por ciento muestra todos los idiomas, en los 6 idiomas`() =
        recorrerLosSeisIdiomas { idioma -> BienvenidaParaTest(idioma) }

    // Robolectric mide el texto con una fuente sustituta MUY angosta (un chip de «Español» mide ~40 dp, en un telefono real unos 100),
    // asi que a 320 dp casi todo entra en una fila aunque el codigo fuera malo. Para que la prueba de «nada fuera de pantalla»
    // apriete de verdad se repite en una pantalla de 140 dp, donde cada grupo TIENE que repartirse en varias filas.
    @Test
    @Config(qualifiers = "w140dp-h8000dp", fontScale = 1.6f)
    fun `en una pantalla muy angosta Ajustes y la bienvenida reparten las opciones en filas, sin salirse`() {
        recorrerLosSeisIdiomas { idioma -> ajustes(idioma) }
    }

    @Test
    @Config(qualifiers = "w140dp-h8000dp", fontScale = 1.6f)
    fun `en una pantalla muy angosta la bienvenida reparte los idiomas en filas, sin salirse`() {
        recorrerLosSeisIdiomas { idioma -> BienvenidaParaTest(idioma) }
    }

    @Test
    fun `antes de elegir el idioma, la bienvenida ya muestra los 7 chips a la vista`() {
        composeTestRule.setContent {
            BienvenidaScreen(
                ajustes = Ajustes(bienvenidaCompletada = false), idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2)), idiomaAplicacionEfectivo = Idioma.ES,
                onAjustesCambiados = {}, onEmpezar = {},
            )
        }
        assertEquals(7, nodosDeOpcion().size) // Idioma del sistema + los 6
        verificarSinDesbordar("bienvenida sin idioma elegido")
    }

    @Test
    fun `el codigo de Ajustes y de la bienvenida no usa horizontalScroll`() {
        val fuentes = listOf("AjustesScreen.kt", "BienvenidaScreen.kt").map { nombre ->
            val f = listOf(File("src/main/java/io/github/percati/lenguapp/ui/$nombre"), File("app/src/main/java/io/github/percati/lenguapp/ui/$nombre")).first { it.isFile }
            nombre to f.readText()
        }
        for ((nombre, texto) in fuentes) {
            assertFalse("$nombre usa horizontalScroll: una opcion fuera de pantalla es una opcion que nadie elige", texto.contains("horizontalScroll"))
            assertTrue("$nombre debe acomodar las opciones con FlowRow", texto.contains("FlowRow("))
        }
    }
}
