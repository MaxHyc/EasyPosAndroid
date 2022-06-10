package com.devhyc.easypos.data.model

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
    //
    //Articulos
    var listaarticulos:Resultado<ArrayList<DTArticulo>>? = null
    //Rubros
    var listarrubros: Resultado<ArrayList<DTRubro>>? = null
    //Caja
    var cajaabierta: Resultado<DTCaja>? = null
    //Terminal
    var terminal: Resultado<DTTerminalPos>? = null
}