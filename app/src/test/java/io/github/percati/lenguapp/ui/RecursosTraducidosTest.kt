package io.github.percati.lenguapp.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Strings de res/ (Ronda F, tarea 2 / REGLAS-PREVENCION P7): ningun string visible queda solo
 * en un idioma. values/ (ingles) es el respaldo y cada idioma de app trae el suyo; solo
 * `app_name` (nombre propio) vive unicamente en values/.
 */
class RecursosTraducidosTest {

    private fun res(): File = listOf(File("src/main/res"), File("app/src/main/res")).first { it.isDirectory }

    private fun strings(carpeta: String): Map<String, String> {
        val archivo = File(res(), "$carpeta/strings.xml")
        if (!archivo.isFile) return emptyMap()
        return Regex("""<string name="([^"]+)"[^>]*>(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)
            .findAll(archivo.readText()).associate { it.groupValues[1] to it.groupValues[2] }
    }

    private val idiomas = listOf("es", "de", "fr", "it", "pt")
    private val propios = setOf("app_name")

    @Test
    fun `cada string visible de values existe en los 5 idiomas restantes`() {
        val base = strings("values")
        assertTrue(base.containsKey("widget_descripcion"))
        for (idioma in idiomas) {
            val traducido = strings("values-$idioma")
            for (nombre in base.keys - propios) {
                assertTrue("values-$idioma no tiene $nombre", traducido[nombre]?.isNotBlank() == true)
            }
        }
    }

    @Test
    fun `values es el respaldo en ingles y las traducciones difieren entre si`() {
        val descripciones = (listOf("values") + idiomas.map { "values-$it" }).map { strings(it).getValue("widget_descripcion") }
        assertEquals("deben ser 6 textos distintos", 6, descripciones.toSet().size)
        assertTrue(strings("values").getValue("widget_descripcion").contains("Shows"))
    }

    @Test
    fun `las traducciones no repiten el espanol del respaldo ni dejan apostrofes sin escapar`() {
        for (carpeta in listOf("values") + idiomas.map { "values-$it" }) {
            for ((nombre, valor) in strings(carpeta)) {
                assertFalse("$carpeta/$nombre: apostrofe sin escapar", Regex("""(?<!\\)'""").containsMatchIn(valor))
            }
        }
        assertFalse(strings("values").getValue("widget_descripcion").contains("Muestra"))
    }

    @Test
    fun `ningun otro recurso de texto en res queda solo en un idioma`() {
        // El manifest y el widget solo apuntan a @string: sin literales de texto.
        val xml = File(res(), "xml").listFiles { f -> f.extension == "xml" }!!
        for (f in xml) {
            val texto = f.readText()
            Regex("""android:(label|description)="([^"]*)"""").findAll(texto).forEach {
                assertTrue("${f.name}: ${it.value} no apunta a un @string", it.groupValues[2].startsWith("@string/"))
            }
        }
        val manifest = File(res(), "../AndroidManifest.xml").readText()
        Regex("""android:(label|description)="([^"]*)"""").findAll(manifest).forEach {
            assertTrue("manifest: ${it.value} no apunta a un @string", it.groupValues[2].startsWith("@string/"))
        }
    }
}
