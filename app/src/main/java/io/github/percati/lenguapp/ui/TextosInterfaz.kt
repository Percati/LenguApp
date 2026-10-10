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
    RETO_LINEA, RETO_REGLAS_BASICO, RETO_REGLAS_AVANZADO, RETO_HABILIDAD, RETO_CONDICIONES, RETO_FRASES_TITULO, RETO_FRASES_AYUDA,
    ACERCA_COMO_TITULO, ACERCA_COMO_TEXTO, ACERCA_CREDITOS_TEXTO, ACERCA_PRUEBA,
    GUARDADOS_EXPORTAR, GUARDADOS_IMPORTAR, GUARDADOS_EXPORTAR_ANKI, GUARDADOS_EXPORTAR_RESPALDO, GUARDADOS_MSG_EXPORTADO, GUARDADOS_MSG_IMPORTADO, GUARDADOS_ERR_INVALIDO, GUARDADOS_ERR_GRANDE, GUARDADOS_ERR_VERSION, GUARDADOS_ERR_ARCHIVO,
    BIBLIOTECA_VISTA_LEXICO, BIBLIOTECA_VISTA_HABILIDADES, HABILIDAD_SEMANAS,
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
        Idioma.ES to "Todavía no guardaste nada. Toca la estrella junto a una palabra o expresión para guardarla.",
        Idioma.EN to "You haven't saved anything yet. Tap the star next to a word or expression to save it.",
        Idioma.DE to "Du hast noch nichts gespeichert. Tippe auf den Stern neben einem Wort oder Ausdruck, um ihn zu speichern.",
        Idioma.FR to "Tu n'as encore rien enregistré. Appuie sur l'étoile à côté d'un mot ou d'une expression pour l'enregistrer.",
        Idioma.IT to "Non hai ancora salvato nulla. Tocca la stella accanto a una parola o espressione per salvarla.",
        Idioma.PT to "Você ainda não salvou nada. Toque na estrela ao lado de uma palavra ou expressão para salvá-la.",
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
    ClaveTexto.RETO_LINEA to mapOf(
        Idioma.ES to "Dilo en voz alta, como en una conversación de verdad.",
        Idioma.EN to "Say it out loud, like in a real conversation.",
        Idioma.DE to "Sag es laut, wie in einem echten Gespräch.",
        Idioma.FR to "Dis-le à voix haute, comme dans une vraie conversation.",
        Idioma.IT to "Dillo ad alta voce, come in una vera conversazione.",
        Idioma.PT to "Diga em voz alta, como em uma conversa de verdade.",
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
    ClaveTexto.ACERCA_CREDITOS_TEXTO to mapOf(
        Idioma.ES to "Las voces sintéticas se generan con Piper: de_DE-thorsten-high (alemán) y en_US-ryan-high y en_US-lessac-high (inglés).",
        Idioma.EN to "The synthetic voices are generated with Piper: de_DE-thorsten-high (German) and en_US-ryan-high and en_US-lessac-high (English).",
        Idioma.DE to "Die synthetischen Stimmen werden mit Piper erzeugt: de_DE-thorsten-high (Deutsch) sowie en_US-ryan-high und en_US-lessac-high (Englisch).",
        Idioma.FR to "Les voix synthétiques sont générées avec Piper : de_DE-thorsten-high (allemand) et en_US-ryan-high et en_US-lessac-high (anglais).",
        Idioma.IT to "Le voci sintetiche sono generate con Piper: de_DE-thorsten-high (tedesco) e en_US-ryan-high e en_US-lessac-high (inglese).",
        Idioma.PT to "As vozes sintéticas são geradas com o Piper: de_DE-thorsten-high (alemão) e en_US-ryan-high e en_US-lessac-high (inglês).",
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
    ClaveTexto.ACERCA_QUE_ES_TEXTO to mapOf(
        Idioma.ES to "LenguApp ofrece cada semana una ficha de práctica de alemán o de inglés, entre los niveles A2 y C2. Cada ficha propone una habilidad y un tema, con vocabulario y expresiones, una misión breve, microtareas y un texto para que una inteligencia artificial corrija tu producción. No es un curso: no enseña desde cero, sino que consolida lo que ya aprendiste en un curso de tu nivel.",
        Idioma.EN to "LenguApp offers a weekly practice sheet in German or English, between levels A2 and C2. Each sheet proposes a skill and a topic, with vocabulary and expressions, a short mission, micro-tasks and a text for an artificial intelligence to correct your output. It is not a course: it does not teach from scratch, but consolidates what you already learned in a course at your level.",
        Idioma.DE to "LenguApp bietet jede Woche ein Übungsblatt für Deutsch oder Englisch, zwischen den Niveaus A2 und C2. Jedes Blatt enthält eine Fähigkeit und ein Thema, mit Wortschatz und Redewendungen, einer kurzen Mission, Mikroaufgaben und einem Text, mit dem eine künstliche Intelligenz deine Sprachproduktion korrigieren kann. Es ist kein Kurs: Es vermittelt nichts von Grund auf, sondern festigt, was du in einem Kurs auf deinem Niveau schon gelernt hast.",
        Idioma.FR to "LenguApp propose chaque semaine une fiche d'entraînement en allemand ou en anglais, entre les niveaux A2 et C2. Chaque fiche présente une compétence et un thème, avec du vocabulaire et des expressions, une courte mission, des microtâches et un texte permettant à une intelligence artificielle de corriger ta production. Ce n'est pas un cours : l'application n'enseigne pas à partir de zéro, elle consolide ce que tu as déjà appris dans un cours de ton niveau.",
        Idioma.IT to "LenguApp propone ogni settimana una scheda di pratica di tedesco o di inglese, tra i livelli A2 e C2. Ogni scheda presenta un'abilità e un tema, con vocabolario ed espressioni, una breve missione, microattività e un testo con cui un'intelligenza artificiale può correggere la tua produzione. Non è un corso: non insegna da zero, ma consolida ciò che hai già imparato in un corso del tuo livello.",
        Idioma.PT to "O LenguApp oferece toda semana uma ficha de prática de alemão ou de inglês, entre os níveis A2 e C2. Cada ficha propõe uma habilidade e um tema, com vocabulário e expressões, uma missão curta, microtarefas e um texto para que uma inteligência artificial corrija a sua produção. Não é um curso: não ensina do zero, mas consolida o que você já aprendeu em um curso do seu nível.",
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
        Idioma.ES to "No hay nada para mostrar con estos filtros.",
        Idioma.EN to "Nothing to show with these filters.",
        Idioma.DE to "Mit diesen Filtern gibt es nichts anzuzeigen.",
        Idioma.FR to "Rien à afficher avec ces filtres.",
        Idioma.IT to "Niente da mostrare con questi filtri.",
        Idioma.PT to "Nada para mostrar com estes filtros.",
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
fun textoAcercaPrueba(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRUEBA, idiomaInterfaz)
fun textoAcercaQueEs(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_QUE_ES_TEXTO, idiomaInterfaz)
fun etiquetaAcercaPrivacidadTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TITULO, idiomaInterfaz)
fun textoAcercaPrivacidad(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TEXTO, idiomaInterfaz)
fun etiquetaAcercaLicenciasTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TITULO, idiomaInterfaz)
fun textoAcercaLicencias(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TEXTO, idiomaInterfaz)
fun textoAcercaVersion(idiomaInterfaz: Idioma, version: String): String =
    interpolar(texto(ClaveTexto.ACERCA_VERSION, idiomaInterfaz), "version" to version)
fun textoRetoLinea(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_LINEA, idiomaInterfaz)
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
