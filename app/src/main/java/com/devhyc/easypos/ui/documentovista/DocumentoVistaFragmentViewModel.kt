package com.devhyc.easypos.ui.documentovista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.DTDocTotales
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.domain.GetDocumentoEmitidoUseCase
import com.devhyc.easypos.domain.GetImpresionUseCase
import com.devhyc.easypos.domain.GetMediosDePagos
import com.devhyc.easypos.domain.PostCalcularDocumento
import com.devhyc.easypos.utilidades.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DocumentoVistaFragmentViewModel @Inject constructor(val getDocumentoEmitidoUseCase: GetDocumentoEmitidoUseCase, val postCalcularDocumento: PostCalcularDocumento, val getImpresionUseCase: GetImpresionUseCase,val getMediosDePagos: GetMediosDePagos) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val MensajeServer = SingleLiveEvent<String>()
    val MensajeErrorLocal = SingleLiveEvent<String>()
    val DocumentoObtenido = MutableLiveData<DTDoc>()
    val DocumentoCalculos = MutableLiveData<DTDocTotales>()
    val Impresion = MutableLiveData<DTImpresion>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()

    fun ObtenerDocumentoEmitido(terminal: String,tipoDocumento:String,NroDoc:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getDocumentoEmitidoUseCase(terminal,tipoDocumento,NroDoc)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            DocumentoObtenido.postValue(result.elemento!!)
                        }
                        else
                        {
                            MensajeServer.postValue(result.mensaje)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeErrorLocal.postValue(e.message)
        }
    }

    fun CalcularDoc(doc:DTDoc) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = postCalcularDocumento(doc)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            DocumentoCalculos.postValue(result.elemento!!)
                        }
                        else
                        {
                            MensajeServer.postValue(result.mensaje)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeErrorLocal.postValue(e.message)
        }
    }

    fun ObtenerImpresion(terminal: String,tipoDocumento:String,NroDoc:Long) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getImpresionUseCase(terminal,tipoDocumento,NroDoc)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            Impresion.postValue(result.elemento!!)
                        }
                        else
                        {
                            MensajeServer.postValue(result.mensaje)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeErrorLocal.postValue(e.message)
        }
    }

    fun ListarMediosDePago() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getMediosDePagos()
                    if (result != null) {
                        if (result.ok) {
                            LMedioPago.postValue(result.elemento!!)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        } catch (e: Exception) {
            MensajeErrorLocal.postValue(e.message)
        }
    }
}