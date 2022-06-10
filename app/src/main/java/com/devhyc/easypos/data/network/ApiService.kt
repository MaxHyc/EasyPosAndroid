package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.DTRubro
import com.devhyc.easypos.data.model.Resultado
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.ArrayList
import javax.inject.Inject

class ApiService @Inject constructor(private val api:ApiClient) {

    suspend fun login(userlogin:DTLoginRequest): Resultado<DTLogin> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTLogin>> = api.login(userlogin)
            response.body()!!
        }
    }

    suspend fun getCajaAbierta(nroTerminal:String): Resultado<DTCaja> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTCaja>> = api.getCajaAbierta(nroTerminal)
            response.body()!!
        }
    }

    suspend fun getTerminal(nroTerminal:String): Resultado<DTTerminalPos> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<DTTerminalPos>> = api.getTerminal(nroTerminal)
            response.body()!!
        }
    }

    suspend fun getListarArticulos(cantidad:Int,listaprecio:String): Resultado<ArrayList<DTArticulo>>  {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTArticulo>>> = api.getListarArticulos(cantidad,listaprecio)
            response.body()!!
        }
    }

    suspend fun getListarArticulosRubros(): Resultado<ArrayList<DTRubro>>  {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTRubro>>> = api.getListarArticulosRubros()
            response.body()!!
        }
    }

    suspend fun getArticulosFiltrado(cantidad:Int,listaprecio:String,tipoBusqueda:Int,filtro:String): Resultado<ArrayList<DTArticulo>> {
        return withContext(Dispatchers.IO)
        {
            val response: Response<Resultado<ArrayList<DTArticulo>>> = api.getArticulosFiltrado(cantidad,listaprecio, tipoBusqueda,filtro)
            response.body()!!
        }
    }
}