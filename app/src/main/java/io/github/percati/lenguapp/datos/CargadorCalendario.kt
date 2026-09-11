package io.github.percati.lenguapp.datos

import android.content.Context
import io.github.percati.lenguapp.modelo.EntradaCalendario
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.DisponibilidadCalendario
import io.github.percati.lenguapp.semana.clasificarDisponibilidad
import io.github.percati.lenguapp.semana.nombreArchivoCalendario

private const val CARPETA_CALENDARIO = "calendario"

/** Sin dependencia de Context: usable desde tests JVM comunes. */
fun parsearCalendario(texto: String): List<EntradaCalendario> =
    jsonContenido.decodeFromString(texto)

/** Nombres tal cual estan en `assets/calendario/`. Base para derivar que idiomas tienen contenido (ver semana/idiomasConContenido). */
fun nombresCalendarioDisponibles(context: Context): List<String> =
    context.assets.list(CARPETA_CALENDARIO)?.toList() ?: emptyList()

/**
 * Distingue "no hay calendario para este (idioma, nivel)" de "no hay ningun
 * calendario para este anio": la Fase 4 necesita decirle al usuario cual de
 * los dos es, no solo que "no hay nada". clasificarDisponibilidad() es la
 * misma funcion que usan los tests JVM contra los assets reales.
 */
fun cargarCalendarioDesdeAssets(context: Context, anioIso: Int, idioma: Idioma, nivel: Nivel): CalendarioCargado {
    val disponibles = nombresCalendarioDisponibles(context)
    return when (clasificarDisponibilidad(disponibles.toSet(), anioIso, idioma, nivel)) {
        DisponibilidadCalendario.SIN_CALENDARIO_PARA_EL_ANIO -> CalendarioCargado.SinCalendarioParaElAnio
        DisponibilidadCalendario.SIN_CALENDARIO_PARA_IDIOMA_O_NIVEL -> CalendarioCargado.SinCalendarioParaIdiomaONivel
        DisponibilidadCalendario.PRESENTE -> {
            val nombre = nombreArchivoCalendario(anioIso, idioma, nivel)
            val texto = context.assets.open("$CARPETA_CALENDARIO/$nombre").bufferedReader().use { it.readText() }
            CalendarioCargado.Encontrado(parsearCalendario(texto))
        }
    }
}
