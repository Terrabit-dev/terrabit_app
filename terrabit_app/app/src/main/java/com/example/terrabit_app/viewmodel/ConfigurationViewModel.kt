package com.example.terrabit_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(userPreferences.getDarkTheme())
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    val nif: String = userPreferences.getNif() ?: "No disponible"

    fun toggleTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        userPreferences.saveDarkTheme(isDark)
    }
}