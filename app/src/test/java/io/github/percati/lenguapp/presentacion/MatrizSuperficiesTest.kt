package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearCategoriasUso
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearPlanillaTextos
import io.github.percati.lenguapp.datos.parsearPlantillasPromptVoz
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.aTextoBilingue
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.ui.ClaveTexto
import io.github.percati.lenguapp.ui.etiquetaBiblioteca
import io.github.percati.lenguapp.ui.etiquetaGuardados
import io.github.percati.lenguapp.ui.etiquetaPlanillaBoton
import io.github.percati.lenguapp.ui.etiquetaDesafioFinde
import io.github.percati.lenguapp.ui.nombreIdioma
import io.github.percati.lenguapp.ui.reglasReto
import io.github.percati.lenguapp.ui.tablaChromeCruda
import io.github.percati.lenguapp.ui.tablaNombresIdiomaCruda
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Matriz de superficies (Ronda F, tarea 6): idioma aprendido (en, de) x idioma de app (los 6) x
 * nivel (A2, B1, B2). Para una ficha real de cada combinacion afirma el IDIOMA DE SALIDA de cada
 * superficie segun la tabla 2 de REGLAS-PREVENCION.md. Sin pantallas: sobre las funciones de
 * `presentacion/` y `datos/` (la pantalla solo las llama).
 *
 * Superficies que NO se pueden probar sin UI (quedan en ContenidoSemanalScreenTest y compania):
 * el cableado de `ContenidoSemanalScreen` (que cada Text use la funcion correcta), el switch de
 * traduccion como control y el copiado del prompt al portapapeles.
 */
class MatrizSuperficiesTest {

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private val fichas: List<Ficha> by lazy {
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
    }
    private val textosPlanilla = parsearPlanillaTextos(File(assets(), "plantillas/planilla-profesor.json").readText())
    private val promptsVoz = parsearPlantillasPromptVoz(File(assets(), "plantillas/prompt-voz.json").readText())
    private val topicsTexto = File(assets(), "temas/topic-nombres.json").readText()
    private val topics: NombresI18n = parsearTopicNombres(topicsTexto)
    private val categorias: CategoriasUso = parsearCategoriasUso(File(assets(), "temas/categorias-uso.json").readText())

    private fun codigo(i: Idioma) = i.name.lowercase()

    /** Una ficha real del par; si hay, una que tenga errores contrastivos para ese idioma de app. */
    private fun fichaPara(aprendido: Idioma, nivel: Nivel, app: Idioma): Ficha {
        val delPar = fichas.filter { it.idioma == aprendido && it.nivel == nivel && it.vocabulario.isNotEmpty() && it.redemittel.isNotEmpty() }
        assertTrue("sin fichas para $aprendido $nivel", delPar.isNotEmpty())
        return delPar.firstOrNull { !it.erroresContrastivos?.get(codigo(app)).isNullOrEmpty() } ?: delPar.first()
    }

    private val aprendidos = listOf(Idioma.EN, Idioma.DE)
    private val niveles = listOf(Nivel.A2, Nivel.B1, Nivel.B2)

    private fun matriz(bloque: (aprendido: Idioma, app: Idioma, nivel: Nivel, ficha: Ficha) -> Unit) {
        for (aprendido in aprendidos) for (app in Idioma.entries) for (nivel in niveles) {
            bloque(aprendido, app, nivel, fichaPara(aprendido, nivel, app))
        }
    }

    // --- Rotulos, botones, ayudas: idioma de app ---

    @Test
    fun `rotulos y botones salen en el idioma de app`() = matriz { aprendido, app, _, _ ->
        val crudo = tablaChromeCruda()
        assertEquals(crudo.getValue(ClaveTexto.GUARDADOS).getValue(app), etiquetaGuardados(app))
        assertEquals(crudo.getValue(ClaveTexto.BIBLIOTECA).getValue(app), etiquetaBiblioteca(app))
        assertEquals(crudo.getValue(ClaveTexto.PLANILLA_BOTON).getValue(app), etiquetaPlanillaBoton(app))
        assertEquals(crudo.getValue(ClaveTexto.DESAFIO_FINDE).getValue(app), etiquetaDesafioFinde(app))
        // el nombre del idioma que se aprende, tambien en el idioma de app
        assertEquals(tablaNombresIdiomaCruda().getValue(aprendido).getValue(app), nombreIdioma(aprendido, app))
    }

    @Test
    fun `las reglas del reto salen en el idioma de app`() = matriz { _, app, nivel, _ ->
        val clave = if (nivel == Nivel.A2 || nivel == Nivel.B1) ClaveTexto.RETO_REGLAS_BASICO else ClaveTexto.RETO_REGLAS_AVANZADO
        val esperado = tablaChromeCruda().getValue(clave).getValue(app).replace("{oralMin}", "6").split('\n')
        assertEquals(esperado, reglasReto(app, nivel, 6))
    }

    // --- Nombres de topic y de categoria: idioma de app ---

    @Test
    fun `nombres de topic y de categoria en el idioma de app`() = matriz { _, app, _, f ->
        val crudoTopic = kotlinx.serialization.json.Json.parseToJsonElement(topicsTexto)
        val esperado = (crudoTopic as kotlinx.serialization.json.JsonObject).getValue(f.topicId)
            .let { (it as kotlinx.serialization.json.JsonObject).getValue(codigo(app)) }
            .let { (it as kotlinx.serialization.json.JsonPrimitive).content }
        assertEquals(esperado, topics.nombre(f.topicId, app))
        f.redemittel.firstNotNullOfOrNull { it.categoriasUso?.firstOrNull() }?.let { cat ->
            assertEquals(categorias.nombres.getValue(cat).getValue(codigo(app)), categorias.nombre(cat, app))
        }
    }

    // --- Ficha: idioma que se aprende; A2/B1 con el switch activo, idioma de app ---

    @Test
    fun `el idioma de la prosa de la ficha sigue la tabla`() = matriz { aprendido, app, nivel, f ->
        val basico = nivel == Nivel.A2 || nivel == Nivel.B1
        assertEquals("$aprendido $nivel: bilingue solo en A2/B1", basico, f.bilingue)
        val conSwitch = idiomaMostradoDeFicha(f.bilingue, aprendido, app, traducir = true)
        val sinSwitch = idiomaMostradoDeFicha(f.bilingue, aprendido, app, traducir = false)
        // con el switch activo: idioma de app en A2/B1 (si difiere del aprendido); en B2+ no hay switch
        assertEquals(if (basico && app != aprendido) app else aprendido, conSwitch)
        assertEquals(aprendido, sinSwitch)
    }

    @Test
    fun `el titulo de la ficha sale en el idioma que corresponde`() = matriz { aprendido, app, nivel, f ->
        val mostrado = idiomaMostradoDeFicha(f.bilingue, aprendido, app)
        val porIdioma = f.titulo.porIdioma
        val esperado = if (f.titulo.plano != null) f.titulo.plano else if (mostrado == aprendido) {
            porIdioma.getValue(codigo(aprendido))
        } else {
            porIdioma[codigo(app)]?.takeIf { it.isNotBlank() } ?: porIdioma.getValue(codigo(aprendido))
        }
        assertEquals("$aprendido $nivel app=$app", esperado, f.titulo.resolver(mostrado, aprendido))
        // apagado el switch, siempre el original
        assertEquals(f.titulo.resolver(aprendido, aprendido), f.titulo.resolver(idiomaMostradoDeFicha(f.bilingue, aprendido, app, false), aprendido))
    }

    @Test
    fun `los errores contrastivos salen traducidos con el switch y originales sin el, y planos en B2`() = matriz { aprendido, app, nivel, f ->
        val items = f.erroresContrastivos?.get(codigo(app)).orEmpty()
        if (items.isEmpty() || app == aprendido) return@matriz
        val activo = erroresParaMostrar(emptyList(), f.erroresContrastivos, aprendido, app, { it.resolver(idiomaMostradoDeFicha(f.bilingue, aprendido, app, true), aprendido) })
        val apagado = erroresParaMostrar(emptyList(), f.erroresContrastivos, aprendido, app, { it.resolver(idiomaMostradoDeFicha(f.bilingue, aprendido, app, false), aprendido) })
        if (nivel == Nivel.A2 || nivel == Nivel.B1) {
            assertEquals(items.map { it.porIdioma.getValue(codigo(app)) }, activo)
            assertEquals(items.map { it.porIdioma.getValue(codigo(aprendido)) }, apagado)
        } else {
            assertTrue(items.all { it.plano != null })
            assertEquals(activo, apagado) // B2+: no hay switch, siempre el mismo texto
        }
    }

    @Test
    fun `las traducciones de vocabulario y de expresiones salen en el idioma de app, siempre`() = matriz { _, app, _, f ->
        val v = f.vocabulario.first()
        val esperadoV = v.traducciones[codigo(app)]?.takeIf { it.isNotBlank() } ?: v.traducciones["es"]?.takeIf { it.isNotBlank() } ?: "—"
        assertEquals(esperadoV, v.traduccionParaMostrar(app))
        val r = f.redemittel.first()
        val propia = r.traducciones[codigo(app)]?.takeIf { it.isNotBlank() }
        val t = r.traduccionParaMostrar(app)
        if (propia != null) assertEquals(TraduccionRedemittel.Disponible(propia), t)
        else assertNotNull(t)
    }

    // --- Planilla: todo en el idioma que se aprende, no en el de app ---

    @Test
    fun `la planilla sale en el idioma que se aprende, incluido el topic, sea cual sea el idioma de app`() = matriz { aprendido, app, _, f ->
        val t = textosPlanilla.getValue(aprendido)
        val p = construirPlanilla(f, t, topics)
        assertEquals(t.titulo, p.titulo)
        assertTrue(p.encabezado.contains(t.nivel))
        val tema = p.secciones.first { it.titulo == t.tema }.lineas.first()
        assertEquals(topics.nombre(f.topicId, aprendido), tema)
        if (app != aprendido) {
            val enApp = topics.nombre(f.topicId, app)
            if (enApp != tema) assertNotEquals("el topic de la planilla salio en el idioma de app", enApp, tema)
        }
        // los textos fijos son los del idioma que se aprende
        assertEquals(listOf(t.situacionCotidiana, t.situacionProfesional), p.secciones.first { it.titulo == t.situaciones }.lineas)
        assertEquals(listOf(t.recordatorio), p.secciones.last().lineas)
    }

    // --- Prompt de correccion y prompt de voz: idioma que se aprende ---

    @Test
    fun `el prompt de correccion sale siempre en el idioma que se aprende, aunque el switch muestre la traduccion`() = matriz { aprendido, app, nivel, f ->
        val prompt = f.promptCorreccion.promptEnIdiomaAprendido(aprendido)
        val original = f.promptCorreccion.plano ?: f.promptCorreccion.porIdioma.getValue(codigo(aprendido))
        assertEquals("$aprendido $nivel app=$app", original, prompt)
        if (nivel == Nivel.A2 || nivel == Nivel.B1) {
            val traduccion = f.promptCorreccion.porIdioma[codigo(app)]
            if (app != aprendido && !traduccion.isNullOrBlank()) assertNotEquals(traduccion, prompt)
        }
    }

    @Test
    fun `el prompt de voz existe solo donde hay plantilla y va en el idioma que se aprende`() = matriz { aprendido, _, nivel, f ->
        val prompt = promptVozPara(f, promptsVoz)
        val hayPlantilla = promptsVoz.containsKey("${codigo(aprendido)}-${nivel.name}")
        if (!hayPlantilla) {
            assertNull("$aprendido $nivel no debe tener prompt de voz", prompt)
        } else {
            assertNotNull(prompt)
            assertTrue(prompt!!.contains(f.titulo.resolver(aprendido, aprendido).trim()))
        }
    }

    // --- Fila de Guardados (Ronda F2, tarea 1): original + traduccion al idioma de app + funcion en idioma aprendido ---

    @Test
    fun `la fila de Guardados muestra original, traduccion en idioma de app y funcion en idioma aprendido`() = matriz { aprendido, app, nivel, f ->
        val enBiblioteca = construirBiblioteca(listOf(f))
        val v = f.vocabulario.first()
        val r = f.redemittel.first()
        val gV = ItemGuardado(idioma = aprendido, nivel = nivel, tipo = TipoGuardado.VOCABULARIO, texto = v.aTextoBilingue(aprendido), skillIdOrigen = f.skillId)
        val gR = ItemGuardado(idioma = aprendido, nivel = nivel, tipo = TipoGuardado.EXPRESION, texto = r.aTextoBilingue(aprendido), funcion = r.funcion, skillIdOrigen = f.skillId)

        val dV = gV.datosFila(app)
        assertEquals("$aprendido $nivel app=$app", v.item, dV.original)
        val esperadaV = (v.traducciones[codigo(app)]?.takeIf { it.isNotBlank() } ?: v.traducciones["es"]?.takeIf { it.isNotBlank() })?.takeIf { it != v.item }
        assertEquals(esperadaV, dV.traduccion)
        assertNull("el vocabulario no tiene funcion", dV.funcion)

        val dR = gR.datosFila(app)
        assertEquals(r.expresion, dR.original)
        val propia = r.traducciones[codigo(app)]?.takeIf { it.isNotBlank() } ?: r.traducciones["es"]?.takeIf { it.isNotBlank() }
        assertEquals(propia?.takeIf { it != r.expresion }, dR.traduccion)
        assertEquals(r.funcion.resolver(aprendido, aprendido).trim().takeIf { it.isNotEmpty() }, dR.funcion)

        // y es EXACTAMENTE lo que muestra la Biblioteca
        val bV = enBiblioteca.first { it.tipo == TipoGuardado.VOCABULARIO && it.texto == v.item }.datosFila(app)
        val bR = enBiblioteca.first { it.tipo == TipoGuardado.EXPRESION && it.texto == r.expresion }.datosFila(app)
        assertEquals(bV, dV)
        assertEquals(bR, dR)
    }

    @Test
    fun `un guardado sin traduccion omite la linea, y uno sin funcion tambien`() {
        val sin = ItemGuardado(idioma = Idioma.DE, nivel = Nivel.B2, tipo = TipoGuardado.EXPRESION,
            texto = TextoBilingue(porIdioma = mapOf("de" to "sowieso")), funcion = TextoBilingue(plano = "   "), skillIdOrigen = "DE-V01")
        for (app in Idioma.entries) {
            val d = sin.datosFila(app)
            assertEquals("sowieso", d.original)
            assertNull("no se repite el original ni se muestra un hueco", d.traduccion)
            assertNull("funcion en blanco se omite", d.funcion)
        }
    }

    @Test
    fun `lo que se guarda en Room ya trae original y traducciones, no hace falta migrar`() {
        // El snapshot es aTextoBilingue(): la palabra bajo la clave del idioma aprendido + traducciones.
        val f = fichas.first { it.idioma == Idioma.EN && it.nivel == Nivel.B1 }
        val v = f.vocabulario.first { (it.traducciones["es"] ?: "").isNotBlank() }
        val snapshot = v.aTextoBilingue(Idioma.EN)
        assertEquals(v.item, snapshot.porIdioma["en"])
        assertEquals(v.traducciones.getValue("es"), snapshot.porIdioma["es"])
        val g = ItemGuardado(idioma = Idioma.EN, nivel = Nivel.B1, tipo = TipoGuardado.VOCABULARIO, texto = snapshot, skillIdOrigen = f.skillId)
        assertEquals(DatosFila(v.item, v.traducciones.getValue("es"), null), g.datosFila(Idioma.ES))
    }
}
