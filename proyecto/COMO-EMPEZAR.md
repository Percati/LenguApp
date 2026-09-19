# Cómo poner LenguApp en GitHub (guía sin experiencia previa)

Esto te va a llevar unos 15-20 minutos la primera vez. Después nunca más tenés
que hacer esto de nuevo — cada conversación de Claude se encarga sola de bajar y
subir los cambios.

## Parte 1 — Crear tu cuenta de GitHub (si no tenés)

1. Andá a **github.com** y hacé clic en "Sign up" (arriba a la derecha).
2. Poné tu email, una contraseña, y un nombre de usuario. Es gratis.
3. Confirmá tu email cuando te llegue el correo de verificación.

Si ya tenés cuenta, saltá a la Parte 2.

## Parte 2 — Crear el repositorio (la "carpeta" del proyecto en GitHub)

1. Ya logueado, hacé clic en el **"+"** arriba a la derecha → **"New repository"**.
2. En "Repository name" poné: `lenguapp`
3. Marcalo como **Private** (así solo vos lo ves) — el botón está debajo del
   nombre, elegí el círculo de "Private".
4. NO marques ninguna de las casillas de abajo ("Add a README", ".gitignore",
   "license") — dejalas todas destildadas. Vamos a subir nosotros el contenido.
5. Hacé clic en **"Create repository"** (botón verde, abajo).

Ahora vas a ver una pantalla con instrucciones técnicas — ignoralas por ahora,
vamos a usar un método más simple (subir por el navegador, sin comandos).

**Copiá la URL de tu repositorio** — es algo como
`https://github.com/tu-usuario/lenguapp` — la vas a necesitar más adelante para
reemplazar `[URL]` en los prompts. Guardala en un bloc de notas.

## Parte 3 — Crear tu token de acceso (para que Claude pueda subir cambios)

Sin esto, Claude puede DESCARGAR el contenido pero no puede SUBIR cambios nuevos.
Es como una contraseña especial, de un solo uso para este propósito.

1. Hacé clic en tu foto de perfil (arriba a la derecha) → **"Settings"**.
2. En el menú de la izquierda, bajá hasta el final → **"Developer settings"**.
3. **"Personal access tokens"** → **"Tokens (classic)"**.
4. **"Generate new token"** → **"Generate new token (classic)"**.
5. En "Note" escribí: `lenguapp-claude`
6. En "Expiration" elegí **"90 days"** (o "No expiration" si preferís no repetir
   este paso, aunque es menos seguro).
7. Marcá la casilla grande **"repo"** (esto marca automáticamente todas las de
   abajo de esa categoría). No hace falta marcar nada más.
8. Bajá y hacé clic en **"Generate token"** (botón verde).
9. **GitHub te va a mostrar el token UNA SOLA VEZ.** Es un texto largo que
   empieza con `ghp_...`. Copialo y guardalo en el mismo bloc de notas que la
   URL. Si lo perdés, tenés que generar uno nuevo (no pasa nada, pero es un paso
   extra).

## Parte 4 — Subir el contenido que ya tenemos

1. Descomprimí el archivo `.zip` que te compartí en tu computadora — te va a
   quedar una carpeta llamada `lenguapp` con todo adentro.
2. En GitHub, andá a tu repositorio recién creado (la URL que guardaste).
3. Hacé clic en **"uploading an existing file"** (aparece en el medio de la
   pantalla de un repo vacío) — o si no lo ves, andá a **"Add file" → "Upload
   files"** (arriba a la derecha de la lista de archivos).
4. **Abrí la carpeta descomprimida en tu explorador de archivos, seleccioná TODO
   lo que está adentro (Ctrl+A o Cmd+A), y arrastralo** a la zona gris que dice
   "Drag files here" en la página de GitHub. Esto puede tardar unos minutos si
   hay muchos archivos — no cierres la pestaña.
5. Abajo de todo, en "Commit changes", dejá el mensaje que aparece por defecto y
   hacé clic en **"Commit changes"** (botón verde).

Listo. Tu repositorio ya tiene todo el proyecto.

## Parte 5 — Usar la URL con el token en cada conversación nueva

Cuando abras una conversación nueva de Claude y le pases uno de los prompts de la
carpeta `prompts/`, reemplazá `[URL]` por esta forma exacta (con tu token adentro,
para que Claude también pueda subir cambios, no solo bajarlos):

```
https://TU_TOKEN@github.com/tu-usuario/lenguapp.git
```

Por ejemplo, si tu usuario es `fer2026` y tu token es `ghp_abc123`, la URL que le
das a Claude es:

```
https://ghp_abc123@github.com/fer2026/lenguapp.git
```

**Importante:** no compartas esa URL con nadie ni la pegues en ningún lado
público — tiene tu token adentro, que da acceso de escritura a tu repositorio.

## Qué hace cada conversación con esto

Cada archivo dentro de `prompts/` (1 al 7) es el primer mensaje que le pegás a
una conversación nueva de Claude, dedicada a una sola tarea. Ya tienen anotadas
todas las reglas y decisiones que fuimos tomando, para que no se pierda ningún
detalle al dividir el trabajo. Abrí cada prompt, reemplazá `[URL]` como se explicó
arriba, y pegalo como primer mensaje de una conversación nueva dentro del mismo
proyecto de Claude.
