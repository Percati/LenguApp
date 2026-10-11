package io.github.percati.lenguapp.ui

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.github.percati.lenguapp.R
import io.github.percati.lenguapp.modelo.FuenteTema

/**
 * Las dos tipografias embebidas (Ronda G, tarea 2), como fuentes VARIABLES (eje `wght` 400-700) con un
 * archivo para el redondo y otro para la cursiva (las fichas usan `*cursiva*`): Regular, Medium y Bold
 * salen del mismo archivo con `FontVariation`. Licencias OFL 1.1 en LICENSES/. SISTEMA no se define
 * aca: es la familia por defecto del dispositivo.
 */
@OptIn(ExperimentalTextApi::class)
private fun familiaVariable(redondo: Int, cursiva: Int): FontFamily {
    val pesos = listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.Bold)
    return FontFamily(
        pesos.flatMap { peso ->
            listOf(
                Font(redondo, weight = peso, style = FontStyle.Normal, variationSettings = FontVariation.Settings(peso, FontStyle.Normal)),
                Font(cursiva, weight = peso, style = FontStyle.Italic, variationSettings = FontVariation.Settings(peso, FontStyle.Italic)),
            )
        },
    )
}

/** Literata (serifa de lectura). */
val FamiliaLectura: FontFamily by lazy { familiaVariable(R.font.literata, R.font.literata_italic) }

/** Atkinson Hyperlegible Next (sans de alta legibilidad). */
val FamiliaClara: FontFamily by lazy { familiaVariable(R.font.atkinson, R.font.atkinson_italic) }

/** La familia de una opcion de tipografia; `null` = la del sistema (no se toca el estilo base). */
fun familiaTipografica(fuente: FuenteTema): FontFamily? = when (fuente) {
    FuenteTema.SISTEMA -> null
    FuenteTema.LECTURA -> FamiliaLectura
    FuenteTema.CLARA -> FamiliaClara
}
