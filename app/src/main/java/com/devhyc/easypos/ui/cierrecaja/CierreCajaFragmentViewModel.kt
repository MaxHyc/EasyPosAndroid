package com.devhyc.easypos.ui.cierrecaja

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.domain.GetCajaAbiertaUseCase
import com.devhyc.easypos.domain.PostCerrarCaja
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CierreCajaFragmentViewModel @Inject constructor(val postCerrarCaja: PostCerrarCaja) : ViewModel()  {
    val isLoading = MutableLiveData<Boolean>()
    val cerrado = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()

    fun CerrarCaja(TotalesDeclarados:DTTotalesDeclarados)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = postCerrarCaja(Globales.Terminal.Codigo,TotalesDeclarados)
            if (result!!.ok)
            {
                cerrado.postValue(true)
                caja.postValue(result.elemento!!)
            }
            else
            {
                cerrado.postValue(false)
            }
            isLoading.postValue(false)
        }
    }
}