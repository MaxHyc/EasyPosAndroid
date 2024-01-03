package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocParametros
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.data.model.Squareup.Country
import javax.inject.Inject

class GetPaisesUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(): Resultado<List<Country>> = repository.getPaises()
}