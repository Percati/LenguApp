package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver

/**
 * Guardados (Ronda C, tarea 2): orden y filtros en cascada, logica pura
 * (sin Compose) para que se pueda testear sin Robolectric. La pantalla
 * (GuardadosScreen.kt) solo arma el estado y llama a estas funciones.
 */
enum class CriterioOrdenGuardados { ALFABETICO, NIVEL, FECHA, TIPO }

enum class DireccionOrden { ASCENDENTE, DESCENDENTE }

/** Filtros activos de la pantalla de Guardados; null en cualquiera es "Todos". */
data class FiltrosGuardados(
    val tipo: TipoGuardado? = null,
    val nivel: Nivel? = null,
    // Solo tiene sentido si tipo == VOCABULARIO.
    val topic: String? = null,
    // Solo tiene sentido si tipo == EXPRESION.
    val categoria: String? = null,
)

/**
 * La palabra/expresion tal cual esta en la ficha (la clave de identidad, no
 * la traduccion) -- la misma que usa [ItemBiblioteca] y que compone la clave
 * de la estrella en toda la app. `aTextoBilingue` siempre guarda esta
 * entrada bajo la clave propia del idioma, asi que resolver(idioma, idioma)
 * la recupera exacta, sin pasar por la logica de traduccion.
 */
fun ItemGuardado.textoOrigen(): String = texto.resolver(idioma, idioma)

/**
 * Indice (idioma, tipo, texto) -> [ItemBiblioteca], para resolver topic y
 * categoriasUso de un guardado buscandolo en el contenido embebido -- Room
 * no guarda ninguna de las dos (regla de la tarea: no se cambia que se
 * guarda en Room).
 */
fun List<ItemBiblioteca>.indiceBiblioteca(): Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca> =
    associateBy { Triple(it.idioma, it.tipo, it.texto) }

private fun ItemGuardado.buscarEnBiblioteca(indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): ItemBiblioteca? =
    indice[Triple(idioma, tipo, textoOrigen())]

/** topics de un guardado via el indice; vacio si ya no esta en el contenido embebido (edicion vieja). */
fun ItemGuardado.topics(indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Set<String> =
    buscarEnBiblioteca(indice)?.topics.orEmpty()

/** categoriasUso de un guardado via el indice; vacio si ya no esta en el contenido embebido. */
fun ItemGuardado.categorias(indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Set<String> =
    buscarEnBiblioteca(indice)?.categorias.orEmpty()

/** Aplica todos los filtros salvo los que se pasan como null explicito (usado para calcular las opciones de cada faceta). */
private fun List<ItemGuardado>.aplicar(
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
): List<ItemGuardado> = filter { filtros.tipo == null || it.tipo == filtros.tipo }
    .filter { filtros.nivel == null || it.nivel == filtros.nivel }
    .filter { filtros.topic == null || filtros.topic in it.topics(indice) }
    .filter { filtros.categoria == null || filtros.categoria in it.categorias(indice) }

/** Los guardados del idioma elegido (Guardados filtra por idioma, no mezcla, igual que la Biblioteca). */
fun List<ItemGuardado>.delIdioma(idioma: Idioma): List<ItemGuardado> = filter { it.idioma == idioma }

/** El resultado final: todos los filtros aplicados. */
fun List<ItemGuardado>.filtrarCascada(
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
): List<ItemGuardado> = aplicar(filtros, indice)

/**
 * Red de seguridad, no el mecanismo principal: cada faceta de la pantalla ya
 * solo ofrece como clickeables los valores con contador > 0 calculado sobre
 * las DEMAS facetas activas (ver opcionesNivel/opcionesTipo/opcionesTopic/
 * opcionesCategoria), asi que un click nuevo nunca puede llevar a una
 * combinacion vacia por si solo. Lo unico que puede dejar un filtro viejo
 * sin soporte es un cambio AJENO a un click sobre ese filtro -- items que
 * cambian debajo (p.ej. se quita una estrella y era el unico guardado de
 * ese nivel). En ese caso no hay forma no ambigua de saber cual de los
 * filtros activos es "el culpable", asi que la combinacion entera vuelve al
 * default en vez de adivinar cual soltar.
 */
fun List<ItemGuardado>.normalizarFiltros(
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
): FiltrosGuardados = if (aplicar(filtros, indice).isEmpty()) FiltrosGuardados() else filtros

/** Valores disponibles de nivel bajo los demas filtros activos (no el de nivel), con cuanto hay de cada uno. */
fun List<ItemGuardado>.opcionesNivel(filtros: FiltrosGuardados, indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Map<Nivel, Int> =
    aplicar(filtros.copy(nivel = null), indice).groupingBy { it.nivel }.eachCount()

/** Valores disponibles de tipo bajo los demas filtros activos, con cuanto hay de cada uno. */
fun List<ItemGuardado>.opcionesTipo(filtros: FiltrosGuardados, indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Map<TipoGuardado, Int> =
    aplicar(filtros.copy(tipo = null), indice).groupingBy { it.tipo }.eachCount()

/** Topics disponibles (solo vocabulario) bajo los demas filtros activos, con cuanto hay de cada uno. */
fun List<ItemGuardado>.opcionesTopic(filtros: FiltrosGuardados, indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Map<String, Int> {
    val base = aplicar(filtros.copy(topic = null), indice)
    val conteo = mutableMapOf<String, Int>()
    for (g in base) for (t in g.topics(indice)) conteo[t] = (conteo[t] ?: 0) + 1
    return conteo
}

/** CategoriasUso disponibles (solo expresiones) bajo los demas filtros activos, con cuanto hay de cada uno. */
fun List<ItemGuardado>.opcionesCategoria(filtros: FiltrosGuardados, indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>): Map<String, Int> {
    val base = aplicar(filtros.copy(categoria = null), indice)
    val conteo = mutableMapOf<String, Int>()
    for (g in base) for (c in g.categorias(indice)) conteo[c] = (conteo[c] ?: 0) + 1
    return conteo
}

/**
 * Orden; el alfabetico es por el original en el idioma que se aprende (la primera linea de la fila). FECHA usa el id de Room: autoincremental, asi que mas alto es
 * mas reciente -- igual que "fecha de guardado, mas reciente primero" sin
 * necesitar un campo de fecha propio.
 */
fun List<ItemGuardado>.ordenar(criterio: CriterioOrdenGuardados, direccion: DireccionOrden, idiomaBase: Idioma): List<ItemGuardado> {
    val comparador: Comparator<ItemGuardado> = when (criterio) {
        CriterioOrdenGuardados.ALFABETICO -> compareBy { it.textoOrigen().lowercase() }
        CriterioOrdenGuardados.NIVEL -> compareBy { it.nivel }
        CriterioOrdenGuardados.FECHA -> compareBy { it.id }
        CriterioOrdenGuardados.TIPO -> compareBy { it.tipo }
    }
    val ascendente = sortedWith(comparador)
    return if (direccion == DireccionOrden.DESCENDENTE) ascendente.asReversed() else ascendente
}
