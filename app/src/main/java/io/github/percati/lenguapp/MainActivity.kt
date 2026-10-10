package io.github.percati.lenguapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.percati.lenguapp.datos.cargarAjustes
import io.github.percati.lenguapp.datos.PlanillaTextos
import io.github.percati.lenguapp.datos.cargarContenidoDesdeAssets
import io.github.percati.lenguapp.datos.cargarPlanillaTextosDesdeAssets
import io.github.percati.lenguapp.datos.NombresI18n
import io.github.percati.lenguapp.datos.cargarTopicNombresDesdeAssets
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.pdf.abrirPlanilla
import io.github.percati.lenguapp.pdf.escribirPlanillaEnCache
import io.github.percati.lenguapp.presentacion.construirPlanilla
import io.github.percati.lenguapp.ui.mensajePlanillaSinLector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.percati.lenguapp.datos.cargarPlantillasPromptVozDesdeAssets
import io.github.percati.lenguapp.datos.guardarAjustes
import io.github.percati.lenguapp.datos.nombresCalendarioDisponibles
import io.github.percati.lenguapp.datos.resolverSemana
import io.github.percati.lenguapp.datos.RepositorioGuardados
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.id
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.presentacion.idiomaAplicacionEfectivo
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.SemanaIso
import io.github.percati.lenguapp.semana.idiomasConContenido
import io.github.percati.lenguapp.semana.nivelesConContenido
import io.github.percati.lenguapp.semana.semanaIsoDe
import io.github.percati.lenguapp.ui.AjustesScreen
import io.github.percati.lenguapp.ui.ContenidoSemanalScreen
import io.github.percati.lenguapp.ui.EstadoGuardados
import io.github.percati.lenguapp.ui.GuardadosScreen
import io.github.percati.lenguapp.ui.PantallaSemana
import io.github.percati.lenguapp.ui.TemaLenguApp
import io.github.percati.lenguapp.ui.claveGuardado
import io.github.percati.lenguapp.ui.BibliotecaScreen
import io.github.percati.lenguapp.ui.claveBiblioteca
import io.github.percati.lenguapp.ui.etiquetaAjustes
import io.github.percati.lenguapp.ui.etiquetaBiblioteca
import io.github.percati.lenguapp.presentacion.construirBiblioteca
import io.github.percati.lenguapp.presentacion.textoOrigen
import io.github.percati.lenguapp.datos.CategoriasUso
import io.github.percati.lenguapp.datos.cargarCategoriasUsoDesdeAssets
import io.github.percati.lenguapp.ui.etiquetaGuardados
import io.github.percati.lenguapp.ui.etiquetaHoy
import io.github.percati.lenguapp.ui.etiquetaSemana
import io.github.percati.lenguapp.ui.etiquetaVolver
import io.github.percati.lenguapp.ui.mensajeDobleAtrasParaSalir
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

private const val DESTINO_PRINCIPAL = "principal"
private const val DESTINO_AJUSTES = "ajustes"
private const val DESTINO_GUARDADOS = "guardados"
private const val DESTINO_BIBLIOTECA = "biblioteca"
private const val DESTINO_FICHA_GUARDADA = "ficha_guardada"
private const val VENTANA_DOBLE_ATRAS_MS = 3000L

/**
 * Pantalla unica + ajustes + navegacion por semana + una pestaña por idioma
 * aprendido. Ajustes es un destino real de navegacion (AJUSTES-FASE-7.md,
 * bloque 2.4), no una bandera booleana: asi el gesto de atras del sistema
 * tiene algo para desapilar. La pantalla de contenido en si
 * (io.github.percati.lenguapp.ui.ContenidoSemanalScreen) no se toca aca.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ajustesIniciales = cargarAjustes(this)
        val nombresCalendario = nombresCalendarioDisponibles(this)
        val idiomasConContenido = idiomasConContenido(nombresCalendario)
        val nivelesConContenido = nivelesConContenido(nombresCalendario)
        // Cargado y parseado UNA sola vez por Activity, no en cada resolucion
        // de semana: Guardados necesita poder buscar la ficha de origen de un
        // item por skillId sin pasar por el calendario (ver "tocar una fila
        // lleva de vuelta a la ficha de origen"), y resolverSemana() usa el
        // mismo mapa en vez de releer y reparsear los 662 JSON de
        // assets/contenido/ en cada cambio de semana/idioma/vuelta de
        // Ajustes -- eso era la causa real de la lentitud reportada (ver el
        // comentario de resolverSemana en RepositorioSemana.kt).
        val contenidoTodos = cargarContenidoDesdeAssets(this)
        val contenidoPorId = contenidoTodos.associateBy { it.id }
        val repositorioGuardados = RepositorioGuardados(this)
        val promptsVoz = cargarPlantillasPromptVozDesdeAssets(this)
        val planillaTextos = cargarPlanillaTextosDesdeAssets(this)
        val topicNombres = cargarTopicNombresDesdeAssets(this)
        val categoriasUso = cargarCategoriasUsoDesdeAssets(this)
        setContent {
            LenguAppApp(
                ajustesIniciales = ajustesIniciales,
                idiomasConContenido = idiomasConContenido,
                nivelesConContenido = nivelesConContenido,
                contenidoTodos = contenidoTodos,
                repositorioGuardados = repositorioGuardados,
                promptsVoz = promptsVoz,
                planillaTextos = planillaTextos,
                topicNombres = topicNombres,
                categoriasUso = categoriasUso,
                resolver = { idioma, nivel, fecha -> resolverSemana(this, contenidoPorId, idioma, nivel, fecha) },
                onGuardarAjustes = { guardarAjustes(this, it) },
            )
        }
    }
}

@Composable
internal fun LenguAppApp(
    ajustesIniciales: Ajustes,
    idiomasConContenido: Set<Idioma>,
    nivelesConContenido: Map<Idioma, Set<Nivel>>,
    resolver: (Idioma, Nivel, LocalDate) -> ResultadoSemana,
    onGuardarAjustes: (Ajustes) -> Unit,
    // Parametro de prueba: la produccion nunca lo pasa, asi que siempre
    // arranca en la fecha real de hoy. Los tests de pantallazo del Bloque
    // 2.1 lo necesitan para fijar de verdad "semana anterior" (no alcanza
    // con que el resolutor devuelva otro contenido: "esHoy" tiene que
    // reflejar la fecha inicial real, no una fecha distinta escondida
    // detras de un resolutor con trampa).
    fechaInicial: LocalDate = LocalDate.now(),
    // Guardados (feature 3): todo el contenido embebido, para poder buscar
    // la ficha de origen de un item guardado por skillId sin pasar por el
    // calendario. `repositorioGuardados` en null desactiva la feature entera
    // (estrellas sin efecto, Guardados vacio) -- asi los tests que no la
    // ejercitan no necesitan levantar Room.
    contenidoTodos: List<ContenidoSemanal> = emptyList(),
    repositorioGuardados: RepositorioGuardados? = null,
    promptsVoz: Map<String, String> = emptyMap(),
    // Planilla del profesor: textos fijos por idioma que se aprende + nombres de topic (banco.json).
    planillaTextos: Map<Idioma, PlanillaTextos> = emptyMap(),
    topicNombres: NombresI18n = NombresI18n(),
    categoriasUso: CategoriasUso = CategoriasUso(),
) {
    var ajustes by remember { mutableStateOf(ajustesIniciales) }
    var fechaVistaIso by rememberSaveable { mutableStateOf(fechaInicial.toString()) }
    var idiomaActivoElegido by rememberSaveable { mutableStateOf<String?>(null) }
    var fichaGuardadaAbiertaId by rememberSaveable { mutableStateOf<String?>(null) }
    val fechaVista = remember(fechaVistaIso) { LocalDate.parse(fechaVistaIso) }
    val navController = rememberNavController()

    var guardadosTodos by remember { mutableStateOf<List<ItemGuardado>>(emptyList()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(repositorioGuardados) {
        guardadosTodos = repositorioGuardados?.listar() ?: emptyList()
    }
    fun alternarGuardado(idioma: Idioma, nivel: Nivel, skillId: String, tipo: TipoGuardado, textoOrigen: String, texto: TextoBilingue, funcion: TextoBilingue?) {
        val repo = repositorioGuardados ?: return
        scope.launch {
            repo.alternar(idioma, nivel, tipo, textoOrigen, texto, funcion, skillId)
            guardadosTodos = repo.listar()
        }
    }
    // Lo que ContenidoSemanalScreen necesita de Guardados para ESTA ficha:
    // que items ya estan guardados (para la estrella llena) y el callback
    // de toggle, con skillId/idioma/nivel de la ficha ya capturados.
    fun estadoGuardadosPara(ficha: Ficha) = EstadoGuardados(
        // La identidad de un guardado es (idioma, tipo, texto): la misma palabra en
        // otra ficha u otro nivel (o desde la Biblioteca) es la misma estrella.
        guardados = guardadosTodos
            .filter { it.idioma == ficha.idioma }
            .map { claveGuardado(it.tipo, it.texto.resolver(it.idioma, it.idioma)) }
            .toSet(),
        onAlternar = { tipo, textoOrigen, texto, funcion ->
            alternarGuardado(ficha.idioma, ficha.nivel, ficha.skillId, tipo, textoOrigen, texto, funcion)
        },
    )

    // Biblioteca: agregada una sola vez (deduplicada entre todas las fichas embebidas).
    val itemsBiblioteca = remember(contenidoTodos) { construirBiblioteca(contenidoTodos) }
    val guardadasBiblioteca = guardadosTodos
        .map { claveBiblioteca(it.idioma, it.tipo, it.texto.resolver(it.idioma, it.idioma)) }
        .toSet()

    // Solo se lee el idioma del dispositivo si el usuario activo "Segun el
    // sistema" -- CLAUDE.md, regla dura #2 enmendada (AJUSTES-FASE-6.md, E).
    val codigoIdiomaSistema = remember(ajustes.idiomaSegunSistema) {
        if (ajustes.idiomaSegunSistema) Locale.getDefault().language else null
    }
    val idiomaAplicacion = idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema)

    val contexto = LocalContext.current
    // Planilla del profesor: se genera en un hilo de fondo, se escribe en la cache de
    // la app y se entrega al lector de PDF del sistema. Sin textos embebidos, no hay boton.
    val onPlanilla: ((ContenidoSemanal) -> Unit)? = if (planillaTextos.isEmpty()) {
        null
    } else {
        { contenido ->
            val idiomaAprendido = when (contenido) {
                is Ficha -> contenido.idioma
                is SemanaEspecial -> contenido.idioma
            }
            val textos = planillaTextos[idiomaAprendido] ?: planillaTextos[Idioma.EN]
            if (textos != null) {
                scope.launch {
                    val archivo = withContext(Dispatchers.IO) {
                        val planilla = construirPlanilla(contenido, textos, topicNombres)
                        escribirPlanillaEnCache(contexto, contenido.id, planilla)
                    }
                    if (!abrirPlanilla(contexto, archivo)) {
                        Toast.makeText(contexto, mensajePlanillaSinLector(idiomaAplicacion), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val idiomasSeleccionados = ajustes.idiomasAprendidos.keys.sortedBy { it.ordinal }
    val idiomaActivo = idiomasSeleccionados.firstOrNull { it.name == idiomaActivoElegido } ?: idiomasSeleccionados.firstOrNull()

    fun actualizarAjustes(nuevos: Ajustes) {
        ajustes = nuevos
        onGuardarAjustes(nuevos)
    }

    TemaLenguApp(familiaTema = ajustes.familiaTema, modoTema = ajustes.modoTema) {
        // AJUSTES-FASE-8.md, B.1: Surface() sin `color` pinta con
        // colorScheme.surface (el 30% de la regla 60-30-10), no con
        // colorScheme.background (el 60%) -- por eso las tarjetas del
        // cuadro de referencia y las insignias, que usan surfaceVariant
        // (mapeado a la misma superficie), se volvian invisibles contra el
        // fondo de la pantalla. El fondo de la app tiene que ser `fondo`.
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = DESTINO_PRINCIPAL) {
                composable(DESTINO_PRINCIPAL) {
                    PantallaPrincipal(
                        ajustes = ajustes,
                        idiomaAplicacion = idiomaAplicacion,
                        fechaVista = fechaVista,
                        idiomasSeleccionados = idiomasSeleccionados,
                        idiomaActivo = idiomaActivo,
                        resolver = resolver,
                        onIdiomaActivoElegido = { idiomaActivoElegido = it.name },
                        onSemanaAnterior = { fechaVistaIso = fechaVista.minusWeeks(1).toString() },
                        onSemanaSiguiente = { fechaVistaIso = fechaVista.plusWeeks(1).toString() },
                        onHoy = { fechaVistaIso = LocalDate.now().toString() },
                        onAjustes = { navController.navigate(DESTINO_AJUSTES) },
                        onGuardados = { navController.navigate(DESTINO_GUARDADOS) },
                        onBiblioteca = { navController.navigate(DESTINO_BIBLIOTECA) },
                        guardadosDeLaFicha = ::estadoGuardadosPara,
                        promptsVoz = promptsVoz,
                        onPlanilla = onPlanilla,
                    )
                }
                composable(DESTINO_AJUSTES) {
                    AjustesScreen(
                        ajustes = ajustes,
                        idiomasConContenido = idiomasConContenido,
                        nivelesConContenido = nivelesConContenido,
                        idiomaAplicacionEfectivo = idiomaAplicacion,
                        onAjustesCambiados = ::actualizarAjustes,
                        onVolver = { navController.popBackStack() },
                    )
                }
                composable(DESTINO_GUARDADOS) {
                    GuardadosScreen(
                        items = guardadosTodos,
                        itemsBiblioteca = itemsBiblioteca,
                        idiomasAprendidos = ajustes.idiomasAprendidos.keys,
                        idiomaBase = ajustes.idiomaBase,
                        idiomaInterfaz = idiomaAplicacion,
                        topicNombres = topicNombres,
                        categoriasUso = categoriasUso,
                        existeFichaOrigen = { fichaDeOrigen(contenidoTodos, it) != null },
                        onAbrirFicha = { item ->
                            fichaDeOrigen(contenidoTodos, item)?.let { ficha ->
                                fichaGuardadaAbiertaId = ficha.id
                                navController.navigate(DESTINO_FICHA_GUARDADA)
                            }
                        },
                        onQuitar = { item ->
                            alternarGuardado(item.idioma, item.nivel, item.skillIdOrigen, item.tipo, item.textoOrigen(), item.texto, item.funcion)
                        },
                        onVolver = { navController.popBackStack() },
                    )
                }
                composable(DESTINO_BIBLIOTECA) {
                    BibliotecaScreen(
                        items = itemsBiblioteca,
                        idiomasAprendidos = ajustes.idiomasAprendidos,
                        idiomaBase = ajustes.idiomaBase,
                        idiomaInterfaz = idiomaAplicacion,
                        topicNombres = topicNombres,
                        categorias = categoriasUso,
                        guardadas = guardadasBiblioteca,
                        onAlternar = { item ->
                            alternarGuardado(
                                item.idioma, item.nivelOrigen, item.skillIdOrigen, item.tipo,
                                item.texto, item.textoBilingue(), item.funcion,
                            )
                        },
                        onVolver = { navController.popBackStack() },
                    )
                }
                composable(DESTINO_FICHA_GUARDADA) {
                    Column(Modifier.fillMaxSize()) {
                        TextButton(onClick = { navController.popBackStack() }) { Text("< ${etiquetaVolver(idiomaAplicacion)}") }
                        contenidoTodos.filterIsInstance<Ficha>().firstOrNull { it.id == fichaGuardadaAbiertaId }?.let { ficha ->
                            ContenidoSemanalScreen(
                                ficha,
                                ajustes.idiomaBase,
                                idiomaInterfaz = idiomaAplicacion,
                                estadoGuardados = estadoGuardadosPara(ficha),
                                promptsVoz = promptsVoz,
                                onPlanilla = onPlanilla,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PantallaPrincipal(
    ajustes: Ajustes,
    idiomaAplicacion: Idioma,
    fechaVista: LocalDate,
    idiomasSeleccionados: List<Idioma>,
    idiomaActivo: Idioma?,
    resolver: (Idioma, Nivel, LocalDate) -> ResultadoSemana,
    onIdiomaActivoElegido: (Idioma) -> Unit,
    onSemanaAnterior: () -> Unit,
    onSemanaSiguiente: () -> Unit,
    onHoy: () -> Unit,
    onAjustes: () -> Unit,
    onGuardados: () -> Unit,
    onBiblioteca: () -> Unit,
    guardadosDeLaFicha: (Ficha) -> EstadoGuardados,
    promptsVoz: Map<String, String>,
    onPlanilla: ((ContenidoSemanal) -> Unit)?,
) {
    ManejarDobleAtrasParaSalir(idiomaAplicacion)

    Column(Modifier.fillMaxSize()) {
        BarraNavegacion(
            semanaIso = semanaIsoDe(fechaVista),
            esHoy = fechaVista == LocalDate.now(),
            idiomaAplicacion = idiomaAplicacion,
            onSemanaAnterior = onSemanaAnterior,
            onSemanaSiguiente = onSemanaSiguiente,
            onHoy = onHoy,
            onAjustes = onAjustes,
            onGuardados = onGuardados,
            onBiblioteca = onBiblioteca,
        )

        if (idiomasSeleccionados.isEmpty() || idiomaActivo == null) {
            SinIdiomaSeleccionado()
        } else {
            ScrollableTabRow(selectedTabIndex = idiomasSeleccionados.indexOf(idiomaActivo).coerceAtLeast(0)) {
                idiomasSeleccionados.forEach { idioma ->
                    Tab(
                        selected = idioma == idiomaActivo,
                        onClick = { onIdiomaActivoElegido(idioma) },
                        text = { Text(idioma.name) },
                    )
                }
            }
            val nivelActivo = ajustes.idiomasAprendidos.getValue(idiomaActivo)
            val resultado = remember(fechaVista, idiomaActivo, nivelActivo) {
                resolver(idiomaActivo, nivelActivo, fechaVista)
            }
            val estadoGuardados = (resultado as? ResultadoSemana.Encontrado)?.contenido
                ?.let { it as? Ficha }
                ?.let(guardadosDeLaFicha)
                ?: EstadoGuardados()
            PantallaSemana(
                resultado, ajustes.idiomaBase, idiomaAplicacion,
                fecha = fechaVista, estadoGuardados = estadoGuardados, promptsVoz = promptsVoz, onPlanilla = onPlanilla,
            )
        }
    }
}

/**
 * AJUSTES-FASE-7.md, bloque 2.4: el primer atras en la pantalla principal
 * (la raiz de la pila: no hay nada que NavHost pueda desapilar) avisa;
 * repetirlo dentro de la ventana cierra. El aviso es un toast, "del mismo
 * tipo que el del idioma no disponible", y va traducido.
 */
@Composable
private fun ManejarDobleAtrasParaSalir(idiomaAplicacion: Idioma) {
    val contexto = LocalContext.current
    val actividad = contexto as? Activity
    var ultimoAtras by remember { mutableLongStateOf(0L) }
    BackHandler {
        val ahora = System.currentTimeMillis()
        if (ahora - ultimoAtras < VENTANA_DOBLE_ATRAS_MS) {
            actividad?.finish()
        } else {
            ultimoAtras = ahora
            Toast.makeText(contexto, mensajeDobleAtrasParaSalir(idiomaAplicacion), Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * AJUSTES-FASE-7.md, bloque 2.1: el boton de Ajustes esta en su propia
 * fila, fuera de la zona del encabezado. La fila de la semana tiene la
 * flecha de ancho fijo (icono, no texto: no varia con el idioma ni con el
 * escalado de fuente) y el texto con `weight(1f)`, asi que el boton no
 * cambia de posicion cuando el sufijo "(hoy)" aparece o desaparece, ni con
 * otro idioma, ni con la fuente del sistema al 150 %.
 *
 * AJUSTES-FASE-8.md, bloque B.7: la flecha de "semana siguiente" se oculta
 * **solo** en la semana en curso, no siempre -- la instruccion original de
 * la Fase 7 era ambigua, no la decision. El tope es hoy, nunca el futuro:
 * estando atras se puede avanzar hasta esa semana y ahi la flecha
 * desaparece. Cuando esta oculta, un Spacer del mismo ancho que el
 * IconButton mantiene el texto centrado.
 */
@Composable
private fun BarraNavegacion(
    semanaIso: SemanaIso,
    esHoy: Boolean,
    idiomaAplicacion: Idioma,
    onSemanaAnterior: () -> Unit,
    onSemanaSiguiente: () -> Unit,
    onHoy: () -> Unit,
    onAjustes: () -> Unit,
    onGuardados: () -> Unit,
    onBiblioteca: () -> Unit,
) {
    val textoSemana = etiquetaSemana(idiomaAplicacion)
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // AJUSTES-FASE-7.md, bloque 1: el acento se reserva para lo
            // accionable -- flechas de navegacion incluidas -- asi que se
            // tinta explicito, no se deja el color de contenido ambiente.
            IconButton(onClick = onSemanaAnterior) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "< $textoSemana",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                "$textoSemana ${semanaIso.semana} · ${semanaIso.anio}" +
                    if (esHoy) " (${etiquetaHoy(idiomaAplicacion)})" else "",
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            if (esHoy) {
                Spacer(Modifier.size(48.dp))
            } else {
                IconButton(onClick = onSemanaSiguiente) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "> $textoSemana",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onHoy, enabled = !esHoy) { Text(etiquetaHoy(idiomaAplicacion)) }
            TextButton(onClick = onBiblioteca) { Text(etiquetaBiblioteca(idiomaAplicacion)) }
            TextButton(onClick = onGuardados) { Text(etiquetaGuardados(idiomaAplicacion)) }
            TextButton(onClick = onAjustes) { Text(etiquetaAjustes(idiomaAplicacion)) }
        }
    }
}

@Composable
private fun SinIdiomaSeleccionado() {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sin idioma seleccionado", style = MaterialTheme.typography.headlineSmall)
        Text("Elegí al menos un idioma en Ajustes para ver contenido.", style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * Guardados (feature 3): la ficha de origen de un item guardado, buscada por
 * identidad (skillId + idioma + nivel), no por id completo -- [ItemGuardado]
 * no guarda order/anio. Si el skill aparecio mas de una vez (dos ordenes, o
 * piloto y 2027 conviviendo), se queda con la primera que encuentra: es una
 * simplificacion deliberada, no hay forma de distinguir cual con el modelo
 * actual. `null` si esa edicion ya no esta en `assets/contenido/` de esta
 * build (ver GuardadosScreen, `existeFichaOrigen`).
 */
private fun fichaDeOrigen(contenidoTodos: List<ContenidoSemanal>, item: ItemGuardado): Ficha? =
    contenidoTodos.filterIsInstance<Ficha>()
        .firstOrNull { it.skillId == item.skillIdOrigen && it.idioma == item.idioma && it.nivel == item.nivel }
