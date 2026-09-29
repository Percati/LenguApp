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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.time.LocalDate

class ResolutorSemanaTest {

    // --- Feature 2 (desafio de fin de semana): sabado 00:00 a domingo 23:59 ---

    @Test
    fun `esFinDeSemana es verdadero el sabado y el domingo`() {
        assertTrue(esFinDeSemana(LocalDate.of(2026, 9, 12))) // sabado
        assertTrue(esFinDeSemana(LocalDate.of(2026, 9, 13))) // domingo
    }

    @Test
    fun `esFinDeSemana es falso de lunes a viernes`() {
        for (dia in 7..11) assertFalse(esFinDeSemana(LocalDate.of(2026, 9, dia)))
    }

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
    //
    // El id de ficha ahora lleva el anio (skillId-nivel-anio-order): el
    // piloto 2026 y 2027 comparten (skillId, nivel, order) y sin el anio el
    // segundo pisaba al primero al componer. Las dos aserciones de id de
    // aca abajo ("DE-G01-B2-2026-1", "DE-V08-B2-2026-1") van a fallar hasta
    // que app/src/main/assets/contenido/ se recomponga con componer.py bajo
    // el esquema nuevo -- hoy sigue con el id viejo (sin anio), asi que
    // idDeEntrada() ya no lo encuentra. Esperado, no un bug de este patch.

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
        assertEquals("DE-G01-B2-2026-1", (encontrado.contenido as Ficha).id)
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
        assertEquals("REVIEW-DE-B2-2026-S40", (encontrado.contenido as SemanaEspecial).id)
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
    fun `un idioma sin ningun calendario en un anio que si tiene otros se distingue de anio sin calendario`() {
        // 2026 tiene calendario para DE y EN (los 10 pares), pero ninguno de FR:
        // clasificarDisponibilidad tiene que distinguir "falta este par" de "falta
        // el anio entero" sin depender de que exista algun nivel real sin cubrir
        // (con los 7 calendarios parciales, DE y EN ya cubren los cinco niveles).
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 7),
            idioma = Idioma.FR, nivel = Nivel.B2,
            calendario = calendarioCargado(2026, Idioma.FR, Nivel.B2),
            contenidoPorId = cargarContenido(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2026, 37), Idioma.FR, Nivel.B2, RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO),
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
        assertEquals("DE-V08-B2-2026-1", (encontrado.contenido as Ficha).id)
    }

    // --- Calendarios parciales 2026 (semana 41 en adelante), ver generar_calendario.py --parcial ---

    @Test
    fun `un calendario que arranca en la semana 41 (no en la 37, como el piloto) resuelve bien esa primera semana`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 10, 5), // lunes de la semana ISO 41
            idioma = Idioma.DE, nivel = Nivel.A2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.A2),
            contenidoPorId = cargarContenido(),
        )
        val encontrado = resultado as? ResultadoSemana.Encontrado ?: error("se esperaba Encontrado, fue $resultado")
        assertEquals(SemanaIso(2026, 41), encontrado.semanaIso)
        // Primer skill del calendario parcial de-A2 (ver generar_calendario.py
        // --parcial, FALTANTES.md seccion 11): la ficha ya esta embebida
        // (seccion 12), asi que la semana 41 resuelve de verdad, no "sin contenido".
        assertEquals("DE-F01-A2-2026-1", (encontrado.contenido as Ficha).id)
    }

    @Test
    fun `una semana anterior al inicio de un calendario parcial (S39, antes de la 41) esta fuera de la edicion`() {
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 9, 21), // lunes de la semana ISO 39, antes de la 41
            idioma = Idioma.DE, nivel = Nivel.A2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.A2), // generado desde S41
            contenidoPorId = cargarContenido(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2026, 39), Idioma.DE, Nivel.A2, RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION),
            resultado,
        )
    }

    @Test
    fun `una semana con entrada de calendario pero sin ficha en el mapa se muestra sin contenido, no crashea`() {
        // A esta altura los assets reales ya tienen las 606 fichas embebidas
        // (FALTANTES.md seccion 12), asi que este caso -- calendario "encontro"
        // una entrada pero contenidoPorId no tiene esa ficha -- ya no se da con
        // datos reales. Sigue siendo un camino real de resolverContenidoDeLaSemana
        // (una build vieja, o el paso intermedio entre calendario y contenido que
        // hubo en sept 2026): se prueba con un mapa vacio a proposito, no
        // dependiendo de que a los assets les falte algo.
        val resultado = resolverContenidoDeLaSemana(
            fecha = LocalDate.of(2026, 10, 5), // S41, primera semana del calendario parcial
            idioma = Idioma.DE, nivel = Nivel.A2,
            calendario = calendarioCargado(2026, Idioma.DE, Nivel.A2),
            contenidoPorId = emptyMap(),
        )
        assertEquals(
            ResultadoSemana.SinContenido(SemanaIso(2026, 41), Idioma.DE, Nivel.A2, RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION),
            resultado,
        )
    }

    @Test
    fun `calendarioCargado distingue las tres celdas de la matriz en assets reales`() {
        assertTrue(calendarioCargado(2026, Idioma.DE, Nivel.B2) is CalendarioCargado.Encontrado)
        assertTrue(calendarioCargado(2026, Idioma.EN, Nivel.C1) is CalendarioCargado.Encontrado)
        assertEquals(CalendarioCargado.SinCalendarioParaIdiomaONivel, calendarioCargado(2026, Idioma.FR, Nivel.B2))
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

    // --- nivelesConContenido: el selector de nivel no puede ofrecer los cinco fijos
    // (CLAUDE.md, regla dura #10) -- con ingles B2 y C1 conviviendo en 2026, hace falta
    // distinguir cuales existen de verdad, cosa que idiomasConContenido solo no permite ---

    @Test
    fun `nivelesConContenido en los assets reales da los cinco niveles para aleman e ingles`() {
        // Desde los 7 calendarios parciales (semana 41-53, sept 2026): DE y EN
        // ya cubren A2-C2 en 2026, aunque `contenido` todavia solo tenga las
        // fichas del piloto (B2/C1) -- calendario y contenido son capas
        // independientes, ver `una semana con calendario pero sin ficha embebida...`.
        val nombres = File(carpetaAssets(), "calendario").list()!!.toList()
        val niveles = nivelesConContenido(nombres)
        assertEquals(Nivel.entries.toSet(), niveles.getValue(Idioma.EN))
        assertEquals(Nivel.entries.toSet(), niveles.getValue(Idioma.DE))
    }

    @Test
    fun `nivelesConContenido agrupa varios niveles del mismo idioma`() {
        val nombres = listOf("calendario_2026_en_B2.json", "calendario_2026_en_C1.json")
        assertEquals(mapOf(Idioma.EN to setOf(Nivel.B2, Nivel.C1)), nivelesConContenido(nombres))
    }

    @Test
    fun `nivelesConContenido sin ningun archivo es un mapa vacio, no un error`() {
        assertEquals(emptyMap<Idioma, Set<Nivel>>(), nivelesConContenido(emptyList()))
    }
}
