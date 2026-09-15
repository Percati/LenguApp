# Conversación: PACKS

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de PACKS del proyecto LenguApp.

Al empezar, siempre:
1. Cloná o actualizá el repo: `git clone [URL] .` (primera vez) o `git pull`.
2. Leé `CLAUDE.md` y `proyecto/CLAUDE.md` y la memoria de este proyecto de Claude (`reglas-fichas.md`,
   `app-decisiones-tecnicas.md`).
3. Mirá `proyecto/data/calendarios/*.json` para saber qué semanas y temas hay que cubrir, y
   `proyecto/contenido/packs/` para ver qué combinaciones idioma-nivel-año-tema-n ya existen.

Tu trabajo: generar `proyecto/contenido/packs/{idioma}-{nivel}-{anio}-{topicId}-{n}.json`
(vocabulario por tema y aparición) para los calendarios que no tengan packs
completos. Cada pack: 10-18 ítems, con `prioridad: "nucleo"` o `"variante"` marcada
en cada uno, y `traducciones` a los otros idiomas de app relevantes.

Regla crítica de continuidad (la rompí varias veces mientras trabajaba en esto, no
la repitas): entre packs consecutivos del MISMO tema (n y n+1), mantené 30-80% de
ítems compartidos. Escribí SIEMPRE tomando el pack anterior real como base —nunca
vocabulario nuevo desde cero— y después de escribir una cadena completa de un tema,
revalidá TODAS las transiciones de esa cadena con un script, no solo la última que
tocaste. Si corregís una transición y eso cambia el pack, revisá si eso rompió la
transición siguiente (efecto dominó) y repetí hasta que las transiciones den
`0% correcciones pendientes`.

Al cerrar una combinación completa:
```
git add -A
git commit -m "Packs: agrega XX-YY-AAAA (N packs)"
git push
```

Reportame siempre "Números: X/Y packs" al cierre de cada tanda.
