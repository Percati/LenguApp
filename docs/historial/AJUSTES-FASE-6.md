# Ajustes para la Fase 6 — pantalla de ajustes y multi-idioma

Cinco cambios. Dos son correcciones de errores de diseño míos, dos son funcionalidad nueva y uno enmienda una regla dura.

---

## A. Quitar los nombres de variables de la interfaz

La pantalla de ajustes muestra hoy "Idioma de las traducciones (baseLanguage)" y "Idioma de la interfaz (uiLanguage)". **Eso es culpa mía**: puse los nombres de campo en el brief y llegaron a la pantalla. Al usuario no le interesa cómo se llama la variable.

Quitar todo paréntesis con nombre de campo de los textos visibles. Los nombres se mantienen en el código y en el JSON, donde sí sirven.

---

## B. Varios idiomas en paralelo, con nivel por idioma

**Cambio de modelo, no de pantalla.** Hoy `Ajustes` tiene `idiomaAprendido: Idioma` y `nivel: Nivel`, ambos singulares. Pasa a:

```kotlin
data class Ajustes(
    val idiomasAprendidos: Map<Idioma, Nivel>,   // reemplaza idiomaAprendido + nivel
    ...
)
```

Comportamiento:

- En "Idioma que aprendés", al tocar un idioma **no seleccionado** se despliega un menú de A2 a C2 para elegir su nivel.
- Al tocar un idioma **ya seleccionado** se deselecciona, y su pestaña desaparece de la pantalla principal.
- La pantalla principal muestra **una pestaña por idioma seleccionado**, cada una con el contenido de *su* nivel. Seis idiomas elegidos, seis pestañas.
- El nivel se puede cambiar después sin deseleccionar.

**Migración obligatoria.** Hay ajustes ya persistidos con el esquema viejo. Al leer un `SharedPreferences` con `idiomaAprendido`/`nivel` singulares, convertirlos a un mapa de un elemento y reescribir. Sin eso, la app arranca sin ningún idioma seleccionado y parece rota.

**Idiomas no disponibles.** Español, francés, italiano y portugués no tienen contenido. Su casilla va en gris, no seleccionable, y **al mantenerla pulsada** aparece un mensaje breve (tipo *toast*, no ventana) diciendo que el idioma todavía no está disponible — en el idioma de interfaz elegido.

Nota: la lista de idiomas disponibles **se deriva del contenido embebido**, no se escribe a mano. Cuando se agreguen fichas de italiano, su casilla se habilita sola. Ya existe el precedente de `variantesConocidas()`, que descubre las variantes desde los datos.

---

## C. Un solo ajuste de idioma para interfaz y glosas

Se unifican en **"Idioma de la aplicación"**. Al elegirlo, cambia tanto la interfaz como las glosas de vocabulario.

**En la interfaz, un ajuste. En los datos, los dos campos siguen existiendo.** El modelo no se toca: `uiLanguage` y `baseLanguage` se escriben con el mismo valor. La razón de dejarlos separados es que son cosas distintas y algún día alguien va a querer interfaz en alemán con glosas en español; el día que eso pase, se desdobla el ajuste y no hay que migrar datos.

### Corrección factual, porque afecta la decisión

**Las glosas no están solo en A2 y B1.** Los 392 ítems de vocabulario del contenido actual tienen glosa, y todo el contenido actual es B2 y C1. Ninguna ficha tiene `bilingue = true` todavía.

Lo que es exclusivo de A2 y B1 es la **prosa bilingüe** (descripción, notas y contraste mostrados también en la lengua base). Las glosas de vocabulario están en todos los niveles.

Consecuencia práctica: hoy **solo existen glosas en español**. Si alguien elige italiano como idioma de la aplicación, la interfaz se traduce pero las glosas caen a español. Eso es correcto como recurso, pero conviene que la app lo diga en una línea en la pantalla de ajustes, en vez de que el usuario descubra el desfase solo.

### Opción "Idioma del sistema"

Se agrega, y **enmienda una regla dura del proyecto**. Ver bloque E.

---

## D. Se elimina el ajuste de variantes regionales

El argumento es correcto: si se ofrecen helvetismos pero no austriacismos, ni británico frente a americano, ni europeo frente a brasileño, la selección es arbitraria. Y completar todas las variedades no es trabajo para ahora, con cuatro idiomas todavía sin ninguna ficha.

**Decisión: el apartado desaparece de los ajustes.** El contenido regional viene siempre incluido, como ítems de vocabulario adicionales: al pack base de 14 se suman los helvetismos relevantes del tema, hasta un máximo de 5.

Lo que cambia en la app:

- Fuera la pantalla de variantes y `variantesDesactivadas` de `Ajustes`.
- Fuera `filtrarVariantesDesactivadas()`.
- **El campo `variante` se queda en el schema.** Los ítems marcados se siguen mostrando con su insignia (`CH`), solo que ya no se pueden ocultar. Es información útil: el usuario tiene que saber que *Velo* no se dice en Berlín.
- La `variante` **a nivel de ficha** deja de tener sentido. Si una ficha entera estaba marcada como regional, hay que rehacerla.

### Consecuencia en el contenido, no en la app

Esto obliga a rehacer la ficha de la semana 53 (`DE-V08 Helvetismen`), que es una ficha entera dedicada al tema. Y hay que decidir si `DE-V08` sigue siendo un skill del banco o se disuelve en ítems adicionales de los demás packs.

**No lo resolvemos en esta fase.** Queda anotado: es trabajo de contenido, no de código, y la app tiene que tolerar que esa ficha exista tal como está hasta que se rehaga.

Ventaja para el futuro: el mecanismo que ya existe (`variante` por ítem) soporta `AT`, `BrE`, `AmE`, `BR` sin rediseñar nada. Cuando quieras equidad entre variedades, es trabajo de contenido y no de arquitectura.

---

## E. Enmienda a una regla dura: lectura del idioma del sistema

La regla 2 del brief dice: **"el único dato que se lee del dispositivo es la fecha"**. La opción "Idioma del sistema" la viola: lee la configuración regional.

Se enmienda a propósito y queda registrado, en lugar de colarse sin que nadie lo note.

**Nueva formulación:** *los únicos datos que se leen del dispositivo son la fecha y, si el usuario lo elige explícitamente, el idioma del sistema. Ningún dato sale del dispositivo.*

Por qué es aceptable: la app no tiene permiso `INTERNET`, así que nada de lo que lea puede salir. El idioma del sistema no identifica a nadie. Y lo lee solo si el usuario activa esa opción.

Por qué hay que escribirlo igual: si la regla queda como está, alguien la va a citar dentro de seis meses para rechazar esta opción, o —peor— la va a usar como precedente para leer algo que sí importe. **Una regla dura que se incumple en silencio deja de ser una regla.**

Actualizar la formulación en `CLAUDE.md` y en la sección 2 de `PROMPT-CLAUDE-CODE.md`.

---

## F. Lo que no cambia

- Las secciones plegables siguen plegadas por defecto.
- Sigue sin haber persistencia de progreso. Los ajustes son preferencias, no progreso.
- Sigue sin permiso `INTERNET`.
- Al terminar, `aapt dump permissions` sobre el APK real.
