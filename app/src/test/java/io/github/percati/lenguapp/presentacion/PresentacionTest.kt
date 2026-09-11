package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.VocabularioItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

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

    // --- contraste: opcional, seccion completa se oculta si no hay nada util ---

    @Test
    fun `contraste ausente no muestra nada`() {
        assertEquals(null, null.contrasteParaMostrar(Idioma.DE))
    }

    @Test
    fun `contraste cae a espanol si el idioma base no esta poblado`() {
        assertEquals("El espanol no...", mapOf("es" to "El espanol no...").contrasteParaMostrar(Idioma.DE))
    }

    // --- variantes regionales: Fase 4, "ofrecer un ajuste para desactivar ese contenido" ---

    private fun fichaDeG01(): Ficha {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        return parsearContenido(File(carpeta, "contenido/DE-G01-B2-1.json").readText()) as Ficha
    }

    @Test
    fun `variantesConocidas encuentra CH en los items de vocabulario, sin estar hardcodeado`() {
        assertEquals(setOf("CH"), variantesConocidas(listOf(fichaDeG01())))
    }

    @Test
    fun `sin variantes desactivadas la ficha vuelve igual`() {
        val ficha = fichaDeG01()
        assertEquals(ficha, ficha.filtrarVariantesDesactivadas(emptySet()))
    }

    @Test
    fun `desactivar CH saca solo los items de vocabulario marcados CH`() {
        val ficha = fichaDeG01()
        val original = ficha.vocabulario.size
        val filtrada = ficha.filtrarVariantesDesactivadas(setOf("CH"))!!
        assertEquals(original - 3, filtrada.vocabulario.size)
        assertTrue(filtrada.vocabulario.none { it.variante == "CH" })
        // El resto del contenido no se toca.
        assertEquals(ficha.titulo, filtrada.titulo)
        assertEquals(ficha.descripcion, filtrada.descripcion)
    }

    @Test
    fun `desactivar una variante que la ficha entera tiene la oculta del todo`() {
        val fichaCH = fichaDeG01().copy(variante = "CH")
        assertNull(fichaCH.filtrarVariantesDesactivadas(setOf("CH")))
    }

    @Test
    fun `desactivar una variante que no aparece no cambia nada`() {
        val ficha = fichaDeG01()
        assertEquals(ficha, ficha.filtrarVariantesDesactivadas(setOf("AT")))
    }
}
