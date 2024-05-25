package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDTransaccionNueva(@SerializedName("medioPagoCodigo") var MedioPagoCodigo:Int,
                               @SerializedName("terminalCodigo") var TerminalCodigo:String,
                               @SerializedName("tipoDocCodigo") val tipoDocCodigo: String,
                               @SerializedName("nroDoc") val nroDoc: Long,
                               @SerializedName("sucursalCodigo") val sucursalCodigo: String,
                               @SerializedName("funcionarioId") val funcionarioId: String,
                               @SerializedName("monedaCodigo") val monedaCodigo: String,
                               @SerializedName("totalFactura") val totalFactura: Double,
                               @SerializedName("totalGravado") val totalGravado: Double,
                               @SerializedName("monedaCodigoPago") val monedaCodigoPago: String,
                               @SerializedName("totalPago") val totalPago: Double,
                               @SerializedName("tipoCambio") val tipoCambio: Double,
                               @SerializedName("conRut") var conRut: Boolean,
                               @SerializedName("cuotas") val cuotas: Int,
                               @SerializedName("plan") val plan: Int,
                               @SerializedName("solicitarConfirmacion") val SolicitarConfirmacion: Boolean)
