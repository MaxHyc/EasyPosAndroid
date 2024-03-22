package com.devhyc.easypos.mercadopago

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.mercadopago.model.DtOrdenEstado
import com.devhyc.easypos.mercadopago.model.DtOrdenResultado
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MercadoPagoViewModel @Inject constructor(): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val mensajeDelServer = MutableLiveData<String>()
    val OrdenCreada = MutableLiveData<DtOrdenResultado>()
    val OrdenConsultada = MutableLiveData<DtOrdenEstado>()
    val EstadoDescripcion = MutableLiveData<String>()
    var ImagenQr = MutableLiveData<String>()
    //
    private lateinit var mp: MpOrdenes
    private var PROCESANDO: Boolean = false
    private var CANCELADO: Boolean = false
    private var MENSAJE: String = ""
    private var MOSTRARQR: Boolean = false
    private var HAYERROR: Boolean = false
    private var _devolucionTransaccion: Long = 0
    //
    lateinit var OrdenPago:DtOrdenResultado
    var medioPago:DTDocPago = DTDocPago()

    fun CrearOrden()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
                try {
                    // TODO("CAMBIAR EL NRO DE TERMINA POR EL QUE SERIA, TIENE 1 A FUEGO PORQUE ES EL QUE ESTA CONFIGURADO")
                    //val referencia = "${Globales.DocumentoEnProceso.cabezal!!.terminal}_${Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo}_${Globales.DocumentoEnProceso.cabezal!!.nroDoc}"
                    val referencia = "1_${Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo}_${Globales.DocumentoEnProceso.cabezal!!.nroDoc}"
                    mp = MpOrdenes("TEST-3073957461722958-123012-b127bb8ad688265cab4970bb2ede70f6-1048617206","https://www.hyc.uy:9063/")
                    EstadoDescripcion.postValue("Solicitando Orden...")
                    val resultadoOrden = mp.crearOrden(
                        Globales.DocumentoEnProceso.complemento!!.codigoSucursal,
                        "1",
                        referencia,
                        Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                        Globales.TotalesDocumento.total)

                    PROCESANDO = true

                    val task = Thread { viewModelScope.launch { ConsultarOrden() }}
                    task.start()

                    while (PROCESANDO) {
                        EstadoDescripcion.postValue(MENSAJE)
                        if (MOSTRARQR) {
                            try {
                                EstadoDescripcion.postValue("Escanee el código QR y continue el pago en su teléfono")
                                ImagenQr.postValue(OrdenPago.urlQr.toString())
                            } catch (e: Exception) {
                                throw Exception("¡Error al mostrar qr de mercadopago!: ${e.message}")
                            }
                        }
                        Thread.sleep(500)
                    }

                    if (CANCELADO) {
                        medioPago = null!!
                    }
                    EstadoDescripcion.postValue(MENSAJE)
                    Thread.sleep(1000)
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

    private suspend fun ConsultarOrden()
    {
            try {
                var respuesta: DtOrdenEstado? = null
                while (PROCESANDO) {
                    respuesta = mp.consultarOrden (OrdenPago.transaccionId)
                    MENSAJE = respuesta.mensaje.toString()
                    if (respuesta.estado == EstadoMercadoPago.Esperando.ordinal) {
                        MOSTRARQR = true
                    }
                    if (respuesta.estado == EstadoMercadoPago.Finalizado.ordinal) {
                        PROCESANDO = false
                    } else {
                        Thread.sleep(1000)
                    }
                }

                respuesta?.resultado?.let {
                    if (it.estadoResultado == EstadoResultadoMercadoPago.Aprobado.ordinal) {
                        MENSAJE = it.mensaje.toString()
                        medioPago.transaccion = respuesta.transaccionId.toString()
                        medioPago.autorizacion = it.autorizacion.toString()
                    } else {
                        MENSAJE = it.mensaje.toString()
                        medioPago = null!!
                    }
                }
            } catch (ex: Exception) {
                MENSAJE = ex.message.toString()
                medioPago = null!!
                PROCESANDO = false
            }
    }

    fun cancelarOrden() {
        viewModelScope.launch {
            try {
                if (OrdenPago != null) {
                    EstadoDescripcion.postValue("Cancelando orden...")
                    mp.cancelarOrden(OrdenPago.transaccionId)
                    EstadoDescripcion.postValue("Orden cancelada.")
                    //IsButtonEnable = Visibility.Hidden
                    CANCELADO = true
                    PROCESANDO = false
                }
            } catch (ex: Exception) {
                //IsButtonEnable = Visibility.Visible
                throw Exception("¡Error al cancelar orden MercadoPago! : ${ex.message}")
            }
        }
    }

    fun reembolsarCompra() {
        viewModelScope.launch {
            try {
                EstadoDescripcion.postValue("Solicitando devolución...")

                /*val referencia =
                    "${App.DocumentoActual.Documento.Cabezal.Terminal}_${App.DocumentoActual.Documento.Cabezal.TipoDocCodigo}_${App.DocumentoActual.Documento.Cabezal.NroDoc}"
                OrdenPago = _ordenesMp.ReembolsoDeOrden(
                    App.DocumentoActual.Documento.Complemento.CodigoSucursal,
                    App.Configuration.TerminalCodigo,
                    referencia,
                    App.DocumentoActual.Documento.Valorizado.MonedaCodigo,
                    App.DocumentoActual.Totales.Total,
                    _devolucionTransaccion.toString()
                )*/

                //IsButtonEnable = Visibility.Visible

               /* PROCESANDO = true
                val task = Thread { ConsultarOrden() }
                task.start()*/

                while (PROCESANDO) {
                    EstadoDescripcion.postValue(MENSAJE)
                    Thread.sleep(500)
                }

                EstadoDescripcion.postValue(MENSAJE)
            } catch (ex: Exception) {
                medioPago = null!!
                throw Exception("¡Error al reembolsar compra de MercadoPago! : ${ex.message}")
            }
        }
    }
}