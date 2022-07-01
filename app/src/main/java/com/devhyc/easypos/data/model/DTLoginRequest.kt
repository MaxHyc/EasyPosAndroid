package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTLoginRequest(@SerializedName("userName") var UserName:String,
                          @SerializedName("password") var Password:String
                          )
