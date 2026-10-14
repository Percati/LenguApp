package io.github.percati.lenguapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Idioma

/** El nombre de la app es un nombre propio: no se traduce. */
const val NOMBRE_APP = "LenguApp"

/**
 * Acerca de (Ronda E, tarea 6): pantalla estatica, sin estado ni persistencia. Que es y que
 * no es (no es un curso: fija lo aprendido), privacidad (sin Internet, sin cuenta, solo lee
 * la fecha), licencias y version. Todo en el idioma de app (TextosInterfaz, 6 idiomas).
 */
@Composable
fun AcercaDeScreen(
    idiomaInterfaz: Idioma,
    version: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaInterfaz)}") }
            Text(etiquetaAcercaDe(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        }
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).finDeScrollSobreBarras().padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(espaciadoActual().entreSecciones),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(NOMBRE_APP, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(textoLineaFija(idiomaInterfaz), style = MaterialTheme.typography.bodyMedium)
            }
            Bloque(etiquetaAcercaQueEsTitulo(idiomaInterfaz), textoAcercaQueEs(idiomaInterfaz))
            Bloque(etiquetaAcercaComoTitulo(idiomaInterfaz), textoAcercaComo(idiomaInterfaz))
            Bloque(etiquetaAcercaPrivacidadTitulo(idiomaInterfaz), textoAcercaPrivacidad(idiomaInterfaz))
            Bloque(
                etiquetaAcercaLicenciasTitulo(idiomaInterfaz),
                textoAcercaLicencias(idiomaInterfaz) + "\n" + textoAcercaFuentes(idiomaInterfaz),
            )
            // Las URL son texto que se selecciona y se copia: sin enlaces tocables ni salidas hacia otras apps (decision de Fer, Ronda S).
            SelectionContainer { Bloque(etiquetaAcercaVocesTitulo(idiomaInterfaz), textoAcercaVoces(idiomaInterfaz)) }
            Column(verticalArrangement = Arrangement.spacedBy(espaciadoActual().entreVinietas)) {
                if (version.isNotBlank()) {
                    Text(
                        textoAcercaVersion(idiomaInterfaz, version),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(textoAcercaPrueba(idiomaInterfaz), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun Bloque(titulo: String, texto: String) {
    Column(verticalArrangement = Arrangement.spacedBy(espaciadoActual().entreVinietas)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}
