package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetImpresionUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String,tipoDoc:String,nroDoc:Long): Resultado<DTImpresion>? = repository.getImpresion(nroTerminal, tipoDoc, nroDoc)
}