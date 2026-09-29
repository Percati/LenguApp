package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate
import kotlin.system.measureNanoTime

/**
 * Medicion objetiva del bug de lentitud reportado (ver RepositorioSemana.kt):
 * resolverSemana() releia y reparseaba los 662 JSON de assets/contenido/
 * (13 MB) en CADA cambio de semana, de idioma activo, o vuelta de Ajustes --
 * porque el resultado vivia en un remember() que se destruye con esa
 * composicion. La correccion es cargar y parsear una sola vez (MainActivity
 * onCreate) y pasar el mapa ya armado.
 *
 * Este test no mide AssetManager (eso requeriria Robolectric + instrumentado
 * en dispositivo), pero si mide el costo real y portable: leer+parsear los
 * 662 JSON reales embebidos. Sirve como numero objetivo y como guarda de
 * regresion: si alguien vuelve a poner el parseo adentro del camino de
 * resolucion, este test lo nota (repetirlo N veces tarda casi lo mismo que
 * una vez sola, en vez de acercarse a cero).
 */
class RepositorioSemanaPerfTest {

    private fun carpetaAssets(): File {
        val candidatos = listOf(File("src/main/assets"), File("app/src/main/assets"))
        return candidatos.firstOrNull { it.isDirectory } ?: error("no se encontro app/src/main/assets")
    }

    private fun cargarYParsearTodo(): Map<String, ContenidoSemanal> =
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }

    @Test
    fun `parsear el mapa una vez y resolver N veces es muchisimo mas rapido que reparsear en cada resolucion`() {
        val nEmulaTransiciones = 20

        // "Antes": releer y reparsear los 662 JSON en cada una de las 20
        // transiciones simuladas (semana adelante/atras, cambio de idioma,
        // volver de Ajustes -- todas terminaban en un resolverSemana() que
        // volvia a leer todo).
        val tiempoReparseandoCadaVez = measureNanoTime {
            repeat(nEmulaTransiciones) { cargarYParsearTodo() }
        }

        // "Ahora": parsear una vez (MainActivity.onCreate) y resolver
        // (busqueda en mapa + logica pura, sin I/O) las mismas 20 veces --
        // con un calendario real, para que de verdad pase por
        // contenidoPorId[idDeEntrada(...)] y no solo por la rama "sin calendario".
        val contenidoPorId = cargarYParsearTodo()
        val calendario = CalendarioCargado.Encontrado(
            parsearCalendario(File(carpetaAssets(), "calendario/calendario_2026_de_B2.json").readText()),
        )
        val fecha = LocalDate.of(2026, 9, 7) // lunes de la semana ISO 37, dentro del calendario del piloto
        val tiempoConMapaCacheado = measureNanoTime {
            repeat(nEmulaTransiciones) {
                resolverContenidoDeLaSemana(fecha, Idioma.DE, Nivel.B2, calendario, contenidoPorId)
            }
        }

        val msAntes = tiempoReparseandoCadaVez / 1_000_000.0
        val msAhora = tiempoConMapaCacheado / 1_000_000.0
        println(
            "RepositorioSemanaPerfTest: $nEmulaTransiciones transiciones -- " +
                "reparseando cada vez: ${"%.1f".format(msAntes)} ms, con mapa cacheado: ${"%.3f".format(msAhora)} ms " +
                "(${"%.0f".format(msAntes / msAhora)}x)",
        )

        // Guarda de regresion, sin depender de un umbral absoluto (que varia
        // por maquina): resolver con el mapa ya armado tiene que ser varios
        // ordenes de magnitud mas rapido que reparsear todo de nuevo cada vez.
        assertTrue(
            "resolver con el mapa cacheado deberia ser al menos 50x mas rapido que reparsear todo en cada resolucion, fue ${msAntes / msAhora}x",
            msAhora * 50 < msAntes,
        )
    }
}
