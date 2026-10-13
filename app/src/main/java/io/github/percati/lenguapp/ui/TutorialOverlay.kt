@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package io.github.percati.lenguapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

const val TAG_TOUR_TARJETA = "tour-tarjeta"
const val TAG_TOUR_SIGUIENTE = "tour-siguiente"
const val TAG_TOUR_ATRAS = "tour-atras"
const val TAG_TOUR_SALTAR = "tour-saltar"
const val TAG_TOUR_LISTO = "tour-listo"
const val TAG_TOUR_FOCO = "tour-foco"

private val ESCRIM = Color.Black.copy(alpha = 0.66f)
private val BORDE_FOCO = Color.White

/**
 * El recuadro del tutorial: oscurece TODA la pantalla menos el ancla del paso (un agujero redondeado con un borde claro), y una
 * tarjeta con el texto del paso y «Siguiente»/«Saltar» (en el ultimo paso, «Listo»). La tarjeta va en la mitad opuesta a lo
 * senalado, asi no lo tapa. Mientras esta, nada de lo de abajo recibe toques; el boton atras abandona el tour (cuenta como visto).
 *
 * Si el ancla del paso no aparece en [ESPERA_ANCLA_MS], el paso se SALTEA (nunca se oscurece la pantalla sin nada senalado).
 * Sin animaciones propias: respeta «Quitar animaciones» del sistema por construccion. Colores: el escrim es negro translucido
 * (legible en los 4 temas, claro y oscuro) y la tarjeta usa los colores del tema, ya verificados AA.
 */
@Composable
fun TutorialOverlay(gestor: GestorTours, idiomaInterfaz: Idioma, modifier: Modifier = Modifier) {
    val tour = gestor.activo ?: return
    val indice = gestor.indice
    val paso = gestor.pasoActual ?: return

    // El boton atras del sistema hace lo mismo que «Atras» de la tarjeta: vuelve un paso; solo en el primero abandona el tour (cuenta como visto).
    BackHandler { if (gestor.puedeVolver) gestor.atras() else gestor.terminar() }

    val margenAlTraer = with(LocalDensity.current) { MARGEN_AL_TRAER_DP.dp.toPx() }
    // Un paso con ancla espera a que la pantalla la componga (y la lleva a la vista); si no aparece, se saltea.
    var listo by remember(tour, indice) { mutableStateOf(paso.ancla == null) }
    LaunchedEffect(tour, indice) {
        val ancla = paso.ancla ?: return@LaunchedEffect
        val rect = withTimeoutOrNull(ESPERA_ANCLA_MS) { snapshotFlow { gestor.registro.rectDe(ancla) }.filterNotNull().first() }
        if (rect == null) {
            gestor.saltearSinAncla()
        } else {
            gestor.registro.traerALaVista(ancla, margenPx = margenAlTraer)
            listo = true
        }
    }
    if (!listo) return

    val rect = paso.ancla?.let { gestor.registro.rectDe(it) }
    var tamano by remember { mutableStateOf(IntSize.Zero) }
    val borde = with(LocalDensity.current) { 2.dp.toPx() }
    val margen = with(LocalDensity.current) { 6.dp.toPx() }
    val radio = with(LocalDensity.current) { 12.dp.toPx() }
    val titulo = textoTour(paso.titulo, idiomaInterfaz)

    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged { tamano = it }
            .pointerInput(Unit) { detectTapGestures { } } // se come los toques: lo de abajo no se usa mientras dura el tour
            .testTag(TAG_TOUR_FOCO),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .drawWithContent {
                    drawRect(ESCRIM)
                    if (rect != null) {
                        val arriba = Offset(rect.left - margen, rect.top - margen)
                        val medida = Size(rect.width + 2 * margen, rect.height + 2 * margen)
                        drawRoundRect(Color.Black, arriba, medida, CornerRadius(radio), blendMode = BlendMode.Clear)
                        drawRoundRect(BORDE_FOCO, arriba, medida, CornerRadius(radio), style = Stroke(borde))
                    }
                },
        )

        // La tarjeta va en la mitad opuesta al foco; sin foco, abajo.
        val enLaMitadDeArriba = rect != null && tamano.height > 0 && rect.center.y < tamano.height / 2f
        Surface(
            modifier = Modifier
                .align(if (enLaMitadDeArriba) Alignment.BottomCenter else if (rect == null) Alignment.Center else Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(max = with(LocalDensity.current) { (tamano.height * 0.55f).toDp() }.coerceAtLeast(160.dp))
                .semantics { paneTitle = titulo }
                .testTag(TAG_TOUR_TARJETA),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        etiquetaTourPaso(gestor.posicion, gestor.totalEfectivo, idiomaInterfaz),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(textoTour(paso.cuerpo, idiomaInterfaz), style = MaterialTheme.typography.bodyMedium)
                    paso.cuerpoExtra?.let { Text(textoTour(it, idiomaInterfaz), style = MaterialTheme.typography.bodyMedium) }
                }
                val ultimo = gestor.esUltimoPaso
                // Atras · Saltar · Siguiente/Listo. En una pantalla angosta los botones pasan a otra linea (FlowRow), no se achican.
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // En el primer paso «Atras» no se muestra (no se deshabilita: un boton gris que no hace nada confunde mas que su ausencia).
                    if (gestor.puedeVolver) {
                        TextButton(
                            onClick = { gestor.atras() },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics { contentDescription = descripcionTourAtras(idiomaInterfaz) }
                                .testTag(TAG_TOUR_ATRAS),
                        ) { Text(etiquetaTourAtras(idiomaInterfaz)) }
                    }
                    TextButton(
                        onClick = { gestor.saltarTodo() },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .semantics { contentDescription = descripcionTourSaltar(idiomaInterfaz) }
                            .testTag(TAG_TOUR_SALTAR),
                    ) { Text(etiquetaTourSaltar(idiomaInterfaz)) }
                    Button(
                        onClick = { if (ultimo) gestor.terminar() else gestor.siguiente() },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .semantics {
                                contentDescription = if (ultimo) descripcionTourListo(idiomaInterfaz) else descripcionTourSiguiente(idiomaInterfaz)
                            }
                            .testTag(if (ultimo) TAG_TOUR_LISTO else TAG_TOUR_SIGUIENTE),
                    ) { Text(if (ultimo) etiquetaTourListo(idiomaInterfaz) else etiquetaTourSiguiente(idiomaInterfaz)) }
                }
            }
        }
    }
}
