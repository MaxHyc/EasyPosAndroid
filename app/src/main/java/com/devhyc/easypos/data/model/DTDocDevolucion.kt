package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocDevolucion(@SerializedName("usuario") var Usuario:String,
                           @SerializedName("terminalCodigo") var TerminalCodigo:String,
                           @SerializedName("tipoDocCodigo") var TipoDocCodigo:String,
                           @SerializedName("devolverTerminalCodigo") var DevolverTerminalCodigo:String,
                           @SerializedName("devolverTipoDocCodigo") var DevolverTipoDocCodigo:String,
                           @SerializedName("devolverNroDoc") var DevolverNroDoc:Long,
                           @SerializedName("anulacionCompleta") var AnulacionCompleta:Boolean,
                           @SerializedName("items") var Items:ArrayList<DTDocDetalle>?,
                           @SerializedName("pagos") var Pagos:ArrayList<DTDocPago>?
                           )
