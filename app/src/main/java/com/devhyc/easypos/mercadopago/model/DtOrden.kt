package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName

data class DtOrden( @SerializedName("sucursalCodigo") val sucursalCodigo: String?,
                    @SerializedName("cajaCodigo") val cajaCodigo: String?,
                    @SerializedName("numeroReferencia") val numeroReferencia: String?,
                    @SerializedName("monedaCodigo") val monedaCodigo: String?,
                    @SerializedName("monto") val monto: Double)
