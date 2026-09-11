package io.github.percati.lenguapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.percati.lenguapp.datos.parsearCalendario
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.semana.CalendarioCargado
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.resolverContenidoDeLaSemana
import io.github.percati.lenguapp.semana.semanaIsoDe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.time.LocalDate

/**
 * B2 (navegacion) y B3 (selector de idioma) de la Fase 5, de punta a punta:
 * usa los assets reales, no dobles de prueba, para que un cambio en el
 * resolutor o en el cableado de MainActivity se note aca.
 */
@RunWith(RobolectricTestRunner::class)
class LenguAppAppTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun carpetaAssets(): File =
        listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

    private fun cargarContenido(): Map<String, ContenidoSemanal> =
        File(carpetaAssets(), "contenido").listFiles { f -> f.extension == "json" }!!
            .associate { val c = parsearContenido(it.readText()); c.id to c }

    private fun resolverDePrueba(idioma: Idioma, nivel: Nivel, fecha: LocalDate): ResultadoSemana {
        val nombreCalendario = "calendario_2026_${idioma.name.lowercase()}_${nivel.name}.json"
        val archivoCalendario = File(carpetaAssets(), "calendario/$nombreCalendario")
        val calendario = if (archivoCalendario.isFile) {
            CalendarioCargado.Encontrado(parsearCalendario(archivoCalendario.readText()))
        } else {
            CalendarioCargado.SinCalendarioParaIdiomaONivel
        }
        return resolverContenidoDeLaSemana(fecha, idioma, nivel, calendario, cargarContenido())
    }

    @Test
    fun `el conmutador de idioma cambia a ingles y mantiene la misma semana`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomaAprendido = Idioma.DE, nivel = Nivel.B2),
                variantesConocidas = emptySet(),
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        // Semana 37 de 2026 en aleman: DE-G01.
        composeTestRule.onNode(hasText("Satzbau", substring = true)).assertExists()

        // La barra de navegacion no esta dentro del scroll de la ficha, asi
        // que siempre esta a la vista: no hace falta performScrollTo().
        composeTestRule.onNodeWithText("EN").performClick()

        // Misma semana (37), ahora en ingles: EN-F01. Si el nivel no
        // cambiara junto con el idioma (B3 rompiendo B2 sin querer), esto
        // mostraria "sin contenido" en vez del titulo.
        composeTestRule.onNode(hasText("Giving and Asking for Opinions", substring = true)).assertExists()
    }

    @Test
    fun `la barra muestra la semana ISO actual`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomaAprendido = Idioma.DE, nivel = Nivel.B2),
                variantesConocidas = emptySet(),
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        val semanaIso = semanaIsoDe(LocalDate.now())
        composeTestRule.onNode(hasText("Semana ${semanaIso.semana} · ${semanaIso.anio}", substring = true)).assertExists()
    }

    @Test
    fun `una semana de 2026 fuera de la edicion (1-36) muestra el motivo, no en blanco`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomaAprendido = Idioma.DE, nivel = Nivel.B2),
                variantesConocidas = emptySet(),
                resolver = { idioma, nivel, _ -> resolverDePrueba(idioma, nivel, LocalDate.of(2026, 2, 16)) }, // semana ISO 8
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNode(hasText("no forma parte de la edición actual", substring = true)).assertExists()
    }
}
