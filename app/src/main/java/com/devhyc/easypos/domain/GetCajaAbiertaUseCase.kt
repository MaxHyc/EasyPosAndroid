package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTTerminalPos
import javax.inject.Inject

class GetCajaAbiertaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String): Resultado<DTCaja>? = repository.getCajaAbierta(nroTerminal)
}