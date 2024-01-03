package com.devhyc.easypos.ui.clientes.listado

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTCliente
import com.devhyc.easypos.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaClientesViewModel @Inject constructor(val getListadoClientesUseCase: GetListadoClientesUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val ColClientes = MutableLiveData<ArrayList<DTCliente>>()

    fun ListarClientes() {
        try {
          viewModelScope.launch {
                isLoading.postValue(true)
                val result = getListadoClientesUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ColClientes.postValue(result.elemento!!)
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