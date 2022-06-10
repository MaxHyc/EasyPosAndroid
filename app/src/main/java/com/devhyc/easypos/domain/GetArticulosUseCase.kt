package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import java.util.ArrayList
import javax.inject.Inject

class GetArticulosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(cantidad:Int,listaprecio:String): Resultado<ArrayList<DTArticulo>>? = repository.getListarArticulos(cantidad,listaprecio)
}