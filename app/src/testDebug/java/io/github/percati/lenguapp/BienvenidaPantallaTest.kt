package io.github.percati.lenguapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
import io.github.percati.lenguapp.ui.TAG_BIENVENIDA
import io.github.percati.lenguapp.ui.TAG_BIENVENIDA_EMPEZAR
import io.github.percati.lenguapp.ui.TAG_VER_BIENVENIDA
import io.github.percati.lenguapp.ui.etiquetaAjustes
import io.github.percati.lenguapp.ui.etiquetaBienvenidaEmpezar
import io.github.percati.lenguapp.ui.etiquetaBienvenidaTitulo
import io.github.percati.lenguapp.ui.etiquetaSegunSistema
import io.github.percati.lenguapp.ui.textoAcercaQueEs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate

/**
 * Pantalla de bienvenida (Ronda N, tarea 2): aparece la primera vez y no la segunda; no se sigue sin elegir idioma; el
 * idioma elegido se aplica de inmediato; se puede volver a ver desde Ajustes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class BienvenidaPantallaTest {

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

    private val guardados = mutableListOf<Ajustes>()

    private fun mostrar(ajustes: Ajustes) {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = ajustes,
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2), Idioma.EN to setOf(Nivel.C1)),
                resolver = ::resolverDePrueba,
                fechaInicial = LocalDate.of(2026, 9, 7),
                onGuardarAjustes = { guardados += it },
            )
        }
    }

    private val primerArranque = Ajustes(bienvenidaCompletada = false)

    @Test
    fun `la primera vez aparece la bienvenida, con el idioma sin elegir y nada mas a la vista`() {
        mostrar(primerArranque)
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA).assertExists()
        // los 6 idiomas, cada uno en su propio idioma, mas «Idioma del sistema»; ninguno marcado de antemano
        for (nombre in listOf("Español", "English", "Deutsch", "Français", "Italiano", "Português")) {
            composeTestRule.onNodeWithText(nombre).assertIsNotSelected()
        }
        composeTestRule.onNodeWithText(etiquetaSegunSistema(Idioma.ES)).assertIsNotSelected()
        // sin elegir idioma no hay forma de seguir: ni boton para empezar, ni el resto de la pantalla
        composeTestRule.onAllNodesWithTag(TAG_BIENVENIDA_EMPEZAR).assertCountEquals(0)
        composeTestRule.onAllNodesWithText(textoAcercaQueEs(Idioma.ES)).assertCountEquals(0)
        // y la app de detras no se ve
        composeTestRule.onAllNodesWithText(etiquetaAjustes(Idioma.ES)).assertCountEquals(0)
        // y no se guardo nada todavia
        assertTrue(guardados.isEmpty())
    }

    @Test
    fun `al elegir el idioma se aplica de inmediato y aparecen la practica y el texto de que es esto`() {
        mostrar(primerArranque)
        composeTestRule.onNodeWithText("Deutsch").performClick()
        composeTestRule.waitForIdle()
        // la pantalla ya habla aleman
        composeTestRule.onNodeWithText(etiquetaBienvenidaTitulo(Idioma.DE)).assertExists()
        composeTestRule.onNodeWithText(etiquetaBienvenidaEmpezar(Idioma.DE)).assertExists()
        composeTestRule.onNodeWithText(textoAcercaQueEs(Idioma.DE)).assertExists()
        composeTestRule.onAllNodesWithText("Deutsch").onFirst().assertIsSelected()
        // y se guardo como ajuste: idioma de la app y de las glosas
        val ultimo = guardados.last()
        assertEquals(Idioma.DE, ultimo.idiomaInterfaz)
        assertEquals(Idioma.DE, ultimo.idiomaBase)
        assertFalse(ultimo.idiomaSegunSistema)
        assertFalse("todavia no se completo", ultimo.bienvenidaCompletada)
    }

    @Test
    fun `elegir Idioma del sistema es una eleccion explicita, y recien ahi se lee`() {
        mostrar(primerArranque)
        composeTestRule.onNodeWithText(etiquetaSegunSistema(Idioma.ES)).performClick()
        composeTestRule.waitForIdle()
        assertTrue(guardados.last().idiomaSegunSistema)
        composeTestRule.onAllNodesWithTag(TAG_BIENVENIDA_EMPEZAR).assertCountEquals(1)
    }

    @Test
    fun `Empezar cierra la bienvenida, la guarda como completada y muestra la app`() {
        mostrar(primerArranque)
        composeTestRule.onNodeWithText("English").performClick()
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA_EMPEZAR).performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertTrue(guardados.last().bienvenidaCompletada)
        composeTestRule.onAllNodesWithTag(TAG_BIENVENIDA).assertCountEquals(0)
        composeTestRule.onNodeWithText(etiquetaAjustes(Idioma.EN)).assertExists()
    }

    @Test
    fun `la segunda vez no aparece, con la bienvenida ya completada`() {
        mostrar(Ajustes(bienvenidaCompletada = true))
        composeTestRule.onAllNodesWithTag(TAG_BIENVENIDA).assertCountEquals(0)
        composeTestRule.onNodeWithText(etiquetaAjustes(Idioma.ES)).assertExists()
    }

    @Test
    fun `sin ningun idioma para practicar no se puede empezar y se avisa`() {
        mostrar(primerArranque)
        composeTestRule.onNodeWithText("Español").performClick()
        // quitar el idioma preseleccionado (Alemán B2): tocarlo seleccionado lo saca
        composeTestRule.onNodeWithText("Alemán").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA_EMPEZAR).assertIsNotEnabled()
        composeTestRule.onNodeWithText("Elige al menos un idioma para practicar.").assertExists()
    }

    @Test
    fun `se puede volver a ver desde Ajustes, y esta vez no obliga a elegir el idioma de nuevo`() {
        mostrar(Ajustes(bienvenidaCompletada = true))
        composeTestRule.onNodeWithText(etiquetaAjustes(Idioma.ES)).performClick()
        composeTestRule.onNodeWithTag(TAG_VER_BIENVENIDA).performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA).assertExists()
        assertFalse(guardados.last().bienvenidaCompletada)
        // todo a la vista de entrada
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA_EMPEZAR).assertIsEnabled()
        composeTestRule.onNodeWithText(textoAcercaQueEs(Idioma.ES)).assertExists()
        composeTestRule.onNodeWithTag(TAG_BIENVENIDA_EMPEZAR).performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertTrue(guardados.last().bienvenidaCompletada)
        composeTestRule.onAllNodesWithTag(TAG_BIENVENIDA).assertCountEquals(0)
    }

    @Test
    fun `el texto de que es esto esta en los 6 idiomas y es el mismo de Acerca de`() {
        val textos = Idioma.entries.map { textoAcercaQueEs(it) }
        assertEquals(6, textos.toSet().size)
        for ((i, t) in Idioma.entries.zip(textos)) {
            assertTrue("$i: sin traducir", t.startsWith("LenguApp") || t.startsWith("O LenguApp"))
            assertFalse("$i: placeholder", t.contains("{"))
        }
        assertTrue(textoAcercaQueEs(Idioma.ES).startsWith("LenguApp no enseña un idioma: te hace practicar el que ya estás aprendiendo."))
        assertTrue(textoAcercaQueEs(Idioma.ES).contains("haz un curso de ese nivel"))
    }
}
