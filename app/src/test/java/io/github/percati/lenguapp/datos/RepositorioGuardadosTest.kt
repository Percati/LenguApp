package io.github.percati.lenguapp.datos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Guardados (feature 3): "guardar/quitar un item se refleja en la pantalla
 * de Guardados" empieza aca, en el repositorio -- GuardadosScreen solo pinta
 * la lista que [RepositorioGuardados.listar] devuelve (ver GuardadosScreenTest
 * para la parte de pantalla). Room en memoria: rapido, sin tocar disco, y
 * el toggle es la misma llamada que usa la estrella de la ficha.
 */
@RunWith(RobolectricTestRunner::class)
class RepositorioGuardadosTest {

    private fun repositorio(): RepositorioGuardados {
        val db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), GuardadosDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        return RepositorioGuardados(db.dao())
    }

    @Test
    fun `guardar un item lo agrega a listar, quitarlo lo saca`() = runBlocking {
        val repo = repositorio()
        val texto = TextoBilingue(porIdioma = mapOf("de" to "die Miete", "es" to "el alquiler"))

        assertTrue(repo.listar().isEmpty())

        repo.alternar(Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, "die Miete", texto, null, "DE-V02")
        val trasGuardar = repo.listar()
        assertEquals(1, trasGuardar.size)
        assertEquals("DE-V02", trasGuardar.single().skillIdOrigen)
        assertTrue(repo.estaGuardado("DE-V02", TipoGuardado.VOCABULARIO, "die Miete"))

        repo.alternar(Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, "die Miete", texto, null, "DE-V02")
        assertTrue("guardar de nuevo lo mismo debe sacarlo (toggle), no duplicarlo", repo.listar().isEmpty())
        assertTrue(!repo.estaGuardado("DE-V02", TipoGuardado.VOCABULARIO, "die Miete"))
    }

    @Test
    fun `una expresion guardada conserva su funcion, un vocabulario no tiene`() = runBlocking {
        val repo = repositorio()
        val funcion = TextoBilingue(porIdioma = mapOf("de" to "einen Vorschlag abschwächen"))
        repo.alternar(
            Idioma.DE, Nivel.B2, TipoGuardado.EXPRESION, "das würde ich so nicht sagen",
            TextoBilingue(porIdioma = mapOf("de" to "das würde ich so nicht sagen")), funcion, "DE-V05",
        )
        val item = repo.listar().single()
        assertEquals(TipoGuardado.EXPRESION, item.tipo)
        assertEquals(funcion, item.funcion)
    }

    @Test
    fun `dos items distintos de la misma ficha se guardan por separado`() = runBlocking {
        val repo = repositorio()
        val t = TextoBilingue(porIdioma = mapOf("de" to "x"))
        repo.alternar(Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, "die Miete", t, null, "DE-V02")
        repo.alternar(Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, "der Mieter", t, null, "DE-V02")
        assertEquals(2, repo.listar().size)
    }
}
