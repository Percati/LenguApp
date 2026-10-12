package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.audio.VELOCIDAD_NORMAL
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VelocidadAudio
import io.github.percati.lenguapp.presentacion.exportarAnkiTsv
import io.github.percati.lenguapp.ui.ayudaVelocidadAudio
import io.github.percati.lenguapp.ui.etiquetaVelocidadAudio
import io.github.percati.lenguapp.ui.textoVelocidadAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * `velocidadAudio` (Ronda O, tarea 2) es un ajuste (regla dura 4: ajustes si, progreso no): cuatro pasos fijos (60, 75, 90 y
 * 100 %), 100 % por omision, se guarda con los demas ajustes y NO viaja en la copia de seguridad de Guardados ni en Anki.
 */
@RunWith(RobolectricTestRunner::class)
class VelocidadAudioAjusteTest {

    private fun contexto(): Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `los pasos son 60, 75, 90 y 100 por ciento, y el valor por omision es el normal`() {
        assertEquals(listOf(60, 75, 90, 100), VelocidadAudio.entries.map { it.porcentaje })
        assertEquals(listOf(0.6f, 0.75f, 0.9f, 1.0f), VelocidadAudio.entries.map { it.factor })
        assertEquals(VelocidadAudio.NORMAL, Ajustes().velocidadAudio)
        assertEquals(VELOCIDAD_NORMAL, Ajustes().velocidadAudio.factor, 0f)
        assertEquals("una instalacion nueva arranca al 100 %", VelocidadAudio.NORMAL, cargarAjustes(contexto()).velocidadAudio)
    }

    @Test
    fun `cada paso se guarda y se recuerda`() {
        val contexto = contexto()
        for (v in VelocidadAudio.entries) {
            guardarAjustes(contexto, cargarAjustes(contexto).copy(velocidadAudio = v))
            assertEquals(v, cargarAjustes(contexto).velocidadAudio)
        }
    }

    @Test
    fun `un valor guardado desconocido o una version anterior sin la clave vuelven al 100 por ciento`() {
        val contexto = contexto()
        val prefs = contexto.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
        prefs.edit().putString("idiomas_aprendidos_json", """{"DE":"B2"}""").putString("velocidad_audio", "TURBO").apply()
        assertEquals(VelocidadAudio.NORMAL, cargarAjustes(contexto).velocidadAudio)
        prefs.edit().remove("velocidad_audio").apply()
        assertEquals(VelocidadAudio.NORMAL, cargarAjustes(contexto).velocidadAudio)
    }

    @Test
    fun `no viaja en la copia de seguridad ni en la exportacion a Anki`() {
        val items = listOf(
            ItemGuardado(
                id = 1, idioma = Idioma.DE, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO,
                texto = TextoBilingue(porIdioma = mapOf("de" to "die Miete", "es" to "el alquiler")), skillIdOrigen = "DE-V02",
            ),
        )
        val respaldo = serializarRespaldo(items).lowercase()
        assertFalse(respaldo.contains("velocidad"))
        assertFalse(exportarAnkiTsv(items, Idioma.ES).lowercase().contains("velocidad"))
    }

    @Test
    fun `la etiqueta, la ayuda y los valores estan en los 6 idiomas`() {
        for (idioma in Idioma.entries) {
            assertTrue("$idioma etiqueta", etiquetaVelocidadAudio(idioma).isNotBlank())
            assertTrue("$idioma ayuda", ayudaVelocidadAudio(idioma).isNotBlank())
            for (v in VelocidadAudio.entries) {
                val t = textoVelocidadAudio(v, idioma)
                assertTrue("$idioma ${v.porcentaje}: $t", t.contains(v.porcentaje.toString()) && t.contains("%") && !t.contains("{"))
            }
        }
        assertEquals("Velocidad del audio", etiquetaVelocidadAudio(Idioma.ES))
        assertEquals("Audio speed", etiquetaVelocidadAudio(Idioma.EN))
        assertEquals("75 %", textoVelocidadAudio(VelocidadAudio.MEDIA, Idioma.ES))
        assertEquals("75%", textoVelocidadAudio(VelocidadAudio.MEDIA, Idioma.EN))
        assertEquals(6, Idioma.entries.map { etiquetaVelocidadAudio(it) }.toSet().size)
    }
}
