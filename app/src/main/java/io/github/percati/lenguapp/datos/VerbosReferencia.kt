package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Un verbo de REFERENCIA (Ronda U): un verbo de consulta de A1/A2 con traduccion a los otros cinco idiomas, clase y regimen, que NO anota
 * ningun item de las fichas. Viene de `assets/verbos/referencia-{idioma}.json`, que la tarea de Gradle `extraerVerbosReferenciaAssets`
 * extrae de `proyecto/contenido/verbos/{idioma}.json` en cada build (solo estos campos: `nivelFuente` y `nota` son de trabajo y no viajan).
 *
 * No es un item de ninguna ficha, asi que NO es marcable (la clave de Guardados es el texto del item) y no tiene semana ni nivel.
 */
@Serializable
data class VerboReferencia(
    /** El infinitivo tal como se muestra («aufstehen», «to achieve»). */
    val item: String,
    val base: String = item,
    val claseVerbo: List<String> = emptyList(),
    /** Traduccion por idioma de app (`es`, `en`, `de`...). El idioma que se aprende no trae la suya. */
    val traducciones: Map<String, String> = emptyMap(),
    /** La raiz de un separable («schliessen» para «abschliessen»): viaja para que la busqueda traiga la familia. */
    val raiz: String? = null,
    val reccion: String? = null,
)

@Serializable
private data class ArchivoVerbosReferencia(val idioma: String = "", val verbos: List<VerboReferencia> = emptyList())

private val jsonLaxo = Json { ignoreUnknownKeys = true }

/** Parseo puro del archivo extraido (sin `Context`, para los tests de JVM). */
fun parsearVerbosReferencia(texto: String): List<VerboReferencia> =
    jsonLaxo.decodeFromString<ArchivoVerbosReferencia>(texto).verbos

/**
 * Los verbos de referencia de `idioma`, o vacio si no hay archivo para el (solo hay de aleman e ingles) o no se puede leer. Se llama solo
 * al entrar a la vista Verbos, no al arrancar la app.
 */
fun cargarVerbosReferenciaDesdeAssets(context: Context, idioma: Idioma): List<VerboReferencia> =
    runCatching {
        context.assets.open("verbos/referencia-${idioma.name.lowercase()}.json").bufferedReader().use { parsearVerbosReferencia(it.readText()) }
    }.getOrDefault(emptyList())
