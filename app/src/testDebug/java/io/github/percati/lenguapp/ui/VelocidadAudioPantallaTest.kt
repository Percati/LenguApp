package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import io.github.percati.lenguapp.audio.ControlAudio
import io.github.percati.lenguapp.audio.IndiceAudio
import io.github.percati.lenguapp.audio.LocalControlAudio
import io.github.percati.lenguapp.audio.LocalVelocidadAudio
import io.github.percati.lenguapp.audio.ReproductorAudio
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VelocidadAudio
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private class ReproductorFalsoVel : ReproductorAudio {
    val velocidades = mutableListOf<Float>()
    override fun reproducir(rutaAsset: String, velocidad: Float, alTerminar: (Boolean) -> Unit): Boolean { velocidades += velocidad; return true }
    override fun detener() {}
    override fun liberar() {}
}

/** La velocidad del audio en pantalla (Ronda O, tarea 2): el ajuste, el paso elegido que llega al reproductor y el aviso discreto en el boton. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class VelocidadAudioPantallaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val item = ItemBiblioteca(
        idioma = Idioma.DE, tipo = TipoGuardado.VOCABULARIO, texto = "die Miete",
        vocabulario = VocabularioItem(item = "die Miete", prioridad = Prioridad.NUCLEO, traducciones = mapOf("es" to "el alquiler")),
        redemittel = null, niveles = setOf(Nivel.B2), topics = emptySet(), categorias = emptySet(), skillIdOrigen = "DE-V01", nivelOrigen = Nivel.B2,
    )

    private fun biblioteca(velocidad: VelocidadAudio, rep: ReproductorFalsoVel) {
        val control = ControlAudio(IndiceAudio.parsear("""{"de": {"Vocabulario": {"die Miete": "m.ogg"}}}"""), rep)
        composeTestRule.setContent {
            CompositionLocalProvider(LocalControlAudio provides control, LocalVelocidadAudio provides velocidad) {
                BibliotecaScreen(
                    items = listOf(item), idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2), idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                    topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = emptySet(), onAlternar = {}, onVolver = {},
                )
            }
        }
    }

    @Test
    fun `Ajustes ofrece los cuatro pasos, con el 100 por ciento marcado por omision`() {
        val cambios = mutableListOf<Ajustes>()
        composeTestRule.setContent {
            AjustesScreen(
                ajustes = Ajustes(), idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = mapOf(Idioma.DE to setOf(Nivel.B2)), idiomaAplicacionEfectivo = Idioma.ES,
                onAjustesCambiados = { cambios += it }, onVolver = {},
            )
        }
        composeTestRule.onNodeWithTag(TAG_VELOCIDAD_AUDIO_PREFIJO + 100).performScrollTo().assertIsSelected()
        composeTestRule.onNodeWithTag(TAG_VELOCIDAD_AUDIO_PREFIJO + 60).performClick()
        composeTestRule.onNodeWithTag(TAG_VELOCIDAD_AUDIO_PREFIJO + 75).performClick()
        assertEquals(listOf(VelocidadAudio.LENTA, VelocidadAudio.MEDIA), cambios.map { it.velocidadAudio })
    }

    @Test
    fun `el paso elegido llega al reproductor al tocar el altavoz`() {
        val rep = ReproductorFalsoVel()
        biblioteca(VelocidadAudio.LENTA, rep)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        assertEquals(listOf(0.6f), rep.velocidades)
    }

    @Test
    fun `al 100 por ciento el boton no muestra nada extra y suena a velocidad normal`() {
        val rep = ReproductorFalsoVel()
        biblioteca(VelocidadAudio.NORMAL, rep)
        composeTestRule.onAllNodesWithTag(TAG_VELOCIDAD_EN_BOTON, useUnmergedTree = true).assertCountEquals(0)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).onFirst().performClick()
        assertEquals(listOf(1.0f), rep.velocidades)
    }

    @Test
    fun `si no esta al 100 por ciento el boton lo deja ver, con un numerito y en la descripcion`() {
        biblioteca(VelocidadAudio.MEDIA, ReproductorFalsoVel())
        composeTestRule.onAllNodesWithTag(TAG_VELOCIDAD_EN_BOTON, useUnmergedTree = true).assertCountEquals(1)
        composeTestRule.onAllNodesWithTag(TAG_BOTON_AUDIO).assertCountEquals(1)
        composeTestRule.onNode(hasContentDescription("Escuchar (75 %)")).assertExists()
    }
}
