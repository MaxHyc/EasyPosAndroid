package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTGenerico
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetListadoDeFuncionariosUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(funcionarioPerfil:Int): Resultado<ArrayList<DTGenerico>>? = repository.getListadoDeFuncionarios(funcionarioPerfil)
}