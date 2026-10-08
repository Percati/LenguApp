package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.modelo.resolver

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
 * `contraste` es {idioma_de_app: entrada}, y cada entrada es un
 * [TextoBilingue]: string plano (formato viejo: el texto en el idioma que se
 * aprende) u objeto {idioma: texto} con el original y su traduccion. Se
 * comporta como el resto de los campos bilingues (revertido a proposito por
 * Fer, oct 2026: antes mostraba SIEMPRE el idioma que se aprende, sin
 * importar el switch, citando AJUSTES-FASE-8.md B.6).
 *
 * Que ENTRADA se usa lo decide solo el idioma de app (`idiomaBase`): la
 * clave nombra contra que lengua se contrasta. Que IDIOMA se muestra de
 * esa entrada lo decide `idiomaMostrado` (el switch de traduccion): el
 * original en el idioma que se aprende, o la traduccion en el de app --
 * con fallback al original si Traducciones todavia no la escribio (una
 * entrada string plano, o un objeto con una sola clave, siempre se ve).
 *
 * La seccion NO depende del switch para aparecer: existe mientras haya una
 * entrada para el idioma de app actual. `null` (clave ausente, o el idioma
 * que se aprende coincide con el de app -- no hay contraste posible contra
 * si mismo) significa que se oculta entera.
 */
fun Map<String, TextoBilingue>?.contrasteParaMostrar(
    idiomaAprendido: Idioma,
    idiomaBase: Idioma,
    idiomaMostrado: Idioma,
): String? {
    if (idiomaAprendido == idiomaBase) return null
    val entrada = this?.get(idiomaBase.name.lowercase()) ?: return null
    if (entrada.resolver(idiomaAprendido, idiomaAprendido).isBlank()) return null
    return entrada.resolver(idiomaMostrado, idiomaAprendido)
}

/**
 * "errores" es universal; erroresContrastivos son ADICIONALES segun la
 * lengua base y se suman, nunca lo reemplazan (los errores de un
 * hispanohablante no son los de un anglohablante). Mismo criterio que
 * contrasteParaMostrar: sin fallback a "es" -- mostrar los errores tipicos
 * de otra lengua base bajo una seccion universal seria tan enganoso como
 * mostrar el contraste en el idioma equivocado. Si el idioma que se
 * aprende coincide con el base, no hay interferencia de lengua base que
 * contrastar.
 */
fun erroresParaMostrar(
    errores: List<String>,
    erroresContrastivos: Map<String, List<String>>?,
    idiomaAprendido: Idioma,
    idiomaBase: Idioma,
): List<String> {
    if (idiomaAprendido == idiomaBase) return errores
    val adicionales = erroresContrastivos?.get(idiomaBase.name.lowercase()).orEmpty()
    return errores + adicionales
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
