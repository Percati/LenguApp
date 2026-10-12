package io.github.percati.lenguapp.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.percati.lenguapp.modelo.Idioma

/** Velocidad de reproduccion normal (100 %). */
const val VELOCIDAD_NORMAL = 1.0f

/** Lo que hace falta para sonar un clip. `ReproductorAssets` es la implementacion real; los tests usan una falsa. */
interface ReproductorAudio {
    /**
     * Empieza a sonar `rutaAsset` (cortando lo que sonara) a `velocidad` (1,0 = normal; el tono se conserva). Si el
     * dispositivo rechaza esa velocidad, suena a velocidad normal: mejor un clip al 100 % que el silencio.
     * Devuelve false si el clip no se pudo abrir. `alTerminar(ok)` se llama una vez cuando termina (ok = true) o falla durante la reproduccion (ok = false).
     */
    fun reproducir(rutaAsset: String, velocidad: Float, alTerminar: (Boolean) -> Unit): Boolean

    /** Corta lo que suene; no hace nada si no suena nada. */
    fun detener()

    /** Suelta los recursos. Despues de esto el reproductor no se vuelve a usar. */
    fun liberar()
}

/**
 * Lo que ven las pantallas: preguntar si un texto tiene audio, tocarlo y saber cual suena. Un solo audio a la vez
 * (reproducir otro corta el anterior). No guarda nada de lo escuchado (regla dura 4): `sonando` es solo el estado de
 * este instante y vive en memoria.
 */
open class ControlAudio(
    private val indice: IndiceAudio = IndiceAudio.VACIO,
    private val reproductor: ReproductorAudio? = null,
) {
    /** Ruta del clip que suena ahora, o null. Estado de Compose: los botones se redibujan solos. */
    var sonando: String? by mutableStateOf(null)
        private set

    /** Ruta del clip de ese texto, o null si no tiene (y entonces no hay boton). */
    open fun ruta(idioma: Idioma, clase: ClaseAudio, texto: String): String? =
        if (reproductor == null) null else indice.ruta(idioma, clase, texto)

    /**
     * Tocar el boton: si ese clip suena, lo corta; si no, lo empieza (cortando el que sonara). `velocidad` es la vigente
     * al tocar (viene del ajuste de Ajustes); el control no la guarda.
     */
    open fun alternar(ruta: String, velocidad: Float = VELOCIDAD_NORMAL) {
        val r = reproductor ?: return
        if (sonando == ruta) {
            detener()
            return
        }
        sonando = ruta
        val abierto = r.reproducir(ruta, velocidad) { _ -> if (sonando == ruta) sonando = null }
        if (!abierto) {
            // Archivo que falta o no se puede abrir: ni crash ni cartel, el boton queda como estaba.
            runCatching { android.util.Log.d("LenguApp", "Audio no disponible: $ruta") } // en un test de JVM puro Log no existe
            if (sonando == ruta) sonando = null
        }
    }

    open fun detener() {
        reproductor?.detener()
        sonando = null
    }

    open fun liberar() {
        reproductor?.liberar()
        sonando = null
    }
}

/** El audio de la app: lo provee `LenguAppApp`. Por omision no hay (ninguna pantalla dibuja un boton de audio). */
val LocalControlAudio = staticCompositionLocalOf<ControlAudio> { SinAudio }

/** Sin audio: ningun texto tiene clip, asi que ninguna pantalla dibuja el boton. Es el valor por omision. */
object SinAudio : ControlAudio()
