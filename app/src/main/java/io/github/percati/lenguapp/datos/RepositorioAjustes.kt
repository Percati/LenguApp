package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel

private const val PREFS = "ajustes"
private const val CLAVE_IDIOMA_APRENDIDO = "idioma_aprendido"
private const val CLAVE_NIVEL = "nivel"
private const val CLAVE_IDIOMA_BASE = "idioma_base"
private const val CLAVE_IDIOMA_INTERFAZ = "idioma_interfaz"
private const val CLAVE_VARIANTES_DESACTIVADAS = "variantes_desactivadas"

private inline fun <reified T : Enum<T>> nombreAEnum(nombre: String?, porDefecto: T): T =
    nombre?.let { runCatching { java.lang.Enum.valueOf(T::class.java, it) }.getOrNull() } ?: porDefecto

/**
 * Ajustes del usuario (que idioma aprende, en que nivel, en que idioma
 * quiere las glosas y los menus, que variantes regionales no quiere ver).
 * Esto es preferencia, no progreso: persistirlo no rompe el modo revista,
 * la semana que corresponde la sigue mandando la fecha, nunca un puntero.
 */
fun cargarAjustes(context: Context): Ajustes {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    return Ajustes(
        idiomaAprendido = nombreAEnum(prefs.getString(CLAVE_IDIOMA_APRENDIDO, null), Idioma.DE),
        nivel = nombreAEnum(prefs.getString(CLAVE_NIVEL, null), Nivel.B2),
        idiomaBase = nombreAEnum(prefs.getString(CLAVE_IDIOMA_BASE, null), Idioma.ES),
        idiomaInterfaz = nombreAEnum(prefs.getString(CLAVE_IDIOMA_INTERFAZ, null), Idioma.ES),
        variantesDesactivadas = prefs.getStringSet(CLAVE_VARIANTES_DESACTIVADAS, null)?.toSet() ?: emptySet(),
    )
}

fun guardarAjustes(context: Context, ajustes: Ajustes) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        .putString(CLAVE_IDIOMA_APRENDIDO, ajustes.idiomaAprendido.name)
        .putString(CLAVE_NIVEL, ajustes.nivel.name)
        .putString(CLAVE_IDIOMA_BASE, ajustes.idiomaBase.name)
        .putString(CLAVE_IDIOMA_INTERFAZ, ajustes.idiomaInterfaz.name)
        .putStringSet(CLAVE_VARIANTES_DESACTIVADAS, ajustes.variantesDesactivadas)
        .apply()
}
