package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import java.util.ArrayList
import javax.inject.Inject

class GetArticulosRubrosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<ArrayList<DTRubro>>? = repository.getListarArticulosRubros()
}