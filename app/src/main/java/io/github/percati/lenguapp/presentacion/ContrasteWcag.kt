package io.github.percati.lenguapp.presentacion

/**
 * Contraste WCAG 2.1 (formula oficial: luminancia relativa + razon de
 * contraste). AJUSTES-FASE-7.md, bloque 1: "hay que medir, no suponer" --
 * esto calcula el numero real a partir de los hex de la paleta, no una
 * apreciacion visual. Sin dependencia de Compose/Android: los mismos
 * colores hex de ui/Tema.kt se pueden medir en un test JVM comun.
 */

/** WCAG AA para texto de cuerpo exige razon >= 4.5:1. */
const val CONTRASTE_MINIMO_AA_TEXTO = 4.5

private fun componentesRgb(colorHex: String): Triple<Int, Int, Int> {
    val limpio = colorHex.removePrefix("#")
    require(limpio.length == 6) { "Color hex invalido: $colorHex" }
    return Triple(
        limpio.substring(0, 2).toInt(16),
        limpio.substring(2, 4).toInt(16),
        limpio.substring(4, 6).toInt(16),
    )
}

private fun linealizar(canal: Int): Double {
    val c = canal / 255.0
    return if (c <= 0.03928) c / 12.92 else Math.pow((c + 0.055) / 1.055, 2.4)
}

private fun luminanciaRelativa(colorHex: String): Double {
    val (r, g, b) = componentesRgb(colorHex)
    return 0.2126 * linealizar(r) + 0.7152 * linealizar(g) + 0.0722 * linealizar(b)
}

/** Razon de contraste WCAG entre dos colores, en formato "#RRGGBB". Simetrica: el orden de los argumentos no importa. */
fun contrasteWcag(colorA: String, colorB: String): Double {
    val l1 = luminanciaRelativa(colorA)
    val l2 = luminanciaRelativa(colorB)
    val masClaro = maxOf(l1, l2)
    val masOscuro = minOf(l1, l2)
    return (masClaro + 0.05) / (masOscuro + 0.05)
}

fun cumpleAaTexto(colorA: String, colorB: String): Boolean =
    contrasteWcag(colorA, colorB) >= CONTRASTE_MINIMO_AA_TEXTO
