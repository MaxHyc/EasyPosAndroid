package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTFamiliaPadre
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetFamiliasUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<List<DTFamiliaPadre>> = repository.getFamilias()
}