package io.github.percati.lenguapp.datos

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val RUTA_CATEGORIAS_USO = "temas/categorias-uso.json"

/**
 * Lista cerrada de `categoriasUso` (proyecto/data/categorias-uso.json), en dos ramas:
 * funciones comunicativas y patrones gramaticales. Los nombres estan en espanol y se
 * muestran tal cual por ahora (la traduccion de las etiquetas es una decision
 * pendiente, ver FALTANTES.md).
 */
@Serializable
data class CategoriasUso(
    val funcionComunicativa: List<String> = emptyList(),
    val patronGramatical: List<String> = emptyList(),
)

private val jsonLaxo = Json { ignoreUnknownKeys = true }

fun parsearCategoriasUso(texto: String): CategoriasUso = jsonLaxo.decodeFromString(texto)

fun cargarCategoriasUsoDesdeAssets(context: Context): CategoriasUso =
    runCatching {
        context.assets.open(RUTA_CATEGORIAS_USO).bufferedReader().use { parsearCategoriasUso(it.readText()) }
    }.getOrDefault(CategoriasUso())
