# Reglas de trabajo aprendidas

Cosas descubiertas trabajando en este proyecto que vale la pena no volver a
aprender por las malas. No son reglas del contenido (eso va en `CLAUDE.md` y la
memoria de proyecto de Claude) — son sobre el proceso de construirlo.

- **Subir siempre CSV, nunca XLSX a herramientas de conocimiento de proyecto.**
  Algunos conversores truncan `.xlsx` en 1.000 filas por hoja y casi no avisan.
- **Ninguna lista con copyright entra al repositorio.** Se distribuye el script y
  la URL de origen, nunca los datos (Goethe, Oxford, Cambridge, etc.).
- **Un verificador automático no es un veredicto.** Una auditoría dio "0
  problemas" y había errores reales; salieron recién con consultas dirigidas.
- **Los campos vacíos son mejores que los campos ausentes.** Un hueco visible se
  llena; uno invisible se olvida.
- **Revalidar el conjunto completo después de cualquier corrección, no solo lo
  que tocaste.** Corregir una transición de solapamiento entre packs puede
  romper la siguiente (efecto dominó) — pasó más de una vez en esta sesión.
  Nunca dar algo por cerrado sin correr la validación de punta a punta de nuevo.
- **Antes de dar un recuento por bueno, verificarlo contra los archivos reales.**
  Varias veces en esta sesión se asumió que algo faltaba (calendarios 2027,
  núcleos de inglés C2, packs de alemán C2) y ya estaba hecho de una sesión
  anterior que no se tenía completamente presente. El listado de archivos manda,
  no el recuerdo de la conversación.
- **Dos fuentes de verdad para lo mismo divergen sin avisar.** Pasó con el
  calendario del piloto (escrito a mano vs. generado) — se resolvió fijando el
  piloto en `data/semanas-fijas.json`, no arbitrando cuál tenía razón.
- **Los huecos de cobertura y los errores de nivel son problemas distintos.**
  Los errores de nivel se concentran arriba (C1/C2, donde cada fuente
  improvisa). Los huecos de cobertura aparecen en cualquier nivel.
- **Un prompt no puede contradecir al schema.** El prompt de packs pedía
  `prioridad: "variante"` mientras el schema solo acepta `"nucleo"`/`"ampliacion"`,
  y `variante` ya era otro campo (regionalismos). Salieron 129 packs inválidos que
  nadie detectó porque la validación del schema recién corre al componer. Corregido
  el 15-09-2026; desde entonces `tools/validar_packs.py` chequea el enum en origen.
- **Escribir un pack traduciendo el del otro idioma produce contenido inútil.** 21 packs
  de inglés (5 en B2, 16 en C1) eran calco ítem por ítem del pack alemán del mismo tema:
  inglés gramatical pero no idiomático, justo lo contrario del objetivo de producción oral.
  Se detecta cruzando los `item` de un pack con las `traducciones[de]` del otro: >=60 % de
  coincidencia es señal de calco. Reescritos en sept-2026; regla documentada en el prompt.
- **La lista de alemán verifica poco del contenido C1, y eso es esperable.** De los 672
  ítems de DE C1, solo 84 se verifican directamente: 245 son expresiones de varias
  palabras (no están en una lista de lemas) y 143 son compuestos cuyo núcleo sí figura
  (`Arbeitsbelastung` → `Belastung`). La lista sale de Lernwortschätze de manuales, que
  listan lemas simples, no compuestos ni colocaciones. Sirve para descartar nivel
  equivocado, no como fuente de la que extraer packs. Al verificar hay que normalizar
  ß→ss: la lista viene con ss (`einfliessen`, `grossen`).
- **DE B2 y DE C2 no tienen lista oficial contra la que verificar** (el Goethe no publica
  Wortliste por encima de B1). Los packs de esos pares van sin verificación externa.
- **El `nivel` de `vocab_de.json` es el nivel más bajo en que la palabra aparece entre las
  fuentes cargadas, no una etiqueta CEFR canónica.** `der Arzt` y `die Eltern` figuran
  como B1 porque solo están en goethe-b1 y dtz: la tajada A2 del repo (787 lemas) es
  menor que la Wortliste A2 real, así que hay vocabulario A2 genuino que la lista solo
  tiene en su versión B1. Conclusión práctica: "por encima del nivel" en A2 significa
  "no está en la fuente A2 cargada", NO "es demasiado difícil". No sirve para descartar
  ítems de un pack A2 de forma automática; sí sirve en sentido inverso (si algo aparece
  como C1, conviene mirarlo).
