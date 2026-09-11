package io.github.percati.lenguapp.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel

/**
 * Provisoria: la pantalla se rediseña despues de la Fase 4, cuando esten
 * todos los elementos. Esta version es funcional, no definitiva.
 *
 * uiLanguage (idiomaInterfaz) y baseLanguage (idiomaBase) son selectores
 * separados a proposito -- CLAUDE.md, regla dura #6 -- aunque hoy los dos
 * arranquen en español.
 */
@Composable
fun AjustesScreen(
    ajustes: Ajustes,
    variantesConocidas: Set<String>,
    onAjustesCambiados: (Ajustes) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< Volver") }
            Text("Ajustes", style = MaterialTheme.typography.headlineSmall)
        }

        SelectorEnum("Idioma que aprendés", Idioma.entries, ajustes.idiomaAprendido) {
            onAjustesCambiados(ajustes.copy(idiomaAprendido = it))
        }
        SelectorEnum("Nivel", Nivel.entries, ajustes.nivel) {
            onAjustesCambiados(ajustes.copy(nivel = it))
        }
        SelectorEnum("Idioma de las traducciones (baseLanguage)", Idioma.entries, ajustes.idiomaBase) {
            onAjustesCambiados(ajustes.copy(idiomaBase = it))
        }
        SelectorEnum("Idioma de la interfaz (uiLanguage)", Idioma.entries, ajustes.idiomaInterfaz) {
            onAjustesCambiados(ajustes.copy(idiomaInterfaz = it))
        }

        if (variantesConocidas.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Variantes regionales", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Desactivá las que no querés ver. El contenido marcado con esa variante se oculta.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    variantesConocidas.sorted().forEach { variante ->
                        val activa = variante !in ajustes.variantesDesactivadas
                        FilterChip(
                            selected = activa,
                            onClick = {
                                val nuevas = if (activa) {
                                    ajustes.variantesDesactivadas + variante
                                } else {
                                    ajustes.variantesDesactivadas - variante
                                }
                                onAjustesCambiados(ajustes.copy(variantesDesactivadas = nuevas))
                            },
                            label = { Text(variante) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T : Enum<T>> SelectorEnum(
    etiqueta: String,
    opciones: List<T>,
    seleccionado: T,
    onSeleccion: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiqueta, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            opciones.forEach { opcion ->
                FilterChip(
                    selected = opcion == seleccionado,
                    onClick = { onSeleccion(opcion) },
                    label = { Text(opcion.name) },
                )
            }
        }
    }
}
