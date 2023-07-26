package com.devhyc.easypos.data.network

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.model.*
import com.google.gson.Gson
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ApiService @Inject constructor(private val api:ApiClient,private val apiLogin:ApiControlLogin) {

    suspend fun login(userlogin:DTLoginRequest): Resultado<DTLogin> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTLogin>> = api.login(userlogin)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun loginControl(userlogin: DTLoginRequest): Resultado<DTUserControlLogin>
    {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTUserControlLogin>> = apiLogin.loginusuariosconfig(userlogin)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    /////////CAJAS

    suspend fun getCajaAbierta(nroTerminal:String): Resultado<DTCaja> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCaja>> = api.getCajaAbierta(nroTerminal)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    //INICIAR CAJA

    suspend fun putIniciarCaja(IngresoCaja: DTIngresoCaja): Resultado<DTCaja> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCaja>> = api.putIniciarCaja(IngresoCaja)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    //CERRAR CAJA
    suspend fun postCerrarCaja(nroTerminal:String, totalesDeclarados: DTTotalesDeclarados): Resultado<DTCaja> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCaja>> = api.postCerrarCaja(nroTerminal, totalesDeclarados)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    //ESTADO DE CAJA
    suspend fun getCajaEstado(nroTerminal:String, nroCaja:String,usuario:String): Resultado<DTCajaEstado> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCajaEstado>> = api.getCajaEstado(nroTerminal,nroCaja, usuario)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    //////////////////

    suspend fun getTerminal(nroTerminal:String): Resultado<DTTerminalPos> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTTerminalPos>> = api.getTerminal(nroTerminal)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarArticulos(cantidad:Int,listaprecio:String): Resultado<ArrayList<DTArticulo>>  {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTArticulo>>> = api.getListarArticulos(cantidad,listaprecio)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarArticulosRubros(): Resultado<ArrayList<DTRubro>>  {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTRubro>>> = api.getListarArticulosRubros()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getArticulosFiltrado(cantidad:Int,listaprecio:String,tipoBusqueda:Int,filtro:String): Resultado<ArrayList<DTArticulo>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTArticulo>>> = api.getArticulosFiltrado(cantidad,listaprecio, tipoBusqueda,filtro)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarMediosDePago(): Resultado<ArrayList<DTMedioPago>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTMedioPago>>> = api.getListarMediosDePago()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getParametrosDocumento(usuario:String,tipoDoc:String): Resultado<DTDocParametros> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDocParametros>> = api.getParametrosDocumento(usuario, tipoDoc)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarBancos(): Resultado<ArrayList<DTBanco>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTBanco>>> = api.getListarBancos()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarFinancieras(): Resultado<ArrayList<DTFinanciera>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTFinanciera>>> = api.getListarFinancieras()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListadoClientes(): Resultado<ArrayList<DTCliente>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTCliente>>> = api.getListarClientes()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getClienteXCodigo(codigo:String): Resultado<DTCliente> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCliente>> = api.getClienteXCodigo(codigo)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getClienteXId(id:Long): Resultado<DTCliente> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCliente>> = api.getClienteXId(id)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getProveedorXCodigo(codigo:String): Resultado<DTCliente> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCliente>> = api.getProveedorXCodigo(codigo)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListadoProveedores(): Resultado<ArrayList<DTCliente>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTCliente>>> = api.getListarProveedores()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun postCalcularDocumento(documento:DTDoc): Resultado<DTDocTotales> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDocTotales>> = api.postCalcularDocumento(documento)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getNuevoDocumento(usuario:String, terminal:String,tipoDoc: String): Resultado<DTDocNuevo> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDocNuevo>> = api.getNuevoDocumento(usuario,terminal,tipoDoc)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun postListarDocumentos(parametros:DTParamDocLista): Resultado<List<DTDocLista>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<List<DTDocLista>>> = api.postListarDocumentos(parametros)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListasDeTiposDocumentos(usuario:String): Resultado<DTDocTipos> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDocTipos>> = api.getListasDeTiposDocumentos(usuario)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getFamilias(): Resultado<List<DTFamiliaPadre>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<List<DTFamiliaPadre>>> = api.getFamilias()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListadoDeListasDePrecio(tipoDoc: String): Resultado<ArrayList<DTGenerico>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTGenerico>>> = api.getListarListaDePrecios(tipoDoc)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun postValidarDocumento(documento:DTDoc): Resultado<String> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<String>> = api.postValidarDoc(documento)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListadoDeFuncionarios(funcionarioPerfil: Int): Resultado<ArrayList<DTGenerico>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTGenerico>>> = api.getListarFuncionarios(funcionarioPerfil)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getFuncionarioXId(idFuncionario:Long): Resultado<DTFuncionario> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTFuncionario>> = api.getFuncionarioXId(idFuncionario)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListadoDeDepositos(): Resultado<ArrayList<DTGenerico>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTGenerico>>> = api.getListarDepositos()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getDepositoXCodigo(codigoDeposito:String): Resultado<DTDeposito> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDeposito>> = api.getDepositoPorCodigo(codigoDeposito)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getListarFormasPagos(): Resultado<ArrayList<DTGenerico>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTGenerico>>> = api.getListarFormasPagos()
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getArticuloPorCodigo(codigo:String,listaPrecio: String): Resultado<DTArticulo> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTArticulo>> = if(listaPrecio != "") api.getArticuloPorCodigoConPrecio(codigo,listaPrecio) else api.getArticuloPorCodigoSinPrecio(codigo)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getArticuloPorBarras(codigo:String,listaPrecio: String): Resultado<DTArticulo> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTArticulo>> = if(listaPrecio != "") api.getArticuloPorBarrasConPrecio(codigo,listaPrecio) else api.getArticuloPorBarrasSinPrecio(codigo)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getArticuloPorSerie(codigo:String,listaPrecio: String): Resultado<ArrayList<DTArticulo>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTArticulo>>> = if(listaPrecio != "") api.getArticuloPorSerieConPrecio(codigo,listaPrecio) else api.getArticuloPorSerieSinPrecio(codigo)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

    suspend fun getDocumentoEmitido(terminal:String,tipoDoc: String,nroDoc:String): Resultado<DTDoc> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTDoc>> = api.getDocumentoEmitido(terminal,tipoDoc,nroDoc)
            if (response.isSuccessful)
            {
                response.body()!!
            }
            else
            {
                var s = response.errorBody()?.string().toString()
                val gson = Gson().fromJson(s, Resultado::class.java)
                Resultado(gson.ok,gson.mensaje,null)
            }
        }
    }

}