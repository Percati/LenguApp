package io.github.percati.lenguapp.ui

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Marcado « » (Ronda D, tarea 1a): sobre TODOS los textos de assets/contenido/ -- no
 * fichas de prueba --, ninguno muestra asteriscos literales ni « » dobles una vez pasado
 * por textoConMarcado, con y sin `angulares` (switch de traduccion en ON y en OFF).
 * `«*x*»` escrito a mano convive con el `*x*` dinamico sin duplicarse.
 */
class MarcadoAssetsRealesTest {

    private fun carpetaContenido(): File =
        listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }

    private fun textos(e: JsonElement): Sequence<String> = when (e) {
        is JsonPrimitive -> if (e.isString) sequenceOf(e.content) else emptySequence()
        is JsonObject -> e.values.asSequence().flatMap { textos(it) }
        is JsonArray -> e.asSequence().flatMap { textos(it) }
    }

    @Test
    fun `ningun texto real muestra asteriscos literales ni comillas angulares dobles`() {
        val archivos = carpetaContenido().listFiles { f -> f.extension == "json" }!!
        assertTrue(archivos.size > 600)
        val problemas = mutableListOf<String>()
        for (archivo in archivos) {
            for (texto in textos(Json.parseToJsonElement(archivo.readText()))) {
                if (texto.none { it == '*' || it == '«' || it == '»' }) continue
                for (angulares in listOf(false, true)) {
                    val r = textoConMarcado(texto, angulares).text
                    val mal = '*' in r || "««" in r || "»»" in r
                    if (mal && problemas.size < 20) problemas += "${archivo.name} (angulares=$angulares): ${texto.take(120)}"
                }
            }
        }
        assertTrue("textos con marcado roto:\n" + problemas.joinToString("\n"), problemas.isEmpty())
    }
}
