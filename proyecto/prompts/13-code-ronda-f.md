# Prompt para Code — Ronda F (pulido y nueva versión 0.2.1)

Leé `CLAUDE.md` (reglas 1-14), `proyecto/REGLAS-PREVENCION.md` y `FALTANTES.md` secciones 21 y 22. **Esperá a que estén aplicados**: (a) el parche de Overview de esta ronda y (b) el parche de Traducciones de tuteo (`prompts/12-…`), con los assets regenerados por Overview: de lo contrario el verificador enganchado a `preBuild` falla por la regla R6. Commit por tarea, `compileDebugKotlin compileDebugUnitTestKotlin test` en verde antes de cada uno. No pushees.

## Tarea 1 — Pestañas de idioma con nombre, no código
En la Biblioteca, en Guardados y en la pantalla principal las pestañas de idioma muestran `EN` / `DE`. Mostrar el **nombre del idioma en el idioma de app** («Alemán», «German», «Deutsch», «Allemand», «Tedesco», «Alemão»…). Reutilizá los nombres que ya existen en `TextosInterfaz`/Ajustes si los hay; si no, creá la clave con los 6 idiomas. Test: ninguna pestaña muestra un código de dos letras en ningún idioma de app.

## Tarea 2 — `widget_descripcion` en todos los idiomas
Hoy `res/values/strings.xml` lo tiene solo en español. Dejá el inglés en `values/` (fallback) y agregá `values-es`, `values-de`, `values-fr`, `values-it`, `values-pt` con la descripción traducida (texto corto, sin tecnicismos). Verificá que ningún otro string de `res/` quede solo en español (el manifest/etiquetas también).

## Tarea 3 — Portugués de Brasil y registro informal en los 6 idiomas
- Las cadenas de interfaz agregadas en Ronda E están en portugués **de Portugal** («telemóvel», «a aprender», «oferece-lhe», «numa só gravação», «a meio», «percebe-se?», «Diga-o»), pero la voz de la app y el contenido son **de Brasil** (`pt_BR-faber-medium`). Pasá TODAS las cadenas PT de `TextosInterfaz`/`EtiquetasSeccion`/planilla a portugués de Brasil («celular», «aprendendo», «oferece», «em uma única gravação», «na metade», «dá para entender?», «Diga isso»…). Dejá «Ajustes» solo si es lo que ya usa la app; si preferís «Configurações», cambialo en todo el idioma.
- Registro: hoy conviven tuteo en ES/DE/IT/EN y trato de usted en FR y PT. Unificá a informal en los 6 idiomas (tú, du, tu, you, **tu** en francés, **você** en portugués). Hacelo en todas las cadenas de interfaz y en `planilla-profesor.json` sólo donde el texto sea para el alumno (el texto de la planilla dirigido al profesor mantiene su registro actual).
- Dejá un test que compruebe que no queda ninguna palabra de una lista corta de portugués europeo en las cadenas PT.

## Tarea 4 — Acerca de coherente con la privacidad
El texto de privacidad de «Acerca de» ya es el correcto. Comprobá que coincide con `PRIVACY.md` (Overview lo corrigió en esta ronda: se leen la fecha y, si el usuario lo elige, el idioma del sistema; solo se guardan las estrellas y los ajustes). Si algo difiere, el texto de la app manda y se reporta.

## Tarea 5 — Versión 0.2.1
- `versionName` `0.2.1`, `versionCode` `3`.
- Compilá el APK debug y reportá su tamaño; confirmá que el manifest final no tiene `INTERNET` ni los permisos removidos (regla 1).

## Tarea 6 — Test de la «matriz de superficies» (chico, previene regresiones)
Un solo test JVM que recorra idioma aprendido (en, de) × idioma de app (6) × nivel (A2, B1, B2) y, para una ficha de cada combinación, afirme el idioma de salida de cada superficie según la tabla de `REGLAS-PREVENCION.md` §2 (etiquetas de UI, nombres de topic/categoría, ficha, planilla, prompt de corrección). Sin pantallas: sobre las funciones de `presentacion/` y `datos/`. Si una superficie no se puede probar sin UI, anotala en el informe en vez de forzarla.

## Fuera de alcance
Las 3 planillas que quedan en dos carillas con 4 líneas (`DE-G25-C2-2027-1`, `DE-V05-B2-2026-1`, `DE-V05-B2-2027-1`): son 3 de 662 y se ven en el teléfono antes de decidir. Ícono, pantalla de carga y UI: esperan la dirección visual de Fer.

## Informe
Commit por tarea, tests, tamaño del APK, decisiones donde el prompt no cerraba. Actualizá `FALTANTES.md`.
