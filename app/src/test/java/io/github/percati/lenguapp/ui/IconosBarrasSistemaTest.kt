package io.github.percati.lenguapp.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import io.github.percati.lenguapp.modelo.FamiliaTema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Iconos de la barra de estado y de navegacion (Ronda K, tarea 1): oscuros sobre fondo claro y claros sobre fondo
 * oscuro, en los 4 temas y en claro y oscuro -- decididos por el fondo real del tema, no por el modo del sistema.
 */
class IconosBarrasSistemaTest {

    private val temas = listOf(FamiliaTema.PAPEL, FamiliaTema.TERRACOTA, FamiliaTema.MAR, FamiliaTema.GRAFITO)

    private fun razon(a: Color, b: Color): Double {
        val l1 = a.luminance().toDouble()
        val l2 = b.luminance().toDouble()
        return (maxOf(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
    }

    @Test
    fun `los 4 temas en claro piden iconos oscuros y en oscuro iconos claros`() {
        for (tema in temas) {
            assertTrue("$tema claro", iconosOscurosSobre(paletaPara(tema, oscuro = false).fondo))
            assertFalse("$tema oscuro", iconosOscurosSobre(paletaPara(tema, oscuro = true).fondo))
        }
    }

    @Test
    fun `la eleccion es siempre la de mayor contraste contra el fondo`() {
        for (tema in temas) for (oscuro in listOf(false, true)) {
            val fondo = paletaPara(tema, oscuro).fondo
            val conNegro = razon(fondo, Color.Black)
            val conBlanco = razon(fondo, Color.White)
            assertEquals("$tema oscuro=$oscuro", conNegro >= conBlanco, iconosOscurosSobre(fondo))
            assertTrue("$tema oscuro=$oscuro: el icono elegido se lee (>= 4.5)", maxOf(conNegro, conBlanco) >= 4.5)
        }
    }

    @Test
    fun `un fondo dinamico intermedio tambien elige el de mayor contraste`() {
        for (g in 0..255 step 15) {
            val fondo = Color(g / 255f, g / 255f, g / 255f)
            assertEquals("gris $g", razon(fondo, Color.Black) >= razon(fondo, Color.White), iconosOscurosSobre(fondo))
        }
    }
}
