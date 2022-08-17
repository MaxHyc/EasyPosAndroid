package com.devhyc.easypos.utilidades;

import android.content.SharedPreferences;

import com.devhyc.easypos.data.model.DTDocItem;
import com.devhyc.easypos.data.model.DTLogin;
import com.devhyc.easypos.data.model.DTMedioPago;
import com.devhyc.easypos.impresion.Impresion;
import com.devhyc.easypos.impresion.ImpresionSunMi;
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
    //INSTANCIAS DE IMPRESION///////
    public static Impresion ControladoraDeImpresion=new Impresion();
    public static ImpresionSunMi ControladoraSunMi=new ImpresionSunMi();
    //DATOS DE IMPRESORA////////////
    public static Integer ImpresionSeleccionada=0;
    public static String DireccionMac;

    public enum eTipoImpresora
    {
        BLUETOOTH("BLUETOOTH",0), SUNMI("SUNMI",1);

        private String nombre;
        private int codigo;

        private eTipoImpresora (String nombre, int codigo){
            this.nombre = nombre;
            this.codigo = codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public int getCodigo() {
            return codigo;
        }
    }
}
