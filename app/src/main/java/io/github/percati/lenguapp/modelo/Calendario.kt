package io.github.percati.lenguapp.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Una fila del calendario precalculado (generar_calendario.py). El calendario
 * no se genera en el dispositivo, solo se embebe y se lee: ver
 * proyecto/calendario-2026-S37-S53.md, seccion A.
 */
@Serializable
data class EntradaCalendario(
    val semana: Int,
    val inicio: String,
    val fin: String,
    val tipo: TipoSemana,
    val skillId: String? = null,
    val order: Int? = null,
    val topicId: String? = null,
    // "semanal" | "survival" | "repaso": lo que generar_calendario.py escribe
    // en cada fila desde que existe el Survival trimestral (CLAUDE.md, regla
    // dura #11). El parser de la app es estricto (sin ignoreUnknownKeys, ver
    // CargadorContenido.kt) y el campo esta en TODOS los calendarios nuevos
    // (2027 y los 7 parciales de 2026), asi que tiene que estar en el modelo
    // aunque nada lo consuma todavia -- el desafio de fin de semana (feature 2)
    // hoy se calcula solo de la fecha del dispositivo (esFinDeSemana en
    // ResolutorSemana.kt), no de esta columna.
    val desafioFinde: String? = null,
)

@Serializable
enum class TipoSemana {
    @SerialName("content") CONTENT,
    @SerialName("review") REVIEW,
    @SerialName("survival") SURVIVAL,
}
