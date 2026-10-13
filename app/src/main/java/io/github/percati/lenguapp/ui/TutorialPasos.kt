package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Tour

/**
 * Los pasos de los cuatro tours y el del desafio (Ronda Q), en el orden y con el contenido de docs/tutorial-guion.md. Cada paso
 * nombra su ancla (un testTag que pone `anclaTour` en su pantalla); un test comprueba que todas existen. Ningun tour pasa de 10 pasos.
 * (El texto de «que es esto y que no es» vive en la bienvenida y en «Acerca de»; el cierre del tour de la ficha ya no lo repite.)
 */
val CATALOGO_TOURS: Map<Tour, List<PasoTour>> = mapOf(
    // En el orden de la pantalla, de arriba hacia abajo. Excepcion deliberada: el interruptor de traducir esta encima del titulo (en la
    // franja fija) pero va en el paso 2, para abrir el tour con «Esta es tu semana» y no con un interruptor que solo existe en A2 y B1.
    // El cierre senala la barra de arriba (se ve siempre). Los pasos 2 y 9 se saltean solos si su ancla no existe (B2+, sin planilla).
    Tour.FICHA to listOf(
        PasoTour("tour-ficha-titulo", ClaveTexto.TOUR_FICHA_1_T, ClaveTexto.TOUR_FICHA_1_C),
        PasoTour("tour-ficha-traducir", ClaveTexto.TOUR_FICHA_2_T, ClaveTexto.TOUR_FICHA_2_C),
        PasoTour("tour-ficha-descripcion", ClaveTexto.TOUR_FICHA_3_T, ClaveTexto.TOUR_FICHA_3_C),
        PasoTour("tour-ficha-errores", ClaveTexto.TOUR_FICHA_4_T, ClaveTexto.TOUR_FICHA_4_C),
        PasoTour("tour-ficha-vocabulario", ClaveTexto.TOUR_FICHA_5_T, ClaveTexto.TOUR_FICHA_5_C),
        PasoTour("tour-ficha-mision", ClaveTexto.TOUR_FICHA_6_T, ClaveTexto.TOUR_FICHA_6_C),
        PasoTour("tour-ficha-autochequeo", ClaveTexto.TOUR_FICHA_7_T, ClaveTexto.TOUR_FICHA_7_C),
        PasoTour("tour-ficha-prompt", ClaveTexto.TOUR_FICHA_8_T, ClaveTexto.TOUR_FICHA_8_C),
        PasoTour("tour-ficha-planilla", ClaveTexto.TOUR_FICHA_9_T, ClaveTexto.TOUR_FICHA_9_C),
        PasoTour("tour-barra-superior", ClaveTexto.TOUR_FICHA_10_T, ClaveTexto.TOUR_FICHA_10_C),
    ),
    Tour.BIBLIOTECA to listOf(
        PasoTour("tour-biblioteca-vistas", ClaveTexto.TOUR_BIBLIOTECA_1_T, ClaveTexto.TOUR_BIBLIOTECA_1_C),
        PasoTour("tour-biblioteca-filtros", ClaveTexto.TOUR_BIBLIOTECA_2_T, ClaveTexto.TOUR_BIBLIOTECA_2_C),
        PasoTour("tour-biblioteca-buscar", ClaveTexto.TOUR_BIBLIOTECA_3_T, ClaveTexto.TOUR_BIBLIOTECA_3_C),
        PasoTour("tour-biblioteca-vista-verbos", ClaveTexto.TOUR_BIBLIOTECA_4_T, ClaveTexto.TOUR_BIBLIOTECA_4_C),
        PasoTour("tour-biblioteca-estrella", ClaveTexto.TOUR_BIBLIOTECA_5_T, ClaveTexto.TOUR_BIBLIOTECA_5_C),
    ),
    Tour.GUARDADOS to listOf(
        PasoTour("tour-guardados-lista", ClaveTexto.TOUR_GUARDADOS_1_T, ClaveTexto.TOUR_GUARDADOS_1_C),
        // Exportar e importar, en un solo paso (los dos botones comparten el ancla).
        PasoTour("tour-guardados-archivo", ClaveTexto.TOUR_GUARDADOS_2_T, ClaveTexto.TOUR_GUARDADOS_2_C),
    ),
    Tour.AJUSTES to listOf(
        PasoTour("tour-ajustes-idiomas", ClaveTexto.TOUR_AJUSTES_1_T, ClaveTexto.TOUR_AJUSTES_1_C),
        PasoTour("tour-ajustes-tema", ClaveTexto.TOUR_AJUSTES_2_T, ClaveTexto.TOUR_AJUSTES_2_C),
        PasoTour("acerca-de", ClaveTexto.TOUR_AJUSTES_3_T, ClaveTexto.TOUR_AJUSTES_3_C),
        PasoTour("ver-bienvenida", ClaveTexto.TOUR_AJUSTES_4_T, ClaveTexto.TOUR_AJUSTES_4_C),
    ),
    Tour.DESAFIO to listOf(
        // El interruptor primero: la consigna solo existe con el interruptor ENCENDIDO y el tour no lo enciende (encenderlo seria la app
        // aceptando el desafio en lugar del usuario: regla dura 13). Con el interruptor apagado el paso 2 se saltea solo.
        PasoTour("tour-desafio-interruptor", ClaveTexto.TOUR_DESAFIO_1_T, ClaveTexto.TOUR_DESAFIO_1_C),
        PasoTour("tour-desafio-consigna", ClaveTexto.TOUR_DESAFIO_2_T, ClaveTexto.TOUR_DESAFIO_2_C),
    ),
)

/** Tope definitivo (Ronda R): diez pasos ya es un tour largo. Si alguna vez hace falta un paso 11, se parte el tour, no se sube el tope. */
const val MAX_PASOS_POR_TOUR = 10
