package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTFamiliaPadre(@SerializedName("id") var id:Long,
                          @SerializedName("codigo") var codigo:String,
                          @SerializedName("nombre") var nombre:String,
                          @SerializedName("valor") var valor:Int,
                          @SerializedName("subfamilias") var subfamilias:List<DTFamiliaHija>
                          )

data class DTFamiliaHija(@SerializedName("id") var id:Long,
                         @SerializedName("codigo") var codigo:String,
                         @SerializedName("nombre") var nombre:String,
                         @SerializedName("valor") var valor:Int,
                         @SerializedName("padreCodigo") var padreCodigo:String,
                         @SerializedName("subfamilias") var subfamilias:List<DTFamiliaHija>
                         )