# Puntos abiertos y cómo se cierran

Estado a septiembre 2026, semana ISO 37. Ordenados por lo que bloquea, no por antigüedad.

---

## A. Bloquean la app

> De los tres puntos de esta sección, dos se cerraron aquí. **Solo queda la app en sí, y eso es trabajo de Claude Code.**

| # | Pendiente | Cómo se cierra | Quién | Esfuerzo |
|---|---|---|---|---|
| A1 | La app no existe | Repo + app Kotlin/Compose. **El generador de calendario y el compilador MD→JSON ya están hechos y probados**: se portan o se llaman como paso de build | **Fer, en Claude Code** | 4–8 semanas de tardes |
| A2 | ~~Generador de calendario~~ | **CERRADO.** Reproducible, verifica sus propias reglas duras y acepta semanas fijadas a mano, con lo que reproduce exactamente el calendario del piloto 2026 | Hecho | — |
| A3 | ~~Compilador Markdown → JSON~~ | **CERRADO.** `tools/compilar_fichas.py`: las 8 fichas del piloto compilan y validan. Detectó de paso que las semanas de repaso no encajan en el schema de ficha, y ahora tienen el suyo | Hecho | — |

## B. Bloquean el contenido

| # | Pendiente | Cómo se cierra | Estado |
|---|---|---|---|
| B1 | Aplicar las 4 correcciones del EGP | Editar el syllabus: G07→`B1 B2`, G15→`B2 C1 C2`, G20→`B2 C2`, **G08 eliminado** (banco inglés: 47→46 skills) | Decidido; falta editar |
| B2 | Fichas de las semanas 41–53 (26 fichas) | Escribirlas con el molde v3, una vez validado el mes piloto | Espera al feedback |
| B3 | Año 2027 | El calendario lo produce el generador. Las fichas de 2026 se reutilizan en otras combinaciones; harán falta las apariciones `order 2` de cada skill | Diciembre 2026 |
| B4 | 96 de 140 packs de vocabulario sin escribir | Por nivel, a medida que haya quién valide | Progresivo |
| B5 | Fichas A2 y B1 (bilingües) | Ya hay fuentes: listas Goethe A1/A2/B1 y Cambridge B1 Preliminary | Se puede empezar |
| B6 | Topics T13 (Servicios) y T14 (Consumo) sin packs | Escribirlos; ya están justificados por el catálogo MCER | Pendiente |

## C. Anclaje y verificación

| # | Pendiente | Cómo se cierra | Realista? |
|---|---|---|---|
| C1 | Auditoría de los skills alemanes | **CERRADA.** Cruce contra Aspekte neu B2/C1 y Netzwerk neu A2: 5 correcciones, 3 skills nuevos, 0 errores en A2. Ver AUDITORIA-ALEMAN.md | Hecho |
| C2 | 18 skills de fluidez + 9 de léxico ingleses sin número de escala | Anclarlos al Companion Volume, que ya está subido | Una sesión |
| C3 | Nadie nativo revisó el banco de inglés | Conseguir revisor; si no aparece, publicar con nota de "no revisado" | Antes de publicar |
| C4 | Expresiones multipalabra en inglés | **PARCIALMENTE CERRADO.** Integrada la lista B1 Preliminary: ahora se verifican phrasal verbs (*break down*, *look after*, *carry on*). Lo de B2+ (*sign off on*) sigue sin fuente | Hecho hasta B1 |
| C5 | ~~Lista Goethe A1 Fit 1~~ | **CERRADO.** Subida y reintegrada: 293 lemas A1, base alemana en 3.243 lemas de 4 fuentes | Hecho |
| C7 | Tenemos CEFR-J Wordlist 1.5; existe la 1.6 | Bajarla de cefr-j.org. Al integrarla con la 1.5, los desacuerdos entre versiones se marcan solos | Fer baja, yo integro |
| C6 | ~~Cruce de cobertura alemán~~ | **CERRADO** por la vía de Aspekte, que reveló 7 huecos; 3 se incorporaron al banco | Hecho |
| C8 | ~~B1 alemán~~ | **CERRADO** con Aspekte neu B1 plus: 1 corrección y 5 huecos, 3 incorporados al banco | Hecho |
| C9 | 14 skills de fluidez y 9 de léxico alemanes sin ancla | Ninguna fuente los cubre: los índices de manuales dan gramática, y el corpus de lectura es receptivo | **Plazo indefinido. Solo revisor humano** |
| C10 | Vocabulario alemán B2+ | No existe lista oficial. El método de extraer de textos graduados funciona, pero el corpus disponible es turístico-regional y solo devuelve topónimos. Haría falta un corpus B2/C1 alemán de temas variados | Abierto |

## D. Publicación

| # | Pendiente | Cómo se cierra |
|---|---|---|
| D1 | Archivos legales | **CASI CERRADO.** `CONTRIBUTING.md` y `PRIVACY.md` escritos. Faltan los dos textos de licencia: **hay que descargarlos**, ver `legal/LEEME-LICENCIAS.md` (dos comandos `curl`) |
| D2 | ~~Helvetismos sin etiquetar~~ | **CERRADO.** Campo `variante` en el schema: si está presente, la app debe mostrarla etiquetada y permitir desactivarla |
| D3 | Publicación en F-Droid con cero Anti-Features | Sin permiso INTERNET, sin Play Services, build reproducible, informe de Exodus |
| D4 | Audio con Piper | **Script escrito** (`tools/generar_audio.py`), con `--dry-run` para estimar antes de generar. Falta que instales Piper y bajes las voces. 90 clips en el piloto, ~2 MB |

## C-bis. Interfaz pendiente

| # | Pendiente | Detalle | Estado |
|---|---|---|---|
| C11 | Traducir el *chrome* de la interfaz a los seis idiomas | Barra de navegación, mensajes de "sin contenido", etiquetas de ajustes. Hoy están en un solo idioma. Code lo señaló al cerrar la Fase 6 y **tuvo razón en no asumirlo**: traducir a seis idiomas sin revisor nativo repite el problema que ya tuvimos con el contenido | **Diferido a propósito.** Con un solo usuario no molesta |
| C12 | Transcribir la sección *Phrase* del IELTS 6.5 | Única fuente de expresiones multipalabra. Requiere versión con capa de texto | Abierto |

## D-bis. Contenido pendiente por el cambio de variantes

| Qué | Detalle |
|---|---|
| Rehacer la ficha de la semana 53 (`DE-V08 Helvetismen`) | Al eliminarse el ajuste de variantes, una ficha entera dedicada a los helvetismos deja de encajar. El contenido regional pasa a ser ítems adicionales de los demás packs, hasta 5 por pack |
| Decidir si `DE-V08` sigue en el banco | O se disuelve en ítems marcados con `variante` dentro de los otros skills |
| Equidad entre variedades | Hoy solo hay `CH`. El mecanismo por ítem ya soporta `AT`, `BrE`, `AmE`, `BR`: es trabajo de contenido, no de arquitectura |
| Fichas de ES, IT, FR y PT | Ver `BIBLIOGRAFIA-ES-IT-FR-PT.pdf`. Empezar por español: es el único con inventario oficial hasta C2 (PCIC) y está gratis en línea |

## E. Corregido tras la Fase 1

| Qué | Detalle |
|---|---|
| Codificación en Windows | Las cinco herramientas usaban `read_text()` / `write_text()` sin declarar codificación. En Linux funcionaba por el locale; en Windows rompía el `·` de las cabeceras. **Detectado por Claude Code en la Fase 1 y corregido en las cinco.** También se añadió reconfiguración de stdout y decodificación explícita de la salida de `pdftotext` |

## F. Decisiones que siguen abiertas

Ninguna. Las tres que quedaban se resolvieron:

1. ~~Mixed conditionals~~ → **eliminado** del banco.
2. **Traducciones a FR/IT/PT** → los campos quedan **presentes y vacíos** en el schema, no ausentes. Un campo vacío es un recordatorio visible de trabajo pendiente; un campo ausente se olvida. El validador acepta `minProperties: 1`, así que basta con `es`.
3. **Interfaz vs. idioma base** → **separados**. Son dos ajustes distintos en la app desde el día uno: `uiLanguage` (menús, botones) y `baseLanguage` (glosas y contraste dentro de la ficha). Un italiano aprendiendo alemán puede querer interfaz en italiano y glosas en inglés.

---

## Lo que ya está cerrado

- Syllabus A2–C2: 88 skills, 14 topics, con método y fuentes declaradas
- Calendario 2026 semanas 37–53 y las reglas del generador
- 8 fichas del mes piloto (semanas 37–40, EN y DE), molde v3
- Schema JSON validado, con prueba negativa
- Verificador de nivel: alemán (3.243 lemas) e inglés (9.427 lemas, 1.597 con etiqueta doble)
- Auditoría de los 20 skills de gramática inglesa contra el EGP
- Licencias decididas, voces TTS decididas
- **Cerrado por hallazgo**: no existen inventarios de vocabulario ni gramática para C1/C2, ni en alemán ni en inglés. Confirmado de forma independiente por el Goethe (manual C1) y por Cambridge (manuales B2/C1/C2 sin lista de vocabulario). Deja de ser una limitación nuestra y pasa a ser un dato de diseño
