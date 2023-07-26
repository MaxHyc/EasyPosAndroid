package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTFuncionario (@SerializedName("id") var id:Long,
                     @SerializedName("nombre") var nombre:String,
                     @SerializedName("apellido") var apellido:String,
                     @SerializedName("cedula") var cedula:String,
                     @SerializedName("activo") var activo:Boolean,
                     @SerializedName("telefono") var telefono:String,
                     @SerializedName("celular1") var celular1:String,
                     @SerializedName("celular2") var celular2:String,
                     @SerializedName("perfil") var perfil:Int,
                     @SerializedName("perfilObj") var perfilObj:DTFuncionarioPerfil,
                     @SerializedName("comision") var comision:Int,
                     @SerializedName("deposito") var deposito:String)
{}

data class DTFuncionarioPerfil(@SerializedName("id") var Id:String,
                                @SerializedName("nombre") var Nombre:String,
                                @SerializedName("apellido") var Apellido:String)
{}
