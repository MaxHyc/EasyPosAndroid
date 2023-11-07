package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTCajaDocumento
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetDocumentosPorTerminalYCajaUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(terminal:String,nroCaja:String): Resultado<List<DTCajaDocumento>> = repository.getDocumentosPorTerminalYCaja(terminal,nroCaja)
}