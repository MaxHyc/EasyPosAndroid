package com.devhyc.easypos.ui.ingresoretiro

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTIngresoCaja
import com.devhyc.easypos.domain.PostCerrarCaja
import com.devhyc.easypos.domain.PutIniciarCaja
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngresoRetiroFragmentViewModel @Inject constructor(val putIniciarCaja: PutIniciarCaja, var postCerrarCaja: PostCerrarCaja): ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val inicioCaja = MutableLiveData<DTCaja>()
    val caja = MutableLiveData<DTCaja>()
    val ingresoCaja= MutableLiveData<DTCaja>()
    val retiroCaja= MutableLiveData<DTCaja>()
    val mensaje = MutableLiveData<String>()

    fun IniciarCaja(monto:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            var caja = DTIngresoCaja(Globales.Terminal.Codigo,Globales.UsuarioLoggueado.usuario,monto.toDouble())
            val result = putIniciarCaja(caja)
            if (result!!.ok)
            {
                inicioCaja.postValue(result.elemento!!)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun Ingreso(monto:String)
    {
       /* viewModelScope.launch {
            isLoading.postValue(true)
            var caja: DTIngresoCaja = DTIngresoCaja(Globales.UsuarioLoggueado.usuario,monto.toDouble())
            val result = ingresoUseCase(caja)
            if (result!!.ok)
            {
                ingresoCaja.postValue(result.elemento!!)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }*/
    }

    fun Retiro(monto:String)
    {
       /* viewModelScope.launch {
            isLoading.postValue(true)
            var caja: DTIngresoCaja = DTIngresoCaja(Globales.UsuarioLoggueado.usuario,monto.toDouble())
            val result = retiroUseCase(caja)
            if (result!!.ok)
            {
                retiroCaja.postValue(result.elemento!!)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }*/
    }
}