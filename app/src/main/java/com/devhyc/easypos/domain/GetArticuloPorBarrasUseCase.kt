package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import javax.inject.Inject

class GetArticuloPorBarrasUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(codigo:String,listaprecio:String): Resultado<DTArticulo>? = repository.getArticuloPorBarras(codigo,listaprecio)
}