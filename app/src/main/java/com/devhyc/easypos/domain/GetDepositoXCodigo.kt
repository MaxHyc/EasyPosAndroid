package com.devhyc.easymanagementmobile.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDeposito
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetDepositoXCodigo  @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(codigoDeposito:String): Resultado<DTDeposito>? = repository.getDepositoXCodigo(codigoDeposito)
}