package com.devhyc.easypos.domain
import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocTransaccion
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class GetConsultarTransaccion @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(numero: String): Resultado<DTDocTransaccion>? = repository.getConsultarTransaccion(numero)
}
