package io.github.percati.lenguapp.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Pantalla de carga (Ronda G, tarea 5): fondo = fondo del tema Papel (claro #F7F6F2 / oscuro #14171A),
 * sin icono propio (el arte llega en la ronda H) y sin tocar el manifest mas alla del tema.
 */
class SplashRecursosTest {

    private fun res(ruta: String): String =
        listOf(File("src/main/res/$ruta"), File("app/src/main/res/$ruta")).first { it.isFile }.readText()

    private fun manifest(): String =
        listOf(File("src/main/AndroidManifest.xml"), File("app/src/main/AndroidManifest.xml")).first { it.isFile }.readText()

    @Test
    fun `el fondo de la splash es el de Papel, claro y oscuro`() {
        assertTrue(res("values/colors.xml").contains("""<color name="fondo_papel">#F7F6F2</color>"""))
        assertTrue(res("values-night/colors.xml").contains("""<color name="fondo_papel">#14171A</color>"""))
        val temas = res("values/themes.xml")
        assertTrue(temas.contains("""parent="Theme.SplashScreen""""))
        assertTrue(temas.contains("""name="windowSplashScreenBackground">@color/fondo_papel"""))
        assertTrue(temas.contains("""name="postSplashScreenTheme">@style/Theme.LenguApp"""))
    }

    @Test
    fun `la splash no tiene icono propio, el drawable esta vacio`() {
        val vacio = res("drawable/splash_vacio.xml")
        assertTrue(vacio.contains("@android:color/transparent"))
        assertFalse("no se inventa un logo", vacio.contains("<path") || vacio.contains("<vector") || vacio.contains("bitmap"))
        // y el manifest no declara un icono de lanzador inventado
        assertFalse(manifest().contains("android:icon="))
    }

    @Test
    fun `la actividad principal arranca con el tema de splash y el manifest no gana permisos`() {
        val m = manifest()
        assertTrue(m.contains("""android:theme="@style/Theme.LenguApp.Splash""""))
        assertFalse(m.contains("android.permission.INTERNET\"") && !m.contains("tools:node=\"remove\""))
        assertEquals(0, Regex("""<uses-permission android:name="android.permission.INTERNET"""").findAll(m).count())
    }
}
