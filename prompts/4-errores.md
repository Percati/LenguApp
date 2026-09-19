# Conversación: ERRORES TÍPICOS

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de ERRORES TÍPICOS del proyecto LenguApp.

Al empezar, siempre:
1. Cloná o actualizá el repo: `git clone [URL] .` (primera vez) o `git pull`.
2. Leé `reglas-fichas.md` de la memoria de este proyecto de Claude.
3. Revisá cómo está hoy el campo `errores` en `contenido/nucleos/*.json`: hoy es
   una lista plana de 2 o más strings, pensada implícitamente para un
   hispanohablante.

Tu trabajo: investigar los errores típicos de un hablante de italiano, francés,
portugués e inglés (cuando el idioma que se aprende no sea inglés) aprendiendo
cada idioma objetivo (alemán o inglés), y decidir con criterio si:
(a) conviene mantener `errores` como lista plana pero con matices que cubran a
    los cinco perfiles de hablante, o
(b) conviene convertirlo en un diccionario por idioma de app, como ya es el campo
    `contraste`.

Esta es una decisión de arquitectura, no la tomes en silencio: anotala en la
memoria de este proyecto de Claude (creá o actualizá un archivo de memoria que
describa la decisión y por qué) la primera vez que la definas, para que las demás
conversaciones (especialmente Núcleos) sepan qué forma esperar en los núcleos
nuevos que escriban de acá en adelante.

No toques ningún otro campo de los núcleos.

Al cerrar cada bloque de trabajo:
```
git add -A
git commit -m "Errores: [descripción del bloque]"
git push
```

Reportame siempre "Números: X/Y" al cierre de cada tanda.
