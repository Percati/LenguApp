package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.audio.ClaseAudio
import io.github.percati.lenguapp.audio.ControlAudio
import io.github.percati.lenguapp.audio.IndiceAudio
import io.github.percati.lenguapp.audio.LocalControlAudio
import io.github.percati.lenguapp.audio.ReproductorAudio
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import io.github.percati.lenguapp.presentacion.esColocacionConNucleo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

private class ReproductorFalso(var abre: Boolean = true) : ReproductorAudio {
    val pedidos = mutableListOf<String>()
    var detenciones = 0
    override fun reproducir(rutaAsset: String, alTerminar: (Boolean) -> Unit): Boolean { pedidos += rutaAsset; return abre }
    override fun detener() { detenciones++ }
    override fun liberar() {}
}

/** El boton de audio en pantalla (Ronda M, tarea 2): solo donde hay clip, un audio a la vez, sin romper ante un archivo que falta. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class AudioPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private fun item(texto: String, tipo: TipoGuardado = TipoGuardado.VOCABULARIO) = ItemBiblioteca(
        idioma = Idioma.DE, tipo = tipo, texto = texto,
        vocabulario = VocabularioItem(item = texto, prioridad = Prioridad.NUCLEO, traducciones = mapOf("es" to "trad $texto")),
        redemittel = null, niveles = setOf(Nivel.B2), topics = emptySet(), categorias = emptySet(), skillIdOrigen = "DE-V01", nivelOrigen = Nivel.B2,
    )

    private fun indice(vararg textos: String) = IndiceAudio.parsear(
        """{"de": {"Vocabulario": {${textos.joinToString(",") { "\"$it\": \"${it.replace(' ', '_')}.ogg\"" }}}}}""",
    )

    private fun mostrarBiblioteca(items: List<ItemBiblioteca>, control: ControlAudio, vistaVerbos: Boolean = false) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalControlAudio provides control) {
                BibliotecaScreen(
                    items = items, idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                    topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(), onAlternar = {}, onVolver = {},
                )
            }
        }
        if (vistaVerbos) composeTestRule.onNodeWithText("Verbos").performClick()
    }

    @Test
    fun `solo el texto con audio muestra el boton, el que no tiene no dibuja nada`() {
        val control = ControlAudio(indice("die Miete"), ReproductorFalso())
        mostrarBiblioteca(listOf(item("die Miete"), item("der Vertrag")), control)
        composeTestRule.onNodeWithText("der Vertrag").assertExists()
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(1)
    }

    @Test
    fun `sin audio en la app no hay ningun boton`() {
        mostrarBiblioteca(listOf(item("die Miete")), io.github.percati.lenguapp.audio.SinAudio)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(0)
    }

    @Test
    fun `tocar el altavoz lo hace sonar y el icono pasa a Detener, tocarlo otra vez lo corta`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice("die Miete"), rep)
        mostrarBiblioteca(listOf(item("die Miete")), control)
        composeTestRule.onAllNodesWithContentDescription("Escuchar").assertCountEquals(1)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        composeTestRule.waitForIdle()
        assertEquals(listOf("audio/DE/die_Miete.ogg"), rep.pedidos)
        composeTestRule.onAllNodesWithContentDescription("Detener").assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription("Escuchar").assertCountEquals(0)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithContentDescription("Escuchar").assertCountEquals(1)
        assertNull(control.sonando)
    }

    @Test
    fun `un solo audio a la vez, tocar otro corta el anterior y solo uno dice Detener`() {
        val control = ControlAudio(indice("die Miete", "der Vertrag"), ReproductorFalso())
        mostrarBiblioteca(listOf(item("die Miete"), item("der Vertrag")), control)
        val botones = composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO)
        botones[0].performClick()
        botones[1].performClick()
        composeTestRule.waitForIdle()
        // la lista va en orden alfabetico (der Vertrag, die Miete): el segundo boton es el de «die Miete»
        assertEquals("audio/DE/die_Miete.ogg", control.sonando)
        composeTestRule.onAllNodesWithContentDescription("Detener").assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription("Escuchar").assertCountEquals(1)
    }

    @Test
    fun `un archivo que falta no rompe ni muestra nada, el boton sigue siendo Escuchar`() {
        val control = ControlAudio(indice("die Miete"), ReproductorFalso(abre = false))
        mostrarBiblioteca(listOf(item("die Miete")), control)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithContentDescription("Escuchar").assertCountEquals(1)
        assertNull(control.sonando)
    }

    @Test
    fun `al salir de la pantalla el audio se corta`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice("die Miete"), rep)
        var visible by mutableStateOf(true)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalControlAudio provides control) {
                if (visible) BibliotecaScreen(
                    items = listOf(item("die Miete")), idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                    topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(), onAlternar = {}, onVolver = {},
                )
            }
        }
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        composeTestRule.waitForIdle()
        assertEquals("audio/DE/die_Miete.ogg", control.sonando)
        composeTestRule.runOnIdle { visible = false }
        composeTestRule.waitForIdle()
        assertNull(control.sonando)
        assertTrue("el reproductor se detuvo", rep.detenciones >= 1)
    }

    @Test
    fun `Guardados tambien ofrece el altavoz junto a la estrella, solo donde hay audio`() {
        val control = ControlAudio(indice("die Miete"), ReproductorFalso())
        fun guardado(texto: String) = ItemGuardado(
            id = 0, idioma = Idioma.DE, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO,
            texto = TextoBilingue(porIdioma = mapOf("de" to texto)), skillIdOrigen = "DE-V02",
        )
        composeTestRule.setContent {
            CompositionLocalProvider(LocalControlAudio provides control) {
                GuardadosScreen(
                    items = listOf(guardado("die Miete"), guardado("der Vertrag")), itemsBiblioteca = emptyList(), idiomasAprendidos = setOf(Idioma.DE),
                    idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES, topicNombres = NombresI18n(), categoriasUso = CategoriasUso(),
                    existeFichaOrigen = { true }, onAbrirFicha = {}, onQuitar = {}, onVolver = {},
                )
            }
        }
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(1)
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_GUARDADOS).assertCountEquals(2)
    }

    @Test
    fun `en la vista Verbos el altavoz va en el verbo y en sus colocaciones, el encabezado del nucleo no lo lleva`() {
        val todos = construirBiblioteca(
            File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>(),
        )
        val colocaciones = todos.filter { it.idioma == Idioma.DE && it.esColocacionConNucleo() && it.vocabulario!!.nucleoVerbal == "umstellen" }
        assertTrue(colocaciones.isNotEmpty())
        val control = ControlAudio(
            IndiceAudio.parsear("""{"de": {"Vocabulario": {${colocaciones.joinToString(",") { "\"${it.texto}\": \"x.ogg\"" }}}}}"""),
            ReproductorFalso(),
        )
        mostrarBiblioteca(colocaciones, control, vistaVerbos = true)
        composeTestRule.onNodeWithText("umstellen").assertExists()
        // una por colocacion; el encabezado del nucleo (sin estrella, sin item) no tiene audio
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(colocaciones.size)
    }

    @Test
    fun `la ficha muestra el altavoz en vocabulario, expresiones y los ejemplos con clip`() {
        val ficha = parsearContenido(File(assets(), "contenido/DE-F01-A2-2026-1.json").readText()) as Ficha
        val vocabulario = ficha.vocabulario.map { it.item.trim() }.distinct().take(2)
        val expresiones = ficha.redemittel.map { it.expresion.trim() }.distinct().take(2)
        val ejemplos = ficha.ejemplos.map { it.texto.resolver(ficha.idioma, ficha.idioma).trim() }.take(1)
        fun bloque(clase: String, textos: List<String>) = "\"$clase\": {${textos.joinToString(",") { "\"${it.replace("\"", "\\\"")}\": \"x.ogg\"" }}}"
        val control = ControlAudio(
            IndiceAudio.parsear("""{"de": {${bloque("Vocabulario", vocabulario)}, ${bloque("Expresión", expresiones)}, ${bloque("Ejemplo", ejemplos)}}}"""),
            ReproductorFalso(),
        )
        composeTestRule.setContent {
            CompositionLocalProvider(LocalControlAudio provides control) {
                ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, topicNombres = NombresI18n())
            }
        }
        // ejemplos: visibles de entrada
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(ejemplos.size)
        // las secciones plegables, abiertas
        composeTestRule.onNodeWithText("${etiquetaSeccion("vocabulario", Idioma.ES)} (${ficha.vocabulario.size})").performClick()
        composeTestRule.onNodeWithText("${etiquetaSeccion("redemittel", Idioma.ES)} (${ficha.redemittel.size})").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(ejemplos.size + vocabulario.size + expresiones.size)
    }

    @Test
    fun `la descripcion del boton existe en los 6 idiomas, Escuchar y Detener distintas`() {
        for (idioma in Idioma.entries) {
            val escuchar = descripcionAudioEscuchar(idioma)
            val detener = descripcionAudioDetener(idioma)
            assertTrue("$idioma escuchar", escuchar.isNotBlank())
            assertTrue("$idioma detener", detener.isNotBlank())
            assertTrue("$idioma: distintas", escuchar != detener)
        }
        assertEquals("Escuchar", descripcionAudioEscuchar(Idioma.ES))
        assertEquals("Detener", descripcionAudioDetener(Idioma.ES))
        assertEquals("Anhören", descripcionAudioEscuchar(Idioma.DE))
        assertEquals("Écouter", descripcionAudioEscuchar(Idioma.FR))
    }
}
