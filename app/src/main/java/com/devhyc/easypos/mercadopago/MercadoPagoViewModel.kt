package com.devhyc.easypos.mercadopago

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTDocPago
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.mercadopago.model.DtOrdenEstado
import com.devhyc.easypos.mercadopago.model.DtOrdenResultado
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MercadoPagoViewModel @Inject constructor(): ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val mensajeDelServer = MutableLiveData<String>()
    val PagoFinalizado = MutableLiveData<DTDocPago>()
    val EstadoDescripcion = MutableLiveData<String>()
    val MensajeEscaneo = MutableLiveData<String>()
    var ImagenQr = MutableLiveData<String>()
    var BotonCancelar = MutableLiveData<Boolean>()
    //
    private lateinit var mp: MpOrdenes
    private var PROCESANDO: Boolean = false
    private var CANCELADO: Boolean = false
    private var MENSAJE: String = ""
    //
    var OrdenPago: DtOrdenResultado? = null
    var medioPago:DTDocPago? = DTDocPago()
    var nroCaja = "1"

    fun CrearOrden()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
                try {
                    // TODO("CAMBIAR EL NRO DE TERMINA POR EL QUE SERIA, TIENE 1 A FUEGO PORQUE ES EL QUE ESTA CONFIGURADO")
                    //val referencia = "${Globales.DocumentoEnProceso.cabezal!!.terminal}_${Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo}_${Globales.DocumentoEnProceso.cabezal!!.nroDoc}"
                    val referencia = "${nroCaja}_${Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo}_${Globales.DocumentoEnProceso.cabezal!!.nroDoc}"
                    mp = MpOrdenes("TEST-3073957461722958-123012-b127bb8ad688265cab4970bb2ede70f6-1048617206","https://www.hyc.uy:9063/")
                    //VEO SI EXISTE ORDEN ANTERIOR
                    try {
                        EstadoDescripcion.postValue("Consultando si hay orden pendiente")
                        var transaccionPendiente = mp.obtenerOrdenPendiente(nroCaja,Globales.DocumentoEnProceso.complemento!!.codigoSucursal)
                        if (transaccionPendiente != null)
                        {
                            EstadoDescripcion.postValue("Cancelando orden pendiente")
                            mp.cancelarOrden(transaccionPendiente.transaccionId)
                        }
                    }
                    catch (e:Exception)
                    {
                        EstadoDescripcion.postValue("No hay ordenes pendientes")
                    }
                    //CREO LA ORDEN
                    EstadoDescripcion.postValue("Solicitando Orden...")
                    OrdenPago = mp.crearOrden(
                        Globales.DocumentoEnProceso.complemento!!.codigoSucursal,
                        "1",
                        referencia,
                        Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                        Globales.TotalesDocumento.total)!!

                    if (!OrdenPago!!.conError)
                    {
                        //Si no hay error
                        var respuesta: DtOrdenEstado? = null
                        EstadoDescripcion.postValue("Orden creada")
                        PROCESANDO = true
                        BotonCancelar.postValue(true)
                        //BUCLE DE CONSULTA DE LA TRANSACCION
                        while (PROCESANDO) {
                            if (CANCELADO)
                            {
                                BotonCancelar.postValue(false)
                                PROCESANDO = false
                                continue
                            }
                            delay(500)
                            //CONSULTO LA ORDEN
                            respuesta = mp.consultarOrden(OrdenPago!!.transaccionId)
                            //MUESTRO QR
                            MensajeEscaneo.postValue("Escanee el código QR y continue el pago en su teléfono")
                            ImagenQr.postValue(OrdenPago!!.urlQr.toString())

                            //ME QUEDO CONSULTANDO EL ESTADO
                            when(respuesta.estado)
                            {
                                EstadoMercadoPago.Esperando.valor ->
                                {
                                    EstadoDescripcion.postValue(respuesta.mensaje)
                                }
                                EstadoMercadoPago.Finalizado.valor ->
                                {
                                    MensajeEscaneo.postValue("")
                                    BotonCancelar.postValue(false)
                                    PROCESANDO = false
                                }
                                EstadoMercadoPago.Pagando.valor ->
                                {
                                    MensajeEscaneo.postValue("")
                                    BotonCancelar.postValue(false)
                                    EstadoDescripcion.postValue("Pagando")
                                }
                            }
                        }

                        //PROCESO FINALIZADO, ME FIJO EL RESULTADO DE LA TRANSACCION
                        if (!CANCELADO)
                        {
                            if (respuesta!!.resultado!!.estadoResultado == EstadoResultadoMercadoPago.Aprobado.valor)
                            {
                                MENSAJE = respuesta!!.resultado!!.mensaje.toString()
                                medioPago!!.transaccion = respuesta.transaccionId.toString()
                                medioPago!!.autorizacion = respuesta!!.resultado!!.autorizacion.toString()
                            }
                            else
                            {
                                MENSAJE = respuesta!!.resultado!!.mensaje.toString()
                                medioPago = null
                            }
                        }
                        else
                        {
                            medioPago = null
                        }
                    }
                    else
                    {
                        //Si hay Error
                        medioPago = null
                        throw Exception("¡Error al crear la orden!: ${OrdenPago!!.mensaje}")
                    }
                }
                catch (e:Exception)
                {
                    mensajeDelServer.postValue(e.message)
                    medioPago = null
                }
            finally {
                PagoFinalizado.postValue(medioPago)
                isLoading.postValue(false)
            }
        }
    }

    fun cancelarOrdenActual() {
        viewModelScope.launch {
            try {
                BotonCancelar.postValue(false)
                if (OrdenPago != null) {
                    EstadoDescripcion.postValue("Cancelando orden...")
                    mp.cancelarOrden(OrdenPago!!.transaccionId)
                    CANCELADO = true
                    EstadoDescripcion.postValue("Orden cancelada.")
                }
            } catch (ex: Exception) {
                throw Exception("¡Error al cancelar orden MercadoPago! : ${ex.message}")
            }
        }
    }
    /*fun reembolsarCompra() {
        viewModelScope.launch {
            try {
                EstadoDescripcion.postValue("Solicitando devolución...")

                *//*val referencia =
                    "${App.DocumentoActual.Documento.Cabezal.Terminal}_${App.DocumentoActual.Documento.Cabezal.TipoDocCodigo}_${App.DocumentoActual.Documento.Cabezal.NroDoc}"
                OrdenPago = _ordenesMp.ReembolsoDeOrden(
                    App.DocumentoActual.Documento.Complemento.CodigoSucursal,
                    App.Configuration.TerminalCodigo,
                    referencia,
                    App.DocumentoActual.Documento.Valorizado.MonedaCodigo,
                    App.DocumentoActual.Totales.Total,
                    _devolucionTransaccion.toString()
                )*//*

                //IsButtonEnable = Visibility.Visible

               *//* PROCESANDO = true
                val task = Thread { ConsultarOrden() }
                task.start()*//*

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
    }*/
}