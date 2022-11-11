package com.devhyc.easypos.data

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.devhyc.easypos.data.model.*
import com.devhyc.easypos.data.network.ApiService
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import javax.inject.Inject

class Repository @Inject constructor(
    private val api: ApiService,
    private val appProvider: AppProvider ) {

    //NUEVO LOGIN COMENTADO
    /*suspend fun login(userlogin:DTLoginRequest): Resultado<DTLogin>
    {
        return try {
            val response = api.login(userlogin)
            if (response.isSuccessful)
            {
                var a = response.body()!!.nombre
                Resultado(true,"",response.body()!!)
            }
            else
            {
                Resultado(false,response.errorBody()?.string().toString(),null)
            }
        }
        catch (e:Exception)
        {
            Resultado(false,e.message.toString(),null)
        }
    }*/

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

    suspend fun getArticulosFiltrado(cantidad:Int,listaprecio:String,tipoBusqueda:Int,filtro:String): Resultado<ArrayList<DTArticulo>> {
        return try {
            val response = api.getArticulosFiltrado(cantidad,listaprecio,tipoBusqueda,filtro)
            appProvider.listaarticulos = response
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

}