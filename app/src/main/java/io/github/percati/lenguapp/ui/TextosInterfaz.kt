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
    ACERCA_DE, LINEA_FIJA, ACERCA_QUE_ES_TITULO, ACERCA_QUE_ES_TEXTO, ACERCA_PRIVACIDAD_TITULO, ACERCA_PRIVACIDAD_TEXTO, ACERCA_LICENCIAS_TITULO, ACERCA_LICENCIAS_TEXTO, ACERCA_VERSION,
    RETO_TITULO, RETO_LINEA, RETO_REGLAS_BASICO, RETO_REGLAS_AVANZADO, RETO_ROTULO_MISION,
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
        Idioma.ES to "Pulsá otra vez para salir",
        Idioma.EN to "Press again to exit",
        Idioma.DE to "Zum Beenden nochmals drücken",
        Idioma.FR to "Appuie à nouveau pour quitter",
        Idioma.IT to "Premi di nuovo per uscire",
        Idioma.PT to "Pressione novamente para sair",
    ),
    ClaveTexto.IDIOMAS_APRENDIDOS to mapOf(
        Idioma.ES to "Idioma que aprendés",
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
        Idioma.ES to "Todavía no guardaste nada. Tocá la estrella junto a una palabra o expresión para guardarla.",
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
    ClaveTexto.RETO_TITULO to mapOf(
        Idioma.ES to "Reto del fin de semana",
        Idioma.EN to "Weekend challenge",
        Idioma.DE to "Wochenend-Challenge",
        Idioma.FR to "Défi du week-end",
        Idioma.IT to "Sfida del weekend",
        Idioma.PT to "Desafio do fim de semana",
    ),
    ClaveTexto.RETO_LINEA to mapOf(
        Idioma.ES to "La misión de la semana, pero en modo real.",
        Idioma.EN to "This week's mission, but for real.",
        Idioma.DE to "Die Wochenaufgabe, aber im Ernstfall.",
        Idioma.FR to "La mission de la semaine, mais en conditions réelles.",
        Idioma.IT to "La missione della settimana, ma in modalità reale.",
        Idioma.PT to "A missão da semana, mas no modo real.",
    ),
    ClaveTexto.RETO_REGLAS_BASICO to mapOf(
        Idioma.ES to "1. De corrido, sin leer.\n2. Una sola toma.\n3. Díselo a una persona o grábate en audio.\n4. Escúchalo: ¿se entiende? Repítelo una vez.",
        Idioma.EN to "1. Out loud and fluently, without reading.\n2. One single take.\n3. Say it to a person, or record yourself.\n4. Listen back: is it clear? Do it once more.",
        Idioma.DE to "1. Flüssig und ohne abzulesen.\n2. Nur ein Versuch.\n3. Sag es einer Person oder nimm dich auf.\n4. Hör es dir an: Ist es verständlich? Sprich es noch einmal.",
        Idioma.FR to "1. D'un trait, sans lire.\n2. Une seule prise.\n3. Dis-le à quelqu'un ou enregistre-toi.\n4. Écoute-toi : est-ce compréhensible ? Recommence une fois.",
        Idioma.IT to "1. Di seguito, senza leggere.\n2. Una sola ripresa.\n3. Dillo a una persona o registrati.\n4. Riascoltati: si capisce? Ripetilo una volta.",
        Idioma.PT to "1. De uma vez, sem ler.\n2. Uma única gravação.\n3. Diga isso a uma pessoa ou grave um áudio.\n4. Escute: dá para entender? Repita uma vez.",
    ),
    ClaveTexto.RETO_REGLAS_AVANZADO to mapOf(
        Idioma.ES to "1. Sin notas, una sola toma de {oralMin} min.\n2. Cambia de interlocutor a mitad (de un amigo a tu jefe, o al revés).\n3. Grábate y apunta una cosa que mejorarías.\n4. Repite con ese cambio.",
        Idioma.EN to "1. No notes, one single take of {oralMin} min.\n2. Switch listener halfway through (from a friend to your boss, or the other way round).\n3. Record yourself and note one thing you would improve.\n4. Do it again with that change.",
        Idioma.DE to "1. Ohne Notizen, ein einziger Durchgang von {oralMin} Min.\n2. Wechsle in der Mitte das Gegenüber (von einem Freund zu einem Vorgesetzten, oder umgekehrt).\n3. Nimm dich auf und notiere eine Sache, die du verbessern würdest.\n4. Wiederhole es mit dieser Änderung.",
        Idioma.FR to "1. Sans notes, en une seule prise de {oralMin} min.\n2. Change d'interlocuteur à mi-parcours (d'un ami à ton supérieur, ou l'inverse).\n3. Enregistre-toi et note une chose à améliorer.\n4. Recommence avec ce changement.",
        Idioma.IT to "1. Senza appunti, una sola ripresa di {oralMin} min.\n2. Cambia interlocutore a metà (da un amico al tuo capo, o viceversa).\n3. Registrati e annota una cosa da migliorare.\n4. Ripeti con quel cambiamento.",
        Idioma.PT to "1. Sem anotações, em uma única gravação de {oralMin} min.\n2. Troque de interlocutor na metade (de um amigo para o seu chefe, ou o contrário).\n3. Faça uma gravação e anote uma coisa que você melhoraria.\n4. Repita com essa mudança.",
    ),
    ClaveTexto.RETO_ROTULO_MISION to mapOf(
        Idioma.ES to "La misión:",
        Idioma.EN to "The mission:",
        Idioma.DE to "Die Wochenaufgabe:",
        Idioma.FR to "La mission :",
        Idioma.IT to "La missione:",
        Idioma.PT to "A missão:",
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
        Idioma.ES to "LenguApp te da cada semana una ficha para practicar hablando el idioma que aprendes, al nivel que elijas. No es un curso: fija lo que ya aprendiste en un curso de tu nivel.",
        Idioma.EN to "LenguApp gives you one sheet a week to practise speaking the language you are learning, at the level you choose. It is not a course: it helps you consolidate what you already learned in a course at your level.",
        Idioma.DE to "LenguApp gibt dir jede Woche ein Blatt, um die Sprache, die du lernst, mündlich zu üben – auf dem Niveau, das du wählst. Es ist kein Kurs: Es festigt, was du in einem Kurs auf deinem Niveau schon gelernt hast.",
        Idioma.FR to "LenguApp te propose chaque semaine une fiche pour t'exercer à parler la langue que tu apprends, au niveau de ton choix. Ce n'est pas un cours : l'application consolide ce que tu as déjà appris dans un cours de ton niveau.",
        Idioma.IT to "LenguApp ti propone ogni settimana una scheda per esercitarti a parlare la lingua che stai imparando, al livello che scegli. Non è un corso: consolida ciò che hai già imparato in un corso del tuo livello.",
        Idioma.PT to "O LenguApp traz toda semana uma ficha para você praticar falando o idioma que está aprendendo, no nível que escolher. Não é um curso: ele fixa o que você já aprendeu em um curso do seu nível.",
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
        Idioma.ES to "Sin Internet y sin cuenta: la app no puede enviar nada a ningún sitio. Solo lee la fecha del dispositivo (y su idioma, únicamente si lo eliges en Ajustes). Lo único que se guarda en tu móvil son los elementos que marcas con la estrella en Guardados.",
        Idioma.EN to "No internet and no account: the app cannot send anything anywhere. It only reads the device's date (and its language, only if you choose that in Settings). The only thing stored on your phone is what you mark with a star in Saved.",
        Idioma.DE to "Kein Internet, kein Konto: Die App kann nirgendwohin etwas senden. Sie liest nur das Datum des Geräts (und seine Sprache, nur wenn du das in den Einstellungen wählst). Auf deinem Handy gespeichert wird nur, was du unter Gespeichert mit dem Stern markierst.",
        Idioma.FR to "Pas d'Internet, pas de compte : l'application ne peut rien envoyer nulle part. Elle lit seulement la date de l'appareil (et sa langue, uniquement si tu le choisis dans Paramètres). Seuls les éléments que tu marques d'une étoile dans Enregistrés sont conservés sur ton téléphone.",
        Idioma.IT to "Niente Internet e niente account: l'app non può inviare nulla da nessuna parte. Legge soltanto la data del dispositivo (e la sua lingua, solo se lo scegli in Impostazioni). Sul telefono viene conservato soltanto ciò che contrassegni con la stella in Salvati.",
        Idioma.PT to "Sem internet e sem conta: o aplicativo não pode enviar nada para lugar nenhum. Ele só lê a data do dispositivo (e o idioma, somente se você escolher isso em Configurações). A única coisa salva no seu celular são os itens que você marca com a estrela em Salvos.",
    ),
    ClaveTexto.ACERCA_LICENCIAS_TITULO to mapOf(
        Idioma.ES to "Licencias",
        Idioma.EN to "Licences",
        Idioma.DE to "Lizenzen",
        Idioma.FR to "Licences",
        Idioma.IT to "Licenze",
        Idioma.PT to "Licenças",
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
        Idioma.ES to "Pegalo al empezar una conversación de voz con tu IA. Decile *feedback* cuando quieras la devolución.",
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
fun etiquetaRetoTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_TITULO, idiomaInterfaz)
fun etiquetaAcercaDe(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_DE, idiomaInterfaz)
fun etiquetaSinIdiomaTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_TITULO, idiomaInterfaz)
fun mensajeSinIdioma(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_TEXTO, idiomaInterfaz)
fun mensajeSinIdiomaWidget(idiomaInterfaz: Idioma): String = texto(ClaveTexto.SIN_IDIOMA_WIDGET, idiomaInterfaz)
fun textoLineaFija(idiomaInterfaz: Idioma): String = texto(ClaveTexto.LINEA_FIJA, idiomaInterfaz)
fun etiquetaAcercaQueEsTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_QUE_ES_TITULO, idiomaInterfaz)
fun textoAcercaQueEs(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_QUE_ES_TEXTO, idiomaInterfaz)
fun etiquetaAcercaPrivacidadTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TITULO, idiomaInterfaz)
fun textoAcercaPrivacidad(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_PRIVACIDAD_TEXTO, idiomaInterfaz)
fun etiquetaAcercaLicenciasTitulo(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TITULO, idiomaInterfaz)
fun textoAcercaLicencias(idiomaInterfaz: Idioma): String = texto(ClaveTexto.ACERCA_LICENCIAS_TEXTO, idiomaInterfaz)
fun textoAcercaVersion(idiomaInterfaz: Idioma, version: String): String =
    interpolar(texto(ClaveTexto.ACERCA_VERSION, idiomaInterfaz), "version" to version)
fun textoRetoLinea(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_LINEA, idiomaInterfaz)
fun etiquetaRetoMision(idiomaInterfaz: Idioma): String = texto(ClaveTexto.RETO_ROTULO_MISION, idiomaInterfaz)

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
