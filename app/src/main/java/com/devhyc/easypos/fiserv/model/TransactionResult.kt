package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TransactionResult(@SerializedName("approvalCode") var approvalCode:String,
                             @SerializedName("authorizedAmount") var authorizedAmount:BigDecimal?,
                             @SerializedName("descriptionCode") var descriptionCode:String?
                             )
