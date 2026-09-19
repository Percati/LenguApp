package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma

/**
 * "Idioma de la aplicación" en Ajustes es un solo control, pero en los
 * datos siguen siendo baseLanguage y uiLanguage por separado (CLAUDE.md,
 * regla dura #6): el dia que alguien quiera interfaz en un idioma y glosas
 * en otro, se desdobla el control en pantalla sin migrar nada, porque el
 * modelo ya los tenia separados.
 *
 * Esta funcion es el unico lugar que resuelve el idioma EFECTIVO: si
 * "segun el sistema" esta activo, se prueba el idioma que llega del
 * dispositivo -- leido solo porque el usuario activo esa opcion (CLAUDE.md,
 * regla dura #2, enmendada en AJUSTES-FASE-6.md bloque E). Si el sistema
 * esta en un idioma no soportado, o si la opcion esta apagada, se usa el
 * ultimo valor elegido a mano (`idiomaInterfaz`, que junto con `idiomaBase`
 * se escribe con el mismo valor mientras el ajuste siga unificado).
 */
fun idiomaAplicacionEfectivo(ajustes: Ajustes, codigoIdiomaSistema: String?): Idioma {
    if (!ajustes.idiomaSegunSistema) return ajustes.idiomaInterfaz
    val delSistema = codigoIdiomaSistema?.let { runCatching { Idioma.valueOf(it.uppercase()) }.getOrNull() }
    return delSistema ?: ajustes.idiomaInterfaz
}

/**
 * Las glosas de vocabulario solo existen en español en 2026: 392 items, y
 * todo el contenido actual es B2/C1 (ninguna ficha bilingue todavia) --
 * correccion factual de AJUSTES-FASE-6.md, bloque C. Si el idioma de la
 * aplicacion no es español, las glosas van a caer a español igual (ver
 * VocabularioItem.traduccionParaMostrar); esto solo dice si corresponde
 * avisarlo en Ajustes, para que el usuario no lo descubra solo.
 */
fun avisoGlosasSoloEnEspanol(idiomaAplicacion: Idioma): Boolean = idiomaAplicacion != Idioma.ES
