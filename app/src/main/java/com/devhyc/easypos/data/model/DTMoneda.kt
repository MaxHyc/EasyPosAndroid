package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTMoneda(@SerializedName("codigo") var codigo:String,
               @SerializedName("nombre") var nombre:String,
               @SerializedName("signo") var signo:String) {
}