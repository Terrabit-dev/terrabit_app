package com.example.terrabit_app.utils

import kotlin.math.*

object LocationUtils {

    private const val WGS84_A = 6378137.0
    private const val WGS84_E2 = 0.00669437999014
    private const val UTM_K0 = 0.9996
    private const val UTM_FALSE_EASTING = 500000.0
    private const val UTM_FALSE_NORTHING = 10_000_000.0

    fun latLonToUTM(lat: Double, lon: Double): Pair<String, String> {
        val a = WGS84_A
        val e2 = WGS84_E2
        val k0 = UTM_K0
        val ePrime2 = e2 / (1 - e2)

        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)

        val zoneNumber = ((lon + 180) / 6).toInt() + 1
        val lonOriginRad = Math.toRadians((zoneNumber - 1) * 6 - 180 + 3.0)

        val sinLat = sin(latRad)
        val cosLat = cos(latRad)
        val tanLat = tan(latRad)

        val N = a / sqrt(1 - e2 * sinLat.pow(2))
        val T = tanLat.pow(2)
        val C = ePrime2 * cosLat.pow(2)
        val A = cosLat * (lonRad - lonOriginRad)

        val e4 = e2 * e2
        val e6 = e4 * e2

        val M = a * (
                (1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256) * latRad
                        - (3 * e2 / 8 + 3 * e4 / 32 + 45 * e6 / 1024) * sin(2 * latRad)
                        + (15 * e4 / 256 + 45 * e6 / 1024) * sin(4 * latRad)
                        - (35 * e6 / 3072) * sin(6 * latRad)
                )

        val easting = k0 * N * (
                A
                        + (1 - T + C) * A.pow(3) / 6
                        + (5 - 18 * T + T * T + 72 * C - 58 * ePrime2) * A.pow(5) / 120
                ) + UTM_FALSE_EASTING

        var northing = k0 * (
                M + N * tanLat * (
                        A.pow(2) / 2
                                + (5 - T + 9 * C + 4 * C * C) * A.pow(4) / 24
                                + (61 - 58 * T + T * T + 600 * C - 330 * ePrime2) * A.pow(6) / 720
                        )
                )
        if (lat < 0) northing += UTM_FALSE_NORTHING

        return Pair(formatUTM(easting), formatUTM(northing))
    }

    private fun formatUTM(value: Double): String {
        val intPart = value.toLong()
        val decimalPart = ((value - intPart) * 100).toLong().coerceAtLeast(0)
        return "${intPart},${decimalPart.toString().padStart(2, '0')}"
    }
}