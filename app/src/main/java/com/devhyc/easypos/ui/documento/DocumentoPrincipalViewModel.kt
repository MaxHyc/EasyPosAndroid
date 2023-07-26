package com.devhyc.easypos.ui.documento

import androidx.lifecycle.ViewModel
import com.devhyc.easymanagementmobile.domain.*
import com.devhyc.easypos.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DocumentoPrincipalViewModel @Inject constructor(val postCalcularDocumento: PostCalcularDocumento, val postValidarDocumento: PostValidarDocumento, val getClienteXCodigo: GetClienteXCodigo, val getClienteXIdUseCase: GetClienteXIdUseCase, val getListadoDeFuncionariosUseCase: GetListadoDeFuncionariosUseCase, val getFuncionarioXIdUseCase: GetFuncionarioXIdUseCase, val getNuevoDocumentoUseCase: GetNuevoDocumentoUseCase, val getParametrosTipoDocUseCase: GetParametrosTipoDocUseCase): ViewModel() {

}