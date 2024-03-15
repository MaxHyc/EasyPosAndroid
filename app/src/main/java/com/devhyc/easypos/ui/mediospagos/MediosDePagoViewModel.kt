package com.devhyc.easypos.ui.mediospagos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devhyc.easypos.data.model.DTBanco
import com.devhyc.easypos.data.model.DTFinanciera
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.domain.*
import com.devhyc.easypos.fiserv.model.ITDRespuesta
import com.devhyc.easypos.fiserv.model.ITDTransaccionNueva
import com.devhyc.easypos.utilidades.Globales
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class MediosDePagoViewModel @Inject constructor(val getMediosDePagos: GetMediosDePagos, val getConsultarTransaccion: GetConsultarTransaccion, val getListarBancosUseCase: GetListarBancosUseCase, val getListarFinancierasUseCase: GetListarFinancierasUseCase,val postEmitirDocumento: PostEmitirDocumento, val postValidarDocumento: PostValidarDocumento, val getImpresionUseCase: GetImpresionUseCase, val postCrearTransaccionITD: postCrearTransaccionITD, val testDeConexionITDUseCase: GetTestDeConexionITDUseCase, val getConsultarTransaccionITDUseCase: GetConsultarTransaccionITDUseCase, val getCancelarTransaccionITDUseCase: GetCancelarTransaccionITDUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val LMedioPago = MutableLiveData<List<DTMedioPago>>()
    val ColFinancieras = MutableLiveData<ArrayList<DTFinanciera>>()
    val ColBancos = MutableLiveData<ArrayList<DTBanco>>()
    val mensajeErrorDelServer = MutableLiveData<String>()
    val mensajeDelServer = MutableLiveData<String>()
    val Impresion = MutableLiveData<DTImpresion>()
    val llamarAppFiserv = MutableLiveData<BigDecimal>()
    var TransaccionConsulta = MutableLiveData<ITDRespuesta?>()
    val TransaccionCancelada = MutableLiveData<ITDRespuesta?>()

    fun ListarMediosDePago() {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getMediosDePagos()
                if (result != null)
                {
                    if (result.ok)
                    {
                        LMedioPago.postValue(result.elemento!!)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }

    fun ListarBancos()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getListarBancosUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ColBancos.postValue(result.elemento!!)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }

    fun ListarFinancieras()
    {
        try {
            viewModelScope.launch {
                isLoading.postValue(true)
                val result = getListarFinancierasUseCase()
                if (result != null)
                {
                    if (result.ok)
                    {
                        ColFinancieras.postValue(result.elemento!!)
                    }
                }
                isLoading.postValue(false)
            }
        }
        catch (e:Exception)
        {
            isLoading.postValue(false)
        }
    }

    fun CrearTransaccionITD()
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
            if (resultCon.ok)
            {
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
                    Globales.TotalesDocumento.totalImpuestos,
                    Globales.DocumentoEnProceso.valorizado!!.monedaCodigo,
                    Globales.TotalesDocumento.total,
                    Globales.DocumentoEnProceso.valorizado!!.tipoCambio,
                    false,
                    1,
                    0
                )
                if (Globales.DocumentoEnProceso.receptor != null)
                {
                    if (Globales.DocumentoEnProceso.receptor!!.receptorTipoDoc == 0)
                        transaccion.conRut = true
                }
                //
                val result = postCrearTransaccionITD(transaccion)
                if(result!!.ok)
                {
                    Globales.IDTransaccionActual = result.elemento!!.transaccionId
                    llamarAppFiserv.postValue(BigDecimal.valueOf(transaccion.totalPago).scaleByPowerOfTen(2))
                }
                else
                {
                    mensajeErrorDelServer.postValue(result.mensaje)
                }
            }
            else
            {
                mensajeErrorDelServer.postValue(resultCon.mensaje)
            }
            isLoading.postValue(false)
        }
    }

    fun ConsultarTransaccionITD(nroTransaccion:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
            if (resultCon.ok)
            {
                mensajeDelServer.postValue(resultCon.mensaje)
                //SI HAY CONEXIÓN CON ITD
                val result = getConsultarTransaccionITDUseCase(nroTransaccion)
                if(result!!.ok)
                {
                    TransaccionConsulta.postValue(result.elemento!!)
                }
                else
                {
                    mensajeErrorDelServer.postValue(result.mensaje)
                }

            }
            isLoading.postValue(false)
        }
    }

    fun CancelarTransaccionITD(nroTransaccion:String)
    {
        viewModelScope.launch {
            isLoading.postValue(true)
            val resultCon = testDeConexionITDUseCase(Globales.Terminal.Codigo)
            if (resultCon.ok)
            {
                mensajeDelServer.postValue(resultCon.mensaje)
                //SI HAY CONEXIÓN CON ITD
                val result = getCancelarTransaccionITDUseCase(Globales.Terminal.Codigo, nroTransaccion)
                if(result!!.ok)
                {
                    TransaccionCancelada.postValue(result.elemento!!)
                }
                else
                {
                    mensajeErrorDelServer.postValue(result.mensaje)
                }
            }
            isLoading.postValue(false)
        }
    }
}