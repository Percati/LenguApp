package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearPlantillasPromptVoz
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/** Tarjeta del prompt de voz (Ronda B, pieza 2): visible solo si hay plantilla para el par, nunca en repaso/Survival. */
@RunWith(RobolectricTestRunner::class)
class PromptVozPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun assets() = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private fun contenido(id: String) = parsearContenido(File(assets(), "contenido/$id.json").readText())
    private fun plantillas() = parsearPlantillasPromptVoz(File(assets(), "plantillas/prompt-voz.json").readText())

    @Test
    fun `una ficha EN C1 con plantilla muestra la tarjeta del prompt de voz con titulo y ayuda`() {
        val ficha = contenido("EN-F01-C1-2026-1")
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, promptsVoz = plantillas()) }
        composeTestRule.onNode(hasText(etiquetaPromptVozTitulo(Idioma.ES))).assertExists()
        composeTestRule.onNode(hasText("Pegalo al empezar una conversación de voz", substring = true)).assertExists()
        composeTestRule.onNode(hasText("You are my spoken English conversation partner", substring = true)).assertExists()
    }

    @Test
    fun `una ficha de aleman B2 no tiene plantilla, asi que no hay tarjeta`() {
        val ficha = contenido("DE-G01-B2-2026-1")
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, promptsVoz = plantillas()) }
        composeTestRule.onNode(hasText(etiquetaPromptVozTitulo(Idioma.ES))).assertDoesNotExist()
    }

    @Test
    fun `una semana de repaso o Survival no muestra el prompt de voz`() {
        val especial = contenido("SURVIVAL-EN-B2-2026-S44")
        composeTestRule.setContent { ContenidoSemanalScreen(especial, idiomaBase = Idioma.ES, promptsVoz = plantillas()) }
        composeTestRule.onNode(hasText(etiquetaPromptVozTitulo(Idioma.ES))).assertDoesNotExist()
    }

    @Test
    fun `sin plantillas cargadas (archivo ausente) la ficha se ve igual, sin tarjeta`() {
        val ficha = contenido("EN-F01-C1-2026-1")
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        composeTestRule.onNode(hasText(etiquetaPromptVozTitulo(Idioma.ES))).assertDoesNotExist()
    }
}
