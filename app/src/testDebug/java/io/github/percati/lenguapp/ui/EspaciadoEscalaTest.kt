package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Espaciado proporcional a la letra (Ronda F2, tarea 3): con fontScale 1,0 / 1,3 / 1,6 el espacio
 * entre viñetas crece con la escala (no son dp fijos) y ningun texto se sale de la pantalla.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class EspaciadoEscalaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // --- pura: calcularEspaciado ---

    @Test
    fun `los espacios son multiplos del tamano de letra tal como se dibuja, y crecen con fontScale`() {
        for (escala in listOf(1.0f, 1.3f, 1.6f)) {
            val d = Density(2f, escala)
            val e = calcularEspaciado(14f, 16f, d)
            // El tamano de letra REAL en px (con la conversion sp->px del sistema, que puede no ser lineal en fontScale altos).
            val cuerpoPx = with(d) { 14f.sp.toPx() }
            val grandePx = with(d) { 16f.sp.toPx() }
            assertEquals(cuerpoPx * FACTOR_ENTRE_VINIETAS, with(d) { e.entreVinietas.toPx() }, 1f)
            assertEquals(grandePx * FACTOR_ENTRE_SECCIONES, with(d) { e.entreSecciones.toPx() }, 1f)
            assertEquals(cuerpoPx * FACTOR_DENTRO_DE_ITEM, with(d) { e.dentroDeItem.toPx() }, 1f)
            assertTrue(with(d) { e.entreVinietas.toPx() } in cuerpoPx * 0.3f..cuerpoPx * 0.4f)
        }
        // crecen con la escala (no son dp fijos)
        val (b, m, a) = listOf(1.0f, 1.3f, 1.6f).map { calcularEspaciado(14f, 16f, Density(2f, it)) }
        assertTrue(b.entreVinietas < m.entreVinietas && m.entreVinietas < a.entreVinietas)
        assertTrue(b.entreSecciones < m.entreSecciones && m.entreSecciones < a.entreSecciones)
        assertTrue(b.entreItems < m.entreItems && m.entreItems < a.entreItems)
    }

    @Test
    fun `el interlineado del cuerpo es proporcional, 1,45 x el tamano, en sp`() {
        val t = tipografiaProporcional()
        assertEquals(14f * 1.45f, t.bodyMedium.lineHeight.value, 0.01f)
        assertEquals(16f * 1.45f, t.bodyLarge.lineHeight.value, 0.01f)
        assertTrue(t.bodyMedium.lineHeight.isSp)
    }

    // --- en pantalla: una ficha real con 3 escalas ---

    private fun ficha(): Ficha =
        listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
            .listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.nivel == Nivel.B2 && it.notas.size >= 3 && it.notas.take(3).all { n -> n.resolver(it.idioma, it.idioma).length > 20 } }

    private fun limpio(t: String) = t.replace("*", "").replace("«", "").replace("»", "")

    /** Brecha vertical (px) entre las dos primeras notas, y los limites de todos los textos, con una escala de letra. */
    private fun medir(escala: Float): Triple<Float, Float, Float> {
        val f = ficha()
        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(2f, escala)) {
                TemaLenguApp(familiaTema = io.github.percati.lenguapp.modelo.FamiliaTema.ACADEMIA, modoTema = io.github.percati.lenguapp.modelo.ModoTema.CLARO) {
                    ContenidoSemanalScreen(f, idiomaBase = Idioma.ES)
                }
            }
        }
        val nodos = composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true).fetchSemanticsNodes()
        fun nodoDe(i: Int): Rect {
            val frag = limpio(f.notas[i].resolver(f.idioma, f.idioma)).take(25)
            return nodos.first { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().any { limpio(it.text).contains(frag) } }.boundsInRoot
        }
        val n0 = nodoDe(0)
        val n1 = nodoDe(1)
        val maxDerecha = nodos.maxOf { it.boundsInRoot.right }
        val minIzquierda = nodos.minOf { it.boundsInRoot.left }
        return Triple(n1.top - n0.bottom, maxDerecha, minIzquierda)
    }

    private fun verificar(escala: Float) {
        val ancho = 400f * 2f // w400dp a densidad 2 de la prueba
        val r = medir(escala)
        val esperado = with(Density(2f, escala)) { 14f.sp.toPx() } * FACTOR_ENTRE_VINIETAS
        assertEquals("escala $escala: brecha entre viñetas", esperado, r.first, 3f)
        assertTrue("escala $escala: algo se sale por la derecha (${r.second} > $ancho)", r.second <= ancho + 1f)
        assertTrue("escala $escala: algo se sale por la izquierda (${r.third})", r.third >= -1f)
    }

    // La brecha esperada es 0,35 x el cuerpo tal como se dibuja a cada escala: crece con la letra.
    @Test
    fun `fontScale 1,0`() = verificar(1.0f)

    @Test
    fun `fontScale 1,3`() = verificar(1.3f)

    @Test
    fun `fontScale 1,6`() = verificar(1.6f)
}
