package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoSemana
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.idContenidoDeEntrada

/**
 * Indice de habilidades (Ronda F2, tarea 7): lista de SOLO LECTURA de las habilidades del idioma
 * y nivel activos, con las semanas del anio en que aparece cada una, para volver a una concreta
 * sin recorrer semanas. Sin estado persistente (regla dura 4): nada de "vista" o "hecho".
 */
data class AparicionHabilidad(val semana: Int, val fichaId: String)

data class FilaHabilidad(
    val skillId: String,
    /** El titulo de la ficha segun la regla de idioma de la ficha (A2/B1: idioma de app; B2+: el que se aprende). */
    val titulo: String,
    /** Semanas del anio (orden de calendario) con el id de la ficha de cada una. */
    val apariciones: List<AparicionHabilidad>,
    /** La ficha que abre un toque: SIEMPRE la ultima aparicion de las que se muestran (que, sin semanas futuras, es la ultima ya pasada). */
    val fichaAAbrir: String,
) {
    val semanas: List<Int> get() = apariciones.map { it.semana }

    /** La primera aparicion que se muestra: por ella se ordena «Por semana». */
    val primeraSemana: Int get() = apariciones.first().semana
}

/** Como se ordena la lista de habilidades. Alfabetico (por titulo, en el idioma que se aprende) es el de siempre y el de cada entrada. */
enum class OrdenHabilidades { ALFABETICO, POR_SEMANA }

/** `filas` en el orden pedido: por titulo, o por la primera aparicion (y por titulo si dos empiezan la misma semana). */
fun List<FilaHabilidad>.ordenadas(orden: OrdenHabilidades): List<FilaHabilidad> = when (orden) {
    OrdenHabilidades.ALFABETICO -> sortedBy { it.titulo.lowercase() }
    OrdenHabilidades.POR_SEMANA -> sortedWith(compareBy<FilaHabilidad> { it.primeraSemana }.thenBy { it.titulo.lowercase() })
}

sealed interface IndiceHabilidades {
    data class Filas(val idioma: Idioma, val nivel: Nivel, val anio: Int, val filas: List<FilaHabilidad>) : IndiceHabilidades

    /** No hay calendario embebido para ese anio / idioma / nivel (mismo criterio que la pantalla de la semana). */
    data class SinCalendario(val anio: Int) : IndiceHabilidades
}

/**
 * Habilidades de `calendario` (del anio ISO `anio`) para las que HAY ficha en `contenidoPorId`
 * (una habilidad sin ficha en el nivel activo no aparece), ordenadas por titulo.
 *
 * SIN SEMANAS FUTURAS (Ronda T): la app es una revista y la semana manda. Si `anio` es ANTERIOR al anio en curso (`anioEnCurso`, lo unico
 * que se toma de la fecha del sistema), la edicion esta cerrada y se muestra entera; si es el anio en curso (o uno posterior), solo las
 * apariciones con `semana <= semanaReferencia`, y una habilidad cuya primera aparicion es futura no aparece. En la semana 2 de un anio la
 * lista tiene una o dos entradas: es lo coherente, y no se rellena con el anio anterior.
 */
fun construirIndiceHabilidades(
    calendario: CalendarioCargado,
    anio: Int,
    idioma: Idioma,
    nivel: Nivel,
    contenidoPorId: Map<String, ContenidoSemanal>,
    idiomaBase: Idioma,
    semanaReferencia: Int,
    anioEnCurso: Int,
): IndiceHabilidades {
    val entradas = (calendario as? CalendarioCargado.Encontrado)?.entradas ?: return IndiceHabilidades.SinCalendario(anio)
    val ultimaSemanaVisible = if (anio < anioEnCurso) Int.MAX_VALUE else semanaReferencia
    val porHabilidad = LinkedHashMap<String, MutableList<AparicionHabilidad>>()
    val fichas = HashMap<String, Ficha>()
    for (entrada in entradas.filter { it.tipo == TipoSemana.CONTENT && it.skillId != null && it.semana <= ultimaSemanaVisible }.sortedBy { it.semana }) {
        val id = idContenidoDeEntrada(entrada, idioma, nivel, anio)
        val ficha = contenidoPorId[id] as? Ficha ?: continue
        porHabilidad.getOrPut(entrada.skillId!!) { mutableListOf() } += AparicionHabilidad(entrada.semana, id)
        fichas.putIfAbsent(entrada.skillId, ficha)
    }
    val filas = porHabilidad.map { (skillId, apariciones) ->
        val ficha = fichas.getValue(skillId)
        val mostrado = idiomaMostradoDeFicha(ficha.bilingue, idioma, idiomaBase)
        FilaHabilidad(
            skillId = skillId,
            titulo = ficha.titulo.resolver(mostrado, idioma).trim(),
            apariciones = apariciones,
            // Siempre la ultima de las que se muestran (ya pasada): es la que mas contenido tiene a la vista, y no depende de la semana de hoy.
            fichaAAbrir = apariciones.last().fichaId,
        )
    }.sortedBy { it.titulo.lowercase() }
    return IndiceHabilidades.Filas(idioma, nivel, anio, filas)
}
