package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDIssuer (@SerializedName("codigo") var Codigo:String,
                 @SerializedName("nombre") var Nombre:String,
                 @SerializedName("aquirerCodigo") var AquirerCodigo:String,
                 @SerializedName("tipo") var Tipo:Int
                 ){
}