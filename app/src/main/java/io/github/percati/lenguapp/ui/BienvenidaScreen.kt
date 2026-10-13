@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package io.github.percati.lenguapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.percati.lenguapp.modelo.Ajustes
import io.github.percati.lenguapp.modelo.Idioma
import io.github.percati.lenguapp.modelo.Nivel

const val TAG_BIENVENIDA = "bienvenida"
const val TAG_BIENVENIDA_EMPEZAR = "bienvenida-empezar"

/**
 * Pantalla de bienvenida (Ronda N; punto 0 de docs/tutorial-guion.md). Aparece solo la primera vez (o cuando se pide
 * verla de nuevo desde Ajustes) y hace tres cosas, en este orden:
 *  1. Elegir el idioma de la app. Obligatorio: hasta que se elige, no se ve nada mas ni se puede seguir. La app NO lee el
 *     idioma del sistema para preseleccionar nada (regla dura 2): ningun chip arranca marcado; solo si el usuario toca
 *     «Idioma del sistema» se lee, que es la excepcion que la regla ya contempla. La eleccion se aplica de inmediato.
 *  2. Que idioma practica y en que nivel: el mismo selector de Ajustes.
 *  3. Que es esto y que no es (el texto mas importante de la app), a la vista, no detras de un «leer mas».
 *
 * `yaEligioIdioma` = true cuando es una repeticion desde Ajustes (el idioma ya fue elegido una vez): se muestra todo de entrada.
 */
@Composable
fun BienvenidaScreen(
    ajustes: Ajustes,
    idiomasConContenido: Set<Idioma>,
    nivelesConContenido: Map<Idioma, Set<Nivel>>,
    idiomaAplicacionEfectivo: Idioma,
    onAjustesCambiados: (Ajustes) -> Unit,
    onEmpezar: () -> Unit,
    modifier: Modifier = Modifier,
    yaEligioIdioma: Boolean = false,
) {
    var eligioIdioma by rememberSaveable { mutableStateOf(yaEligioIdioma) }
    val idioma = idiomaAplicacionEfectivo

    Column(
        modifier = modifier.fillMaxSize().testTag(TAG_BIENVENIDA).verticalScroll(rememberScrollState()).finDeScrollSobreBarras().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(NOMBRE_APP, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            if (eligioIdioma) Text(etiquetaBienvenidaTitulo(idioma), style = MaterialTheme.typography.titleMedium)
        }

        // 1. Idioma de la app. Antes de elegir, el rotulo es el mismo en los 6 idiomas (la app aun no sabe en cual hablarte)
        //    y ningun chip esta marcado.
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                if (eligioIdioma) etiquetaIdiomaApp(idioma) else etiquetaBienvenidaIdiomaInicial(idioma),
                style = MaterialTheme.typography.titleMedium,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = eligioIdioma && ajustes.idiomaSegunSistema,
                    onClick = {
                        eligioIdioma = true
                        onAjustesCambiados(ajustes.copy(idiomaSegunSistema = true))
                    },
                    label = { Text(etiquetaSegunSistema(idioma)) },
                )
                Idioma.entries.forEach { i ->
                    FilterChip(
                        selected = eligioIdioma && !ajustes.idiomaSegunSistema && i == ajustes.idiomaInterfaz,
                        onClick = {
                            eligioIdioma = true
                            onAjustesCambiados(ajustes.copy(idiomaBase = i, idiomaInterfaz = i, idiomaSegunSistema = false))
                        },
                        // cada idioma escrito en SU propio idioma
                        label = { Text(nombreIdioma(i, i)) },
                    )
                }
            }
        }

        if (eligioIdioma) {
            // 2. Que practica y en que nivel (el mismo componente que Ajustes).
            SelectorIdiomasAprendidos(
                idiomasAprendidos = ajustes.idiomasAprendidos,
                idiomasConContenido = idiomasConContenido,
                nivelesConContenido = nivelesConContenido,
                idiomaInterfaz = idioma,
                onCambiar = { onAjustesCambiados(ajustes.copy(idiomasAprendidos = it)) },
            )

            // 3. Que es esto y que no es: prosa de cuerpo, a la vista.
            Text(textoAcercaQueEs(idioma), style = MaterialTheme.typography.bodyLarge)

            val puedeEmpezar = ajustes.idiomasAprendidos.isNotEmpty()
            if (!puedeEmpezar) {
                Text(
                    etiquetaBienvenidaElegirPractica(idioma),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onEmpezar,
                enabled = puedeEmpezar,
                modifier = Modifier.fillMaxWidth().testTag(TAG_BIENVENIDA_EMPEZAR),
            ) { Text(etiquetaBienvenidaEmpezar(idioma)) }
        }
    }
}
