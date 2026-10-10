package io.github.percati.lenguapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.percati.lenguapp.datos.NombresI18n
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.ChallengeType
import io.github.percati.lenguapp.modelo.Clase
import io.github.percati.lenguapp.modelo.ContenidoSemanal
import io.github.percati.lenguapp.modelo.CuadroReferencia
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.aTextoBilingue
import io.github.percati.lenguapp.modelo.resolver
import io.github.percati.lenguapp.modelo.VocabularioItem
import io.github.percati.lenguapp.presentacion.TraduccionRedemittel
import io.github.percati.lenguapp.presentacion.contrasteParaMostrar
import io.github.percati.lenguapp.presentacion.erroresParaMostrar
import io.github.percati.lenguapp.presentacion.expresionesDeLaFicha
import io.github.percati.lenguapp.presentacion.idiomaMostradoDeFicha
import io.github.percati.lenguapp.presentacion.oralMinRedondeado
import io.github.percati.lenguapp.presentacion.promptEnIdiomaAprendido
import io.github.percati.lenguapp.presentacion.promptVozPara
import io.github.percati.lenguapp.presentacion.traduccionParaMostrar
import io.github.percati.lenguapp.semana.RazonSinContenido
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.semana.esFinDeSemana
import java.time.LocalDate

/**
 * AJUSTES-FASE-8.md, B.2: separacion entre secciones (Beispiele, Hinweise,
 * Kontrast, Typische Fehler...) resuelta con espacio, no con lineas. La
 * Fase 5 ya habia pedido menos reglas divisorias y mas contraste
 * tipografico; el resultado quedo demasiado plano. Este valor es bastante
 * mayor que el espaciado dentro de una seccion (Seccion() usa 8.dp entre su
 * titulo y el cuerpo, y entre parrafos/vinetas del mismo cuerpo) para que el
 * corte entre secciones se note sin una regla. Un separador tenue en color
 * secundario queda como ultimo recurso si esto no alcanza -- no aplicado.
 */
/** Cuantas frases de la ficha se ofrecen en el reto del fin de semana. */
private const val MAX_FRASES_RETO = 3

private val ESPACIO_ENTRE_SECCIONES = 32.dp

/** Separacion entre items de vocabulario / expresiones: mayor que la que hay DENTRO de un item (palabra + traduccion pegadas). */
private val ESPACIO_ENTRE_ITEMS = 22.dp

/**
 * Lo que [ContenidoSemanalScreen] necesita de Guardados (feature 3) para
 * pintar la estrella de cada item de vocabulario/Redemittel, sin saber nada
 * de Room ni de coroutines: el llamador (io.github.percati.lenguapp.MainActivity)
 * carga y persiste, esto solo pinta y avisa. [guardados] son las claves
 * (ver [claveGuardado]) ya guardadas de LA FICHA QUE SE ESTA MOSTRANDO.
 */
data class EstadoGuardados(
    val guardados: Set<String> = emptySet(),
    val onAlternar: (tipo: TipoGuardado, textoOrigen: String, texto: TextoBilingue, funcion: TextoBilingue?) -> Unit =
        { _, _, _, _ -> },
)

/** tipo + texto en el idioma que se aprende: identifica un item dentro de una ficha, sin depender de la base de datos. */
fun claveGuardado(tipo: TipoGuardado, textoOrigen: String): String = "$tipo|$textoOrigen"

/**
 * Punto de entrada de la pantalla unica: la Fase 4 agrega navegacion y
 * ajustes, pero el "no hay nada para esta celda de la matriz" es el caso
 * normal desde ahora (ver CLAUDE.md) y no puede dejar la pantalla en blanco
 * ni romper la app.
 */
@Composable
fun PantallaSemana(
    resultado: ResultadoSemana,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    modifier: Modifier = Modifier,
    fecha: LocalDate = LocalDate.now(),
    estadoGuardados: EstadoGuardados = EstadoGuardados(),
    promptsVoz: Map<String, String> = emptyMap(),
    onPlanilla: ((ContenidoSemanal) -> Unit)? = null,
    topicNombres: NombresI18n = NombresI18n(),
) {
    when (resultado) {
        is ResultadoSemana.Encontrado ->
            ContenidoSemanalScreen(resultado.contenido, idiomaBase, modifier, idiomaInterfaz, fecha, estadoGuardados, promptsVoz, onPlanilla, topicNombres)
        is ResultadoSemana.SinContenido -> SinContenidoMensaje(resultado, idiomaInterfaz, modifier)
    }
}

/** AJUSTES-FASE-9.md, bloque B: estos tres mensajes son parte del chrome, traducidos a los seis idiomas de interfaz. */
@Composable
private fun SinContenidoMensaje(sinContenido: ResultadoSemana.SinContenido, idiomaInterfaz: Idioma, modifier: Modifier = Modifier) {
    val mensaje = when (sinContenido.razon) {
        RazonSinContenido.ANIO_SIN_CALENDARIO ->
            mensajeSinCalendario(idiomaInterfaz, sinContenido.semanaIso.anio)
        RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO ->
            mensajeSinContenidoNivel(idiomaInterfaz, sinContenido.idioma, sinContenido.nivel)
        RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION ->
            mensajeSinContenidoSemana(idiomaInterfaz, sinContenido.semanaIso.anio)
    }
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(mensajeSinContenidoTitulo(idiomaInterfaz), style = MaterialTheme.typography.headlineSmall)
        Text(mensaje, style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * Cuadro de referencia, vocabulario y Redemittel van en SeccionPlegable
 * (colapsados por defecto): una ficha completa ronda las 1.000 palabras y
 * desplegada entera no se lee. El resto de las secciones son cortas y van
 * siempre visibles.
 */
@Composable
fun ContenidoSemanalScreen(
    contenido: ContenidoSemanal,
    idiomaBase: Idioma,
    modifier: Modifier = Modifier,
    idiomaInterfaz: Idioma = idiomaBase,
    // Parametro de prueba, como fechaInicial en LenguAppApp: la produccion
    // nunca lo pasa, asi que siempre arranca en la fecha real de hoy.
    fecha: LocalDate = LocalDate.now(),
    estadoGuardados: EstadoGuardados = EstadoGuardados(),
    // Plantillas de prompt de voz por "idioma-nivel" (ver presentacion/PromptVoz.kt).
    promptsVoz: Map<String, String> = emptyMap(),
    // Planilla del profesor (PDF local): si es null el boton no se muestra. Quien llama
    // (MainActivity.kt) es el que sabe de archivos e Intents; esta pantalla solo avisa.
    onPlanilla: ((ContenidoSemanal) -> Unit)? = null,
    // Nombres de topic en 6 idiomas: el reto del fin de semana muestra el tema en el idioma de app.
    topicNombres: NombresI18n = NombresI18n(),
) {
    when (contenido) {
        is Ficha -> FichaContenido(contenido, idiomaBase, idiomaInterfaz, fecha, estadoGuardados, promptsVoz, onPlanilla, topicNombres, modifier)
        is SemanaEspecial -> SemanaEspecialContenido(contenido, idiomaBase, idiomaInterfaz, onPlanilla, modifier)
    }
}

// AJUSTES-FASE-8.md, B.4: contenido seleccionable y copiable para traducir a
// mano un pasaje. SelectionContainer envuelve toda la ficha; el boton de
// copiar el prompt se excluye con DisableSelection para que el gesto de
// seleccionar texto no le gane al click ni lo deje capturado.
@Composable
private fun FichaContenido(
    ficha: Ficha,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    fecha: LocalDate,
    estadoGuardados: EstadoGuardados,
    promptsVoz: Map<String, String>,
    onPlanilla: ((ContenidoSemanal) -> Unit)?,
    topicNombres: NombresI18n,
    modifier: Modifier = Modifier,
) {
    // Feature 1 (switch de traduccion en vivo): estado local, nunca
    // persistido -- "modo revista", igual criterio que regla dura #13. Se
    // resetea al cambiar de ficha (`remember(ficha.id)`) y siempre arranca
    // traducido (idiomaBase), nunca con el ultimo estado de la ficha anterior.
    //
    // El switch solo tiene sentido en una ficha bilingue (A2/B1) cuando el
    // idioma de app NO es el que se aprende: si coinciden no hay nada que
    // traducir. Sin switch (B2+, o idiomas iguales) se muestra siempre el
    // idioma que se aprende.
    var traducir by remember(ficha.id) { mutableStateOf(true) }
    val puedeTraducir = ficha.bilingue && idiomaBase != ficha.idioma
    val traduccionActiva = puedeTraducir && traducir
    val idiomaMostrado = idiomaMostradoDeFicha(ficha.bilingue, ficha.idioma, idiomaBase, traducir)
    val t: (TextoBilingue) -> String = { it.resolver(idiomaMostrado, ficha.idioma) }
    // Cuando se muestra la traduccion, la palabra objetivo (*cita*) queda
    // sin traducir: ademas de la cursiva va entre « » (ver textoConMarcado).
    val m: (String) -> AnnotatedString = { textoConMarcado(it, angulares = traduccionActiva) }
    // Titulos de seccion en el idioma que se esta mostrando (ver EtiquetasSeccion.kt).
    val et: (String) -> String = { etiquetaSeccion(it, idiomaMostrado) }

    // Feature 2 (desafio de fin de semana): mismo criterio, CLAUDE.md regla
    // dura #13 explicita ("arranca en off, nunca se persiste"). Solo se
    // ofrece dentro de la ventana sabado-domingo.
    var modoDesafio by remember(ficha.id) { mutableStateOf(false) }
    val enFinDeSemana = esFinDeSemana(fecha)

    Column(modifier = modifier.fillMaxSize()) {
        if (puedeTraducir || enFinDeSemana) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (puedeTraducir) {
                    SwitchTraduccion(
                        activo = traducir,
                        idiomaInterfaz = idiomaInterfaz,
                        idiomaDestino = idiomaBase,
                        onCambiar = { traducir = it },
                    )
                }
                if (enFinDeSemana) {
                    SwitchDesafioFinde(activo = modoDesafio, idiomaInterfaz = idiomaInterfaz, onCambiar = { modoDesafio = it })
                }
            }
        }

        SelectionContainer {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_SECCIONES),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ficha.variante?.let { EtiquetaVariante(it) }
                    Text(t(ficha.titulo), style = MaterialTheme.typography.headlineSmall)
                    Text(
                        m(t(ficha.subtitulo)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Insignia(ficha.topicId)
                        Insignia(etiquetaChallengeType(ficha.challengeType))
                        Insignia("${ficha.evidencia.escritura}w · ${formatoOralMin(ficha.evidencia.oralMin)}")
                        Insignia("${ficha.evidencia.minutosEstimados} min")
                    }
                }

                if (modoDesafio) {
                    // El reto NO es la mision repetida (REGLAS-PREVENCION P4): no muestra la consigna ni los
                    // requisitos. Es hablar en voz alta: una linea, el tema y la habilidad, las condiciones
                    // (una idea por fila, con icono) y hasta 3 frases de la ficha para usar. Rotulos y
                    // condiciones en el idioma de app; las frases en el que se aprende. El resto de la
                    // ficha se oculta mientras el reto esta activo.
                    RetoFinDeSemana(
                        ficha = ficha,
                        idiomaInterfaz = idiomaInterfaz,
                        habilidad = t(ficha.titulo),
                        tema = topicNombres.nombre(ficha.topicId, idiomaInterfaz),
                    )
                } else {
                    Text(m(t(ficha.descripcion)), style = MaterialTheme.typography.bodyLarge)

                    ficha.cuadroReferencia?.let { cuadro ->
                        SeccionPlegable(titulo = t(cuadro.titulo).ifBlank { et("cuadroReferencia") }) {
                            CuadroReferenciaTabla(cuadro, t, traduccionActiva)
                        }
                    }

                    Seccion(et("ejemplos")) {
                        // El original en el idioma que se aprende se muestra
                        // SIEMPRE; la traduccion es un agregado debajo (otra
                        // tipografia), solo con el switch activo -- nunca
                        // reemplaza al original.
                        ficha.ejemplos.forEach { ejemplo ->
                            val original = ejemplo.texto.resolver(ficha.idioma, ficha.idioma)
                            val traduccion = if (traduccionActiva) {
                                ejemplo.texto.resolver(idiomaBase, ficha.idioma).takeIf { it.isNotBlank() && it != original }
                            } else {
                                null
                            }
                            EjemploFila(original, traduccion)
                        }
                    }

                    Seccion(et("notas")) {
                        ficha.notas.forEach { Vinieta(t(it), traduccionActiva) }
                    }

                    // El contraste ya no depende del switch para APARECER (solo
                    // del idioma de app: la entrada es la de idiomaBase), solo
                    // cambia de idioma -- titulo y contenido. Ver
                    // contrasteParaMostrar(). Las citas ajenas al idioma del texto
                    // ya vienen entre « » escritas a mano (Contrastes), asi que NO
                    // se envuelve nada dinamicamente: un `*würde*` suelto es del
                    // mismo idioma que la linea y no lleva « ».
                    ficha.contraste.contrasteParaMostrar(ficha.idioma, idiomaBase, idiomaMostrado)?.let { texto ->
                        val titulo = if (traduccionActiva) et("contraste") else etiquetaContraste(ficha.idioma, idiomaBase)
                        Seccion(titulo) {
                            Text(textoConMarcado(texto), style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Seccion(et("errores")) {
                        // Los errores contrastivos son los de la lengua base
                        // (idiomaBase), no los del idioma que el switch muestra.
                        ficha.errores.forEach { Vinieta(t(it), traduccionActiva) }
                        // En A2/B1 cada item es bilingue: traduccion con el switch activo,
                        // original apagado (t()); en B2+ es el string plano. Las citas ajenas
                        // ya vienen entre « » a mano, como en el contraste: sin angulares.
                        erroresParaMostrar(emptyList(), ficha.erroresContrastivos, ficha.idioma, idiomaBase, t)
                            .forEach { Vinieta(it) }
                    }

                    SeccionPlegable(titulo = "${et("vocabulario")} (${ficha.vocabulario.size})") {
                        Column(verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_ITEMS)) {
                            // Las glosas siempre en el idioma de app: el switch
                            // cambia la prosa, no la traduccion del vocabulario.
                            ficha.vocabulario.forEach { VocabularioFila(it, idiomaBase, ficha.idioma, estadoGuardados) }
                        }
                    }

                    SeccionPlegable(titulo = "${et("redemittel")} (${ficha.redemittel.size})") {
                        Column(verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_ITEMS)) {
                            ficha.redemittel.forEach {
                                RedemittelFila(it, idiomaBase, idiomaMostrado, ficha.idioma, estadoGuardados)
                            }
                        }
                    }

                    Seccion(et("mision")) {
                        Text(m(t(ficha.mision.consigna)), style = MaterialTheme.typography.bodyLarge)
                        ficha.mision.requisitos.forEach { Vinieta(t(it), traduccionActiva) }
                    }

                    Seccion(et("microtareas")) {
                        ficha.microtareas.forEach {
                            Vinieta("${etiquetaDia(it.dia, idiomaMostrado)} (${it.minutos} min) — ${t(it.texto)}", traduccionActiva)
                        }
                    }

                    Seccion(et("autochequeo")) {
                        ficha.autochequeo.forEach { Vinieta(t(it), traduccionActiva) }
                    }

                    // Siempre en el idioma que se aprende (tabla 2 de REGLAS-PREVENCION), no sigue al switch;
                    // el titulo de la tarjeta si (es una seccion de la ficha).
                    TarjetaPrompt(ficha.promptCorreccion.promptEnIdiomaAprendido(ficha.idioma), et("promptCorreccion"), idiomaInterfaz)

                    // Prompt de voz: siempre en el idioma que se aprende (la IA
                    // tiene que hablarlo), sin switch de traduccion; solo si hay
                    // plantilla para este (idioma, nivel). Titulo y ayuda en el
                    // idioma de interfaz.
                    promptVozPara(ficha, promptsVoz)?.let { promptVoz ->
                        TarjetaPrompt(
                            promptVoz,
                            etiquetaPromptVozTitulo(idiomaInterfaz),
                            idiomaInterfaz,
                            ayuda = etiquetaPromptVozAyuda(idiomaInterfaz),
                        )
                    }
                }

                onPlanilla?.let { BotonPlanilla(etiquetaPlanillaBoton(idiomaInterfaz)) { it(ficha) } }
            }
        }
    }
}

/**
 * Feature 1: switch de traduccion en vivo. Distinto visualmente del de
 * abajo (Switch vs. chip). Toda la fila es clickeable, no solo el Switch:
 * mas area de toque, y el Switch se vuelve puramente visual
 * (`onCheckedChange = null`) para no manejar el toggle dos veces. La
 * etiqueta dice que HACE ("Traducir al espanol"), no que idioma se ve.
 */
@Composable
private fun SwitchTraduccion(activo: Boolean, idiomaInterfaz: Idioma, idiomaDestino: Idioma, onCambiar: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onCambiar(!activo) },
    ) {
        Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(etiquetaTraducirA(idiomaInterfaz, idiomaDestino), style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
        Switch(checked = activo, onCheckedChange = null)
    }
}

/**
 * Un ejemplo: el original en el idioma que se aprende, y debajo -- solo si
 * hay traduccion que mostrar -- la traduccion en una tipografia distinta
 * (mas chica, color secundario, sin vineta propia).
 */
@Composable
private fun EjemploFila(original: String, traduccion: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Vinieta(original)
        if (traduccion != null) {
            Text(
                textoConMarcado(traduccion, angulares = true),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}

/** Feature 2: switch del desafio de fin de semana. Un FilterChip, no un Switch: visualmente distinto a propósito. */
@Composable
private fun SwitchDesafioFinde(activo: Boolean, idiomaInterfaz: Idioma, onCambiar: (Boolean) -> Unit) {
    FilterChip(
        selected = activo,
        onClick = { onCambiar(!activo) },
        label = { Text(etiquetaDesafioFinde(idiomaInterfaz)) },
        leadingIcon = {
            Icon(
                if (activo) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun SemanaEspecialContenido(
    especial: SemanaEspecial,
    idiomaBase: Idioma,
    idiomaInterfaz: Idioma,
    onPlanilla: ((ContenidoSemanal) -> Unit)?,
    modifier: Modifier = Modifier,
) = SelectionContainer {
    // Campos bilingues (A2/B1): idioma base del usuario, y el que se aprende si falta esa clave.
    // Solo las semanas A2/B1 son bilingues; B2+ se muestran en el idioma que se aprende.
    val bilingue = especial.nivel == Nivel.A2 || especial.nivel == Nivel.B1
    val idiomaTexto = if (bilingue) idiomaBase else especial.idioma
    val traducido = idiomaTexto != especial.idioma
    val t: (TextoBilingue) -> String = { it.resolver(idiomaTexto, especial.idioma) }
    val et: (String) -> String = { etiquetaSeccion(it, idiomaTexto) }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_SECCIONES),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(t(especial.titulo), style = MaterialTheme.typography.headlineSmall)
            val minutos = especial.minutosEstimados?.let { " · $it min" } ?: ""
            Text(
                "${etiquetaClase(especial.clase, idiomaTexto)} · ${etiquetaSemana(idiomaTexto)} ${especial.semana}$minutos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(textoConMarcado(t(especial.consigna), angulares = traducido), style = MaterialTheme.typography.bodyLarge)

        especial.requisitos?.takeIf { it.isNotEmpty() }?.let { requisitos ->
            Seccion(et("requisitos")) { requisitos.forEach { Vinieta(t(it), traducido) } }
        }

        especial.microtareas?.takeIf { it.isNotEmpty() }?.let { tareas ->
            Seccion(et("microtareas")) { tareas.forEach { Vinieta(t(it), traducido) } }
        }

        Seccion(et("autochequeo")) {
            especial.autochequeo.forEach { Vinieta(t(it), traducido) }
        }

        especial.promptCorreccion.promptEnIdiomaAprendido(especial.idioma).takeIf { it.isNotBlank() }?.let {
            TarjetaPrompt(it, et("promptCorreccion"), idiomaInterfaz)
        }

        onPlanilla?.let { BotonPlanilla(etiquetaPlanillaBoton(idiomaInterfaz)) { it(especial) } }
    }
}

/** Boton de ancho completo al final de la semana: abre la planilla del profesor (PDF generado en el dispositivo). */
@Composable
private fun BotonPlanilla(texto: String, onClick: () -> Unit) {
    DisableSelection {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(texto) }
    }
}

/**
 * Jerarquia tipografica en vez de lineas divisorias -- AJUSTES-FASE-5.md,
 * B4.3, reforzado en AJUSTES-FASE-8.md, B.2: titleLarge ya distingue titulo
 * de cuerpo por tamaño, pero por defecto tiene el mismo peso (Normal) que
 * bodyMedium/bodyLarge. FontWeight.Bold suma el segundo eje de contraste
 * que pidio B.2, antes de considerar un separador.
 */
/** Iconos de las condiciones del reto, en el orden de las filas (Material Icons existentes). */
private fun iconosReto(nivel: Nivel): List<ImageVector> =
    if (nivel == Nivel.A2 || nivel == Nivel.B1) {
        listOf(Icons.Filled.Mic, Icons.Filled.Timer, Icons.Filled.Group, Icons.Filled.Replay)
    } else {
        listOf(Icons.Filled.Mic, Icons.Filled.Timer, Icons.Filled.SwapHoriz, Icons.Filled.Replay)
    }

@Composable
private fun RetoFinDeSemana(ficha: Ficha, idiomaInterfaz: Idioma, habilidad: String, tema: String) {
    Column(verticalArrangement = Arrangement.spacedBy(ESPACIO_ENTRE_SECCIONES)) {
        Seccion(etiquetaDesafioFinde(idiomaInterfaz)) {
            Text(textoRetoLinea(idiomaInterfaz), style = MaterialTheme.typography.bodyLarge)
            FilaDato(etiquetaBibliotecaTema(idiomaInterfaz), tema)
            FilaDato(etiquetaRetoHabilidad(idiomaInterfaz), habilidad)
        }
        Seccion(etiquetaRetoCondiciones(idiomaInterfaz)) {
            val iconos = iconosReto(ficha.nivel)
            reglasReto(idiomaInterfaz, ficha.nivel, oralMinRedondeado(ficha.evidencia.oralMin)).forEachIndexed { i, regla ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(iconos.getOrElse(i) { iconos.last() }, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(regla, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        // Las mismas expresiones que el prompt de voz (expresionesDeLaFicha), en el idioma que se aprende.
        val frases = expresionesDeLaFicha(ficha, MAX_FRASES_RETO)
        if (frases.isNotEmpty()) {
            Seccion(etiquetaRetoFrasesTitulo(idiomaInterfaz)) {
                Text(textoRetoFrasesAyuda(idiomaInterfaz), style = MaterialTheme.typography.bodyMedium)
                frases.forEach { Vinieta(it) }
            }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$etiqueta:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Text(valor, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        contenido()
    }
}

@Composable
private fun Vinieta(texto: String, angulares: Boolean = false) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(textoConMarcado(texto, angulares), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(end = 4.dp))
    }
}

/** Insignia chica y neutra: metadatos de cabecera, "núcleo", variante regional, bajo nivel a propósito. */
@Composable
private fun Insignia(
    texto: String,
    contenedor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contenido: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Surface(color = contenedor, contentColor = contenido, shape = RoundedCornerShape(6.dp)) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

/**
 * Feature 3 (Guardados): estrella junto a cada item de vocabulario/Redemittel.
 * Toggle sin confirmacion -- tocar de nuevo saca el item, no hay dialogo.
 */
@Composable
private fun BotonGuardar(guardado: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(
            if (guardado) Icons.Filled.Star else Icons.Outlined.StarBorder,
            contentDescription = null,
            tint = if (guardado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EtiquetaVariante(variante: String) {
    Insignia(
        "Variante regional: $variante",
        contenedor = MaterialTheme.colorScheme.tertiaryContainer,
        contenido = MaterialTheme.colorScheme.onTertiaryContainer,
    )
}

@Composable
private fun VocabularioFila(item: VocabularioItem, idiomaBase: Idioma, idiomaAprendido: Idioma, estadoGuardados: EstadoGuardados) {
    // Palabra y traduccion van pegadas (son una unidad); lo demas del item
    // (reccion, nota, insignias) tiene un poco mas de aire, y entre items la
    // separacion es mayor todavia (ESPACIO_ENTRE_ITEMS, en quien llama).
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // AJUSTES-FASE-8.md, B.5: sin la etiqueta "núcleo" (nombre de
            // campo mostrado en crudo, y siempre en español -- CLAUDE.md,
            // regla dura #9). El dato se sigue usando: el peso tipografico
            // ya distinguia los items nucleo de los diez secundarios
            // (AJUSTES-FASE-5.md, B4.4), asi que alcanza con eso solo.
            Text(
                item.item,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.prioridad == Prioridad.NUCLEO) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
            )
            BotonGuardar(
                guardado = claveGuardado(TipoGuardado.VOCABULARIO, item.item) in estadoGuardados.guardados,
                onClick = {
                    estadoGuardados.onAlternar(TipoGuardado.VOCABULARIO, item.item, item.aTextoBilingue(idiomaAprendido), null)
                },
            )
        }
        Text(
            item.traduccionParaMostrar(idiomaBase),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        }
        item.reccion?.takeIf { it.isNotBlank() }?.let {
            Text(
                "rección: $it",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item.nota?.let {
            Text(
                textoConMarcado(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (item.variante != null || item.bajoNivelJustificado) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.variante?.let { Insignia(it) }
                if (item.bajoNivelJustificado) Insignia("bajo nivel, a propósito")
            }
        }
    }
}

@Composable
private fun RedemittelFila(
    item: RedemittelItem,
    idiomaBase: Idioma,
    idiomaMostrado: Idioma,
    idiomaAprendido: Idioma,
    estadoGuardados: EstadoGuardados,
) {
    // Orden: expresion, su traduccion (pegadas), y la funcion debajo de la
    // traduccion -- antes la funcion quedaba en el medio, entre las dos.
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.expresion,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                BotonGuardar(
                    guardado = claveGuardado(TipoGuardado.EXPRESION, item.expresion) in estadoGuardados.guardados,
                    onClick = {
                        estadoGuardados.onAlternar(
                            TipoGuardado.EXPRESION,
                            item.expresion,
                            item.aTextoBilingue(idiomaAprendido),
                            item.funcion,
                        )
                    },
                )
            }
            // La traduccion de la expresion es la del idioma de app (idiomaBase),
            // siempre: el switch cambia la prosa de la ficha, no las glosas.
            val texto = when (val traduccion = item.traduccionParaMostrar(idiomaBase)) {
                is TraduccionRedemittel.Disponible -> traduccion.texto
                TraduccionRedemittel.SinEquivalenciaDirecta -> "Sin equivalencia directa: se aprende por situación."
                TraduccionRedemittel.SinTraducirTodavia -> "—"
            }
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            textoConMarcado(item.funcion.resolver(idiomaMostrado, idiomaAprendido), angulares = idiomaMostrado != idiomaAprendido),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Cada fila como una mini-tarjeta con "columna: valor" en vez de una grilla
 * rigida: en pantalla estrecha y con fuente grande, una grilla de 4-5
 * columnas con oraciones largas se corta o se sale. Esto envuelve el texto
 * en vez de truncarlo.
 */
@Composable
private fun CuadroReferenciaTabla(cuadro: CuadroReferencia, t: (TextoBilingue) -> String, angulares: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        cuadro.filas.forEach { fila ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    fila.forEachIndexed { i, celdaBilingue ->
                        val celda = t(celdaBilingue)
                        if (celda.isNotBlank()) {
                            val etiqueta = cuadro.columnas.getOrNull(i)?.let(t)
                            Text(
                                textoConMarcado(if (etiqueta != null) "$etiqueta: $celda" else celda, angulares),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
        cuadro.notaPie?.let(t)?.takeIf { it.isNotBlank() }?.let {
            Text(
                textoConMarcado(it, angulares),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** El unico botón que toca el sistema: copia el prompt de corrección al portapapeles. */
@Composable
private fun TarjetaPrompt(prompt: String, titulo: String, idiomaInterfaz: Idioma, angulares: Boolean = false, ayuda: String? = null) {
    val portapapeles = LocalClipboardManager.current
    var copiado by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            ayuda?.let { Text(textoConMarcado(it), style = MaterialTheme.typography.bodySmall) }
            Text(textoConMarcado(prompt, angulares), style = MaterialTheme.typography.bodyMedium)
            // Es la unica accion de la pantalla: boton de ancho completo,
            // no un boton mas perdido entre el resto -- AJUSTES-FASE-5.md, B4.5.
            // DisableSelection (AJUSTES-FASE-8.md, B.4): el resto de la ficha
            // es seleccionable, pero este boton no debe quedar capturado por
            // el gesto de seleccionar texto.
            DisableSelection {
                Button(
                    onClick = {
                        portapapeles.setText(AnnotatedString(prompt))
                        copiado = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (copiado) etiquetaCopiado(idiomaInterfaz) else etiquetaCopiarPortapapeles(idiomaInterfaz))
                }
            }
        }
    }
}

private fun etiquetaClase(clase: Clase, idioma: Idioma): String = when (clase) {
    Clase.REVIEW -> etiquetaSemanaRepaso(idioma)
    Clase.SURVIVAL -> etiquetaSemanaSurvival(idioma)
}

private fun etiquetaChallengeType(tipo: ChallengeType): String =
    tipo.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }

private fun formatoOralMin(oralMin: Double): String {
    val redondeado = if (oralMin == oralMin.toInt().toDouble()) oralMin.toInt().toString() else oralMin.toString()
    return "${redondeado}min oral"
}
