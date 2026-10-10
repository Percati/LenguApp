package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.datos.parsearPlanillaTextos
import io.github.percati.lenguapp.modelo.Idioma
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Registro y variante de los textos de interfaz (Ronda F, tarea 3): portugues de BRASIL
 * (la voz y el contenido son pt_BR) y trato informal en los 6 idiomas. Recorre las tablas
 * crudas de TextosInterfaz y EtiquetasSeccion y los textos de la planilla en portugues.
 */
class RegistroTextosTest {

    private fun cadenas(idioma: Idioma): List<Pair<String, String>> =
        tablaChromeCruda().flatMap { (clave, porIdioma) -> porIdioma[idioma]?.let { listOf("${clave.name}" to it) }.orEmpty() } +
            etiquetasSeccionCrudas().flatMap { (clave, porIdioma) -> porIdioma[idioma]?.let { listOf("seccion.$clave" to it) }.orEmpty() }

    private fun planillaPt(): List<Pair<String, String>> {
        val carpeta = listOf(File("src/main/assets"), File("app/src/main/assets")).first { it.isDirectory }
        val t = parsearPlanillaTextos(File(carpeta, "plantillas/planilla-profesor.json").readText()).getValue(Idioma.PT)
        return listOf(
            "titulo" to t.titulo, "objetivo" to t.objetivo, "enfoque" to t.enfoque, "gramatica" to t.gramatica,
            "expresionesObjetivo" to t.expresionesObjetivo, "expresionesReparacion" to t.expresionesReparacion,
            "situaciones" to t.situaciones, "situacionCotidiana" to t.situacionCotidiana,
            "situacionProfesional" to t.situacionProfesional, "giro" to t.giro, "giroTexto" to t.giroTexto,
            "criterios" to t.criterios, "recordatorio" to t.recordatorio,
        ) + t.frasesReparacion.map { "frase" to it } + t.criteriosLista.map { "criterio" to it }
    }

    // Lista corta de formas del portugues europeo (o de Portugal) que no van en una app de Brasil.
    private val europeo = Regex(
        """telem[óo]vel|\ba (aprender|estudar|falar|ler)\b|oferece-lhe|\bnuma\b|\bnum\b|\ba meio\b|percebe-se|-o\b|""" +
            """\bpartilh|\becr[ãa]\b|ficheiro|quotidian|por agora|\bAjustes\b|\bGuardados?\b|aplica[çc][ãa]o|""" +
            """\bconsigo\b|\bpara a guardar\b|\bensina-?me\b|a sua conta|\bpesquis""",
        RegexOption.IGNORE_CASE,
    )

    @Test
    fun `ninguna cadena en portugues usa formas de Portugal`() {
        val malas = (cadenas(Idioma.PT) + planillaPt()).filter { (_, texto) -> europeo.containsMatchIn(texto) }
        assertEquals("cadenas PT con portugues europeo: $malas", emptyList<Pair<String, String>>(), malas)
    }

    @Test
    fun `el portugues usa los terminos del Brasil que se eligieron`() {
        assertEquals("Configurações", etiquetaAjustes(Idioma.PT))
        assertEquals("Salvos", etiquetaGuardados(Idioma.PT))
        assertTrue(mensajeSinIdioma(Idioma.PT).contains("Configurações"))
        assertTrue(textoAcercaPrivacidad(Idioma.PT).contains("celular"))
        assertTrue(textoAcercaQueEs(Idioma.PT).contains("você"))
        assertTrue(reglasReto(Idioma.PT, io.github.percati.lenguapp.modelo.Nivel.C1, 6).joinToString(" ").contains("Uma gravação de"))
    }

    @Test
    fun `el frances tutea, sin formas de vous en la interfaz`() {
        val formal = Regex("""\b(vous|votre|vos)\b|-vous\b|\b(Appuyez|Choisissez|Collez|Dites|[ÉE]coutez|Enregistrez|Changez|Recommencez|Notez)\b""", RegexOption.IGNORE_CASE)
        val malas = cadenas(Idioma.FR).filter { (_, texto) -> formal.containsMatchIn(texto) }
        assertEquals("cadenas FR de usted: $malas", emptyList<Pair<String, String>>(), malas)
    }

    // --- Acerca de / privacidad coherente con PRIVACY.md (Ronda F, tarea 4) ---

    @Test
    fun `el texto de privacidad dice lo mismo que PRIVACY md, en los 6 idiomas`() {
        // fecha, idioma del sistema solo si lo elige el usuario, sin Internet ni cuenta, y se guardan ajustes y estrellas
        val claves = mapOf(
            Idioma.ES to listOf("Sin Internet", "fecha", "únicamente si lo eliges", "ajustes", "estrella"),
            Idioma.EN to listOf("No internet", "date", "only if you choose", "settings", "star"),
            Idioma.DE to listOf("Kein Internet", "Datum", "nur wenn du", "Einstellungen", "Stern"),
            Idioma.FR to listOf("Pas d'Internet", "date", "uniquement si tu", "paramètres", "étoile"),
            Idioma.IT to listOf("Niente Internet", "data", "solo se lo scegli", "impostazioni", "stella"),
            Idioma.PT to listOf("Sem internet", "data", "somente se você", "configurações", "estrela"),
        )
        for ((idioma, palabras) in claves) {
            val texto = textoAcercaPrivacidad(idioma)
            for (p in palabras) assertTrue("$idioma: falta '$p' en: $texto", texto.contains(p, ignoreCase = true))
        }
        val raiz = listOf(File("../PRIVACY.md"), File("PRIVACY.md")).first { it.isFile }.readText()
        assertTrue(raiz.contains("INTERNET"))
        assertTrue(raiz.contains("idioma del sistema"))
        assertTrue(raiz.contains("estrella"))
        assertTrue(raiz.contains("ajustes", ignoreCase = true))
    }

    @Test
    fun `los otros idiomas tambien tutean en la interfaz`() {
        val formal = mapOf(
            Idioma.ES to Regex("""\busted(es)?\b|\b(Pulsá|Tocá|Pegalo|Decile|aprendés|[Ee]legí|[Pp]egá|tenés|querés|podés|vos)\b""", RegexOption.IGNORE_CASE),
            Idioma.IT to Regex("""\bLei\b|\bLa sua\b"""),
            Idioma.DE to Regex("""\b(Ihnen|Ihr|Ihre|Ihren)\b|\b(Wählen|Drücken|Geben|Tippen) Sie\b"""),
            Idioma.EN to Regex("""\byour honou?r\b""", RegexOption.IGNORE_CASE),
        )
        for ((idioma, patron) in formal) {
            val malas = cadenas(idioma).filter { (_, texto) -> patron.containsMatchIn(texto) }
            assertEquals("cadenas $idioma de usted: $malas", emptyList<Pair<String, String>>(), malas)
        }
    }
}
