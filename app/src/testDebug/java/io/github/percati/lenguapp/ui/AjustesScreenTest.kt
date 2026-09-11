package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

/**
 * AJUSTES-FASE-6.md, bloques B y C: nivel por idioma con menu, deseleccion,
 * idioma de la aplicacion unificado, aviso de idioma no disponible.
 */
@RunWith(RobolectricTestRunner::class)
class AjustesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun montar(inicial: Ajustes): () -> Ajustes {
        lateinit var obtenerActual: () -> Ajustes
        composeTestRule.setContent {
            var ajustes by remember { mutableStateOf(inicial) }
            obtenerActual = { ajustes }
            AjustesScreen(
                ajustes = ajustes,
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                idiomaAplicacionEfectivo = ajustes.idiomaInterfaz,
                onAjustesCambiados = { ajustes = it },
                onVolver = {},
            )
        }
        return obtenerActual
    }

    @Test
    fun `tocar un idioma no seleccionado abre el menu de nivel, y elegir uno lo agrega`() {
        val actual = montar(Ajustes(idiomasAprendidos = emptyMap()))

        // "EN" aparece tambien en el selector de idioma de la aplicacion mas
        // abajo: la primera ocurrencia es la de "Idioma que aprendés".
        composeTestRule.onAllNodesWithText("EN").onFirst().performClick()
        composeTestRule.onNodeWithText("C1").performClick()

        assertEquals(mapOf(Idioma.EN to Nivel.C1), actual().idiomasAprendidos)
    }

    @Test
    fun `tocar un idioma ya seleccionado lo saca`() {
        val actual = montar(Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2, Idioma.EN to Nivel.C1)))

        composeTestRule.onAllNodesWithText("DE").onFirst().performClick()

        assertEquals(mapOf(Idioma.EN to Nivel.C1), actual().idiomasAprendidos)
    }

    @Test
    fun `el boton de nivel deja cambiarlo sin deseleccionar el idioma`() {
        val actual = montar(Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)))

        // El boton de nivel muestra el nivel actual ("B2"): tocarlo reabre el menu.
        composeTestRule.onNodeWithText("B2").performClick()
        composeTestRule.onNodeWithText("C1").performClick()

        assertEquals(mapOf(Idioma.DE to Nivel.C1), actual().idiomasAprendidos)
    }

    @Test
    fun `elegir un idioma de la aplicacion escribe base e interfaz con el mismo valor y apaga segun el sistema`() {
        val actual = montar(Ajustes(idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES, idiomaSegunSistema = true))

        // "DE" aparece dos veces en pantalla (la pestaña de idioma aprendido
        // y el selector de idioma de la aplicacion): se toma la ultima, que
        // es el selector de idioma de la aplicacion.
        composeTestRule.onAllNodesWithText("DE").onLast().performClick()

        val ajustes = actual()
        assertEquals(Idioma.DE, ajustes.idiomaBase)
        assertEquals(Idioma.DE, ajustes.idiomaInterfaz)
        assertEquals(false, ajustes.idiomaSegunSistema)
    }

    @Test
    fun `elegir Según el sistema activa idiomaSegunSistema`() {
        val actual = montar(Ajustes(idiomaSegunSistema = false))

        // "Según el sistema" aparece dos veces (idioma de la aplicacion y
        // modo de tema): la primera es el selector de idioma.
        composeTestRule.onAllNodesWithText("Según el sistema").onFirst().performClick()

        assertEquals(true, actual().idiomaSegunSistema)
    }

    @Test
    fun `mantener pulsado un idioma sin contenido muestra un toast en el idioma de interfaz`() {
        montar(Ajustes(idiomaInterfaz = Idioma.EN))

        composeTestRule.onAllNodesWithText("ES").onFirst().performTouchInput { longClick() }

        assertEquals(mensajeIdiomaNoDisponible(Idioma.EN), ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `el aviso de glosas en espanol aparece solo si el idioma de la aplicacion no es espanol`() {
        montar(Ajustes(idiomaInterfaz = Idioma.DE, idiomaBase = Idioma.DE))

        composeTestRule.onNodeWithText(
            "Las traducciones de vocabulario todavía solo existen en español; se van a mostrar en español mientras tanto.",
        ).assertExists()
    }
}
