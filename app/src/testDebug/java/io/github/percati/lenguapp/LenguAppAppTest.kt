package io.github.percati.lenguapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
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
import io.github.percati.lenguapp.ui.mensajeDobleAtrasParaSalir
import io.github.percati.lenguapp.ui.mensajeSinContenidoSemana
import io.github.percati.lenguapp.ui.mensajeSinContenidoTitulo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast
import java.io.File
import java.time.LocalDate

/**
 * B2 (navegacion) de la Fase 5 y B (multi-idioma con pestañas) de la Fase 6,
 * de punta a punta: usa los assets reales, no dobles de prueba, para que un
 * cambio en el resolutor o en el cableado de MainActivity se note aca.
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

    // Mismos (idioma, nivel) que usan las ajustesIniciales de este archivo:
    // DE en B2, EN en C1. No hace falta que coincida exacto con
    // idiomasConContenido de cada test -- una clave de mas no molesta.
    private val nivelesDePrueba = mapOf(Idioma.DE to setOf(Nivel.B2), Idioma.EN to setOf(Nivel.C1))

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
    fun `la pestaña de otro idioma aprendido cambia de idioma y mantiene la misma semana`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2, Idioma.EN to Nivel.C1)),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        // Las pestañas van en orden fijo (el de declaracion de Idioma: EN
        // antes que DE), no en el orden en que se seleccionaron: por eso la
        // pestaña activa por defecto es EN, semana 37 de 2026 -> EN-F01.
        composeTestRule.onNode(hasText("Giving and Asking for Opinions", substring = true)).assertExists()

        // Las pestañas no estan dentro del scroll de la ficha, asi que
        // siempre estan a la vista: no hace falta performScrollTo().
        composeTestRule.onNodeWithText("DE").performClick()

        // Misma semana (37), ahora en aleman: DE-G01. Si el nivel no
        // viniera del mapa por idioma, esto mostraria "sin contenido".
        composeTestRule.onNode(hasText("Satzbau", substring = true)).assertExists()
    }

    @Test
    fun `un solo idioma aprendido muestra su pestaña y su contenido`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNodeWithText("DE").assertExists()
        composeTestRule.onNode(hasText("Satzbau", substring = true)).assertExists()
    }

    @Test
    fun `sin ningun idioma aprendido lo dice, no se rompe`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = emptyMap()),
                idiomasConContenido = setOf(Idioma.DE, Idioma.EN),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNodeWithText("Sin idioma seleccionado").assertExists()
    }

    @Test
    fun `la barra muestra la semana ISO actual`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
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
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = { idioma, nivel, _ -> resolverDePrueba(idioma, nivel, LocalDate.of(2026, 2, 16)) }, // semana ISO 8
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNode(hasText(mensajeSinContenidoSemana(Idioma.ES, 2026), substring = true)).assertExists()
    }

    // --- AJUSTES-FASE-9.md, bloque B: el mensaje de "sin contenido" tambien se traduce ---

    @Test
    fun `el mensaje de sin contenido sigue el idioma de la aplicacion, no queda fijo en espanol`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(
                    idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2),
                    idiomaBase = Idioma.IT,
                    idiomaInterfaz = Idioma.IT,
                ),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = { idioma, nivel, _ -> resolverDePrueba(idioma, nivel, LocalDate.of(2026, 2, 16)) }, // semana ISO 8
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNode(hasText(mensajeSinContenidoTitulo(Idioma.IT), substring = true)).assertExists()
        composeTestRule.onNode(hasText(mensajeSinContenidoSemana(Idioma.IT, 2026), substring = true)).assertExists()
    }

    // --- AJUSTES-FASE-7.md, bloque 2.4: Ajustes es un destino real, no una bandera ---

    @Test
    fun `el boton de ajustes navega, y el gesto de atras del sistema vuelve a la principal (no cierra la app)`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        composeTestRule.onNodeWithText("Ajustes").performClick()
        composeTestRule.onNodeWithText("Idioma que aprendés").assertExists()

        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        // De vuelta en la principal, no cerrada: el gesto de atras desapilo
        // el destino "ajustes" en vez de ir directo al sistema.
        assertFalse(composeTestRule.activity.isFinishing)
        composeTestRule.onNode(hasText("Satzbau", substring = true)).assertExists()
    }

    @Test
    fun `un solo atras en la principal avisa con un toast, no cierra`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        assertEquals(mensajeDobleAtrasParaSalir(Idioma.ES), ShadowToast.getTextOfLatestToast())
        assertFalse(composeTestRule.activity.isFinishing)
    }

    @Test
    fun `doble atras en la principal, dentro de la ventana, cierra`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        assertTrue(composeTestRule.activity.isFinishing)
    }

    // --- AJUSTES-FASE-7.md, bloque 2.2: "Semana", "Ajustes" y "Hoy" siguen el idioma de la aplicacion ---

    @Test
    fun `el encabezado se traduce con el idioma de la aplicacion (aleman)`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(
                    idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2),
                    idiomaBase = Idioma.DE,
                    idiomaInterfaz = Idioma.DE,
                ),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        // "Woche" tambien aparece dentro de la prosa alemana de la ficha, y
        // "Heute" tambien en el boton (ahora traducido): alcanza con que la
        // primera ocurrencia de cada uno exista.
        composeTestRule.onAllNodesWithText("Woche", substring = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Heute", substring = true).onFirst().assertExists()
        composeTestRule.onNodeWithText("Einstellungen").assertExists()
    }

    // --- AJUSTES-FASE-8.md, bloque B.7: la flecha de avanzar se oculta solo en la semana actual ---

    @Test
    fun `en la semana actual no hay boton de avanzar, solo el de retroceder`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        // Arranca en la semana de hoy: ahi el tope es hoy, no hay adelante
        // al que ir. El de "semana anterior" siempre existe.
        composeTestRule.onNodeWithContentDescription("< Semana").assertExists()
        composeTestRule.onNodeWithContentDescription("> Semana").assertDoesNotExist()
    }

    @Test
    fun `Hoy vuelve directo a la semana de hoy desde varias semanas atras, no solo una hacia adelante`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        val atras = composeTestRule.onNodeWithContentDescription("< Semana")
        atras.performClick()
        atras.performClick()
        atras.performClick()
        composeTestRule.onNode(hasText("(Hoy)", substring = true)).assertDoesNotExist()

        composeTestRule.onNodeWithText("Hoy").performClick()

        val semanaIso = semanaIsoDe(LocalDate.now())
        composeTestRule.onNode(hasText("Semana ${semanaIso.semana} · ${semanaIso.anio} (Hoy)", substring = true)).assertExists()
    }

    @Test
    fun `estando en una semana anterior reaparece el boton de avanzar, y avanzar tres veces vuelve exactamente a hoy`() {
        composeTestRule.setContent {
            LenguAppApp(
                ajustesIniciales = Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)),
                idiomasConContenido = setOf(Idioma.DE),
                nivelesConContenido = nivelesDePrueba,
                resolver = ::resolverDePrueba,
                onGuardarAjustes = {},
            )
        }
        val atras = composeTestRule.onNodeWithContentDescription("< Semana")
        atras.performClick()
        atras.performClick()
        atras.performClick()

        composeTestRule.onNodeWithContentDescription("> Semana").assertExists()

        val adelante = composeTestRule.onNodeWithContentDescription("> Semana")
        adelante.performClick()
        adelante.performClick()
        adelante.performClick()

        val semanaIso = semanaIsoDe(LocalDate.now())
        composeTestRule.onNode(hasText("Semana ${semanaIso.semana} · ${semanaIso.anio} (Hoy)", substring = true)).assertExists()
        // De vuelta en hoy, el tope: la flecha de avanzar desaparece otra vez.
        composeTestRule.onNodeWithContentDescription("> Semana").assertDoesNotExist()
    }
}
