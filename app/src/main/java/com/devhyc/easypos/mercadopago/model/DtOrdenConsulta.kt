package com.devhyc.easypos.mercadopago.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class DtOrdenConsulta(@SerializedName("fechaDesde") var fechaDesde: LocalDateTime,
                           @SerializedName("fechaHasta") var fechaHasta: LocalDateTime,
                           @SerializedName("sucursalCodigo") var sucursalCodigo: String,
                           @SerializedName("cajaCodigo") var cajaCodigo: String)
