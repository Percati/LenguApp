package io.github.percati.lenguapp.audio

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * El audio ingles NO viaja en un release (Ronda N, tarea 1). El empaquetado no se puede probar desde un test de JVM, asi que
 * la garantia real es la tarea de Gradle `verificarReleaseSinAudioIngles` (falla el release si los assets fusionados llevan
 * `audio/EN`); este test vigila que la configuracion que la sostiene siga en `app/build.gradle.kts` y que el debug conserve
 * los clips para que Fer pueda probarlos.
 */
class AudioInglesFueraDelReleaseTest {

    private val gradle: String by lazy {
        listOf(File("build.gradle.kts"), File("app/build.gradle.kts")).first { it.isFile }.readText().replace("\r\n", "\n")
    }

    @Test
    fun `el release ignora la carpeta de audio ingles y una tarea falla si se cuela`() {
        assertTrue("falta ignoreAssetsPatterns para release", gradle.contains("""selector().withBuildType("release")"""))
        assertTrue(gradle.contains("""ignoreAssetsPatterns.add("<dir>EN")"""))
        assertTrue("falta la guarda", gradle.contains("val verificarReleaseSinAudioIngles by tasks.registering"))
        assertTrue("la guarda debe colgar del empaquetado de release", gradle.contains("dependsOn(verificarReleaseSinAudioIngles)"))
    }

    @Test
    fun `el interruptor esta apagado en release y encendido en debug`() {
        val release = gradle.substringAfter("release {").substringBefore("}")
        val debug = gradle.substringAfter("debug {").substringBefore("}")
        assertTrue(release.contains("""buildConfigField("boolean", "AUDIO_INGLES", "false")"""))
        assertTrue(debug.contains("""buildConfigField("boolean", "AUDIO_INGLES", "true")"""))
    }

    @Test
    fun `los clips ingleses siguen en el repo para el debug`() {
        val assets = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val clips = File(assets, "audio/EN").listFiles { f -> f.extension == "ogg" }!!.size
        assertTrue("clips ingleses en el repo: $clips", clips > 4000)
    }
}
