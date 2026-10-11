package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextStyle
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.FuenteTema
import io.github.percati.lenguapp.modelo.ModoTema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tipografias (Ronda G, tarea 2): la opcion elegida se aplica a TODA la escala de Material 3 y es independiente del tema de color. */
@RunWith(RobolectricTestRunner::class)
class TipografiaTemaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun estilos(t: androidx.compose.material3.Typography): List<TextStyle> = listOf(
        t.displayLarge, t.displayMedium, t.displaySmall, t.headlineLarge, t.headlineMedium, t.headlineSmall,
        t.titleLarge, t.titleMedium, t.titleSmall, t.bodyLarge, t.bodyMedium, t.bodySmall, t.labelLarge, t.labelMedium, t.labelSmall,
    )

    @Test
    fun `Lectura aplica Literata a los 15 estilos de la escala, no solo al cuerpo`() {
        val t = tipografiaProporcional(familia = familiaTipografica(FuenteTema.LECTURA))
        assertTrue(estilos(t).all { it.fontFamily == FamiliaLectura })
    }

    @Test
    fun `Clara aplica Atkinson a los 15 estilos y Sistema deja la del dispositivo`() {
        assertTrue(estilos(tipografiaProporcional(familia = familiaTipografica(FuenteTema.CLARA))).all { it.fontFamily == FamiliaClara })
        assertNull(familiaTipografica(FuenteTema.SISTEMA))
        // Sistema: no se toca la familia base de Material 3 (sans-serif del dispositivo)
        val sistema = estilos(tipografiaProporcional(familia = null)).map { it.fontFamily }.toSet()
        assertEquals(setOf<androidx.compose.ui.text.font.FontFamily?>(androidx.compose.ui.text.font.FontFamily.SansSerif), sistema)
        assertTrue(sistema.none { it == FamiliaLectura || it == FamiliaClara })
    }

    @Test
    fun `el interlineado proporcional se conserva con cualquier tipografia`() {
        for (f in FuenteTema.entries) {
            val t = tipografiaProporcional(familia = familiaTipografica(f))
            assertEquals(16f * 1.5f, t.bodyLarge.lineHeight.value, 0.01f)
        }
    }

    @Test
    fun `el tema entrega la tipografia elegida, sea cual sea el color`() {
        var vista: Pair<FamiliaTema, androidx.compose.ui.text.font.FontFamily?>? = null
        composeTestRule.setContent {
            TemaLenguApp(FamiliaTema.TERRACOTA, ModoTema.OSCURO, FuenteTema.LECTURA) {
                vista = FamiliaTema.TERRACOTA to MaterialTheme.typography.bodyLarge.fontFamily
                Text("Muestra", style = MaterialTheme.typography.bodyLarge)
            }
        }
        composeTestRule.onNodeWithText("Muestra").assertExists()
        assertEquals(FamiliaLectura, vista!!.second)
    }

    @Test
    fun `las dos fuentes embebidas se cargan y dibujan sin romper, en redondo, cursiva y negrita`() {
        composeTestRule.setContent {
            for (f in listOf(FuenteTema.LECTURA, FuenteTema.CLARA)) {
                TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO, f) {
                    androidx.compose.foundation.layout.Column {
                        Text("Redondo ${f.name} «á»", style = MaterialTheme.typography.bodyLarge)
                        Text("Cursiva ${f.name}", style = MaterialTheme.typography.bodyLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                        Text("Negrita ${f.name}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold))
                        Text("Medium ${f.name}", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium))
                    }
                }
            }
        }
        composeTestRule.onNodeWithText("Redondo LECTURA «á»").assertExists()
        composeTestRule.onNodeWithText("Negrita CLARA").assertExists()
    }
}
