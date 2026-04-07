package com.example.terrabit_app

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.example.terrabit_app.viewmodel.bovinos.ConfigurationViewModel
import com.example.terrabit_app.viewmodel.bovinos.DrawerViewModel
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.terrabit_app.utils.bluetooth.BluetoothViewModel
import com.felhr.usbserial.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import java.security.MessageDigest
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Para obtener los pines de preproducción:
        printCertPin()


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


    // Herramienta de desarrollo: imprime los pines SHA-256 del servidor en el logcat.
    // Solo se ejecuta en builds de debug. Filtra por "CERT_PIN" en el logcat.
    // El primer pin = servidor, el segundo = intermedio (usar como backup en ApiInterface).
    private fun printCertPin() {
        Thread {
            Log.d("CERT_PIN", "Iniciando conexión...")
            try {
                val url = URL("https://preproduccio.aplicacions.agricultura.gencat.cat")
                val conn = url.openConnection() as HttpsURLConnection
                conn.connectTimeout = 10000
                conn.connect()
                Log.d("CERT_PIN", "Conectado, leyendo certificados...")
                val certs = conn.serverCertificates
                for (cert in certs) {
                    val md = MessageDigest.getInstance("SHA-256")
                    val pubKey = cert.publicKey.encoded
                    val digest = md.digest(pubKey)
                    val pin = "sha256/" + Base64.encodeToString(digest, Base64.NO_WRAP)
                    Log.d("CERT_PIN", pin)
                }
                conn.disconnect()
            } catch (e: Exception) {
                Log.e("CERT_PIN", "Error: ${e.message}")
                Log.e("CERT_PIN", "Causa: ${e.cause}")
            }
        }.start()

    }
}