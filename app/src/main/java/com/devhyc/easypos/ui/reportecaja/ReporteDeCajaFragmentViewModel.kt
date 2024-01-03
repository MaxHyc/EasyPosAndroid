package com.devhyc.easypos.ui.reportecaja

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTCajaEstado
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.domain.GetEstadoCaja
import com.devhyc.easypos.domain.GetImpresionCierreCajaUseCase
import com.devhyc.easypos.domain.PostCerrarCaja
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReporteDeCajaFragmentViewModel @Inject constructor(val getEstadoCaja: GetEstadoCaja,val getImpresionCierreCajaUseCase: GetImpresionCierreCajaUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()
    val estado = MutableLiveData<DTCajaEstado>()
    val impresionReporte = MutableLiveData<DTImpresion>()

    fun EstadoDeCaja(NroCaja:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getEstadoCaja(Globales.Terminal.Codigo,NroCaja, Globales.UsuarioLoggueado.usuario)
            if(result!!.ok)
            {
                estado.postValue(result.elemento!!)
            }
            else
            {
                mensajeDelServer.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun ImpresionReporte(caja:DTCajaEstado)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getImpresionCierreCajaUseCase(Globales.Terminal.Codigo,caja.Cabezal.NroCaja.toString(),Globales.UsuarioLoggueado.usuario)
            if (result!!.ok)
            {
                impresionReporte.postValue(result.elemento!!)
            }
            else
            {
                mensajeDelServer.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }
}