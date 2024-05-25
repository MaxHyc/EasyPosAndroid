package com.devhyc.easypos.data.model

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.model.Squareup.Country
import com.integration.easyposkotlin.data.model.DTCaja
import com.integration.easyposkotlin.data.model.DTArticulo
import com.integration.easyposkotlin.data.model.DTTerminalPos
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class AppProvider  @Inject constructor()  {
    lateinit var estado: String
    lateinit var cantidadArticulos: String
    //Rubros
    var login: Resultado<DTLogin>? = null
    var loginControl: Resultado<DTUserControlLogin>? = null
    //
    //Articulos
    var listaarticulos:Resultado<ArrayList<DTArticulo>>? = null
    //Rubros
    var listarrubros: Resultado<ArrayList<DTRubro>>? = null
    //Caja
    var cajaabierta: Resultado<DTCaja>? = null
    //CajaEstado
    var cajaEstado: Resultado<DTCajaEstado>? = null
    //Terminal
    var terminal: Resultado<DTTerminalPos>? = null
    //
    var ParametrosTipoDoc: Resultado<DTDocParametros>? = null
    //
    var listadoPaises: List<Country>? = emptyList()
    //
    var listadoPaisesAMano: List<Country> = listOf(
        Country("URUGUAY","MONTEVIDEO",0),
        Country("ARGENTINA","BUENOS AIRES",0))
}