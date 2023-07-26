package com.devhyc.easypos.data.model

import com.devhyc.easypos.data.model.DTMoneda
import com.google.gson.annotations.SerializedName

data class DTGenerico(@SerializedName("id") var Id: String,
                      @SerializedName("codigo") var Codigo: String,
                      @SerializedName("descripcion") var Descripcion: String?,
                      @SerializedName("nombre") var Nombre:String?,
                      @SerializedName("apellido") var Apellido:String?,
                      @SerializedName("sucursal") var Sucursal:String?,
                      @SerializedName("dias") var Dias:Int?
                      )
