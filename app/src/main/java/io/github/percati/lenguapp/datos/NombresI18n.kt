package io.github.percati.lenguapp.datos

import io.github.percati.lenguapp.modelo.Idioma

/**
 * Nombres de cosas con clave canonica (topics T01..T14, categoriasUso) en los 6 idiomas:
 * clave -> codigo de idioma -> nombre. La clave NO cambia con el idioma (es lo que se
 * guarda y se compara); solo el nombre que se muestra. Fallback: espanol, y si ni eso, la
 * propia clave (un valor sin nombre se ve feo, no desaparece).
 */
class NombresI18n(private val mapa: Map<String, Map<String, String>> = emptyMap()) {

    val claves: Set<String> get() = mapa.keys

    fun nombre(clave: String, idioma: Idioma): String {
        val porIdioma = mapa[clave] ?: return clave
        return porIdioma[idioma.name.lowercase()]?.takeIf { it.isNotBlank() }
            ?: porIdioma["es"]?.takeIf { it.isNotBlank() }
            ?: clave
    }

    /** Pares (clave, idioma) sin nombre propio en ese idioma: lo que el test de cobertura exige vacio. */
    fun faltantes(idiomas: Collection<Idioma> = Idioma.entries): List<Pair<String, Idioma>> =
        mapa.flatMap { (clave, porIdioma) ->
            idiomas.filter { porIdioma[it.name.lowercase()].isNullOrBlank() }.map { clave to it }
        }

    fun isEmpty(): Boolean = mapa.isEmpty()
}
