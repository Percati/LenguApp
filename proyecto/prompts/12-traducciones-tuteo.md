# Prompt para Traducciones — Español neutro (tuteo) en todo el contenido

Leé `proyecto/CLAUDE.md` (regla 14), `proyecto/REGLAS-PREVENCION.md` y `proyecto/FALTANTES.md` 6.1. Clon fresco de `origin/main`.

## Problema
El español del contenido mezcla voseo rioplatense («¿Y vos?», «Tenés razón», «¿Por qué pensás eso?», «Probá primero con…») con tuteo (UI, planilla, el resto). La app se publica para cualquier hispanohablante, así que el español debe ser neutro. Estimación: unas 85 cadenas en unos 62 archivos (sobre todo traducciones de Redemittel: `redemittel[].traducciones.es`), más autochequeos y alguna misión.

## Qué hacer
En todo valor bajo una clave `es` de `contenido/nucleos`, `contenido/packs` y `contenido/ocurrencias` (incluidas las semanas de repaso y Survival):
- Pasar el voseo a tuteo: «vos» → «tú» (o «ti/contigo» según el caso), «tenés» → «tienes», «podés» → «puedes», «pensás/opinás/querés/sabés» → «piensas/opinas/quieres/sabes», imperativos «elegí/probá/mirá/decí/revisá/anotá» → «elige/prueba/mira/di/revisa/anota», «sos» → «eres», «vení/salí/poné/tené» → «ven/sal/pon/ten», etc.
- Neutro de verdad: sin regionalismos de España ni de América («vale», «ahorita», «chévere»…). Si una expresión coloquial solo funciona en un país, usá la formulación más neutral que cumpla la misma función comunicativa.
- Cuidado con las traducciones de Redemittel: tienen que seguir siendo equivalentes funcionales del original (por ejemplo «You're right» → «Tienes razón»).
- El detector `R6` de `verificar_consistencia.py` cubre una lista fija de formas. **No te limites a ella**: revisá a mano también las formas en -ás/-és/-ís (opinás, creés, sentís, preferís…) que la lista no ve.

## Restricciones
- Cambiá **solo** el voseo. Nada más en esa cadena (ni redacción, ni orden, ni puntuación), y ninguna otra clave de idioma (en/de/fr/it/pt) ni ningún otro campo.
- Conservá `*x*`, `**x**` y los « » tal como están.
- Sin `ß` en alemán, sin tocar `assets/`.
- Excepción documentada: ninguna. Un «vos» o un «tenés» en una cita textual de otro idioma no existe aquí (esas citas están fuera de la clave `es`).

## Verificación obligatoria
1. `python3 proyecto/tools/componer.py --contenido proyecto/contenido --salida <tmp> --schema proyecto/schema/ficha.schema.json` → «606 fichas compuestas, 56 semanas especiales, 0 con problemas».
2. `python3 proyecto/tools/verificar_consistencia.py --build <tmp>` → **0 problemas** (R6 pasa de unos 236 a 0 sobre las fichas compuestas; ninguna otra regla debe saltar).
3. Script sobre todo el diff: para cada archivo, comparar contra `HEAD` y confirmar que las únicas cadenas distintas cuelgan de una clave `es`, y que cada cambio es plausiblemente solo de voseo a tuteo (mostrá 15 ejemplos antes/después en el informe).

## Entrega
Parche `git format-patch` desde `origin/main`, probado con `git am` en clon fresco; informe corto con cantidades y los casos en que dudaste. No pushees ni toques `assets/` (los regenera Overview).
