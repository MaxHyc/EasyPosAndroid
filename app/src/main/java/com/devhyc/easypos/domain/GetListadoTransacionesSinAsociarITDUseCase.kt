package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista
import javax.inject.Inject

class GetListadoTransacionesSinAsociarITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String): Resultado<ArrayList<ITDTransaccionLista>>? = repository.getTransaccionesSinAsociarITD(nroTerminal)
}