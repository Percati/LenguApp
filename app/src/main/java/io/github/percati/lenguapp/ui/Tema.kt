package io.github.percati.lenguapp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Sin marca propia todavia; M3 por defecto alcanza para que el contenido se lea bien. */
@Composable
fun TemaLenguApp(content: @Composable () -> Unit) {
    val esquema = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = esquema, content = content)
}
