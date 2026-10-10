package io.github.percati.lenguapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
) {
    val idiomas = remember(idiomasAprendidos) { idiomasAprendidos.keys.sortedBy { it.ordinal } }
    var idiomaElegido by remember(idiomas) { mutableStateOf(idiomas.firstOrNull()) }
    var nivelesPorIdioma by remember { mutableStateOf<Map<Idioma, Set<Nivel>>>(emptyMap()) }
    var tipo by remember { mutableStateOf(TipoGuardado.VOCABULARIO) }
    // Topic y categorias elegidos son DE la pestana: al cambiar de idioma arrancan vacios (un valor del otro idioma podria no existir aca).
    var topic by remember(idiomaElegido) { mutableStateOf<String?>(null) }
    var categoriasElegidas by remember(idiomaElegido) { mutableStateOf<Set<String>>(emptySet()) }
    var busqueda by remember { mutableStateOf("") }

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

    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaInterfaz)}") }
            Text(etiquetaBiblioteca(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        }

        if (idioma == null) {
            Text(etiquetaBibliotecaVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(espaciadoActual().entreItems)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ScrollableTabRow(selectedTabIndex = idiomas.indexOf(idioma).coerceAtLeast(0)) {
                        idiomas.forEach { i ->
                            Tab(selected = i == idioma, onClick = { idiomaElegido = i }, text = { Text(nombreIdioma(i, idiomaInterfaz)) })
                        }
                    }

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
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Nivel.entries.forEach { n ->
                            FilterChip(
                                selected = n in niveles,
                                onClick = {
                                    val nuevo = if (n in niveles) niveles - n else niveles + n
                                    nivelesPorIdioma = nivelesPorIdioma + (idioma to nuevo)
                                },
                                label = { Text(n.name) },
                            )
                        }
                    }

                    if (tipo == TipoGuardado.VOCABULARIO) {
                        Text(etiquetaBibliotecaTema(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = topic == null, onClick = { topic = null }, label = { Text(etiquetaFiltroTodos(idiomaInterfaz)) })
                            topicsDisponibles.sorted().forEach { id ->
                                FilterChip(
                                    selected = topic == id,
                                    onClick = { topic = id },
                                    label = { Text("${topicNombres.nombre(id, idiomaInterfaz)} (${contadorTopics[id] ?: 0})") },
                                )
                            }
                        }
                    } else {
                        RamaCategorias(etiquetaBibliotecaFunciones(idiomaInterfaz), etiquetaQuitar(idiomaInterfaz), categorias.funcionComunicativa.filter { it in categoriasDisponibles }, categoriasElegidas, contadorCategorias, { categorias.nombre(it, idiomaInterfaz) }) { categoriasElegidas = it }
                        RamaCategorias(etiquetaBibliotecaPatrones(idiomaInterfaz), etiquetaQuitar(idiomaInterfaz), categorias.patronGramatical.filter { it in categoriasDisponibles }, categoriasElegidas, contadorCategorias, { categorias.nombre(it, idiomaInterfaz) }) { categoriasElegidas = it }
                        if (SIN_CATEGORIA in categoriasDisponibles) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = SIN_CATEGORIA in categoriasElegidas,
                                    onClick = { categoriasElegidas = alternarEn(categoriasElegidas, SIN_CATEGORIA) },
                                    label = { Text("${etiquetaBibliotecaSinCategoria(idiomaInterfaz)} (${contadorCategorias[SIN_CATEGORIA] ?: 0})") },
                                )
                            }
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
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            valores.forEach { v ->
                FilterChip(selected = v in elegidas, onClick = { onCambio(alternarEn(elegidas, v)) }, label = { Text("${nombre(v)} (${contador[v] ?: 0})") })
            }
        }
    }
}

@Composable
private fun FilaBiblioteca(item: ItemBiblioteca, idiomaBase: Idioma, guardada: Boolean, onAlternar: () -> Unit) {
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
    )
}
