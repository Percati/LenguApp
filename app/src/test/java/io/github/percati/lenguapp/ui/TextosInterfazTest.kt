package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * AJUSTES-FASE-7.md, bloque 2.2: alcance acotado a proposito a estas
 * cuatro cadenas (Semana, Ajustes, Hoy, el aviso de doble atras), en los
 * seis idiomas soportados como idioma de interfaz. El resto del chrome
 * sigue diferido.
 */
class TextosInterfazTest {

    private val seis = Idioma.entries

    @Test
    fun `etiquetaSemana tiene las seis traducciones del documento`() {
        assertEquals("Semana", etiquetaSemana(Idioma.ES))
        assertEquals("Week", etiquetaSemana(Idioma.EN))
        assertEquals("Woche", etiquetaSemana(Idioma.DE))
        assertEquals("Semaine", etiquetaSemana(Idioma.FR))
        assertEquals("Settimana", etiquetaSemana(Idioma.IT))
        assertEquals("Semana", etiquetaSemana(Idioma.PT))
    }

    @Test
    fun `etiquetaAjustes tiene las seis traducciones del documento`() {
        assertEquals("Ajustes", etiquetaAjustes(Idioma.ES))
        assertEquals("Settings", etiquetaAjustes(Idioma.EN))
        assertEquals("Einstellungen", etiquetaAjustes(Idioma.DE))
        assertEquals("Paramètres", etiquetaAjustes(Idioma.FR))
        assertEquals("Impostazioni", etiquetaAjustes(Idioma.IT))
        assertEquals("Configurações", etiquetaAjustes(Idioma.PT))
    }

    @Test
    fun `etiquetaHoy tiene las seis traducciones del documento`() {
        assertEquals("Hoy", etiquetaHoy(Idioma.ES))
        assertEquals("Today", etiquetaHoy(Idioma.EN))
        assertEquals("Heute", etiquetaHoy(Idioma.DE))
        assertEquals("Aujourd'hui", etiquetaHoy(Idioma.FR))
        assertEquals("Oggi", etiquetaHoy(Idioma.IT))
        assertEquals("Hoje", etiquetaHoy(Idioma.PT))
    }

    @Test
    fun `los seis idiomas dan un mensaje de doble atras distinto entre si`() {
        val mensajes = seis.map { mensajeDobleAtrasParaSalir(it) }
        assertEquals(seis.size, mensajes.toSet().size)
        mensajes.forEach { assertNotEquals("", it) }
    }

    @Test
    fun `mensajeIdiomaNoDisponible sigue cubriendo los seis idiomas (Fase 6)`() {
        seis.forEach { idioma -> assertNotEquals("", mensajeIdiomaNoDisponible(idioma)) }
    }
}
