# CLAUDE.md — instrucciones del repositorio

Claude Code lee este archivo automáticamente. Contiene lo que **no se vuelve a decidir**.
El brief completo está en `proyecto/PROMPT-CLAUDE-CODE.md`.

## Reglas duras

1. **Nunca agregar el permiso `INTERNET` al manifiesto.** Es la prueba verificable de que la app no puede rastrear nada. Si algo parece necesitar red, no se hace.
   **Excepción ya revisada:** el manifiesto fusionado trae `io.github.percati.lenguapp.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, con `protectionLevel="signature"`. No es de red: la agrega AndroidX (Compose/Activity) para que `registerReceiver` con receptores no exportados sea seguro entre apps del mismo firmante en versiones viejas de Android. Es de solo la propia app (nadie más puede tenerla salvo que firme con la misma clave), no aparece en Exodus como rastreador, y sacarla arriesgaría romper `registerReceiver` sin ganar nada real. No tocar esto por estética.
   **`androidx.glance:glance-appwidget` (el widget de la Fase 4) arrastra WorkManager, y WorkManager declara `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`, `FOREGROUND_SERVICE` y `ACCESS_NETWORK_STATE`.** Esos cuatro se sacan a mano en `AndroidManifest.xml` con `tools:node="remove"`. No es un descuido pendiente de arreglar: con `minSdk 26` WorkManager usa siempre JobScheduler y nunca el backend viejo de AlarmManager que necesita los dos primeros, y el widget no declara restricciones de red ni trabajo en primer plano, así que los otros dos tampoco hacen falta. Verificado con `aapt dump permissions` sobre el APK real, no solo sobre el manifiesto fusionado.
   **Ya no depende de que alguien se acuerde de correr `aapt dump permissions` a mano.** La tarea Gradle `verificarPermisosApk` (`app/build.gradle.kts`) corre `aapt dump permissions` sobre el APK real después de cada `assembleDebug` (`finalizedBy`, porque necesita que el APK ya exista) y rompe el build si aparece un permiso fuera de la lista blanca `permisosPermitidos`. Es el hallazgo más importante del proyecto hasta ahora: si WorkManager (o lo que sea que arrastre una futura versión de AndroidX) cambia de comportamiento interno, `tools:node="remove"` puede dejar de alcanzar, o puede alcanzar pero romper algo en silencio — por ejemplo, el widget dejando de actualizarse sin que ningún test lo note, porque nada en este proyecto instrumenta el ciclo de vida real de WorkManager. Si `verificarPermisosApk` corta el build tras subir una dependencia, **no agregar el permiso nuevo a la lista blanca sin entender primero por qué apareció** — el mismo análisis que llevó a `tools:node="remove"`, no un reflejo.
2. El único dato que se lee del dispositivo es **la fecha**.
3. Sin Google Play Services, Firebase, analítica, informes de fallos ni SDK de terceros.
4. **Sin persistencia de progreso.** Modo revista: manda la fecha. No implementarla aunque parezca una mejora obvia.
5. Semanas **ISO 8601** con la API nativa (`IsoFields.WEEK_OF_WEEK_BASED_YEAR`). Nunca a mano.
6. `uiLanguage` y `baseLanguage` son **ajustes distintos**. No unificarlos.
7. El calendario se **precalcula y se embebe**, no se genera en el dispositivo.
8. Las semanas de repaso y Survival usan `semana-especial.schema.json`, **no** el schema de ficha.

## Convenciones de git

Cuando un test falla por un cambio intencional, la actualización del test va en un **commit aparte** del cambio de código, con el motivo en el mensaje. **Si el motivo no entra en una línea, el problema es el cambio, no el test** — no lo actualices, revisá el cambio primero.

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
