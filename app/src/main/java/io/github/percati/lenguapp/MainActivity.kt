package io.github.percati.lenguapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import io.github.percati.lenguapp.ui.etiquetaAjustes
import io.github.percati.lenguapp.ui.etiquetaHoy
import io.github.percati.lenguapp.ui.etiquetaSemana
import io.github.percati.lenguapp.ui.mensajeDobleAtrasParaSalir
import java.time.LocalDate
import java.util.Locale

private const val DESTINO_PRINCIPAL = "principal"
private const val DESTINO_AJUSTES = "ajustes"
private const val VENTANA_DOBLE_ATRAS_MS = 3000L

/**
 * Pantalla unica + ajustes + navegacion por semana + una pestaña por idioma
 * aprendido. Ajustes es un destino real de navegacion (AJUSTES-FASE-7.md,
 * bloque 2.4), no una bandera booleana: asi el gesto de atras del sistema
 * tiene algo para desapilar. La pantalla de contenido en si
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
    // Parametro de prueba: la produccion nunca lo pasa, asi que siempre
    // arranca en la fecha real de hoy. Los tests de pantallazo del Bloque
    // 2.1 lo necesitan para fijar de verdad "semana anterior" (no alcanza
    // con que el resolutor devuelva otro contenido: "esHoy" tiene que
    // reflejar la fecha inicial real, no una fecha distinta escondida
    // detras de un resolutor con trampa).
    fechaInicial: LocalDate = LocalDate.now(),
) {
    var ajustes by remember { mutableStateOf(ajustesIniciales) }
    var fechaVistaIso by rememberSaveable { mutableStateOf(fechaInicial.toString()) }
    var idiomaActivoElegido by rememberSaveable { mutableStateOf<String?>(null) }
    val fechaVista = remember(fechaVistaIso) { LocalDate.parse(fechaVistaIso) }
    val navController = rememberNavController()

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

    TemaLenguApp(familiaTema = ajustes.familiaTema, modoTema = ajustes.modoTema) {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = DESTINO_PRINCIPAL) {
                composable(DESTINO_PRINCIPAL) {
                    PantallaPrincipal(
                        ajustes = ajustes,
                        idiomaAplicacion = idiomaAplicacion,
                        fechaVista = fechaVista,
                        idiomasSeleccionados = idiomasSeleccionados,
                        idiomaActivo = idiomaActivo,
                        resolver = resolver,
                        onIdiomaActivoElegido = { idiomaActivoElegido = it.name },
                        onSemanaAnterior = { fechaVistaIso = fechaVista.minusWeeks(1).toString() },
                        onHoy = { fechaVistaIso = LocalDate.now().toString() },
                        onAjustes = { navController.navigate(DESTINO_AJUSTES) },
                    )
                }
                composable(DESTINO_AJUSTES) {
                    AjustesScreen(
                        ajustes = ajustes,
                        idiomasConContenido = idiomasConContenido,
                        idiomaAplicacionEfectivo = idiomaAplicacion,
                        onAjustesCambiados = ::actualizarAjustes,
                        onVolver = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}

@Composable
private fun PantallaPrincipal(
    ajustes: Ajustes,
    idiomaAplicacion: Idioma,
    fechaVista: LocalDate,
    idiomasSeleccionados: List<Idioma>,
    idiomaActivo: Idioma?,
    resolver: (Idioma, Nivel, LocalDate) -> ResultadoSemana,
    onIdiomaActivoElegido: (Idioma) -> Unit,
    onSemanaAnterior: () -> Unit,
    onHoy: () -> Unit,
    onAjustes: () -> Unit,
) {
    ManejarDobleAtrasParaSalir(idiomaAplicacion)

    Column(Modifier.fillMaxSize()) {
        BarraNavegacion(
            semanaIso = semanaIsoDe(fechaVista),
            esHoy = fechaVista == LocalDate.now(),
            idiomaAplicacion = idiomaAplicacion,
            onSemanaAnterior = onSemanaAnterior,
            onHoy = onHoy,
            onAjustes = onAjustes,
        )

        if (idiomasSeleccionados.isEmpty() || idiomaActivo == null) {
            SinIdiomaSeleccionado()
        } else {
            ScrollableTabRow(selectedTabIndex = idiomasSeleccionados.indexOf(idiomaActivo).coerceAtLeast(0)) {
                idiomasSeleccionados.forEach { idioma ->
                    Tab(
                        selected = idioma == idiomaActivo,
                        onClick = { onIdiomaActivoElegido(idioma) },
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

/**
 * AJUSTES-FASE-7.md, bloque 2.4: el primer atras en la pantalla principal
 * (la raiz de la pila: no hay nada que NavHost pueda desapilar) avisa;
 * repetirlo dentro de la ventana cierra. El aviso es un toast, "del mismo
 * tipo que el del idioma no disponible", y va traducido.
 */
@Composable
private fun ManejarDobleAtrasParaSalir(idiomaAplicacion: Idioma) {
    val contexto = LocalContext.current
    val actividad = contexto as? Activity
    var ultimoAtras by remember { mutableLongStateOf(0L) }
    BackHandler {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimoAtras < VENTANA_DOBLE_ATRAS_MS) {
            actividad?.finish()
        } else {
            ultimoAtras = ahora
            Toast.makeText(contexto, mensajeDobleAtrasParaSalir(idiomaAplicacion), Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * AJUSTES-FASE-7.md, bloque 2.1: el boton de Ajustes esta en su propia
 * fila, fuera de la zona del encabezado. La fila de la semana tiene la
 * flecha de ancho fijo (icono, no texto: no varia con el idioma ni con el
 * escalado de fuente) y el texto con `weight(1f)`, asi que el boton no
 * cambia de posicion cuando el sufijo "(hoy)" aparece o desaparece, ni con
 * otro idioma, ni con la fuente del sistema al 150 %.
 *
 * Bloque 0: sin flecha de "semana siguiente". **Es una decision de
 * producto, no una limitacion tecnica**: ver el futuro invita a
 * adelantarse y hacer varias misiones en paralelo, lo contrario de un
 * sistema semanal. El Spacer del ancho de un IconButton mantiene el texto
 * centrado igual que si la flecha siguiera ahi. resolverContenidoDeLaSemana
 * (semana/ResolutorSemana.kt) sigue recibiendo la fecha como parametro y
 * no usa LocalDate.now() internamente -- sigue cubierta por sus tests,
 * incluidos los tres casos de borde ISO -- asi que reactivar la
 * navegacion hacia adelante en el futuro es agregar de vuelta un boton
 * que llame a `fechaVista.plusWeeks(1)`, no rehacer nada.
 */
@Composable
private fun BarraNavegacion(
    semanaIso: SemanaIso,
    esHoy: Boolean,
    idiomaAplicacion: Idioma,
    onSemanaAnterior: () -> Unit,
    onHoy: () -> Unit,
    onAjustes: () -> Unit,
) {
    val textoSemana = etiquetaSemana(idiomaAplicacion)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // AJUSTES-FASE-7.md, bloque 1: el acento se reserva para lo
            // accionable -- flechas de navegacion incluidas -- asi que se
            // tinta explicito, no se deja el color de contenido ambiente.
            IconButton(onClick = onSemanaAnterior) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "< $textoSemana",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                "$textoSemana ${semanaIso.semana} · ${semanaIso.anio}" +
                    if (esHoy) " (${etiquetaHoy(idiomaAplicacion)})" else "",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(48.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onHoy, enabled = !esHoy) { Text(etiquetaHoy(idiomaAplicacion)) }
            TextButton(onClick = onAjustes) { Text(etiquetaAjustes(idiomaAplicacion)) }
        }
    }
}

@Composable
private fun SinIdiomaSeleccionado() {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sin idioma seleccionado", style = MaterialTheme.typography.headlineSmall)
        Text("Elegí al menos un idioma en Ajustes para ver contenido.", style = MaterialTheme.typography.bodyLarge)
    }
}
