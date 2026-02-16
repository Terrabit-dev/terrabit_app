package com.example.terrabit_app.data.network

import com.example.terrabit_app.data.network.animales.PetIdentificacion
import com.example.terrabit_app.data.network.animales.PetModicarAnimal
import com.example.terrabit_app.data.network.animales.RegistroMuerteBovi
import com.example.terrabit_app.data.network.animales.RegistroNacimientoBovi
import com.example.terrabit_app.data.network.guias.PeticionAltaGuia
import com.example.terrabit_app.data.network.guias.PeticionModificarGuia
import com.example.terrabit_app.data.network.guiasPorcinos.GuiaMobilitatPorcinos
import com.example.terrabit_app.data.network.guiasPorcinos.PeticionModificarGuiaPorcinos
import com.example.terrabit_app.data.network.material.PetSolicitudDuplicado
import com.example.terrabit_app.data.network.material.PetSolicitudMaterial
import com.example.terrabit_app.data.network.moviminetos.modelos.PetConfirmacionMovi
import com.example.terrabit_app.data.network.moviminetos.modelos.PetModificacioMovi
import com.example.terrabit_app.data.network.moviminetos.modelos.PetRegistroIntercanvi

class Repositorio {
    val apiInterface = ApiInterface.create()

    suspend fun getIdentificadoresDisponibles(
        nif: String,
        passwordMobilitat: String,
        codiMO: String
    ) = apiInterface.getIdentificadoresDisponibles(nif, passwordMobilitat, codiMO)

    suspend fun getDescargaGuiasMobilitat(
        nif: String,
        passwordMobilitat: String,
        codiMo: String,
        codiRega: String,
        dataSortida: String
    ) = apiInterface.getDescargaGuiasMobilitat(nif, passwordMobilitat, codiMo, codiRega, dataSortida)

    suspend fun getConfirmacionMovimientos(
        nif: String,
        passwordMobilitat: String,
        explotacioDestinacio: String,
        dataSortida: String
    ) = apiInterface.getConfirmacionMovimientos(nif, passwordMobilitat, explotacioDestinacio, dataSortida)

    suspend fun putRegistrarMuerte(request: RegistroMuerteBovi) =
        apiInterface.putRegistrarMuerte(request)
    suspend fun putRegistrarNacimiento(request: RegistroNacimientoBovi) =
        apiInterface.putRegistrarNacimiento(request)

    suspend fun putMoficarAnimal(request: PetModicarAnimal) =
        apiInterface.putMoficarAnimal(request)

    suspend fun putAltaGuia(request: PeticionAltaGuia) =
        apiInterface.putAltaGuia(request)

    suspend fun putModificarGuia(request: PeticionModificarGuia) =
        apiInterface.putModificarGuia(request)


    suspend fun putConfirmarMovi(request: PetConfirmacionMovi) =
        apiInterface.putConfirmarMovi(request)

    suspend fun putRegistroIntercanvio(request: PetRegistroIntercanvi) =
        apiInterface.putRegistroIntercanvio(request)

    suspend fun putMoficarMovi(request: PetModificacioMovi) =
        apiInterface.putMoficarMovi(request)

    suspend fun putIdentificacionPendiente(request: PetIdentificacion) =
        apiInterface.putIdentificacionPendiente(request)

    suspend fun putSolicitudDuplicado(request: PetSolicitudDuplicado) =
        apiInterface.putSolicitudDuplicado(request)

    suspend fun putSolicitudMaterial(request: PetSolicitudMaterial) =
        apiInterface.putSolicitudMaterial(request)


    //Porcinos
    suspend fun putMovilidadPorcinos(request: GuiaMobilitatPorcinos) =
        apiInterface.putMovilidadPorcinos(request)

    suspend fun getDescargaGuiasMobilitatPorcions(
        nif: String,
        passwordMobilitat: String,
        codiMo: String,
        codiRega: String,
        dataSortida: String
    ) = apiInterface.getGuiesMobilitatPorcinos(nif, passwordMobilitat, codiMo, codiRega, dataSortida)

    suspend fun putModificarGuiaPorcinos(request: PeticionModificarGuiaPorcinos) =
        apiInterface.putModificarGuiaPorcinos(request)
}