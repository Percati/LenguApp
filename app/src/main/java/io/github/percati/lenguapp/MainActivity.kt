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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.datos.cargarAjustes
import io.github.percati.lenguapp.datos.guardarAjustes
import io.github.percati.lenguapp.datos.nombresCalendarioDisponibles
import io.github.percati.lenguapp.datos.resolverSemana
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.presentacion.idiomaAplicacionEfectivo
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.SemanaIso
import io.github.percati.lenguapp.semana.idiomasConContenido
import io.github.percati.lenguapp.semana.semanaIsoDe
import io.github.percati.lenguapp.ui.AjustesScreen
import io.github.percati.lenguapp.ui.PantallaSemana
import io.github.percati.lenguapp.ui.TemaLenguApp
import java.time.LocalDate
import java.util.Locale

/**
 * Pantalla unica + ajustes + navegacion por semana + una pestaña por idioma
 * aprendido (Fase 6). La pantalla de contenido en si
 * (io.github.percati.lenguapp.ui.ContenidoSemanalScreen) no se toca aca.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ajustesIniciales = cargarAjustes(this)
        val idiomasConContenido = idiomasConContenido(nombresCalendarioDisponibles(this))
        setContent {
            LenguAppApp(
                ajustesIniciales = ajustesIniciales,
                idiomasConContenido = idiomasConContenido,
                resolver = { idioma, nivel, fecha -> resolverSemana(this, idioma, nivel, fecha) },
                onGuardarAjustes = { guardarAjustes(this, it) },
            )
        }
    }
}

@Composable
internal fun LenguAppApp(
    ajustesIniciales: Ajustes,
    idiomasConContenido: Set<Idioma>,
    resolver: (Idioma, Nivel, LocalDate) -> ResultadoSemana,
    onGuardarAjustes: (Ajustes) -> Unit,
) {
    var ajustes by remember { mutableStateOf(ajustesIniciales) }
    var fechaVistaIso by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var mostrandoAjustes by rememberSaveable { mutableStateOf(false) }
    var idiomaActivoElegido by rememberSaveable { mutableStateOf<String?>(null) }
    val fechaVista = remember(fechaVistaIso) { LocalDate.parse(fechaVistaIso) }

    // Solo se lee el idioma del dispositivo si el usuario activo "Segun el
    // sistema" -- CLAUDE.md, regla dura #2 enmendada (AJUSTES-FASE-6.md, E).
    val codigoIdiomaSistema = remember(ajustes.idiomaSegunSistema) {
        if (ajustes.idiomaSegunSistema) Locale.getDefault().language else null
    }
    val idiomaAplicacion = idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema)

    val idiomasSeleccionados = ajustes.idiomasAprendidos.keys.sortedBy { it.ordinal }
    val idiomaActivo = idiomasSeleccionados.firstOrNull { it.name == idiomaActivoElegido } ?: idiomasSeleccionados.firstOrNull()

    fun actualizarAjustes(nuevos: Ajustes) {
        ajustes = nuevos
        onGuardarAjustes(nuevos)
    }

    TemaLenguApp {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (mostrandoAjustes) {
                AjustesScreen(
                    ajustes = ajustes,
                    idiomasConContenido = idiomasConContenido,
                    idiomaAplicacionEfectivo = idiomaAplicacion,
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
                        onAjustes = { mostrandoAjustes = true },
                    )

                    if (idiomasSeleccionados.isEmpty() || idiomaActivo == null) {
                        SinIdiomaSeleccionado()
                    } else {
                        ScrollableTabRow(selectedTabIndex = idiomasSeleccionados.indexOf(idiomaActivo).coerceAtLeast(0)) {
                            idiomasSeleccionados.forEach { idioma ->
                                Tab(
                                    selected = idioma == idiomaActivo,
                                    onClick = { idiomaActivoElegido = idioma.name },
                                    text = { Text(idioma.name) },
                                )
                            }
                        }
                        val nivelActivo = ajustes.idiomasAprendidos.getValue(idiomaActivo)
                        val resultado = remember(fechaVista, idiomaActivo, nivelActivo) {
                            resolver(idiomaActivo, nivelActivo, fechaVista)
                        }
                        PantallaSemana(resultado, ajustes.idiomaBase)
                    }
                }
            }
        }
    }
}

/**
 * Navegacion en modo lectura: mover la semana en pantalla no toca ningun
 * puntero de progreso, solo cambia que fecha se le pasa al resolutor (la
 * misma funcion de la Fase 2). El selector de idioma vive en las pestañas
 * de abajo desde la Fase 6, no aca.
 */
@Composable
private fun BarraNavegacion(
    semanaIso: SemanaIso,
    esHoy: Boolean,
    onSemanaAnterior: () -> Unit,
    onHoy: () -> Unit,
    onSemanaSiguiente: () -> Unit,
    onAjustes: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onSemanaAnterior) { Text("< Semana") }
        Text(
            "Semana ${semanaIso.semana} · ${semanaIso.anio}" + if (esHoy) " (hoy)" else "",
            style = MaterialTheme.typography.labelLarge,
        )
        TextButton(onClick = onSemanaSiguiente) { Text("Semana >") }
        TextButton(onClick = onHoy, enabled = !esHoy) { Text("Hoy") }
        TextButton(onClick = onAjustes) { Text("Ajustes") }
    }
}

@Composable
private fun SinIdiomaSeleccionado() {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sin idioma seleccionado", style = MaterialTheme.typography.headlineSmall)
        Text("Elegí al menos un idioma en Ajustes para ver contenido.", style = MaterialTheme.typography.bodyLarge)
    }
}
