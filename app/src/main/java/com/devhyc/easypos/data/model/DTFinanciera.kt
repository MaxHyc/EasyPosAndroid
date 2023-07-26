package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTFinanciera (@SerializedName("codigo") var Codigo:String,
                    @SerializedName("nombre") var Nombre:String,
                    @SerializedName("tipoActivo") var TipoActivo:String,
                    ) {
}