package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetImpresionCierreCajaUseCase  @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String,nroCaja:String,usuario:String): Resultado<DTImpresion>? = repository.getImpresionCierreCaja(nroTerminal, nroCaja, usuario)
}