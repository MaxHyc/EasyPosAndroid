package com.devhyc.easypos.ui.mediospagos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.DTFinanciera
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediosDePagoViewModel @Inject constructor(val getMediosDePagos: GetMediosDePagos, val getListarBancosUseCase: GetListarBancosUseCase, val getListarFinancierasUseCase: GetListarFinancierasUseCase,val postEmitirDocumento: PostEmitirDocumento, val getConsultarTransaccion: GetConsultarTransaccion, val postValidarDocumento: PostValidarDocumento) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val ColFinancieras = MutableLiveData<ArrayList<DTFinanciera>>()
    val ColBancos = MutableLiveData<ArrayList<DTBanco>>()

    fun ListarMediosDePago() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getMediosDePagos()
                if (result != null)
                {
                    if (result.ok)
                    {
                        LMedioPago.postValue(result.elemento!!)
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

    fun ListarFinancieras()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
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
            isLoading.postValue(false)
        }
    }
}