# Brief para arrancar en Claude Code

## 1. Antes de empezar: el riesgo que hay que aceptar

El contenido del piloto **todavía no se usó**. Eso significa que el molde de la ficha —los campos, su granularidad— está diseñado pero no validado en uso real. Si tras cuatro semanas de práctica resulta que sobra el cuadro de referencia o falta un campo, cambia el schema, y con él el compilador y la capa de datos de la aplicación.

Dos formas de manejarlo:

- **Esperar cuatro semanas** a validar el molde y después construir. Más seguro, más lento.
- **Construir ya, con el lector genérico.** Que la pantalla renderice *los campos que existan* en el JSON en vez de tener una función por campo. Así un campo nuevo aparece solo y un campo eliminado no rompe nada.

Recomendación: la segunda. El costo de hacer el lector genérico es de horas; el de rehacer pantallas acopladas al schema, de días.

## 2. Archivos que hay que copiar al repositorio

| Ruta en el repo | De dónde | Para qué |
|---|---|---|
| `CLAUDE.md` | raíz | Claude Code lo lee solo. Las reglas que no se rediscuten |
| `schema/ficha.schema.json` | tal cual | Contrato de datos de las semanas de contenido |
| `schema/semana-especial.schema.json` | tal cual | Contrato de las semanas de repaso y Survival |
| `build/*.json` | tal cual | **8 fichas ya compiladas y validadas.** Datos reales para arrancar sin tocar el pipeline |
| `content/*.md` | `fichas/*.md` | Fuente del contenido |
| `tools/*.py` | tal cual | Compilador, generador de calendario, audio, control de calidad |
| `data/banco.json` | tal cual | Skills y topics en formato máquina |
| `data/semanas-fijas.json` | tal cual | Semanas curadas del piloto 2026 |
| `docs/syllabus.pdf` | `syllabus-app-idiomas.pdf` | Plan de estudios, para revisores |
| `legal/` | tal cual | `CONTRIBUTING.md`, `PRIVACY.md` y las instrucciones de licencia |

**Lo que NO va al repositorio:** `data/vocab_de.json`, `data/vocab_en.json`, `data/egp.json` ni ninguna lista fuente. Derivan de material con copyright de Goethe/Hueber, Oxford University Press, Cambridge, TUFS y Klett. Se regeneran con los scripts y las URL de `tools/fuentes.json` y `tools/fuentes_en.json`. Añadirlos a `.gitignore` en el primer commit.

## 3. Prompt inicial

> Vas a construir una aplicación Android en Kotlin con Jetpack Compose. Leé primero `CLAUDE.md`, que tiene las reglas no negociables, y después `schema/ficha.schema.json` y un par de archivos de `build/` para entender la forma del dato.
>
> La aplicación es un calendario de práctica lingüística sin conexión. Muestra el contenido de la semana ISO en curso según la fecha del dispositivo. No tiene cuentas, no guarda progreso y no accede a la red: el manifiesto no debe declarar el permiso `INTERNET`.
>
> Arrancá por lo mínimo utilizable, en este orden, y pará después de cada paso para que lo revise:
>
> 1. Proyecto base, `.gitignore` con las exclusiones de `BRIEF-CLAUDE-CODE.md`, y los JSON de `build/` como assets.
> 2. Capa de datos: cargar y parsear los assets, distinguiendo semanas de contenido de semanas especiales por sus dos schemas. Tests de la lógica de semana ISO 8601, incluido el borde entre diciembre y enero, y del caso "no hay ficha para esta semana", que es el estado habitual y no un error.
> 3. Pantalla única que renderice la ficha de la semana en curso. **Lector genérico**: recorrer los campos presentes en el JSON en vez de una función por campo, porque el schema todavía puede cambiar. Cuadro de referencia y vocabulario plegados, abiertos bajo demanda.
> 4. Botón de copiar al portapapeles para el prompt de corrección. Es la única interacción externa de la aplicación y no llama a ningún servicio.
> 5. Navegación a semanas y años anteriores, en solo lectura.
> 6. Ajustes: idioma de aprendizaje, nivel, `uiLanguage` y `baseLanguage` por separado, y conmutador de variantes regionales.
>
> No hagas nada de la sección "Qué NO construir" de `CLAUDE.md`. Si algo del schema te parece mal diseñado, decilo antes de trabajar alrededor del problema.

## 4. Orden sugerido y esfuerzo

| Paso | Esfuerzo |
|---|---|
| 1–2 · Base y capa de datos con tests | 2–3 tardes |
| 3 · Pantalla de la semana | 3–5 tardes |
| 4 · Copiar al portapapeles | 1 tarde |
| 5 · Navegación histórica | 2–3 tardes |
| 6 · Ajustes | 3–4 tardes |
| Empaquetado y F-Droid | proceso de revisión de semanas |

## 5. Lo que conviene decirle a Claude Code cuando aparezca la tentación

Tres cosas que van a proponerse solas y hay que rechazar:

- **"Agrego una base de datos Room para el contenido."** No: el contenido es de solo lectura y viaja en el APK. Room es superficie de auditoría innecesaria.
- **"Agrego el permiso de internet para descargar contenido nuevo."** No, y esta es la regla que define el proyecto.
- **"Guardo qué semanas completó el usuario."** No en esta fase. El modo revista es una decisión de diseño, no una limitación.
