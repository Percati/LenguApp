package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/** Busqueda de la vista Verbos en pantalla (Ronda H, tarea 2). */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class BusquedaVerbosPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun base(): ItemBiblioteca {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val ficha = parsearContenido(File(assets, "contenido/DE-F01-A2-2026-1.json").readText()) as Ficha
        return construirBiblioteca(listOf(ficha)).first { it.tipo == TipoGuardado.VOCABULARIO }
    }

    private fun verbo(texto: String, clases: List<String>): ItemBiblioteca {
        val b = base()
        return b.copy(
            texto = texto, niveles = setOf(Nivel.B2),
            vocabulario = b.vocabulario!!.copy(item = texto, claseVerbo = clases, reccion = null, traducciones = mapOf("es" to "trad-$texto")),
        )
    }

    private val lista by lazy {
        listOf(
            verbo("zumachen", listOf("trennbar")), verbo("aufmachen", listOf("trennbar")), verbo("abmachen", listOf("trennbar")),
            verbo("sich beschweren", listOf("reflexivo")), verbo("arbeiten", listOf("simple")), verbo("übernehmen", listOf("trennbar")),
        )
    }

    private fun mostrar(items: List<ItemBiblioteca> = lista, vistaVerbos: Boolean = true) {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = items, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(), onAlternar = {}, onVolver = {},
            )
        }
        if (vistaVerbos) composeTestRule.onNodeWithText("Verbos").performClick()
    }

    @Test
    fun `el campo filtra mientras se escribe, sin distinguir mayusculas ni diacriticos, y por traduccion`() {
        mostrar()
        composeTestRule.onNodeWithText("Buscar").performTextInput("MACHEN")
        for (v in listOf("aufmachen", "zumachen", "abmachen")) composeTestRule.onNodeWithText(v).assertExists()
        composeTestRule.onAllNodesWithText("arbeiten").assertCountEquals(0)

        composeTestRule.onNodeWithText("Buscar").performTextReplacement("uber")
        composeTestRule.onNodeWithText("übernehmen").assertExists()
        composeTestRule.onAllNodesWithText("zumachen").assertCountEquals(0)

        // por la traduccion al idioma de app (las de prueba son "trad-<verbo>")
        composeTestRule.onNodeWithText("Buscar").performTextReplacement("trad-arbe")
        composeTestRule.onNodeWithText("arbeiten").assertExists()
        composeTestRule.onAllNodesWithText("übernehmen").assertCountEquals(0)
    }

    @Test
    fun `la busqueda se combina con los chips de clase`() {
        mostrar()
        composeTestRule.onNodeWithText("Reflexivo (1)").performClick()
        composeTestRule.onNodeWithText("Buscar").performTextInput("machen")
        composeTestRule.onNodeWithText(mensajeVerbosSinResultados(Idioma.ES)).assertExists()
        composeTestRule.onNodeWithText("Reflexivo (1)").performClick()
        composeTestRule.onNodeWithText("zumachen").assertExists()
    }

    @Test
    fun `sin resultados se dice, distinto del estado vacio, y el boton de borrar limpia`() {
        mostrar()
        composeTestRule.onNodeWithText("Buscar").performTextInput("qqqqzzzz")
        composeTestRule.onNodeWithText(mensajeVerbosSinResultados(Idioma.ES)).assertExists()
        composeTestRule.onAllNodesWithText(mensajeVerbosVacio(Idioma.ES)).assertCountEquals(0)
        composeTestRule.onNodeWithContentDescription(descripcionBorrarBusqueda(Idioma.ES)).performClick()
        composeTestRule.onAllNodesWithText(mensajeVerbosSinResultados(Idioma.ES)).assertCountEquals(0)
        composeTestRule.onNodeWithText("arbeiten").assertExists()
        // con el campo vacio el boton ya no esta
        composeTestRule.onAllNodesWithContentDescription(descripcionBorrarBusqueda(Idioma.ES)).assertCountEquals(0)
    }

    @Test
    fun `un par sin verbos no ofrece campo de busqueda en la vista Verbos`() {
        val sustantivo = base().let { b -> b.copy(texto = "x", niveles = setOf(Nivel.B2), vocabulario = b.vocabulario!!.copy(item = "x", claseVerbo = emptyList())) }
        mostrar(listOf(sustantivo))
        composeTestRule.onAllNodesWithText("Buscar").assertCountEquals(0)
        composeTestRule.onNodeWithText(mensajeVerbosVacio(Idioma.ES)).assertExists()
    }

    @Test
    fun `la otra vista sigue con su propia busqueda de siempre`() {
        mostrar(vistaVerbos = false)
        composeTestRule.onNodeWithText("Buscar").assertExists()
    }

    @Test
    fun `con aleman B2 en Ajustes, buscar stellen muestra los 6 miembros de la familia aunque ninguno sea de B2`() {
        fun v(texto: String, nivel: Nivel) = verbo(texto, listOf("trennbar")).copy(niveles = setOf(nivel))
        val familia = listOf(
            v("bestellen", Nivel.A2), v("sich bestellen", Nivel.B1), v("sich vorstellen", Nivel.B1),
            v("nachbestellen", Nivel.C1), v("kaltstellen", Nivel.C2), v("richtigstellen", Nivel.C2),
        )
        mostrar(familia + verbo("arbeiten", listOf("simple"))) // Ajustes: DE en B2
        composeTestRule.onNodeWithText("Buscar").performTextInput("stellen")
        for (m in familia) composeTestRule.onNodeWithText(m.texto).assertExists()
        composeTestRule.onAllNodesWithText("arbeiten").assertCountEquals(0)
        composeTestRule.onAllNodesWithText(mensajeVerbosSinResultados(Idioma.ES)).assertCountEquals(0)
    }

    // --- Ronda T, tarea 6: la vista Verbos ya no muestra el nivel ---

    @Test
    fun `la vista Verbos no muestra el nivel, solo el regimen, y sin regimen no dibuja la linea`() {
        val conRegimen = verbo("warten", listOf("verbo-preposicion")).let { it.copy(vocabulario = it.vocabulario!!.copy(reccion = "auf + Akk")) }
        mostrar(listOf(conRegimen, verbo("arbeiten", listOf("simple"))))
        composeTestRule.onNodeWithText("auf + Akk").assertExists()
        for (n in Nivel.entries) {
            composeTestRule.onAllNodesWithText(n.name).assertCountEquals(0)
            composeTestRule.onAllNodesWithText("${n.name} · auf + Akk").assertCountEquals(0)
        }
        // el verbo sin regimen ni clase de chip se ve igual, sin linea extra
        composeTestRule.onNodeWithText("arbeiten").assertExists()
    }
}
