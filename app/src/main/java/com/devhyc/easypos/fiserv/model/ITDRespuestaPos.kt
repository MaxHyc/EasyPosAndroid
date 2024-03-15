package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDRespuestaPos(@SerializedName("codigo") var Codigo:String,
                           @SerializedName("descripcion_mayor") var Descripcion_mayor:String,
                           @SerializedName("descripcion_menor") var Descripcion_menor:String
                           )
