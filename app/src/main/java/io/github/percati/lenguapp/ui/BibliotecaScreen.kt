package io.github.percati.lenguapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import io.github.percati.lenguapp.presentacion.FilaHabilidad
import io.github.percati.lenguapp.presentacion.IndiceHabilidades
import androidx.compose.foundation.layout.ColumnScope
import io.github.percati.lenguapp.presentacion.BuscadorVerbos
import io.github.percati.lenguapp.presentacion.contarClases
import io.github.percati.lenguapp.presentacion.filtrarPorClases
import io.github.percati.lenguapp.presentacion.EntradaVerbo
import io.github.percati.lenguapp.presentacion.entradasVerbo
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.datosFila
import io.github.percati.lenguapp.presentacion.SIN_CATEGORIA
import io.github.percati.lenguapp.presentacion.TraduccionRedemittel
import io.github.percati.lenguapp.presentacion.categoriasDelIdioma
import io.github.percati.lenguapp.presentacion.contarCategorias
import io.github.percati.lenguapp.presentacion.contarTopics
import io.github.percati.lenguapp.presentacion.filtrar
import io.github.percati.lenguapp.presentacion.topicsDelIdioma
import io.github.percati.lenguapp.presentacion.traduccionParaMostrar

const val TAG_ESTRELLA_BIBLIOTECA = "estrella-biblioteca"

/** Clave de la estrella en la Biblioteca: idioma + tipo + texto (la identidad de un guardado, ver RepositorioGuardados.alternar). */
fun claveBiblioteca(idioma: Idioma, tipo: TipoGuardado, texto: String): String = "$idioma|$tipo|$texto"

/**
 * Biblioteca: consulta pura en pantalla de todo el vocabulario y las expresiones
 * embebidos, deduplicados. Pestañas por idioma aprendido (filtra, no mezcla); nivel
 * multi-seleccion con el nivel actual del idioma por defecto (se cambia aca sin
 * tocar Ajustes); vocabulario por topic, expresiones por categoriasUso (agrupadas
 * en sus dos ramas); buscador libre. La traduccion siempre en el idioma de app. Sin
 * persistencia: filtros y busqueda arrancan en el default en cada apertura (regla
 * dura #4). La estrella es la de Guardados (mismo Room).
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun BibliotecaScreen(
    items: List<ItemBiblioteca>,
    idiomasAprendidos: Map<Idioma, Nivel>,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    topicNombres: NombresI18n,
    categorias: CategoriasUso,
    guardadas: Set<String>,
    onAlternar: (ItemBiblioteca) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    // Segunda vista: indice de habilidades del idioma de la pestana (solo lectura) y como abrir una.
    habilidades: (Idioma) -> IndiceHabilidades = { IndiceHabilidades.SinCalendario(0) },
    onAbrirFicha: (String) -> Unit = {},
) {
    val idiomas = remember(idiomasAprendidos) { idiomasAprendidos.keys.sortedBy { it.ordinal } }
    var idiomaElegido by remember(idiomas) { mutableStateOf(idiomas.firstOrNull()) }
    var nivelesPorIdioma by remember { mutableStateOf<Map<Idioma, Set<Nivel>>>(emptyMap()) }
    var tipo by remember { mutableStateOf(TipoGuardado.VOCABULARIO) }
    // Topic y categorias elegidos son DE la pestana: al cambiar de idioma arrancan vacios (un valor del otro idioma podria no existir aca).
    var topic by remember(idiomaElegido) { mutableStateOf<String?>(null) }
    var categoriasElegidas by remember(idiomaElegido) { mutableStateOf<Set<String>>(emptySet()) }
    var busqueda by remember { mutableStateOf("") }
    // Vista Verbos: las clases elegidas (chips) son DE la pestana; sin persistencia.
    var clasesElegidas by remember(idiomaElegido) { mutableStateOf<Set<String>>(emptySet()) }
    // La busqueda de la vista Verbos filtra mientras se escribe, sobre los ya filtrados por clase; sin persistencia.
    var busquedaVerbos by remember(idiomaElegido) { mutableStateOf("") }
    // Vista: palabras y expresiones (la de siempre) o indice de habilidades. Sin persistencia: cada apertura arranca en la primera.
    var vista by remember { mutableStateOf(VistaBiblioteca.LEXICO) }

    val idioma = idiomaElegido
    val nivelesDefault = idioma?.let { setOfNotNull(idiomasAprendidos[it]) } ?: emptySet()
    val niveles = idioma?.let { nivelesPorIdioma[it] ?: nivelesDefault } ?: emptySet()
    val filtrados = remember(items, idioma, tipo, niveles, topic, categoriasElegidas, busqueda, idiomaBase) {
        if (idioma == null) emptyList() else items.filtrar(idioma, tipo, niveles, topic, categoriasElegidas, busqueda, idiomaBase)
    }
    // Las listas de chips salen de los items del idioma de la pestana, todos sus niveles (P3);
    // el contador es con el filtro de nivel activo.
    val topicsDisponibles = remember(items, idioma) { if (idioma == null) emptySet() else items.topicsDelIdioma(idioma) }
    val categoriasDisponibles = remember(items, idioma) { if (idioma == null) emptySet() else items.categoriasDelIdioma(idioma) }
    val contadorTopics = remember(items, idioma, niveles) { if (idioma == null) emptyMap() else items.contarTopics(idioma, niveles) }
    val contadorCategorias = remember(items, idioma, niveles) { if (idioma == null) emptyMap() else items.contarCategorias(idioma, niveles) }
    val hayFiltrosQueQuitar = niveles != nivelesDefault || topic != null || categoriasElegidas.isNotEmpty()
    fun quitarTodosLosFiltros() {
        if (idioma != null) nivelesPorIdioma = nivelesPorIdioma - idioma
        topic = null
        categoriasElegidas = emptySet()
    }

    // Vista Verbos: TODAS las entradas del idioma de la pestana (verbos propios + nucleos verbales de las
    // colocaciones, fusionados por infinitivo); el nivel no es una dimension de la vista (ni filtra ni se fija
    // en el de Ajustes). Chips de clase y cuentas sobre ese conjunto (P3).
    val verbos = remember(items, idioma) { if (idioma == null) emptyList() else items.entradasVerbo(idioma) }
    val conteoClases = remember(verbos) { verbos.contarClases() }
    // El texto normalizado de cada entrada y de sus colocaciones se calcula una vez, no en cada tecla.
    val buscadorVerbos = remember(verbos, idiomaBase) { BuscadorVerbos(verbos, idiomaBase) }
    val verbosFiltrados = remember(verbos, clasesElegidas, busquedaVerbos, buscadorVerbos) {
        buscadorVerbos.filtrar(verbos.filtrarPorClases(clasesElegidas), busquedaVerbos)
    }

    // El indice se arma solo si se esta mirando esa vista.
    val indiceHabilidades = if (idioma != null && vista == VistaBiblioteca.HABILIDADES) remember(idioma, habilidades) { habilidades(idioma) } else null

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaInterfaz)}") }
            Text(etiquetaBiblioteca(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        }

        if (idioma == null) {
            Text(etiquetaBibliotecaVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
            return@Column
        }

        LazyColumn {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = espaciadoActual().entreItems)) {
                    ScrollableTabRow(selectedTabIndex = idiomas.indexOf(idioma).coerceAtLeast(0)) {
                        idiomas.forEach { i ->
                            Tab(selected = i == idioma, onClick = { idiomaElegido = i }, text = { Text(nombreIdioma(i, idiomaInterfaz)) })
                        }
                    }

                    // Selector de vistas: una fila desplazable que aguanta tres (la futura vista "Verbos"
                    // se suma a esta lista y a `when (vista)` mas abajo).
                    SelectorDeVistas(
                        vistas = listOf(
                            OpcionFiltro(VistaBiblioteca.LEXICO.name, etiquetaBibliotecaVistaLexico(idiomaInterfaz)),
                            OpcionFiltro(VistaBiblioteca.HABILIDADES.name, etiquetaBibliotecaVistaHabilidades(idiomaInterfaz)),
                            OpcionFiltro(VistaBiblioteca.VERBOS.name, etiquetaBibliotecaVistaVerbos(idiomaInterfaz)),
                        ),
                        elegida = vista.name,
                        onElegir = { vista = VistaBiblioteca.valueOf(it) },
                    )

                    if (vista == VistaBiblioteca.LEXICO) Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = tipo == TipoGuardado.VOCABULARIO,
                            onClick = { tipo = TipoGuardado.VOCABULARIO },
                            label = { Text(etiquetaTipoVocabulario(idiomaInterfaz)) },
                        )
                        FilterChip(
                            selected = tipo == TipoGuardado.EXPRESION,
                            onClick = { tipo = TipoGuardado.EXPRESION },
                            label = { Text(etiquetaTipoExpresion(idiomaInterfaz)) },
                        )
                        if (hayFiltrosQueQuitar) {
                            TextButton(onClick = { quitarTodosLosFiltros() }) { Text(etiquetaQuitarFiltros(idiomaInterfaz)) }
                        }
                    }

                    Text(etiquetaNivel(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
                    GrupoChips(
                        opciones = Nivel.entries.map { OpcionFiltro(it.name, it.name) },
                        estaElegida = { Nivel.valueOf(it.clave) in niveles },
                        onElegir = { o ->
                            val n = Nivel.valueOf(o.clave)
                            val nuevo = if (n in niveles) niveles - n else niveles + n
                            nivelesPorIdioma = nivelesPorIdioma + (idioma to nuevo)
                        },
                    )

                    if (tipo == TipoGuardado.VOCABULARIO) {
                        Text(etiquetaBibliotecaTema(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
                        GrupoChips(
                            opciones = listOf(OpcionFiltro("", etiquetaFiltroTodos(idiomaInterfaz))) +
                                topicsDisponibles.sorted().map { OpcionFiltro(it, "${topicNombres.nombre(it, idiomaInterfaz)} (${contadorTopics[it] ?: 0})") },
                            estaElegida = { (topic ?: "") == it.clave },
                            onElegir = { topic = it.clave.ifEmpty { null } },
                            disposicion = DisposicionChips.AJUSTADA,
                        )
                    } else {
                        RamaCategorias(etiquetaBibliotecaFunciones(idiomaInterfaz), etiquetaQuitar(idiomaInterfaz), categorias.funcionComunicativa.filter { it in categoriasDisponibles }, categoriasElegidas, contadorCategorias, { categorias.nombre(it, idiomaInterfaz) }) { categoriasElegidas = it }
                        RamaCategorias(etiquetaBibliotecaPatrones(idiomaInterfaz), etiquetaQuitar(idiomaInterfaz), categorias.patronGramatical.filter { it in categoriasDisponibles }, categoriasElegidas, contadorCategorias, { categorias.nombre(it, idiomaInterfaz) }) { categoriasElegidas = it }
                        if (SIN_CATEGORIA in categoriasDisponibles) {
                            GrupoChips(
                                opciones = listOf(OpcionFiltro(SIN_CATEGORIA, "${etiquetaBibliotecaSinCategoria(idiomaInterfaz)} (${contadorCategorias[SIN_CATEGORIA] ?: 0})")),
                                estaElegida = { SIN_CATEGORIA in categoriasElegidas },
                                onElegir = { categoriasElegidas = alternarEn(categoriasElegidas, SIN_CATEGORIA) },
                                disposicion = DisposicionChips.AJUSTADA,
                            )
                        }
                    }

                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        label = { Text(etiquetaBibliotecaBuscar(idiomaInterfaz)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    }

                }
            }

            if (vista == VistaBiblioteca.VERBOS) {
                // La busqueda es LA forma de usar esta vista (cientos de entradas): queda fija arriba mientras se
                // recorre la lista, con fondo opaco. Los chips de clase se van con el scroll (la busqueda tiene prioridad).
                if (verbos.isNotEmpty()) {
                    stickyHeader(key = "buscar-verbos") {
                        Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = busquedaVerbos,
                                onValueChange = { busquedaVerbos = it },
                                label = { Text(etiquetaBibliotecaBuscar(idiomaInterfaz)) },
                                singleLine = true,
                                trailingIcon = if (busquedaVerbos.isEmpty()) null else ({
                                    IconButton(onClick = { busquedaVerbos = "" }) {
                                        Icon(Icons.Filled.Close, contentDescription = descripcionBorrarBusqueda(idiomaInterfaz))
                                    }
                                }),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            )
                        }
                    }
                }
                // Un solo grupo: la clase. Solo las que existen en las entradas de este idioma (P3);
                // `simple` nunca es un chip; sin ninguno elegido se ven todas.
                if (conteoClases.isNotEmpty()) {
                    item(key = "chips-verbos") {
                        GrupoChips(
                            opciones = conteoClases.map { (clase, n) ->
                                OpcionFiltro(clase, "${etiquetaClaseVerbo(clase, idiomaInterfaz) ?: clase} ($n)")
                            },
                            estaElegida = { it.clave in clasesElegidas },
                            onElegir = { clasesElegidas = alternarEn(clasesElegidas, it.clave) },
                            disposicion = DisposicionChips.AJUSTADA,
                        )
                    }
                }
                if (verbos.isEmpty()) {
                    item { Text(mensajeVerbosVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge) }
                }
                if (verbos.isNotEmpty() && verbosFiltrados.isEmpty()) {
                    item { Text(mensajeVerbosSinResultados(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge) }
                }
                items(verbosFiltrados, key = { "verbo|${it.propio?.idioma ?: idioma}|${it.infinitivo}" }) { entrada ->
                    FilaEntradaVerbo(
                        entrada = entrada,
                        idioma = idioma,
                        idiomaBase = idiomaBase,
                        idiomaInterfaz = idiomaInterfaz,
                        clasesElegidas = clasesElegidas,
                        guardadas = guardadas,
                        onAlternar = onAlternar,
                    )
                }
                return@LazyColumn
            }

            if (indiceHabilidades != null) {
                when (val indice: IndiceHabilidades = indiceHabilidades) {
                    is IndiceHabilidades.SinCalendario ->
                        item { Text(mensajeSinCalendario(idiomaInterfaz, indice.anio), style = MaterialTheme.typography.bodyLarge) }
                    is IndiceHabilidades.Filas -> {
                        item {
                            Text(
                                "${etiquetaNivel(idiomaInterfaz)} ${indice.nivel.name} · ${indice.anio}",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        if (indice.filas.isEmpty()) item { Text(etiquetaBibliotecaVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge) }
                        items(indice.filas, key = { "habilidad|${it.skillId}" }) { fila ->
                            FilaHabilidadIndice(fila, idiomaInterfaz) { onAbrirFicha(fila.fichaAAbrir) }
                        }
                    }
                }
                return@LazyColumn
            }

            if (filtrados.isEmpty()) {
                item { Text(etiquetaBibliotecaVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge) }
            }
            items(filtrados, key = { claveBiblioteca(it.idioma, it.tipo, it.texto) }) { item ->
                FilaBiblioteca(
                    item = item,
                    idiomaBase = idiomaBase,
                    guardada = claveBiblioteca(item.idioma, item.tipo, item.texto) in guardadas,
                    onAlternar = { onAlternar(item) },
                )
            }
        }
    }
}

private fun alternarEn(conjunto: Set<String>, valor: String): Set<String> =
    if (valor in conjunto) conjunto - valor else conjunto + valor

/** Una rama de la lista cerrada de categoriasUso, como una fila de chips con su titulo (los nombres, en el idioma de app). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RamaCategorias(titulo: String, etiquetaQuitar: String, valores: List<String>, elegidas: Set<String>, contador: Map<String, Int>, nombre: (String) -> String, onCambio: (Set<String>) -> Unit) {
    if (valores.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium)
            if (elegidas.any { it in valores }) {
                TextButton(onClick = { onCambio(elegidas - valores.toSet()) }) { Text(etiquetaQuitar, style = MaterialTheme.typography.labelMedium) }
            }
        }
        GrupoChips(
            opciones = valores.map { OpcionFiltro(it, "${nombre(it)} (${contador[it] ?: 0})") },
            estaElegida = { it.clave in elegidas },
            onElegir = { onCambio(alternarEn(elegidas, it.clave)) },
            disposicion = DisposicionChips.AJUSTADA,
        )
    }
}

@Composable
private fun FilaBiblioteca(
    item: ItemBiblioteca,
    idiomaBase: Idioma,
    guardada: Boolean,
    onAlternar: () -> Unit,
    extra: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val d = item.datosFila(idiomaBase)
    FilaPalabra(
        original = d.original,
        traduccion = d.traduccion,
        funcion = d.funcion,
        idiomaAprendido = item.idioma,
        idiomaBase = idiomaBase,
        guardada = guardada,
        tagEstrella = TAG_ESTRELLA_BIBLIOTECA,
        descripcionEstrella = null,
        onEstrella = onAlternar,
        extra = extra,
    )
}

/**
 * Una entrada de la vista Verbos. Verbo propio: su fila de siempre (marcable, con traduccion). Nucleo puro: un
 * encabezado con el infinitivo, SIN estrella ni traduccion (no es un item). En los dos casos las colocaciones
 * van visibles debajo, cada una con su traduccion y su propia estrella (la clave de Guardados es la de la colocacion).
 */
@Composable
private fun FilaEntradaVerbo(
    entrada: EntradaVerbo,
    idioma: Idioma,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    clasesElegidas: Set<String>,
    guardadas: Set<String>,
    onAlternar: (ItemBiblioteca) -> Unit,
) {
    // La linea discreta: nivel mas bajo (dato, no filtro) + regimen en aleman o separabilidad en ingles.
    val linea = entrada.lineaExtra(clasesElegidas) { etiquetaClaseVerbo(it, idiomaInterfaz) }.ifEmpty { null }
    val extra: (@Composable ColumnScope.() -> Unit)? = if (linea == null) null else ({
        Text(linea, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    })
    Column {
        val propio = entrada.propio
        if (propio != null) {
            FilaBiblioteca(
                item = propio,
                idiomaBase = idiomaBase,
                guardada = claveBiblioteca(propio.idioma, propio.tipo, propio.texto) in guardadas,
                onAlternar = { onAlternar(propio) },
                extra = extra,
            )
        } else {
            FilaPalabra(
                original = entrada.infinitivo, traduccion = null, funcion = null,
                idiomaAprendido = idioma, idiomaBase = idiomaBase,
                guardada = false, tagEstrella = TAG_ESTRELLA_BIBLIOTECA, descripcionEstrella = null, onEstrella = {},
                extra = extra, conEstrella = false,
            )
        }
        for (colocacion in entrada.colocaciones) {
            Box(Modifier.padding(start = 20.dp)) {
                FilaBiblioteca(
                    item = colocacion,
                    idiomaBase = idiomaBase,
                    guardada = claveBiblioteca(colocacion.idioma, colocacion.tipo, colocacion.texto) in guardadas,
                    onAlternar = { onAlternar(colocacion) },
                )
            }
        }
    }
}

/** Las tres vistas de la Biblioteca. */
private enum class VistaBiblioteca { LEXICO, HABILIDADES, VERBOS }

/** Una habilidad del indice: titulo y semanas del anio; un toque abre la ficha. */
@Composable
private fun FilaHabilidadIndice(fila: FilaHabilidad, idiomaInterfaz: Idioma, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = espaciadoActual().entreItems),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(espaciadoActual().dentroDeItem)) {
                Text(fila.titulo, style = MaterialTheme.typography.bodyLarge)
                Text(
                    textoHabilidadSemanas(idiomaInterfaz, fila.semanas),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}
