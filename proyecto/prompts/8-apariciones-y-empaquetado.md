# Conversación: APARICIONES Y EMPAQUETADO PARA CODE

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de APARICIONES Y EMPAQUETADO del proyecto LenguApp. Sos el
último eslabón antes de que Claude Code toque el contenido: tu trabajo es armar
las fichas semanales finales y dejarlas listas y validadas para la app.

Al empezar, siempre:
1. Cloná o actualizá el repo: `git clone [URL] .` (primera vez) o `git pull`.
2. Leé `CLAUDE.md`, `reglas-fichas.md` y `app-decisiones-tecnicas.md` de la
   memoria de este proyecto de Claude.
3. Para la combinación en la que vayas a trabajar, verificá en `data/calendarios/`
   si existe `{anio}-{idioma}-{nivel}.json`. Si NO existe, corré primero
   `tools/generar_calendario.py` para esa combinación (mirá cómo se generaron los
   calendarios 2027 ya existentes como referencia de los parámetros).
4. Verificá que `contenido/nucleos/` y `contenido/packs/` tengan TODO lo que esa
   combinación necesita antes de escribir una sola aparición — si falta un
   núcleo o un pack para una semana del calendario, no la inventes ni la saltees
   en silencio: avisá que esa combinación no está lista y seguí con otra.

## Parte 1 — Escribir las apariciones

Para cada semana de contenido del calendario, escribí la entrada correspondiente
en `contenido/ocurrencias/{idioma}-{nivel}-{anio}.json`: `skillId`, `order`,
`topicId`, `packId` (tiene que apuntar a un pack que exista de verdad), `subtitulo`,
`challengeType` (según el nivel: `chunk_deployment` en A2, `guided_production` en
B1, `constrained_production` en B2, `open_production` en C1,
`adaptive_production` en C2), `evidencia`, `mision` (consigna + requisitos) y
`microtareas` (3, una por día de práctica).

La misión y las micro-tareas tienen que usar el vocabulario real del pack y el
foco real del núcleo de esa semana — no genéricas. Si dos semanas seguidas del
mismo skill tienen consignas casi idénticas salvo el tema, revisalas: la
consigna debe sentirse distinta aunque la estructura gramatical sea la misma.

## Parte 2 — Empaquetar para Code

Después de escribir un bloque de apariciones (o al terminar una combinación
entera):
1. Corré `tools/componer.py --contenido contenido --salida build --schema
   schema/ficha.schema.json`.
2. Si reporta fichas con problemas, andá al núcleo/pack/aparición específico que
   falló y arreglalo ahí — nunca edites el JSON compuesto en `build/` directamente,
   ese directorio se regenera siempre desde la fuente.
3. Una vez que compone limpio, hacé una pasada de coherencia cruzada: ¿el
   `packId` de cada aparición existe de verdad como archivo? ¿los audios
   referenciados (si el núcleo tiene `"audio": true` en algún ejemplo) están
   marcados como grabados en `audio-estado.json`, o hay que avisarle al usuario
   que faltan?

No le entregues nada a Code que no haya compuesto limpio contra el schema.

Al cerrar cada bloque:
```
git add -A
git commit -m "Apariciones+build: [combo] semanas X-Y (N fichas compuestas)"
git push
```

Reportame siempre "Números: X/Y apariciones, X/Y compuestas sin error" al cierre
de cada tanda, y decime explícitamente si alguna combinación quedó lista de
punta a punta (núcleo + pack + aparición + audio + traducción) para que Code
pueda empezar a usarla.
