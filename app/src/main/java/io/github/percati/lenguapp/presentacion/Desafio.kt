package io.github.percati.lenguapp.presentacion

/**
 * Encabezado del desafio del fin de semana (Ronda K, tarea 2): la consigna de la mision de la ficha, reencuadrada
 * como tarea oral por la linea que la precede. Si la ficha no trae consigna, el bloque no queda sin encabezado:
 * cae a la descripcion y, como ultimo recurso, al titulo de la ficha.
 */
fun consignaDelDesafio(consigna: String, descripcion: String, titulo: String): String =
    listOf(consigna, descripcion, titulo).firstOrNull { it.isNotBlank() }?.trim().orEmpty()
