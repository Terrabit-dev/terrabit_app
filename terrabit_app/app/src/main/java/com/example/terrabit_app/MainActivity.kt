package com.example.terrabit_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terrabit_app.ui.navigation.Navigation
import com.example.terrabit_app.ui.theme.Terrabit_appTheme
import com.example.terrabit_app.viewmodel.ConfigurationViewModel
import com.example.terrabit_app.viewmodel.DrawerViewModel
import androidx.appcompat.app.AppCompatActivity
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configViewModel: ConfigurationViewModel = viewModel()
            val isDarkTheme by configViewModel.isDarkTheme.collectAsStateWithLifecycle()

            Terrabit_appTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bluetoothViewModel: BluetoothViewModel = viewModel()
                    val drawerViewModel: DrawerViewModel = viewModel()
                    Navigation(bluetoothViewModel, drawerViewModel, configViewModel)
                }
            }
        }
    }
}