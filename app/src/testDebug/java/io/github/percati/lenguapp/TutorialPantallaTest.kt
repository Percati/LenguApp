package io.github.percati.lenguapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
    private val sinContenido = LocalDate.of(2035, 1, 3)

    private val guardados = mutableListOf<Ajustes>()
    private lateinit var gestor: GestorTours

    private fun mostrar(fecha: LocalDate, vistos: Set<Tour>, salteados: Boolean = false) {
        gestor = GestorTours(CATALOGO_TOURS)
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(
                    idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                    bienvenidaCompletada = true, toursVistos = vistos, toursSalteados = salteados,
                ),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2), Idioma.EN to setOf(Nivel.C1)),
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
        composeTestRule.mainClock.advanceTimeBy(300)
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
    fun `todas las anclas del tour de la ficha existen en la pantalla principal`() {
        mostrar(lunes, todosVistos)
        verificarAnclas(Tour.FICHA)
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
    fun `abandonar con el boton atras cuenta como visto`() {
        mostrar(lunes, vistos = emptySet())
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
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
    fun `el sabado, con la ficha ya vista, el tour del desafio enciende el interruptor y al terminar lo deja apagado`() {
        mostrar(sabado, vistos = setOf(Tour.FICHA))
        composeTestRule.onNodeWithText(etiquetaTourPaso(1, 2, Idioma.ES)).assertExists()
        // la consigna del desafio se ve porque el tour encendio el interruptor
        assertTrue("tour-desafio-consigna" in gestor.registro.ids())
        composeTestRule.onNodeWithTag(TAG_TOUR_SIGUIENTE).performClick()
        asentar()
        composeTestRule.onNodeWithText(etiquetaTourPaso(2, 2, Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_TOUR_LISTO).performClick()
        asentar()
        assertTrue(Tour.DESAFIO in guardados.last().toursVistos)
        assertFalse("el interruptor vuelve a quedar apagado", "tour-desafio-consigna" in gestor.registro.ids())
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
}
