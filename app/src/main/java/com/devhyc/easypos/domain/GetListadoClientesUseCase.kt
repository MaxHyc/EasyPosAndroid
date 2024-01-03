package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetListadoClientesUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<ArrayList<DTCliente>>? = repository.getListadoClientes()
}