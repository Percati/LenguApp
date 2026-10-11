package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado

/**
 * Vista «Verbos» de la Biblioteca (Ronda H): los items de `vocabulario` con `claseVerbo` no vacio
 * (contenido de «Verbos», FALTANTES 29/30), del idioma y nivel que se estan viendo, ya deduplicados por
 * el original (construirBiblioteca agrupa por idioma + tipo + texto: un verbo es UN item aunque este en
 * varios packs). Sin filtro de nivel (decision de Fer): el nivel que se podia derivar era el del tema donde
 * el verbo aparecio primero, no su dificultad. Un solo grupo de chips: la clase.
 */

/** `simple` existe solo para que todo verbo tenga clase: nunca es un chip. */
const val CLASE_VERBO_SIMPLE = "simple"

/**
 * Las clases que la app conoce, en el orden en que se ofrecen como chips. Si el contenido suma una clase
 * antes que la app, el verbo se muestra igual pero esa clase no es un chip (ver [clasesDeChip]).
 */
val CLASES_VERBO_CONOCIDAS: List<String> = listOf(
    // aleman
    "reflexivo", "trennbar", "mit-Praposition", "mit-Dativ",
    // ingles
    "phrasal-separable", "phrasal-inseparable", "verbo-preposicion",
)

/** El item es un verbo etiquetado (alguna clase, aunque sea desconocida o `simple`). */
fun ItemBiblioteca.esVerbo(): Boolean = tipo == TipoGuardado.VOCABULARIO && !vocabulario?.claseVerbo.isNullOrEmpty()

/** Las clases del verbo que son chip: conocidas y no `simple`. */
fun ItemBiblioteca.clasesDeChip(): List<String> =
    vocabulario?.claseVerbo.orEmpty().filter { it != CLASE_VERBO_SIMPLE && it in CLASES_VERBO_CONOCIDAS }

/**
 * Los verbos de `idioma` que aparecen en `nivel`, ordenados alfabeticamente por el original. Una clase
 * desconocida no saca al verbo de la lista.
 */
fun List<ItemBiblioteca>.verbosDe(idioma: Idioma, nivel: Nivel): List<ItemBiblioteca> =
    filter { it.idioma == idioma && it.esVerbo() && nivel in it.niveles }
        .sortedBy { it.texto.lowercase() }

/** Cuantos verbos tiene cada clase de chip dentro de `verbos`, solo las que tienen al menos uno (P3: nunca un chip vacio). */
fun List<ItemBiblioteca>.contarClases(): Map<String, Int> {
    val conteo = linkedMapOf<String, Int>()
    for (clase in CLASES_VERBO_CONOCIDAS) {
        val n = count { clase in it.clasesDeChip() }
        if (n > 0) conteo[clase] = n
    }
    return conteo
}

/** Con ninguna clase elegida se ven todos los verbos (incluidos los `simple`); con varias, los que tienen alguna. */
fun List<ItemBiblioteca>.filtrarPorClases(elegidas: Set<String>): List<ItemBiblioteca> =
    if (elegidas.isEmpty()) this else filter { v -> v.clasesDeChip().any { it in elegidas } }

/**
 * La linea discreta de debajo de un verbo (parametro `extra` de la fila): en aleman el regimen (`reccion`,
 * p.ej. «von + Dat») cuando existe; en ingles la separabilidad del phrasal verb, salvo que ya se
 * desprenda de un chip elegido. `null` = no se dibuja nada.
 */
fun ItemBiblioteca.lineaExtraVerbo(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String? {
    val v = vocabulario ?: return null
    v.reccion?.trim()?.takeIf { it.isNotEmpty() }?.let { return it }
    val separabilidad = v.claseVerbo.firstOrNull { it == "phrasal-separable" || it == "phrasal-inseparable" } ?: return null
    if (separabilidad in clasesElegidas) return null
    return etiquetaDeClase(separabilidad)
}
