package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTImpresion(
    @SerializedName("posisionX") var posicionX: Int,
    @SerializedName("posisionY") var posicionY: Int,
    @SerializedName("masPaginas") var masPaginas: List<DTImpresion>,
    @SerializedName("textos") var textos: List<ImpresionTexto>,
    @SerializedName("imagenes") var imagenes: List<ImpresionImagen>,
    @SerializedName("lineas") var lineas: List<ImpresionLinea>,
    @SerializedName("rects") var rects: List<ImpresionRect>,
    @SerializedName("anchoHoja") var anchoHoja: Int,
    @SerializedName("caracteresXLinea") var caracteresXLinea: Int,
    @SerializedName("tipoImpresora") var tipoImpresora: Int
)

data class ImpresionLinea(
    @SerializedName("posxFinal") var posxFinal: Int,
    @SerializedName("posyFinal") var posyFinal: Int,
    @SerializedName("fuente") var fuente: String,
    @SerializedName("fuenteSize") var fuenteSize: Int,
    @SerializedName("color") var color: Int,
    @SerializedName("posx") var posx: Int,
    @SerializedName("posy") var posy: Int,
    @SerializedName("alineacion") var alineacion: Int,
    @SerializedName("grosor") var grosor: Int
)

data class ImpresionRect(
    @SerializedName("width") var width: Int,
    @SerializedName("height") var height: Int,
    @SerializedName("fuente") var fuente: String,
    @SerializedName("fuenteSize") var fuenteSize: Int,
    @SerializedName("color") var color: Int,
    @SerializedName("posx") var posx: Int,
    @SerializedName("posy") var posy: Int,
    @SerializedName("alineacion") var alineacion: Int,
    @SerializedName("grosor") var grosor: Int
)

data class ImpresionTexto(
    @SerializedName("texto") var texto: String,
    @SerializedName("fuente") var fuente: String,
    @SerializedName("fuenteSize") var fuenteSize: Int,
    @SerializedName("color") var color: Int,
    @SerializedName("posx") var posx: Int,
    @SerializedName("posy") var posy: Int,
    @SerializedName("alineacion") var alineacion: Int
)

data class ImpresionImagen(
    @SerializedName("imagen") var imagen: String,
    @SerializedName("ancho") var ancho: Int,
    @SerializedName("largo") var largo: Int,
    @SerializedName("posx") var posx: Int,
    @SerializedName("posy") var posy: Int,
    @SerializedName("alineacion") var alineacion: Int
)
