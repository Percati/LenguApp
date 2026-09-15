# Conversación: NÚCLEOS

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de NÚCLEOS del proyecto LenguApp.

Al empezar, siempre:
1. Cloná o actualizá el repo: `git clone [URL] .` (primera vez) o `git pull` (si ya existe localmente).
2. Leé `CLAUDE.md`, `syllabus-app-idiomas.md` y la memoria de este proyecto de Claude
   (archivos `reglas-fichas.md` y `app-decisiones-tecnicas.md`, si el sistema de
   memoria de Claude los muestra en el listado).
3. Leé `contenido/nucleos/` para ver qué combinaciones idioma+skill+nivel ya existen,
   comparando contra `data/banco.json`.

Tu trabajo: escribir `contenido/nucleos/{SKILL}-{NIVEL}.json` para las combinaciones
que falten. Cada núcleo debe validar contra `schema/ficha.schema.json` (usá
jsonschema en Python para chequear antes de dar nada por cerrado — no a ojo).

Reglas de contenido que NO tenés que reinventar (ya están decididas):
- A2/B1: campo `bilingue: true`, prosa en el idioma que se aprende Y en español.
- B2/C1/C2: sin `bilingue`, prosa monolingüe; solo vocabulario, redemittel/expressions
  y contraste van bilingües.
- El campo `contraste` es un diccionario por idioma de app: escribí SOLO la clave
  `"es"`. Las claves `it`, `fr`, `pt`, `en` las agrega la conversación de Contrastes
  — no las toques si ya existen, no inventes contenido para esas claves.
- El campo `errores` es una lista simple pensada para hispanohablante por ahora. La
  conversación de Errores Típicos decidirá si conviene separarlo por idioma de app.
- Mínimos de schema a no romper: `redemittel` 6-12, `autochequeo` 5 exacto,
  `ejemplos` 3+, `notas` 2+, `errores` 2+, `descripcion` 200-1600 caracteres,
  `promptCorreccion` 150+ caracteres, `cuadroReferencia.filas` 3+.

Antes de cerrar cualquier tanda: revalidá TODOS los núcleos que tocaste contra el
schema real (no solo los últimos), igual que revalidarías packs — un fallo
descubierto tarde es más caro que revalidar de más.

Al final de la sesión (o cada vez que completes una combinación):
```
git add -A
git commit -m "Núcleos: agrega XX-YY (N núcleos)"
git push
```

Reportame siempre "Números: X/Y núcleos" al cierre de cada tanda de trabajo, y
avisame de cualquier riesgo o inconsistencia que encuentres antes de seguir.
