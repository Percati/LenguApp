# Reglas de prevención (oct 2026)

Salen de los errores que Fer fue encontrando a mano en la app. Cada regla dice
**qué se rompió**, **la regla** y **cómo se comprueba**. Antes de sumar un
idioma (español, francés, italiano, portugués) se recorre la sección 3.

## 1. Reglas

| # | Qué se rompió | Regla | Cómo se comprueba |
|---|---|---|---|
| P1 | La planilla de DE B1 mostraba el topic en español; la de C1, en español en vez de inglés | **Cada superficie tiene un idioma de salida fijo** (tabla 2). Ningún texto sale de una fuente de un solo idioma: todo nombre propio de la app (topics, categorías, rótulos) existe en los 6 idiomas | `verificar_consistencia.py` R2 y R3; test de Code por superficie |
| P2 | Un error común de DE B1 seguía en alemán con el switch en español | **En A2/B1 toda la prosa es bilingüe**, incluidos los ítems de `erroresContrastivos` (`{aprendido: original, clave: traducción}`). Un campo nuevo de prosa nace bilingüe o no nace | `verificar_consistencia.py` R1; esquema |
| P3 | La Biblioteca mostraba filtros que daban lista vacía en todos los niveles de un idioma | **Ninguna lista de filtros es fija**: se calcula en ejecución a partir de los ítems del idioma elegido. Un valor sin ningún ítem en ningún nivel de ese idioma no se muestra. Vacío en un nivel concreto sí se permite | Test de Code contra los assets reales, por idioma |
| P4 | El desafío del fin de semana era idéntico a la misión | **Una función que reutiliza contenido debe verse distinta de su fuente**: cambia el encuadre, no solo el título | Test: texto visible del desafío ≠ texto visible de la misión |
| P5 | Fragmentos en otro idioma sin « » | **Todo fragmento en un idioma distinto al del texto que lo rodea va entre « »**, escrito a mano en el dato; el renderizador no lo adivina. Francés con espacio interior (`« I think »`), el resto sin. El `titulo` es la excepción: no lleva marcado de ningún tipo (tope de 80 caracteres) | `verificar_consistencia.py` **R4 (que estén balanceados) + R8 (que EXISTAN)**; ver FALTANTES 14 |
| P6 | Los assets de la app estaban desfasados del contenido | **`assets/contenido/` se recompone y se compara en cada build** | Tarea Gradle `verificarAssetsContenidoActualizados` |
| P7 | Textos de la interfaz escritos solo en español | **Ningún texto visible se escribe en Kotlin fuera de `TextosInterfaz`** y cada clave tiene los 6 idiomas | Test de cobertura de claves |
| P8 | La planilla se salía a una segunda carilla casi vacía | **La planilla ocupa una hoja (dos carillas) como máximo; si la segunda lleva 3 líneas o menos, se compacta para que entre todo en una** | Test de layout de Code sobre fichas reales |
| P9 | Las instrucciones trataban al aprendiente de *Sie*, *vous* y *Lei* mientras el español ya estaba en tú | **La app le habla al aprendiente de tú en los seis idiomas**, también en alemán (`du`), francés (`tu`) e italiano (`tu`). Alcanza solo a los **campos de instrucción** (misión, micro-tareas, autochequeo, prompt, notas, descripción, subtítulo, requisitos): en `redemittel`, `ejemplos`, `cuadroReferencia`, `contraste`, `erroresContrastivos` y `vocabulario` el registro formal puede ser justamente lo que se enseña, y ahí no se toca. Tampoco se toca lo que va entre « » ni entre `*…*`: ahí va el idioma que se aprende | `verificar_consistencia.py` **R9**, con `EXENTAS_R9` (cada excepción dice por qué) y `--autotest`; ver FALTANTES 6.1.3 |
| P10 | Quedaban 90 cadenas con `ß` en alemán | **Ortografía suiza: `ss`, nunca `ß`.** La única excepción es la cadena donde la eszett es el objeto de la explicación (la ficha `DE-V08` y las notas sobre «ss/ß») | `verificar_consistencia.py` **R10**, con `EXENTAS_R10`; ver FALTANTES 7.5 |
| P11 | Dos repeticiones del mismo verbo llevaban clase o régimen distintos, y la clasificación por heurística se equivocaba en los verbos con prefijo | **La clase de un verbo se decide una sola vez, en la tabla maestra `contenido/verbos/{idioma}.json`, leyendo ítem por ítem; nunca por heurística.** Los packs solo reciben lo que dice la tabla (`tools/aplicar_verbos.py`). Un ítem fuera de la tabla no lleva `claseVerbo` ni `reccion` verbal. Si el régimen no es seguro, `reccion` queda vacío y se lista | `verificar_consistencia.py` **R11** (misma clase y mismo `reccion` en todas las repeticiones; `simple` exclusiva; formato de `reccion`); `aplicar_verbos.py` sin `--escribir` debe dar 0 diferencias; el esquema repite las reglas |

## 2. Idioma de salida por superficie

| Superficie | Idioma |
|---|---|
| Rótulos, botones, ayudas, Acerca de | Idioma de app |
| Ficha, B2/C1/C2 | Idioma que se aprende (sin switch) |
| Ficha, A2/B1 | Idioma que se aprende; con el switch activo, idioma de app |
| Traducciones de vocabulario y expresiones (ficha, Biblioteca, Guardados) | Idioma de app, siempre |
| Nombres de topic y de categoría en Biblioteca y Guardados | Idioma de app |
| Planilla del profesor (todo, incluido el nombre del topic) | Idioma que se aprende |
| Prompt de corrección y prompt de voz | Idioma que se aprende |
| Fragmento en otro idioma dentro de un texto | Entre « » |

## 3. Antes de sumar un idioma

1. **Idioma de app.** Los 6 idiomas ya existen en la interfaz: comprobar que no queda una clave sin traducir (`TextosInterfaz`, `EtiquetasSeccion`, planilla, topics, categorías). `verificar_consistencia.py` debe dar 0.
2. **Idioma a aprender.** Recorrer la tabla 2 con una ficha de cada nivel A2 a C2 en cada idioma de app: ficha con y sin switch, Biblioteca (todas las pestañas), Guardados, planilla, prompts, desafío del fin de semana, semana de repaso y Survival.
3. **Filtros.** En la pestaña del idioma nuevo, ningún chip da vacío en todos los niveles (P3).
4. **Errores contrastivos.** El idioma nuevo agrega una clave a `erroresContrastivos` de todos los núcleos de los demás idiomas, y los núcleos nuevos llevan las 5 claves.
5. **Contraste y « ».** Aplicar P5 al contenido nuevo antes de embeberlo.
6. **Ortografía y tuteo.** El tuteo ya está unificado en los seis idiomas para el contenido de alemán e inglés (FALTANTES 6.1.3): el contenido del idioma nuevo **nace tuteando** y con `ss` en alemán, y R9/R10 lo comprueban. Dos cosas que las reglas **no** pueden ver y hay que leer a ojo:
   - En italiano la cortesía usa la 3.ª persona del indicativo (`che cosa ha comprato?`), idéntica a una 3.ª persona real (`che cosa ha detto il medico?`). R9 no las distingue.
   - Los fragmentos alemanes marcados con `*…*` dentro de una instrucción en otro idioma (`*interessieren Sie sich*`, `*beschweren Sie sich*`) siguen en formal: son la expresión que se enseña y R9 no mira dentro de las marcas. Si se decide pasarlos a `du`, es un pase aparte sobre `redemittel`/`vocabulario`, no sobre las instrucciones.
7. **Autotest de las reglas.** `python3 proyecto/tools/verificar_consistencia.py --autotest` prueba R9/R10 contra casos fijos y avisa si alguna excepción quedó de más (ya no la marca la regla). Un chequeo que da 0 porque está ciego no sirve.
8. **Regenerar assets** y dejar que el guard de Gradle dé verde.
