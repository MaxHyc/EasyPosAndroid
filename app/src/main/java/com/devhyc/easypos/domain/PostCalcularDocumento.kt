package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.*
import javax.inject.Inject

class PostCalcularDocumento @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(documento: DTDoc): Resultado<DTDocTotales> = repository.postCalcularDocumento(documento)
}