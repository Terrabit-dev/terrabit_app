package com.example.terrabit_app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R

@Composable
fun alertsErrosScreens(codigo: Int): String {
    val listasErrorsApp = mapOf(
        0 to stringResource(R.string.form_error_universal),
        1 to stringResource(R.string.form_error_mother_id),
        2 to stringResource(R.string.form_error_breeding_id),
        3 to stringResource(R.string.form_error_birthdate_id),
        4 to stringResource(R.string.form_error_selected_sex),
        5 to stringResource(R.string.form_error_selected_raze),
        6 to stringResource(R.string.form_error_selected_aptitude),
        7 to stringResource(R.string.form_error_type_dead),
        8 to stringResource(R.string.form_error_dead_date),
        9 to stringResource(R.string.form_error_months_pregnacy),
        10 to stringResource(R.string.form_error_laltitud),
        11 to stringResource(R.string.form_error_longitud),
        12 to stringResource(R.string.form_error_animal_id),
        13 to stringResource(R.string.form_error_date_indentification),
        14 to stringResource(R.string.form_error_remo_code),
        15 to stringResource(R.string.form_error_arrival_date),
        16 to stringResource(R.string.form_error_arrival_time),
        17 to stringResource(R.string.form_error_ates_code),
        18 to stringResource(R.string.form_error_destination_farm),
        19 to stringResource(R.string.form_error_arrival_status)
    )

    // Si el código existe en el mapa, lo devuelve.
    return listasErrorsApp[codigo] ?: stringResource(R.string.form_error_universal)
}

