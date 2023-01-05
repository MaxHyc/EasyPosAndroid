package com.devhyc.easypos.data.model.plexo

import com.google.gson.annotations.SerializedName

data class DTPaymentRequest(@SerializedName("ReferenceId") var ReferenceId:String,
                            @SerializedName("InvoiceNumber") var InvoiceNumber:String,
                            @SerializedName("PaymentMethod") var PaymentMethod:DTPaymentMethod,
                            @SerializedName("Items") var Items:List<DTItemsPayment>,
                            @SerializedName("Installments") var Installments:Int,
                            @SerializedName("Amount") var Amount:DTAmount,
                            @SerializedName("BrowserDetails") var BrowserDetails:List<DTBrowserDetails>,
                            @SerializedName("StatementDescriptor") var StatementDescriptor:String,
                            @SerializedName("MerchantId") var MerchantId:Int
                            /*@SerializedName("Capture") var Capture:String,
                            @SerializedName("Expiration") var DateTime:String,
                            @SerializedName("CustomerId") var CustomerId:String,
                            @SerializedName("ReturnUrl") var ReturnUrl:String,
                            @SerializedName("CallbackUrl") var CallbackUrl:String,
                            @SerializedName("Metadata") var BrowserDetails:String,*/
                            )

data class DTAmount(@SerializedName("Currency") var Currency:String,
                    @SerializedName("Total") var Total:Double,
                    @SerializedName("Details") var Details:DTAmountDetails
                    )

data class DTAmountDetails(@SerializedName("Tax") var Tax:DTAmountTax)

data class DTAmountTax(@SerializedName("Type") var Type:String,
                       @SerializedName("Rate") var Rate:Double,
                       @SerializedName("Amount") var Amount:Double,
                       @SerializedName("TipAmount") var TipAmount:Double,
                       @SerializedName("DiscountAmount") var DiscountAmount:Double,
                       @SerializedName("TaxableAmount") var TaxableAmount:Double,
                       )

data class DTPaymentMethod(@SerializedName("Type") var Type:String,
                           @SerializedName("Token") var Token:String,
                           @SerializedName("Card") var Card:DTCard,
                           @SerializedName("Fields") var Fields:List<String>
                           )

data class DTCard(@SerializedName("Number") var Number:String,
                  @SerializedName("ExpMonth") var ExpMonth:Int,
                  @SerializedName("ExpYear") var ExpYear:Int,
                  @SerializedName("Cvc") var Cvc:String,
                  @SerializedName("Cardholder") var Cardholder:DTCardHolder
                  )

data class DTCardHolder(@SerializedName("FirstName") var FirstName:String,
                        @SerializedName("LastName") var LastName:String,
                        @SerializedName("Email") var Email:String,
                        @SerializedName("Birthdate") var Birthdate:String,
                        @SerializedName("PhoneNumber") var PhoneNumber:String,
                        @SerializedName("Identification") var Identification:DTIdentificacion,
                        @SerializedName("BillinAddress") var BillinAddress:DTBillingAddress,
                        )

data class DTIdentificacion(@SerializedName("Type") var Type:Int,
                            @SerializedName("Value") var Value:String
                            )

data class DTBillingAddress(@SerializedName("City") var City:String,
                            @SerializedName("Country") var Country:String,
                            @SerializedName("Line1") var Line1:String,
                            @SerializedName("Line2") var Line2:String,
                            @SerializedName("PostalCode") var PostalCode:String,
                            @SerializedName("State") var State:String
                            )

data class DTBrowserDetails (@SerializedName("DeviceFringerprint") var DeviceFringerPrint:String,
                             @SerializedName("IpAddress") var IpAddress:String
                             )

data class DTItemsPayment (@SerializedName("ReferenceId") var ReferenceId: String,
                           @SerializedName("Name") var Name: String,
                           @SerializedName("Description") var Description: String,
                           @SerializedName("Quantity") var Quantity: Int,
                           @SerializedName("Price") var Price: Double,
                           @SerializedName("Discount") var Discount: Double,
                           @SerializedName("Tax") var Tax: DTItemTax
                           )

data class DTItemTax(@SerializedName("Rate") var Rate: Double,
                     @SerializedName("Amount") var Amount: Double
                     )
