package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTMedioPago(@SerializedName("id") var Id:String,
                       @SerializedName("nombre") var Nombre:String,
                       @SerializedName("tipo") var Tipo:String,
                       @SerializedName("proveedor") var Proveedor:String,
                       @SerializedName("limiteCuotas") var LimiteCuotas:Int,
                       @SerializedName("seleccionado") var seleccionado: Boolean)


