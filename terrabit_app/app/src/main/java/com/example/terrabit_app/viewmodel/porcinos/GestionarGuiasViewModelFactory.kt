package com.example.terrabit_app.viewmodel.porcinos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.terrabit_app.data.network.ApiInterface
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences

class GestionarGuiasViewModelFactory(
    private val repo: Repositorio,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GestionarGuiasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GestionarGuiasViewModel(repo, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}