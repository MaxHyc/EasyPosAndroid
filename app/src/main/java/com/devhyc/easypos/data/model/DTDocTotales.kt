package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocTotales(@SerializedName("subtotal") var subtotal:Double,
                        @SerializedName("totalImpuestos") var totalImpuestos:Double,
                        @SerializedName("totalDtos") var totalDtos:Double,
                        @SerializedName("redondeo") var redondeo:Double,
                        @SerializedName("total") var total:Double,
                        @SerializedName("items") var items:List<DTDocDetalle>)
