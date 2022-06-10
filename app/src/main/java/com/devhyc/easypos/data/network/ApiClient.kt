package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import retrofit2.Response
import retrofit2.http.*
import java.util.ArrayList

interface ApiClient {
    //LOGIN
    @POST("usuarios/login")
    suspend fun login(@Body login: DTLoginRequest): Response<Resultado<DTLogin>>

    //CAJA ABIERTA
    @GET("Cajas/abierta/{nroterminal}")
    suspend fun getCajaAbierta(@Path("nroterminal") nroterminal: String?): Response<Resultado<DTCaja>>

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
}