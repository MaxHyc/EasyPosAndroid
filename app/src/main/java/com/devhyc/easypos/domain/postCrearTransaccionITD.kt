package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import javax.inject.Inject

class postCrearTransaccionITD @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(transaccion:ITDTransaccionNueva): Resultado<ITDRespuesta>? = repository.postCrearTransaccionITD(transaccion)
}