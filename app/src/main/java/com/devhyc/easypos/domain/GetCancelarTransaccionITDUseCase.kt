package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDConfiguracion
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import javax.inject.Inject

class GetCancelarTransaccionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal: String, nroTransaccion: String,proveedor:String,confirm:Boolean): Resultado<ITDRespuesta>? = repository.getCancelarTransaccionITD(nroTerminal,nroTransaccion,proveedor, confirm)
}