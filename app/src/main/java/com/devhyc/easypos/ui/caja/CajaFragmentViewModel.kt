package com.devhyc.easypos.ui.caja

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.domain.GetCajaAbiertaUseCase
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CajaFragmentViewModel @Inject constructor(val getCajaAbiertaUseCase: GetCajaAbiertaUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val iniciar = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()

    fun ObtenerCajaAbierta()
    {
        viewModelScope.launch {
            try
            {
                isLoading.postValue(true)
                //val result = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                val result = getCajaAbiertaUseCase("1")
                if (result!!.ok)
                {
                    iniciar.postValue(true)
                    caja.postValue(result.elemento!!)
                }
                else
                {
                    iniciar.postValue(false)
                }
                isLoading.postValue(false)
            }
            catch (e:Exception)
            {
                mensajeDelServer.postValue(e.message)
            }
        }
    }

}