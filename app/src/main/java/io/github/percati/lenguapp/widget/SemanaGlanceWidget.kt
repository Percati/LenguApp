package io.github.percati.lenguapp.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.percati.lenguapp.MainActivity
import io.github.percati.lenguapp.datos.cargarAjustes
import io.github.percati.lenguapp.datos.resolverSemana
import io.github.percati.lenguapp.modelo.Ficha
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.SemanaEspecial
import io.github.percati.lenguapp.presentacion.idiomaAplicacionEfectivo
import io.github.percati.lenguapp.semana.RazonSinContenido
import io.github.percati.lenguapp.semana.ResultadoSemana
import io.github.percati.lenguapp.ui.mensajeSinCalendario
import io.github.percati.lenguapp.ui.mensajeSinContenidoNivel
import io.github.percati.lenguapp.ui.mensajeSinContenidoSemana
import io.github.percati.lenguapp.ui.textoSinMarcado
import java.util.Locale

/**
 * Modo revista tambien en el widget: no guarda nada propio, lee los mismos
 * ajustes persistidos (idioma/nivel, no progreso) y resuelve la semana con
 * la misma funcion que la pantalla principal, cada vez que el sistema pide
 * actualizarlo.
 */
class SemanaGlanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val ajustes = cargarAjustes(context)
        // El widget muestra un solo idioma: el primero de los seleccionados,
        // en el mismo orden estable (declaracion del enum) que las pestañas
        // de la pantalla principal. Si no hay ninguno, no hay nada que
        // resolver -- no es un ResultadoSemana.SinContenido (eso es sobre
        // disponibilidad de calendario, esto es sobre preferencia vacia).
        val primerIdioma = ajustes.idiomasAprendidos.keys.minByOrNull { it.ordinal }
        val resultado = primerIdioma?.let { idioma ->
            resolverSemana(context, idioma, ajustes.idiomasAprendidos.getValue(idioma))
        }
        // Misma resolucion que MainActivity.kt: "segun el sistema" solo lee
        // el idioma del dispositivo si el usuario activo esa opcion
        // (CLAUDE.md, regla dura #2 enmendada).
        val codigoIdiomaSistema = if (ajustes.idiomaSegunSistema) Locale.getDefault().language else null
        val idiomaInterfaz = idiomaAplicacionEfectivo(ajustes, codigoIdiomaSistema)

        provideContent {
            GlanceTheme {
                ContenidoWidget(resultado, idiomaInterfaz)
            }
        }
    }
}

@Composable
private fun ContenidoWidget(resultado: ResultadoSemana?, idiomaInterfaz: Idioma) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(12.dp)
            .clickable(actionStartActivity(Intent(context, MainActivity::class.java))),
    ) {
        when (resultado) {
            is ResultadoSemana.Encontrado -> {
                val (titulo, subtitulo) = when (val contenido = resultado.contenido) {
                    is Ficha -> contenido.titulo to contenido.subtitulo
                    is SemanaEspecial -> contenido.titulo to null
                }
                Text(
                    text = titulo,
                    style = TextStyle(fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onBackground),
                )
                if (!subtitulo.isNullOrBlank()) {
                    Text(text = textoSinMarcado(subtitulo), style = TextStyle(color = GlanceTheme.colors.onBackground))
                }
            }
            is ResultadoSemana.SinContenido -> Text(
                text = mensajeSinContenido(resultado, idiomaInterfaz),
                style = TextStyle(color = GlanceTheme.colors.onBackground),
            )
            null -> Text(
                text = "Elegí un idioma en Ajustes.",
                style = TextStyle(color = GlanceTheme.colors.onBackground),
            )
        }
    }
}

/** AJUSTES-FASE-9.md, bloque B: mismas plantillas que la pantalla principal (TextosInterfaz.kt), no un texto propio. */
private fun mensajeSinContenido(sinContenido: ResultadoSemana.SinContenido, idiomaInterfaz: Idioma): String = when (sinContenido.razon) {
    RazonSinContenido.ANIO_SIN_CALENDARIO -> mensajeSinCalendario(idiomaInterfaz, sinContenido.semanaIso.anio)
    RazonSinContenido.IDIOMA_O_NIVEL_SIN_CONTENIDO -> mensajeSinContenidoNivel(idiomaInterfaz, sinContenido.idioma, sinContenido.nivel)
    RazonSinContenido.SEMANA_FUERA_DE_LA_EDICION -> mensajeSinContenidoSemana(idiomaInterfaz, sinContenido.semanaIso.anio)
}
