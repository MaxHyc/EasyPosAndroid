package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetListarBancosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<ArrayList<DTBanco>>? = repository.getListaBancos()
}