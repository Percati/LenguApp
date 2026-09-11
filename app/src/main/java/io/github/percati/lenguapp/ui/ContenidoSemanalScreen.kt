package io.github.percati.lenguapp.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.presentacion.TraduccionRedemittel
import io.github.percati.lenguapp.presentacion.contrasteParaMostrar
import io.github.percati.lenguapp.presentacion.traduccionParaMostrar
import io.github.percati.lenguapp.semana.RazonSinContenido
import io.github.percati.lenguapp.semana.ResultadoSemana

/**
 * Punto de entrada de la pantalla unica: la Fase 4 agrega navegacion y
 * ajustes, pero el "no hay nada para esta celda de la matriz" es el caso
 * normal desde ahora (ver CLAUDE.md) y no puede dejar la pantalla en blanco
 * ni romper la app.
 */
@Composable
fun PantallaSemana(resultado: ResultadoSemana, idiomaBase: Idioma, modifier: Modifier = Modifier) {
    when (resultado) {
        is ResultadoSemana.Encontrado -> ContenidoSemanalScreen(resultado.contenido, idiomaBase, modifier)
        is ResultadoSemana.SinContenido -> SinContenidoMensaje(resultado, modifier)
    }
}

@Composable
private fun SinContenidoMensaje(sinContenido: ResultadoSemana.SinContenido, modifier: Modifier = Modifier) {
    val mensaje = when (sinContenido.razon) {
        RazonSinContenido.ANIO_SIN_CALENDARIO ->
            "Todavía no hay calendario para el año ${sinContenido.semanaIso.anio}."
        RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO ->
            "Todavía no hay contenido para ${sinContenido.idioma.name} en nivel ${sinContenido.nivel.name}."
        RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION ->
            "La semana ${sinContenido.semanaIso.semana} de ${sinContenido.semanaIso.anio} no forma parte de la edición actual."
    }
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text("Sin contenido esta semana", style = MaterialTheme.typography.headlineSmall)
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

@Composable
private fun FichaContenido(ficha: Ficha, idiomaBase: Idioma, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ficha.variante?.let { EtiquetaVariante(it) }
            Text(ficha.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(
                ficha.subtitulo,
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

        Text(textoConMarcado(ficha.descripcion), style = MaterialTheme.typography.bodyLarge)

        ficha.cuadroReferencia?.let { cuadro ->
            val tituloPropio = etiquetaSeccion("cuadroReferencia", ficha.idioma, ficha.bilingue)
            SeccionPlegable(titulo = cuadro.titulo.ifBlank { tituloPropio }) {
                CuadroReferenciaTabla(cuadro)
            }
        }

        Seccion(etiquetaSeccion("ejemplos", ficha.idioma, ficha.bilingue)) {
            ficha.ejemplos.forEach { Vinieta(it.texto) }
        }

        Seccion(etiquetaSeccion("notas", ficha.idioma, ficha.bilingue)) {
            ficha.notas.forEach { Vinieta(it) }
        }

        ficha.contraste.contrasteParaMostrar(idiomaBase)?.let { texto ->
            Seccion(etiquetaContraste(ficha.idioma, idiomaBase)) {
                Text(textoConMarcado(texto), style = MaterialTheme.typography.bodyMedium)
            }
        }

        Seccion(etiquetaSeccion("errores", ficha.idioma, ficha.bilingue)) {
            ficha.errores.forEach { Vinieta(it) }
        }

        SeccionPlegable(titulo = "${etiquetaSeccion("vocabulario", ficha.idioma, ficha.bilingue)} (${ficha.vocabulario.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.vocabulario.forEach { VocabularioFila(it, idiomaBase) }
            }
        }

        SeccionPlegable(titulo = "${etiquetaSeccion("redemittel", ficha.idioma, ficha.bilingue)} (${ficha.redemittel.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.redemittel.forEach { RedemittelFila(it, idiomaBase) }
            }
        }

        Seccion(etiquetaSeccion("mision", ficha.idioma, ficha.bilingue)) {
            Text(textoConMarcado(ficha.mision.consigna), style = MaterialTheme.typography.bodyLarge)
            ficha.mision.requisitos.forEach { Vinieta(it) }
        }

        Seccion(etiquetaSeccion("microtareas", ficha.idioma, ficha.bilingue)) {
            ficha.microtareas.forEach {
                Vinieta("${etiquetaDia(it.dia, ficha.idioma)} (${it.minutos} min) — ${it.texto}")
            }
        }

        Seccion(etiquetaSeccion("autochequeo", ficha.idioma, ficha.bilingue)) {
            ficha.autochequeo.forEach { Vinieta(it) }
        }

        TarjetaPrompt(ficha.promptCorreccion, etiquetaSeccion("promptCorreccion", ficha.idioma, ficha.bilingue))
    }
}

@Composable
private fun SemanaEspecialContenido(especial: SemanaEspecial, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
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
 * B4.3: titleLarge contra bodyMedium/bodyLarge ya distingue titulo de
 * cuerpo sin necesitar una regla horizontal.
 */
@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge)
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
            Text(
                item.item,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.prioridad == Prioridad.NUCLEO) FontWeight.Bold else FontWeight.Normal,
            )
            // Los cuatro items nucleo se distinguen de los diez secundarios,
            // no solo por el peso de fuente -- AJUSTES-FASE-5.md, B4.4.
            if (item.prioridad == Prioridad.NUCLEO) {
                Insignia(
                    "núcleo",
                    contenedor = MaterialTheme.colorScheme.primaryContainer,
                    contenido = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
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
private fun RedemittelFila(item: RedemittelItem, idiomaBase: Idioma) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(item.expresion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(
            textoConMarcado(item.funcion),
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
private fun CuadroReferenciaTabla(cuadro: CuadroReferencia) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        cuadro.filas.forEach { fila ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fila.forEachIndexed { i, celda ->
                        if (celda.isNotBlank()) {
                            val etiqueta = cuadro.columnas.getOrNull(i)
                            Text(
                                textoConMarcado(if (etiqueta != null) "$etiqueta: $celda" else celda),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
        cuadro.notaPie?.takeIf { it.isNotBlank() }?.let {
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
