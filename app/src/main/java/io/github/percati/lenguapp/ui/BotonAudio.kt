package io.github.percati.lenguapp.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.audio.ClaseAudio
import io.github.percati.lenguapp.audio.LocalControlAudio
import io.github.percati.lenguapp.modelo.Idioma

const val TAG_BOTON_AUDIO = "boton-audio"

/**
 * Icono de altavoz, discreto, SOLO si ese texto tiene clip en el indice (si no, no dibuja nada: nunca un boton que
 * no suena). Mientras suena cambia a "detener" y se tine con el acento. El area tactil es de 48 dp aunque el icono
 * mida `tamano` (IconButton la amplia, igual que la estrella). Un solo audio a la vez; tocarlo de nuevo lo corta.
 */
@Composable
fun BotonAudio(idioma: Idioma, clase: ClaseAudio, texto: String, idiomaInterfaz: Idioma, tamano: Dp = 40.dp) {
    val control = LocalControlAudio.current
    val ruta = control.ruta(idioma, clase, texto) ?: return
    val suena = control.sonando == ruta
    IconButton(onClick = { control.alternar(ruta) }, modifier = Modifier.size(tamano).testTag(TAG_BOTON_AUDIO)) {
        Icon(
            if (suena) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = if (suena) descripcionAudioDetener(idiomaInterfaz) else descripcionAudioEscuchar(idiomaInterfaz),
            tint = if (suena) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
    }
}

/** Corta el audio cuando la pantalla sale de la composicion (o cambia `clave`): nada suena en una pantalla que ya no esta. */
@Composable
fun DetenerAudioAlSalir(clave: Any? = Unit) {
    val control = LocalControlAudio.current
    DisposableEffect(control, clave) {
        onDispose { control.detener() }
    }
}
