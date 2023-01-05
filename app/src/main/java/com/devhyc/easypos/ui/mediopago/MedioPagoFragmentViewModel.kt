package com.devhyc.easypos.ui.mediopago

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.domain.GetArticulosRubrosUseCase
import com.devhyc.easypos.domain.GetMediosDePagos
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedioPagoFragmentViewModel @Inject constructor(val getMediosDePagos: GetMediosDePagos) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()

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

    /*fun CrearOrdenCashDro()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                var data:HashMap<String,String> = HashMap()
                data.put("operation","startOperation")
                data.put("name", "admin")
                data.put("password","1")
                data.put("type","")
                data.put("posid",Globales.NroCaja)
                data.put("posuser",Globales.UsuarioLoggueado.usuario)
                data.put("aliasid","")
                data.put("parameters","{}")
                val result = crearOrdenCash(data)
                if (result != null)
                {
                    if (result.ok)
                    {
                        //LMedioPago.postValue(result.elemento!!)
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