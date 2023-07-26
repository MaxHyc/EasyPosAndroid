package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTFinanciera
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetListarFinancierasUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<ArrayList<DTFinanciera>>? = repository.getListaFinancieras()
}