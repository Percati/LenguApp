package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import io.github.percati.lenguapp.modelo.Nivel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

private const val PREFS = "ajustes"
private const val CLAVE_IDIOMAS_APRENDIDOS = "idiomas_aprendidos_json"
private const val CLAVE_IDIOMA_BASE = "idioma_base"
private const val CLAVE_IDIOMA_INTERFAZ = "idioma_interfaz"
private const val CLAVE_IDIOMA_SEGUN_SISTEMA = "idioma_segun_sistema"
private const val CLAVE_FAMILIA_TEMA = "familia_tema"
private const val CLAVE_MODO_TEMA = "modo_tema"

// Esquema viejo (Fases 3-5): un idioma y un nivel singulares. Se lee una
// sola vez para migrar; nunca se vuelve a escribir en este formato.
private const val CLAVE_VIEJA_IDIOMA_APRENDIDO = "idioma_aprendido"
private const val CLAVE_VIEJA_NIVEL = "nivel"

private inline fun <reified T : Enum<T>> nombreAEnum(nombre: String?, porDefecto: T): T =
    nombre?.let { runCatching { java.lang.Enum.valueOf(T::class.java, it) }.getOrNull() } ?: porDefecto

/**
 * Ajustes del usuario. Es preferencia, no progreso: persistirlo no rompe el
 * modo revista, la semana que corresponde la sigue mandando la fecha, nunca
 * un puntero. El tema (familia + modo) es preferencia igual que el idioma
 * -- AJUSTES-FASE-7.md, bloque 1.
 *
 * Migracion obligatoria (AJUSTES-FASE-6.md, bloque B): si hay un
 * `idioma_aprendido`/`nivel` del esquema viejo y todavia no se escribio el
 * nuevo, se convierte a un mapa de un elemento y se reescribe de una. Sin
 * esto la app arranca sin ningun idioma seleccionado y parece rota.
 */
fun cargarAjustes(context: Context): Ajustes {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val idiomasJson = prefs.getString(CLAVE_IDIOMAS_APRENDIDOS, null)
    val idiomasAprendidos = idiomasJson?.let {
        runCatching { jsonContenido.decodeFromString<Map<Idioma, Nivel>>(it) }.getOrNull()
    }

    if (idiomasAprendidos != null) {
        return Ajustes(
            idiomasAprendidos = idiomasAprendidos,
            idiomaBase = nombreAEnum(prefs.getString(CLAVE_IDIOMA_BASE, null), Idioma.ES),
            idiomaInterfaz = nombreAEnum(prefs.getString(CLAVE_IDIOMA_INTERFAZ, null), Idioma.ES),
            idiomaSegunSistema = prefs.getBoolean(CLAVE_IDIOMA_SEGUN_SISTEMA, false),
            familiaTema = nombreAEnum(prefs.getString(CLAVE_FAMILIA_TEMA, null), FamiliaTema.ACADEMIA),
            modoTema = nombreAEnum(prefs.getString(CLAVE_MODO_TEMA, null), ModoTema.SEGUN_SISTEMA),
        )
    }

    // Sin formato nuevo todavia: migrar desde el esquema viejo si existe, o
    // arrancar con el default. En los dos casos se reescribe de una para que
    // la proxima carga entre por la rama de arriba.
    val idiomaViejo = prefs.getString(CLAVE_VIEJA_IDIOMA_APRENDIDO, null)
        ?.let { runCatching { Idioma.valueOf(it) }.getOrNull() }
    val nivelViejo = nombreAEnum(prefs.getString(CLAVE_VIEJA_NIVEL, null), Nivel.B2)
    val migrado = Ajustes(
        idiomasAprendidos = if (idiomaViejo != null) mapOf(idiomaViejo to nivelViejo) else Ajustes().idiomasAprendidos,
        idiomaBase = nombreAEnum(prefs.getString(CLAVE_IDIOMA_BASE, null), Idioma.ES),
        idiomaInterfaz = nombreAEnum(prefs.getString(CLAVE_IDIOMA_INTERFAZ, null), Idioma.ES),
        idiomaSegunSistema = prefs.getBoolean(CLAVE_IDIOMA_SEGUN_SISTEMA, false),
        familiaTema = nombreAEnum(prefs.getString(CLAVE_FAMILIA_TEMA, null), FamiliaTema.ACADEMIA),
        modoTema = nombreAEnum(prefs.getString(CLAVE_MODO_TEMA, null), ModoTema.SEGUN_SISTEMA),
    )
    guardarAjustes(context, migrado)
    return migrado
}

fun guardarAjustes(context: Context, ajustes: Ajustes) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        .putString(CLAVE_IDIOMAS_APRENDIDOS, jsonContenido.encodeToString(ajustes.idiomasAprendidos))
        .putString(CLAVE_IDIOMA_BASE, ajustes.idiomaBase.name)
        .putString(CLAVE_IDIOMA_INTERFAZ, ajustes.idiomaInterfaz.name)
        .putBoolean(CLAVE_IDIOMA_SEGUN_SISTEMA, ajustes.idiomaSegunSistema)
        .putString(CLAVE_FAMILIA_TEMA, ajustes.familiaTema.name)
        .putString(CLAVE_MODO_TEMA, ajustes.modoTema.name)
        .remove(CLAVE_VIEJA_IDIOMA_APRENDIDO)
        .remove(CLAVE_VIEJA_NIVEL)
        .apply()
}
