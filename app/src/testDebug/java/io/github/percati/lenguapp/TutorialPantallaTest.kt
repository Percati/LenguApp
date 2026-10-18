package io.github.percati.lenguapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.serializarRespaldo
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Tour
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import io.github.percati.lenguapp.ui.CATALOGO_TOURS
import io.github.percati.lenguapp.ui.ClaveTexto
import io.github.percati.lenguapp.ui.GestorTours
import io.github.percati.lenguapp.ui.TAG_TOUR_FOCO
import io.github.percati.lenguapp.ui.TAG_TOUR_LISTO
import io.github.percati.lenguapp.ui.TAG_TOUR_SALTAR
import io.github.percati.lenguapp.ui.TAG_TOUR_SIGUIENTE
import io.github.percati.lenguapp.ui.TAG_TOUR_TARJETA
import io.github.percati.lenguapp.ui.TAG_VER_BIENVENIDA
import io.github.percati.lenguapp.ui.descripcionSeccion
import io.github.percati.lenguapp.ui.etiquetaDesafioFinde
import io.github.percati.lenguapp.ui.etiquetaTourPaso
import io.github.percati.lenguapp.ui.textoTour
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate

/**
 * El tutorial de punta a punta (Ronda Q), sobre la app real y los assets reales:
 *  - TODAS las anclas de TODOS los pasos existen en su pantalla (la red que evita que el tutorial se pudra en silencio),
 *  - cada tour se lanza cuando corresponde y una sola vez, «Saltar» saltea todo, abandonar cuenta como visto,
 *  - sin ficha el tour de la ficha no se lanza ni se da por visto, y el del desafio solo con el interruptor visible.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class TutorialPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

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

    private val lunes = LocalDate.of(2026, 9, 7)
    private val sabado = LocalDate.of(2026, 9, 12)
    private val lunesA2 = LocalDate.of(2026, 10, 5) // semana 41: la primera con contenido de A2
    private val sinContenido = LocalDate.of(2035, 1, 3)

    private val guardados = mutableListOf<Ajustes>()
    private lateinit var gestor: GestorTours

    private fun mostrar(fecha: LocalDate, vistos: Set<Tour>, salteados: Boolean = false, nivel: Nivel = Nivel.B2, conPlanilla: Boolean = false) {
        gestor = GestorTours(CATALOGO_TOURS)
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(
                    idiomasAprendidos = mapOf(Idioma.DE to nivel), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                    bienvenidaCompletada = true, toursVistos = vistos, toursSalteados = salteados,
                ),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.A2, Nivel.B2), Idioma.EN to setOf(Nivel.C1)),
                planillaTextos = if (conPlanilla) io.github.percati.lenguapp.datos.cargarPlanillaTextosDesdeAssets(composeTestRule.activity) else emptyMap(),
                resolver = ::resolverDePrueba,
                fechaInicial = fecha,
                onGuardarAjustes = { guardados += it },
                contenidoTodos = contenido.values.toList(),
                gestorTours = gestor,
            )
        }
        asentar()
    }

    private fun asentar() {
        composeTestRule.waitForIdle()
        // lo bastante para que un paso sin ancla agote su espera (600 ms) y se saltee
        composeTestRule.mainClock.advanceTimeBy(800)
        composeTestRule.waitForIdle()
    }

    private val todosVistos = Tour.entries.toSet()

    private fun anclasDe(tour: Tour) = CATALOGO_TOURS.getValue(tour).mapNotNull { it.ancla }.toSet()

    private fun verificarAnclas(tour: Tour) {
        val faltan = anclasDe(tour) - gestor.registro.ids()
        assertTrue("$tour: anclas que no existen en su pantalla: $faltan (hay ${gestor.registro.ids()})", faltan.isEmpty())
    }

    // ---------------- la red: todas las anclas existen ----------------

    @Test
    fun `todas las anclas del tour de la ficha existen en la pantalla principal, en una ficha A2 con planilla`() {
        // A2: existe el interruptor de traducir; con planillaTextos existe el boton de la planilla
        mostrar(lunesA2, todosVistos, nivel = Nivel.A2, conPlanilla = true)
        verificarAnclas(Tour.FICHA)
    }

    @Test
    fun `las anclas de la ficha estan en el orden de la pantalla, sin zigzag`() {
        mostrar(lunesA2, todosVistos, nivel = Nivel.A2, conPlanilla = true)
        val arriba = listOf(
            "tour-ficha-titulo", "tour-ficha-descripcion", "tour-ficha-errores", "tour-ficha-vocabulario",
            "tour-ficha-mision", "tour-ficha-autochequeo", "tour-ficha-prompt", "tour-ficha-planilla",
        ).map { gestor.registro.rectDe(it)!!.top }
        assertEquals("de arriba hacia abajo: $arriba", arriba.sorted(), arriba)
        assertEquals("sin repetir posiciones", arriba.size, arriba.toSet().size)
        // el interruptor de traducir esta en la franja fija, encima del titulo, pero el tour lo toma en el paso 2
        assertTrue(gestor.registro.rectDe("tour-ficha-traducir")!!.top < gestor.registro.rectDe("tour-ficha-titulo")!!.top)
    }

    @Test
    fun `en B2 sin planilla el tour de la ficha se saltea el interruptor y la planilla, y cuenta 8 pasos sin saltar numeros`() {
        mostrar(lunes, vistos = emptySet())
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 8, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
        // del paso 1 al 3 del catalogo (el interruptor no existe en B2): se muestra como «2 de 8»
        composeTestRule.onNodeWithText(etiquetaTourPaso(2, 8, Idioma.ES)).assertExists()
    }

    @Test
    fun `todas las anclas del tour de la Biblioteca existen`() {
        mostrar(lunes, todosVistos)
        composeTestRule.onNodeWithText("Biblioteca").performClick()
        asentar()
        verificarAnclas(Tour.BIBLIOTECA)
    }

    @Test
    fun `todas las anclas del tour de Guardados existen, con la lista vacia`() {
        mostrar(lunes, todosVistos)
        composeTestRule.onNodeWithText("Guardados").performClick()
        asentar()
        verificarAnclas(Tour.GUARDADOS)
    }

    @Test
    fun `todas las anclas del tour de Ajustes existen`() {
        mostrar(lunes, todosVistos)
        composeTestRule.onNodeWithText("Ajustes").performClick()
        asentar()
        verificarAnclas(Tour.AJUSTES)
    }

    @Test
    fun `todas las anclas del tour del desafio existen, una vez visible el interruptor y encendido`() {
        mostrar(sabado, todosVistos)
        assertTrue("el interruptor se ve el sabado", "tour-desafio-interruptor" in gestor.registro.ids())
        composeTestRule.onNodeWithText(etiquetaDesafioFinde(Idioma.ES)).performClick()
        asentar()
        verificarAnclas(Tour.DESAFIO)
    }

    @Test
    fun `ningun tour de la lista queda sin verificar`() {
        // si se agrega un tour al enum hay que sumar su prueba de anclas arriba
        assertEquals(setOf(Tour.FICHA, Tour.BIBLIOTECA, Tour.GUARDADOS, Tour.AJUSTES, Tour.DESAFIO), Tour.entries.toSet())
        assertEquals(Tour.entries.toSet(), CATALOGO_TOURS.keys)
    }

    // ---------------- cuando se lanzan ----------------

    @Test
    fun `el tour de la ficha se lanza solo la primera vez que hay ficha, recorre sus 8 pasos y cuenta como visto`() {
        mostrar(lunes, vistos = emptySet())
        composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).assertExists()
        for (n in 1..8) {
            composeTestRule.onNodeWithText(etiquetaTourPaso(n, 8, Idioma.ES)).assertExists()
            if (n < 8) {
                composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
                asentar()
            }
        }
        // el cierre ya no repite el parrafo de «que es esto y que no es» (vive en la bienvenida y en «Acerca de»)
        composeTestRule.onAllNodesWithText(textoTour(ClaveTexto.ACERCA_QUE_ES_TEXTO, Idioma.ES)).assertCountEquals(0)
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).performClick()
        asentar()
        assertTrue(Tour.FICHA in guardados.last().toursVistos)
        assertFalse(guardados.last().toursSalteados)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `Saltar saltea todos los tours que faltan, no solo el actual`() {
        mostrar(lunes, vistos = emptySet())
        composeTestRule.onNodeWithTag(TAG_TOUR_SALTAR).performClick()
        asentar()
        val ajustes = guardados.last()
        assertEquals(Tour.entries.toSet(), ajustes.toursVistos)
        assertTrue(ajustes.toursSalteados)
        // y al entrar a la Biblioteca ya no hay tour
        composeTestRule.onNodeWithText("Biblioteca").performClick()
        asentar()
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `el boton atras del sistema vuelve un paso y solo en el primero abandona, y eso cuenta como visto`() {
        mostrar(lunes, vistos = emptySet())
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(2, 8, Idioma.ES)).assertExists()
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 8, Idioma.ES)).assertExists()
        assertTrue("todavia no se dio por visto", guardados.none { Tour.FICHA in it.toursVistos })
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        asentar()
        assertTrue(Tour.FICHA in guardados.last().toursVistos)
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }

    @Test
    fun `sin ficha el tour de la ficha no se lanza y queda pendiente, no se da por visto`() {
        mostrar(sinContenido, vistos = emptySet())
        composeTestRule.onAllNodesWithTag(TAG_TOUR_TARJETA).assertCountEquals(0)
        assertTrue("no se guardo nada: el tour sigue pendiente", guardados.none { Tour.FICHA in it.toursVistos })
        assertNotNull(gestor)
        assertFalse(Tour.FICHA in gestor.vistos)
    }

    @Test
    fun `el tour de la Biblioteca se lanza al entrar la primera vez y no la segunda`() {
        mostrar(lunes, vistos = setOf(Tour.FICHA))
        composeTestRule.onAllNodesWithTag(TAG_TOUR_TARJETA).assertCountEquals(0) // la ficha ya se vio
        composeTestRule.onNodeWithText("Biblioteca").performClick()
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 5, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SALTAR).performClick() // termina sin recorrerlo: vale igual
        asentar()
        assertTrue(Tour.BIBLIOTECA in guardados.last().toursVistos)
    }

    @Test
    fun `el tour del desafio solo se lanza con el interruptor visible, el sabado`() {
        // lunes: el interruptor no existe, no hay tour aunque la ficha ya se vio
        mostrar(lunes, vistos = setOf(Tour.FICHA))
        composeTestRule.onAllNodesWithTag(TAG_TOUR_TARJETA).assertCountEquals(0)
    }

    @Test
    fun `el sabado, con la ficha ya vista, el tour del desafio senala el interruptor y NO lo enciende (regla dura 13)`() {
        mostrar(sabado, vistos = setOf(Tour.FICHA))
        // el interruptor esta apagado: la consigna no existe, su paso se saltea y el contador lo sabe de entrada
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 1, Idioma.ES)).assertExists()
        assertFalse("la consigna no aparece: el tour no encendio el interruptor", "tour-desafio-consigna" in gestor.registro.ids())
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).performClick()
        asentar()
        assertTrue(Tour.DESAFIO in guardados.last().toursVistos)
        assertFalse("sigue apagado", "tour-desafio-consigna" in gestor.registro.ids())
    }

    @Test
    fun `el sabado, sin ver la ficha, primero va el tour de la ficha y el del desafio espera`() {
        mostrar(sabado, vistos = emptySet())
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 8, Idioma.ES)).assertExists()
        composeTestRule.onAllNodesWithText(etiquetaTourPaso(1, 2, Idioma.ES)).assertCountEquals(0)
    }

    @Test
    fun `Ver la bienvenida y el tutorial otra vez reinicia los tours`() {
        mostrar(lunes, vistos = todosVistos)
        composeTestRule.onNodeWithText("Ajustes").performClick()
        asentar()
        composeTestRule.onNodeWithTag(TAG_VER_BIENVENIDA).performScrollTo().performClick()
        asentar()
        val ajustes = guardados.last()
        assertFalse(ajustes.bienvenidaCompletada)
        assertTrue(ajustes.toursVistos.isEmpty())
        assertFalse(ajustes.toursSalteados)
    }

    @Test
    fun `el estado del tutorial no viaja en la copia de seguridad`() {
        val respaldo = serializarRespaldo(emptyList()).lowercase()
        assertFalse(respaldo.contains("tour"))
        assertFalse(respaldo.contains("tutorial"))
    }

    @Test
    fun `la opcion Verbos del selector de la Biblioteca es un ancla propia, y el selector sigue siendo el de siempre`() {
        mostrar(lunes, todosVistos)
        composeTestRule.onNodeWithText("Biblioteca").performClick()
        asentar()
        val ids = gestor.registro.ids()
        assertTrue("tour-biblioteca-vista-verbos" in ids && "tour-biblioteca-vistas" in ids)
        // los chips de las tres vistas siguen ahi, con su texto
        composeTestRule.onNodeWithText("Verbos").assertExists()
        composeTestRule.onNodeWithText("Habilidades").assertExists()
    }

    @Test
    fun `con el interruptor encendido a mano, el tour del desafio muestra los dos pasos`() {
        mostrar(sabado, todosVistos)
        composeTestRule.onNodeWithText(etiquetaDesafioFinde(Idioma.ES)).performClick()
        asentar()
        assertTrue("tour-desafio-consigna" in gestor.registro.ids())
        composeTestRule.runOnIdle { gestor.vistos = emptySet(); gestor.lanzar(Tour.DESAFIO) }
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 2, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(2, 2, Idioma.ES)).assertExists()
    }

    // --- Ronda T, T1: el foco tocable sobre la app real ---

    @Test
    fun `en el paso del vocabulario el encabezado se puede tocar, la lista se abre, el foco sigue al encabezado y el tour no avanza`() {
        mostrar(lunes, vistos = emptySet())
        var seguridad = 0
        while (gestor.pasoActual?.ancla != "tour-ficha-vocabulario" && seguridad++ < 10) {
            composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
            asentar()
        }
        assertEquals("tour-ficha-vocabulario", gestor.pasoActual?.ancla)
        val indiceAntes = gestor.indice
        val abiertasAntes = composeTestRule.onAllNodesWithContentDescription(descripcionSeccion(true, Idioma.ES)).fetchSemanticsNodes().size
        val antes = gestor.registro.rectDe("tour-ficha-vocabulario")!!
        composeTestRule.onNodeWithTag("tour-ficha-vocabulario").performClick()
        asentar()
        val abiertasDespues = composeTestRule.onAllNodesWithContentDescription(descripcionSeccion(true, Idioma.ES)).fetchSemanticsNodes().size
        assertEquals("la lista se abrio", abiertasAntes + 1, abiertasDespues)
        assertEquals("el tour sigue en el mismo paso", indiceAntes, gestor.indice)
        val despues = gestor.registro.rectDe("tour-ficha-vocabulario")!!
        // el foco sigue al encabezado (el ancla se vuelve a registrar donde esta ahora) y su alto no crecio con la lista
        assertEquals(antes.height, despues.height, 1f)
        composeTestRule.onNodeWithTag(TAG_TOUR_FOCO).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_TARJETA).assertExists()
        // lo que no es el encabezado sigue sin recibir toques: el boton de otra seccion no se abre
        val ctrl = composeTestRule.onAllNodesWithContentDescription(descripcionSeccion(false, Idioma.ES))
        if (ctrl.fetchSemanticsNodes().isNotEmpty()) {
            ctrl[0].performClick()
            asentar()
            assertEquals(abiertasDespues, composeTestRule.onAllNodesWithContentDescription(descripcionSeccion(true, Idioma.ES)).fetchSemanticsNodes().size)
        }
    }

    @Test
    fun `el sabado el interruptor del desafio se puede encender desde su paso, y entonces aparece el segundo paso`() {
        mostrar(sabado, vistos = setOf(Tour.FICHA))
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 1, Idioma.ES)).assertExists()
        assertFalse("tour-desafio-consigna" in gestor.registro.ids())
        composeTestRule.onNodeWithTag("tour-desafio-interruptor").performClick()
        asentar()
        // el usuario lo encendio con su mano (el tour no lo hace por el): ahora la consigna existe y el contador la cuenta
        assertTrue("tour-desafio-consigna" in gestor.registro.ids())
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 2, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(2, 2, Idioma.ES)).assertExists()
    }

    // --- Ronda T, tarea 4: la planilla del profesor no va en el desafio ---

    @Test
    fun `el boton de la planilla esta en la ficha y no en el desafio del fin de semana`() {
        mostrar(sabado, todosVistos, conPlanilla = true)
        composeTestRule.onAllNodesWithTag("tour-ficha-planilla").assertCountEquals(1)
        composeTestRule.onNodeWithTag("tour-desafio-interruptor").performClick()
        asentar()
        composeTestRule.onAllNodesWithTag("tour-ficha-planilla").assertCountEquals(0)
        // el bloque de titulo (semana, insignias) se queda en los dos modos
        composeTestRule.onAllNodesWithTag("tour-ficha-titulo").assertCountEquals(1)
        // y al apagar el interruptor vuelve
        composeTestRule.onNodeWithTag("tour-desafio-interruptor").performClick()
        asentar()
        composeTestRule.onAllNodesWithTag("tour-ficha-planilla").assertCountEquals(1)
    }

    @Test
    fun `en el desafio el paso de la planilla del tour se saltea solo, sin dejar un agujero colgado`() {
        mostrar(sabado, todosVistos, conPlanilla = true)
        composeTestRule.onNodeWithTag("tour-desafio-interruptor").performClick()
        asentar()
        assertFalse("tour-ficha-planilla" in gestor.registro.ids())
        composeTestRule.runOnIdle { gestor.vistos = emptySet(); gestor.lanzar(Tour.FICHA) }
        asentar()
        var seguridad = 0
        while (seguridad++ < 12) {
            assertTrue("nunca se muestra el paso de la planilla", gestor.pasoActual?.ancla != "tour-ficha-planilla")
            val hayFoco = composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).fetchSemanticsNodes().isNotEmpty()
            if (!hayFoco) break
            val siguiente = composeTestRule.onAllNodesWithTag(TAG_TOUR_SIGUIENTE).fetchSemanticsNodes()
            if (siguiente.isEmpty()) {
                composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).performClick()
                asentar()
                break
            }
            composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
            asentar()
        }
        composeTestRule.onAllNodesWithTag(TAG_TOUR_FOCO).assertCountEquals(0)
    }
}
