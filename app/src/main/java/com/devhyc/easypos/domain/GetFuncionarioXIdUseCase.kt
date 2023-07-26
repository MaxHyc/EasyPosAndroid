package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTFuncionario
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetFuncionarioXIdUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(idFuncionario:Long): Resultado<DTFuncionario>? = repository.getFuncionarioXId(idFuncionario)
}