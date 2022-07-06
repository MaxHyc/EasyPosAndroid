package com.devhyc.easypos.ui.itemDoc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.domain.GetArticulosFiltradoUseCase
import com.devhyc.easypos.domain.GetArticulosRubrosUseCase
import com.devhyc.easypos.domain.GetArticulosUseCase
import com.integration.easyposkotlin.data.model.DTArticulo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDocFragmentViewModel @Inject constructor(val getArticulosRubrosUseCase: GetArticulosRubrosUseCase) : ViewModel() {

    val rubrosModel = MutableLiveData<List<DTRubro>>()
    val isLoading = MutableLiveData<Boolean>()
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
}