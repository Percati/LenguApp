package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/** Vista «Verbos» en pantalla (Ronda H): tercera vista, chips por clase, extra, estrella, estado vacio. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class VistaVerbosTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun base(): ItemBiblioteca {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val ficha = parsearContenido(File(assets, "contenido/DE-F01-A2-2026-1.json").readText()) as Ficha
        return construirBiblioteca(listOf(ficha)).first { it.tipo == TipoGuardado.VOCABULARIO }
    }

    /** Un item de vocabulario normal (el primero de la ficha real puede ser un verbo: se le quita la clase). */
    private fun noVerbo(texto: String, nivel: Nivel = Nivel.B2): ItemBiblioteca {
        val b = base()
        return b.copy(texto = texto, niveles = setOf(nivel), vocabulario = b.vocabulario!!.copy(item = texto, claseVerbo = emptyList()))
    }

    private fun verbo(texto: String, clases: List<String>, reccion: String? = null, idioma: Idioma = Idioma.DE, nivel: Nivel = Nivel.B2): ItemBiblioteca {
        val b = base()
        return b.copy(
            idioma = idioma, texto = texto, niveles = setOf(nivel),
            vocabulario = b.vocabulario!!.copy(item = texto, claseVerbo = clases, reccion = reccion, traducciones = mapOf("es" to "trad-$texto")),
        )
    }

    private fun mostrar(
        lista: List<ItemBiblioteca>,
        aprendidos: Map<Idioma, Nivel> = mapOf(Idioma.DE to Nivel.B2),
        guardadas: Set<String> = emptySet(),
        onAlternar: (ItemBiblioteca) -> Unit = {},
    ) {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = lista, idiomasAprendidos = aprendidos, idiomaBase = Idioma.ES, idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(), categorias = CategoriasUso(), guardadas = guardadas,
                onAlternar = onAlternar, onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("Verbos").performClick()
    }

    private val lista by lazy {
        listOf(
            verbo("zumachen", listOf("trennbar")),
            verbo("sich beschweren", listOf("reflexivo", "mit-Praposition"), reccion = "über + Akk"),
            verbo("arbeiten", listOf("simple")),
            verbo("aufmachen", listOf("trennbar")),
            verbo("verbo-futuro", listOf("clase-del-futuro")),
            noVerbo("kein-verbo"), // vocabulario normal: no entra
        )
    }

    @Test
    fun `la Biblioteca tiene tres vistas y Verbos lista los verbos del nivel, alfabeticos, sin los que no son verbo`() {
        mostrar(lista)
        composeTestRule.onNodeWithText("Vocabulario y expresiones").assertExists()
        composeTestRule.onNodeWithText("Habilidades").assertExists()
        for (v in listOf("aufmachen", "arbeiten", "sich beschweren", "verbo-futuro", "zumachen")) composeTestRule.onNodeWithText(v).assertExists()
        composeTestRule.onAllNodesWithText("kein-verbo").assertCountEquals(0)
        // alfabetico por el original
        val tops = listOf("arbeiten", "aufmachen", "sich beschweren", "verbo-futuro", "zumachen").map {
            composeTestRule.onAllNodes(hasText(it)).fetchSemanticsNodes().first().boundsInRoot.top
        }
        assertEquals(tops.sorted(), tops)
    }

    @Test
    fun `los chips son solo las clases presentes con su cuenta, simple y la desconocida no`() {
        mostrar(lista)
        composeTestRule.onNodeWithText("Separable (2)").assertExists()
        composeTestRule.onNodeWithText("Reflexivo (1)").assertExists()
        composeTestRule.onNodeWithText("Con preposición (1)").assertExists()
        // no hay verbos con dativo ni phrasals: sin chip
        composeTestRule.onAllNodes(hasText("Con dativo", substring = true)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasText("phrasal", substring = true, ignoreCase = true)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasText("simple", substring = true, ignoreCase = true)).assertCountEquals(0)
        composeTestRule.onAllNodes(hasText("clase-del-futuro", substring = true)).assertCountEquals(0)
    }

    @Test
    fun `elegir un chip filtra, elegir otro suma, y quitarlos devuelve todos`() {
        mostrar(lista)
        composeTestRule.onNodeWithText("Separable (2)").performClick()
        composeTestRule.onNodeWithText("zumachen").assertExists()
        composeTestRule.onAllNodesWithText("arbeiten").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("sich beschweren").assertCountEquals(0)
        composeTestRule.onNodeWithText("Reflexivo (1)").performClick()
        composeTestRule.onNodeWithText("sich beschweren").assertExists()
        composeTestRule.onNodeWithText("Separable (2)").performClick()
        composeTestRule.onNodeWithText("Reflexivo (1)").performClick()
        composeTestRule.onNodeWithText("arbeiten").assertExists() // los simple, con todos
    }

    @Test
    fun `la linea discreta lleva el nivel y, en aleman, el regimen, y sin regimen solo el nivel`() {
        mostrar(lista)
        composeTestRule.onNodeWithText("B2 · über + Akk").assertExists()
        composeTestRule.onAllNodes(hasText("+ Dat", substring = true)).assertCountEquals(0)
        // los verbos sin regimen dibujan solo el nivel
        composeTestRule.onAllNodesWithText("B2").assertCountEquals(lista.count { it.vocabulario?.claseVerbo?.isNotEmpty() == true } - 1)
    }

    @Test
    fun `en ingles la separabilidad se ve hasta que el chip la dice`() {
        val en = listOf(
            verbo("to pay back", listOf("phrasal-separable"), idioma = Idioma.EN, nivel = Nivel.B1),
            verbo("to get off", listOf("phrasal-inseparable"), idioma = Idioma.EN, nivel = Nivel.B1),
        )
        mostrar(en, aprendidos = mapOf(Idioma.EN to Nivel.B1))
        // sin chip elegido: cada fila dice su nivel y su clase (ademas de estar en el chip, con su cuenta)
        composeTestRule.onNodeWithText("B1 · Phrasal separable").assertExists()
        composeTestRule.onNodeWithText("B1 · Phrasal inseparable").assertExists()
        composeTestRule.onNodeWithText("Phrasal separable (1)").performClick()
        // con el chip: la fila ya no repite la clase (queda el nivel y el chip)
        composeTestRule.onAllNodes(hasText("Phrasal separable", substring = true)).assertCountEquals(1)
        composeTestRule.onNodeWithText("B1").assertExists()
    }

    @Test
    fun `la estrella es la misma de las otras vistas, avisa con el item y su clave de Guardados`() {
        val tocados = mutableListOf<ItemBiblioteca>()
        mostrar(lista, guardadas = setOf("DE|VOCABULARIO|zumachen"), onAlternar = { tocados += it })
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA)[0].performClick() // arbeiten (primero)
        assertEquals("arbeiten", tocados.single().texto)
        assertEquals(TipoGuardado.VOCABULARIO, tocados.single().tipo)
    }

    @Test
    fun `un par sin verbos etiquetados muestra la invitacion breve, sin chips`() {
        mostrar(listOf(noVerbo("sustantivo")))
        composeTestRule.onNodeWithText(mensajeVerbosVacio(Idioma.ES)).assertExists()
        composeTestRule.onAllNodes(hasText("(", substring = true)).assertCountEquals(0)
    }

    @Test
    fun `el nivel de Ajustes no recorta, se ven los verbos de todos los niveles, cada uno con su nivel`() {
        val otroNivel = verbo("hochladen", listOf("trennbar"), nivel = Nivel.C1)
        val a2 = verbo("anmelden", listOf("trennbar"), nivel = Nivel.A2)
        mostrar(lista + otroNivel + a2) // Ajustes: DE en B2
        composeTestRule.onNodeWithText("hochladen").assertExists()
        composeTestRule.onNodeWithText("anmelden").assertExists()
        composeTestRule.onNodeWithText("C1").assertExists()
        composeTestRule.onNodeWithText("A2").assertExists()
        // y la vista ya no encabeza con un "Nivel B2"
        composeTestRule.onAllNodes(hasText("Nivel B2", substring = true)).assertCountEquals(0)
    }

    @Test
    fun `no hay filtro de nivel, ni chips ni selector de niveles en la vista`() {
        // con verbos de varios niveles: la linea extra SI muestra el nivel (dato), pero ningun control lo ofrece como filtro
        val variados = listOf(
            verbo("anmelden", listOf("trennbar"), nivel = Nivel.A2), verbo("aufmachen", listOf("trennbar"), nivel = Nivel.B1),
            verbo("hochladen", listOf("trennbar"), nivel = Nivel.C1), verbo("beschweren", listOf("reflexivo"), nivel = Nivel.C2),
        )
        mostrar(variados)
        for (n in listOf("A2", "B1", "C1", "C2")) {
            composeTestRule.onNodeWithText(n).assertExists() // como dato de la fila
            composeTestRule.onAllNodes(hasText(n) and hasClickAction()).assertCountEquals(0) // nunca como control (chip)
        }
        // los unicos controles de filtro son los de clase: el grupo de chips del lexico (niveles) no se compone aca
        composeTestRule.onAllNodes(hasText("Nivel", substring = true)).assertCountEquals(0)
    }
}
