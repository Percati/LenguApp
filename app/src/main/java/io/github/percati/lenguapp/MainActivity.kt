package io.github.percati.lenguapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.percati.lenguapp.datos.resolverSemana
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.ui.PantallaSemana
import io.github.percati.lenguapp.ui.TemaLenguApp

/**
 * Pantalla unica. El selector de idioma/nivel llega en la Fase 4; por ahora
 * alemán B2 es el valor por defecto (uno de los dos combos con contenido).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idiomaBase = Idioma.ES
        val resultado = resolverSemana(this, idioma = Idioma.DE, nivel = Nivel.B2)
        setContent {
            LenguAppScreen(resultado = resultado, idiomaBase = idiomaBase)
        }
    }
}

@Composable
private fun LenguAppScreen(
    resultado: ResultadoSemana,
    idiomaBase: Idioma,
) {
    TemaLenguApp {
        Surface(modifier = Modifier.fillMaxSize()) {
            PantallaSemana(resultado = resultado, idiomaBase = idiomaBase)
        }
    }
}
