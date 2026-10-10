package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import io.github.percati.lenguapp.presentacion.datosFila
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * Guardados muestra lo mismo que la Biblioteca (Ronda F2, tarea 1): original en el idioma que se
 * aprende, traduccion al idioma de app y funcion en el idioma que se aprende. Sobre fichas reales,
 * con distintos idiomas aprendidos / de app / niveles.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class GuardadosFilaRealTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun ficha(aprendido: Idioma, nivel: Nivel, app: Idioma): Ficha {
        val carpeta = listOf(File("src/main/assets/contenido"), File("app/src/main/assets/contenido")).first { it.isDirectory }
        return carpeta.listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>()
            .first { f ->
                f.idioma == aprendido && f.nivel == nivel &&
                    f.vocabulario.any { !it.traducciones[app.name.lowercase()].isNullOrBlank() } &&
                    f.redemittel.any { !it.traducciones[app.name.lowercase()].isNullOrBlank() && it.funcion.resolver(aprendido, aprendido).isNotBlank() }
            }
    }

    private fun textosEnPantalla(): List<String> =
        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .flatMap { n -> n.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text } }

    private fun verificar(aprendido: Idioma, nivel: Nivel, app: Idioma) {
        val f = ficha(aprendido, nivel, app)
        val items = construirBiblioteca(listOf(f))
        val v = items.first { it.tipo == io.github.percati.lenguapp.modelo.TipoGuardado.VOCABULARIO && !it.vocabulario!!.traducciones[app.name.lowercase()].isNullOrBlank() }
        val r = items.first { it.tipo == io.github.percati.lenguapp.modelo.TipoGuardado.EXPRESION && !it.redemittel!!.traducciones[app.name.lowercase()].isNullOrBlank() && it.funcion != null }
        val guardados = listOf(v, r).mapIndexed { i, it ->
            ItemGuardado(
                id = (i + 1).toLong(), idioma = it.idioma, nivel = it.nivelOrigen, tipo = it.tipo,
                texto = it.textoBilingue(), funcion = it.funcion, skillIdOrigen = it.skillIdOrigen,
            )
        }
        composeTestRule.setContent {
            GuardadosScreen(
                items = guardados, itemsBiblioteca = items, idiomasAprendidos = setOf(aprendido),
                idiomaBase = app, idiomaInterfaz = app,
                topicNombres = NombresI18n(), categoriasUso = CategoriasUso(),
                existeFichaOrigen = { true }, onAbrirFicha = {}, onQuitar = {}, onVolver = {},
            )
        }
        fun limpio(t: String) = t.replace("«", "").replace("»", "").replace("*", "")
        val visibles = textosEnPantalla().map { limpio(it) }
        val tag = "$aprendido $nivel app=$app"

        val dV = v.datosFila(app)
        val dR = r.datosFila(app)
        // 1. el original, en el idioma que se aprende
        assertTrue("$tag: falta el original ${dV.original}", dV.original in visibles)
        assertTrue("$tag: falta el original ${dR.original}", dR.original in visibles)
        // 2. la traduccion, en el idioma de app
        assertTrue("$tag: falta la traduccion de ${dV.original}", dV.traduccion != null && limpio(dV.traduccion!!) in visibles)
        assertTrue("$tag: falta la traduccion de ${dR.original}", dR.traduccion != null && limpio(dR.traduccion!!) in visibles)
        // 3. la funcion de la expresion, en el idioma que se aprende
        assertTrue("$tag: falta la funcion", dR.funcion != null && limpio(dR.funcion!!) in visibles)
        // la funcion NO esta en el idioma de app (cuando son idiomas distintos)
        val funcionEnApp = r.funcion!!.porIdioma[app.name.lowercase()]
        if (app != aprendido && !funcionEnApp.isNullOrBlank() && funcionEnApp != dR.funcion) {
            assertFalse("$tag: la funcion salio en el idioma de app", limpio(funcionEnApp) in visibles)
        }
    }

    @Test
    fun `EN A2 con la app en espanol`() = verificar(Idioma.EN, Nivel.A2, Idioma.ES)

    @Test
    fun `DE B1 con la app en ingles`() = verificar(Idioma.DE, Nivel.B1, Idioma.EN)

    @Test
    fun `DE B2 con la app en frances`() = verificar(Idioma.DE, Nivel.B2, Idioma.FR)

    @Test
    fun `EN B2 con la app en aleman`() = verificar(Idioma.EN, Nivel.B2, Idioma.DE)
}
