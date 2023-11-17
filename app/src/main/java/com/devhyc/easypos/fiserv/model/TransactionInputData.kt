package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import java.math.BigDecimal

data class TransactionInputData (@SerializedName("transactionType") var transactionType:TransactionTypes,
                                 @SerializedName("amount") var amount:BigDecimal= BigDecimal(0),
                                 @SerializedName("invoiceId") var invoiceId:Int?,
                                 @SerializedName("currency") var currency:Int?,
                                 @SerializedName("acquirerId") var acquirerId:Int?
                                 )