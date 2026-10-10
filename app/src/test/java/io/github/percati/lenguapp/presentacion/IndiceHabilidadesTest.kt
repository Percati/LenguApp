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

    private fun indice(idioma: Idioma, nivel: Nivel, base: Idioma = Idioma.ES, semanaRef: Int = 1) =
        construirIndiceHabilidades(calendario(idioma, nivel), 2026, idioma, nivel, contenido, base, semanaRef) as IndiceHabilidades.Filas

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
    fun `un toque abre la proxima aparicion desde la semana de referencia, o la ultima si ya pasaron todas`() {
        val habilidadRepetida = Nivel.entries.flatMap { n -> listOf(Idioma.EN, Idioma.DE).map { it to n } }
            .firstNotNullOfOrNull { (i, n) -> indice(i, n).filas.firstOrNull { it.apariciones.size >= 2 }?.let { Triple(i, n, it.skillId) } }
        // en el calendario parcial de 2026 puede no haber repeticiones; entonces se prueba la regla con una sola aparicion
        val (idioma, nivel) = pares.first()
        val fila = indice(idioma, nivel).filas.first()
        val ultima = fila.apariciones.last().fichaId
        val primera = fila.apariciones.first().fichaId
        assertEquals(primera, indice(idioma, nivel, semanaRef = 0).filas.first { it.skillId == fila.skillId }.fichaAAbrir)
        assertEquals(ultima, indice(idioma, nivel, semanaRef = 99).filas.first { it.skillId == fila.skillId }.fichaAAbrir)
        if (habilidadRepetida != null) {
            val (i, n, skill) = habilidadRepetida
            val f = indice(i, n).filas.first { it.skillId == skill }
            val segunda = f.apariciones[1]
            assertEquals(segunda.fichaId, indice(i, n, semanaRef = f.apariciones[0].semana + 1).filas.first { it.skillId == skill }.fichaAAbrir)
        }
    }

    @Test
    fun `una habilidad sin ficha en el nivel activo no aparece`() {
        val (idioma, nivel) = pares.first()
        val filas = indice(idioma, nivel).filas
        val quitada = filas.first()
        val sinEsa = contenido - quitada.apariciones.map { it.fichaId }.toSet()
        val r = construirIndiceHabilidades(calendario(idioma, nivel), 2026, idioma, nivel, sinEsa, Idioma.ES, 1) as IndiceHabilidades.Filas
        assertTrue(r.filas.none { it.skillId == quitada.skillId })
        assertEquals(filas.size - 1, r.filas.size)
    }

    @Test
    fun `sin calendario para el anio no hay indice, se dice por que`() {
        val r = construirIndiceHabilidades(CalendarioCargado.SinCalendarioParaElAnio, 2030, Idioma.EN, Nivel.B2, contenido, Idioma.ES, 1)
        assertEquals(IndiceHabilidades.SinCalendario(2030), r)
        assertEquals(IndiceHabilidades.SinCalendario(2026), construirIndiceHabilidades(CalendarioCargado.SinCalendarioParaIdiomaONivel, 2026, Idioma.EN, Nivel.B2, contenido, Idioma.ES, 1))
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
