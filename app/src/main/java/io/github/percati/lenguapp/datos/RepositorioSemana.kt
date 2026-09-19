package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import io.github.percati.lenguapp.semana.semanaIsoDe
import java.time.LocalDate

/**
 * Punto de entrada para la pantalla: dada la fecha del dispositivo (unico
 * dato que se lee), devuelve el contenido de esa semana o, si la matriz
 * (idioma, nivel, anio) no tiene nada ahi, ResultadoSemana.SinContenido.
 */
fun resolverSemana(
    context: Context,
    idioma: Idioma,
    nivel: Nivel,
    fecha: LocalDate = LocalDate.now(),
): ResultadoSemana {
    val anioIso = semanaIsoDe(fecha).anio
    val calendario = cargarCalendarioDesdeAssets(context, anioIso, idioma, nivel)
    val contenidoPorId = cargarContenidoDesdeAssets(context).associateBy { it.id }
    return resolverContenidoDeLaSemana(fecha, idioma, nivel, calendario, contenidoPorId)
}
