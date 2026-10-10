package io.github.percati.lenguapp.pdf

import io.github.percati.lenguapp.datos.PlanillaTextos
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearPlanillaTextos
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.presentacion.PlanillaContenido
import io.github.percati.lenguapp.presentacion.SeccionPlanilla
import io.github.percati.lenguapp.presentacion.construirPlanilla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Planilla del profesor (Ronda E, tarea 2), sobre TODAS las fichas y semanas especiales
 * reales (todos los pares idioma x nivel, mas repaso y Survival de cada uno):
 *  - una hoja (dos carillas) como maximo;
 *  - nunca una segunda carilla con 3 lineas o menos si alguna compactacion la evitaba;
 *  - ni un texto en otro idioma que el que se aprende (REGLAS-PREVENCION P1/P8).
 * El layout se prueba sin dibujar (maquetar), asi que es rapido; PdfDocument no corre
 * bajo Robolectric.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PlanillaTodosLosParesTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private val textos = parsearPlanillaTextos(File(assets(), "plantillas/planilla-profesor.json").readText())
    private val topicNombres = parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText())
    private val todo: List<ContenidoSemanal> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }
    }

    private fun idiomaDe(c: ContenidoSemanal): Idioma = when (c) {
        is Ficha -> c.idioma
        is SemanaEspecial -> c.idioma
    }

    private fun planilla(c: ContenidoSemanal) = construirPlanilla(c, textos.getValue(idiomaDe(c)), topicNombres)

    @Test
    fun `ninguna planilla real pasa de dos carillas`() {
        assertTrue(todo.size > 600)
        val malas = todo.filter { PlanillaPdf.elegirLayout(planilla(it)).cantidadPaginas > 2 }.map { it.id }
        assertEquals("planillas de mas de dos carillas", emptyList<String>(), malas)
    }

    @Test
    fun `ninguna segunda carilla queda con 3 lineas o menos si alguna compactacion la evitaba`() {
        var compactadas = 0
        val malas = mutableListOf<String>()
        for (c in todo) {
            val p = planilla(c)
            val elegido = PlanillaPdf.elegirLayout(p)
            if (elegido.estilo != PlanillaPdf.NORMAL) compactadas++
            if (elegido.cantidadPaginas == 2 && elegido.lineasUltimaPagina <= PlanillaPdf.MAX_LINEAS_COLA) {
                // Solo vale si se agotaron TODAS las compactaciones y ninguna entraba en una.
                val algunaEntraba = PlanillaPdf.COMPACTACIONES.any { PlanillaPdf.maquetar(p, it).cantidadPaginas == 1 }
                if (algunaEntraba) malas += c.id
            }
        }
        println("PLANILLAS: ${todo.size} total, $compactadas compactadas a una hoja, ${todo.count { PlanillaPdf.elegirLayout(planilla(it)).cantidadPaginas == 1 }} en una hoja, ${todo.count { PlanillaPdf.elegirLayout(planilla(it)).cantidadPaginas == 2 }} en dos")
        assertEquals("segunda carilla con <= 3 lineas habiendo compactacion posible ($compactadas compactadas)", emptyList<String>(), malas)
    }

    @Test
    fun `las compactaciones respetan los minimos de cuerpo de 9 pt y margenes de 15 mm, en orden`() {
        val minimoMargen = 15f * 72f / 25.4f
        for (e in PlanillaPdf.COMPACTACIONES) {
            assertTrue(e.cuerpo >= PlanillaPdf.CUERPO_MINIMO)
            assertTrue(e.margen >= minimoMargen - 0.01f)
        }
        val l = PlanillaPdf.COMPACTACIONES
        // primero solo espaciado (mismo cuerpo y margenes), despues cuerpo, al final margenes
        assertEquals(PlanillaPdf.NORMAL.cuerpo, l.first().cuerpo, 0f)
        assertEquals(PlanillaPdf.NORMAL.margen, l.first().margen, 0f)
        assertTrue(l.first().interlineado < PlanillaPdf.NORMAL.interlineado)
        assertTrue(l.first().espacioSeccion < PlanillaPdf.NORMAL.espacioSeccion)
        assertTrue(l.last().margen < PlanillaPdf.NORMAL.margen)
        assertTrue(l.zipWithNext().all { (a, b) -> b.cuerpo <= a.cuerpo })
    }

    private fun baseIngles(): PlanillaContenido {
        val ficha = todo.filterIsInstance<Ficha>().first { it.idioma == Idioma.EN }
        return construirPlanilla(ficha, textos.getValue(Idioma.EN), topicNombres)
    }

    @Test
    fun `un contenido que se pasa por una linea se compacta a una sola hoja`() {
        val base = baseIngles()
        fun con(n: Int) = base.copy(secciones = base.secciones + SeccionPlanilla("X", List(n) { "linea de relleno numero ${it + 1}" }))
        // Se agregan lineas de a una hasta que la planilla normal cae a la segunda carilla.
        var extra = 0
        while (PlanillaPdf.maquetar(con(extra), PlanillaPdf.NORMAL).cantidadPaginas == 1) extra++
        val p = con(extra)
        val normal = PlanillaPdf.maquetar(p, PlanillaPdf.NORMAL)
        assertEquals(2, normal.cantidadPaginas)
        assertTrue(normal.lineasUltimaPagina <= PlanillaPdf.MAX_LINEAS_COLA)
        val elegido = PlanillaPdf.elegirLayout(p)
        assertEquals(1, elegido.cantidadPaginas)
        assertTrue(elegido.estilo != PlanillaPdf.NORMAL)
    }

    @Test
    fun `un contenido con una segunda carilla llena no se compacta`() {
        val base = baseIngles()
        val p = base.copy(secciones = base.secciones + SeccionPlanilla("X", List(25) { "linea de relleno numero ${it + 1}" }))
        val elegido = PlanillaPdf.elegirLayout(p)
        assertEquals(PlanillaPdf.NORMAL, elegido.estilo)
        assertEquals(2, elegido.cantidadPaginas)
        assertTrue(elegido.lineasUltimaPagina > PlanillaPdf.MAX_LINEAS_COLA)
    }

    // --- idioma: todo en el que se aprende ---

    private fun fijosDe(t: PlanillaTextos) = setOf(
        t.titulo, t.objetivo, t.tema, t.enfoque, t.gramatica, t.expresionesObjetivo, t.expresionesReparacion,
        t.situaciones, t.giro, t.criterios,
    )

    @Test
    fun `ninguna planilla real tiene un texto fijo ni el topic en otro idioma que el aprendido`() {
        val malas = mutableListOf<String>()
        for (c in todo) {
            val aprendido = idiomaDe(c)
            val t = textos.getValue(aprendido)
            val p: PlanillaContenido = planilla(c)
            if (p.titulo != t.titulo) malas += "${c.id}: titulo"
            if (!p.encabezado.contains(t.nivel)) malas += "${c.id}: encabezado sin el rotulo de nivel"
            val fijos = fijosDe(t)
            for (sec in p.secciones) {
                if (sec.titulo.isNotEmpty() && sec.titulo !in fijos) malas += "${c.id}: seccion ${sec.titulo}"
                // las secciones de texto 100% fijo, linea por linea
                when (sec.titulo) {
                    t.expresionesReparacion -> if (sec.lineas != t.frasesReparacion) malas += "${c.id}: reparacion"
                    t.situaciones -> if (sec.lineas != listOf(t.situacionCotidiana, t.situacionProfesional)) malas += "${c.id}: situaciones"
                    t.giro -> if (sec.lineas != listOf(t.giroTexto)) malas += "${c.id}: giro"
                    t.criterios -> if (sec.lineas != t.criteriosLista) malas += "${c.id}: criterios"
                    "" -> if (sec.lineas != listOf(t.recordatorio)) malas += "${c.id}: recordatorio"
                }
            }
            if (c is Ficha) {
                val tema = p.secciones.first { it.titulo == t.tema }.lineas.first()
                if (tema != topicNombres.nombre(c.topicId, aprendido)) malas += "${c.id}: topic $tema"
                // y el nombre existe de verdad (no es el fallback a la propia clave)
                if (tema == c.topicId) malas += "${c.id}: topic sin nombre"
            }
        }
        assertEquals(emptyList<String>(), malas)
    }

    @Test
    fun `el topic de DE B1 y de EN C1 sale en aleman y en ingles, no en espanol`() {
        for ((id, idioma) in listOf("DE-F01-B1-2026-1" to Idioma.DE, "EN-F01-C1-2026-1" to Idioma.EN)) {
            val f = todo.first { it.id == id } as Ficha
            val t = textos.getValue(idioma)
            val tema = planilla(f).secciones.first { it.titulo == t.tema }.lineas.first()
            assertEquals(topicNombres.nombre(f.topicId, idioma), tema)
            assertFalse("$id: topic en espanol", tema == topicNombres.nombre(f.topicId, Idioma.ES))
        }
    }

    @Test
    fun `los textos fijos de cada idioma aprendible difieren de los del espanol`() {
        val es = fijosDe(textos.getValue(Idioma.ES))
        for ((idioma, t) in textos) {
            if (idioma == Idioma.ES) continue
            assertTrue("$idioma: demasiados textos fijos iguales al espanol", fijosDe(t).intersect(es).size <= 2)
        }
    }
}
