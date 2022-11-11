package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTCajaEstado
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetEstadoCaja @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(nroTerminal:String,nroCaja:String,usuario:String): Resultado<DTCajaEstado>? = repository.getCajaEstado(nroTerminal,nroCaja,usuario)
}