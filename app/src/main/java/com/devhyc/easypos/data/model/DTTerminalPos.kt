package com.integration.easyposkotlin.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DTTerminalPos(@SerializedName("codigo") var Codigo:String,
                         @SerializedName("descripcion") var Descripcion:String,
                         @SerializedName("sucursalDoc") var SucursalDoc:String,
                         @SerializedName("deposito") var Deposito:String,
                         @SerializedName("autorizaRubros") var AutorizaRubros:String,
                         @SerializedName("nroRollo") var NroRollo:String,
                         @SerializedName("documentos") var Documentos:Documento,
                         @SerializedName("usuariosSupervisor") var UsuariosSupervisor:List<UsuariosSupervisor>
                         )

data class Documento(@SerializedName("cobro") var Cobro:String,
                     @SerializedName("contado") var Contado:String,
                     @SerializedName("credito") var Credito:String,
                     @SerializedName("devContado") var DevContado:String,
                     @SerializedName("ingreso") var Ingreso:String,
                     @SerializedName("nCredito") var NCredito:String,
                     @SerializedName("otros") var Otros:List<Otro>,
                     @SerializedName("pago") var Pago:String,
                     @SerializedName("retiro") var Retiro:String)

data class Otro(@SerializedName("autoriza") var Autoriza:Boolean,
                @SerializedName("tipo") var Tipo:String)

data class UsuariosSupervisor(@SerializedName("apellido") var Apellido:String,
                     @SerializedName("funcionario") var Funcionario:Int,
                     @SerializedName("nombre") var Nombre:String,
                     @SerializedName("password") var Password:String,
                     @SerializedName("perfil") var Perfil:Int,
                     @SerializedName("username") var Username:String)
