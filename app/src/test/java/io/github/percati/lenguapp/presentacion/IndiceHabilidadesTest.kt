package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoSemana
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Indice de habilidades (Ronda F2, tarea 7), sobre los calendarios y las fichas REALES de assets:
 * para EN y DE en cada nivel lista las habilidades que existen, con sus semanas segun el calendario.
 */
class IndiceHabilidadesTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val contenido: Map<String, ContenidoSemanal> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.map { parsearContenido(it.readText()) }.associateBy { it.id }
    }

    private fun calendario(idioma: Idioma, nivel: Nivel): CalendarioCargado.Encontrado =
        CalendarioCargado.Encontrado(parsearCalendario(File(assets(), "calendario/calendario_2026_${idioma.name.lowercase()}_${nivel.name}.json").readText()))

    // Por omision el anio en curso es 2027: la edicion 2026 esta cerrada y se ve entera (los tests de la Ronda T pasan 2026 para cortar).
    private fun indice(idioma: Idioma, nivel: Nivel, base: Idioma = Idioma.ES, semanaRef: Int = 1, anioEnCurso: Int = 2027) =
        construirIndiceHabilidades(calendario(idioma, nivel), 2026, idioma, nivel, contenido, base, semanaRef, anioEnCurso) as IndiceHabilidades.Filas

    private val pares = listOf(Idioma.EN, Idioma.DE).flatMap { i -> Nivel.entries.map { n -> i to n } }

    @Test
    fun `EN y DE en cada nivel listan las habilidades del calendario con sus semanas`() {
        for ((idioma, nivel) in pares) {
            val entradas = calendario(idioma, nivel).entradas.filter { it.tipo == TipoSemana.CONTENT && it.skillId != null }
            assertTrue("$idioma $nivel: calendario sin habilidades", entradas.isNotEmpty())
            val esperado = entradas.groupBy { it.skillId!! }.mapValues { (_, e) -> e.map { it.semana }.sorted() }

            val filas = indice(idioma, nivel).filas
            assertEquals("$idioma $nivel: habilidades", esperado.keys, filas.map { it.skillId }.toSet())
            for (fila in filas) {
                assertEquals("$idioma $nivel ${fila.skillId}: semanas", esperado.getValue(fila.skillId), fila.semanas)
                assertTrue(fila.titulo.isNotBlank())
                // cada aparicion apunta a una ficha que existe en los assets, de esa habilidad y nivel
                for (a in fila.apariciones) {
                    val f = contenido[a.fichaId] as Ficha
                    assertEquals(fila.skillId, f.skillId)
                    assertEquals(nivel, f.nivel)
                }
            }
            // ordenadas por titulo
            assertEquals(filas.map { it.titulo.lowercase() }, filas.map { it.titulo.lowercase() }.sorted())
        }
    }

    @Test
    fun `el titulo sigue la regla de idioma de la ficha, A2 y B1 en el idioma de app, B2 y superiores en el que se aprende`() {
        for ((idioma, nivel) in pares) {
            val fila = indice(idioma, nivel, base = Idioma.ES).filas.first()
            val ficha = contenido[fila.apariciones.first().fichaId] as Ficha
            val esperado = if (nivel == Nivel.A2 || nivel == Nivel.B1) {
                ficha.titulo.porIdioma["es"]?.takeIf { it.isNotBlank() } ?: ficha.titulo.porIdioma.getValue(idioma.name.lowercase())
            } else {
                ficha.titulo.plano ?: ficha.titulo.porIdioma.getValue(idioma.name.lowercase())
            }
            assertEquals("$idioma $nivel", esperado.trim(), fila.titulo)
        }
    }

    @Test
    fun `un toque abre siempre la ultima aparicion de las que se muestran`() {
        val (idioma, nivel) = pares.first()
        val fila = indice(idioma, nivel).filas.first()
        // edicion cerrada (se muestra entera): la ultima de todas, sin importar la semana de referencia
        for (ref in listOf(0, 1, 40, 99)) {
            assertEquals(fila.apariciones.last().fichaId, indice(idioma, nivel, semanaRef = ref).filas.first { it.skillId == fila.skillId }.fichaAAbrir)
        }
        // anio en curso: la ultima ya pasada
        val repetida = pares.firstNotNullOfOrNull { (i, n) -> indice(i, n).filas.firstOrNull { it.apariciones.size >= 2 }?.let { Triple(i, n, it) } }
        if (repetida != null) {
            val (i, n, f) = repetida
            val primera = f.apariciones[0]
            val r = indice(i, n, semanaRef = f.apariciones[1].semana - 1, anioEnCurso = 2026).filas.first { it.skillId == f.skillId }
            assertEquals(listOf(primera.semana), r.semanas)
            assertEquals(primera.fichaId, r.fichaAAbrir)
        }
    }

    // --- Ronda T, tarea 3: sin semanas futuras, y orden por semana ---

    @Test
    fun `en el anio en curso no hay semanas futuras y una habilidad cuya primera aparicion es futura no esta`() {
        val (idioma, nivel) = Idioma.DE to Nivel.B2
        val todo = indice(idioma, nivel, anioEnCurso = 2027).filas
        val semanas = todo.flatMap { it.semanas }.sorted().distinct()
        val corte = semanas[semanas.size / 2]
        val cortado = indice(idioma, nivel, semanaRef = corte, anioEnCurso = 2026).filas
        assertTrue(cortado.isNotEmpty() && cortado.size < todo.size)
        assertTrue("ninguna semana pasa de $corte", cortado.flatMap { it.semanas }.all { it <= corte })
        val futuras = todo.filter { it.primeraSemana > corte }
        assertTrue("el caso de prueba tiene habilidades futuras", futuras.isNotEmpty())
        assertTrue(cortado.none { c -> futuras.any { it.skillId == c.skillId } })
        // la semana de referencia misma SI se muestra
        assertTrue(cortado.flatMap { it.semanas }.contains(corte))
        // en la semana 0 (antes de todo) no hay nada, y eso no rompe
        assertEquals(emptyList<FilaHabilidad>(), indice(idioma, nivel, semanaRef = 0, anioEnCurso = 2026).filas)
    }

    @Test
    fun `en un anio anterior al en curso estan todas, aunque la semana de referencia sea la 1`() {
        val (idioma, nivel) = Idioma.DE to Nivel.B2
        val esperado = calendario(idioma, nivel).entradas.filter { it.tipo == TipoSemana.CONTENT && it.skillId != null }.mapNotNull { it.skillId }.toSet()
        assertEquals(esperado, indice(idioma, nivel, semanaRef = 1, anioEnCurso = 2027).filas.map { it.skillId }.toSet())
        // el anio en curso a secas (2026) con la semana 1 muestra casi nada: la diferencia es la que importa
        assertTrue(indice(idioma, nivel, semanaRef = 1, anioEnCurso = 2026).filas.size < esperado.size)
    }

    @Test
    fun `por semana ordena por la primera aparicion, con el titulo de desempate, y alfabetico por titulo`() {
        val (idioma, nivel) = Idioma.DE to Nivel.B2
        val filas = indice(idioma, nivel).filas
        val porSemana = filas.ordenadas(OrdenHabilidades.POR_SEMANA)
        assertEquals(porSemana.map { it.primeraSemana }, porSemana.map { it.primeraSemana }.sorted())
        for ((a, b) in porSemana.zipWithNext()) if (a.primeraSemana == b.primeraSemana) assertTrue(a.titulo.lowercase() <= b.titulo.lowercase())
        assertEquals(filas.map { it.skillId }.toSet(), porSemana.map { it.skillId }.toSet())
        val alfa = porSemana.ordenadas(OrdenHabilidades.ALFABETICO)
        assertEquals(alfa.map { it.titulo.lowercase() }, alfa.map { it.titulo.lowercase() }.sorted())
        assertEquals("el orden de las filas del indice ya es el alfabetico", filas.map { it.skillId }, alfa.map { it.skillId })
        assertTrue("el orden por semana no es el alfabetico (si no, la prueba no prueba nada)", porSemana.map { it.skillId } != alfa.map { it.skillId })
    }

    @Test
    fun `una habilidad sin ficha en el nivel activo no aparece`() {
        val (idioma, nivel) = pares.first()
        val filas = indice(idioma, nivel).filas
        val quitada = filas.first()
        val sinEsa = contenido - quitada.apariciones.map { it.fichaId }.toSet()
        val r = construirIndiceHabilidades(calendario(idioma, nivel), 2026, idioma, nivel, sinEsa, Idioma.ES, 1, 2027) as IndiceHabilidades.Filas
        assertTrue(r.filas.none { it.skillId == quitada.skillId })
        assertEquals(filas.size - 1, r.filas.size)
    }

    @Test
    fun `sin calendario para el anio no hay indice, se dice por que`() {
        val r = construirIndiceHabilidades(CalendarioCargado.SinCalendarioParaElAnio, 2030, Idioma.EN, Nivel.B2, contenido, Idioma.ES, 1, 2030)
        assertEquals(IndiceHabilidades.SinCalendario(2030), r)
        assertEquals(IndiceHabilidades.SinCalendario(2026), construirIndiceHabilidades(CalendarioCargado.SinCalendarioParaIdiomaONivel, 2026, Idioma.EN, Nivel.B2, contenido, Idioma.ES, 1, 2026))
    }

    @Test
    fun `las semanas de repaso y survival no son habilidades`() {
        val (idioma, nivel) = Idioma.DE to Nivel.B2
        val entradas = calendario(idioma, nivel).entradas
        assertTrue(entradas.any { it.tipo != TipoSemana.CONTENT })
        val filas = indice(idioma, nivel).filas
        assertEquals(entradas.filter { it.tipo == TipoSemana.CONTENT }.mapNotNull { it.skillId }.toSet(), filas.map { it.skillId }.toSet())
    }
}
