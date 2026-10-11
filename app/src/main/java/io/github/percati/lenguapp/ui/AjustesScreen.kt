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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.github.percati.lenguapp.modelo.FuenteTema
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
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
    nivelesConContenido: Map<Idioma, Set<Nivel>>,
    idiomaAplicacionEfectivo: Idioma,
    onAjustesCambiados: (Ajustes) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    // Acerca de vive aca (no en la pantalla principal, que gana alto para el contenido).
    onAcercaDe: () -> Unit = {},
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaAplicacionEfectivo)}") }
            Text(etiquetaAjustes(idiomaAplicacionEfectivo), style = MaterialTheme.typography.headlineSmall)
        }

        EncabezadoApp(idiomaAplicacionEfectivo, onAcercaDe)

        SelectorIdiomasAprendidos(
            idiomasAprendidos = ajustes.idiomasAprendidos,
            idiomasConContenido = idiomasConContenido,
            nivelesConContenido = nivelesConContenido,
            idiomaInterfaz = idiomaAplicacionEfectivo,
            onCambiar = { onAjustesCambiados(ajustes.copy(idiomasAprendidos = it)) },
        )

        SelectorIdiomaAplicacion(
            idiomaInterfaz = idiomaAplicacionEfectivo,
            idiomaSegunSistema = ajustes.idiomaSegunSistema,
            idiomaElegido = ajustes.idiomaInterfaz,
            onElegirIdioma = {
                onAjustesCambiados(ajustes.copy(idiomaBase = it, idiomaInterfaz = it, idiomaSegunSistema = false))
            },
            onElegirSistema = { onAjustesCambiados(ajustes.copy(idiomaSegunSistema = true)) },
        )

        if (avisoGlosasSoloEnEspanol(idiomaAplicacionEfectivo)) {
            Text(
                avisoGlosasTexto(idiomaAplicacionEfectivo),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        SelectorFamiliaTema(
            idiomaInterfaz = idiomaAplicacionEfectivo,
            familiaElegida = ajustes.familiaTema,
            modo = ajustes.modoTema,
            onElegir = { onAjustesCambiados(ajustes.copy(familiaTema = it)) },
        )

        SelectorModoTema(
            idiomaInterfaz = idiomaAplicacionEfectivo,
            modoElegido = ajustes.modoTema,
            onElegir = { onAjustesCambiados(ajustes.copy(modoTema = it)) },
        )

        SelectorFuente(
            idiomaInterfaz = idiomaAplicacionEfectivo,
            fuenteElegida = ajustes.fuente,
            onElegir = { onAjustesCambiados(ajustes.copy(fuente = it)) },
        )
    }
}

/**
 * Tocar un idioma no seleccionado abre el menu de nivel; tocarlo ya
 * seleccionado lo saca (y su pestaña desaparece de la pantalla principal).
 * El boton con el nivel, aparte, deja cambiarlo sin deseleccionar --
 * AJUSTES-FASE-6.md, bloque B. El menu solo ofrece los niveles que
 * `nivelesConContenido` reporta para ese idioma (CLAUDE.md, regla dura #10):
 * mientras cada idioma tenia un solo nivel, ofrecer los cinco igual no se
 * notaba porque elegir cualquier otro caia a "sin contenido"; con ingles B2
 * y C1 conviviendo hay que distinguir cuales existen de verdad.
 */
@Composable
private fun SelectorIdiomasAprendidos(
    idiomasAprendidos: Map<Idioma, Nivel>,
    idiomasConContenido: Set<Idioma>,
    nivelesConContenido: Map<Idioma, Set<Nivel>>,
    idiomaInterfaz: Idioma,
    onCambiar: (Map<Idioma, Nivel>) -> Unit,
) {
    val contexto = LocalContext.current
    var menuAbiertoPara by remember { mutableStateOf<Idioma?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiquetaIdiomasAprendidos(idiomaInterfaz), style = MaterialTheme.typography.titleMedium)
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
                                label = { Text(nombreIdioma(idioma, idiomaInterfaz)) },
                            )
                            DropdownMenu(expanded = menuAbiertoPara == idioma, onDismissRequest = { menuAbiertoPara = null }) {
                                Text(
                                    etiquetaNivel(idiomaInterfaz),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                )
                                val nivelesDisponibles = nivelesConContenido[idioma] ?: emptySet()
                                Nivel.entries.filter { it in nivelesDisponibles }.forEach { nivel ->
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
                            TextButton(
                                onClick = { menuAbiertoPara = idioma },
                                modifier = Modifier.semantics {
                                    contentDescription = "${etiquetaCambiarNivel(idiomaInterfaz)}: ${nivelActual.name}"
                                },
                            ) { Text(nivelActual.name) }
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
            onLongClick = { Toast.makeText(contexto, mensajeIdiomaNoDisponible(idiomaInterfaz, idioma), Toast.LENGTH_SHORT).show() },
        ),
    ) {
        Text(nombreIdioma(idioma, idiomaInterfaz), modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
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
    idiomaInterfaz: Idioma,
    idiomaSegunSistema: Boolean,
    idiomaElegido: Idioma,
    onElegirIdioma: (Idioma) -> Unit,
    onElegirSistema: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiquetaIdiomaApp(idiomaInterfaz), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = idiomaSegunSistema,
                onClick = onElegirSistema,
                label = { Text(etiquetaSegunSistema(idiomaInterfaz)) },
            )
            Idioma.entries.forEach { idioma ->
                FilterChip(
                    selected = !idiomaSegunSistema && idioma == idiomaElegido,
                    onClick = { onElegirIdioma(idioma) },
                    label = { Text(nombreIdioma(idioma, idioma)) },
                )
            }
        }
    }
}

private fun nombreVisible(modo: ModoTema, idiomaInterfaz: Idioma): String = when (modo) {
    ModoTema.CLARO -> etiquetaModoClaro(idiomaInterfaz)
    ModoTema.OSCURO -> etiquetaModoOscuro(idiomaInterfaz)
    ModoTema.SEGUN_SISTEMA -> etiquetaSegunSistema(idiomaInterfaz)
}

/**
 * Familia de tema y modo son dos ajustes separados, no uno --
 * AJUSTES-FASE-7.md, bloque 1. Los nombres de los temas salen en el idioma de app
 * (nombreTema); SISTEMA solo se ofrece donde hay color dinamico (API 31+).
 */
@Composable
private fun SelectorFamiliaTema(idiomaInterfaz: Idioma, familiaElegida: FamiliaTema, modo: ModoTema, onElegir: (FamiliaTema) -> Unit) {
    val oscuro = modoSeVeOscuro(modo)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiquetaEstiloVisual(idiomaInterfaz), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            familiasOfrecidas().forEach { familia ->
                FilterChip(
                    selected = familia == familiaElegida,
                    onClick = { onElegir(familia) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(nombreTema(familia, idiomaInterfaz))
                            // Vista previa: fondo, superficie y acento del tema en el modo actual.
                            val (fondo, superficie, acento) = coloresDeVistaPrevia(familia, oscuro)
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                for (color in listOf(fondo, superficie, acento)) PuntoDeColor(color)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun SelectorModoTema(idiomaInterfaz: Idioma, modoElegido: ModoTema, onElegir: (ModoTema) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiquetaModo(idiomaInterfaz), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ModoTema.entries.forEach { modo ->
                FilterChip(
                    selected = modo == modoElegido,
                    onClick = { onElegir(modo) },
                    label = { Text(nombreVisible(modo, idiomaInterfaz)) },
                )
            }
        }
    }
}

/**
 * Arriba del todo de Ajustes: el nombre de la app, la linea fija que dice para que es (no es un
 * curso: fija lo aprendido en uno) y la fila "Acerca de". Antes vivian en la pantalla principal.
 */
@Composable
private fun EncabezadoApp(idiomaInterfaz: Idioma, onAcercaDe: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(espaciadoActual().entreVinietas)) {
        Text(NOMBRE_APP, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            textoLineaFija(idiomaInterfaz),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onAcercaDe).padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(etiquetaAcercaDe(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

/** Un punto de color de la vista previa de un tema, con un borde fino para que se vea aunque sea del color del fondo. */
@Composable
private fun PuntoDeColor(color: Color) {
    Box(
        Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(0.75.dp, MaterialTheme.colorScheme.outline, CircleShape),
    )
}

/** Tipografia: cada opcion escribe su nombre en su propia fuente, con una muestra corta ("Aa"). */
@Composable
private fun SelectorFuente(idiomaInterfaz: Idioma, fuenteElegida: FuenteTema, onElegir: (FuenteTema) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(etiquetaFuente(idiomaInterfaz), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FuenteTema.entries.forEach { fuente ->
                val familia = familiaTipografica(fuente) ?: FontFamily.Default
                FilterChip(
                    selected = fuente == fuenteElegida,
                    onClick = { onElegir(fuente) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Aa", fontFamily = familia, fontWeight = FontWeight.Bold)
                            Text(nombreFuente(fuente, idiomaInterfaz), fontFamily = familia)
                        }
                    },
                )
            }
        }
    }
}
