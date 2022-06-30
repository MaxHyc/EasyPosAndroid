package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTImpresora(@SerializedName("nombre") var nombre: String,
                       @SerializedName("mac") var mac: String,
                       @SerializedName("linkeada") var linkeada: Boolean,
                       @SerializedName("seleccionada") var seleccionada: Boolean,
                       @SerializedName("tipo") var tipo: Int
                       )
