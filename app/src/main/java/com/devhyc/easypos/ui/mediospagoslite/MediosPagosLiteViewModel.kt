package com.devhyc.easypos.ui.mediospagoslite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.fiserv.model.ITDTransaccionAnular
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import com.devhyc.easypos.fiserv.model.ITDValidacion
import com.devhyc.easypos.fiserv.model.ITDValidacionConsulta
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.devhyc.easypos.utilidades.SingleLiveEvent
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MediosPagosLiteViewModel @Inject constructor(val nuevoDocumentoUseCase: GetNuevoDocumentoUseCase, val postDevolverDocumentoUseCase: PostDevolverDocumentoUseCase,val getMediosDePagos: GetMediosDePagos, val getConsultarTransaccion: GetConsultarTransaccion, val postEmitirDocumento: PostEmitirDocumento, val postValidarDocumento: PostValidarDocumento, val postCrearTransaccionITDUseCase: postCrearTransaccionITDUseCase, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase, val postValidarTransaccionITDUseCase: PostValidarTransaccionITDUseCase,val postCrearDevolucionITDUseCase: postCrearDevolucionITDUseCase, val postCrearAnulacionITDUseCase: PostCrearAnulacionITDUseCase) : ViewModel() {
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val Impresion = MutableLiveData<DTImpresion>()
    var TransaccionFinalizada = MutableLiveData<DTDocTransaccion>()
    var pagos:ArrayList<DTDocPago> = ArrayList()
    var pagoseleccionado:Int = 0
    var transaccionTarjeta = MutableLiveData<ITDValidacion>()
    val isLoading = MutableLiveData<Boolean>()
    //
    private val _compraMercadoPago = SingleLiveEvent<Boolean>()
    val compraMercadoPago: LiveData<Boolean> = _compraMercadoPago
    fun compraMercadoPago(value:Boolean)
    {
        _compraMercadoPago.value = value
    }
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
    //
    fun AgregarMedio(medio:DTMedioPago,monto:Double,esDevolucion: Boolean)
    {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                mostrarEstado("Agregando medio de pago")
                pagoseleccionado = medio.Id.toInt()
                when (medio.Tipo) {
                    Globales.TMedioPago.TARJETA.codigo.toString() -> {
                        if (medio.Proveedor == "GEOCOM") {
                            mostrarEstado("Agregando pago FISERV")
                            if (esDevolucion)
                                ObtenerNroTransaccionITD(medio.Proveedor, monto)
                            else
                                CrearTransaccionITD(monto)
                        }
                    }
                    Globales.TMedioPago.MERCADOP.codigo.toString() -> {
                        mostrarEstado("Agregando pago MercadoPago")
                        compraMercadoPago(true)
                    }
                    else -> {
                        //ADD PAGO CON OTRO MEDIO
                        var pago = DTDocPago()
                        pago.importe = monto
                        pago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                        pago.medioPagoCodigo = pagoseleccionado
                        pago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
                        pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
                        pago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
                        pagos.add(pago)
                        if (esDevolucion)
                            DevolverVenta()
                        else
                            FinalizarVenta()
                    }
                }

            } catch (e: Exception) {
                mostrarErrorLocal(e.message.toString())
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun ListarMediosDePago() {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                val result = getMediosDePagos()
                if (result != null) {
                    if (result.ok) {
                                    LMedioPago.postValue(result.elemento!!)
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

    //FISERV

    fun CrearTransaccionITD(montopago: Double) {
        isLoading.postValue(true)
        viewModelScope.launch {
            try
            {
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
                    val result = postCrearTransaccionITDUseCase(transaccion)
                    if (result!!.ok) {
                        mostrarEstado("Transacción creada")
                        Globales.IDTransaccionActual = result.elemento!!.transaccionId
                        mostrarEstado("Abriendo App FISERV")
                        Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionCompra(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)))
                    }
                    else
                    {
                        mostrarErrorServer(result.mensaje)
                    }
                }
                else
                {
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

    fun CrearDevolucionITD(nroTransaccion: String) {
        isLoading.postValue(true)
        viewModelScope.launch {
            try
            {
                mostrarEstado("Creando transacción de devolución")
                val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                if (resultCon.ok) {
                    mostrarEstado(resultCon.mensaje)
                    //
                    var restDocNuevo = nuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario,Globales.Terminal.Codigo,Globales.Terminal.Documentos.DevContado)
                    if (restDocNuevo!!.ok)
                    {
                        //OBTENGO NUEVO DOCUMENTO
                        //SI HAY CONEXIÓN CON ITD
                        //CREO LA TRANSACCION
                        var transaccion = ITDTransaccionNueva(
                            Globales.Terminal.Codigo,
                            Globales.Terminal.Documentos.DevContado,
                            restDocNuevo.elemento!!.documento.cabezal!!.nroDoc,
                            Globales.Terminal.SucursalDoc,
                            Globales.UsuarioLoggueado.funcionarioId.toString(),
                            Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                            Globales.TotalesDocumento.total,
                            Globales.TotalesDocumento.subtotalGravadoConDto,
                            Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                            Globales.TotalesDocumento.total,
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
                        val result = postCrearDevolucionITDUseCase(transaccion,nroTransaccion)
                        if (result!!.ok) {
                            mostrarEstado("Transacción de devolucion creada")
                            Globales.IDTransaccionActual = result.elemento!!.transaccionId
                            mostrarEstado("Abriendo App FISERV")
                            Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionDevolucion(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)))
                        } else {
                            mostrarErrorServer(result.mensaje)
                        }
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

    fun CrearAnulacionITD(nroTransaccion:String)
    {
        isLoading.postValue(true)
        viewModelScope.launch {
            try
            {
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
                        Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionAnulacion(
                            BigDecimal.valueOf(Globales.TotalesDocumento.total).scaleByPowerOfTen(2)))
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

    fun ConsultarTransaccionITD(nroTransaccion: String, esDevolucion:Boolean) {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
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
                                if (result.elemento!!.accion == "CANCELAR")
                                {
                                    contador = tespera
                                    mostrarErrorServer(result.elemento!!.mensaje)
                                }
                                else
                                {
                                    mostrarEstado("Intentando conectar con FISERV intento $contador de $tespera")
                                    contador += 1
                                    Thread.sleep(1000)
                                }
                            }
                            else
                            {
                                contador = tespera
                                if (result.elemento!!.pago != null)
                                {
                                    result.elemento!!.pago!!.medioPagoCodigo = pagoseleccionado
                                    result.elemento!!.pago!!.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
                                    //AGREGO EL PAGO DE FISERV
                                    pagos.add(result.elemento!!.pago!!)
                                    mostrarEstado("Listo para procesar la transacción")
                                    if (esDevolucion)
                                        DevolverVenta()
                                    else
                                        FinalizarVenta()
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

    fun ObtenerNroTransaccionITD(proveedor: String,montopago: Double)
    {
        isLoading.postValue(true)
        viewModelScope.launch {
            try {
                mostrarEstado("COMPROBANDO CONEXIÓN CON FISERV")
                val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
                if (resultCon.ok) {
                    mostrarEstado("CONEXIÓN OK")
                    //SI HAY CONEXIÓN CON ITD
                    val result = postValidarTransaccionITDUseCase(
                        ITDValidacionConsulta(proveedor,
                            Globales.DocumentoEnProceso.cabezal!!.terminal,
                            Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo,
                            Globales.DocumentoEnProceso.cabezal!!.nroDoc,
                            montopago,
                            Globales.DocumentoEnProceso.valorizado!!.monedaCodigo))
                    if (result!!.ok)
                    {
                        mostrarEstado("NRO TRANSACCIÓN OBTENIDO")
                        transaccionTarjeta.postValue(result!!.elemento)
                    }
                    else
                    {
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

    private fun createTransactionCompra(monto: BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.SALE,
            monto,
            null,
            currency = convertToCurrencyType(Globales.currencySelected),
            null
        )
    }

    private fun createTransactionDevolucion(monto: BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.REFUND,
            monto,
            null,
            currency = convertToCurrencyType(Globales.currencySelected),
            null
        )
    }

    private fun createTransactionAnulacion(monto: BigDecimal): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.VOID,
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

    ///MANAGEMENT

    fun FinalizarVenta()
    {
            viewModelScope.launch {
                try {
                    isLoading.postValue(true)
                    //AGREGO LOS PAGOS Y SELECCIONO EL DEPOSITO
                    Globales.DocumentoEnProceso.valorizado!!.pagos = pagos
                    Globales.DocumentoEnProceso.complemento!!.codigoDeposito = Globales.Terminal.Deposito
                    //
                    mostrarEstado("Validando documento")
                    val result = postValidarDocumento(Globales.DocumentoEnProceso)
                    if (result != null) {
                        if (result.ok)
                        {
                            mostrarEstado("Emitiendo documento")
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
                                            mostrarEstado("Transacción finalizada")
                                            cont = tiempoEspera
                                        }
                                        else
                                        {
                                            mostrarEstado("Consultando transacción $cont / $tiempoEspera")
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
                catch (e:Exception)
                {
                    mostrarErrorLocal(e.message.toString())
                }
                finally {
                    isLoading.postValue(false)
                }
            }
    }

    fun DevolverVenta()
    {
        viewModelScope.launch {
            try {
                isLoading.postValue(true)
                var docdevolucion = DTDocDevolucion(
                    Globales.UsuarioLoggueado.usuario,
                    Globales.Terminal.Codigo,
                    Globales.Terminal.Documentos.DevContado,
                    Globales.DocumentoEnProceso.cabezal!!.terminal,
                    Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo,
                    Globales.DocumentoEnProceso.cabezal!!.nroDoc,
                    true,
                    null,
                    pagos)

                mostrarEstado("Devolviendo documento")
                var trans = postDevolverDocumentoUseCase(docdevolucion)
                if (trans != null)
                {
                    //CONSULTO EL ESTADO DE LA TRANSACCION POR ESE NRO DE TRANSACCION
                    if (trans.elemento!!.nroTransaccion.isNotEmpty())
                    {
                        mostrarEstado("Consultando transacción")
                        var nroTrans = trans.elemento!!.nroTransaccion
                        var cont = 0
                        var tiempoEspera = trans.elemento!!.tiempoEsperaSeg
                        while (cont < tiempoEspera)
                        {
                            //CONSULTO LA TRANSACCION POR EL NUMERO
                            trans = getConsultarTransaccion(nroTrans)!!
                            if (trans.elemento!!.finalizada)
                            {
                                mostrarEstado("Transacción finalizada")
                                cont = tiempoEspera
                            }
                            else
                            {
                                mostrarEstado("Consultando transaccion $cont / $tiempoEspera")
                                cont += 1
                                Thread.sleep(1000)
                            }
                        }
                        if(trans!!.ok)
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