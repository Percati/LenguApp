package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import java.io.InputStream

/**
 * Copia de seguridad de Guardados (Ronda F2, tarea 6): `lenguapp-guardados.json`, version 1.
 * Es un archivo que el USUARIO elige en el momento (selector del sistema), no una lectura del
 * dispositivo (CLAUDE.md, regla dura 2 enmendada). De lo que se lee solo se usan los campos de
 * [ItemRespaldo]; nada se ejecuta ni se interpreta mas alla de eso.
 */
const val VERSION_RESPALDO = 1
const val NOMBRE_ARCHIVO_RESPALDO = "lenguapp-guardados.json"

/** Limites razonables: una lista de referencia hecha a mano nunca se acerca a esto. */
const val LIMITE_BYTES_RESPALDO = 5_000_000
const val LIMITE_ITEMS_RESPALDO = 50_000
private const val LIMITE_TEXTO = 2_000
private const val LIMITE_SKILL_ID = 80

@Serializable
data class ItemRespaldo(
    val idioma: Idioma,
    val nivel: Nivel,
    val tipo: TipoGuardado,
    val texto: TextoBilingue,
    val funcion: TextoBilingue? = null,
    val skillIdOrigen: String,
)

@Serializable
data class Respaldo(val version: Int, val items: List<ItemRespaldo>)

/**
 * La palabra o expresion original, en el idioma que se aprende: la identidad de un guardado junto con idioma y tipo.
 * ESTRICTO: tiene que estar bajo la clave del idioma que se aprende (o ser un string plano); no cae a otra traduccion.
 */
fun ItemRespaldo.original(): String = texto.plano ?: texto.porIdioma[idioma.name.lowercase()].orEmpty()

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true; prettyPrint = true }

fun serializarRespaldo(items: List<ItemGuardado>): String = json.encodeToString(
    Respaldo.serializer(),
    Respaldo(
        version = VERSION_RESPALDO,
        // del mas viejo al mas nuevo: al importar se conserva el orden de guardado
        items = items.sortedBy { it.id }.map {
            ItemRespaldo(it.idioma, it.nivel, it.tipo, it.texto, it.funcion, it.skillIdOrigen)
        },
    ),
)

/** Por que se rechaza un archivo (cada uno tiene su mensaje en los 6 idiomas). */
enum class ErrorRespaldo { DEMASIADO_GRANDE, NO_VALIDO, VERSION_DESCONOCIDA }

sealed interface LecturaRespaldo {
    data class Correcta(val items: List<ItemRespaldo>) : LecturaRespaldo
    data class Rechazada(val error: ErrorRespaldo) : LecturaRespaldo
}

/** Lee como mucho [LIMITE_BYTES_RESPALDO] bytes: devuelve null si el archivo es mas grande. */
fun leerLimitado(entrada: InputStream, limite: Int = LIMITE_BYTES_RESPALDO): ByteArray? {
    val buffer = java.io.ByteArrayOutputStream()
    val chunk = ByteArray(8 * 1024)
    while (true) {
        val n = entrada.read(chunk)
        if (n < 0) break
        buffer.write(chunk, 0, n)
        if (buffer.size() > limite) return null
    }
    return buffer.toByteArray()
}

fun parsearRespaldo(bytes: ByteArray?): LecturaRespaldo {
    if (bytes == null || bytes.size > LIMITE_BYTES_RESPALDO) return LecturaRespaldo.Rechazada(ErrorRespaldo.DEMASIADO_GRANDE)
    val raiz = try {
        json.parseToJsonElement(bytes.toString(Charsets.UTF_8))
    } catch (e: SerializationException) {
        return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    } catch (e: IllegalArgumentException) {
        return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    }
    val objeto = raiz as? JsonObject ?: return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    val version = (objeto["version"] as? JsonPrimitive)?.intOrNull ?: return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    if (version != VERSION_RESPALDO) return LecturaRespaldo.Rechazada(ErrorRespaldo.VERSION_DESCONOCIDA)

    val respaldo = try {
        json.decodeFromJsonElement(Respaldo.serializer(), raiz)
    } catch (e: SerializationException) {
        return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    } catch (e: IllegalArgumentException) {
        return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    }
    if (respaldo.items.size > LIMITE_ITEMS_RESPALDO) return LecturaRespaldo.Rechazada(ErrorRespaldo.DEMASIADO_GRANDE)
    if (!respaldo.items.all(::itemValido)) return LecturaRespaldo.Rechazada(ErrorRespaldo.NO_VALIDO)
    return LecturaRespaldo.Correcta(respaldo.items)
}

private fun cortos(t: TextoBilingue?): Boolean =
    t == null || ((t.plano?.length ?: 0) <= LIMITE_TEXTO && t.porIdioma.all { (k, v) -> k.length <= 5 && v.length <= LIMITE_TEXTO })

/** Estructura minima: hay un original no vacio y todo es de tamano razonable. */
private fun itemValido(i: ItemRespaldo): Boolean =
    i.original().isNotBlank() && i.original().length <= LIMITE_TEXTO &&
        cortos(i.texto) && cortos(i.funcion) &&
        i.skillIdOrigen.isNotBlank() && i.skillIdOrigen.length <= LIMITE_SKILL_ID
