package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.*
import com.google.gson.Gson
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class ApiService @Inject constructor(private val api:ApiClient) {

    //suspend fun login(userlogin:DTLoginRequest): Response<DTLogin>

    suspend fun login(userlogin:DTLoginRequest): Resultado<DTLogin> {
        return withContext(Dispatchers.IO)
        {
           //api.login(userlogin)
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
            //Resultado de peruba
           /* val lista = ArrayList<DTMedioPago>()
            var l = DTMedioPago("1","Efectivo","EF","1")
            lista.add(l)
            l = DTMedioPago("2","Cheque","CH","2")
            lista.add(l)
            l = DTMedioPago("3","Tarjeta","TJ","3")
            lista.add(l)
            Resultado(true,"", lista)*/
        }
    }

}