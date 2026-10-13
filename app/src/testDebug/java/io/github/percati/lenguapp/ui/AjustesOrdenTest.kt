package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Orden de Ajustes (Ronda P, tarea 1): los ajustes arriba; al final, el nombre de la app, «Ver la bienvenida otra vez» y, ultima, «Acerca de». */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class AjustesOrdenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun mostrar() {
        composeTestRule.setContent {
            AjustesScreen(
                ajustes = Ajustes(), idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2), Idioma.EN to setOf(Nivel.C1)),
                idiomaAplicacionEfectivo = Idioma.ES, onAjustesCambiados = {}, onVolver = {},
            )
        }
    }

    private fun arribaDeTag(tag: String) = composeTestRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.top
    private fun arribaDeTexto(texto: String) = composeTestRule.onNodeWithText(texto).fetchSemanticsNode().boundsInRoot.top

    @Test
    fun `Acerca de es la ultima fila y Ver la bienvenida otra vez la anterior`() {
        mostrar()
        val bienvenida = arribaDeTag(TAG_VER_BIENVENIDA)
        val acerca = arribaDeTag(TAG_ACERCA_DE)
        assertTrue("Acerca de ($acerca) debajo de la bienvenida ($bienvenida)", acerca > bienvenida)
        // nada de la pantalla queda debajo de «Acerca de»: su borde inferior es el mas bajo de todo el contenido
        val ultimaOpcion = composeTestRule.onNodeWithTag(TAG_VELOCIDAD_AUDIO_PREFIJO + 100).fetchSemanticsNode().boundsInRoot.bottom
        assertTrue("la bienvenida va despues de la ultima opcion (velocidad del audio)", bienvenida > ultimaOpcion)
    }

    @Test
    fun `los ajustes van arriba y el nombre de la app, abajo de ellos y antes de las dos filas finales`() {
        mostrar()
        val titulo = arribaDeTexto(etiquetaAjustes(Idioma.ES))
        val idiomasQueSePractican = arribaDeTexto(etiquetaIdiomasAprendidos(Idioma.ES))
        val idiomaDeLaApp = arribaDeTexto(etiquetaIdiomaApp(Idioma.ES))
        val tipografia = arribaDeTexto(etiquetaFuente(Idioma.ES))
        val velocidad = arribaDeTexto(etiquetaVelocidadAudio(Idioma.ES))
        val nombre = arribaDeTexto(NOMBRE_APP)
        val bienvenida = arribaDeTag(TAG_VER_BIENVENIDA)
        val acerca = arribaDeTag(TAG_ACERCA_DE)
        val orden = listOf(titulo, idiomasQueSePractican, idiomaDeLaApp, tipografia, velocidad, nombre, bienvenida, acerca)
        assertTrue("orden vertical: $orden", orden == orden.sorted() && orden.toSet().size == orden.size)
    }
}
