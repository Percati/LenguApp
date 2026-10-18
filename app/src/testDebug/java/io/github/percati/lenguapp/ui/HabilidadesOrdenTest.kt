package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.presentacion.AparicionHabilidad
import io.github.percati.lenguapp.presentacion.FilaHabilidad
import io.github.percati.lenguapp.presentacion.IndiceHabilidades
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * El orden de la vista Habilidades de la Biblioteca (Ronda T, tarea 3): dos chips, «Alfabetico» por omision y «Por semana», que no
 * persisten (cada entrada a la Biblioteca arranca en «Alfabetico», regla dura 4).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class HabilidadesOrdenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun carpeta(): File = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }

    private val ficha: Ficha by lazy {
        carpeta().listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>().first { it.idioma == Idioma.DE && it.nivel == Nivel.B2 }
    }

    // Alfabetico: Aaa, Bbb, Ccc. Por semana: Ccc (semana 1), Bbb (2), Aaa (3).
    private val indice = IndiceHabilidades.Filas(
        Idioma.DE, Nivel.B2, 2026,
        listOf(
            FilaHabilidad("DE-A", "Aaa habilidad", listOf(AparicionHabilidad(3, "a")), "a"),
            FilaHabilidad("DE-B", "Bbb habilidad", listOf(AparicionHabilidad(2, "b")), "b"),
            FilaHabilidad("DE-C", "Ccc habilidad", listOf(AparicionHabilidad(1, "c")), "c"),
        ),
    )

    private var dentro by mutableStateOf(true)

    private fun montar() {
        composeTestRule.setContent {
            if (dentro) {
                BibliotecaScreen(
                    items = construirBiblioteca(listOf(ficha)), idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES,
                    idiomaInterfaz = Idioma.ES, topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(),
                    onAlternar = {}, onVolver = {}, habilidades = { indice },
                )
            }
        }
        composeTestRule.onNodeWithText(etiquetaBibliotecaVistaHabilidades(Idioma.ES)).performClick()
        composeTestRule.waitForIdle()
    }

    private fun arriba(titulo: String) = composeTestRule.onNodeWithText(titulo).fetchSemanticsNode().boundsInRoot.top

    private fun orden(): List<String> =
        listOf("Aaa habilidad", "Bbb habilidad", "Ccc habilidad").sortedBy { arriba(it) }.map { it.substring(0, 3) }

    @Test
    fun `las dos opciones de orden existen en los 6 idiomas`() {
        for (idioma in Idioma.entries) {
            assertTrue(etiquetaBibliotecaOrdenAlfabetico(idioma).isNotBlank() && etiquetaBibliotecaOrdenSemana(idioma).isNotBlank())
        }
        assertEquals("Alfabético", etiquetaBibliotecaOrdenAlfabetico(Idioma.ES))
        assertEquals("Por semana", etiquetaBibliotecaOrdenSemana(Idioma.ES))
    }

    @Test
    fun `la vista Habilidades arranca en alfabetico y Por semana la reordena por la primera aparicion`() {
        montar()
        composeTestRule.onNodeWithText("Alfabético").assertExists()
        assertEquals(listOf("Aaa", "Bbb", "Ccc"), orden())
        composeTestRule.onNodeWithText("Por semana").performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("Ccc", "Bbb", "Aaa"), orden())
        composeTestRule.onNodeWithText("Alfabético").performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("Aaa", "Bbb", "Ccc"), orden())
    }

    @Test
    fun `el orden elegido no persiste, al volver a entrar a la Biblioteca arranca en alfabetico`() {
        montar()
        composeTestRule.onNodeWithText("Por semana").performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("Ccc", "Bbb", "Aaa"), orden())
        // sale de la Biblioteca y vuelve a entrar
        composeTestRule.runOnIdle { dentro = false }
        composeTestRule.waitForIdle()
        composeTestRule.runOnIdle { dentro = true }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText(etiquetaBibliotecaVistaHabilidades(Idioma.ES)).performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("Aaa", "Bbb", "Ccc"), orden())
    }
}
