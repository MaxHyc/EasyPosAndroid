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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransaccionesITDFragmentViewModel @Inject constructor(val getListadoTransacionesSinAsociarITDUseCase: GetListadoTransacionesSinAsociarITDUseCase,val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase ,val getCajaAbiertaUseCase: GetCajaAbiertaUseCase, val postCrearAnulacionITDUseCase: PostCrearAnulacionITDUseCase, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getListadoTransaccionesITDUseCase: GetListadoTransaccionesITDUseCase, val postConsultarEstadoTransaccionITDUseCase: PostConsultarEstadoTransaccionITDUseCase):ViewModel(){
    val isLoading = MutableLiveData<Boolean>()
    val MensajeServer = MutableLiveData<String>()
    val ListadoDocumentos = MutableLiveData<ArrayList<ITDTransaccionLista>>()
    val MedioPagoCargado = MutableLiveData<DTDocPago>()
    val mostrarEstado = SingleLiveEvent<String>()
    val mostrarErrorLocal = SingleLiveEvent<String>()
    val mostrarErrorServer = SingleLiveEvent<String>()
    //

    fun ListarDocumentosITD() {

        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    //
                    val resultcaja = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                    if (resultcaja!!.ok)
                    {
                        if(resultcaja!!.elemento == null)
                            mostrarErrorLocal.postValue("No hay una caja abierta")
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
                        mostrarErrorLocal.postValue("No hay una caja abierta")
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
    }

    fun ListarDocumentosSinAsociarITD() {

        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    //
                    val resultcaja = getCajaAbiertaUseCase(Globales.Terminal.Codigo)
                    if (resultcaja!!.ok)
                    {
                        if(resultcaja!!.elemento == null)
                            mostrarErrorLocal.postValue("No hay una caja abierta")
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
                        mostrarErrorLocal.postValue("No hay una caja abierta")
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
    }

    fun ConsultarEstadoTransaccion(nroTransaccion:String,proveedor: String,transacionessinasociar:Boolean)
    {

        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    val result = postConsultarEstadoTransaccionITDUseCase(nroTransaccion,proveedor)
                    if (result != null)
                    {
                        if (result.ok)
                        {
                            if (transacionessinasociar)
                                ListarDocumentosSinAsociarITD()
                            else
                                ListarDocumentosITD()
                        }
                        else
                        {
                            MensajeServer.postValue(result.mensaje)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
    }

    fun ConsultarTransaccionITD(nroTransaccion: String, proveedor:String) {

        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    //SI HAY CONEXIÓN CON ITD
                    var contador=0
                    var tespera=15
                    while(contador<tespera)
                    {
                        mostrarEstado.postValue("Consultando transacción: Intentos $contador de $tespera")
                        val result = getConsultarTransaccionITDUseCase(nroTransaccion,proveedor)
                        if (result!!.ok) {
                            if (result.elemento!!.conError)
                            {
                                mostrarEstado.postValue("Intentando conectar con FISERV intento $contador de $tespera")
                                contador += 1
                                Thread.sleep(1000)
                            }
                            else
                            {
                                contador = tespera
                                if (result.elemento!!.pago != null)
                                {
                                    var idmediopago:Int=0
                                    Globales.MediosPagoDocumento.forEach {
                                        if(it.ProveedorItd == proveedor)
                                        {
                                            idmediopago = it.Id
                                        }
                                    }
                                    if (idmediopago!=0)
                                    {
                                        result.elemento!!.pago!!.medioPagoCodigo = idmediopago
                                        result.elemento!!.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                                        MedioPagoCargado.postValue(result.elemento!!.pago)
                                    }
                                    else
                                    {
                                        mostrarErrorLocal.postValue("No hay medio de pago configurado para el pago seleccionado")
                                    }
                                }
                            }
                        }
                        else
                        {
                            mostrarErrorServer.postValue(result.mensaje + "(Intentos $contador de $tespera)" )
                            contador+=1
                            Thread.sleep(1000)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            MensajeServer.postValue(e.message)
        }
    }
}