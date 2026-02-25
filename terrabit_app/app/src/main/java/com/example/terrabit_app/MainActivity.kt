package com.example.terrabit_app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terrabit_app.ui.navigation.Navigation
import com.example.terrabit_app.ui.theme.Terrabit_appTheme
import com.example.terrabit_app.viewmodel.DrawerViewModel  // ← NUEVO IMPORT
import com.example.terrabit_app.viewmodel.MainViewmodel
import androidx.appcompat.app.AppCompatActivity
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Terrabit_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val bluetoothViewModel: BluetoothViewModel = viewModel()
                    val drawerViewModel: DrawerViewModel = viewModel()
                    Navigation(bluetoothViewModel, drawerViewModel)
                }
            }
        }
    }
}