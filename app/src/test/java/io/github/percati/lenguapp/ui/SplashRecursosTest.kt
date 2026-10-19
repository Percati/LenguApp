package io.github.percati.lenguapp.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Pantalla de carga (Ronda G, reescrita en la Ronda V): fondo solido #253C52 (`fondo_splash`), el mismo en claro y en oscuro, con la L
 * (`splash_l`) como icono estatico. NO usa `fondo_papel`: sobre el papel claro la L contrasta 1.38/1.29 y casi no se ve.
 */
class SplashRecursosTest {

    private fun res(ruta: String): String =
        listOf(File("src/main/res/$ruta"), File("app/src/main/res/$ruta")).first { it.isFile }.readText().replace("\r\n", "\n")

    private fun manifest(): String =
        listOf(File("src/main/AndroidManifest.xml"), File("app/src/main/AndroidManifest.xml")).first { it.isFile }.readText()

    private fun estiloSplash(): String {
        val temas = res("values/themes.xml")
        val ini = temas.indexOf("""<style name="Theme.LenguApp.Splash"""")
        return temas.substring(ini, temas.indexOf("</style>", ini))
    }

    @Test
    fun `el fondo de la splash es el azul propio, igual en claro y en oscuro, y no el de Papel`() {
        val splash = estiloSplash()
        assertTrue(splash.contains("""parent="Theme.SplashScreen""""))
        assertTrue(splash.contains("""name="windowSplashScreenBackground">@color/fondo_splash"""))
        // A PROPOSITO no fondo_papel: con el papel claro la L es casi invisible (contraste 1.38 el asta, 1.29 la cola). No «arreglarlo».
        assertFalse("la splash NO usa fondo_papel", splash.contains("fondo_papel"))
        assertTrue(res("values/colors.xml").contains("""<color name="fondo_splash">#253C52</color>"""))
        // el mismo en claro y en oscuro: no se redefine en values-night
        assertFalse(res("values-night/colors.xml").contains("fondo_splash"))
        assertFalse(res("values-night/themes.xml").contains("Splash"))
        // y el papel de la ventana de despues no cambio
        assertTrue(res("values/colors.xml").contains("""<color name="fondo_papel">#F7F6F2</color>"""))
        assertTrue(res("values-night/colors.xml").contains("""<color name="fondo_papel">#14171A</color>"""))
        assertTrue(splash.contains("""name="postSplashScreenTheme">@style/Theme.LenguApp"""))
    }

    @Test
    fun `el icono es la L estatica, sin color de fondo de icono ni animacion`() {
        val splash = estiloSplash()
        assertTrue(splash.contains("""name="windowSplashScreenAnimatedIcon">@drawable/splash_l"""))
        // windowSplashScreenIconBackgroundColor cambiaria el lienzo a 240 (circulo util de 160) y el arte dejaria de entrar
        assertFalse(splash.contains("IconBackgroundColor"))
        assertFalse(splash.contains("AnimationDuration") || splash.contains("animated-vector") || splash.contains("<animation"))
        val raiz = listOf(File("src/main/res"), File("app/src/main/res")).first { it.isDirectory }
        assertTrue(File(raiz, "drawable-xxhdpi/splash_l.webp").isFile)
        assertFalse("el placeholder vacio ya no existe", File(raiz, "drawable/splash_vacio.xml").exists())
    }

    @Test
    fun `la actividad principal arranca con el tema de splash y el manifest no gana permisos`() {
        val m = manifest()
        assertTrue(m.contains("""android:theme="@style/Theme.LenguApp.Splash""""))
        assertFalse(m.contains("android.permission.INTERNET\"") && !m.contains("tools:node=\"remove\""))
        assertEquals(0, Regex("""<uses-permission android:name="android.permission.INTERNET"""").findAll(m).count())
    }
}
