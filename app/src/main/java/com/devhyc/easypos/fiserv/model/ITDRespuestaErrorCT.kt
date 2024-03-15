package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDRespuestaErrorCT(@SerializedName("codigo") var Codigo:String,
                               @SerializedName("descripcion") var Descripcion:String)
