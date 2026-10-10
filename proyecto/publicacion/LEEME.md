# Publicación (Play Store / F-Droid)

Textos listos en `fastlane/metadata/android/<idioma>/` (es-ES, en-US, de-DE, fr-FR, it-IT, pt-BR): el mismo formato lo lee
F-Droid, y se pueden pegar tal cual en la consola de Play. Límites: título ≤ 30, descripción corta ≤ 80,
larga ≤ 4000 caracteres. fr-FR, it-IT y pt-BR traducidos de es-ES en registro informal (tu / você; pt de Brasil); falta que un hablante los revise antes de publicar.
El nombre "LenguApp" es provisorio.

## Cómo dejar claro que NO es un curso
Tres lugares, de más a menos visible, sin guardar nada en el dispositivo (regla dura 4):
1. **Tienda**: es lo primero que dice la descripción (línea con ⚠, en las tres versiones) y lo primero que dice la descripción corta.
2. **Pantalla principal**: una línea fija bajo el nombre de la app: «Para fijar lo que ya aprendiste en tu curso.» (los 6 idiomas).
3. **Acerca de**: pantalla estática con qué es y qué no es, privacidad, licencias y versión.
No hay pantalla de bienvenida que se muestre una sola vez: requeriría recordar que ya se vio.

## Mensajes clave (en este orden)
1. No es un curso: fija lo que ya aprendiste.
2. Un reto por semana: una habilidad y un tema.
3. Vocabulario con audio, misión y microtareas cortas.
4. Sin conexión, sin cuenta, sin anuncios, sin rastreo; gratis y abierta.
5. Alemán e inglés de A2 a C2, interfaz en 6 idiomas.

## Antes de publicar
- Verificar con `FALTANTES.md` que los niveles y semanas que se anuncian existen (la descripción promete «A2 a C2» para alemán e inglés).
- Capturas: ficha de la semana, Biblioteca, Guardados, planilla PDF (4 a 6, en el idioma de cada ficha de tienda).
- Icono y pantalla de carga: pendientes (dependen de la dirección visual).
- Política de privacidad: `legal/PRIVACY.md`. Categoría sugerida: Educación. Clasificación de contenido: apta para todos.
