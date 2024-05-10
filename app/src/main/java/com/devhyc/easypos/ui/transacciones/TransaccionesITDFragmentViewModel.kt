package com.devhyc.easypos.ui.transacciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.fiserv.model.ITDTransaccionAnular
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransaccionesITDFragmentViewModel @Inject constructor(val getListadoTransacionesSinAsociarITDUseCase: GetListadoTransacionesSinAsociarITDUseCase,val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase ,val getCajaAbiertaUseCase: GetCajaAbiertaUseCase, val postCrearAnulacionITDUseCase: PostCrearAnulacionITDUseCase, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getListadoTransaccionesITDUseCase: GetListadoTransaccionesITDUseCase, val postConsultarEstadoTransaccionITDUseCase: PostConsultarEstadoTransaccionITDUseCase):ViewModel(){
    val isLoading = MutableLiveData<Boolean>()
    val MensajeServer = MutableLiveData<String>()
    val ListadoDocumentos = MutableLiveData<ArrayList<ITDTransaccionLista>>()
    val ActualizarLista = MutableLiveData<String>()
    val MedioPagoCargado = MutableLiveData<DTDocPago>()

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
    //
    private val _informe = SingleLiveEvent<String>()
    val mostrarInforme: LiveData<String> = _informe

    fun mostrarInforme(message: String) {
        _informe.value = message
    }

    fun ListarDocumentosITD() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                //
                val resultcaja = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                if (resultcaja!!.ok)
                {
                    if(resultcaja!!.elemento == null)
                        mostrarErrorLocal("No hay una caja abierta")
                    else
                    {
                        //
                        val result = getListadoTransaccionesITDUseCase(Globales.Terminal.Codigo,resultcaja.elemento!!.Nro.toLong())
                        if (result != null)
                        {
                            if (result.ok)
                            {
                                ListadoDocumentos.postValue(result.elemento!!)
                            }
                            else
                            {
                                MensajeServer.postValue(result.mensaje)
                            }
                        }
                    }
                }
                else
                {
                    mostrarErrorLocal("No hay una caja abierta")
                }
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
        finally {
            isLoading.postValue(false)
        }
    }

    fun ListarDocumentosSinAsociarITD() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                //
                val resultcaja = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                if (resultcaja!!.ok)
                {
                    if(resultcaja!!.elemento == null)
                        mostrarErrorLocal("No hay una caja abierta")
                    else
                    {
                        //
                        val result = getListadoTransacionesSinAsociarITDUseCase(Globales.Terminal.Codigo)
                        if (result != null)
                        {
                            if (result.ok)
                            {
                                ListadoDocumentos.postValue(result.elemento!!)
                            }
                            else
                            {
                                MensajeServer.postValue(result.mensaje)
                            }
                        }
                    }
                }
                else
                {
                    mostrarErrorLocal("No hay una caja abierta")
                }
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
        finally {
            isLoading.postValue(false)
        }
    }

    fun ConsultarEstadoTransaccion(nroTransaccion:String,transacionessinasociar:Boolean)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = postConsultarEstadoTransaccionITDUseCase(nroTransaccion)
                if (result != null)
                {
                    if (result.ok)
                    {
                        if (transacionessinasociar)
                            ListarDocumentosSinAsociarITD()
                        else
                            ListarDocumentosITD()
                        ActualizarLista.postValue(result.mensaje)
                    }
                    else
                    {
                        MensajeServer.postValue(result.mensaje)
                    }
                }
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
        finally {
            isLoading.postValue(false)
        }
    }

    fun ConsultarTransaccionITD(nroTransaccion: String, esDevolucion:Boolean, codigoMedioPago:Int) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                //SI HAY CONEXIÓN CON ITD
                var contador=0
                var tespera=15
                while(contador<tespera)
                {
                    mostrarEstado("Consultando transacción: Intentos $contador de $tespera")
                    val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                    if (resultCon.ok) {
                        val result = getConsultarTransaccionITDUseCase(nroTransaccion)
                        if (result!!.ok) {
                            if (result.elemento!!.conError)
                            {
                                mostrarEstado("Intentando conectar con FISERV intento $contador de $tespera")
                                contador += 1
                                Thread.sleep(1000)
                            }
                            else
                            {
                                contador = tespera
                                if (result.elemento!!.pago != null)
                                {
                                    result.elemento!!.pago!!.medioPagoCodigo = codigoMedioPago
                                    result.elemento!!.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                                    MedioPagoCargado.postValue(result.elemento!!.pago)
                                }
                            }
                        }
                        else
                        {
                            mostrarErrorServer(result.mensaje + "(Intentos $contador de $tespera)" )
                            contador+=1
                            Thread.sleep(1000)
                        }
                    }
                    else
                    {
                        mostrarEstado("Intentando conectar con FISERV intento $contador de $tespera")
                        contador += 1
                        Thread.sleep(1000)
                    }
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

    fun AnularTransaccion(nroTransaccion: String)
    {
        viewModelScope.launch {
            try
            {
                isLoading.postValue(true)
                mostrarEstado("Creando transacción de anulación")
                val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                if (resultCon.ok) {
                    mostrarEstado(resultCon.mensaje)
                    //SI HAY CONEXIÓN CON ITD
                    //CREO LA TRANSACCION DE ANULACION
                    var transaccion = ITDTransaccionAnular(
                        nroTransaccion,
                        Globales.Terminal.Codigo,
                        Globales.Terminal.Documentos.DevContado,
                        Globales.UsuarioLoggueado.funcionarioId.toString(),
                        Globales.DocumentoEnProceso.complemento!!.codigoSucursal,
                    )
                    //
                    val result = postCrearAnulacionITDUseCase(transaccion)
                    if (result!!.ok) {
                        mostrarEstado("Transacción de anulación creada")
                        Globales.IDTransaccionActual = result.elemento!!.transaccionId
                        mostrarEstado("Abriendo App FISERV")
                        Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionInputData(
                            BigDecimal.valueOf(0).scaleByPowerOfTen(2)))
                    } else {
                        mostrarErrorServer(result.mensaje)
                    }
                } else {
                    mostrarErrorServer(resultCon.mensaje)
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

    private fun createTransactionInputData(monto: BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.REFUND,
            monto,
            null,
            currency = convertToCurrencyType(Globales.currencySelected),
            null
        )
    }

    private fun convertToCurrencyType(currencyType: String): Int {
        return when (currencyType) {
            "USD" ->
                840
            "UYU" ->
                858
            else ->
                840
        }
    }
}