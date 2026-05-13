package com.example.terrabit_app.utils

import android.util.Log
import com.example.terrabit_app.utils.SecureLog
import com.felhr.usbserial.BuildConfig

/**
 * Logger seguro. Sanitiza automáticamente credenciales en cualquier mensaje
 * antes de imprimirlas. En builds release no logea nada (defensa en profundidad
 * por encima del ProGuard, por si en algún momento SecureLog.e queda activo).
 *
 * Casos cubiertos:
 *  - Query strings:    "...nif=12345&passwordMobilitat=secret..."
 *  - JSON:             {"nif":"12345","passwordMobilitat":"secret"}
 *  - Volcados libres:  "nif=12345 password=secret codiMo=ABC"
 *  - toString() de data classes Retrofit con SerializedName en campos sensibles.
 *
 * Para enmascarar manualmente strings sueltos (NIF, codiMO) usa maskPartial().
 */
object SecureLog {

    private val SENSITIVE_KEYS = listOf(
        "passwordMobilitat", "passwordmobilitat", "password", "contrasenya", "contrasena",
        "nif", "dni", "nie",
        "token", "authorization", "auth",
        "apikey", "api_key", "secret",
        "session", "cookie"
    )

    /**
     * Regex que detecta `key=value` o `"key":"value"` o `"key": "value"` donde key
     * sea uno de los nombres sensibles. Reemplaza el valor por "***".
     *
     * Construida una sola vez (lazy) para evitar coste por llamada en hot paths.
     */
    private val sensitiveRegex: Regex by lazy {
        val keysAlt = SENSITIVE_KEYS.joinToString("|") { Regex.escape(it) }
        // Tres patrones distintos para query, JSON y key=value libre. Insensible a mayúsculas.
        Regex(
            "(\"(?:$keysAlt)\"\\s*:\\s*\")[^\"]*(\")" +  // "key":"value"
                    "|" +
                    "((?:^|[?&])(?:$keysAlt)=)[^&\\s\"]+" +     // ?key=value o &key=value
                    "|" +
                    "(\\b(?:$keysAlt)\\s*[:=]\\s*)\\S+",        // key=value o key: value libre
            RegexOption.IGNORE_CASE
        )
    }

    private fun sanitize(message: String?): String {
        if (message.isNullOrEmpty()) return message ?: ""
        return sensitiveRegex.replace(message) { match ->
            // Cada alternativa del regex deja capturado el "prefijo" del valor.
            // Reemplazamos por prefijo + *** + cierre si lo hubiera.
            val prefix = match.groups[1]?.value ?: match.groups[3]?.value ?: match.groups[5]?.value ?: ""
            val suffix = match.groups[2]?.value ?: ""
            "$prefix***$suffix"
        }
    }

    /**
     * Enmascara parcialmente un valor: deja los `visibleStart` primeros y
     * `visibleEnd` últimos caracteres visibles. Útil para NIF/codiMO donde
     * mantener parte ayuda al debug pero no expone toda la credencial.
     */
    fun maskPartial(value: String?, visibleStart: Int = 2, visibleEnd: Int = 2): String {
        if (value.isNullOrEmpty()) return "***"
        if (value.length <= visibleStart + visibleEnd) return "***"
        return "${value.take(visibleStart)}***${value.takeLast(visibleEnd)}"
    }

    fun d(tag: String, message: String) {
        if (!BuildConfig.DEBUG) return
        SecureLog.d(tag, sanitize(message))
    }

    fun i(tag: String, message: String) {
        if (!BuildConfig.DEBUG) return
        Log.i(tag, sanitize(message))
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        if (throwable != null) SecureLog.w(tag, sanitize(message), throwable)
        else SecureLog.w(tag, sanitize(message))
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (!BuildConfig.DEBUG) return
        if (throwable != null) SecureLog.e(tag, sanitize(message), throwable)
        else SecureLog.e(tag, sanitize(message))
    }
}