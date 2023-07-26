package com.devhyc.easypos.data

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.data.network.ApiService
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

    suspend fun putIniciarCaja(IngresoCaja: DTIngresoCaja): Resultado<DTCaja> {
        return try {
            val response = api.putIniciarCaja(IngresoCaja)
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
            val response = api.getArticulosFiltrado(cantidad, listaPrecio, tipo, valorBusqueda)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    ///////////////////////////////////////////////////////////////

    suspend fun getNuevoDocumento(usuario:String,terminal:String,tipoDoc:String): Resultado<DTDocNuevo>
    {
        return try {
            val response = api.getNuevoDocumento(usuario,terminal,tipoDoc)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getDocumentoEmitido(terminal:String,tipoDoc:String,nroDoc:String): Resultado<DTDoc>
    {
        return try {
            val response = api.getDocumentoEmitido(terminal,tipoDoc,nroDoc)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postListarDocumentos(parametros:DTParamDocLista): Resultado<List<DTDocLista>>
    {
        return try {
            val response = api.postListarDocumentos(parametros)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }


    suspend fun getListaMediosDePago(): Resultado<ArrayList<DTMedioPago>>
    {
        return try {
            val response = api.getListarMediosDePago()
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListaBancos(): Resultado<ArrayList<DTBanco>>
    {
        return try {
            val response = api.getListarBancos()
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListaFinancieras(): Resultado<ArrayList<DTFinanciera>>
    {
        return try {
            val response = api.getListarFinancieras()
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postCalcularDocumento(documento:DTDoc): Resultado<DTDocTotales>
    {
        return try {
            val response = api.postCalcularDocumento(documento)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListasDeTiposDocumentos(usuario:String): Resultado<DTDocTipos>
    {
        return try {
            val response = api.getListasDeTiposDocumentos(usuario)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getFamilias(): Resultado<List<DTFamiliaPadre>>
    {
        return try {
            val response = api.getFamilias()
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListadoListasPrecio(tipoDoc: String): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            val response = api.getListadoDeListasDePrecio(tipoDoc)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun postValidarDocumento(documento:DTDoc): Resultado<String>
    {
        return try {
            val response = api.postValidarDocumento(documento)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListadoDeFuncionarios(funcionarioPerfil: Int): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            val response = api.getListadoDeFuncionarios(funcionarioPerfil)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getFuncionarioXId(idFuncionario:Long): Resultado<DTFuncionario>
    {
        return try {
            val response = api.getFuncionarioXId(idFuncionario)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getListadoDeDepositos(): Resultado<ArrayList<DTGenerico>>
    {
        return try {
            val response = api.getListadoDeDepositos()
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getDepositoXCodigo(codigoDeposito:String): Resultado<DTDeposito>
    {
        return try {
            val response = api.getDepositoXCodigo(codigoDeposito)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getArticuloPorCodigo(codigo:String,listaPrecio: String): Resultado<DTArticulo>
    {
        return try {
            val response = api.getArticuloPorCodigo(codigo, listaPrecio)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getArticuloPorBarras(codigo:String,listaPrecio: String): Resultado<DTArticulo>
    {
        return try {
            val response = api.getArticuloPorBarras(codigo, listaPrecio)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getArticuloPorSerie(codigo:String,listaPrecio: String): Resultado<ArrayList<DTArticulo>>
    {
        return try {
            val response = api.getArticuloPorSerie(codigo, listaPrecio)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
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
            val response = api.getClienteXCodigo(codigo)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }

    suspend fun getClienteXId(id:Long): Resultado<DTCliente>
    {
        return try {
            val response = api.getClienteXId(id)
            return response
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }



}