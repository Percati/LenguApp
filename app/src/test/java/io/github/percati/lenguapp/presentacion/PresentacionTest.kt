package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.VocabularioItem
import org.junit.Assert.assertEquals
import org.junit.Test

class PresentacionTest {

    private fun vocab(traducciones: Map<String, String>) =
        VocabularioItem(item = "x", prioridad = Prioridad.NUCLEO, traducciones = traducciones)

    private fun redemittel(traducciones: Map<String, String?>) =
        RedemittelItem(expresion = "x", funcion = TextoBilingue.de("y"), traducciones = traducciones)

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

    @Test
    fun `contraste con dos claves (es, en) elige la del idioma base sin importar el orden del mapa`() {
        val contraste = mapOf("es" to "El espanol no tiene...", "en" to "English has no...")
        assertEquals("El espanol no tiene...", contraste.contrasteParaMostrar(Idioma.DE, Idioma.ES))
        assertEquals("English has no...", contraste.contrasteParaMostrar(Idioma.DE, Idioma.EN))
    }

    // --- erroresContrastivos: se SUMAN a errores, nunca lo reemplazan (mismo criterio que contraste) ---

    @Test
    fun `sin erroresContrastivos, la lista es solo la universal`() {
        assertEquals(
            listOf("universal 1", "universal 2"),
            erroresParaMostrar(listOf("universal 1", "universal 2"), null, Idioma.DE, Idioma.ES),
        )
    }

    @Test
    fun `con erroresContrastivos para el idioma base, se agregan al final de los universales`() {
        val universales = listOf("universal 1", "universal 2")
        val contrastivos = mapOf("en" to listOf("solo para anglohablantes"))
        assertEquals(
            listOf("universal 1", "universal 2", "solo para anglohablantes"),
            erroresParaMostrar(universales, contrastivos, Idioma.DE, Idioma.EN),
        )
    }

    @Test
    fun `erroresContrastivos de otro idioma base NO se mezclan -- mismo criterio que el contraste`() {
        val universales = listOf("universal 1")
        val contrastivos = mapOf("en" to listOf("solo para anglohablantes"))
        assertEquals(universales, erroresParaMostrar(universales, contrastivos, Idioma.DE, Idioma.FR))
    }

    @Test
    fun `erroresContrastivos no aporta nada si el idioma que se aprende coincide con el base`() {
        val universales = listOf("universal 1")
        val contrastivos = mapOf("de" to listOf("no deberia poder pasar esto"))
        assertEquals(universales, erroresParaMostrar(universales, contrastivos, Idioma.DE, Idioma.DE))
    }
}
