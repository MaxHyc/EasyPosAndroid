package com.devhyc.easypos.ui.addarticulos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.domain.GetArticuloPorBarrasUseCase
import com.devhyc.easypos.domain.GetArticuloPorCodigoUseCase
import com.devhyc.easypos.domain.GetArticuloPorSerieUseCase
import com.devhyc.easypos.domain.GetArticulosRubrosUseCase
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddArticuloFragmentViewModel @Inject constructor(val getArticuloPorBarrasUseCase: GetArticuloPorBarrasUseCase, val getArticuloPorCodigoUseCase: GetArticuloPorCodigoUseCase, val getArticuloPorSerieUseCase: GetArticuloPorSerieUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val articuloEncontrado = MutableLiveData<DTArticulo>()
    val listaSerieEncontradas = MutableLiveData<ArrayList<DTArticulo>>()
    val mostrarMensaje = SingleLiveEvent<String>()
    val mostrarErrorLocal = SingleLiveEvent<String>()
    val enfocarCodigo = MutableLiveData<Boolean>()

    fun ObtenerArticuloPorCodigo(codigo:String,listaPrecio:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getArticuloPorCodigoUseCase(codigo,listaPrecio)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            articuloEncontrado.postValue(result.elemento!!)
                        }
                        else
                        {
                            mostrarMensaje.postValue(result.mensaje)
                            enfocarCodigo.postValue(true)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message)
        }
    }

    fun ObtenerArticuloPorBarras(codigo:String,listaPrecio:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getArticuloPorBarrasUseCase(codigo,listaPrecio)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            articuloEncontrado.postValue(result.elemento!!)
                        }
                        else
                        {
                            mostrarMensaje.postValue(result.mensaje)
                            enfocarCodigo.postValue(true)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message)
        }
    }

    fun ObtenerArticuloPorSerie(codigo:String,listaPrecio:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getArticuloPorSerieUseCase(codigo,listaPrecio)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            listaSerieEncontradas.postValue(result.elemento!!)
                        }
                        else
                        {
                            mostrarMensaje.postValue(result.mensaje)
                            enfocarCodigo.postValue(false)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message)
        }
    }
}