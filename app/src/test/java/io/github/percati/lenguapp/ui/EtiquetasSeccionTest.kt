package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Dia
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EtiquetasSeccionTest {

    @Test
    fun `las etiquetas en aleman coinciden con la tabla original`() {
        assertEquals("Übersichtskasten", etiquetaSeccion("cuadroReferencia", Idioma.DE))
        assertEquals("Themenwortschatz", etiquetaSeccion("vocabulario", Idioma.DE))
        assertEquals("Beschreibung", etiquetaSeccion("descripcion", Idioma.DE))
    }

    @Test
    fun `las etiquetas en ingles coinciden con la tabla original`() {
        assertEquals("Vocabulary", etiquetaSeccion("vocabulario", Idioma.EN))
        assertEquals("Correction prompt", etiquetaSeccion("promptCorreccion", Idioma.EN))
    }

    @Test
    fun `redemittel es Expresiones en espanol, no Redemittel`() {
        assertEquals("Expresiones", etiquetaSeccion("redemittel", Idioma.ES))
        assertEquals("Expressions", etiquetaSeccion("redemittel", Idioma.EN))
        assertEquals("Redemittel", etiquetaSeccion("redemittel", Idioma.DE))
        assertEquals("Espressioni", etiquetaSeccion("redemittel", Idioma.IT))
    }

    @Test
    fun `resuelve los seis idiomas, ya no solo ingles y aleman`() {
        assertEquals("Exemples", etiquetaSeccion("ejemplos", Idioma.FR))
        assertEquals("Esempi", etiquetaSeccion("ejemplos", Idioma.IT))
        assertEquals("Exemplos", etiquetaSeccion("ejemplos", Idioma.PT))
        assertEquals("Ejemplos", etiquetaSeccion("ejemplos", Idioma.ES))
    }

    @Test
    fun `toda clave tiene etiqueta no vacia en los seis idiomas`() {
        val tabla = tablaEtiquetasSeccionCruda()
        tabla.forEach { (clave, porIdioma) ->
            Idioma.entries.forEach { idioma ->
                assertTrue("$clave sin etiqueta en $idioma", !porIdioma[idioma].isNullOrBlank())
            }
        }
    }

    @Test
    fun `una clave desconocida se devuelve tal cual, no rompe`() {
        assertEquals("algo-nuevo", etiquetaSeccion("algo-nuevo", Idioma.DE))
    }

    @Test
    fun `el titulo de contraste traducido nombra al idioma de app en el propio idioma de app`() {
        assertEquals("Contraste con el español", etiquetaSeccion("contraste", Idioma.ES))
        assertEquals("Contrast with English", etiquetaSeccion("contraste", Idioma.EN))
        assertEquals("Kontrast zum Deutschen", etiquetaSeccion("contraste", Idioma.DE))
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
