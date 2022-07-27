package com.devhyc.easypos.utilidades;

import android.content.SharedPreferences;

import com.devhyc.easypos.data.model.DTDocItem;
import com.devhyc.easypos.data.model.DTLogin;
import com.devhyc.easypos.data.model.DTMedioPago;
import com.integration.easyposkotlin.data.model.DTCaja;
import com.integration.easyposkotlin.data.model.DTTerminalPos;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Globales {
    public static SharedPreferences sharedPreferences;
    public static String DireccionServidor = "";
    public static DTLogin UsuarioLoggueado;
    public static Boolean CerrarApp=false;
    public static Boolean EnPrincipal=false;
    //TERMINAL
    public static DTTerminalPos Terminal;
    //CAJA
    public static DTCaja CajaActual;
    public static String NroCaja="";
    //HERRAMIENTAS
    public static Herramientas Herramientas=new Herramientas();
    //DATOS DEL DOCUMENTO///////////
    public static ArrayList<DTDocItem> ItemsDeDocumento = new ArrayList<>();
    public static ArrayList<DTMedioPago> MediosPagoDocumento = new ArrayList<>();
    ////////////////////////////////
}
