package com.example.terrabit_app

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terrabit_app.ui.navigation.Navigation
import com.example.terrabit_app.ui.theme.Terrabit_appTheme
import com.example.terrabit_app.viewmodel.ConfigurationViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.example.terrabit_app.viewmodel.bovinos.DrawerViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags("ca")
            )
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val configViewModel: ConfigurationViewModel = hiltViewModel()
            val isDarkTheme by configViewModel.isDarkTheme.collectAsStateWithLifecycle()

            Terrabit_appTheme(darkTheme = isDarkTheme) {
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

    override fun recreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        super.recreate()
    }
}