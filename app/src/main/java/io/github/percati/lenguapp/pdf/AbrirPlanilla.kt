package io.github.percati.lenguapp.pdf

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import io.github.percati.lenguapp.presentacion.PlanillaContenido
import java.io.File

private const val CARPETA_CACHE_PLANILLAS = "planillas"

/**
 * Escribe la planilla en la cache de la app (`cacheDir/planillas/`): no hace
 * falta ningun permiso de almacenamiento, y el sistema puede limpiarla cuando
 * quiera -- se regenera en cada pedido. Nada sale del dispositivo.
 */
fun escribirPlanillaEnCache(context: Context, id: String, contenido: PlanillaContenido): File {
    val carpeta = File(context.cacheDir, CARPETA_CACHE_PLANILLAS).apply { mkdirs() }
    val archivo = File(carpeta, "planilla-$id.pdf")
    archivo.outputStream().use { PlanillaPdf.escribir(contenido, it) }
    return archivo
}

/**
 * Entrega el PDF al lector del sistema (que ofrece guardar/compartir) via
 * `FileProvider` (autoridad `<applicationId>.fileprovider`, solo `cache/planillas/`).
 * Si no hay ninguna app que abra PDF, cae a un "compartir" generico. Devuelve
 * false si tampoco hay con quien compartirlo.
 */
fun abrirPlanilla(context: Context, archivo: File): Boolean {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    val ver = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return try {
        context.startActivity(ver)
        true
    } catch (e: ActivityNotFoundException) {
        val compartir = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(Intent.createChooser(compartir, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            true
        } catch (e2: ActivityNotFoundException) {
            false
        }
    }
}
