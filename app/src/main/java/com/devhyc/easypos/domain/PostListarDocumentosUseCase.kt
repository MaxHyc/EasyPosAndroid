package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocLista
import com.devhyc.easypos.data.model.DTParamDocLista
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class PostListarDocumentosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(parametros: DTParamDocLista,terminal:String): Resultado<List<DTDocLista>> =
        repository.postListarDocumentos(parametros, terminal)
}