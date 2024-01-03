package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTImpresionCPCL(@SerializedName("documentoCpcl") var DocumentoCpcl: String,
                      @SerializedName("imagenQr") var ImagenQr: String?,
                      @SerializedName("datosDgiCpcl") var DatosDgiCpcl: String?,
                      @SerializedName("imagenFirma") var ImagenFirma: String?,
                      @SerializedName("aclaracionFirmaCpcl") var AclaracionFirmaCpcl: String?,
                      @SerializedName("vias") var Vias: Int
                      )
{

}