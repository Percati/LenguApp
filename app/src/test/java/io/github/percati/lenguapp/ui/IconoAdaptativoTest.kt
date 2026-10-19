package io.github.percati.lenguapp.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * El icono adaptativo (Ronda V, tarea 3): cableado de los assets que trajo Overview. No hay icono monocromo (hueco documentado), ni
 * respaldo heredado (minSdk 26), ni mas densidades que xxhdpi y xxxhdpi.
 */
class IconoAdaptativoTest {

    private fun carpeta(ruta: String): File = listOf(File("src/main/$ruta"), File("app/src/main/$ruta")).first { it.exists() }
    private fun texto(ruta: String): String = carpeta(ruta).readText().replace("\r\n", "\n")

    @Test
    fun `el manifest declara icon y roundIcon`() {
        val m = texto("AndroidManifest.xml")
        assertTrue(m.contains("""android:icon="@mipmap/ic_launcher""""))
        assertTrue(m.contains("""android:roundIcon="@mipmap/ic_launcher_round""""))
        assertEquals(0, Regex("""android.permission.INTERNET"""").findAll(m.replace("tools:node=\"remove\"", "")).count())
    }

    @Test
    fun `los dos iconos adaptativos apuntan al fondo plano y al primer plano`() {
        for (nombre in listOf("ic_launcher", "ic_launcher_round")) {
            val x = texto("res/mipmap-anydpi-v26/$nombre.xml")
            assertTrue(nombre, x.contains("<adaptive-icon"))
            assertTrue(nombre, x.contains("""<background android:drawable="@drawable/ic_launcher_background""""))
            assertTrue(nombre, x.contains("""<foreground android:drawable="@mipmap/ic_launcher_foreground""""))
            assertFalse("$nombre: sin <monochrome> (hueco documentado, lo decide Fer)", x.contains("<monochrome"))
            assertTrue("$nombre: el hueco del monocromo esta documentado", x.contains("HUECO DOCUMENTADO"))
        }
    }

    @Test
    fun `el fondo es el azul de la marca, plano, y los primeros planos existen solo en xxhdpi y xxxhdpi`() {
        assertTrue(texto("res/drawable/ic_launcher_background.xml").contains("#253C52"))
        assertTrue(carpeta("res/mipmap-xxxhdpi/ic_launcher_foreground.webp").isFile)
        assertTrue(carpeta("res/mipmap-xxhdpi/ic_launcher_foreground.webp").isFile)
        // nada de respaldo heredado ni de densidades de mas: ni un ic_launcher suelto en ninguna carpeta mipmap
        val mipmaps = carpeta("res").listFiles { f -> f.isDirectory && f.name.startsWith("mipmap") }!!
        assertEquals(setOf("mipmap-anydpi-v26", "mipmap-xxhdpi", "mipmap-xxxhdpi"), mipmaps.map { it.name }.toSet())
        for (d in mipmaps) assertTrue("${d.name}: ${d.list()?.toList()}", d.list()!!.none { (it.startsWith("ic_launcher.") || it.startsWith("ic_launcher_round.")) && !it.endsWith(".xml") })
    }

    @Test
    fun `minSdk es 26, que es lo que permite no tener respaldo heredado`() {
        val gradle = listOf(File("build.gradle.kts"), File("app/build.gradle.kts")).first { it.isFile }.readText()
        assertTrue(gradle.contains("minSdk = 26"))
    }
}
