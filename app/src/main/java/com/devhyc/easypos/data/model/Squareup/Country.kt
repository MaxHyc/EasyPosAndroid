package com.devhyc.easypos.data.model.Squareup

import com.google.gson.annotations.SerializedName

data class Country(@SerializedName("name") var name:String,
                   @SerializedName("capital") var capital:String,
                   @SerializedName("population") var population:Int)
