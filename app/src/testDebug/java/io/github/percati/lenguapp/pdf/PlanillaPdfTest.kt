package io.github.percati.lenguapp.pdf

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.datos.parsearPlanillaTextos
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.presentacion.construirPlanilla
import android.graphics.Bitmap
import android.graphics.Canvas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Generacion del PDF de la planilla (Ronda B, pieza 3): no crashea y entra en una o dos paginas. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PlanillaPdfTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private val textos = parsearPlanillaTextos(File(assets(), "plantillas/planilla-profesor.json").readText())
    private val topicNombres = parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText())

    /**
     * PdfDocument es nativo y no corre bajo Robolectric ("document is closed"), asi
     * que el layout y los saltos de pagina se prueban contra un Bitmap por pagina; la
     * escritura real del PDF solo se puede ver en un dispositivo.
     */
    private fun paginas(id: String, idioma: Idioma): Int {
        val contenido = parsearContenido(File(assets(), "contenido/$id.json").readText())
        val planilla = construirPlanilla(contenido, textos.getValue(idioma), topicNombres = topicNombres)
        return PlanillaPdf.dibujar(planilla, object : PlanillaPdf.Paginador {
            override fun abrir(numero: Int): Canvas = Canvas(Bitmap.createBitmap(595, 842, Bitmap.Config.ARGB_8888))
            override fun cerrar() = Unit
        })
    }

    private fun verificar(id: String, idioma: Idioma) {
        val n = paginas(id, idioma)
        assertTrue("$id: $n paginas", n in 1..2)
    }

    @Test
    fun `un par A2 (aleman) genera el PDF sin crashear, en una o dos paginas`() = verificar("DE-F01-A2-2026-1", Idioma.DE)

    @Test
    fun `un par C1 (ingles) genera el PDF sin crashear, en una o dos paginas`() = verificar("EN-F01-C1-2026-1", Idioma.EN)

    @Test
    fun `una semana de repaso genera el PDF sin crashear, en una o dos paginas`() = verificar("REVIEW-DE-B2-2026-S40", Idioma.DE)

    @Test
    fun `una semana Survival tambien`() = verificar("SURVIVAL-EN-B2-2026-S44", Idioma.EN)

    @Test
    fun `envolver parte en espacios y respeta el ancho`() {
        val paint = android.graphics.Paint().apply { textSize = 11f }
        val lineas = PlanillaPdf.envolver("uno dos tres cuatro cinco seis siete ocho nueve diez", paint, 80f)
        assertTrue(lineas.size > 1)
        assertEquals("uno dos tres cuatro cinco seis siete ocho nueve diez", lineas.joinToString(" "))
    }
}
