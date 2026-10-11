package io.github.percati.lenguapp.datos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.FamiliaTema
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ModoTema
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val PREFS = "ajustes"

/**
 * Es preferencia, no progreso (ver el comentario en RepositorioAjustes.kt):
 * persistirla no viola el modo revista. Estos tests son el contrato de esa
 * distincion -- guardar y volver a leer tiene que dar exactamente lo mismo
 * -- mas la migracion obligatoria del esquema viejo (AJUSTES-FASE-6.md,
 * bloque B): sin ella la app arranca sin ningun idioma seleccionado.
 */
@RunWith(RobolectricTestRunner::class)
class RepositorioAjustesTest {

    private fun contexto(): Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `sin nada guardado, los valores por defecto son un combo que si tiene contenido`() {
        assertEquals(Ajustes(), cargarAjustes(contexto()))
    }

    @Test
    fun `guardar y volver a cargar da lo mismo que se guardo, con varios idiomas`() {
        val contexto = contexto()
        val ajustes = Ajustes(
            idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2, Idioma.EN to Nivel.C1, Idioma.FR to Nivel.A2),
            idiomaBase = Idioma.PT,
            idiomaInterfaz = Idioma.PT,
            idiomaSegunSistema = false,
        )
        guardarAjustes(contexto, ajustes)
        assertEquals(ajustes, cargarAjustes(contexto))
    }

    @Test
    fun `guardar y cargar preserva idiomaSegunSistema activo`() {
        val contexto = contexto()
        guardarAjustes(contexto, Ajustes(idiomaSegunSistema = true))
        assertEquals(true, cargarAjustes(contexto).idiomaSegunSistema)
    }

    @Test
    fun `guardar una segunda vez reemplaza, no acumula, los idiomas aprendidos`() {
        val contexto = contexto()
        guardarAjustes(contexto, Ajustes(idiomasAprendidos = mapOf(Idioma.DE to Nivel.B2)))
        guardarAjustes(contexto, Ajustes(idiomasAprendidos = mapOf(Idioma.EN to Nivel.C1)))
        assertEquals(mapOf(Idioma.EN to Nivel.C1), cargarAjustes(contexto).idiomasAprendidos)
    }

    @Test
    fun `migracion- el esquema viejo idioma_aprendido y nivel sueltos se convierte a un mapa de un elemento`() {
        val contexto = contexto()
        contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("idioma_aprendido", "EN")
            .putString("nivel", "C1")
            .apply()

        val ajustes = cargarAjustes(contexto)

        assertEquals(mapOf(Idioma.EN to Nivel.C1), ajustes.idiomasAprendidos)
    }

    @Test
    fun `migracion- se reescribe de una, la proxima carga no depende de las claves viejas`() {
        val contexto = contexto()
        contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("idioma_aprendido", "EN")
            .putString("nivel", "C1")
            .apply()

        cargarAjustes(contexto) // dispara la migracion y reescribe

        val prefs = contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        assertNull(prefs.getString("idioma_aprendido", null))
        assertNull(prefs.getString("nivel", null))
        assertEquals(mapOf(Idioma.EN to Nivel.C1), cargarAjustes(contexto).idiomasAprendidos)
    }

    @Test
    fun `guardar y cargar preserva familiaTema y modoTema, es preferencia igual que el idioma`() {
        val contexto = contexto()
        val ajustes = Ajustes(familiaTema = FamiliaTema.TERRACOTA, modoTema = ModoTema.OSCURO)
        guardarAjustes(contexto, ajustes)
        assertEquals(ajustes, cargarAjustes(contexto))
    }

    @Test
    fun `migracion- el esquema viejo sin claves de tema cae a Papel y Segun el sistema`() {
        val contexto = contexto()
        contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("idioma_aprendido", "DE")
            .apply()

        val ajustes = cargarAjustes(contexto)

        assertEquals(FamiliaTema.PAPEL, ajustes.familiaTema)
        assertEquals(ModoTema.SEGUN_SISTEMA, ajustes.modoTema)
    }

    @Test
    fun `migracion- esquema viejo con idioma pero sin nivel cae a B2`() {
        val contexto = contexto()
        contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("idioma_aprendido", "DE")
            .apply()

        assertEquals(mapOf(Idioma.DE to Nivel.B2), cargarAjustes(contexto).idiomasAprendidos)
    }

    // --- Ronda G: migracion del tema guardado ---

    private fun conTemaGuardado(valor: String?): Ajustes {
        val contexto = contexto()
        val prefs = contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        // un ajuste completo, con otros valores no por defecto: la migracion no debe tocar nada mas
        guardarAjustes(contexto, Ajustes(idiomaBase = Idioma.EN, idiomaInterfaz = Idioma.DE, modoTema = ModoTema.OSCURO, idiomasAprendidos = mapOf(Idioma.EN to Nivel.C1)))
        val editor = prefs.edit()
        if (valor == null) editor.remove("familia_tema") else editor.putString("familia_tema", valor)
        editor.apply()
        return cargarAjustes(contexto)
    }

    @Test
    fun `migracion de tema- ACADEMIA pasa a PAPEL y EDITORIAL a MAR`() {
        assertEquals(FamiliaTema.PAPEL, conTemaGuardado("ACADEMIA").familiaTema)
        assertEquals(FamiliaTema.MAR, conTemaGuardado("EDITORIAL").familiaTema)
    }

    @Test
    fun `migracion de tema- un valor desconocido o ausente cae a PAPEL, los nuevos se conservan`() {
        assertEquals(FamiliaTema.PAPEL, conTemaGuardado("NO_EXISTE").familiaTema)
        assertEquals(FamiliaTema.PAPEL, conTemaGuardado("").familiaTema)
        assertEquals(FamiliaTema.PAPEL, conTemaGuardado(null).familiaTema)
        for (f in FamiliaTema.entries) assertEquals(f, conTemaGuardado(f.name).familiaTema)
    }

    @Test
    fun `migracion de tema- no se pierde ningun otro ajuste`() {
        val a = conTemaGuardado("EDITORIAL")
        assertEquals(Idioma.EN, a.idiomaBase)
        assertEquals(Idioma.DE, a.idiomaInterfaz)
        assertEquals(ModoTema.OSCURO, a.modoTema)
        assertEquals(mapOf(Idioma.EN to Nivel.C1), a.idiomasAprendidos)
    }
}
