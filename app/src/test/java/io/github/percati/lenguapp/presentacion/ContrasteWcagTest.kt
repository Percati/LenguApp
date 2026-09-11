package io.github.percati.lenguapp.presentacion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrasteWcagTest {

    @Test
    fun `negro sobre blanco da el contraste maximo, 21 a 1`() {
        assertEquals(21.0, contrasteWcag("#000000", "#FFFFFF"), 0.01)
    }

    @Test
    fun `el mismo color consigo mismo da 1 a 1, el minimo posible`() {
        assertEquals(1.0, contrasteWcag("#3A5F43", "#3A5F43"), 0.01)
    }

    @Test
    fun `el orden de los argumentos no cambia el resultado`() {
        assertEquals(contrasteWcag("#242E26", "#F4F1EA"), contrasteWcag("#F4F1EA", "#242E26"), 0.0001)
    }

    // --- AJUSTES-FASE-7.md, bloque 1: los dos pares que pidio medir explicitamente ---

    @Test
    fun `Editorial claro- texto sobre superficie cumple AA con margen amplio`() {
        val razon = contrasteWcag("#242E26", "#F4F1EA")
        assertTrue("esperaba >= 4.5, fue $razon", razon >= CONTRASTE_MINIMO_AA_TEXTO)
        assertEquals(12.46, razon, 0.01)
        assertTrue(cumpleAaTexto("#242E26", "#F4F1EA"))
    }

    @Test
    fun `Academia oscuro- secundario sobre superficie cumple AA, es el caso mas ajustado de los cuatro temas`() {
        val razon = contrasteWcag("#94A3B8", "#1E293B")
        assertTrue("esperaba >= 4.5, fue $razon", razon >= CONTRASTE_MINIMO_AA_TEXTO)
        assertEquals(5.71, razon, 0.01)
        assertTrue(cumpleAaTexto("#94A3B8", "#1E293B"))
    }

    // --- Hallazgo no pedido explicitamente, pero que aparecio al medir todos los pares: reportado, no corregido en silencio ---

    @Test
    fun `Editorial claro- el color Secundario de la paleta NO cumple AA como texto sobre su propia superficie`() {
        // #708090 es el "Secundario" de la familia Editorial (ver
        // AJUSTES-FASE-7.md). No es ninguno de los dos pares que se pidio
        // medir, pero surgio al barrer los seis roles de los cuatro temas:
        // ver el resumen en el mensaje de revision del Bloque 1.
        val sobreSuperficie = contrasteWcag("#708090", "#F4F1EA")
        val sobreFondo = contrasteWcag("#708090", "#FDFBF7")
        assertTrue("se esperaba que fallara AA (documentado): $sobreSuperficie", sobreSuperficie < CONTRASTE_MINIMO_AA_TEXTO)
        assertTrue("se esperaba que fallara AA (documentado): $sobreFondo", sobreFondo < CONTRASTE_MINIMO_AA_TEXTO)
    }
}
