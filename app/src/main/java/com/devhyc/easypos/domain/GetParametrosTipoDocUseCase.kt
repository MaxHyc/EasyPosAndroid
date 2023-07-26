package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocParametros
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetParametrosTipoDocUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(usuario:String,tipoDoc:String): Resultado<DTDocParametros>? = repository.getParametrosDocumento(usuario,tipoDoc)
}