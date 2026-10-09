package io.github.percati.lenguapp.datos

import android.content.Context
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val RUTA_PLANTILLAS_VOZ = "plantillas/prompt-voz.json"

/**
 * `plantillas` de prompt-voz.json: {"en-B2": "...", "en-C1": "..."}. Las
 * claves que empiezan con "_" (p.ej. "_nota") son documentacion, no
 * plantillas. El texto de las plantillas NO se toca desde Kotlin: Fer las
 * itera en el JSON (proyecto/contenido/plantillas/), por eso no viven en codigo.
 */
fun parsearPlantillasPromptVoz(texto: String): Map<String, String> {
    val raiz: JsonObject = jsonContenido.parseToJsonElement(texto).jsonObject
    val plantillas = raiz["plantillas"]?.jsonObject ?: return emptyMap()
    return plantillas.filterKeys { !it.startsWith("_") }.mapValues { it.value.jsonPrimitive.content }
}

/** Sin plantillas embebidas (archivo ausente) la tarjeta simplemente no aparece. */
fun cargarPlantillasPromptVozDesdeAssets(context: Context): Map<String, String> =
    runCatching {
        context.assets.open(RUTA_PLANTILLAS_VOZ).bufferedReader().use { parsearPlantillasPromptVoz(it.readText()) }
    }.getOrDefault(emptyMap())
