package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.expresionesDeLaFicha
import io.github.percati.lenguapp.presentacion.oralMinRedondeado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate

/**
 * Desafio de fin de semana (Ronda F2, tarea 2 / REGLAS-PREVENCION P4): NO es la mision repetida.
 * Sobre fichas reales de cada nivel A2 a C2: no aparece ni la consigna ni los requisitos de la
 * mision, si el encuadre propio (linea, tema, habilidad, condiciones, frases); no persiste nada
 * (regla dura 13).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class RetoFinDeSemanaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sabado = LocalDate.of(2026, 9, 12)

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private val topics: NombresI18n by lazy { parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText()) }

    private fun fichaDe(nivel: Nivel): Ficha =
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.nivel == nivel && it.mision.requisitos.size >= 2 && it.redemittel.size >= 3 }

    private fun textosEnPantalla(): List<String> =
        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }

    private fun limpio(t: String) = t.replace("*", "").replace("«", "").replace("»", "")

    private fun fragmentos(t: TextoBilingue, ficha: Ficha, mostrado: Idioma) =
        limpio(t.resolver(mostrado, ficha.idioma)).take(30)

    private fun activarReto(ficha: Ficha, idiomaApp: Idioma) {
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = idiomaApp, fecha = sabado, topicNombres = topics) }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(idiomaApp))).performClick()
        composeTestRule.waitForIdle()
    }

    private fun verificar(nivel: Nivel, idiomaApp: Idioma) {
        val ficha = fichaDe(nivel)
        activarReto(ficha, idiomaApp)
        val conReto = textosEnPantalla()
        val unidos = conReto.joinToString(" | ")
        val visibles = conReto.map { limpio(it) }

        // La mision NO se muestra: ni consigna ni requisitos, en ningun idioma posible
        for (idioma in listOf(idiomaApp, ficha.idioma)) {
            assertFalse("$nivel: aparece la consigna: $unidos", visibles.any { it.contains(fragmentos(ficha.mision.consigna, ficha, idioma)) })
            for (req in ficha.mision.requisitos) {
                assertFalse("$nivel: aparece un requisito", visibles.any { it.contains(fragmentos(req, ficha, idioma)) })
            }
        }
        // ...ni la descripcion u otras secciones de la ficha
        assertFalse(visibles.any { it.contains(fragmentos(ficha.descripcion, ficha, ficha.idioma)) })

        // Encabezado, linea, tema y habilidad
        assertTrue(unidos, conReto.contains(textoRetoLinea(idiomaApp)))
        assertTrue(unidos, conReto.contains("${etiquetaBibliotecaTema(idiomaApp)}:"))
        assertTrue(unidos, conReto.contains(topics.nombre(ficha.topicId, idiomaApp)))   // el tema, en el idioma de app
        assertTrue(unidos, conReto.contains("${etiquetaRetoHabilidad(idiomaApp)}:"))
        assertTrue("falta el titulo de la ficha como habilidad", visibles.any { it.isNotBlank() && it == limpio(ficha.titulo.resolver(if (ficha.bilingue) idiomaApp else ficha.idioma, ficha.idioma)) })

        // Condiciones: 4 filas, una por regla, segun el nivel
        assertTrue(unidos, conReto.contains(etiquetaRetoCondiciones(idiomaApp)))
        val reglas = reglasReto(idiomaApp, nivel, oralMinRedondeado(ficha.evidencia.oralMin))
        assertEquals(4, reglas.size)
        for (r in reglas) assertTrue("falta la condicion: $r", conReto.contains(r))
        assertFalse("quedo un placeholder sin resolver: $unidos", unidos.contains("{"))
        if (nivel == Nivel.A2 || nivel == Nivel.B1) {
            assertEquals(reglasReto(idiomaApp, Nivel.A2, 6), reglas) // A2 y B1 comparten las condiciones
        } else {
            assertTrue(reglas[1].contains(oralMinRedondeado(ficha.evidencia.oralMin).toString()))
            assertNotEquals(reglasReto(idiomaApp, Nivel.A2, 6), reglas)
        }

        // Frases para usar: hasta 3, en el idioma que se aprende, con el encabezado en idioma de app
        assertTrue(unidos, conReto.contains(etiquetaRetoFrasesTitulo(idiomaApp)))
        assertTrue(unidos, conReto.contains(textoRetoFrasesAyuda(idiomaApp)))
        val frases = expresionesDeLaFicha(ficha, 3)
        assertEquals(3, frases.size)
        for (fr in frases) assertTrue("falta la frase $fr", conReto.contains(fr))
        val cuarta = ficha.redemittel.getOrNull(3)?.expresion?.trim()
        if (cuarta != null && cuarta !in frases) assertFalse("no debe haber mas de 3 frases", conReto.contains(cuarta))
    }

    @Test
    fun `A2 con la app en espanol`() = verificar(Nivel.A2, Idioma.ES)

    @Test
    fun `B1 con la app en ingles`() = verificar(Nivel.B1, Idioma.EN)

    @Test
    fun `B2 con la app en aleman`() = verificar(Nivel.B2, Idioma.DE)

    @Test
    fun `C1 con la app en frances`() = verificar(Nivel.C1, Idioma.FR)

    @Test
    fun `C2 con la app en portugues`() = verificar(Nivel.C2, Idioma.PT)

    @Test
    fun `las condiciones existen en los 6 idiomas para todos los niveles, una idea corta por fila`() {
        for (idioma in Idioma.entries) for (nivel in Nivel.entries) {
            val reglas = reglasReto(idioma, nivel, 7)
            assertEquals("$idioma $nivel", 4, reglas.size)
            assertTrue(reglas.all { it.isNotBlank() && !it.contains("{") && it.length <= 80 })
            assertTrue("$idioma $nivel: sin numeracion", reglas.none { it.matches(Regex("""^\d+\..*""")) })
            if (nivel == Nivel.B2 || nivel == Nivel.C1 || nivel == Nivel.C2) assertTrue("$idioma $nivel: falta oralMin", reglas[1].contains("7"))
        }
        for (idioma in Idioma.entries) assertNotEquals(reglasReto(idioma, Nivel.A2, 6), reglasReto(idioma, Nivel.C1, 6))
        for (idioma in Idioma.entries) {
            assertTrue(textoRetoLinea(idioma).isNotBlank())
            assertTrue(etiquetaRetoHabilidad(idioma).isNotBlank())
        }
        assertEquals("Dilo en voz alta, como en una conversación de verdad.", textoRetoLinea(Idioma.ES))
    }

    @Test
    fun `una ficha sin expresiones no muestra la seccion de frases`() {
        val ficha = fichaDe(Nivel.B2).copy(redemittel = emptyList())
        activarReto(ficha, Idioma.ES)
        composeTestRule.onAllNodesWithText(etiquetaRetoFrasesTitulo(Idioma.ES)).assertCountEqualsCero()
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteractionCollection.assertCountEqualsCero() {
        assertEquals(0, fetchSemanticsNodes().size)
    }

    @Test
    fun `el reto no persiste nada, cada apertura arranca en off (regla dura 13)`() {
        val ficha = fichaDe(Nivel.B2)
        lateinit var reabrir: () -> Unit
        composeTestRule.setContent {
            var version by remember { mutableIntStateOf(0) }
            reabrir = { version++ }
            key(version) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado, topicNombres = topics) }
        }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.waitForIdle()
        assertTrue(textosEnPantalla().contains(etiquetaRetoCondiciones(Idioma.ES)))

        composeTestRule.runOnIdle(reabrir)
        composeTestRule.waitForIdle()
        assertFalse(textosEnPantalla().contains(etiquetaRetoCondiciones(Idioma.ES)))
    }
}
