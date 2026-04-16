package com.example.terrabit_app.utils

/**
 * Utilidad para traducir el código de país ISO 3166-1 numérico (3 dígitos)
 * que devuelven los lectores RFID ISO 11784/11785 al código alfa-2 visible
 * en los crotales visuales (ej. 724 → "ES").
 *
 * Solo se sustituyen los 3 primeros dígitos; el resto del identificador
 * se conserva intacto tal como lo entrega el lector.
 */
object CodigoPaisUtils {

    /**
     * Mapa ISO 3166-1 numérico → alfa-2.
     * Cubre los principales países productores de ganado bovino y porcino.
     * Añadir entradas según necesidad sin tocar el resto del código.
     */
    private val CODIGOS_PAIS: Map<String, String> = mapOf(
        // Europa
        "724" to "ES",   // España
        "250" to "FR",   // Francia
        "276" to "DE",   // Alemania
        "380" to "IT",   // Italia
        "620" to "PT",   // Portugal
        "056" to "BE",   // Bélgica
        "528" to "NL",   // Países Bajos
        "826" to "GB",   // Reino Unido
        "372" to "IE",   // Irlanda
        "040" to "AT",   // Austria
        "208" to "DK",   // Dinamarca
        "246" to "FI",   // Finlandia
        "752" to "SE",   // Suecia
        "578" to "NO",   // Noruega
        "756" to "CH",   // Suiza
        "616" to "PL",   // Polonia
        "203" to "CZ",   // República Checa
        "348" to "HU",   // Hungría
        "642" to "RO",   // Rumanía
        "100" to "BG",   // Bulgaria
        "300" to "GR",   // Grecia
        "191" to "HR",   // Croacia
        "703" to "SK",   // Eslovaquia
        "705" to "SI",   // Eslovenia
        // América
        "840" to "US",   // Estados Unidos
        "124" to "CA",   // Canadá
        "076" to "BR",   // Brasil
        "032" to "AR",   // Argentina
        "484" to "MX",   // México
        "858" to "UY",   // Uruguay
        "170" to "CO",   // Colombia
        "604" to "PE",   // Perú
        "152" to "CL",   // Chile
        // Oceanía
        "036" to "AU",   // Australia
        "554" to "NZ",   // Nueva Zelanda
        // África
        "710" to "ZA",   // Sudáfrica
        // Asia / Oriente Medio
        "356" to "IN",   // India
        "156" to "CN",   // China
        "792" to "TR",   // Turquía
        "682" to "SA",   // Arabia Saudí
        "376" to "IL",   // Israel
    )

    /**
     * Traduce el identificador RFID raw: sustituye los 3 primeros dígitos
     * (código de país numérico) por el código alfa-2 correspondiente.
     *
     * Si el prefijo no está en la tabla, devuelve el identificador sin cambios.
     *
     * Ejemplos:
     *   "7242200100001234567" → "ES2200100001234567"
     *   "2762200100001234567" → "DE2200100001234567"
     *   "XYZ2200100001234567" → "XYZ2200100001234567"  (sin cambio)
     */
    fun traducirCodigoPais(identificador: String): String {
        if (identificador.length < 3) return identificador
        val prefijo = identificador.take(3)
        val alpha2  = CODIGOS_PAIS[prefijo] ?: return identificador
        return alpha2 + identificador.drop(3)
    }

    /**
     * Devuelve el código alfa-2 para un prefijo dado, o null si no existe.
     * Útil para mostrar el país en la UI sin transformar el identificador completo.
     */
    fun obtenerAlpha2(prefijoPais: String): String? = CODIGOS_PAIS[prefijoPais]
}