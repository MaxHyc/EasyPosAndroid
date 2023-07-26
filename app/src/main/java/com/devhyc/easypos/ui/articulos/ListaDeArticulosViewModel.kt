package com.devhyc.easypos.ui.articulos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTFamiliaPadre
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.domain.GetArticulosFiltradosUseCase
import com.devhyc.easypos.domain.GetFamiliasUseCase
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaDeArticulosViewModel @Inject constructor(val getFamiliasUseCase: GetFamiliasUseCase, val getArticulosFiltradosUseCase: GetArticulosFiltradosUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val ListaFamilias = MutableLiveData<List<DTFamiliaPadre>>()
    val ListaArticulos = MutableLiveData<List<DTArticulo>>()
    val mensajeDelServer = MutableLiveData<String>()
    val mcargando = MutableLiveData<Boolean>()

    fun CargarFamilias() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getFamiliasUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ListaFamilias.postValue(result.elemento!!)
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

    fun CargarArticulos(cantidad:Int,listaPecio:String,tipo:Int,valorBusqueda:String)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getArticulosFiltradosUseCase(cantidad, listaPecio,tipo,valorBusqueda)
                if (result != null)
                {
                    if (result.ok)
                    {
                        ListaArticulos.postValue(result.elemento!!)
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