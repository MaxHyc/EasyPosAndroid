package com.devhyc.easypos.ui.mediospagoslite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import com.devhyc.easypos.utilidades.Globales
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MediosPagosLiteViewModel @Inject constructor(val getMediosDePagos: GetMediosDePagos, val getConsultarTransaccion: GetConsultarTransaccion, val getListarBancosUseCase: GetListarBancosUseCase, val getListarFinancierasUseCase: GetListarFinancierasUseCase, val postEmitirDocumento: PostEmitirDocumento, val postValidarDocumento: PostValidarDocumento, val getImpresionUseCase: GetImpresionUseCase, val postCrearTransaccionITD: postCrearTransaccionITD, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase, val getCancelarTransaccionITDUseCase: GetCancelarTransaccionITDUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val mensajeErrorDelServer = MutableLiveData<String>()
    val mensajeDelServer = MutableLiveData<String>()
    val Impresion = MutableLiveData<DTImpresion>()
    val llamarAppFiserv = MutableLiveData<BigDecimal>()
    var TransaccionConsulta = MutableLiveData<ITDRespuesta?>()
    var TransaccionFinalizada = MutableLiveData<DTDocTransaccion>()

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
            mensajeDelServer.postValue("Creando transacción")
            isLoading.postValue(true)
            val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
            if (resultCon.ok) {
                mensajeDelServer.postValue(resultCon.mensaje)
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
                    mensajeDelServer.postValue("Transacción creada")
                    Globales.IDTransaccionActual = result.elemento!!.transaccionId
                    mensajeDelServer.postValue("Abriendo App FISERV")
                    llamarAppFiserv.postValue(
                        BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)
                    )
                } else {
                    mensajeErrorDelServer.postValue(result.mensaje)
                }
            } else {
                mensajeErrorDelServer.postValue(resultCon.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun ConsultarTransaccionITD(nroTransaccion: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
            if (resultCon.ok) {
                mensajeDelServer.postValue(resultCon.mensaje)
                //SI HAY CONEXIÓN CON ITD
                val result = getConsultarTransaccionITDUseCase(nroTransaccion)
                if (result!!.ok) {
                    TransaccionConsulta.postValue(result.elemento!!)
                } else {
                    mensajeErrorDelServer.postValue(result.mensaje)
                }
            }
            isLoading.postValue(false)
        }
    }

    ////////

    fun FinalizarVenta()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
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
                                        mensajeErrorDelServer.postValue(trans.elemento!!.errorMensaje)
                                    }
                                }
                                else
                                {
                                    mensajeErrorDelServer.postValue(trans.mensaje)
                                }
                            }
                        }
                    }
                    else
                    {
                        mensajeErrorDelServer.postValue(result.mensaje)
                    }
                }
                isLoading.postValue(false)
            }
        } catch (e: Exception) {
            isLoading.postValue(false)
        }
    }


}