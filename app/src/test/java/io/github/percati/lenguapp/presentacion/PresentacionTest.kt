package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.VocabularioItem
import org.junit.Assert.assertEquals
import org.junit.Test

class PresentacionTest {

    private fun vocab(traducciones: Map<String, String>) =
        VocabularioItem(item = "x", prioridad = Prioridad.NUCLEO, traducciones = traducciones)

    private fun redemittel(traducciones: Map<String, String?>) =
        RedemittelItem(expresion = "x", funcion = "y", traducciones = traducciones)

    // --- VocabularioItem: nunca clave cruda ni "null" ---

    @Test
    fun `usa la traduccion del idioma base cuando esta disponible`() {
        assertEquals("Miete", vocab(mapOf("de" to "Miete", "es" to "alquiler")).traduccionParaMostrar(Idioma.DE))
    }

    @Test
    fun `cae a espanol si el idioma base no tiene traduccion todavia`() {
        assertEquals("el alquiler", vocab(mapOf("es" to "el alquiler")).traduccionParaMostrar(Idioma.FR))
    }

    @Test
    fun `un valor vacio a proposito no es un error, cae a espanol igual que si faltara la clave`() {
        assertEquals("el alquiler", vocab(mapOf("de" to "", "es" to "el alquiler")).traduccionParaMostrar(Idioma.DE))
    }

    @Test
    fun `sin ninguna traduccion util muestra un guion, nunca vacio ni la clave`() {
        assertEquals("—", vocab(mapOf("de" to "")).traduccionParaMostrar(Idioma.DE))
    }

    // --- RedemittelItem: null explicito != sin traducir todavia ---

    @Test
    fun `redemittel con texto real se muestra disponible`() {
        val resultado = redemittel(mapOf("es" to "creo que...")).traduccionParaMostrar(Idioma.ES)
        assertEquals(TraduccionRedemittel.Disponible("creo que..."), resultado)
    }

    @Test
    fun `redemittel con es explicitamente null es sin equivalencia directa, no sin traducir`() {
        val resultado = redemittel(mapOf("es" to null)).traduccionParaMostrar(Idioma.ES)
        assertEquals(TraduccionRedemittel.SinEquivalenciaDirecta, resultado)
    }

    @Test
    fun `redemittel sin ninguna clave es sin traducir todavia`() {
        val resultado = redemittel(emptyMap()).traduccionParaMostrar(Idioma.ES)
        assertEquals(TraduccionRedemittel.SinTraducirTodavia, resultado)
    }

    @Test
    fun `redemittel cae a espanol cuando el idioma base no tiene esa clave`() {
        val resultado = redemittel(mapOf("es" to "por eso...")).traduccionParaMostrar(Idioma.DE)
        assertEquals(TraduccionRedemittel.Disponible("por eso..."), resultado)
    }

    @Test
    fun `redemittel con idioma base vacio cae a espanol en vez de mostrar un hueco`() {
        val resultado = redemittel(mapOf("de" to "", "es" to "por eso...")).traduccionParaMostrar(Idioma.DE)
        assertEquals(TraduccionRedemittel.Disponible("por eso..."), resultado)
    }

    @Test
    fun `redemittel null explicito en el idioma base no cae a espanol, es una afirmacion definitiva`() {
        val resultado = redemittel(mapOf("de" to null, "es" to "por eso...")).traduccionParaMostrar(Idioma.DE)
        assertEquals(TraduccionRedemittel.SinEquivalenciaDirecta, resultado)
    }

    // --- contraste: a diferencia de las glosas, NUNCA cae a espanol (AJUSTES-FASE-8.md, B.6) ---

    @Test
    fun `contraste ausente no muestra nada`() {
        assertEquals(null, null.contrasteParaMostrar(Idioma.DE, Idioma.ES))
    }

    @Test
    fun `contraste con el idioma base disponible se muestra`() {
        assertEquals(
            "El espanol no...",
            mapOf("es" to "El espanol no...", "de" to "Das Deutsche...").contrasteParaMostrar(Idioma.DE, Idioma.ES),
        )
    }

    @Test
    fun `contraste NO cae a espanol si el idioma base no esta poblado -- se oculta la seccion entera`() {
        assertEquals(null, mapOf("es" to "El espanol no...").contrasteParaMostrar(Idioma.DE, Idioma.FR))
    }

    @Test
    fun `contraste se oculta si el idioma que se aprende coincide con el idioma base, no hay contraste posible`() {
        assertEquals(null, mapOf("de" to "Das Deutsche...").contrasteParaMostrar(Idioma.DE, Idioma.DE))
    }
}
