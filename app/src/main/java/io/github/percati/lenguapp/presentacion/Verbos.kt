package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.datos.VerboReferencia
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.TipoGuardado

/**
 * Vista «Verbos» de la Biblioteca (Ronda H, corregida en la I): TODOS los items de `vocabulario` con
 * `claseVerbo` no vacio del idioma (contenido de «Verbos», FALTANTES 29/30), ya deduplicados por el
 * original (construirBiblioteca agrupa por idioma + tipo + texto: un verbo es UN item aunque este en varios
 * packs). El nivel NO es una dimension de la vista (decision de Fer): ni filtra, ni se fija en el nivel de
 * Ajustes, ni se muestra (Ronda T): no es una propiedad del verbo sino de nuestra cobertura -- el nivel mas bajo de los
 * packs donde aparece --, la tabla maestra no lo tiene y los verbos de referencia que vendran no tendran ninguno. La linea
 * extra lleva solo la clase (separabilidad del phrasal) y el regimen en aleman. Un solo grupo de chips: la clase.
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
 * Una entrada de la vista Verbos: UNA por infinitivo, con hasta tres fuentes fusionadas.
 *  - [propio] != null: el infinitivo es un item de una ficha (marcable, con traduccion); [colocaciones] pueden estar o no.
 *  - [propio] == null: no hay item. Con [colocaciones] es un nucleo (encabezado sin estrella, estrella en cada colocacion); sin ellas es
 *    un verbo de SOLO referencia (Ronda U): traduccion y linea extra, sin estrella (no es un item de ninguna ficha).
 *  - [referencia] (Ronda U): el verbo de consulta del mismo infinitivo, si lo hay. Rellena la traduccion cuando no hay propio (si hay
 *    propio manda la suya, que viene del contenido), la clase si ni el propio ni el nucleo declaran una y el regimen; NUNCA vuelve
 *    marcable a la entrada: la estrella sigue la regla de siempre (esta si hay `propio`, y si no vive en cada colocacion).
 */
data class EntradaVerbo(
    val infinitivo: String,
    val propio: ItemBiblioteca?,
    val colocaciones: List<ItemBiblioteca>,
    /** claseVerbo del propio, o claseNucleo de las colocaciones (R11: coinciden cuando son los dos), o la de la referencia si no hay otra. */
    val clases: List<String>,
    val reccionNucleo: String?,
    val referencia: VerboReferencia? = null,
    /** El idioma que se aprende. Lo pone [entradasVerbo]; solo hace falta para decidir la glosa de una referencia. */
    val idioma: Idioma? = null,
) {
    /**
     * La traduccion al idioma de app: la del verbo propio si la tiene (viene del contenido, esta revisada y es la que el usuario ya
     * vio en una ficha); si no, la de referencia. Sin glosa cuando el idioma de app es el que se aprende.
     */
    fun traduccion(idiomaBase: Idioma): String? {
        // con propio manda su traduccion (o ninguna): la de referencia es el relleno de cuando NO hay propio
        if (propio != null) return propio.datosFila(idiomaBase).traduccion
        return referencia?.traduccionPara(idiomaBase, idioma ?: return null)
    }

    /** Las clases que son chip: conocidas y no `simple`. */
    fun clasesDeChip(): List<String> = clases.filter { it != CLASE_VERBO_SIMPLE && it in CLASES_VERBO_CONOCIDAS }

    /** Linea discreta de debajo de la entrada (ver [lineaExtraVerbo]). */
    fun lineaExtra(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String {
        val reccion = propio?.vocabulario?.reccion?.trim()?.takeIf { it.isNotEmpty() } ?: reccionNucleo?.trim()?.takeIf { it.isNotEmpty() }
            ?: referencia?.reccion?.trim()?.takeIf { it.isNotEmpty() }
        return lineaExtra(reccion, clases, clasesElegidas, etiquetaDeClase)
    }
}

/**
 * Las entradas de la vista Verbos de `idioma`: los verbos propios + los nucleos verbales de las colocaciones,
 * fusionados por infinitivo exacto, ordenados alfabeticamente. Las colocaciones de cada entrada van ordenadas.
 */
fun List<ItemBiblioteca>.entradasVerbo(idioma: Idioma, referencia: List<VerboReferencia> = emptyList()): List<EntradaVerbo> {
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
            idioma = idioma,
        )
    }
    for ((nucleo, items) in porNucleo) {
        if (nucleo in textosPropios) continue
        entradas += EntradaVerbo(
            infinitivo = nucleo, propio = null, colocaciones = colocacionesDe(nucleo),
            clases = items.flatMap { it.vocabulario!!.claseNucleo }.distinct(),
            reccionNucleo = items.firstNotNullOfOrNull { it.vocabulario!!.reccionNucleo?.trim()?.takeIf { r -> r.isNotEmpty() } },
            idioma = idioma,
        )
    }
    return fusionarReferencia(entradas, referencia, idioma).sortedBy { it.infinitivo.lowercase() }
}

/** La clave con la que se fusionan las fuentes: el infinitivo en minusculas y, en ingles, sin el «to » de delante. */
internal fun claveFusionVerbo(infinitivo: String, idioma: Idioma): String {
    val t = infinitivo.trim().lowercase()
    return if (idioma == Idioma.EN && t.startsWith("to ")) t.removePrefix("to ").trim() else t
}

/** Objeto de relleno al final de un phrasal verb en los items de las fichas («to deal with something»): la tabla maestra lo anota como `base` sin el. */
private val OBJETO_FINAL_EN = Regex("""\s+(something|someone|somebody|oneself|yourself)$""")

/**
 * Mete la tercera fuente (los verbos de referencia) en la MISMA fusion por infinitivo: una referencia cuyo infinitivo ya es una entrada
 * (verbo propio o nucleo) se cuelga de ella (sin cambiar su propio, sus colocaciones ni su clase); una que no coincide con ninguna es una
 * entrada nueva, de solo referencia. Sin esto, 124 verbos (aufstehen, kommen, machen, take, make...) saldrian en dos filas: una con glosa y
 * sin colocaciones, otra con colocaciones y sin glosa. Dos desacuerdos de clase entre fuentes estan anotados a proposito (el nucleo `hoeren`
 * contra su referencia, `deal with` contra la suya): aca no se resuelven ni se vigilan, la clase del propio/nucleo manda y punto.
 */
private fun fusionarReferencia(entradas: List<EntradaVerbo>, referencia: List<VerboReferencia>, idioma: Idioma): List<EntradaVerbo> {
    if (referencia.isEmpty()) return entradas
    val resultado = ArrayList<EntradaVerbo>(entradas.size + referencia.size)
    val posicion = HashMap<String, Int>(entradas.size * 2)
    // Segunda clave, solo en ingles y solo si la primera no encuentra nada: el item de la ficha «to deal with something» es la anotacion de
    // la referencia «to deal with» (la tabla los une por `base`, que en los assets no viaja). Una entrada que ya es «deal with» manda.
    val posicionSinObjeto = HashMap<String, Int>()
    for (e in entradas) {
        val clave = claveFusionVerbo(e.infinitivo, idioma)
        posicion[clave] = resultado.size
        if (idioma == Idioma.EN && OBJETO_FINAL_EN.containsMatchIn(clave)) posicionSinObjeto.putIfAbsent(OBJETO_FINAL_EN.replace(clave, ""), resultado.size)
        resultado += e
    }
    for (r in referencia) {
        val destino = posicion[claveFusionVerbo(r.item, idioma)] ?: posicion[claveFusionVerbo(r.base, idioma)]
            ?: posicionSinObjeto[claveFusionVerbo(r.item, idioma)]
        if (destino == null) {
            posicion[claveFusionVerbo(r.item, idioma)] = resultado.size
            resultado += EntradaVerbo(
                infinitivo = r.item.trim(), propio = null, colocaciones = emptyList(), clases = r.claseVerbo, reccionNucleo = null,
                referencia = r, idioma = idioma,
            )
        } else {
            val e = resultado[destino]
            if (e.referencia != null) continue // una referencia repetida: gana la primera
            // la clase de referencia solo rellena: si el propio o el nucleo ya declaran una, esa manda
            resultado[destino] = e.copy(referencia = r, clases = e.clases.ifEmpty { r.claseVerbo })
        }
    }
    return resultado
}

/**
 * La glosa de un verbo de referencia para el idioma de app (con el espanol de respaldo, igual que el resto de la app). Sin glosa si el
 * idioma de app es el que se aprende, y nunca el propio infinitivo.
 */
fun VerboReferencia.traduccionPara(idiomaBase: Idioma, idiomaAprendido: Idioma): String? {
    if (idiomaBase == idiomaAprendido) return null
    return (traducciones[idiomaBase.name.lowercase()]?.trim()?.takeIf { it.isNotEmpty() } ?: traducciones["es"]?.trim()?.takeIf { it.isNotEmpty() })
        ?.takeIf { it != item.trim() }
}

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
 * La linea discreta de debajo de un verbo (parametro `extra` de la fila): el regimen en aleman (`reccion`, p.ej.
 * «von + Dat») cuando existe, o en ingles la separabilidad del phrasal verb, salvo que ya se desprenda de un chip
 * elegido. Sin nivel (Ronda T). Si no hay nada que decir es la cadena vacia y la fila no dibuja la linea.
 */
fun ItemBiblioteca.lineaExtraVerbo(clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String =
    lineaExtra(vocabulario?.reccion?.trim()?.takeIf { it.isNotEmpty() }, vocabulario?.claseVerbo.orEmpty(), clasesElegidas, etiquetaDeClase)

private fun lineaExtra(reccion: String?, clases: List<String>, clasesElegidas: Set<String>, etiquetaDeClase: (String) -> String?): String {
    val detalle = reccion ?: run {
        val separabilidad = clases.firstOrNull { it == "phrasal-separable" || it == "phrasal-inseparable" }
        if (separabilidad == null || separabilidad in clasesElegidas) null else etiquetaDeClase(separabilidad)
    }
    return detalle.orEmpty()
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
 * Una entrada coincide por su infinitivo, por su traduccion al idioma de app (la del verbo propio o, si no hay, la de referencia), por
 * la raiz de referencia o por el
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
            e.traduccion(idiomaBase)?.let { partes += normalizarBusqueda(it) }
            e.referencia?.raiz?.let { partes += normalizarBusqueda(it) }
            for (c in e.colocaciones) {
                partes += normalizarBusqueda(c.texto)
                c.datosFila(idiomaBase).traduccion?.let { partes += normalizarBusqueda(it) }
            }
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
