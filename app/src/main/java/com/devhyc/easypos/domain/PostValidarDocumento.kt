package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class PostValidarDocumento @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(documento: DTDoc): Resultado<String> =
        repository.postValidarDocumento(documento)
}