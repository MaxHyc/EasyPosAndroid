package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTTerminalPos
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface ApiClient {
    //LOGIN
    @POST("usuarios/login")
    //suspend fun login(@Body login: DTLoginRequest): Response<DTLogin>
    suspend fun login(@Body login: DTLoginRequest): Response<Resultado<DTLogin>>

    //CAJA
    //CAJA OBTENER CAJA ABIERTA
    @GET("Cajas/abierta/{nroterminal}")
    suspend fun getCajaAbierta(@Path("nroterminal") nroterminal: String?): Response<Resultado<DTCaja>>
    //INICIAR CAJA
    @PUT("Cajas")
    suspend fun putIniciarCaja(@Body IngresoCaja: DTIngresoCaja): Response<Resultado<DTCaja>>
    //CERRAR CAJA
    @POST("Cajas/{terminal_codigo}")
    suspend fun postCerrarCaja(@Path("terminal_codigo") nroterminal: String?,@Body totalesDeclarados: DTTotalesDeclarados): Response<Resultado<DTCaja>>
    //ESTADO DE CAJA
    @GET("Cajas/estado/{terminal_codigo}/{caja_nro}/{usuario_login}")
    suspend fun getCajaEstado(@Path("terminal_codigo") nroTerminal: String?, @Path("caja_nro") nroCaja: String?, @Path("usuario_login") usuario: String?): Response<Resultado<DTCajaEstado>>

    //TERMINAL
    @GET("Cajas/terminal/{nroterminal}")
    suspend fun getTerminal(@Path("nroterminal") terminal: String?): Response<Resultado<DTTerminalPos>>

    //LISTAR ARTICULOS
    @GET("articulos/{cantidad}/{listaprecio}")
    suspend fun getListarArticulos(@Path("cantidad") cantidad: Int, @Path("listaprecio") listaPrecio: String): Response<Resultado<ArrayList<DTArticulo>>>

    //LISTAR ARTICULOS FILTRADO
    @GET("articulos/{cantidad}/{listaprecio}/{tipobusqueda}/{filtro}")
    suspend fun getArticulosFiltrado(@Path("cantidad") cantidad: Int, @Path("listaprecio") listaPrecio: String,@Path("tipobusqueda") tipoBusqueda: Int,@Path("filtro") filtro: String): Response<Resultado<ArrayList<DTArticulo>>>

    //LISTAR ARTICULOS RUBROS
    @GET("articulos/rubros")
    suspend fun getListarArticulosRubros(): Response<Resultado<ArrayList<DTRubro>>>

    //LISTAR MEDIOS DE PAGO
    @GET("MedioDePago")
    suspend fun getListarMediosDePago(): Response<Resultado<ArrayList<DTMedioPago>>>

    //NUEVOS ENDPOINTS
    //Parametros de documento
    @GET("documentos/parametros/{usuario}/{tipoDoc}")
    suspend fun getParametrosDocumento(@Path("usuario") usuario:String,@Path("tipoDoc") tipoDoc:String): Response<Resultado<DTDocParametros>>

    @GET("Bancos")
    suspend fun getListarBancos(): Response<Resultado<ArrayList<DTBanco>>>

    @GET("Financieras")
    suspend fun getListarFinancieras(): Response<Resultado<ArrayList<DTFinanciera>>>

    //DOCUMENTOS

    @GET("documentos/nuevo/{usuario}/{terminal}/{tipoDoc}")
    suspend fun getNuevoDocumento(@Path("usuario") usuario:String,@Path("terminal") terminal:String, @Path("tipoDoc") tipoDoc:String): Response<Resultado<DTDocNuevo>>

    @POST("documentos/calcular")
    suspend fun postCalcularDocumento(@Body documento: DTDoc): Response<Resultado<DTDocTotales>>

    @GET("documentos/tipos/{usuario}")
    suspend fun getListasDeTiposDocumentos(@Path("usuario") usuario:String): Response<Resultado<DTDocTipos>>

    @GET("documentos/pendiente/{terminal}/{tipodoc}/{nrodoc}")
    suspend fun getDocumentoPendiente(@Path("terminal") terminal:String,@Path("tipodoc") tipodoc:String,@Path("nrodoc") nrodoc:String): Response<Resultado<DTDoc>>

    @GET("documentos/emitido/{terminal}/{tipodoc}/{nrodoc}")
    suspend fun getDocumentoEmitido(@Path("terminal") terminal:String,@Path("tipodoc") tipodoc:String,@Path("nrodoc") nrodoc:String): Response<Resultado<DTDoc>>

    @POST("documentos/listar")
    suspend fun postListarDocumentos(@Body filtros: DTParamDocLista): Response<Resultado<List<DTDocLista>>>
    //
    @GET("articulos/familias")
    suspend fun getFamilias(): Response<Resultado<List<DTFamiliaPadre>>>

    //VALIDAR DOCUMENTO
    @POST("Documentos/validar")
    suspend fun postValidarDoc(@Body documento:DTDoc): Response<Resultado<String>>

    //FUNCIONARIOS
    @GET("Funcionarios/listar/{funcionarioperfil}")
    suspend fun getListarFuncionarios(@Path("funcionarioperfil") funcionarioPerfil:Int): Response<Resultado<ArrayList<DTGenerico>>>

    @GET("Funcionarios/{idFuncionario}")
    suspend fun getFuncionarioXId(@Path("idFuncionario") idFuncionario:Long): Response<Resultado<DTFuncionario>>

    @GET("Sucursales/depositos")
    suspend fun getListarDepositos(): Response<Resultado<ArrayList<DTGenerico>>>

    @GET("Sucursales/depositos/{codigo}")
    suspend fun getDepositoPorCodigo(@Path("codigo") codigoDeposito:String): Response<Resultado<DTDeposito>>

    @GET("Clientes/formaspagos")
    suspend fun getListarFormasPagos(): Response<Resultado<ArrayList<DTGenerico>>>

    //ARTICULOS
    //CODIGO INTERNO
    @GET("Articulos/codigo/{codigo}/{listaprecio}")
    suspend fun getArticuloPorCodigoConPrecio(@Path("codigo") codigoArt:String,@Path("listaprecio") listaPrecio:String): Response<Resultado<DTArticulo>>
    @GET("Articulos/codigo/{codigo}")
    suspend fun getArticuloPorCodigoSinPrecio(@Path("codigo") codigoArt:String): Response<Resultado<DTArticulo>>

    //CODIGO DE BARRAS
    @GET("Articulos/barras/{codigo}/{listaprecio}")
    suspend fun getArticuloPorBarrasConPrecio(@Path("codigo") codigoArt:String,@Path("listaprecio") listaPrecio:String): Response<Resultado<DTArticulo>>
    @GET("Articulos/barras/{codigo}")
    suspend fun getArticuloPorBarrasSinPrecio(@Path("codigo") codigoArt:String): Response<Resultado<DTArticulo>>

    //SERIE
    @GET("Articulos/serie/{codigo}/{listaprecio}")
    suspend fun getArticuloPorSerieConPrecio(@Path("codigo") codigoArt:String,@Path("listaprecio") listaPrecio:String): Response<Resultado<ArrayList<DTArticulo>>>
    @GET("Articulos/serie/{codigo}")
    suspend fun getArticuloPorSerieSinPrecio(@Path("codigo") codigoArt:String): Response<Resultado<ArrayList<DTArticulo>>>

    //CLIENTES
    @GET("clientes/{codigo}")
    suspend fun getClienteXCodigo(@Path("codigo") codigo:String): Response<Resultado<DTCliente>>

    @GET("clientes/id/{codigo}")
    suspend fun getClienteXId(@Path("codigo") codigo:Long): Response<Resultado<DTCliente>>

    @GET("clientes/listar")
    suspend fun getListarClientes(): Response<Resultado<ArrayList<DTCliente>>>

    //PROVEEDORES
    @GET("proveedores/{codigo}")
    suspend fun getProveedorXCodigo(@Path("codigo") codigo:String): Response<Resultado<DTCliente>>

    @GET("proveedores/id/{codigo}")
    suspend fun getProveedorXId(@Path("codigo") codigo:Long): Response<Resultado<DTCliente>>

    @GET("proveedores/listar")
    suspend fun getListarProveedores(): Response<Resultado<ArrayList<DTCliente>>>

    //LISTAR LISTA DE PRECIOS
    @GET("ListasPrecios/listar/{tipodoc}")
    suspend fun getListarListaDePrecios(@Path("tipodoc") tipoDoc: String): Response<Resultado<ArrayList<DTGenerico>>>

}