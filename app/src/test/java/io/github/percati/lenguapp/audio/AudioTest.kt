package io.github.percati.lenguapp.audio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.BuildConfig
import io.github.percati.lenguapp.datos.parsearContenido
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

/** Reproductor falso: registra lo que se le pide y deja al test decidir si el clip "se abre". */
private class ReproductorFalso(var abre: Boolean = true) : ReproductorAudio {
    val pedidos = mutableListOf<String>()
    var detenciones = 0
    var liberado = false
    private var alTerminar: ((Boolean) -> Unit)? = null

    override fun reproducir(rutaAsset: String, alTerminar: (Boolean) -> Unit): Boolean {
        pedidos += rutaAsset
        this.alTerminar = alTerminar
        return abre
    }

    override fun detener() { detenciones++ }
    override fun liberar() { liberado = true }
    fun terminar(ok: Boolean = true) = alTerminar?.invoke(ok)
}

private fun assets(): File = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }

/** Indice de audio (Ronda M, tarea 1), sobre el `indice.json` real. */
class IndiceAudioTest {

    private val json by lazy { File(assets(), "audio/indice.json").readText() }
    private val indice by lazy { IndiceAudio.parsear(json) }

    @Test
    fun `el indice real se parsea y trae los dos idiomas con las tres clases`() {
        assertEquals(setOf(Idioma.DE, Idioma.EN), indice.idiomasConAudio())
        assertEquals("8143 textos con audio", 8143, indice.total)
    }

    @Test
    fun `todo archivo del indice existe en los assets de audio`() {
        val crudo = kotlinx.serialization.json.Json.parseToJsonElement(json).jsonObject
        var n = 0
        for ((idioma, clases) in crudo) for ((_, m) in clases.jsonObject) {
            for ((_, archivo) in m.jsonObject) {
                n++
                val f = File(assets(), "audio/${idioma.uppercase()}/${archivo.jsonPrimitive.content}")
                assertTrue("falta ${f.path}", f.isFile)
            }
        }
        assertEquals(8143, n)
    }

    @Test
    fun `un texto con audio da su ruta y uno sin audio da null`() {
        val ficha = File(assets(), "contenido").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
            .map { parsearContenido(it.readText()) }.filterIsInstance<Ficha>().first { it.idioma == Idioma.DE }
        val conAudio = ficha.vocabulario.first { indice.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, it.item.trim()) != null }.item.trim()
        val ruta = indice.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, conAudio)!!
        assertTrue(ruta, ruta.startsWith("audio/DE/") && ruta.endsWith(".ogg"))
        assertNull(indice.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "zzz texto que no existe"))
        // la clase importa: el mismo texto como expresion no es el mismo clip
        assertNull(indice.ruta(Idioma.DE, ClaseAudio.EXPRESION, "zzz texto que no existe"))
        assertNull(IndiceAudio.VACIO.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, conAudio))
    }

    @Test
    fun `el parser es estricto, un idioma o una clase desconocidos son un error`() {
        try { IndiceAudio.parsear("""{"xx": {"Vocabulario": {"a": "a.ogg"}}}"""); fail("idioma desconocido") } catch (_: IllegalArgumentException) {}
        try { IndiceAudio.parsear("""{"de": {"Chiste": {"a": "a.ogg"}}}"""); fail("clase desconocida") } catch (_: IllegalArgumentException) {}
        try { IndiceAudio.parsear("""{"de": {"Vocabulario": {"a": 3}}}"""); fail("valor no texto") } catch (_: Exception) {}
        val ok = IndiceAudio.parsear("""{"de": {"Vocabulario": {"a": "a.ogg"}}}""")
        assertEquals("audio/DE/a.ogg", ok.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "a"))
    }

    @Test
    fun `sin ingles el indice no ofrece ningun clip ingles`() {
        val sinIngles = IndiceAudio.parsear(json, incluirIngles = false)
        assertEquals(setOf(Idioma.DE), sinIngles.idiomasConAudio())
        val conIngles = IndiceAudio.parsear(json, incluirIngles = true)
        assertTrue(conIngles.total > sinIngles.total)
    }

    @Test
    fun `el interruptor del audio ingles esta encendido en debug y apagado en release`() {
        assertEquals("AUDIO_INGLES debe valer lo mismo que DEBUG", BuildConfig.DEBUG, BuildConfig.AUDIO_INGLES)
    }
}

/** Control del audio: un solo clip a la vez, sin crash ante un archivo que falta. */
class ControlAudioTest {

    private val indice = IndiceAudio.parsear("""{"de": {"Vocabulario": {"a": "a.ogg", "b": "b.ogg"}}}""")

    @Test
    fun `un texto con audio tiene ruta y uno sin audio no, asi que no hay boton`() {
        val control = ControlAudio(indice, ReproductorFalso())
        assertEquals("audio/DE/a.ogg", control.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "a"))
        assertNull(control.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "c"))
        assertNull(control.ruta(Idioma.EN, ClaseAudio.VOCABULARIO, "a"))
        assertNull(SinAudio.ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "a"))
        assertNull("sin reproductor tampoco hay boton", ControlAudio(indice, null).ruta(Idioma.DE, ClaseAudio.VOCABULARIO, "a"))
    }

    @Test
    fun `tocar un clip lo hace sonar, tocarlo otra vez lo corta`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice, rep)
        control.alternar("audio/DE/a.ogg")
        assertEquals("audio/DE/a.ogg", control.sonando)
        control.alternar("audio/DE/a.ogg")
        assertNull(control.sonando)
        assertEquals(1, rep.detenciones)
        assertEquals(listOf("audio/DE/a.ogg"), rep.pedidos)
    }

    @Test
    fun `un solo audio a la vez, tocar otro corta el anterior`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice, rep)
        control.alternar("audio/DE/a.ogg")
        control.alternar("audio/DE/b.ogg")
        assertEquals("audio/DE/b.ogg", control.sonando)
        assertEquals(listOf("audio/DE/a.ogg", "audio/DE/b.ogg"), rep.pedidos)
        // el reproductor real corta el anterior al empezar el nuevo; el control solo marca uno como sonando
        rep.terminar()
        assertNull(control.sonando)
    }

    @Test
    fun `al terminar el clip, el boton vuelve a reposo`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice, rep)
        control.alternar("audio/DE/a.ogg")
        rep.terminar(ok = true)
        assertNull(control.sonando)
        control.alternar("audio/DE/a.ogg")
        rep.terminar(ok = false)
        assertNull(control.sonando)
    }

    @Test
    fun `un archivo que no se puede abrir no rompe ni deja el boton colgado`() {
        val rep = ReproductorFalso(abre = false)
        val control = ControlAudio(indice, rep)
        control.alternar("audio/DE/a.ogg")
        assertNull(control.sonando)
        control.alternar("audio/DE/b.ogg")
        assertNull(control.sonando)
        assertEquals(2, rep.pedidos.size)
    }

    @Test
    fun `detener y liberar sueltan el reproductor y dejan todo en reposo`() {
        val rep = ReproductorFalso()
        val control = ControlAudio(indice, rep)
        control.alternar("audio/DE/a.ogg")
        control.detener()
        assertNull(control.sonando)
        assertEquals(1, rep.detenciones)
        control.alternar("audio/DE/b.ogg")
        control.liberar()
        assertTrue(rep.liberado)
        assertNull(control.sonando)
        // detener sin nada sonando tampoco falla
        SinAudio.detener()
        SinAudio.alternar("audio/DE/a.ogg")
        assertNull(SinAudio.sonando)
    }
}

/** El reproductor real (MediaPlayer), con un archivo que no existe: nada de crash. */
@RunWith(RobolectricTestRunner::class)
class ReproductorAssetsTest {

    private val contexto: Context get() = ApplicationProvider.getApplicationContext()

    @Test
    fun `un clip inexistente devuelve false, no lanza y no llama a alTerminar`() {
        val rep = ReproductorAssets(contexto)
        var llamado = false
        assertFalse(rep.reproducir("audio/DE/no-existe.ogg") { llamado = true })
        assertFalse(llamado)
        rep.detener()
        rep.liberar()
    }

    @Test
    fun `detener y liberar sin nada sonando no hacen nada raro`() {
        val rep = ReproductorAssets(contexto)
        rep.detener()
        rep.detener()
        rep.liberar()
    }

    @Test
    fun `el indice se carga desde los assets de la app y sin el ingles no trae clips ingleses`() {
        val todo = cargarIndiceAudioDesdeAssets(contexto, incluirIngles = true)
        val sinIngles = cargarIndiceAudioDesdeAssets(contexto, incluirIngles = false)
        assertEquals(8143, todo.total)
        assertEquals(setOf(Idioma.DE), sinIngles.idiomasConAudio())
    }
}
