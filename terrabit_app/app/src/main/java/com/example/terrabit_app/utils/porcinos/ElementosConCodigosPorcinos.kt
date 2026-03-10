package com.example.terrabit_app.utils.porcinos

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R

class ElementosConCodigosPorcinos {
    @Composable
    fun categorias(): Map<String, String> {
        val codigos = mapOf<String, String>(
            "00" to stringResource(R.string.category_fattening),
            "01" to stringResource(R.string.category_piglets),
            "02" to stringResource(R.string.category_rearing),
            "03" to stringResource(R.string.category_females),
            "04" to stringResource(R.string.category_replacement),
            "05" to stringResource(R.string.category_boars)
        )
        return codigos
    }

    @Composable
    fun medios(): Map<String, String> {
        val codigos = mapOf<String, String>(
             "01" to stringResource(R.string.option_truck),
            "999" to stringResource(R.string.option_other)
        )
        return codigos
    }

    fun categoriasB(): Map<String, String> {
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
}