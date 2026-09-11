package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Es preferencia, no progreso (ver el comentario en RepositorioAjustes.kt):
 * persistirla no viola el modo revista. Estos tests son el contrato de esa
 * distincion -- guardar y volver a leer tiene que dar exactamente lo mismo.
 */
@RunWith(RobolectricTestRunner::class)
class RepositorioAjustesTest {

    private fun contexto(): Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `sin nada guardado, los valores por defecto son un combo que si tiene contenido`() {
        val ajustes = cargarAjustes(contexto())
        assertEquals(Ajustes(), ajustes)
    }

    @Test
    fun `guardar y volver a cargar da lo mismo que se guardo`() {
        val contexto = contexto()
        val ajustes = Ajustes(
            idiomaAprendido = Idioma.EN,
            nivel = Nivel.C1,
            idiomaBase = Idioma.PT,
            idiomaInterfaz = Idioma.FR,
            variantesDesactivadas = setOf("CH", "AT"),
        )
        guardarAjustes(contexto, ajustes)
        assertEquals(ajustes, cargarAjustes(contexto))
    }

    @Test
    fun `guardar una segunda vez reemplaza, no acumula, las variantes desactivadas`() {
        val contexto = contexto()
        guardarAjustes(contexto, Ajustes(variantesDesactivadas = setOf("CH")))
        guardarAjustes(contexto, Ajustes(variantesDesactivadas = setOf("AT")))
        assertEquals(setOf("AT"), cargarAjustes(contexto).variantesDesactivadas)
    }
}
