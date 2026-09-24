package io.github.percati.lenguapp.ui

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/**
 * Las dos reglas no negociables de la Fase 3, sobre una ficha real: (1)
 * cuadro/vocabulario/Redemittel arrancan plegados y se abren bajo demanda;
 * (2) el boton de copiar es la unica funcion que toca el sistema, y de
 * verdad escribe en el portapapeles del sistema operativo (no solo cambia
 * su propio texto).
 *
 * La ficha completa es mas alta que la ventana de prueba de Robolectric, asi
 * que un nodo mas alla del viewport hay que scrollearlo a la vista
 * (performScrollTo) antes de performClick(): un click en una coordenada
 * fuera de la ventana visible no llega a nada.
 */
@RunWith(RobolectricTestRunner::class)
class ContenidoSemanalScreenInteraccionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun ficha(): Ficha {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets"))
            .first { it.isDirectory }
        return parsearContenido(File(carpeta, "contenido/DE-G01-B2-2026-1.json").readText()) as Ficha
    }

    // --- Ingles como segunda lengua base: contraste (AJUSTES-FASE-8.md, B.6) y erroresContrastivos ---

    @Test
    fun `con la app en ingles aparece el contraste ingles, no el espanol, y se suman los errores contrastivos`() {
        val ficha = ficha()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.EN) }

        composeTestRule.onNode(hasText("Das Englische hat eine fast so feste Wortstellung", substring = true)).assertExists()
        composeTestRule.onNode(hasText("Das Spanische kennt keine Verbendstellung", substring = true)).assertDoesNotExist()

        // erroresContrastivos.en se agrega a los universales, no los reemplaza:
        // el universal "Komma vor dass..." sigue estando, junto al adicional
        // "Das Komma vor dass weglassen...".
        composeTestRule.onNode(hasText("Die Satzklammer auflösen", substring = true)).assertExists()
        composeTestRule.onNode(hasText("vergessen: im Deutschen Pflicht", substring = true)).assertExists()

        // Las glosas de vocabulario ahora traen ingles ademas de espanol.
        val tituloVocabulario = etiquetaSeccion("vocabulario", ficha.idioma, ficha.bilingue)
        composeTestRule.onNode(hasText(tituloVocabulario, substring = true)).performScrollTo().performClick()
        composeTestRule.onNode(hasText("the notice period", substring = true)).assertExists()
    }

    @Test
    fun `con la app en espanol aparece el contraste espanol, sin los errores contrastivos de ingles`() {
        val ficha = ficha()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNode(hasText("Das Spanische kennt keine Verbendstellung", substring = true)).assertExists()
        composeTestRule.onNode(hasText("Das Englische hat eine fast so feste Wortstellung", substring = true)).assertDoesNotExist()

        // "Die Satzklammer auflösen" es un error universal (ficha.errores, no
        // erroresContrastivos): erroresParaMostrar() siempre lo incluye, sin
        // importar idiomaBase (ver Presentacion.kt) -- por eso tambien
        // aparece aca. Lo que este test tiene que verificar es que el
        // adicional erroresContrastivos.en (que solo se suma cuando
        // idiomaBase es ingles) no aparezca con la app en espanol.
        composeTestRule.onNode(hasText("weglassen, weil es vor", substring = true)).assertDoesNotExist()
    }

    // Marcado en linea en los campos que la convencion de "palabra objetivo sin
    // traducir" usa: subtitulo, consigna, requisitos y microtareas. Ninguno
    // debe mostrar los asteriscos literales.
    @Test
    fun `el marcado con asteriscos se interpreta en subtitulo, consigna, requisitos y microtareas`() {
        val base = ficha()
        val ficha = base.copy(
            subtitulo = TextoBilingue.de("Palabra clave ${'*'}zuverlassig${'*'} en el subtitulo"),
            mision = base.mision.copy(
                consigna = TextoBilingue.de("Consigna con ${'*'}Wortstellung${'*'} sin traducir"),
                requisitos = listOf(TextoBilingue.de("Requisito con ${'*'}Nebensatz${'*'} marcado")),
            ),
            microtareas = base.microtareas.mapIndexed { i, m ->
                if (i == 0) m.copy(texto = TextoBilingue.de("Microtarea con ${'*'}Satzklammer${'*'} marcada")) else m
            },
        )
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNode(hasText("Palabra clave zuverlassig en el subtitulo")).assertExists()
        composeTestRule.onNode(hasText("Consigna con Wortstellung sin traducir")).assertExists()
        composeTestRule.onNode(hasText("Requisito con Nebensatz marcado", substring = true)).assertExists()
        composeTestRule.onNode(hasText("Microtarea con Satzklammer marcada", substring = true)).assertExists()
        composeTestRule.onNode(hasText("${'*'}", substring = true)).assertDoesNotExist()
    }

    // Campos bilingues A2/B1: el objeto {idioma: texto} se muestra en el idioma de
    // la app, y cae al idioma que se aprende si falta esa clave.
    @Test
    fun `un campo bilingue se muestra en el idioma de la app y cae al que se aprende si falta`() {
        val base = ficha()
        val ficha = base.copy(
            titulo = TextoBilingue(porIdioma = mapOf("de" to "Titel DE", "es" to "Titulo ES")),
            subtitulo = TextoBilingue(porIdioma = mapOf("de" to "Untertitel DE")),
        )
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        composeTestRule.onNode(hasText("Titulo ES")).assertExists()
        composeTestRule.onNode(hasText("Titel DE")).assertDoesNotExist()
        composeTestRule.onNode(hasText("Untertitel DE")).assertExists()
    }

    @Test
    fun `vocabulario y redemittel arrancan plegados y se abren al tocar el titulo`() {
        val ficha = ficha()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }
        // Los titulos siguen el idioma de la ficha desde la Fase 5 (B1): DE-G01
        // es alemana, asi que la seccion se llama "Themenwortschatz", no
        // "Vocabulario".
        val tituloVocabulario = etiquetaSeccion("vocabulario", ficha.idioma, ficha.bilingue)
        val tituloRedemittel = etiquetaSeccion("redemittel", ficha.idioma, ficha.bilingue)

        // Plegado: el primer item de vocabulario no esta en pantalla todavia.
        composeTestRule.onNode(hasText(ficha.vocabulario.first().item, substring = true)).assertDoesNotExist()

        composeTestRule.onNode(hasText(tituloVocabulario, substring = true)).performScrollTo().performClick()
        composeTestRule.onNode(hasText(ficha.vocabulario.first().item, substring = true)).assertExists()

        // El Redemittel sigue plegado: abrir uno no abre el otro.
        composeTestRule.onNode(hasText(ficha.redemittel.first().expresion, substring = true)).assertDoesNotExist()
        composeTestRule.onNode(hasText(tituloRedemittel, substring = true)).performScrollTo().performClick()
        composeTestRule.onNode(hasText(ficha.redemittel.first().expresion, substring = true)).assertExists()
    }

    @Test
    fun `el cuadro de referencia arranca plegado y se abre al tocar el titulo`() {
        val ficha = ficha()
        val cuadro = ficha.cuadroReferencia ?: error("DE-G01-B2-1 deberia traer cuadroReferencia")
        // La ultima columna (el ejemplo) no se repite en otra seccion de la
        // ficha, a diferencia de la primera ("Hauptsatz"), que tambien
        // aparece en la descripcion siempre visible. El texto renderizado no
        // trae los asteriscos del marcado en linea (Fase 5, bloque A): hay
        // que buscar el texto ya limpio, no el crudo del JSON.
        val celdaUnicaDeLaTabla = textoConMarcado(cuadro.filas.first().last().resolver(Idioma.ES, ficha.idioma)).text
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNode(hasText(celdaUnicaDeLaTabla, substring = true)).assertDoesNotExist()
        composeTestRule.onNodeWithText(cuadro.titulo.resolver(Idioma.ES, ficha.idioma)).performScrollTo().performClick()
        composeTestRule.onNode(hasText(celdaUnicaDeLaTabla, substring = true)).assertExists()
    }

    @Test
    fun `copiar el prompt lo escribe de verdad en el portapapeles del sistema`() {
        val ficha = ficha()
        composeTestRule.setContent { ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES) }

        composeTestRule.onNodeWithText("Copiar al portapapeles").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Copiado").assertExists()

        val portapapeles = ApplicationProvider.getApplicationContext<Context>()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val copiado = portapapeles.primaryClip?.getItemAt(0)?.text?.toString()
        assertEquals(ficha.promptCorreccion.resolver(Idioma.ES, ficha.idioma), copiado)
    }

    @Test
    fun `una semana especial sin prompt de correccion no muestra el boton de copiar`() {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val survival = parsearContenido(File(carpeta, "contenido/SURVIVAL-EN-B2-S44.json").readText())
        composeTestRule.setContent { ContenidoSemanalScreen(survival, idiomaBase = Idioma.ES) }

        composeTestRule.onNode(hasText("Copiar al portapapeles", substring = true)).assertDoesNotExist()
    }

    /**
     * Los pantallazos de ContenidoSemanalScreenRenderTest quedan siempre
     * plegados (es el estado por defecto). El cuadro de referencia expandido
     * es el punto de mayor riesgo de desborde: hasta 5 columnas con oraciones
     * largas en 360dp. Este pantallazo verifica ese caso a ojo.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `pantallazo del cuadro de referencia expandido, para inspeccion visual`() {
        val ficha = ficha()
        composeTestRule.setContent {
            Box(Modifier.size(360.dp, 800.dp)) {
                ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES)
            }
        }
        composeTestRule.onNodeWithText(ficha.cuadroReferencia!!.titulo.resolver(Idioma.ES, ficha.idioma)).performScrollTo().performClick()
        composeTestRule.waitForIdle()

        val activity = composeTestRule.activity
        val vista: View = activity.window.decorView
        val densidad = activity.resources.displayMetrics.density
        val anchoPx = (360 * densidad).toInt()
        val altoPx = (800 * densidad).toInt()
        vista.measure(
            View.MeasureSpec.makeMeasureSpec(anchoPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(altoPx, View.MeasureSpec.EXACTLY),
        )
        vista.layout(0, 0, anchoPx, altoPx)
        val bitmap = Bitmap.createBitmap(anchoPx, altoPx, Bitmap.Config.ARGB_8888)
        vista.draw(Canvas(bitmap))
        val destino = File("build/pantallazos-fase3", "DE-G01-B2-1-cuadro-expandido.png")
        destino.parentFile?.mkdirs()
        FileOutputStream(destino).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    /**
     * B4.4 y B4.6: nucleo se distingue por negrita (AJUSTES-FASE-8.md, B.5
     * saco la insignia "núcleo" pero conservo el peso tipografico); variante
     * y bajoNivelJustificado siguen con insignia. DE-G05 tiene los tres
     * casos reales.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `pantallazo del vocabulario expandido, para inspeccion visual`() {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val ficha = parsearContenido(File(carpeta, "contenido/DE-G05-B2-2026-1.json").readText()) as Ficha
        composeTestRule.setContent {
            Box(Modifier.size(360.dp, 800.dp)) {
                ContenidoSemanalScreen(ficha, idiomaBase = Idioma.ES)
            }
        }
        val tituloVocabulario = etiquetaSeccion("vocabulario", ficha.idioma, ficha.bilingue)
        composeTestRule.onNode(hasText(tituloVocabulario, substring = true)).performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNode(hasText(ficha.vocabulario.first().item, substring = true)).performScrollTo()

        val activity = composeTestRule.activity
        val vista: View = activity.window.decorView
        val densidad = activity.resources.displayMetrics.density
        val anchoPx = (360 * densidad).toInt()
        val altoPx = (800 * densidad).toInt()
        vista.measure(
            View.MeasureSpec.makeMeasureSpec(anchoPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(altoPx, View.MeasureSpec.EXACTLY),
        )
        vista.layout(0, 0, anchoPx, altoPx)
        val bitmap = Bitmap.createBitmap(anchoPx, altoPx, Bitmap.Config.ARGB_8888)
        vista.draw(Canvas(bitmap))
        val destino = File("build/pantallazos-fase3", "DE-G05-B2-1-vocabulario-expandido.png")
        destino.parentFile?.mkdirs()
        FileOutputStream(destino).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
