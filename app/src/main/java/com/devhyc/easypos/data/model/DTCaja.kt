package com.integration.easyposkotlin.data.model

import com.google.gson.annotations.SerializedName

data class DTCaja(@SerializedName("fechaHora") var FechaHora:String,
                  @SerializedName("nro") var Nro:Int,
                  @SerializedName("usuario") var Usuario:String)
