package io.github.percati.lenguapp.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.presentacion.avisoGlosasSoloEnEspanol

/**
 * Provisoria en su estetica (el rediseño visual de Fase 5 no toco esta
 * pantalla). Funcionalmente completa para la Fase 6: multi-idioma con nivel
 * por idioma, idioma de la aplicacion unificado con opcion "segun el
 * sistema", sin nombres de campo en el texto visible (AJUSTES-FASE-6.md,
 * bloques A-C).
 */
@Composable
fun AjustesScreen(
    ajustes: Ajustes,
    idiomasConContenido: Set<Idioma>,
    idiomaAplicacionEfectivo: Idioma,
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

        SelectorIdiomasAprendidos(
            idiomasAprendidos = ajustes.idiomasAprendidos,
            idiomasConContenido = idiomasConContenido,
            idiomaInterfaz = idiomaAplicacionEfectivo,
            onCambiar = { onAjustesCambiados(ajustes.copy(idiomasAprendidos = it)) },
        )

        SelectorIdiomaAplicacion(
            idiomaSegunSistema = ajustes.idiomaSegunSistema,
            idiomaElegido = ajustes.idiomaInterfaz,
            onElegirIdioma = {
                onAjustesCambiados(ajustes.copy(idiomaBase = it, idiomaInterfaz = it, idiomaSegunSistema = false))
            },
            onElegirSistema = { onAjustesCambiados(ajustes.copy(idiomaSegunSistema = true)) },
        )

        if (avisoGlosasSoloEnEspanol(idiomaAplicacionEfectivo)) {
            Text(
                "Las traducciones de vocabulario todavía solo existen en español; se van a mostrar en español mientras tanto.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Tocar un idioma no seleccionado abre el menu de nivel; tocarlo ya
 * seleccionado lo saca (y su pestaña desaparece de la pantalla principal).
 * El boton con el nivel, aparte, deja cambiarlo sin deseleccionar --
 * AJUSTES-FASE-6.md, bloque B.
 */
@Composable
private fun SelectorIdiomasAprendidos(
    idiomasAprendidos: Map<Idioma, Nivel>,
    idiomasConContenido: Set<Idioma>,
    idiomaInterfaz: Idioma,
    onCambiar: (Map<Idioma, Nivel>) -> Unit,
) {
    val contexto = LocalContext.current
    var menuAbiertoPara by remember { mutableStateOf<Idioma?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Idioma que aprendés", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Idioma.entries.forEach { idioma ->
                val nivelActual = idiomasAprendidos[idioma]
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (idioma !in idiomasConContenido) {
                        IdiomaNoDisponible(idioma, idiomaInterfaz, contexto)
                    } else {
                        Box {
                            FilterChip(
                                selected = nivelActual != null,
                                onClick = {
                                    if (nivelActual != null) onCambiar(idiomasAprendidos - idioma) else menuAbiertoPara = idioma
                                },
                                label = { Text(idioma.name) },
                            )
                            DropdownMenu(expanded = menuAbiertoPara == idioma, onDismissRequest = { menuAbiertoPara = null }) {
                                Nivel.entries.forEach { nivel ->
                                    DropdownMenuItem(
                                        text = { Text(nivel.name) },
                                        onClick = {
                                            onCambiar(idiomasAprendidos + (idioma to nivel))
                                            menuAbiertoPara = null
                                        },
                                    )
                                }
                            }
                        }
                        if (nivelActual != null) {
                            TextButton(onClick = { menuAbiertoPara = idioma }) { Text(nivelActual.name) }
                        }
                    }
                }
            }
        }
    }
}

/** Gris, no seleccionable; mantener pulsado avisa por que -- AJUSTES-FASE-6.md, bloque B. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IdiomaNoDisponible(idioma: Idioma, idiomaInterfaz: Idioma, contexto: Context) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.combinedClickable(
            onClick = {},
            onLongClick = { Toast.makeText(contexto, mensajeIdiomaNoDisponible(idiomaInterfaz), Toast.LENGTH_SHORT).show() },
        ),
    ) {
        Text(idioma.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}

/**
 * Un solo control en pantalla ("Idioma de la aplicación"); en los datos
 * sigue escribiendo idiomaBase e idiomaInterfaz por separado (CLAUDE.md,
 * regla dura #6) -- eso lo resuelve quien llama a onElegirIdioma, no esta
 * funcion. "Según el sistema" enmienda la regla dura #2: lee el idioma del
 * dispositivo solo porque el usuario activo esta opcion.
 */
@Composable
private fun SelectorIdiomaAplicacion(
    idiomaSegunSistema: Boolean,
    idiomaElegido: Idioma,
    onElegirIdioma: (Idioma) -> Unit,
    onElegirSistema: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Idioma de la aplicación", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = idiomaSegunSistema,
                onClick = onElegirSistema,
                label = { Text("Según el sistema") },
            )
            Idioma.entries.forEach { idioma ->
                FilterChip(
                    selected = !idiomaSegunSistema && idioma == idiomaElegido,
                    onClick = { onElegirIdioma(idioma) },
                    label = { Text(idioma.name) },
                )
            }
        }
    }
}
