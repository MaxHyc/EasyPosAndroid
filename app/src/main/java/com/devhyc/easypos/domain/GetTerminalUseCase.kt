package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTTerminalPos
import javax.inject.Inject

class GetTerminalUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String): Resultado<DTTerminalPos>? = repository.getTerminal(nroTerminal)
}