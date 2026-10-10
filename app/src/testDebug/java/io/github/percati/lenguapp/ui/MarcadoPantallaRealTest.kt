package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Marcado « » (Ronda D, tarea 1b): fichas reales (EN C1, DE B2, DE A2) con el switch de
 * traduccion en ON (default) y en OFF: ningun texto de la pantalla muestra asteriscos
 * literales ni « » dobles, y las citas ajenas escritas a mano siguen entre « ».
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class MarcadoPantallaRealTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun ficha(id: String): Ficha {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        return parsearContenido(File(carpeta, "contenido/$id.json").readText()) as Ficha
    }

    private fun textosEnPantalla(): List<String> =
        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }

    private fun verificar(id: String) {
        val ficha = ficha(id)
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        fun chequear(estado: String) {
            val textos = textosEnPantalla()
            assertTrue("$id ($estado): pantalla vacia", textos.size > 20)
            val mal = textos.filter { '*' in it || "««" in it || "»»" in it }
            assertTrue("$id ($estado): ${mal.map { it.take(100) }}", mal.isEmpty())
        }

        chequear("traduccion ON (o sin switch)")
        // El switch solo existe en las fichas bilingues (A2/B1); en B2/C1 la ficha
        // se muestra siempre en el idioma que se aprende.
        val switch = composeTestRule.onAllNodes(hasText("Traducir", substring = true))
        if (switch.fetchSemanticsNodes().isNotEmpty()) {
            switch[0].performClick()
            composeTestRule.waitForIdle()
            chequear("traduccion OFF")
        }
    }

    @Test
    fun `EN C1`() = verificar("EN-F01-C1-2026-1")

    @Test
    fun `DE B2 con contraste citado`() = verificar("DE-G09-B2-2026-1")

    @Test
    fun `DE A2`() = verificar("DE-F01-A2-2026-1")

    @Test
    fun `el contraste y los errores contrastivos no agregan angulares a lo que es del idioma de la linea`() {
        val ficha = ficha("DE-G09-B2-2026-1")
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.EN) }
        val todo = textosEnPantalla().joinToString("\n")
        // Fuente: "*würde* auch im *wenn*-Satz setzen, parallel zu «*would*»." -- aleman con una
        // cita inglesa a mano: solo `would` va entre « ».
        assertTrue(todo, todo.contains("würde auch im wenn-Satz setzen, parallel zu «would»."))
    }

    @Test
    fun `DE B1 con la app en espanol, con el switch activo el error contrastivo sale en espanol, apagado en aleman`() {
        val ficha = ficha("DE-F13-B1-2027-1")
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        fun todo() = textosEnPantalla().joinToString(" | ")
        assertTrue(todo(), todo().contains("Trasladar «Señor» con el nombre de pila"))
        assertTrue(todo(), !todo().contains("mit dem Vornamen übertragen"))

        composeTestRule.onAllNodes(hasText("Traducir", substring = true))[0].performClick()
        composeTestRule.waitForIdle()
        assertTrue(todo(), todo().contains("mit dem Vornamen übertragen"))
        assertTrue(todo(), !todo().contains("Trasladar «Señor» con el nombre de pila"))
    }

    @Test
    fun `el prompt de correccion sale en el idioma que se aprende aunque el switch muestre la traduccion`() {
        val ficha = ficha("DE-F13-B1-2027-1")
        val original = ficha.promptCorreccion.porIdioma.getValue("de").replace("*", "").take(40)
        val traduccion = ficha.promptCorreccion.porIdioma.getValue("es").replace("*", "").take(40)
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        // switch activo (default): la prosa va en espanol, pero el prompt a copiar sigue en aleman
        val todo = textosEnPantalla().joinToString(" | ") { it.replace("*", "").replace("«", "").replace("»", "") }
        assertTrue(todo, todo.contains(original.replace("«", "").replace("»", "")))
        assertTrue(!todo.contains(traduccion.replace("«", "").replace("»", "")))
    }
}
