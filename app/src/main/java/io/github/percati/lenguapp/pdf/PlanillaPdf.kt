package io.github.percati.lenguapp.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import io.github.percati.lenguapp.presentacion.PlanillaContenido
import io.github.percati.lenguapp.presentacion.SeccionPlanilla
import java.io.OutputStream

/**
 * Dibuja la planilla del profesor con `android.graphics.pdf.PdfDocument` (ya
 * incluido en Android: sin librerias de terceros, sin red). A4 en puntos
 * (595 x 842, 72 por pulgada) con margenes de 20 mm -- por encima del minimo
 * de impresion de 15 mm --. Estetica sobria: titulo grande, regla fina,
 * secciones en verde oscuro (el acento del tema Academia), cuerpo en gris casi
 * negro, sin colores de fondo (se imprime bien en blanco y negro).
 *
 * Devuelve la cantidad de paginas escritas; el contenido esta pensado para
 * entrar en una o dos.
 */
object PlanillaPdf {
    private const val ANCHO_PAGINA = 595
    private const val ALTO_PAGINA = 842
    private const val MARGEN = 56.7f // 20 mm
    private const val SANGRIA_VINIETA = 14f
    private const val ESPACIO_ENTRE_SECCIONES = 14f

    private fun pintura(tamanio: Float, color: Int, negrita: Boolean = false, cursiva: Boolean = false) =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = tamanio
            this.color = color
            typeface = Typeface.create(
                Typeface.SANS_SERIF,
                when {
                    negrita && cursiva -> Typeface.BOLD_ITALIC
                    negrita -> Typeface.BOLD
                    cursiva -> Typeface.ITALIC
                    else -> Typeface.NORMAL
                },
            )
        }

    private val titulo = pintura(22f, Color.rgb(0x1F, 0x2A, 0x22), negrita = true)
    private val encabezado = pintura(12f, Color.rgb(0x55, 0x60, 0x5A))
    private val seccion = pintura(12.5f, Color.rgb(0x3A, 0x5F, 0x43), negrita = true)
    private val cuerpo = pintura(11f, Color.rgb(0x1A, 0x1A, 0x1A))
    private val recordatorio = pintura(10.5f, Color.rgb(0x44, 0x44, 0x44), cursiva = true)
    private val regla = Paint().apply { color = Color.rgb(0xBB, 0xBB, 0xBB); strokeWidth = 0.7f }

    /** Parte `texto` en lineas que entren en `ancho` puntos, cortando solo en espacios (una palabra mas larga que la linea se deja entera). */
    internal fun envolver(texto: String, paint: Paint, ancho: Float): List<String> {
        val lineas = mutableListOf<String>()
        for (parrafo in texto.split('\n')) {
            var actual = StringBuilder()
            for (palabra in parrafo.split(' ').filter { it.isNotEmpty() }) {
                val candidata = if (actual.isEmpty()) palabra else "$actual $palabra"
                if (actual.isNotEmpty() && paint.measureText(candidata) > ancho) {
                    lineas += actual.toString()
                    actual = StringBuilder(palabra)
                } else {
                    actual = StringBuilder(candidata)
                }
            }
            if (actual.isNotEmpty()) lineas += actual.toString()
        }
        return lineas
    }

    /** Abstrae de donde sale cada pagina: PdfDocument en el dispositivo, un Bitmap en los tests (PdfDocument es nativo y no corre bajo Robolectric). */
    internal interface Paginador {
        fun abrir(numero: Int): Canvas
        fun cerrar()
    }

    fun escribir(contenido: PlanillaContenido, salida: OutputStream): Int {
        val doc = PdfDocument()
        var pagina: PdfDocument.Page? = null
        val paginas = dibujar(
            contenido,
            object : Paginador {
                override fun abrir(numero: Int): Canvas {
                    val p = doc.startPage(PdfDocument.PageInfo.Builder(ANCHO_PAGINA, ALTO_PAGINA, numero).create())
                    pagina = p
                    return p.canvas
                }

                override fun cerrar() {
                    pagina?.let { doc.finishPage(it) }
                    pagina = null
                }
            },
        )
        doc.writeTo(salida)
        doc.close()
        return paginas
    }

    /** Layout y saltos de pagina; devuelve la cantidad de paginas. Toda la logica de dibujo vive aca, independiente de PdfDocument. */
    internal fun dibujar(contenido: PlanillaContenido, paginador: Paginador): Int {
        var numero = 0
        var canvas: Canvas? = null
        var y = MARGEN
        val limiteInferior = ALTO_PAGINA - MARGEN
        val anchoTexto = ANCHO_PAGINA - 2 * MARGEN

        fun abrirPagina() {
            if (numero > 0) paginador.cerrar()
            numero += 1
            canvas = paginador.abrir(numero)
            y = MARGEN
        }

        fun asegurarEspacio(alto: Float) {
            if (y + alto > limiteInferior) abrirPagina()
        }

        fun lineaTexto(texto: String, paint: Paint, x: Float) {
            val alto = paint.textSize * 1.4f
            asegurarEspacio(alto)
            y += paint.textSize
            canvas!!.drawText(texto, x, y, paint)
            y += alto - paint.textSize
        }

        abrirPagina()
        for (l in envolver(contenido.titulo, titulo, anchoTexto)) lineaTexto(l, titulo, MARGEN)
        for (l in envolver(contenido.encabezado, encabezado, anchoTexto)) lineaTexto(l, encabezado, MARGEN)
        y += 4f
        canvas!!.drawLine(MARGEN, y, ANCHO_PAGINA - MARGEN, y, regla)
        y += ESPACIO_ENTRE_SECCIONES

        for (sec in contenido.secciones) {
            dibujarSeccion(sec, ::asegurarEspacio, ::lineaTexto, { y += it }, { canvas!! }, { y }, anchoTexto)
        }
        paginador.cerrar()
        return numero
    }

    private fun dibujarSeccion(
        sec: SeccionPlanilla,
        asegurarEspacio: (Float) -> Unit,
        lineaTexto: (String, Paint, Float) -> Unit,
        avanzar: (Float) -> Unit,
        canvas: () -> Canvas,
        yActual: () -> Float,
        anchoTexto: Float,
    ) {
        val esRecordatorio = sec.titulo.isEmpty()
        val paintCuerpo = if (esRecordatorio) recordatorio else cuerpo
        // Un titulo de seccion nunca queda solo al pie de una pagina: se pide lugar para el titulo y la primera linea.
        asegurarEspacio(seccion.textSize * 1.4f + paintCuerpo.textSize * 1.4f + 4f)
        if (esRecordatorio) {
            canvas().drawLine(MARGEN, yActual(), ANCHO_PAGINA - MARGEN, yActual(), regla)
            avanzar(8f)
        } else {
            lineaTexto(sec.titulo, seccion, MARGEN)
            avanzar(3f)
        }
        for (linea in sec.lineas) {
            val x = if (sec.conVinietas) MARGEN + SANGRIA_VINIETA else MARGEN
            val partes = envolver(linea, paintCuerpo, anchoTexto - (x - MARGEN))
            partes.forEachIndexed { i, parte ->
                if (sec.conVinietas && i == 0) {
                    asegurarEspacio(paintCuerpo.textSize * 1.4f)
                    canvas().drawText("•", MARGEN + 2f, yActual() + paintCuerpo.textSize, paintCuerpo)
                }
                lineaTexto(parte, paintCuerpo, x)
            }
            avanzar(2.5f)
        }
        avanzar(ESPACIO_ENTRE_SECCIONES - 2.5f)
    }
}
