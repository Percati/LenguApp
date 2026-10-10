package io.github.percati.lenguapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Idioma

/**
 * La fila de una palabra o expresion, UNICA para la Biblioteca y Guardados (Ronda F2, tarea 1):
 * antes eran dos composables y divergieron (Guardados mostraba solo la traduccion). Muestra
 * siempre, sin depender del idioma de app ni del switch de traduccion de A2/B1:
 *  1. el original, en el idioma que se aprende;
 *  2. su traduccion al idioma de app, solo si existe (si no, se omite: ni un hueco ni el original repetido);
 *  3. el contexto/funcion, solo si existe y no esta en blanco, siempre en el idioma que se aprende.
 * Lo unico que cambia entre pantallas es la estrella (`guardada`, su etiqueta y su accion).
 */
@Composable
fun FilaPalabra(
    original: String,
    traduccion: String?,
    funcion: String?,
    idiomaAprendido: Idioma,
    idiomaBase: Idioma,
    guardada: Boolean,
    tagEstrella: String,
    descripcionEstrella: String?,
    onEstrella: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                (if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                    .weight(1f)
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(espaciadoActual().dentroDeItem),
            ) {
                Text(original, style = MaterialTheme.typography.bodyLarge)
                // La traduccion esta en idiomaBase: una cita *entre asteriscos* ahi adentro es una
                // palabra del idioma que se aprende que quedo sin traducir (como en la ficha).
                traduccion?.let {
                    Text(textoConMarcado(it, angulares = idiomaAprendido != idiomaBase), style = MaterialTheme.typography.bodyMedium)
                }
                funcion?.let {
                    Text(
                        textoConMarcado(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onEstrella, modifier = Modifier.size(40.dp).padding(end = 4.dp).testTag(tagEstrella)) {
                Icon(
                    if (guardada) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = descripcionEstrella,
                    tint = if (guardada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
