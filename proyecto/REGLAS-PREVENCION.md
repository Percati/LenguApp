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
6. **Ortografía y tuteo.** Ver FALTANTES 6.1 y 7.5: el tuteo unificado se hace al sumar el primer idioma nuevo.
7. **Regenerar assets** y dejar que el guard de Gradle dé verde.
