package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.DTDocTransaccion
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class PostEmitirDocumento @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(documento: DTDoc): Resultado<DTDocTransaccion> = repository.postEmitirDocumento(documento)
}