package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTMedioPago(@SerializedName("id") var id:String,
                       @SerializedName("nombre") var nombre:String,
                       @SerializedName("monto") var monto:Double)
