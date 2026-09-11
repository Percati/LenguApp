# Ajustes para la Fase 7 — correcciones de interfaz, navegación y temas

Tres bloques. **El 2 va primero**: son cuatro defectos encontrados usando la app, y dos de ellos son errores reales, no preferencias. Arreglar defectos antes de rediseñar evita rediseñar sobre algo roto.

---

## Bloque 2 — Cuatro defectos encontrados en uso

### 2.1 El botón de ajustes choca con el encabezado y rompe el diseño

Síntomas observados:

- Se superpone con la información del encabezado.
- En la **semana actual** el diseño se rompe y pasa a vertical.
- Cuando **no** es la semana actual, se parte en dos renglones.

Que el diseño cambie según sea o no la semana en curso indica que la barra se está acomodando al ancho de su contenido, y ese ancho varía: la etiqueta "Hoy" aparece o desaparece. **Un diseño que depende del largo del texto se va a volver a romper** — con otro idioma de interfaz, con una semana de dos dígitos, o con la fuente del sistema agrandada.

Reubicar el botón de ajustes fuera de la zona del encabezado.

**Criterio de aceptación, no negociable:** verificar el diseño en las cuatro combinaciones —semana actual y semana anterior, por cada uno de los dos idiomas— **y además con la fuente del sistema al 150 %**, que es la condición con la que ya se probaron las fichas en la Fase 3. Sin desbordes, sin saltos de renglón inesperados y sin cambio de orientación del contenido.

### 2.2 «Semanas», «Ajustes» y «Hoy» no se traducen

Estas tres palabras siguen en un idioma fijo al cambiar el idioma de la aplicación.

Es el *chrome* que quedó diferido al cerrar la Fase 6, y la decisión de diferirlo fue correcta: traducir toda la interfaz a seis idiomas sin revisor nativo repite el problema que ya tuvimos con el contenido. **Pero estas tres son las únicas visibles en la pantalla principal**, y verlas en otro idioma mientras todo lo demás cambió es peor que no haber traducido nada.

Alcance acotado a propósito: **solo esas tres cadenas**, en los seis idiomas. El resto del chrome sigue diferido.

### 2.3 Los días de las micro-tareas están siempre en español

En los datos, `microtareas[].dia` es un enum con tres valores: `lun`, `mie`, `vie`. Es una clave interna, no texto para mostrar — y hoy se muestra tal cual.

Los días van **en el idioma que se aprende**, no en el de la aplicación. Es la misma regla que ya rige para los títulos de sección: el contenido de la ficha se muestra en su propio idioma.

| Clave | en | de | es | fr | it | pt |
|---|---|---|---|---|---|---|
| `lun` | Mon | Mo | lun | lun | lun | seg |
| `mie` | Wed | Mi | mié | mer | mer | qua |
| `vie` | Fri | Fr | vie | ven | ven | sex |

La tabla va escrita, no derivada de `Locale`: los nombres abreviados que produce el sistema no coinciden con la convención de los manuales (*Mo/Mi/Fr* en alemán, no *Mon/Wed/Fri*), y son justo los que el usuario va a reconocer del material impreso.

### 2.4 El gesto de atrás desde Ajustes cierra la app

Estando en Ajustes, el gesto de atrás del sistema —o el botón de la barra del teléfono— cierra la aplicación en vez de volver a la pantalla anterior.

**La causa probable, que importa más que el síntoma:** si Ajustes se muestra con una bandera booleana dentro de `MainActivity` y no como un destino de navegación, el gesto de atrás no tiene nada que apilar y va directo al sistema. Interceptar el gesto con un `BackHandler` tapa el síntoma; **la corrección es que Ajustes sea un destino real con su propia entrada en la pila**. Si no lo es, hay que hacerlo así.

Comportamiento pedido:

- **Desde Ajustes**: atrás vuelve a la pantalla principal.
- **Desde la pantalla principal**: el primer atrás muestra un mensaje breve —del mismo tipo que el del idioma no disponible— indicando que hay que repetirlo para salir. El segundo, dentro de dos o tres segundos, cierra.
- El mensaje va traducido según el idioma de la aplicación, igual que el punto 2.2.

Nota: el doble atrás para salir es un patrón que Android ya no recomienda, porque interfiere con el *predictive back*. Se implementa porque lo pediste, pero conviene saber que puede sentirse raro en Android 14 o posterior. Si molesta, quitarlo es de una línea.

---

## Bloque 0 — La navegación solo va hacia atrás

Hoy se puede navegar hacia adelante y hacia atrás. **Queda solo hacia atrás**, en todos los idiomas.

Motivo: poder ver las semanas que vienen invita a adelantarse y hacer varias misiones en paralelo, que es lo contrario de un sistema semanal. El modo revista ya asume que la semana perdida se perdió; ver el futuro no agrega nada y sí agrega presión.

**Cómo hacerlo, que es la parte importante:**

- **Quitar el botón de "semana siguiente" de la interfaz.**
- **No borrar la lógica.** La función que resuelve una fecha arbitraria (`resolverContenidoDeLaSemana` recibe la fecha, no usa `LocalDate.now()` internamente) se queda intacta y sigue cubierta por sus tests, incluidos los tres casos de borde ISO. Es lo que permitiría reactivar la navegación hacia adelante en una versión futura sin rehacer nada.
- **Sí permitir volver a hoy.** Si el usuario navegó tres semanas atrás, tiene que poder regresar a la semana en curso en un toque — eso es navegación hacia adelante en el sentido mecánico, pero no expone contenido futuro. El tope superior es la semana de hoy, no la semana en la que está parado.
- Dejar un comentario en el código explicando que el límite es una decisión de producto y no una limitación técnica, para que nadie lo "arregle" en seis meses.

---

## Bloque 1 — Temas visuales

Cuatro temas: dos claros y dos oscuros, en dos familias.

Cuatro temas: dos claros y dos oscuros, en dos familias.

### Requisito de implementación

Los cuatro son **seleccionables por el usuario**, más una opción **"Según el sistema"** que elige el claro o el oscuro de la familia activa según el modo del dispositivo.

Eso significa dos ajustes, no uno:

1. **Familia de tema**: Academia / Editorial
2. **Modo**: Claro / Oscuro / Según el sistema

Nota sobre la regla 2 del proyecto: leer el modo claro-oscuro del dispositivo es la misma clase de lectura que el idioma del sistema, ya enmendada en `AJUSTES-FASE-6.md`. Queda cubierta por esa enmienda — configuración local, nada que salga del dispositivo.

### Familia 1 — Academia y Comunidad (azul Oxford y grafito)

Autoridad y calma; limpio y utilitario, propio de herramientas libres confiables.

| Rol | Claro | Oscuro |
|---|---|---|
| Fondo (60 %) | `#F8FAFC` | `#0F172A` |
| Superficies y tarjetas (30 %) | `#FFFFFF` | `#1E293B` |
| Texto principal | `#1E293B` | `#F1F5F9` |
| Acento (10 %) | `#0F4C81` | `#60A5FA` |
| Secundario | `#64748B` | `#94A3B8` |

### Familia 2 — Editorial y Naturaleza (verde sabio y lino)

Emula un libro impreso de buena calidad. El verde apagado se asocia a concentración prolongada.

| Rol | Claro | Oscuro |
|---|---|---|
| Fondo (60 %) | `#FDFBF7` | `#141C16` |
| Superficies y tarjetas (30 %) | `#F4F1EA` | `#1E2B21` |
| Texto principal | `#242E26` | `#EAECE8` |
| Acento (10 %) | `#3A5F43` | `#86A789` |
| Secundario | `#708090` | `#A3B19B` |

### Regla de reparto

**60-30-10**: 60 % fondo dominante, 30 % superficies de estructura, 10 % acento para acciones. El acento se reserva para lo accionable — botón de copiar el prompt, pestañas activas, flechas de navegación. No se usa para decorar.

### Dos comprobaciones que hay que hacer, no suponer

**1. Contraste.** Los cuatro temas tienen que cumplir **WCAG AA (4,5:1)** para texto de cuerpo. Dos pares a medir antes de darlos por buenos:

- Editorial claro: `#242E26` sobre `#F4F1EA` (superficie, no fondo).
- Academia oscuro: `#94A3B8` como texto secundario sobre `#1E293B`. El secundario sobre superficie es el caso más ajustado de los cuatro temas.

Si alguno no llega, hay que ajustar el color, no el tamaño de letra. Y reportarlo, no corregirlo en silencio: la paleta la eligió el usuario.

**2. El marcado en línea tiene que seguir distinguiéndose.** `*cita*` y `**destaque**` se ven por estilo, no por color. En Satzbau el destaque marca **dónde va el verbo** y es el punto de la ficha: si en algún tema se pierde, el tema está mal, no la ficha.

---

## Orden de trabajo

1. **Bloque 2** — los cuatro defectos. Son errores, no gusto.
2. **Bloque 0** — navegación solo hacia atrás. Chico y sin dependencias.
3. **Bloque 1** — los cuatro temas. Al final, sobre una pantalla ya correcta.

El 2.1 y el Bloque 1 tocan la misma zona, así que hacer el 2.1 primero evita reubicar el botón dos veces.

## Lo que no cambia

- La lógica de navegación hacia adelante sigue existiendo y testeada; solo se retira el botón.

- Secciones plegadas por defecto.
- Sin persistencia de progreso. El tema es preferencia, igual que el idioma.
- Sin permiso `INTERNET`.
- `aapt dump permissions` sobre el APK al terminar.
