package org.retoslinguisticos.app.datos

import android.content.Context
import kotlinx.serialization.json.Json
import org.retoslinguisticos.app.modelo.ContenidoSemanal

/**
 * Compilar_fichas.py ya valido este JSON contra el schema antes de que
 * llegara a assets/; ficha.schema.json y semana-especial.schema.json son la
 * autoridad. Este parser es deliberadamente estricto (sin ignoreUnknownKeys,
 * sin isLenient): un campo faltante o sobrante debe fallar aca, no mostrar
 * datos a medias en pantalla.
 */
val jsonContenido: Json = Json.Default

/** Sin dependencia de Context: usable desde tests JVM comunes. */
fun parsearContenido(texto: String): ContenidoSemanal =
    jsonContenido.decodeFromString(texto)

private const val CARPETA_CONTENIDO = "contenido"

/** Lee y parsea todos los JSON de `assets/contenido`. */
fun cargarContenidoDesdeAssets(context: Context): List<ContenidoSemanal> {
    val assets = context.assets
    val nombres = assets.list(CARPETA_CONTENIDO)?.filter { it.endsWith(".json") } ?: emptyList()
    return nombres.map { nombre ->
        val texto = assets.open("$CARPETA_CONTENIDO/$nombre").bufferedReader().use { it.readText() }
        parsearContenido(texto)
    }
}
