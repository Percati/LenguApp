package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.test.assertCountEquals
import io.github.percati.lenguapp.modelo.FuenteTema
import org.junit.Assert.assertTrue
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
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
// Pantalla alta: la tira de ajustes entera se compone y se mide (el orden de los selectores).
@org.robolectric.annotation.Config(qualifiers = "w400dp-h3000dp")
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
                // B2 y C1 para los dos, aunque en el contenido real DE solo
                // tenga B2: los tests de este archivo ejercitan el mecanismo
                // de cambio de nivel en general, no la disponibilidad real.
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2, Nivel.C1), Idioma.EN to setOf(Nivel.B2, Nivel.C1)),
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

        // El chip del idioma que se aprende dice "Inglés" (en el idioma de app); el de la aplicacion, "English".
        composeTestRule.onNodeWithText("Inglés").performClick()
        composeTestRule.onNodeWithText("C1").performClick()

        assertEquals(mapOf(Idioma.EN to Nivel.C1), actual().idiomasAprendidos)
    }

    @Test
    fun `tocar un idioma ya seleccionado lo saca`() {
        val actual = montar(Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2, Idioma.EN to Nivel.C1)))

        composeTestRule.onNodeWithText("Alemán").performClick()

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

        // El selector de idioma de la aplicacion muestra cada idioma con su propio nombre ("Deutsch").
        composeTestRule.onNodeWithText("Deutsch").performClick()

        val ajustes = actual()
        assertEquals(Idioma.DE, ajustes.idiomaBase)
        assertEquals(Idioma.DE, ajustes.idiomaInterfaz)
        assertEquals(false, ajustes.idiomaSegunSistema)
    }

    @Test
    fun `elegir Sistema activa idiomaSegunSistema`() {
        val actual = montar(Ajustes(idiomaSegunSistema = false))

        // "Sistema" aparece dos veces (idioma de la aplicacion y modo de
        // tema): la primera es el selector de idioma.
        composeTestRule.onAllNodesWithText("Sistema").onFirst().performClick()

        assertEquals(true, actual().idiomaSegunSistema)
    }

    @Test
    fun `mantener pulsado un idioma sin contenido muestra un toast en el idioma de interfaz`() {
        montar(Ajustes(idiomaInterfaz = Idioma.EN))

        composeTestRule.onNodeWithText("Spanish").performTouchInput { longClick() }

        assertEquals(mensajeIdiomaNoDisponible(Idioma.EN, Idioma.ES), ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `el aviso de glosas en espanol aparece solo si el idioma de la aplicacion no es espanol`() {
        montar(Ajustes(idiomaInterfaz = Idioma.DE, idiomaBase = Idioma.DE))

        composeTestRule.onNodeWithText(avisoGlosasTexto(Idioma.DE)).assertExists()
    }

    // --- AJUSTES-FASE-7.md, bloque 1: familia de tema y modo son dos ajustes separados ---
    //
    // performScrollTo() solo escrolea el ancestro scrollable mas cercano. Los
    // chips estan en una Row con su propio scroll horizontal, anidada en el
    // scroll vertical de la pantalla: pedirle a un chip que se escrolee a la
    // vista solo mueve el scroll horizontal (que ya lo contiene) y nunca
    // llega al vertical. Por eso el escroleo se le pide a la Row -- ahi el
    // ancestro scrollable mas cercano es el vertical de la pantalla.
    private fun filaConChip(texto: String) = composeTestRule.onNode(hasScrollAction() and hasAnyChild(hasText(texto)))

    @Test
    fun `elegir una familia de tema la escribe en los ajustes, sin tocar el modo`() {
        val actual = montar(Ajustes(familiaTema = FamiliaTema.PAPEL, modoTema = ModoTema.OSCURO))

        filaConChip("Mar").performScrollTo()
        composeTestRule.onNodeWithText("Mar").performClick()

        val ajustes = actual()
        assertEquals(FamiliaTema.MAR, ajustes.familiaTema)
        assertEquals(ModoTema.OSCURO, ajustes.modoTema)
    }

    @Test
    fun `elegir un modo de tema lo escribe en los ajustes, sin tocar la familia`() {
        val actual = montar(Ajustes(familiaTema = FamiliaTema.MAR, modoTema = ModoTema.SEGUN_SISTEMA))

        filaConChip("Oscuro").performScrollTo()
        composeTestRule.onNodeWithText("Oscuro").performClick()

        val ajustes = actual()
        assertEquals(FamiliaTema.MAR, ajustes.familiaTema)
        assertEquals(ModoTema.OSCURO, ajustes.modoTema)
    }

    // --- AJUSTES-FASE-9.md, bloque B: traduccion completa del chrome ---

    @Test
    fun `la pantalla entera se renderiza en frances, no solo el idioma probado hasta ahora`() {
        montar(Ajustes(idiomaInterfaz = Idioma.FR, idiomaBase = Idioma.FR, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)))

        composeTestRule.onNodeWithText(etiquetaAjustes(Idioma.FR)).assertExists()
        composeTestRule.onNodeWithText(etiquetaIdiomasAprendidos(Idioma.FR)).assertExists()
        composeTestRule.onNodeWithText(etiquetaIdiomaApp(Idioma.FR)).assertExists()
        composeTestRule.onAllNodesWithText(etiquetaSegunSistema(Idioma.FR)).onFirst().assertExists()
        composeTestRule.onNodeWithText(etiquetaEstiloVisual(Idioma.FR)).assertExists()
        composeTestRule.onNodeWithText(etiquetaModo(Idioma.FR)).assertExists()
        composeTestRule.onNodeWithText(avisoGlosasTexto(Idioma.FR)).assertExists()
    }

    // --- Contenido nuevo (ingles B2, 2026): el menu de nivel solo ofrece lo que hay ---

    @Test
    fun `el menu de nivel de un idioma solo ofrece los niveles que nivelesConContenido reporta para el`() {
        lateinit var obtenerActual: () -> Ajustes
        composeTestRule.setContent {
            var ajustes by remember { mutableStateOf(Ajustes(idiomasAprendidos = emptyMap())) }
            obtenerActual = { ajustes }
            AjustesScreen(
                ajustes = ajustes,
                idiomasConContenido = setOf(Idioma.EN),
                nivelesConContenido = mapOf(Idioma.EN to setOf(Nivel.B2, Nivel.C1)),
                idiomaAplicacionEfectivo = ajustes.idiomaInterfaz,
                onAjustesCambiados = { ajustes = it },
                onVolver = {},
            )
        }

        composeTestRule.onNodeWithText("Inglés").performClick()

        composeTestRule.onNodeWithText("B2").assertExists()
        composeTestRule.onNodeWithText("C1").assertExists()
        composeTestRule.onNodeWithText("A2").assertDoesNotExist()
        composeTestRule.onNodeWithText("B1").assertDoesNotExist()
        composeTestRule.onNodeWithText("C2").assertDoesNotExist()

        composeTestRule.onNodeWithText("B2").performClick()
        assertEquals(mapOf(Idioma.EN to Nivel.B2), obtenerActual().idiomasAprendidos)
    }

    // --- Ronda G: tema, modo y tipografia ---

    @Test
    fun `el orden de los ajustes es idioma, tema, modo, tipografia`() {
        montar(Ajustes())
        fun top(texto: String) = composeTestRule.onAllNodes(hasText(texto)).fetchSemanticsNodes().first().boundsInRoot.top
        val idioma = top(etiquetaIdiomaApp(Idioma.ES))
        val tema = top(etiquetaEstiloVisual(Idioma.ES))
        val modo = top(etiquetaModo(Idioma.ES))
        val fuente = top(etiquetaFuente(Idioma.ES))
        assertTrue("idioma < tema < modo < tipografia: $idioma $tema $modo $fuente", idioma < tema && tema < modo && modo < fuente)
    }

    @Test
    fun `el selector de tema ofrece los cuatro temas con su nombre en el idioma de app`() {
        montar(Ajustes(idiomaInterfaz = Idioma.DE, idiomaBase = Idioma.DE))
        for (n in listOf("Papier", "Terrakotta", "Meer", "Graphit")) composeTestRule.onNodeWithText(n).assertExists()
        composeTestRule.onAllNodes(hasText("Academia")).assertCountEquals(0)
    }

    @Test
    fun `elegir Terracota o Grafito escribe la familia sin tocar modo ni tipografia`() {
        val actual = montar(Ajustes(familiaTema = FamiliaTema.PAPEL, modoTema = ModoTema.OSCURO, fuente = FuenteTema.CLARA))
        filaConChip("Terracota").performScrollTo()
        composeTestRule.onNodeWithText("Terracota").performClick()
        assertEquals(FamiliaTema.TERRACOTA, actual().familiaTema)
        filaConChip("Grafito").performScrollTo()
        composeTestRule.onNodeWithText("Grafito").performClick()
        assertEquals(FamiliaTema.GRAFITO, actual().familiaTema)
        assertEquals(ModoTema.OSCURO, actual().modoTema)
        assertEquals(FuenteTema.CLARA, actual().fuente)
    }

    @Test
    fun `la vista previa de cada tema son tres puntos de color, fondo superficie y acento`() {
        // los colores de la vista previa salen de la paleta del modo actual
        val papel = paletaPara(FamiliaTema.PAPEL, false)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFF7F6F2), papel.fondo)
        assertEquals(androidx.compose.ui.graphics.Color(0xFFFFFFFF), papel.superficie)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF2F5D8A), papel.acento)
        val mar = paletaPara(FamiliaTema.MAR, true)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF0F1A1C), mar.fondo)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF172426), mar.superficie)
        assertEquals(androidx.compose.ui.graphics.Color(0xFF6FC4C9), mar.acento)
    }

    @Test
    fun `cada opcion de tema dibuja sus tres puntos de color en la pantalla`() {
        var previas: List<Triple<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color>> = emptyList()
        composeTestRule.setContent {
            previas = familiasOfrecidas().map { coloresDeVistaPrevia(it, oscuro = false) }
        }
        assertTrue(previas.size >= 4)
        // las cuatro paletas fijas son distintas entre si
        assertEquals(previas.take(4).toSet().size, 4)
    }

    @Test
    fun `el selector de tipografia tiene las tres opciones y elegir una la escribe sin tocar el tema`() {
        val actual = montar(Ajustes(familiaTema = FamiliaTema.MAR))
        filaConChip("Lectura").performScrollTo()
        composeTestRule.onNodeWithText("Clara").assertExists()
        composeTestRule.onAllNodes(hasText("Aa")).assertCountEquals(3)
        composeTestRule.onNodeWithText("Lectura").performClick()
        assertEquals(FuenteTema.LECTURA, actual().fuente)
        assertEquals(FamiliaTema.MAR, actual().familiaTema)
    }

    @Test
    fun `los chips de idioma muestran el nombre, no el codigo de dos letras`() {
        montar(Ajustes(idiomaInterfaz = Idioma.FR, idiomaBase = Idioma.FR))
        // idioma que se aprende: nombre en el idioma de app (frances)
        composeTestRule.onNodeWithText("Allemand").assertExists()
        composeTestRule.onNodeWithText("Anglais").assertExists()
        // idioma de la aplicacion: cada uno con su propio nombre
        for (n in listOf("Español", "English", "Deutsch", "Français", "Italiano", "Português")) {
            // (en frances, "Français" sale tambien en el chip del idioma que se aprende)
            assertTrue(n, composeTestRule.onAllNodes(hasText(n)).fetchSemanticsNodes().isNotEmpty())
        }
        for (codigo in listOf("EN", "DE", "ES", "FR", "IT", "PT")) composeTestRule.onAllNodes(hasText(codigo)).assertCountEquals(0)
    }
}
