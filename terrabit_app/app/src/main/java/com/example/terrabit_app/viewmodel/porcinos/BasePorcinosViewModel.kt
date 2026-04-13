package com.example.terrabit_app.viewmodel.porcinos

import androidx.lifecycle.ViewModel
import com.example.terrabit_app.data.network.Repositorio
import com.example.terrabit_app.utils.UserPreferences
import com.google.gson.JsonParser

abstract class BasePorcinosViewModel : ViewModel() {

    protected abstract val repositorio: Repositorio
    protected abstract val userPreferences: UserPreferences

    // ─── Credenciales lazy (recogen cambios de CodiMO) ───────────────────────
    val nif      get() = userPreferences.getNif()      ?: ""
    val password get() = userPreferences.getPassword() ?: ""
    val codiMo   get() = userPreferences.getCodiMO()   ?: ""

    // ─── Utilidades de fecha compartidas por los 4 VMs ───────────────────────

    /**
     * "dd/MM/yyyy" + "HH:mm" → "yyyyMMddHHmm"
     * Usada en CrearGuia, EditarGuia y GestionarGuias
     */
    protected fun convertirFechaHoraAFormatoAPI(fecha: String, hora: String): String {
        val f = fecha.split("/")
        val h = hora.split(":")
        return if (f.size == 3 && h.size == 2) "${f[2]}${f[1]}${f[0]}${h[0]}${h[1]}" else ""
    }

    /**
     * "dd/MM/yyyy HH:mm" → "yyyyMMddHHmm"
     * Usada en EntradasPorcinos y GestionarGuias (displayToApiFormat)
     */
    protected fun displayToApiFormat(display: String): String {
        return try {
            val partes = display.trim().split(" ")
            val (dia, mes, anio) = partes[0].split("/")
            val (h, m) = partes[1].split(":")
            "$anio$mes$dia$h$m"
        } catch (e: Exception) { "000101010000" }
    }

    /**
     * Parseo de errores de API — antes duplicado solo en GestionarGuias,
     * centralizado aquí para uso futuro
     */
    protected fun extraerDescripcion(rawJson: String, httpCode: Int): String {
        if (rawJson.isBlank()) return "Error $httpCode"
        return try {
            val element = JsonParser.parseString(rawJson)
            when {
                element.isJsonArray -> element.asJsonArray
                    .mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                    .filter { it.isNotBlank() }.joinToString("\n").ifBlank { "Error $httpCode" }
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    obj.getAsJsonArray("errors")
                        ?.mapNotNull { it.asJsonObject.get("descripcio")?.asString }
                        ?.filter { it.isNotBlank() }?.joinToString("\n")
                        ?.ifBlank { obj.get("descripcio")?.asString ?: "Error $httpCode" }
                        ?: obj.get("descripcio")?.asString ?: "Error $httpCode"
                }
                else -> "Error $httpCode"
            }
        } catch (e: Exception) { "Error $httpCode" }
    }
}