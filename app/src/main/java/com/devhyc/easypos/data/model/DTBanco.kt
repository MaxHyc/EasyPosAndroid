package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTBanco (@SerializedName("codigo") var Codigo:String,
               @SerializedName("nombre") var Nombre:String,
               @SerializedName("activo") var Activo:Boolean
               ) {
}