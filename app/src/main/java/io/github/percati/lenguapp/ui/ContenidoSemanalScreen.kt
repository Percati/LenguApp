package io.github.percati.lenguapp.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.ChallengeType
import io.github.percati.lenguapp.modelo.Clase
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.CuadroReferencia
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.presentacion.TraduccionRedemittel
import io.github.percati.lenguapp.presentacion.contrasteParaMostrar
import io.github.percati.lenguapp.presentacion.erroresParaMostrar
import io.github.percati.lenguapp.presentacion.traduccionParaMostrar
import io.github.percati.lenguapp.semana.RazonSinContenido
import io.github.percati.lenguapp.semana.ResultadoSemana

/**
 * AJUSTES-FASE-8.md, B.2: separacion entre secciones (Beispiele, Hinweise,
 * Kontrast, Typische Fehler...) resuelta con espacio, no con lineas. La
 * Fase 5 ya habia pedido menos reglas divisorias y mas contraste
 * tipografico; el resultado quedo demasiado plano. Este valor es bastante
 * mayor que el espaciado dentro de una seccion (Seccion() usa 8.dp entre su
 * titulo y el cuerpo, y entre parrafos/vinetas del mismo cuerpo) para que el
 * corte entre secciones se note sin una regla. Un separador tenue en color
 * secundario queda como ultimo recurso si esto no alcanza -- no aplicado.
 */
private val ESPACIO_ENTRE_SECCIONES = 32.dp

/**
 * Punto de entrada de la pantalla unica: la Fase 4 agrega navegacion y
 * ajustes, pero el "no hay nada para esta celda de la matriz" es el caso
 * normal desde ahora (ver CLAUDE.md) y no puede dejar la pantalla en blanco
 * ni romper la app.
 */
@Composable
fun PantallaSemana(resultado: ResultadoSemana, idiomaBase: Idioma, idiomaInterfaz: Idioma, modifier: Modifier = Modifier) {
    when (resultado) {
        is ResultadoSemana.Encontrado -> ContenidoSemanalScreen(resultado.contenido, idiomaBase, modifier)
        is ResultadoSemana.SinContenido -> SinContenidoMensaje(resultado, idiomaInterfaz, modifier)
    }
}

/** AJUSTES-FASE-9.md, bloque B: estos tres mensajes son parte del chrome, traducidos a los seis idiomas de interfaz. */
@Composable
private fun SinContenidoMensaje(sinContenido: ResultadoSemana.SinContenido, idiomaInterfaz: Idioma, modifier: Modifier = Modifier) {
    val mensaje = when (sinContenido.razon) {
        RazonSinContenido.ANIO_SIN_CALENDARIO ->
            mensajeSinCalendario(idiomaInterfaz, sinContenido.semanaIso.anio)
        RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO ->
            mensajeSinContenidoNivel(idiomaInterfaz, sinContenido.idioma, sinContenido.nivel)
        RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION ->
            mensajeSinContenidoSemana(idiomaInterfaz, sinContenido.semanaIso.anio)
    }
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(mensajeSinContenidoTitulo(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        Text(mensaje, style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * Cuadro de referencia, vocabulario y Redemittel van en SeccionPlegable
 * (colapsados por defecto): una ficha completa ronda las 1.000 palabras y
 * desplegada entera no se lee. El resto de las secciones son cortas y van
 * siempre visibles.
 */
@Composable
fun ContenidoSemanalScreen(
    contenido: ContenidoSemanal,
    idiomaBase: Idioma,
    modifier: Modifier = Modifier,
) {
    when (contenido) {
        is Ficha -> FichaContenido(contenido, idiomaBase, modifier)
        is SemanaEspecial -> SemanaEspecialContenido(contenido, modifier)
    }
}

// AJUSTES-FASE-8.md, B.4: contenido seleccionable y copiable para traducir a
// mano un pasaje. SelectionContainer envuelve toda la ficha; el boton de
// copiar el prompt se excluye con DisableSelection para que el gesto de
// seleccionar texto no le gane al click ni lo deje capturado.
@Composable
private fun FichaContenido(ficha: Ficha, idiomaBase: Idioma, modifier: Modifier = Modifier) = SelectionContainer {
    // Campos bilingues (A2/B1): idioma de la app, y el que se aprende si falta esa clave.
    val t: (TextoBilingue) -> String = { it.resolver(idiomaBase, ficha.idioma) }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_SECCIONES),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ficha.variante?.let { EtiquetaVariante(it) }
            Text(t(ficha.titulo), style = MaterialTheme.typography.headlineSmall)
            Text(
                textoConMarcado(t(ficha.subtitulo)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Insignia(ficha.topicId)
                Insignia(etiquetaChallengeType(ficha.challengeType))
                Insignia("${ficha.evidencia.escritura}w · ${formatoOralMin(ficha.evidencia.oralMin)}")
                Insignia("${ficha.evidencia.minutosEstimados} min")
            }
        }

        Text(textoConMarcado(t(ficha.descripcion)), style = MaterialTheme.typography.bodyLarge)

        ficha.cuadroReferencia?.let { cuadro ->
            val tituloPropio = etiquetaSeccion("cuadroReferencia", ficha.idioma, ficha.bilingue)
            SeccionPlegable(titulo = t(cuadro.titulo).ifBlank { tituloPropio }) {
                CuadroReferenciaTabla(cuadro, t)
            }
        }

        Seccion(etiquetaSeccion("ejemplos", ficha.idioma, ficha.bilingue)) {
            ficha.ejemplos.forEach { Vinieta(t(it.texto)) }
        }

        Seccion(etiquetaSeccion("notas", ficha.idioma, ficha.bilingue)) {
            ficha.notas.forEach { Vinieta(t(it)) }
        }

        ficha.contraste.contrasteParaMostrar(ficha.idioma, idiomaBase)?.let { texto ->
            Seccion(etiquetaContraste(ficha.idioma, idiomaBase)) {
                Text(textoConMarcado(texto), style = MaterialTheme.typography.bodyMedium)
            }
        }

        Seccion(etiquetaSeccion("errores", ficha.idioma, ficha.bilingue)) {
            erroresParaMostrar(ficha.errores.map(t), ficha.erroresContrastivos, ficha.idioma, idiomaBase).forEach { Vinieta(it) }
        }

        SeccionPlegable(titulo = "${etiquetaSeccion("vocabulario", ficha.idioma, ficha.bilingue)} (${ficha.vocabulario.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.vocabulario.forEach { VocabularioFila(it, idiomaBase) }
            }
        }

        SeccionPlegable(titulo = "${etiquetaSeccion("redemittel", ficha.idioma, ficha.bilingue)} (${ficha.redemittel.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.redemittel.forEach { RedemittelFila(it, idiomaBase, ficha.idioma) }
            }
        }

        Seccion(etiquetaSeccion("mision", ficha.idioma, ficha.bilingue)) {
            Text(textoConMarcado(t(ficha.mision.consigna)), style = MaterialTheme.typography.bodyLarge)
            ficha.mision.requisitos.forEach { Vinieta(t(it)) }
        }

        Seccion(etiquetaSeccion("microtareas", ficha.idioma, ficha.bilingue)) {
            ficha.microtareas.forEach {
                Vinieta("${etiquetaDia(it.dia, ficha.idioma)} (${it.minutos} min) — ${t(it.texto)}")
            }
        }

        Seccion(etiquetaSeccion("autochequeo", ficha.idioma, ficha.bilingue)) {
            ficha.autochequeo.forEach { Vinieta(t(it)) }
        }

        TarjetaPrompt(t(ficha.promptCorreccion), etiquetaSeccion("promptCorreccion", ficha.idioma, ficha.bilingue))
    }
}

@Composable
private fun SemanaEspecialContenido(especial: SemanaEspecial, modifier: Modifier = Modifier) = SelectionContainer {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_SECCIONES),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(especial.titulo, style = MaterialTheme.typography.headlineSmall)
            val minutos = especial.minutosEstimados?.let { " · $it min" } ?: ""
            Text(
                "${etiquetaClase(especial.clase)} · semana ${especial.semana}$minutos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(textoConMarcado(especial.consigna), style = MaterialTheme.typography.bodyLarge)

        especial.requisitos?.takeIf { it.isNotEmpty() }?.let { requisitos ->
            Seccion(etiquetaSeccion("requisitos", especial.idioma, bilingue = false)) { requisitos.forEach { Vinieta(it) } }
        }

        especial.microtareas?.takeIf { it.isNotEmpty() }?.let { tareas ->
            Seccion(etiquetaSeccion("microtareas", especial.idioma, bilingue = false)) { tareas.forEach { Vinieta(it) } }
        }

        Seccion(etiquetaSeccion("autochequeo", especial.idioma, bilingue = false)) {
            especial.autochequeo.forEach { Vinieta(it) }
        }

        especial.promptCorreccion.takeIf { it.isNotBlank() }?.let {
            TarjetaPrompt(it, etiquetaSeccion("promptCorreccion", especial.idioma, bilingue = false))
        }
    }
}

/**
 * Jerarquia tipografica en vez de lineas divisorias -- AJUSTES-FASE-5.md,
 * B4.3, reforzado en AJUSTES-FASE-8.md, B.2: titleLarge ya distingue titulo
 * de cuerpo por tamaño, pero por defecto tiene el mismo peso (Normal) que
 * bodyMedium/bodyLarge. FontWeight.Bold suma el segundo eje de contraste
 * que pidio B.2, antes de considerar un separador.
 */
@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        contenido()
    }
}

@Composable
private fun Vinieta(texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(textoConMarcado(texto), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 4.dp))
    }
}

/** Insignia chica y neutra: metadatos de cabecera, "núcleo", variante regional, bajo nivel a propósito. */
@Composable
private fun Insignia(
    texto: String,
    contenedor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contenido: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Surface(color = contenedor, contentColor = contenido, shape = RoundedCornerShape(6.dp)) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun EtiquetaVariante(variante: String) {
    Insignia(
        "Variante regional: $variante",
        contenedor = MaterialTheme.colorScheme.tertiaryContainer,
        contenido = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}

@Composable
private fun VocabularioFila(item: VocabularioItem, idiomaBase: Idioma) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // AJUSTES-FASE-8.md, B.5: sin la etiqueta "núcleo" (nombre de
            // campo mostrado en crudo, y siempre en español -- CLAUDE.md,
            // regla dura #9). El dato se sigue usando: el peso tipografico
            // ya distinguia los items nucleo de los diez secundarios
            // (AJUSTES-FASE-5.md, B4.4), asi que alcanza con eso solo.
            Text(
                item.item,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.prioridad == Prioridad.NUCLEO) FontWeight.Bold else FontWeight.Normal,
            )
        }
        Text(
            item.traduccionParaMostrar(idiomaBase),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        item.reccion?.takeIf { it.isNotBlank() }?.let {
            Text(
                "rección: $it",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item.nota?.let {
            Text(
                textoConMarcado(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.variante != null || item.bajoNivelJustificado) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.variante?.let { Insignia(it) }
                if (item.bajoNivelJustificado) Insignia("bajo nivel, a propósito")
            }
        }
    }
}

@Composable
private fun RedemittelFila(item: RedemittelItem, idiomaBase: Idioma, idiomaAprendido: Idioma) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(item.expresion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            textoConMarcado(item.funcion.resolver(idiomaBase, idiomaAprendido)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val texto = when (val traduccion = item.traduccionParaMostrar(idiomaBase)) {
            is TraduccionRedemittel.Disponible -> traduccion.texto
            TraduccionRedemittel.SinEquivalenciaDirecta -> "Sin equivalencia directa: se aprende por situación."
            TraduccionRedemittel.SinTraducirTodavia -> "—"
        }
        Text(texto, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * Cada fila como una mini-tarjeta con "columna: valor" en vez de una grilla
 * rigida: en pantalla estrecha y con fuente grande, una grilla de 4-5
 * columnas con oraciones largas se corta o se sale. Esto envuelve el texto
 * en vez de truncarlo.
 */
@Composable
private fun CuadroReferenciaTabla(cuadro: CuadroReferencia, t: (TextoBilingue) -> String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        cuadro.filas.forEach { fila ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fila.forEachIndexed { i, celdaBilingue ->
                        val celda = t(celdaBilingue)
                        if (celda.isNotBlank()) {
                            val etiqueta = cuadro.columnas.getOrNull(i)?.let(t)
                            Text(
                                textoConMarcado(if (etiqueta != null) "$etiqueta: $celda" else celda),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
        cuadro.notaPie?.let(t)?.takeIf { it.isNotBlank() }?.let {
            Text(
                textoConMarcado(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** El unico botón que toca el sistema: copia el prompt de corrección al portapapeles. */
@Composable
private fun TarjetaPrompt(prompt: String, titulo: String) {
    val portapapeles = LocalClipboardManager.current
    var copiado by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(textoConMarcado(prompt), style = MaterialTheme.typography.bodyMedium)
            // Es la unica accion de la pantalla: boton de ancho completo,
            // no un boton mas perdido entre el resto -- AJUSTES-FASE-5.md, B4.5.
            // DisableSelection (AJUSTES-FASE-8.md, B.4): el resto de la ficha
            // es seleccionable, pero este boton no debe quedar capturado por
            // el gesto de seleccionar texto.
            DisableSelection {
                Button(
                    onClick = {
                        portapapeles.setText(AnnotatedString(prompt))
                        copiado = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (copiado) "Copiado" else "Copiar al portapapeles")
                }
            }
        }
    }
}

private fun etiquetaClase(clase: Clase): String = when (clase) {
    Clase.REVIEW -> "Semana de repaso"
    Clase.SURVIVAL -> "Semana Survival"
}

private fun etiquetaChallengeType(tipo: ChallengeType): String =
    tipo.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun formatoOralMin(oralMin: Double): String {
    val redondeado = if (oralMin == oralMin.toInt().toDouble()) oralMin.toInt().toString() else oralMin.toString()
    return "${redondeado}min oral"
}
