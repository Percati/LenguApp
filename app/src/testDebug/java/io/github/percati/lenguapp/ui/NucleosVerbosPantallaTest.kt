package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import io.github.percati.lenguapp.presentacion.esColocacionConNucleo
import io.github.percati.lenguapp.presentacion.esVerbo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

private val itemsReales: List<ItemBiblioteca> by lazy {
    val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    construirBiblioteca(
        File(assets, "contenido").listFiles { f -> f.extension == "json" }!!
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>(),
    )
}

/** Los nucleos verbales en la vista Verbos, en pantalla (Ronda J, tarea 1): sobre los items reales de la familia «stellen». */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class NucleosVerbosPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun colocacionesDe(nucleo: String) =
        itemsReales.filter { it.idioma == Idioma.DE && it.esColocacionConNucleo() && it.vocabulario!!.nucleoVerbal == nucleo }

    private fun propio(texto: String) = itemsReales.first { it.idioma == Idioma.DE && it.texto == texto && it.esVerbo() }

    private fun mostrar(lista: List<ItemBiblioteca>, guardadas: Set<String> = emptySet(), onAlternar: (ItemBiblioteca) -> Unit = {}) {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = lista, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = guardadas,
                onAlternar = onAlternar, onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("Verbos").performClick()
    }

    @Test
    fun `un nucleo puro es un encabezado sin estrella, y sus colocaciones se ven debajo con traduccion y con estrella`() {
        val colocaciones = colocacionesDe("stellen")
        assertTrue(colocaciones.size >= 3)
        mostrar(colocaciones)
        composeTestRule.onNodeWithText("stellen").assertIsDisplayed()
        // una estrella por colocacion; el nucleo no tiene
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA).assertCountEquals(colocaciones.size)
        for (c in colocaciones) {
            composeTestRule.onNodeWithText(c.texto).assertIsDisplayed()
            val traduccion = c.vocabulario!!.traducciones["es"]
            if (!traduccion.isNullOrBlank()) composeTestRule.onAllNodesWithText(traduccion).onFirst().assertIsDisplayed()
        }
    }

    @Test
    fun `la estrella de una colocacion marca la colocacion, con su propia clave, no el nucleo`() {
        val colocaciones = colocacionesDe("umstellen")
        val marcados = mutableListOf<ItemBiblioteca>()
        mostrar(colocaciones, onAlternar = { marcados += it })
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA).onFirst().performClick()
        assertEquals(1, marcados.size)
        assertTrue(marcados.single().esColocacionConNucleo())
        assertEquals(colocaciones.first().texto, marcados.single().texto)
        assertEquals("VOCABULARIO|${colocaciones.first().texto}", marcados.single().let { "${it.tipo}|${it.texto}" })
    }

    @Test
    fun `una colocacion no aparece suelta ademas de debajo de su nucleo, y un nucleo fusionado es una sola fila marcable`() {
        val bestellen = propio("bestellen")
        val colocaciones = colocacionesDe("bestellen")
        assertTrue(colocaciones.isNotEmpty())
        mostrar(listOf(bestellen) + colocaciones)
        // «bestellen» UNA vez (la fila del verbo propio), no dos (una marcable y otra no)
        composeTestRule.onAllNodesWithText("bestellen").assertCountEquals(1)
        // cada colocacion UNA vez
        for (c in colocaciones) composeTestRule.onAllNodesWithText(c.texto).assertCountEquals(1)
        // estrellas: la del verbo propio + una por colocacion
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA).assertCountEquals(1 + colocaciones.size)
    }

    @Test
    fun `la estrella de una colocacion guardada se enciende por su clave`() {
        val colocaciones = colocacionesDe("umstellen")
        val c = colocaciones.first()
        mostrar(colocaciones, guardadas = setOf(claveBiblioteca(c.idioma, c.tipo, c.texto)))
        // dos filas con estrella como minimo no: umstellen puede tener una sola colocacion; la unica esta guardada
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA).assertCountEquals(colocaciones.size)
    }

    @Test
    fun `un nucleo aporta su clase al chip, y su fila ya no lleva el nivel`() {
        val colocaciones = colocacionesDe("umstellen")
        mostrar(colocaciones)
        // umstellen es trennbar: un chip «Separable (1)» y su fila
        composeTestRule.onNodeWithText("Separable (1)").assertIsDisplayed()
        for (nivel in io.github.percati.lenguapp.modelo.Nivel.entries) composeTestRule.onAllNodesWithText(nivel.name).assertCountEquals(0)
        composeTestRule.onNodeWithText("Separable (1)").performClick()
        composeTestRule.onNodeWithText("umstellen").assertIsDisplayed()
    }

    @Test
    fun `buscar Frage trae stellen y se ve la colocacion que coincidio`() {
        mostrar(colocacionesDe("stellen") + colocacionesDe("einstellen"))
        composeTestRule.onNodeWithText("einstellen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Buscar").performTextInput("frage")
        composeTestRule.onNodeWithText("stellen").assertIsDisplayed()
        composeTestRule.onNodeWithText("die Frage stellen").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("einstellen").assertCountEquals(0)
    }
}

/** El campo de busqueda queda fijo arriba de la vista Verbos mientras se recorre la lista (Ronda J, tarea 2). */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h600dp")
class BuscadorFijoVerbosTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `el campo de busqueda sigue a la vista despues de recorrer la lista, y los chips se van con el scroll`() {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = itemsReales, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(),
                onAlternar = {}, onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("Verbos").performClick()
        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alemán").assertIsDisplayed()

        composeTestRule.onNode(hasScrollToIndexAction() and SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange)).performScrollToIndex(300)
        composeTestRule.waitForIdle()
        // la lista avanzo: la pestana de idioma ya no esta, pero el campo si
        composeTestRule.onAllNodesWithText("Alemán").assertCountEquals(0)
        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
        // y se puede escribir sin volver arriba
        composeTestRule.onNodeWithText("Buscar").performTextInput("stellen")
        composeTestRule.onNodeWithText("Buscar").assertIsDisplayed()
    }
}
