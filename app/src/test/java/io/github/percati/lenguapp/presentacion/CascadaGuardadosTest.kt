package io.github.percati.lenguapp.presentacion

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.ItemGuardado
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.modelo.Prioridad
import io.github.percati.lenguapp.modelo.RedemittelItem
import io.github.percati.lenguapp.modelo.TextoBilingue
import io.github.percati.lenguapp.modelo.TipoGuardado
import io.github.percati.lenguapp.modelo.VocabularioItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Guardados (Ronda C, tarea 2): orden y filtros en cascada, sin Compose. */
class CascadaGuardadosTest {

    private fun guardado(
        id: Long,
        idioma: Idioma = Idioma.DE,
        nivel: Nivel = Nivel.B2,
        tipo: TipoGuardado = TipoGuardado.VOCABULARIO,
        texto: String,
    ) = ItemGuardado(
        id = id,
        idioma = idioma,
        nivel = nivel,
        tipo = tipo,
        texto = TextoBilingue(porIdioma = mapOf(idioma.name.lowercase() to texto, "es" to "trad-$texto")),
        skillIdOrigen = "DE-V01",
    )

    private fun itemBiblioteca(
        idioma: Idioma,
        tipo: TipoGuardado,
        texto: String,
        topics: Set<String> = emptySet(),
        categorias: Set<String> = emptySet(),
    ) = ItemBiblioteca(
        idioma = idioma,
        tipo = tipo,
        texto = texto,
        vocabulario = if (tipo == TipoGuardado.VOCABULARIO) VocabularioItem(item = texto, prioridad = Prioridad.NUCLEO, traducciones = emptyMap()) else null,
        redemittel = if (tipo == TipoGuardado.EXPRESION) RedemittelItem(expresion = texto, funcion = TextoBilingue(plano = "func"), traducciones = emptyMap()) else null,
        niveles = setOf(Nivel.B2),
        topics = topics,
        categorias = categorias,
        skillIdOrigen = "DE-V01",
        nivelOrigen = Nivel.B2,
    )

    // --- orden ---

    @Test
    fun `orden alfabetico en ambos sentidos usa el texto en idiomaBase`() {
        val a = guardado(1, texto = "zebra")
        val b = guardado(2, texto = "ancla")
        val lista = listOf(a, b)
        assertEquals(listOf(b, a).map { it.id }, lista.ordenar(CriterioOrdenGuardados.ALFABETICO, DireccionOrden.ASCENDENTE, Idioma.DE).map { it.id })
        assertEquals(listOf(a, b).map { it.id }, lista.ordenar(CriterioOrdenGuardados.ALFABETICO, DireccionOrden.DESCENDENTE, Idioma.DE).map { it.id })
    }

    @Test
    fun `orden por nivel en ambos sentidos`() {
        val bajo = guardado(1, nivel = Nivel.A2, texto = "a")
        val alto = guardado(2, nivel = Nivel.C1, texto = "b")
        val lista = listOf(alto, bajo)
        assertEquals(listOf(bajo, alto), lista.ordenar(CriterioOrdenGuardados.NIVEL, DireccionOrden.ASCENDENTE, Idioma.ES))
        assertEquals(listOf(alto, bajo), lista.ordenar(CriterioOrdenGuardados.NIVEL, DireccionOrden.DESCENDENTE, Idioma.ES))
    }

    @Test
    fun `orden por fecha usa el id de Room, default mas reciente primero`() {
        val viejo = guardado(1, texto = "a")
        val nuevo = guardado(2, texto = "b")
        val lista = listOf(viejo, nuevo)
        assertEquals(listOf(nuevo, viejo), lista.ordenar(CriterioOrdenGuardados.FECHA, DireccionOrden.DESCENDENTE, Idioma.ES))
        assertEquals(listOf(viejo, nuevo), lista.ordenar(CriterioOrdenGuardados.FECHA, DireccionOrden.ASCENDENTE, Idioma.ES))
    }

    @Test
    fun `orden por tipo en ambos sentidos`() {
        val voc = guardado(1, tipo = TipoGuardado.VOCABULARIO, texto = "a")
        val expr = guardado(2, tipo = TipoGuardado.EXPRESION, texto = "b")
        val lista = listOf(voc, expr)
        assertEquals(listOf(voc, expr), lista.ordenar(CriterioOrdenGuardados.TIPO, DireccionOrden.ASCENDENTE, Idioma.ES))
        assertEquals(listOf(expr, voc), lista.ordenar(CriterioOrdenGuardados.TIPO, DireccionOrden.DESCENDENTE, Idioma.ES))
    }

    // --- cascada ---

    @Test
    fun `topics y categorias se resuelven buscando el guardado en la Biblioteca por idioma, tipo y texto`() {
        val g = guardado(1, texto = "Tisch")
        val indice = listOf(itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01", "T02"))).indiceBiblioteca()
        assertEquals(setOf("T01", "T02"), g.topics(indice))
        assertEquals(emptySet<String>(), g.categorias(indice))
    }

    @Test
    fun `un guardado que ya no esta en el contenido embebido no tiene topics ni categorias, pero no rompe`() {
        val g = guardado(1, texto = "palabra-de-una-edicion-vieja")
        val indice = emptyMap<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>()
        assertTrue(g.topics(indice).isEmpty())
        assertTrue(g.categorias(indice).isEmpty())
    }

    @Test
    fun `filtrar por topic solo deja los guardados de vocabulario de ese topic`() {
        val conT01 = guardado(1, texto = "Tisch")
        val conT02 = guardado(2, texto = "Stuhl")
        val indice = listOf(
            itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01")),
            itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Stuhl", topics = setOf("T02")),
        ).indiceBiblioteca()
        val lista = listOf(conT01, conT02)
        val r = lista.filtrarCascada(FiltrosGuardados(topic = "T01"), indice)
        assertEquals(listOf(conT01), r)
    }

    @Test
    fun `filtrar por categoria solo deja las expresiones de esa categoria`() {
        val conCat = guardado(1, tipo = TipoGuardado.EXPRESION, texto = "danke")
        val sinCat = guardado(2, tipo = TipoGuardado.EXPRESION, texto = "bitte")
        val indice = listOf(
            itemBiblioteca(Idioma.DE, TipoGuardado.EXPRESION, "danke", categorias = setOf("Agradecer")),
            itemBiblioteca(Idioma.DE, TipoGuardado.EXPRESION, "bitte", categorias = emptySet()),
        ).indiceBiblioteca()
        val lista = listOf(conCat, sinCat)
        val r = lista.filtrarCascada(FiltrosGuardados(categoria = "Agradecer"), indice)
        assertEquals(listOf(conCat), r)
    }

    @Test
    fun `las opciones de una faceta se calculan sobre las demas, no sobre si misma, y traen el contador`() {
        val a2voc = guardado(1, nivel = Nivel.A2, tipo = TipoGuardado.VOCABULARIO, texto = "a")
        val b2voc = guardado(2, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO, texto = "b")
        val b2expr = guardado(3, nivel = Nivel.B2, tipo = TipoGuardado.EXPRESION, texto = "c")
        val lista = listOf(a2voc, b2voc, b2expr)
        val indice = emptyMap<Triple<Idioma, TipoGuardado, String>, ItemBiblioteca>()

        // Con tipo=VOCABULARIO activo, las opciones de NIVEL deben ignorar el
        // propio filtro de nivel pero SI tener en cuenta tipo: A2 y B2 deben
        // salir ambos (de vocabulario), nunca B2 solo por culpa del item EXPRESION.
        val filtros = FiltrosGuardados(tipo = TipoGuardado.VOCABULARIO)
        val opciones = lista.opcionesNivel(filtros, indice)
        assertEquals(mapOf(Nivel.A2 to 1, Nivel.B2 to 1), opciones)

        val opcionesTipo = lista.opcionesTipo(FiltrosGuardados(), indice)
        assertEquals(mapOf(TipoGuardado.VOCABULARIO to 2, TipoGuardado.EXPRESION to 1), opcionesTipo)
    }

    @Test
    fun `normalizarFiltros no toca una combinacion que sigue teniendo resultados`() {
        val conT01 = guardado(1, nivel = Nivel.A2, texto = "Tisch")
        val indice = listOf(itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01"))).indiceBiblioteca()
        val lista = listOf(conT01)

        val filtros = FiltrosGuardados(nivel = Nivel.A2, topic = "T01")
        assertEquals(filtros, lista.normalizarFiltros(filtros, indice))
    }

    @Test
    fun `normalizarFiltros vuelve al default entero si la combinacion quedo vacia (p ej el item cambio debajo)`() {
        val conT01 = guardado(1, nivel = Nivel.A2, texto = "Tisch")
        val indice = listOf(itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01"))).indiceBiblioteca()
        val lista = listOf(conT01)

        // Nivel B2 ya no tiene soporte (el unico guardado es A2): no hay forma no
        // ambigua de saber si el filtro viejo era nivel o topic, asi que vuelve
        // todo al default -- nunca una lista vacia por un filtro que quedo stale.
        val filtros = FiltrosGuardados(nivel = Nivel.B2, topic = "T01")
        val normalizado = lista.normalizarFiltros(filtros, indice)
        assertEquals(FiltrosGuardados(), normalizado)
        assertTrue(lista.filtrarCascada(normalizado, indice).isNotEmpty())
    }

    @Test
    fun `ninguna combinacion de filtros normalizados da una lista vacia, sobre un set variado`() {
        val items = listOf(
            guardado(1, nivel = Nivel.A2, tipo = TipoGuardado.VOCABULARIO, texto = "Tisch"),
            guardado(2, nivel = Nivel.B2, tipo = TipoGuardado.VOCABULARIO, texto = "Stuhl"),
            guardado(3, nivel = Nivel.B2, tipo = TipoGuardado.EXPRESION, texto = "danke"),
            guardado(4, nivel = Nivel.C1, tipo = TipoGuardado.EXPRESION, texto = "bitte"),
        )
        val indice = listOf(
            itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Tisch", topics = setOf("T01")),
            itemBiblioteca(Idioma.DE, TipoGuardado.VOCABULARIO, "Stuhl", topics = setOf("T02")),
            itemBiblioteca(Idioma.DE, TipoGuardado.EXPRESION, "danke", categorias = setOf("Agradecer")),
            itemBiblioteca(Idioma.DE, TipoGuardado.EXPRESION, "bitte", categorias = setOf("Pedir")),
        ).indiceBiblioteca()

        val niveles = listOf(null) + Nivel.entries
        val tipos = listOf(null) + TipoGuardado.entries
        val topics = listOf(null, "T01", "T02")
        val categorias = listOf(null, "Agradecer", "Pedir")
        for (n in niveles) for (t in tipos) for (top in topics) for (cat in categorias) {
            val crudo = FiltrosGuardados(tipo = t, nivel = n, topic = top, categoria = cat)
            val normalizado = items.normalizarFiltros(crudo, indice)
            assertTrue(
                "crudo=$crudo normalizado=$normalizado dio vacio",
                items.filtrarCascada(normalizado, indice).isNotEmpty(),
            )
        }
    }
}
