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
        6 to stringResource(R.string.form_error_selected_aptitude)
    )

    // Si el código existe en el mapa, lo devuelve.
    return listasErrorsApp[codigo] ?: stringResource(R.string.form_error_universal)
}
