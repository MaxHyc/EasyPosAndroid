package com.devhyc.easypos.ui.tarjetamanual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.DTFinanciera
import com.devhyc.easypos.domain.GetListarBancosUseCase
import com.devhyc.easypos.domain.GetListarFinancierasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TarjetaManualFragmentViewModel @Inject constructor(val getListarFinancierasUseCase: GetListarFinancierasUseCase,val getListarBancosUseCase: GetListarBancosUseCase): ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val ColFinancieras = MutableLiveData<ArrayList<DTFinanciera>>()
    val mensajeError = MutableLiveData<String>()
    val ColBancos = MutableLiveData<ArrayList<DTBanco>>()

    fun ListarFinancieras()
    {
        isLoading.postValue(false)
        try {
            viewModelScope.launch {
                val result = getListarFinancierasUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ColFinancieras.postValue(result.elemento!!)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mensajeError.postValue(e.message)
        }
        finally {
            isLoading.postValue(false)
        }
    }

    fun ListarBancos()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getListarBancosUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ColBancos.postValue(result.elemento!!)
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