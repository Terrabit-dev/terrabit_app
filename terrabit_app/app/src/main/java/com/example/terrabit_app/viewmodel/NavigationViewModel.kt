package com.example.terrabit_app.viewmodel.bovinos

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    suspend fun haySesionActiva(): Boolean = userPreferences.haySesionPersistente()
}