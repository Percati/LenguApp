# Verbos — cerrar la primera tanda: la cuenta, la trampa de la herramienta y el cruce inglés

Escrito por Overview el 18-oct-2026, sobre el informe de la tanda 1 del prompt 40.

*(Registrado en el repo el 18-oct junto con los prompts 42 y 43: se entregó a Fer sin commitear mientras la tanda seguía abierta.)*

Dos cosas antes de las tareas, porque corresponde.

**Tocar la herramienta no era opcional y tu diagnóstico fue exacto.** Leí el código: la comprobación final de `aplicar_verbos.py` recorre `list(tabla) + list(otros) + list(cols)` y marca error por cada clave que no apareció en ningún pack. Una entrada de referencia cae ahí por definición, así que el requisito 3 del prompt 40 («0 errores») **era imposible de cumplir** tal como lo escribí. Lo paraste y avisaste, que es lo que había que hacer.

**Tus cuatro decisiones de criterio me parecen bien y son las que yo habría tomado**, en particular la de los siete verbos con la preposición en la `forma` (`belong to`, `consist of`, `depend on`…) frente a los cinco con la preposición en `nota` (`care`, `point`, `apply`…). El criterio —la lleva en la forma el verbo que sin ella significa otra cosa— es el correcto. **No las uniformes.**

## Tarea 1 — la cuenta no cierra, y hay que reconciliarla

Tu informe dice que de los 277 candidatos alemanes de A1 y A2, **128 ya estaban en la tabla**. **Yo mido 76**, por coincidencia exacta contra `forma`, `item` y `base`.

De los 201 que yo veo ausentes, puedo explicar 31:

- **26 figuran solo como el `raiz` de otra entrada**: `bleiben`, `braten`, `bringen`, `fahren`, `fallen`, `finden`, `geben`, `halten`, `holen`, `hören`, `kennen`, `kommen` y catorce más. Que `aufstehen` tenga `raiz: "stehen"` **no pone `stehen` en la lista**: no hay ninguna entrada cuya `forma` sea `stehen`.
- **5 figuran solo como `sich X`**, y la forma desnuda es otro verbo.
- Los **170 restantes no los puedo explicar**.

Unos 52 verbos parecen caídos entre las dos cuentas. Lo que necesito:

1. **La lista de los 128**, con **cómo coincidió cada uno** (contra qué campo).
2. **La reconciliación que sume 277**, con cada resultado posible y su total.

**Y si mi medición está mal, decímelo con la lista y lo acepto sin discutir.** Lo que no se puede es que las dos cuentas convivan: dentro de tres meses nadie va a saber cuál era la buena.

## Tarea 2 — cerrar la trampa que abrió la herramienta

Al sacar las entradas de referencia del mapa que se aplica, quedó una trampa: el día que un pack nuevo use un verbo que ya está en la lista de referencia, esa aparición **no se anota y nada avisa**. El verbo queda sin `claseVerbo` en el pack y la herramienta sigue dando 0 errores.

Que la herramienta lo detecte. Te dejo elegir entre **error** y **aviso**, con mi preferencia y el motivo: **error**, porque un aviso entre seiscientas líneas de salida no se mira, y porque una regla que se incumple en silencio deja de ser una regla — es el criterio del resto de las guardas del proyecto. El mensaje tiene que decir qué hacer, no solo que algo está mal.

Y fijalo con una prueba, al estilo de las otras herramientas: dos casos, con y sin el ítem de referencia en el pack.

## Tarea 3 — la selección inglesa no tiene red

El filtro alemán es **repetible**: lema sin artículo, terminado en `-en`/`-rn`/`-ln`, nivel A1 o A2. Cualquiera puede volver a correrlo y obtener los mismos 277.

El inglés fue **a ojo sobre 2300 lemas**, y nadie —vos incluido— puede saber qué quedó afuera. Eso no es culpa tuya: el prompt 40 no te dio un filtro porque no existe uno equivalente. Pero sí te dio dos listas, y hay que usarlas como red.

Cruzá la selección contra **`EN Grammar Vocabulary First and First for Schools Phrasal verbs.pdf`** y **`Irregular verbs.pdf`** (documentos del proyecto) e informá **qué verbo de nivel A1 o A2 de esas listas no está ni en la tabla ni en tu tanda**. Si la respuesta es «ninguno», queda documentado y la selección deja de ser un acto de fe.

## Tarea 4 — FALTANTES 72

La sección que ofreciste. Que lleve la tabla reconciliada de la tarea 1, la decisión de `referencia: true` y por qué hubo que tocar la herramienta, la trampa y cómo quedó cerrada, el cruce de la tarea 3, las cuatro decisiones de criterio tal como las dejaste, y lo que queda abierto.

## Qué no hacer

- **Nada de B1** hasta que la cuenta de la tarea 1 cierre y Fer apruebe la forma.
- No uniformes las cuatro decisiones de criterio.
- No toques los packs, los núcleos, las ocurrencias ni nada de `app/`.

## Lo de siempre

Un commit, nada de push, el parche en `outputs/verbos-referencia-1b/`, commiteado por ruta explícita.

**Y mandá el archivo del parche a la conversación:** la vez pasada llegó el informe sin el parche, así que solo pude verificar lo que se lee del repo y nada de lo que escribiste.
