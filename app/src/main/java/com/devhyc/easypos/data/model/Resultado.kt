package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class Resultado<T> (@SerializedName("ok") var ok:Boolean,
                            @SerializedName("mensaje") var mensaje:String,
                            @SerializedName("elemento") var elemento:T?)