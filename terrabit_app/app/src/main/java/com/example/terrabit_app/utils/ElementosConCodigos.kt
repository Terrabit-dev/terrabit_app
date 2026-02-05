package com.example.terrabit_app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R
import kotlin.String


class ElementosConCodigos {
    @Composable
    fun sexos(): Map<String, String>{
        val tipos = mapOf<String, String>(
            stringResource(R.string.male) to "02",
            stringResource(R.string.female) to "01"
        )
        return tipos
    }
    @Composable
    fun aptitudes(): Map<String, String> {
        val aptitudes = mapOf<String, String>(
            stringResource(R.string.option_aptitude_meat) to "02",
            stringResource(R.string.option_aptitude_milk) to "01",
            stringResource(R.string.option_aptitude_double) to "03"
        )
        return aptitudes
    }
    @Composable
    fun Muertes(): Map<String, String>{
        val listaTiposMuerte = mapOf<String, String>(
            stringResource(R.string.form_type_dead_dead) to "01",
            stringResource(R.string.form_type_dead_abort) to "02"
        )
        return listaTiposMuerte
    }

    @Composable
    fun Transporte(): Map<String, String> {
        val listaTransporte = mapOf<String, String>(
            stringResource(R.string.option_truck) to "04",
            stringResource(R.string.option_boat) to "05",
            stringResource(R.string.option_airplane) to "06",
            stringResource(R.string.option_train) to "07",
            stringResource(R.string.option_walking) to "08",
            stringResource(R.string.option_other) to "99"
        )
        return listaTransporte
    }

    @Composable
    fun EstadosLlegada(): Map<String, String> {
        val listaEstadosLlegada = mapOf<String, String>(
            stringResource(R.string.option_arrival) to "92",
            stringResource(R.string.option_death_transport) to "93",
            stringResource(R.string.option_death_stable) to "94",
            stringResource(R.string.option_sacrificed) to "80"
        )
        return listaEstadosLlegada
    }

}