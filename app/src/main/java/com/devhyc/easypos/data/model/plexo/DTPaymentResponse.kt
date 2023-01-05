package com.devhyc.easypos.data.model.plexo

import com.google.gson.annotations.SerializedName

data class DTPaymentResponse(@SerializedName("") var Id: String,
                             @SerializedName("") var ReferenceId: String,
                             @SerializedName("") var CreatedAt: String,
                             @SerializedName("") var UpdatedAt: String,
                             @SerializedName("") var ProcessedAt: String,
                             @SerializedName("") var ExpiresAt: String,
                             @SerializedName("") var InvoiceNumber: String,

                             )