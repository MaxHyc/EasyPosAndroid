package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter

data class DTCajaEstado (@SerializedName("nombre") var Nombre:String,
                         @SerializedName("cabezal") var Cabezal:DTCajaCabezal)

data class DTCajaCabezal (@SerializedName("esCierre") var EsCierre:Boolean,
                          @SerializedName("fechaHoraActual") var FechaHoraActual:String,
                          @SerializedName("usuarioLogueado") var UsuarioLogueado:String,
                          @SerializedName("nroCaja") var NroCaja:Long,
                          @SerializedName("fechaHora") var FechaHora:String,
                          @SerializedName("fechaHoraCierre") var FechaHoraCierre:String,
                          @SerializedName("terminalCodigo") var TerminalCodigo:String,
                          @SerializedName("nrosDocumentos") var NrosDocumentos:List<DTCajaNroDocumentos>,
                          @SerializedName("usuarioCaja") var UsuarioCaja:String,
                          @SerializedName("totalVentaDia") var TotalVentaDia:Double,
                          @SerializedName("totalVentaDiaME") var TotalVentaDiaME:Double,
                          @SerializedName("ventasExenta") var VentasExenta:Double,
                          @SerializedName("ventasIvaMin") var VentasIvaMin:Double,
                          @SerializedName("ventasIvaBas") var VentasIvaBas:Double,
                          @SerializedName("totalIvaDia") var TotalIvaDia:Double)

data class DTCajaNroDocumentos (@SerializedName("nombre") var Nombre:String,
                                @SerializedName("nroDesde") var NroDesde:Long,
                                @SerializedName("nroHasta") var NroHasta:Long
                                )

data class DTCajaMovimiento (@SerializedName("nro") var Nro:Long,
                             @SerializedName("monedaCodigo") var MonedaCodigo:Long,
                             @SerializedName("monto") var Monto:Long
                             )

data class DtCajaPago (@SerializedName("numero") var Numero:Int,
                       @SerializedName("monedaCodigo") var MonedaCodigo:String,
                       @SerializedName("tipoCambio") var TipoCambio:Double,
                       @SerializedName("monto") var Monto:Double,
                       @SerializedName("medioPago") var MedioPago:Int,
                       )


data class DTCajaDocumento (@SerializedName("terminalCodigo") var TerminalCodigo: String,
                            @SerializedName("tipoDocCodigo") var TipoDocCodigo: String,
                            @SerializedName("nroDoc") var NroDoc: Long,
                            @SerializedName("fecha") var Fecha: String,
                            @SerializedName("hora") var Hora: String,
                            @SerializedName("monedaSigno") var MonedaSigno: String,
                            @SerializedName("totalDocumento") var TotalDocumento: Double
                            //@SerializedName("pagos") var Pagos: List<DtCajaPago>
                            )
