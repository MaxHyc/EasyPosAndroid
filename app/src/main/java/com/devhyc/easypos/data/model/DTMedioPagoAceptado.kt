package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTMedioPagoAceptado(@SerializedName("nombre") var Nombre:String,
                               @SerializedName("tipo") var Tipo:String,
                               @SerializedName("pago") var Pago:Double,
                               @SerializedName("cambio") var Cambio:Double
                               )
