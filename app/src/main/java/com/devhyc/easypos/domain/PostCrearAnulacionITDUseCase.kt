package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionAnular
import javax.inject.Inject

class PostCrearAnulacionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(transaccion:ITDTransaccionAnular): Resultado<ITDRespuesta>? = repository.postCrearAnulacionITD(transaccion)
}