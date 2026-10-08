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

        // Arranca traducido (idiomaBase = ES): la etiqueta dice que HACE el switch, y el titulo se ve en espanol.
        composeTestRule.onNode(hasText(etiquetaTraducirA(Idioma.ES, Idioma.ES))).assertExists()
        composeTestRule.onNodeWithText("Estructura de la oración").assertExists()
        composeTestRule.onNode(hasText("Satzbau")).assertDoesNotExist()

        // Toda la fila es clickeable (SwitchTraduccion), no hace falta acertarle al Switch exacto.
        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()

        // La etiqueta no cambia al tocarla (antes "Ver en X" cambiaba de idioma y era ambigua).
        composeTestRule.onNode(hasText(etiquetaTraducirA(Idioma.ES, Idioma.ES))).assertExists()
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
        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()
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
        composeTestRule.onNode(hasText("Traducir", substring = true)).assertDoesNotExist()
    }

    @Test
    fun `el switch de traduccion se oculta si el idioma de la app es el que se aprende`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.DE) }
        composeTestRule.onNode(hasText("übersetzen", substring = true)).assertDoesNotExist()
        // Sin switch se ve el idioma que se aprende.
        composeTestRule.onNodeWithText("Satzbau").assertExists()
    }

    // --- Ejemplos: el original SIEMPRE, la traduccion solo con el switch activo ---

    private fun fichaConEjemplo(): Ficha {
        val base = fichaBilingue()
        return base.copy(
            ejemplos = listOf(
                base.ejemplos.first().copy(
                    texto = TextoBilingue(porIdioma = mapOf("de" to "Ich finde das gut.", "es" to "Me parece bien.")),
                ),
            ),
        )
    }

    @Test
    fun `un ejemplo muestra siempre el original, y la traduccion aparece debajo solo con el switch activo`() {
        val ficha = fichaConEjemplo()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNodeWithText("Ich finde das gut.").assertExists()
        composeTestRule.onNodeWithText("Me parece bien.").assertExists()

        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()

        composeTestRule.onNodeWithText("Ich finde das gut.").assertExists()
        composeTestRule.onNode(hasText("Me parece bien.")).assertDoesNotExist()
    }

    // --- Palabra objetivo (*cita*) entre « » solo cuando se muestra la traduccion ---

    @Test
    fun `la palabra objetivo va entre comillas angulares al traducir, y sin ellas en el idioma que se aprende`() {
        val base = fichaBilingue()
        val ficha = base.copy(
            descripcion = TextoBilingue(
                porIdioma = mapOf("de" to "Nach *weil* steht das Verb am Ende.", "es" to "Después de *weil* el verbo va al final."),
            ),
        )
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNodeWithText("Después de «weil» el verbo va al final.").assertExists()

        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()
        composeTestRule.onNodeWithText("Nach weil steht das Verb am Ende.").assertExists()
    }

    // --- Contraste bilingue: la seccion existe siempre, solo cambia de idioma ---

    private fun fichaConContraste(entrada: TextoBilingue): Ficha =
        fichaBilingue().copy(contraste = mapOf("es" to entrada))

    @Test
    fun `el contraste aparece en los dos modos del switch, cambiando titulo y contenido de idioma`() {
        val ficha = fichaConContraste(
            TextoBilingue(porIdioma = mapOf("de" to "Das Spanische kennt keine Verbendstellung.", "es" to "El español no tiene verbo final.")),
        )
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        // Traducido (por defecto): titulo y texto en el idioma de app.
        composeTestRule.onNodeWithText("Contraste con el español").assertExists()
        composeTestRule.onNodeWithText("El español no tiene verbo final.").assertExists()

        // Idioma que se aprende: la seccion sigue, titulo y texto en aleman.
        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()
        composeTestRule.onNodeWithText("Kontrast zum Spanischen").assertExists()
        composeTestRule.onNodeWithText("Das Spanische kennt keine Verbendstellung.").assertExists()
        composeTestRule.onNode(hasText("Contraste con el español")).assertDoesNotExist()
    }

    @Test
    fun `una entrada de contraste sin traduccion cae al original en el modo traducido, sin crashear`() {
        val ficha = fichaConContraste(TextoBilingue(porIdioma = mapOf("de" to "Das Spanische kennt keine Verbendstellung.")))
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        composeTestRule.onNodeWithText("Das Spanische kennt keine Verbendstellung.").assertExists()
        composeTestRule.onNodeWithText("Contraste con el español").assertExists()
    }

    // --- Titulos de seccion en los 6 idiomas, siguiendo el idioma mostrado ---

    @Test
    fun `los titulos de seccion siguen el idioma mostrado y redemittel es Expresiones en espanol`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNodeWithText("Ejemplos").assertExists()
        composeTestRule.onNode(hasText("Expresiones", substring = true)).assertExists()
        composeTestRule.onNode(hasText("Redemittel", substring = true)).assertDoesNotExist()

        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()
        composeTestRule.onNodeWithText("Beispiele").assertExists()
        composeTestRule.onNode(hasText("Redemittel", substring = true)).assertExists()
    }

    @Test
    fun `la etiqueta del dia de las microtareas sigue el idioma mostrado, no el que se aprende`() {
        val ficha = fichaBilingue()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNode(hasText("lun (", substring = true)).assertExists()
        composeTestRule.onNode(hasText("Traducir", substring = true)).performClick()
        composeTestRule.onNode(hasText("Mo (", substring = true)).assertExists()
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
