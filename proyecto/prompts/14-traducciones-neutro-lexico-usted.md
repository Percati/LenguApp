# Prompt para Traducciones — español neutro, parte 2 (léxico y «usted»)

Leé `CLAUDE.md`, `REGLAS-PREVENCION.md` (P2, P5), `FALTANTES.md` 6.1, 6.1.1 y 23, y tu propio informe del prompt 12. Trabajás sobre `proyecto/contenido/` (nunca `build/` ni `assets/`; Overview regenera los assets). No pushees. Decisiones de Fer del 10-oct-2026:
- **Léxico:** español neutro con una **lista corta** de sustituciones (no se reescribe el léxico entero).
- **Tratamiento:** **tú en todo**, también en la prosa de las fichas de alemán (misión, microtareas, retos, autochequeos). Ni «usted» ni «vosotros».

## Tarea 1 — «usted» → «tú» (solo valores bajo clave `es`)
En el contenido compuesto hay ~438 imperativos de «usted» en ~145 archivos (`Reescriba` 120, `Compruebe` 63, `Revise` 61, `Escriba` 52, `Hable` 50, `Describa` 22, `Use`, `Haga`, `Responda`, `Anote`, `Explique`, `Indique`, `Tenga`, `Compare`, `Repita`, `Ordene`, `Subraye`, `Mire`, `Marque`, `Reformule`, `Formule`…). Casi seguro vienen de pocas cadenas fuente repetidas en núcleos/plantillas: buscá la **fuente**, no el compuesto, y corregí ahí. Convertí también pronombres y posesivos asociados («le», «su», «se» reflexivo de cortesía → «te», «tu»). Revisá cada caso: un imperativo de cortesía NO es lo mismo que una cita en una frase de ejemplo en la que el usted es parte del contenido (p. ej. un Redemittel formal alemán con «Sie» cuya traducción debe conservar el registro formal): ahí **no se toca**, se lista como excepción documentada.

## Tarea 2 — Lista corta de léxico peninsular
Medido hoy en `es`: `piso` (37+3), `billete(s)` (36+24), `aparcar` (9), `coche` (8), `ordenador` (6), `móvil` (6), `camarero` (3), `tío/tía` (4), `coger confianza` (1). **No cambies `vale`** (es el verbo valer) ni `acá/allá`.
Proponé en tu informe, **antes de aplicar**, una tabla término → sustituto con una línea de justificación. Punto de partida (corregilo si lo ves mal): `piso` como vivienda→`apartamento` (si es «planta» de un edificio, «piso» es válido en toda Latinoamérica y se deja); `coche`→`auto`; `ordenador`→`computadora`; `móvil`→`celular`; `aparcar`→`estacionar`; `billete`: distinguí **dinero** («billete» sirve en toda Latinoamérica, dejalo) de **pasaje/entrada** («pasaje» o «boleto» según contexto, el más entendido); `camarero`: si no hay término neutro, dejalo y documentalo; `tío/tía` coloquial («un tío», «tía») → «tipo», «persona», «mujer» según el sentido; `coger confianza` → «ganar confianza». Reglas:
- Cambiá solo glosas y ejemplos en `es`; el alemán/inglés queda intacto.
- Si un cambio altera una explicación (p. ej. el contraste «Flat/Wohnung/piso»), releé la cadena entera.
- Donde no haya sustituto neutro, **dejá el término y listalo** en FALTANTES 6.1.1 en vez de forzar.

## Tarea 3 — Que no vuelva a pasar (verificador)
Ampliá `tools/verificar_consistencia.py` con la regla **R7**, sin tocar R1-R6: (a) imperativos de «usted» de la lista de la tarea 1 más sus formas frecuentes, solo sobre valores bajo clave `es` y con su lista de excepciones explícita; (b) la lista corta de léxico peninsular de la tarea 2, con la misma lista de excepciones. Probala con el método del prompt 12: **demostrá que detecta el 100 % de lo que cambiaste y que no marca falsos positivos** en el resto (un chequeo que da 0 porque está ciego no sirve). Cuidado con formas ambiguas con el pretérito o el subjuntivo («use» puede ser «yo use»). Verificá que `verificarConsistenciaContenido` sigue en verde con R7 incluida.

## Verificación obligatoria
1. `componer.py` → 606 fichas, 56 semanas especiales, 0 con problemas.
2. `verificar_consistencia.py`: R1-R7 en 0.
3. Script sobre todo el diff: todas las cadenas cambiadas cuelgan de una clave `es`; mismos « » y `*`; sin `ß`; estructura JSON idéntica; ningún archivo cambiado salvo contenido y el verificador. `validar_packs` sin problemas.
4. Barrido independiente, no solo R7: buscá más «usted» (`-e/-a` de cortesía, «su», «le», «sírvase») y más peninsulismos (`vosotros`, `os`, `vuestro`, `grifo`, `zumo`, `nevera`, `currar`, `guay`, `mogollón`, `chaval`) y reportá lo que encuentres.

## Informe
Tabla de léxico aplicada, excepciones de «usted» que dejaste y por qué, 15 antes/después, casos de duda, hallazgos del barrido. Actualizá `FALTANTES.md` 6.1.1 (cerrar «usted/tú» y la lista corta; dejar abierto solo lo que no tenga sustituto neutro).
