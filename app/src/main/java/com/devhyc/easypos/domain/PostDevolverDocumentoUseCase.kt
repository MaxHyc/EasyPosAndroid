package com.devhyc.easypos.domain

import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTDocDevolucion
import com.devhyc.easypos.data.model.DTDocTransaccion
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import javax.inject.Inject

data class PostDevolverDocumentoUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(docDevolucion: DTDocDevolucion): Resultado<DTDocTransaccion>? = repository.postDevolverDocumento(docDevolucion)
}
