package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel

/**
 * Traduccion completa del *chrome* de la interfaz a los seis idiomas
 * soportados como idioma de interfaz -- AJUSTES-FASE-9.md, bloque B. Se
 * levanta el diferimiento de AJUSTES-FASE-7.md (bloque 2.2), que habia
 * acotado esto a "Semana", "Ajustes", "Hoy" y el aviso de doble atras.
 *
 * A diferencia de EtiquetasSeccion.kt (indexada por el idioma de la ficha
 * que se esta leyendo), esto indexa por los 6 idiomas soportados como
 * idioma de interfaz.
 *
 * `TABLA_CHROME` es la tabla resuelta del documento, palabra por palabra:
 * ninguna cadena de aca se inventa. `TextosInterfazTest.kt` la recorre
 * entera para detectar un idioma sin traducir en alguna clave -- con 6
 * idiomas x ~30 claves el olvido silencioso es el modo de fallo esperable,
 * y ya paso dos veces en uso.
 *
 * Los textos con `{marca}` son plantillas con marcas con nombre, nunca
 * `String.format` posicional: el orden de los elementos cambia entre
 * idiomas (en aleman el nivel va antes del verbo final). `interpolar()`
 * reemplaza por nombre.
 */
enum class ClaveTexto {
    SEMANA, HOY, AJUSTES, VOLVER,
    SIN_CONTENIDO_TITULO, SIN_CONTENIDO_NIVEL, SIN_CONTENIDO_SEMANA, SIN_CALENDARIO,
    SALIR_CONFIRMAR,
    IDIOMAS_APRENDIDOS, IDIOMA_APP, SEGUN_SISTEMA, ESTILO_VISUAL, MODO, MODO_CLARO, MODO_OSCURO,
    NIVEL, CAMBIAR_NIVEL, IDIOMA_NO_DISPONIBLE, AVISO_GLOSAS,
    GUARDADOS, GUARDADOS_VACIO, FILTRO_IDIOMA, FILTRO_TIPO, FILTRO_TODOS,
    TIPO_VOCABULARIO, TIPO_EXPRESION, DESAFIO_FINDE, TRADUCIR_A,
    SEMANA_REPASO, SEMANA_SURVIVAL, COPIAR_PORTAPAPELES, COPIADO, PROMPT_VOZ_TITULO, PROMPT_VOZ_AYUDA, PLANILLA_BOTON, PLANILLA_SIN_LECTOR,
    BIBLIOTECA, BIBLIOTECA_BUSCAR, BIBLIOTECA_TEMA, BIBLIOTECA_SIN_CATEGORIA, BIBLIOTECA_FUNCIONES, BIBLIOTECA_PATRONES, BIBLIOTECA_VACIO,
    QUITAR_FILTROS, QUITAR,
    ORDENAR_POR, ORDEN_ALFABETICO, ORDEN_FECHA, ASCENDENTE, DESCENDENTE, QUITAR_ESTRELLA,
    SIN_IDIOMA_TITULO, SIN_IDIOMA_TEXTO, SIN_IDIOMA_WIDGET,
    RETO_CONSIGNA_LINEA, RETO_REGLAS_BASICO, RETO_REGLAS_AVANZADO, RETO_HABILIDAD, RETO_CONDICIONES, RETO_FRASES_TITULO, RETO_FRASES_AYUDA,
    ACERCA_COMO_TITULO, ACERCA_COMO_TEXTO, ACERCA_CREDITOS_TEXTO, ACERCA_PRUEBA,
    GUARDADOS_EXPORTAR, GUARDADOS_IMPORTAR, GUARDADOS_EXPORTAR_ANKI, GUARDADOS_EXPORTAR_RESPALDO, GUARDADOS_MSG_EXPORTADO, GUARDADOS_MSG_IMPORTADO, GUARDADOS_ERR_INVALIDO, GUARDADOS_ERR_GRANDE, GUARDADOS_ERR_VERSION, GUARDADOS_ERR_ARCHIVO,
    BIBLIOTECA_VISTA_LEXICO, BIBLIOTECA_VISTA_HABILIDADES, HABILIDAD_SEMANAS,
    TEMA_PAPEL, TEMA_TERRACOTA, TEMA_MAR, TEMA_GRAFITO,
    FUENTE_TITULO, FUENTE_LECTURA, FUENTE_CLARA, ACERCA_FUENTES_TEXTO,
    BIBLIOTECA_VISTA_VERBOS, VERBO_REFLEXIVO, VERBO_TRENNBAR, VERBO_MIT_PRAPOSITION, VERBO_MIT_DATIV, VERBO_PHRASAL_SEPARABLE, VERBO_PHRASAL_INSEPARABLE, VERBO_VERBO_PREPOSICION, VERBOS_VACIO,
    VERBOS_SIN_RESULTADOS, BUSCAR_BORRAR,
    SECCION_CONTRAER, SECCION_EXPANDIR, AUDIO_ESCUCHAR, AUDIO_DETENER,
    TOUR_FICHA_1_T, TOUR_FICHA_1_C, TOUR_FICHA_6_T, TOUR_FICHA_6_C, TOUR_FICHA_3_T, TOUR_FICHA_3_C, TOUR_FICHA_4_T, TOUR_FICHA_4_C, TOUR_FICHA_5_T, TOUR_FICHA_5_C, TOUR_FICHA_7_T, TOUR_FICHA_7_C, TOUR_FICHA_8_T, TOUR_FICHA_8_C, TOUR_FICHA_10_T, TOUR_FICHA_10_C, TOUR_BIBLIOTECA_1_T, TOUR_BIBLIOTECA_1_C, TOUR_BIBLIOTECA_2_T, TOUR_BIBLIOTECA_2_C, TOUR_BIBLIOTECA_3_T, TOUR_BIBLIOTECA_3_C, TOUR_BIBLIOTECA_4_T, TOUR_BIBLIOTECA_4_C, TOUR_BIBLIOTECA_5_T, TOUR_BIBLIOTECA_5_C, TOUR_GUARDADOS_1_T, TOUR_GUARDADOS_1_C, TOUR_GUARDADOS_2_T, TOUR_GUARDADOS_2_C, TOUR_GUARDADOS_3_T, TOUR_GUARDADOS_3_C, TOUR_AJUSTES_1_T, TOUR_AJUSTES_1_C, TOUR_AJUSTES_2_T, TOUR_AJUSTES_2_C, TOUR_AJUSTES_3_T, TOUR_AJUSTES_3_C, TOUR_AJUSTES_4_T, TOUR_AJUSTES_4_C, TOUR_DESAFIO_1_T, TOUR_DESAFIO_1_C, TOUR_DESAFIO_2_T, TOUR_DESAFIO_2_C,
    TOUR_FICHA_2_T, TOUR_FICHA_2_C, TOUR_FICHA_9_T, TOUR_FICHA_9_C,
    TOUR_ATRAS, TOUR_ATRAS_CD, TOUR_SIGUIENTE, TOUR_SALTAR, TOUR_LISTO, TOUR_PASO, TOUR_SIGUIENTE_CD, TOUR_SALTAR_CD, TOUR_LISTO_CD,
    VELOCIDAD_AUDIO_TITULO, VELOCIDAD_AUDIO_AYUDA, VELOCIDAD_AUDIO_VALOR,
    BIENVENIDA_TITULO, BIENVENIDA_IDIOMA_INICIAL, BIENVENIDA_ELEGIR_PRACTICA, BIENVENIDA_EMPEZAR, BIENVENIDA_VER_DE_NUEVO,
    ACERCA_DE, LINEA_FIJA, ACERCA_QUE_ES_TITULO, ACERCA_QUE_ES_TEXTO, ACERCA_PRIVACIDAD_TITULO, ACERCA_PRIVACIDAD_TEXTO, ACERCA_LICENCIAS_TITULO, ACERCA_LICENCIAS_TEXTO, ACERCA_VERSION,
}

private val TABLA_CHROME: Map<ClaveTexto, Map<Idioma, String>> = mapOf(
    ClaveTexto.SEMANA to mapOf(
        Idioma.ES to "Semana", Idioma.EN to "Week", Idioma.DE to "Woche",
        Idioma.FR to "Semaine", Idioma.IT to "Settimana", Idioma.PT to "Semana",
    ),
    ClaveTexto.HOY to mapOf(
        Idioma.ES to "Hoy", Idioma.EN to "Today", Idioma.DE to "Heute",
        Idioma.FR to "Aujourd'hui", Idioma.IT to "Oggi", Idioma.PT to "Hoje",
    ),
    ClaveTexto.AJUSTES to mapOf(
        Idioma.ES to "Ajustes", Idioma.EN to "Settings", Idioma.DE to "Einstellungen",
        Idioma.FR to "Paramètres", Idioma.IT to "Impostazioni", Idioma.PT to "Configurações",
    ),
    ClaveTexto.VOLVER to mapOf(
        Idioma.ES to "Volver", Idioma.EN to "Back", Idioma.DE to "Zurück",
        Idioma.FR to "Retour", Idioma.IT to "Indietro", Idioma.PT to "Voltar",
    ),
    ClaveTexto.SIN_CONTENIDO_TITULO to mapOf(
        Idioma.ES to "Sin contenido esta semana",
        Idioma.EN to "No content this week",
        Idioma.DE to "Diese Woche kein Inhalt",
        Idioma.FR to "Pas de contenu cette semaine",
        Idioma.IT to "Nessun contenuto questa settimana",
        Idioma.PT to "Sem conteúdo esta semana",
    ),
    ClaveTexto.SIN_CONTENIDO_NIVEL to mapOf(
        Idioma.ES to "Todavía no hay contenido para {idioma} en nivel {nivel}.",
        Idioma.EN to "There is no content yet for {idioma} at level {nivel}.",
        Idioma.DE to "Für {idioma} auf Niveau {nivel} gibt es noch keinen Inhalt.",
        Idioma.FR to "Il n'y a pas encore de contenu pour {idioma} au niveau {nivel}.",
        Idioma.IT to "Non c'è ancora contenuto per {idioma} al livello {nivel}.",
        Idioma.PT to "Ainda não há conteúdo para {idioma} no nível {nivel}.",
    ),
    ClaveTexto.SIN_CONTENIDO_SEMANA to mapOf(
        Idioma.ES to "Esta semana queda fuera de la edición {anio}.",
        Idioma.EN to "This week is outside the {anio} edition.",
        Idioma.DE to "Diese Woche liegt außerhalb der Ausgabe {anio}.",
        Idioma.FR to "Cette semaine est en dehors de l'édition {anio}.",
        Idioma.IT to "Questa settimana è fuori dall'edizione {anio}.",
        Idioma.PT to "Esta semana está fora da edição {anio}.",
    ),
    ClaveTexto.SIN_CALENDARIO to mapOf(
        Idioma.ES to "Todavía no hay calendario para {anio}.",
        Idioma.EN to "There is no calendar for {anio} yet.",
        Idioma.DE to "Für {anio} gibt es noch keinen Kalender.",
        Idioma.FR to "Il n'y a pas encore de calendrier pour {anio}.",
        Idioma.IT to "Non c'è ancora un calendario per {anio}.",
        Idioma.PT to "Ainda não há calendário para {anio}.",
    ),
    ClaveTexto.SALIR_CONFIRMAR to mapOf(
        Idioma.ES to "Pulsa otra vez para salir",
        Idioma.EN to "Press again to exit",
        Idioma.DE to "Zum Beenden nochmals drücken",
        Idioma.FR to "Appuie à nouveau pour quitter",
        Idioma.IT to "Premi di nuovo per uscire",
        Idioma.PT to "Pressione novamente para sair",
    ),
    ClaveTexto.IDIOMAS_APRENDIDOS to mapOf(
        Idioma.ES to "Idioma que aprendes",
        Idioma.EN to "Language you're learning",
        Idioma.DE to "Sprache, die du lernst",
        Idioma.FR to "Langue que tu apprends",
        Idioma.IT to "Lingua che stai imparando",
        Idioma.PT to "Idioma que você está aprendendo",
    ),
    ClaveTexto.IDIOMA_APP to mapOf(
        Idioma.ES to "Idioma de la aplicación",
        Idioma.EN to "App language",
        Idioma.DE to "Sprache der App",
        Idioma.FR to "Langue de l'application",
        Idioma.IT to "Lingua dell'applicazione",
        Idioma.PT to "Idioma do aplicativo",
    ),
    // Una sola palabra por idioma: "Según el sistema" y sus equivalentes no
    // entraban en el ancho de pantalla en varios de los 6 idiomas y obligaban
    // a deslizar para leer la opcion completa.
    ClaveTexto.SEGUN_SISTEMA to mapOf(
        Idioma.ES to "Sistema",
        Idioma.EN to "System",
        Idioma.DE to "System",
        Idioma.FR to "Système",
        Idioma.IT to "Sistema",
        Idioma.PT to "Sistema",
    ),
    ClaveTexto.ESTILO_VISUAL to mapOf(
        Idioma.ES to "Estilo visual", Idioma.EN to "Visual style", Idioma.DE to "Erscheinungsbild",
        Idioma.FR to "Style visuel", Idioma.IT to "Stile visivo", Idioma.PT to "Estilo visual",
    ),
    ClaveTexto.MODO to mapOf(
        Idioma.ES to "Modo", Idioma.EN to "Mode", Idioma.DE to "Modus",
        Idioma.FR to "Mode", Idioma.IT to "Modalità", Idioma.PT to "Modo",
    ),
    ClaveTexto.MODO_CLARO to mapOf(
        Idioma.ES to "Claro", Idioma.EN to "Light", Idioma.DE to "Hell",
        Idioma.FR to "Clair", Idioma.IT to "Chiaro", Idioma.PT to "Claro",
    ),
    ClaveTexto.MODO_OSCURO to mapOf(
        Idioma.ES to "Oscuro", Idioma.EN to "Dark", Idioma.DE to "Dunkel",
        Idioma.FR to "Sombre", Idioma.IT to "Scuro", Idioma.PT to "Escuro",
    ),
    ClaveTexto.NIVEL to mapOf(
        Idioma.ES to "Nivel", Idioma.EN to "Level", Idioma.DE to "Niveau",
        Idioma.FR to "Niveau", Idioma.IT to "Livello", Idioma.PT to "Nível",
    ),
    ClaveTexto.CAMBIAR_NIVEL to mapOf(
        Idioma.ES to "Cambiar nivel", Idioma.EN to "Change level", Idioma.DE to "Niveau ändern",
        Idioma.FR to "Changer de niveau", Idioma.IT to "Cambia livello", Idioma.PT to "Mudar de nível",
    ),
    ClaveTexto.IDIOMA_NO_DISPONIBLE to mapOf(
        Idioma.ES to "{idioma} todavía no está disponible",
        Idioma.EN to "{idioma} isn't available yet",
        Idioma.DE to "{idioma} ist noch nicht verfügbar",
        Idioma.FR to "{idioma} n'est pas encore disponible",
        Idioma.IT to "{idioma} non è ancora disponibile",
        Idioma.PT to "{idioma} ainda não está disponível",
    ),
    ClaveTexto.AVISO_GLOSAS to mapOf(
        Idioma.ES to "Las traducciones de vocabulario todavía solo existen en español.",
        Idioma.EN to "Vocabulary translations only exist in Spanish so far.",
        Idioma.DE to "Die Wortschatzübersetzungen liegen bisher nur auf Spanisch vor.",
        Idioma.FR to "Les traductions du vocabulaire n'existent pour l'instant qu'en espagnol.",
        Idioma.IT to "Le traduzioni del lessico esistono per ora solo in spagnolo.",
        Idioma.PT to "As traduções de vocabulário só existem em espanhol por enquanto.",
    ),
    // --- Guardados (feature 3) ---
    ClaveTexto.GUARDADOS to mapOf(
        Idioma.ES to "Guardados", Idioma.EN to "Saved", Idioma.DE to "Gespeichert",
        Idioma.FR to "Enregistrés", Idioma.IT to "Salvati", Idioma.PT to "Salvos",
    ),
    ClaveTexto.GUARDADOS_VACIO to mapOf(
        Idioma.ES to "Aquí irán tus palabras y expresiones. Toca la estrella junto a una para guardarla.",
        Idioma.EN to "Your words and expressions will live here. Tap the star next to one to save it.",
        Idioma.DE to "Hier sammelst du Wörter und Ausdrücke. Tippe auf den Stern neben einem Eintrag, um ihn zu speichern.",
        Idioma.FR to "Tes mots et expressions seront ici. Appuie sur l'étoile à côté de l'un d'eux pour l'enregistrer.",
        Idioma.IT to "Qui troverai le tue parole ed espressioni. Tocca la stella accanto a una per salvarla.",
        Idioma.PT to "Aqui ficam suas palavras e expressões. Toque na estrela ao lado de uma para salvá-la.",
    ),
    ClaveTexto.FILTRO_IDIOMA to mapOf(
        Idioma.ES to "Idioma", Idioma.EN to "Language", Idioma.DE to "Sprache",
        Idioma.FR to "Langue", Idioma.IT to "Lingua", Idioma.PT to "Idioma",
    ),
    ClaveTexto.FILTRO_TIPO to mapOf(
        Idioma.ES to "Tipo", Idioma.EN to "Type", Idioma.DE to "Art",
        Idioma.FR to "Type", Idioma.IT to "Tipo", Idioma.PT to "Tipo",
    ),
    ClaveTexto.FILTRO_TODOS to mapOf(
        Idioma.ES to "Todos", Idioma.EN to "All", Idioma.DE to "Alle",
        Idioma.FR to "Tous", Idioma.IT to "Tutti", Idioma.PT to "Todos",
    ),
    ClaveTexto.TIPO_VOCABULARIO to mapOf(
        Idioma.ES to "Vocabulario", Idioma.EN to "Vocabulary", Idioma.DE to "Wortschatz",
        Idioma.FR to "Vocabulaire", Idioma.IT to "Lessico", Idioma.PT to "Vocabulário",
    ),
    ClaveTexto.TIPO_EXPRESION to mapOf(
        Idioma.ES to "Expresiones", Idioma.EN to "Expressions", Idioma.DE to "Redemittel",
        Idioma.FR to "Expressions", Idioma.IT to "Espressioni", Idioma.PT to "Expressões",
    ),
    // --- Desafio de fin de semana (feature 2) ---
    ClaveTexto.DESAFIO_FINDE to mapOf(
        Idioma.ES to "Desafío de fin de semana", Idioma.EN to "Weekend challenge", Idioma.DE to "Wochenend-Herausforderung",
        Idioma.FR to "Défi du week-end", Idioma.IT to "Sfida del weekend", Idioma.PT to "Desafio de fim de semana",
    ),
    // --- Reto del fin de semana (Ronda E, tarea 4) ---
    // --- Desafio de fin de semana (Ronda F2, tarea 2) ---
    // Linea oral que va ANTES de la consigna de la ficha (Ronda K, encuadre corregido en la L): presenta la consigna
    // como el TEMA de lo que se va a contar, no como una orden. Asi funciona aunque la consigna (que esta redactada
    // para la evidencia escrita) empiece con "Escribe…": se lee como tema y no hace falta aclarar nada entre parentesis.
    // {oralMin} = evidencia.oralMin redondeado; "min" y no "minutos" para no depender del plural.
    ClaveTexto.RETO_CONSIGNA_LINEA to mapOf(
        Idioma.ES to "Esto es lo que vas a contar. En voz alta, {oralMin} min, de una sola vez y sin escribir nada:",
        Idioma.EN to "This is what you'll talk about. Out loud, {oralMin} min, in one go, with no writing:",
        Idioma.DE to "Davon erzählst du. Laut, {oralMin} Min. am Stück und ohne etwas aufzuschreiben:",
        Idioma.FR to "Voilà ce que tu vas raconter. À voix haute, {oralMin} min, d'un seul trait et sans rien écrire :",
        Idioma.IT to "Questo è ciò che racconterai. Ad alta voce, {oralMin} min, tutto d'un fiato e senza scrivere nulla:",
        Idioma.PT to "Isto é o que você vai contar. Em voz alta, {oralMin} min, de uma só vez e sem escrever nada:",
    ),
    ClaveTexto.RETO_REGLAS_BASICO to mapOf(
        Idioma.ES to "De corrido, sin leer\nUna sola toma\nDíselo a una persona o grábate\nEscúchalo y repítelo una vez",
        Idioma.EN to "Fluently, without reading\nOne single take\nSay it to a person or record yourself\nListen back and repeat it once",
        Idioma.DE to "Flüssig, ohne abzulesen\nNur ein Versuch\nSag es einer Person oder nimm dich auf\nHör es dir an und wiederhole es einmal",
        Idioma.FR to "D'un trait, sans lire\nUne seule prise\nDis-le à quelqu'un ou enregistre-toi\nÉcoute-toi et recommence une fois",
        Idioma.IT to "Di seguito, senza leggere\nUna sola ripresa\nDillo a una persona o registrati\nRiascoltati e ripetilo una volta",
        Idioma.PT to "De uma vez, sem ler\nUma única gravação\nDiga isso a uma pessoa ou grave um áudio\nEscute e repita uma vez",
    ),
    ClaveTexto.RETO_REGLAS_AVANZADO to mapOf(
        Idioma.ES to "Sin notas\nUna toma de {oralMin} min\nCambia de interlocutor a mitad\nGrábate y apunta una cosa que mejorarías",
        Idioma.EN to "No notes\nOne take of {oralMin} min\nSwitch listener halfway through\nRecord yourself and note one thing to improve",
        Idioma.DE to "Ohne Notizen\nEin Durchgang von {oralMin} Min.\nWechsle in der Mitte das Gegenüber\nNimm dich auf und notiere eine Sache, die du verbessern würdest",
        Idioma.FR to "Sans notes\nUne prise de {oralMin} min\nChange d'interlocuteur à mi-parcours\nEnregistre-toi et note une chose à améliorer",
        Idioma.IT to "Senza appunti\nUna ripresa di {oralMin} min\nCambia interlocutore a metà\nRegistrati e annota una cosa da migliorare",
        Idioma.PT to "Sem anotações\nUma gravação de {oralMin} min\nTroque de interlocutor na metade\nGrave um áudio e anote uma coisa que você melhoraria",
    ),
    ClaveTexto.RETO_HABILIDAD to mapOf(
        Idioma.ES to "Habilidad",
        Idioma.EN to "Skill",
        Idioma.DE to "Fähigkeit",
        Idioma.FR to "Compétence",
        Idioma.IT to "Abilità",
        Idioma.PT to "Habilidade",
    ),
    ClaveTexto.RETO_CONDICIONES to mapOf(
        Idioma.ES to "Condiciones",
        Idioma.EN to "Conditions",
        Idioma.DE to "Bedingungen",
        Idioma.FR to "Conditions",
        Idioma.IT to "Condizioni",
        Idioma.PT to "Condições",
    ),
    ClaveTexto.RETO_FRASES_TITULO to mapOf(
        Idioma.ES to "Frases para usar",
        Idioma.EN to "Phrases to use",
        Idioma.DE to "Ausdrücke zum Verwenden",
        Idioma.FR to "Phrases à utiliser",
        Idioma.IT to "Frasi da usare",
        Idioma.PT to "Frases para usar",
    ),
    ClaveTexto.RETO_FRASES_AYUDA to mapOf(
        Idioma.ES to "Intenta usar al menos dos:",
        Idioma.EN to "Try to use at least two:",
        Idioma.DE to "Versuche, mindestens zwei zu verwenden:",
        Idioma.FR to "Essaie d'en utiliser au moins deux :",
        Idioma.IT to "Prova a usarne almeno due:",
        Idioma.PT to "Tente usar pelo menos duas:",
    ),
    // --- Exportar / importar Guardados (Ronda F2, tarea 6) ---
    ClaveTexto.GUARDADOS_EXPORTAR to mapOf(
        Idioma.ES to "Exportar",
        Idioma.EN to "Export",
        Idioma.DE to "Exportieren",
        Idioma.FR to "Exporter",
        Idioma.IT to "Esporta",
        Idioma.PT to "Exportar",
    ),
    ClaveTexto.GUARDADOS_IMPORTAR to mapOf(
        Idioma.ES to "Importar",
        Idioma.EN to "Import",
        Idioma.DE to "Importieren",
        Idioma.FR to "Importer",
        Idioma.IT to "Importa",
        Idioma.PT to "Importar",
    ),
    ClaveTexto.GUARDADOS_EXPORTAR_ANKI to mapOf(
        Idioma.ES to "A Anki (TSV)",
        Idioma.EN to "To Anki (TSV)",
        Idioma.DE to "Für Anki (TSV)",
        Idioma.FR to "Pour Anki (TSV)",
        Idioma.IT to "Per Anki (TSV)",
        Idioma.PT to "Para o Anki (TSV)",
    ),
    ClaveTexto.GUARDADOS_EXPORTAR_RESPALDO to mapOf(
        Idioma.ES to "Copia de seguridad (JSON)",
        Idioma.EN to "Backup (JSON)",
        Idioma.DE to "Sicherung (JSON)",
        Idioma.FR to "Sauvegarde (JSON)",
        Idioma.IT to "Copia di sicurezza (JSON)",
        Idioma.PT to "Cópia de segurança (JSON)",
    ),
    ClaveTexto.GUARDADOS_MSG_EXPORTADO to mapOf(
        Idioma.ES to "{n} elementos exportados.",
        Idioma.EN to "{n} items exported.",
        Idioma.DE to "{n} Einträge exportiert.",
        Idioma.FR to "{n} éléments exportés.",
        Idioma.IT to "{n} elementi esportati.",
        Idioma.PT to "{n} itens exportados.",
    ),
    ClaveTexto.GUARDADOS_MSG_IMPORTADO to mapOf(
        Idioma.ES to "Importados: {nuevos} nuevos, {ya} ya estaban.",
        Idioma.EN to "Imported: {nuevos} new, {ya} already there.",
        Idioma.DE to "Importiert: {nuevos} neu, {ya} bereits vorhanden.",
        Idioma.FR to "Importés : {nuevos} nouveaux, {ya} déjà présents.",
        Idioma.IT to "Importati: {nuevos} nuovi, {ya} già presenti.",
        Idioma.PT to "Importados: {nuevos} novos, {ya} já estavam.",
    ),
    ClaveTexto.GUARDADOS_ERR_INVALIDO to mapOf(
        Idioma.ES to "El archivo no es una copia de seguridad válida.",
        Idioma.EN to "The file is not a valid backup.",
        Idioma.DE to "Die Datei ist keine gültige Sicherung.",
        Idioma.FR to "Le fichier n'est pas une sauvegarde valide.",
        Idioma.IT to "Il file non è una copia di sicurezza valida.",
        Idioma.PT to "O arquivo não é uma cópia de segurança válida.",
    ),
    ClaveTexto.GUARDADOS_ERR_GRANDE to mapOf(
        Idioma.ES to "El archivo es demasiado grande.",
        Idioma.EN to "The file is too large.",
        Idioma.DE to "Die Datei ist zu groß.",
        Idioma.FR to "Le fichier est trop volumineux.",
        Idioma.IT to "Il file è troppo grande.",
        Idioma.PT to "O arquivo é grande demais.",
    ),
    ClaveTexto.GUARDADOS_ERR_VERSION to mapOf(
        Idioma.ES to "El archivo es de una versión que esta app no conoce.",
        Idioma.EN to "The file is from a version this app does not know.",
        Idioma.DE to "Die Datei stammt aus einer Version, die diese App nicht kennt.",
        Idioma.FR to "Le fichier provient d'une version que cette application ne connaît pas.",
        Idioma.IT to "Il file proviene da una versione che questa app non conosce.",
        Idioma.PT to "O arquivo é de uma versão que este aplicativo não conhece.",
    ),
    ClaveTexto.GUARDADOS_ERR_ARCHIVO to mapOf(
        Idioma.ES to "No se pudo acceder al archivo.",
        Idioma.EN to "The file could not be accessed.",
        Idioma.DE to "Auf die Datei konnte nicht zugegriffen werden.",
        Idioma.FR to "Impossible d'accéder au fichier.",
        Idioma.IT to "Impossibile accedere al file.",
        Idioma.PT to "Não foi possível acessar o arquivo.",
    ),
    // --- Indice de habilidades (Ronda F2, tarea 7) ---
    ClaveTexto.BIBLIOTECA_VISTA_LEXICO to mapOf(
        Idioma.ES to "Vocabulario y expresiones",
        Idioma.EN to "Vocabulary and expressions",
        Idioma.DE to "Wortschatz und Redewendungen",
        Idioma.FR to "Vocabulaire et expressions",
        Idioma.IT to "Vocabolario ed espressioni",
        Idioma.PT to "Vocabulário e expressões",
    ),
    ClaveTexto.BIBLIOTECA_VISTA_HABILIDADES to mapOf(
        Idioma.ES to "Habilidades",
        Idioma.EN to "Skills",
        Idioma.DE to "Fähigkeiten",
        Idioma.FR to "Compétences",
        Idioma.IT to "Abilità",
        Idioma.PT to "Habilidades",
    ),
    ClaveTexto.HABILIDAD_SEMANAS to mapOf(
        Idioma.ES to "sem. {semanas}",
        Idioma.EN to "wk {semanas}",
        Idioma.DE to "KW {semanas}",
        Idioma.FR to "sem. {semanas}",
        Idioma.IT to "sett. {semanas}",
        Idioma.PT to "sem. {semanas}",
    ),
    // --- Descripciones para lectores de pantalla (TalkBack) ---
    ClaveTexto.SECCION_CONTRAER to mapOf(
        Idioma.ES to "Contraer",
        Idioma.EN to "Collapse",
        Idioma.DE to "Einklappen",
        Idioma.FR to "Réduire",
        Idioma.IT to "Comprimi",
        Idioma.PT to "Recolher",
    ),
    ClaveTexto.SECCION_EXPANDIR to mapOf(
        Idioma.ES to "Expandir",
        Idioma.EN to "Expand",
        Idioma.DE to "Ausklappen",
        Idioma.FR to "Développer",
        Idioma.IT to "Espandi",
        Idioma.PT to "Expandir",
    ),
    // --- Vista Verbos (Ronda H) ---
    ClaveTexto.VERBOS_SIN_RESULTADOS to mapOf(
        Idioma.ES to "Ningún verbo de esta lista coincide. Prueba con otra búsqueda.",
        Idioma.EN to "No verb in this list matches. Try another search.",
        Idioma.DE to "Kein Verb in dieser Liste passt. Versuche eine andere Suche.",
        Idioma.FR to "Aucun verbe de cette liste ne correspond. Essaie une autre recherche.",
        Idioma.IT to "Nessun verbo di questo elenco corrisponde. Prova con un'altra ricerca.",
        Idioma.PT to "Nenhum verbo desta lista corresponde. Tente outra busca.",
    ),
    ClaveTexto.BUSCAR_BORRAR to mapOf(
        Idioma.ES to "Borrar búsqueda",
        Idioma.EN to "Clear search",
        Idioma.DE to "Suche löschen",
        Idioma.FR to "Effacer la recherche",
        Idioma.IT to "Cancella la ricerca",
        Idioma.PT to "Limpar a busca",
    ),
    ClaveTexto.BIBLIOTECA_VISTA_VERBOS to mapOf(
        Idioma.ES to "Verbos",
        Idioma.EN to "Verbs",
        Idioma.DE to "Verben",
        Idioma.FR to "Verbes",
        Idioma.IT to "Verbi",
        Idioma.PT to "Verbos",
    ),
    ClaveTexto.VERBO_REFLEXIVO to mapOf(
        Idioma.ES to "Reflexivo",
        Idioma.EN to "Reflexive",
        Idioma.DE to "Reflexiv",
        Idioma.FR to "Pronominal",
        Idioma.IT to "Riflessivo",
        Idioma.PT to "Reflexivo",
    ),
    ClaveTexto.VERBO_TRENNBAR to mapOf(
        Idioma.ES to "Separable",
        Idioma.EN to "Separable",
        Idioma.DE to "Trennbar",
        Idioma.FR to "Séparable",
        Idioma.IT to "Separabile",
        Idioma.PT to "Separável",
    ),
    ClaveTexto.VERBO_MIT_PRAPOSITION to mapOf(
        Idioma.ES to "Con preposición",
        Idioma.EN to "With preposition",
        Idioma.DE to "Mit Präposition",
        Idioma.FR to "Avec préposition",
        Idioma.IT to "Con preposizione",
        Idioma.PT to "Com preposição",
    ),
    ClaveTexto.VERBO_MIT_DATIV to mapOf(
        Idioma.ES to "Con dativo",
        Idioma.EN to "With dative",
        Idioma.DE to "Mit Dativ",
        Idioma.FR to "Avec datif",
        Idioma.IT to "Con il dativo",
        Idioma.PT to "Com dativo",
    ),
    ClaveTexto.VERBO_PHRASAL_SEPARABLE to mapOf(
        Idioma.ES to "Phrasal separable",
        Idioma.EN to "Separable phrasal verb",
        Idioma.DE to "Trennbares Phrasal Verb",
        Idioma.FR to "Phrasal verb séparable",
        Idioma.IT to "Phrasal verb separabile",
        Idioma.PT to "Phrasal verb separável",
    ),
    ClaveTexto.VERBO_PHRASAL_INSEPARABLE to mapOf(
        Idioma.ES to "Phrasal inseparable",
        Idioma.EN to "Inseparable phrasal verb",
        Idioma.DE to "Untrennbares Phrasal Verb",
        Idioma.FR to "Phrasal verb inséparable",
        Idioma.IT to "Phrasal verb inseparabile",
        Idioma.PT to "Phrasal verb inseparável",
    ),
    ClaveTexto.VERBO_VERBO_PREPOSICION to mapOf(
        Idioma.ES to "Verbo + preposición",
        Idioma.EN to "Verb + preposition",
        Idioma.DE to "Verb + Präposition",
        Idioma.FR to "Verbe + préposition",
        Idioma.IT to "Verbo + preposizione",
        Idioma.PT to "Verbo + preposição",
    ),
    ClaveTexto.VERBOS_VACIO to mapOf(
        Idioma.ES to "Todavía no hay verbos etiquetados para este idioma.",
        Idioma.EN to "No tagged verbs for this language yet.",
        Idioma.DE to "Für diese Sprache gibt es noch keine gekennzeichneten Verben.",
        Idioma.FR to "Pas encore de verbes étiquetés pour cette langue.",
        Idioma.IT to "Non ci sono ancora verbi etichettati per questa lingua.",
        Idioma.PT to "Ainda não há verbos etiquetados para este idioma.",
    ),
    // --- Tipografias (Ronda G) ---
    ClaveTexto.FUENTE_TITULO to mapOf(
        Idioma.ES to "Tipografía",
        Idioma.EN to "Typeface",
        Idioma.DE to "Schrift",
        Idioma.FR to "Police",
        Idioma.IT to "Carattere",
        Idioma.PT to "Fonte",
    ),
    ClaveTexto.FUENTE_LECTURA to mapOf(
        Idioma.ES to "Lectura",
        Idioma.EN to "Reading",
        Idioma.DE to "Lesen",
        Idioma.FR to "Lecture",
        Idioma.IT to "Lettura",
        Idioma.PT to "Leitura",
    ),
    ClaveTexto.FUENTE_CLARA to mapOf(
        Idioma.ES to "Clara",
        Idioma.EN to "Clear",
        Idioma.DE to "Klar",
        Idioma.FR to "Claire",
        Idioma.IT to "Chiara",
        Idioma.PT to "Clara",
    ),
    ClaveTexto.ACERCA_FUENTES_TEXTO to mapOf(
        Idioma.ES to "Las tipografías Literata y Atkinson Hyperlegible Next se distribuyen bajo la licencia SIL OFL 1.1.",
        Idioma.EN to "The Literata and Atkinson Hyperlegible Next typefaces are distributed under the SIL OFL 1.1 licence.",
        Idioma.DE to "Die Schriften Literata und Atkinson Hyperlegible Next werden unter der SIL OFL 1.1 verbreitet.",
        Idioma.FR to "Les polices Literata et Atkinson Hyperlegible Next sont distribuées sous la licence SIL OFL 1.1.",
        Idioma.IT to "I caratteri Literata e Atkinson Hyperlegible Next sono distribuiti con la licenza SIL OFL 1.1.",
        Idioma.PT to "As fontes Literata e Atkinson Hyperlegible Next são distribuídas sob a licença SIL OFL 1.1.",
    ),
    // --- Temas de color (Ronda G) ---
    ClaveTexto.TEMA_PAPEL to mapOf(
        Idioma.ES to "Papel",
        Idioma.EN to "Paper",
        Idioma.DE to "Papier",
        Idioma.FR to "Papier",
        Idioma.IT to "Carta",
        Idioma.PT to "Papel",
    ),
    ClaveTexto.TEMA_TERRACOTA to mapOf(
        Idioma.ES to "Terracota",
        Idioma.EN to "Terracotta",
        Idioma.DE to "Terrakotta",
        Idioma.FR to "Terre cuite",
        Idioma.IT to "Terracotta",
        Idioma.PT to "Terracota",
    ),
    ClaveTexto.TEMA_MAR to mapOf(
        Idioma.ES to "Mar",
        Idioma.EN to "Sea",
        Idioma.DE to "Meer",
        Idioma.FR to "Mer",
        Idioma.IT to "Mare",
        Idioma.PT to "Mar",
    ),
    ClaveTexto.TEMA_GRAFITO to mapOf(
        Idioma.ES to "Grafito",
        Idioma.EN to "Graphite",
        Idioma.DE to "Graphit",
        Idioma.FR to "Graphite",
        Idioma.IT to "Grafite",
        Idioma.PT to "Grafite",
    ),
    // --- Estado sin idioma elegido (pantalla y widget) ---
    ClaveTexto.SIN_IDIOMA_TITULO to mapOf(
        Idioma.ES to "Sin idioma seleccionado",
        Idioma.EN to "No language selected",
        Idioma.DE to "Keine Sprache ausgewählt",
        Idioma.FR to "Aucune langue sélectionnée",
        Idioma.IT to "Nessuna lingua selezionata",
        Idioma.PT to "Nenhum idioma selecionado",
    ),
    ClaveTexto.SIN_IDIOMA_TEXTO to mapOf(
        Idioma.ES to "Elige al menos un idioma en Ajustes para ver contenido.",
        Idioma.EN to "Choose at least one language in Settings to see content.",
        Idioma.DE to "Wähle in den Einstellungen mindestens eine Sprache, um Inhalte zu sehen.",
        Idioma.FR to "Choisis au moins une langue dans Paramètres pour voir du contenu.",
        Idioma.IT to "Scegli almeno una lingua in Impostazioni per vedere i contenuti.",
        Idioma.PT to "Escolha pelo menos um idioma em Configurações para ver o conteúdo.",
    ),
    ClaveTexto.SIN_IDIOMA_WIDGET to mapOf(
        Idioma.ES to "Elige un idioma en Ajustes.",
        Idioma.EN to "Choose a language in Settings.",
        Idioma.DE to "Wähle in den Einstellungen eine Sprache.",
        Idioma.FR to "Choisis une langue dans Paramètres.",
        Idioma.IT to "Scegli una lingua in Impostazioni.",
        Idioma.PT to "Escolha um idioma em Configurações.",
    ),
    // --- Acerca de y linea fija (Ronda E, tarea 6) ---
    ClaveTexto.ACERCA_COMO_TITULO to mapOf(
        Idioma.ES to "Cómo está pensada",
        Idioma.EN to "How it is designed",
        Idioma.DE to "Wie sie gedacht ist",
        Idioma.FR to "Comment elle est conçue",
        Idioma.IT to "Come è pensata",
        Idioma.PT to "Como foi pensado",
    ),
    ClaveTexto.ACERCA_COMO_TEXTO to mapOf(
        Idioma.ES to "Las habilidades más importantes de cada nivel se repiten durante el año con distinta profundidad, porque la repetición espaciada es la base del método. Cuatro veces al año se dedica una semana al repaso, y cada fin de semana hay un desafío de práctica oral. El contenido se publica en ediciones anuales. Puedes practicar alemán o inglés, y la interfaz está disponible en español, inglés, alemán, francés, italiano y portugués.",
        Idioma.EN to "The most important skills of each level come back during the year at a different depth, because spaced repetition is the basis of the method. Four times a year one week is devoted to review, and every weekend there is an oral practice challenge. The content is published in yearly editions. You can practise German or English, and the interface is available in Spanish, English, German, French, Italian and Portuguese.",
        Idioma.DE to "Die wichtigsten Fähigkeiten jedes Niveaus kehren im Lauf des Jahres in unterschiedlicher Tiefe wieder, denn verteiltes Wiederholen ist die Grundlage der Methode. Viermal im Jahr ist eine Woche der Wiederholung gewidmet, und an jedem Wochenende gibt es eine mündliche Übungsaufgabe. Die Inhalte erscheinen in jährlichen Ausgaben. Du kannst Deutsch oder Englisch üben, und die Oberfläche gibt es auf Spanisch, Englisch, Deutsch, Französisch, Italienisch und Portugiesisch.",
        Idioma.FR to "Les compétences les plus importantes de chaque niveau reviennent au fil de l'année avec une profondeur différente, car la répétition espacée est la base de la méthode. Quatre fois par an, une semaine est consacrée à la révision, et chaque week-end propose un défi de pratique orale. Le contenu est publié en éditions annuelles. Tu peux pratiquer l'allemand ou l'anglais, et l'interface est disponible en espagnol, anglais, allemand, français, italien et portugais.",
        Idioma.IT to "Le abilità più importanti di ogni livello ritornano durante l'anno con una profondità diversa, perché la ripetizione distanziata è la base del metodo. Quattro volte all'anno una settimana è dedicata al ripasso, e ogni fine settimana c'è una sfida di pratica orale. I contenuti vengono pubblicati in edizioni annuali. Puoi praticare tedesco o inglese, e l'interfaccia è disponibile in spagnolo, inglese, tedesco, francese, italiano e portoghese.",
        Idioma.PT to "As habilidades mais importantes de cada nível voltam ao longo do ano com profundidade diferente, porque a repetição espaçada é a base do método. Quatro vezes por ano uma semana é dedicada à revisão, e todo fim de semana há um desafio de prática oral. O conteúdo é publicado em edições anuais. Você pode praticar alemão ou inglês, e a interface está disponível em espanhol, inglês, alemão, francês, italiano e português.",
    ),
    // Voces: solo se nombra la alemana (CC0). Las inglesas actuales no se pueden publicar (docs/licencias-voces.md) y su
    // audio esta apagado en release; cuando se regeneren con una voz de corpus limpio hay que sumarla aca.
    ClaveTexto.ACERCA_CREDITOS_TEXTO to mapOf(
        Idioma.ES to "Audio generado con Piper (MIT). Voz alemana: Thorsten-Voice, CC0.",
        Idioma.EN to "Audio generated with Piper (MIT). German voice: Thorsten-Voice, CC0.",
        Idioma.DE to "Audio mit Piper (MIT) erzeugt. Deutsche Stimme: Thorsten-Voice, CC0.",
        Idioma.FR to "Audio généré avec Piper (MIT). Voix allemande : Thorsten-Voice, CC0.",
        Idioma.IT to "Audio generato con Piper (MIT). Voce tedesca: Thorsten-Voice, CC0.",
        Idioma.PT to "Áudio gerado com o Piper (MIT). Voz alemã: Thorsten-Voice, CC0.",
    ),
    // --- Audio (Ronda M): contentDescription del boton de altavoz ---
    ClaveTexto.AUDIO_ESCUCHAR to mapOf(
        Idioma.ES to "Escuchar", Idioma.EN to "Listen", Idioma.DE to "Anhören",
        Idioma.FR to "Écouter", Idioma.IT to "Ascolta", Idioma.PT to "Ouvir",
    ),
    ClaveTexto.AUDIO_DETENER to mapOf(
        Idioma.ES to "Detener", Idioma.EN to "Stop", Idioma.DE to "Stopp",
        Idioma.FR to "Arrêter", Idioma.IT to "Interrompi", Idioma.PT to "Parar",
    ),
    ClaveTexto.ACERCA_PRUEBA to mapOf(
        Idioma.ES to "Esta es una versión de prueba; tus comentarios ayudan a mejorarla.",
        Idioma.EN to "This is a test version; your feedback helps improve it.",
        Idioma.DE to "Dies ist eine Testversion; dein Feedback hilft, sie zu verbessern.",
        Idioma.FR to "Ceci est une version de test ; tes commentaires aident à l'améliorer.",
        Idioma.IT to "Questa è una versione di prova; i tuoi commenti aiutano a migliorarla.",
        Idioma.PT to "Esta é uma versão de teste; seus comentários ajudam a melhorá-la.",
    ),
    ClaveTexto.ACERCA_DE to mapOf(
        Idioma.ES to "Acerca de",
        Idioma.EN to "About",
        Idioma.DE to "Über die App",
        Idioma.FR to "À propos",
        Idioma.IT to "Informazioni",
        Idioma.PT to "Sobre",
    ),
    ClaveTexto.LINEA_FIJA to mapOf(
        Idioma.ES to "Para fijar lo que ya aprendiste en tu curso.",
        Idioma.EN to "To consolidate what you already learned in your course.",
        Idioma.DE to "Um zu festigen, was du in deinem Kurs schon gelernt hast.",
        Idioma.FR to "Pour fixer ce que tu as déjà appris dans ton cours.",
        Idioma.IT to "Per fissare ciò che hai già imparato nel tuo corso.",
        Idioma.PT to "Para fixar o que você já aprendeu no seu curso.",
    ),
    ClaveTexto.ACERCA_QUE_ES_TITULO to mapOf(
        Idioma.ES to "Qué es y qué no es",
        Idioma.EN to "What it is and what it isn't",
        Idioma.DE to "Was sie ist und was nicht",
        Idioma.FR to "Ce que c'est et ce que ce n'est pas",
        Idioma.IT to "Cosa è e cosa non è",
        Idioma.PT to "O que é e o que não é",
    ),
    // «Que es esto y que no es»: el texto mas importante de la app. UNA sola clave para dos lugares: la pantalla de
    // bienvenida (Ronda N) y «Acerca de». Con la Ronda N reemplaza al parrafo anterior de «Acerca de».
    ClaveTexto.ACERCA_QUE_ES_TEXTO to mapOf(
        Idioma.ES to "LenguApp no enseña un idioma: te hace practicar el que ya estás aprendiendo. Cada semana te propone un tema, una habilidad y un desafío para producir, no para estudiar. Si estás empezando un nivel, haz un curso de ese nivel; esta app es para que lo que aprendes ahí no se te quede en el cuaderno.",
        Idioma.EN to "LenguApp doesn't teach you a language: it makes you practise the one you're already learning. Every week it gives you a topic, a skill and a challenge to produce language, not to study it. If you're just starting a level, take a course at that level; this app is there so that what you learn in it doesn't stay in your notebook.",
        Idioma.DE to "LenguApp bringt dir keine Sprache bei: Es lässt dich die Sprache üben, die du schon lernst. Jede Woche schlägt es dir ein Thema, eine Fähigkeit und eine Herausforderung vor, bei der du Sprache selbst hervorbringst, statt zu lernen. Wenn du ein Niveau gerade erst beginnst, besuche einen Kurs auf diesem Niveau; diese App sorgt dafür, dass das, was du dort lernst, nicht im Heft stehen bleibt.",
        Idioma.FR to "LenguApp n'enseigne pas une langue : elle te fait pratiquer celle que tu apprends déjà. Chaque semaine, elle te propose un thème, une compétence et un défi pour produire, pas pour étudier. Si tu débutes un niveau, suis un cours de ce niveau ; cette appli sert à ce que ce que tu y apprends ne reste pas dans ton cahier.",
        Idioma.IT to "LenguApp non insegna una lingua: ti fa praticare quella che stai già imparando. Ogni settimana ti propone un tema, un'abilità e una sfida per produrre, non per studiare. Se stai iniziando un livello, segui un corso di quel livello; questa app serve a fare in modo che quello che impari lì non resti sul quaderno.",
        Idioma.PT to "O LenguApp não ensina um idioma: ele faz você praticar o que já está aprendendo. Toda semana propõe um tema, uma habilidade e um desafio para produzir, não para estudar. Se você está começando um nível, faça um curso desse nível; este app existe para que o que você aprende lá não fique no caderno.",
    ),
    // --- Tutorial con foco (Ronda Q): botones y rotulos del recuadro ---
    ClaveTexto.TOUR_ATRAS to mapOf(
        Idioma.ES to "Atrás", Idioma.EN to "Back", Idioma.DE to "Zurück",
        Idioma.FR to "Retour", Idioma.IT to "Indietro", Idioma.PT to "Voltar",
    ),
    ClaveTexto.TOUR_ATRAS_CD to mapOf(
        Idioma.ES to "Volver al paso anterior del tutorial", Idioma.EN to "Go back to the previous tutorial step",
        Idioma.DE to "Zum vorherigen Schritt des Tutorials", Idioma.FR to "Revenir à l'étape précédente du tutoriel",
        Idioma.IT to "Torna al passo precedente del tutorial", Idioma.PT to "Voltar ao passo anterior do tutorial",
    ),
    ClaveTexto.TOUR_SIGUIENTE to mapOf(
        Idioma.ES to "Siguiente", Idioma.EN to "Next", Idioma.DE to "Weiter",
        Idioma.FR to "Suivant", Idioma.IT to "Avanti", Idioma.PT to "Seguinte",
    ),
    ClaveTexto.TOUR_SALTAR to mapOf(
        Idioma.ES to "Saltar", Idioma.EN to "Skip", Idioma.DE to "Überspringen",
        Idioma.FR to "Passer", Idioma.IT to "Salta", Idioma.PT to "Pular",
    ),
    ClaveTexto.TOUR_LISTO to mapOf(
        Idioma.ES to "Listo", Idioma.EN to "Done", Idioma.DE to "Fertig",
        Idioma.FR to "Terminé", Idioma.IT to "Fatto", Idioma.PT to "Pronto",
    ),
    ClaveTexto.TOUR_PASO to mapOf(
        Idioma.ES to "Paso {n} de {total}", Idioma.EN to "Step {n} of {total}", Idioma.DE to "Schritt {n} von {total}",
        Idioma.FR to "Étape {n} sur {total}", Idioma.IT to "Passo {n} di {total}", Idioma.PT to "Passo {n} de {total}",
    ),
    ClaveTexto.TOUR_SIGUIENTE_CD to mapOf(
        Idioma.ES to "Ir al paso siguiente del tutorial", Idioma.EN to "Go to the next tutorial step",
        Idioma.DE to "Zum nächsten Schritt des Tutorials", Idioma.FR to "Passer à l'étape suivante du tutoriel",
        Idioma.IT to "Vai al passo successivo del tutorial", Idioma.PT to "Ir para o próximo passo do tutorial",
    ),
    ClaveTexto.TOUR_SALTAR_CD to mapOf(
        Idioma.ES to "Saltar todo el tutorial", Idioma.EN to "Skip the whole tutorial",
        Idioma.DE to "Das ganze Tutorial überspringen", Idioma.FR to "Passer tout le tutoriel",
        Idioma.IT to "Salta tutto il tutorial", Idioma.PT to "Pular todo o tutorial",
    ),
    ClaveTexto.TOUR_LISTO_CD to mapOf(
        Idioma.ES to "Terminar este recorrido", Idioma.EN to "Finish this tour",
        Idioma.DE to "Diese Tour beenden", Idioma.FR to "Terminer cette visite",
        Idioma.IT to "Termina questo percorso", Idioma.PT to "Terminar este percurso",
    ),
    // --- Tutorial con foco (Ronda Q): titulo (T) y cuerpo (C) de cada paso, ES fuente = docs/tutorial-guion.md ---
    ClaveTexto.TOUR_FICHA_1_T to mapOf(
        Idioma.ES to "Esta es tu semana",
        Idioma.EN to "This is your week",
        Idioma.DE to "Das ist deine Woche",
        Idioma.FR to "Voici ta semaine",
        Idioma.IT to "Questa è la tua settimana",
        Idioma.PT to "Esta é a sua semana",
    ),
    ClaveTexto.TOUR_FICHA_1_C to mapOf(
        Idioma.ES to "Un tema y una habilidad, del lunes al domingo. No hace falta terminarla: haz lo que puedas.",
        Idioma.EN to "One topic and one skill, from Monday to Sunday. You don't have to finish it: do what you can.",
        Idioma.DE to "Ein Thema und eine Fähigkeit, von Montag bis Sonntag. Du musst nicht alles schaffen: Mach, was du kannst.",
        Idioma.FR to "Un thème et une compétence, du lundi au dimanche. Pas besoin de tout finir : fais ce que tu peux.",
        Idioma.IT to "Un tema e un'abilità, dal lunedì alla domenica. Non serve finire tutto: fai quello che puoi.",
        Idioma.PT to "Um tema e uma habilidade, de segunda a domingo. Não precisa terminar tudo: faça o que puder.",
    ),
    ClaveTexto.TOUR_FICHA_6_T to mapOf(
        Idioma.ES to "La misión",
        Idioma.EN to "The mission",
        Idioma.DE to "Die Wochenaufgabe",
        Idioma.FR to "La mission",
        Idioma.IT to "La missione",
        Idioma.PT to "A missão",
    ),
    ClaveTexto.TOUR_FICHA_6_C to mapOf(
        Idioma.ES to "Lo que te pedimos que produzcas esta semana, con sus requisitos y sus micro-tareas si quieres ir por partes.",
        Idioma.EN to "What we ask you to produce this week, with its requirements and micro-tasks if you want to go step by step.",
        Idioma.DE to "Das, was du diese Woche hervorbringen sollst, mit Anforderungen und Mikroaufgaben, falls du in Etappen vorgehen willst.",
        Idioma.FR to "Ce que nous te demandons de produire cette semaine, avec ses exigences et ses micro-tâches si tu préfères avancer par étapes.",
        Idioma.IT to "Ciò che ti chiediamo di produrre questa settimana, con i suoi requisiti e le sue micro-attività se vuoi procedere per gradi.",
        Idioma.PT to "O que pedimos que você produza nesta semana, com seus requisitos e suas microtarefas se quiser ir por partes.",
    ),
    ClaveTexto.TOUR_FICHA_3_T to mapOf(
        Idioma.ES to "Para entender de qué va",
        Idioma.EN to "To understand what it's about",
        Idioma.DE to "Um zu verstehen, worum es geht",
        Idioma.FR to "Pour comprendre de quoi il s'agit",
        Idioma.IT to "Per capire di cosa si tratta",
        Idioma.PT to "Para entender do que se trata",
    ),
    ClaveTexto.TOUR_FICHA_3_C to mapOf(
        Idioma.ES to "La descripción, el cuadro de referencia, los ejemplos y las notas. Es la explicación corta, no un capítulo de gramática.",
        Idioma.EN to "The description, the reference table, the examples and the notes. It's the short explanation, not a grammar chapter.",
        Idioma.DE to "Beschreibung, Übersichtskasten, Beispiele und Hinweise. Das ist die kurze Erklärung, kein Grammatikkapitel.",
        Idioma.FR to "La description, le tableau de référence, les exemples et les notes. C'est l'explication courte, pas un chapitre de grammaire.",
        Idioma.IT to "La descrizione, il quadro di riferimento, gli esempi e le note. È la spiegazione breve, non un capitolo di grammatica.",
        Idioma.PT to "A descrição, o quadro de referência, os exemplos e as notas. É a explicação curta, não um capítulo de gramática.",
    ),
    ClaveTexto.TOUR_FICHA_4_T to mapOf(
        Idioma.ES to "Lo que se compara y lo que falla",
        Idioma.EN to "What gets compared and what goes wrong",
        Idioma.DE to "Was verglichen wird und was schiefgeht",
        Idioma.FR to "Ce qu'on compare et ce qui coince",
        Idioma.IT to "Ciò che si confronta e ciò che non va",
        Idioma.PT to "O que se compara e o que dá errado",
    ),
    ClaveTexto.TOUR_FICHA_4_C to mapOf(
        Idioma.ES to "El contraste y los errores típicos: qué cambia respecto de tu idioma y en qué solemos equivocarnos.",
        Idioma.EN to "The contrast and the typical mistakes: what differs from your language and where we usually go wrong.",
        Idioma.DE to "Der Kontrast und die typischen Fehler: was sich gegenüber deiner Sprache ändert und wo wir uns meistens irren.",
        Idioma.FR to "Le contraste et les erreurs fréquentes : ce qui change par rapport à ta langue et où l'on se trompe d'habitude.",
        Idioma.IT to "Il confronto e gli errori comuni: cosa cambia rispetto alla tua lingua e dove di solito sbagliamo.",
        Idioma.PT to "O contraste e os erros comuns: o que muda em relação ao seu idioma e onde costumamos errar.",
    ),
    ClaveTexto.TOUR_FICHA_5_T to mapOf(
        Idioma.ES to "Vocabulario y expresiones",
        Idioma.EN to "Vocabulary and expressions",
        Idioma.DE to "Wortschatz und Redemittel",
        Idioma.FR to "Vocabulaire et expressions",
        Idioma.IT to "Vocabolario ed espressioni",
        Idioma.PT to "Vocabulário e expressões",
    ),
    ClaveTexto.TOUR_FICHA_5_C to mapOf(
        Idioma.ES to "Toca el encabezado para abrir la lista. Con la estrella guardas lo que te sirva; después lo encontrarás en Guardados.",
        Idioma.EN to "Tap the heading to open the list. Use the star to save whatever is useful; you'll find it later in Saved.",
        Idioma.DE to "Tippe auf die Überschrift, um die Liste zu öffnen. Mit dem Stern speicherst du, was dir nützt; später findest du es unter Gespeichert.",
        Idioma.FR to "Touche l'en-tête pour ouvrir la liste. Avec l'étoile, tu enregistres ce qui t'est utile ; tu le retrouveras ensuite dans Enregistrés.",
        Idioma.IT to "Tocca l'intestazione per aprire l'elenco. Con la stella salvi ciò che ti serve; poi lo troverai in Salvati.",
        Idioma.PT to "Toque no cabeçalho para abrir a lista. Com a estrela você salva o que for útil; depois vai encontrar em Salvos.",
    ),
    ClaveTexto.TOUR_FICHA_7_T to mapOf(
        Idioma.ES to "El autochequeo",
        Idioma.EN to "The self-check",
        Idioma.DE to "Die Selbstkontrolle",
        Idioma.FR to "L'auto-évaluation",
        Idioma.IT to "L'autoverifica",
        Idioma.PT to "A autoavaliação",
    ),
    ClaveTexto.TOUR_FICHA_7_C to mapOf(
        Idioma.ES to "Antes de darla por hecha, mira si cumples esto.",
        Idioma.EN to "Before calling it done, check whether you meet this.",
        Idioma.DE to "Bevor du die Woche als erledigt betrachtest, prüfe, ob du das erfüllst.",
        Idioma.FR to "Avant de la considérer comme terminée, vérifie que tu remplis ceci.",
        Idioma.IT to "Prima di darla per conclusa, controlla se rispetti questo.",
        Idioma.PT to "Antes de dar a semana por concluída, veja se você cumpre isto.",
    ),
    ClaveTexto.TOUR_FICHA_8_T to mapOf(
        Idioma.ES to "Los prompts y la planilla",
        Idioma.EN to "The prompts and the sheet",
        Idioma.DE to "Die Prompts und das Blatt",
        Idioma.FR to "Les prompts et la fiche",
        Idioma.IT to "I prompt e la scheda",
        Idioma.PT to "Os prompts e a ficha",
    ),
    ClaveTexto.TOUR_FICHA_8_C to mapOf(
        Idioma.ES to "Un texto listo para pegarle a una IA y que te corrija, y una hoja para imprimir y darle a quien te enseña. Van siempre en el idioma que estás aprendiendo, para que la respuesta también.",
        Idioma.EN to "A text ready to paste into an AI so it corrects you, and a sheet to print and hand to whoever teaches you. They are always in the language you're learning, so the reply is too.",
        Idioma.DE to "Ein Text, den du einer KI zum Korrigieren einfügen kannst, und ein Blatt zum Ausdrucken für alle, die dich unterrichten. Beide sind immer in der Sprache, die du lernst, damit auch die Antwort darin kommt.",
        Idioma.FR to "Un texte prêt à coller dans une IA pour qu'elle te corrige, et une fiche à imprimer pour la remettre à la personne qui t'enseigne. Ils sont toujours dans la langue que tu apprends, pour que la réponse le soit aussi.",
        Idioma.IT to "Un testo pronto da incollare in un'IA perché ti corregga, e una scheda da stampare e consegnare a chi ti insegna. Sono sempre nella lingua che stai imparando, così anche la risposta lo è.",
        Idioma.PT to "Um texto pronto para colar em uma IA e ela corrigir você, e uma folha para imprimir e entregar a quem ensina você. Ficam sempre no idioma que você está aprendendo, para que a resposta também fique.",
    ),
    ClaveTexto.TOUR_FICHA_10_T to mapOf(
        Idioma.ES to "Biblioteca, Guardados y Ajustes",
        Idioma.EN to "Library, Saved and Settings",
        Idioma.DE to "Bibliothek, Gespeichert und Einstellungen",
        Idioma.FR to "Bibliothèque, Enregistrés et Paramètres",
        Idioma.IT to "Biblioteca, Salvati e Impostazioni",
        Idioma.PT to "Biblioteca, Salvos e Configurações",
    ),
    ClaveTexto.TOUR_FICHA_10_C to mapOf(
        Idioma.ES to "Arriba tienes Biblioteca (todo el vocabulario y las expresiones del año), Guardados (lo que marcaste con la estrella) y Ajustes. Cada una se explica sola cuando entres.",
        Idioma.EN to "At the top you have Library (all of the year's vocabulary and expressions), Saved (what you starred) and Settings. Each one explains itself when you open it.",
        Idioma.DE to "Oben findest du Bibliothek (der gesamte Wortschatz und alle Redewendungen des Jahres), Gespeichert (was du mit dem Stern markiert hast) und Einstellungen. Jede erklärt sich selbst, wenn du sie öffnest.",
        Idioma.FR to "En haut, tu as Bibliothèque (tout le vocabulaire et les expressions de l'année), Enregistrés (ce que tu as marqué d'une étoile) et Paramètres. Chacune s'explique d'elle-même quand tu y entres.",
        Idioma.IT to "In alto trovi Biblioteca (tutto il vocabolario e le espressioni dell'anno), Salvati (ciò che hai segnato con la stella) e Impostazioni. Ognuna si spiega da sola quando ci entri.",
        Idioma.PT to "Em cima você tem Biblioteca (todo o vocabulário e as expressões do ano), Salvos (o que você marcou com a estrela) e Configurações. Cada uma se explica sozinha quando você entra.",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_1_T to mapOf(
        Idioma.ES to "Tres vistas",
        Idioma.EN to "Three views",
        Idioma.DE to "Drei Ansichten",
        Idioma.FR to "Trois vues",
        Idioma.IT to "Tre viste",
        Idioma.PT to "Três visões",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_1_C to mapOf(
        Idioma.ES to "Vocabulario y expresiones, Habilidades y Verbos.",
        Idioma.EN to "Vocabulary and expressions, Skills and Verbs.",
        Idioma.DE to "Wortschatz und Redewendungen, Fähigkeiten und Verben.",
        Idioma.FR to "Vocabulaire et expressions, Compétences et Verbes.",
        Idioma.IT to "Vocabolario ed espressioni, Abilità e Verbi.",
        Idioma.PT to "Vocabulário e expressões, Habilidades e Verbos.",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_2_T to mapOf(
        Idioma.ES to "Los filtros se arman solos",
        Idioma.EN to "The filters build themselves",
        Idioma.DE to "Die Filter bauen sich von selbst auf",
        Idioma.FR to "Les filtres se composent tout seuls",
        Idioma.IT to "I filtri si costruiscono da soli",
        Idioma.PT to "Os filtros se montam sozinhos",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_2_C to mapOf(
        Idioma.ES to "Salen de lo que hay en tu idioma y tu nivel: si un filtro no aparece, es que no hay nada que filtrar.",
        Idioma.EN to "They come from what exists in your language and level: if a filter doesn't appear, there is nothing to filter.",
        Idioma.DE to "Sie ergeben sich aus dem, was es in deiner Sprache und auf deinem Niveau gibt: Erscheint ein Filter nicht, gibt es nichts zu filtern.",
        Idioma.FR to "Ils viennent de ce qui existe dans ta langue et ton niveau : si un filtre n'apparaît pas, c'est qu'il n'y a rien à filtrer.",
        Idioma.IT to "Nascono da ciò che c'è nella tua lingua e nel tuo livello: se un filtro non compare, non c'è nulla da filtrare.",
        Idioma.PT to "Eles saem do que existe no seu idioma e no seu nível: se um filtro não aparece, é porque não há nada para filtrar.",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_3_T to mapOf(
        Idioma.ES to "La búsqueda",
        Idioma.EN to "Search",
        Idioma.DE to "Die Suche",
        Idioma.FR to "La recherche",
        Idioma.IT to "La ricerca",
        Idioma.PT to "A busca",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_3_C to mapOf(
        Idioma.ES to "No distingue mayúsculas ni tildes, y busca también en la traducción.",
        Idioma.EN to "It ignores capitals and accents, and it also searches the translation.",
        Idioma.DE to "Sie unterscheidet nicht zwischen Gross- und Kleinschreibung oder Akzenten und sucht auch in der Übersetzung.",
        Idioma.FR to "Elle ne distingue ni majuscules ni accents, et cherche aussi dans la traduction.",
        Idioma.IT to "Non distingue maiuscole né accenti e cerca anche nella traduzione.",
        Idioma.PT to "Não diferencia maiúsculas nem acentos e busca também na tradução.",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_4_T to mapOf(
        Idioma.ES to "Verbos",
        Idioma.EN to "Verbs",
        Idioma.DE to "Verben",
        Idioma.FR to "Verbes",
        Idioma.IT to "Verbi",
        Idioma.PT to "Verbos",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_4_C to mapOf(
        Idioma.ES to "Están todos los del idioma, de cualquier nivel, agrupados por verbo; debajo de algunos verás las frases donde se usan. Busca la raíz («stellen») para ver la familia entera.",
        Idioma.EN to "All the verbs of the language, from any level, grouped by verb; under some you'll see the phrases where they are used. Search the stem («stellen») to see the whole family.",
        Idioma.DE to "Alle Verben der Sprache, aus jedem Niveau, nach Verb gruppiert; unter manchen siehst du die Wendungen, in denen sie vorkommen. Suche den Stamm («stellen»), um die ganze Familie zu sehen.",
        Idioma.FR to "Tous les verbes de la langue, de n'importe quel niveau, regroupés par verbe ; sous certains, tu verras les phrases où ils s'emploient. Cherche la racine («stellen») pour voir toute la famille.",
        Idioma.IT to "Ci sono tutti i verbi della lingua, di qualsiasi livello, raggruppati per verbo; sotto alcuni vedrai le frasi in cui si usano. Cerca la radice («stellen») per vedere l'intera famiglia.",
        Idioma.PT to "Estão todos os verbos do idioma, de qualquer nível, agrupados por verbo; sob alguns você verá as frases em que são usados. Busque a raiz («stellen») para ver a família inteira.",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_5_T to mapOf(
        Idioma.ES to "La estrella",
        Idioma.EN to "The star",
        Idioma.DE to "Der Stern",
        Idioma.FR to "L'étoile",
        Idioma.IT to "La stella",
        Idioma.PT to "A estrela",
    ),
    ClaveTexto.TOUR_BIBLIOTECA_5_C to mapOf(
        Idioma.ES to "Hace lo mismo que en la ficha.",
        Idioma.EN to "It does the same as on the sheet.",
        Idioma.DE to "Sie tut dasselbe wie auf dem Blatt.",
        Idioma.FR to "Elle fait la même chose que sur la fiche.",
        Idioma.IT to "Fa la stessa cosa che nella scheda.",
        Idioma.PT to "Faz o mesmo que na ficha.",
    ),
    ClaveTexto.TOUR_GUARDADOS_1_T to mapOf(
        Idioma.ES to "Aquí está lo que marcaste",
        Idioma.EN to "Here's what you starred",
        Idioma.DE to "Hier steht, was du markiert hast",
        Idioma.FR to "Voici ce que tu as marqué",
        Idioma.IT to "Qui c'è ciò che hai segnato",
        Idioma.PT to "Aqui está o que você marcou",
    ),
    ClaveTexto.TOUR_GUARDADOS_1_C to mapOf(
        Idioma.ES to "Se ve igual que en la Biblioteca.",
        Idioma.EN to "It looks the same as in the Library.",
        Idioma.DE to "Es sieht genauso aus wie in der Bibliothek.",
        Idioma.FR to "Cela s'affiche comme dans la Bibliothèque.",
        Idioma.IT to "Si vede come nella Biblioteca.",
        Idioma.PT to "Aparece como na Biblioteca.",
    ),
    ClaveTexto.TOUR_GUARDADOS_2_T to mapOf(
        Idioma.ES to "Exportar",
        Idioma.EN to "Export",
        Idioma.DE to "Exportieren",
        Idioma.FR to "Exporter",
        Idioma.IT to "Esporta",
        Idioma.PT to "Exportar",
    ),
    ClaveTexto.TOUR_GUARDADOS_2_C to mapOf(
        Idioma.ES to "A Anki o como copia de seguridad, eligiendo tú el archivo.",
        Idioma.EN to "To Anki or as a backup, with you choosing the file.",
        Idioma.DE to "Nach Anki oder als Sicherungskopie; die Datei wählst du selbst.",
        Idioma.FR to "Vers Anki ou en copie de sauvegarde, avec le fichier que tu choisis.",
        Idioma.IT to "Verso Anki o come copia di sicurezza, scegliendo tu il file.",
        Idioma.PT to "Para o Anki ou como cópia de segurança, escolhendo você o arquivo.",
    ),
    ClaveTexto.TOUR_GUARDADOS_3_T to mapOf(
        Idioma.ES to "Importar",
        Idioma.EN to "Import",
        Idioma.DE to "Importieren",
        Idioma.FR to "Importer",
        Idioma.IT to "Importa",
        Idioma.PT to "Importar",
    ),
    ClaveTexto.TOUR_GUARDADOS_3_C to mapOf(
        Idioma.ES to "Une lo que traes con lo que ya tienes, sin duplicar.",
        Idioma.EN to "It merges what you bring with what you already have, without duplicates.",
        Idioma.DE to "Es führt zusammen, was du mitbringst und was du schon hast, ohne Duplikate.",
        Idioma.FR to "Cela réunit ce que tu apportes et ce que tu as déjà, sans doublons.",
        Idioma.IT to "Unisce ciò che porti con ciò che hai già, senza duplicati.",
        Idioma.PT to "Une o que você traz com o que já tem, sem duplicar.",
    ),
    ClaveTexto.TOUR_AJUSTES_1_T to mapOf(
        Idioma.ES to "Idioma de la app y qué practicas",
        Idioma.EN to "App language and what you practise",
        Idioma.DE to "Sprache der App und was du übst",
        Idioma.FR to "Langue de l'appli et ce que tu pratiques",
        Idioma.IT to "Lingua dell'app e cosa pratichi",
        Idioma.PT to "Idioma do app e o que você pratica",
    ),
    ClaveTexto.TOUR_AJUSTES_1_C to mapOf(
        Idioma.ES to "Aquí cambias el idioma de la app y los idiomas y niveles que practicas.",
        Idioma.EN to "Here you change the app language and the languages and levels you practise.",
        Idioma.DE to "Hier änderst du die Sprache der App sowie die Sprachen und Niveaus, die du übst.",
        Idioma.FR to "Ici, tu changes la langue de l'appli ainsi que les langues et niveaux que tu pratiques.",
        Idioma.IT to "Qui cambi la lingua dell'app e le lingue e i livelli che pratichi.",
        Idioma.PT to "Aqui você muda o idioma do app e os idiomas e níveis que pratica.",
    ),
    ClaveTexto.TOUR_AJUSTES_2_T to mapOf(
        Idioma.ES to "Tema y tipografía",
        Idioma.EN to "Theme and typeface",
        Idioma.DE to "Erscheinungsbild und Schrift",
        Idioma.FR to "Thème et police",
        Idioma.IT to "Tema e carattere",
        Idioma.PT to "Tema e fonte",
    ),
    ClaveTexto.TOUR_AJUSTES_2_C to mapOf(
        Idioma.ES to "Elige lo que te resulte más cómodo de leer.",
        Idioma.EN to "Pick whatever is most comfortable for you to read.",
        Idioma.DE to "Wähle, was für dich am angenehmsten zu lesen ist.",
        Idioma.FR to "Choisis ce qui est le plus confortable à lire pour toi.",
        Idioma.IT to "Scegli ciò che ti è più comodo da leggere.",
        Idioma.PT to "Escolha o que for mais confortável para ler.",
    ),
    ClaveTexto.TOUR_AJUSTES_3_T to mapOf(
        Idioma.ES to "Acerca de",
        Idioma.EN to "About",
        Idioma.DE to "Über die App",
        Idioma.FR to "À propos",
        Idioma.IT to "Informazioni",
        Idioma.PT to "Sobre",
    ),
    ClaveTexto.TOUR_AJUSTES_3_C to mapOf(
        Idioma.ES to "Con lo que la app guarda y lo que no.",
        Idioma.EN to "With what the app stores and what it doesn't.",
        Idioma.DE to "Mit dem, was die App speichert und was nicht.",
        Idioma.FR to "Avec ce que l'appli enregistre et ce qu'elle n'enregistre pas.",
        Idioma.IT to "Con ciò che l'app salva e ciò che non salva.",
        Idioma.PT to "Com o que o app salva e o que não salva.",
    ),
    ClaveTexto.TOUR_AJUSTES_4_T to mapOf(
        Idioma.ES to "Repetir el tutorial",
        Idioma.EN to "Repeat the tutorial",
        Idioma.DE to "Das Tutorial wiederholen",
        Idioma.FR to "Refaire le tutoriel",
        Idioma.IT to "Ripetere il tutorial",
        Idioma.PT to "Repetir o tutorial",
    ),
    ClaveTexto.TOUR_AJUSTES_4_C to mapOf(
        Idioma.ES to "Desde aquí puedes volver a ver la bienvenida y el tutorial cuando quieras.",
        Idioma.EN to "From here you can see the welcome screen and the tutorial again whenever you like.",
        Idioma.DE to "Von hier aus kannst du den Willkommensbildschirm und das Tutorial jederzeit erneut ansehen.",
        Idioma.FR to "D'ici, tu peux revoir l'écran de bienvenue et le tutoriel quand tu veux.",
        Idioma.IT to "Da qui puoi rivedere la schermata di benvenuto e il tutorial quando vuoi.",
        Idioma.PT to "Daqui você pode rever as boas-vindas e o tutorial quando quiser.",
    ),
    ClaveTexto.TOUR_DESAFIO_1_T to mapOf(
        Idioma.ES to "Esto es para hablar",
        Idioma.EN to "This is for speaking",
        Idioma.DE to "Das ist zum Sprechen",
        Idioma.FR to "C'est pour parler",
        Idioma.IT to "Questo è per parlare",
        Idioma.PT to "Isto é para falar",
    ),
    ClaveTexto.TOUR_DESAFIO_1_C to mapOf(
        Idioma.ES to "No para escribir. La consigna está arriba y las condiciones debajo.",
        Idioma.EN to "Not for writing. The task is at the top and the conditions below.",
        Idioma.DE to "Nicht zum Schreiben. Die Aufgabe steht oben, die Bedingungen darunter.",
        Idioma.FR to "Pas pour écrire. La consigne est en haut et les conditions en dessous.",
        Idioma.IT to "Non per scrivere. La consegna è in alto e le condizioni sotto.",
        Idioma.PT to "Não é para escrever. O enunciado está em cima e as condições embaixo.",
    ),
    ClaveTexto.TOUR_DESAFIO_2_T to mapOf(
        Idioma.ES to "El interruptor arranca apagado",
        Idioma.EN to "The switch starts off",
        Idioma.DE to "Der Schalter ist zu Beginn aus",
        Idioma.FR to "L'interrupteur démarre éteint",
        Idioma.IT to "L'interruttore parte spento",
        Idioma.PT to "O interruptor começa desligado",
    ),
    ClaveTexto.TOUR_DESAFIO_2_C to mapOf(
        Idioma.ES to "Cada vez que abres la app: no se guarda nada.",
        Idioma.EN to "Every time you open the app: nothing is saved.",
        Idioma.DE to "Bei jedem Öffnen der App: Es wird nichts gespeichert.",
        Idioma.FR to "À chaque ouverture de l'appli : rien n'est enregistré.",
        Idioma.IT to "A ogni apertura dell'app: non si salva nulla.",
        Idioma.PT to "Toda vez que você abre o app: nada é salvo.",
    ),
    // --- Tutorial: pasos nuevos de la ficha (Ronda R). TEXTO PROVISIONAL: Fer edita los textos en docs/tutorial-textos.md ---
    ClaveTexto.TOUR_FICHA_2_T to mapOf(
        Idioma.ES to "Ficha en tu idioma",
        Idioma.EN to "Sheet in your language",
        Idioma.DE to "Blatt in deiner Sprache",
        Idioma.FR to "Fiche dans ta langue",
        Idioma.IT to "Scheda nella tua lingua",
        Idioma.PT to "Ficha no seu idioma",
    ),
    ClaveTexto.TOUR_FICHA_2_C to mapOf(
        Idioma.ES to "En A2 y B1 puedes ver la ficha traducida a tu idioma o en el idioma que estás aprendiendo. Desde B2 ya no aparece: la ficha va siempre en el idioma que aprendes.",
        Idioma.EN to "In A2 and B1 you can see the sheet translated into your language or in the language you're learning. From B2 on it no longer appears: the sheet is always in the language you're learning.",
        Idioma.DE to "In A2 und B1 kannst du das Blatt in deine Sprache übersetzt oder in der Sprache sehen, die du lernst. Ab B2 erscheint dieser Schalter nicht mehr: Das Blatt ist immer in der Sprache, die du lernst.",
        Idioma.FR to "En A2 et B1, tu peux voir la fiche traduite dans ta langue ou dans la langue que tu apprends. À partir de B2, cet interrupteur n'apparaît plus : la fiche est toujours dans la langue que tu apprends.",
        Idioma.IT to "In A2 e B1 puoi vedere la scheda tradotta nella tua lingua o nella lingua che stai imparando. Da B2 questo interruttore non compare più: la scheda è sempre nella lingua che impari.",
        Idioma.PT to "Em A2 e B1 você pode ver a ficha traduzida para o seu idioma ou no idioma que está aprendendo. A partir do B2 este interruptor não aparece mais: a ficha fica sempre no idioma que você aprende.",
    ),
    ClaveTexto.TOUR_FICHA_9_T to mapOf(
        Idioma.ES to "La planilla para tu profesor",
        Idioma.EN to "The sheet for your teacher",
        Idioma.DE to "Das Blatt für deine Lehrperson",
        Idioma.FR to "La fiche pour ton enseignant",
        Idioma.IT to "La scheda per il tuo insegnante",
        Idioma.PT to "A ficha para o seu professor",
    ),
    ClaveTexto.TOUR_FICHA_9_C to mapOf(
        Idioma.ES to "Genera una hoja en PDF, en el idioma que aprendes y en una sola carilla, para imprimir y darle a quien te da clase.",
        Idioma.EN to "It generates a PDF sheet, in the language you're learning and on a single page, to print and hand to whoever teaches you.",
        Idioma.DE to "Es erzeugt ein PDF-Blatt in der Sprache, die du lernst, auf einer einzigen Seite, zum Ausdrucken und Weitergeben an die Person, die dich unterrichtet.",
        Idioma.FR to "Elle génère une fiche en PDF, dans la langue que tu apprends et sur une seule page, à imprimer et à remettre à la personne qui t'enseigne.",
        Idioma.IT to "Genera una scheda in PDF, nella lingua che impari e su una sola facciata, da stampare e consegnare a chi ti insegna.",
        Idioma.PT to "Gera uma folha em PDF, no idioma que você aprende e em uma única página, para imprimir e entregar a quem dá aula para você.",
    ),
    // --- Velocidad del audio (Ronda O) ---
    ClaveTexto.VELOCIDAD_AUDIO_TITULO to mapOf(
        Idioma.ES to "Velocidad del audio", Idioma.EN to "Audio speed", Idioma.DE to "Audiogeschwindigkeit",
        Idioma.FR to "Vitesse de l'audio", Idioma.IT to "Velocità dell'audio", Idioma.PT to "Velocidade do áudio",
    ),
    ClaveTexto.VELOCIDAD_AUDIO_AYUDA to mapOf(
        Idioma.ES to "Para escuchar las palabras y las frases más despacio.",
        Idioma.EN to "To hear words and phrases more slowly.",
        Idioma.DE to "Um Wörter und Sätze langsamer zu hören.",
        Idioma.FR to "Pour écouter les mots et les phrases plus lentement.",
        Idioma.IT to "Per ascoltare parole e frasi più lentamente.",
        Idioma.PT to "Para ouvir as palavras e as frases mais devagar.",
    ),
    // El porcentaje se escribe como en cada idioma (con o sin espacio antes del %).
    ClaveTexto.VELOCIDAD_AUDIO_VALOR to mapOf(
        Idioma.ES to "{n}\u00A0%", Idioma.EN to "{n}%", Idioma.DE to "{n}\u00A0%",
        Idioma.FR to "{n}\u00A0%", Idioma.IT to "{n}%", Idioma.PT to "{n}%",
    ),
    // --- Bienvenida (Ronda N) ---
    ClaveTexto.BIENVENIDA_TITULO to mapOf(
        Idioma.ES to "Te damos la bienvenida", Idioma.EN to "Welcome", Idioma.DE to "Willkommen",
        Idioma.FR to "Bienvenue", Idioma.IT to "Ti diamo il benvenuto", Idioma.PT to "Boas-vindas",
    ),
    // Antes de elegir el idioma la app todavia no sabe en cual hablarte: el rotulo es el mismo en los 6.
    ClaveTexto.BIENVENIDA_IDIOMA_INICIAL to Idioma.entries.associateWith { "Idioma · Language · Sprache · Langue · Lingua · Idioma" },
    ClaveTexto.BIENVENIDA_ELEGIR_PRACTICA to mapOf(
        Idioma.ES to "Elige al menos un idioma para practicar.",
        Idioma.EN to "Choose at least one language to practise.",
        Idioma.DE to "Wähle mindestens eine Sprache zum Üben.",
        Idioma.FR to "Choisis au moins une langue à pratiquer.",
        Idioma.IT to "Scegli almeno una lingua da praticare.",
        Idioma.PT to "Escolha pelo menos um idioma para praticar.",
    ),
    ClaveTexto.BIENVENIDA_EMPEZAR to mapOf(
        Idioma.ES to "Empezar", Idioma.EN to "Start", Idioma.DE to "Los geht's",
        Idioma.FR to "Commencer", Idioma.IT to "Inizia", Idioma.PT to "Começar",
    ),
    ClaveTexto.BIENVENIDA_VER_DE_NUEVO to mapOf(
        Idioma.ES to "Ver la bienvenida y el tutorial otra vez", Idioma.EN to "See the welcome screen and the tutorial again",
        Idioma.DE to "Willkommensbildschirm und Tutorial erneut ansehen", Idioma.FR to "Revoir l'écran de bienvenue et le tutoriel",
        Idioma.IT to "Rivedi la schermata di benvenuto e il tutorial", Idioma.PT to "Ver as boas-vindas e o tutorial de novo",
    ),
    ClaveTexto.ACERCA_PRIVACIDAD_TITULO to mapOf(
        Idioma.ES to "Privacidad",
        Idioma.EN to "Privacy",
        Idioma.DE to "Datenschutz",
        Idioma.FR to "Confidentialité",
        Idioma.IT to "Privacy",
        Idioma.PT to "Privacidade",
    ),
    ClaveTexto.ACERCA_PRIVACIDAD_TEXTO to mapOf(
        Idioma.ES to "Sin Internet y sin cuenta: la app no puede enviar nada a ningún sitio. Solo lee la fecha del dispositivo (y su idioma, únicamente si lo eliges en Ajustes). Lo único que se guarda en tu celular son tus ajustes y los elementos que marcas con la estrella en Guardados. No utiliza cuentas ni estadísticas de uso, no solicita permiso de Internet ni de ubicación, y no envía información a terceros. Si exportas o importas tus Guardados, lo haces con un archivo que eliges tú en ese momento; la app no lee ningún otro archivo.",
        Idioma.EN to "No internet and no account: the app cannot send anything anywhere. It only reads the device's date (and its language, only if you choose that in Settings). The only things stored on your phone are your settings and what you mark with a star in Saved. It uses no accounts or usage statistics, does not request permission for internet or location, and sends no information to third parties. If you export or import your Saved items, you do it with a file you choose at that moment; the app reads no other file.",
        Idioma.DE to "Kein Internet, kein Konto: Die App kann nirgendwohin etwas senden. Sie liest nur das Datum des Geräts (und seine Sprache, nur wenn du das in den Einstellungen wählst). Auf deinem Handy gespeichert werden nur deine Einstellungen und das, was du unter Gespeichert mit dem Stern markierst. Sie verwendet weder Konten noch Nutzungsstatistiken, fordert keine Berechtigung für Internet oder Standort an und sendet keine Informationen an Dritte. Wenn du deine gespeicherten Einträge exportierst oder importierst, geschieht das mit einer Datei, die du in diesem Moment selbst auswählst; die App liest keine andere Datei.",
        Idioma.FR to "Pas d'Internet, pas de compte : l'application ne peut rien envoyer nulle part. Elle lit seulement la date de l'appareil (et sa langue, uniquement si tu le choisis dans Paramètres). Seuls tes paramètres et les éléments que tu marques d'une étoile dans Enregistrés sont conservés sur ton téléphone. Elle n'utilise ni compte ni statistiques d'utilisation, ne demande aucune autorisation d'accès à Internet ni à la localisation, et n'envoie aucune information à des tiers. Si tu exportes ou importes tes éléments enregistrés, tu le fais avec un fichier que tu choisis à ce moment-là ; l'application ne lit aucun autre fichier.",
        Idioma.IT to "Niente Internet e niente account: l'app non può inviare nulla da nessuna parte. Legge soltanto la data del dispositivo (e la sua lingua, solo se lo scegli in Impostazioni). Sul telefono vengono conservate soltanto le tue impostazioni e ciò che contrassegni con la stella in Salvati. Non usa account né statistiche d'uso, non richiede l'autorizzazione per Internet né per la posizione e non invia informazioni a terze parti. Se esporti o importi i tuoi elementi salvati, lo fai con un file che scegli tu in quel momento; l'app non legge nessun altro file.",
        Idioma.PT to "Sem internet e sem conta: o aplicativo não pode enviar nada para lugar nenhum. Ele só lê a data do dispositivo (e o idioma, somente se você escolher isso em Configurações). As únicas coisas salvas no seu celular são as suas configurações e os itens que você marca com a estrela em Salvos. Não usa contas nem estatísticas de uso, não solicita permissão de internet nem de localização e não envia informações a terceiros. Se você exportar ou importar os seus Salvos, isso é feito com um arquivo que você escolhe naquele momento; o aplicativo não lê nenhum outro arquivo.",
    ),
    ClaveTexto.ACERCA_LICENCIAS_TITULO to mapOf(
        Idioma.ES to "Licencias y créditos",
        Idioma.EN to "Licences and credits",
        Idioma.DE to "Lizenzen und Danksagungen",
        Idioma.FR to "Licences et crédits",
        Idioma.IT to "Licenze e crediti",
        Idioma.PT to "Licenças e créditos",
    ),
    ClaveTexto.ACERCA_LICENCIAS_TEXTO to mapOf(
        Idioma.ES to "Código: GPL-3.0. Contenido: CC BY-SA 4.0.",
        Idioma.EN to "Code: GPL-3.0. Content: CC BY-SA 4.0.",
        Idioma.DE to "Code: GPL-3.0. Inhalt: CC BY-SA 4.0.",
        Idioma.FR to "Code : GPL-3.0. Contenu : CC BY-SA 4.0.",
        Idioma.IT to "Codice: GPL-3.0. Contenuti: CC BY-SA 4.0.",
        Idioma.PT to "Código: GPL-3.0. Conteúdo: CC BY-SA 4.0.",
    ),
    ClaveTexto.ACERCA_VERSION to mapOf(
        Idioma.ES to "Versión {version}",
        Idioma.EN to "Version {version}",
        Idioma.DE to "Version {version}",
        Idioma.FR to "Version {version}",
        Idioma.IT to "Versione {version}",
        Idioma.PT to "Versão {version}",
    ),
    // --- Textos fijos de la ficha que habian quedado en espanol ---
    ClaveTexto.SEMANA_REPASO to mapOf(
        Idioma.ES to "Semana de repaso", Idioma.EN to "Review week", Idioma.DE to "Wiederholungswoche",
        Idioma.FR to "Semaine de révision", Idioma.IT to "Settimana di ripasso", Idioma.PT to "Semana de revisão",
    ),
    ClaveTexto.SEMANA_SURVIVAL to mapOf(
        Idioma.ES to "Semana Survival", Idioma.EN to "Survival week", Idioma.DE to "Survival-Woche",
        Idioma.FR to "Semaine Survival", Idioma.IT to "Settimana Survival", Idioma.PT to "Semana Survival",
    ),
    ClaveTexto.COPIAR_PORTAPAPELES to mapOf(
        Idioma.ES to "Copiar al portapapeles", Idioma.EN to "Copy to clipboard", Idioma.DE to "In die Zwischenablage kopieren",
        Idioma.FR to "Copier dans le presse-papiers", Idioma.IT to "Copia negli appunti", Idioma.PT to "Copiar para a área de transferência",
    ),
    ClaveTexto.COPIADO to mapOf(
        Idioma.ES to "Copiado", Idioma.EN to "Copied", Idioma.DE to "Kopiert",
        Idioma.FR to "Copié", Idioma.IT to "Copiato", Idioma.PT to "Copiado",
    ),
    // --- Prompt de conversacion de voz (Ronda B, pieza 2) ---
    ClaveTexto.PROMPT_VOZ_TITULO to mapOf(
        Idioma.ES to "Prompt para conversar por voz",
        Idioma.EN to "Prompt for a voice conversation",
        Idioma.DE to "Prompt für ein Sprachgespräch",
        Idioma.FR to "Prompt pour converser à l'oral",
        Idioma.IT to "Prompt per conversare a voce",
        Idioma.PT to "Prompt para conversar por voz",
    ),
    ClaveTexto.PROMPT_VOZ_AYUDA to mapOf(
        Idioma.ES to "Pégalo al empezar una conversación de voz con tu IA. Dile *feedback* cuando quieras la devolución.",
        Idioma.EN to "Paste it at the start of a voice conversation with your AI. Say *feedback* whenever you want the debrief.",
        Idioma.DE to "Füge ihn zu Beginn eines Sprachgesprächs mit deiner KI ein. Sag *feedback*, wenn du die Rückmeldung möchtest.",
        Idioma.FR to "Colle-le au début d'une conversation vocale avec ton IA. Dis *feedback* quand tu veux le retour.",
        Idioma.IT to "Incollalo all'inizio di una conversazione vocale con la tua IA. Di' *feedback* quando vuoi la valutazione.",
        Idioma.PT to "Cole no início de uma conversa por voz com a sua IA. Diga *feedback* quando quiser o retorno.",
    ),
    // --- Planilla del profesor (Ronda B, pieza 3) ---
    ClaveTexto.PLANILLA_BOTON to mapOf(
        Idioma.ES to "Planilla del profesor (PDF)",
        Idioma.EN to "Teacher's sheet (PDF)",
        Idioma.DE to "Unterrichtsblatt für Lehrkräfte (PDF)",
        Idioma.FR to "Fiche de l'enseignant (PDF)",
        Idioma.IT to "Scheda dell'insegnante (PDF)",
        Idioma.PT to "Ficha do professor (PDF)",
    ),
    ClaveTexto.PLANILLA_SIN_LECTOR to mapOf(
        Idioma.ES to "No hay ninguna aplicación para abrir o compartir el PDF.",
        Idioma.EN to "There is no app to open or share the PDF.",
        Idioma.DE to "Es gibt keine App, um das PDF zu öffnen oder zu teilen.",
        Idioma.FR to "Aucune application ne peut ouvrir ou partager le PDF.",
        Idioma.IT to "Nessuna app può aprire o condividere il PDF.",
        Idioma.PT to "Não há nenhum aplicativo para abrir ou compartilhar o PDF.",
    ),
    // --- Biblioteca (Ronda B, pieza 4) ---
    ClaveTexto.BIBLIOTECA to mapOf(
        Idioma.ES to "Biblioteca",
        Idioma.EN to "Library",
        Idioma.DE to "Bibliothek",
        Idioma.FR to "Bibliothèque",
        Idioma.IT to "Biblioteca",
        Idioma.PT to "Biblioteca",
    ),
    ClaveTexto.BIBLIOTECA_BUSCAR to mapOf(
        Idioma.ES to "Buscar",
        Idioma.EN to "Search",
        Idioma.DE to "Suchen",
        Idioma.FR to "Rechercher",
        Idioma.IT to "Cerca",
        Idioma.PT to "Buscar",
    ),
    ClaveTexto.BIBLIOTECA_TEMA to mapOf(
        Idioma.ES to "Tema",
        Idioma.EN to "Topic",
        Idioma.DE to "Thema",
        Idioma.FR to "Thème",
        Idioma.IT to "Tema",
        Idioma.PT to "Tema",
    ),
    ClaveTexto.BIBLIOTECA_SIN_CATEGORIA to mapOf(
        Idioma.ES to "Sin categoría",
        Idioma.EN to "No category",
        Idioma.DE to "Ohne Kategorie",
        Idioma.FR to "Sans catégorie",
        Idioma.IT to "Senza categoria",
        Idioma.PT to "Sem categoria",
    ),
    ClaveTexto.BIBLIOTECA_FUNCIONES to mapOf(
        Idioma.ES to "Funciones comunicativas",
        Idioma.EN to "Communicative functions",
        Idioma.DE to "Kommunikative Funktionen",
        Idioma.FR to "Fonctions communicatives",
        Idioma.IT to "Funzioni comunicative",
        Idioma.PT to "Funções comunicativas",
    ),
    ClaveTexto.BIBLIOTECA_PATRONES to mapOf(
        Idioma.ES to "Patrones gramaticales",
        Idioma.EN to "Grammar patterns",
        Idioma.DE to "Grammatikalische Muster",
        Idioma.FR to "Structures grammaticales",
        Idioma.IT to "Strutture grammaticali",
        Idioma.PT to "Padrões gramaticais",
    ),
    ClaveTexto.BIBLIOTECA_VACIO to mapOf(
        Idioma.ES to "Prueba con otros filtros.",
        Idioma.EN to "Try different filters.",
        Idioma.DE to "Versuche es mit anderen Filtern.",
        Idioma.FR to "Essaie avec d'autres filtres.",
        Idioma.IT to "Prova con altri filtri.",
        Idioma.PT to "Tente com outros filtros.",
    ),
    // --- Biblioteca/Guardados: limpiar filtros (Ronda C) ---
    ClaveTexto.QUITAR_FILTROS to mapOf(
        Idioma.ES to "Quitar filtros",
        Idioma.EN to "Clear filters",
        Idioma.DE to "Filter zurücksetzen",
        Idioma.FR to "Effacer les filtres",
        Idioma.IT to "Rimuovi filtri",
        Idioma.PT to "Remover filtros",
    ),
    // Version corta para los botones junto al titulo de cada rama de
    // categoriasUso (el titulo ya nombra el grupo, no hace falta repetirlo).
    ClaveTexto.QUITAR to mapOf(
        Idioma.ES to "Quitar",
        Idioma.EN to "Clear",
        Idioma.DE to "Zurücksetzen",
        Idioma.FR to "Effacer",
        Idioma.IT to "Rimuovi",
        Idioma.PT to "Remover",
    ),
    ClaveTexto.ORDENAR_POR to mapOf(
        Idioma.ES to "Ordenar por",
        Idioma.EN to "Sort by",
        Idioma.DE to "Sortieren nach",
        Idioma.FR to "Trier par",
        Idioma.IT to "Ordina per",
        Idioma.PT to "Ordenar por",
    ),
    ClaveTexto.ORDEN_ALFABETICO to mapOf(
        Idioma.ES to "Alfabético",
        Idioma.EN to "Alphabetical",
        Idioma.DE to "Alphabetisch",
        Idioma.FR to "Alphabétique",
        Idioma.IT to "Alfabetico",
        Idioma.PT to "Alfabético",
    ),
    ClaveTexto.ORDEN_FECHA to mapOf(
        Idioma.ES to "Fecha",
        Idioma.EN to "Date",
        Idioma.DE to "Datum",
        Idioma.FR to "Date",
        Idioma.IT to "Data",
        Idioma.PT to "Data",
    ),
    ClaveTexto.ASCENDENTE to mapOf(
        Idioma.ES to "Ascendente",
        Idioma.EN to "Ascending",
        Idioma.DE to "Aufsteigend",
        Idioma.FR to "Croissant",
        Idioma.IT to "Crescente",
        Idioma.PT to "Crescente",
    ),
    ClaveTexto.DESCENDENTE to mapOf(
        Idioma.ES to "Descendente",
        Idioma.EN to "Descending",
        Idioma.DE to "Absteigend",
        Idioma.FR to "Décroissant",
        Idioma.IT to "Decrescente",
        Idioma.PT to "Decrescente",
    ),
    ClaveTexto.QUITAR_ESTRELLA to mapOf(
        Idioma.ES to "Quitar de Guardados",
        Idioma.EN to "Remove from Saved",
        Idioma.DE to "Aus Gespeichert entfernen",
        Idioma.FR to "Retirer des éléments enregistrés",
        Idioma.IT to "Rimuovi dai salvati",
        Idioma.PT to "Remover dos Salvos",
    ),
    // --- Switch de traduccion en vivo (feature 1) ---
    // Dice que HACE el switch (traducir al idioma de app), no que idioma se esta
    // viendo ahora: la etiqueta vieja "Ver en {idioma}" cambiaba de idioma al
    // tocarla y se leia igual de bien como "estoy viendo X" que como "pasar a X".
    // {idioma} es siempre el idioma de app (el destino de la traduccion).
    ClaveTexto.TRADUCIR_A to mapOf(
        Idioma.ES to "Traducir al {idioma}",
        Idioma.EN to "Translate to {idioma}",
        Idioma.DE to "Auf {idioma} übersetzen",
        Idioma.FR to "Traduire en {idioma}",
        Idioma.IT to "Traduci in {idioma}",
        Idioma.PT to "Traduzir para {idioma}",
    ),
)

/**
 * Nombres de idioma para interpolar en las plantillas ({idioma}), no para
 * mostrar solos: los chips de idioma siguen con el codigo corto (DE, EN),
 * igual que los de nivel (B2, C1) -- eso no es parte de esta tabla.
 */
private val TABLA_NOMBRES_IDIOMA: Map<Idioma, Map<Idioma, String>> = mapOf(
    Idioma.EN to mapOf(
        Idioma.ES to "Inglés", Idioma.EN to "English", Idioma.DE to "Englisch",
        Idioma.FR to "Anglais", Idioma.IT to "Inglese", Idioma.PT to "Inglês",
    ),
    Idioma.DE to mapOf(
        Idioma.ES to "Alemán", Idioma.EN to "German", Idioma.DE to "Deutsch",
        Idioma.FR to "Allemand", Idioma.IT to "Tedesco", Idioma.PT to "Alemão",
    ),
    Idioma.ES to mapOf(
        Idioma.ES to "Español", Idioma.EN to "Spanish", Idioma.DE to "Spanisch",
        Idioma.FR to "Espagnol", Idioma.IT to "Spagnolo", Idioma.PT to "Espanhol",
    ),
    Idioma.FR to mapOf(
        Idioma.ES to "Francés", Idioma.EN to "French", Idioma.DE to "Französisch",
        Idioma.FR to "Français", Idioma.IT to "Francese", Idioma.PT to "Francês",
    ),
    Idioma.IT to mapOf(
        Idioma.ES to "Italiano", Idioma.EN to "Italian", Idioma.DE to "Italienisch",
        // La tabla de AJUSTES-FASE-9.md traia "Italiano" tambien en la columna fr (error de
        // transcripcion): ahora que el nombre se ve en las pestanas, se corrige a "Italien".
        Idioma.FR to "Italien", Idioma.IT to "Italiano", Idioma.PT to "Italiano",
    ),
    Idioma.PT to mapOf(
        Idioma.ES to "Portugués", Idioma.EN to "Portuguese", Idioma.DE to "Portugiesisch",
        Idioma.FR to "Portugais", Idioma.IT to "Portoghese", Idioma.PT to "Português",
    ),
)

/**
 * Visibilidad de modulo solo para TextosInterfazTest.kt: el test de
 * cobertura recorre la tabla cruda, sin pasar por el fallback a español de
 * texto()/nombreIdioma() -- ese fallback es justo lo que le permitiria a
 * una clave faltante pasar desapercibida en un test que solo mirara la
 * salida de las funciones publicas.
 */
internal fun tablaChromeCruda(): Map<ClaveTexto, Map<Idioma, String>> = TABLA_CHROME
internal fun tablaNombresIdiomaCruda(): Map<Idioma, Map<Idioma, String>> = TABLA_NOMBRES_IDIOMA

private fun texto(clave: ClaveTexto, idiomaInterfaz: Idioma): String {
    val porIdioma = TABLA_CHROME.getValue(clave)
    return porIdioma[idiomaInterfaz] ?: porIdioma.getValue(Idioma.ES)
}

private fun interpolar(plantilla: String, vararg pares: Pair<String, String>): String =
    pares.fold(plantilla) { texto, (clave, valor) -> texto.replace("{$clave}", valor) }

/** Nombre de [idioma] en el idioma de interfaz [idiomaInterfaz], para interpolar en una plantilla. */
fun nombreIdioma(idioma: Idioma, idiomaInterfaz: Idioma): String {
    val porIdioma = TABLA_NOMBRES_IDIOMA.getValue(idioma)
    return porIdioma[idiomaInterfaz] ?: porIdioma.getValue(Idioma.ES)
}

fun etiquetaSemana(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SEMANA, idiomaInterfaz)
fun etiquetaHoy(idiomaInterfaz: Idioma): String = texto(ClaveTexto.HOY, idiomaInterfaz)
fun etiquetaAjustes(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AJUSTES, idiomaInterfaz)
fun etiquetaVolver(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VOLVER, idiomaInterfaz)

fun mensajeSinContenidoTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_CONTENIDO_TITULO, idiomaInterfaz)

fun mensajeSinContenidoNivel(idiomaInterfaz: Idioma, idiomaAprendido: Idioma, nivel: Nivel): String = interpolar(
    texto(ClaveTexto.SIN_CONTENIDO_NIVEL, idiomaInterfaz),
    "idioma" to nombreIdioma(idiomaAprendido, idiomaInterfaz),
    "nivel" to nivel.name,
)

fun mensajeSinContenidoSemana(idiomaInterfaz: Idioma, anio: Int): String =
    interpolar(texto(ClaveTexto.SIN_CONTENIDO_SEMANA, idiomaInterfaz), "anio" to anio.toString())

fun mensajeSinCalendario(idiomaInterfaz: Idioma, anio: Int): String =
    interpolar(texto(ClaveTexto.SIN_CALENDARIO, idiomaInterfaz), "anio" to anio.toString())

/** AJUSTES-FASE-7.md, bloque 2.4: "del mismo tipo que el del idioma no disponible" -- un toast, no una ventana. */
fun mensajeDobleAtrasParaSalir(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SALIR_CONFIRMAR, idiomaInterfaz)

fun etiquetaIdiomasAprendidos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.IDIOMAS_APRENDIDOS, idiomaInterfaz)
fun etiquetaIdiomaApp(idiomaInterfaz: Idioma): String = texto(ClaveTexto.IDIOMA_APP, idiomaInterfaz)
fun etiquetaSegunSistema(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SEGUN_SISTEMA, idiomaInterfaz)
fun etiquetaEstiloVisual(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ESTILO_VISUAL, idiomaInterfaz)
fun etiquetaModo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO, idiomaInterfaz)
fun etiquetaModoClaro(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO_CLARO, idiomaInterfaz)
fun etiquetaModoOscuro(idiomaInterfaz: Idioma): String = texto(ClaveTexto.MODO_OSCURO, idiomaInterfaz)
fun etiquetaNivel(idiomaInterfaz: Idioma): String = texto(ClaveTexto.NIVEL, idiomaInterfaz)
fun etiquetaCambiarNivel(idiomaInterfaz: Idioma): String = texto(ClaveTexto.CAMBIAR_NIVEL, idiomaInterfaz)

/** [idiomaSinContenido] es el idioma que todavia no tiene contenido embebido, no el de interfaz. */
fun mensajeIdiomaNoDisponible(idiomaInterfaz: Idioma, idiomaSinContenido: Idioma): String = interpolar(
    texto(ClaveTexto.IDIOMA_NO_DISPONIBLE, idiomaInterfaz),
    "idioma" to nombreIdioma(idiomaSinContenido, idiomaInterfaz),
)

fun avisoGlosasTexto(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AVISO_GLOSAS, idiomaInterfaz)

fun etiquetaGuardados(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS, idiomaInterfaz)
fun mensajeGuardadosVacio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_VACIO, idiomaInterfaz)
fun etiquetaFiltroIdioma(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_IDIOMA, idiomaInterfaz)
fun etiquetaFiltroTipo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_TIPO, idiomaInterfaz)
fun etiquetaFiltroTodos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FILTRO_TODOS, idiomaInterfaz)
fun etiquetaTipoVocabulario(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TIPO_VOCABULARIO, idiomaInterfaz)
fun etiquetaTipoExpresion(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TIPO_EXPRESION, idiomaInterfaz)
fun etiquetaDesafioFinde(idiomaInterfaz: Idioma): String = texto(ClaveTexto.DESAFIO_FINDE, idiomaInterfaz)
fun etiquetaAcercaDe(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_DE, idiomaInterfaz)
fun descripcionSeccion(expandida: Boolean, idiomaInterfaz: Idioma): String =
    texto(if (expandida) ClaveTexto.SECCION_CONTRAER else ClaveTexto.SECCION_EXPANDIR, idiomaInterfaz)
fun etiquetaBibliotecaVistaVerbos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_VISTA_VERBOS, idiomaInterfaz)
fun mensajeVerbosVacio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VERBOS_VACIO, idiomaInterfaz)
fun mensajeVerbosSinResultados(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VERBOS_SIN_RESULTADOS, idiomaInterfaz)
fun descripcionBorrarBusqueda(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BUSCAR_BORRAR, idiomaInterfaz)

/** Nombre de una clase de verbo (`claseVerbo`) en el idioma de app; `null` si la app no la conoce (el verbo se ve igual, sin chip) o es `simple`. */
fun etiquetaClaseVerbo(clase: String, idiomaInterfaz: Idioma): String? = when (clase) {
    "reflexivo" -> texto(ClaveTexto.VERBO_REFLEXIVO, idiomaInterfaz)
    "trennbar" -> texto(ClaveTexto.VERBO_TRENNBAR, idiomaInterfaz)
    "mit-Praposition" -> texto(ClaveTexto.VERBO_MIT_PRAPOSITION, idiomaInterfaz)
    "mit-Dativ" -> texto(ClaveTexto.VERBO_MIT_DATIV, idiomaInterfaz)
    "phrasal-separable" -> texto(ClaveTexto.VERBO_PHRASAL_SEPARABLE, idiomaInterfaz)
    "phrasal-inseparable" -> texto(ClaveTexto.VERBO_PHRASAL_INSEPARABLE, idiomaInterfaz)
    "verbo-preposicion" -> texto(ClaveTexto.VERBO_VERBO_PREPOSICION, idiomaInterfaz)
    else -> null
}
fun etiquetaFuente(idiomaInterfaz: Idioma): String = texto(ClaveTexto.FUENTE_TITULO, idiomaInterfaz)
fun textoAcercaFuentes(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_FUENTES_TEXTO, idiomaInterfaz)

/** Nombre de una opcion de tipografia en el idioma de app; SISTEMA reutiliza el rotulo "Sistema". */
fun nombreFuente(fuente: io.github.percati.lenguapp.modelo.FuenteTema, idiomaInterfaz: Idioma): String = when (fuente) {
    io.github.percati.lenguapp.modelo.FuenteTema.SISTEMA -> etiquetaSegunSistema(idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FuenteTema.LECTURA -> texto(ClaveTexto.FUENTE_LECTURA, idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FuenteTema.CLARA -> texto(ClaveTexto.FUENTE_CLARA, idiomaInterfaz)
}

/** Nombre de un tema de color en el idioma de app; SISTEMA reutiliza el rotulo "Sistema" de los selectores. */
fun nombreTema(familia: io.github.percati.lenguapp.modelo.FamiliaTema, idiomaInterfaz: Idioma): String = when (familia) {
    io.github.percati.lenguapp.modelo.FamiliaTema.PAPEL -> texto(ClaveTexto.TEMA_PAPEL, idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FamiliaTema.TERRACOTA -> texto(ClaveTexto.TEMA_TERRACOTA, idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FamiliaTema.MAR -> texto(ClaveTexto.TEMA_MAR, idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FamiliaTema.GRAFITO -> texto(ClaveTexto.TEMA_GRAFITO, idiomaInterfaz)
    io.github.percati.lenguapp.modelo.FamiliaTema.SISTEMA -> etiquetaSegunSistema(idiomaInterfaz)
}
fun etiquetaBibliotecaVistaLexico(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_VISTA_LEXICO, idiomaInterfaz)
fun etiquetaBibliotecaVistaHabilidades(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_VISTA_HABILIDADES, idiomaInterfaz)
fun textoHabilidadSemanas(idiomaInterfaz: Idioma, semanas: List<Int>): String =
    interpolar(texto(ClaveTexto.HABILIDAD_SEMANAS, idiomaInterfaz), "semanas" to semanas.joinToString(", "))
fun etiquetaGuardadosExportar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_EXPORTAR, idiomaInterfaz)
fun etiquetaGuardadosImportar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_IMPORTAR, idiomaInterfaz)
fun etiquetaGuardadosExportarAnki(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_EXPORTAR_ANKI, idiomaInterfaz)
fun etiquetaGuardadosExportarRespaldo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_EXPORTAR_RESPALDO, idiomaInterfaz)
fun mensajeGuardadosExportado(idiomaInterfaz: Idioma, n: Int): String =
    interpolar(texto(ClaveTexto.GUARDADOS_MSG_EXPORTADO, idiomaInterfaz), "n" to n.toString())
fun mensajeGuardadosImportado(idiomaInterfaz: Idioma, nuevos: Int, ya: Int): String =
    interpolar(texto(ClaveTexto.GUARDADOS_MSG_IMPORTADO, idiomaInterfaz), "nuevos" to nuevos.toString(), "ya" to ya.toString())
fun mensajeGuardadosErrorInvalido(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_ERR_INVALIDO, idiomaInterfaz)
fun mensajeGuardadosErrorGrande(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_ERR_GRANDE, idiomaInterfaz)
fun mensajeGuardadosErrorVersion(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_ERR_VERSION, idiomaInterfaz)
fun mensajeGuardadosErrorArchivo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.GUARDADOS_ERR_ARCHIVO, idiomaInterfaz)
fun etiquetaSinIdiomaTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_TITULO, idiomaInterfaz)
fun mensajeSinIdioma(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_TEXTO, idiomaInterfaz)
fun mensajeSinIdiomaWidget(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_WIDGET, idiomaInterfaz)
fun textoLineaFija(idiomaInterfaz: Idioma): String = texto(ClaveTexto.LINEA_FIJA, idiomaInterfaz)
fun etiquetaAcercaQueEsTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_QUE_ES_TITULO, idiomaInterfaz)
fun etiquetaAcercaComoTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_COMO_TITULO, idiomaInterfaz)
fun textoAcercaComo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_COMO_TEXTO, idiomaInterfaz)
fun textoAcercaCreditos(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_CREDITOS_TEXTO, idiomaInterfaz)
/** Un texto del tutorial (titulo o cuerpo de un paso): las claves las nombra el catalogo de pasos. */
fun textoTour(clave: ClaveTexto, idiomaInterfaz: Idioma): String = texto(clave, idiomaInterfaz)
fun etiquetaTourAtras(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_ATRAS, idiomaInterfaz)
fun descripcionTourAtras(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_ATRAS_CD, idiomaInterfaz)
fun etiquetaTourSiguiente(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_SIGUIENTE, idiomaInterfaz)
fun etiquetaTourSaltar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_SALTAR, idiomaInterfaz)
fun etiquetaTourListo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_LISTO, idiomaInterfaz)
fun etiquetaTourPaso(n: Int, total: Int, idiomaInterfaz: Idioma): String =
    interpolar(texto(ClaveTexto.TOUR_PASO, idiomaInterfaz), "n" to n.toString(), "total" to total.toString())
fun descripcionTourSiguiente(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_SIGUIENTE_CD, idiomaInterfaz)
fun descripcionTourSaltar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_SALTAR_CD, idiomaInterfaz)
fun descripcionTourListo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.TOUR_LISTO_CD, idiomaInterfaz)
fun etiquetaVelocidadAudio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VELOCIDAD_AUDIO_TITULO, idiomaInterfaz)
fun ayudaVelocidadAudio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.VELOCIDAD_AUDIO_AYUDA, idiomaInterfaz)
fun textoVelocidadAudio(velocidad: io.github.percati.lenguapp.modelo.VelocidadAudio, idiomaInterfaz: Idioma): String =
    interpolar(texto(ClaveTexto.VELOCIDAD_AUDIO_VALOR, idiomaInterfaz), "n" to velocidad.porcentaje.toString())
fun etiquetaBienvenidaTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIENVENIDA_TITULO, idiomaInterfaz)
fun etiquetaBienvenidaIdiomaInicial(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIENVENIDA_IDIOMA_INICIAL, idiomaInterfaz)
fun etiquetaBienvenidaElegirPractica(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIENVENIDA_ELEGIR_PRACTICA, idiomaInterfaz)
fun etiquetaBienvenidaEmpezar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIENVENIDA_EMPEZAR, idiomaInterfaz)
fun etiquetaVerBienvenida(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIENVENIDA_VER_DE_NUEVO, idiomaInterfaz)
fun descripcionAudioEscuchar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AUDIO_ESCUCHAR, idiomaInterfaz)
fun descripcionAudioDetener(idiomaInterfaz: Idioma): String = texto(ClaveTexto.AUDIO_DETENER, idiomaInterfaz)
fun textoAcercaPrueba(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRUEBA, idiomaInterfaz)
fun textoAcercaQueEs(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_QUE_ES_TEXTO, idiomaInterfaz)
fun etiquetaAcercaPrivacidadTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TITULO, idiomaInterfaz)
fun textoAcercaPrivacidad(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TEXTO, idiomaInterfaz)
fun etiquetaAcercaLicenciasTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TITULO, idiomaInterfaz)
fun textoAcercaLicencias(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TEXTO, idiomaInterfaz)
fun textoAcercaVersion(idiomaInterfaz: Idioma, version: String): String =
    interpolar(texto(ClaveTexto.ACERCA_VERSION, idiomaInterfaz), "version" to version)
fun textoRetoConsignaLinea(idiomaInterfaz: Idioma, oralMin: Int): String =
    interpolar(texto(ClaveTexto.RETO_CONSIGNA_LINEA, idiomaInterfaz), "oralMin" to oralMin.toString())
fun etiquetaRetoHabilidad(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_HABILIDAD, idiomaInterfaz)
fun etiquetaRetoCondiciones(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_CONDICIONES, idiomaInterfaz)
fun etiquetaRetoFrasesTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_FRASES_TITULO, idiomaInterfaz)
fun textoRetoFrasesAyuda(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_FRASES_AYUDA, idiomaInterfaz)

/**
 * Las reglas del reto del fin de semana, una por renglon, en el idioma de app. A2/B1:
 * de corrido y sin leer; B2/C1/C2: sin notas, `oralMin` minutos, con cambio de
 * interlocutor. `oralMin` ya viene redondeado (ver oralMinRedondeado).
 */
fun reglasReto(idiomaInterfaz: Idioma, nivel: Nivel, oralMin: Int): List<String> {
    val basico = nivel == Nivel.A2 || nivel == Nivel.B1
    val clave = if (basico) ClaveTexto.RETO_REGLAS_BASICO else ClaveTexto.RETO_REGLAS_AVANZADO
    return interpolar(texto(clave, idiomaInterfaz), "oralMin" to oralMin.toString()).split('\n')
}
fun etiquetaSemanaRepaso(idioma: Idioma): String = texto(ClaveTexto.SEMANA_REPASO, idioma)
fun etiquetaSemanaSurvival(idioma: Idioma): String = texto(ClaveTexto.SEMANA_SURVIVAL, idioma)
fun etiquetaCopiarPortapapeles(idiomaInterfaz: Idioma): String = texto(ClaveTexto.COPIAR_PORTAPAPELES, idiomaInterfaz)
fun etiquetaCopiado(idiomaInterfaz: Idioma): String = texto(ClaveTexto.COPIADO, idiomaInterfaz)
fun etiquetaPlanillaBoton(idiomaInterfaz: Idioma): String = texto(ClaveTexto.PLANILLA_BOTON, idiomaInterfaz)
fun mensajePlanillaSinLector(idiomaInterfaz: Idioma): String = texto(ClaveTexto.PLANILLA_SIN_LECTOR, idiomaInterfaz)
fun etiquetaBiblioteca(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA, idiomaInterfaz)
fun etiquetaBibliotecaBuscar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_BUSCAR, idiomaInterfaz)
fun etiquetaBibliotecaTema(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_TEMA, idiomaInterfaz)
fun etiquetaBibliotecaSinCategoria(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_SIN_CATEGORIA, idiomaInterfaz)
fun etiquetaBibliotecaFunciones(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_FUNCIONES, idiomaInterfaz)
fun etiquetaBibliotecaPatrones(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_PATRONES, idiomaInterfaz)
fun etiquetaBibliotecaVacio(idiomaInterfaz: Idioma): String = texto(ClaveTexto.BIBLIOTECA_VACIO, idiomaInterfaz)
fun etiquetaQuitarFiltros(idiomaInterfaz: Idioma): String = texto(ClaveTexto.QUITAR_FILTROS, idiomaInterfaz)
fun etiquetaQuitar(idiomaInterfaz: Idioma): String = texto(ClaveTexto.QUITAR, idiomaInterfaz)
fun etiquetaOrdenarPor(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ORDENAR_POR, idiomaInterfaz)
fun etiquetaOrdenAlfabetico(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ORDEN_ALFABETICO, idiomaInterfaz)
fun etiquetaOrdenFecha(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ORDEN_FECHA, idiomaInterfaz)
fun etiquetaAscendente(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ASCENDENTE, idiomaInterfaz)
fun etiquetaDescendente(idiomaInterfaz: Idioma): String = texto(ClaveTexto.DESCENDENTE, idiomaInterfaz)
fun etiquetaQuitarEstrella(idiomaInterfaz: Idioma): String = texto(ClaveTexto.QUITAR_ESTRELLA, idiomaInterfaz)
fun etiquetaPromptVozTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.PROMPT_VOZ_TITULO, idiomaInterfaz)
fun etiquetaPromptVozAyuda(idiomaInterfaz: Idioma): String = texto(ClaveTexto.PROMPT_VOZ_AYUDA, idiomaInterfaz)

/**
 * Etiqueta del switch de traduccion: "Traducir al espanol". [idiomaDestino] es
 * el idioma de app al que se traduce (el que el usuario eligio en Ajustes),
 * nombrado en el idioma de interfaz. Los idiomas romances lo llevan en
 * minuscula ("al espanol"); en aleman e ingles los nombres de idioma son
 * nombres propios y quedan capitalizados.
 */
fun etiquetaTraducirA(idiomaInterfaz: Idioma, idiomaDestino: Idioma): String {
    val nombre = nombreIdioma(idiomaDestino, idiomaInterfaz)
    val nombreEnContexto = if (idiomaInterfaz == Idioma.EN || idiomaInterfaz == Idioma.DE) nombre else nombre.lowercase()
    return interpolar(texto(ClaveTexto.TRADUCIR_A, idiomaInterfaz), "idioma" to nombreEnContexto)
}
