package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

class DTFecha (@SerializedName("fechayyyyMMdd") var Fecha_yyyyMMdd:String,
               @SerializedName("fechaddMMYYYY") var FechaDD_MM_YYYY:String,
               @SerializedName("FechayyyygMMgdd") var FechayyyygMMgdd:String,
               @SerializedName("FechayyyyMMddHHmmss") var FechayyyyMMddHHmmss:String
               )
{

}