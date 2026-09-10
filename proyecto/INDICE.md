# Índice del proyecto — App de retos lingüísticos

Estado al 9 de septiembre de 2026. Todo lo que existe, qué es cada cosa y qué hace falta para construir la app.

---

## 1. Documentos de contenido

| Archivo | Qué es | ¿Necesario para la app? |
|---|---|---|
| `syllabus-app-idiomas.md` | **El documento central.** Plan de estudios completo A2–C2: método y fuentes, arquitectura de contenido, tipos de reto por nivel, 46 skills de inglés, 41 de alemán, 14 topics con escalera de profundidad, 28 packs de vocabulario, licencias y deudas | **Sí** — de aquí sale todo el contenido |
| `syllabus-app-idiomas.pdf` | El mismo, en PDF, para pasarle a un revisor nativo | No, pero es el que hace el proyecto auditable |
| `fichas/EN-C1-2026-S37-S40.md` + `S41-S53.md` | **17 fichas de inglés C1**: semanas 37 a 53, año 2026 completo desde el arranque | **Sí** |
| `fichas/DE-B2-2026-S37-S40.md` + `S41-S53.md` | **17 fichas de alemán B2**: semanas 37 a 53 | **Sí** |
| `calendario-2026-S37-S53.md` | Reglas del generador de calendario + el calendario de las semanas 37 a 53 | **Sí** — la parte A es la especificación a codear |

## 2. Estructura de datos

| Archivo | Qué es | ¿Necesario? |
|---|---|---|
| `schema/ficha.schema.json` | **La planilla en blanco.** Define qué campos tiene una ficha y cuáles son obligatorios. Valida las 400 fichas futuras en segundos | **Sí, imprescindible** |
| `schema/semana-especial.schema.json` | Schema aparte para las semanas de repaso y Survival, que no tienen skill ni topic | **Sí** |
| `build/*.json` | **Las 34 fichas compiladas y validadas**: 28 de contenido + 4 semanas de repaso + 2 Survival | **Sí** — datos listos para la app |


## 3. Herramientas de control de calidad

| Archivo | Qué hace | ¿Necesario? |
|---|---|---|
| `tools/build_wordlists.py` | Fusiona listas oficiales de vocabulario en una base por nivel. Marca los lemas donde las fuentes discrepan | No para la app; sí para el contenido |
| `tools/check_level.py` | Revisa el vocabulario de una ficha y marca lo que está por debajo del nivel declarado | No para la app; sí para el contenido |
| `tools/generar_calendario.py` | **Deriva el calendario de un año a partir de las reglas.** Reproducible: misma semilla = mismo calendario. Verifica sus propias reglas duras | **Sí** |
| `tools/compilar_fichas.py` | **Convierte las fichas en Markdown al JSON de la app** y las valida contra el schema. El puente entre el contenido legible y el dato | **Sí, imprescindible** |
| `tools/generar_audio.py` | Genera los MP3 con Piper en tiempo de compilación, dos velocidades por frase. `--dry-run` estima peso sin generar | Sí, cuando haya audio |
| `tools/fuentes.json` | URLs de las listas alemanas (Goethe, DTZ) | Sí, para reconstruir |
| `tools/fuentes_en.json` | URLs de las fuentes inglesas (Oxford, CEFR-J, Octanove) | Sí, para reconstruir |

## 4. Datos construidos

| Archivo | Contenido | ¿Necesario? |
|---|---|---|
| `data/vocab_de.json` | 3.243 lemas alemanes A1–B1, de 4 fuentes | Solo para verificar contenido |
| `data/vocab_en.json` | 9.444 lemas ingleses A1–C2, de 7 fuentes, con variante BrE/AmE y 1.597 etiquetas dobles | Solo para verificar contenido |
| `data/egp.json` | Las 1.222 estructuras del English Grammar Profile | Solo para auditar |
| `data/banco.json` | Skills y topics en formato máquina, entrada del generador | **Sí** |
| `data/semanas-fijas.json` | Semanas fijadas a mano del piloto 2026. Hace que el generador reproduzca exactamente el calendario contra el que se escribieron las fichas | **Sí** |

**Ninguno de estos tres se distribuye en el repositorio público**: derivan de material con copyright. Se regeneran con los scripts y las URLs.

## 5. Documentos de proceso

| Archivo | Qué es |
|---|---|
| `PROYECTO.md` | Hoja de ruta y decisiones cerradas: arquitectura, alcance, licencias, voces TTS |
| `PENDIENTES.md` | Todo lo abierto, agrupado por lo que bloquea |
| `AUDITORIA-EGP.md` | Auditoría de los skills de gramática inglesa contra el EGP: 4 correcciones y 1 skill eliminado |
| `AUDITORIA-LECTURA.md` | Contraste contra 354 textos de lectura graduada: 20 tríos paralelos ingleses confirman 6 asignaciones y corrigen 2. El corpus alemán no sirvió y ahí se explica por qué |
| `AUDITORIA-ALEMAN.md` | Cruce de los skills alemanes contra Aspekte neu B1+/B2/C1 y Netzwerk neu A2/B1: 6 correcciones y 6 skills nuevos |
| `FUENTES-INGLES.md` | Qué fuente inglesa existe, con qué licencia y en qué formato |
| `INDICE.md` | Este documento |
| `legal/CONTRIBUTING.md` | Reglas para contribuir: licencias, originalidad obligatoria, qué no se acepta |
| `legal/PRIVACY.md` | Política de privacidad, con las cuatro formas de verificarla |
| `legal/LEEME-LICENCIAS.md` | Los dos comandos para descargar GPL-3.0 y CC BY-SA 4.0, y por qué no los escribí yo |

---

## 6. Lo mínimo para construir la app

Si mañana empezás en Claude Code, con **cinco archivos** alcanza:

1. `schema/ficha.schema.json` — la forma del dato
2. `build/*.json` — las 8 fichas ya compiladas: se puede arrancar leyendo esto
3. `tools/generar_calendario.py` + `data/banco.json` + `data/semanas-fijas.json` — el calendario resuelto y reproducible
4. `tools/compilar_fichas.py` + `fichas/*.md` — el contenido y su compilador
5. `PROYECTO.md` — las decisiones que no se rediscuten
6. `legal/` — para el primer commit público

Todo lo demás es el taller donde se fabrica el contenido, no la app.

---

## 7. Qué falta, en orden

| # | Falta | Tamaño |
|---|---|---|
| 1 | **Usar el contenido y dar feedback** | 17 semanas disponibles. Ya no bloquea el resto |
| 2 | ~~Fichas semanas 41–53~~ | **Hechas.** 2026 completo |
| 3 | ~~Auditoría de los skills alemanes~~ | **Hecha.** Queda B1 y los skills de fluidez: plazo indefinido |
| 4 | Fichas del año 2027 | ~88 fichas |
| 5 | Packs de vocabulario de los otros niveles | 112 de 140 |
| 6 | ~~Lista B1 Preliminary~~ | **Hecha.** Los phrasal verbs de B1 ya se verifican |
| 7 | ~~Cruce de cobertura alemán~~ | **Hecho** |
| 8 | Traducciones a FR/IT/PT | Campos vacíos a propósito |

---

## 8. Reglas de trabajo aprendidas

- **Subir siempre CSV, nunca XLSX.** El conversor de conocimiento de proyecto trunca los `.xlsx` en 1.000 filas por hoja y casi no avisa. Costó una auditoría rehecha.
- **Ninguna lista con copyright entra al repositorio.** Se distribuye el script y la URL, nunca los datos.
- **El verificador automático no es un veredicto.** La auditoría del EGP dio "0 problemas" y había 4 errores; salieron de consultas dirigidas.
- **Un verificador que compara lemas no ve qué se enseña sobre ellos.** *Die Überweisung* es B1 como palabra, pero su doble sentido (médico y bancario) es B2. Se resolvió con una marca (†) que declara el desvío en lugar de sacrificar el contenido.
- **Los campos vacíos son mejores que los campos ausentes.** Un hueco visible se llena; uno invisible se olvida.
- **La circularidad de una fuente no siempre es un defecto.** Aspekte neu deriva de Profile deutsch, y por eso mismo sirvió: transmite el criterio de la autoridad a la que no teníamos acceso.
- **Los errores se concentran arriba.** A2 y B1 son territorio estandarizado; C1 y C2 son donde cada autor improvisa. Verificar de arriba hacia abajo rinde mucho más.
- **Dos fuentes de verdad para lo mismo divergen sin avisar.** El calendario del piloto estaba escrito a mano y el generador producía otro distinto. Se arregló fijando el piloto en `data/semanas-fijas.json`, no eligiendo cuál de los dos tenía razón.
- **Saber cuándo parar, pero verificar que sea el momento.** Cerré el cruce alemán por rendimiento decreciente y me apuré: faltaba el índice de Aspekte B1 plus, que después reveló cinco huecos. El rendimiento no caía, me faltaba la fuente.
- **Los huecos de cobertura y los errores de nivel son problemas distintos.** Los errores de nivel se concentran en C1. Los huecos aparecen en todos los niveles, porque nacen de armar el banco desde lo que uno cree importante en vez de desde un temario completo.
