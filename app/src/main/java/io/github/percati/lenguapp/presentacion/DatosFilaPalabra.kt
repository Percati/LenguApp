package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver

/**
 * Lo que muestra la fila de una palabra o expresion (ui/FilaPalabra.kt), resuelto igual para un
 * [ItemBiblioteca] y para un [ItemGuardado]: original (idioma que se aprende), traduccion al
 * idioma de app (null si no hay) y funcion (null si no hay o esta en blanco). Funciones puras,
 * para que Biblioteca y Guardados no puedan divergir otra vez.
 */
data class DatosFila(val original: String, val traduccion: String?, val funcion: String?)

private fun String?.limpio(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

/** Traduccion en `idiomaBase` (si falta, la espanola, igual que el resto de la app); nunca el propio original. */
private fun traduccionDe(porIdioma: Map<String, String?>, original: String, idiomaBase: Idioma): String? =
    (porIdioma[idiomaBase.name.lowercase()].limpio() ?: porIdioma["es"].limpio())?.takeIf { it != original.trim() }

fun ItemBiblioteca.datosFila(idiomaBase: Idioma): DatosFila {
    val propios: Map<String, String?> = vocabulario?.traducciones ?: redemittel?.traducciones.orEmpty()
    return DatosFila(
        original = texto,
        traduccion = traduccionDe(propios, texto, idiomaBase),
        funcion = funcion?.resolver(idioma, idioma).limpio(),
    )
}

/**
 * El snapshot de Room ya trae la palabra bajo la clave del idioma que se aprende y las
 * traducciones bajo las suyas ([io.github.percati.lenguapp.modelo.aTextoBilingue]): no hace falta migrar nada.
 */
fun ItemGuardado.datosFila(idiomaBase: Idioma): DatosFila {
    val original = texto.resolver(idioma, idioma)
    val sinOriginal = (texto.porIdioma - idioma.name.lowercase())
    return DatosFila(
        original = original,
        traduccion = traduccionDe(sinOriginal, original, idiomaBase),
        funcion = funcion?.resolver(idioma, idioma).limpio(),
    )
}

/** Atajo para tests y para el orden alfabetico: el texto original de un guardado. */
fun TextoBilingue.original(idioma: Idioma): String = resolver(idioma, idioma)
