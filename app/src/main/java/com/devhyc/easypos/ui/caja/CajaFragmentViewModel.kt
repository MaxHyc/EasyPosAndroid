package com.devhyc.easypos.ui.caja

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTIngresoCaja
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CajaFragmentViewModel @Inject constructor(val getCajaAbiertaUseCase: GetCajaAbiertaUseCase, val postIniciarCaja: PostIniciarCaja, var postCerrarCaja: PostCerrarCaja, val getImpresionInicioCajaUseCase: GetImpresionInicioCajaUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val existeCaja = MutableLiveData<Boolean>()
    val caja = MutableLiveData<DTCaja>()
    val mensajeDelServer = MutableLiveData<String>()
    val mensaje = MutableLiveData<String>()

    fun ObtenerCajaAbierta()
    {
        viewModelScope.launch {
            try
            {
                isLoading.postValue(true)
                val result = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                if (result!!.ok)
                {
                    if(result!!.elemento == null)
                        existeCaja.postValue(false)
                    else
                    {
                        existeCaja.postValue(true)
                        caja.postValue(result.elemento!!)
                    }
                }
                else
                {
                    existeCaja.postValue(false)
                }
            }
            catch (e:Exception)
            {
                mensajeDelServer.postValue(e.message)
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }

   /* fun IniciarCaja(monto:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            var caja = DTIngresoCaja(Globales.Terminal.Codigo,Globales.UsuarioLoggueado.usuario,monto.toDouble())
            val result = postIniciarCaja(caja)
            if (result!!.ok)
            {
                iniciarCaja.postValue(result.elemento!!)
                ImpresionInicio(result.elemento!!)
                ObtenerCajaAbierta()
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun ImpresionInicio(caja:DTCaja)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val result = getImpresionInicioCajaUseCase(Globales.Terminal.Codigo,caja.Nro.toString())
            if (result!!.ok)
            {
                impresionInicio.postValue(result.elemento!!)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }*/
}