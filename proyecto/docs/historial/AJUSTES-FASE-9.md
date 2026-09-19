# Ajustes para la Fase 9 — internacionalización real

Cuatro bloques. El A ya está hecho. El B es la traducción completa de la interfaz, con la tabla resuelta para que no haya que inventar nada. El C y el D son deuda de contenido, con su plan.

---

## A. Hecho: manifiesto de audio

`tools/manifiesto_audio.py` genera dos archivos:

- **`audio-manifiesto.md`** — legible, para revisar antes de grabar. Agrupado por idioma, con tres secciones por idioma: frases de ejemplo, vocabulario y expresiones. Cada fila lleva el texto y la ficha de la que sale.
- **`audio-manifiesto.json`** — entrada de `generar_audio.py`.

| Idioma | Textos | Voces | Clips |
|---|---|---|---|
| Alemán | 362 | `de_DE-thorsten-high` | 724 |
| Inglés | 358 | `en_US-ryan-high` + `en_US-lessac-high` | 1.432 |
| **Total** | **720** | | **2.156** ≈ 52 MB |

**El idioma de cada texto es siempre el que se aprende, nunca el de la glosa.** Por eso la voz se deduce del campo `idioma` de la ficha y no hay que declararla a mano. El inglés se genera con las dos voces porque son seleccionables.

Los nombres de archivo son deterministas —hash del texto, la voz y la velocidad—, así que el script salta lo que ya existe y se puede correr en tandas.

---

## B. Traducir la interfaz entera

Se levanta el diferimiento. Hasta ahora estaba justificado; con el idioma de aplicación ya implementado, ver la mitad de la pantalla en español es peor que no haber traducido nada.

**Alcance: todo el *chrome*.** Estas son las cadenas, resueltas en los seis idiomas. Van escritas y no derivadas de recursos del sistema, por las mismas razones que la tabla de días: los nombres que produce `Locale` no siempre coinciden con la convención esperada.

### Pantalla principal

| Clave | es | en | de | fr | it | pt |
|---|---|---|---|---|---|---|
| `semana` | Semana | Week | Woche | Semaine | Settimana | Semana |
| `hoy` | Hoy | Today | Heute | Aujourd'hui | Oggi | Hoje |
| `ajustes` | Ajustes | Settings | Einstellungen | Paramètres | Impostazioni | Ajustes |
| `volver` | Volver | Back | Zurück | Retour | Indietro | Voltar |
| `sin_contenido_titulo` | Sin contenido esta semana | No content this week | Diese Woche kein Inhalt | Pas de contenu cette semaine | Nessun contenuto questa settimana | Sem conteúdo esta semana |
| `sin_contenido_nivel` | Todavía no hay contenido para {idioma} en nivel {nivel}. | There is no content yet for {idioma} at level {nivel}. | Für {idioma} auf Niveau {nivel} gibt es noch keinen Inhalt. | Il n'y a pas encore de contenu pour {idioma} au niveau {nivel}. | Non c'è ancora contenuto per {idioma} al livello {nivel}. | Ainda não há conteúdo para {idioma} no nível {nivel}. |
| `sin_contenido_semana` | Esta semana queda fuera de la edición {anio}. | This week is outside the {anio} edition. | Diese Woche liegt außerhalb der Ausgabe {anio}. | Cette semaine est en dehors de l'édition {anio}. | Questa settimana è fuori dall'edizione {anio}. | Esta semana está fora da edição {anio}. |
| `sin_calendario` | Todavía no hay calendario para {anio}. | There is no calendar for {anio} yet. | Für {anio} gibt es noch keinen Kalender. | Il n'y a pas encore de calendrier pour {anio}. | Non c'è ancora un calendario per {anio}. | Ainda não há calendário para {anio}. |
| `salir_confirmar` | Pulsá otra vez para salir | Press again to exit | Zum Beenden nochmals drücken | Appuyez à nouveau pour quitter | Premi di nuovo per uscire | Pressione novamente para sair |

### Ajustes

| Clave | es | en | de | fr | it | pt |
|---|---|---|---|---|---|---|
| `idiomas_aprendidos` | Idioma que aprendés | Language you're learning | Sprache, die du lernst | Langue que vous apprenez | Lingua che stai imparando | Idioma que está a aprender |
| `idioma_app` | Idioma de la aplicación | App language | Sprache der App | Langue de l'application | Lingua dell'applicazione | Idioma da aplicação |
| `segun_sistema` | Según el sistema | Follow the system | Systemeinstellung | Selon le système | Come il sistema | Conforme o sistema |
| `estilo_visual` | Estilo visual | Visual style | Erscheinungsbild | Style visuel | Stile visivo | Estilo visual |
| `modo` | Modo | Mode | Modus | Mode | Modalità | Modo |
| `modo_claro` | Claro | Light | Hell | Clair | Chiaro | Claro |
| `modo_oscuro` | Oscuro | Dark | Dunkel | Sombre | Scuro | Escuro |
| `nivel` | Nivel | Level | Niveau | Niveau | Livello | Nível |
| `cambiar_nivel` | Cambiar nivel | Change level | Niveau ändern | Changer de niveau | Cambia livello | Mudar de nível |
| `idioma_no_disponible` | {idioma} todavía no está disponible | {idioma} isn't available yet | {idioma} ist noch nicht verfügbar | {idioma} n'est pas encore disponible | {idioma} non è ancora disponibile | {idioma} ainda não está disponível |
| `familia_academia` | Academia | Academic | Akademisch | Académique | Accademico | Académico |
| `familia_editorial` | Editorial | Editorial | Editorial | Éditorial | Editoriale | Editorial |
| `sin_idioma` | Sin idioma seleccionado | No language selected | Keine Sprache ausgewählt | Aucune langue sélectionnée | Nessuna lingua selezionata | Nenhum idioma selecionado |
| `aviso_glosas` | Las traducciones de vocabulario todavía solo existen en español. | Vocabulary translations only exist in Spanish so far. | Die Wortschatzübersetzungen liegen bisher nur auf Spanisch vor. | Les traductions du vocabulaire n'existent pour l'instant qu'en espagnol. | Le traduzioni del lessico esistono per ora solo in spagnolo. | As traduções de vocabulário só existem em espanhol por agora. |

### Nombres de idioma, para interpolar en las plantillas

| Código | es | en | de | fr | it | pt |
|---|---|---|---|---|---|---|
| `en` | Inglés | English | Englisch | Anglais | Inglese | Inglês |
| `de` | Alemán | German | Deutsch | Allemand | Tedesco | Alemão |
| `es` | Español | Spanish | Spanisch | Espagnol | Spagnolo | Espanhol |
| `fr` | Francés | French | Französisch | Français | Francese | Francês |
| `it` | Italiano | Italian | Italienisch | Italien | Italiano | Italiano |
| `pt` | Portugués | Portuguese | Portugiesisch | Portugais | Portoghese | Português |

### Corrección y añadidos posteriores

- **`it` en la columna `fr` decía «Italiano» y debe decir «Italien».** Error de la tabla original. Code lo implementó tal cual porque la instrucción era no inventar cadenas, y lo reportó: fue lo correcto.
- Se agregaron tres claves que faltaban y Code detectó al cablear: `familia_academia`, `familia_editorial` y `sin_idioma`.

### Dos advertencias

**No usar `String.format` con orden posicional.** Las plantillas llevan marcas con nombre (`{idioma}`, `{nivel}`, `{anio}`) porque el orden de los elementos cambia entre idiomas: en alemán el nivel va antes del verbo final.

**Un test que recorra las seis columnas.** Debe fallar si alguna clave queda sin traducir en algún idioma. Con seis idiomas y unas treinta claves, el olvido silencioso es el modo de fallo esperable, y es justo el que el usuario ya detectó dos veces.

---

## C. El contraste con la lengua base: qué falta y dónde va

### C.1 El diseño correcto

`contraste` ya es un mapa por idioma base. Lo que falta es escribir las claves que no son `es`. Para una ficha alemana:

```json
"contraste": {
  "es": "El español no tiene posición final del verbo…",
  "en": "English has no verb-final position either, but…",
  "it": "…"
}
```

Con la regla B.6 de la fase anterior —ocultar la sección si falta la clave— la app ya se comporta bien mientras el mapa esté incompleto.

### C.2 El problema que descubriste en «Typische Fehler»

Seis entradas de `errores` y `notas` mencionan el español dentro de material que se presenta como universal:

| Ficha | Fragmento |
|---|---|
| `DE-G01` | *im Deutschen Pflicht, im Spanischen nicht* |
| `DE-G08` | *Präposition aus dem Spanischen* |
| `DE-G10` | *entspricht ungefähr ser/estar* |
| `DE-V02` | *Wörtliche Übersetzung aus dem Spanischen* |
| `EN-F02` | *a fossilised Spanish-speaker error* |
| `EN-F15` | *Silence costs more in English than in Spanish* |

**Decisión de diseño: el material contrastivo vive en un solo lugar, `contraste`.** Los `errores` y las `notas` quedan universales.

Es más limpio que agregar un campo contrastivo a cada error, por tres razones: no hay dos sitios donde buscar lo mismo, la regla de ocultado ya existe y funciona, y el schema no cambia — `errores` sigue siendo un arreglo de cadenas.

Trabajo concreto: en esas seis entradas, quitar la parte contrastiva del texto universal y trasladarla a `contraste.es`. Son seis ediciones en las fichas Markdown, no un cambio estructural.

**Estas seis no son todas.** Son las que mencionan el español *explícitamente*. Puede haber otras que lo asuman sin nombrarlo, y esas solo aparecen leyendo. Queda como revisión pendiente.

---

## D. Las traducciones de vocabulario y expresiones

Volumen real, contando lo que hay:

| | Ítems | × 4 idiomas faltantes |
|---|---|---|
| Vocabulario | 392 | **1.568** |
| Expresiones (Redemittel) | 230 | **920** |
| **Total** | 622 | **2.488 glosas** |

Y hay un límite que conviene decir ahora: **puedo escribir con confianza las de inglés, y con confianza razonable las de italiano y portugués. Las de francés son las más flojas**, y ninguna de las tres las podés validar vos.

Dos caminos, y el segundo me parece mejor:

1. Escribir las 2.488 de una. Ocho o diez sesiones, sin poder verificar tres cuartos del resultado.
2. **Escribir primero las de inglés** — 622 glosas, es el único idioma base además del español que tenés a nivel C1, así que **las podés validar leyendo**. Con eso la app queda funcional para un usuario anglófono, que es el caso más probable después del hispanohablante. Las otras tres esperan.

El orden recomendado de idiomas base es entonces: **inglés, después portugués e italiano, y francés al final**, que es el inverso del orden de confianza pero coincide con el de utilidad: hay más gente aprendiendo alemán desde el inglés que desde el francés.

---

## E. Lo que no cambia

- Secciones plegadas por defecto.
- Sin persistencia de progreso.
- Sin permiso `INTERNET`.
- `aapt dump permissions` sobre el APK al terminar.
