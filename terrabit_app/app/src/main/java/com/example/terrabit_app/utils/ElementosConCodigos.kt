package com.example.terrabit_app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.terrabit_app.R
import kotlin.String


class ElementosConCodigos {

    private val mapaSexos: Map<String, Int> = mapOf(
        "02" to R.string.male,
        "01" to R.string.female
    )

    private val mapaAptitudes: Map<String, Int> = mapOf(
        "02" to R.string.option_aptitude_meat,
        "01" to R.string.option_aptitude_milk,
        "03" to R.string.option_aptitude_double
    )

    private val mapaMuertes: Map<String, Int> = mapOf(
        "01" to R.string.form_type_dead_dead,
        "02" to R.string.form_type_dead_abort
    )

    private val mapaTransporte: Map<String, Int> = mapOf(
        "04" to R.string.option_truck,
        "05" to R.string.option_boat,
        "06" to R.string.option_airplane,
        "07" to R.string.option_train,
        "08" to R.string.option_walking,
        "99" to R.string.option_other
    )

    private val mapaEstadosLlegada: Map<String, Int> = mapOf(
        "92" to R.string.option_arrival,
        "93" to R.string.option_death_transport,
        "94" to R.string.option_death_stable,
        "80" to R.string.option_sacrificed
    )

    private val mapaOpcionesSiNo: Map<String, Int> = mapOf(
        "SI" to R.string.option_yes,
        "NO" to R.string.option_no
    )

    // Este se mantiene como <String, String> porque son valores en duro, no recursos
    private val mapaTiposPresentacion: Map<String, String> = mapOf(
        "1" to "I",
        "2" to "IIA",
        "3" to "IIB",
        "4" to "IIIA",
        "5" to "IIIB"
    )

    private val mapaTiposEnvios: Map<String, Int> = mapOf(
        "01" to R.string.option_ordinary_mail,
        "04" to R.string.option_certificate_mail
    )


    private val mapaTiposDireccionEnvio: Map<String, Int> = mapOf(
        "01" to R.string.option_oc, // Asegúrate de crear este recurso en tu strings.xml
        "02" to R.string.option_rancher,
        "03" to R.string.option_alternative_address
    )

    private val mapaTiposMaterialDuplicados: Map<String, Int> = mapOf(
        "07" to R.string.option_crotal,
        "20" to R.string.option_electronic_crotal,
        "21" to R.string.option_electronic_injectable,
        "22" to R.string.option_ruminal_bowl
    )

    private val mapaTiposMaterial: Map<String, Int> = mapOf(
        "21" to R.string.option_electronic_injectable,
        "22" to R.string.option_ruminal_bowl,
        "23" to R.string.option_crotal_simple_tisular,
        "24" to R.string.option_crotal_double_tisular,
        "26" to R.string.option_crotal_electronic_crotal,
        "25" to R.string.option_rebuilding
    )

    private val mapaOficinasComarcales: Map<String, String> = mapOf(
        "OC001" to "Alt Camp", "OC002" to "Alt Empordà", "OC003" to "Alt Penedès",
        "OC004" to "Alt Urgell", "OC005" to "Alta Ribagorça", "OC006" to "Anoia",
        "OC007" to "Bages", "OC008" to "Baix Camp", "OC009" to "Baix Ebre",
        "OC010" to "Baix Empordà", "OC011" to "Baix Llobregat", "OC012" to "Baix Penedès",
        "OC013" to "Barcelonès", "OC014" to "Berguedà", "OC015" to "Cerdanya",
        "OC016" to "Conca de Barberà", "OC017" to "Garraf", "OC018" to "Garrigues",
        "OC019" to "Garrotxa", "OC020" to "Gironès", "OC021" to "Maresme",
        "OC022" to "Montsià", "OC023" to "Noguera", "OC024" to "Osona",
        "OC025" to "Pallars Jussà", "OC026" to "Pallars Sobirà", "OC027" to "Pla d'Urgell",
        "OC028" to "Pla de l'Estany", "OC029" to "Priorat", "OC030" to "Ribera d'Ebre",
        "OC031" to "Ripollès", "OC032" to "Segarra", "OC033" to "Segrià",
        "OC034" to "La Selva", "OC035" to "Solsonès", "OC036" to "Tarragonès",
        "OC037" to "Terra Alta", "OC038" to "Urgell", "OC039" to "Vall d'Aran",
        "OC040" to "Vallès Occidental", "OC041" to "Vallès Oriental", "OC042" to "Moianès"
    )

    private val mapaEmpresaSubministradora: Map<String, String> = mapOf(
        "B02164317" to "DATAMARS IBERICA SLU",
        "A78100609" to "AZASA"
    )
    private val mapaRazas: Map<String, Int> = mapOf(
        "9999" to R.string.raza_9999, "0000" to R.string.raza_0000, "0099" to R.string.raza_0099,
        "0201" to R.string.raza_0201, "1101" to R.string.raza_1101, "1164" to R.string.raza_1164,
        "1102" to R.string.raza_1102, "0801" to R.string.raza_0801, "1116" to R.string.raza_1116,
        "1166" to R.string.raza_1166, "0202" to R.string.raza_0202, "1103" to R.string.raza_1103,
        "1125" to R.string.raza_1125, "1151" to R.string.raza_1151, "0203" to R.string.raza_0203,
        "1121" to R.string.raza_1121, "0802" to R.string.raza_0802, "1160" to R.string.raza_1160,
        "0204" to R.string.raza_0204, "0205" to R.string.raza_0205, "1104" to R.string.raza_1104,
        "1137" to R.string.raza_1137, "1132" to R.string.raza_1132, "9901" to R.string.raza_9901,
        "1141" to R.string.raza_1141, "1124" to R.string.raza_1124, "0206" to R.string.raza_0206,
        "0207" to R.string.raza_0207, "9902" to R.string.raza_9902, "0208" to R.string.raza_0208,
        "1105" to R.string.raza_1105, "1152" to R.string.raza_1152, "1131" to R.string.raza_1131,
        "1144" to R.string.raza_1144, "1145" to R.string.raza_1145, "1134" to R.string.raza_1134,
        "0225" to R.string.raza_0225, "0209" to R.string.raza_0209, "9903" to R.string.raza_9903,
        "1153" to R.string.raza_1153, "1113" to R.string.raza_1113, "0210" to R.string.raza_0210,
        "0211" to R.string.raza_0211, "0212" to R.string.raza_0212, "0803" to R.string.raza_0803,
        "0213" to R.string.raza_0213, "1130" to R.string.raza_1130, "0214" to R.string.raza_0214,
        "1111" to R.string.raza_1111, "1146" to R.string.raza_1146, "0804" to R.string.raza_0804,
        "1156" to R.string.raza_1156, "0215" to R.string.raza_0215, "1154" to R.string.raza_1154,
        "0805" to R.string.raza_0805, "0216" to R.string.raza_0216, "1114" to R.string.raza_1114,
        "0217" to R.string.raza_0217, "0806" to R.string.raza_0806, "1115" to R.string.raza_1115,
        "0807" to R.string.raza_0807, "0233" to R.string.raza_0233, "1140" to R.string.raza_1140,
        "1147" to R.string.raza_1147, "1117" to R.string.raza_1117, "0218" to R.string.raza_0218,
        "1107" to R.string.raza_1107, "0220" to R.string.raza_0220, "0219" to R.string.raza_0219,
        "1109" to R.string.raza_1109, "0808" to R.string.raza_0808, "1136" to R.string.raza_1136,
        "1159" to R.string.raza_1159, "0221" to R.string.raza_0221, "1108" to R.string.raza_1108,
        "1155" to R.string.raza_1155, "1123" to R.string.raza_1123, "1162" to R.string.raza_1162,
        "1126" to R.string.raza_1126, "1157" to R.string.raza_1157, "0809" to R.string.raza_0809,
        "0222" to R.string.raza_0222, "1138" to R.string.raza_1138, "0223" to R.string.raza_0223,
        "1127" to R.string.raza_1127, "1167" to R.string.raza_1167, "1139" to R.string.raza_1139,
        "1168" to R.string.raza_1168, "1158" to R.string.raza_1158, "1112" to R.string.raza_1112,
        "0810" to R.string.raza_0810, "0224" to R.string.raza_0224, "1143" to R.string.raza_1143,
        "1129" to R.string.raza_1129, "1135" to R.string.raza_1135, "9908" to R.string.raza_9908,
        "1169" to R.string.raza_1169, "1171" to R.string.raza_1171, "1120" to R.string.raza_1120,
        "1122" to R.string.raza_1122, "1142" to R.string.raza_1142, "0226" to R.string.raza_0226,
        "0811" to R.string.raza_0811, "0812" to R.string.raza_0812, "1161" to R.string.raza_1161,
        "1118" to R.string.raza_1118, "0813" to R.string.raza_0813, "1149" to R.string.raza_1149,
        "0227" to R.string.raza_0227, "1163" to R.string.raza_1163, "1110" to R.string.raza_1110,
        "9906" to R.string.raza_9906, "9907" to R.string.raza_9907, "0814" to R.string.raza_0814,
        "1165" to R.string.raza_1165, "1119" to R.string.raza_1119, "0228" to R.string.raza_0228,
        "1150" to R.string.raza_1150, "1128" to R.string.raza_1128, "1170" to R.string.raza_1170,
        "1148" to R.string.raza_1148, "0229" to R.string.raza_0229, "0230" to R.string.raza_0230,
        "0231" to R.string.raza_0231, "0232" to R.string.raza_0232
    )

    fun getRazas(): Map<String, Int> {
        return mapaRazas
    }

    fun getRazaBovinasId(codigo: String): Int {
        return mapaRazas[codigo] ?: R.string.raza_0099 // Devuelve "Desconocida" por defecto
    }

    fun getSexos(): Map<String, Int>{
        return mapaSexos
    }

    fun getAptitudes(): Map<String, Int>{
        return mapaAptitudes
    }

    fun getMuertes() : Map<String, Int>{
        return mapaMuertes
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
            "04" to stringResource(R.string.option_truck),
            "05" to stringResource(R.string.option_boat),
            "06" to stringResource(R.string.option_airplane),
            "07" to stringResource(R.string.option_train),
            "08" to stringResource(R.string.option_walking),
            "99" to stringResource(R.string.option_other)
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
             "SI" to stringResource(R.string.option_yes) ,
             "NO" to stringResource(R.string.option_no)
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
    fun tiposMaterialDuplicados(): Map<String, String>{
        val listaTiposMaterial = mapOf<String, String>(
            "07" to stringResource(R.string.option_crotal),
            "20" to stringResource(R.string.option_electronic_crotal),
            "21" to stringResource(R.string.option_electronic_injectable),
            "22" to stringResource(R.string.option_ruminal_bowl)
        )
        return listaTiposMaterial
    }
    @Composable
    fun tiposMaterial(): Map<String, String>{
        val listaTiposMaterial = mapOf<String, String>(
            "21" to stringResource(R.string.option_electronic_injectable),
            "22" to stringResource(R.string.option_ruminal_bowl),
            "23" to stringResource(R.string.option_crotal_simple_tisular),
            "24" to stringResource(R.string.option_crotal_double_tisular),
            "26" to stringResource(R.string.option_crotal_electronic_crotal),
            "25" to stringResource(R.string.option_rebuilding)
        )
        return listaTiposMaterial
    }

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


    fun tipoEmpresaSubministradora(): Map<String, String>{
        val listaEmpresas = mapOf<String, String>(
            "B02164317" to "DATAMARS IBERICA SLU",
            "A78100609" to "AZASA"
        )
        return  listaEmpresas
    }

    fun razasBovinas(): Map<String, String> {
        val listaRazas = mapOf(
            "9999" to "Otras",
            "0000" to "Conjunto Mestizo",
            "0099" to "Desconocida",
            "0201" to "Abondance",
            "1101" to "Albera",
            "1164" to "Alentejana",
            "1102" to "Alistana-Sanabresa",
            "0801" to "Angler",
            "1116" to "Angus",
            "1166" to "Arouquesa",
            "0202" to "Armoricaine",
            "1103" to "Asturiana de la Montaña",
            "1125" to "Asturiana de los Valles",
            "1151" to "Ayrshire",
            "0203" to "Aurochs Reconstitue",
            "1121" to "Avileña-Negra Ibérica",
            "0802" to "Aubrac",
            "1160" to "Baltata Romanesca",
            "0204" to "Bazadaise",
            "0205" to "Bearnaise",
            "1104" to "Betizu",
            "1137" to "Berrenda Colorada",
            "1132" to "Berrenda Negra",
            "9901" to "Bisonte",
            "1141" to "Blanca Belga",
            "1124" to "Blanca Cacereña",
            "0206" to "Blue du Nord",
            "0207" to "Bordelaise",
            "9902" to "Brahman",
            "0208" to "Brettonne Pie Noire",
            "1105" to "Bruna de los Pirineos",
            "1152" to "Búfalo",
            "1131" to "Cárdena Andaluza",
            "1144" to "Cachena",
            "1145" to "Caldelana",
            "1134" to "Canaria",
            "0225" to "Camargue",
            "0209" to "Casta (Aure y St. Girons)",
            "9903" to "Cebú",
            "1153" to "Chianina",
            "1113" to "Charolesa",
            "0210" to "Coopelso",
            "0211" to "Corse",
            "0212" to "Creole",
            "0803" to "Dexter",
            "0213" to "Ferrandaise",
            "1130" to "Fleckvieh",
            "0214" to "Froment du Leon",
            "1111" to "Frisona",
            "1146" to "Frieiresa",
            "0804" to "Galloway",
            "1156" to "Gasconne",
            "0215" to "Gelbvieh",
            "1154" to "Guernsey",
            "0805" to "Highland",
            "0216" to "Herens",
            "1114" to "Hereford",
            "0217" to "Inra",
            "0806" to "Irish Maol / Droimeann",
            "1115" to "Jersey",
            "0807" to "Kerry",
            "0233" to "Kobe",
            "1140" to "Lidia",
            "1147" to "Limiana",
            "1117" to "Limusina",
            "0218" to "Lourdaise",
            "1107" to "Mallorquina",
            "0220" to "Marchigiana",
            "0219" to "Maraichine",
            "1109" to "Marismeña",
            "0808" to "Maine Anjou-Rouge des Prés",
            "1136" to "Menorquina",
            "1159" to "Mertolenga",
            "0221" to "Mirandaise",
            "1108" to "Monchina",
            "1155" to "Montbeliard",
            "1123" to "Morucha",
            "1162" to "Pasiega",
            "1126" to "MRY",
            "1157" to "Murciana-Levantina",
            "0809" to "Murray Grey",
            "0222" to "N. Dama",
            "1138" to "Negra Andaluza",
            "0223" to "Nantaise",
            "1127" to "Normanda",
            "1167" to "Norueguesa",
            "1139" to "Pajuna",
            "1168" to "Pallaresa",
            "1158" to "Parda de Montaña",
            "1112" to "Parda",
            "0810" to "Partenaise",
            "0224" to "Pie Rouge des Plaines",
            "1143" to "Piamontesa",
            "1129" to "Pirenaica",
            "1135" to "Palmera",
            "9908" to "Pinzgauer",
            "1169" to "Preta",
            "1171" to "Ramo Grande",
            "1120" to "Retinta",
            "1122" to "Rubia Gallega",
            "1142" to "Rubia de Aquitania",
            "0226" to "Rouge Flamande",
            "0811" to "Romagnola",
            "0812" to "Rotbunte",
            "1161" to "Roja Letona",
            "1118" to "Roja Danesa",
            "0813" to "Salers",
            "1149" to "Sayaguesa",
            "0227" to "Saosnoise",
            "1163" to "Serrana de Teruel",
            "1110" to "Serrana Negra",
            "9906" to "Shorthorn",
            "9907" to "Simmental",
            "0814" to "South Devon",
            "1165" to "Swedish Red and White",
            "1119" to "St. Gertrudis",
            "0228" to "Tarentaise",
            "1150" to "Terreña",
            "1158" to "Parda de Montaña",
            "1128" to "Tudanca",
            "1170" to "Valdostana",
            "1148" to "Vianesa",
            "0229" to "Villard de Lans",
            "0230" to "Vosgienne",
            "0231" to "Wagyu",
            "0232" to "Watusi"
        )
        return listaRazas
    }




    private val mapaPaises: Map<String, Int> = mapOf(
        "000" to R.string.pais_000, "101" to R.string.pais_101, "102" to R.string.pais_102,
        "103" to R.string.pais_103, "104" to R.string.pais_104, "105" to R.string.pais_105,
        "106" to R.string.pais_106, "107" to R.string.pais_107, "108" to R.string.pais_108,
        "109" to R.string.pais_109, "110" to R.string.pais_110, "111" to R.string.pais_111,
        "112" to R.string.pais_112, "113" to R.string.pais_113, "114" to R.string.pais_114,
        "115" to R.string.pais_115, "201" to R.string.pais_201, "202" to R.string.pais_202,
        "203" to R.string.pais_203, "204" to R.string.pais_204, "205" to R.string.pais_205,
        "206" to R.string.pais_206, "207" to R.string.pais_207, "208" to R.string.pais_208,
        "209" to R.string.pais_209, "210" to R.string.pais_210, "211" to R.string.pais_211,
        "212" to R.string.pais_212, "213" to R.string.pais_213, "214" to R.string.pais_214,
        "215" to R.string.pais_215, "216" to R.string.pais_216, "217" to R.string.pais_217,
        "218" to R.string.pais_218, "219" to R.string.pais_219, "220" to R.string.pais_220,
        "221" to R.string.pais_221, "222" to R.string.pais_222, "223" to R.string.pais_223,
        "224" to R.string.pais_224, "225" to R.string.pais_225, "226" to R.string.pais_226,
        "227" to R.string.pais_227, "228" to R.string.pais_228, "229" to R.string.pais_229,
        "230" to R.string.pais_230, "231" to R.string.pais_231, "232" to R.string.pais_232,
        "233" to R.string.pais_233, "234" to R.string.pais_234, "235" to R.string.pais_235,
        "236" to R.string.pais_236, "237" to R.string.pais_237, "238" to R.string.pais_238,
        "239" to R.string.pais_239, "240" to R.string.pais_240, "241" to R.string.pais_241,
        "242" to R.string.pais_242, "243" to R.string.pais_243, "244" to R.string.pais_244,
        "245" to R.string.pais_245, "246" to R.string.pais_246, "247" to R.string.pais_247,
        "248" to R.string.pais_248, "249" to R.string.pais_249, "251" to R.string.pais_251,
        "252" to R.string.pais_252, "253" to R.string.pais_253, "254" to R.string.pais_254,
        "255" to R.string.pais_255, "256" to R.string.pais_256, "257" to R.string.pais_257,
        "258" to R.string.pais_258, "259" to R.string.pais_259, "260" to R.string.pais_260,
        "261" to R.string.pais_261, "262" to R.string.pais_262, "263" to R.string.pais_263,
        "264" to R.string.pais_264, "265" to R.string.pais_265, "266" to R.string.pais_266,
        "267" to R.string.pais_267, "268" to R.string.pais_268, "269" to R.string.pais_269,
        "270" to R.string.pais_270, "271" to R.string.pais_271, "272" to R.string.pais_272,
        "273" to R.string.pais_273, "274" to R.string.pais_274, "275" to R.string.pais_275,
        "276" to R.string.pais_276, "277" to R.string.pais_277, "278" to R.string.pais_278,
        "279" to R.string.pais_279, "280" to R.string.pais_280, "281" to R.string.pais_281,
        "282" to R.string.pais_282, "283" to R.string.pais_283, "284" to R.string.pais_284,
        "285" to R.string.pais_285, "286" to R.string.pais_286, "287" to R.string.pais_287,
        "288" to R.string.pais_288, "289" to R.string.pais_289, "290" to R.string.pais_290,
        "291" to R.string.pais_291, "292" to R.string.pais_292, "293" to R.string.pais_293,
        "294" to R.string.pais_294, "295" to R.string.pais_295, "296" to R.string.pais_296,
        "297" to R.string.pais_297, "298" to R.string.pais_298, "299" to R.string.pais_299,
        "300" to R.string.pais_300, "301" to R.string.pais_301, "302" to R.string.pais_302,
        "303" to R.string.pais_303, "304" to R.string.pais_304, "305" to R.string.pais_305,
        "306" to R.string.pais_306, "307" to R.string.pais_307, "308" to R.string.pais_308,
        "309" to R.string.pais_309, "310" to R.string.pais_310, "311" to R.string.pais_311,
        "312" to R.string.pais_312, "313" to R.string.pais_313, "314" to R.string.pais_314,
        "315" to R.string.pais_315, "316" to R.string.pais_316, "317" to R.string.pais_317,
        "318" to R.string.pais_318, "319" to R.string.pais_319, "320" to R.string.pais_320,
        "321" to R.string.pais_321, "322" to R.string.pais_322, "323" to R.string.pais_323,
        "324" to R.string.pais_324, "325" to R.string.pais_325, "326" to R.string.pais_326,
        "327" to R.string.pais_327, "328" to R.string.pais_328, "329" to R.string.pais_329,
        "330" to R.string.pais_330, "331" to R.string.pais_331, "332" to R.string.pais_332,
        "333" to R.string.pais_333, "334" to R.string.pais_334, "335" to R.string.pais_335,
        "336" to R.string.pais_336, "337" to R.string.pais_337, "338" to R.string.pais_338,
        "339" to R.string.pais_339, "340" to R.string.pais_340, "341" to R.string.pais_341,
        "342" to R.string.pais_342, "343" to R.string.pais_343, "344" to R.string.pais_344,
        "345" to R.string.pais_345, "346" to R.string.pais_346, "347" to R.string.pais_347,
        "348" to R.string.pais_348, "349" to R.string.pais_349, "350" to R.string.pais_350,
        "351" to R.string.pais_351, "352" to R.string.pais_352, "353" to R.string.pais_353,
        "354" to R.string.pais_354, "355" to R.string.pais_355, "356" to R.string.pais_356,
        "357" to R.string.pais_357, "358" to R.string.pais_358, "359" to R.string.pais_359,
        "360" to R.string.pais_360, "361" to R.string.pais_361, "362" to R.string.pais_362,
        "363" to R.string.pais_363, "364" to R.string.pais_364, "365" to R.string.pais_365,
        "366" to R.string.pais_366, "367" to R.string.pais_367, "368" to R.string.pais_368,
        "369" to R.string.pais_369, "370" to R.string.pais_370, "371" to R.string.pais_371,
        "372" to R.string.pais_372, "373" to R.string.pais_373, "374" to R.string.pais_374,
        "375" to R.string.pais_375, "376" to R.string.pais_376, "377" to R.string.pais_377,
        "378" to R.string.pais_378, "379" to R.string.pais_379, "380" to R.string.pais_380,
        "381" to R.string.pais_381, "382" to R.string.pais_382, "383" to R.string.pais_383,
        "384" to R.string.pais_384, "385" to R.string.pais_385, "386" to R.string.pais_386,
        "387" to R.string.pais_387, "388" to R.string.pais_388, "389" to R.string.pais_389,
        "390" to R.string.pais_390, "391" to R.string.pais_391, "392" to R.string.pais_392,
        "393" to R.string.pais_393, "394" to R.string.pais_394, "395" to R.string.pais_395,
        "396" to R.string.pais_396, "397" to R.string.pais_397, "398" to R.string.pais_398,
        "399" to R.string.pais_399, "400" to R.string.pais_400, "401" to R.string.pais_401,
        "402" to R.string.pais_402, "403" to R.string.pais_403, "404" to R.string.pais_404,
        "405" to R.string.pais_405, "406" to R.string.pais_406, "407" to R.string.pais_407,
        "408" to R.string.pais_408, "409" to R.string.pais_409, "410" to R.string.pais_410,
        "411" to R.string.pais_411, "413" to R.string.pais_413, "414" to R.string.pais_414,
        "415" to R.string.pais_415, "999" to R.string.pais_999
    )

    fun getNombrePaisId(codigo: String): Int {
        return mapaPaises[codigo] ?: R.string.pais_000 // Devuelve "Desconocido" por defecto
    }

}