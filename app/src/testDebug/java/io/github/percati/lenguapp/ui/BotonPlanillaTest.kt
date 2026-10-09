package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/** El boton de la planilla del profesor esta al final de toda semana (ficha, repaso, Survival) y avisa a quien llama. */
@RunWith(RobolectricTestRunner::class)
class BotonPlanillaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun contenido(id: String): ContenidoSemanal {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        return parsearContenido(File(carpeta, "contenido/$id.json").readText())
    }

    private fun probar(id: String) {
        val pedidos = mutableListOf<ContenidoSemanal>()
        val c = contenido(id)
        composeTestRule.setContent { ContenidoSemanalScreen(c, idiomaBase = Idioma.ES, onPlanilla = { pedidos += it }) }
        composeTestRule.onNode(hasText(etiquetaPlanillaBoton(Idioma.ES))).performScrollTo().performClick()
        assertEquals(listOf(c), pedidos)
    }

    @Test
    fun `una ficha tiene el boton y al tocarlo pide la planilla de esa ficha`() = probar("DE-G01-B2-2026-1")

    @Test
    fun `una semana de repaso tambien`() = probar("REVIEW-DE-B2-2026-S40")

    @Test
    fun `una semana Survival tambien`() = probar("SURVIVAL-EN-B2-2026-S44")

    @Test
    fun `sin onPlanilla (sin textos embebidos) no hay boton`() {
        val c = contenido("DE-G01-B2-2026-1")
        composeTestRule.setContent { ContenidoSemanalScreen(c, idiomaBase = Idioma.ES) }
        composeTestRule.onNode(hasText(etiquetaPlanillaBoton(Idioma.ES))).assertDoesNotExist()
    }
}
