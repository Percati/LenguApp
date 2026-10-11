package io.github.percati.lenguapp.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Pantalla completa (Ronda K, tarea 1): la app se dibuja de borde a borde y el fondo del tema se ve DETRAS de la
 * barra de estado y la de navegacion. Las reglas, en un solo lugar:
 *  - Arriba y a los costados (barra de estado, notch, recorte en horizontal) se respeta UNA vez, en la raiz de la
 *    app ([Modifier.margenesSuperiorYLateralesDelSistema]): los encabezados nunca quedan bajo la barra o el recorte.
 *  - Abajo (barra de gestos o de botones, y el teclado) NO se respeta en el contenedor: el contenido se desplaza por
 *    DEBAJO de la barra y termina con relleno suficiente para que la ultima fila no quede escondida
 *    ([Modifier.finDeScrollSobreBarras] dentro de un scroll, [paddingInferiorDelSistema] como `contentPadding`).
 */

/** Barra de estado, recorte de camara y costados: se aplica una vez, en la raiz de la app. */
fun Modifier.margenesSuperiorYLateralesDelSistema(): Modifier = composed {
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
}

/**
 * Relleno inferior de un contenedor que se desplaza (barra de navegacion y teclado). Va DENTRO del scroll
 * (despues de `verticalScroll`, antes del `padding` propio de la pantalla): el contenido pasa por debajo de la barra
 * y, al final, queda ese espacio de mas.
 */
fun Modifier.finDeScrollSobreBarras(): Modifier = composed {
    windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
}

/** Lo mismo para un `LazyColumn`: se pasa como `contentPadding` (no como `padding` del contenedor). */
@Composable
fun paddingInferiorDelSistema(): PaddingValues =
    WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom).asPaddingValues()

/**
 * Los iconos de las barras del sistema son oscuros sobre un fondo claro y claros sobre uno oscuro: gana el que da
 * mas contraste (el punto de cruce de la razon de contraste WCAG entre blanco y negro es una luminancia de ~0,179).
 * Se decide con el fondo REAL del tema elegido, no con el modo del sistema.
 */
fun iconosOscurosSobre(fondo: Color): Boolean = fondo.luminance() > LUMINANCIA_DE_CRUCE

private const val LUMINANCIA_DE_CRUCE = 0.179f

/** Ajusta los iconos de la barra de estado y de la de navegacion al fondo del tema vigente. */
@Composable
fun AjustarIconosDeBarras(fondo: Color) {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    val oscuros = iconosOscurosSobre(fondo)
    SideEffect {
        val ventana = vista.context.actividad()?.window ?: return@SideEffect
        val controlador = WindowCompat.getInsetsController(ventana, vista)
        controlador.isAppearanceLightStatusBars = oscuros
        controlador.isAppearanceLightNavigationBars = oscuros
    }
}

private fun Context.actividad(): Activity? {
    var contexto: Context? = this
    while (contexto is ContextWrapper) {
        if (contexto is Activity) return contexto
        contexto = contexto.baseContext
    }
    return null
}
