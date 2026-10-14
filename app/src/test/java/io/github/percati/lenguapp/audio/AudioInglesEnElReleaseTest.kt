package io.github.percati.lenguapp.audio

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * El audio ingles VIAJA en el release (Ronda S, tarea 6; antes `AudioInglesFueraDelReleaseTest`, de la Ronda N). Se publica como esta,
 * atribuyendo (Acerca de, bloque «Voces»). El empaquetado no se puede probar desde un test de JVM, asi que este test vigila que no vuelva
 * a colarse en `app/build.gradle.kts` nada que lo deje fuera: ni la exclusion de la carpeta ni la guarda que hacia fallar el release.
 */
class AudioInglesEnElReleaseTest {

    private val gradle: String by lazy {
        listOf(File("build.gradle.kts"), File("app/build.gradle.kts")).first { it.isFile }.readText().replace("\r\n", "\n")
    }

    @Test
    fun `el release ya no excluye la carpeta de audio ingles ni tiene la guarda que lo hacia fallar`() {
        assertFalse("sobra el ignoreAssetsPatterns de la carpeta EN", gradle.contains("ignoreAssetsPatterns"))
        assertFalse("sobra el bloque androidComponents de la exclusion", gradle.contains("androidComponents"))
        assertFalse("sobra la tarea guarda", gradle.contains("verificarReleaseSinAudioIngles"))
        assertFalse("sobre el dependsOn del empaquetado", gradle.contains("packageReleaseBundle"))
    }

    @Test
    fun `el interruptor esta encendido en release y en debug`() {
        val release = gradle.substringAfter("release {").substringBefore("}")
        val debug = gradle.substringAfter("debug {").substringBefore("}")
        assertTrue(release.contains("""buildConfigField("boolean", "AUDIO_INGLES", "true")"""))
        assertTrue(debug.contains("""buildConfigField("boolean", "AUDIO_INGLES", "true")"""))
    }

    @Test
    fun `las demas guardas de la construccion siguen en pie`() {
        // la Ronda S solo quita la del audio ingles: las otras, y la de preBuild, no se tocan
        assertTrue(gradle.contains("verificarAssetsContenidoActualizados"))
        assertTrue(gradle.contains("verificarConsistenciaContenido"))
    }

    @Test
    fun `los clips ingleses estan en el repo y por lo tanto en las dos variantes`() {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val clips = File(assets, "audio/EN").listFiles { f -> f.extension == "ogg" }!!.size
        assertTrue("clips ingleses en el repo: $clips", clips > 4000)
    }
}
