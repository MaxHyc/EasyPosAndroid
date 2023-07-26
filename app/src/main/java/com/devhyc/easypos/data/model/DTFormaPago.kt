package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTFormaPago(@SerializedName("codigo") var Codigo:String,
                       @SerializedName("nombre") var Nombre:String,
                       @SerializedName("dias") var Dias:Int
                       )
