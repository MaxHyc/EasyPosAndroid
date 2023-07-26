package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetClienteXCodigo @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(codigo: String): Resultado<DTCliente>? = repository.getClienteXCodigo(codigo)
}
