package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocTipos
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetTestDeConexionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal: String): Resultado<Boolean> = repository.getTestDeConexionITD(nroTerminal)
}