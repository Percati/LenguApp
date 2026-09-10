# App de retos lingüísticos — Hoja de ruta y decisiones

Documento vivo. Registra lo **decidido**, para que no se vuelva a discutir, y lo **pendiente**, para que no se olvide.
Última actualización: septiembre 2026 (semana ISO 37).

---

## 1. Qué es

Una app Android, libre y sin conexión, que cada semana muestra un tema gramatical o comunicativo (**Skill**) y un tema de conversación (**Topic**), con vocabulario, misión semanal, tres micro-tareas y un prompt copiable para que un asistente de IA corrija lo producido. Repetición espaciada: cada Skill reaparece 2–3 veces al año con enfoque distinto.

**No es** un curso. Es un sistema de práctica deliberada para quien ya estudió la teoría y necesita forzarse a usarla.

---

## 2. Decisiones cerradas

### Arquitectura
| Decisión | Valor |
|---|---|
| Offline real | Sin permiso `INTERNET` en el manifiesto |
| Único dato leído del dispositivo | La fecha |
| Indexación temporal | Semana ISO 8601, por (añoISO, semanaISO) |
| Modo | **Revista**: manda la fecha, la semana perdida se perdió. Sin puntero de progreso |
| Calendario | **Derivado por reglas**, no escrito a mano. Función determinista con semilla = año |
| Fuente del contenido | Markdown legible → compila a JSON (app) y a PDF (syllabus auditable) |
| Estructura de archivos | Por `(skill, nivel, order)`. Ningún archivo se llama "semana 12" |

### Alcance
| Decisión | Valor |
|---|---|
| Idiomas de aprendizaje | EN, DE ahora; ES, FR, IT, PT después |
| Niveles | A2–C2, con tipo de reto distinto por nivel |
| Prosa de la ficha | Bilingüe en A2 y B1; monolingüe de B2 para arriba |
| Idioma de interfaz | Separado del idioma base del contenido. Campos distintos en el schema |
| Traducciones | 5 por ítem (nunca al idioma que se aprende). En 2026 solo `es` |
| Topics | 14, alineados con el catálogo temático MCER que publica el Goethe |

### Licencias
| Parte | Licencia | Motivo |
|---|---|---|
| Código | **GPL-3.0-only** | Copyleft fuerte: mata el fork cerrado con anuncios |
| Contenido | **CC BY-SA 4.0** | Estándar de contenido educativo abierto; obliga a que las traducciones vuelvan |
| Listas del Goethe | **No se redistribuyen** | Propiedad de Goethe/Hueber. El repo trae el script, no los datos |

Prueba de ausencia de rastreadores: publicación en F-Droid sin Anti-Features + ausencia del permiso `INTERNET` + informe de Exodus Privacy.

### Audio (TTS)
Generado **localmente con Piper** antes de compilar y empaquetado en el APK. El teléfono nunca descarga ni genera audio. Dos velocidades por frase: normal y 80 % (shadowing).

| Idioma | Voz Piper | Nota |
|---|---|---|
| Inglés | `en_US-ryan-high` | seleccionable |
| Inglés | `en_US-lessac-high` | seleccionable |
| Español (LatAm) | `es_AR-daniela-high` | rioplatense |
| Alemán | `de_DE-thorsten-high` | |
| Italiano | `it_IT-serena-high` | |
| Portugués | `pt_BR-faber-medium` | |
| Francés | `fr_FR-upmc-medium` | |

Inglés es el único idioma con dos voces y el usuario elige. Peso estimado: ~25 KB por clip, ~50 MB con dos velocidades para el catálogo completo.

---

## 3. Anclaje del nivel: qué existe y qué no

- **Inglés**: English Grammar Profile (Cambridge, corpus de más de 55 millones de palabras, ~1.200 descriptores) y English Vocabulary Profile. Alternativa gratuita para vocabulario: Oxford 3000/5000, mapeado al MCER.
- **Alemán**: Profile deutsch (Langenscheidt) describe la gramática **solo hasta B2**; asume que en C1/C2 el trabajo es de uso activo, no de estructuras nuevas.
- **El Goethe no publica lista de vocabulario B2, C1 ni C2.** El manual del C1 dice explícitamente que no existen inventarios de vocabulario ni de gramática para ese nivel, porque se usan textos auténticos y se espera que el aprendiente deduzca por formación de palabras. Consecuencia de diseño: el banco alemán C2 no lleva gramática, y `DE-V06 Wortbildung` es prioritario.
- **Listas gratuitas que sí existen**: Goethe A1 (Fit 1), A2, B1, y la lista del DTZ (nivel A2–B1). Son la base del verificador.

⚠️ **Advertencia**: el archivo que circula como "Goethe B2 Wortliste" es en realidad la lista del DTZ renombrada (mismo MD5). No es B2.

---

## 4. Herramientas del taller

| Herramienta | Qué hace |
|---|---|
| `schema/ficha.schema.json` | Define los campos obligatorios de una ficha. Valida las 400 en segundos |
| `tools/build_wordlists.py` | Fusiona todas las listas oficiales en una base por nivel. Marca los lemas en los que las fuentes de un mismo nivel no coinciden |
| `tools/check_level.py` | Revisa el vocabulario de una ficha y marca lo que está por debajo del nivel declarado |

Las listas fuente van en `tools/fuentes/` y **no se versionan**. `tools/fuentes.json` trae las URL para descargarlas.

**Cuantas más versiones y años se agreguen a `fuentes.json`, mejor**: una palabra que estaba en la edición 2009 y no en la de 2016 queda registrada con las dos, y el verificador la marca como nivel disputado en vez de perderla.

---

## 5. Estado

**Hecho**: syllabus completo A2–C2 (88 skills, 14 topics), calendario 37–53 y sus reglas, 8 fichas del mes piloto (semanas 37–40, EN y DE), schema, verificador, base de vocabulario alemán (3.243 lemas de 4 fuentes).

**Siguiente**: usar el mes piloto → si el molde aguanta, extender a las semanas 41–53 y al año 2027 completo; en paralelo, empezar la app en Claude Code.

**Deudas abiertas**
| # | Deuda | Cómo se cierra |
|---|---|---|
| 1 | Nadie nativo revisó el banco de inglés | Conseguir revisor, o publicar con nota de "no revisado" |
| 2 | Asignaciones alemán C1/C2 sin fuente | Comprar Profile deutsch (~50 €) o revisor que lo tenga |
| 3 | 96 de 140 packs de vocabulario sin escribir | Por nivel, cuando haya quién valide |
| 4 | Helvetismos sin ancla MCER | Etiquetar como variante y hacerlo desactivable |
| 5 | Traducciones solo en español | Cuando haya hablantes de FR/IT/PT |
| 6 | Verificador solo para alemán | Construir el equivalente inglés con Oxford 3000/5000 |
| 7 | Fichas A2 y B1 (bilingües) sin escribir | Con las listas A1/A2/B1 ya disponibles |
