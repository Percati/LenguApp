package io.github.percati.lenguapp.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Una opcion de un grupo de chips: `clave` (identidad estable, lo que se compara) y `etiqueta` (lo que
 * se ve, ya en el idioma de app y con su contador si lo lleva). La lista la calcula quien llama desde
 * los datos del idioma (REGLAS-PREVENCION P3: ninguna lista de filtros es fija).
 */
data class OpcionFiltro(val clave: String, val etiqueta: String)

/** Como se acomodan los chips cuando son muchos. */
enum class DisposicionChips {
    /** Una sola fila que se desplaza en horizontal: nunca crece en alto ni corta una etiqueta. */
    DESPLAZABLE,

    /** Varias lineas; todos visibles (la lista de topics, por ejemplo). */
    AJUSTADA,
}

/**
 * Grupo de chips de filtro de Material 3 REUTILIZABLE (Ronda G, tarea 6): recibe su lista y dice cuales
 * estan elegidas; no sabe nada de nivel, topic ni tipo. La Biblioteca lo usa hoy para nivel, topic y
 * categorias; la vista "Verbos" sumara dos grupos (nivel y tipo) con el mismo componente.
 *
 * Si `maxLineasColapsado` no es null y la disposicion es AJUSTADA, el grupo se colapsa a ese numero de
 * lineas con un boton "ver mas / ver menos" cuando hay chips de sobra.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GrupoChips(
    opciones: List<OpcionFiltro>,
    estaElegida: (OpcionFiltro) -> Boolean,
    onElegir: (OpcionFiltro) -> Unit,
    modifier: Modifier = Modifier,
    disposicion: DisposicionChips = DisposicionChips.DESPLAZABLE,
    maxLineasColapsado: Int? = null,
    etiquetaVerMas: String = "",
    etiquetaVerMenos: String = "",
) {
    val chips: @Composable (OpcionFiltro) -> Unit = { opcion ->
        FilterChip(selected = estaElegida(opcion), onClick = { onElegir(opcion) }, label = { Text(opcion.etiqueta) })
    }
    when (disposicion) {
        DisposicionChips.DESPLAZABLE -> Row(
            modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) { opciones.forEach { chips(it) } }

        DisposicionChips.AJUSTADA -> {
            // El estado de "ver mas" es local y no se guarda (regla dura 4): cada apertura arranca colapsado.
            var expandido by remember { mutableStateOf(false) }
            FlowRow(
                modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxLines = if (maxLineasColapsado == null || expandido) Int.MAX_VALUE else maxLineasColapsado,
                overflow = if (maxLineasColapsado == null) {
                    FlowRowOverflow.Visible
                } else {
                    FlowRowOverflow.expandOrCollapseIndicator(
                        expandIndicator = { TextButton(onClick = { expandido = true }) { Text(etiquetaVerMas) } },
                        collapseIndicator = { TextButton(onClick = { expandido = false }) { Text(etiquetaVerMenos) } },
                        minRowsToShowCollapse = maxLineasColapsado + 1,
                    )
                },
            ) { opciones.forEach { chips(it) } }
        }
    }
}

/**
 * Selector de las vistas de una pantalla (hoy "Vocabulario y expresiones" | "Habilidades" en la Biblioteca;
 * manana, tambien "Verbos"). Una sola fila desplazable de chips: aguanta tres vistas en pantalla angosta,
 * con la letra al 160 % y con los textos largos del aleman o del portugues, sin romperse ni cortar una etiqueta.
 */
@Composable
fun SelectorDeVistas(
    vistas: List<OpcionFiltro>,
    elegida: String,
    onElegir: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    GrupoChips(
        opciones = vistas,
        estaElegida = { it.clave == elegida },
        onElegir = { onElegir(it.clave) },
        modifier = modifier,
        disposicion = DisposicionChips.DESPLAZABLE,
    )
}
