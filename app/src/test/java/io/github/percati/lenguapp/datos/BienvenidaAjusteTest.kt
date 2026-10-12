package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.presentacion.exportarAnkiTsv
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * `bienvenidaCompletada` (Ronda N, tarea 2) es un ajuste (regla dura 4: ajustes si, progreso no): se guarda con los demas
 * ajustes, arranca en false en una instalacion nueva y NO viaja en la copia de seguridad de Guardados ni en la exportacion a Anki.
 */
@RunWith(RobolectricTestRunner::class)
class BienvenidaAjusteTest {

    private fun contexto(): Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `una instalacion nueva no tiene la bienvenida completada`() {
        assertFalse(cargarAjustes(contexto()).bienvenidaCompletada)
    }

    @Test
    fun `completarla se guarda y se recuerda, y se puede volver a pedir`() {
        val contexto = contexto()
        guardarAjustes(contexto, cargarAjustes(contexto).copy(bienvenidaCompletada = true))
        assertTrue(cargarAjustes(contexto).bienvenidaCompletada)
        guardarAjustes(contexto, cargarAjustes(contexto).copy(bienvenidaCompletada = false))
        assertFalse(cargarAjustes(contexto).bienvenidaCompletada)
    }

    @Test
    fun `un ajuste guardado por una version anterior, sin la clave, la muestra una vez`() {
        val contexto = contexto()
        val prefs = contexto.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
        prefs.edit().putString("idioma_interfaz", "EN").putString("idiomas_aprendidos_json", """{"DE":"B2"}""").apply()
        val cargado = cargarAjustes(contexto)
        assertEquals(Idioma.EN, cargado.idiomaInterfaz)
        assertFalse(cargado.bienvenidaCompletada)
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
        assertFalse(respaldo.contains("bienvenida"))
        assertFalse("ni ningun otro ajuste", respaldo.contains("idiomasaprendidos") || respaldo.contains("idioma_interfaz") || respaldo.contains("familiatema"))
        assertFalse(exportarAnkiTsv(items, Idioma.ES).lowercase().contains("bienvenida"))
    }
}
