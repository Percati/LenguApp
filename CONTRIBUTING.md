# Cómo contribuir

Gracias por el interés. Este proyecto acepta aportes de contenido y de código, con reglas distintas para cada uno.

## Licencias: lo que aceptás al contribuir

Al enviar una contribución aceptás que se publique bajo:

- **GPL-3.0** si es código.
- **CC BY-SA 4.0** si es contenido pedagógico (fichas, vocabulario, ejemplos, traducciones).

No hay acuerdo de cesión de derechos: conservás la autoría de lo tuyo. Pero **una vez integrado, no se puede relicenciar sin tu permiso**, así que la licencia tiene que quedar clara desde el primer aporte.

## Contenido: originalidad obligatoria

**No se aceptan listas ni ejemplos copiados de material con copyright.** En concreto, nada de: listas de vocabulario del Goethe-Institut, entradas del English Vocabulary Profile, frases de ejemplo de manuales, ni contenido de Profile deutsch.

Esas fuentes se pueden usar **como referencia para verificar el nivel** de algo, y se citan. Lo que se escribe tiene que ser original.

Si tenés dudas sobre si algo es copia o no, la prueba es simple: si alguien con el original al lado reconocería la fuente, es copia.

## Contenido: cómo se escribe

1. El contenido vive en Markdown legible, no en JSON. El JSON se compila.
2. Cada ficha tiene que validar contra `schema/ficha.schema.json`.
3. El vocabulario tiene que pasar `tools/check_level.py` sin ítems por debajo del nivel declarado.
4. En alemán, cada ítem léxico lleva **caso o rección**. Sin excepciones.
5. La prosa de la ficha va en el idioma que se aprende, salvo en A2 y B1, que son bilingües.
6. Los campos de traducción a idiomas que no hablás se dejan **vacíos, no inventados**.

## Contenido regional

Lo que sea propio de una variedad (helvetismos, español rioplatense, inglés americano) se marca con el campo `variante` y tiene que poder desactivarse. No se mezcla con el estándar sin etiquetar.

## Revisión de nivel

Si sos hablante nativo o docente y querés revisar asignaciones de nivel, eso es lo que más falta. Mirá `PENDIENTES.md`, sección C: los skills de fluidez y léxico no tienen ninguna fuente que los cubra y dependen enteramente de juicio humano.

## Código

- Sin dependencias de red en la app. **El manifiesto no lleva el permiso `INTERNET`**, y cualquier PR que lo agregue se rechaza.
- Sin analítica, sin informes de fallos automáticos, sin Play Services.
- Compilación reproducible.

## Qué no se acepta

- Publicidad, compras dentro de la app, telemetría, cuentas de usuario.
- Cualquier lectura del dispositivo que no sea la fecha.
- Contenido de nivel que no puedas justificar con una fuente o con tu propia competencia declarada.
