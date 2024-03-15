package com.devhyc.easypos.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.core.di.NetworkModule
import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.domain.GetTerminalUseCase
import com.devhyc.easypos.domain.LoginControlUseCase
import com.devhyc.easypos.domain.LoginUseCase
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(val loginUseCase: LoginUseCase, val getTerminalUseCase: GetTerminalUseCase, val loginControlUseCase: LoginControlUseCase): ViewModel() {
    val isLoadingTerminal = MutableLiveData<Boolean>()
    val isLoadingAutomatico = MutableLiveData<Boolean>()
    val isLoadingControlLogin =MutableLiveData<Boolean>()
    val isLoadingInicioSesion = MutableLiveData<Boolean>()
    val LoginModel = MutableLiveData<DTLogin>()
    val iniciar = MutableLiveData<Boolean>()
    val mensaje = MutableLiveData<String>()
    val LoginAutomatico = MutableLiveData<DTLogin>()
    val iniciarAutomatico = MutableLiveData<Boolean>()

    fun iniciarSesionControlLogin(user: String,pass: String,automatico:Boolean)
    {
        viewModelScope.launch {
            isLoadingControlLogin.postValue(true)
            var userlogin = DTLoginRequest(user,pass)
            val result = loginControlUseCase(userlogin)
            if (result!!.ok)
            {
                Globales.UsuarioLoggueadoConfig = result.elemento!!
                Globales.DireccionServidor = result.elemento!!.urlServicio
                //Globales.NroCaja = result.elemento!!.terminalCodigo
                Globales.NroCaja = "2"
                NetworkModule.provideRetrofit().newBuilder()
                if (automatico)
                    iniciarSessionAutomatico(result.elemento!!.sistemaUsuario,result.elemento!!.sistemaPass)
                    else
                    iniciarSesion(result.elemento!!.sistemaUsuario,result.elemento!!.sistemaPass)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoadingControlLogin.postValue(false)
        }
    }

    fun iniciarSesion(user: String,pass:String)
    {
        viewModelScope.launch {
            isLoadingInicioSesion.postValue(true)
            var userlogin = DTLoginRequest(user,pass)
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
            isLoadingInicioSesion.postValue(false)
        }
    }

    fun iniciarSessionAutomatico(user: String,pass:String)
    {
        viewModelScope.launch {
            isLoadingAutomatico.postValue(true)
            var userlogin = DTLoginRequest(user,pass)
            val result = loginUseCase(userlogin)
            if (result!!.ok)
            {
                LoginAutomatico.postValue(result.elemento!!)
                iniciarAutomatico.postValue(true)
            }
            else
            {
                mensaje.postValue(result.mensaje)
            }
            isLoadingAutomatico.postValue(false)
        }
    }

    fun obtenerTerminal()
    {
        viewModelScope.launch {
            isLoadingTerminal.postValue(true)
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
            isLoadingTerminal.postValue(false)
        }
    }
}