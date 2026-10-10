package io.github.percati.lenguapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.CriterioOrdenGuardados
import io.github.percati.lenguapp.presentacion.DireccionOrden
import io.github.percati.lenguapp.presentacion.FiltrosGuardados
import io.github.percati.lenguapp.presentacion.ItemBiblioteca
import io.github.percati.lenguapp.presentacion.datosFila
import io.github.percati.lenguapp.presentacion.delIdioma
import io.github.percati.lenguapp.presentacion.filtrarCascada
import io.github.percati.lenguapp.presentacion.indiceBiblioteca
import io.github.percati.lenguapp.presentacion.normalizarFiltros
import io.github.percati.lenguapp.presentacion.opcionesCategoria
import io.github.percati.lenguapp.presentacion.opcionesNivel
import io.github.percati.lenguapp.presentacion.opcionesTipo
import io.github.percati.lenguapp.presentacion.opcionesTopic
import io.github.percati.lenguapp.presentacion.ordenar

const val TAG_ESTRELLA_GUARDADOS = "estrella-guardados"

/**
 * Feature 3 (Guardados), reescrita en la Ronda C tarea 2: lista vertical con
 * estrella (tocarla quita el item, misma fila de Room que la Biblioteca),
 * orden (alfabetico/nivel/fecha/tipo, cada uno ascendente o descendente,
 * default fecha mas reciente primero) y filtros en cascada (idioma por
 * pestaña, tipo, nivel, topic en vocabulario, categoriasUso en expresiones)
 * que nunca pueden dar una combinacion vacia -- ver
 * presentacion/CascadaGuardados.kt para la logica pura (testeada sin
 * Robolectric) y FiltrosGuardados.normalizarFiltros() para el autoajuste.
 * Sin persistencia de orden ni de filtros (CLAUDE.md regla dura #4): cada
 * apertura arranca en el default.
 */
@Composable
fun GuardadosScreen(
    items: List<ItemGuardado>,
    itemsBiblioteca: List<ItemBiblioteca>,
    idiomasAprendidos: Set<Idioma>,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    topicNombres: NombresI18n,
    categoriasUso: CategoriasUso,
    // false cuando el contenido de esa semana/edicion ya no esta en
    // assets/contenido/ de la build actual (una edicion anterior, por
    // ejemplo): la fila se sigue mostrando, sin el link.
    existeFichaOrigen: (ItemGuardado) -> Boolean,
    onAbrirFicha: (ItemGuardado) -> Unit,
    onQuitar: (ItemGuardado) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val indice = remember(itemsBiblioteca) { itemsBiblioteca.indiceBiblioteca() }
    val idiomasDisponibles = remember(items, idiomasAprendidos) {
        (idiomasAprendidos + items.map { it.idioma }).sortedBy { it.ordinal }
    }
    var idiomaElegido by remember(idiomasDisponibles) { mutableStateOf(idiomasDisponibles.firstOrNull()) }
    var filtros by remember(idiomaElegido) { mutableStateOf(FiltrosGuardados()) }
    var criterioOrden by remember { mutableStateOf(CriterioOrdenGuardados.FECHA) }
    var direccionOrden by remember { mutableStateOf(DireccionOrden.DESCENDENTE) }

    val idioma = idiomaElegido
    val itemsIdioma = remember(items, idioma) { if (idioma == null) emptyList() else items.delIdioma(idioma) }
    val filtrosValidos = remember(itemsIdioma, filtros, indice) { itemsIdioma.normalizarFiltros(filtros, indice) }
    val filtrados = remember(itemsIdioma, filtrosValidos, indice, criterioOrden, direccionOrden, idiomaBase) {
        itemsIdioma.filtrarCascada(filtrosValidos, indice).ordenar(criterioOrden, direccionOrden, idiomaBase)
    }
    val hayFiltrosQueQuitar = filtrosValidos != FiltrosGuardados()

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onVolver) { Text("< ${etiquetaVolver(idiomaInterfaz)}") }
            Text(etiquetaGuardados(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        }

        if (idiomasDisponibles.isEmpty() || idioma == null) {
            Text(mensajeGuardadosVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
        } else {
            ScrollableTabRow(selectedTabIndex = idiomasDisponibles.indexOf(idioma).coerceAtLeast(0)) {
                idiomasDisponibles.forEach { i ->
                    Tab(selected = i == idioma, onClick = { idiomaElegido = i }, text = { Text(nombreIdioma(i, idiomaInterfaz)) })
                }
            }

            if (hayFiltrosQueQuitar) {
                TextButton(onClick = { filtros = FiltrosGuardados() }) { Text(etiquetaQuitarFiltros(idiomaInterfaz)) }
            }

            FacetaTipo(itemsIdioma, filtrosValidos, indice, idiomaInterfaz) { filtros = filtrosValidos.copy(tipo = it, topic = null, categoria = null) }
            FacetaNivel(itemsIdioma, filtrosValidos, indice, idiomaInterfaz) { filtros = filtrosValidos.copy(nivel = it) }
            if (filtrosValidos.tipo == TipoGuardado.VOCABULARIO) {
                FacetaTopic(itemsIdioma, filtrosValidos, indice, topicNombres, idiomaInterfaz) { filtros = filtrosValidos.copy(topic = it) }
            }
            if (filtrosValidos.tipo == TipoGuardado.EXPRESION) {
                FacetaCategoria(itemsIdioma, filtrosValidos, indice, categoriasUso, idiomaInterfaz) { filtros = filtrosValidos.copy(categoria = it) }
            }
            FilaOrden(criterioOrden, direccionOrden, idiomaInterfaz, onCriterio = { criterioOrden = it }, onDireccion = { direccionOrden = it })

            if (filtrados.isEmpty()) {
                Text(mensajeGuardadosVacio(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filtrados.forEach { item ->
                        FilaGuardado(
                            item,
                            idiomaBase,
                            idiomaInterfaz,
                            enlazable = existeFichaOrigen(item),
                            onClick = { onAbrirFicha(item) },
                            onQuitar = { onQuitar(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FacetaTipo(
    items: List<ItemGuardado>,
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
    idiomaInterfaz: Idioma,
    onElegir: (TipoGuardado?) -> Unit,
) {
    val opciones = items.opcionesTipo(filtros, indice)
    val total = opciones.values.sum()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiquetaFiltroTipo(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filtros.tipo == null, onClick = { onElegir(null) }, label = { Text("${etiquetaFiltroTodos(idiomaInterfaz)} ($total)") })
            if ((opciones[TipoGuardado.VOCABULARIO] ?: 0) > 0) {
                FilterChip(
                    selected = filtros.tipo == TipoGuardado.VOCABULARIO,
                    onClick = { onElegir(TipoGuardado.VOCABULARIO) },
                    label = { Text("${etiquetaTipoVocabulario(idiomaInterfaz)} (${opciones.getValue(TipoGuardado.VOCABULARIO)})") },
                )
            }
            if ((opciones[TipoGuardado.EXPRESION] ?: 0) > 0) {
                FilterChip(
                    selected = filtros.tipo == TipoGuardado.EXPRESION,
                    onClick = { onElegir(TipoGuardado.EXPRESION) },
                    label = { Text("${etiquetaTipoExpresion(idiomaInterfaz)} (${opciones.getValue(TipoGuardado.EXPRESION)})") },
                )
            }
        }
    }
}

@Composable
private fun FacetaNivel(
    items: List<ItemGuardado>,
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
    idiomaInterfaz: Idioma,
    onElegir: (Nivel?) -> Unit,
) {
    val opciones = items.opcionesNivel(filtros, indice)
    val total = opciones.values.sum()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiquetaNivel(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filtros.nivel == null, onClick = { onElegir(null) }, label = { Text("${etiquetaFiltroTodos(idiomaInterfaz)} ($total)") })
            Nivel.entries.filter { (opciones[it] ?: 0) > 0 }.forEach { nivel ->
                FilterChip(selected = filtros.nivel == nivel, onClick = { onElegir(nivel) }, label = { Text("${nivel.name} (${opciones.getValue(nivel)})") })
            }
        }
    }
}

@Composable
private fun FacetaTopic(
    items: List<ItemGuardado>,
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
    topicNombres: NombresI18n,
    idiomaInterfaz: Idioma,
    onElegir: (String?) -> Unit,
) {
    val opciones = items.opcionesTopic(filtros, indice)
    if (opciones.isEmpty()) return
    val total = opciones.values.sum()
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiquetaBibliotecaTema(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filtros.topic == null, onClick = { onElegir(null) }, label = { Text("${etiquetaFiltroTodos(idiomaInterfaz)} ($total)") })
            opciones.keys.sorted().forEach { id ->
                val nombre = topicNombres.nombre(id, idiomaInterfaz)
                FilterChip(selected = filtros.topic == id, onClick = { onElegir(id) }, label = { Text("$nombre (${opciones.getValue(id)})") })
            }
        }
    }
}

@Composable
private fun FacetaCategoria(
    items: List<ItemGuardado>,
    filtros: FiltrosGuardados,
    indice: Map<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>,
    categoriasUso: CategoriasUso,
    idiomaInterfaz: Idioma,
    onElegir: (String?) -> Unit,
) {
    val opciones = items.opcionesCategoria(filtros, indice)
    if (opciones.isEmpty()) return
    val total = opciones.values.sum()
    // El orden de la lista cerrada (funciones primero, despues patrones), no alfabetico:
    // es el mismo orden que usa la Biblioteca para las dos ramas.
    val ordenCerrado = categoriasUso.funcionComunicativa + categoriasUso.patronGramatical
    val valoresOrdenados = opciones.keys.sortedBy { ordenCerrado.indexOf(it).let { i -> if (i < 0) Int.MAX_VALUE else i } }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiquetaBibliotecaFunciones(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filtros.categoria == null, onClick = { onElegir(null) }, label = { Text("${etiquetaFiltroTodos(idiomaInterfaz)} ($total)") })
            valoresOrdenados.forEach { cat ->
                FilterChip(selected = filtros.categoria == cat, onClick = { onElegir(cat) }, label = { Text("${categoriasUso.nombre(cat, idiomaInterfaz)} (${opciones.getValue(cat)})") })
            }
        }
    }
}

@Composable
private fun FilaOrden(
    criterio: CriterioOrdenGuardados,
    direccion: DireccionOrden,
    idiomaInterfaz: Idioma,
    onCriterio: (CriterioOrdenGuardados) -> Unit,
    onDireccion: (DireccionOrden) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(etiquetaOrdenarPor(idiomaInterfaz), style = MaterialTheme.typography.labelMedium)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = criterio == CriterioOrdenGuardados.ALFABETICO, onClick = { onCriterio(CriterioOrdenGuardados.ALFABETICO) }, label = { Text(etiquetaOrdenAlfabetico(idiomaInterfaz)) })
            FilterChip(selected = criterio == CriterioOrdenGuardados.NIVEL, onClick = { onCriterio(CriterioOrdenGuardados.NIVEL) }, label = { Text(etiquetaNivel(idiomaInterfaz)) })
            FilterChip(selected = criterio == CriterioOrdenGuardados.FECHA, onClick = { onCriterio(CriterioOrdenGuardados.FECHA) }, label = { Text(etiquetaOrdenFecha(idiomaInterfaz)) })
            FilterChip(selected = criterio == CriterioOrdenGuardados.TIPO, onClick = { onCriterio(CriterioOrdenGuardados.TIPO) }, label = { Text(etiquetaFiltroTipo(idiomaInterfaz)) })
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = direccion == DireccionOrden.ASCENDENTE, onClick = { onDireccion(DireccionOrden.ASCENDENTE) }, label = { Text(etiquetaAscendente(idiomaInterfaz)) })
            FilterChip(selected = direccion == DireccionOrden.DESCENDENTE, onClick = { onDireccion(DireccionOrden.DESCENDENTE) }, label = { Text(etiquetaDescendente(idiomaInterfaz)) })
        }
    }
}

@Composable
private fun FilaGuardado(item: ItemGuardado, idiomaBase: Idioma, idiomaInterfaz: Idioma, enlazable: Boolean, onClick: () -> Unit, onQuitar: () -> Unit) {
    // La misma fila que la Biblioteca (ui/FilaPalabra.kt): original, traduccion y funcion; solo cambia la estrella.
    val d = item.datosFila(idiomaBase)
    FilaPalabra(
        original = d.original,
        traduccion = d.traduccion,
        funcion = d.funcion,
        idiomaAprendido = item.idioma,
        idiomaBase = idiomaBase,
        guardada = true,
        tagEstrella = TAG_ESTRELLA_GUARDADOS,
        descripcionEstrella = etiquetaQuitarEstrella(idiomaInterfaz),
        onEstrella = onQuitar,
        onClick = if (enlazable) onClick else null,
    )
}
