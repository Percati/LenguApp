package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.CompositionLocalProvider
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Columna de lectura (Ronda G, tarea 4): en una pantalla ancha (tablet) el texto de la ficha no se
 * estira de borde a borde, y en una angosta usa todo el ancho.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w1000dp-h4000dp")
class ColumnaLecturaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun ficha(): Ficha =
        listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
            .listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>().first { it.nivel == Nivel.B2 }

    @Test
    fun `en una pantalla de 1000 dp el texto de la ficha ocupa como mucho 640 dp, centrado`() {
        val f = ficha()
        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, 1f)) {
                TemaLenguApp(FamiliaTema.PAPEL, ModoTema.CLARO) { ContenidoSemanalScreen(f, idiomaBase = Idioma.ES) }
            }
        }
        val nodos = composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true).fetchSemanticsNodes()
        // Los parrafos (texto largo): el cuerpo de la ficha. (Las insignias van en una fila con scroll horizontal propio.)
        val parrafos = nodos.filter { n -> (n.config.getOrNull(SemanticsProperties.Text)?.firstOrNull()?.text?.length ?: 0) > 80 }
        assertTrue(parrafos.isNotEmpty())
        val izquierda = parrafos.minOf { it.boundsInRoot.left }
        val derecha = parrafos.maxOf { it.boundsInRoot.right }
        assertTrue("columna de ${derecha - izquierda} dp, debia ser <= 640", derecha - izquierda <= 640f)
        assertTrue("izquierda=$izquierda", izquierda > 100f)
    }
}
