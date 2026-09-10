# Verificador de inglés — fuentes, licencias y estado

Estado: **construido y probado**. `data/vocab_en.json` contiene **9.444 lemas** de A1 a C2, de 7 fuentes.

> **Nota sobre las subidas**: el conversor de conocimiento de proyecto **trunca los `.xlsx` en 1.000 filas por hoja, sin avisar salvo por una línea al final**. Subir siempre `.csv`. Costó una auditoría rehecha.

---

## 1. Fuentes usadas

### 1.1 Oxford 3000 / 5000 — PDF, cuatro archivos

Gratis, con nivel MCER junto a cada palabra. El 3000 cubre A1–B2; el 5000 agrega 2.000 palabras de B2–C1. Corpus de más de 2.000 millones de palabras que cubre inglés británico, americano y mundial.

Base: `https://www.oxfordlearnersdictionaries.com/external/pdf/wordlists/oxford-3000-5000/`

| Archivo | Variante | Lemas extraídos |
|---|---|---|
| `The_Oxford_3000.pdf` | BrE | 2.996 de 3.000 |
| `American_Oxford_3000.pdf` | AmE | 2.995 de 3.000 |
| `The_Oxford_5000.pdf` | BrE | 1.999 de 2.000 |
| `American_Oxford_5000.pdf` | AmE | 1.999 de 2.000 |

**Se usan los alfabéticos, no los `_by_CEFR_level`.** Los alfabéticos traen el nivel en línea y por acepción (*alien n. B2, adj. C1*), lo que es estrictamente más información. Los agrupados por nivel pierden esa granularidad y son más frágiles de parsear por su maquetación en columnas.

Limitación: palabras sueltas. Sin phrasal verbs, sin colocaciones, sin modismos.

### 1.2 CEFR-J — CSV, desde GitHub

Proyecto de la Universidad de Estudios Extranjeros de Tokio (Tono Lab).

| Recurso | Entradas | Aporta |
|---|---|---|
| CEFR-J Vocabulary Profile 1.5 | 6.863 | A1–B2, subniveles colapsados a MCER |
| CEFR-J Vocabulary Profile **1.6** | 6.864 | Integrada. **Diferencia real con la 1.5: cero cambios de nivel, 18 palabras añadidas, 17 quitadas.** La actualización fue cosmética |
| Octanove Vocabulary Profile C1/C2 1.0 | 1.955 | **C1 y C2**, donde el Oxford 5000 se corta |
| CEFR-J Grammar Profile | 499 estructuras | Nivel por estructura **con referencia cruzada al EGP** |

Espejo: `https://github.com/openlanguageprofiles/olp-en-cefrj`

**Licencia**: los datasets de vocabulario y gramática de CEFR-J se pueden usar sin cargo para fines de investigación y comerciales, siempre que se cite correctamente; el copyright es de Tono Laboratory (TUFS). El Octanove Vocabulary Profile para C1/C2 está bajo Creative Commons Attribution-ShareAlike 4.0 — la misma licencia que el contenido de este proyecto.

**Corrección respecto del análisis previo**: la **CEFR-J Phrase List** existe, pero **no está en el espejo de GitHub**. Hay que bajarla de `http://www.cefr-j.org/download_eng` aceptando la licencia. Queda pendiente; es lo que mejoraría la cobertura de expresiones multipalabra.

### 1.3 EVP y EGP (Cambridge) — solo consulta

English Vocabulary Profile (~7.000 lemas) y English Grammar Profile (más de 1.200 estructuras), construidos sobre el Cambridge Learner Corpus. Gratuitos y buscables online con registro, **sin descarga masiva**. Se usan como referencia manual, citada; no se scrapean ni se empaquetan.

`https://www.englishprofile.org/`

El Grammar Profile de CEFR-J cubre parte de esta necesidad con su columna EGP.

---

## 2. Resultado

9.427 lemas, A1–C2. **1.597 con etiqueta doble** (`B1/B2`, `B2/C1`, …): son los lemas en los que dos fuentes que cubren el mismo nivel no coinciden, exactamente el fenómeno que se quería capturar. Comparación con el alemán, donde solo hay dos ediciones del mismo organismo y el desacuerdo es mucho menor:

| | Lemas | Etiqueta doble |
|---|---|---|
| Alemán | 3.243 | 0 |
| Inglés | 9.427 | 1.597 |

La diferencia no es un defecto: en inglés hay cuatro proyectos independientes (Oxford, CEFR-J, Octanove) frente a dos ediciones del Goethe.

Cada lema guarda además la **variante** (BrE/AmE) de las fuentes en que aparece, y el verificador avisa cuando algo está atestiguado en una sola.

---

## 3. Lo que hay que aceptar

- Las expresiones multipalabra (*to sign off on*, *a stretch assignment*) siguen sin verificarse: se marcan como "expresión no atestiguada", nunca como correctas. La Phrase List de CEFR-J mejoraría esto.
- La verificación de nivel de los 47 skills de inglés contra el EGP es tarea manual, pendiente.

---

## 4. Cita obligatoria

> Tono, Y. *CEFR-J Wordlist Version 1.5/1.6*. Tokyo University of Foreign Studies. `http://www.cefr-j.org/download.html`
> *CEFR-J Grammar Profile*, versión 20180315. Tono Laboratory, TUFS.
> *Octanove Vocabulary Profile* C1/C2 v1.0, Octanove Labs, CC BY-SA 4.0.
> *The Oxford 3000 / The Oxford 5000*, © Oxford University Press.

Los datos fuente **no se versionan en el repositorio**: se descargan con las URL de `tools/fuentes.json` y `tools/fuentes_en.json`.
