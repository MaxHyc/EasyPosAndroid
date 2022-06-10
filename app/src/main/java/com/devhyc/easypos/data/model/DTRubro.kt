package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTRubro(@SerializedName("id") var id:String,
                   @SerializedName("codigo") var codigo:String,
                   @SerializedName("nombre") var nombre:String,
                   @SerializedName("impuestoCodigo") var impuestoCodigo:String,
                   @SerializedName("impuestoTasa") var impuestoTasa:String)
