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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MediosPagosLiteViewModel @Inject constructor(val nuevoDocumentoUseCase: GetNuevoDocumentoUseCase, val postDevolverDocumentoUseCase: PostDevolverDocumentoUseCase,val getMediosDePagos: GetMediosDePagos, val getConsultarTransaccion: GetConsultarTransaccion, val postEmitirDocumento: PostEmitirDocumento, val postValidarDocumento: PostValidarDocumento, val postCrearTransaccionITDUseCase: postCrearTransaccionITDUseCase, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase, val postValidarTransaccionITDUseCase: PostValidarTransaccionITDUseCase,val postCrearDevolucionITDUseCase: postCrearDevolucionITDUseCase, val postCrearAnulacionITDUseCase: PostCrearAnulacionITDUseCase) : ViewModel() {
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val Impresion = MutableLiveData<DTImpresion>()
    var TransaccionFinalizada = SingleLiveEvent<DTDocTransaccion>()
    var pagos:ArrayList<DTDocPago> = ArrayList()
    var pagoseleccionado:Int = 0
    var transaccionTarjeta = SingleLiveEvent<ITDValidacion>()
    val isLoading = MutableLiveData<Boolean>()
    val compraTarjetaAuxOCheque = SingleLiveEvent<String>()
    val compraMercadoPago = SingleLiveEvent<Boolean>()
    val mostrarErrorServer = SingleLiveEvent<String>()
    val mostrarEstado = SingleLiveEvent<String>()
    val mostrarErrorLocal = SingleLiveEvent<String>()

    fun AgregarMedio(medio:DTMedioPago,monto:Double,esDevolucion: Boolean)
    {
        try
        {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    mostrarEstado.postValue("Agregando medio de pago")
                    pagoseleccionado = medio.Id
                    when (medio.Tipo) {
                        Globales.TMedioPago.TARJETA.codigo.toString() -> {
                            if (medio.Proveedor.isNullOrEmpty())
                            {
                                compraTarjetaAuxOCheque.postValue(medio.Tipo)
                            }
                            else
                            {
                                if (medio.Proveedor == "DOM")
                                {
                                    pagos.add(CrearPagoSimple(monto,pagoseleccionado))
                                    if (esDevolucion)
                                        DevolverVenta()
                                    else
                                        FinalizarVenta()
                                }
                                else
                                {
                                    mostrarEstado.postValue("Agregando pago ${medio.Nombre}")
                                    if (esDevolucion)
                                        ObtenerNroTransaccionITD(medio.ProveedorItd, pagoseleccionado,monto)
                                    else
                                        CrearTransaccionITD(monto,pagoseleccionado)
                                }
                            }
                        }
                        Globales.TMedioPago.MERCADOP.codigo.toString() -> {
                            mostrarEstado.postValue("Agregando pago MercadoPago")
                            compraMercadoPago.postValue(true)
                        }
                        Globales.TMedioPago.CHEQUE.codigo.toString() -> {
                            compraTarjetaAuxOCheque.postValue(medio.Tipo)
                        }
                        else -> {
                            pagos.add(CrearPagoSimple(monto,pagoseleccionado))
                            if (esDevolucion)
                                DevolverVenta()
                            else
                                FinalizarVenta()
                        }
                    }
                    isLoading.postValue(false)
                }
                //isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun CrearPagoSimple(monto:Double,mediopagoid: Int): DTDocPago
    {
            //ADD PAGO CON OTRO MEDIO
            var pago = DTDocPago()
            pago.importe = monto
            pago.tipoCambio = Globales.DocumentoEnProceso.valorizado!!.tipoCambio
            pago.medioPagoCodigo = mediopagoid
            pago.monedaCodigo = Globales.DocumentoEnProceso.valorizado!!.monedaCodigo
            pago.fecha = Globales.DocumentoEnProceso.cabezal!!.fecha
            pago.fechaVto = Globales.DocumentoEnProceso.cabezal!!.fecha
            return pago
    }

    fun ListarMediosDePago() {
        try {
        viewModelScope.launch {
            isLoading.postValue(true)
            withContext(Dispatchers.IO)
            {
                val result = getMediosDePagos()
                if (result != null) {
                    if (result.ok) {
                        LMedioPago.postValue(result.elemento!!)
                    }
                }
            }
            isLoading.postValue(false)
        }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    //FISERV

    fun CrearTransaccionITD(montopago: Double,mediopagoid:Int) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    mostrarEstado.postValue("Creando transacción")
                    val resultCon = testDeConexionITDUseCase(mediopagoid)
                    if (resultCon.ok) {
                        mostrarEstado.postValue(resultCon.mensaje)
                        //SI HAY CONEXIÓN CON ITD
                        //CREO LA TRANSACCION
                        var transaccion = ITDTransaccionNueva(
                            mediopagoid,
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
                            0,
                            false
                        )
                        if (Globales.DocumentoEnProceso.receptor != null) {
                            if (Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc == 0)
                                transaccion.conRut = true
                        }
                        //
                        val result = postCrearTransaccionITDUseCase(transaccion)
                        if (result!!.ok) {
                            mostrarEstado.postValue("Transacción creada")
                            Globales.IDTransaccionActual = result.elemento!!.transaccionId
                            Globales.ProveedorActual = result.elemento!!.Proveedor
                            if (Globales.ImpresionSeleccionada == Globales.eTipoImpresora.FISERV.codigo)
                            {
                                mostrarEstado.postValue("Abriendo App FISERV")
                                Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionCompra(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)))
                            }
                            else
                            {
                                TODO("crear Bucle proceso de itd")
                            }
                        }
                        else
                        {
                            mostrarErrorServer.postValue(result.mensaje)
                        }
                    }
                    else
                    {
                        mostrarErrorServer.postValue(resultCon.mensaje)
                    }
                    isLoading.postValue(false)
                }
                //isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun CrearDevolucionITD(nroTransaccion: String,mediopagoid: Int) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    mostrarEstado.postValue("Creando transacción de devolución")
                    val resultCon = testDeConexionITDUseCase(mediopagoid)
                    if (resultCon.ok) {
                        mostrarEstado.postValue(resultCon.mensaje)
                        //
                        var restDocNuevo = nuevoDocumentoUseCase(Globales.UsuarioLoggueado.usuario,Globales.Terminal.Codigo,Globales.Terminal.Documentos.DevContado)
                        if (restDocNuevo!!.ok)
                        {
                            //OBTENGO NUEVO DOCUMENTO
                            //SI HAY CONEXIÓN CON ITD
                            //CREO LA TRANSACCION
                            var transaccion = ITDTransaccionNueva(
                                mediopagoid,
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
                                0,
                                false
                            )
                            if (Globales.DocumentoEnProceso.receptor != null) {
                                if (Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc == 0)
                                    transaccion.conRut = true
                            }
                            //
                            val result = postCrearDevolucionITDUseCase(transaccion,nroTransaccion)
                            if (result!!.ok) {
                                mostrarEstado.postValue("Transacción de devolucion creada")
                                Globales.IDTransaccionActual = result.elemento!!.transaccionId
                                Globales.ProveedorActual = result.elemento!!.Proveedor
                                if(Globales.ImpresionSeleccionada == Globales.eTipoImpresora.FISERV.codigo)
                                {
                                    mostrarEstado.postValue("Abriendo App FISERV")
                                    Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionDevolucion(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2)))
                                }
                                else
                                {
                                    TODO("Bulcle de devolucion")
                                }
                            }
                            else
                            {
                                mostrarErrorServer.postValue(result.mensaje)
                            }
                        }
                    }
                    else
                    {
                        mostrarErrorServer.postValue(resultCon.mensaje)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun CrearAnulacionITD(nroTransaccion:String,proveedor: String,mediopagoid: Int, ticketPos:Int,acquirerId:Int)
    {
        try
        {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    mostrarEstado.postValue("Creando transacción de anulación")
                    val resultCon = testDeConexionITDUseCase(mediopagoid)
                    if (resultCon.ok) {
                        mostrarEstado.postValue(resultCon.mensaje)
                        //SI HAY CONEXIÓN CON ITD
                        //CREO LA TRANSACCION DE ANULACION
                        var transaccion = ITDTransaccionAnular(
                            nroTransaccion,
                            proveedor,
                            Globales.Terminal.Codigo,
                            Globales.Terminal.Documentos.DevContado,
                            Globales.UsuarioLoggueado.funcionarioId.toString(),
                            Globales.DocumentoEnProceso.complemento!!.codigoSucursal,
                        )
                        //
                        val result = postCrearAnulacionITDUseCase(transaccion)
                        if (result!!.ok) {
                            mostrarEstado.postValue("Transacción de anulación creada")
                            Globales.IDTransaccionActual = result.elemento!!.transaccionId
                            Globales.ProveedorActual = result.elemento!!.Proveedor
                            if(Globales.ImpresionSeleccionada == Globales.eTipoImpresora.FISERV.codigo)
                            {
                                mostrarEstado.postValue("Abriendo App FISERV")
                                Globales.transactionLauncherPresenter.onConfirmClicked(createTransactionAnulacion(BigDecimal.valueOf(Globales.TotalesDocumento.total).scaleByPowerOfTen(2),ticketPos,acquirerId))
                            }
                            else
                            {
                                TODO("Blocle de anulacion")
                            }
                        } else {
                            mostrarErrorServer.postValue(result.mensaje)
                        }
                    } else {
                        mostrarErrorServer.postValue(resultCon.mensaje)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun ConsultarTransaccionITD(nroTransaccion: String,proveedor: String, esDevolucion:Boolean) {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    //SI HAY CONEXIÓN CON ITD
                    var contador=0
                    var tespera=15
                    while(contador<tespera)
                    {
                        mostrarEstado.postValue("Consultando transacción: Intentos $contador de $tespera")
                        val result = getConsultarTransaccionITDUseCase(nroTransaccion,proveedor)
                        if (result!!.ok)
                        {
                            if (result.elemento!!.conError)
                            {
                                if (result.elemento!!.accion == "CANCELAR")
                                {
                                    contador = tespera
                                    mostrarErrorServer.postValue(result.elemento!!.mensaje)
                                }
                                else
                                {
                                    mostrarEstado.postValue("Intentando conectar con FISERV intento $contador de $tespera")
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
                                    mostrarEstado.postValue("Listo para procesar la transacción")
                                    if (esDevolucion)
                                        DevolverVenta()
                                    else
                                        FinalizarVenta()
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
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun ObtenerNroTransaccionITD(proveedor: String,mediopagoid: Int,montopago: Double)
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    mostrarEstado.postValue("COMPROBANDO CONEXIÓN CON FISERV")
                    mostrarEstado.postValue("CONEXIÓN OK")
                    //SI HAY CONEXIÓN CON ITD
                    val result = postValidarTransaccionITDUseCase(
                        ITDValidacionConsulta(
                            proveedor,
                            Globales.DocumentoEnProceso.cabezal!!.terminal,
                            Globales.DocumentoEnProceso.cabezal!!.tipoDocCodigo,
                            Globales.DocumentoEnProceso.cabezal!!.nroDoc,
                            montopago,
                            Globales.DocumentoEnProceso.valorizado!!.monedaCodigo))
                    if (result!!.ok)
                    {
                        mostrarEstado.postValue("NRO TRANSACCIÓN OBTENIDO")
                        var validacion = result!!.elemento
                        //alidacion!!.Proveedor = proveedor
                        validacion!!.MedioPagoId = mediopagoid
                        transaccionTarjeta.postValue(validacion)
                    }
                    else
                    {
                        mostrarErrorServer.postValue(result.mensaje)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
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

    private fun createTransactionAnulacion(monto: BigDecimal, TicketPos:Int,AcquirerId:Int): TransactionInputData {
        return TransactionInputData(
            transactionType = TransactionTypes.VOID,
            monto,
            TicketPos,
            currency = convertToCurrencyType(Globales.currencySelected),
            AcquirerId
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
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
                    isLoading.postValue(true)
                    //AGREGO LOS PAGOS Y SELECCIONO EL DEPOSITO
                    Globales.DocumentoEnProceso.valorizado!!.pagos = pagos
                    Globales.DocumentoEnProceso.complemento!!.codigoDeposito = Globales.Terminal.Deposito
                    //
                    mostrarEstado.postValue("Validando documento")
                    val result = postValidarDocumento(Globales.DocumentoEnProceso)
                    if (result != null) {
                        if (result.ok)
                        {
                            mostrarEstado.postValue("Emitiendo documento")
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
                                            mostrarEstado.postValue("Transacción finalizada")
                                            cont = tiempoEspera
                                        }
                                        else
                                        {
                                            mostrarEstado.postValue("Consultando transacción $cont / $tiempoEspera")
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
                                            mostrarErrorServer.postValue(trans.elemento!!.errorMensaje)
                                        }
                                    }
                                    else
                                    {
                                        mostrarErrorServer.postValue(trans.mensaje)
                                    }
                                }
                            }
                        }
                        else
                        {
                            mostrarErrorServer.postValue(result.mensaje)
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }

    fun DevolverVenta()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                withContext(Dispatchers.IO)
                {
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

                    mostrarEstado.postValue("Devolviendo documento")
                    var trans = postDevolverDocumentoUseCase(docdevolucion)
                    if (trans != null)
                    {
                        //CONSULTO EL ESTADO DE LA TRANSACCION POR ESE NRO DE TRANSACCION
                        if (trans.elemento!!.nroTransaccion.isNotEmpty())
                        {
                            mostrarEstado.postValue("Consultando transacción")
                            var nroTrans = trans.elemento!!.nroTransaccion
                            var cont = 0
                            var tiempoEspera = trans.elemento!!.tiempoEsperaSeg
                            while (cont < tiempoEspera)
                            {
                                //CONSULTO LA TRANSACCION POR EL NUMERO
                                trans = getConsultarTransaccion(nroTrans)!!
                                if (trans.elemento!!.finalizada)
                                {
                                    mostrarEstado.postValue("Transacción finalizada")
                                    cont = tiempoEspera
                                }
                                else
                                {
                                    mostrarEstado.postValue("Consultando transaccion $cont / $tiempoEspera")
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
                                    mostrarErrorServer.postValue(trans.elemento!!.errorMensaje)
                                }
                            }
                            else
                            {
                                mostrarErrorServer.postValue(trans.mensaje)
                            }
                        }
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            mostrarErrorLocal.postValue(e.message.toString())
        }
    }
}