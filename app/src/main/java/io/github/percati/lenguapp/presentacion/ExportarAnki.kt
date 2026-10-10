package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado

const val NOMBRE_ARCHIVO_ANKI = "lenguapp-guardados.tsv"

/**
 * Un campo de un TSV para Anki: un tabulador o un salto de linea dentro del texto romperia las
 * columnas o las filas, asi que se reemplazan por un espacio (y se juntan los espacios); si el
 * campo lleva comillas se encierra entre comillas dobladas, como espera la importacion de texto de Anki.
 */
fun campoTsv(texto: String): String {
    val plano = texto.replace(Regex("[\\t\\r\\n]+"), " ").replace(Regex(" {2,}"), " ").trim()
    return if (plano.contains('"')) "\"" + plano.replace("\"", "\"\"") + "\"" else plano
}

/**
 * Guardados como TSV para importar en Anki (UTF-8, tabuladores, texto plano). Una fila por item:
 *  - anverso: el original, en el idioma que se aprende;
 *  - reverso: su traduccion al idioma de app y, si existe, el contexto/funcion (en el idioma que
 *    se aprende), separados por " — " -- los mismos datos que muestra la fila de la app
 *    ([datosFila]);
 *  - etiquetas: "idioma nivel" (p.ej. "de B2": dos etiquetas).
 * Se exportan TODOS los guardados (lo mas simple: sin filtros vigentes), del mas viejo al mas nuevo.
 */
fun exportarAnkiTsv(items: List<ItemGuardado>, idiomaBase: Idioma): String {
    val filas = items.sortedBy { it.id }.map { item ->
        val d = item.datosFila(idiomaBase)
        val reverso = listOfNotNull(d.traduccion, d.funcion).joinToString(" — ")
        listOf(d.original, reverso, "${item.idioma.name.lowercase()} ${item.nivel.name}").joinToString("\t") { campoTsv(it) }
    }
    return (listOf("#separator:tab", "#html:false", "#tags column:3") + filas).joinToString("\n") + "\n"
}
