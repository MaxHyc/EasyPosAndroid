package com.devhyc.easypos.utilidades;

import android.content.SharedPreferences;

import com.devhyc.easypos.data.model.DTLogin;
import com.integration.easyposkotlin.data.model.DTCaja;
import com.integration.easyposkotlin.data.model.DTTerminalPos;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Globales {
    public static SharedPreferences sharedPreferences;
    public static String DireccionServidor = "";
    public static DTLogin UsuarioLoggueado;
    public static String NroCaja="";
    public static Boolean CerrarApp=false;
    public static Boolean EnPrincipal=false;
    //TERMINAL
    public static DTTerminalPos Terminal;
    //
    public static DTCaja CajaActual;
    //Listar Articulos
    public static Integer CantidadAListar=10;
    public static String ListaDePrecioAListar="";
    //
    public static Herramientas Herramientas=new Herramientas();
}
