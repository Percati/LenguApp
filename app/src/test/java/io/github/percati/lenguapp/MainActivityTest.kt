package io.github.percati.lenguapp

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityTest {

    @Test
    fun `cambiar a EN elige C1, el unico nivel con contenido en ingles`() {
        assertEquals(Nivel.C1, nivelConContenidoPara(Idioma.EN))
    }

    @Test
    fun `cambiar a DE elige B2, el unico nivel con contenido en aleman`() {
        assertEquals(Nivel.B2, nivelConContenidoPara(Idioma.DE))
    }
}
