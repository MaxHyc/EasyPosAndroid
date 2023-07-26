package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTCliente (@SerializedName("id") var id:Long,
                      @SerializedName("codigo") var codigo:String,
                      @SerializedName("nombre") var nombre:String,
                      @SerializedName("tipoDocumento") var tipoDocumento:Int,
                      @SerializedName("documento") var documento:String,
                      @SerializedName("razonSocial") var razonSocial:String,
                      @SerializedName("direccion") var direccion:String,
                      @SerializedName("ciudad") var ciudad:String,
                      @SerializedName("listaPrecioCodigo") var listaPrecioCodigo:String,
                      @SerializedName("descuentoGeneral") var descuentoGeneral:Double,
                      @SerializedName("telefono") var telefono:String,
                      @SerializedName("formaPago") var FormaPago:DTFormaPago?,
                      @SerializedName("enviarFactura") var EnviarFactura:Boolean,
                      @SerializedName("enviarFacturaMail") var EnviarFacturaMail:String
                      ) {
    constructor(): this(0,"","",0,"","","","","",0.0,"",null,false,"")
}