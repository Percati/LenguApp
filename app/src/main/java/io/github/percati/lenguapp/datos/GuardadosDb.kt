package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Delete
import androidx.room.Insert
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import kotlinx.serialization.encodeToString

/**
 * Guardados (feature 3): unica tabla persistida de datos de usuario en toda
 * la app -- excepcion documentada a CLAUDE.md regla dura #4, ver ese
 * archivo. `textoOrigen` es la palabra/expresion en el idioma que se
 * aprende, tal cual esta en la ficha (VocabularioItem.item /
 * RedemittelItem.expresion): sirve solo para el toggle guardar/quitar
 * (¿esta ya guardado ESTE item de ESTA ficha?), no se expone en
 * [ItemGuardado] -- ahi lo que se muestra es [textoJson] resuelto por
 * idioma, no la clave de identidad.
 */
@Entity(tableName = "guardados")
data class GuardadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val idioma: Idioma,
    val nivel: Nivel,
    val tipo: TipoGuardado,
    val textoOrigen: String,
    val textoJson: String,
    val funcionJson: String?,
    val skillIdOrigen: String,
)

fun GuardadoEntity.aDominio(): ItemGuardado = ItemGuardado(
    id = id,
    idioma = idioma,
    nivel = nivel,
    tipo = tipo,
    texto = jsonContenido.decodeFromString(TextoBilingue.serializer(), textoJson),
    funcion = funcionJson?.let { jsonContenido.decodeFromString(TextoBilingue.serializer(), it) },
    skillIdOrigen = skillIdOrigen,
)

class Convertidores {
    @TypeConverter fun idiomaAString(v: Idioma): String = v.name

    @TypeConverter fun stringAIdioma(v: String): Idioma = Idioma.valueOf(v)

    @TypeConverter fun nivelAString(v: Nivel): String = v.name

    @TypeConverter fun stringANivel(v: String): Nivel = Nivel.valueOf(v)

    @TypeConverter fun tipoAString(v: TipoGuardado): String = v.name

    @TypeConverter fun stringATipo(v: String): TipoGuardado = TipoGuardado.valueOf(v)
}

@Dao
interface GuardadoDao {
    @Query("SELECT * FROM guardados ORDER BY id DESC")
    suspend fun listarTodos(): List<GuardadoEntity>

    @Query("SELECT * FROM guardados WHERE skillIdOrigen = :skillIdOrigen AND tipo = :tipo AND textoOrigen = :textoOrigen LIMIT 1")
    suspend fun buscar(skillIdOrigen: String, tipo: TipoGuardado, textoOrigen: String): GuardadoEntity?

    @Query("SELECT * FROM guardados WHERE idioma = :idioma AND tipo = :tipo AND textoOrigen = :textoOrigen")
    suspend fun buscarPorTexto(idioma: Idioma, tipo: TipoGuardado, textoOrigen: String): List<GuardadoEntity>

    @Insert
    suspend fun insertar(entidad: GuardadoEntity)

    @Delete
    suspend fun borrar(entidad: GuardadoEntity)
}

@Database(entities = [GuardadoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Convertidores::class)
abstract class GuardadosDatabase : RoomDatabase() {
    abstract fun dao(): GuardadoDao
}

private var instancia: GuardadosDatabase? = null

private fun db(context: Context): GuardadosDatabase =
    instancia ?: Room.databaseBuilder(context.applicationContext, GuardadosDatabase::class.java, "guardados.db")
        .build().also { instancia = it }

/**
 * Repositorio sin dependencia de Context mas alla de abrir la base: el resto
 * de la app (pantallas, tests) trabaja con [ItemGuardado]/[GuardadoDao] de
 * dominio, nunca con la entidad de Room directamente.
 */
class RepositorioGuardados(private val dao: GuardadoDao) {
    constructor(context: Context) : this(db(context).dao())

    suspend fun listar(): List<ItemGuardado> = dao.listarTodos().map { it.aDominio() }

    suspend fun estaGuardado(skillIdOrigen: String, tipo: TipoGuardado, textoOrigen: String): Boolean =
        dao.buscar(skillIdOrigen, tipo, textoOrigen) != null

    /**
     * Guarda si no estaba, lo quita si ya estaba -- toggle sin confirmacion.
     *
     * La identidad de un guardado es (idioma, tipo, texto), no (skill, tipo,
     * texto): la misma palabra o expresion aparece en varias fichas y niveles, y la
     * estrella de la ficha y la de la Biblioteca tienen que ser la misma estrella.
     * `skillIdOrigen` y `nivel` quedan como el origen de la PRIMERA vez que se la
     * guardo (para volver a esa ficha desde Guardados). Quitar borra todas las filas
     * con ese texto, tambien las que haya dejado una version anterior de la app
     * (cuando la identidad incluia el skill).
     */
    suspend fun alternar(
        idioma: Idioma,
        nivel: Nivel,
        tipo: TipoGuardado,
        textoOrigen: String,
        texto: TextoBilingue,
        funcion: TextoBilingue?,
        skillIdOrigen: String,
    ) {
        val existentes = dao.buscarPorTexto(idioma, tipo, textoOrigen)
        if (existentes.isNotEmpty()) {
            existentes.forEach { dao.borrar(it) }
        } else {
            dao.insertar(
                GuardadoEntity(
                    idioma = idioma,
                    nivel = nivel,
                    tipo = tipo,
                    textoOrigen = textoOrigen,
                    textoJson = jsonContenido.encodeToString(texto),
                    funcionJson = funcion?.let { jsonContenido.encodeToString(it) },
                    skillIdOrigen = skillIdOrigen,
                ),
            )
        }
    }
}
