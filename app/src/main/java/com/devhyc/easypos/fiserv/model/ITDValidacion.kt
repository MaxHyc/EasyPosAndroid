package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDValidacion(@SerializedName("transaccionId") var TransaccionId:String,
                         @SerializedName("aplicaAnulacion") var AplicaAnulacion:Boolean,
                         @SerializedName("proveedor") var Proveedor:String,
                         @SerializedName("acquirer") var Acquirer:Int,
                         @SerializedName("ticketPos") var TicketPos:Int,
                         var MedioPagoId:Int
                         )
