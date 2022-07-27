package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTIngresoCaja(@SerializedName("terminalCodigo") var terminalCodigo:String,
                         @SerializedName("userName") var userName:String,
                         @SerializedName("monto") var monto:Double)
