package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocVaucher(@SerializedName("comercio") var Comercio:ArrayList<DTDocVaucherFila>,
                        @SerializedName("cliente") var Cliente: ArrayList<DTDocVaucherFila>
                        )
data class DTDocVaucherFila( @SerializedName("salto") val salto: Boolean = false,
                             @SerializedName("saltoX") val saltoX: Int = 1,
                             @SerializedName("cel1") val cel1: String = "",
                             @SerializedName("cel2") val cel2: String = "",
                             @SerializedName("cel3") val cel3: String = "",
                             @SerializedName("tipoFuente") val tipoFuente: Int = 0)
