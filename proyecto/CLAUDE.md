# CLAUDE.md — instrucciones del repositorio

Claude Code lee este archivo automáticamente. Contiene lo que **no se vuelve a decidir**.
El brief completo está en `proyecto/PROMPT-CLAUDE-CODE.md`.

## Reglas duras

1. **Nunca agregar el permiso `INTERNET` al manifiesto.** Es la prueba verificable de que la app no puede rastrear nada. Si algo parece necesitar red, no se hace.
2. Los únicos datos que se leen del dispositivo son **la fecha** y, **si el usuario lo elige explícitamente**, el idioma del sistema. Nada sale del dispositivo. (La formulación original decía solo "la fecha"; se enmendó al agregar la opción "Idioma del sistema" — ver `docs/historial/AJUSTES-FASE-6.md`, bloque E. Una regla dura que se incumple en silencio deja de ser una regla.)
3. Sin Google Play Services, Firebase, analítica, informes de fallos ni SDK de terceros.
4. **Sin persistencia de progreso.** Modo revista: manda la fecha. No implementarla aunque parezca una mejora obvia.
5. Semanas **ISO 8601** con la API nativa (`IsoFields.WEEK_OF_WEEK_BASED_YEAR`). Nunca a mano.
6. `uiLanguage` y `baseLanguage` son **dos campos distintos en los datos**, aunque en la pantalla de ajustes se presenten como uno solo. No fusionarlos en el modelo: el día que alguien quiera interfaz en un idioma y glosas en otro, se desdobla el ajuste sin migrar datos.
9. Los nombres de campo (`uiLanguage`, `baseLanguage`, `challengeType`…) **nunca aparecen en texto visible al usuario**.
10. La lista de idiomas y niveles disponibles **se deriva del contenido embebido**, nunca se escribe a mano.
7. El calendario se **precalcula y se embebe**, no se genera en el dispositivo.
8. Las semanas de repaso usan `semana-especial.schema.json`, **no** el schema de ficha.
11. **Presupuesto de semanas (desde 2027):** 52 semanas ISO = 4 repasos + 48 semanas de contenido. El Survival ya no ocupa semanas enteras: el desafio vive en el **fin de semana** de cada semana (sabado 00:00 a domingo 23:59, hora local) y cuatro veces al anio, al cierre de cada trimestre, ese desafio es el Survival largo en vez del semanal. El esquema anterior (6 repasos + 3 semanas Survival = 43 de contenido) quedo atras; `data/semanas-fijas.json` y el piloto 2026 siguen bajo el esquema viejo y no se regeneran.
12. **Cobertura del banco:** todo skill de un par (idioma, nivel) aparece **al menos una vez** en el anio. Solo repiten los marcados como nucleo de ESE nivel, via el campo `repiteEn` de `banco.json`, que es una lista de niveles porque la importancia depende del nivel y no del skill. **El criterio de que se machaca es fijo: fluidez (F) > expresiones y lexico oral (V) > sintesis (K) > precision gramatical (G)**, con un **cupo minimo reservado a la sintesis que crece con el nivel: 2 en B2, 3 en C1, 4 en C2** (0 en A2 y B1, donde la serie K no existe). La app existe para mejorar el habla, asi que se repite lo que se usa al hablar y la gramatica de precision aparece igual, una vez; el cupo de sintesis esta porque reformular sobre la marcha ES una habilidad oral, y pesa mas cuanto mas alto el nivel. Sin ese cupo, los niveles apretados (de-C1, en-C1, en-B2) repetian solo fluidez. Este orden y estos cupos NO se vuelven a discutir por nivel. Antes el minimo era dos apariciones para todos, y eso obligaba a descartar hasta 20 skills por par.
13. **El switch del desafio del fin de semana no persiste nada.** Solo se muestra dentro de la ventana del fin de semana (derivada de la fecha, que si se puede leer) y **en cada apertura de la app arranca en off**. Aceptar el desafio no se guarda: si se guardara seria persistencia de progreso y violaria la regla 4. El compromiso es del usuario, no del almacenamiento.

## Estado del contenido

El pipeline de contenido cambió: ya NO se escribe una ficha por semana en Markdown
y se compila directo. Ahora hay tres capas independientes que se combinan:

1. `contenido/nucleos/{SKILL}-{NIVEL}.json` — el contenido pedagógico fijo del
   skill (gramática, ejemplos, contraste, errores). No cambia entre apariciones.
2. `contenido/packs/{idioma}-{nivel}-{anio}-{topicId}-{n}.json` — el vocabulario
   de una aparición concreta de un tema.
3. `contenido/ocurrencias/{idioma}-{nivel}-{anio}.json` — qué skill+topic+pack
   corresponde a cada semana del año, más la misión y micro-tareas de esa semana.

En los packs, `prioridad` es `"nucleo"` o `"ampliacion"` — **nunca `"variante"`**,
que es otro campo del ítem (regionalismo, p.ej. `"CH"`). Los packs de cada idioma se redactan desde ese idioma, nunca traduciendo el pack del otro
idioma del mismo tema (ver `prompts/2-packs.md`). Validar siempre con
`python3 proyecto/tools/validar_packs.py --anio AAAA` antes de commitear packs.

`tools/componer.py` combina las tres capas y escribe la ficha final en `build/`,
validada contra `schema/ficha.schema.json`. **Nunca editar `build/` a mano**: se
regenera siempre desde la fuente.

Ver `FALTANTES.md` para el estado real y actualizado de qué combinación de
idioma/nivel/año tiene cada una de las tres capas completa. Ese archivo se
actualiza seguido — no asumas que una fecha vieja en otro documento sigue
vigente.

## Portabilidad

Las herramientas de `proyecto/tools/` declaran `encoding="utf-8"` en toda lectura y escritura, y reconfiguran stdout. **En Windows, sin eso, Python cae a cp1252 y rompe los caracteres no ASCII** de las fichas. Si se agrega una herramienta, mantener la misma convención.

## Comandos

```sh
python3 proyecto/tools/compilar_fichas.py proyecto/fichas/*.md \
    --salida app/src/main/assets/contenido --schema proyecto/schema/ficha.schema.json

python3 proyecto/tools/generar_calendario.py --anio 2026 --desde 37 \
    --idioma de --nivel B2 --banco proyecto/data/banco.json \
    --fijas proyecto/data/semanas-fijas.json --formato json

python3 proyecto/tools/check_level.py proyecto/fichas/*.md --vocab proyecto/data/vocab_de.json
```

El audio no se genera desde un script del repo: Fer lo genera localmente con
Piper y lo sube directo a `assets/audio/` (Opus 24 kbps mono, `.ogg`).

`assembleDebug` debe fallar si el JSON de `assets/` no valida contra el schema.

## Licencias

Código GPL-3.0, contenido CC BY-SA 4.0. Ver `proyecto/legal/`. Los dos textos de licencia hay que descargarlos: `proyecto/legal/LEEME-LICENCIAS.md`.
