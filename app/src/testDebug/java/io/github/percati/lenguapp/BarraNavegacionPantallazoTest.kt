package io.github.percati.lenguapp

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
import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

/**
 * AJUSTES-FASE-7.md, bloque 2.1, criterio de aceptacion "no negociable":
 * verificar el diseño en las cuatro combinaciones -semana actual y semana
 * anterior, por cada uno de los dos idiomas- y ademas con la fuente del
 * sistema al 150 %. Esto renderiza la pantalla completa en cada una de las
 * 8 combinaciones (4 x 2 escalas de fuente) y guarda un PNG para inspeccion
 * visual en build/pantallazos-fase7/ -- mismo mecanismo que
 * ContenidoSemanalScreenRenderTest (Fase 3): captureToImage() se cuelga
 * bajo Robolectric, dibujar la decorView a mano no.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class BarraNavegacionPantallazoTest(private val nombreCaso: String, private val semanaActual: Boolean, private val idioma: Idioma, private val escalaFuente: Float) {

    companion object {
        private const val ANCHO_DP = 360
        private const val ALTO_DP = 800

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun casos(): List<Array<Any>> = listOf(
            arrayOf("semana-actual_de_100pct", true, Idioma.DE, 1.0f),
            arrayOf("semana-actual_en_100pct", true, Idioma.EN, 1.0f),
            arrayOf("semana-anterior_de_100pct", false, Idioma.DE, 1.0f),
            arrayOf("semana-anterior_en_100pct", false, Idioma.EN, 1.0f),
            arrayOf("semana-actual_de_150pct", true, Idioma.DE, 1.5f),
            arrayOf("semana-actual_en_150pct", true, Idioma.EN, 1.5f),
            arrayOf("semana-anterior_de_150pct", false, Idioma.DE, 1.5f),
            arrayOf("semana-anterior_en_150pct", false, Idioma.EN, 1.5f),
        )

        private fun carpetaAssets(): File =
            listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun cargarContenido(): Map<String, ContenidoSemanal> =
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }

    private fun resolverDePrueba(idioma: Idioma, nivel: Nivel, fecha: LocalDate): ResultadoSemana {
        val nombreCalendario = "calendario_2026_${idioma.name.lowercase()}_${nivel.name}.json"
        val archivoCalendario = File(carpetaAssets(), "calendario/$nombreCalendario")
        val calendario = if (archivoCalendario.isFile) {
            CalendarioCargado.Encontrado(parsearCalendario(archivoCalendario.readText()))
        } else {
            CalendarioCargado.SinCalendarioParaIdiomaONivel
        }
        return resolverContenidoDeLaSemana(fecha, idioma, nivel, calendario, cargarContenido())
    }

    @Test
    fun `renderiza sin excepciones, y se guarda para inspeccion visual`() {
        // "Semana actual" es la fecha real de hoy (para que "esHoy" de
        // verdad de "true", no solo que el resolutor devuelva otra cosa);
        // "semana anterior", tres semanas antes.
        val fechaHoy = LocalDate.now()
        val fechaVistaDePrueba = if (semanaActual) fechaHoy else fechaHoy.minusWeeks(3)

        composeTestRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = escalaFuente)) {
                Box(Modifier.size(ANCHO_DP.dp, ALTO_DP.dp)) {
                    LenguAppApp(
                        ajustesIniciales = Ajustes(
                            idiomasAprendidos = mapOf(idioma to if (idioma == Idioma.DE) Nivel.B2 else Nivel.C1),
                            idiomaBase = idioma,
                            idiomaInterfaz = idioma,
                        ),
                        idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                        resolver = ::resolverDePrueba,
                        onGuardarAjustes = {},
                        fechaInicial = fechaVistaDePrueba,
                    )
                }
            }
        }
        composeTestRule.waitForIdle()

        guardarPantallazo(nombreCaso)
    }

    private fun guardarPantallazo(nombre: String) {
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

        val destino = File("build/pantallazos-fase7", "$nombre.png")
        destino.parentFile?.mkdirs()
        FileOutputStream(destino).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
