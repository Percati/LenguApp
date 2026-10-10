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
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
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
 * Reto del fin de semana (Ronda E, tarea 4 / REGLAS-PREVENCION P4): una funcion que reutiliza
 * contenido no puede verse igual que su fuente. Sobre fichas reales de cada nivel A2 a C2: el
 * texto visible del reto no es el de la mision, y no persiste nada (regla dura 13).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class RetoFinDeSemanaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sabado = LocalDate.of(2026, 9, 12)

    private fun fichaDe(nivel: Nivel): Ficha {
        val carpeta = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
        return carpeta.listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.nivel == nivel && it.mision.requisitos.size >= 3 }
    }

    private fun textosEnPantalla(): List<String> =
        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }

    private fun verificar(nivel: Nivel) {
        val ficha = fichaDe(nivel)
        val idiomaApp = Idioma.ES
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = idiomaApp, fecha = sabado) }

        // En A2/B1 bilingues la ficha arranca traducida (idioma de app); en B2+ en el idioma que se aprende.
        val mostrado = if (ficha.bilingue) idiomaApp else ficha.idioma
        fun limpio(t: String) = t.replace("*", "").replace("«", "").replace("»", "")
        fun inicio(t: io.github.percati.lenguapp.modelo.TextoBilingue) = limpio(t.resolver(mostrado, ficha.idioma)).take(25)

        val conMision = textosEnPantalla().toSet()
        // la mision se ve normal: consigna y todos los requisitos
        assertTrue(conMision.any { limpio(it).contains(inicio(ficha.mision.consigna)) })

        composeTestRule.onNode(hasText(etiquetaDesafioFinde(idiomaApp))).performClick()
        composeTestRule.waitForIdle()
        val conReto = textosEnPantalla()
        val unidos = conReto.joinToString(" | ")

        // el encuadre fijo: titulo, linea, 4 reglas y el rotulo de la mision
        assertTrue(unidos, conReto.contains(etiquetaRetoTitulo(idiomaApp)))
        assertTrue(unidos, conReto.contains(textoRetoLinea(idiomaApp)))
        val reglas = reglasReto(idiomaApp, nivel, oralMinRedondeado(ficha.evidencia.oralMin))
        assertEquals(4, reglas.size)
        for (r in reglas) assertTrue("falta la regla: $r", conReto.contains(r))
        assertTrue(unidos, conReto.contains(etiquetaRetoMision(idiomaApp)))
        assertFalse("quedo un placeholder sin resolver: $unidos", unidos.contains("{"))

        // el reto NO es el texto de la mision: tiene al menos 7 textos que la mision no tiene
        val soloDelReto = conReto.toSet() - conMision
        assertTrue("textos propios del reto: $soloDelReto", soloDelReto.size >= 7)
        assertNotEquals(conMision, conReto.toSet())

        // de la mision solo se muestran la consigna y los dos primeros requisitos
        val requisitos = ficha.mision.requisitos.map { inicio(it) }
        val visibles = conReto.map { limpio(it) }
        assertTrue("falta el requisito 1", visibles.any { it.contains(requisitos[0]) })
        assertTrue("falta el requisito 2", visibles.any { it.contains(requisitos[1]) })
        assertFalse("el requisito 3 no debe verse en el reto", visibles.any { it.contains(requisitos[2]) })
        // y el resto de la ficha se oculta
        assertFalse(visibles.any { it.contains(inicio(ficha.descripcion)) })

        // las reglas de A2/B1 y las de B2+ son distintas; B2+ lleva los minutos orales de la ficha
        if (nivel == Nivel.A2 || nivel == Nivel.B1) {
            assertTrue(reglas.first().startsWith("1. De corrido"))
        } else {
            assertTrue(reglas.first().contains("${oralMinRedondeado(ficha.evidencia.oralMin)} min"))
        }
    }

    @Test
    fun `A2`() = verificar(Nivel.A2)

    @Test
    fun `B1`() = verificar(Nivel.B1)

    @Test
    fun `B2`() = verificar(Nivel.B2)

    @Test
    fun `C1`() = verificar(Nivel.C1)

    @Test
    fun `C2`() = verificar(Nivel.C2)

    @Test
    fun `las reglas existen en los 6 idiomas para todos los niveles, sin placeholders ni renglones vacios`() {
        for (idioma in Idioma.entries) for (nivel in Nivel.entries) {
            val reglas = reglasReto(idioma, nivel, 7)
            assertEquals("$idioma $nivel", 4, reglas.size)
            assertTrue(reglas.all { it.isNotBlank() && !it.contains("{") })
            if (nivel == Nivel.B2 || nivel == Nivel.C1 || nivel == Nivel.C2) assertTrue("$idioma $nivel: falta oralMin", reglas.first().contains("7"))
        }
        // basico y avanzado difieren en cada idioma
        for (idioma in Idioma.entries) assertNotEquals(reglasReto(idioma, Nivel.A2, 6), reglasReto(idioma, Nivel.C1, 6))
    }

    @Test
    fun `el reto no persiste nada, cada apertura arranca en off (regla dura 13)`() {
        val ficha = fichaDe(Nivel.B2)
        lateinit var reabrir: () -> Unit
        composeTestRule.setContent {
            var version by remember { mutableIntStateOf(0) }
            reabrir = { version++ }
            key(version) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado) }
        }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.waitForIdle()
        assertTrue(textosEnPantalla().contains(etiquetaRetoTitulo(Idioma.ES)))

        composeTestRule.runOnIdle(reabrir)
        composeTestRule.waitForIdle()
        assertFalse(textosEnPantalla().contains(etiquetaRetoTitulo(Idioma.ES)))
    }
}
