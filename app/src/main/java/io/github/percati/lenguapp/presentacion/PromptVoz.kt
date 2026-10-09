package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.resolver

/** Cuantas expresiones / palabras de vocabulario entran como maximo en el prompt de voz. */
private const val MAX_ITEMS_PROMPT_VOZ = 6

/** Minutos orales por defecto cuando la ficha no trae un `oralMin` utilizable. */
private const val ORAL_MIN_POR_DEFECTO = 6

/** Clave de plantilla para una ficha: "en-B2", "en-C1"... (idioma en minuscula + nivel). */
fun clavePlantillaVoz(ficha: Ficha): String = "${ficha.idioma.name.lowercase()}-${ficha.nivel.name}"

/**
 * Prompt de conversacion de voz para pegar en una IA externa con modo voz
 * (mismo mecanismo que `promptCorreccion`: se copia al portapapeles, la app no
 * habla con ninguna IA). `null` si no hay plantilla para el par (idioma, nivel)
 * de la ficha: la tarjeta se muestra solo cuando hay plantilla, asi que
 * "que pares tienen prompt de voz" se deriva del contenido embebido
 * (CLAUDE.md, regla dura #10), nunca con un `if` por idioma.
 *
 * Los placeholders ({titulo} {consigna} {requisitos} {expresiones}
 * {vocabulario} {oralMin}) se resuelven desde la ficha EN EL IDIOMA QUE SE
 * APRENDE -- la IA tiene que hablarlo --, no en el de app. Un placeholder sin
 * dato quita la linea entera en vez de dejar llaves ni un renglon vacio.
 */
fun promptVozPara(ficha: Ficha, plantillas: Map<String, String>): String? {
    val plantilla = plantillas[clavePlantillaVoz(ficha)] ?: return null
    val aprendido = ficha.idioma
    fun t(texto: TextoBilingue) = texto.resolver(aprendido, aprendido).trim()

    val valores = mapOf(
        "titulo" to t(ficha.titulo),
        "consigna" to t(ficha.mision.consigna),
        "requisitos" to ficha.mision.requisitos.map(::t).filter { it.isNotEmpty() }.joinToString("; "),
        "expresiones" to ficha.redemittel.take(MAX_ITEMS_PROMPT_VOZ).map { it.expresion.trim() }
            .filter { it.isNotEmpty() }.joinToString(", "),
        "vocabulario" to ficha.vocabulario.filter { it.prioridad == Prioridad.NUCLEO }
            .take(MAX_ITEMS_PROMPT_VOZ).map { it.item.trim() }.filter { it.isNotEmpty() }.joinToString(", "),
        "oralMin" to Math.round(ficha.evidencia.oralMin).toInt().takeIf { it > 0 }.let { (it ?: ORAL_MIN_POR_DEFECTO).toString() },
    )

    val patron = Regex("\\{(titulo|consigna|requisitos|expresiones|vocabulario|oralMin)\\}")
    return plantilla.lines().mapNotNull { linea ->
        val usados = patron.findAll(linea).map { it.groupValues[1] }.toList()
        if (usados.any { valores.getValue(it).isEmpty() }) {
            null
        } else {
            patron.replace(linea) { valores.getValue(it.groupValues[1]) }
        }
    }.joinToString("\n")
}
