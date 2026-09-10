package io.github.percati.lenguapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Clase
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.CuadroReferencia
import io.github.percati.lenguapp.modelo.Dia
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
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ficha.variante?.let { EtiquetaVariante(it) }
            Text(ficha.titulo, style = MaterialTheme.typography.headlineSmall)
            Text(
                ficha.subtitulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "Tema ${ficha.topicId} · ${ficha.evidencia.minutosEstimados} min",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(ficha.descripcion, style = MaterialTheme.typography.bodyLarge)

        ficha.cuadroReferencia?.let { cuadro ->
            SeccionPlegable(titulo = cuadro.titulo.ifBlank { "Cuadro de referencia" }) {
                CuadroReferenciaTabla(cuadro)
            }
        }

        Seccion("Ejemplos") {
            ficha.ejemplos.forEach { Vinieta(it.texto) }
        }

        Seccion("Para tener en cuenta") {
            ficha.notas.forEach { Vinieta(it) }
        }

        ficha.contraste.contrasteParaMostrar(idiomaBase)?.let { texto ->
            Seccion("Contraste con tu lengua") {
                Text(texto, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Seccion("Errores comunes") {
            ficha.errores.forEach { Vinieta(it) }
        }

        SeccionPlegable(titulo = "Vocabulario (${ficha.vocabulario.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.vocabulario.forEach { VocabularioFila(it, idiomaBase) }
            }
        }

        SeccionPlegable(titulo = "Redemittel (${ficha.redemittel.size})") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ficha.redemittel.forEach { RedemittelFila(it, idiomaBase) }
            }
        }

        Seccion("Misión de la semana") {
            Text(ficha.mision.consigna, style = MaterialTheme.typography.bodyLarge)
            ficha.mision.requisitos.forEach { Vinieta(it) }
        }

        Seccion("Micro-tareas") {
            ficha.microtareas.forEach {
                Vinieta("${etiquetaDia(it.dia)} (${it.minutos} min) — ${it.texto}")
            }
        }

        Seccion("Autochequeo") {
            ficha.autochequeo.forEach { Vinieta(it) }
        }

        TarjetaPrompt(ficha.promptCorreccion)
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

        Text(especial.consigna, style = MaterialTheme.typography.bodyLarge)

        especial.requisitos?.takeIf { it.isNotEmpty() }?.let { requisitos ->
            Seccion("Requisitos") { requisitos.forEach { Vinieta(it) } }
        }

        especial.microtareas?.takeIf { it.isNotEmpty() }?.let { tareas ->
            Seccion("Micro-tareas") { tareas.forEach { Vinieta(it) } }
        }

        Seccion("Autochequeo") {
            especial.autochequeo.forEach { Vinieta(it) }
        }

        especial.promptCorreccion.takeIf { it.isNotBlank() }?.let { TarjetaPrompt(it) }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        contenido()
    }
}

@Composable
private fun Vinieta(texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 4.dp))
    }
}

@Composable
private fun EtiquetaVariante(variante: String) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            "Variante regional: $variante",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun VocabularioFila(item: VocabularioItem, idiomaBase: Idioma) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            item.item,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (item.prioridad == Prioridad.NUCLEO) FontWeight.Bold else FontWeight.Normal,
        )
        Text(
            item.traduccionParaMostrar(idiomaBase),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        listOfNotNull(
            item.reccion?.takeIf { it.isNotBlank() && it != "—" }?.let { "rección: $it" },
            item.variante?.let { "variante: $it" },
            item.nota,
        ).forEach { detalle ->
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RedemittelFila(item: RedemittelItem, idiomaBase: Idioma) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(item.expresion, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Text(item.funcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                if (etiqueta != null) "$etiqueta: $celda" else celda,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
        cuadro.notaPie?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** El unico botón que toca el sistema: copia el prompt de corrección al portapapeles. */
@Composable
private fun TarjetaPrompt(prompt: String) {
    val portapapeles = LocalClipboardManager.current
    var copiado by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Prompt de corrección", style = MaterialTheme.typography.titleMedium)
            Text(prompt, style = MaterialTheme.typography.bodyMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {
                    portapapeles.setText(AnnotatedString(prompt))
                    copiado = true
                }) {
                    Text(if (copiado) "Copiado" else "Copiar al portapapeles")
                }
            }
        }
    }
}

private fun etiquetaDia(dia: Dia): String = when (dia) {
    Dia.LUN -> "Lunes"
    Dia.MIE -> "Miércoles"
    Dia.VIE -> "Viernes"
}

private fun etiquetaClase(clase: Clase): String = when (clase) {
    Clase.REVIEW -> "Semana de repaso"
    Clase.SURVIVAL -> "Semana Survival"
}
