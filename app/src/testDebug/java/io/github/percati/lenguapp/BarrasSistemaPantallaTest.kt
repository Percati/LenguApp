package io.github.percati.lenguapp

import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import io.github.percati.lenguapp.ui.etiquetaAcercaDe
import io.github.percati.lenguapp.ui.etiquetaAjustes
import io.github.percati.lenguapp.ui.etiquetaVolver
import io.github.percati.lenguapp.ui.textoAcercaPrueba
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.time.LocalDate

/**
 * Pantalla completa (Ronda K, tarea 1): con barras del sistema simuladas (estado arriba, navegacion abajo) el
 * contenido no queda tapado -- los encabezados arrancan debajo de la barra de estado y el final de un scroll
 * deja la ultima fila por encima de la barra de navegacion.
 */
@RunWith(RobolectricTestRunner::class)
class BarrasSistemaPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val barraEstadoPx = 90
    private val barraNavegacionPx = 150

    private fun carpetaAssets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val contenido: Map<String, ContenidoSemanal> by lazy {
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }
    }

    private fun resolverDePrueba(idioma: Idioma, nivel: Nivel, fecha: LocalDate): ResultadoSemana {
        val archivo = File(carpetaAssets(), "calendario/calendario_2026_${idioma.name.lowercase()}_${nivel.name}.json")
        val calendario = if (archivo.isFile) CalendarioCargado.Encontrado(parsearCalendario(archivo.readText())) else CalendarioCargado.SinCalendarioParaIdiomaONivel
        return resolverContenidoDeLaSemana(fecha, idioma, nivel, calendario, contenido)
    }

    private fun mostrarConBarras() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2), Idioma.EN to setOf(Nivel.C1)),
                resolver = ::resolverDePrueba,
                fechaInicial = LocalDate.of(2026, 9, 7),
                onGuardarAjustes = {},
            )
        }
        // Las barras del sistema, simuladas: lo que entregaria Android a una app en pantalla completa.
        composeTestRule.runOnUiThread {
            val contenidoVista = composeTestRule.activity.findViewById<ViewGroup>(android.R.id.content)
            val insets = WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.statusBars(), Insets.of(0, barraEstadoPx, 0, 0))
                .setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(0, 0, 0, barraNavegacionPx))
                .build()
            ViewCompat.dispatchApplyWindowInsets(contenidoVista.getChildAt(0), insets)
        }
        composeTestRule.waitForIdle()
    }

    private fun arriba(texto: String) =
        composeTestRule.onNodeWithText(texto).fetchSemanticsNode().boundsInRoot.top

    private fun abajo(texto: String) =
        composeTestRule.onNodeWithText(texto).fetchSemanticsNode().boundsInRoot.bottom

    private fun altoDeLaPantalla() = composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.bottom

    @Test
    fun `la pantalla principal arranca debajo de la barra de estado`() {
        mostrarConBarras()
        assertTrue("el boton de Ajustes (${arriba(etiquetaAjustes(Idioma.ES))}) no puede quedar bajo la barra de estado ($barraEstadoPx)", arriba(etiquetaAjustes(Idioma.ES)) >= barraEstadoPx)
    }

    @Test
    fun `Ajustes y Acerca de arrancan bajo la barra de estado y su final queda sobre la barra de navegacion`() {
        mostrarConBarras()
        composeTestRule.onNodeWithText(etiquetaAjustes(Idioma.ES)).performClick()
        assertTrue("Volver en Ajustes", arriba("< ${etiquetaVolver(Idioma.ES)}") >= barraEstadoPx)
        composeTestRule.onNodeWithText(etiquetaAcercaDe(Idioma.ES)).performScrollTo().performClick()

        assertTrue("Volver en Acerca de", arriba("< ${etiquetaVolver(Idioma.ES)}") >= barraEstadoPx)
        // al final del scroll, la ultima linea se ve entera por encima de la barra de navegacion
        val ultima = textoAcercaPrueba(Idioma.ES)
        // hasta el tope del scroll (performScrollTo se detiene en cuanto el nodo asoma, aunque sea bajo la barra)
        composeTestRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.VerticalScrollAxisRange))
            .performSemanticsAction(SemanticsActions.ScrollBy) { it(0f, 100000f) }
        composeTestRule.waitForIdle()
        val limite = altoDeLaPantalla() - barraNavegacionPx
        assertTrue("la ultima linea de Acerca de (${abajo(ultima)}) debe terminar sobre la barra de navegacion ($limite)", abajo(ultima) <= limite)
    }

    @Test
    fun `sin barras del sistema nada se corre`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2)),
                resolver = ::resolverDePrueba,
                fechaInicial = LocalDate.of(2026, 9, 7),
                onGuardarAjustes = {},
            )
        }
        assertTrue(arriba(etiquetaAjustes(Idioma.ES)) < barraEstadoPx)
        assertEquals(0f, composeTestRule.onRoot().fetchSemanticsNode().boundsInRoot.top, 0.5f)
    }
}
