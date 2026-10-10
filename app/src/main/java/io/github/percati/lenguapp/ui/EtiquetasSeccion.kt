package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Dia
import io.github.percati.lenguapp.modelo.Idioma

/**
 * Titulos de seccion de la ficha, en los SEIS idiomas soportados (antes eran
 * dos mapas fijos -- ingles/aleman por la ficha y un espanol concatenado
 * para las bilingues -- que nunca resolvian DE/IT/FR/PT y dejaban
 * "Redemittel" tambien en espanol). Mismo patron que TIPO_VOCABULARIO /
 * TIPO_EXPRESION en TextosInterfaz.kt: una tabla clave -> idioma -> texto.
 *
 * Que idioma se pide lo decide quien llama: una ficha bilingue (A2/B1) usa
 * el idioma que el switch de traduccion esta mostrando; una no bilingue, el
 * idioma que se aprende (los titulos siguen el idioma de la ficha, no el
 * de la interfaz -- proyecto/AJUSTES-FASE-5.md, B1).
 *
 * "contraste" es un caso aparte: es el titulo de la seccion de contraste
 * cuando se muestra TRADUCIDA, y nombra al idioma de app en el propio idioma
 * de app ("Contraste con el espanol"). Cuando se muestra en el idioma que se
 * aprende el titulo nombra al idioma BASE desde afuera -- ver
 * [etiquetaContraste].
 */
private val ETIQUETAS: Map<String, Map<Idioma, String>> = mapOf(
    "descripcion" to mapOf(
        Idioma.ES to "Descripción", Idioma.EN to "Description", Idioma.DE to "Beschreibung",
        Idioma.FR to "Description", Idioma.IT to "Descrizione", Idioma.PT to "Descrição",
    ),
    "cuadroReferencia" to mapOf(
        Idioma.ES to "Cuadro de referencia", Idioma.EN to "Reference", Idioma.DE to "Übersichtskasten",
        Idioma.FR to "Tableau de référence", Idioma.IT to "Quadro di riferimento", Idioma.PT to "Quadro de referência",
    ),
    "ejemplos" to mapOf(
        Idioma.ES to "Ejemplos", Idioma.EN to "Examples", Idioma.DE to "Beispiele",
        Idioma.FR to "Exemples", Idioma.IT to "Esempi", Idioma.PT to "Exemplos",
    ),
    "notas" to mapOf(
        Idioma.ES to "Notas", Idioma.EN to "Notes", Idioma.DE to "Hinweise",
        Idioma.FR to "Notes", Idioma.IT to "Note", Idioma.PT to "Notas",
    ),
    "errores" to mapOf(
        Idioma.ES to "Errores comunes", Idioma.EN to "Common mistakes", Idioma.DE to "Typische Fehler",
        Idioma.FR to "Erreurs fréquentes", Idioma.IT to "Errori comuni", Idioma.PT to "Erros comuns",
    ),
    "vocabulario" to mapOf(
        Idioma.ES to "Vocabulario", Idioma.EN to "Vocabulary", Idioma.DE to "Themenwortschatz",
        Idioma.FR to "Vocabulaire", Idioma.IT to "Lessico", Idioma.PT to "Vocabulário",
    ),
    "redemittel" to mapOf(
        Idioma.ES to "Expresiones", Idioma.EN to "Expressions", Idioma.DE to "Redemittel",
        Idioma.FR to "Expressions", Idioma.IT to "Espressioni", Idioma.PT to "Expressões",
    ),
    "mision" to mapOf(
        Idioma.ES to "Misión", Idioma.EN to "Mission", Idioma.DE to "Wochenaufgabe",
        Idioma.FR to "Mission", Idioma.IT to "Missione", Idioma.PT to "Missão",
    ),
    // El desafio de fin de semana reusa la mision con otro encabezado: no es
    // contenido nuevo, asi que no tiene fila propia en las traducciones
    // resueltas de AJUSTES-FASE-5.md.
    "desafio" to mapOf(
        Idioma.ES to "Desafío de fin de semana", Idioma.EN to "Weekend Challenge", Idioma.DE to "Wochenend-Herausforderung",
        Idioma.FR to "Défi du week-end", Idioma.IT to "Sfida del weekend", Idioma.PT to "Desafio de fim de semana",
    ),
    // No esta en la tabla de AJUSTES-FASE-5.md (esa cubre solo la ficha):
    // SemanaEspecial tiene su propia seccion "requisitos", sin "mision".
    "requisitos" to mapOf(
        Idioma.ES to "Requisitos", Idioma.EN to "Requirements", Idioma.DE to "Anforderungen",
        Idioma.FR to "Exigences", Idioma.IT to "Requisiti", Idioma.PT to "Requisitos",
    ),
    "microtareas" to mapOf(
        Idioma.ES to "Micro-tareas", Idioma.EN to "Micro-tasks", Idioma.DE to "Mikroaufgaben",
        Idioma.FR to "Micro-tâches", Idioma.IT to "Micro-attività", Idioma.PT to "Microtarefas",
    ),
    "autochequeo" to mapOf(
        Idioma.ES to "Autochequeo", Idioma.EN to "Self-check", Idioma.DE to "Selbstkontrolle",
        Idioma.FR to "Auto-évaluation", Idioma.IT to "Autoverifica", Idioma.PT to "Autoavaliação",
    ),
    "promptCorreccion" to mapOf(
        Idioma.ES to "Prompt de corrección", Idioma.EN to "Correction prompt", Idioma.DE to "Korrekturprompt",
        Idioma.FR to "Prompt de correction", Idioma.IT to "Prompt di correzione", Idioma.PT to "Prompt de correção",
    ),
    "contraste" to mapOf(
        Idioma.ES to "Contraste con el español", Idioma.EN to "Contrast with English", Idioma.DE to "Kontrast zum Deutschen",
        Idioma.FR to "Contraste avec le français", Idioma.IT to "Contrasto con l'italiano", Idioma.PT to "Contraste com o português",
    ),
)

/** Visibilidad de modulo solo para EtiquetasSeccionTest: recorre la tabla cruda, sin el fallback a la clave de [etiquetaSeccion]. */
internal fun tablaEtiquetasSeccionCruda(): Map<String, Map<Idioma, String>> = ETIQUETAS

/** Titulo de seccion `clave` en `idioma`; una clave desconocida se devuelve tal cual (no rompe la pantalla). */
fun etiquetaSeccion(clave: String, idioma: Idioma): String = ETIQUETAS[clave]?.get(idioma) ?: clave

/** Solo para los tests de cobertura/registro: la tabla cruda, sin fallback. */
internal fun etiquetasSeccionCrudas(): Map<String, Map<Idioma, String>> = ETIQUETAS

/**
 * Nombres de idioma para armar "Kontrast zum Spanischen" / "Contrast with
 * Spanish" con el idioma base real del usuario, no fijo a español --
 * AJUSTES-FASE-5.md, B1.
 */
private val NOMBRE_IDIOMA_EN = mapOf(
    Idioma.ES to "Spanish", Idioma.EN to "English", Idioma.DE to "German",
    Idioma.FR to "French", Idioma.IT to "Italian", Idioma.PT to "Portuguese",
)

private val NOMBRE_IDIOMA_DE_DATIVO = mapOf(
    Idioma.ES to "Spanischen", Idioma.EN to "Englischen", Idioma.DE to "Deutschen",
    Idioma.FR to "Französischen", Idioma.IT to "Italienischen", Idioma.PT to "Portugiesischen",
)

fun etiquetaContraste(idioma: Idioma, idiomaBase: Idioma): String = when (idioma) {
    Idioma.DE -> "Kontrast zum ${NOMBRE_IDIOMA_DE_DATIVO[idiomaBase] ?: idiomaBase.name}"
    else -> "Contrast with ${NOMBRE_IDIOMA_EN[idiomaBase] ?: idiomaBase.name}"
}

/**
 * `microtareas[].dia` es una clave interna (lun/mie/vie), no texto para
 * mostrar. Va en el idioma que se aprende, igual que los titulos de
 * seccion -- AJUSTES-FASE-7.md, bloque 2.3. Tabla escrita a mano, no
 * derivada de `Locale`: los abreviados del sistema no coinciden con la
 * convencion de los manuales (*Mo/Mi/Fr* en aleman, no *Mon/Wed/Fri*), y
 * son justo los que el usuario reconoce del material impreso.
 */
private val ETIQUETAS_DIA: Map<Idioma, Map<Dia, String>> = mapOf(
    Idioma.EN to mapOf(Dia.LUN to "Mon", Dia.MIE to "Wed", Dia.VIE to "Fri"),
    Idioma.DE to mapOf(Dia.LUN to "Mo", Dia.MIE to "Mi", Dia.VIE to "Fr"),
    Idioma.ES to mapOf(Dia.LUN to "lun", Dia.MIE to "mié", Dia.VIE to "vie"),
    Idioma.FR to mapOf(Dia.LUN to "lun", Dia.MIE to "mer", Dia.VIE to "ven"),
    Idioma.IT to mapOf(Dia.LUN to "lun", Dia.MIE to "mer", Dia.VIE to "ven"),
    Idioma.PT to mapOf(Dia.LUN to "seg", Dia.MIE to "qua", Dia.VIE to "sex"),
)

fun etiquetaDia(dia: Dia, idioma: Idioma): String =
    ETIQUETAS_DIA[idioma]?.get(dia) ?: ETIQUETAS_DIA.getValue(Idioma.ES).getValue(dia)
