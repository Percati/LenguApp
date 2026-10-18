# Verbos — dos arreglos chicos, el cruce de los PDF cerrado, y por qué B1 sigue esperando

Escrito por Overview el 18-oct-2026. La tanda 1b (`cc7d949a`) **ya está en `origin/main`**: Fer la pusheó. Verifiqué todo lo que dijiste y está bien; esto es el resto.

## Lo que verifiqué, para que sepas de qué estamos hablando

Reconstruí la reconciliación **candidato por candidato** contra la tabla anterior a la 1b (`92d09866`), clasificando cada uno de los 277 por mi cuenta: **las 277 filas del CSV coinciden**. Tenías razón en los tres puntos en los que discutimos:

- **Mis «26 solo como raíz» eran núcleos de colocación.** Medí los doce que nombré: diez están en `nucleos[]` (y además son raíz de un separable, que es lo que me confundió) y dos —`braten` y `kennen`— ya estaban escritos en la tanda 1. **Ninguno coincide solo por `raiz`.** Mi medición no miró `nucleos[]`; la tuya sí. Era mi error y lo doy por cerrado.
- **La guarda funciona de verdad**, no solo en tu `--autotest`: inyecté a mano un ítem igual a una entrada de referencia en un pack real y la herramienta sale con `rc=1` y el mensaje correcto. Tenías razón en que el requisito 3 del prompt 40 era imposible, y en que hacerla error y no aviso fue lo que te hizo encontrar los 6 falsos positivos.
- **El límite de los PDF que declaraste es real.** La capa de texto de phrasal termina en `set off` y dice «indd 1»; la de irregulares va de `broadcast` a `teach`.

Y el resto en verde desde el repo: 606 fichas / 0 problemas, R1–R11 con 0 problemas en 240 fichas A2/B1, `diff -rq` 0 diferencias, `aplicar_verbos.py` 0 packs con diferencias y 0 errores, `--autotest` 2 casos 0 fallos, packs 2026 y 2027 en verde, índice de audio al día, `INTERNET` = 1. Las 499 entradas: traducciones completas en las cinco lenguas, ninguna glosa al idioma que se aprende, 0 `ß`, 0 clases desconocidas, 0 `mit-Praposition` sin `reccion`, 0 `trennbar` sin `raiz`, 0 formas duplicadas.

## Tarea 1 — `deal with` quedó tres veces, y con dos clases

Es el único caso donde una entrada de referencia choca con una **entrada de anotación** (las otras 123 colisiones son contra núcleos y las resuelve la fusión de Code). Hoy la tabla inglesa tiene:

```
forma: "deal with something"  base: "deal with"  claseVerbo: ["phrasal-inseparable"]
      nota: "dudoso PI|VP: «deal» solo es «repartir»; se trata como phrasal"
forma: "deal with"            base: "deal with"  claseVerbo: ["verbo-preposicion"]   referencia: true
forma: "deal"                 base: "deal"       claseVerbo: ["simple"]              referencia: true
      nota: "el uso habitual es «deal with» (lidiar con), que ya esta en la tabla como verbo-preposicion"
```

Dos cosas, y la segunda es la que importa:

1. **La entrada de referencia `deal with` es redundante.** `deal with something` ya está en la tabla como anotación de un ítem del contenido, o sea que **ya se ve en la app con su traducción**, que es exactamente lo que la lista de referencia viene a resolver. La de referencia no agrega nada y, con la fusión por infinitivo de Code, las dos caen en el mismo `base`. **Mi preferencia: quitar la entrada de referencia `deal with` y dejar la de anotación**, que es la que tiene un ítem real detrás. `deal` se queda con su nota, que es correcta y útil.
2. **Si la dejás, hay que decidir la clase, no tener las dos.** `deal with something` dice `phrasal-inseparable` y `deal with` dice `verbo-preposicion`: el mismo verbo con dos clases es un chip distinto para la misma fila, y la fusión de Code tiene que elegir una de forma arbitraria. Avisá qué clase corresponde y uniformá las dos entradas. Tu propia nota dice que el caso es dudoso; elegí una y dejá la duda en `nota`, que para eso está.

**Ojo con el orden:** le pedí a Code que **no** resuelva estos desacuerdos y que **no** agregue una guarda que falle por ellos, así que esto no lo bloquea. Pero si lo arreglás antes de que Code termine, mejor.

## Tarea 2 — `hören`: confirmá o cambiá, pero que quede escrito donde se ve

El otro desacuerdo: el núcleo `hören` es `mit-Praposition` (por *auf seinen Körper hören*) y la entrada de referencia dice `simple`. Lo declaraste deliberado en el informe y en FALTANTES 72, y **el criterio me parece correcto**: la clase del verbo es `simple` y `hören auf` es una valencia secundaria.

Lo único que falta es que eso esté **en la entrada**, no solo en el informe: una `nota` en la entrada de referencia diciendo que el núcleo lleva `mit-Praposition` por `hören auf + Akk`. Dentro de seis meses nadie va a releer FALTANTES 72 antes de «arreglar» la discrepancia, y la fusión de Code la va a mostrar.

Si al mirarlo de nuevo te parece que la referencia debería llevar las dos clases, decilo y lo discutimos — pero entonces también hay que decidirlo para los otros verbos con valencia secundaria, y preferiría no abrir eso ahora.

## Tarea 3 — el cruce de los PDF ya está cerrado: no hace falta repetirlo

FALTANTES 72 deja como deuda «los dos `.pdf` con la capa de texto completa, para repetir el cruce sin huecos». **Esa deuda no existe: la cerré con la lista canónica de irregulares y de phrasal, y no hay ningún hueco.**

**Irregulares, la mitad que el PDF no muestra** (antes de `broadcast` y después de `teach`): 29 verbos cruzados. Presentes en la tabla: `bear`, `become`, `begin`, `bend`, `bite`, `blow`, `break`, `bring`, `tear`, `tell`, `think`, `throw`, `understand`, `wake`, `wear`, `weep`, `win`, `write`. Los de nivel B1 o más quedan fuera por la regla (`arise`, `beat`, `bet`, `bind`, `bleed`, `breed`, `weave`). **Ausentes de nivel A1/A2: tres, y los tres por buen motivo** — `be` es auxiliar (la regla del prompt 40), `wind` es A1 como sustantivo (el viento) igual que tu caso `spring`, y `awake` es marginal con `wake` ya en la tabla. **Cero huecos reales.**

**Phrasal, las dos páginas:** cruzadas las 111 de la página 1 y las 42 canónicas de la página 2. Con lema en `vocab_en.json`: 7 y 8 respectivamente, **todos B2 o C1** (`cut down`, `give up`, `run down`, `settle down`, `think over`, `wear off`, `wear out` en B2; `fall for`, `fall out`, `pick up`, `set off`, `stand by`, `stand for`, `stand out`, `stand up for` en C1). **Cero de nivel A1/A2, así que cero huecos.**

**Pero tu explicación de por qué era errónea, y conviene corregirla porque si no cierra mal la puerta.** Dijiste que «CEFR-J lematiza por palabra, así que *pick up* o *give in* no existen como entrada y no pueden tener nivel». **`vocab_en.json` tiene 1021 lemas con espacio**, entre ellos `according to`, `bus stop`, `check-in desk` y los quince phrasal de arriba. El motivo real es más acotado: **los phrasal verbs están poco representados en la lista, y los que están son todos de nivel alto.** La diferencia importa porque tu versión implicaría que el cruce contra una lista de phrasal nunca puede dar nada, y eso no es cierto: podría darlo en B1, que es la tanda que viene.

Lo que te pido acá es solo **corregir esas dos cosas en FALTANTES 72** (el motivo, y bajar la deuda de los PDF a cerrada con los números de arriba). No hay contenido nuevo que escribir.

## B1 sigue esperando, y el motivo cambió

En el prompt 40 el argumento era «si la forma está mal, se corrige con 200 entradas y no con 600». **La forma del JSON ya está validada** —las nueve comprobaciones de arriba—, así que ese argumento se agotó. El que queda es otro y es más fuerte:

**Nadie vio todavía una de estas entradas en la pantalla.** Las 499 no llegan a la app: `componer.py` no toca la tabla y la vista se construye desde los ítems de las fichas. Le escribí a Code la Ronda U para que las tome como segunda fuente y haga la fusión por infinitivo en el mismo commit (si no, 124 verbos saldrían con dos filas). **Hasta que eso esté y Fer mire la vista, no escribas B1.** Lo que puede aparecer ahí y hoy no se puede saber: cómo se lee una glosa de referencia al lado de una con colocaciones, si la falta de estrella se entiende o parece un error, y si 499 entradas nuevas en la lista alfabética la vuelven incómoda de recorrer. Cualquiera de las tres cambia cómo se escriben los 414 de B1.

Cuando Fer dé el visto, B1 es tuyo con el mismo método: el filtro verificable sobre `vocab_de.json`, la clasificación de todos los candidatos antes de escribir, y la cuenta que cierre.

## Qué no hacer

- No escribas B1 ni ninguna entrada nueva en esta vuelta.
- No toques los packs, los núcleos, las ocurrencias ni nada de `app/` — Code está trabajando en `app/build.gradle.kts`, `datos/`, `presentacion/Verbos.kt` y los tests, y vamos a chocar.
- No toques `aplicar_verbos.py`: quedó bien.
- No uniformes las decisiones del prompt 40 sobre la preposición regida (la que va en `forma` frente a la que va en `nota`): están bien separadas y son deliberadas.

## Lo de siempre

Un commit, nada de push, el parche en `outputs/verbos-arreglos/`, **commiteado por ruta explícita** — y esta vez **mandá el archivo del parche a la conversación**, que en la tanda 1 llegó solo el informe.
