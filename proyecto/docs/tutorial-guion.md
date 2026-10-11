# Guion del tutorial por partes (borrador para una ronda futura)

Escrito por Overview el 11-oct-2026 a pedido de Fer, **antes** de que exista el código, para no inventarlo con la interfaz encima. El español es el idioma fuente; las traducciones a los otros 5 se hacen recién cuando el guion esté cerrado.

## Cómo funciona
- **Por partes, no de una.** Cada sección lanza su propio tour **la primera vez que se entra**. Ninguno pasa de 8 pasos. La suma son unos 25, pero nunca se ven juntos.
- **Se puede saltear**, y «Saltar» saltea **todos** los tours restantes, no solo el actual. Es seguro porque se pueden repetir.
- **Repetir:** un botón en Ajustes reinicia los contadores, y la próxima visita a cada sección vuelve a lanzar su tour.
- **Degrada sin romperse:** si un paso no encuentra lo que señala, se saltea y el tour sigue. Nunca oscurece la pantalla sin nada señalado, nunca crashea. Un test falla cuando un anclaje desaparece, para que la ruptura no sea silenciosa.
- **Idioma:** el primer arranque lo elige el usuario en la pantalla de bienvenida (la app no lee el idioma del sistema por su cuenta: regla dura 2). Al repetirlo desde Ajustes, va en el idioma de la app.
- **Pocos anclajes por tour.** Un paso puede explicar un grupo de secciones en vez de una por una.

## 0. Bienvenida (primer arranque, antes de la ficha) — no se saltea
Es una pantalla, no un tour con foco. Tres cosas, en este orden:
1. **Idioma de la app.** Sin esto la app no sabe en qué mostrarse.
2. **Qué idioma practicás y en qué nivel.**
3. **Qué es esto, y qué no es.** El texto más importante de la app:
   > LenguApp no enseña un idioma: te hace practicar el que ya estás aprendiendo. Cada semana te propone un tema, una habilidad y un desafío para producir, no para estudiar. Si estás empezando un nivel, hacé un curso de ese nivel; esta app es para que lo que aprendés ahí no se te quede en el cuaderno.

Este mensaje **también** vive en «Acerca de», en la guía de testers y en la descripción de la tienda: el tutorial se ve una vez y se saltea.

## 1. Tour de la ficha (primer arranque, 7 pasos)
La ficha es lo primero que se ve. Los pasos agrupan, no van sección por sección.
1. **Esta es tu semana.** Un tema y una habilidad, del lunes al domingo. No hace falta terminarla: hacé lo que puedas.
2. **La misión.** Lo que te pedimos que produzcas esta semana, con sus requisitos y sus micro-tareas si querés ir por partes.
3. **Para entender de qué va** (descripción, cuadro de referencia, ejemplos, notas). Es la explicación corta, no un capítulo de gramática.
4. **Lo que se compara y lo que falla** (contraste y errores típicos). Qué cambia respecto de tu idioma y en qué solemos equivocarnos.
5. **Vocabulario y expresiones.** Tocá la estrella para guardar lo que te sirva; lo vas a encontrar después en Guardados. *(Si las secciones arrancan plegadas, el paso tiene que abrirlas o explicar el encabezado, no señalar una caja cerrada: decisión abierta.)*
6. **El autochequeo.** Antes de darla por hecha, mirá si cumplís esto.
7. **Los prompts y la planilla.** Un texto listo para pegarle a una IA y que te corrija, y una hoja para imprimir y darle a quien te enseña. Van siempre en el idioma que estás aprendiendo, para que la respuesta también.

**Cierre del tour (1 paso más, sin foco):** arriba tenés Biblioteca (todo el vocabulario y las expresiones del año), Guardados (lo que marcaste con la estrella) y Ajustes. Cada una se explica sola cuando entres.

## 2. Tour de la Biblioteca (5 pasos)
1. **Tres vistas.** Vocabulario y expresiones, Habilidades y Verbos.
2. **Los filtros se arman solos** con lo que hay en tu idioma y tu nivel: si un filtro no aparece, es que no hay nada que filtrar.
3. **La búsqueda** no distingue mayúsculas ni tildes, y busca también en la traducción.
4. **Verbos.** Están todos los del idioma, de cualquier nivel, agrupados por verbo; debajo de algunos vas a ver las frases donde se usan. Buscá la raíz («stellen») para ver la familia entera.
5. **La estrella** hace lo mismo que en la ficha.

## 3. Tour de Guardados (3 pasos)
1. **Acá está lo que marcaste**, igual que se ve en la Biblioteca.
2. **Exportar a Anki o hacer una copia**, eligiendo vos el archivo.
3. **Importar** une lo que traés con lo que ya tenés, sin duplicar.

## 4. Tour de Ajustes (4 pasos)
1. **Idioma de la app y qué practicás.**
2. **Tema y tipografía.** Elegí lo que te resulte más cómodo de leer.
3. **Acerca de**, con lo que la app guarda y lo que no.
4. **Repetir el tutorial** desde acá, cuando quieras.

## 5. Desafío del fin de semana (2 pasos, solo sábados y domingos)
No tiene tour propio al entrar, porque el interruptor solo existe sábado y domingo y el tour se perdería durante semanas. Se lanza **la primera vez que el interruptor está visible**.
1. **Esto es para hablar**, no para escribir. La consigna está arriba y las condiciones debajo.
2. **El interruptor arranca apagado** cada vez que abrís la app: no se guarda nada.

## Lo que falta decidir
- Las secciones plegadas de la ficha: ¿el tour las abre o explica el encabezado?
- Primer arranque sin calendario para la semana en curso: la ficha puede estar vacía y el tour de la ficha no tendría qué señalar. ¿Se pospone al primer día con ficha?
- Dónde vive el estado («tutorial visto» por sección y «salteado» global): es un ajuste, no progreso, así que la regla dura 4 lo permite — pero **no** debe viajar en la copia de seguridad de Guardados.
- Si un tour se abandona a la mitad (botón atrás), ¿cuenta como visto?

## Costo estimado
Unos **25 pasos** entre todos los tours, más la bienvenida: con título y cuerpo en 6 idiomas son **~300 cadenas nuevas**. Es más texto que el tutorial de una sola vez, no menos; lo que mejora es el uso, no el costo. Suma a la deuda de fr/it/pt sin revisión de nativo. El anclaje toca 5 pantallas. Es una ronda del tamaño de la Ronda G.

## Cuándo
**Cuando la interfaz deje de moverse.** Anclar un tutorial a pantallas que cambian cada ronda es trabajo que se tira. La **bienvenida** (punto 0), en cambio, no tiene anclajes y se puede hacer en cualquier momento: es la parte de más valor y la más barata.
