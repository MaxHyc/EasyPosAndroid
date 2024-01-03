package com.devhyc.easypos.impresion

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTImpresionCPCL
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.graphics.ZebraImageFactory
import com.zebra.sdk.graphics.ZebraImageI
import com.zebra.sdk.printer.ZebraPrinterFactory
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.Charset

@AndroidEntryPoint
class Impresion : AppCompatActivity() {

    /*fun Print(CodigoCPCL:String,Contexto:Context,copias:Int)
    {
        for (i in 1..copias) {
            Imprimir(CodigoCPCL,Contexto).execute()
        }
    }*/

   /* val array: ByteArray = codigoCPCL.toByteArray(Charset.defaultCharset())
    var mac:String = Globales.sharedPreferences.getString("mac", "").toString()
    conAlt = BluetoothConnection(mac)
    //
    if (conAlt != null)
    {
        if (!conAlt!!.isConnected)
        {
            conAlt!!.open()
        }
        //IMPRIMIR DISEÑO CPCL
        conAlt!!.write(array)
        conAlt!!.close()
    }*/

    fun Print(impresion: DTImpresionCPCL, Contexto:Context)
    {

        for (i in 1..impresion.Vias) {
            Imprimir(impresion,Contexto).execute()
        }
    }

    fun PrintCodigoCPCL(CodigoCPCL:String,Contexto:Context,copias:Int)
    {
        for (i in 1..copias) {
            Imprimir(DTImpresionCPCL("","","","","",1),Contexto).execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Imprimir(private var impresion:DTImpresionCPCL,private var cnt: Context) : AsyncTask<String, String, String>() {

        private val dialogo = ProgressDialog(cnt)

        private fun ImprimirImagen(conexion:BluetoothConnection,imagen:String)
        {
            try {
                var comandos:String = "! U1 setvar \"device.languages\" \"hybrid_xml_zpl\"\r\n"
                conexion!!.write(comandos.toByteArray())
                comandos = "! U1 setvar \"zpl.label_length\" \"260\"\r\n"
                conexion!!.write(comandos.toByteArray())
                comandos = "! UTILITIES\r\nIN-MILIMETERS\r\nSETFF 10 2\r\nPRINT\r\n"
                conexion!!.write(comandos.toByteArray())
                var zebraPrinter = ZebraPrinterFactory.getInstance(conexion)
                val base = com.zebra.sdk.util.internal.Base64.decode(imagen)
                var b = BitmapFactory.decodeByteArray(base, 0, base.size)
                b = b.rotate(180f)
                var i: ZebraImageI = ZebraImageFactory.getImage(b)
                zebraPrinter.printImage(i,200,30,220,220,false)
            }
            catch (e:Exception)
            {
                throw Exception(e.message)
            }
        }

        fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }

        override fun onPreExecute() {
            dialogo.setTitle("Imprimiendo")
            dialogo.setIcon(R.drawable.printer)
            dialogo.setMessage("Aguarde mientras se procesa la impresión")
            dialogo.setCancelable(false)
            dialogo.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            var conAlt: Connection? = null
            var result:String = "OK"
            try
            {
                val mac:String = Globales.sharedPreferences.getString("mac", "").toString()
                var array:ByteArray
                conAlt = BluetoothConnection(mac)
                //
                if (conAlt != null)
                {
                    if (!conAlt!!.isConnected)
                    {
                        //dialogo.setMessage("Conectando con impresora")
                        conAlt!!.open()
                        //dialogo.setMessage("Impresora conectada correctamente")
                        Thread.sleep(500)
                    }
                    //IMPRIMIR DISEÑO PRINCIPAL
                    if (impresion.DocumentoCpcl != null)
                    {
                        //dialogo.setMessage("Imprimiendo")
                        array = impresion.DocumentoCpcl.toByteArray(Charset.defaultCharset())
                        conAlt!!.write(array)
                        Thread.sleep(1000)
                    }
                    //IMPRIMIR QR
                    if (impresion.ImagenQr != null)
                    {
                        //dialogo.setMessage("Imprimiendo QR")
                        ImprimirImagen(conAlt, impresion.ImagenQr!!)
                        Thread.sleep(500)
                    }
                    //IMPRIMIR DATOS DGI
                    if (impresion.DatosDgiCpcl != null)
                    {
                        //dialogo.setMessage("Imprimiendo datos de documento")
                        array = impresion.DatosDgiCpcl!!.toByteArray(Charset.defaultCharset())
                        conAlt!!.write(array)
                        Thread.sleep(500)
                    }
                    //IMPRIMIR FIRMA
                    if (impresion.ImagenFirma != null)
                    {
                        //dialogo.setMessage("Imprimiendo firma")
                        ImprimirImagen(conAlt,impresion.ImagenFirma!!)
                        Thread.sleep(500)
                    }
                    //IMPRIMIR ACLARACION
                    if (impresion.AclaracionFirmaCpcl != null)
                    {
                        //dialogo.setMessage("Imprimiendo aclaración")
                        array = impresion.AclaracionFirmaCpcl!!.toByteArray(Charset.defaultCharset())
                        conAlt!!.write(array)
                        Thread.sleep(500)
                    }
                }
            }
            catch (e:Exception)
            {
                result = e.message.toString()
            }
            finally {
                if (conAlt != null)
                {
                    if(conAlt!!.isConnected)
                    {
                        //dialogo.setMessage("Cerrando conexión con impresora")
                        conAlt!!.close()
                        Thread.sleep(500)
                    }
                }
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            if (dialogo.isShowing)
                dialogo.dismiss()
            if (result == "OK")
            {
                AlertView.showOk("Impresión correcta",result,cnt)
            }
            else
            {
                AlertView.showError("Error al imprimir",result,cnt)
            }
        }
    }
}