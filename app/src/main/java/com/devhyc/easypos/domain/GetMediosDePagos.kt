package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetMediosDePagos @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<ArrayList<DTMedioPago>>? = repository.getListaMediosDePago()
}