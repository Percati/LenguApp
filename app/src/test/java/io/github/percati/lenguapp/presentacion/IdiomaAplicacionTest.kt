package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IdiomaAplicacionTest {

    // --- idiomaAplicacionEfectivo: AJUSTES-FASE-6.md, bloque C ---

    @Test
    fun `sin idioma segun el sistema, usa el idioma de interfaz elegido a mano`() {
        val ajustes = Ajustes(idiomaInterfaz = Idioma.DE, idiomaSegunSistema = false)
        assertEquals(Idioma.DE, idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema = "fr"))
    }

    @Test
    fun `con idioma segun el sistema activo, usa el idioma del dispositivo si es soportado`() {
        val ajustes = Ajustes(idiomaInterfaz = Idioma.ES, idiomaSegunSistema = true)
        assertEquals(Idioma.DE, idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema = "de"))
    }

    @Test
    fun `segun el sistema con un idioma no soportado cae al elegido a mano`() {
        // "ca" (catalan) no es ninguno de los 6 idiomas soportados.
        val ajustes = Ajustes(idiomaInterfaz = Idioma.PT, idiomaSegunSistema = true)
        assertEquals(Idioma.PT, idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema = "ca"))
    }

    @Test
    fun `segun el sistema sin codigo de idioma (nulo) cae al elegido a mano`() {
        val ajustes = Ajustes(idiomaInterfaz = Idioma.IT, idiomaSegunSistema = true)
        assertEquals(Idioma.IT, idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema = null))
    }

    // --- avisoGlosasSoloEnEspanol: correccion factual de AJUSTES-FASE-6.md, bloque C ---

    @Test
    fun `no avisa si el idioma de la aplicacion es espanol`() {
        assertFalse(avisoGlosasSoloEnEspanol(Idioma.ES))
    }

    @Test
    fun `avisa para cualquier otro idioma`() {
        assertTrue(avisoGlosasSoloEnEspanol(Idioma.DE))
        assertTrue(avisoGlosasSoloEnEspanol(Idioma.IT))
    }
}
