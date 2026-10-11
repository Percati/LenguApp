# Prompt para Traducciones — el tuteo que R9 nunca miró (cadenas planas)

Leé `CLAUDE.md`, `proyecto/REGLAS-PREVENCION.md` (**P9**), `proyecto/FALTANTES.md` **6.1**, **27** y **43**, y tu informe del prompt 19. Trabajás sobre `proyecto/contenido/` y `proyecto/tools/verificar_consistencia.py` (nunca `build/` ni `app/src/main/assets/`). No pushees. Partí del `origin/main` actual.

## Qué pasó
El prompt 19 te pidió marcar el trato formal **«bajo las claves `de`, `fr`, `it`»**. Esa frase la escribí yo y dejó afuera, sin que nadie lo notara, **todas las cadenas planas**: los campos de instrucción de B2, C1 y C2 no son bilingües, así que son `string`, no `{idioma: texto}`, y R9 nunca los miró. Lo mismo pasa con los campos de A2 y B1 que todavía no se tradujeron y siguen como `string`.

**Medición de Overview sobre el contenido actual: ~699 cadenas planas en alemán con trato formal**, repartidas así por campo y nivel:

| campo | A2 | B1 | B2 | C1 | C2 |
|---|---|---|---|---|---|
| consigna | — | 12 | 65 | 65 | 65 |
| microtareas | 60 | 45 | 54 | 47 | 18 |
| requisitos | 18 | 12 | 6 | 16 | 18 |
| promptCorreccion | — | 6 | 39 | 41 | 27 |
| autochequeo | — | 18 | 2 | — | — |
| notas | 12 | 18 | 5 | 2 | 1 |
| descripcion / subtitulo | — | 18 | 4 | 3 | 2 |

Ejemplos reales: «Nehmen Sie Stellung zu der Frage…», «Moderieren Sie ein Gespräch…», «Wie sicher sind Sie wirklich?», «eine Bedingung, unter der Sie Ihre Position ändern würden».

**Por qué ahora es urgente:** la Ronda K puso la consigna como **encabezado en negrita del desafío del fin de semana**. Un usuario de alemán C2 abre la app un sábado y lo primero que lee, en el texto más grande de la pantalla, es «Nehmen Sie Stellung…». P9 dice que la app le habla de tú en los seis idiomas; hoy no lo cumple.

## Tarea 1 — Ampliar R9 (detector primero)
- R9 pasa a mirar los campos de instrucción (`mision.consigna`, `mision.requisitos`, `microtareas`, `autochequeo`, `promptCorreccion`, `notas`, `descripcion`, `subtitulo`) **tanto cuando son objeto `{idioma: texto}` como cuando son `string` plano**. Para la cadena plana, el idioma es el de la ficha.
- No cambia nada más de R9: siguen fuera `redemittel`, `ejemplos`, `cuadroReferencia`, `contraste`, `erroresContrastivos` y `vocabulario`, y no se toca lo que va entre « » ni entre `*…*`.
- **Cuidado con los falsos positivos**, que acá son más que en el prompt 19: en alemán, `Sie` al principio de oración puede ser «ella» o «ellos», y `sie` en minúscula nunca es formal. Marcá solo lo que de verdad es trato formal, y poné cada excepción en `EXENTAS_R9` con su motivo.
- **Dos exenciones legítimas que ya conozco y tienen que seguir pasando** (son contenido que se enseña, el contraste Sie/du): en `de-B1-2027.json`, ficha `DE-F13`, «Schreib zwei kurze Nachrichten: eine an deine Nachbarin (Sie) und eine an einen Freund (du)» y «Eine Kollegin ist überfordert. Schreib ihr eine Nachricht (du) und eine E-Mail an den Chef».
- Demostrá sobre el contenido real que marca el 100 % de lo que vas a corregir y **0 falsos positivos** sobre el resto. Revisá también `fr` e `it` por si hay cadenas planas ahí (creo que no, pero comprobalo y decilo).

## Tarea 2 — Corregir
- Pasá a **du / dein / dir** todo lo que R9 marque, reescribiendo la oración entera cuando haga falta (persona del verbo, pronombres, posesivos, reflexivos): «Nehmen Sie Stellung zu…» → «Nimm Stellung zu…», «Wie sicher sind Sie wirklich?» → «Wie sicher bist du wirklich?».
- Conservá el sentido, el nivel y el largo; los topes de campo siguen vigentes y el `promptCorreccion` alemán tiene un mínimo de 150 caracteres.
- Ortografía suiza (`ss`, nunca `ß`): R10 sigue en 0.
- No toques nada fuera de estos campos. No traduzcas: solo cambiás el trato.

## Verificación obligatoria
1. `componer.py --contenido proyecto/contenido --salida <dir> --schema proyecto/schema/ficha.schema.json` → 606 fichas, 56 semanas especiales, 0 con problemas. `verificar_consistencia.py --build <dir>`: **R1–R11 en 0**.
2. Autotest de R9 con casos negativos nuevos: `Sie` a principio de oración como «ella/ellos», `sie` minúscula, y las dos exenciones de `DE-F13`.
3. Script sobre el diff: toda cadena cambiada cuelga de uno de esos campos; estructura JSON idéntica; mismos « » y `*`; ningún tope superado.
4. Barrido a ojo: 40 cadenas al azar, con el antes y el después.

## Informe
Conteo real por campo y nivel (comparalo con mi tabla y decime dónde difiere y por qué), excepciones con su motivo, 15 antes/después, y los casos de duda. Actualizá `FALTANTES.md` (sección 43) y `REGLAS-PREVENCION.md` (P9: que diga explícitamente que alcanza a las cadenas planas, no solo a las claves de idioma).
