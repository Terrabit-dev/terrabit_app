package com.example.terrabit_app.utils.porcinos

import android.util.Log

object GtrDateFormatter {

    /**
     * Une la fecha (DD/MM/YYYY) y la hora (HH:MM) para la API (YYYYMMDDHHMM)
     */
    fun toApiFormat(fecha: String, hora: String): String {
        return try {
            if (fecha.contains("/") && hora.contains(":")) {
                val partesFecha = fecha.split("/")
                val partesHora = hora.split(":")

                if (partesFecha.size == 3 && partesHora.size == 2) {
                    val dia = partesFecha[0]
                    val mes = partesFecha[1]
                    val anio = partesFecha[2]
                    val horas = partesHora[0]
                    val minutos = partesHora[1]
                    "$anio$mes$dia$horas$minutos"
                } else ""
            } else ""
        } catch (e: Exception) {
            Log.e("GTR_Conv", "Error en conversión API: ${e.message}")
            ""
        }
    }

    /**
     * Extrae la fecha visual (DD/MM/YYYY) de un String de la API
     */
    fun toVisualDate(apiDate: String): String {
        return if (apiDate.length >= 8) {
            "${apiDate.substring(6, 8)}/${apiDate.substring(4, 6)}/${apiDate.substring(0, 4)}"
        } else ""
    }

    /**
     * Extrae la hora visual (HH:MM) de un String de la API
     */
    fun toVisualTime(apiDate: String): String {
        return if (apiDate.length >= 12) {
            "${apiDate.substring(8, 10)}:${apiDate.substring(10, 12)}"
        } else ""
    }
}