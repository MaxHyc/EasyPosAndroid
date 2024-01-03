package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTIngresoCaja
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTCaja
import javax.inject.Inject

class PostIniciarCaja @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(IngresoCaja: DTIngresoCaja): Resultado<DTCaja>? = repository.postIniciarCaja(IngresoCaja)
}