package com.devhyc.easypos.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.domain.GetArticulosUseCase
import com.devhyc.easypos.domain.GetCajaAbiertaUseCase
import com.devhyc.easypos.domain.GetTerminalUseCase
import com.devhyc.easypos.domain.LoginUseCase
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTTerminalPos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(val loginUseCase: LoginUseCase, val getTerminalUseCase: GetTerminalUseCase,val getCajaAbiertaUseCase: GetCajaAbiertaUseCase): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val LoginModel = MutableLiveData<DTLogin>()
    val iniciar = MutableLiveData<Boolean>()
    val mensaje = MutableLiveData<String>()
    val cajaAbierta = MutableLiveData<DTCaja>()

    fun iniciarSesion(user: String,pass:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            var userlogin:DTLoginRequest = DTLoginRequest(user,pass)
            val result = loginUseCase(userlogin)
            if (result!!.ok)
            {
                iniciar.postValue(true)
                LoginModel.postValue(result.elemento!!)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun obtenerTerminal()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val terminal = getTerminalUseCase(Globales.NroCaja)
            if(terminal !=null)
            {
                if(terminal.ok)
                {
                    Globales.Terminal = terminal.elemento
                }
                else
                {
                    mensaje.postValue(terminal.mensaje)
                }
            }
            else
            {
                mensaje.postValue("No existe la terminal ingresada")
            }
            isLoading.postValue(false)
        }
    }

    fun obtenerCajaAbierta()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val caja = getCajaAbiertaUseCase(Globales.NroCaja)
            if(caja !=null)
            {
                if(caja.ok)
                {
                    Globales.CajaActual = caja.elemento
                    iniciar.postValue(true)
                }
                else
                {
                    mensaje.postValue(caja.mensaje)
                }
            }
            isLoading.postValue(false)
        }
    }

}