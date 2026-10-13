package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.Tour
import io.github.percati.lenguapp.presentacion.exportarAnkiTsv
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Estado del tutorial (Ronda Q): `toursVistos` y `toursSalteados` son ajustes (regla dura 4: ajustes si, progreso no). Se guardan con
 * los demas, una instalacion nueva arranca sin ninguno visto y NO viajan en la copia de seguridad de Guardados ni en Anki.
 */
@RunWith(RobolectricTestRunner::class)
class ToursAjusteTest {

    private fun contexto(): Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `una instalacion nueva no tiene ningun tour visto ni salteado, pero el modelo por omision no lanza tours`() {
        val nuevo = cargarAjustes(contexto())
        assertTrue(nuevo.toursVistos.isEmpty())
        assertFalse(nuevo.toursSalteados)
        assertEquals("el modelo por omision: todos vistos (pantallas y tests sin tours)", Tour.entries.toSet(), Ajustes().toursVistos)
    }

    @Test
    fun `los tours vistos y el salteado se guardan y se recuerdan`() {
        val contexto = contexto()
        guardarAjustes(contexto, cargarAjustes(contexto).copy(toursVistos = setOf(Tour.FICHA, Tour.AJUSTES)))
        assertEquals(setOf(Tour.FICHA, Tour.AJUSTES), cargarAjustes(contexto).toursVistos)
        guardarAjustes(contexto, cargarAjustes(contexto).copy(toursVistos = Tour.entries.toSet(), toursSalteados = true))
        val c = cargarAjustes(contexto)
        assertEquals(Tour.entries.toSet(), c.toursVistos)
        assertTrue(c.toursSalteados)
        // reiniciar (Ver la bienvenida y el tutorial otra vez)
        guardarAjustes(contexto, c.copy(toursVistos = emptySet(), toursSalteados = false))
        assertTrue(cargarAjustes(contexto).toursVistos.isEmpty())
        assertFalse(cargarAjustes(contexto).toursSalteados)
    }

    @Test
    fun `un nombre de tour desconocido se ignora y no rompe`() {
        assertEquals(setOf(Tour.FICHA), leerToursVistos("FICHA,TOUR_DEL_FUTURO, "))
        assertEquals(emptySet<Tour>(), leerToursVistos(null))
        assertEquals(emptySet<Tour>(), leerToursVistos(""))
        assertEquals(Tour.entries.toSet(), leerToursVistos(Tour.entries.joinToString(",") { it.name }))
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
        assertFalse(respaldo.contains("tour"))
        assertFalse(respaldo.contains("tutorial"))
        assertFalse(exportarAnkiTsv(items, Idioma.ES).lowercase().contains("tour"))
    }
}
