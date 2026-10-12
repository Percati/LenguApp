package io.github.percati.lenguapp.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
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

    override fun reproducir(rutaAsset: String, alTerminar: (Boolean) -> Unit): Boolean {
        soltarJugador()
        return try {
            val p = MediaPlayer()
            jugador = p
            contexto.assets.openFd(rutaAsset).use { fd -> p.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            p.setAudioAttributes(atributos)
            p.setOnPreparedListener { if (jugador === it) { pedirFoco(); it.start() } }
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
