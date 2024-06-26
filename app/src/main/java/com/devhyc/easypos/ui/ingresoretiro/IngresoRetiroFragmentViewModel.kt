package com.devhyc.easypos.ui.ingresoretiro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.integration.easyposkotlin.data.model.DTCaja
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngresoRetiroFragmentViewModel @Inject constructor(val postIniciarCaja: PostIniciarCaja,val getImpresionInicioCajaUseCase: GetImpresionInicioCajaUseCase,val postValidarDocumento: PostValidarDocumento,val postEmitirDocumento: PostEmitirDocumento, val getConsultarTransaccion: GetConsultarTransaccion,val getNuevoDocumentoUseCase: GetNuevoDocumentoUseCase): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    var TransaccionFinalizada = MutableLiveData<DTDocTransaccion>()

    val iniciarCaja = MutableLiveData<DTCaja>()
    val impresionInicio = MutableLiveData<DTImpresion>()


    private val _mostrarEstado = MutableLiveData<String>()
    val mostrarEstado: LiveData<String> = _mostrarEstado
    fun mostrarEstado(mensaje:String)
    {
        _mostrarEstado.value = mensaje
    }
    //
    private val _errorlocal = SingleLiveEvent<String>()
    val mostrarErrorLocal: LiveData<String> = _errorlocal

    fun mostrarErrorLocal(message: String) {
        _errorlocal.value = message
    }
    //
    private val _errorServer = SingleLiveEvent<String>()
    val mostrarErrorServer: LiveData<String> = _errorServer

    fun mostrarErrorServer(message: String) {
        _errorServer.value = message
    }

    fun RealizarMovimientoDeCaja(moneda:String,monto:String,observacion:String,codigoMovimiento:Int)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                var tipoDoc = ""
                when(codigoMovimiento)
                {
                    Globales.TTipoMovimientoCaja.INGRESO.codigo -> tipoDoc = Globales.Terminal.Documentos.Ingreso
                    Globales.TTipoMovimientoCaja.RETIRO.codigo -> tipoDoc = Globales.Terminal.Documentos.Retiro
                }

                val resultDoc = getNuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario, Globales.Terminal.Codigo,tipoDoc)
                if (resultDoc != null)
                {
                    if(resultDoc.ok)
                    {
                        //CARGO EL MONTO Y MONEDA
                        resultDoc.elemento!!.documento.complemento!!.codigoSucursal = Globales.Terminal.SucursalDoc
                        resultDoc.elemento!!.documento.cabezal!!.observaciones = observacion
                        when(moneda)
                        {
                            "1" -> { resultDoc.elemento!!.documento.valorizado!!.monedaCodigo = "1" }
                            "2" -> { resultDoc.elemento!!.documento.valorizado!!.monedaCodigo = "2" }
                            "" -> { resultDoc.elemento!!.documento.valorizado!!.monedaCodigo = "1" }
                        }
                        resultDoc.elemento!!.documento.valorizado!!.monedaCodigo = moneda.toString()
                        resultDoc.elemento!!.documento.valorizado!!.ImporteManual = monto.toDouble()
                        //
                        val result = postValidarDocumento(resultDoc.elemento!!.documento)
                        if (result != null) {
                            if (result.ok)
                            {
                                var trans = postEmitirDocumento(resultDoc.elemento!!.documento)
                                if (trans != null)
                                {
                                    //CONSULTO EL ESTADO DE LA TRANSACCION POR ESE NRO DE TRANSACCION
                                    if (trans.elemento!!.nroTransaccion.isNotEmpty())
                                    {
                                        var nroTrans = trans.elemento!!.nroTransaccion
                                        var cont = 0
                                        var tiempoEspera = trans.elemento!!.tiempoEsperaSeg
                                        while (cont < tiempoEspera)
                                        {
                                            //CONSULTO LA TRANSACCION POR EL NUMERO
                                            trans = getConsultarTransaccion(nroTrans)!!
                                            if (trans.elemento!!.finalizada)
                                            {
                                                cont = tiempoEspera
                                            }
                                            else
                                            {
                                                cont += 1
                                                Thread.sleep(1000)
                                            }
                                        }
                                        if(trans.ok)
                                        {
                                            if (trans.elemento!!.errorCodigo == 0)
                                            {
                                                Globales.isEmitido = true
                                                TransaccionFinalizada.postValue(trans.elemento)
                                            }
                                            else if(trans.elemento!!.errorCodigo != 0)
                                            {
                                                mostrarErrorServer(trans.elemento!!.errorMensaje)
                                            }
                                        }
                                        else
                                        {
                                            mostrarErrorServer(trans.mensaje)
                                        }
                                    }
                                }
                            }
                            else
                            {
                                mostrarErrorServer(result.mensaje)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            mostrarErrorLocal(e.message.toString())
        }
        finally {
            isLoading.postValue(false)
        }
    }

    fun IniciarCaja(monto:String)
    {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                var caja = DTIngresoCaja(Globales.Terminal.Codigo,Globales.UsuarioLoggueado.usuario,monto.toDouble())
                val result = postIniciarCaja(caja)
                if (result!!.ok)
                {
                    //iniciarCaja.postValue(result.elemento!!)
                    ImpresionInicio(result.elemento!!)
                }
                else
                {
                    mostrarErrorServer(result.mensaje)
                }
            }
            catch (e:Exception)
            {
                mostrarErrorLocal(e.message.toString())
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }

    fun ImpresionInicio(caja:DTCaja)
    {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val result = getImpresionInicioCajaUseCase(Globales.Terminal.Codigo,caja.Nro.toString())
                if (result!!.ok)
                {
                    impresionInicio.postValue(result.elemento!!)
                }
                else
                {
                    mostrarErrorServer(result.mensaje)
                }
            }
            catch (e:Exception)
            {
                mostrarErrorLocal(e.message.toString())
            }
            finally {
                isLoading.postValue(false)
            }
        }
    }
}