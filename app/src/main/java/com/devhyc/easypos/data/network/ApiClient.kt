package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.DTMedioPago
import com.devhyc.easypos.data.model.Resultado
import com.devhyc.easypos.fiserv.model.*
import com.devhyc.easypos.mercadopago.model.*
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
    @POST("Cajas/iniciar")
    suspend fun postIniciarCaja(@Body IngresoCaja: DTIngresoCaja): Response<Resultado<DTCaja>>
    //CERRAR CAJA
    @POST("Cajas/{terminal_codigo}")
    suspend fun postCerrarCaja(@Path("terminal_codigo") nroterminal: String?,@Body totalesDeclarados: DTTotalesDeclarados): Response<Resultado<DTCaja>>
    //ESTADO DE CAJA
    @GET("Cajas/estado/{terminal_codigo}/{caja_nro}/{usuario_login}")
    suspend fun getCajaEstado(@Path("terminal_codigo") nroTerminal: String?, @Path("caja_nro") nroCaja: String?, @Path("usuario_login") usuario: String?): Response<Resultado<DTCajaEstado>>
    //DOCUMENTOS POR TERMINAL Y CAJA
    @GET("Cajas/documentos/{terminal}/{nrocaja}")
    suspend fun getDocumentosPorTerminalYCaja(@Path("terminal") nroTerminal: String?, @Path("nrocaja") nroCaja:String?): Response<Resultado<List<DTCajaDocumento>>>

    @GET("Impresion/cajainicio/{terminal}/{nrocaja}")
    suspend fun getImpresionInicioCaja(@Path("terminal") nroTerminal: String?, @Path("nrocaja") nroCaja: String?): Response<Resultado<DTImpresion>>

    @GET("Impresion/cajacierre/{terminal}/{nrocaja}/{usuario}")
    suspend fun getImpresionCierreCaja(@Path("terminal") nroTerminal: String?, @Path("nrocaja") nroCaja: String?, @Path("usuario") usuarioLogueado:String): Response<Resultado<DTImpresion>>

    //IMPRESION
    @GET("impresion/documentoipos/{terminal}/{tipodoc}/{nrodoc}")
    suspend fun getImpresion(@Path("terminal") nroTerminal: String?,@Path("tipodoc") tipoDoc: String?,@Path("nrodoc") nroDoc:Long): Response<Resultado<DTImpresion>>

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
    @GET("MedioDePago/pos")
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

    @GET("documentos/posnuevo/{usuario}/{terminal}/{tipoDoc}")
    suspend fun getNuevoDocumento(@Path("usuario") usuario:String,@Path("terminal") terminal:String, @Path("tipoDoc") tipoDoc:String): Response<Resultado<DTDocNuevo>>

    @POST("documentos/calcular")
    suspend fun postCalcularDocumento(@Body documento: DTDoc): Response<Resultado<DTDocTotales>>

    @GET("documentos/tipos/{usuario}")
    suspend fun getListasDeTiposDocumentos(@Path("usuario") usuario:String): Response<Resultado<DTDocTipos>>

    @GET("documentos/pendiente/{terminal}/{tipodoc}/{nrodoc}")
    suspend fun getDocumentoPendiente(@Path("terminal") terminal:String,@Path("tipodoc") tipodoc:String,@Path("nrodoc") nrodoc:String): Response<Resultado<DTDoc>>

    @GET("documentos/emitido/{terminal}/{tipodoc}/{nrodoc}")
    suspend fun getDocumentoEmitido(@Path("terminal") terminal:String,@Path("tipodoc") tipodoc:String,@Path("nrodoc") nrodoc:String): Response<Resultado<DTDoc>>

    @POST("documentos/listar/{terminal}")
    suspend fun postListarDocumentos(@Body filtros: DTParamDocLista,@Path("terminal") terminal: String): Response<Resultado<List<DTDocLista>>>

    @POST("documentos/devolucion")
    suspend fun postDevolverDocumento(@Body docDevolucion: DTDocDevolucion): Response<Resultado<DTDocTransaccion>>

    //
    @GET("articulos/familias")
    suspend fun getFamilias(): Response<Resultado<List<DTFamiliaPadre>>>

    //GUARDAR DOCUMENTO COMO EMITIDO
    @POST("Documentos/emitir")
    suspend fun postEmitirDocumento(@Body documento: DTDoc): Response<Resultado<DTDocTransaccion>>

    //VALIDAR DOCUMENTO
    @POST("Documentos/validar")
    suspend fun postValidarDoc(@Body documento:DTDoc): Response<Resultado<String>>

    //CONSULTAR TRANSACCION
    //Numero,EsCPCL,iPos
    @GET("Documentos/transaccion/{numero}/false/true")
    suspend fun getConsultarTransaccion(@Path("numero") numero: String): Response<Resultado<DTDocTransaccion>>

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

    //FISERVITD
    @GET("itd/configuracion/{nroTerminal}")
    suspend fun getConfiguracionTerminalITD(@Path("nroTerminal") nroTerminal: String): Response<Resultado<ITDConfiguracion>>

    @GET("itd/test/{mediopago}")
    suspend fun getTestDeConexionITD(@Path("mediopago") medioPago: Int): Response<Resultado<Boolean>>

    @POST("itd")
    suspend fun postCrearTransaccionITD(@Body transaccion: ITDTransaccionNueva?): Response<Resultado<ITDRespuesta>>

    @GET("itd/{idTransaccion}/{proveedor}")
    suspend fun getConsultarTransaccionITD(@Path("idTransaccion") nroTransaccion:String,@Path("proveedor") proveedor:String): Response<Resultado<ITDRespuesta>>

    @GET("itd/cancelar/{nroTerminal}/{nroTransaccion}/{proveedor}/{confirm}")
    suspend fun getCancelarTransaccionITD(@Path("nroTerminal") nroTerminal: String, @Path("nroTransaccion") nroTransaccion: String, @Path("proveedor") proveedor:String,@Path("confirm") confirm:Boolean): Response<Resultado<ITDRespuesta>>

    @GET("itd/transacciones/{nroTerminal}/{nroCaja}")
    suspend fun getListarTransacciones(@Path("nroTerminal") nroTerminal: String?, @Path("nroCaja") nroCaja: Long): Response<Resultado<ArrayList<ITDTransaccionLista>>>

    @GET("itd/sinasociar/{terminal}")
    suspend fun getTransaccionesSinAsociarITD(@Path("terminal") nroTerminal: String?): Response<Resultado<ArrayList<ITDTransaccionLista>>>

    @POST("itd/consultar/{nroTransaccion}/{proveedor}")
    suspend fun postConsultarEstadoTransaccion(@Path("nroTransaccion") nroTransaccion: String,@Path("proveedor") proveedor: String ): Response<Resultado<Boolean>>

    @POST("itd/anulacion")
    suspend fun postCrearAnulacionITD(@Body transaccion: ITDTransaccionAnular?): Response<Resultado<ITDRespuesta>>

    @POST("itd/devolucion/{idtransaccion}")
    suspend fun postCrearDevolucionITD(@Body transaccion: ITDTransaccionNueva?, @Path("idtransaccion") idTransaccion:String): Response<Resultado<ITDRespuesta>>

    @POST("itd/validar")
    suspend fun postValidarTransaccionITD(@Body validacionConsulta:ITDValidacionConsulta): Response<Resultado<ITDValidacion>>

}