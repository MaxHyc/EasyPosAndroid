package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import javax.inject.Inject

class GetArticulosFiltradosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(cantidad:Int,listaPrecio:String,tipo:Int,valorBusqueda:String): Resultado<ArrayList<DTArticulo>> = repository.getArticulosFiltrado(cantidad, listaPrecio, tipo, valorBusqueda)
}