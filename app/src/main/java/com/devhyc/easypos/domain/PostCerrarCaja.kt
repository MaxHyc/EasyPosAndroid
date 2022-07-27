package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTIngresoCaja
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTCaja
import java.util.ArrayList
import javax.inject.Inject

class PostCerrarCaja @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(totalesDeclarados: DTTotalesDeclarados): Resultado<DTCaja>? = repository.postCerrarCaja(totalesDeclarados)
}