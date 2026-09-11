package io.github.percati.lenguapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.datos.cargarAjustes
import io.github.percati.lenguapp.datos.cargarContenidoDesdeAssets
import io.github.percati.lenguapp.datos.guardarAjustes
import io.github.percati.lenguapp.datos.resolverSemana
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.presentacion.filtrarVariantesDesactivadas
import io.github.percati.lenguapp.presentacion.variantesConocidas
import io.github.percati.lenguapp.semana.SemanaIso
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.semanaIsoDe
import io.github.percati.lenguapp.ui.AjustesScreen
import io.github.percati.lenguapp.ui.ContenidoSemanalScreen
import io.github.percati.lenguapp.ui.PantallaSemana
import io.github.percati.lenguapp.ui.TemaLenguApp
import java.time.LocalDate

/**
 * Pantalla unica + ajustes + navegacion por semana (Fase 4). La pantalla de
 * contenido en si (io.github.percati.lenguapp.ui.ContenidoSemanalScreen) no
 * se toca aca: se rediseña despues de la Fase 4.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ajustesIniciales = cargarAjustes(this)
        val variantes = variantesConocidas(cargarContenidoDesdeAssets(this))
        setContent {
            LenguAppApp(
                ajustesIniciales = ajustesIniciales,
                variantesConocidas = variantes,
                resolver = { idioma, nivel, fecha -> resolverSemana(this, idioma, nivel, fecha) },
                onGuardarAjustes = { guardarAjustes(this, it) },
            )
        }
    }
}

@Composable
internal fun LenguAppApp(
    ajustesIniciales: Ajustes,
    variantesConocidas: Set<String>,
    resolver: (Idioma, Nivel, LocalDate) -> ResultadoSemana,
    onGuardarAjustes: (Ajustes) -> Unit,
) {
    var ajustes by remember { mutableStateOf(ajustesIniciales) }
    var fechaVistaIso by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var mostrandoAjustes by rememberSaveable { mutableStateOf(false) }
    val fechaVista = remember(fechaVistaIso) { LocalDate.parse(fechaVistaIso) }

    fun actualizarAjustes(nuevos: Ajustes) {
        ajustes = nuevos
        onGuardarAjustes(nuevos)
    }

    TemaLenguApp {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (mostrandoAjustes) {
                AjustesScreen(
                    ajustes = ajustes,
                    variantesConocidas = variantesConocidas,
                    onAjustesCambiados = ::actualizarAjustes,
                    onVolver = { mostrandoAjustes = false },
                )
            } else {
                Column(Modifier.fillMaxSize()) {
                    BarraNavegacion(
                        semanaIso = semanaIsoDe(fechaVista),
                        esHoy = fechaVista == LocalDate.now(),
                        onSemanaAnterior = { fechaVistaIso = fechaVista.minusWeeks(1).toString() },
                        onHoy = { fechaVistaIso = LocalDate.now().toString() },
                        onSemanaSiguiente = { fechaVistaIso = fechaVista.plusWeeks(1).toString() },
                        idiomaAprendido = ajustes.idiomaAprendido,
                        onCambiarIdioma = { actualizarAjustes(ajustes.copy(idiomaAprendido = it, nivel = nivelConContenidoPara(it))) },
                        onAjustes = { mostrandoAjustes = true },
                    )
                    val resultado = remember(fechaVista, ajustes.idiomaAprendido, ajustes.nivel) {
                        resolver(ajustes.idiomaAprendido, ajustes.nivel, fechaVista)
                    }
                    PantallaConVariantesFiltradas(resultado, ajustes)
                }
            }
        }
    }
}

/**
 * Barra de navegacion en modo lectura: mover la semana en pantalla no toca
 * ningun puntero de progreso, solo cambia que fecha se le pasa al resolutor
 * (que sigue siendo la misma funcion de la Fase 2). Con dos idiomas, un
 * conmutador visible aca es mejor que enterrarlo en Ajustes -- el selector
 * completo (los 6 idiomas) sigue estando en Ajustes tambien.
 */
@Composable
private fun BarraNavegacion(
    semanaIso: SemanaIso,
    esHoy: Boolean,
    onSemanaAnterior: () -> Unit,
    onHoy: () -> Unit,
    onSemanaSiguiente: () -> Unit,
    idiomaAprendido: Idioma,
    onCambiarIdioma: (Idioma) -> Unit,
    onAjustes: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onSemanaAnterior) { Text("< Semana") }
            Text(
                "Semana ${semanaIso.semana} · ${semanaIso.anio}" + if (esHoy) " (hoy)" else "",
                style = MaterialTheme.typography.labelLarge,
            )
            TextButton(onClick = onSemanaSiguiente) { Text("Semana >") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(Idioma.DE, Idioma.EN).forEach { idioma ->
                    FilterChip(
                        selected = idioma == idiomaAprendido,
                        onClick = { onCambiarIdioma(idioma) },
                        label = { Text(idioma.name) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onHoy, enabled = !esHoy) { Text("Hoy") }
                TextButton(onClick = onAjustes) { Text("Ajustes") }
            }
        }
    }
}

/**
 * Si la ficha resuelta pertenece a una variante regional desactivada,
 * CLAUDE.md pide "desactivar ese contenido": esto no es un motivo de
 * ResultadoSemana.SinContenido (esos son sobre disponibilidad de calendario,
 * este es sobre preferencia del usuario), asi que se resuelve aca, no en el
 * resolutor de la Fase 2.
 */
@Composable
private fun PantallaConVariantesFiltradas(resultado: ResultadoSemana, ajustes: Ajustes) {
    val contenido = (resultado as? ResultadoSemana.Encontrado)?.contenido
    if (contenido is Ficha) {
        val filtrada = contenido.filtrarVariantesDesactivadas(ajustes.variantesDesactivadas)
        if (filtrada == null) {
            MensajeVarianteDesactivada(contenido.variante ?: "")
        } else {
            ContenidoSemanalScreen(filtrada, ajustes.idiomaBase)
        }
    } else {
        PantallaSemana(resultado, ajustes.idiomaBase)
    }
}

/**
 * DE solo tiene contenido en B2 y EN solo en C1 (piloto 2026): sin esto, el
 * conmutador de idioma de la cabecera cambiaria a EN pero se quedaria en
 * B2, mostrando "sin contenido" -- justo lo que B3 pide arreglar. Cuando
 * haya mas de un nivel por idioma, esto se reemplaza por elegir el nivel
 * mas cercano al que tenia antes.
 */
internal fun nivelConContenidoPara(idioma: Idioma): Nivel = when (idioma) {
    Idioma.EN -> Nivel.C1
    else -> Nivel.B2
}

@Composable
private fun MensajeVarianteDesactivada(variante: String) {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Contenido oculto", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Esta semana pertenece a la variante regional \"$variante\", que tenés desactivada en Ajustes.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
