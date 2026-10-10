package io.github.percati.lenguapp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

/**
 * Espaciado vertical PROPORCIONAL al tamano de letra (Ronda F2, tarea 3).
 *
 * Causa real de que "no escalara": los espacios entre viñetas, entre items y entre secciones eran
 * `Arrangement.spacedBy(8.dp / 22.dp / 32.dp)` -- dp fijos, que NO siguen la escala de fuente del
 * sistema --, asi que con la letra al 130 % o 160 % el texto crecia y el espacio no. (El
 * interlineado en si ya estaba en sp, porque el tema usaba la tipografia por defecto de
 * Material 3, cuyos `lineHeight` son sp; solo que esos valores no estaban fijados a una
 * proporcion explicita: ver [tipografiaProporcional].)
 *
 * Ahora cada espacio es un multiplo del tamano de letra EN sp, convertido a dp con la densidad
 * actual (que incluye `fontScale`): crece y se achica con la letra.
 *  - entre viñetas / renglones de lista: 0,35 x cuerpo
 *  - dentro de un item (palabra + traduccion): 0,15 x cuerpo
 *  - entre items de una lista (vocabulario, filas de Biblioteca/Guardados): 0,6 x cuerpo
 *  - entre secciones: 1 x cuerpo grande
 */
@Immutable
class Espaciado(
    val entreVinietas: Dp,
    val dentroDeItem: Dp,
    val entreItems: Dp,
    val entreSecciones: Dp,
)

const val FACTOR_ENTRE_VINIETAS = 0.35f
const val FACTOR_DENTRO_DE_ITEM = 0.15f
const val FACTOR_ENTRE_ITEMS = 0.6f
const val FACTOR_ENTRE_SECCIONES = 1.0f

/** Pura, para poder probarla con distintas densidades/escalas: tamanos de letra en sp -> espacios en dp. */
fun calcularEspaciado(cuerpoSp: Float, cuerpoGrandeSp: Float, density: Density): Espaciado = with(density) {
    Espaciado(
        entreVinietas = (cuerpoSp * FACTOR_ENTRE_VINIETAS).sp.toDp(),
        dentroDeItem = (cuerpoSp * FACTOR_DENTRO_DE_ITEM).sp.toDp(),
        entreItems = (cuerpoSp * FACTOR_ENTRE_ITEMS).sp.toDp(),
        entreSecciones = (cuerpoGrandeSp * FACTOR_ENTRE_SECCIONES).sp.toDp(),
    )
}

@Composable
fun espaciadoActual(): Espaciado {
    val t = MaterialTheme.typography
    return calcularEspaciado(t.bodyMedium.fontSize.value, t.bodyLarge.fontSize.value, LocalDensity.current)
}

/** Interlineado en sp = tamano x factor: cuerpo 1,45; etiquetas 1,35; titulos 1,3; headline 1,25; display 1,2. */
private fun TextStyle.conInterlineado(factor: Float): TextStyle =
    if (fontSize.isSp) copy(lineHeight = (fontSize.value * factor).sp) else this

fun tipografiaProporcional(base: Typography = Typography()): Typography = base.copy(
    displayLarge = base.displayLarge.conInterlineado(1.2f),
    displayMedium = base.displayMedium.conInterlineado(1.2f),
    displaySmall = base.displaySmall.conInterlineado(1.2f),
    headlineLarge = base.headlineLarge.conInterlineado(1.25f),
    headlineMedium = base.headlineMedium.conInterlineado(1.25f),
    headlineSmall = base.headlineSmall.conInterlineado(1.25f),
    titleLarge = base.titleLarge.conInterlineado(1.3f),
    titleMedium = base.titleMedium.conInterlineado(1.3f),
    titleSmall = base.titleSmall.conInterlineado(1.3f),
    bodyLarge = base.bodyLarge.conInterlineado(1.45f),
    bodyMedium = base.bodyMedium.conInterlineado(1.45f),
    bodySmall = base.bodySmall.conInterlineado(1.45f),
    labelLarge = base.labelLarge.conInterlineado(1.35f),
    labelMedium = base.labelMedium.conInterlineado(1.35f),
    labelSmall = base.labelSmall.conInterlineado(1.35f),
)
