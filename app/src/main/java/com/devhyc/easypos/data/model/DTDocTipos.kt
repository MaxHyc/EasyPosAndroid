package com.devhyc.easypos.data.model

import com.google.gson.annotations.SerializedName
import com.integration.easyposkotlin.data.model.Documento
import com.integration.easyposkotlin.data.model.UsuariosSupervisor

data class DTDocTipos (@SerializedName("clientes") var Clientes:List<DTDocParametros>,
                       @SerializedName("proveedores") var Proveedores:List<DTDocParametros>,
                       @SerializedName("movimientos") var Movimientos:List<DTDocParametros>) {
}