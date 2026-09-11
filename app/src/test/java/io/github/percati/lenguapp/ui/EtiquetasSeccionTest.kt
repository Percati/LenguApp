package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Test

class EtiquetasSeccionTest {

    @Test
    fun `una ficha alemana muestra los titulos en aleman`() {
        assertEquals("Übersichtskasten", etiquetaSeccion("cuadroReferencia", Idioma.DE, bilingue = false))
        assertEquals("Themenwortschatz", etiquetaSeccion("vocabulario", Idioma.DE, bilingue = false))
    }

    @Test
    fun `una ficha inglesa muestra los titulos en ingles`() {
        assertEquals("Vocabulary", etiquetaSeccion("vocabulario", Idioma.EN, bilingue = false))
        assertEquals("Correction prompt", etiquetaSeccion("promptCorreccion", Idioma.EN, bilingue = false))
    }

    @Test
    fun `bilingue muestra los dos idiomas, ficha primero y espanol despues`() {
        assertEquals("Beschreibung / Descripción", etiquetaSeccion("descripcion", Idioma.DE, bilingue = true))
    }

    @Test
    fun `una clave desconocida no rompe, devuelve la clave cruda`() {
        assertEquals("algo-nuevo", etiquetaSeccion("algo-nuevo", Idioma.DE, bilingue = false))
    }

    @Test
    fun `la etiqueta de contraste usa el idioma base real, no fija a espanol`() {
        assertEquals("Kontrast zum Französischen", etiquetaContraste(Idioma.DE, Idioma.FR))
        assertEquals("Contrast with French", etiquetaContraste(Idioma.EN, Idioma.FR))
    }

    @Test
    fun `la etiqueta de contraste con espanol de base sigue funcionando como antes`() {
        assertEquals("Kontrast zum Spanischen", etiquetaContraste(Idioma.DE, Idioma.ES))
    }
}
