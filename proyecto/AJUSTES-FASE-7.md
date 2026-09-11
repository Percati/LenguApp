# Ajustes para la Fase 7 — temas visuales y navegación hacia atrás

Dos bloques: los cuatro temas y un cambio en la navegación.

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

## Lo que no cambia

- La lógica de navegación hacia adelante sigue existiendo y testeada; solo se retira el botón.

- Secciones plegadas por defecto.
- Sin persistencia de progreso. El tema es preferencia, igual que el idioma.
- Sin permiso `INTERNET`.
- `aapt dump permissions` sobre el APK al terminar.
