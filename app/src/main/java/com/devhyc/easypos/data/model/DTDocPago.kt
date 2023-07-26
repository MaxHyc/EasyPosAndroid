package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

data class DTDocPago(@SerializedName("medioPagoCodigo") var medioPagoCodigo:Int,
                     @SerializedName("monedaCodigo") var monedaCodigo:String,
                     @SerializedName("tipoCambio") var tipoCambio:Double,
                     @SerializedName("importe") var importe:Double,
                     @SerializedName("numero") var numero:String,
                     @SerializedName("fecha") var fecha:String?,
                     @SerializedName("fechaVto") var fechaVto:String?,
                     @SerializedName("bancoCodigo") var bancoCodigo:String,
                     @SerializedName("tarjetaCodigo") var tarjetaCodigo:String,
                     @SerializedName("cuotas") var cuotas:Int,
                     @SerializedName("autorizacion") var autorizacion:String,
                     @SerializedName("transaccion") var transaccion:String
                     /*@SerializedName("STRING1") var MPNOMBRE:String,
                     @SerializedName("STRING2") var MPMONEDA:String*/
                     )
{
    constructor() : this(0,"",0.0,0.0,"","","","","",0,"","")
}
