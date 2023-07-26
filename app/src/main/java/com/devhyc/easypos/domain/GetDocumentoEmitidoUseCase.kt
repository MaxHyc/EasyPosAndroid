package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetDocumentoEmitidoUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(terminal:String,tipoDoc:String,nroDoc:String): Resultado<DTDoc> = repository.getDocumentoEmitido(terminal,tipoDoc, nroDoc)
}