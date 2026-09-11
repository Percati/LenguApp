package io.github.percati.lenguapp.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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
 * AJUSTES-FASE-7.md, bloque 1: "en Satzbau marca donde va el verbo; si se
 * pierde, el tema esta mal, no la ficha" -- se pidio medir, no suponer. Este
 * test no hace una asercion automatica de pixeles (el destaque es
 * FontWeight.Bold sobre el mismo color de texto, no un cambio de color, asi
 * que no hay un pixel-diff simple que lo capture): guarda un PNG de
 * DE-G01-B2-1 (Satzbau) en cada uno de los cuatro temas para revision visual
 * en build/pantallazos-fase7-bloque1/, igual que el criterio de aceptacion
 * de la Fase 3 en ContenidoSemanalScreenRenderTest.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TemaSatzbauPantallazoTest(
    private val nombreTema: String,
    private val familiaTema: FamiliaTema,
    private val modoTema: ModoTema,
) {

    companion object {
        private const val ANCHO_DP = 360
        private const val ALTO_DP = 1400

        private fun carpetaAssets(): File {
            val candidatos = listOf(File("src/main/assets"), File("app/src/main/assets"))
            return candidatos.firstOrNull { it.isDirectory } ?: error("no se encontro app/src/main/assets")
        }

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun temas(): List<Array<Any>> = listOf(
            arrayOf("academia-claro", FamiliaTema.ACADEMIA, ModoTema.CLARO),
            arrayOf("academia-oscuro", FamiliaTema.ACADEMIA, ModoTema.OSCURO),
            arrayOf("editorial-claro", FamiliaTema.EDITORIAL, ModoTema.CLARO),
            arrayOf("editorial-oscuro", FamiliaTema.EDITORIAL, ModoTema.OSCURO),
        )
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `Satzbau con su cuadro de referencia se renderiza en el tema`() {
        val contenido = parsearContenido(File(carpetaAssets(), "contenido/DE-G01-B2-1.json").readText())

        composeTestRule.setContent {
            TemaLenguApp(familiaTema = familiaTema, modoTema = modoTema) {
                // MaterialTheme no pinta fondo por si solo: el Surface es lo
                // que aplica colorScheme.background, igual que en MainActivity.kt.
                Surface(modifier = Modifier.size(ANCHO_DP.dp, ALTO_DP.dp)) {
                    Box {
                        ContenidoSemanalScreen(contenido, idiomaBase = Idioma.ES)
                    }
                }
            }
        }
        composeTestRule.waitForIdle()

        guardarPantallazo(nombreTema)
    }

    private fun guardarPantallazo(nombreTema: String) {
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

        val destino = File("build/pantallazos-fase7-bloque1", "satzbau-$nombreTema.png")
        destino.parentFile?.mkdirs()
        FileOutputStream(destino).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
