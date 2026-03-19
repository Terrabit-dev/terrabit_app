package com.example.terrabit_app.utils

import kotlin.math.*

// Utility para Ubicacion, se usa para pantalla Fallecimiento
// Utility de conversion de lat/lon a UTM

object LocationUtils {

    fun latLonToUTM(lat: Double, lon: Double): Pair<String, String> {
        val a = 6378137.0
        val e2 = 0.00669437999014
        val k0 = 0.9996

        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)

        val zoneNumber = ((lon + 180) / 6).toInt() + 1
        val lonOriginRad = Math.toRadians((zoneNumber - 1) * 6 - 180 + 3.0)

        val N = a / sqrt(1 - e2 * sin(latRad).pow(2))
        val T = tan(latRad).pow(2)
        val C = e2 / (1 - e2) * cos(latRad).pow(2)
        val A = cos(latRad) * (lonRad - lonOriginRad)

        val e4 = e2 * e2
        val e6 = e4 * e2

        val M = a * (
                (1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256) * latRad
                        - (3 * e2 / 8 + 3 * e4 / 32 + 45 * e6 / 1024) * sin(2 * latRad)
                        + (15 * e4 / 256 + 45 * e6 / 1024) * sin(4 * latRad)
                        - (35 * e6 / 3072) * sin(6 * latRad)
                )

        val easting = k0 * N * (
                A + (1 - T + C) * A.pow(3) / 6
                        + (5 - 18 * T + T * T + 72 * C - 58 * (e2 / (1 - e2))) * A.pow(5) / 120
                ) + 500000.0

        var northing = k0 * (M + N * tan(latRad) * (
                A.pow(2) / 2
                        + (5 - T + 9 * C + 4 * C * C) * A.pow(4) / 24
                        + (61 - 58 * T + T * T + 600 * C - 330 * (e2 / (1 - e2))) * A.pow(6) / 720
                ))
        if (lat < 0) northing += 10000000.0

        return Pair(formatUTM(easting), formatUTM(northing))
    }

    private fun formatUTM(value: Double): String {
        val intPart = value.toLong()
        val decimalPart = ((value - intPart) * 100).toLong()
        return "${intPart},${decimalPart.toString().padStart(2, '0')}"
    }
}