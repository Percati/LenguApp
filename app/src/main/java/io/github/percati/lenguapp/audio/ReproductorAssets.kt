package io.github.percati.lenguapp.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log

/**
 * Reproduce los `.ogg` DESDE los assets, sin copiarlos a disco y sin permisos (regla dura 1: ni INTERNET ni nada).
 * `MediaPlayer` con `AssetFileDescriptor`: viene con Android, no suma nada al APK (ExoPlayer sumaria varios MB).
 *
 * Canal de medios (USAGE_MEDIA, voz): sigue el volumen de medios del telefono, y con el volumen en cero no hay
 * sonido; no se fuerza el canal de llamada ni el de alarma. Pide el foco de audio "con duck" mientras suena (la
 * musica baja, no se corta) y lo devuelve al terminar.
 */
class ReproductorAssets(private val contexto: Context) : ReproductorAudio {

    private var jugador: MediaPlayer? = null
    private var peticionFoco: AudioFocusRequest? = null

    private val atributos = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
        .build()

    override fun reproducir(rutaAsset: String, velocidad: Float, alTerminar: (Boolean) -> Unit): Boolean {
        soltarJugador()
        return try {
            val p = MediaPlayer()
            jugador = p
            contexto.assets.openFd(rutaAsset).use { fd -> p.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            p.setAudioAttributes(atributos)
            // La velocidad se aplica AQUI, ya preparado y antes de start(): setPlaybackParams lanza IllegalStateException si el
            // reproductor no esta en un estado valido, y antes de preparar no cambia nada. (Ojo si algun dia hay pausa:
            // en varias versiones, setPlaybackParams sobre un reproductor pausado lo hace arrancar.)
            p.setOnPreparedListener {
                if (jugador === it) {
                    aplicarVelocidadSegura(velocidad, { params -> it.playbackParams = params })
                    pedirFoco()
                    it.start()
                }
            }
            p.setOnCompletionListener {
                if (jugador === it) { soltarJugador(); alTerminar(true) }
            }
            p.setOnErrorListener { m, what, extra ->
                Log.d("LenguApp", "Error de audio ($what/$extra) en $rutaAsset")
                if (jugador === m) { soltarJugador(); alTerminar(false) }
                true
            }
            p.prepareAsync()
            true
        } catch (e: Exception) {
            Log.d("LenguApp", "No se pudo abrir el audio $rutaAsset: ${e.message}")
            soltarJugador()
            false
        }
    }

    override fun detener() = soltarJugador()

    override fun liberar() = soltarJugador()

    private fun soltarJugador() {
        val p = jugador
        jugador = null
        if (p != null) {
            p.setOnPreparedListener(null)
            p.setOnCompletionListener(null)
            p.setOnErrorListener(null)
            runCatching { if (p.isPlaying) p.stop() }
            runCatching { p.release() }
        }
        devolverFoco()
    }

    private fun pedirFoco() {
        val gestor = contexto.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        val peticion = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(atributos)
            .build()
        peticionFoco = peticion
        runCatching { gestor.requestAudioFocus(peticion) }
    }

    private fun devolverFoco() {
        val peticion = peticionFoco ?: return
        peticionFoco = null
        val gestor = contexto.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
        runCatching { gestor.abandonAudioFocusRequest(peticion) }
    }
}

/**
 * Aplica `velocidad` con `PlaybackParams` (solo cambia la velocidad: el tono queda en 1,0, la voz no suena grave). Si no
 * es 1,0 y el dispositivo la rechaza (IllegalArgumentException, IllegalStateException, lo que sea), no se rompe ni se calla:
 * el clip suena a velocidad normal y queda una linea en el log de depuracion. Devuelve la velocidad que quedo efectiva.
 */
internal fun aplicarVelocidadSegura(velocidad: Float, aplicar: (PlaybackParams) -> Unit): Float {
    if (velocidad == VELOCIDAD_NORMAL) return VELOCIDAD_NORMAL
    return try {
        aplicar(PlaybackParams().setSpeed(velocidad).setPitch(1.0f))
        velocidad
    } catch (e: Exception) {
        runCatching { Log.d("LenguApp", "El dispositivo rechazo la velocidad $velocidad: ${e.message}; suena a velocidad normal") }
        VELOCIDAD_NORMAL
    }
}
