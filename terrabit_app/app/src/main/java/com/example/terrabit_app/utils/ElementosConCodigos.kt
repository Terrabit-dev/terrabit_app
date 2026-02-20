package com.example.terrabit_app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R
import kotlin.String


class ElementosConCodigos {
    @Composable
    fun sexos(): Map<String, String>{
        val tipos = mapOf<String, String>(
            "02" to stringResource(R.string.male),
            "01" to stringResource(R.string.female)
        )
        return tipos
    }

    @Composable
    fun aptitudes(): Map<String, String> {
        val aptitudes = mapOf<String, String>(
            "02" to stringResource(R.string.option_aptitude_meat),
            "01" to stringResource(R.string.option_aptitude_milk),
            "03" to stringResource(R.string.option_aptitude_double)
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
    fun tipoEmpresaSubministradora(): Map<String, String>{
        val listaEmpresas = mapOf<String, String>(
            "B02164317" to "DATAMARS IBERICA SLU",
            "A78100609" to "AZASA"
        )
        return  listaEmpresas
    }
    @Composable
    fun razasBovinas(): Map<String, String> {
        val listaRazas = mapOf(
            "0000" to "Conjunto Mestizo",
            "0099" to "Desconocida",
            "0201" to "Abondance",
            "0202" to "Armoricaine",
            "0203" to "Aurochs Reconstitue",
            "0204" to "Bazadaise",
            "0205" to "Bearnaise",
            "0206" to "Blue du Nord",
            "0207" to "Bordelaise",
            "0208" to "Brettonne Pie Noire",
            "0209" to "Casta (Aure y St. Girons)",
            "0210" to "Coopelso",
            "0211" to "Corse",
            "0212" to "Creole",
            "0213" to "Ferrandaise",
            "0214" to "Froment du Leon",
            "0215" to "Gelbvieh",
            "0216" to "Herens",
            "0217" to "Inra",
            "0218" to "Lourdaise",
            "0219" to "Maraichine",
            "0220" to "Marchigiana",
            "0221" to "Mirandaise",
            "0222" to "N. Dama",
            "0223" to "Nantaise",
            "0224" to "Pie Rouge des Plaines",
            "0225" to "Camargue",
            "0226" to "Rouge Flamande",
            "0227" to "Saosnoise",
            "0228" to "Tarentaise",
            "0229" to "Villard de Lans",
            "0230" to "Vosgienne",
            "0231" to "Wagyu",
            "0232" to "Watusi",
            "0233" to "Kobe",
            "0801" to "Angler",
            "0802" to "Aubrac",
            "0803" to "Dexter",
            "0804" to "Galloway",
            "0805" to "Highland",
            "0806" to "Irish Maol / Droimeann",
            "0807" to "Kerry",
            "0808" to "Maine Anjou-Rouge des Prés",
            "0809" to "Murray Grey",
            "0810" to "Partenaise",
            "0811" to "Romagnola",
            "0812" to "Rotbunte",
            "0813" to "Salers",
            "0814" to "South Devon",
            "1101" to "Albera",
            "1102" to "Alistana-Sanabresa",
            "1103" to "Asturiana de la Montaña",
            "1104" to "Betizu",
            "1105" to "Bruna de los Pirineos",
            "1107" to "Mallorquina",
            "1108" to "Monchina",
            "1109" to "Marismeña",
            "1110" to "Serrana Negra",
            "1111" to "Frisona",
            "1112" to "Parda",
            "1113" to "Charolesa",
            "1114" to "Hereford",
            "1115" to "Jersey",
            "1116" to "Angus",
            "1117" to "Limusina",
            "1118" to "Roja Danesa",
            "1119" to "St. Gertrudis",
            "1120" to "Retinta",
            "1121" to "Avileña-Negra Ibérica",
            "1122" to "Rubia Gallega",
            "1123" to "Morucha",
            "1124" to "Blanca Cacereña",
            "1125" to "Asturiana de los Valles",
            "1126" to "MRY",
            "1127" to "Normanda",
            "1128" to "Tudanca",
            "1129" to "Pirenaica",
            "1130" to "Fleckvieh",
            "1131" to "Cárdena Andaluza",
            "1132" to "Berrenda Negra",
            "1134" to "Canaria",
            "1135" to "Palmera",
            "1136" to "Menorquina",
            "1137" to "Berrenda Colorada",
            "1138" to "Negra Andaluza",
            "1139" to "Pajuna",
            "1140" to "Lidia",
            "1141" to "Blanca Belga",
            "1142" to "Rubia de Aquitania",
            "1143" to "Piamontesa",
            "1144" to "Cachena",
            "1145" to "Caldelana",
            "1146" to "Frieiresa",
            "1147" to "Limiana",
            "1148" to "Vianesa",
            "1149" to "Sayaguesa",
            "1150" to "Terreña",
            "1151" to "Ayrshire",
            "1152" to "Búfalo",
            "1153" to "Chianina",
            "1154" to "Guernsey",
            "1155" to "Montbeliard",
            "1156" to "Gasconne",
            "1157" to "Murciana-Levantina",
            "1158" to "Parda de Montaña",
            "1159" to "Mertolenga",
            "1160" to "Baltata Romanesca",
            "1161" to "Roja Letona",
            "1162" to "Pasiega",
            "1163" to "Serrana de Teruel",
            "1164" to "Alentejana",
            "1165" to "Swedish Red and White",
            "1166" to "Arouquesa",
            "1167" to "Norueguesa",
            "1168" to "Pallaresa",
            "1169" to "Preta",
            "1170" to "Valdostana",
            "1171" to "Ramo Grande",
            "9901" to "Bisonte",
            "9902" to "Brahman",
            "9903" to "Cebú",
            "9906" to "Shorthorn",
            "9907" to "Simmental",
            "9908" to "Pinzgauer",
            "9999" to "Otras"
        )
        return listaRazas
    }
}