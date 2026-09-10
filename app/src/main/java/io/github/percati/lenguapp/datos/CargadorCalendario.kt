package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.EntradaCalendario
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import java.io.FileNotFoundException

private const val CARPETA_CALENDARIO = "calendario"

/** Sin dependencia de Context: usable desde tests JVM comunes. */
fun parsearCalendario(texto: String): List<EntradaCalendario> =
    jsonContenido.decodeFromString(texto)

/**
 * `null` si no hay calendario embebido para (anioIso, idioma, nivel): la
 * matriz idioma x nivel x anio esta casi siempre vacia, por diseno (ver
 * CLAUDE.md), y eso no es un error.
 */
fun cargarCalendarioDesdeAssets(context: Context, anioIso: Int, idioma: Idioma, nivel: Nivel): List<EntradaCalendario>? {
    val nombre = "calendario_${anioIso}_${idioma.name.lowercase()}_${nivel.name}.json"
    val texto = try {
        context.assets.open("$CARPETA_CALENDARIO/$nombre").bufferedReader().use { it.readText() }
    } catch (e: FileNotFoundException) {
        return null
    }
    return parsearCalendario(texto)
}
