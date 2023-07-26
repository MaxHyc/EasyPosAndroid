package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTParamDocLista(@SerializedName("pendientes") var Pendientes:Boolean,
                           @SerializedName("anulados") var Anulados:Boolean,
                           @SerializedName("fechaDesde") var FechaDesde:String,
                           @SerializedName("fechaHasta") var FechaHasta:String)
