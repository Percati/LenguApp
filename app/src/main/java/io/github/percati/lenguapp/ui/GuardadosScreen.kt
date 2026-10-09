package io.github.percati.lenguapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver

/**
 * Feature 3 (Guardados): filtra, no mezcla -- un idioma a la vez, como pide
 * la especificacion, reusando el mismo patron de pestañas por idioma que
 * PantallaPrincipal (MainActivity.kt) usa para `idiomasAprendidos`. Nivel y
 * tipo son chips de un solo filtro activo cada uno, con "Todos" como
 * opcion por defecto.
 */
@Composable
fun GuardadosScreen(
    items: List<ItemGuardado>,
    idiomasAprendidos: Set<Idioma>,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    // false cuando el contenido de esa semana/edicion ya no esta en
    // assets/contenido/ de la build actual (una edicion anterior, por
    // ejemplo): la fila se sigue mostrando, sin el link.
    existeFichaOrigen: (ItemGuardado) -> Boolean,
    onAbrirFicha: (ItemGuardado) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val idiomasDisponibles = remember(items, idiomasAprendidos) {
        (idiomasAprendidos + items.map { it.idioma }).sortedBy { it.ordinal }
    }
    var idiomaElegido by remember(idiomasDisponibles) { mutableStateOf(idiomasDisponibles.firstOrNull()) }
    var nivelElegido by remember { mutableStateOf<Nivel?>(null) }
    var tipoElegido by remember { mutableStateOf<TipoGuardado?>(null) }

    val filtrados = remember(items, idiomaElegido, nivelElegido, tipoElegido) {
        items.filter { it.idioma == idiomaElegido }
            .filter { nivelElegido == null || it.nivel == nivelElegido }
            .filter { tipoElegido == null || it.tipo == tipoElegido }
    }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaInterfaz)}") }
            Text(etiquetaGuardados(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        }

        if (idiomasDisponibles.isEmpty()) {
            Text(mensajeGuardadosVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
        } else {
            ScrollableTabRow(selectedTabIndex = idiomasDisponibles.indexOf(idiomaElegido).coerceAtLeast(0)) {
                idiomasDisponibles.forEach { idioma ->
                    Tab(
                        selected = idioma == idiomaElegido,
                        onClick = { idiomaElegido = idioma },
                        text = { Text(idioma.name) },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(etiquetaFiltroTipo(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(selected = tipoElegido == null, onClick = { tipoElegido = null }, label = { Text(etiquetaFiltroTodos(idiomaInterfaz)) })
                    FilterChip(
                        selected = tipoElegido == TipoGuardado.VOCABULARIO,
                        onClick = { tipoElegido = TipoGuardado.VOCABULARIO },
                        label = { Text(etiquetaTipoVocabulario(idiomaInterfaz)) },
                    )
                    FilterChip(
                        selected = tipoElegido == TipoGuardado.EXPRESION,
                        onClick = { tipoElegido = TipoGuardado.EXPRESION },
                        label = { Text(etiquetaTipoExpresion(idiomaInterfaz)) },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(etiquetaNivel(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(selected = nivelElegido == null, onClick = { nivelElegido = null }, label = { Text(etiquetaFiltroTodos(idiomaInterfaz)) })
                    Nivel.entries.forEach { nivel ->
                        FilterChip(selected = nivelElegido == nivel, onClick = { nivelElegido = nivel }, label = { Text(nivel.name) })
                    }
                }
            }

            if (filtrados.isEmpty()) {
                Text(mensajeGuardadosVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filtrados.forEach { item ->
                        FilaGuardado(
                            item,
                            idiomaBase,
                            enlazable = existeFichaOrigen(item),
                            onClick = { onAbrirFicha(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaGuardado(item: ItemGuardado, idiomaBase: Idioma, enlazable: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = if (enlazable) Modifier.clickable(onClick = onClick) else Modifier,
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // resolver() prefiere idiomaBase: si difiere del idioma aprendido, lo que
            // se muestra puede ser una traduccion con una cita *entre asteriscos* del
            // idioma que se aprende sin traducir -- misma regla que en la ficha
            // (ContenidoSemanalScreen, textoConMarcado con angulares).
            val angulares = idiomaBase != item.idioma
            Text(textoConMarcado(item.texto.resolver(idiomaBase, item.idioma), angulares), style = MaterialTheme.typography.bodyLarge)
            item.funcion?.let {
                Text(
                    textoConMarcado(it.resolver(idiomaBase, item.idioma), angulares),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                "${item.idioma.name} · ${item.nivel.name} · ${item.skillIdOrigen}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
