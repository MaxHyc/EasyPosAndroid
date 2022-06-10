package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTLogin(@SerializedName("usuario") var usuario:String,
                   @SerializedName("nombre") var nombre:String,
                   @SerializedName("apellido") var apellido:String,
                   @SerializedName("funcionarioId") var funcionarioId:Int,
                   @SerializedName("perfilId") var perfilId:Int,
                   @SerializedName("token") var token:String)
