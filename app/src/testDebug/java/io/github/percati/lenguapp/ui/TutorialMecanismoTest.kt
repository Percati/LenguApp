package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Tour
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * El mecanismo del foco (Ronda Q, tarea 1), con un catalogo de pruebas: ancla por testTag, el paso sin ancla se saltea y el tour sigue,
 * «Saltar» saltea todo, atras abandona (cuenta como visto), un tour se ofrece una sola vez y la pantalla de abajo no recibe toques.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h800dp")
@androidx.compose.foundation.ExperimentalFoundationApi
class TutorialMecanismoTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun paso(ancla: String?) = PasoTour(ancla, ClaveTexto.VELOCIDAD_AUDIO_TITULO, ClaveTexto.VELOCIDAD_AUDIO_AYUDA)

    private val vistosAvisados = mutableListOf<Tour>()
    private var saltoTodo = false
    private var clicsEnLaPantalla = 0

    private fun montar(pasos: List<PasoTour>, idioma: Idioma = Idioma.ES): GestorTours {
        val gestor = GestorTours(mapOf(Tour.FICHA to pasos))
        gestor.alVerTour = { vistosAvisados += it }
        gestor.alSaltarTodos = { saltoTodo = true }
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(400.dp, 800.dp)) {
                    Column {
                        Box(Modifier.size(100.dp, 60.dp).anclaTour("a1")) { Text("uno") }
                        Row { Button(onClick = { clicsEnLaPantalla++ }, modifier = Modifier.anclaTour("a2")) { Text("dos") } }
                    }
                    TutorialOverlay(gestor, idioma)
                }
            }
        }
        return gestor
    }

    private fun lanzar(gestor: GestorTours) {
        composeTestRule.runOnIdle { assertTrue(gestor.lanzar(Tour.FICHA)) }
        composeTestRule.waitForIdle()
    }

    private fun pasarElTiempoDeEspera() {
        composeTestRule.mainClock.advanceTimeBy(ESPERA_ANCLA_MS + 200)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `sin lanzar nada no hay recuadro`() {
        montar(listOf(paso("a1")))
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_TARJETA).assertCountEquals(0)
    }

    @Test
    fun `el foco se dibuja sobre el ancla y la tarjeta trae el paso, el titulo y los botones`() {
        val gestor = montar(listOf(paso("a1"), paso("a2"), paso(null)))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_FOCO).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).assertIsDisplayed()
        composeTestRule.onNodeWithText("Paso 1 de 3").assertExists()
        composeTestRule.onNodeWithText(textoTour(ClaveTexto.VELOCIDAD_AUDIO_TITULO, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SALTAR).assertExists()
        // el ancla quedo registrada con sus limites reales
        val rect = gestor.registro.rectDe("a1")!!
        assertEquals(100f, rect.width, 1f)
        assertEquals(60f, rect.height, 1f)
    }

    @Test
    fun `Siguiente avanza, el ultimo paso dice Listo y Listo termina y cuenta como visto`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Paso 2 de 2").assertExists()
        composeTestRule.onAllNodesWithTag(TAG_TOUR_SIGUIENTE).assertCountEquals(0)
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf(Tour.FICHA), vistosAvisados)
        assertNull(gestor.activo)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `un paso cuya ancla no existe se saltea y el tour sigue`() {
        val gestor = montar(listOf(paso("a1"), paso("no-existe"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        pasarElTiempoDeEspera()
        // del paso 1 saltó al 3 (indice 2): el 2 no tenia ancla
        assertEquals(2, gestor.indice)
        composeTestRule.onNodeWithText("Paso 3 de 3").assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).assertExists()
    }

    @Test
    fun `si ninguna ancla existe, el tour se saltea entero sin oscurecer nada y cuenta como visto`() {
        val gestor = montar(listOf(paso("nada-1"), paso("nada-2")))
        lanzar(gestor)
        // mientras espera el ancla no se dibuja nada: nunca una pantalla oscura sin nada senalado
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
        repeat(3) { pasarElTiempoDeEspera() }
        assertNull(gestor.activo)
        assertEquals(listOf(Tour.FICHA), vistosAvisados)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `Saltar termina todo y avisa que se salteo todo, sin contar solo este tour`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SALTAR).performClick()
        composeTestRule.waitForIdle()
        assertTrue(saltoTodo)
        assertNull(gestor.activo)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `abandonar con el boton atras cuenta como visto`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        lanzar(gestor)
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        assertNull(gestor.activo)
        assertEquals(listOf(Tour.FICHA), vistosAvisados)
    }

    @Test
    fun `un tour se ofrece una sola vez, no con otro en marcha y no si se salteo todo`() {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("a1")), Tour.AJUSTES to listOf(paso("a1"))))
        assertTrue(gestor.puedeLanzar(Tour.FICHA))
        assertTrue(gestor.lanzar(Tour.FICHA))
        assertFalse("otro tour con uno en marcha", gestor.puedeLanzar(Tour.AJUSTES))
        gestor.terminar()
        gestor.vistos = setOf(Tour.FICHA)
        assertFalse("ya visto", gestor.puedeLanzar(Tour.FICHA))
        assertTrue(gestor.puedeLanzar(Tour.AJUSTES))
        gestor.salteados = true
        assertFalse("se salteo todo", gestor.puedeLanzar(Tour.AJUSTES))
        assertFalse("un tour sin pasos no se lanza", GestorTours(emptyMap()).puedeLanzar(Tour.FICHA))
    }

    @Test
    fun `mientras dura el tour la pantalla de abajo no recibe toques`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        composeTestRule.onNodeWithText("dos").performClick()
        assertEquals("sin tour, el boton responde", 1, clicsEnLaPantalla)
        lanzar(gestor)
        composeTestRule.onNodeWithText("dos").performClick()
        assertEquals("con el tour en marcha, el toque no llega al boton de abajo", 1, clicsEnLaPantalla)
    }

    @Test
    fun `los botones tienen su descripcion en el idioma de la app y 48 dp de alto como minimo`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")), Idioma.DE)
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).assertHeightIsAtLeast(48.dp).assert(hasContentDescription(descripcionTourSiguiente(Idioma.DE)))
        composeTestRule.onNodeWithTag(TAG_TOUR_SALTAR).assertHeightIsAtLeast(48.dp).assert(hasContentDescription(descripcionTourSaltar(Idioma.DE)))
        composeTestRule.onNodeWithText("Schritt 1 von 2").assertExists()
        composeTestRule.onNodeWithText("Weiter").assertExists()
    }

    @Test
    fun `los textos del recuadro estan en los 6 idiomas`() {
        for (i in Idioma.entries) {
            for (t in listOf(etiquetaTourSiguiente(i), etiquetaTourSaltar(i), etiquetaTourListo(i), descripcionTourSiguiente(i), descripcionTourSaltar(i), descripcionTourListo(i))) {
                assertTrue("$i: texto vacio", t.isNotBlank())
            }
            val paso = etiquetaTourPaso(2, 7, i)
            assertTrue("$i: $paso", paso.contains("2") && paso.contains("7") && !paso.contains("{"))
        }
        assertEquals("Paso 2 de 7", etiquetaTourPaso(2, 7, Idioma.ES))
    }

    // ---------------- el margen al traer el ancla a la vista (Ronda R, tarea 1) ----------------

    @Test
    fun `se pide a la vista el rectangulo inflado arriba y abajo, no el nodo pelado`() {
        val gestor = montar(listOf(paso("a1")))
        lanzar(gestor)
        var pedido: androidx.compose.ui.geometry.Rect? = null
        composeTestRule.runOnIdle {
            kotlinx.coroutines.runBlocking { gestor.registro.traerALaVista("a1", margenPx = 32f) { _, rect -> pedido = rect } }
        }
        val rect = pedido!!
        assertEquals("el ancla mide 100 x 60", 100f, rect.width, 0.5f)
        assertEquals("inflado 32 arriba y abajo: 60 + 64", 124f, rect.height, 0.5f)
        assertEquals(-32f, rect.top, 0.01f)
        assertEquals(92f, rect.bottom, 0.5f)
        // la funcion pura, con otro margen
        assertEquals(androidx.compose.ui.geometry.Rect(0f, -40f, 100f, 100f), rectanguloConMargen(100f, 60f, 40f))
        assertEquals(32, MARGEN_AL_TRAER_DP)
    }

    @Test
    fun `un ancla lejos, dentro de un scroll, queda despegada de los dos bordes al traerla`() {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("lejos"), paso("cerca"))))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(400.dp, 600.dp)) {
                    Column(Modifier.size(400.dp, 600.dp).verticalScroll(rememberScrollState())) {
                        Box(Modifier.size(400.dp, 60.dp).anclaTour("cerca")) { Text("arriba") }
                        Box(Modifier.size(400.dp, 1500.dp))
                        Box(Modifier.size(400.dp, 60.dp).anclaTour("lejos")) { Text("lejos") }
                        Box(Modifier.size(400.dp, 800.dp))
                    }
                    TutorialOverlay(gestor, Idioma.ES)
                }
            }
        }
        lanzar(gestor)
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()
        val rect = gestor.registro.rectDe("lejos")!!
        assertTrue("el ancla no queda pegada arriba: top=${rect.top}", rect.top >= 32f - 1f)
        assertTrue("el ancla no queda pegada abajo: bottom=${rect.bottom} de 600", rect.bottom <= 600f - 32f + 1f)
    }

    @Test
    fun `un ancla que ya esta a la vista y despegada de los bordes no desplaza nada`() {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("medio"))))
        val estado = androidx.compose.foundation.ScrollState(0)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(400.dp, 600.dp)) {
                    Column(Modifier.size(400.dp, 600.dp).verticalScroll(estado)) {
                        Box(Modifier.size(400.dp, 250.dp))
                        Box(Modifier.size(400.dp, 60.dp).anclaTour("medio")) { Text("medio") }
                        Box(Modifier.size(400.dp, 1500.dp))
                    }
                    TutorialOverlay(gestor, Idioma.ES)
                }
            }
        }
        lanzar(gestor)
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.waitForIdle()
        assertEquals("sin salto gratuito", 0, estado.value)
    }
}
