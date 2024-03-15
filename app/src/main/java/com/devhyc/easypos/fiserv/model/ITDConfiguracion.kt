package com.devhyc.easypos.fiserv.model

import com.google.gson.annotations.SerializedName

data class ITDConfiguracion(@SerializedName("fechaHora") var FechaHora:String,
                            @SerializedName("terminalFiserv") var TerminalFiserv:String,
                            @SerializedName("systemId") var SystemId:String,
                            @SerializedName("leyAplica") var LeyAplica:Int,
                            @SerializedName("urlServicio") var UrlServicio:String,
                            @SerializedName("mensajesPos") var MensajesPos:ArrayList<ITDRespuestaPos>,
                            @SerializedName("mensajesError") var MensajesError:ArrayList<ITDRespuestaErrorCT>,
                            @SerializedName("issuers") var Issuers:ArrayList<ITDIssuer>)