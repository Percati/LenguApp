package io.github.percati.lenguapp.ui

import io.github.percati.lenguapp.modelo.Tour

/**
 * Los pasos de los cuatro tours y el del desafio (Ronda Q), en el orden y con el contenido de docs/tutorial-guion.md. Cada paso
 * nombra su ancla (un testTag que pone `anclaTour` en su pantalla); un test comprueba que todas existen. Ningun tour pasa de 8 pasos.
 * El ultimo paso del tour de la ficha lleva, ademas, el texto de «que es esto y que no es» (ACERCA_QUE_ES_TEXTO), el mismo de la
 * bienvenida y de «Acerca de», sin reescribir: es la unica redundancia deliberada de la app.
 */
val CATALOGO_TOURS: Map<Tour, List<PasoTour>> = mapOf(
    Tour.FICHA to listOf(
        PasoTour("tour-ficha-titulo", ClaveTexto.TOUR_FICHA_1_T, ClaveTexto.TOUR_FICHA_1_C),
        PasoTour("tour-ficha-mision", ClaveTexto.TOUR_FICHA_2_T, ClaveTexto.TOUR_FICHA_2_C),
        PasoTour("tour-ficha-descripcion", ClaveTexto.TOUR_FICHA_3_T, ClaveTexto.TOUR_FICHA_3_C),
        PasoTour("tour-ficha-errores", ClaveTexto.TOUR_FICHA_4_T, ClaveTexto.TOUR_FICHA_4_C),
        PasoTour("tour-ficha-vocabulario", ClaveTexto.TOUR_FICHA_5_T, ClaveTexto.TOUR_FICHA_5_C),
        PasoTour("tour-ficha-autochequeo", ClaveTexto.TOUR_FICHA_6_T, ClaveTexto.TOUR_FICHA_6_C),
        PasoTour("tour-ficha-prompt", ClaveTexto.TOUR_FICHA_7_T, ClaveTexto.TOUR_FICHA_7_C),
        PasoTour("tour-barra-superior", ClaveTexto.TOUR_FICHA_8_T, ClaveTexto.TOUR_FICHA_8_C),
    ),
    Tour.BIBLIOTECA to listOf(
        PasoTour("tour-biblioteca-vistas", ClaveTexto.TOUR_BIBLIOTECA_1_T, ClaveTexto.TOUR_BIBLIOTECA_1_C),
        PasoTour("tour-biblioteca-filtros", ClaveTexto.TOUR_BIBLIOTECA_2_T, ClaveTexto.TOUR_BIBLIOTECA_2_C),
        PasoTour("tour-biblioteca-buscar", ClaveTexto.TOUR_BIBLIOTECA_3_T, ClaveTexto.TOUR_BIBLIOTECA_3_C),
        PasoTour("tour-biblioteca-vistas", ClaveTexto.TOUR_BIBLIOTECA_4_T, ClaveTexto.TOUR_BIBLIOTECA_4_C),
        PasoTour("tour-biblioteca-estrella", ClaveTexto.TOUR_BIBLIOTECA_5_T, ClaveTexto.TOUR_BIBLIOTECA_5_C),
    ),
    Tour.GUARDADOS to listOf(
        PasoTour("tour-guardados-lista", ClaveTexto.TOUR_GUARDADOS_1_T, ClaveTexto.TOUR_GUARDADOS_1_C),
        PasoTour("tour-guardados-archivo", ClaveTexto.TOUR_GUARDADOS_2_T, ClaveTexto.TOUR_GUARDADOS_2_C),
        PasoTour("tour-guardados-archivo", ClaveTexto.TOUR_GUARDADOS_3_T, ClaveTexto.TOUR_GUARDADOS_3_C),
    ),
    Tour.AJUSTES to listOf(
        PasoTour("tour-ajustes-idiomas", ClaveTexto.TOUR_AJUSTES_1_T, ClaveTexto.TOUR_AJUSTES_1_C),
        PasoTour("tour-ajustes-tema", ClaveTexto.TOUR_AJUSTES_2_T, ClaveTexto.TOUR_AJUSTES_2_C),
        PasoTour("acerca-de", ClaveTexto.TOUR_AJUSTES_3_T, ClaveTexto.TOUR_AJUSTES_3_C),
        PasoTour("ver-bienvenida", ClaveTexto.TOUR_AJUSTES_4_T, ClaveTexto.TOUR_AJUSTES_4_C),
    ),
    Tour.DESAFIO to listOf(
        PasoTour("tour-desafio-consigna", ClaveTexto.TOUR_DESAFIO_1_T, ClaveTexto.TOUR_DESAFIO_1_C),
        PasoTour("tour-desafio-interruptor", ClaveTexto.TOUR_DESAFIO_2_T, ClaveTexto.TOUR_DESAFIO_2_C),
    ),
)

const val MAX_PASOS_POR_TOUR = 8
