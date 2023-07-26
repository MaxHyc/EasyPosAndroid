package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocTipos
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetTiposDeDocumentos @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario: String): Resultado<DTDocTipos>? = repository.getListasDeTiposDocumentos(usuario)
}