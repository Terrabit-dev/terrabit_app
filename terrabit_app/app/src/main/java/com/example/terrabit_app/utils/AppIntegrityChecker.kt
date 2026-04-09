package com.example.terrabit_app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import com.felhr.usbserial.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppIntegrityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val EXPECTED_CERT_HASH = "muHfSk1GMUyEtFj7cCvW7TZzoFR5+55AEZnunIaJOa0="

    fun isApkValid(): Boolean {
        if (BuildConfig.DEBUG) return true
        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    ?.apkContentsSigners
                    ?: emptyArray()
            } else {
                @Suppress("DEPRECATION")
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                    .signatures
                    ?: emptyArray()
            }
            signatures.any { sig ->
                val hash = Base64.encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(sig.toByteArray()),
                    Base64.NO_WRAP
                )
                hash == EXPECTED_CERT_HASH
            }
        } catch (e: Exception) {
            false
        }
    }
}