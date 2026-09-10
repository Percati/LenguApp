package io.github.percati.lenguapp.semana

import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.EntradaCalendario
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoSemana
import java.time.LocalDate
import java.time.temporal.IsoFields

/**
 * (anioIso, semanaIso) de una fecha. El anio ISO puede no coincidir con el
 * anio de calendario en los bordes de diciembre/enero: la semana 53 de 2026
 * va del 28/12/2026 al 3/1/2027, y el 1/1/2027 sigue siendo semana 53 de
 * 2026. Por eso esto usa la API nativa y nunca se calcula a mano.
 */
data class SemanaIso(val anio: Int, val semana: Int)

fun semanaIsoDe(fecha: LocalDate): SemanaIso = SemanaIso(
    anio = fecha.get(IsoFields.WEEK_BASED_YEAR),
    semana = fecha.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
)

/**
 * Resultado de pedir el contenido de una semana. SinContenido no es un error:
 * es el caso normal para casi toda la matriz (idioma, nivel, anio) — ver
 * CLAUDE.md, "Estado del contenido".
 */
sealed interface ResultadoSemana {
    data class Encontrado(val semanaIso: SemanaIso, val contenido: ContenidoSemanal) : ResultadoSemana
    data class SinContenido(val semanaIso: SemanaIso, val idioma: Idioma, val nivel: Nivel) : ResultadoSemana
}

/**
 * `calendario` es nulo si no hay calendario embebido para (anioIso, idioma,
 * nivel) — el caso normal fuera de 2026/de-B2 y 2026/en-C1. `contenidoPorId`
 * es todo el contenido cargado de assets, indexado por [ContenidoSemanal.id].
 */
fun resolverContenidoDeLaSemana(
    fecha: LocalDate,
    idioma: Idioma,
    nivel: Nivel,
    calendario: List<EntradaCalendario>?,
    contenidoPorId: Map<String, ContenidoSemanal>,
): ResultadoSemana {
    val semanaIso = semanaIsoDe(fecha)
    val entrada = calendario?.firstOrNull { it.semana == semanaIso.semana }
    val contenido = entrada?.let { contenidoPorId[idDeEntrada(it, idioma, nivel)] }
    return if (contenido != null) {
        ResultadoSemana.Encontrado(semanaIso, contenido)
    } else {
        ResultadoSemana.SinContenido(semanaIso, idioma, nivel)
    }
}

/** El mismo esquema de id que arma compilar_fichas.py al compilar cada pieza. */
private fun idDeEntrada(entrada: EntradaCalendario, idioma: Idioma, nivel: Nivel): String =
    when (entrada.tipo) {
        TipoSemana.CONTENT -> "${entrada.skillId}-${nivel.name}-${entrada.order}"
        TipoSemana.REVIEW -> "REVIEW-${idioma.name}-S${entrada.semana}"
        TipoSemana.SURVIVAL -> "SURVIVAL-${idioma.name}-S${entrada.semana}"
    }
