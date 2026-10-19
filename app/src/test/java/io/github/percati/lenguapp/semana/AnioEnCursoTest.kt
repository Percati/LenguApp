package io.github.percati.lenguapp.semana

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/** El anio "en curso" del indice de habilidades sale de la fecha inicial de la app, en anios ISO (Ronda U, tarea 3). */
class AnioEnCursoTest {

    @Test
    fun `sale de la fecha inicial y no del reloj`() {
        assertEquals(2026, anioEnCursoDe(LocalDate.of(2026, 9, 7)))
        assertEquals(2030, anioEnCursoDe(LocalDate.of(2030, 6, 15)))
        assertEquals(1999, anioEnCursoDe(LocalDate.of(1999, 3, 3)))
    }

    @Test
    fun `es el anio ISO, no el de calendario, en la frontera de ano`() {
        // 1-ene-2027 (viernes) es la semana 53 de 2026; 31-dic-2029 (lunes) ya es la semana 1 de 2030
        assertEquals(2026, anioEnCursoDe(LocalDate.of(2027, 1, 1)))
        assertEquals(2030, anioEnCursoDe(LocalDate.of(2029, 12, 31)))
        assertEquals(2027, anioEnCursoDe(LocalDate.of(2027, 1, 4)))
    }
}
