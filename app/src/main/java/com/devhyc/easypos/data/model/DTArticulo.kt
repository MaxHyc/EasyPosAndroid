package com.integration.easyposkotlin.data.model

import com.google.gson.annotations.SerializedName

data class DTArticulo(@SerializedName("id") var id:Int,
                      @SerializedName("nombre") var nombre:String,
                      @SerializedName("codigo") var codigo:String,
                      @SerializedName("familia") var familia:Int,
                      @SerializedName("familiaNombre") var familianombre:String,
                      @SerializedName("familiaCodigo") var familiaCodigo:String,
                      @SerializedName("utilidad") var utilidad:Int,
                      @SerializedName("impuesto") var impuesto:Int,
                      @SerializedName("impuestoValor") var impuestoValor:Int,
                      @SerializedName("descripcion1") var descripcion1:String,
                      @SerializedName("descripcion2") var descripcion2:String,
                      @SerializedName("descripcion3") var descripcion3:String,
                      @SerializedName("preferido") var preferido:Int,
                      @SerializedName("descontinuado") var descontinuado:Int,
                      @SerializedName("grupo") var grupo:String,
                      @SerializedName("modificado") var modificado:String,
                      @SerializedName("esRubro") var esRubro:Int,
                      @SerializedName("vtoDias") var vtoDias:Int,
                      @SerializedName("fob") var fob:Int,
                      @SerializedName("costo") var costo:Int,
                      @SerializedName("moneda") var moneda:String,
                      @SerializedName("precioSinImp") var precioSinImp:Double,
                      @SerializedName("precioFinal") var precioFinal:Double,
                      @SerializedName("monedaSigno") var monedaSigno:String,
                      @SerializedName("codigoBarras") var codigoBarras:String,
                      @SerializedName("foto") var foto:List<Byte>?,
                      @SerializedName("usaSerie") var usaSerie:Boolean) {
    constructor() : this(0,"","",0,"","",0,0,0,"","","",0,0,"","",0,0,0,0,"",0.0,0.0,"","",null,false)
}
