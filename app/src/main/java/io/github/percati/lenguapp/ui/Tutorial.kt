@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package io.github.percati.lenguapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.layout.positionInRoot
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

    /**
     * Lleva el ancla a la vista si esta dentro de un scroll (Ajustes, la ficha, listas de la Biblioteca). Pide el rectangulo del ancla
     * INFLADO `margenPx` arriba y abajo ([rectanguloConMargen]), no el nodo pelado: el contenedor desplaza de mas y el ancla queda
     * despegada del borde. Si ya esta entera a la vista y a esa distancia de los bordes, el contenedor no desplaza nada. Si el ancla
     * es lo primero o lo ultimo del contenido, el margen no se puede cumplir y el contenedor hace lo que puede (no se inventa relleno).
     * El agujero del foco no cambia: esto es desplazamiento, no dibujo. `pedir` existe para que un test vea el rectangulo pedido.
     */
    suspend fun traerALaVista(
        id: String,
        margenPx: Float = 0f,
        pedir: suspend (BringIntoViewRequester, Rect) -> Unit = { requester, rect -> requester.bringIntoView(rect) },
    ) {
        val requester = requesters[id] ?: return
        val rect = rects[id] ?: return
        runCatching { pedir(requester, rectanguloConMargen(rect.width, rect.height, margenPx)) }
    }
}

/** Margen, en dp, con el que se trae un ancla a la vista (arriba y abajo). */
const val MARGEN_AL_TRAER_DP = 32

/** El rectangulo del ancla, en coordenadas del propio nodo, inflado `margen` arriba y abajo. */
fun rectanguloConMargen(ancho: Float, alto: Float, margen: Float): Rect = Rect(0f, -margen, ancho, alto + margen)

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

    // Los indices de los pasos MOSTRADOS antes del actual: «Atras» vuelve al paso mostrado anterior, no al indice anterior (un paso
    // salteado por falta de ancla se vuelve a saltear al retroceder, porque nunca entro en la pila).
    private val pila = mutableStateListOf<Int>()

    /** Se puede volver un paso: hay un paso mostrado antes del actual (en el primero, «Atras» no se ofrece). */
    val puedeVolver: Boolean get() = activo != null && pila.isNotEmpty()

    // Los pasos que se saltearon por falta de ancla (por indice): no cuentan en «Paso n de total».
    private val omitidos = mutableStateListOf<Int>()

    /** El numero de este paso entre los efectivamente mostrados (1, 2, 3… sin huecos); al retroceder baja. */
    val posicion: Int get() = pila.size + 1

    /**
     * Cuantos pasos tiene el tour contando solo los que se van a mostrar: el catalogo menos los ya salteados y los que se
     * ve que se van a saltear (su ancla no esta en pantalla ahora). En el caso normal es el tamano del catalogo, igual que antes.
     */
    val totalEfectivo: Int
        get() {
            val tour = activo ?: return 0
            val pasos = pasos(tour)
            val sinAncla = pasos.indices.count { i -> i in omitidos || (i > indice && !seMostrara(pasos[i])) }
            return pasos.size - sinAncla
        }

    /** Es el ultimo paso que se mostrara: despues no queda ninguno cuya ancla este en pantalla. */
    val esUltimoPaso: Boolean
        get() {
            val tour = activo ?: return true
            val pasos = pasos(tour)
            return (indice + 1 until pasos.size).none { i -> i !in omitidos && seMostrara(pasos[i]) }
        }

    private fun seMostrara(paso: PasoTour): Boolean = paso.ancla == null || registro.rectDe(paso.ancla) != null

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
        pila.clear()
        omitidos.clear()
        activo = tour
        return true
    }

    /** «Siguiente»: el paso actual queda en la pila de los mostrados y se pasa al que sigue (o se termina). */
    fun siguiente() {
        val tour = activo ?: return
        if (indice + 1 >= pasos(tour).size) {
            terminar()
        } else {
            pila.add(indice)
            indice++
        }
    }

    /** El ancla del paso actual no aparecio: se pasa al que sigue SIN apilarlo (al retroceder tampoco se vuelve a mostrar). */
    fun saltearSinAncla() {
        val tour = activo ?: return
        if (indice !in omitidos) omitidos.add(indice)
        if (indice + 1 >= pasos(tour).size) terminar() else indice++
    }

    /** «Atras»: vuelve al paso mostrado anterior. Sin paso anterior (el primero) no hace nada. */
    fun atras() {
        if (activo == null || pila.isEmpty()) return
        indice = pila.removeAt(pila.lastIndex)
    }

    /** Termina el tour (al llegar al final o al abandonarlo con atras): cuenta como visto. */
    fun terminar() {
        val tour = activo ?: return
        activo = null
        indice = 0
        pila.clear()
        omitidos.clear()
        alVerTour(tour)
    }

    /** «Saltar»: el tour en marcha y todos los que faltan quedan como vistos. */
    fun saltarTodo() {
        if (activo == null) return
        activo = null
        indice = 0
        pila.clear()
        omitidos.clear()
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
            // Posicion y tamano SIN recortar: boundsInRoot() recorta contra los ancestros y devuelve un rectangulo vacio para un nodo que
            // todavia esta fuera de la pantalla dentro de un scroll, justo el caso en que hay que traerlo a la vista.
            if (coordenadas.isAttached) registro.registrar(id, Rect(coordenadas.positionInRoot(), coordenadas.size.toSize()), requester)
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
