package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.modelo.aTextoBilingue
import io.github.percati.lenguapp.modelo.resolver

/**
 * Un item de la Biblioteca: una palabra o expresion DEDUPLICADA entre todas las
 * fichas embebidas. Si aparece en varios niveles (o varios topics), es UN item
 * con todos sus niveles y topics -- sale en cada filtro que le corresponda.
 *
 * `skillIdOrigen`/`nivelOrigen` son los de la primera ficha donde aparece (ordenada
 * por nivel y id): es el origen que se guarda si el usuario la marca con la
 * estrella, igual que la estrella de la ficha.
 */
data class ItemBiblioteca(
    val idioma: Idioma,
    val tipo: TipoGuardado,
    val texto: String,
    val vocabulario: VocabularioItem?,
    val redemittel: RedemittelItem?,
    val niveles: Set<Nivel>,
    val topics: Set<String>,
    val categorias: Set<String>,
    val skillIdOrigen: String,
    val nivelOrigen: Nivel,
) {
    /** Lo mismo que aTextoBilingue de la ficha: la palabra en el idioma que se aprende + sus traducciones. */
    fun textoBilingue(): TextoBilingue =
        vocabulario?.aTextoBilingue(idioma) ?: redemittel!!.aTextoBilingue(idioma)

    val funcion: TextoBilingue? get() = redemittel?.funcion
}

/** Clave de la estrella: la misma que usa la ficha (tipo + texto en el idioma que se aprende). */
fun ItemBiblioteca.claveGuardado(): String = "$tipo|$texto"

/**
 * Agrega todo el contenido embebido: solo fichas (las semanas especiales no tienen
 * vocabulario ni expresiones). Items iguales (mismo idioma, tipo y texto) se unen.
 */
fun construirBiblioteca(contenidoTodos: List<ContenidoSemanal>): List<ItemBiblioteca> {
    class Acum(
        val idioma: Idioma, val tipo: TipoGuardado, val texto: String,
        val vocabulario: VocabularioItem?, val redemittel: RedemittelItem?,
        val skillIdOrigen: String, val nivelOrigen: Nivel,
    ) {
        val niveles = linkedSetOf<Nivel>()
        val topics = linkedSetOf<String>()
        val categorias = linkedSetOf<String>()
    }

    val acumulados = LinkedHashMap<Triple<Idioma, TipoGuardado, String>, Acum>()
    val fichas = contenidoTodos.filterIsInstance<Ficha>().sortedWith(compareBy({ it.nivel }, { it.id }))
    for (ficha in fichas) {
        for (v in ficha.vocabulario) {
            val texto = v.item.trim()
            if (texto.isEmpty()) continue
            val a = acumulados.getOrPut(Triple(ficha.idioma, TipoGuardado.VOCABULARIO, texto)) {
                Acum(ficha.idioma, TipoGuardado.VOCABULARIO, texto, v, null, ficha.skillId, ficha.nivel)
            }
            a.niveles += ficha.nivel
            a.topics += ficha.topicId
        }
        for (r in ficha.redemittel) {
            val texto = r.expresion.trim()
            if (texto.isEmpty()) continue
            val a = acumulados.getOrPut(Triple(ficha.idioma, TipoGuardado.EXPRESION, texto)) {
                Acum(ficha.idioma, TipoGuardado.EXPRESION, texto, null, r, ficha.skillId, ficha.nivel)
            }
            a.niveles += ficha.nivel
            a.topics += ficha.topicId
            a.categorias += r.categoriasUso.orEmpty()
        }
    }
    return acumulados.values.map {
        ItemBiblioteca(
            it.idioma, it.tipo, it.texto, it.vocabulario, it.redemittel,
            it.niveles, it.topics, it.categorias, it.skillIdOrigen, it.nivelOrigen,
        )
    }
}

/** Marca interna del chip "Sin categoria" en el filtro de expresiones. */
const val SIN_CATEGORIA = "\u0000sin-categoria"

/**
 * Filtros de la Biblioteca. Un filtro vacio no filtra. `niveles`: multi-seleccion, un
 * item sale si aparece en AL MENOS uno. `categorias` (solo expresiones): multi, OR;
 * [SIN_CATEGORIA] matchea las expresiones con la lista vacia.
 */
fun List<ItemBiblioteca>.filtrar(
    idioma: Idioma,
    tipo: TipoGuardado,
    niveles: Set<Nivel>,
    topic: String?,
    categorias: Set<String>,
    busqueda: String,
    idiomaBase: Idioma,
): List<ItemBiblioteca> {
    val q = busqueda.trim().lowercase()
    return filter { it.idioma == idioma && it.tipo == tipo }
        .filter { niveles.isEmpty() || it.niveles.any { n -> n in niveles } }
        .filter { tipo != TipoGuardado.VOCABULARIO || topic == null || topic in it.topics }
        .filter {
            tipo != TipoGuardado.EXPRESION || categorias.isEmpty() ||
                (SIN_CATEGORIA in categorias && it.categorias.isEmpty()) ||
                it.categorias.any { c -> c in categorias }
        }
        .filter { q.isEmpty() || it.coincide(q, idiomaBase) }
        .sortedBy { it.texto.lowercase() }
}

private fun ItemBiblioteca.coincide(q: String, idiomaBase: Idioma): Boolean {
    if (texto.lowercase().contains(q)) return true
    val traduccion = vocabulario?.traduccionParaMostrar(idiomaBase)
        ?: (redemittel?.traduccionParaMostrar(idiomaBase) as? TraduccionRedemittel.Disponible)?.texto
    if (traduccion != null && traduccion.lowercase().contains(q)) return true
    return funcion?.resolver(idioma, idioma)?.lowercase()?.contains(q) == true
}
