package io.github.percati.lenguapp.modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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

/**
 * Texto de prosa que el schema deja como string plano O como objeto
 * {idioma: texto} (A2/B1 bilingues, proyecto/schema/ficha.schema.json, "campos
 * bilingues A2/B1"). El string plano (B2/C1/C2 y el piloto 2026) se resuelve
 * siempre igual; el objeto se resuelve con [resolver].
 */
@Serializable(with = TextoBilingueSerializer::class)
data class TextoBilingue(
    val plano: String? = null,
    val porIdioma: Map<String, String> = emptyMap(),
) {
    companion object {
        fun de(texto: String) = TextoBilingue(plano = texto)
    }
}

/**
 * Texto en el idioma de la app; si falta esa clave (o esta vacia) cae al
 * idioma que se aprende, y si tampoco esta, a cualquier otro que haya -- nunca
 * lanza. Un string plano se devuelve tal cual, sin mirar los idiomas.
 */
fun TextoBilingue.resolver(idiomaApp: Idioma, idiomaAprendido: Idioma): String {
    plano?.let { return it }
    fun de(idioma: Idioma) = porIdioma[idioma.name.lowercase()]?.takeIf { it.isNotBlank() }
    return de(idiomaApp) ?: de(idiomaAprendido) ?: porIdioma.values.firstOrNull { it.isNotBlank() } ?: ""
}

internal object TextoBilingueSerializer : KSerializer<TextoBilingue> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun deserialize(decoder: Decoder): TextoBilingue {
        val elemento = (decoder as? JsonDecoder ?: throw SerializationException("TextoBilingue solo se lee desde JSON")).decodeJsonElement()
        return when (elemento) {
            is JsonPrimitive ->
                if (elemento.isString) TextoBilingue(plano = elemento.content)
                else throw SerializationException("se esperaba un string o un objeto {idioma: texto}, no $elemento")
            is JsonObject -> TextoBilingue(
                porIdioma = elemento.mapValues { (clave, valor) ->
                    (valor as? JsonPrimitive)?.takeIf { it.isString }?.content
                        ?: throw SerializationException("el valor de '$clave' debe ser un string, no $valor")
                },
            )
            else -> throw SerializationException("se esperaba un string o un objeto {idioma: texto}, no $elemento")
        }
    }

    override fun serialize(encoder: Encoder, value: TextoBilingue) {
        val salida = encoder as? JsonEncoder ?: throw SerializationException("TextoBilingue solo se escribe a JSON")
        salida.encodeJsonElement(
            value.plano?.let { JsonPrimitive(it) } ?: JsonObject(value.porIdioma.mapValues { JsonPrimitive(it.value) }),
        )
    }
}

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
    val titulo: TextoBilingue,
    val subtitulo: TextoBilingue,
    val ancla: String? = null,
    val challengeType: ChallengeType,
    val bilingue: Boolean = false,
    val evidencia: Evidencia,
    val descripcion: TextoBilingue,
    val cuadroReferencia: CuadroReferencia? = null,
    val ejemplos: List<Ejemplo>,
    val notas: List<TextoBilingue>,
    // Un renglon por lengua base; solo las lenguas con contenido escrito
    // estan presentes (2026: "es" en todas, "en" en las 14 fichas alemanas).
    // AJUSTES posteriores a Fase 9: la app muestra la del idioma base y
    // oculta la seccion entera si falta la clave -- nunca cae a "es" bajo
    // un titulo que anuncia otro idioma (ver contrasteParaMostrar()).
    val contraste: Map<String, String>? = null,
    val errores: List<TextoBilingue>,
    // Errores tipicos ADICIONALES segun la lengua base (los de un
    // anglohablante que aprende aleman no son los de un hispanohablante):
    // se suman a errores, no lo reemplazan. Ausente si no hay ninguno
    // escrito para ese par (idioma que se aprende, lengua base).
    val erroresContrastivos: Map<String, List<String>>? = null,
    val vocabulario: List<VocabularioItem>,
    val redemittel: List<RedemittelItem>,
    val mision: Mision,
    val microtareas: List<Microtarea>,
    val autochequeo: List<TextoBilingue>,
    val promptCorreccion: TextoBilingue,
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
    val titulo: TextoBilingue,
    val columnas: List<TextoBilingue>,
    val filas: List<List<TextoBilingue>>,
    val notaPie: TextoBilingue? = null,
)

@Serializable
data class Ejemplo(
    val grupo: String? = null,
    val texto: TextoBilingue,
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
    val funcion: TextoBilingue,
    // null = sin equivalencia directa; se aprende por situacion, no es un error.
    val traducciones: Map<String, String?>,
)

@Serializable
data class Mision(
    val consigna: TextoBilingue,
    val requisitos: List<TextoBilingue>,
)

@Serializable
data class Microtarea(
    val dia: Dia,
    val texto: TextoBilingue,
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
    // Agregado cuando ingles paso a tener B2 y C1 en 2026: el id ya lo
    // llevaba (REVIEW-EN-B2-S40 vs REVIEW-EN-C1-S40) porque cada nivel tiene
    // su propia semana de repaso/Survival, pero el campo explicito faltaba.
    val nivel: Nivel,
    val clase: Clase,
    val titulo: TextoBilingue,
    val minutosEstimados: Int? = null,
    val consigna: TextoBilingue,
    val requisitos: List<TextoBilingue>? = null,
    // Aca son texto libre, a diferencia de Ficha.microtareas que son objetos.
    val microtareas: List<TextoBilingue>? = null,
    val autochequeo: List<TextoBilingue>,
    val promptCorreccion: TextoBilingue,
) : ContenidoSemanal
