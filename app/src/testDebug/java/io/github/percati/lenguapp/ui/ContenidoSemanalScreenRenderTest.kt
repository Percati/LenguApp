package io.github.percati.lenguapp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Criterio de aceptacion de la Fase 3: las 34 piezas de contenido se
 * renderizan sin recortes ni desbordes, en pantalla estrecha y con fuente
 * grande. Esto renderiza cada una de las 34 con Robolectric (sin emulador),
 * a 360dp de ancho y fontScale 1.5, y guarda un PNG del viewport (360x800dp)
 * para inspeccion visual en build/pantallazos-fase3/. Que no tire excepcion
 * ya cubre "no se rompe"; el PNG es lo que permite revisar "no se corta ni
 * se desborda" a ojo.
 *
 * No usa SemanticsNodeInteraction.captureToImage(): esa API pasa por un
 * forceRedraw + PixelCopy pensado para una ventana real y se cuelga bajo
 * Robolectric (ComposeTimeoutException). Dibujar la decorView a mano sobre
 * un Canvas respaldado por un Bitmap evita ese camino roto.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ContenidoSemanalScreenRenderTest(private val nombreArchivo: String) {

    companion object {
        private const val ANCHO_DP = 360
        private const val ALTO_DP = 800

        private fun carpetaAssets(): File {
            val candidatos = listOf(File("src/main/assets"), File("app/src/main/assets"))
            return candidatos.firstOrNull { it.isDirectory } ?: error("no se encontro app/src/main/assets")
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun archivos(): List<String> =
            File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
                .map { it.name }
                .sorted()
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `renderiza sin excepciones, angosta y con fuente grande`() {
        val contenido = parsearContenido(File(carpetaAssets(), "contenido/$nombreArchivo").readText())

        composeTestRule.setContent {
            TemaLenguApp(familiaTema = FamiliaTema.ACADEMIA, modoTema = ModoTema.CLARO) {
                CompositionLocalProvider(
                    LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.5f),
                ) {
                    Box(Modifier.size(ANCHO_DP.dp, ALTO_DP.dp)) {
                        ContenidoSemanalScreen(contenido, idiomaBase = Idioma.ES)
                    }
                }
            }
        }
        composeTestRule.waitForIdle()

        guardarPantallazo(nombreArchivo)
    }

    private fun guardarPantallazo(nombreJson: String) {
        val activity = composeTestRule.activity
        val vista: View = activity.window.decorView
        val densidad = activity.resources.displayMetrics.density
        val anchoPx = (ANCHO_DP * densidad).toInt()
        val altoPx = (ALTO_DP * densidad).toInt()

        vista.measure(
            View.MeasureSpec.makeMeasureSpec(anchoPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(altoPx, View.MeasureSpec.EXACTLY),
        )
        vista.layout(0, 0, anchoPx, altoPx)

        val bitmap = Bitmap.createBitmap(anchoPx, altoPx, Bitmap.Config.ARGB_8888)
        vista.draw(Canvas(bitmap))

        val destino = File("build/pantallazos-fase3", nombreJson.removeSuffix(".json") + ".png")
        destino.parentFile?.mkdirs()
        FileOutputStream(destino).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
