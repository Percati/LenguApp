package io.github.percati.lenguapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.ModoTema

/**
 * Cuatro temas (AJUSTES-FASE-7.md, bloque 1): dos familias -- Academia y
 * Comunidad (azul Oxford y grafito), Editorial y Naturaleza (verde sabio y
 * lino) -- cada una con variante clara y oscura. Los cinco colores de cada
 * fila son los del documento, sin retocar: si alguno no cumple contraste
 * (ver presentacion/ContrasteWcag.kt), se reporta, no se corrige en
 * silencio ac.
 */
private data class PaletaTema(
    val fondo: Color,
    val superficie: Color,
    val texto: Color,
    val acento: Color,
    val secundario: Color,
    val onAcento: Color,
)

private val ACADEMIA_CLARO = PaletaTema(
    fondo = Color(0xFFF8FAFC),
    superficie = Color(0xFFFFFFFF),
    texto = Color(0xFF1E293B),
    acento = Color(0xFF0F4C81),
    secundario = Color(0xFF64748B),
    onAcento = Color(0xFFFFFFFF),
)

private val ACADEMIA_OSCURO = PaletaTema(
    fondo = Color(0xFF0F172A),
    superficie = Color(0xFF1E293B),
    texto = Color(0xFFF1F5F9),
    acento = Color(0xFF60A5FA),
    secundario = Color(0xFF94A3B8),
    onAcento = Color(0xFF0F172A),
)

private val EDITORIAL_CLARO = PaletaTema(
    fondo = Color(0xFFFDFBF7),
    superficie = Color(0xFFF4F1EA),
    texto = Color(0xFF242E26),
    acento = Color(0xFF3A5F43),
    secundario = Color(0xFF708090),
    onAcento = Color(0xFFFFFFFF),
)

private val EDITORIAL_OSCURO = PaletaTema(
    fondo = Color(0xFF141C16),
    superficie = Color(0xFF1E2B21),
    texto = Color(0xFFEAECE8),
    acento = Color(0xFF86A789),
    secundario = Color(0xFFA3B19B),
    onAcento = Color(0xFF141C16),
)

/**
 * 60-30-10 (AJUSTES-FASE-7.md): fondo domina, superficie estructura, acento
 * solo para lo accionable -- boton de copiar el prompt, pestañas activas,
 * flechas de navegacion. `secondaryContainer` (fondo por defecto de un
 * FilterChip seleccionado, y de la tarjeta del prompt) es la unica
 * excepcion deliberada: un 25% de acento sobre la superficie, para que un
 * chip seleccionado o "activo" se distinga de uno que no lo esta -- sigue
 * sin ser decoracion, es estado. Las insignias puramente informativas
 * (núcleo, variante regional) van en `primaryContainer`/`tertiaryContainer`
 * = superficie lisa, sin acento.
 */
private fun PaletaTema.aColorScheme(oscuro: Boolean): ColorScheme {
    val base = if (oscuro) darkColorScheme() else lightColorScheme()
    val acentoSobreSuperficie = lerp(superficie, acento, 0.25f)
    return base.copy(
        primary = acento,
        onPrimary = onAcento,
        primaryContainer = superficie,
        onPrimaryContainer = texto,
        secondary = acento,
        onSecondary = onAcento,
        secondaryContainer = acentoSobreSuperficie,
        onSecondaryContainer = texto,
        tertiary = secundario,
        onTertiary = if (oscuro) fondo else Color.White,
        tertiaryContainer = superficie,
        onTertiaryContainer = texto,
        background = fondo,
        onBackground = texto,
        surface = superficie,
        onSurface = texto,
        surfaceVariant = superficie,
        onSurfaceVariant = secundario,
        outline = secundario,
    )
}

private fun paletaPara(familia: FamiliaTema, oscuro: Boolean): PaletaTema = when (familia) {
    FamiliaTema.ACADEMIA -> if (oscuro) ACADEMIA_OSCURO else ACADEMIA_CLARO
    FamiliaTema.EDITORIAL -> if (oscuro) EDITORIAL_OSCURO else EDITORIAL_CLARO
}

/** "Segun el sistema" enmienda CLAUDE.md regla dura #2 (AJUSTES-FASE-6.md, bloque E): configuracion local, nada sale del dispositivo. */
@Composable
fun TemaLenguApp(familiaTema: FamiliaTema, modoTema: ModoTema, content: @Composable () -> Unit) {
    val oscuroDelSistema = isSystemInDarkTheme()
    val oscuro = when (modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SEGUN_SISTEMA -> oscuroDelSistema
    }
    val esquema = paletaPara(familiaTema, oscuro).aColorScheme(oscuro)
    MaterialTheme(colorScheme = esquema, content = content)
}
