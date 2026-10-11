package io.github.percati.lenguapp.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.ModoTema

/**
 * Cuatro temas (Ronda G, direccion visual de Fer, 10-oct-2026): PAPEL (por defecto), TERRACOTA, MAR y
 * GRAFITO, cada uno con variante clara y oscura, mas SISTEMA (color dinamico de Material 3, solo en
 * Android 12+ / API 31+; antes de eso SISTEMA no se ofrece y, si estaba guardado, se ve como PAPEL).
 * Los valores son los del documento, sin retocar (verificados AA en presentacion/ContrasteWcag.kt y
 * ContrasteWcagTest): si alguno no cumple contraste, se reporta, no se corrige en silencio.
 */
internal data class PaletaTema(
    val fondo: Color,
    val superficie: Color,
    val texto: Color,
    val acento: Color,
    val secundario: Color,
    /** Blanco en claro; el color de fondo en oscuro. */
    val onAcento: Color,
)

private fun hex(valor: Long) = Color(0xFF000000 or valor)

private val PAPEL_CLARO = PaletaTema(hex(0xF7F6F2), hex(0xFFFFFF), hex(0x1F2328), hex(0x2F5D8A), hex(0x5A6169), hex(0xFFFFFF))
private val PAPEL_OSCURO = PaletaTema(hex(0x14171A), hex(0x1E2226), hex(0xE6E8EA), hex(0x8DB4DE), hex(0xA3ABB2), hex(0x14171A))
private val TERRACOTA_CLARO = PaletaTema(hex(0xFAF5EF), hex(0xFFFFFF), hex(0x2B2320), hex(0xB4542F), hex(0x6B5D55), hex(0xFFFFFF))
private val TERRACOTA_OSCURO = PaletaTema(hex(0x1A1512), hex(0x251E1A), hex(0xF0E7DF), hex(0xE59A78), hex(0xBDADA3), hex(0x1A1512))
private val MAR_CLARO = PaletaTema(hex(0xF2F7F7), hex(0xFFFFFF), hex(0x172326), hex(0x1F7A80), hex(0x4F6568), hex(0xFFFFFF))
private val MAR_OSCURO = PaletaTema(hex(0x0F1A1C), hex(0x172426), hex(0xDFEDEE), hex(0x6FC4C9), hex(0x9FB8BB), hex(0x0F1A1C))
private val GRAFITO_CLARO = PaletaTema(hex(0xF5F5F5), hex(0xFFFFFF), hex(0x212121), hex(0x3A3A3A), hex(0x5C5C5C), hex(0xFFFFFF))
private val GRAFITO_OSCURO = PaletaTema(hex(0x161616), hex(0x212121), hex(0xEAEAEA), hex(0xCFCFCF), hex(0xA8A8A8), hex(0x161616))

/**
 * 60-30-10 (AJUSTES-FASE-7.md): fondo domina, superficie estructura, acento
 * solo para lo accionable -- boton de copiar el prompt, pestañas activas,
 * flechas de navegacion, la estrella de Guardados. `secondaryContainer` (fondo por defecto de un
 * FilterChip seleccionado, y de la tarjeta del prompt) es la unica
 * excepcion deliberada: un 25% de acento sobre la superficie, para que un
 * chip seleccionado o "activo" se distinga de uno que no lo esta -- sigue
 * sin ser decoracion, es estado. Las insignias puramente informativas
 * (núcleo, variante regional) van en `primaryContainer`/`tertiaryContainer`
 * = superficie lisa, sin acento.
 */
internal fun PaletaTema.aColorScheme(oscuro: Boolean): ColorScheme {
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

/** La paleta fija de una familia (SISTEMA no tiene: sus colores los da el sistema; aca cae a PAPEL). */
internal fun paletaPara(familia: FamiliaTema, oscuro: Boolean): PaletaTema = when (familia) {
    FamiliaTema.PAPEL, FamiliaTema.SISTEMA -> if (oscuro) PAPEL_OSCURO else PAPEL_CLARO
    FamiliaTema.TERRACOTA -> if (oscuro) TERRACOTA_OSCURO else TERRACOTA_CLARO
    FamiliaTema.MAR -> if (oscuro) MAR_OSCURO else MAR_CLARO
    FamiliaTema.GRAFITO -> if (oscuro) GRAFITO_OSCURO else GRAFITO_CLARO
}

/** El color dinamico de Material 3 existe desde Android 12 (API 31). */
fun colorDinamicoDisponible(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/** Los temas que se ofrecen en este dispositivo: SISTEMA solo si hay color dinamico. */
fun familiasOfrecidas(): List<FamiliaTema> =
    FamiliaTema.entries.filter { it != FamiliaTema.SISTEMA || colorDinamicoDisponible() }

/** "Segun el sistema" enmienda CLAUDE.md regla dura #2 (AJUSTES-FASE-6.md, bloque E): configuracion local, nada sale del dispositivo. */
@Composable
fun TemaLenguApp(familiaTema: FamiliaTema, modoTema: ModoTema, content: @Composable () -> Unit) {
    val oscuroDelSistema = isSystemInDarkTheme()
    val oscuro = when (modoTema) {
        ModoTema.CLARO -> false
        ModoTema.OSCURO -> true
        ModoTema.SEGUN_SISTEMA -> oscuroDelSistema
    }
    val contexto = LocalContext.current
    val esquema = if (familiaTema == FamiliaTema.SISTEMA && colorDinamicoDisponible()) {
        if (oscuro) dynamicDarkColorScheme(contexto) else dynamicLightColorScheme(contexto)
    } else {
        paletaPara(familiaTema, oscuro).aColorScheme(oscuro)
    }
    MaterialTheme(colorScheme = esquema, typography = tipografiaProporcional(), content = content)
}
