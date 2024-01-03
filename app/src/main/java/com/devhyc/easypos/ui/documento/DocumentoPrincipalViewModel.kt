package com.devhyc.easypos.ui.documento

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easymanagementmobile.domain.*
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentoPrincipalViewModel @Inject constructor(val postCalcularDocumento: PostCalcularDocumento, val postValidarDocumento: PostValidarDocumento, val getClienteXCodigo: GetClienteXCodigo, val getClienteXIdUseCase: GetClienteXIdUseCase, val getListadoDeFuncionariosUseCase: GetListadoDeFuncionariosUseCase, val getFuncionarioXIdUseCase: GetFuncionarioXIdUseCase,val getNuevoDocumentoUseCase: GetNuevoDocumentoUseCase,val getParametrosTipoDocUseCase: GetParametrosTipoDocUseCase, val postEmitirDocumento: PostEmitirDocumento, val getConsultarTransaccion: GetConsultarTransaccion,val getCajaAbiertaUseCase: GetCajaAbiertaUseCase): ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()

    fun ObtenerCajaAbierta()
    {
        viewModelScope.launch {
            try
            {
                isLoading.postValue(true)
                val result = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                if (result!!.ok)
                {
                    if(result!!.elemento == null)
                        caja.postValue(null)
                    else
                    {
                        caja.postValue(result.elemento!!)
                    }
                }
                else
                {
                    caja.postValue(null)
                }
            }
            catch (e:Exception)
            {
                mensajeDelServer.postValue(e.message)
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }
}