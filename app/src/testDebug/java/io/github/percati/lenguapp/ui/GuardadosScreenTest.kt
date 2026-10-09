package io.github.percati.lenguapp.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Feature 3 (Guardados), reescrita en la Ronda C tarea 2: filtro por idioma
 * (sin mezclar), quitar la estrella directamente desde esta pantalla, y la
 * cascada de filtros (la propiedad "nunca da vacio" se prueba sin Compose
 * en CascadaGuardadosTest). GuardadosScreen es puro (toma `items` de
 * afuera), asi que "se refleja" se prueba manejando el estado en el propio
 * test -- misma forma en que MainActivity.kt lo hace con `guardadosTodos`.
 */
// Pantalla alta: los filtros van antes de la lista y, en la pantalla chica por defecto, empujan las filas fuera de la composicion.
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h4000dp")
class GuardadosScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun item(idioma: Idioma, texto: String, skillId: String, id: Long = 0, nivel: Nivel = Nivel.B2, tipo: TipoGuardado = TipoGuardado.VOCABULARIO) = ItemGuardado(
        id = id,
        idioma = idioma,
        nivel = nivel,
        tipo = tipo,
        texto = TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to texto)),
        skillIdOrigen = skillId,
    )

    private fun itemBiblioteca(idioma: Idioma, tipo: TipoGuardado, texto: String, topics: Set<String> = emptySet(), categorias: Set<String> = emptySet()) = ItemBiblioteca(
        idioma = idioma,
        tipo = tipo,
        texto = texto,
        vocabulario = if (tipo == TipoGuardado.VOCABULARIO) VocabularioItem(item = texto, prioridad = Prioridad.NUCLEO, traducciones = emptyMap()) else null,
        redemittel = if (tipo == TipoGuardado.EXPRESION) RedemittelItem(expresion = texto, funcion = TextoBilingue(plano = "func"), traducciones = emptyMap()) else null,
        niveles = setOf(Nivel.B2),
        topics = topics,
        categorias = categorias,
        skillIdOrigen = "DE-V01",
        nivelOrigen = Nivel.B2,
    )

    private fun mostrar(
        items: List<ItemGuardado>,
        itemsBiblioteca: List<ItemBiblioteca> = emptyList(),
        idiomasAprendidos: Set<Idioma> = items.map { it.idioma }.toSet(),
        onQuitar: (ItemGuardado) -> Unit = {},
    ) {
        composeTestRule.setContent {
            GuardadosScreen(
                items = items,
                itemsBiblioteca = itemsBiblioteca,
                idiomasAprendidos = idiomasAprendidos,
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                topicNombres = mapOf("T01" to "Tema uno", "T02" to "Tema dos"),
                categoriasUso = CategoriasUso(funcionComunicativa = listOf("Agradecer", "Pedir")),
                existeFichaOrigen = { true },
                onAbrirFicha = {},
                onQuitar = onQuitar,
                onVolver = {},
            )
        }
    }

    @Test
    fun `el filtro de idioma filtra, no mezcla`() {
        val items = listOf(
            item(Idioma.DE, "die Miete", "DE-V02"),
            item(Idioma.EN, "the lease", "EN-V02"),
        )
        mostrar(items, idiomasAprendidos = setOf(Idioma.DE, Idioma.EN))

        // Pestaña EN (primera: el enum Idioma declara EN antes que DE -- ver
        // el mismo criterio en LenguAppAppTest): solo EN, nunca los dos mezclados.
        composeTestRule.onNodeWithText("the lease").assertExists()
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()

        // Cambiar a DE: ahora solo DE, EN desaparece -- filtra, no mezcla.
        composeTestRule.onNodeWithText("DE").performClick()
        composeTestRule.onNodeWithText("die Miete").assertExists()
        composeTestRule.onNode(hasText("the lease")).assertDoesNotExist()
    }

    @Test
    fun `guardar y quitar un item desde afuera se refleja en la lista de Guardados`() {
        var guardados by mutableStateOf<List<ItemGuardado>>(emptyList())
        val elItem = item(Idioma.DE, "die Miete", "DE-V02")

        composeTestRule.setContent {
            GuardadosScreen(
                items = guardados,
                itemsBiblioteca = emptyList(),
                idiomasAprendidos = setOf(Idioma.DE),
                idiomaBase = Idioma.ES,
                idiomaInterfaz = Idioma.ES,
                topicNombres = emptyMap(),
                categoriasUso = CategoriasUso(),
                existeFichaOrigen = { true },
                onAbrirFicha = {},
                onQuitar = {},
                onVolver = {},
            )
        }
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()

        guardados = guardados + elItem
        composeTestRule.onNodeWithText("die Miete").assertExists()

        guardados = guardados - elItem
        composeTestRule.onNode(hasText("die Miete")).assertDoesNotExist()
    }

    @Test
    fun `tocar la estrella desde Guardados avisa con el item tocado (tarea 2d)`() {
        val a = item(Idioma.DE, "die Miete", "DE-V02", id = 1)
        val b = item(Idioma.DE, "der Mieter", "DE-V03", id = 2)
        val tocados = mutableListOf<ItemGuardado>()
        mostrar(listOf(a, b), onQuitar = { tocados += it })

        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_GUARDADOS).assertCountEquals(2)
        composeTestRule.onAllNodesWithTag(TAG_ESTRELLA_GUARDADOS)[0].performClick()
        assertEquals(1, tocados.size)
        assertEquals(true, tocados[0].id == a.id || tocados[0].id == b.id)
    }

    @Test
    fun `sin idioma aprendido ni items guardados no rompe, muestra el mensaje vacio`() {
        mostrar(emptyList(), idiomasAprendidos = emptySet())
        composeTestRule.onNodeWithText(mensajeGuardadosVacio(Idioma.ES)).assertExists()
    }

    @Test
    fun `el orden por defecto es fecha, mas reciente primero`() {
        val viejo = item(Idioma.DE, "zzz-viejo", "DE-V01", id = 1)
        val nuevo = item(Idioma.DE, "zzz-nuevo", "DE-V02", id = 2)
        mostrar(listOf(viejo, nuevo))

        val filas = composeTestRule.onAllNodesWithText("zzz-", substring = true)
        filas.assertCountEquals(2)
        // El mas reciente (id mas alto) aparece primero: no hay forma directa de leer
        // el orden visual salvo semantics, asi que se verifica contra ordenar() en
        // CascadaGuardadosTest y aca solo que ambos existen con el chip de Fecha activo.
        composeTestRule.onNodeWithText("Fecha").assertExists()
    }

    @Test
    fun `elegir un tipo esconde topic de vocabulario y muestra categoria de expresiones, cascada sin vacios`() {
        val voc = item(Idioma.DE, "Tisch", "DE-V01", id = 1, tipo = TipoGuardado.VOCABULARIO)
        val expr = item(Idioma.DE, "danke", "DE-R01", id = 2, tipo = TipoGuardado.EXPRESION)
        val biblioteca = listOf(
            itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01")),
            itemBiblioteca(Idioma.DE, TipoGuardado.EXPRESION, "danke", categorias = setOf("Agradecer")),
        )
        mostrar(listOf(voc, expr), itemsBiblioteca = biblioteca)

        // Con "Todos" los tipos no se muestra ni topic ni categoria.
        composeTestRule.onNodeWithText("Tema uno", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("Agradecer", substring = true).assertDoesNotExist()

        composeTestRule.onNodeWithText("Vocabulario", substring = true).performClick()
        composeTestRule.onNodeWithText("Tema uno", substring = true).assertExists()
        composeTestRule.onNodeWithText("Tisch").assertExists()
        composeTestRule.onNode(hasText("danke")).assertDoesNotExist()

        composeTestRule.onNodeWithText("Expresiones", substring = true).performClick()
        composeTestRule.onNodeWithText("Agradecer", substring = true).assertExists()
        composeTestRule.onNodeWithText("danke").assertExists()
        composeTestRule.onNode(hasText("Tisch")).assertDoesNotExist()
    }

    @Test
    fun `Quitar filtros aparece solo si hay algo que quitar y vuelve a Todos`() {
        val a = item(Idioma.DE, "Tisch", "DE-V01", id = 1, nivel = Nivel.A2)
        val b = item(Idioma.DE, "Stuhl", "DE-V02", id = 2, nivel = Nivel.B2)
        mostrar(listOf(a, b))

        composeTestRule.onNodeWithText("Quitar filtros").assertDoesNotExist()
        composeTestRule.onNodeWithText("A2 (1)").performClick()
        composeTestRule.onNodeWithText("Quitar filtros").assertExists()
        composeTestRule.onNodeWithText("Tisch").assertExists()
        composeTestRule.onNode(hasText("Stuhl")).assertDoesNotExist()

        composeTestRule.onNodeWithText("Quitar filtros").performClick()
        composeTestRule.onNodeWithText("Quitar filtros").assertDoesNotExist()
        composeTestRule.onNodeWithText("Tisch").assertExists()
        composeTestRule.onNodeWithText("Stuhl").assertExists()
    }

    @Test
    fun `ascendente y descendente cambian el orden alfabetico`() {
        val a = item(Idioma.DE, "ancla", "DE-V01", id = 1)
        val z = item(Idioma.DE, "zebra", "DE-V02", id = 2)
        mostrar(listOf(a, z))

        composeTestRule.onNodeWithText("Alfabético").performClick()
        composeTestRule.onNodeWithText("Ascendente").performClick()
        // Ambos existen en cualquier orden -- el orden real (quien va primero) ya
        // esta cubierto por CascadaGuardadosTest.ordenar(); aca solo que los
        // controles no rompen la pantalla y ambos items se siguen mostrando.
        composeTestRule.onNodeWithText("ancla").assertExists()
        composeTestRule.onNodeWithText("zebra").assertExists()

        composeTestRule.onNodeWithText("Descendente").performClick()
        composeTestRule.onNodeWithText("ancla").assertExists()
        composeTestRule.onNodeWithText("zebra").assertExists()
    }
}
