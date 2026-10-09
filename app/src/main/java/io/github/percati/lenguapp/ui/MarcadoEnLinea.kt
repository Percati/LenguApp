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

/**
 * El mismo texto sin los marcadores, para superficies que no admiten texto con
 * estilo (el widget de Glance): sin esto los asteriscos se verian literales.
 */
fun textoSinMarcado(texto: String): String = textoConMarcado(texto).text

/**
 * `angulares = true` es para cuando la ficha muestra una TRADUCCION: la
 * palabra objetivo (`*cita*`) queda sin traducir en el idioma que se
 * aprende, y ademas de la cursiva se envuelve entre « » para que se vea
 * donde empieza y termina dentro de la frase traducida. Si el texto ya trae
 * los « » escritos a mano alrededor (como los escribe Traducciones en las
 * celdas traducidas), no se duplican; tampoco se envuelve una cita que ya
 * contiene « » adentro. El destaque `**x**` no se envuelve:
 * marca una posicion (p.ej. del verbo), no una palabra citada.
 */
fun textoConMarcado(texto: String, angulares: Boolean = false): AnnotatedString = buildAnnotatedString {
    var indice = 0
    for (coincidencia in PATRON_MARCADO.findAll(texto)) {
        append(texto.substring(indice, coincidencia.range.first))
        val destaque = coincidencia.groups[1]?.value
        val cita = coincidencia.groups[2]?.value
        when {
            destaque != null -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(destaque) }
            cita != null -> {
                val yaEntreAngulares = texto.getOrNull(coincidencia.range.first - 1) == '«' &&
                    texto.getOrNull(coincidencia.range.last + 1) == '»'
                // Una cita que ya lleva « » adentro (p.ej. `*«vorrei» – ich hätte gern*`)
                // marca ella misma sus fragmentos ajenos: envolverla daria «««.
                val traeAngulares = '«' in cita || '»' in cita
                val envolver = angulares && !yaEntreAngulares && !traeAngulares
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    if (envolver) append("«")
                    append(cita)
                    if (envolver) append("»")
                }
            }
        }
        indice = coincidencia.range.last + 1
    }
    append(texto.substring(indice))
}
