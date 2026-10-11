package io.github.percati.lenguapp.presentacion

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.ui.aColorScheme
import io.github.percati.lenguapp.ui.paletaPara
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrasteWcagTest {

    // --- la formula ---

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

    // --- Ronda G: los cuatro temas x dos modos, medidos sobre los ColorScheme REALES ---

    private fun hex(c: Color): String = "#%06X".format(c.toArgb() and 0xFFFFFF)

    private val temasDeColor = listOf(FamiliaTema.PAPEL, FamiliaTema.TERRACOTA, FamiliaTema.MAR, FamiliaTema.GRAFITO)

    /** Los valores del documento de direccion visual (fondo, superficie, texto, acento, secundario). */
    private val esperado = mapOf(
        (FamiliaTema.PAPEL to false) to listOf("#F7F6F2", "#FFFFFF", "#1F2328", "#2F5D8A", "#5A6169"),
        (FamiliaTema.PAPEL to true) to listOf("#14171A", "#1E2226", "#E6E8EA", "#8DB4DE", "#A3ABB2"),
        (FamiliaTema.TERRACOTA to false) to listOf("#FAF5EF", "#FFFFFF", "#2B2320", "#B4542F", "#6B5D55"),
        (FamiliaTema.TERRACOTA to true) to listOf("#1A1512", "#251E1A", "#F0E7DF", "#E59A78", "#BDADA3"),
        (FamiliaTema.MAR to false) to listOf("#F2F7F7", "#FFFFFF", "#172326", "#1F7A80", "#4F6568"),
        (FamiliaTema.MAR to true) to listOf("#0F1A1C", "#172426", "#DFEDEE", "#6FC4C9", "#9FB8BB"),
        (FamiliaTema.GRAFITO to false) to listOf("#F5F5F5", "#FFFFFF", "#212121", "#3A3A3A", "#5C5C5C"),
        (FamiliaTema.GRAFITO to true) to listOf("#161616", "#212121", "#EAEAEA", "#CFCFCF", "#A8A8A8"),
    )

    private fun esquema(familia: FamiliaTema, oscuro: Boolean): ColorScheme = paletaPara(familia, oscuro).aColorScheme(oscuro)

    @Test
    fun `los valores de cada paleta son los del documento, sin retocar`() {
        for (f in temasDeColor) for (oscuro in listOf(false, true)) {
            val e = esquema(f, oscuro)
            val obtenido = listOf(hex(e.background), hex(e.surface), hex(e.onSurface), hex(e.primary), hex(e.onSurfaceVariant))
            assertEquals("$f ${if (oscuro) "oscuro" else "claro"}", esperado.getValue(f to oscuro), obtenido)
            // onAcento: blanco en claro, el fondo en oscuro
            val onAcento = hex(e.onPrimary)
            assertEquals(if (oscuro) hex(e.background) else "#FFFFFF", onAcento)
        }
    }

    @Test
    fun `los cuatro temas en claro y oscuro cumplen AA, 4,5 a 1, en todos los pares de texto`() {
        val fallos = mutableListOf<String>()
        for (f in temasDeColor) for (oscuro in listOf(false, true)) {
            val e = esquema(f, oscuro)
            val modo = if (oscuro) "oscuro" else "claro"
            val pares = mapOf(
                "texto/fondo" to (e.onBackground to e.background),
                "texto/superficie" to (e.onSurface to e.surface),
                "secundario/fondo" to (e.onSurfaceVariant to e.background),
                "secundario/superficie" to (e.onSurfaceVariant to e.surface),
                "onAcento/acento" to (e.onPrimary to e.primary),
                "chip seleccionado (secondaryContainer) / su texto" to (e.onSecondaryContainer to e.secondaryContainer),
                // el acento como texto o icono accionable sobre el fondo y la superficie
                "acento/fondo" to (e.primary to e.background),
                "acento/superficie" to (e.primary to e.surface),
            )
            for ((nombre, par) in pares) {
                val razon = contrasteWcag(hex(par.first), hex(par.second))
                if (razon < CONTRASTE_MINIMO_AA_TEXTO) fallos += "%s %s - %s: %.2f".format(f, modo, nombre, razon)
            }
        }
        assertTrue("pares bajo 4,5:1 (se reportan, no se corrigen):\n" + fallos.joinToString("\n"), fallos.isEmpty())
    }

    @Test
    fun `el modo oscuro y el claro de cada tema difieren, y el acento nunca es el color del fondo`() {
        for (f in temasDeColor) {
            val claro = esquema(f, false)
            val oscuro = esquema(f, true)
            assertTrue(hex(claro.background) != hex(oscuro.background))
            assertTrue(hex(claro.primary) != hex(claro.background))
            assertTrue(hex(oscuro.primary) != hex(oscuro.background))
        }
    }
}
