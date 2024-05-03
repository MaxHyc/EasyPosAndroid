package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDTransaccionAnular(@SerializedName("transaccionId") var TransaccionId:String,
                                @SerializedName("terminalCodigo") var TerminalCodigo:String,
                                @SerializedName("tipoDocCodigo") var TipoDocCodigo:String,
                                @SerializedName("funcionarioId") var FuncionarioId:String,
                                @SerializedName("sucursalCodigo") var SucursalCodigo:String)
