package io.github.percati.lenguapp.modelo

import io.github.percati.lenguapp.datos.jsonContenido
import io.github.percati.lenguapp.datos.parsearContenido
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

/**
 * Los campos de prosa bilingues aceptan string plano (B2/C1/C2, piloto 2026) u
 * objeto {idioma: texto} (A2/B1 traducido): proyecto/schema/ficha.schema.json.
 */
class TextoBilingueTest {

    private fun texto(json: String): TextoBilingue = jsonContenido.decodeFromString(TextoBilingue.serializer(), json)

    // --- deserializacion y resolucion ---

    @Test
    fun `un string plano se lee como plano y se resuelve igual en cualquier idioma`() {
        val t = texto("\"Verbstellung unter Zeitdruck\"")
        assertEquals("Verbstellung unter Zeitdruck", t.resolver(Idioma.ES, Idioma.DE))
        assertEquals("Verbstellung unter Zeitdruck", t.resolver(Idioma.EN, Idioma.DE))
    }

    @Test
    fun `un objeto por idioma resuelve el idioma de la app`() {
        val t = texto("""{"de":"Wortstellung","es":"Orden de palabras","en":"Word order"}""")
        assertEquals("Orden de palabras", t.resolver(Idioma.ES, Idioma.DE))
        assertEquals("Word order", t.resolver(Idioma.EN, Idioma.DE))
        assertEquals("Wortstellung", t.resolver(Idioma.DE, Idioma.DE))
    }

    @Test
    fun `si falta la clave del idioma de la app cae al idioma que se aprende, sin crashear`() {
        val t = texto("""{"de":"Wortstellung","es":"Orden de palabras"}""")
        assertEquals("Wortstellung", t.resolver(Idioma.FR, Idioma.DE))
    }

    @Test
    fun `una clave vacia cuenta como faltante`() {
        val t = texto("""{"de":"Wortstellung","es":""}""")
        assertEquals("Wortstellung", t.resolver(Idioma.ES, Idioma.DE))
    }

    @Test
    fun `si no esta ni el idioma de la app ni el que se aprende usa cualquier otro, y sin nada da vacio`() {
        assertEquals("Word order", texto("""{"en":"Word order"}""").resolver(Idioma.ES, Idioma.DE))
        assertEquals("", texto("{}").resolver(Idioma.ES, Idioma.DE))
    }

    @Test
    fun `un valor que no es string falla en vez de mostrar datos a medias`() {
        for (malo in listOf("42", "null", "[\"a\"]", """{"es":1}""")) {
            try {
                texto(malo)
                fail("se esperaba SerializationException para $malo")
            } catch (e: SerializationException) {
                // esperado
            }
        }
    }

    @Test
    fun `ida y vuelta conserva la forma`() {
        for (json in listOf("\"plano\"", """{"de":"a","es":"b"}""")) {
            val t = texto(json)
            assertEquals(t, texto(jsonContenido.encodeToString(TextoBilingue.serializer(), t)))
        }
    }

    // --- una ficha completa ---

    private fun jsonPiloto(): JsonObject {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        return jsonContenido.parseToJsonElement(File(carpeta, "contenido/DE-G01-B2-2026-1.json").readText()).jsonObject
    }

    private fun bilingue(de: JsonElement) = buildJsonObject {
        put("de", de)
        put("es", JsonPrimitive("ES: ${(de as JsonPrimitive).content}"))
    }

    private fun conCampo(objeto: JsonElement, campo: String, f: (JsonElement) -> JsonElement): JsonObject {
        val m = objeto.jsonObject.toMutableMap()
        m[campo] = f(m.getValue(campo))
        return JsonObject(m)
    }

    private fun lista(e: JsonElement, f: (JsonElement) -> JsonElement) = JsonArray(e.jsonArray.map(f))

    @Test
    fun `una semana especial acepta sus seis campos como string plano u objeto por idioma`() {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val base = jsonContenido.parseToJsonElement(File(carpeta, "contenido/REVIEW-DE-B2-2026-S40.json").readText()).jsonObject

        val plano = parsearContenido(base.toString()) as SemanaEspecial
        assertEquals(plano.titulo.plano, plano.titulo.resolver(Idioma.ES, plano.idioma))

        val m = base.toMutableMap()
        m["titulo"] = bilingue(base.getValue("titulo"))
        m["consigna"] = bilingue(base.getValue("consigna"))
        m["promptCorreccion"] = bilingue(base.getValue("promptCorreccion"))
        m["requisitos"] = lista(base.getValue("requisitos")) { bilingue(it) }
        m["microtareas"] = lista(base.getValue("microtareas")) { bilingue(it) }
        m["autochequeo"] = lista(base.getValue("autochequeo")) { bilingue(it) }
        val e = parsearContenido(JsonObject(m).toString()) as SemanaEspecial

        assertTrue(e.titulo.resolver(Idioma.ES, Idioma.DE).startsWith("ES: "))
        assertTrue(e.consigna.resolver(Idioma.ES, Idioma.DE).startsWith("ES: "))
        assertTrue(e.promptCorreccion.resolver(Idioma.ES, Idioma.DE).startsWith("ES: "))
        assertTrue(e.requisitos!!.all { it.resolver(Idioma.ES, Idioma.DE).startsWith("ES: ") })
        assertTrue(e.microtareas!!.all { it.resolver(Idioma.ES, Idioma.DE).startsWith("ES: ") })
        assertTrue(e.autochequeo.all { it.resolver(Idioma.ES, Idioma.DE).startsWith("ES: ") })
        // Sin clave "fr" cae al aleman.
        assertEquals(plano.titulo.plano, e.titulo.resolver(Idioma.FR, Idioma.DE))
    }

    @Test
    fun `una ficha con campos string plano (piloto 2026) sigue funcionando igual`() {
        val ficha = parsearContenido(jsonPiloto().toString()) as Ficha
        assertEquals("Satzbau: Haupt- und Nebensatz", ficha.titulo.resolver(Idioma.ES, ficha.idioma))
        assertEquals("Verbstellung unter Zeitdruck", ficha.subtitulo.resolver(Idioma.EN, ficha.idioma))
        assertTrue(ficha.errores.all { it.plano != null })
    }

    @Test
    fun `una ficha con los campos como objeto resuelve el idioma correcto en cada campo`() {
        val base = jsonPiloto()
        val m = base.toMutableMap()
        m["titulo"] = bilingue(base.getValue("titulo"))
        m["subtitulo"] = bilingue(base.getValue("subtitulo"))
        m["descripcion"] = bilingue(base.getValue("descripcion"))
        m["notas"] = lista(base.getValue("notas")) { bilingue(it) }
        m["errores"] = lista(base.getValue("errores")) { bilingue(it) }
        m["autochequeo"] = lista(base.getValue("autochequeo")) { bilingue(it) }
        m["promptCorreccion"] = bilingue(base.getValue("promptCorreccion"))
        m["ejemplos"] = lista(base.getValue("ejemplos")) { e -> conCampo(e, "texto") { bilingue(it) } }
        m["microtareas"] = lista(base.getValue("microtareas")) { e -> conCampo(e, "texto") { bilingue(it) } }
        m["redemittel"] = lista(base.getValue("redemittel")) { e -> conCampo(e, "funcion") { bilingue(it) } }
        m["mision"] = conCampo(
            conCampo(base.getValue("mision"), "consigna") { bilingue(it) },
            "requisitos",
        ) { r -> lista(r) { bilingue(it) } }
        m["cuadroReferencia"] = conCampo(
            conCampo(
                conCampo(base.getValue("cuadroReferencia"), "titulo") { bilingue(it) },
                "columnas",
            ) { c -> lista(c) { bilingue(it) } },
            "filas",
        ) { fs -> lista(fs) { fila -> lista(fila) { bilingue(it) } } }

        val f = parsearContenido(JsonObject(m).toString()) as Ficha
        val es = { t: TextoBilingue -> t.resolver(Idioma.ES, Idioma.DE) }
        val fr = { t: TextoBilingue -> t.resolver(Idioma.FR, Idioma.DE) }

        assertEquals("ES: Satzbau: Haupt- und Nebensatz", es(f.titulo))
        assertEquals("ES: Verbstellung unter Zeitdruck", es(f.subtitulo))
        assertTrue(es(f.descripcion).startsWith("ES: "))
        assertTrue(f.notas.all { es(it).startsWith("ES: ") })
        assertTrue(f.errores.all { es(it).startsWith("ES: ") })
        assertTrue(f.autochequeo.all { es(it).startsWith("ES: ") })
        assertTrue(es(f.promptCorreccion).startsWith("ES: "))
        assertTrue(f.ejemplos.all { es(it.texto).startsWith("ES: ") })
        assertTrue(es(f.cuadroReferencia!!.titulo).startsWith("ES: "))
        assertTrue(f.cuadroReferencia!!.columnas.all { es(it).startsWith("ES: ") })
        assertTrue(f.cuadroReferencia!!.filas.flatten().all { es(it).startsWith("ES: ") })
        assertTrue(es(f.mision.consigna).startsWith("ES: "))
        assertTrue(f.mision.requisitos.all { es(it).startsWith("ES: ") })
        assertTrue(f.microtareas.all { es(it.texto).startsWith("ES: ") })
        assertTrue(f.redemittel.all { es(it.funcion).startsWith("ES: ") })

        // Sin clave "fr": cae al aleman (idioma que se aprende) en todos los campos.
        assertEquals("Satzbau: Haupt- und Nebensatz", fr(f.titulo))
        assertTrue(f.redemittel.none { fr(it.funcion).startsWith("ES: ") })
        assertTrue(f.mision.requisitos.none { fr(it).startsWith("ES: ") })
    }
}
