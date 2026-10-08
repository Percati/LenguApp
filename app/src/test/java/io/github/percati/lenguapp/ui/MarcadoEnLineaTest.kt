package io.github.percati.lenguapp.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.junit.Assert.assertEquals
import org.junit.Test

class MarcadoEnLineaTest {

    @Test
    fun `texto sin marcado queda igual, sin estilos`() {
        val resultado = textoConMarcado("texto plano sin nada")
        assertEquals("texto plano sin nada", resultado.text)
        assertEquals(0, resultado.spanStyles.size)
    }

    @Test
    fun `destaque doble asterisco se pone en negrita`() {
        val resultado = textoConMarcado("Verb an zweiter Stelle im **Hauptsatz**.")
        assertEquals("Verb an zweiter Stelle im Hauptsatz.", resultado.text)
        val negrita = resultado.spanStyles.single()
        assertEquals(SpanStyle(fontWeight = FontWeight.Bold), negrita.item)
        assertEquals("Hauptsatz", resultado.text.substring(negrita.start, negrita.end))
    }

    @Test
    fun `cita simple asterisco se pone en cursiva`() {
        val resultado = textoConMarcado("*Ich glaube, dass es geht.* dijo ella")
        assertEquals("Ich glaube, dass es geht. dijo ella", resultado.text)
        val cursiva = resultado.spanStyles.single()
        assertEquals(SpanStyle(fontStyle = FontStyle.Italic), cursiva.item)
        assertEquals("Ich glaube, dass es geht.", resultado.text.substring(cursiva.start, cursiva.end))
    }

    @Test
    fun `distingue destaque de cita en el mismo texto, sin anidar`() {
        val resultado = textoConMarcado("*Weil wir umziehen müssen*, **kündigen** wir.")
        assertEquals("Weil wir umziehen müssen, kündigen wir.", resultado.text)
        assertEquals(2, resultado.spanStyles.size)
        val estilos = resultado.spanStyles.map { it.item }.toSet()
        assertEquals(setOf(SpanStyle(fontStyle = FontStyle.Italic), SpanStyle(fontWeight = FontWeight.Bold)), estilos)
    }

    @Test
    fun `varios destaques en el mismo texto se estilizan todos`() {
        val resultado = textoConMarcado("**Satztyp**: Hauptsatz. **Beispiel**: Wir suchen eine Wohnung.")
        assertEquals("Satztyp: Hauptsatz. Beispiel: Wir suchen eine Wohnung.", resultado.text)
        assertEquals(2, resultado.spanStyles.size)
        assertEquals(true, resultado.spanStyles.all { it.item == SpanStyle(fontWeight = FontWeight.Bold) })
    }

    @Test
    fun `texto vacio no rompe nada`() {
        assertEquals(AnnotatedString(""), textoConMarcado(""))
    }

    @Test
    fun `textoSinMarcado quita los marcadores para superficies sin estilo (widget)`() {
        assertEquals("Say the word and no more", textoSinMarcado("Say *the word* and **no** more"))
    }

    // --- angulares: palabra objetivo entre « » cuando la ficha muestra la traduccion ---

    @Test
    fun `angulares envuelve la cita entre comillas angulares y la deja en cursiva`() {
        val resultado = textoConMarcado("Se dice *weil* y el verbo va al final.", angulares = true)
        assertEquals("Se dice «weil» y el verbo va al final.", resultado.text)
        val cursiva = resultado.spanStyles.single()
        assertEquals(SpanStyle(fontStyle = FontStyle.Italic), cursiva.item)
        assertEquals("«weil»", resultado.text.substring(cursiva.start, cursiva.end))
    }

    @Test
    fun `sin angulares la cita solo se italiza, como siempre`() {
        assertEquals("Se dice weil", textoConMarcado("Se dice *weil*").text)
    }

    @Test
    fun `angulares no duplica si el texto ya trae las comillas angulares escritas a mano`() {
        assertEquals("Se dice «weil»", textoConMarcado("Se dice «*weil*»", angulares = true).text)
    }

    @Test
    fun `angulares no envuelve el destaque doble asterisco`() {
        assertEquals("el verbo kündigen", textoConMarcado("el verbo **kündigen**", angulares = true).text)
    }
}
