package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado

/**
 * Vista «Verbos» de la Biblioteca (Ronda H, corregida en la I): TODOS los items de `vocabulario` con
 * `claseVerbo` no vacio del idioma (contenido de «Verbos», FALTANTES 29/30), ya deduplicados por el
 * original (construirBiblioteca agrupa por idioma + tipo + texto: un verbo es UN item aunque este en varios
 * packs). El nivel NO es una dimension de la vista (decision de Fer): ni filtra ni se fija en el nivel de
 * Ajustes -- las familias de verbos separables viven repartidas entre niveles --; solo se muestra como dato
 * en la linea extra (el nivel mas bajo en que aparece). Un solo grupo de chips: la clase. La lista NO es
 * exhaustiva: los verbos que solo viven dentro de colocaciones no estan etiquetados (FALTANTES 34).
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
 * Todos los verbos de `idioma`, de cualquier nivel, ordenados alfabeticamente por el original. Una clase
 * desconocida no saca al verbo de la lista.
 */
fun List<ItemBiblioteca>.verbosDe(idioma: Idioma): List<ItemBiblioteca> =
    filter { it.idioma == idioma && it.esVerbo() }
        .sortedBy { it.texto.lowercase() }

/** El nivel mas bajo en que aparece el item (dato, no filtro). */
fun ItemBiblioteca.nivelMasBajo(): Nivel? = niveles.minOrNull()

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
 * La linea discreta de debajo de un verbo (parametro `extra` de la fila): el nivel mas bajo en que aparece,
 * como dato (`A2`), seguido -- con « · » -- del regimen en aleman (`reccion`, p.ej. «von + Dat») cuando existe,
 * o en ingles de la separabilidad del phrasal verb, salvo que ya se desprenda de un chip elegido.
 */
fun ItemBiblioteca.lineaExtraVerbo(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String {
    val v = vocabulario
    val detalle = v?.reccion?.trim()?.takeIf { it.isNotEmpty() } ?: run {
        val separabilidad = v?.claseVerbo?.firstOrNull { it == "phrasal-separable" || it == "phrasal-inseparable" }
        if (separabilidad == null || separabilidad in clasesElegidas) null else etiquetaDeClase(separabilidad)
    }
    return listOfNotNull(nivelMasBajo()?.name, detalle).joinToString(" · ")
}

/**
 * Una cadena para comparar en la busqueda: minusculas, sin diacriticos (u = ü = Ü) y ß = ss. Asi `uber`
 * encuentra «übernehmen» y `UBER` tambien.
 */
private val MARCAS_COMBINANTES = Regex("\\p{Mn}+")

fun normalizarBusqueda(texto: String): String =
    java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
        .replace(MARCAS_COMBINANTES, "")
        .lowercase(java.util.Locale.ROOT)
        .replace("ß", "ss")
        .trim()

/**
 * Busqueda de la vista Verbos, MIENTRAS se escribe: por subcadena, insensible a mayusculas y diacriticos,
 * sobre el original (idioma que se aprende) y sobre la traduccion al idioma de app. Sin datos nuevos: «machen»
 * encuentra machen, aufmachen, zumachen, abmachen porque el texto los contiene. Vacia = no filtra.
 *
 * Son unos 300-360 items y se filtra en cada tecla: el texto normalizado de cada verbo se calcula UNA vez
 * (al crear el buscador), no en cada tecla.
 */
class BuscadorVerbos(verbos: List<ItemBiblioteca>, idiomaBase: Idioma) {
    // original normalizado + separador + traduccion normalizada (la consulta nunca lleva el separador:
    // una coincidencia no puede cruzar de uno al otro)
    private val buscable = java.util.IdentityHashMap<ItemBiblioteca, String>(verbos.size * 2).also { m ->
        for (v in verbos) m[v] = normalizarBusqueda(v.texto) + "\u0000" + (v.datosFila(idiomaBase).traduccion?.let(::normalizarBusqueda) ?: "")
    }

    fun filtrar(lista: List<ItemBiblioteca>, consulta: String): List<ItemBiblioteca> {
        val q = normalizarBusqueda(consulta)
        if (q.isEmpty()) return lista
        return lista.filter { buscable[it]?.contains(q) == true }
    }
}

fun List<ItemBiblioteca>.filtrarPorBusquedaVerbos(consulta: String, idiomaBase: Idioma): List<ItemBiblioteca> =
    BuscadorVerbos(this, idiomaBase).filtrar(this, consulta)
