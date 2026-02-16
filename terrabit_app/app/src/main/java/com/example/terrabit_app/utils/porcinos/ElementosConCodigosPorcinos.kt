package com.example.terrabit_app.utils.porcinos

import androidx.compose.runtime.Composable

class ElementosConCodigosPorcinos {
    @Composable
    fun categorias(): Map<String, String> {
        val codigos = mapOf<String, String>(
            "Engreix" to "00",
            "Garrins" to "01",
            "Recria/Transició" to "02",
            "Famelles" to "03",
            "Reposició" to "04",
            "Sementals" to "05"
        )
        return codigos
    }

    @Composable
    fun medios(): Map<String, String> {
        val codigos = mapOf<String, String>(
            "Camió" to "01",
            "Altres" to "999"
        )
        return codigos
    }
}