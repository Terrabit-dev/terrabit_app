package com.example.terrabit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.terrabit_app.utils.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _isDarkTheme = MutableStateFlow(userPreferences.getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    val nif: String = userPreferences.getNif() ?: "No disponible"

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        userPreferences.saveDarkTheme(isDark)
    }
}