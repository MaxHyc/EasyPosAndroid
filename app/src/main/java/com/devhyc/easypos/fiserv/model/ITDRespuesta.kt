package com.devhyc.easypos.fiserv.model

import com.devhyc.easypos.data.model.DTDocPago
import com.google.gson.annotations.SerializedName

data class ITDRespuesta(@SerializedName("transaccionId") val transaccionId: String,
                        @SerializedName("estado") val estado: Int,
                        @SerializedName("mensaje") val mensaje: String,
                        @SerializedName("mensajePos") val mensajePos: String,
                        @SerializedName("conError") val conError: Boolean,
                        @SerializedName("accion") val accion: String,
                        @SerializedName("pago") val pago: DTDocPago?)

enum class ITDTransaccionAccion {
    ESPERANDO, PROCESANDO, CONFIRMACION, CANCELAR
}