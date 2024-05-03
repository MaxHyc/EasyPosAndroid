package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDValidacionConsulta(@SerializedName("proveedor") var proveedor:String,
                                 @SerializedName("terminalCodigo") var terminalCodigo:String,
                                 @SerializedName("tipoDocCodigo") var tipoDocCodigo:String,
                                 @SerializedName("nroDoc") var nroDoc:Long,
                                 @SerializedName("importe") var importe:Double,
                                 @SerializedName("monedaCodigo") var monedaCodigo:String
                                )
