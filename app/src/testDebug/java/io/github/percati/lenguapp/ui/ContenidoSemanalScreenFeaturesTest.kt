package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.TextoBilingue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.time.LocalDate

/**
 * Feature 1 (switch de traduccion en vivo) y feature 2 (desafio de fin de
 * semana), sobre una ficha real del piloto vuelta bilingue con `.copy()`
 * (el piloto 2026 en si sigue en string plano -- ver FALTANTES.md 4.8).
 */
@RunWith(RobolectricTestRunner::class)
class ContenidoSemanalScreenFeaturesTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Semana 37 de 2026: lunes 7 -> sabado 12 y domingo 13 son su fin de
    // semana; lunes 14 ya es la semana siguiente pero sigue sirviendo de
    // "no es fin de semana" para esta ficha.
    private val sabado = LocalDate.of(2026, 9, 12)
    private val lunes = LocalDate.of(2026, 9, 14)

    private fun fichaBilingue(): Ficha {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val base = parsearContenido(File(carpeta, "contenido/DE-G01-B2-2026-1.json").readText()) as Ficha
        return base.copy(
            bilingue = true,
            titulo = TextoBilingue(porIdioma = mapOf("de" to "Satzbau", "es" to "Estructura de la oración")),
            subtitulo = TextoBilingue(porIdioma = mapOf("de" to "Verbstellung", "es" to "Posición del verbo")),
            descripcion = TextoBilingue(porIdioma = mapOf("de" to "Die Beschreibung auf Deutsch.", "es" to "La descripción en español.")),
        )
    }

    // --- Feature 1: switch de traduccion en vivo ---

    @Test
    fun `el switch de traduccion arranca mostrando idiomaBase, y alternar cambia el idioma mostrado`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        // Arranca en idiomaBase (ES): la etiqueta del switch lo dice, y el titulo se ve en espanol.
        composeTestRule.onNode(hasText(etiquetaVerEnIdioma(Idioma.ES, Idioma.ES))).assertExists()
        composeTestRule.onNodeWithText("Estructura de la oración").assertExists()
        composeTestRule.onNode(hasText("Satzbau")).assertDoesNotExist()

        // Toda la fila es clickeable (SwitchTraduccion), no hace falta acertarle al Switch exacto.
        composeTestRule.onNode(hasText("Ver en", substring = true)).performClick()

        composeTestRule.onNode(hasText(etiquetaVerEnIdioma(Idioma.ES, Idioma.DE))).assertExists()
        composeTestRule.onNodeWithText("Satzbau").assertExists()
        composeTestRule.onNode(hasText("Estructura de la oración")).assertDoesNotExist()
    }

    @Test
    fun `el switch de traduccion no persiste entre aperturas -- una composicion nueva arranca de nuevo en idiomaBase`() {
        val ficha = fichaBilingue()
        // createAndroidComposeRule solo admite un setContent() por test:
        // "reabrir la app" se simula con key() -- cambiar la key tira el
        // remember() de adentro y arma la subcomposicion desde cero, que es
        // justo lo que pasa en un restart real (Activity/composicion nuevas).
        lateinit var reabrir: () -> Unit
        composeTestRule.setContent {
            var version by remember { mutableIntStateOf(0) }
            reabrir = { version++ }
            key(version) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        }

        // "Apertura" 1: se activa el switch (se ve el idioma que se aprende).
        composeTestRule.onNode(hasText("Ver en", substring = true)).performClick()
        composeTestRule.onNodeWithText("Satzbau").assertExists()

        // "Apertura" 2: arranca de nuevo en idiomaBase (CLAUDE.md regla dura #13, mismo criterio).
        composeTestRule.runOnIdle(reabrir)
        composeTestRule.onNodeWithText("Estructura de la oración").assertExists()
        composeTestRule.onNode(hasText("Satzbau")).assertDoesNotExist()
    }

    @Test
    fun `el switch de traduccion no aparece en fichas no bilingues`() {
        val ficha = fichaBilingue().copy(bilingue = false)
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        composeTestRule.onNode(hasText("Ver en", substring = true)).assertDoesNotExist()
    }

    // --- Feature 2: desafio de fin de semana ---

    @Test
    fun `el desafio no aparece fuera de la ventana sabado-domingo`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = lunes) }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).assertDoesNotExist()
    }

    @Test
    fun `el desafio aparece el sabado, y activarlo reemplaza el contenido normal por la mision`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado) }

        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).assertExists()
        // Sin activar el switch, se ve el contenido semanal por defecto, no el desafio.
        composeTestRule.onNode(hasText("La descripción en español.")).assertExists()

        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()

        // "Pasar al desafio": el resto de la ficha (descripcion, vocabulario) se oculta.
        composeTestRule.onNode(hasText("La descripción en español.")).assertDoesNotExist()
    }

    @Test
    fun `el switch del desafio arranca en off cada vez, aunque haya quedado en on antes`() {
        val ficha = fichaBilingue()
        lateinit var reabrir: () -> Unit
        composeTestRule.setContent {
            var version by remember { mutableIntStateOf(0) }
            reabrir = { version++ }
            key(version) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado) }
        }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.onNode(hasText("La descripción en español.")).assertDoesNotExist()

        // Nueva "apertura": arranca en off de nuevo, se ve el contenido normal.
        composeTestRule.runOnIdle(reabrir)
        composeTestRule.onNode(hasText("La descripción en español.")).assertExists()
    }
}
