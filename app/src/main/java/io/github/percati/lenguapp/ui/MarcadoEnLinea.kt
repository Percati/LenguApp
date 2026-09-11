package io.github.percati.lenguapp.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Una sola convencion de marcado en linea, plana y sin anidar (garantizado
 * por el compilador -- ver el $comment de ficha.schema.json): `*cita*` para
 * una palabra o expresion citada, `**destaque**` para el elemento sobre el
 * que se llama la atencion (p.ej. la posicion del verbo en Satzbau). No es
 * decoracion: en esa ficha el destaque es todo el punto de la carta.
 *
 * Parser de un nivel: no hace falta manejar anidamiento porque el
 * compilador no lo produce. `**` se prueba antes que `*` para no partir
 * "**x**" en "*" + "*x*" + "*".
 */
private val PATRON_MARCADO = Regex("""\*\*([^*]+)\*\*|\*([^*]+)\*""")

fun textoConMarcado(texto: String): AnnotatedString = buildAnnotatedString {
    var indice = 0
    for (coincidencia in PATRON_MARCADO.findAll(texto)) {
        append(texto.substring(indice, coincidencia.range.first))
        val destaque = coincidencia.groups[1]?.value
        val cita = coincidencia.groups[2]?.value
        when {
            destaque != null -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(destaque) }
            cita != null -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(cita) }
        }
        indice = coincidencia.range.last + 1
    }
    append(texto.substring(indice))
}
