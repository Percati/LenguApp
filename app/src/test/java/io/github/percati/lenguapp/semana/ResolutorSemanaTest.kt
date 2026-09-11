package io.github.percati.lenguapp.semana

import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
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

    // --- Fase 2: casos de borde del anio ISO vs. el anio de calendario ---

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
    // El calendario lo genera generarCalendarioAssets (app/build.gradle.kts)
    // antes de compilar/testear: ver el gradle.build.kts, preBuild.

    private fun carpetaAssets(): File {
        val candidatos = listOf(File("src/main/assets"), File("app/src/main/assets"))
        return candidatos.firstOrNull { it.isDirectory } ?: error("no se encontro app/src/main/assets")
    }

    private fun cargarContenido(): Map<String, ContenidoSemanal> =
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }

    /**
     * Mismo shell de I/O que CargadorCalendario.kt (que usa Context.assets en
     * vez de File), pero llamando a la misma clasificarDisponibilidad() que
     * usa la app: si ese criterio se rompe, este test lo nota igual que la
     * app en el telefono.
     */
    private fun calendarioCargado(anio: Int, idioma: Idioma, nivel: Nivel): CalendarioCargado {
        val carpeta = File(carpetaAssets(), "calendario")
        val disponibles = carpeta.list()?.toSet() ?: emptySet()
        return when (clasificarDisponibilidad(disponibles, anio, idioma, nivel)) {
            DisponibilidadCalendario.PRESENTE ->
                CalendarioCargado.Encontrado(parsearCalendario(File(carpeta, nombreArchivoCalendario(anio, idioma, nivel)).readText()))
            DisponibilidadCalendario.SIN_CALENDARIO_PARA_IDIOMA_O_NIVEL -> CalendarioCargado.SinCalendarioParaIdiomaONivel
            DisponibilidadCalendario.SIN_CALENDARIO_PARA_EL_ANIO -> CalendarioCargado.SinCalendarioParaElAnio
        }
    }

    @Test
    fun `la semana 37 de 2026 en aleman B2 resuelve a la ficha DE-G01`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 7), // lunes de la semana ISO 37
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.B2),
            contenidoPorId = cargarContenido(),
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals(SemanaIso(2026, 37), encontrado.semanaIso)
        assertEquals("DE-G01-B2-1", (encontrado.contenido as Ficha).id)
    }

    @Test
    fun `la semana 40 de 2026 en aleman B2 resuelve a la semana especial de repaso`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 28), // lunes de la semana ISO 40
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.B2),
            contenidoPorId = cargarContenido(),
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals("REVIEW-DE-S40", (encontrado.contenido as SemanaEspecial).id)
    }

    @Test
    fun `una semana anterior al piloto (S8 2026) esta fuera de la edicion`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 2, 16), // lunes de la semana ISO 8
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.B2), // generado desde S37
            contenidoPorId = cargarContenido(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2026, 8), Idioma.DE, Nivel.B2, RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION),
            resultado,
        )
    }

    @Test
    fun `un nivel sin calendario en un anio que si tiene otros se distingue de anio sin calendario`() {
        // 2026 tiene calendario para DE-B2 y EN-C1, pero no para DE-A2.
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 7),
            idioma = Idioma.DE, nivel = Nivel.A2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.A2),
            contenidoPorId = cargarContenido(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2026, 37), Idioma.DE, Nivel.A2, RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO),
            resultado,
        )
    }

    @Test
    fun `4 de enero de 2027 cae en el anio ISO 2027, que todavia no tiene ningun calendario`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2027, 1, 4),
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendarioCargado(2027, Idioma.DE, Nivel.B2), // no existe ningun calendario_2027_*
            contenidoPorId = cargarContenido(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2027, 1), Idioma.DE, Nivel.B2, RazonSinContenido.ANIO_SIN_CALENDARIO),
            resultado,
        )
    }

    @Test
    fun `1 de enero de 2027 resuelve con el calendario 2026 (misma semana ISO 53)`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2027, 1, 1),
            idioma = Idioma.DE, nivel = Nivel.B2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.B2),
            contenidoPorId = cargarContenido(),
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals("DE-V08-B2-1", (encontrado.contenido as Ficha).id)
    }

    @Test
    fun `calendarioCargado distingue las tres celdas de la matriz en assets reales`() {
        assertTrue(calendarioCargado(2026, Idioma.DE, Nivel.B2) is CalendarioCargado.Encontrado)
        assertTrue(calendarioCargado(2026, Idioma.EN, Nivel.C1) is CalendarioCargado.Encontrado)
        assertEquals(CalendarioCargado.SinCalendarioParaIdiomaONivel, calendarioCargado(2026, Idioma.DE, Nivel.A2))
        assertEquals(CalendarioCargado.SinCalendarioParaElAnio, calendarioCargado(2027, Idioma.DE, Nivel.B2))
    }

    // --- idiomasConContenido: Fase 6, se deriva de assets/calendario/, nunca a mano ---

    @Test
    fun `idiomasConContenido en los assets reales es exactamente DE y EN`() {
        val nombres = File(carpetaAssets(), "calendario").list()!!.toList()
        assertEquals(setOf(Idioma.DE, Idioma.EN), idiomasConContenido(nombres))
    }

    @Test
    fun `idiomasConContenido ignora archivos que no siguen el patron de nombre`() {
        val nombres = listOf("calendario_2026_de_B2.json", "LEEME.txt", "calendario_2026_xx_B2.json")
        assertEquals(setOf(Idioma.DE), idiomasConContenido(nombres))
    }

    @Test
    fun `idiomasConContenido sin ningun archivo es un conjunto vacio, no un error`() {
        assertEquals(emptySet<Idioma>(), idiomasConContenido(emptyList()))
    }

    @Test
    fun `idiomasConContenido no duplica un idioma con calendarios de mas de un anio o nivel`() {
        val nombres = listOf("calendario_2026_de_B2.json", "calendario_2027_de_C1.json")
        assertEquals(setOf(Idioma.DE), idiomasConContenido(nombres))
    }
}
