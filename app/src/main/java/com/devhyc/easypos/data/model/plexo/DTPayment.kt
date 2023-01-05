package com.devhyc.easypos.data.model.plexo

import com.google.gson.annotations.SerializedName

data class DTPayment(@SerializedName("legacyId") var Id:Int,
                     @SerializedName("name") var Nombre:String,
                     @SerializedName("pictureUrl") var Imagen:String,
                     @SerializedName("type") var Type:String,
                     @SerializedName("settings") var Settings:DTSettingsPayment,
                     @SerializedName("banks") var Banks:List<DTBanks>,
                     @SerializedName("paymentProcessors") var PaymentProcessors:List<DTPaymentProcessors>
                     )
data class DTSettingsPayment(@SerializedName("bin") var bin:DTBin,
                             @SerializedName("cardNumber") var cardNumber:DTCardNumber,
                             @SerializedName("securityCode") var securityCode:DTSecurityCode,
                             @SerializedName("currencies") var currencies:List<DTCurrencies>
                             )

data class DTBin(@SerializedName("pattern") var pattern:String)

data class DTCardNumber(@SerializedName("length") var length:Int,
                        @SerializedName("luhn") var luhn:Boolean
                        )

data class DTSecurityCode(@SerializedName("length") var length:Int,
                          @SerializedName("required") var required:Boolean
                          )

data class DTCurrencies(@SerializedName("id") var id:String,
                        @SerializedName("isoCode") var isoCode:String,
                        @SerializedName("name") var name:String,
                        @SerializedName("plural") var plural:String,
                        @SerializedName("symbol") var symbol:String
)

data class DTBanks(@SerializedName("id") var id:Int,
                   @SerializedName("name") var name:String,
                   @SerializedName("shortName") var shortName:String,
                   @SerializedName("pictureUrl") var pictureUrl:String,
                   @SerializedName("externalId") var externalId:String
                   )

data class DTPaymentProcessors(@SerializedName("id") var id:Int,
                               @SerializedName("acquirer") var acquirer:Int,
                               @SerializedName("settings") var settings:DTSettingsPaymentProcessors
                               )

data class DTSettingsPaymentProcessors(@SerializedName("fields") var fields:List<DTFields>,
                                       @SerializedName("fingerprint") var fingerprint:DTFingerPrint,
                                       @SerializedName("cardholderFields") var cardholderFields:List<DTCardHolderFields>
)

data class DTFields(@SerializedName("id") var id:Int,
                    @SerializedName("label") var label:String,
                    @SerializedName("name") var name:String,
                    @SerializedName("description") var description:String,
                    @SerializedName("required") var required:Boolean
                    )

data class DTFingerPrint(@SerializedName("name") var name:String)

data class DTCardHolderFields(@SerializedName("id") var id:String)


