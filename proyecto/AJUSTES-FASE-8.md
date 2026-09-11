# Ajustes para la Fase 8

Diez puntos reportados en uso. Se reparten en tres grupos: **lo ya corregido en los datos**, **lo que le toca a la app** y **lo que es deuda de contenido y no se resuelve con código**.

---

## A. Ya corregido en los datos — recompilar y listo

### A.1 Títulos de cuadro en minúscula

16 de los 28 títulos de cuadro empezaban en minúscula: *opinion moves*, *die drei Deklinationstypen*, *the summary skeleton*.

Era mi compilador: el título sale de partir el encabezado `Übersichtskasten — die drei Deklinationstypen` por el guion, y lo que sigue al guion viene en minúscula. **Corregido: ahora 0 de 28.**

Detalle de implementación que conviene no "mejorar": la mayúscula se pone **solo en la primera letra**, sin tocar el resto. Usar `capitalize()` bajaría el resto y en alemán rompería los sustantivos — *Die drei deklinationstypen*.

---

## B. Para la app

### B.1 Las tarjetas del cuadro de referencia perdieron el fondo

Antes cada fila del cuadro se veía como una tarjeta con fondo; ahora es texto plano corrido. Restituir el fondo usando **el color de superficie del tema activo** (30 % de la regla 60-30-10), no un gris fijo: tiene que funcionar en los cuatro temas.

### B.2 No hay separación visual entre secciones

*Beispiele*, *Hinweise*, *Kontrast*, *Typische Fehler* se encadenan sin corte.

Aviso, porque ya discutimos esto: en la Fase 5 pedí explícitamente **menos líneas divisorias y más contraste tipográfico**, y el resultado quedó demasiado plano. La corrección no es volver a llenar de reglas horizontales. Lo que funciona, en este orden:

1. **Espacio vertical generoso** entre secciones — bastante más que entre párrafos de una misma sección.
2. **Contraste de tamaño y peso** entre el título de sección y su cuerpo.
3. Un separador tenue en color secundario **solo si con lo anterior no alcanza**.

El riesgo a evitar es el péndulo: de "demasiado plano" a "demasiado rayado".

### B.3 Las etiquetas de la ficha se ven como texto plano

`T01`, `Open production`, `400w · 6 min`, `60min` van como **burbujas** con fondo de superficie y texto secundario. Es el patrón que ya pedí en la Fase 5, punto 1, y quedó a medias.

### B.4 El contenido tiene que ser seleccionable y copiable

Hoy el texto no se puede seleccionar. Hace falta para traducir a mano un pasaje.

En Compose los `Text` no son seleccionables por defecto: hay que envolver el contenido en `SelectionContainer`. Cuidado con un detalle: **el botón de copiar el prompt tiene que seguir funcionando** y no quedar capturado por la selección.

### B.5 Quitar la etiqueta «núcleo»

Sale del campo `prioridad` mostrado en crudo, y además siempre en español.

Se quita el texto, **pero el dato se sigue usando**: los cuatro ítems núcleo se distinguen por peso tipográfico o por orden, no por una palabra al lado. La distinción es información útil y no conviene perderla.

### B.6 La sección de contraste no debe mostrarse con el contraste equivocado

Con idioma de aplicación distinto del español, el título se traduce a *Kontrast zum Englisch* pero el cuerpo sigue diciendo *Das Spanische kennt keine Verbendstellung*. El título miente sobre el contenido.

`contraste` es un mapa por idioma base y **solo tiene la clave `es`**. Regla para la app:

- Si existe `contraste[idiomaBase]`, mostrarla.
- Si **no** existe, **ocultar la sección entera**. Nunca caer a `es` bajo un título que anuncia otro idioma.
- Si el idioma de la aplicación **coincide** con el idioma que se aprende, ocultarla también: no hay contraste posible.

Es una regla distinta de la que rige para las glosas. Ahí el recurso a español es aceptable porque una glosa en otro idioma sigue siendo útil; acá el contenido afirma algo falso.

### B.7 Navegación: el botón de adelante solo se oculta en la semana actual

Mi instrucción de la Fase 7 fue ambigua y **el error es mío**: dije "quitar el botón de semana siguiente" y en el mismo bloque "sí permitir volver a hoy". Se interpretó como quitarlo siempre.

Comportamiento correcto: **la flecha de adelante se oculta únicamente cuando se está en la semana en curso.** Estando tres semanas atrás, se puede avanzar hasta hoy y ahí desaparece. El tope es hoy, nunca el futuro. Aplica a todos los idiomas y niveles.

Sigue valiendo lo demás: la lógica que resuelve una fecha arbitraria no se toca.

---

## C. Deuda de contenido — no se arregla con código

### C.1 Las traducciones solo existen en español

Vale para las glosas del vocabulario **y** para los Redemittel. `traducciones` es un mapa de cinco idiomas y **solo `es` está poblado**, en los 392 ítems. No es un defecto de la app: es contenido que no está escrito.

Lo que sí le toca a la app: cuando falta la clave del idioma base, el recurso a `es` es correcto, pero **conviene decirlo en una línea** en lugar de que el usuario descubra el desfase solo. Ya existe un aviso equivalente en la pantalla de ajustes.

Escribir las otras cuatro traducciones son 392 × 4 = **1.568 glosas**, más las de Redemittel. Y no las puedo validar en francés, italiano ni portugués.

### C.2 El contraste en otros idiomas base exige fichas nuevas

Tenés razón: es material nuevo, no una traducción. *El español no tiene posición final del verbo* y *English has no verb-final position* son observaciones distintas con consecuencias distintas. Multiplica por cada idioma base.

Hasta que exista, la regla B.6 evita que se muestre información falsa.

### C.3 Fichas de los niveles restantes: hay que decidir el alcance

Pediste A2 a C2 en inglés y alemán para 2026. Los números reales:

| | Hecho | Falta |
|---|---|---|
| Combinaciones idioma × nivel | 2 de 10 | **8** |
| Fichas | 34 | **~136** |
| Palabras | ~21.000 | **~120.000** |

A eso se suma que **A2 y B1 son bilingües por diseño**: la prosa va también en la lengua base, así que esas cuatro combinaciones tienen el doble de texto.

No es trabajo de una sesión ni de tres. Y hacerlo de corrido tiene un problema peor que el volumen: **ninguna de las ocho está validada en el uso propio**, que es el criterio que sostuvo la calidad hasta ahora.

**Orden propuesto**, del más sólido al más incierto:

| # | Combinación | Por qué en este lugar |
|---|---|---|
| 1 | **Inglés B2** | Adyacente a C1, que ya está escrito y auditado contra el EGP. El verificador cubre B2 con cuatro fuentes |
| 2 | **Alemán C1** | Adyacente a B2. Auditado contra Aspekte, Erkundungen y Sicher! |
| 3 | **Inglés C2** | El EGP tiene 112 entradas de C2. Sin `Objective Proficiency` legible, es lo más flojo del inglés |
| 4 | **Alemán C2** | Erkundungen C2 da la secuencia. Cuatro ítems de gramática, el resto registro y léxico |
| 5–6 | **Inglés B1**, **Alemán B1** | Bilingües: doble texto. Fuentes sólidas (Goethe B1, Cambridge B1 Preliminary) |
| 7–8 | **Inglés A2**, **Alemán A2** | Bilingües y con `challengeType` distinto (`chunk_deployment`): repertorio cerrado, no producción libre. Son las fichas más diferentes de todas las escritas |

Mi recomendación: **una combinación por vez, empezando por inglés B2**, y usarla unas semanas antes de seguir. Si al terminar la primera el molde aguanta en un nivel que no es el tuyo, las siete restantes son mecánicas.

Si preferís tenerlas todas antes de usarlas, se puede — pero entonces son ocho sesiones largas y el riesgo es que un defecto del molde se replique 136 veces en vez de 17.

---

## D. Decisión pendiente sobre la paleta

Code encontró, barriendo los cuatro temas, que el **secundario de Editorial claro (`#708090`) no cumple WCAG AA como texto**: 3,59:1 sobre superficie y 3,92:1 sobre fondo, ambos por debajo de 4,5:1. Lo reportó en vez de corregirlo, que era lo correcto: la paleta la elegiste vos.

Candidatos que conservan el tono pizarra y sí cumplen:

| Color | Sobre superficie | Sobre fondo |
|---|---|---|
| **`#5C6874`** | 5,05:1 | 5,51:1 |
| `#5A6773` | 5,14:1 | 5,61:1 |
| `#56626E` | 5,53:1 | 6,03:1 |

**Recomiendo `#5C6874`**: es el más cercano al original que pasa con margen.

Los otros dos pares que pedí medir dieron bien: `#242E26` sobre `#F4F1EA` da 12,46:1 y `#94A3B8` sobre `#1E293B` da 5,71:1.

---

## E. Lo que no cambia

- Secciones plegadas por defecto.
- Sin persistencia de progreso.
- Sin permiso `INTERNET`.
- `aapt dump permissions` sobre el APK al terminar.
