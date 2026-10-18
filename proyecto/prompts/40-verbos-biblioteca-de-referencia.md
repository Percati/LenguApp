# Verbos — primera tanda de verbos de referencia (A1 y A2)

Escrito por Overview el 18-oct-2026. Fer quiere **ampliar la biblioteca de verbos** con los de uso más común, para poder evacuar dudas buscando ahí. No un diccionario: una referencia suficientemente grande.

**Leé esto antes de escribir una línea, porque cambia qué es la tarea.**

## El hallazgo que define el trabajo

Medí la tabla maestra y el contenido:

| | alemán | inglés |
|---|---|---|
| Verbos en `contenido/verbos/{idioma}.json` | 305 | 360 |
| Núcleos verbales declarados | 197 | 256 |
| Verbos de la tabla que **no** aparecen en ningún pack | **0** | **0** |
| Entradas que hoy ve el usuario en la vista Verbos | ~470 | ~617 |

O sea: **la tabla maestra tiene exactamente los verbos que ya están en los packs**, porque se construyó a partir del contenido. No es un diccionario: es una **tabla de anotación** que le pone `claseVerbo` y `reccion` a ítems que ya existen.

**Consecuencia que hay que entender bien:** la vista Verbos de la app se construye desde los ítems de vocabulario del contenido (`ItemBiblioteca`), no desde esta tabla. **Un verbo que no está en ningún pack no puede aparecer en la app hoy.** Así que ampliar la biblioteca son dos trabajos:

1. **El tuyo, ahora:** escribir las entradas de referencia en la tabla, con su traducción y su clase.
2. **Una ronda posterior de Code:** que la vista tome **una segunda fuente** de entradas, para que esos verbos se vean.

Como el 2 todavía no existe, **lo que escribas no se va a ver en la app hasta entonces**, y por eso esta primera tanda es chica a propósito: si la forma está mal, se corrige con 200 entradas y no con 600.

## Tarea — la primera tanda: A1 y A2

### De dónde salen los verbos

De **`proyecto/data/vocab_de.json`** (7683 lemas) y **`proyecto/data/vocab_en.json`** (10284), que son las listas que ya se armaron con `build_wordlists.py` a partir de los perfiles de Goethe, CEFR-J y compañía. Cada entrada trae `forma`, `nivel`, `niveles_atestiguados` y `apariciones` con su `fuente`. **Es la fuente de verdad para decidir qué verbo es común: no la inventes ni la saques de otro lado.**

Medido en alemán: hay ~2670 candidatos a verbo, de los cuales **2540 no están en la tabla maestra** — 79 de A1, 123 de A2, 414 de B1, 1382 de B2 y 542 de C1.

**Esta tanda es A1 + A2 solamente: ~200 verbos en alemán**, y el equivalente en inglés. Nada de B1 todavía, y **nunca** los 1382 de B2: eso sería el diccionario que Fer dijo que no quiere, y además los verbos de B2 y C1 ya están cubiertos por el contenido de esos niveles.

**Por qué A1 y A2 primero, y no los de nivel alto:** la duda que se va a buscar en la biblioteca es «¿qué quería decir esta palabra que ya vi diez veces?», y eso pasa con los verbos básicos, no con los raros. Un verbo de C1 que apareció una vez en una ficha **ya está** en la vista, con su semana y su traducción.

### Cómo identificar los verbos en la lista

En alemán, las entradas de sustantivo traen el artículo en `forma` («die Wohnung»), así que un candidato a verbo es una entrada **sin artículo** que termina en `-en`, `-rn` o `-ln`. **Ese filtro es un punto de partida, no un oráculo:** deja entrar sustantivos plurales y adjetivos, así que hay que revisar la lista a mano antes de escribir. En inglés no hay un filtro equivalente y hay que ir por semántica; `EN Grammar Vocabulary First and First for Schools Phrasal verbs.pdf` e `Irregular verbs.pdf` (documentos del proyecto) son buenas listas de arranque para los phrasal e irregulares.

**Decime cuántos candidatos quedaron después de tu revisión a mano**, por nivel, antes de escribir las traducciones: si el filtro deja mucha basura, lo ajustamos entre los dos.

### La forma de cada entrada nueva

En el array `verbos` de `contenido/verbos/{idioma}.json`, **sin tocar ninguna entrada existente**:

```json
{
  "forma": "aufstehen",
  "item": "aufstehen",
  "base": "aufstehen",
  "raiz": "stehen",
  "claseVerbo": ["trennbar"],
  "referencia": true,
  "nivelFuente": "A1",
  "traducciones": {
    "es": "levantarse",
    "en": "to get up",
    "fr": "se lever",
    "it": "alzarsi",
    "pt": "levantar-se"
  }
}
```

Reglas de la forma, y son importantes:

- **`referencia: true`** distingue estas entradas de las que anotan ítems del contenido. `aplicar_verbos.py` **no las debe aplicar a ningún pack** — comprobalo corriendo la herramienta y confirmando que sigue dando **0 packs con diferencias**. Si las aplica, pará y avisá: hay que arreglar la herramienta antes de seguir.
- **`traducciones` sin el idioma que se aprende.** Un verbo alemán lleva es, en, fr, it, pt; uno inglés lleva es, de, fr, it, pt. **Nunca una glosa al propio idioma** — desde la Ronda R la app no muestra glosa cuando el idioma de la app es el que se aprende, así que una glosa `de` en un verbo alemán es un campo muerto.
- **`claseVerbo`** con las clases que ya conoce la app: alemán `reflexivo`, `trennbar`, `mit-Praposition`, `mit-Dativ`; inglés `phrasal-separable`, `phrasal-inseparable`, `verbo-preposicion`; y `simple` cuando no es ninguna. **No inventes clases nuevas:** la app muestra el verbo igual, pero la clase no sería un chip y nadie podría filtrar por ella.
- **`reccion`** solo en alemán y solo cuando el verbo la tiene de verdad (`von + Dat`, `auf + Akk`), con la misma notación que las entradas que ya están.
- **`nivelFuente`** es un **campo de trabajo**, como `base`, `raiz` y `nota`: queda en la tabla para saber de dónde salió cada verbo y **no llega al contenido ni a la app**. Fer decidió que la vista Verbos **deja de mostrar el nivel**, porque el nivel no es una propiedad del verbo sino de nuestra cobertura.
- **`raiz`** en los separables y los reflexivos, como ya se hace: es lo que permite que la búsqueda por «stellen» encuentre la familia entera, que es la función que Fer más usa de esta vista.

### Lo que NO entra

- Verbos que ya están en la tabla (son 305 en alemán y 360 en inglés: **comprobá contra `forma` antes de agregar**, no después).
- Modales y auxiliares (`sein`, `haben`, `werden`, `können`, `müssen`, `be`, `have`, `do`): son gramática y están en los núcleos de los skills, no en una lista de consulta.
- Verbos que en la lista figuran solo como parte de una expresión fija: el que va a la tabla es el infinitivo.
- Nada de niveles B1 a C2 en esta tanda.

### Qué entregar

1. El parche con las entradas nuevas en los dos archivos de `contenido/verbos/`.
2. **La cuenta:** cuántas entradas nuevas por idioma y por `nivelFuente`, y cuántos candidatos descartaste a mano y por qué.
3. La confirmación de que `python3 proyecto/tools/aplicar_verbos.py` sigue dando **0 packs con diferencias** y **0 errores**, y que `python3 proyecto/tools/validar_packs.py --anio 2026` y `--anio 2027` siguen en verde.
4. **Diez entradas al azar, puestas en el informe**, para que Fer mire las traducciones sin abrir el JSON. Él juzga alemán e inglés; francés, italiano y portugués **no los puede revisar nadie todavía**, así que escribilos con cuidado y marcá cualquiera que te haya dado duda.
5. Lo que decidiste donde este prompt no cerraba.

## Qué no hacer

- No toques los packs, los núcleos, las ocurrencias ni nada de `app/`.
- No modifiques entradas existentes de la tabla, ni les agregues `traducciones` (las suyas salen del pack).
- No agregues `nivel` como campo que llegue al contenido.
- No escribas la segunda tanda (B1) hasta que Fer apruebe la forma de esta.

## Lo de siempre

Un commit, nada de push, el parche en `outputs/verbos-referencia-1/`.
