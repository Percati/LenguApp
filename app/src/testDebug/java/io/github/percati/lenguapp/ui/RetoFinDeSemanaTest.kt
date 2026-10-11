package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.datos.parsearTopicNombres
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.consignaDelDesafio
import io.github.percati.lenguapp.presentacion.expresionesDeLaFicha
import io.github.percati.lenguapp.presentacion.oralMinRedondeado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.time.LocalDate

/**
 * Desafio de fin de semana (Ronda F2, tarea 2 / REGLAS-PREVENCION P4; consigna en la Ronda K): NO es la mision
 * repetida. La mision completa es consigna + requisitos + micro-tareas + evidencia escrita; el desafio reusa SOLO la
 * consigna, como encabezado oral (linea de encuadre con los minutos + consigna), y nunca los requisitos ni las
 * micro-tareas. Sobre fichas reales de cada nivel A2 a C2: consigna, tema, habilidad, condiciones y frases, en ese
 * orden; no persiste nada (regla dura 13).
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h6000dp")
class RetoFinDeSemanaTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val sabado = LocalDate.of(2026, 9, 12)

    private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
    private val topics: NombresI18n by lazy { parsearTopicNombres(File(assets(), "temas/topic-nombres.json").readText()) }

    private fun fichaDe(nivel: Nivel): Ficha =
        File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { it.nivel == nivel && it.mision.requisitos.size >= 2 && it.redemittel.size >= 3 }

    private fun textosEnPantalla(): List<String> =
        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }

    private fun limpio(t: String) = t.replace("*", "").replace("«", "").replace("»", "")

    private fun fragmentos(t: TextoBilingue, ficha: Ficha, mostrado: Idioma) =
        limpio(t.resolver(mostrado, ficha.idioma)).take(30)

    private fun activarReto(ficha: Ficha, idiomaApp: Idioma) {
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = idiomaApp, fecha = sabado, topicNombres = topics) }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(idiomaApp))).performClick()
        composeTestRule.waitForIdle()
    }

    private fun verificar(nivel: Nivel, idiomaApp: Idioma) {
        val ficha = fichaDe(nivel)
        activarReto(ficha, idiomaApp)
        val conReto = textosEnPantalla()
        val unidos = conReto.joinToString(" | ")
        val visibles = conReto.map { limpio(it) }

        // La consigna de la mision SI se muestra (en el idioma que se esta mostrando); sus requisitos y micro-tareas NO,
        // en ningun idioma posible
        val mostrado = if (ficha.bilingue) idiomaApp else ficha.idioma
        assertTrue("$nivel: falta la consigna: $unidos", visibles.any { it.contains(fragmentos(ficha.mision.consigna, ficha, mostrado)) })
        for (idioma in listOf(idiomaApp, ficha.idioma)) {
            for (req in ficha.mision.requisitos) {
                assertFalse("$nivel: aparece un requisito", visibles.any { it.contains(fragmentos(req, ficha, idioma)) })
            }
            for (mt in ficha.microtareas) {
                assertFalse("$nivel: aparece una micro-tarea", visibles.any { it.contains(fragmentos(mt.texto, ficha, idioma)) })
            }
        }
        // ...ni la descripcion u otras secciones de la ficha
        assertFalse(visibles.any { it.contains(fragmentos(ficha.descripcion, ficha, ficha.idioma)) })

        // Linea oral con los minutos de la ficha, y la consigna justo debajo, antes que todo lo demas
        val lineaOral = textoRetoConsignaLinea(idiomaApp, oralMinRedondeado(ficha.evidencia.oralMin))
        assertTrue(unidos, conReto.contains(lineaOral))
        assertTrue("la linea lleva los minutos de la ficha", lineaOral.contains(oralMinRedondeado(ficha.evidencia.oralMin).toString()))
        val iLinea = conReto.indexOf(lineaOral)
        val iConsigna = conReto.indexOfFirst { limpio(it).contains(fragmentos(ficha.mision.consigna, ficha, mostrado)) }
        val iTema = conReto.indexOf("${etiquetaBibliotecaTema(idiomaApp)}:")
        val iHabilidad = conReto.indexOf("${etiquetaRetoHabilidad(idiomaApp)}:")
        val iCondiciones = conReto.indexOf(etiquetaRetoCondiciones(idiomaApp))
        val iFrases = conReto.indexOf(etiquetaRetoFrasesTitulo(idiomaApp))
        assertEquals("el orden del bloque: linea oral, consigna, tema, habilidad, condiciones, frases ($conReto)", listOf(iLinea, iConsigna, iTema, iHabilidad, iCondiciones, iFrases).sorted(), listOf(iLinea, iConsigna, iTema, iHabilidad, iCondiciones, iFrases))
        assertEquals("consigna justo despues de la linea", iLinea + 1, iConsigna)
        assertTrue(conReto.contains(etiquetaDesafioFinde(idiomaApp)))
        assertTrue(unidos, conReto.contains("${etiquetaBibliotecaTema(idiomaApp)}:"))
        assertTrue(unidos, conReto.contains(topics.nombre(ficha.topicId, idiomaApp)))   // el tema, en el idioma de app
        assertTrue(unidos, conReto.contains("${etiquetaRetoHabilidad(idiomaApp)}:"))
        assertTrue("falta el titulo de la ficha como habilidad", visibles.any { it.isNotBlank() && it == limpio(ficha.titulo.resolver(if (ficha.bilingue) idiomaApp else ficha.idioma, ficha.idioma)) })

        // Condiciones: 4 filas, una por regla, segun el nivel
        assertTrue(unidos, conReto.contains(etiquetaRetoCondiciones(idiomaApp)))
        val reglas = reglasReto(idiomaApp, nivel, oralMinRedondeado(ficha.evidencia.oralMin))
        assertEquals(4, reglas.size)
        for (r in reglas) assertTrue("falta la condicion: $r", conReto.contains(r))
        assertFalse("quedo un placeholder sin resolver: $unidos", unidos.contains("{"))
        if (nivel == Nivel.A2 || nivel == Nivel.B1) {
            assertEquals(reglasReto(idiomaApp, Nivel.A2, 6), reglas) // A2 y B1 comparten las condiciones
        } else {
            assertTrue(reglas[1].contains(oralMinRedondeado(ficha.evidencia.oralMin).toString()))
            assertNotEquals(reglasReto(idiomaApp, Nivel.A2, 6), reglas)
        }

        // Frases para usar: hasta 3, en el idioma que se aprende, con el encabezado en idioma de app
        assertTrue(unidos, conReto.contains(etiquetaRetoFrasesTitulo(idiomaApp)))
        assertTrue(unidos, conReto.contains(textoRetoFrasesAyuda(idiomaApp)))
        val frases = expresionesDeLaFicha(ficha, 3)
        assertEquals(3, frases.size)
        for (fr in frases) assertTrue("falta la frase $fr", conReto.contains(fr))
        val cuarta = ficha.redemittel.getOrNull(3)?.expresion?.trim()
        if (cuarta != null && cuarta !in frases) assertFalse("no debe haber mas de 3 frases", conReto.contains(cuarta))
    }

    @Test
    fun `A2 con la app en espanol`() = verificar(Nivel.A2, Idioma.ES)

    @Test
    fun `B1 con la app en ingles`() = verificar(Nivel.B1, Idioma.EN)

    @Test
    fun `B2 con la app en aleman`() = verificar(Nivel.B2, Idioma.DE)

    @Test
    fun `C1 con la app en frances`() = verificar(Nivel.C1, Idioma.FR)

    @Test
    fun `C2 con la app en portugues`() = verificar(Nivel.C2, Idioma.PT)

    @Test
    fun `las condiciones existen en los 6 idiomas para todos los niveles, una idea corta por fila`() {
        for (idioma in Idioma.entries) for (nivel in Nivel.entries) {
            val reglas = reglasReto(idioma, nivel, 7)
            assertEquals("$idioma $nivel", 4, reglas.size)
            assertTrue(reglas.all { it.isNotBlank() && !it.contains("{") && it.length <= 80 })
            assertTrue("$idioma $nivel: sin numeracion", reglas.none { it.matches(Regex("""^\d+\..*""")) })
            if (nivel == Nivel.B2 || nivel == Nivel.C1 || nivel == Nivel.C2) assertTrue("$idioma $nivel: falta oralMin", reglas[1].contains("7"))
        }
        for (idioma in Idioma.entries) assertNotEquals(reglasReto(idioma, Nivel.A2, 6), reglasReto(idioma, Nivel.C1, 6))
        for (idioma in Idioma.entries) {
            val linea = textoRetoConsignaLinea(idioma, 7)
            assertTrue("$idioma: sin traducir", linea.isNotBlank() && linea != textoRetoConsignaLinea(Idioma.ES, 7) || idioma == Idioma.ES)
            assertTrue("$idioma: lleva los minutos", linea.contains("7 "))
            assertFalse("$idioma: placeholder sin resolver: $linea", linea.contains("{") || linea.contains("}"))
            assertTrue(etiquetaRetoHabilidad(idioma).isNotBlank())
        }
        assertEquals("Esto es lo que vas a contar. En voz alta, 7 min, de una sola vez y sin escribir nada:", textoRetoConsignaLinea(Idioma.ES, 7))
        assertEquals("This is what you'll talk about. Out loud, 7 min, in one go, with no writing:", textoRetoConsignaLinea(Idioma.EN, 7))
        // Ronda L: sin el parentesis que aclaraba la contradiccion, en los 6 idiomas
        for (idioma in Idioma.entries) {
            val linea = textoRetoConsignaLinea(idioma, 7)
            assertFalse("$idioma: quedo un parentesis: $linea", linea.contains("(") || linea.contains(")"))
        }
        // RETO_LINEA («Dilo en voz alta…») se fundio en la linea nueva: no queda la vieja
        assertFalse(ClaveTexto.entries.any { it.name == "RETO_LINEA" })
    }

    @Test
    fun `una ficha sin expresiones no muestra la seccion de frases`() {
        val ficha = fichaDe(Nivel.B2).copy(redemittel = emptyList())
        activarReto(ficha, Idioma.ES)
        composeTestRule.onAllNodesWithText(etiquetaRetoFrasesTitulo(Idioma.ES)).assertCountEqualsCero()
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteractionCollection.assertCountEqualsCero() {
        assertEquals(0, fetchSemanticsNodes().size)
    }

    @Test
    fun `el reto no persiste nada, cada apertura arranca en off (regla dura 13)`() {
        val ficha = fichaDe(Nivel.B2)
        lateinit var reabrir: () -> Unit
        composeTestRule.setContent {
            var version by remember { mutableIntStateOf(0) }
            reabrir = { version++ }
            key(version) { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado, topicNombres = topics) }
        }
        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.waitForIdle()
        assertTrue(textosEnPantalla().contains(etiquetaRetoCondiciones(Idioma.ES)))

        composeTestRule.runOnIdle(reabrir)
        composeTestRule.waitForIdle()
        assertFalse(textosEnPantalla().contains(etiquetaRetoCondiciones(Idioma.ES)))
    }

    @Test
    fun `P4 el bloque completo del desafio es distinto de la mision, y no vuelve a mostrar requisitos ni micro-tareas`() {
        val ficha = fichaDe(Nivel.B2)
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES, fecha = sabado, topicNombres = topics) }
        val mision = textosEnPantalla().map { limpio(it) }
        val consigna = fragmentos(ficha.mision.consigna, ficha, ficha.idioma)
        // la mision completa (en la ficha normal) trae consigna + requisitos + micro-tareas
        assertTrue(mision.any { it.contains(consigna) })
        assertTrue(ficha.mision.requisitos.all { r -> mision.any { it.contains(fragmentos(r, ficha, ficha.idioma)) } })
        assertTrue(ficha.microtareas.all { m -> mision.any { it.contains(fragmentos(m.texto, ficha, ficha.idioma)) } })

        composeTestRule.onNode(hasText(etiquetaDesafioFinde(Idioma.ES))).performClick()
        composeTestRule.waitForIdle()
        val desafio = textosEnPantalla().map { limpio(it) }
        // la consigna se reusa; nada mas de la mision
        assertTrue(desafio.any { it.contains(consigna) })
        assertTrue("requisitos en el desafio", ficha.mision.requisitos.none { r -> desafio.any { it.contains(fragmentos(r, ficha, ficha.idioma)) } })
        assertTrue("micro-tareas en el desafio", ficha.microtareas.none { m -> desafio.any { it.contains(fragmentos(m.texto, ficha, ficha.idioma)) } })
        // y el bloque visible, completo, no es el de la mision (tiene encuadre propio y le faltan partes)
        assertNotEquals(mision.toSet(), desafio.toSet())
        assertTrue("el desafio trae textos propios", (desafio.toSet() - mision.toSet()).isNotEmpty())
        assertTrue("a la mision completa le falta lo que el desafio quita", (mision.toSet() - desafio.toSet()).size >= ficha.mision.requisitos.size)
    }

    @Test
    fun `una ficha sin consigna no queda sin encabezado, cae a la descripcion`() {
        val base = fichaDe(Nivel.B2)
        val sinConsigna = base.copy(mision = base.mision.copy(consigna = TextoBilingue.de("")))
        activarReto(sinConsigna, Idioma.ES)
        val visibles = textosEnPantalla().map { limpio(it) }
        assertTrue("sin consigna ni descripcion de reemplazo: $visibles", visibles.any { it.contains(fragmentos(sinConsigna.descripcion, sinConsigna, sinConsigna.idioma)) })
        // la linea oral sigue, y el resto del bloque tambien
        assertTrue(visibles.contains(textoRetoConsignaLinea(Idioma.ES, oralMinRedondeado(sinConsigna.evidencia.oralMin))))
        assertTrue(visibles.contains(etiquetaRetoCondiciones(Idioma.ES)))
    }

    @Test
    fun `consignaDelDesafio cae de la consigna a la descripcion y de ahi al titulo`() {
        assertEquals("Ve a Berlín", consignaDelDesafio("  Ve a Berlín ", "desc", "titulo"))
        assertEquals("desc", consignaDelDesafio("", "desc", "titulo"))
        assertEquals("desc", consignaDelDesafio("   ", " desc ", "titulo"))
        assertEquals("titulo", consignaDelDesafio("", " ", "titulo"))
        assertEquals("", consignaDelDesafio("", "", ""))
    }
}
