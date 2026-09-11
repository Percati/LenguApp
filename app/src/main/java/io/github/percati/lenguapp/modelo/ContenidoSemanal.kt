package io.github.percati.lenguapp.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

/**
 * Lo que la app muestra en una semana. Dos formas a proposito: una Ficha
 * (Skill + Topic) y una SemanaEspecial (Review/Survival) no comparten campos
 * obligatorios, y forzarlas a un solo tipo exigiria hacer opcionales casi
 * todos los campos de una de las dos. Ver los dos archivos en proyecto/schema.
 */
@Serializable(with = ContenidoSemanalSerializer::class)
sealed interface ContenidoSemanal

/** El id ya es unico entre Ficha y SemanaEspecial (prefijos distintos); sirve para indexar por id sin conocer el subtipo. */
val ContenidoSemanal.id: String
    get() = when (this) {
        is Ficha -> id
        is SemanaEspecial -> id
    }

internal object ContenidoSemanalSerializer :
    JsonContentPolymorphicSerializer<ContenidoSemanal>(ContenidoSemanal::class) {
    override fun selectDeserializer(element: JsonElement) =
        if ("_tipo" in element.jsonObject) SemanaEspecial.serializer() else Ficha.serializer()
}

@Serializable
enum class Idioma {
    @SerialName("en") EN, @SerialName("de") DE, @SerialName("es") ES,
    @SerialName("fr") FR, @SerialName("it") IT, @SerialName("pt") PT,
}

@Serializable
enum class Nivel { A2, B1, B2, C1, C2 }

@Serializable
enum class Categoria {
    @SerialName("grammar") GRAMMAR, @SerialName("fluency") FLUENCY,
    @SerialName("vocabulary") VOCABULARY,
}

@Serializable
enum class ChallengeType {
    @SerialName("chunk_deployment") CHUNK_DEPLOYMENT,
    @SerialName("guided_production") GUIDED_PRODUCTION,
    @SerialName("constrained_production") CONSTRAINED_PRODUCTION,
    @SerialName("open_production") OPEN_PRODUCTION,
    @SerialName("adaptive_production") ADAPTIVE_PRODUCTION,
}

@Serializable
enum class Prioridad { @SerialName("nucleo") NUCLEO, @SerialName("ampliacion") AMPLIACION }

@Serializable
enum class Dia { @SerialName("lun") LUN, @SerialName("mie") MIE, @SerialName("vie") VIE }

@Serializable
enum class Clase { @SerialName("review") REVIEW, @SerialName("survival") SURVIVAL }

/** Una aparicion de un Skill en un nivel concreto, con su Topic. proyecto/schema/ficha.schema.json */
@Serializable
data class Ficha(
    val id: String,
    val skillId: String,
    val order: Int,
    val idioma: Idioma,
    val nivel: Nivel,
    val categoria: Categoria,
    val topicId: String,
    val titulo: String,
    val subtitulo: String,
    val ancla: String? = null,
    val challengeType: ChallengeType,
    val bilingue: Boolean = false,
    val evidencia: Evidencia,
    val descripcion: String,
    val cuadroReferencia: CuadroReferencia? = null,
    val ejemplos: List<Ejemplo>,
    val notas: List<String>,
    // Solo "es" esta poblado en 2026; el resto de las lenguas base llegan despues.
    val contraste: Map<String, String>? = null,
    val errores: List<String>,
    val vocabulario: List<VocabularioItem>,
    val redemittel: List<RedemittelItem>,
    val mision: Mision,
    val microtareas: List<Microtarea>,
    val autochequeo: List<String>,
    val promptCorreccion: String,
    // Solo dato para el generador de calendario; no se muestra en la ficha.
    val skillsRelacionados: List<String>? = null,
    val topicBlocklist: List<String>? = null,
    // Si esta presente (p.ej. "CH"), la app DEBE etiquetarla y permitir desactivarla.
    val variante: String? = null,
) : ContenidoSemanal

@Serializable
data class Evidencia(
    val escritura: Int,
    val oralMin: Double,
    val minutosEstimados: Int,
)

@Serializable
data class CuadroReferencia(
    val titulo: String,
    val columnas: List<String>,
    val filas: List<List<String>>,
    val notaPie: String? = null,
)

@Serializable
data class Ejemplo(
    val grupo: String? = null,
    val texto: String,
    val audio: Boolean = false,
)

@Serializable
data class VocabularioItem(
    val item: String,
    val prioridad: Prioridad,
    // Caso o preposicion que rige. Obligatorio de facto en aleman.
    val reccion: String? = null,
    val tipo: String? = null,
    val nota: String? = null,
    // p.ej. "CH" para helvetismos.
    val variante: String? = null,
    // Lo rellena check_level.py; no se escribe a mano.
    val nivelVerificado: String? = null,
    // Valores vacios a proposito en los idiomas todavia sin traducir: no es
    // un error, hay que caer al idioma base y nunca mostrar la clave cruda.
    val traducciones: Map<String, String>,
    // El item esta bajo el nivel declarado a proposito (distincion, registro
    // o doble sentido que si corresponde al nivel), no es un error de banco.
    val bajoNivelJustificado: Boolean = false,
)

@Serializable
data class RedemittelItem(
    val expresion: String,
    val funcion: String,
    // null = sin equivalencia directa; se aprende por situacion, no es un error.
    val traducciones: Map<String, String?>,
)

@Serializable
data class Mision(
    val consigna: String,
    val requisitos: List<String>,
)

@Serializable
data class Microtarea(
    val dia: Dia,
    val texto: String,
    val minutos: Int,
)

/**
 * Semana de repaso o Survival: no tiene skill ni topic.
 * proyecto/schema/semana-especial.schema.json
 */
@Serializable
data class SemanaEspecial(
    @SerialName("_tipo") val tipo: String = "semana_especial",
    val id: String,
    val semana: Int,
    val idioma: Idioma,
    val clase: Clase,
    val titulo: String,
    val minutosEstimados: Int? = null,
    val consigna: String,
    val requisitos: List<String>? = null,
    // Aca son texto libre, a diferencia de Ficha.microtareas que son objetos.
    val microtareas: List<String>? = null,
    val autochequeo: List<String>,
    val promptCorreccion: String,
) : ContenidoSemanal
