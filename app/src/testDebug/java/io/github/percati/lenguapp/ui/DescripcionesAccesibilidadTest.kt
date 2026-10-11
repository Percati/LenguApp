package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/**
 * Descripciones para lectores de pantalla (Ronda H, tarea 3): TalkBack las lee en voz alta, asi que van en
 * el idioma de app como cualquier texto (P7).
 */
@RunWith(RobolectricTestRunner::class)
class DescripcionesAccesibilidadTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `Contraer y Expandir existen en los 6 idiomas y no son el espanol en los otros`() {
        for (idioma in Idioma.entries) for (expandida in listOf(true, false)) {
            assertTrue("$idioma $expandida", descripcionSeccion(expandida, idioma).isNotBlank())
        }
        assertEquals("Contraer", descripcionSeccion(true, Idioma.ES))
        assertEquals("Expandir", descripcionSeccion(false, Idioma.ES))
        assertEquals("Ausklappen", descripcionSeccion(false, Idioma.DE))
        assertEquals("Einklappen", descripcionSeccion(true, Idioma.DE))
        for (idioma in Idioma.entries - Idioma.ES - Idioma.PT) {
            assertNotEquals(descripcionSeccion(false, Idioma.ES), descripcionSeccion(false, idioma))
        }
    }

    @Test
    fun `con la app en aleman la seccion plegable se anuncia en aleman, no en espanol`() {
        val ficha = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
            .listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>().first { it.nivel == Nivel.B2 }
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.DE, idiomaInterfaz = Idioma.DE) }
        }
        // vocabulario y expresiones arrancan plegados: "Ausklappen"
        assertTrue(composeTestRule.onAllNodesWithContentDescription("Ausklappen").fetchSemanticsNodes().size >= 2)
        composeTestRule.onAllNodesWithContentDescription("Expandir").assertCountEquals(0)
        composeTestRule.onAllNodesWithContentDescription("Contraer").assertCountEquals(0)
    }

    @Test
    fun `ningun contentDescription del codigo es una palabra fija en un idioma`() {
        val raiz = listOf(File("src/main/java"), File("app/src/main/java")).first { it.isDirectory }
        val literal = Regex("""contentDescription\s*=\s*"[A-Za-zÁ-ú]""")
        val malos = raiz.walkTopDown().filter { it.extension == "kt" }
            .filter { archivo -> literal.containsMatchIn(archivo.readText()) }.map { it.name }.toList()
        assertEquals("contentDescription con texto fijo en: $malos", emptyList<String>(), malos)
    }
}
