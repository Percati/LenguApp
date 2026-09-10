package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.VocabularioItem

private const val SIN_TRADUCCION = "—"

/**
 * Idioma base, y si no hay nada ahi "es" -- el unico poblado en 2026. Un
 * valor vacio a proposito cuenta como "nada ahi", no como un error: cae
 * igual que si la clave faltara.
 */
private fun Map<String, String>.valorParaIdiomaBase(idiomaBase: Idioma): String? =
    this[idiomaBase.name.lowercase()]?.takeIf { it.isNotBlank() }
        ?: this["es"]?.takeIf { it.isNotBlank() }

/**
 * Traduccion de un item de vocabulario para mostrar en pantalla. Nunca la
 * clave cruda ni el texto "null": si no hay nada util en ningun idioma, un
 * guion.
 */
fun VocabularioItem.traduccionParaMostrar(idiomaBase: Idioma): String =
    traducciones.valorParaIdiomaBase(idiomaBase) ?: SIN_TRADUCCION

/** `contraste` es opcional y solo "es" esta poblado en 2026; `null` significa que la seccion no se muestra. */
fun Map<String, String>?.contrasteParaMostrar(idiomaBase: Idioma): String? =
    this?.valorParaIdiomaBase(idiomaBase)

/**
 * En Redemittel un valor `null` explicito no es "todavia no traducido": es
 * una afirmacion deliberada de que la expresion no tiene equivalencia directa
 * y se aprende por situacion (CLAUDE.md). Un `String?` no distingue esos dos
 * casos -- por eso este tipo en vez de devolver directamente la traduccion.
 */
sealed interface TraduccionRedemittel {
    data class Disponible(val texto: String) : TraduccionRedemittel
    data object SinEquivalenciaDirecta : TraduccionRedemittel
    data object SinTraducirTodavia : TraduccionRedemittel
}

fun RedemittelItem.traduccionParaMostrar(idiomaBase: Idioma): TraduccionRedemittel {
    val clave = idiomaBase.name.lowercase()
    return resolverClave(clave) ?: resolverClave("es") ?: TraduccionRedemittel.SinTraducirTodavia
}

/** `null` de retorno significa "seguir probando el siguiente idioma", no "sin equivalencia". */
private fun RedemittelItem.resolverClave(clave: String): TraduccionRedemittel? {
    if (clave !in traducciones) return null
    return when (val valor = traducciones[clave]) {
        null -> TraduccionRedemittel.SinEquivalenciaDirecta
        else -> valor.takeIf { it.isNotBlank() }?.let { TraduccionRedemittel.Disponible(it) }
    }
}
