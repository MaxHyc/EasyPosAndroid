package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName

data class DtOrdenResultado(@SerializedName("transaccionId") var transaccionId: Long,
                       @SerializedName("estado") var estado: Int,
                       @SerializedName("mensaje") var mensaje: String?,
                       @SerializedName("conError") var conError: Boolean,
                       @SerializedName("urlQr") var urlQr: String?) {
}