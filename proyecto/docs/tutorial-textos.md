# Textos del tutorial con foco — fuente editable (español)

Acá están **todos** los títulos y cuerpos que muestra el tutorial, en español, para editar a mano. Es el documento de trabajo: Fer edita esta columna y Overview traduce a los otros cinco idiomas y lo pasa a `TextosInterfaz.kt`. Lo demás (inglés, alemán, francés, italiano, portugués) **no se edita acá**.

**Las filas se identifican por el ancla**, no por el nombre de la clave: así siguen valiendo aunque Code renumere `TOUR_FICHA_n` al reordenar los pasos.

**Estado (14-oct-2026, versión 0.4.1):** la Ronda R ya aplicó el **orden nuevo**, las dos anclas nuevas, la fusión de Guardados y la inversión del desafío. Los textos **no se tocaron**, salvo tres que nacieron en esa ronda y están marcados **PROVISIONAL**: son los que escribí yo en el prompt y hay que decidirlos acá. La columna «Propuesta de Overview» sigue sin aplicarse: eso es justamente lo que se decide en este archivo.

**Cómo editar:** cambiá el texto de la columna «Título» y «Cuerpo» donde quieras. Si querés borrar un paso, escribí `BORRAR` en el título. Si querés uno nuevo, agregá una fila y decime a qué parte de la pantalla tiene que apuntar (no hace falta que sepas el nombre del ancla). No hace falta que respetes el largo: si un texto se va de largo, la tarjeta tiene su propio desplazamiento.

**Registro:** español neutro, tuteo («puedes», «eliges»), sin «vos» ni «usted». Es lo que usa toda la app.

**Regla que conviene no romper:** el foco manda sobre el texto. Si el recuadro ilumina una cosa, el cuerpo habla de esa cosa; puede decir dónde mirar después («debajo», «cuando cierres esto»), pero no describir algo que no está a la vista. Y ningún paso puede pedir que toques algo: mientras el tour está en marcha, el recuadro se come los toques.

---

## Tour de la ficha (el primero que se ve)

En el **orden nuevo**, que es el orden en que las cosas aparecen en la pantalla, de arriba hacia abajo. La columna «Propuesta de Overview» es mi reescritura de los pasos que leí flojos contra lo que la app muestra; donde dice «—», el texto actual me parece bien.

| # | Ancla (a qué apunta) | Título actual | Cuerpo actual | Propuesta de Overview |
|---|---|---|---|---|
| 1 | `tour-ficha-titulo` — el bloque de título de la semana | Esta es tu semana | Un tema y una habilidad, del lunes al domingo. No hace falta terminarla: haz lo que puedas. | — |
| 2 | `tour-ficha-traducir` — el interruptor «Traducir al …» (solo existe en A2 y B1) | Ficha en tu idioma **(PROVISIONAL)** | En A2 y B1 puedes ver la ficha traducida a tu idioma o en el idioma que estás aprendiendo. Desde B2 ya no aparece: la ficha va siempre en el idioma que aprendes. **(PROVISIONAL)** | — *(es mi texto del prompt; decidilo o reescribilo)* |
| 3 | `tour-ficha-descripcion` — el párrafo de descripción | Para entender de qué va | La descripción, el cuadro de referencia, los ejemplos y las notas. Es la explicación corta, no un capítulo de gramática. | **Título:** «La explicación corta». **Cuerpo:** «De qué va la habilidad de esta semana. Debajo, si la ficha los trae, vienen el cuadro de referencia, los ejemplos y las notas. Es media página, no un capítulo de gramática.» *(el foco cae solo sobre la descripción, no sobre las otras tres cosas)* |
| 4 | `tour-ficha-errores` — la sección «Errores comunes» | Lo que se compara y lo que falla | El contraste y los errores típicos: qué cambia respecto de tu idioma y en qué solemos equivocarnos. | **Título:** «Los errores típicos». **Cuerpo:** «En qué se suele equivocar quien viene de tu idioma. Algunas fichas traen además un contraste con tu idioma, justo encima.» *(el foco cae solo sobre los errores)* |
| 5 | `tour-ficha-vocabulario` — el encabezado de la sección de vocabulario | Vocabulario y expresiones | Toca el encabezado para abrir la lista. Con la estrella guardas lo que te sirva; después lo encontrarás en Guardados. | **Título:** «Vocabulario y expresiones». **Cuerpo:** «El encabezado abre la lista cuando termines con esto. Cada palabra y cada expresión tienen una estrella: lo que marques queda en Guardados.» *(la estrella no se ve con la lista cerrada, y durante el tour no se puede tocar nada)* |
| 6 | `tour-ficha-mision` — la sección «Misión» | La misión | Lo que te pedimos que produzcas esta semana, con sus requisitos y sus micro-tareas si quieres ir por partes. | — |
| 7 | `tour-ficha-autochequeo` — la sección «Autochequeo» | El autochequeo | Antes de darla por hecha, mira si cumples esto. | — |
| 8 | `tour-ficha-prompt` — la tarjeta del prompt de corrección | Los prompts y la planilla | Un texto listo para pegarle a una IA y que te corrija, y una hoja para imprimir y darle a quien te enseña. Van siempre en el idioma que estás aprendiendo, para que la respuesta también. | **Título:** «El prompt para la IA». **Cuerpo:** «Un texto listo para pegarle a una IA y que te corrija lo que escribiste. Va siempre en el idioma que estás aprendiendo, para que la respuesta también.» *(la planilla pasa a tener su propio paso, el 9)* |
| 9 | `tour-ficha-planilla` — el botón de la planilla del profesor (solo si la ficha trae planilla) | La planilla para tu profesor **(PROVISIONAL)** | Genera una hoja en PDF, en el idioma que aprendes y en una sola carilla, para imprimir y darle a quien te da clase. **(PROVISIONAL)** | — *(es mi texto del prompt; decidilo o reescribilo)* |
| 10 | `tour-barra-superior` — la fila de Biblioteca, Guardados y Ajustes (cierre) | Biblioteca, Guardados y Ajustes | Arriba tienes Biblioteca (todo el vocabulario y las expresiones del año), Guardados (lo que marcaste con la estrella) y Ajustes. Cada una se explica sola cuando entres. | — *(el párrafo de «LenguApp no enseña un idioma…» ya se quitó en la Ronda R)* |

## Tour de la Biblioteca

| # | Ancla | Título actual | Cuerpo actual | Propuesta de Overview |
|---|---|---|---|---|
| 1 | `tour-biblioteca-vistas` — el selector de vistas entero | Tres vistas | Vocabulario y expresiones, Habilidades y Verbos. | — |
| 2 | `tour-biblioteca-filtros` — la fila de filtros | Los filtros se arman solos | Salen de lo que hay en tu idioma y tu nivel: si un filtro no aparece, es que no hay nada que filtrar. | — |
| 3 | `tour-biblioteca-buscar` — la barra de búsqueda | La búsqueda | No distingue mayúsculas ni tildes, y busca también en la traducción. | — |
| 4 | `tour-biblioteca-vista-verbos` — la **opción** «Verbos» del selector (hoy apunta al selector entero, igual que el paso 1) | Verbos | Están todos los del idioma, de cualquier nivel, agrupados por verbo; debajo de algunos verás las frases donde se usan. Busca la raíz («stellen») para ver la familia entera. | — *(cambia el ancla, no el texto)* |
| 5 | `tour-biblioteca-estrella` — la estrella de la primera fila | La estrella | Hace lo mismo que en la ficha. | — |

## Tour de Guardados

Eran tres pasos y los dos últimos señalaban el mismo grupo de botones: la Ronda R los fusionó. Quedan dos.

| # | Ancla | Título actual | Cuerpo actual | Propuesta de Overview |
|---|---|---|---|---|
| 1 | `tour-guardados-lista` — la lista | Aquí está lo que marcaste | Se ve igual que en la Biblioteca. | **Título:** «Lo que marcaste con la estrella». **Cuerpo:** «Aparece aquí, con el mismo aspecto que en la Biblioteca. Si todavía no marcaste nada, está vacío: es lo normal la primera vez.» *(en el primer ingreso la lista está vacía y el foco cae sobre el aviso de «no hay guardados»)* |
| 2 | `tour-guardados-archivo` — el grupo de botones de archivo (ya fusionado) | Exportar e importar **(PROVISIONAL)** | Puedes mandar tus guardados a Anki o guardar una copia de seguridad, y volver a importarla. El archivo lo eliges tú en el momento; al importar, lo que traes se suma a lo que ya tienes, sin duplicar. **(PROVISIONAL)** | — *(es mi texto del prompt; decidilo o reescribilo)* |
| ~~3~~ | ~~`tour-guardados-archivo`~~ | ~~Importar~~ | ~~Une lo que traes con lo que ya tienes, sin duplicar.~~ | **ya fusionado con el 2 en la Ronda R** |

## Tour de Ajustes

| # | Ancla | Título actual | Cuerpo actual | Propuesta de Overview |
|---|---|---|---|---|
| 1 | `tour-ajustes-idiomas` — los dos selectores de idioma | Idioma de la app y qué practicas | Aquí cambias el idioma de la app y los idiomas y niveles que practicas. | — |
| 2 | `tour-ajustes-tema` — tema, modo y tipografía | Tema y tipografía | Elige lo que te resulte más cómodo de leer. | — |
| 3 | `acerca-de` — la fila «Acerca de» | Acerca de | Con lo que la app guarda y lo que no. | — |
| 4 | `ver-bienvenida` — la fila «Ver la bienvenida y el tutorial otra vez» | Repetir el tutorial | Desde aquí puedes volver a ver la bienvenida y el tutorial cuando quieras. | — |

## Tour del desafío del fin de semana

El orden ya se invirtió en la Ronda R: primero el interruptor, después la consigna. **Ojo con este tour:** el interruptor arranca apagado y el tour no lo enciende, así que hoy el paso 2 **casi siempre se saltea** y el tour dice «Paso 1 de 1». Si se aprueba que el elemento con foco se pueda tocar (ver `prompts/35`), el paso 1 puede invitar a encenderlo y el 2 pasa a verse.

| # | Ancla | Título actual | Cuerpo actual | Propuesta de Overview |
|---|---|---|---|---|
| 1 | `tour-desafio-interruptor` — el interruptor | El interruptor arranca apagado | Cada vez que abres la app: no se guarda nada. | **Título:** «El desafío del fin de semana». **Cuerpo:** «Aparece sábado y domingo. Es para hablar en voz alta, no para escribir. Enciéndelo cuando quieras hacerlo: arranca apagado cada vez que abres la app y no se guarda nada.» |
| 2 | `tour-desafio-consigna` — la consigna | Esto es para hablar | No para escribir. La consigna está arriba y las condiciones debajo. | **Título:** «La consigna». **Cuerpo:** «Arriba, con los minutos que te pedimos; debajo, el tema, la habilidad, las condiciones y unas frases para empezar.» *(si dejas el interruptor apagado, este paso no se muestra: no hay nada que señalar)* |

## Los botones del recuadro

| Clave | Texto en español | Nota |
|---|---|---|
| `TOUR_SIGUIENTE` | Siguiente | |
| `TOUR_ATRAS` | Atrás | el botón que pediste, ya en la app |
| `TOUR_SALTAR` | Saltar | Saltea **todos** los tours que faltan, no solo este |
| `TOUR_LISTO` | Listo | En el último paso, en vez de «Siguiente» |
| `TOUR_PASO` | Paso {n} de {total} | |
