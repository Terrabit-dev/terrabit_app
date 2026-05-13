package com.example.terrabit_app.utils

import com.example.terrabit_app.utils.SecureLog

object DateUtils {
    fun convertirFechaAFormatoAPI(fecha: String): String {
        return try {
            val partes = fecha.split("/")
            if (partes.size == 3) {
                val dia = partes[0]
                val mes = partes[1]
                val anio = partes[2]
                "$anio$mes$dia"
            } else {
                ""
            }
        } catch (e: Exception) {
            SecureLog.e("Error conversión fecha", e.message ?: "Error desconocido")
            ""
        }
    }
}