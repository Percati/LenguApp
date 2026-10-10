package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * erroresContrastivos bilingue en A2/B1 (Ronda E, tarea 5), sobre TODAS las fichas reales:
 * con el switch activo (idioma de app = la clave del mapa) ningun item muestra el original,
 * y apagado muestra el original; en B2+ siguen siendo strings planos.
 */
class ErroresContrastivosRealesTest {

    private val fichas: List<Ficha> by lazy {
        val carpeta = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
        carpeta.listFiles { f -> f.extension == "json" }!!.map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
    }

    private fun idiomaDe(clave: String) = Idioma.entries.first { it.name.equals(clave, ignoreCase = true) }

    @Test
    fun `en A2 y B1 cada item trae original y traduccion y con el switch activo sale la traduccion`() {
        val bilingues = fichas.filter { it.nivel == Nivel.A2 || it.nivel == Nivel.B1 }.filter { !it.erroresContrastivos.isNullOrEmpty() }
        assertTrue(bilingues.size > 100)
        var items = 0
        for (f in bilingues) {
            for ((clave, lista) in f.erroresContrastivos!!) {
                val idiomaApp = idiomaDe(clave)
                for (item in lista) {
                    items++
                    val original = item.porIdioma[f.idioma.name.lowercase()]
                    val traduccion = item.porIdioma[clave]
                    assertTrue("${f.id}/$clave: falta original o traduccion", !original.isNullOrBlank() && !traduccion.isNullOrBlank())
                    val activo = erroresParaMostrar(emptyList(), f.erroresContrastivos, f.idioma, idiomaApp, { it.resolver(idiomaApp, f.idioma) })
                    val apagado = erroresParaMostrar(emptyList(), f.erroresContrastivos, f.idioma, idiomaApp, { it.resolver(f.idioma, f.idioma) })
                    assertTrue("${f.id}/$clave: con el switch activo sale el original", traduccion in activo && original !in activo)
                    assertTrue("${f.id}/$clave: con el switch apagado sale la traduccion", original in apagado && traduccion !in apagado)
                    assertNotEquals(original, traduccion)
                }
            }
        }
        assertTrue("items revisados: $items", items > 400)
    }

    @Test
    fun `en B2 y superiores siguen siendo strings planos`() {
        val altas = fichas.filter { it.nivel != Nivel.A2 && it.nivel != Nivel.B1 }.filter { !it.erroresContrastivos.isNullOrEmpty() }
        assertTrue(altas.isNotEmpty())
        for (f in altas) for ((_, lista) in f.erroresContrastivos!!) {
            assertTrue("${f.id}: B2+ debe ser plano", lista.all { it.plano != null })
        }
    }

    @Test
    fun `una ficha DE B1 real, con la app en espanol, muestra la traduccion espanola`() {
        val f = fichas.first { it.id == "DE-F13-B1-2027-1" }
        val activo = erroresParaMostrar(emptyList(), f.erroresContrastivos, Idioma.DE, Idioma.ES, { it.resolver(Idioma.ES, Idioma.DE) })
        assertEquals(1, activo.size)
        assertTrue(activo.single().startsWith("Trasladar"))
    }
}
