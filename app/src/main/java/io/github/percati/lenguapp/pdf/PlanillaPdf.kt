package io.github.percati.lenguapp.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import io.github.percati.lenguapp.presentacion.PlanillaContenido
import java.io.OutputStream

/**
 * Dibuja la planilla del profesor con `android.graphics.pdf.PdfDocument` (ya
 * incluido en Android: sin librerias de terceros, sin red). A4 en puntos
 * (595 x 842, 72 por pulgada). Estetica sobria: titulo grande, regla fina,
 * secciones en verde oscuro (el acento del tema Academia), cuerpo en gris casi
 * negro, sin colores de fondo (se imprime bien en blanco y negro).
 *
 * Una hoja (dos carillas) como maximo (REGLAS-PREVENCION P8): si la segunda
 * carilla queda con [MAX_LINEAS_COLA] lineas o menos, se compacta para que todo
 * entre en una, probando en orden (1) menos interlineado y espacio entre
 * secciones, (2) cuerpo mas chico hasta [CUERPO_MINIMO] pt, (3) margenes de 15
 * mm. Si aun asi no entra, se deja con el estilo normal en dos carillas. Nunca
 * mas de dos.
 *
 * El layout ([maquetar]) produce operaciones de dibujo por pagina, sin tocar
 * PdfDocument: asi se puede probar (y ensayar cada estilo) sin dibujar nada.
 */
object PlanillaPdf {
    private const val ANCHO_PAGINA = 595
    private const val ALTO_PAGINA = 842
    private const val SANGRIA_VINIETA = 14f
    internal const val MAX_LINEAS_COLA = 3
    internal const val CUERPO_MINIMO = 9f
    private const val MM = 72f / 25.4f

    /** Medidas que se achican al compactar. */
    internal data class Estilo(
        val cuerpo: Float = 11f,
        val interlineado: Float = 1.4f,
        val espacioSeccion: Float = 14f,
        val margen: Float = 20f * MM,
    )

    /** Estilo normal y, en orden, las compactaciones que se prueban. */
    internal val NORMAL = Estilo()
    internal val COMPACTACIONES: List<Estilo> = buildList {
        // (1) interlineado y espacio entre secciones.
        val apretado = Estilo(interlineado = 1.25f, espacioSeccion = 8f)
        add(apretado)
        // (2) cuerpo hasta el minimo legible.
        for (c in listOf(10.5f, 10f, 9.5f, CUERPO_MINIMO)) add(apretado.copy(cuerpo = c))
        // (3) margenes de 15 mm (el minimo de impresion).
        add(apretado.copy(cuerpo = CUERPO_MINIMO, margen = 15f * MM))
    }

    private class Pinturas(estilo: Estilo) {
        val titulo = pintura(22f, Color.rgb(0x1F, 0x2A, 0x22), negrita = true)
        val encabezado = pintura(12f, Color.rgb(0x55, 0x60, 0x5A))
        val seccion = pintura(minOf(12.5f, estilo.cuerpo + 1.5f), Color.rgb(0x3A, 0x5F, 0x43), negrita = true)
        val cuerpo = pintura(estilo.cuerpo, Color.rgb(0x1A, 0x1A, 0x1A))
        val recordatorio = pintura(estilo.cuerpo - 0.5f, Color.rgb(0x44, 0x44, 0x44), cursiva = true)
        val regla = Paint().apply { color = Color.rgb(0xBB, 0xBB, 0xBB); strokeWidth = 0.7f }
    }

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

    /** Una operacion de dibujo ya posicionada. `vinieta`: el "•" que acompana a una linea (no cuenta como linea de texto). */
    internal sealed interface Op {
        data class Texto(val texto: String, val x: Float, val y: Float, val paint: Paint, val vinieta: Boolean = false) : Op
        data class Linea(val x1: Float, val y: Float, val x2: Float, val paint: Paint) : Op
    }

    /** El resultado de maquetar: operaciones por pagina. */
    internal class Layout(val estilo: Estilo, val paginas: List<List<Op>>) {
        val cantidadPaginas: Int get() = paginas.size

        /** Lineas de texto de la ultima pagina (sin contar las vinietas). */
        val lineasUltimaPagina: Int get() = paginas.last().count { it is Op.Texto && !it.vinieta }
    }

    internal fun maquetar(contenido: PlanillaContenido, estilo: Estilo): Layout {
        val p = Pinturas(estilo)
        val margen = estilo.margen
        val paginas = mutableListOf<MutableList<Op>>()
        var ops = mutableListOf<Op>()
        var y = margen
        val limiteInferior = ALTO_PAGINA - margen
        val anchoTexto = ANCHO_PAGINA - 2 * margen

        fun abrirPagina() {
            ops = mutableListOf()
            paginas += ops
            y = margen
        }

        fun asegurarEspacio(alto: Float) {
            if (y + alto > limiteInferior) abrirPagina()
        }

        fun lineaTexto(texto: String, paint: Paint, x: Float, conVinieta: Boolean = false) {
            val alto = paint.textSize * estilo.interlineado
            asegurarEspacio(alto)
            y += paint.textSize
            if (conVinieta) ops += Op.Texto("•", margen + 2f, y, paint, vinieta = true)
            ops += Op.Texto(texto, x, y, paint)
            y += alto - paint.textSize
        }

        abrirPagina()
        for (l in envolver(contenido.titulo, p.titulo, anchoTexto)) lineaTexto(l, p.titulo, margen)
        for (l in envolver(contenido.encabezado, p.encabezado, anchoTexto)) lineaTexto(l, p.encabezado, margen)
        y += 4f
        ops += Op.Linea(margen, y, ANCHO_PAGINA - margen, p.regla)
        y += estilo.espacioSeccion

        for (sec in contenido.secciones) {
            val esRecordatorio = sec.titulo.isEmpty()
            val paintCuerpo = if (esRecordatorio) p.recordatorio else p.cuerpo
            // Un titulo de seccion nunca queda solo al pie de una pagina: se pide lugar para el titulo y la primera linea.
            asegurarEspacio(p.seccion.textSize * estilo.interlineado + paintCuerpo.textSize * estilo.interlineado + 4f)
            if (esRecordatorio) {
                ops += Op.Linea(margen, y, ANCHO_PAGINA - margen, p.regla)
                y += 8f
            } else {
                lineaTexto(sec.titulo, p.seccion, margen)
                y += 3f
            }
            for (linea in sec.lineas) {
                val x = if (sec.conVinietas) margen + SANGRIA_VINIETA else margen
                envolver(linea, paintCuerpo, anchoTexto - (x - margen)).forEachIndexed { i, parte ->
                    lineaTexto(parte, paintCuerpo, x, conVinieta = sec.conVinietas && i == 0)
                }
                y += 2.5f
            }
            y += estilo.espacioSeccion - 2.5f
        }
        return Layout(estilo, paginas)
    }

    /**
     * El layout que se usa: el normal, salvo que quede en dos carillas con
     * [MAX_LINEAS_COLA] lineas o menos en la segunda; ahi se prueban las compactaciones
     * en orden y se toma la primera que entra en una. Si ninguna entra, el normal.
     */
    internal fun elegirLayout(contenido: PlanillaContenido): Layout {
        val normal = maquetar(contenido, NORMAL)
        if (normal.cantidadPaginas != 2 || normal.lineasUltimaPagina > MAX_LINEAS_COLA) return normal
        for (estilo in COMPACTACIONES) {
            val l = maquetar(contenido, estilo)
            if (l.cantidadPaginas == 1) return l
        }
        return normal
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

    /** Elige el layout y lo dibuja pagina por pagina; devuelve la cantidad de paginas. */
    internal fun dibujar(contenido: PlanillaContenido, paginador: Paginador): Int {
        val layout = elegirLayout(contenido)
        layout.paginas.forEachIndexed { i, ops ->
            val canvas = paginador.abrir(i + 1)
            for (op in ops) when (op) {
                is Op.Texto -> canvas.drawText(op.texto, op.x, op.y, op.paint)
                is Op.Linea -> canvas.drawLine(op.x1, op.y, op.x2, op.y, op.paint)
            }
            paginador.cerrar()
        }
        return layout.cantidadPaginas
    }
}
