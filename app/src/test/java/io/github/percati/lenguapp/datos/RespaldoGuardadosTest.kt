package io.github.percati.lenguapp.datos

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.ByteArrayInputStream

/**
 * Copia de seguridad de Guardados (Ronda F2, tarea 6): ida y vuelta, fusion sin duplicar, archivo
 * corrupto, version desconocida y limites. Room en memoria.
 */
@RunWith(RobolectricTestRunner::class)
class RespaldoGuardadosTest {

    private fun repositorio(): RepositorioGuardados {
        val db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), GuardadosDatabase::class.java)
            .allowMainThreadQueries().build()
        return RepositorioGuardados(db.dao())
    }

    private fun bilingue(vararg pares: Pair<String, String>) = TextoBilingue(porIdioma = mapOf(*pares))

    private suspend fun guardarEjemplos(repo: RepositorioGuardados) {
        repo.alternar(Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, "die Miete", bilingue("de" to "die Miete", "es" to "el alquiler", "en" to "the rent"), null, "DE-V02")
        repo.alternar(
            Idioma.EN, Nivel.C1, TipoGuardado.EXPRESION, "I'd say", bilingue("en" to "I'd say", "es" to "yo diría"),
            bilingue("en" to "softening an opinion", "es" to "suavizar una opinión"), "EN-F01",
        )
        repo.alternar(Idioma.EN, Nivel.A2, TipoGuardado.VOCABULARIO, "the lease", TextoBilingue(porIdioma = mapOf("en" to "the lease")), null, "EN-V02")
    }

    private fun sinId(items: List<ItemGuardado>) = items.map { it.copy(id = 0) }

    private fun items(texto: String) = (parsearRespaldo(texto.toByteArray()) as LecturaRespaldo.Correcta).items

    // --- ida y vuelta ---

    @Test
    fun `exportar e importar a una base vacia deja los mismos items`() = runBlocking {
        val origen = repositorio()
        guardarEjemplos(origen)
        val json = serializarRespaldo(origen.listar())

        val destino = repositorio()
        val lectura = parsearRespaldo(json.toByteArray())
        assertTrue(lectura is LecturaRespaldo.Correcta)
        val r = destino.importar((lectura as LecturaRespaldo.Correcta).items)

        assertEquals(ResultadoImportacion(agregados = 3, yaEstaban = 0), r)
        assertEquals(sinId(origen.listar()).sortedBy { it.texto.toString() }, sinId(destino.listar()).sortedBy { it.texto.toString() })
    }

    @Test
    fun `el archivo lleva version 1 y los campos pedidos, nada mas`() = runBlocking {
        val repo = repositorio()
        guardarEjemplos(repo)
        val json = serializarRespaldo(repo.listar())
        assertTrue(json.contains("\"version\": 1"))
        for (campo in listOf("idioma", "nivel", "tipo", "texto", "funcion", "skillIdOrigen")) assertTrue("falta $campo", json.contains("\"$campo\""))
        assertTrue(!json.contains("\"id\""))
        assertEquals("lenguapp-guardados.json", NOMBRE_ARCHIVO_RESPALDO)
    }

    // --- fusion sin duplicar ---

    @Test
    fun `importar fusiona sin duplicar y sin pisar lo existente`() = runBlocking {
        val repo = repositorio()
        guardarEjemplos(repo)
        val respaldo = serializarRespaldo(repo.listar())
        // la misma palabra ya guardada pero con otro origen: no se pisa
        val existente = repo.listar().first { it.texto.porIdioma["en"] == "I'd say" }

        val repo2 = repositorio()
        repo2.alternar(Idioma.EN, Nivel.B1, TipoGuardado.EXPRESION, "I'd say", bilingue("en" to "I'd say", "es" to "OTRA TRADUCCION"), null, "EN-OTRO")
        val r = repo2.importar(items(respaldo))

        assertEquals(ResultadoImportacion(agregados = 2, yaEstaban = 1), r)
        assertEquals(3, repo2.listar().size)
        val intacto = repo2.listar().single { it.texto.porIdioma["en"] == "I'd say" }
        assertEquals("EN-OTRO", intacto.skillIdOrigen) // no se piso
        assertEquals("OTRA TRADUCCION", intacto.texto.porIdioma["es"])
        assertNull(intacto.funcion)
        assertEquals(existente.texto.porIdioma["en"], intacto.texto.porIdioma["en"])

        // importar dos veces lo mismo no duplica
        val otra = repo2.importar(items(respaldo))
        assertEquals(ResultadoImportacion(agregados = 0, yaEstaban = 3), otra)
        assertEquals(3, repo2.listar().size)
    }

    @Test
    fun `duplicados dentro del propio archivo cuentan como ya estaban`() = runBlocking {
        val item = """{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"das Büro"},"skillIdOrigen":"DE-V01"}"""
        val archivo = """{"version":1,"items":[$item,$item]}"""
        val repo = repositorio()
        val r = repo.importar(items(archivo))
        assertEquals(ResultadoImportacion(1, 1), r)
        assertEquals(1, repo.listar().size)
    }

    @Test
    fun `el mismo texto en otro idioma o de otro tipo no es duplicado`() = runBlocking {
        val repo = repositorio()
        val a = """{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"Tag"},"skillIdOrigen":"DE-V01"}"""
        val b = """{"idioma":"en","nivel":"B2","tipo":"VOCABULARIO","texto":{"en":"Tag"},"skillIdOrigen":"EN-V01"}"""
        val c = """{"idioma":"de","nivel":"B2","tipo":"EXPRESION","texto":{"de":"Tag"},"skillIdOrigen":"DE-V01"}"""
        assertEquals(ResultadoImportacion(3, 0), repo.importar(items("""{"version":1,"items":[$a,$b,$c]}""")))
    }

    // --- archivos que se rechazan ---

    private fun rechazo(texto: String): ErrorRespaldo =
        (parsearRespaldo(texto.toByteArray()) as LecturaRespaldo.Rechazada).error

    @Test
    fun `un archivo corrupto o que no es JSON se rechaza con un error claro`() {
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("esto no es json"))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de"""))      // truncado
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("[1,2,3]"))                                          // raiz que no es objeto
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo(""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"items":[]}"""))                                // sin version
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":"uno","items":[]}"""))               // version no numerica
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1}"""))                               // sin items
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":"nada"}"""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"klingon","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"x"},"skillIdOrigen":"X"}]}"""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de","nivel":"Z9","tipo":"VOCABULARIO","texto":{"de":"x"},"skillIdOrigen":"X"}]}"""))
        // binario
        val r = parsearRespaldo(byteArrayOf(0, 1, 2, -1, -2, 7))
        assertEquals(ErrorRespaldo.NO_VALIDO, (r as LecturaRespaldo.Rechazada).error)
    }

    @Test
    fun `una version desconocida se rechaza, la actual se acepta`() {
        assertEquals(ErrorRespaldo.VERSION_DESCONOCIDA, rechazo("""{"version":2,"items":[]}"""))
        assertEquals(ErrorRespaldo.VERSION_DESCONOCIDA, rechazo("""{"version":0,"items":[]}"""))
        assertEquals(emptyList<ItemRespaldo>(), items("""{"version":1,"items":[]}"""))
    }

    @Test
    fun `items sin original o desmesurados se rechazan`() {
        // sin la clave del idioma que se aprende no hay original
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"es":"solo traduccion"},"skillIdOrigen":"X"}]}"""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"   "},"skillIdOrigen":"X"}]}"""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"${"a".repeat(3000)}"},"skillIdOrigen":"X"}]}"""))
        assertEquals(ErrorRespaldo.NO_VALIDO, rechazo("""{"version":1,"items":[{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"x"},"skillIdOrigen":"${"S".repeat(200)}"}]}"""))
    }

    @Test
    fun `un archivo demasiado grande se rechaza antes de interpretarlo`() {
        val grande = ByteArray(LIMITE_BYTES_RESPALDO + 10) { 'a'.code.toByte() }
        assertNull(leerLimitado(ByteArrayInputStream(grande)))
        assertEquals(ErrorRespaldo.DEMASIADO_GRANDE, (parsearRespaldo(null) as LecturaRespaldo.Rechazada).error)
        assertEquals(ErrorRespaldo.DEMASIADO_GRANDE, (parsearRespaldo(grande) as LecturaRespaldo.Rechazada).error)
        // uno chico se lee entero
        assertEquals(5, leerLimitado(ByteArrayInputStream(ByteArray(5)))!!.size)
    }

    @Test
    fun `lo que no esta en los campos documentados se ignora, nunca se interpreta`() = runBlocking {
        val archivo = """{"version":1,"extra":"<script>","items":[{"idioma":"de","nivel":"B2","tipo":"VOCABULARIO","texto":{"de":"das Büro"},"skillIdOrigen":"DE-V01","ejecutar":"rm -rf /"}]}"""
        val repo = repositorio()
        assertEquals(ResultadoImportacion(1, 0), repo.importar(items(archivo)))
        val g = repo.listar().single()
        assertEquals("das Büro", g.texto.porIdioma["de"])
    }
}
