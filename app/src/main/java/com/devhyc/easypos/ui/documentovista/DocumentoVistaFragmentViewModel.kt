package com.devhyc.easypos.ui.documentovista

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDoc
import com.devhyc.easypos.data.model.DTDocTotales
import com.devhyc.easypos.domain.GetDocumentoEmitidoUseCase
import com.devhyc.easypos.domain.PostCalcularDocumento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentoVistaFragmentViewModel @Inject constructor(val getDocumentoEmitidoUseCase: GetDocumentoEmitidoUseCase, val postCalcularDocumento: PostCalcularDocumento) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val MensajeServer = MutableLiveData<String>()
    val DocumentoObtenido = MutableLiveData<DTDoc>()
    val DocumentoCalculos = MutableLiveData<DTDocTotales>()

    fun ObtenerDocumentoEmitido(terminal: String,tipoDocumento:String,NroDoc:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
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
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }

    fun CalcularDoc(doc:DTDoc) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
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
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }
}