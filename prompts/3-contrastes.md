# Conversación: CONTRASTES

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de CONTRASTES del proyecto LenguApp.

Al empezar, siempre:
1. Cloná o actualizá el repo: `git clone [URL] .` (primera vez) o `git pull`.
2. Leé `reglas-fichas.md` de la memoria de este proyecto de Claude — ahí está la
   regla exacta que gobierna este trabajo: "la sección de discrepancias no es
   universal, depende del par (idioma que se aprende, idioma de la app)".
3. Los seis idiomas de app previstos son ES, EN, DE, IT, FR, PT. El campo
   `contraste` de cada núcleo en `contenido/nucleos/*.json` ya tiene la clave
   `"es"` escrita. Tu trabajo es agregar `"it"`, `"fr"`, `"pt"` y, cuando el idioma
   que se aprende no sea inglés, también `"en"` — SIN tocar ninguna otra parte del
   archivo (ni `errores`, ni `descripcion`, ni nada más).

Priorizá los núcleos de categoría `grammar`, donde el contraste realmente cambia el
comportamiento del alumno (por ejemplo: un francófono y un hispanohablante tienen
problemas distintos con el subjuntivo alemán; un italófono y un portugués nativo
comparten más intuiciones entre sí que con un francófono). Si dos idiomas de app
comparten exactamente el mismo fenómeno de contraste, no lo repitas mecánicamente
palabra por palabra — está bien que el texto sea similar si el fenómeno lo es,
pero verificá que sea genuinamente así y no una copia perezosa.

Trabajá combinación por combinación (ej: agregar "it" a todos los núcleos de
alemán C1 antes de pasar a otra cosa) para poder commitear en bloques con sentido.

Al cerrar cada bloque:
```
git add -A
git commit -m "Contrastes: agrega [idioma app] a [idioma+nivel] (N núcleos)"
git push
```

Reportame siempre "Números: X/Y idiomas de app cubiertos para [combo], Y núcleos
tocados" al cierre de cada tanda.
