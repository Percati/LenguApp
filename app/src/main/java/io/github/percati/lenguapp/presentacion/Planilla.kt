package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.PlanillaTextos
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.ui.textoSinMarcado

/** Una seccion de la planilla: titulo + lineas (con vinietas o parrafo corrido). */
data class SeccionPlanilla(val titulo: String, val lineas: List<String>, val conVinietas: Boolean = true)

/** Contenido ya resuelto de la planilla del profesor, independiente de como se dibuja (ver pdf/PlanillaPdf.kt). */
data class PlanillaContenido(
    val titulo: String,
    val encabezado: String,
    val secciones: List<SeccionPlanilla>,
)

private const val MAX_EXPRESIONES_OBJETIVO = 3
private const val MAX_FILAS_CUADRO = 3

/**
 * Planilla del profesor para una semana, EN EL IDIOMA QUE SE APRENDE (la lee el
 * profesor de ese idioma). Reutiliza lo que la ficha ya tiene -- objetivo
 * (mision.consigna), topic, enfoque (subtitulo), estructura de repaso (titulo +
 * cuadro de referencia, sin la explicacion larga) y las primeras 3 expresiones
 * (redemittel) --; solo los textos fijos (secciones, 2 frases de reparacion, las
 * dos situaciones, el giro inesperado, los 3 criterios y el recordatorio) vienen
 * del JSON de textos. El giro es una instruccion generica al profesor: no hay
 * preguntas escritas. `nombreTopic` solo existe en espanol (banco.json): si no
 * hay, se muestra el id del topic.
 */
fun construirPlanilla(contenido: ContenidoSemanal, textos: PlanillaTextos, nombreTopic: String?): PlanillaContenido =
    when (contenido) {
        is Ficha -> construirPlanillaFicha(contenido, textos, nombreTopic)
        is SemanaEspecial -> construirPlanillaEspecial(contenido, textos)
    }

private fun limpio(texto: String): String = textoSinMarcado(texto).trim()

private fun TextoBilingue.enAprendido(idioma: Idioma): String = limpio(resolver(idioma, idioma))

private fun construirPlanillaFicha(ficha: Ficha, textos: PlanillaTextos, nombreTopic: String?): PlanillaContenido {
    val aprendido = ficha.idioma
    val gramatica = buildList {
        add(ficha.titulo.enAprendido(aprendido))
        ficha.cuadroReferencia?.let { cuadro ->
            cuadro.titulo.enAprendido(aprendido).takeIf { it.isNotEmpty() }?.let(::add)
            cuadro.filas.take(MAX_FILAS_CUADRO).forEach { fila ->
                fila.map { it.enAprendido(aprendido) }.filter { it.isNotEmpty() }.joinToString(" · ")
                    .takeIf { it.isNotEmpty() }?.let(::add)
            }
        }
    }
    val expresiones = ficha.redemittel.take(MAX_EXPRESIONES_OBJETIVO).map { item ->
        val funcion = item.funcion.enAprendido(aprendido)
        if (funcion.isEmpty()) item.expresion.trim() else "${item.expresion.trim()} — $funcion"
    }
    val secciones = listOfNotNull(
        SeccionPlanilla(textos.objetivo, listOf(ficha.mision.consigna.enAprendido(aprendido)), conVinietas = false),
        SeccionPlanilla(
            textos.tema,
            listOf(nombreTopic?.takeIf { it.isNotBlank() } ?: ficha.topicId, ficha.subtitulo.enAprendido(aprendido))
                .filter { it.isNotEmpty() },
        ),
        SeccionPlanilla(textos.gramatica, gramatica.filter { it.isNotEmpty() }).takeIf { gramatica.isNotEmpty() },
        SeccionPlanilla(textos.expresionesObjetivo, expresiones).takeIf { expresiones.isNotEmpty() },
        SeccionPlanilla(textos.expresionesReparacion, textos.frasesReparacion),
    ) + seccionesComunes(textos)
    return PlanillaContenido(
        titulo = textos.titulo,
        encabezado = "${ficha.titulo.enAprendido(aprendido)} · ${textos.nivel} ${ficha.nivel.name}",
        secciones = secciones,
    )
}

private fun construirPlanillaEspecial(especial: SemanaEspecial, textos: PlanillaTextos): PlanillaContenido {
    val aprendido = especial.idioma
    val pasos = (especial.requisitos.orEmpty() + especial.microtareas.orEmpty()).map { it.enAprendido(aprendido) }
        .filter { it.isNotEmpty() }
    val secciones = listOfNotNull(
        SeccionPlanilla(textos.objetivo, listOf(especial.consigna.enAprendido(aprendido)), conVinietas = false),
        SeccionPlanilla(textos.enfoque, pasos).takeIf { pasos.isNotEmpty() },
        SeccionPlanilla(textos.expresionesReparacion, textos.frasesReparacion),
    ) + seccionesComunes(textos)
    return PlanillaContenido(
        titulo = textos.titulo,
        encabezado = "${especial.titulo.enAprendido(aprendido)} · ${textos.nivel} ${especial.nivel.name} · ${textos.semana} ${especial.semana}",
        secciones = secciones,
    )
}

private fun seccionesComunes(textos: PlanillaTextos): List<SeccionPlanilla> = listOf(
    SeccionPlanilla(textos.situaciones, listOf(textos.situacionCotidiana, textos.situacionProfesional)),
    SeccionPlanilla(textos.giro, listOf(textos.giroTexto), conVinietas = false),
    SeccionPlanilla(textos.criterios, textos.criteriosLista),
    SeccionPlanilla("", listOf(textos.recordatorio), conVinietas = false),
)
