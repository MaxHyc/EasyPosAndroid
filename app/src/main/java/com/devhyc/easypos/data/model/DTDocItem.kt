package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocItem(@SerializedName("id") var id:String,
                     @SerializedName("nombre") var nombre:String,
                     @SerializedName("cantidad") var cantidad:Double,
                     @SerializedName("precio") var precio:Double
                     )
