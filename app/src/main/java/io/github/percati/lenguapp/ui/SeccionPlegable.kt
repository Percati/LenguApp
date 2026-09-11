package io.github.percati.lenguapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Cuadro de referencia, vocabulario y Redemittel usan esto: plegados por
 * defecto. Una ficha completa ronda las 1.000 palabras; desplegada entera no
 * se lee (ver criterio de aceptacion de la Fase 3).
 */
@Composable
fun SeccionPlegable(
    titulo: String,
    modifier: Modifier = Modifier,
    expandidaPorDefecto: Boolean = false,
    contenido: @Composable () -> Unit,
) {
    var expandida by rememberSaveable { mutableStateOf(expandidaPorDefecto) }
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandida = !expandida }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expandida) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expandida) "Contraer" else "Expandir",
            )
        }
        if (expandida) {
            Column(Modifier.padding(bottom = 8.dp)) { contenido() }
        }
    }
}
