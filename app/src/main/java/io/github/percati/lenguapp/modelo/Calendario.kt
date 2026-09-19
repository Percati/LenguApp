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
)

@Serializable
enum class TipoSemana {
    @SerialName("content") CONTENT,
    @SerialName("review") REVIEW,
    @SerialName("survival") SURVIVAL,
}
