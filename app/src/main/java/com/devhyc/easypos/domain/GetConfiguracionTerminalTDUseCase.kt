package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocTipos
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDConfiguracion
import javax.inject.Inject

class GetConfiguracionTerminalTDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal: String): Resultado<ITDConfiguracion>? = repository.getConfiguracionTerminalITD(nroTerminal)
}