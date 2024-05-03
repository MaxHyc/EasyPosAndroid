package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista
import javax.inject.Inject

class GetListadoTransaccionesITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String,nroCaja:Long): Resultado<ArrayList<ITDTransaccionLista>>? = repository.getListarTransaccionesITD(nroTerminal, nroCaja)
}