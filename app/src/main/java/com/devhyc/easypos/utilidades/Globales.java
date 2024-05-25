package com.devhyc.easypos.utilidades;

import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin;
import com.devhyc.easypos.data.model.DTDoc;
import com.devhyc.easypos.data.model.DTDocItem;
import com.devhyc.easypos.data.model.DTDocParametros;
import com.devhyc.easypos.data.model.DTDocTotales;
import com.devhyc.easypos.data.model.DTLogin;
import com.devhyc.easypos.data.model.DTMedioPago;
import com.devhyc.easypos.data.model.DTMedioPagoAceptado;
import com.devhyc.easypos.fiserv.FiservITD;
import com.devhyc.easypos.fiserv.device.DeviceApi;
import com.devhyc.easypos.fiserv.device.IDeviceService;
import com.devhyc.easypos.fiserv.presenter.TransactionLauncherPresenter;
import com.devhyc.easypos.impresion.Impresion;
import com.devhyc.easypos.impresion.ImpresionFiserv;
import com.ingenico.fiservitdapi.transaction.Transaction;
import com.integration.easyposkotlin.data.model.DTCaja;
import com.integration.easyposkotlin.data.model.DTTerminalPos;
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2;
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2;
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2;
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2;
import com.sunmi.pay.hardware.aidlv2.system.BasicOptV2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class Globales {
    //
    public static Boolean isEmitido=false;
    //
    public static String CiudadPorDefecto = "Uruguay";
    //Fiserv
    public static FiservITD fiserv = new FiservITD();
    public static String IDTransaccionActual="";
    public static String ProveedorActual="";
    //FISERV
    public static Transaction transactionApi;
    public static TransactionLauncherPresenter transactionLauncherPresenter;
    public static ArrayAdapter<String> currencySpinnerAdapter;
    public static IDeviceService deviceService;
    public static DeviceApi deviceApi;
    public static String currencySelected = "UYU";
    public static Integer TiempoEntreImpresion=0;

    //Fecha
    public static String FechaJson = "yyyy-MM-dd'T'HH:mm:ss";
    public static String Fecha_dd_MM_yyyy_HH_mm_ss= "dd/MM/yyyy HH:mm:ss";
    //
    public static SharedPreferences sharedPreferences;
    public static String DireccionServidor = "";
    public static String DireccionPlexo = "";
    public static DTLogin UsuarioLoggueado;
    public static DTUserControlLogin UsuarioLoggueadoConfig;
    public static Boolean CerrarApp=false;
    public static Boolean EnPrincipal=false;
    //DOCUMENTOS
    public static DTDoc DocumentoEnProceso;
    public static DTDocParametros ParametrosDocumento;
    public static DTDocTotales TotalesDocumento;
    public static String CodigoTipoDocSeleccionado="";
    public static Boolean editando_documento;
    public static int MonedaSeleccionada;
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
    public static ImpresionFiserv ControladoraFiservPrint = new ImpresionFiserv();

    //DATOS DE IMPRESORA////////////
    public static Integer ImpresionSeleccionada=0;
    public static String DireccionMac;

    //VARIABLES DE LECTOR DE TARJETAS
    public static BasicOptV2 mBasicOptV2 = null;
    public static ReadCardOptV2 mReadCardOptV2 = null;
    public static EMVOptV2 mEMVOptV2 = null;
    public static PinPadOptV2 mPinPadOptV2 = null;
    public static SecurityOptV2 mSecurityOptV2 = null;

    //TARJETAS
    public static DTMedioPagoAceptado PagoTarjetaAprobado;

    //CashDRO
    public static String IpCashDro;
    public static String userCashdro;
    public static String passCashdro;
    public static String posIdCashdro;

    public static String Deposito;
    public static String UsuarioAnterior="";
    public static String PassAnterior="";
    public static Boolean SesionViva=false;


    public enum eTipoImpresora
    {
        BLUETOOTH("BLUETOOTH",0), FISERV("FISERV",1);

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

    public enum eAlineacionImpresion
    {
        LEFT("LEFT",1), RIGHT("RIGHT",2), CENTER("CENTER",0);

        private String nombre;
        private int codigo;

        private eAlineacionImpresion (String nombre, int codigo){
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

    public enum TMoneda
    {
        PESOS("PESOS",0), DOLARES("DOLARES",1);

        private String nombre;
        private int codigo;

        private TMoneda (String nombre, int codigo){
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

    public enum TMedioPago
    {
        EFECTIVO("EFECTIVO",1),
        CHEQUE("CHEQUE",2),
        TARJETA("TARJETA",3),
        TICKET("TICKET",4),
        PUNTO("PUNTO",5),
        GIFTCARD("GIFTCARD",6),
        MERCADOP("MERCADO PAGO",7),
        REDONDEO("REDONDEO",9),
        ;

        private String nombre;
        private int codigo;

        private TMedioPago (String nombre, int codigo){
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

    public enum TBusquedaGenerica
    {
        LISTAPRECIO("LISTAPRECIO",1),
        FUNCIONARIO("FUNCIONARIO",2),
        DEPOSITO("DEPOSITO",3),
        FORMAPAGO("FORMAPAGO",4),
        ;

        private String nombre;
        private int codigo;

        private TBusquedaGenerica (String nombre, int codigo){
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

    public enum TTipoBusqueda
    {
        CODIGOBARRAS("CODIGOBARRAS",1),
        CODIGOINTERNO("CODIGOINTERNO",0),
        ;

        private String nombre;
        private int codigo;

        private TTipoBusqueda (String nombre, int codigo){
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

    public enum TTipoMovimientoCaja
    {
        INICIO("INICIO",0),
        INGRESO("INGRESO",1),
        RETIRO("RETIRO",2),
        ;

        private String nombre;
        private int codigo;

        private TTipoMovimientoCaja (String nombre, int codigo){
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

    public enum TProveedorTarjeta
    {
        FISERV("GEOCOM","GEOCOM"),
        HANDY("HANDY","HANDY"),
        GETNET("GETNET","GETNET"),
        OCA("OCA","OCA")
        ;

        private String nombre;
        private String valor;

        private TProveedorTarjeta (String nombre, String valor){
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public String getValor() {
            return valor;
        }
    }

}
