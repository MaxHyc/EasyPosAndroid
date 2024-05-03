package com.devhyc.easypos.data

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.data.model.Squareup.Country
import com.devhyc.easypos.data.network.ApiService
import com.devhyc.easypos.fiserv.model.*
import com.devhyc.easypos.mercadopago.model.*
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import javax.inject.Inject

class Repository @Inject constructor(
    private val api: ApiService,
    private val appProvider: AppProvider ) {

    suspend fun loginControl(userlogin:DTLoginRequest): Resultado<DTUserControlLogin>
    {
        return try {
            val response = api.loginControl(userlogin)
            appProvider.loginControl = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun login(userlogin:DTLoginRequest): Resultado<DTLogin>
    {
        return try {
             val response = api.login(userlogin)
             appProvider.login = response
             return response
         }
         catch (e:Exception)
         {
             Resultado(false,e.message.toString(),null)
         }
    }

    suspend fun getPaises(): Resultado<List<Country>>
    {
        return try {
            val response = api.getPaises()
            appProvider.listadoPaises = response
            return Resultado(true,"",response)
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(), emptyList())
        }
    }

    //////CAJAS

    suspend fun getCajaAbierta(nroTerminal:String): Resultado<DTCaja> {
       return try {
            val response = api.getCajaAbierta(nroTerminal)
            appProvider.cajaabierta = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    //INICIAR CAJA

    suspend fun postIniciarCaja(IngresoCaja: DTIngresoCaja): Resultado<DTCaja> {
        return try {
            val response = api.postIniciarCaja(IngresoCaja)
            appProvider.cajaabierta = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    //CERRAR CAJA

    suspend fun postCerrarCaja(nroTerminal:String,totalesDeclarados: DTTotalesDeclarados): Resultado<DTCaja> {
        return try {
            val response = api.postCerrarCaja(nroTerminal, totalesDeclarados)
            appProvider.cajaabierta = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    //ESTADO DE CAJA

    suspend fun getCajaEstado(nroTerminal: String,nroCaja:String, usuario:String): Resultado<DTCajaEstado>
    {
        return try {
            val response = api.getCajaEstado(nroTerminal, nroCaja, usuario)
            appProvider.cajaEstado = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    //IMPRIMIR INICIO CAJA

    suspend fun getImpresionInicioCaja(nroTerminal: String,nroCaja:String): Resultado<DTImpresion>
    {
        return try {
            return api.getImpresionInicioCaja(nroTerminal, nroCaja)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    //IMPRIMIR CIERRE CAJA

    suspend fun getImpresionCierreCaja(nroTerminal: String,nroCaja:String,usuario: String): Resultado<DTImpresion>
    {
        return try {
            return api.getImpresionCierreCaja(nroTerminal, nroCaja, usuario)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    ////////////

    suspend fun getTerminal(nroTerminal:String): Resultado<DTTerminalPos> {
        return try {
            val response = api.getTerminal(nroTerminal)
            appProvider.terminal = response
            return response
        } catch (e: Exception) {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListarArticulos(cantidad:Int,listaprecio:String): Resultado<ArrayList<DTArticulo>> {
        return try {
            val response = api.getListarArticulos(cantidad,listaprecio)
            appProvider.listaarticulos = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListarArticulosRubros(): Resultado<ArrayList<DTRubro>> {
        return try {
            val response = api.getListarArticulosRubros()
            appProvider.listarrubros = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getArticulosFiltrado(cantidad:Int,listaPrecio:String,tipo:Int,valorBusqueda:String): Resultado<ArrayList<DTArticulo>>
    {
        return try {
            return api.getArticulosFiltrado(cantidad, listaPrecio, tipo, valorBusqueda)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    ///////////////////////////////////////////////////////////////

    suspend fun getNuevoDocumento(usuario:String,terminal:String,tipoDoc:String): Resultado<DTDocNuevo>
    {
        return try {
            return api.getNuevoDocumento(usuario, terminal, tipoDoc)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getDocumentoEmitido(terminal:String,tipoDoc:String,nroDoc:String): Resultado<DTDoc>
    {
        return try {
            return api.getDocumentoEmitido(terminal, tipoDoc, nroDoc)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postListarDocumentos(parametros:DTParamDocLista,terminal:String): Resultado<List<DTDocLista>>
    {
        return try {
            return api.postListarDocumentos(parametros,terminal)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }


    suspend fun getListaMediosDePago(): Resultado<ArrayList<DTMedioPago>>
    {
        return try {
            return api.getListarMediosDePago()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListaBancos(): Resultado<ArrayList<DTBanco>>
    {
        return try {
            return api.getListarBancos()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListaFinancieras(): Resultado<ArrayList<DTFinanciera>>
    {
        return try {
            return api.getListarFinancieras()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postCalcularDocumento(documento:DTDoc): Resultado<DTDocTotales>
    {
        return try {
            return api.postCalcularDocumento(documento)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListasDeTiposDocumentos(usuario:String): Resultado<DTDocTipos>
    {
        return try {
            return api.getListasDeTiposDocumentos(usuario)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getFamilias(): Resultado<List<DTFamiliaPadre>>
    {
        return try {
            return api.getFamilias()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListadoListasPrecio(tipoDoc: String): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            return api.getListadoDeListasDePrecio(tipoDoc)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postValidarDocumento(documento:DTDoc): Resultado<String>
    {
        return try {
            return api.postValidarDocumento(documento)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListadoDeFuncionarios(funcionarioPerfil: Int): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            return api.getListadoDeFuncionarios(funcionarioPerfil)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getFuncionarioXId(idFuncionario:Long): Resultado<DTFuncionario>
    {
        return try {
            return api.getFuncionarioXId(idFuncionario)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListadoDeDepositos(): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            return api.getListadoDeDepositos()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getDepositoXCodigo(codigoDeposito:String): Resultado<DTDeposito>
    {
        return try {
            return api.getDepositoXCodigo(codigoDeposito)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getArticuloPorCodigo(codigo:String,listaPrecio: String): Resultado<DTArticulo>
    {
        return try {
            return api.getArticuloPorCodigo(codigo, listaPrecio)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getArticuloPorBarras(codigo:String,listaPrecio: String): Resultado<DTArticulo>
    {
        return try {
            return api.getArticuloPorBarras(codigo, listaPrecio)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getArticuloPorSerie(codigo:String,listaPrecio: String): Resultado<ArrayList<DTArticulo>>
    {
        return try {
            return api.getArticuloPorSerie(codigo, listaPrecio)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getParametrosDocumento(usuario:String,tipoDoc:String): Resultado<DTDocParametros>
    {
        return try {
            val response = api.getParametrosDocumento(usuario,tipoDoc)
            appProvider.ParametrosTipoDoc = response
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getClienteXCodigo(codigo:String): Resultado<DTCliente>
    {
        return try {
            return api.getClienteXCodigo(codigo)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getClienteXId(id:Long): Resultado<DTCliente>
    {
        return try {
            return api.getClienteXId(id)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postEmitirDocumento(documento:DTDoc): Resultado<DTDocTransaccion>
    {
        return try {
            return api.postEmitirDocumento(documento)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getConsultarTransaccion(numero:String): Resultado<DTDocTransaccion>
    {
        return try {
            return api.getConsultarTransaccion(numero)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getDocumentosPorTerminalYCaja(nroTerminal: String,nroCaja: String): Resultado<List<DTCajaDocumento>>
    {
        return try {
            return api.getDocumentosPorTerminalYCaja(nroTerminal, nroCaja)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListadoClientes(): Resultado<ArrayList<DTCliente>>
    {
        return try {
            return api.getListadoClientes()
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getImpresion(nroTerminal: String,tipoDoc: String,nroDoc: Long): Resultado<DTImpresion>
    {
        return try {
            return api.getImpresion(nroTerminal, tipoDoc, nroDoc)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    ////FISERV
    suspend fun postCrearTransaccionITD(transaccion:ITDTransaccionNueva): Resultado<ITDRespuesta>
    {
        return try {
            return api.postCrearTransaccionITD(transaccion)
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }
    suspend fun getConfiguracionTerminalITD(nroTerminal: String): Resultado<ITDConfiguracion>
    {
        return try {
            return api.getConfiguracionTerminalITD(nroTerminal)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getTestDeConexionITD(nroTerminal: String): Resultado<Boolean>
    {
        return try {
            return api.getTestDeConexionITD(nroTerminal)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getConsultarTransaccionITD(nroTransaccion: String): Resultado<ITDRespuesta>
    {
        return try {
            return api.getConsultarTransaccionITD(nroTransaccion)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getCancelarTransaccionITD(nroTerminal: String, nroTransaccion: String): Resultado<ITDRespuesta>
    {
        return try {
            return api.getCancelarTransaccionITD(nroTerminal,nroTransaccion)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getListarTransaccionesITD(nroTerminal: String, nroCaja: Long): Resultado<ArrayList<ITDTransaccionLista>>
    {
        return try {
            return api.getListarTransaccionesITD(nroTerminal,nroCaja)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun getTransaccionesSinAsociarITD(nroTerminal: String): Resultado<ArrayList<ITDTransaccionLista>>
    {
        return try {
            return api.getTransaccionesSinAsociarITD(nroTerminal)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postConsultarEstadoTransaccionITD(nroTransaccion: String): Resultado<Boolean>
    {
        return try {
            return api.postConsultarEstadoTransaccionITD(nroTransaccion)
        } catch (e: Exception) {
            Resultado(false, e.message.toString(), null)
        }
    }

    suspend fun postCrearAnulacionITD(transaccion:ITDTransaccionAnular): Resultado<ITDRespuesta>
    {
        return try {
            return api.postCrearAnulacionITD(transaccion)
        } catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postDevolverDocumento(docDevolucion: DTDocDevolucion): Resultado<DTDocTransaccion>
    {
        return try {
            return api.postDevolverDocumento(docDevolucion)
        } catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postValidarTransaccionITD(validarConsulta: ITDValidacionConsulta): Resultado<ITDValidacion>
    {
        return try {
            return api.postValidarTransaccionITD(validarConsulta)
        } catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postCrearDevolucionITD(transaccion:ITDTransaccionNueva,idTransaccion:String): Resultado<ITDRespuesta>
    {
        return try {
            return api.postCrearDevolucionITD(transaccion,idTransaccion)
        } catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }
}