package com.example.monitoreossilos.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class CameraHelper(private val context: Context) {

    fun crearArchivoFotoTemporal(): Uri {
        val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
        val archivo = File.createTempFile("SILO_INSPECCION_", ".jpg", carpeta)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )
    }

    fun tienePermisoCamara(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
}