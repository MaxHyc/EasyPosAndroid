package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName

data class DTDocLista(@SerializedName("fecha") var Fecha:String,
                      @SerializedName("fechaEmision") var FechaEmision:String,
                      @SerializedName("fechaEntrega") var FechaEntrega:String,
                      @SerializedName("terminalCodigo") var TerminalCodigo:String,
                      @SerializedName("tipoDocCodigo") var TipoDocCodigo:String,
                      @SerializedName("nroDoc") var NroDoc:Long,
                      @SerializedName("usuario") var Usuario:String,
                      @SerializedName("funcionarioNombre") var FuncionarioNombre:String,
                      @SerializedName("clienteNombre") var ClienteNombre:String,
                      @SerializedName("clienteDoc") var ClienteDoc:String,
                      @SerializedName("serie") var Serie:String,
                      @SerializedName("numero") var Numero:Long,
                      @SerializedName("tipoCfeNombre") var TipoCfeNombre:String,
                      @SerializedName("serieCfe") var SerieCfe:String,
                      @SerializedName("nroCfe") var NroCfe:Long,
                      @SerializedName("monedaSigno") var MonedaSigno:String,
                      @SerializedName("total") var Total:Double
                      )
