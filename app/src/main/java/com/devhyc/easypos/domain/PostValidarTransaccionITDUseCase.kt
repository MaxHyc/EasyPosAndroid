package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDValidacion
import com.devhyc.easypos.fiserv.model.ITDValidacionConsulta
import javax.inject.Inject

class PostValidarTransaccionITDUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(validacionConsulta: ITDValidacionConsulta): Resultado<ITDValidacion> =
        repository.postValidarTransaccionITD(validacionConsulta)
}