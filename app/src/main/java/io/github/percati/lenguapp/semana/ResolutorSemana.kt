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
 * Que se encontro al buscar el calendario embebido de (anioIso, idioma,
 * nivel). Lo arma CargadorCalendario listando `assets/calendario/`: si el
 * archivo exacto no esta pero hay otros del mismo anio, la celda (idioma,
 * nivel) es la que falta; si no hay ninguno del anio, el anio entero todavia
 * no tiene calendario generado (ej.: 2027).
 */
sealed interface CalendarioCargado {
    data class Encontrado(val entradas: List<EntradaCalendario>) : CalendarioCargado
    data object SinCalendarioParaIdiomaONivel : CalendarioCargado
    data object SinCalendarioParaElAnio : CalendarioCargado
}

/** Nombre exacto del archivo embebido para (anioIso, idioma, nivel). Lo generan generarCalendarioAssets y CargadorCalendario a partir de la misma funcion. */
fun nombreArchivoCalendario(anioIso: Int, idioma: Idioma, nivel: Nivel): String =
    "calendario_${anioIso}_${idioma.name.lowercase()}_${nivel.name}.json"

/**
 * Pura: dado el listado de archivos que hay en `assets/calendario/`, decide
 * cual de las tres situaciones aplica, sin leer ni parsear ningun archivo
 * todavia. CargadorCalendario (Android) y los tests JVM llaman a esta misma
 * funcion para no duplicar el criterio de clasificacion.
 */
fun clasificarDisponibilidad(disponibles: Set<String>, anioIso: Int, idioma: Idioma, nivel: Nivel): DisponibilidadCalendario {
    val nombre = nombreArchivoCalendario(anioIso, idioma, nivel)
    return when {
        nombre in disponibles -> DisponibilidadCalendario.PRESENTE
        disponibles.any { it.startsWith("calendario_${anioIso}_") } -> DisponibilidadCalendario.SIN_CALENDARIO_PARA_IDIOMA_O_NIVEL
        else -> DisponibilidadCalendario.SIN_CALENDARIO_PARA_EL_ANIO
    }
}

enum class DisponibilidadCalendario { PRESENTE, SIN_CALENDARIO_PARA_IDIOMA_O_NIVEL, SIN_CALENDARIO_PARA_EL_ANIO }

private val PATRON_NOMBRE_CALENDARIO = Regex("""^calendario_\d{4}_([a-z]{2})_[A-Z]\d\.json$""")

/**
 * Que idiomas tienen contenido, en cualquier anio o nivel. Se deriva de los
 * nombres de archivo de `assets/calendario/`, nunca se escribe a mano
 * (CLAUDE.md, regla dura #10): cuando se agreguen fichas de un idioma
 * nuevo, su calendario aparece solo y esto lo recoge sin tocar codigo.
 */
fun idiomasConContenido(nombresCalendario: Collection<String>): Set<Idioma> =
    nombresCalendario
        .mapNotNull { PATRON_NOMBRE_CALENDARIO.matchEntire(it)?.groupValues?.get(1) }
        .mapNotNull { codigo -> runCatching { Idioma.valueOf(codigo.uppercase()) }.getOrNull() }
        .toSet()

/** Por que no hay contenido esta semana. La Fase 4 se lo dice al usuario segun este motivo. */
enum class RazonSinContenido {
    /** Ningun calendario embebido para este anio ISO, en ningun idioma ni nivel (ej.: 2027 todavia). */
    ANIO_SIN_CALENDARIO,

    /** El anio tiene calendario para otros (idioma, nivel), pero no para este. */
    IDIOMA_O_NIVEL_SIN_CONTENIDO,

    /** El calendario de (idioma, nivel) existe, pero esta semana no forma parte de la edicion (antes de "--desde", o la pieza referenciada no esta en el contenido embebido). */
    SEMANA_FUERA_DE_LA_EDICION,
}

/**
 * Resultado de pedir el contenido de una semana. SinContenido no es un error:
 * es el caso normal para casi toda la matriz (idioma, nivel, anio) — ver
 * CLAUDE.md, "Estado del contenido".
 */
sealed interface ResultadoSemana {
    data class Encontrado(val semanaIso: SemanaIso, val contenido: ContenidoSemanal) : ResultadoSemana
    data class SinContenido(
        val semanaIso: SemanaIso,
        val idioma: Idioma,
        val nivel: Nivel,
        val razon: RazonSinContenido,
    ) : ResultadoSemana
}

/** `contenidoPorId` es todo el contenido cargado de assets, indexado por [ContenidoSemanal.id]. */
fun resolverContenidoDeLaSemana(
    fecha: LocalDate,
    idioma: Idioma,
    nivel: Nivel,
    calendario: CalendarioCargado,
    contenidoPorId: Map<String, ContenidoSemanal>,
): ResultadoSemana {
    val semanaIso = semanaIsoDe(fecha)
    fun sinContenido(razon: RazonSinContenido) = ResultadoSemana.SinContenido(semanaIso, idioma, nivel, razon)

    return when (calendario) {
        is CalendarioCargado.SinCalendarioParaElAnio -> sinContenido(RazonSinContenido.ANIO_SIN_CALENDARIO)
        is CalendarioCargado.SinCalendarioParaIdiomaONivel -> sinContenido(RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO)
        is CalendarioCargado.Encontrado -> {
            val entrada = calendario.entradas.firstOrNull { it.semana == semanaIso.semana }
            val contenido = entrada?.let { contenidoPorId[idDeEntrada(it, idioma, nivel)] }
            if (contenido != null) {
                ResultadoSemana.Encontrado(semanaIso, contenido)
            } else {
                sinContenido(RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION)
            }
        }
    }
}

/**
 * El mismo esquema de id que arma compilar_fichas.py al compilar cada pieza.
 * REVIEW y SURVIVAL llevan el nivel ademas del idioma: un mismo idioma puede
 * tener mas de un nivel con calendario propio (ingles B2 y C1 en 2026), y la
 * semana de repaso o Survival de cada nivel es contenido distinto, no el
 * mismo texto para los dos.
 */
private fun idDeEntrada(entrada: EntradaCalendario, idioma: Idioma, nivel: Nivel): String =
    when (entrada.tipo) {
        TipoSemana.CONTENT -> "${entrada.skillId}-${nivel.name}-${entrada.order}"
        TipoSemana.REVIEW -> "REVIEW-${idioma.name}-${nivel.name}-S${entrada.semana}"
        TipoSemana.SURVIVAL -> "SURVIVAL-${idioma.name}-${nivel.name}-S${entrada.semana}"
    }
