package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

private const val RUTA_PLANILLA = "plantillas/planilla-profesor.json"
private const val RUTA_TOPIC_NOMBRES = "temas/topic-nombres.json"

/**
 * Textos fijos de la planilla del profesor, en el idioma que se aprende (la
 * planilla la lee el profesor de ESE idioma). Viven en
 * proyecto/contenido/plantillas/planilla-profesor.json y no en Kotlin, igual
 * que el prompt de voz: Fer los revisa e itera ahi.
 */
@Serializable
data class PlanillaTextos(
    val titulo: String,
    val nivel: String,
    val objetivo: String,
    val tema: String,
    val enfoque: String,
    val gramatica: String,
    val expresionesObjetivo: String,
    val expresionesReparacion: String,
    val frasesReparacion: List<String>,
    val situaciones: String,
    val situacionCotidiana: String,
    val situacionProfesional: String,
    val giro: String,
    val giroTexto: String,
    val criterios: String,
    val criteriosLista: List<String>,
    val recordatorio: String,
    val semana: String,
)

@Serializable
private data class ArchivoPlanilla(val textos: Map<String, PlanillaTextos>)

/** Textos por codigo de idioma ("es", "en", "de"...). */
fun parsearPlanillaTextos(texto: String): Map<Idioma, PlanillaTextos> {
    val archivo = jsonSinEstricto.decodeFromString<ArchivoPlanilla>(texto)
    return archivo.textos.mapNotNull { (clave, textos) ->
        Idioma.entries.firstOrNull { it.name.equals(clave, ignoreCase = true) }?.let { it to textos }
    }.toMap()
}

// El archivo trae "_nota" en la raiz (documentacion): el parser estricto de
// contenido (jsonContenido) lo rechazaria, y esto no es contenido de ficha.
private val jsonSinEstricto = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

fun cargarPlanillaTextosDesdeAssets(context: Context): Map<Idioma, PlanillaTextos> =
    runCatching {
        context.assets.open(RUTA_PLANILLA).bufferedReader().use { parsearPlanillaTextos(it.readText()) }
    }.getOrDefault(emptyMap())

/** {"T01": "Trabajo y carrera", ...}. Solo en espanol por ahora (banco.json). */
fun parsearTopicNombres(texto: String): Map<String, String> =
    jsonSinEstricto.decodeFromString(texto)

fun cargarTopicNombresDesdeAssets(context: Context): Map<String, String> =
    runCatching {
        context.assets.open(RUTA_TOPIC_NOMBRES).bufferedReader().use { parsearTopicNombres(it.readText()) }
    }.getOrDefault(emptyMap())
