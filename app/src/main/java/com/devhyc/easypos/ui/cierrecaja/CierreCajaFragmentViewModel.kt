package com.devhyc.easypos.ui.cierrecaja

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTCajaEstado
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTTotalesDeclarados
import com.devhyc.easypos.domain.GetCajaAbiertaUseCase
import com.devhyc.easypos.domain.GetEstadoCaja
import com.devhyc.easypos.domain.GetImpresionCierreCajaUseCase
import com.devhyc.easypos.domain.PostCerrarCaja
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CierreCajaFragmentViewModel @Inject constructor(val postCerrarCaja: PostCerrarCaja, val getEstadoCaja: GetEstadoCaja, val getImpresionCierreCajaUseCase: GetImpresionCierreCajaUseCase) : ViewModel()  {
    val isLoading = MutableLiveData<Boolean>()
    val cerrado = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()
    val estado = MutableLiveData<DTCajaEstado>()
    val impresionCierre = MutableLiveData<DTImpresion>()

    fun CerrarCaja(TotalesDeclarados:DTTotalesDeclarados)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = postCerrarCaja(Globales.Terminal.Codigo,TotalesDeclarados)
            if (result!!.ok)
            {
                caja.postValue(result.elemento!!)
            }
            else
            {
                mensajeDelServer.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun ImpresionCierre(caja:DTCaja)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getImpresionCierreCajaUseCase(Globales.Terminal.Codigo,caja.Nro.toString(),Globales.UsuarioLoggueado.usuario)
            if (result!!.ok)
            {
                impresionCierre.postValue(result.elemento!!)
            }
            else
            {
                mensajeDelServer.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }
}