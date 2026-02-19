package com.example.terrabit_app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R
import kotlin.String


class ElementosConCodigos {
    @Composable
    fun sexos(): Map<String, String>{
        val tipos = mapOf<String, String>(
            stringResource(R.string.male) to "02",
            stringResource(R.string.female) to "01"
        )
        return tipos
    }
    @Composable
    fun aptitudes(): Map<String, String> {
        val aptitudes = mapOf<String, String>(
            stringResource(R.string.option_aptitude_meat) to "02",
            stringResource(R.string.option_aptitude_milk) to "01",
            stringResource(R.string.option_aptitude_double) to "03"
        )
        return aptitudes
    }
    @Composable
    fun muertes(): Map<String, String>{
        val listaTiposMuerte = mapOf<String, String>(
            stringResource(R.string.form_type_dead_dead) to "01",
            stringResource(R.string.form_type_dead_abort) to "02"
        )
        return listaTiposMuerte
    }

    @Composable
    fun transporte(): Map<String, String> {
        val listaTransporte = mapOf<String, String>(
            stringResource(R.string.option_truck) to "04",
            stringResource(R.string.option_boat) to "05",
            stringResource(R.string.option_airplane) to "06",
            stringResource(R.string.option_train) to "07",
            stringResource(R.string.option_walking) to "08",
            stringResource(R.string.option_other) to "99"
        )
        return listaTransporte
    }

    @Composable
    fun estadosLlegada(): Map<String, String> {
        val listaEstadosLlegada = mapOf<String, String>(
            "92" to stringResource(R.string.option_arrival),
            "93" to stringResource(R.string.option_death_transport),
            "94" to stringResource(R.string.option_death_stable),
            "80" to stringResource(R.string.option_sacrificed)
        )
        return listaEstadosLlegada
    }

    @Composable
    fun opcionesSiNo(): Map<String, String>{
        val listaOpciones= mapOf<String, String>(
            stringResource(R.string.option_yes) to "SI",
            stringResource(R.string.option_no) to "NO"
        )
        return listaOpciones
    }

    @Composable
    fun tiposPresentacion(): Map<String, String>{
        val listaTiposPresentacion = mapOf<String, String>(
            "1" to "I",
            "2" to "IIA",
            "3" to "IIB",
            "4" to "IIIA",
            "5" to "IIIB"
        )
        return listaTiposPresentacion

    }

    @Composable
    fun tiposEnvios() : Map<String, String> {
        val listaTiposEnvios = mapOf<String, String>(
            "01" to stringResource(R.string.option_ordinary_mail),
            "04" to stringResource(R.string.option_certificate_mail)
        )
        return listaTiposEnvios
    }

    @Composable
    fun tiposDireccionEnvio(): Map<String, String>{
        val listaTiposDireccionEnvio = mapOf<String, String>(
            "01" to "OC",
            "02" to stringResource(R.string.option_rancher),
            "03" to stringResource(R.string.option_alternative_address)
        )
        return  listaTiposDireccionEnvio
    }

    @Composable
    fun tiposMaterial(): Map<String, String>{
        val listaTiposMaterial = mapOf<String, String>(
            "07" to stringResource(R.string.option_crotal),
            "20" to stringResource(R.string.option_electronic_crotal),
            "21" to stringResource(R.string.option_electronic_injectable),
            "22" to stringResource(R.string.option_ruminal_bowl)
        )
        return listaTiposMaterial
    }

    @Composable
    fun tiposOficinasComarcales(): Map<String, String> {
        val listaOc = mapOf<String, String>(
            "OC001" to "Alt Camp",
            "OC002" to "Alt Empordà",
            "OC003" to "Alt Penedès",
            "OC004" to "Alt Urgell",
            "OC005" to "Alta Ribagorça",
            "OC006" to "Anoia",
            "OC007" to "Bages",
            "OC008" to "Baix Camp",
            "OC009" to "Baix Ebre",
            "OC010" to "Baix Empordà",
            "OC011" to "Baix Llobregat",
            "OC012" to "Baix Penedès",
            "OC013" to "Barcelonès",
            "OC014" to "Berguedà",
            "OC015" to "Cerdanya",
            "OC016" to "Conca de Barberà",
            "OC017" to "Garraf",
            "OC018" to "Garrigues",
            "OC019" to "Garrotxa",
            "OC020" to "Gironès",
            "OC021" to "Maresme",
            "OC022" to "Montsià",
            "OC023" to "Noguera",
            "OC024" to "Osona",
            "OC025" to "Pallars Jussà",
            "OC026" to "Pallars Sobirà",
            "OC027" to "Pla d'Urgell",
            "OC028" to "Pla de l'Estany",
            "OC029" to "Priorat",
            "OC030" to "Ribera d'Ebre",
            "OC031" to "Ripollès",
            "OC032" to "Segarra",
            "OC033" to "Segrià",
            "OC034" to "La Selva",
            "OC035" to "Solsonès",
            "OC036" to "Tarragonès",
            "OC037" to "Terra Alta",
            "OC038" to "Urgell",
            "OC039" to "Vall d'Aran",
            "OC040" to "Vallès Occidental",
            "OC041" to "Vallès Oriental",
            "OC042" to "Moianès"
        )
        return listaOc
    }

    @Composable
    fun TipoEmpresaSubministradora(): Map<String, String>{
        val listaEmpresas = mapOf<String, String>(
            "B02164317" to "DATAMARS IBERICA SLU",
            "A78100609" to "AZASA"
        )
        return  listaEmpresas
    }
}