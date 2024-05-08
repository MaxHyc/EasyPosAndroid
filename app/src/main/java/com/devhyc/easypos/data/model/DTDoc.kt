package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDoc(@SerializedName("cabezal") var cabezal:DTDocCabezal?,
                 @SerializedName("receptor") var receptor:DTDocReceptor?,
                 @SerializedName("complemento") var complemento:DTDocComplemento?,
                 @SerializedName("valorizado") var valorizado:DTDocValorizado?,
                 @SerializedName("detalle") var detalle:List<DTDocDetalle>?,
                 @SerializedName("referencias") var referencias:List<DTDocReferencias>?
                 )
{
    constructor() : this(null,null,null,null,null,null)
}

data class DTDocCabezal(@SerializedName("terminal") var terminal:String,
                     @SerializedName("tipoDocCodigo") var tipoDocCodigo:String,
                     @SerializedName("usuario") var usuario:String,
                     @SerializedName("observaciones") var observaciones:String,
                     @SerializedName("fecha") var fecha:String?,
                     @SerializedName("nroDoc") var nroDoc:Long
) {
    constructor() : this("","","","",null,0)
}

data class DTDocReceptor(@SerializedName("clienteGenerico") var clienteGenerico:Boolean,
                      @SerializedName("clienteId") var clienteId:Long,
                      @SerializedName("clienteCodigo") var clienteCodigo:String,
                      @SerializedName("clienteNombre") var clienteNombre:String?,
                      @SerializedName("receptorTipoDoc") var receptorTipoDoc:Int,
                      @SerializedName("receptorRazon") var receptorRazon:String,
                      @SerializedName("receptorRut") var receptorRut:String,
                      @SerializedName("receptorDireccion") var receptorDireccion:String,
                      @SerializedName("receptorCiudad") var receptorCiudad:String,
                      @SerializedName("receptorPais") var receptorPais:String,
                      @SerializedName("receptorMail") var receptorMail:String,
                      @SerializedName("receptorTel") var receptorTel:String) {
    constructor() : this(false,0,"","",0,"","","","","","","")
}

data class DTDocComplemento(@SerializedName("codigoSucursal") var codigoSucursal:String,
                         @SerializedName("codigoDeposito") var codigoDeposito:String,
                         @SerializedName("compraId") var compraId:String,
                         @SerializedName("lugarEntrega") var lugarEntrega:String,
                         @SerializedName("fechaEntrega") var fechaEntrega:String?,
                         @SerializedName("funcionarioId") var funcionarioId:Int) {
    constructor() : this("","","","",null,0)
}

data class DTDocValorizado(@SerializedName("monedaCodigo") var monedaCodigo:String,
                           @SerializedName("tipoCambio") var tipoCambio:Double,
                           @SerializedName("formaPagoDias") var formaPagoDias:Int,
                           @SerializedName("listaPrecioCodigo") var listaPrecioCodigo:String,
                           @SerializedName("pagos") var pagos:ArrayList<DTDocPago>,
                           @SerializedName("importeManual") var ImporteManual:Double?)
{
    constructor() : this("1",0.0,0,"", ArrayList(),null)
}

data class DTDocDetalle(@SerializedName("articuloId") var articuloId:Long,
                        @SerializedName("codigoIngresado") var codigoIngresado:String,
                        @SerializedName("descripcion") var descripcion:String,
                        @SerializedName("descripcionAdicional") var descripcionAdicional:String,
                        @SerializedName("unidad") var unidad:String,
                        @SerializedName("cantidad") var cantidad:Double,
                        @SerializedName("precioUnitario") var precioUnitario:Double,
                        @SerializedName("impuestoCodigo") var impuestoCodigo:Int,
                        @SerializedName("impuestoTasa") var impuestoTasa:Double,
                        @SerializedName("descuentoPorc") var descuentoPorc:Double,
                        @SerializedName("noFacturable") var noFacturable:Boolean,
                        @SerializedName("promocion") var promocion:String,
                        @SerializedName("precioOriginal") var precioOriginal:Double,
                        @SerializedName("Grupos") var grupos:String,
                        @SerializedName("serie") var serie:String)
{
    constructor() : this(0,"","","","",0.0,0.0,0,0.0,0.0,false,"",0.0,"","")
}

data class DTDocReferencias(@SerializedName("terminalCodigo") var terminalCodigo:String,
                         @SerializedName("tipoDocCodigo") var tipoDocCodigo:String,
                         @SerializedName("nroDocumento") var nroDocumento:Long,
                         @SerializedName("fecha") var fecha:String,
                         @SerializedName("tipoCfe") var tipoCfe:String,
                         @SerializedName("serieCfe") var serieCfe:String,
                         @SerializedName("nroCfe") var nroCfe:Long,
                         @SerializedName("monedaCodigo") var monedaCodigo:String,
                         @SerializedName("monedaSigno") var monedaSigno:String,
                         @SerializedName("total") var total:Double)
{
    constructor() : this("","",0,"","","",0,"","",0.0)
}
