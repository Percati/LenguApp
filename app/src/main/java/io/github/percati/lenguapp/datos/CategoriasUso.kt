package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val RUTA_CATEGORIAS_USO = "temas/categorias-uso.json"

/**
 * Lista cerrada de `categoriasUso` (proyecto/data/categorias-uso.json), en dos ramas:
 * funciones comunicativas y patrones gramaticales. El valor guardado en el contenido
 * es el nombre en espanol (clave canonica); lo que se MUESTRA sale de `nombres`, en el
 * idioma de app.
 */
@Serializable
data class CategoriasUso(
    val funcionComunicativa: List<String> = emptyList(),
    val patronGramatical: List<String> = emptyList(),
    // categoria (en espanol, la clave canonica que se guarda en `categoriasUso`) -> nombre por idioma.
    val nombres: Map<String, Map<String, String>> = emptyMap(),
) {
    private val i18n by lazy { NombresI18n(nombres) }

    /** El nombre de la categoria en el idioma de app (fallback: espanol, la propia clave). */
    fun nombre(categoria: String, idioma: Idioma): String = i18n.nombre(categoria, idioma)

    fun faltantes(): List<Pair<String, Idioma>> = i18n.faltantes()
}

private val jsonLaxo = Json { ignoreUnknownKeys = true }

fun parsearCategoriasUso(texto: String): CategoriasUso = jsonLaxo.decodeFromString(texto)

fun cargarCategoriasUsoDesdeAssets(context: Context): CategoriasUso =
    runCatching {
        context.assets.open(RUTA_CATEGORIAS_USO).bufferedReader().use { parsearCategoriasUso(it.readText()) }
    }.getOrDefault(CategoriasUso())
