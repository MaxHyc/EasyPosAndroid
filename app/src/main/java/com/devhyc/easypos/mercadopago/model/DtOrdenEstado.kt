package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName

data class DtOrdenEstado(@SerializedName("conError") var conError: Boolean,
                         @SerializedName("transaccionId") var transaccionId: Long,
                         @SerializedName("estado") var estado: Int,
                         @SerializedName("mensaje") var mensaje: String?,
                         @SerializedName("fechaInicio") var fechaInicio: String,
                         @SerializedName("fechaFin") var fechaFin: String,
                         @SerializedName("resultado") var resultado: DtOrdenEstadoResultado?)

data class DtOrdenEstadoResultado(
    @SerializedName("ordenId") var ordenId: Long,
    @SerializedName("estadoResultado") var estadoResultado: Int,
    @SerializedName("mensaje") var mensaje: String?,
    @SerializedName("autorizacion") var autorizacion: String?
)
