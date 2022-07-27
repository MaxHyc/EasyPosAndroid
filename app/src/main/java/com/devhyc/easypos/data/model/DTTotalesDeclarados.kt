package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTTotalesDeclarados(@SerializedName("totalPesos") var totalPesos:Double,
                               @SerializedName("totalDoalres") var totalDoalres:Double,
                               @SerializedName("totalTarjetas") var totalTarjetas:Double,
                               @SerializedName("totalTarjetasDolares") var totalTarjetasDolares:Double,
                               @SerializedName("totalCheques") var totalCheques:Double,
                               @SerializedName("totalChequesDolares") var totalChequesDolares:Double,
                               @SerializedName("totalCreditos") var totalCreditos:Double,
                               @SerializedName("totalTickets") var totalTickets:Double)
