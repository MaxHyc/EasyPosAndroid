package com.devhyc.easypos.ui.listadodocumentos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDocLista
import com.devhyc.easypos.data.model.DTParamDocLista
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.domain.PostListarDocumentosUseCase
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaDeDocumentosViewModel @Inject constructor(val postListarDocumentosUseCase: PostListarDocumentosUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val ListadoDocs = MutableLiveData<Resultado<List<DTDocLista>>>()

    fun ListarDocumentos(parametros:DTParamDocLista)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = postListarDocumentosUseCase(parametros, Globales.Terminal.Codigo)
                if (result != null)
                {
                    ListadoDocs.postValue(result)
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
