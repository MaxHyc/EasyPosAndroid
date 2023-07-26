package com.devhyc.easypos.data.model

import com.devhyc.easypos.data.model.DTMoneda
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

data class DTDocParametros(@SerializedName("codigo") var Codigo:String,
                           @SerializedName("descripcion") var Descripcion:String,
                           @SerializedName("modificadores") var Modificadores:DTDocParamMod,
                           @SerializedName("validaciones") var Validaciones:DTDocParamVal,
                           @SerializedName("configuraciones") var Configuraciones:DTDocParamConf)

data class DTDocParamMod (@SerializedName("modificaFecha") var ModificaFecha:Boolean,
                          @SerializedName("modificaPrecio") var ModificaPrecio:Boolean,
                          @SerializedName("modificaMoneda") var ModificaMoneda:Boolean,
                          @SerializedName("modificaTipoCambio") var ModificaTipoCambio:Boolean,
                          @SerializedName("modificaDescuento") var ModificaDescuento:Boolean,
                          @SerializedName("modificaFormaPago") var ModificaFormaPago:Boolean,
                          @SerializedName("lugarDeEntrega") var LugarDeEntrega:Boolean,
                          @SerializedName("fechaEntrega ") var FechaEntrega:Boolean,
                          @SerializedName("enviaMail_No_Uncheck_Check") var EnviaMailCheck:Int
                          )

data class DTDocParamVal (@SerializedName("valorizado") var Valorizado:Boolean,
                          @SerializedName("conImpuestos") var ConImpuestos:Boolean,
                          @SerializedName("controlaSerie") var ControlaSerie:Boolean,
                          @SerializedName("esPreVenta") var EsPreVenta:Boolean,
                          @SerializedName("conFirma") var ConFirma:Boolean)

data class DTDocParamConf (@SerializedName("tipoCodigo") var TipoCodigo:Int,
                           @SerializedName("modificaLista") var ModificaLista:Boolean,
                           @SerializedName("listaPrecioCodigo") var ListaPrecioCodigo:String,
                           @SerializedName("afectacion") var Afectacion:Int,
                           @SerializedName("tipoCambio") var TipoCambio:Double,
                           @SerializedName("monedaCodigo") var MonedaCodigo:String,
                           @SerializedName("monedas") var Monedas:List<DTMoneda>,
                           @SerializedName("clienteAsocia") var ClienteAsocia:Boolean,
                           @SerializedName("clienteAsociaTipo") var ClienteAsociaTipo:Int,
                           @SerializedName("clienteDefID") var ClienteDefID:Long,
                           @SerializedName("cuentaCorrienteAfecta") var CuentaCorrienteAfecta:Boolean,
                           @SerializedName("cuentaCorrienteSuma") var CuentaCorrienteSuma:Boolean,
                           @SerializedName("stockAfecta") var StockAfecta:Boolean,
                           @SerializedName("stockValida") var StockValida:Boolean,
                           @SerializedName("stockModificaDeposito") var StockModificaDeposito:Boolean,
                           @SerializedName("stockSuma") var StockSuma:Boolean,
                           @SerializedName("stockDepositoCodigo") var StockDepositoCodigo:String,
                           @SerializedName("medioPagoAplica") var MedioPagoAplica:Boolean,
                           @SerializedName("medioPagoDefID") var MedioPagoDefID:Int,
                           @SerializedName("funcionarioAsocia") var FuncionarioAsocia:Boolean,
                           @SerializedName("funcionarioPerfil") var FuncionarioPerfil:Int,
                           @SerializedName("funcionarioDefID") var FuncionarioDefID:Int)
