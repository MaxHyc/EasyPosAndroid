package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName

data class DtTransaccion(@SerializedName("transaccionId") var transaccionId: Long,
                         @SerializedName("detalle") var detalle: String?,
                         @SerializedName("fecha") var fecha: String,
                         @SerializedName("referencia") var referencia: String?,
                         @SerializedName("monedaCodigo") var monedaCodigo: String?,
                         @SerializedName("monto") var monto: Double)
