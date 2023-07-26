package com.devhyc.easymanagementmobile.data.model

import com.google.gson.annotations.SerializedName

data class DTUserControlLogin(@SerializedName("userName") var userName:String,
                         @SerializedName("urlServicio") var urlServicio:String,
                         @SerializedName("sistemaUsuario") var sistemaUsuario:String,
                         @SerializedName("sistemaPass") var sistemaPass:String,
                         @SerializedName("terminalCodigo") var terminalCodigo:String,
                         @SerializedName("modulos") var modulos:ArrayList<DTUserControlModulo>)

data class DTUserControlModulo(@SerializedName("nombre") var nombre:String,
                               @SerializedName("activo") var activo:Boolean)
