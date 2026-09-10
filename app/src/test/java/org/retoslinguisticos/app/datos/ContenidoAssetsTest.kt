package org.retoslinguisticos.app.datos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.retoslinguisticos.app.modelo.Clase
import org.retoslinguisticos.app.modelo.Ficha
import org.retoslinguisticos.app.modelo.SemanaEspecial
import java.io.File
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * assembleDebug depende de testDebugUnitTest (ver app/build.gradle.kts), asi
 * que este test es la verificacion real de "el JSON de assets/ valida contra
 * el schema": no hay validador JSON Schema aparte, se reusan los mismos
 * modelos que carga la app en el telefono. Si algo en assets/contenido no
 * encaja con ficha.schema.json o semana-especial.schema.json, decodeFromString
 * lanza y el build falla aca, no en el telefono.
 */
class ContenidoAssetsTest {

    private fun carpetaContenido(): File {
        val candidatos = listOf(
            File("src/main/assets/contenido"),
            File("app/src/main/assets/contenido"),
        )
        return candidatos.firstOrNull { it.isDirectory }
            ?: error("No se encontro app/src/main/assets/contenido")
    }

    @Test
    fun `todo el contenido 2026 del piloto carga sin excepciones`() {
        val archivos = carpetaContenido().listFiles { f -> f.extension == "json" }
            ?.sortedBy { it.name } ?: emptyList()
        assertTrue("no se encontraron JSON en assets/contenido", archivos.isNotEmpty())

        val contenidos = archivos.map { archivo ->
            try {
                parsearContenido(archivo.readText())
            } catch (e: SerializationException) {
                error("${archivo.name} no valida contra el schema: ${e.message}")
            }
        }

        val fichas = contenidos.filterIsInstance<Ficha>()
        val especiales = contenidos.filterIsInstance<SemanaEspecial>()

        // Estado del contenido segun CLAUDE.md: 34 fichas 2026 (37-53),
        // EN C1 y DE B2, con 4 semanas de repaso y 2 Survival.
        assertEquals(34, contenidos.size)
        assertEquals(28, fichas.size)
        assertEquals(6, especiales.size)
        assertEquals(4, especiales.count { it.clase == Clase.REVIEW })
        assertEquals(2, especiales.count { it.clase == Clase.SURVIVAL })
    }

    @Test
    fun `las semanas especiales conocidas del piloto cargan como SemanaEspecial`() {
        val ids = listOf("REVIEW-DE-S40", "REVIEW-DE-S48", "REVIEW-EN-S40", "REVIEW-EN-S48",
            "SURVIVAL-DE-S44", "SURVIVAL-EN-S44")
        for (id in ids) {
            val archivo = File(carpetaContenido(), "$id.json")
            assertTrue("falta $id.json", archivo.isFile)
            val contenido = parsearContenido(archivo.readText())
            assertTrue("$id no se cargo como SemanaEspecial", contenido is SemanaEspecial)
        }
    }

    @Test
    fun `una ficha de la semana piloto carga como Ficha con sus campos`() {
        val archivo = File(carpetaContenido(), "DE-G01-B2-1.json")
        val ficha = parsearContenido(archivo.readText()) as Ficha
        assertEquals("DE-G01-B2-1", ficha.id)
        assertEquals("T07", ficha.topicId)
        assertTrue(ficha.vocabulario.isNotEmpty())
        // die Hausordnung declara variante "CH": la app debe poder verla.
        assertTrue(ficha.vocabulario.any { it.variante == "CH" })
    }

    @Test
    fun `falla a proposito si se borra un campo obligatorio de una ficha`() {
        val original = File(carpetaContenido(), "DE-G01-B2-1.json").readText()
        val objeto = Json.parseToJsonElement(original).jsonObject
        val mutilado = JsonObject(objeto.filterKeys { it != "descripcion" })

        try {
            parsearContenido(mutilado.toString())
            fail("se esperaba SerializationException al faltar 'descripcion'")
        } catch (e: SerializationException) {
            // esperado: descripcion es obligatorio en ficha.schema.json
        }
    }
}
