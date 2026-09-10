package io.github.percati.lenguapp.semana

import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.modelo.id
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate

class ResolutorSemanaTest {

    // --- A2.3 / Fase 2: casos de borde del anio ISO vs. el anio de calendario ---

    @Test
    fun `28 de diciembre de 2026 es semana ISO 53 de 2026`() {
        assertEquals(SemanaIso(2026, 53), semanaIsoDe(LocalDate.of(2026, 12, 28)))
    }

    @Test
    fun `1 de enero de 2027 tambien es semana ISO 53 de 2026`() {
        assertEquals(SemanaIso(2026, 53), semanaIsoDe(LocalDate.of(2027, 1, 1)))
    }

    @Test
    fun `4 de enero de 2027 es semana ISO 1 de 2027`() {
        assertEquals(SemanaIso(2027, 1), semanaIsoDe(LocalDate.of(2027, 1, 4)))
    }

    // --- resolverContenidoDeLaSemana contra los assets reales embebidos ---

    private fun carpetaAssets(): File {
        val candidatos = listOf(File("src/main/assets"), File("app/src/main/assets"))
        return candidatos.firstOrNull { it.isDirectory } ?: error("no se encontro app/src/main/assets")
    }

    private fun cargarContenido(): Map<String, io.github.percati.lenguapp.modelo.ContenidoSemanal> =
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }

    private fun cargarCalendario(nombre: String) =
        parsearCalendario(File(carpetaAssets(), "calendario/$nombre").readText())

    @Test
    fun `la semana 37 de 2026 en aleman B2 resuelve a la ficha DE-G01`() {
        val contenido = cargarContenido()
        val calendario = cargarCalendario("calendario_2026_de_B2.json")
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 7), // lunes de la semana ISO 37
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendario, contenidoPorId = contenido,
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals(SemanaIso(2026, 37), encontrado.semanaIso)
        assertEquals("DE-G01-B2-1", (encontrado.contenido as Ficha).id)
    }

    @Test
    fun `la semana 40 de 2026 en aleman B2 resuelve a la semana especial de repaso`() {
        val contenido = cargarContenido()
        val calendario = cargarCalendario("calendario_2026_de_B2.json")
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 28), // lunes de la semana ISO 40
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendario, contenidoPorId = contenido,
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals("REVIEW-DE-S40", (encontrado.contenido as SemanaEspecial).id)
    }

    @Test
    fun `una semana anterior al piloto (S8 2026) no tiene contenido`() {
        val contenido = cargarContenido()
        val calendario = cargarCalendario("calendario_2026_de_B2.json") // generado desde S37
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 2, 16), // lunes de la semana ISO 8
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendario, contenidoPorId = contenido,
        )
        assertTrue(resultado is ResultadoSemana.SinContenido)
        assertEquals(SemanaIso(2026, 8), (resultado as ResultadoSemana.SinContenido).semanaIso)
    }

    @Test
    fun `un idioma y nivel sin calendario embebido no tiene contenido`() {
        val contenido = cargarContenido()
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 7),
            idioma = Idioma.ES, nivel = Nivel.A2,
            calendario = null, // no existe calendario_2026_es_A2.json
            contenidoPorId = contenido,
        )
        assertTrue(resultado is ResultadoSemana.SinContenido)
    }

    @Test
    fun `4 de enero de 2027 cae en el anio ISO 2027, que todavia no tiene calendario`() {
        val contenido = cargarContenido()
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2027, 1, 4),
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = null, // no existe calendario_2027_de_B2.json todavia
            contenidoPorId = contenido,
        )
        assertEquals(ResultadoSemana.SinContenido(SemanaIso(2027, 1), Idioma.DE, Nivel.B2), resultado)
    }

    @Test
    fun `1 de enero de 2027 resuelve con el calendario 2026 (misma semana ISO 53)`() {
        val contenido = cargarContenido()
        val calendario = cargarCalendario("calendario_2026_de_B2.json")
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2027, 1, 1),
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendario, contenidoPorId = contenido,
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals("DE-V08-B2-1", (encontrado.contenido as Ficha).id)
    }
}
