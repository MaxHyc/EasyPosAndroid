package com.devhyc.easypos.ui.mediospagoslite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.truncate

@HiltViewModel
class MediosPagosLiteViewModel @Inject constructor(val getMediosDePagos: GetMediosDePagos, val getConsultarTransaccion: GetConsultarTransaccion, val getListarBancosUseCase: GetListarBancosUseCase, val getListarFinancierasUseCase: GetListarFinancierasUseCase, val postEmitirDocumento: PostEmitirDocumento, val postValidarDocumento: PostValidarDocumento, val getImpresionUseCase: GetImpresionUseCase, val postCrearTransaccionITD: postCrearTransaccionITD, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase, val getCancelarTransaccionITDUseCase: GetCancelarTransaccionITDUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val Impresion = MutableLiveData<DTImpresion>()
    var TransaccionFinalizada = MutableLiveData<DTDocTransaccion>()
    var pagos:ArrayList<DTDocPago> = ArrayList()
    var pagoseleccionado:Int = 0
    //
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

    fun ListarMediosDePago() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getMediosDePagos()
                if (result != null) {
                    if (result.ok) {
                        LMedioPago.postValue(result.elemento!!)
                    }
                }
                isLoading.postValue(false)
            }
        } catch (e: Exception) {
            isLoading.postValue(false)
        }
    }

    //FISERV

    fun CrearTransaccionITD(montopago: Double) {
        viewModelScope.launch {
            try
            {
                isLoading.postValue(true)
                mostrarEstado("Creando transacción")
                val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                if (resultCon.ok) {
                    mostrarEstado(resultCon.mensaje)
                    //SI HAY CONEXIÓN CON ITD
                    //CREO LA TRANSACCION
                    var transaccion = ITDTransaccionNueva(
                        Globales.DocumentoEnProceso.cabezal!!.terminal,
                        Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo,
                        Globales.DocumentoEnProceso.cabezal!!.nroDoc,
                        Globales.DocumentoEnProceso.complemento!!.codigoSucursal,
                        Globales.UsuarioLoggueado.funcionarioId.toString(),
                        Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                        Globales.TotalesDocumento.total,
                        Globales.TotalesDocumento.subtotalGravadoConDto,
                        Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                        montopago,
                        Globales.DocumentoEnProceso.valorizado!!.tipoCambio,
                        false,
                        1,
                        0
                    )
                    if (Globales.DocumentoEnProceso.receptor != null) {
                        if (Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc == 0)
                            transaccion.conRut = true
                    }
                    //
                    val result = postCrearTransaccionITD(transaccion)
                    if (result!!.ok) {
                        mostrarEstado("Transacción creada")
                        Globales.IDTransaccionActual = result.elemento!!.transaccionId
                        mostrarEstado("Abriendo App FISERV")
                        Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionInputData(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)))
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
            transactionType = TransactionTypes.SALE,
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

    fun ConsultarTransaccionITD(nroTransaccion: String) {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                if (resultCon.ok) {
                    //mensajeDelServer.postValue(resultCon.mensaje)
                    //SI HAY CONEXIÓN CON ITD
                    val result = getConsultarTransaccionITDUseCase(nroTransaccion)
                    if (result!!.ok) {
                        //TransaccionConsulta.postValue(result.elemento!!)
                        if (!result.elemento!!.conError)
                        {
                            if (result.elemento!!.pago != null)
                            {
                                result.elemento!!.pago!!.medioPagoCodigo = pagoseleccionado
                                result.elemento!!.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                                //AGREGO EL PAGO DE FISERV
                                pagos.add(result.elemento!!.pago!!)
                                FinalizarVenta()
                            }
                        }
                        else
                        {
                            mostrarInforme("${result.elemento!!.mensaje} | ${result.elemento!!.mensajePos} \n(Transac. ${result.elemento!!.transaccionId}")
                        }
                    } else {
                        mostrarErrorServer(result.mensaje)
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

    ////////

    fun FinalizarVenta()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                //AGREGO LOS PAGOS Y SELECCIONO EL DEPOSITO
                Globales.DocumentoEnProceso.valorizado!!.pagos = pagos
                Globales.DocumentoEnProceso.complemento!!.codigoDeposito = Globales.Terminal.Deposito
                //
                val result = postValidarDocumento(Globales.DocumentoEnProceso)
                if (result != null) {
                    if (result.ok)
                    {
                        var trans = postEmitirDocumento(Globales.DocumentoEnProceso)
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
        } catch (e: Exception) {
            mostrarErrorLocal(e.message.toString())
        }
        finally {
            isLoading.postValue(false)
        }
    }
}