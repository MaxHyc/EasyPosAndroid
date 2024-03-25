package com.devhyc.easypos.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class DTDocPago(@SerializedName("medioPagoCodigo") var medioPagoCodigo:Int,
                     @SerializedName("monedaCodigo") var monedaCodigo: String?,
                     @SerializedName("tipoCambio") var tipoCambio:Double,
                     @SerializedName("importe") var importe:Double,
                     @SerializedName("numero") var numero: String?,
                     @SerializedName("fecha") var fecha:String?,
                     @SerializedName("fechaVto") var fechaVto:String?,
                     @SerializedName("bancoCodigo") var bancoCodigo: String?,
                     @SerializedName("tarjetaCodigo") var tarjetaCodigo: String?,
                     @SerializedName("cuotas") var cuotas:Int,
                     @SerializedName("autorizacion") var autorizacion: String?,
                     @SerializedName("transaccion") var transaccion: String?,
                     @SerializedName("voucher") var Vaucher: DTDocVaucher?
                     ) : Parcelable
{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        TODO("Vaucher")
    ) {
    }

    constructor() : this(0,"",0.0,0.0,"","","","","",0,"","",null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(medioPagoCodigo)
        parcel.writeString(monedaCodigo)
        parcel.writeDouble(tipoCambio)
        parcel.writeDouble(importe)
        parcel.writeString(numero)
        parcel.writeString(fecha)
        parcel.writeString(fechaVto)
        parcel.writeString(bancoCodigo)
        parcel.writeString(tarjetaCodigo)
        parcel.writeInt(cuotas)
        parcel.writeString(autorizacion)
        parcel.writeString(transaccion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DTDocPago> {
        override fun createFromParcel(parcel: Parcel): DTDocPago {
            return DTDocPago(parcel)
        }

        override fun newArray(size: Int): Array<DTDocPago?> {
            return arrayOfNulls(size)
        }
    }
}
