package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Exportacion a Anki (Ronda F2, tarea 6): TSV UTF-8 con cabeceras, anverso/reverso/etiquetas. */
class ExportarAnkiTest {

    private fun item(id: Long, idioma: Idioma, nivel: Nivel, tipo: TipoGuardado, texto: TextoBilingue, funcion: TextoBilingue? = null) =
        ItemGuardado(id = id, idioma = idioma, nivel = nivel, tipo = tipo, texto = texto, funcion = funcion, skillIdOrigen = "X-V01")

    private fun lineas(tsv: String) = tsv.trimEnd('\n').split('\n')

    @Test
    fun `lleva las cabeceras de Anki y una fila por item, del mas viejo al mas nuevo`() {
        val a = item(2, Idioma.DE, Nivel.B2, TipoGuardado.VOCABULARIO, TextoBilingue(porIdioma = mapOf("de" to "die Miete", "es" to "el alquiler")))
        val b = item(1, Idioma.EN, Nivel.C1, TipoGuardado.VOCABULARIO, TextoBilingue(porIdioma = mapOf("en" to "the lease", "es" to "el contrato de alquiler")))
        val l = lineas(exportarAnkiTsv(listOf(a, b), Idioma.ES))
        assertEquals(listOf("#separator:tab", "#html:false", "#tags column:3"), l.take(3))
        assertEquals(listOf("the lease\tel contrato de alquiler\ten C1", "die Miete\tel alquiler\tde B2"), l.drop(3))
    }

    @Test
    fun `el reverso es la traduccion al idioma de app y, si existe, el contexto en el idioma aprendido`() {
        val expresion = item(
            1, Idioma.EN, Nivel.B2, TipoGuardado.EXPRESION,
            TextoBilingue(porIdioma = mapOf("en" to "I'd say", "es" to "yo diría", "de" to "ich würde sagen")),
            TextoBilingue(porIdioma = mapOf("en" to "softening an opinion", "es" to "suavizar una opinión")),
        )
        assertEquals("I'd say\tyo diría — softening an opinion\ten B2", lineas(exportarAnkiTsv(listOf(expresion), Idioma.ES)).last())
        assertEquals("I'd say\tich würde sagen — softening an opinion\ten B2", lineas(exportarAnkiTsv(listOf(expresion), Idioma.DE)).last())
    }

    @Test
    fun `sin traduccion o sin contexto no se repite el original ni queda un separador suelto`() {
        val sinTraduccion = item(1, Idioma.DE, Nivel.A2, TipoGuardado.VOCABULARIO, TextoBilingue(porIdioma = mapOf("de" to "sowieso")))
        assertEquals("sowieso\t\tde A2", lineas(exportarAnkiTsv(listOf(sinTraduccion), Idioma.ES)).last())
        val soloContexto = item(2, Idioma.DE, Nivel.A2, TipoGuardado.EXPRESION, TextoBilingue(porIdioma = mapOf("de" to "na ja")), TextoBilingue(plano = "Zögern ausdrücken"))
        assertEquals("na ja\tZögern ausdrücken\tde A2", lineas(exportarAnkiTsv(listOf(soloContexto), Idioma.ES)).last())
    }

    @Test
    fun `los tabuladores y saltos de linea dentro del texto no rompen columnas ni filas`() {
        val raro = item(
            1, Idioma.EN, Nivel.B2, TipoGuardado.EXPRESION,
            TextoBilingue(porIdioma = mapOf("en" to "first\tsecond\nthird", "es" to "uno\tdos\r\ntres")),
            TextoBilingue(plano = "linea1\nlinea2\tcol"),
        )
        val l = lineas(exportarAnkiTsv(listOf(raro), Idioma.ES))
        assertEquals("exactamente 3 cabeceras + 1 fila", 4, l.size)
        val columnas = l.last().split('\t')
        assertEquals(3, columnas.size)
        assertEquals("first second third", columnas[0])
        assertEquals("uno dos tres — linea1 linea2 col", columnas[1])
        assertEquals("en B2", columnas[2])
    }

    @Test
    fun `un campo con comillas se encierra entre comillas dobladas`() {
        assertEquals("\"say \"\"hi\"\"\"", campoTsv("say \"hi\""))
        assertEquals("sin comillas", campoTsv("sin comillas"))
        assertEquals("a b", campoTsv("a \t\t b"))
    }

    @Test
    fun `sin guardados solo quedan las cabeceras, y termina en salto de linea`() {
        val tsv = exportarAnkiTsv(emptyList(), Idioma.ES)
        assertEquals(3, lineas(tsv).size)
        assertTrue(tsv.endsWith("\n"))
        assertEquals("lenguapp-guardados.tsv", NOMBRE_ARCHIVO_ANKI)
    }
}
