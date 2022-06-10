package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import java.util.ArrayList
import javax.inject.Inject

class GetArticulosFiltradoUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(cantidad:Int,listaprecio:String,tipoBusqueda:Int,filtro:String): Resultado<ArrayList<DTArticulo>>? = repository.getArticulosFiltrado(cantidad,listaprecio,tipoBusqueda,filtro)
}