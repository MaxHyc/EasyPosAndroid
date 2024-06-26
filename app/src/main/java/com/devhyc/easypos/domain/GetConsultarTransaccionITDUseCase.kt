package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDeposito
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import javax.inject.Inject

class GetConsultarTransaccionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTransaccion: String, proveedor:String): Resultado<ITDRespuesta>? =
        repository.getConsultarTransaccionITD(nroTransaccion,proveedor)
}