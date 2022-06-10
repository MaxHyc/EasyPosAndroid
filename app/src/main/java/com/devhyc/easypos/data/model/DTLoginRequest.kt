package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTLoginRequest(@SerializedName("UserName") var UserName:String,
                          @SerializedName("Password") var Password:String
                          )
