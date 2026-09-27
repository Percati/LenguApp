package io.github.percati.lenguapp.modelo

import kotlinx.serialization.Serializable

/**
 * Guardados: marcadores de vocabulario/Redemittel que el usuario elige
 * conservar para repasar, no progreso pedagogico (que aprendio, que le
 * falta). Es la unica excepcion documentada a CLAUDE.md regla dura #4 -- ver
 * ese archivo para la justificacion completa. Persiste en Room
 * (datos/GuardadosDb.kt); este archivo es el modelo de dominio, sin nada de
 * Android ni de Room.
 */
@Serializable
enum class TipoGuardado { VOCABULARIO, EXPRESION }

/**
 * `texto` y `funcion` son un snapshot de [TextoBilingue] tomado al guardar,
 * no una referencia viva al [VocabularioItem]/[RedemittelItem] de origen: si
 * una edicion futura deja de embeber esa ficha, el guardado sigue
 * mostrandose igual (ver [skillIdOrigen]).
 */
@Serializable
data class ItemGuardado(
    val id: Long = 0,
    val idioma: Idioma,
    val nivel: Nivel,
    val tipo: TipoGuardado,
    val texto: TextoBilingue,
    // Solo para tipo == EXPRESION: la funcion de la expresion (que hace, no
    // que significa). El vocabulario no tiene equivalente.
    val funcion: TextoBilingue? = null,
    val skillIdOrigen: String,
)

/**
 * El item de vocabulario, tal cual esta en la ficha, como [TextoBilingue]:
 * la palabra en el idioma que se aprende es la clave propia de ese idioma, y
 * [VocabularioItem.traducciones] aporta el resto -- mismo mecanismo que ya
 * resuelve contraste/erroresContrastivos, reempaquetado para reusar
 * [resolver] en la pantalla de Guardados en vez de traduccionParaMostrar().
 * Una traduccion vacia ("todavia sin traducir", ver VocabularioItem) se
 * omite: [TextoBilingue.resolver] ya trata una clave vacia como ausente.
 */
fun VocabularioItem.aTextoBilingue(idioma: Idioma): TextoBilingue =
    TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to item) + traducciones.filterValues { it.isNotBlank() })

/**
 * Igual que [VocabularioItem.aTextoBilingue], para la expresion misma de un
 * Redemittel. `null` en [RedemittelItem.traducciones] es "sin equivalencia
 * directa" (ver RedemittelItem): al guardar se omite en vez de guardarse
 * como ausente-a-completar, porque acá no hay pantalla que distinga las dos
 * situaciones (esa distincion, [io.github.percati.lenguapp.presentacion.TraduccionRedemittel],
 * es propia de la ficha en vivo) -- mostrar el idioma que se aprende de
 * fallback es razonable en los dos casos.
 */
fun RedemittelItem.aTextoBilingue(idioma: Idioma): TextoBilingue =
    TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to expresion) + traducciones.filterValues { !it.isNullOrBlank() }.mapValues { it.value!! })
