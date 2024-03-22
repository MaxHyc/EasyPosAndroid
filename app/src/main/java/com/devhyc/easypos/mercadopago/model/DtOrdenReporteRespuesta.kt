package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName

data class DtOrdenReporteRespuesta(@SerializedName("fechaDesde") var fechaDesde: String,
                                   @SerializedName("fechaHasta") var fechaHasta: String,
                                   @SerializedName("totalPesos") var totalPesos: Double,
                                   @SerializedName("totalDolares") var totalDolares: Double,
                                   @SerializedName("transacciones") var transacciones: List<DtTransaccion>)
