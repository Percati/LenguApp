# CLAUDE.md — instrucciones del repositorio

Claude Code lee este archivo automáticamente. Contiene lo que **no se vuelve a decidir**.
El brief completo está en `proyecto/PROMPT-CLAUDE-CODE.md`.

## Reglas duras

1. **Nunca agregar el permiso `INTERNET` al manifiesto.** Es la prueba verificable de que la app no puede rastrear nada. Si algo parece necesitar red, no se hace.
2. Los únicos datos que se leen del dispositivo son **la fecha** y, **si el usuario lo elige explícitamente**, el idioma del sistema. Nada sale del dispositivo. (La formulación original decía solo "la fecha"; se enmendó al agregar la opción "Idioma del sistema" — ver `AJUSTES-FASE-6.md`, bloque E. Una regla dura que se incumple en silencio deja de ser una regla.)
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

**2026 está completo**: 34 fichas compiladas y validadas en `proyecto/build/` — semanas 37 a 53 en inglés C1 y alemán B2, con sus 4 semanas de repaso y 2 Survival.

2027 no existe todavía. Y ningún otro nivel ni idioma tiene contenido. **"Esta semana / este nivel no tiene contenido" sigue siendo el caso normal para casi toda la matriz**, así que hay que manejarlo con elegancia desde la fase 1.

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
python3 proyecto/tools/generar_audio.py app/src/main/assets/contenido/*.json --dry-run
```

`assembleDebug` debe fallar si el JSON de `assets/` no valida contra el schema.

## Licencias

Código GPL-3.0, contenido CC BY-SA 4.0. Ver `proyecto/legal/`. Los dos textos de licencia hay que descargarlos: `proyecto/legal/LEEME-LICENCIAS.md`.
