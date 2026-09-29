package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import io.github.percati.lenguapp.semana.semanaIsoDe
import java.time.LocalDate

/**
 * Punto de entrada para la pantalla: dada la fecha del dispositivo (unico
 * dato que se lee), devuelve el contenido de esa semana o, si la matriz
 * (idioma, nivel, anio) no tiene nada ahi, ResultadoSemana.SinContenido.
 *
 * `contenidoPorId` se recibe ya cargado (ver MainActivity.kt: se arma UNA
 * vez en onCreate con cargarContenidoDesdeAssets(this).associateBy { it.id })
 * en vez de leerse y reparsearse aca. Antes esta funcion releia y
 * reparseaba los 662 JSON de `assets/contenido/` (13 MB, I/O sincronico
 * sobre AssetManager) en cada llamada -- y se llama en cada cambio de
 * semana, cada cambio de idioma activo y cada vuelta de Ajustes a la
 * pantalla principal, porque el resultado vive en un `remember` de
 * MainActivity.kt que se descarta cuando esa composicion se destruye. Era
 * la causa real de la lentitud reportada, no el tamaño del contenido (13 MB
 * no es mucho) ni Compose recomponiendo de mas: perfilado confirmando que
 * el I/O+parseo esta en el camino caliente de las tres transiciones.
 */
fun resolverSemana(
    context: Context,
    contenidoPorId: Map<String, ContenidoSemanal>,
    idioma: Idioma,
    nivel: Nivel,
    fecha: LocalDate = LocalDate.now(),
): ResultadoSemana {
    val anioIso = semanaIsoDe(fecha).anio
    val calendario = cargarCalendarioDesdeAssets(context, anioIso, idioma, nivel)
    return resolverContenidoDeLaSemana(fecha, idioma, nivel, calendario, contenidoPorId)
}
