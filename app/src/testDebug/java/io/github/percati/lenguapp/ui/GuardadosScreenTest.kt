package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

/**
 * Feature 3 (Guardados): filtro por idioma y reflejo de guardar/quitar en
 * la pantalla. GuardadosScreen es puro (toma `items` de afuera), asi que
 * "se refleja" se prueba manejando el estado en el propio test -- misma
 * forma en que MainActivity.kt lo hace con `guardadosTodos`.
 */
@RunWith(RobolectricTestRunner::class)
class GuardadosScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun item(idioma: Idioma, texto: String, skillId: String) = ItemGuardado(
        idioma = idioma,
        nivel = Nivel.B2,
        tipo = TipoGuardado.VOCABULARIO,
        texto = TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to texto)),
        skillIdOrigen = skillId,
    )

    @Test
    fun `el filtro de idioma filtra, no mezcla`() {
        val items = listOf(
            item(Idioma.DE, "die Miete", "DE-V02"),
            item(Idioma.EN, "the lease", "EN-V02"),
        )
        composeTestRule.setContent {
            GuardadosScreen(
                items = items,
                idiomasAprendidos = setOf(Idioma.DE, Idioma.EN),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                existeFichaOrigen = { true },
                onAbrirFicha = {},
                onVolver = {},
            )
        }

        // Pestaña EN (primera: el enum Idioma declara EN antes que DE -- ver
        // el mismo criterio en LenguAppAppTest): solo EN, nunca los dos mezclados.
        composeTestRule.onNodeWithText("the lease").assertExists()
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()

        // Cambiar a DE: ahora solo DE, EN desaparece -- filtra, no mezcla.
        composeTestRule.onNodeWithText("DE").performClick()
        composeTestRule.onNodeWithText("die Miete").assertExists()
        composeTestRule.onNode(hasText("the lease")).assertDoesNotExist()
    }

    @Test
    fun `guardar y quitar un item se refleja en la lista de Guardados`() {
        var guardados by mutableStateOf<List<ItemGuardado>>(emptyList())
        val elItem = item(Idioma.DE, "die Miete", "DE-V02")

        composeTestRule.setContent {
            GuardadosScreen(
                items = guardados,
                idiomasAprendidos = setOf(Idioma.DE),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                existeFichaOrigen = { true },
                onAbrirFicha = {},
                onVolver = {},
            )
        }
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()

        // "Guardar" (lo que en la ficha real hace el toggle de la estrella).
        guardados = guardados + elItem
        composeTestRule.onNodeWithText("die Miete").assertExists()

        // "Quitar" (tocar la estrella de nuevo).
        guardados = guardados - elItem
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()
    }

    @Test
    fun `sin idioma aprendido ni items guardados no rompe, muestra el mensaje vacio`() {
        composeTestRule.setContent {
            GuardadosScreen(
                items = emptyList(),
                idiomasAprendidos = emptySet(),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                existeFichaOrigen = { true },
                onAbrirFicha = {},
                onVolver = {},
            )
        }
        composeTestRule.onNodeWithText(mensajeGuardadosVacio(Idioma.ES)).assertExists()
    }
}
