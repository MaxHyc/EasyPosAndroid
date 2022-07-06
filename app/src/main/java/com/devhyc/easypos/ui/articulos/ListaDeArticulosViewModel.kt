package com.devhyc.easypos.ui.articulos

import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.domain.GetArticulosFiltradoUseCase
import com.devhyc.easypos.domain.GetArticulosRubrosUseCase
import com.devhyc.easypos.domain.GetArticulosUseCase
import com.devhyc.easypos.domain.GetTerminalUseCase
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaDeArticulosViewModel @Inject constructor(val getArticulosUseCase: GetArticulosUseCase,val getArticulosFiltradoUseCase: GetArticulosFiltradoUseCase, val getArticulosRubrosUseCase: GetArticulosRubrosUseCase) : ViewModel() {

    val articulosModel = MutableLiveData<List<DTArticulo>>()
    val rubrosModel = MutableLiveData<List<DTRubro>>()
    val isLoading = MutableLiveData<Boolean>()
    val cargacompletaArticulos = MutableLiveData<Boolean>()
    val cargacompletaRubros = MutableLiveData<Boolean>()

    fun ListarRubros() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getArticulosRubrosUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        rubrosModel.postValue(result.elemento!!)
                        cargacompletaRubros.postValue(true)
                    }
                    else
                    {
                        cargacompletaRubros.postValue(false)
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

  /*  fun ListarArticulos() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getArticulosUseCase(Globales.CantidadAListar,Globales.ListaDePrecioAListar)
                if (result != null)
                {
                    if (result.ok)
                    {
                        articulosModel.postValue(result.elemento!!)
                        cargacompletaArticulos.postValue(true)
                    }
                    else
                    {
                        cargacompletaArticulos.postValue(false)
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

    fun ListarArticulosFiltrado(tipoBusqueda:Int,filtro:String) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getArticulosFiltradoUseCase(Globales.CantidadAListar,Globales.ListaDePrecioAListar,tipoBusqueda,filtro)
                if (result != null)
                {
                    if (result.ok)
                    {
                        articulosModel.postValue(result.elemento!!)
                        cargacompletaArticulos.postValue(true)
                    }
                    else
                    {
                        cargacompletaArticulos.postValue(false)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }*/
}