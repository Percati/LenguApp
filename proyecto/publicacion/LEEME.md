# Publicación (Play Store / F-Droid)

Textos listos en `fastlane/metadata/android/<idioma>/` (es-ES, en-US, de-DE, fr-FR, it-IT, pt-BR): el mismo formato lo lee
F-Droid, y se pueden pegar tal cual en la consola de Play. Límites: título ≤ 30, descripción corta ≤ 80,
larga ≤ 4000 caracteres. fr-FR, it-IT y pt-BR traducidos de es-ES en registro informal (tu / você; pt de Brasil); falta que un hablante los revise antes de publicar.
El nombre "LenguApp" es provisorio.

## Cómo dejar claro que NO es un curso
Tres lugares, de más a menos visible, sin guardar nada en el dispositivo (regla dura 4):
1. **Tienda**: es lo primero que dice la descripción (línea con ⚠, en las tres versiones) y lo primero que dice la descripción corta.
2. **Ajustes**: arriba del todo, el nombre de la app y una línea fija: «Para fijar lo que ya aprendiste en tu curso.» (los 6 idiomas), con una fila «Acerca de». Ya no está en la pantalla principal, que queda sin encabezado para dar el alto al contenido.
3. **Acerca de**: pantalla estática con qué es y qué no es, cómo está pensada, privacidad, licencias y versión.
4. **Bienvenida** (Ronda N, 0.3.0): en el primer arranque, después de elegir idioma y nivel, el mismo texto de «Acerca de» a la vista. Recordar que ya se vio es un **ajuste** (`bienvenidaCompletada`), que la regla dura 4 permite; no es progreso.

## Mensajes clave (en este orden)
1. No es un curso: fija lo que ya aprendiste.
2. Un reto por semana: una habilidad y un tema.
3. Vocabulario con audio, misión y microtareas cortas.
4. Sin conexión, sin cuenta, sin anuncios, sin rastreo; gratis y abierta.
5. Alemán e inglés de A2 a C2, interfaz en 6 idiomas.

## Antes de publicar
- Verificar con `FALTANTES.md` que los niveles y semanas que se anuncian existen (la descripción promete «A2 a C2» para alemán e inglés).
- Capturas: ficha de la semana, Biblioteca, Guardados, planilla PDF (4 a 6, en el idioma de cada ficha de tienda).
- Icono y pantalla de carga: pendientes (Fer los hará cuando tenga un diseño que le guste).
- **Audio inglés: NO se publica.** Las voces `en_US-ryan` (CC BY-NC-SA 4.0) y `en_US-lessac` (Blizzard 2013) no se pueden redistribuir; el release ya excluye `assets/audio/EN` y lo verifica una tarea de Gradle. Hasta regenerarlos con una voz de corpus limpio, **la descripción de la tienda no puede prometer audio en inglés**: revisar las 6 descripciones antes de publicar.
- **Firma de la app**: no hay `signingConfig` en `app/build.gradle.kts`. Hay que crear el almacén de claves, guardarlo fuera del repo y configurarlo (y, en Play, decidir si se usa la firma gestionada por Google).
- **Formulario de seguridad de los datos** (Play): la app no recoge ni comparte nada; marcar todo en «no» y apuntar a `legal/PRIVACY.md`.
- Política de privacidad: `legal/PRIVACY.md`. Categoría sugerida: Educación. Clasificación de contenido: apta para todos.
