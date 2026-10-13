package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import io.github.percati.lenguapp.presentacion.datosFila
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Sin glosa cuando el idioma de la app es el que se aprende (Ronda R, tarea 8): con la app en aleman aprendiendo aleman (o ingles con
 * ingles) no hay glosa que mostrar, y la ficha, la Biblioteca y Guardados lo hacen limpio: ni un guion suelto, ni un parentesis vacio, ni la
 * glosa en espanol de respaldo, ni una linea en blanco donde iba la traduccion. Se prueba contra el espanol como control.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class GlosaMismoIdiomaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun carpeta(): File = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }

    // Una ficha alemana real con vocabulario y expresiones, y glosas en espanol.
    private val ficha: Ficha by lazy {
        carpeta().listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.idioma == Idioma.DE && it.nivel == Nivel.B2 && it.vocabulario.size >= 5 && it.redemittel.size >= 3 }
    }

    private val glosasEs: List<String> by lazy {
        ficha.vocabulario.mapNotNull { it.traducciones["es"]?.trim() }.filter { it.isNotBlank() && it != "—" }.distinct().take(8)
    }

    @Test
    fun `datosFila no trae glosa si el idioma de la app es el que se aprende, y si trae en los demas`() {
        val item = construirBiblioteca(listOf(ficha)).first { it.vocabulario != null && !it.vocabulario!!.traducciones["es"].isNullOrBlank() }
        assertNull("aleman con aleman: sin glosa", item.datosFila(Idioma.DE).traduccion)
        assertEquals("el espanol sigue con su glosa", item.vocabulario!!.traducciones["es"]!!.trim(), item.datosFila(Idioma.ES).traduccion)
        assertNotNull("el ingles tambien", item.datosFila(Idioma.EN).traduccion)
        // y la busqueda no encuentra nada por una glosa que no se muestra
        assertEquals("", item.traduccionBuscable(Idioma.DE))
        assertTrue(item.traduccionBuscable(Idioma.ES).isNotBlank())
    }

    @Test
    fun `la ficha en aleman con la app en aleman no muestra glosas, ni un guion suelto`() {
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.DE, topicNombres = NombresI18n()) }
        composeTestRule.onNodeWithText("${etiquetaSeccion("vocabulario", Idioma.DE)} (${ficha.vocabulario.size})").performClick()
        composeTestRule.onNodeWithText("${etiquetaSeccion("redemittel", Idioma.DE)} (${ficha.redemittel.size})").performClick()
        composeTestRule.waitForIdle()
        ficha.vocabulario.first().item.let { composeTestRule.onAllNodesWithText(it).assertCountEquals(1) } // la palabra, una vez
        assertTrue("necesito glosas en espanol para el control", glosasEs.isNotEmpty())
        for (g in glosasEs) composeTestRule.onAllNodesWithText(g).assertCountEquals(0)
        composeTestRule.onAllNodesWithText("—").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("Sin equivalencia directa: se aprende por situación.").assertCountEquals(0)
    }

    @Test
    fun `como control, la misma ficha con la app en espanol si muestra las glosas`() {
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, topicNombres = NombresI18n()) }
        // la ficha es de B2: sus titulos van en el idioma que se aprende, aunque la app este en espanol
        composeTestRule.onNodeWithText("${etiquetaSeccion("vocabulario", Idioma.DE)} (${ficha.vocabulario.size})").performClick()
        composeTestRule.waitForIdle()
        assertTrue(glosasEs.isNotEmpty())
        composeTestRule.onNodeWithText(glosasEs.first()).assertExists()
    }

    @Test
    fun `la Biblioteca con la app en aleman aprendiendo aleman no muestra glosas`() {
        val items = construirBiblioteca(listOf(ficha))
        composeTestRule.setContent {
            BibliotecaScreen(
                items = items, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.DE, idiomaInterfaz = Idioma.DE,
                topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(), onAlternar = {}, onVolver = {},
            )
        }
        composeTestRule.waitForIdle()
        for (g in glosasEs) composeTestRule.onAllNodesWithText(g).assertCountEquals(0)
        composeTestRule.onAllNodesWithText("—").assertCountEquals(0)
        // la palabra si esta
        composeTestRule.onAllNodesWithText(ficha.vocabulario.first().item.trim()).assertCountEquals(1)
    }

    @Test
    fun `Guardados con la app en aleman aprendiendo aleman no muestra glosa`() {
        fun guardado(idioma: Idioma, texto: String, glosa: String) = ItemGuardado(
            id = 1, idioma = idioma, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO,
            texto = TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to texto, "es" to glosa)), skillIdOrigen = "DE-V02",
        )
        composeTestRule.setContent {
            GuardadosScreen(
                items = listOf(guardado(Idioma.DE, "die Miete", "el alquiler")), itemsBiblioteca = emptyList(),
                idiomasAprendidos = setOf(Idioma.DE), idiomaBase = Idioma.DE, idiomaInterfaz = Idioma.DE,
                topicNombres = NombresI18n(), categoriasUso = CategoriasUso(),
                existeFichaOrigen = { true }, onAbrirFicha = {}, onQuitar = {}, onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("die Miete").assertExists()
        composeTestRule.onAllNodesWithText("el alquiler").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("—").assertCountEquals(0)
    }

    @Test
    fun `como control, Guardados con la app en espanol si muestra la glosa`() {
        val item = ItemGuardado(
            id = 1, idioma = Idioma.DE, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO,
            texto = TextoBilingue(porIdioma = mapOf("de" to "die Miete", "es" to "el alquiler")), skillIdOrigen = "DE-V02",
        )
        composeTestRule.setContent {
            GuardadosScreen(
                items = listOf(item), itemsBiblioteca = emptyList(), idiomasAprendidos = setOf(Idioma.DE),
                idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES, topicNombres = NombresI18n(), categoriasUso = CategoriasUso(),
                existeFichaOrigen = { true }, onAbrirFicha = {}, onQuitar = {}, onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("el alquiler").assertExists()
    }

    @Test
    fun `el aviso de que las glosas solo existen en espanol ya no existe, en ningun idioma`() {
        assertTrue(ClaveTexto.entries.none { it.name == "AVISO_GLOSAS" })
    }
}
