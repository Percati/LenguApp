# Licencias de terceros

## Tipografías (embebidas en `app/src/main/res/font/`)

| Archivo | Familia | Autores / origen | Licencia |
|---|---|---|---|
| `literata.ttf`, `literata_italic.ttf` | Literata | TypeTogether / Google Fonts — https://github.com/googlefonts/literata | SIL Open Font License 1.1 — `OFL-Literata.txt` |
| `atkinson.ttf`, `atkinson_italic.ttf` | Atkinson Hyperlegible Next | Braille Institute / Applied Design Works, repositorio de Google Fonts — https://github.com/googlefonts/atkinson-hyperlegible-next | SIL Open Font License 1.1 — `OFL-AtkinsonHyperlegibleNext.txt` |

La OFL 1.1 permite usar, estudiar, modificar y redistribuir las fuentes (también dentro de una app
GPL-3.0 con contenido CC BY-SA 4.0), siempre que se conserve el aviso de copyright y la licencia, y
que las versiones modificadas no usen el nombre reservado. Las fuentes están **modificadas**
(subconjunto de caracteres y ejes variables recortados) solo para reducir el tamaño; el aviso de
copyright y la licencia siguen en la tabla `name` de cada archivo y en estos textos.

**Cómo se generaron los archivos de `res/font/`** (reproducible):
1. Originales variables de los repositorios oficiales (`fonts/variable/*.ttf`).
2. `fonttools varLib.instancer`: Literata con `opsz=14 wght=400:700`; Atkinson con `wght=400:700`.
3. `fonttools subset`: Latin + Latin Extended-A/B (U+0020–024F), diacríticos combinantes (U+0300–036F),
   puntuación general (U+2010–2027, « » ‹ ›), €, ™ y las pocas flechas/signos que usa el contenido;
   sin hinting; tablas de capas `kern, liga, calt, locl, mark, mkmk, ccmp`.
