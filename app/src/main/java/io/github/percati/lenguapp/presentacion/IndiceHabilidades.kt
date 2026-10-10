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
    /** La ficha que abre un toque: la proxima aparicion desde la semana de referencia, o la ultima si ya pasaron todas. */
    val fichaAAbrir: String,
) {
    val semanas: List<Int> get() = apariciones.map { it.semana }
}

sealed interface IndiceHabilidades {
    data class Filas(val idioma: Idioma, val nivel: Nivel, val anio: Int, val filas: List<FilaHabilidad>) : IndiceHabilidades

    /** No hay calendario embebido para ese anio / idioma / nivel (mismo criterio que la pantalla de la semana). */
    data class SinCalendario(val anio: Int) : IndiceHabilidades
}

/**
 * Habilidades de `calendario` (del anio ISO `anio`) para las que HAY ficha en `contenidoPorId`
 * (una habilidad sin ficha en el nivel activo no aparece), ordenadas por titulo.
 */
fun construirIndiceHabilidades(
    calendario: CalendarioCargado,
    anio: Int,
    idioma: Idioma,
    nivel: Nivel,
    contenidoPorId: Map<String, ContenidoSemanal>,
    idiomaBase: Idioma,
    semanaReferencia: Int,
): IndiceHabilidades {
    val entradas = (calendario as? CalendarioCargado.Encontrado)?.entradas ?: return IndiceHabilidades.SinCalendario(anio)
    val porHabilidad = LinkedHashMap<String, MutableList<AparicionHabilidad>>()
    val fichas = HashMap<String, Ficha>()
    for (entrada in entradas.filter { it.tipo == TipoSemana.CONTENT && it.skillId != null }.sortedBy { it.semana }) {
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
            fichaAAbrir = (apariciones.firstOrNull { it.semana >= semanaReferencia } ?: apariciones.last()).fichaId,
        )
    }.sortedBy { it.titulo.lowercase() }
    return IndiceHabilidades.Filas(idioma, nivel, anio, filas)
}
