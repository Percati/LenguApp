package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Dia
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

    // --- etiquetaDia: AJUSTES-FASE-7.md, bloque 2.3 -- en el idioma que se aprende, tabla escrita ---

    @Test
    fun `los dias de una ficha alemana usan las abreviaturas del aleman, no las del sistema`() {
        assertEquals("Mo", etiquetaDia(Dia.LUN, Idioma.DE))
        assertEquals("Mi", etiquetaDia(Dia.MIE, Idioma.DE))
        assertEquals("Fr", etiquetaDia(Dia.VIE, Idioma.DE))
    }

    @Test
    fun `los dias de una ficha inglesa usan las abreviaturas del ingles`() {
        assertEquals("Mon", etiquetaDia(Dia.LUN, Idioma.EN))
        assertEquals("Wed", etiquetaDia(Dia.MIE, Idioma.EN))
        assertEquals("Fri", etiquetaDia(Dia.VIE, Idioma.EN))
    }

    @Test
    fun `los seis idiomas tienen sus tres dias, exactamente la tabla del documento`() {
        assertEquals("lun", etiquetaDia(Dia.LUN, Idioma.ES))
        assertEquals("mié", etiquetaDia(Dia.MIE, Idioma.ES))
        assertEquals("vie", etiquetaDia(Dia.VIE, Idioma.ES))
        assertEquals("lun", etiquetaDia(Dia.LUN, Idioma.FR))
        assertEquals("mer", etiquetaDia(Dia.MIE, Idioma.FR))
        assertEquals("ven", etiquetaDia(Dia.VIE, Idioma.FR))
        assertEquals("lun", etiquetaDia(Dia.LUN, Idioma.IT))
        assertEquals("mer", etiquetaDia(Dia.MIE, Idioma.IT))
        assertEquals("ven", etiquetaDia(Dia.VIE, Idioma.IT))
        assertEquals("seg", etiquetaDia(Dia.LUN, Idioma.PT))
        assertEquals("qua", etiquetaDia(Dia.MIE, Idioma.PT))
        assertEquals("sex", etiquetaDia(Dia.VIE, Idioma.PT))
    }
}
