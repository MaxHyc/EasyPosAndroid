package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDeposito(@SerializedName("codigo") var Codigo:String,
                      @SerializedName("nombre") var Nombre:String,
                      @SerializedName("direccion") var Direccion:String,
                      @SerializedName("tel") var Tel:String,
                      @SerializedName("detalle") var Detalle:String,
                      @SerializedName("activo") var Activo:Boolean,
                      @SerializedName("sucursalCodigo") var SucursalCodigo:String,
                      @SerializedName("sucursalObj") var SucursalObj:DTDepositoSucursal)
{

}

data class DTDepositoSucursal(@SerializedName("codigo") var Codigo:String,
                              @SerializedName("empresaCodigo") var EmpresaCodigo:String,
                              @SerializedName("nombre") var Nombre:String)
