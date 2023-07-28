package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocTransaccion(@SerializedName("nroTransaccion") var nroTransaccion:String,
                            @SerializedName("terminalCodigo") var terminalCodigo:String,
                            @SerializedName("tipoDocCodigo") var tipoDocCodigo:String,
                            @SerializedName("nroDoc") var nroDoc:Long,
                            @SerializedName("fechaHora") var fechaHora:String,
                            @SerializedName("finalizada") var finalizada:Boolean,
                            @SerializedName("errorCodigo") var errorCodigo:Int,
                            @SerializedName("errorMensaje") var errorMensaje:String,
                            @SerializedName("tiempoEsperaSeg") var tiempoEsperaSeg:Int
                            )
