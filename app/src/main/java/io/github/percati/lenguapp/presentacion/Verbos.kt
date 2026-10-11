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
 * en la linea extra (el nivel mas bajo en que aparece). Un solo grupo de chips: la clase.
 *
 * Ronda J (FALTANTES 37/38): la vista lista DOS clases de entrada juntas, por infinitivo y alfabeticamente:
 *  - el verbo propio (un item con `claseVerbo`; tiene traduccion y es marcable), y
 *  - el nucleo verbal de una o mas colocaciones (`nucleoVerbal`: «die Frage stellen» -> «stellen»), que NO es un
 *    item: sin traduccion propia y NO marcable (la clave de Guardados es el texto del item, y un nucleo sin item
 *    daria una clave que ninguna ficha puede volver a producir). La estrella vive en cada colocacion.
 * Cuando un nucleo coincide EXACTAMENTE con un verbo propio es UNA sola entrada (la del verbo propio, con sus
 * colocaciones debajo). Una colocacion con nucleo no se lista suelta: aparece solo debajo de su entrada.
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

/** La colocacion de un nucleo verbal (un item de vocabulario con `nucleoVerbal`; nunca tiene `claseVerbo`). */
fun ItemBiblioteca.esColocacionConNucleo(): Boolean =
    tipo == TipoGuardado.VOCABULARIO && !vocabulario?.nucleoVerbal.isNullOrBlank()

/**
 * Una entrada de la vista Verbos: el verbo propio y/o el nucleo verbal con sus colocaciones.
 *  - [propio] != null: el infinitivo es un item (marcable, con traduccion); [colocaciones] pueden estar o no.
 *  - [propio] == null: es un nucleo puro (encabezado sin estrella ni traduccion); siempre hay colocaciones.
 */
data class EntradaVerbo(
    val infinitivo: String,
    val propio: ItemBiblioteca?,
    val colocaciones: List<ItemBiblioteca>,
    /** claseVerbo del propio, o claseNucleo de las colocaciones (R11: coinciden cuando son los dos). */
    val clases: List<String>,
    val reccionNucleo: String?,
) {
    /** El nivel mas bajo: el del verbo propio, o el de sus colocaciones si es un nucleo puro (dato, no filtro). */
    val nivelMasBajo: Nivel?
        get() = propio?.nivelMasBajo() ?: colocaciones.flatMap { it.niveles }.minOrNull()

    /** Las clases que son chip: conocidas y no `simple`. */
    fun clasesDeChip(): List<String> = clases.filter { it != CLASE_VERBO_SIMPLE && it in CLASES_VERBO_CONOCIDAS }

    /** Linea discreta de debajo de la entrada (ver [lineaExtraVerbo]). */
    fun lineaExtra(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String {
        val reccion = propio?.vocabulario?.reccion?.trim()?.takeIf { it.isNotEmpty() } ?: reccionNucleo?.trim()?.takeIf { it.isNotEmpty() }
        return lineaExtra(nivelMasBajo, reccion, clases, clasesElegidas, etiquetaDeClase)
    }
}

/**
 * Las entradas de la vista Verbos de `idioma`: los verbos propios + los nucleos verbales de las colocaciones,
 * fusionados por infinitivo exacto, ordenados alfabeticamente. Las colocaciones de cada entrada van ordenadas.
 */
fun List<ItemBiblioteca>.entradasVerbo(idioma: Idioma): List<EntradaVerbo> {
    val delIdioma = filter { it.idioma == idioma }
    val propios = delIdioma.filter { it.esVerbo() }
    val porNucleo = delIdioma.filter { it.esColocacionConNucleo() && !it.esVerbo() }
        .groupBy { it.vocabulario!!.nucleoVerbal!!.trim() }
    val colocacionesDe = { nucleo: String -> porNucleo[nucleo].orEmpty().sortedBy { it.texto.lowercase() } }

    val entradas = ArrayList<EntradaVerbo>(propios.size + porNucleo.size)
    val textosPropios = HashSet<String>(propios.size * 2)
    for (p in propios) {
        textosPropios += p.texto
        val colocaciones = colocacionesDe(p.texto)
        entradas += EntradaVerbo(
            p.texto, p, colocaciones, p.vocabulario!!.claseVerbo,
            colocaciones.firstNotNullOfOrNull { it.vocabulario!!.reccionNucleo },
        )
    }
    for ((nucleo, items) in porNucleo) {
        if (nucleo in textosPropios) continue
        entradas += EntradaVerbo(
            infinitivo = nucleo, propio = null, colocaciones = colocacionesDe(nucleo),
            clases = items.flatMap { it.vocabulario!!.claseNucleo }.distinct(),
            reccionNucleo = items.firstNotNullOfOrNull { it.vocabulario!!.reccionNucleo?.trim()?.takeIf { r -> r.isNotEmpty() } },
        )
    }
    return entradas.sortedBy { it.infinitivo.lowercase() }
}

/** El nivel mas bajo en que aparece el item (dato, no filtro). */
fun ItemBiblioteca.nivelMasBajo(): Nivel? = niveles.minOrNull()

/**
 * Cuantas entradas tiene cada clase de chip dentro de `entradas`, solo las que tienen al menos una (P3: nunca
 * un chip vacio). Un nucleo aporta su `claseNucleo`, igual que un verbo propio aporta su `claseVerbo`.
 */
fun List<EntradaVerbo>.contarClases(): Map<String, Int> {
    val conteo = linkedMapOf<String, Int>()
    for (clase in CLASES_VERBO_CONOCIDAS) {
        val n = count { clase in it.clasesDeChip() }
        if (n > 0) conteo[clase] = n
    }
    return conteo
}

/** Con ninguna clase elegida se ven todas las entradas (incluidas las `simple`); con varias, las que tienen alguna. */
fun List<EntradaVerbo>.filtrarPorClases(elegidas: Set<String>): List<EntradaVerbo> =
    if (elegidas.isEmpty()) this else filter { e -> e.clasesDeChip().any { it in elegidas } }

/**
 * La linea discreta de debajo de un verbo (parametro `extra` de la fila): el nivel mas bajo en que aparece,
 * como dato (`A2`), seguido -- con « · » -- del regimen en aleman (`reccion`, p.ej. «von + Dat») cuando existe,
 * o en ingles de la separabilidad del phrasal verb, salvo que ya se desprenda de un chip elegido.
 */
fun ItemBiblioteca.lineaExtraVerbo(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String =
    lineaExtra(nivelMasBajo(), vocabulario?.reccion?.trim()?.takeIf { it.isNotEmpty() }, vocabulario?.claseVerbo.orEmpty(), clasesElegidas, etiquetaDeClase)

private fun lineaExtra(nivel: Nivel?, reccion: String?, clases: List<String>, clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String {
    val detalle = reccion ?: run {
        val separabilidad = clases.firstOrNull { it == "phrasal-separable" || it == "phrasal-inseparable" }
        if (separabilidad == null || separabilidad in clasesElegidas) null else etiquetaDeClase(separabilidad)
    }
    return listOfNotNull(nivel?.name, detalle).joinToString(" · ")
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
 * Busqueda de la vista Verbos, MIENTRAS se escribe: por subcadena, insensible a mayusculas y diacriticos.
 * Una entrada coincide por su infinitivo, por su traduccion al idioma de app (si es un verbo propio) o por el
 * TEXTO O LA TRADUCCION de cualquiera de sus colocaciones: «Frage» trae «stellen» (sus colocaciones estan
 * siempre visibles debajo, asi que se ve la que coincidio). Vacia = no filtra.
 *
 * Son ~470/~560 entradas con ~760 colocaciones y se filtra en cada tecla: el texto normalizado de cada entrada
 * y de sus colocaciones se calcula UNA vez (al crear el buscador), no en cada tecla.
 */
class BuscadorVerbos(entradas: List<EntradaVerbo>, idiomaBase: Idioma) {
    // infinitivo + traduccion + (texto + traduccion de cada colocacion), normalizado y separado por \u0000
    // (la consulta nunca lleva el separador: una coincidencia no puede cruzar de un campo al otro)
    private val buscable = java.util.IdentityHashMap<EntradaVerbo, String>(entradas.size * 2).also { m ->
        for (e in entradas) {
            val partes = ArrayList<String>(2 + e.colocaciones.size * 2)
            partes += normalizarBusqueda(e.infinitivo)
            e.propio?.datosFila(idiomaBase)?.traduccion?.let { partes += normalizarBusqueda(it) }
            m[e] = partes.joinToString("\u0000")
        }
    }

    fun filtrar(lista: List<EntradaVerbo>, consulta: String): List<EntradaVerbo> {
        val q = normalizarBusqueda(consulta)
        if (q.isEmpty()) return lista
        return lista.filter { buscable[it]?.contains(q) == true }
    }
}

fun List<EntradaVerbo>.filtrarPorBusquedaVerbos(consulta: String, idiomaBase: Idioma): List<EntradaVerbo> =
    BuscadorVerbos(this, idiomaBase).filtrar(this, consulta)
