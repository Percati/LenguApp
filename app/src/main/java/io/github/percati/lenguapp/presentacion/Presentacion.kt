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

/**
 * `contraste` es opcional y solo "es" esta poblado en 2026. A diferencia de
 * las glosas, ACA no cae a español (AJUSTES-FASE-8.md, B.6): el titulo de
 * la seccion anuncia el idioma base ("Contraste con el aleman"), y mostrar
 * la prosa en español bajo ese titulo afirmaria algo falso -- no es un
 * texto que "sigue siendo util en otro idioma" como una glosa, es una
 * observacion especifica de un idioma. `null` (clave ausente, o el idioma
 * que se aprende coincide con el base -- no hay contraste posible contra
 * si mismo) significa que la seccion entera se oculta.
 */
fun Map<String, String>?.contrasteParaMostrar(idiomaAprendido: Idioma, idiomaBase: Idioma): String? {
    if (idiomaAprendido == idiomaBase) return null
    return this?.get(idiomaBase.name.lowercase())?.takeIf { it.isNotBlank() }
}

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
