@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package io.github.percati.lenguapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import io.github.percati.lenguapp.modelo.Tour

/**
 * El tutorial con foco, por partes (Ronda Q; guion en docs/tutorial-guion.md). Cada seccion lanza su propio tour la primera vez
 * que se entra; «Saltar» saltea todos los que faltan; todo se puede repetir. Nada de esto es progreso: es un ajuste (regla dura 4).
 *
 * Mecanismo: cada paso nombra un ANCLA, un `testTag` (no coordenadas), y el foco se dibuja sobre los limites reales de ese nodo
 * ([anclaTour] los registra). Si un paso no encuentra su ancla, SE SALTEA y el tour sigue: nunca se oscurece la pantalla sin
 * nada senalado, nunca se espera sin fin, nunca se rompe. Un test recorre todos los pasos de todos los tours y falla si alguna
 * ancla no existe en su pantalla, para que el tutorial no se pudra en silencio cuando cambie la interfaz.
 */
/**
 * Un paso: `ancla` es el testTag de lo que se senala (null = un paso sin foco, una tarjeta al centro); `titulo` y `cuerpo` son
 * claves de TextosInterfaz (6 idiomas); `cuerpoExtra` es un segundo parrafo opcional (tambien una clave).
 */
data class PasoTour(
    val ancla: String?,
    val titulo: ClaveTexto,
    val cuerpo: ClaveTexto,
    val cuerpoExtra: ClaveTexto? = null,
)

/** Cuanto se espera, como maximo, a que la pantalla componga el ancla de un paso antes de saltearlo. */
const val ESPERA_ANCLA_MS = 600L

/** Donde esta cada ancla en pantalla (en coordenadas de la raiz), mientras esta componida. */
class RegistroAnclas {
    private val rects = mutableStateMapOf<String, Rect>()
    private val requesters = HashMap<String, BringIntoViewRequester>()

    fun rectDe(id: String): Rect? = rects[id]

    /** Las anclas que hoy estan en pantalla (para los tests). */
    fun ids(): Set<String> = rects.keys.toSet()

    internal fun registrar(id: String, rect: Rect, requester: BringIntoViewRequester) {
        rects[id] = rect
        requesters[id] = requester
    }

    internal fun quitar(id: String, requester: BringIntoViewRequester) {
        if (requesters[id] === requester) {
            requesters.remove(id)
            rects.remove(id)
        }
    }

    /** Lleva el ancla a la vista si esta dentro de un scroll (Ajustes, la ficha, listas de la Biblioteca). */
    @OptIn(ExperimentalFoundationApi::class)
    suspend fun traerALaVista(id: String) {
        runCatching { requesters[id]?.bringIntoView() }
    }
}

/**
 * Quien sabe que tour esta en marcha y en que paso. El estado persistente (que tours se vieron, si se salto todo) lo pone
 * `LenguAppApp` desde los ajustes; el gestor solo lo consulta y avisa cuando algo cambia.
 */
class GestorTours(val catalogo: Map<Tour, List<PasoTour>> = emptyMap()) {
    val registro = RegistroAnclas()

    var activo by mutableStateOf<Tour?>(null)
        private set
    var indice by mutableIntStateOf(0)
        private set

    // Estado persistente, inyectado desde afuera (ajustes): no es estado de Compose, se lee al lanzar.
    var vistos: Set<Tour> = emptySet()
    var salteados: Boolean = false
    var alVerTour: (Tour) -> Unit = {}
    var alSaltarTodos: () -> Unit = {}

    fun pasos(tour: Tour): List<PasoTour> = catalogo[tour].orEmpty()

    val pasoActual: PasoTour? get() = activo?.let { pasos(it).getOrNull(indice) }

    /** Un tour se ofrece una sola vez, si no se salteo todo, si no hay otro en marcha y si tiene pasos. */
    fun puedeLanzar(tour: Tour): Boolean = activo == null && !salteados && tour !in vistos && pasos(tour).isNotEmpty()

    fun lanzar(tour: Tour): Boolean {
        if (!puedeLanzar(tour)) return false
        indice = 0
        activo = tour
        return true
    }

    fun siguiente() {
        val tour = activo ?: return
        if (indice + 1 >= pasos(tour).size) terminar() else indice++
    }

    /** Termina el tour (al llegar al final o al abandonarlo con atras): cuenta como visto. */
    fun terminar() {
        val tour = activo ?: return
        activo = null
        indice = 0
        alVerTour(tour)
    }

    /** «Saltar»: el tour en marcha y todos los que faltan quedan como vistos. */
    fun saltarTodo() {
        if (activo == null) return
        activo = null
        indice = 0
        alSaltarTodos()
    }
}

/** El gestor de la app: lo provee `LenguAppApp`. Por omision no hay nada que lanzar (pantallas sueltas, tests). */
val LocalGestorTours = staticCompositionLocalOf { GestorTours() }

/**
 * Marca este nodo como ancla de un paso del tutorial: le pone `testTag(id)` y registra sus limites reales (se actualizan al
 * desplazarse) y la forma de traerlo a la vista. Se usa el id como unico testTag del nodo.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.anclaTour(id: String): Modifier = composed {
    val registro = LocalGestorTours.current.registro
    val requester = remember { BringIntoViewRequester() }
    DisposableEffect(registro, id) { onDispose { registro.quitar(id, requester) } }
    Modifier
        .testTag(id)
        .bringIntoViewRequester(requester)
        .onGloballyPositioned { coordenadas ->
            if (coordenadas.isAttached) registro.registrar(id, coordenadas.boundsInRoot(), requester)
        }
}

/**
 * Pide el tour de una seccion al ENTRAR a ella (Biblioteca, Guardados, Ajustes): se lanza solo si no se vio, no se salto todo y no hay
 * otro en marcha. Sin gestor con pasos (pantallas sueltas, tests) no hace nada.
 */
@androidx.compose.runtime.Composable
fun LanzarTourAlEntrar(tour: Tour) {
    val gestor = LocalGestorTours.current
    androidx.compose.runtime.LaunchedEffect(gestor, tour) { gestor.lanzar(tour) }
}
