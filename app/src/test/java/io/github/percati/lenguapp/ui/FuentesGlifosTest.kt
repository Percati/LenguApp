package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer

/**
 * Tipografias embebidas (Ronda G, tarea 2): cada TTF de res/font/ tiene glifos para « » ‹ › y las letras
 * acentuadas de los seis idiomas; y los caracteres que el contenido y la interfaz usan y la fuente NO cubre
 * son exactamente los documentados (para esos Android cae a la fuente del sistema, no se ven como cuadrados).
 * Lee la tabla `cmap` del TTF a mano (formatos 4 y 12), sin librerias.
 */
class FuentesGlifosTest {

    private fun res(ruta: String): File =
        listOf(File("src/main/res/$ruta"), File("app/src/main/res/$ruta")).first { it.isFile }

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    /** Todos los code points que cubre la tabla cmap (subtablas Unicode, formatos 4 y 12). */
    private fun codepoints(archivo: File): Set<Int> {
        val b = ByteBuffer.wrap(archivo.readBytes())
        val numTablas = b.getShort(4).toInt() and 0xFFFF
        var cmapOffset = -1
        for (i in 0 until numTablas) {
            val o = 12 + i * 16
            val tag = String(ByteArray(4) { b.get(o + it) }, Charsets.US_ASCII)
            if (tag == "cmap") cmapOffset = b.getInt(o + 8)
        }
        require(cmapOffset >= 0) { "${archivo.name}: sin tabla cmap" }
        val n = b.getShort(cmapOffset + 2).toInt() and 0xFFFF
        val resultado = mutableSetOf<Int>()
        for (i in 0 until n) {
            val e = cmapOffset + 4 + i * 8
            val plataforma = b.getShort(e).toInt() and 0xFFFF
            val sub = cmapOffset + b.getInt(e + 4)
            if (plataforma != 0 && plataforma != 3) continue
            when (b.getShort(sub).toInt() and 0xFFFF) {
                4 -> {
                    val segX2 = b.getShort(sub + 6).toInt() and 0xFFFF
                    val finales = sub + 14
                    val iniciales = finales + segX2 + 2
                    val deltas = iniciales + segX2
                    val rangos = deltas + segX2
                    for (s in 0 until segX2 / 2) {
                        val fin = b.getShort(finales + 2 * s).toInt() and 0xFFFF
                        val ini = b.getShort(iniciales + 2 * s).toInt() and 0xFFFF
                        if (ini == 0xFFFF) continue
                        val delta = b.getShort(deltas + 2 * s).toInt()
                        val rango = b.getShort(rangos + 2 * s).toInt() and 0xFFFF
                        for (c in ini..fin) {
                            val glifo = if (rango == 0) (c + delta) and 0xFFFF else {
                                val g = b.getShort(rangos + 2 * s + rango + 2 * (c - ini)).toInt() and 0xFFFF
                                if (g == 0) 0 else (g + delta) and 0xFFFF
                            }
                            if (glifo != 0) resultado += c
                        }
                    }
                }
                12 -> {
                    val grupos = b.getInt(sub + 12)
                    for (g in 0 until grupos) {
                        val o = sub + 16 + g * 12
                        val ini = b.getInt(o)
                        val fin = b.getInt(o + 4)
                        for (c in ini..fin) resultado += c
                    }
                }
            }
        }
        return resultado
    }

    private val familias = mapOf(
        "Literata" to listOf("font/literata.ttf", "font/literata_italic.ttf"),
        "Atkinson Hyperlegible Next" to listOf("font/atkinson.ttf", "font/atkinson_italic.ttf"),
    )

    // Los que se piden: comillas angulares y las letras de los seis idiomas
    private val requeridos = "«»‹›áéíóúñüöäãõçàèìòùâêîôûœÁÉÍÓÚÑÜÖÄÃÕÇÀÈÌÒÙÂÊÎÔÛŒß¿¡"

    @Test
    fun `cada fuente cubre las comillas angulares y las letras acentuadas de los 6 idiomas`() {
        for ((nombre, archivos) in familias) for (a in archivos) {
            val cubiertos = codepoints(res(a))
            val faltan = requeridos.filter { it.code !in cubiertos }
            assertEquals("$nombre ($a) no cubre: $faltan", "", faltan)
        }
    }

    // Lo que usa el contenido y la interfaz pero que ninguna de estas fuentes trae de origen (verificado con
    // los originales de Google Fonts): Android lo dibuja con la fuente del sistema.
    private val sinGlifoDocumentado = mapOf(
        "font/literata.ttf" to setOf('↔', '✗'),
        "font/literata_italic.ttf" to setOf('↔', '✗'),
        "font/atkinson.ttf" to setOf('→', '↔', '₂', '✗'),
        "font/atkinson_italic.ttf" to setOf('→', '↔', '₂', '✗'),
    )

    private fun caracteresUsados(): Set<Char> {
        val usados = mutableSetOf<Char>()
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.forEach { f ->
            f.readText().forEach { if (it.code > 127) usados += it }
        }
        for (idioma in Idioma.entries) {
            tablaChromeCruda().values.forEach { porIdioma -> porIdioma[idioma]?.forEach { if (it.code > 127) usados += it } }
            etiquetasSeccionCrudas().values.forEach { porIdioma -> porIdioma[idioma]?.forEach { if (it.code > 127) usados += it } }
        }
        return usados
    }

    @Test
    fun `lo que el contenido y la interfaz usan y la fuente no cubre es exactamente lo documentado`() {
        val usados = caracteresUsados()
        assertTrue(usados.size > 30)
        for ((archivo, documentado) in sinGlifoDocumentado) {
            val cubiertos = codepoints(res(archivo))
            val faltan = usados.filter { it.code !in cubiertos }.toSet()
            assertEquals("$archivo: caracteres usados sin glifo", documentado, faltan)
        }
    }

    @Test
    fun `los archivos pesan poco, 4 fuentes en menos de 1,5 MB`() {
        val total = familias.values.flatten().sumOf { res(it).length() }
        assertTrue("total $total bytes", total < 1_500_000)
    }

    @Test
    fun `el contenido real se sigue parseando (los caracteres vienen de assets validos)`() {
        val f = File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.first()
        parsearContenido(f.readText())
    }
}
