package com.devhyc.easypos.ui.articulos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTFamiliaPadre
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.domain.GetArticulosFiltradosUseCase
import com.devhyc.easypos.domain.GetArticulosRubrosUseCase
import com.devhyc.easypos.domain.GetFamiliasUseCase
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListaDeArticulosViewModel @Inject constructor(val getFamiliasUseCase: GetFamiliasUseCase, val getArticulosFiltradosUseCase: GetArticulosFiltradosUseCase,val getArticulosRubrosUseCase: GetArticulosRubrosUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val ListaFamilias = MutableLiveData<List<DTFamiliaPadre>>()
    val ListaArticulos = MutableLiveData<List<DTArticulo>>()
    val mensajeDelServer = SingleLiveEvent<String>()
    val mensajeDeError = SingleLiveEvent<String>()
    val ListaRubros = MutableLiveData<ArrayList<DTRubro>>()

    fun CargarFamilias() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getFamiliasUseCase()
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            ListaFamilias.postValue(result.elemento!!)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mensajeDeError.postValue(e.message)
        }
    }

    fun CargarArticulos(cantidad:Int,listaPecio:String,tipo:Int,valorBusqueda:String)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getArticulosFiltradosUseCase(cantidad, listaPecio,tipo,valorBusqueda)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            ListaArticulos.postValue(result.elemento!!)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mensajeDeError.postValue(e.message)
        }
    }

    fun CargarRubros() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    val result = getArticulosRubrosUseCase()
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            ListaRubros.postValue(result.elemento!!)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mensajeDeError.postValue(e.message)
        }
    }
}