package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
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

/** Biblioteca (Ronda B, pieza 4): nivel por defecto, nivel multi-seleccion dentro de la pantalla y estrella. */
// Pantalla alta: los filtros van dentro del LazyColumn y, en la pantalla chica por defecto, empujan las filas fuera de la composicion.
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class BibliotecaScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun base(): ItemBiblioteca {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val ficha = parsearContenido(File(assets, "contenido/DE-F01-A2-2026-1.json").readText()) as Ficha
        return construirBiblioteca(listOf(ficha)).first { it.tipo == TipoGuardado.VOCABULARIO }
    }

    private fun items(): List<ItemBiblioteca> {
        val b = base()
        return listOf(
            b.copy(texto = "zzz-actual", niveles = setOf(Nivel.B2)),
            b.copy(texto = "zzz-anterior", niveles = setOf(Nivel.A2)),
        )
    }

    private fun mostrar(guardadas: Set<String> = emptySet(), onAlternar: (ItemBiblioteca) -> Unit = {}) {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = items(),
                idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(mapOf("T01" to mapOf("es" to "Tema uno"))),
                categorias = CategoriasUso(),
                guardadas = guardadas,
                onAlternar = onAlternar,
                onVolver = {},
            )
        }
    }

    @Test
    fun `arranca en el nivel actual del idioma y se cambia dentro de la pantalla`() {
        mostrar()
        composeTestRule.onAllNodesWithText("zzz-actual").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-anterior").assertCountEquals(0)

        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onAllNodesWithText("zzz-anterior").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-actual").assertCountEquals(1)
    }

    @Test
    fun `la estrella avisa con el item tocado`() {
        val tocados = mutableListOf<String>()
        mostrar(onAlternar = { tocados += it.texto })
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_BIBLIOTECA)[0].performClick()
        assertEquals(listOf("zzz-actual"), tocados)
    }

    @Test
    fun `el titulo y el buscador van en el idioma de la app`() {
        mostrar()
        composeTestRule.onAllNodesWithText("Biblioteca").assertCountEquals(1)
        composeTestRule.onNodeWithText("Buscar").assertExists()
    }

    @Test
    fun `Quitar filtros solo aparece si hay algo que quitar y devuelve el nivel al default`() {
        mostrar()
        composeTestRule.onNodeWithText("Quitar filtros").assertDoesNotExist()

        composeTestRule.onNodeWithText("A2").performClick()
        composeTestRule.onNodeWithText("Quitar filtros").assertExists()
        composeTestRule.onAllNodesWithText("zzz-anterior").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-actual").assertCountEquals(1)

        composeTestRule.onNodeWithText("Quitar filtros").performClick()
        composeTestRule.onNodeWithText("Quitar filtros").assertDoesNotExist()
        composeTestRule.onAllNodesWithText("zzz-actual").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-anterior").assertCountEquals(0)
    }

    private fun itemsConCategorias(): List<ItemBiblioteca> {
        val b = base().copy(tipo = TipoGuardado.EXPRESION, niveles = setOf(Nivel.B2))
        return listOf(
            b.copy(texto = "zzz-pedir", categorias = setOf("Pedir")),
            b.copy(texto = "zzz-sugerir", categorias = setOf("Sugerir")),
        )
    }

    private fun mostrarExpresiones() {
        composeTestRule.setContent {
            BibliotecaScreen(
                items = itemsConCategorias(),
                idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                topicNombres = NombresI18n(mapOf("T01" to mapOf("es" to "Tema uno"))),
                categorias = CategoriasUso(funcionComunicativa = listOf("Pedir", "Sugerir")),
                guardadas = emptySet(),
                onAlternar = {},
                onVolver = {},
            )
        }
        composeTestRule.onNodeWithText("Expresiones").performClick()
    }

    @Test
    fun `el boton Quitar de una rama de categorias solo borra esa rama`() {
        mostrarExpresiones()
        composeTestRule.onAllNodesWithText("Quitar").assertCountEquals(0)

        composeTestRule.onNodeWithText("Pedir").performClick()
        composeTestRule.onAllNodesWithText("zzz-pedir").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-sugerir").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("Quitar").assertCountEquals(1)

        composeTestRule.onAllNodesWithText("Quitar")[0].performClick()
        composeTestRule.onAllNodesWithText("Quitar").assertCountEquals(0)
        composeTestRule.onAllNodesWithText("zzz-pedir").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("zzz-sugerir").assertCountEquals(1)
    }
}
