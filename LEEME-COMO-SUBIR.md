# Cómo subir estos archivos al repo — paso a paso

Son **18 archivos** de la sesión del 18-09-2026: todo lo que hoy quedó sin subir a
GitHub, en un solo paquete. Ya están organizados con la misma estructura de
carpetas del repo, así que no hay que decidir dónde va cada uno: se copian encima
y listo.

> Lo de la primera parte del día (el `autochequeo` de `DE-K06-C1`, los 14
> `contraste` que faltaban y el prompt de Núcleos) ya está en GitHub: se subió
> antes de que el token perdiera permiso. Este paquete es el resto.

Aparte va `todos-los-cambios.patch`, que es lo mismo en formato git para quien
prefiera `git am`. **No hace falta usarlo** si seguís los pasos de abajo, y no lo
copies al repo.

El token viejo lo dimos de baja, así que primero hay que crear uno nuevo.

---

## Paso 1 — Crear un token nuevo en GitHub

1. Entrá a GitHub y hacé clic en tu foto, arriba a la derecha → **Settings**.
2. Bajá hasta el final del menú de la izquierda → **Developer settings**.
3. **Personal access tokens** → **Tokens (classic)** → botón **Generate new token**
   → **Generate new token (classic)**.
4. Completá:
   - **Note**: `LenguApp` (es solo un nombre para acordarte).
   - **Expiration**: 90 días está bien.
   - **Scopes**: marcá la casilla **`repo`**. Esa sola. Es la que da permiso de
     escritura, que es justo lo que le faltaba al token anterior.
5. Botón **Generate token** abajo.
6. **Copiá el token ahora.** Empieza con `ghp_`. GitHub no lo vuelve a mostrar
   nunca más; si lo perdés hay que generar otro.

> **Importante:** no lo pegues en un chat, ni en un archivo del repo, ni en un
> documento compartido. GitHub escanea repos y revoca automáticamente los tokens
> que encuentra publicados — es casi seguro lo que le pasó al anterior. Guardalo
> en tu gestor de contraseñas.

---

## Paso 2 — Poner los archivos en tu carpeta local

Elegí el camino que te sirva según si ya tenés el repo bajado o no.

### Si YA tenés la carpeta del repo en tu computadora

1. Abrí una terminal en la carpeta del repo y traé lo último:

   ```
   git pull
   ```

2. Copiá el contenido de `LenguApp-18-09/` **encima** de tu carpeta del repo,
   respetando las subcarpetas. En el explorador de archivos: entrá a
   `LenguApp-18-09`, seleccioná `CLAUDE.md` y la carpeta `proyecto`, copiá, y
   pegá en la raíz del repo aceptando reemplazar.

   O por terminal, desde dentro de `LenguApp-18-09`:

   ```
   cp -r ./. /ruta/a/tu/repo/
   ```

   (En Windows con PowerShell: `Copy-Item -Path .\* -Destination C:\ruta\a\tu\repo -Recurse -Force`)

3. **No copies** al repo ni este `LEEME-COMO-SUBIR.md` ni `todos-los-cambios.patch`. Son solo para vos.

### Si NO tenés el repo bajado todavía

```
git clone https://github.com/Percati/LenguApp.git
cd LenguApp
```

Te va a pedir usuario y contraseña: poné tu usuario de GitHub y, **como
contraseña, el token** del Paso 1. Después copiá los archivos como arriba.

---

## Paso 3 — Revisar qué vas a subir (opcional pero recomendado)

Desde la carpeta del repo:

```
git status
```

Tiene que listar exactamente 18 archivos, 3 de ellos como nuevos
(`EN-G08-B1.json`, `EN-G08-B2.json`, `EN-G08-C1.json`) y 15 como modificados
(los 10 calendarios, `banco.json`, `generar_calendario.py`, `FALTANTES.md` y los
dos `CLAUDE.md`). Si aparecen archivos que no esperabas, pará y avisá antes de seguir.

Para ver los cambios en detalle:

```
git diff
```

(Se sale de esa vista con la tecla `q`.)

---

## Paso 4 — Subirlo

```
git add -A
git commit -m "Calendario 48 semanas, prioridad de habla y cupo de sintesis + EN-G08"
git push
```

En el `push` te va a pedir credenciales: **usuario de GitHub** y, como
contraseña, **el token**. Si te lo pide en cada push y te molesta, corré una sola
vez:

```
git config --global credential.helper store
```

y en el siguiente push las guarda (en texto plano en tu carpeta de usuario, así
que hacelo solo en tu computadora personal).

---

## Paso 5 — Confirmar

Entrá a `https://github.com/Percati/LenguApp` y fijate que arriba diga el mensaje
del commit que acabás de hacer. Si dice eso, está subido.

---

## Si algo sale mal

- **`Authentication failed` / `could not read Password`** → el token no tiene el
  scope `repo`, está vencido, o se pegó mal. Generá otro y revisá que la casilla
  `repo` quede marcada.
- **`Updates were rejected because the remote contains work that you do not have`**
  → alguien (otra conversación) subió algo entremedio. Corré `git pull`, resolvé
  si hay conflicto, y volvé a hacer `git push`.
- **`Please tell me who you are`** → falta identificarte una sola vez:

  ```
  git config --global user.name "FerP"
  git config --global user.email "fernandopercat@gmail.com"
  ```

- **Querés deshacer todo antes de subir** → `git checkout -- .` vuelve los
  archivos modificados a como estaban. Los tres `EN-G08-*.json` son nuevos, así
  que esos se borran a mano.

---

## Alternativa sin terminal: GitHub Desktop

Si la terminal te resulta incómoda, instalá **GitHub Desktop**
(`desktop.github.com`). Iniciás sesión con tu cuenta, hacés *File → Clone
repository*, elegís `Percati/LenguApp`, copiás los archivos encima de la carpeta
que te crea, y la app te muestra la lista de cambios con un cuadro para escribir
el mensaje y un botón **Commit** y otro **Push origin**. Es el mismo Paso 4 pero
con botones, y maneja el token por su cuenta.
