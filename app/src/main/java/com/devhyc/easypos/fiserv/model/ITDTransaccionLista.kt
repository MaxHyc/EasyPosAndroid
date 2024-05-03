package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDTransaccionLista(@SerializedName("consultaIsEnabled") var ConsultaIsEnabled:Boolean,
                               @SerializedName("transaccionId") var TransaccionId:String,
                               @SerializedName("terminalCodigo") var TerminalCodigo:String,
                               @SerializedName("tipodocCodigo") var TipodocCodigo:String,
                               @SerializedName("nroDoc") var NroDoc:Long,
                               @SerializedName("documento") var Documento:String,
                               @SerializedName("estado") var Estado:Int,
                               @SerializedName("monedaSigno") var MonedaSigno:String,
                               @SerializedName("monto") var Monto:Double,
                               @SerializedName("fechaHora") var FechaHora:String,
                               @SerializedName("tipo") var Tipo:String)
{

}
enum class ITDEstadoTransaccion(val value: Int) {
    CONERROR(0),
    APROBADA(1),
    REVERSADA(2),
    ANULADA(3),
    CANCELADA(4)
}