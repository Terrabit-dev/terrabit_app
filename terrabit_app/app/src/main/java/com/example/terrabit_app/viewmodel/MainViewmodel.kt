package com.example.terrabit_app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Identificador
import com.example.terrabit_app.data.network.Identificadores
import com.example.terrabit_app.data.network.Repositorio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewmodel : ViewModel(){
    private val repositorio = Repositorio()

    private val _identificadores = MutableLiveData<Identificadores>()
    val identificadores = _identificadores

    fun getIdentificadores(nif: String, password: String, codiMO: String){
        CoroutineScope(Dispatchers.IO).launch {
            val response = repositorio.getIdentificadoresDisponibles(nif,password,codiMO)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful){
                    _identificadores.value = response.body()
                }
                else{
                    Log.e("Error identificadores:", response.message())
                }
            }
        }
    }
}