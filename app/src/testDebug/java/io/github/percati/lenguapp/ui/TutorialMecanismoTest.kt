package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.boundsInRoot
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
        // del paso 1 salto al 3 (indice 2): el 2 no tenia ancla, y el contador no salta numeros
        assertEquals(2, gestor.indice)
        composeTestRule.onNodeWithText("Paso 2 de 2").assertExists()
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
    fun `Paso n de total no salta numeros cuando se saltea un paso, y al retroceder baja`() {
        val gestor = montar(listOf(paso("a1"), paso("no-existe"), paso("a2"), paso("a1")))
        lanzar(gestor)
        // el 2.o no tiene ancla: se sabe de entrada, asi que ya empieza en «1 de 3»
        composeTestRule.onNodeWithText("Paso 1 de 3").assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        pasarElTiempoDeEspera()
        composeTestRule.onNodeWithText("Paso 2 de 3").assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Paso 3 de 3").assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_ATRAS).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Paso 2 de 3").assertExists()
        composeTestRule.onAllNodesWithTag(TAG_TOUR_LISTO).assertCountEquals(0)
    }

    @Test
    fun `si los pasos que quedan no tienen ancla, el paso actual ya dice Listo`() {
        val gestor = montar(listOf(paso("a1"), paso("no-existe-1"), paso("no-existe-2")))
        lanzar(gestor)
        composeTestRule.onNodeWithText("Paso 1 de 1").assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).assertExists()
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
    fun `el boton atras del sistema vuelve un paso, y solo en el primero abandona y cuenta como visto`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, gestor.indice)
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        assertEquals("el gesto vuelve un paso en vez de abandonar", 0, gestor.indice)
        assertTrue(vistosAvisados.isEmpty())
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        assertNull(gestor.activo)
        assertEquals(listOf(Tour.FICHA), vistosAvisados)
    }

    @Test
    fun `Atras de la tarjeta no se muestra en el primer paso, y despues vuelve al paso mostrado anterior`() {
        val gestor = montar(listOf(paso("a1"), paso("a2"), paso("a1")))
        lanzar(gestor)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_ATRAS).assertCountEquals(0)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_TOUR_ATRAS).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_ATRAS).performClick()
        composeTestRule.waitForIdle()
        assertEquals(0, gestor.indice)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_ATRAS).assertCountEquals(0)
        composeTestRule.onNodeWithText("Paso 1 de 3").assertExists()
    }

    @Test
    fun `Atras vuelve al paso mostrado, no al indice anterior, y no vuelve a mostrar el que se salteo`() {
        val gestor = montar(listOf(paso("a1"), paso("no-existe"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        pasarElTiempoDeEspera()
        assertEquals("salto del 1 al 3", 2, gestor.indice)
        composeTestRule.onNodeWithTag(TAG_TOUR_ATRAS).performClick()
        composeTestRule.waitForIdle()
        assertEquals("volvio al 1 (el 2 se saltea tambien al retroceder)", 0, gestor.indice)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_ATRAS).assertCountEquals(0)
    }

    @Test
    fun `el boton Atras tiene 48 dp y su descripcion en el idioma de la app, y los textos estan en los 6 idiomas`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")), Idioma.FR)
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_TOUR_ATRAS).assertHeightIsAtLeast(48.dp).assert(hasContentDescription(descripcionTourAtras(Idioma.FR)))
        for (i in Idioma.entries) {
            assertTrue(etiquetaTourAtras(i).isNotBlank() && descripcionTourAtras(i).isNotBlank())
        }
        assertEquals("Atrás", etiquetaTourAtras(Idioma.ES))
        assertEquals("Back", etiquetaTourAtras(Idioma.EN))
    }

    @Test
    @Config(qualifiers = "w140dp-h800dp")
    fun `en una pantalla muy angosta los tres botones se reparten en lineas, ninguno sale de la pantalla`() {
        val gestor = montar(listOf(paso("a1"), paso("a2")))
        lanzar(gestor)
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        composeTestRule.waitForIdle()
        for (tag in listOf(TAG_TOUR_ATRAS, TAG_TOUR_SALTAR, TAG_TOUR_LISTO)) {
            val b = composeTestRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
            assertTrue("$tag fuera de la pantalla: $b", b.left >= -0.5f && b.right <= 140.5f)
        }
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
        assertEquals(96, MARGEN_AL_TRAER_DP)
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
        // 96 dp (~1,5 cm) de los dos bordes, porque en 600 dp de alto entra: 60 + 96 + 96 = 252
        assertTrue("el ancla no queda pegada arriba: top=${rect.top}", rect.top >= 96f - 1f)
        assertTrue("el ancla no queda pegada abajo: bottom=${rect.bottom} de 600", rect.bottom <= 600f - 96f + 1f)
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

    @Test
    fun `si los dos margenes no caben se prioriza el de arriba y se recorta el de abajo`() {
        // 400 de alto + 96 + 96 = 592, y solo caben 500: arriba se queda con los 96 enteros
        val r = rectanguloConMargen(100f, 400f, 96f, altoMaximo = 500f)
        assertEquals(-96f, r.top, 0.01f)
        assertEquals(404f, r.bottom, 0.01f)
        assertEquals(500f, r.height, 0.01f)
        // si caben, nada cambia
        assertEquals(androidx.compose.ui.geometry.Rect(0f, -96f, 100f, 496f), rectanguloConMargen(100f, 400f, 96f, altoMaximo = 700f))
        // un ancla mas alta que todo lo que cabe: se pide el comienzo, con su margen de arriba
        val alta = rectanguloConMargen(100f, 3000f, 96f, altoMaximo = 500f)
        assertEquals(-96f, alta.top, 0.01f)
        assertEquals(404f, alta.bottom, 0.01f)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun `en una pantalla de 360 x 640 un ancla alta no entra con los dos margenes, y queda con el de arriba`() {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("alta"))))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(360.dp, 640.dp)) {
                    Column(Modifier.size(360.dp, 640.dp).verticalScroll(rememberScrollState())) {
                        Box(Modifier.size(360.dp, 1200.dp))
                        Box(Modifier.size(360.dp, 400.dp).anclaTour("alta")) { Text("alta") }
                        Box(Modifier.size(360.dp, 1200.dp))
                    }
                    TutorialOverlay(gestor, Idioma.ES)
                }
            }
        }
        lanzar(gestor)
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()
        val rect = gestor.registro.rectDe("alta")!!
        // 400 + 96 + 96 = 592 > 80 % de 640 (512): se prioriza el margen de arriba
        println("MEDIDA-360x640 top=${rect.top} bottom=${rect.bottom}")
        assertTrue("el comienzo del ancla queda a >= 96 dp del borde de arriba: top=${rect.top}", rect.top >= 96f - 1f)
    }

    // --- Ronda S, T2: el agujero y la decision del lado de la tarjeta, recortados a la pantalla ---

    @Test
    fun `recortarAlRecuadro recorta lo que se pasa y da null si no se cruza`() {
        val r = androidx.compose.ui.geometry.Rect(-50f, 250f, 300f, 1250f)
        assertEquals(androidx.compose.ui.geometry.Rect(0f, 250f, 300f, 600f), recortarAlRecuadro(r, 400f, 600f))
        assertNull("debajo de la pantalla", recortarAlRecuadro(androidx.compose.ui.geometry.Rect(0f, 700f, 100f, 800f), 400f, 600f))
        assertNull("arriba de la pantalla", recortarAlRecuadro(androidx.compose.ui.geometry.Rect(0f, -300f, 100f, -10f), 400f, 600f))
        assertNull("justo en el borde, sin area", recortarAlRecuadro(androidx.compose.ui.geometry.Rect(0f, 600f, 100f, 700f), 400f, 600f))
        // un ancla entera adentro queda igual
        val entera = androidx.compose.ui.geometry.Rect(10f, 20f, 110f, 80f)
        assertEquals(entera, recortarAlRecuadro(entera, 400f, 600f))
    }

    @Test
    fun `el lado de la tarjeta se decide con el centro del foco recortado, no con el del ancla entera`() {
        // el ancla entera tiene el centro en y = 600 (mitad de abajo en 800), pero lo que se ve es 100..800: centro 450, tambien abajo
        // del medio; y uno que arranca arriba (-1000..300): sin recortar el centro es -350, recortado 150. Ambos lados se deciden con lo visible.
        val a = recortarAlRecuadro(androidx.compose.ui.geometry.Rect(0f, 100f, 100f, 1100f), 400f, 800f)!!
        assertEquals(450f, a.center.y, 0.01f)
        assertFalse(focoEnLaMitadDeArriba(a, 800f))
        val b = recortarAlRecuadro(androidx.compose.ui.geometry.Rect(0f, -1000f, 100f, 300f), 400f, 800f)!!
        assertEquals(150f, b.center.y, 0.01f)
        assertTrue(focoEnLaMitadDeArriba(b, 800f))
        assertFalse("sin alto no hay mitad", focoEnLaMitadDeArriba(b, 0f))
    }

    private fun montarConAncla(topDp: Int, altoDp: Int): GestorTours {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("fuera"))))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(400.dp, 800.dp)) {
                    Box(Modifier.offset(y = topDp.dp).size(100.dp, altoDp.dp).anclaTour("fuera"))
                    TutorialOverlay(gestor, Idioma.ES)
                }
            }
        }
        return gestor
    }

    @Test
    fun `un ancla que se pasa de la pantalla pone la tarjeta del lado opuesto al pedazo que se ve`() {
        // el pedazo visible es 500..800 (abajo): la tarjeta va arriba; el agujero se dibuja recortado a la pantalla
        val gestor = montarConAncla(topDp = 500, altoDp = 1800)
        lanzar(gestor)
        pasarElTiempoDeEspera()
        composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).assertIsDisplayed()
        val tarjeta = composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).fetchSemanticsNode().boundsInRoot
        assertTrue("la tarjeta esta arriba: top=${tarjeta.top}", tarjeta.top < 200f)
    }

    @Test
    fun `un ancla completamente fuera de la pantalla se trata como un paso sin foco, con la tarjeta abajo`() {
        val gestor = montarConAncla(topDp = 1000, altoDp = 60)
        lanzar(gestor)
        pasarElTiempoDeEspera()
        composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).assertIsDisplayed()
        val tarjeta = composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).fetchSemanticsNode().boundsInRoot
        assertTrue("la tarjeta esta abajo, no en el centro: top=${tarjeta.top}", tarjeta.top > 400f)
    }

    private fun medirEnPantallaChica(altoAncla: Int, altoVisor: Int): androidx.compose.ui.geometry.Rect {
        val gestor = GestorTours(mapOf(Tour.FICHA to listOf(paso("alta"))))
        composeTestRule.setContent {
            CompositionLocalProvider(LocalGestorTours provides gestor) {
                Box(Modifier.size(360.dp, 640.dp)) {
                    // un visor mas bajo que la pantalla, como en la app real (barra de arriba y barra de abajo se comen alto)
                    Column(Modifier.offset(y = 100.dp).size(360.dp, altoVisor.dp).verticalScroll(rememberScrollState())) {
                        Box(Modifier.size(360.dp, 1200.dp))
                        Box(Modifier.size(360.dp, altoAncla.dp).anclaTour("alta")) { Text("alta") }
                        Box(Modifier.size(360.dp, 1200.dp))
                    }
                    TutorialOverlay(gestor, Idioma.ES)
                }
            }
        }
        lanzar(gestor)
        composeTestRule.mainClock.advanceTimeBy(2000)
        composeTestRule.waitForIdle()
        return gestor.registro.rectDe("alta")!!
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun `en 360 x 640 con un visor de 440 dp, un ancla alta de 400 queda con el comienzo a 96 dp del borde de arriba del visor`() {
        val r = medirEnPantallaChica(altoAncla = 400, altoVisor = 440)
        println("MEDIDA-visor440-ancla400 arriba=${r.top - 100f} abajo=${100f + 440f - r.bottom}")
        assertEquals("el comienzo del ancla, a 96 dp del borde de arriba del visor", 96f, r.top - 100f, 2f)
    }

    @Test
    @Config(qualifiers = "w360dp-h640dp")
    fun `en 360 x 640 con un visor de 440 dp, un ancla de 150 queda a 96 dp o mas de los dos bordes`() {
        val r = medirEnPantallaChica(altoAncla = 150, altoVisor = 440)
        println("MEDIDA-visor440-ancla150 arriba=${r.top - 100f} abajo=${100f + 440f - r.bottom}")
        assertTrue("arriba ${r.top - 100f}", r.top - 100f >= 95f)
        assertTrue("abajo ${100f + 440f - r.bottom}", 100f + 440f - r.bottom >= 95f)
    }
}
