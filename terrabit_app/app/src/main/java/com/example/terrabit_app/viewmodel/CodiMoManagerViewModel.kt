package com.example.terrabit_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CodiMoManagerViewModel : ViewModel() {
    private val _codisMoExpandido = MutableLiveData(false)

    val codisMoExpandido = _codisMoExpandido


    fun toggleCodisMoExpandido(){
        _codisMoExpandido.value = !(_codisMoExpandido.value ?: false)
    }

    fun cerrarCodisMo(){
        _codisMoExpandido.value = false
    }
}


/**
 *     val context = LocalContext.current
 *     val userPreferences = remember { UserPreferences(context) }
 *     // lista de Mos por usuario
 *     val codisMos = userPreferences.getUserMOList()
 * val codisMosExpandidos by viewModel.codisMoExpandido.observeAsState(false)
 * Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
 *
 *                 Button(onClick = { viewModel.toggleCodisMoExpandido() }) { Text("Codigo MO") }
 *
 *                 DropdownMenu(
 *                     expanded = codisMosExpandidos,
 *                     onDismissRequest = { viewModel.cerrarCodisMo() }
 *                 ) {
 *                     // Items normales
 *                     codisMos.forEach { option ->
 *                         DropdownMenuItem(text = { Text(option) }, onClick = { /* ... */ })
 *                     }
 *
 *                     Divider() // Opcional: una línea divisoria
 *                     DropdownMenuItem(
 *                         text = { Text("Elemento Fijo al Final", color = Color.Red) },
 *                         onClick = { /* Acción especial */ }
 *                     )
 *                 }
 *             }
 * */