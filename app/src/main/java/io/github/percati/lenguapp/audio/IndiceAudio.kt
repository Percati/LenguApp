package io.github.percati.lenguapp.audio

import android.content.Context
import io.github.percati.lenguapp.datos.jsonContenido
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Indice del audio (Ronda M): que archivo `.ogg` de `assets/audio/<IDIOMA>/` corresponde a cada texto del contenido.
 * Lo genera `proyecto/tools/indice_audio.py` a partir de `audio-estado.json`; la forma es
 * `{idioma: {clase: {texto exacto: archivo}}}` con las tres clases de abajo. La clave es el TEXTO EXACTO del
 * contenido en el idioma que se aprende (el `item` del vocabulario, la `expresion` del redemittel, el `texto` del ejemplo).
 * Si un texto no esta en el indice no hay audio y no se dibuja ningun boton: nunca un boton que no suena.
 */
enum class ClaseAudio(val clave: String) {
    VOCABULARIO("Vocabulario"),
    EXPRESION("Expresión"),
    EJEMPLO("Ejemplo"),
}

const val RUTA_INDICE_AUDIO = "audio/indice.json"

class IndiceAudio private constructor(private val porIdioma: Map<Idioma, Map<ClaseAudio, Map<String, String>>>) {

    /** Ruta del clip dentro de los assets (`audio/DE/xxx.ogg`), o null si ese texto no tiene audio. */
    fun ruta(idioma: Idioma, clase: ClaseAudio, texto: String): String? =
        porIdioma[idioma]?.get(clase)?.get(texto)?.let { "audio/${idioma.name}/$it" }

    /** Cuantos textos tienen audio (para tests e informes). */
    val total: Int get() = porIdioma.values.sumOf { clases -> clases.values.sumOf { it.size } }

    fun idiomasConAudio(): Set<Idioma> = porIdioma.filterValues { it.values.any { m -> m.isNotEmpty() } }.keys

    companion object {
        val VACIO = IndiceAudio(emptyMap())

        /**
         * Parser estricto, como el resto del contenido: un idioma o una clase desconocidos son un error, no se ignoran.
         * `incluirIngles = false` deja el ingles fuera (interruptor de compilacion, ver `BuildConfig.AUDIO_INGLES`).
         */
        fun parsear(json: String, incluirIngles: Boolean = true): IndiceAudio {
            val crudo = jsonContenido.decodeFromString(
                MapSerializer(String.serializer(), MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer()))),
                json,
            )
            val resultado = LinkedHashMap<Idioma, Map<ClaseAudio, Map<String, String>>>()
            for ((claveIdioma, clases) in crudo) {
                val idioma = Idioma.entries.firstOrNull { it.name.equals(claveIdioma, ignoreCase = true) }
                    ?: throw IllegalArgumentException("indice de audio: idioma desconocido «$claveIdioma»")
                val porClase = LinkedHashMap<ClaseAudio, Map<String, String>>()
                for ((claveClase, textos) in clases) {
                    val clase = ClaseAudio.entries.firstOrNull { it.clave == claveClase }
                        ?: throw IllegalArgumentException("indice de audio: clase desconocida «$claveClase»")
                    porClase[clase] = textos
                }
                if (idioma == Idioma.EN && !incluirIngles) continue
                resultado[idioma] = porClase
            }
            return IndiceAudio(resultado)
        }
    }
}

/** Lee `assets/audio/indice.json`. Sin indice (o ilegible) no hay audio y la app sigue igual. */
fun cargarIndiceAudioDesdeAssets(context: Context, incluirIngles: Boolean): IndiceAudio =
    runCatching {
        context.assets.open(RUTA_INDICE_AUDIO).bufferedReader(Charsets.UTF_8).use { IndiceAudio.parsear(it.readText(), incluirIngles) }
    }.getOrElse {
        android.util.Log.d("LenguApp", "Sin indice de audio: ${it.message}")
        IndiceAudio.VACIO
    }
