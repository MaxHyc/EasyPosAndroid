package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista
import javax.inject.Inject

class PostConsultarEstadoTransaccionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTransaccion:String): Resultado<Boolean>? = repository.postConsultarEstadoTransaccionITD(nroTransaccion)
}