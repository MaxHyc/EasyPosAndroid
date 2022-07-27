package com.devhyc.easypos.impresion

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
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

    fun Print(CodigoCPCL:String,Contexto:Context,copias:Int)
    {
        for (i in 1..copias) {
            Imprimir(CodigoCPCL,Contexto).execute()
        }
    }

    fun PrintWithSign(CodigoCPCL:String,Imagen:String,Contexto:Context,copias:Int)
    {
        for (i in 1..copias)
        {
            ImprimirConFirma(CodigoCPCL,Imagen,Contexto).execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    class Imprimir(private var codigoCPCL:String,private var cnt: Context) : AsyncTask<String, String, String>() {

        private val dialogo = ProgressDialog(cnt)

        override fun onPreExecute() {
            dialogo.setTitle("Imprimiendo")
            dialogo.setMessage("aguarde unos instantes")
            dialogo.setCancelable(false)
            dialogo.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            var conAlt: Connection? = null
            var result:String = "OK"
            try
            {
                val array: ByteArray = codigoCPCL.toByteArray(Charset.defaultCharset())
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
                }
            }
            catch (e:Exception)
            {
                result = e.message.toString()
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

    @SuppressLint("StaticFieldLeak")
    class ImprimirConFirma(private var codigoCPCL:String, private var imagen:String, private var cnt: Context) : AsyncTask<String, String, String>() {

        fun Bitmap.rotate(degrees: Float): Bitmap {
            val matrix = Matrix().apply { postRotate(degrees) }
            return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        }

        private val dialogo = ProgressDialog(cnt)

        override fun onPreExecute() {
            dialogo.setTitle("Imprimiendo")
            dialogo.setMessage("aguarde unos instantes")
            dialogo.setCancelable(false)
            dialogo.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            var conAlt: Connection? = null
            var result:String = "OK"
            try
            {
                //
                val array: ByteArray = codigoCPCL.toByteArray(Charset.defaultCharset())
                //
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

                    //Setear HYBRID ZPL
                    //var comando:String = "! U1 setvar \"device.languages\" \"hybrid_xml_zpl\"\n\rPRINT"
                    //conAlt!!.write(comando.toByteArray(Charset.defaultCharset()))

                    //COMANDOS
                    //var comandos:String = "! UTILITIES\nIN-MILIMETERS\nSETFF 10 2\nPRINT"
                    //conAlt!!.write(comandos.toByteArray())

                    //Imprimir IMAGEN
                    var zebraPrinter = ZebraPrinterFactory.getInstance(conAlt)
                    val base = com.zebra.sdk.util.internal.Base64.decode(imagen)
                    var b = BitmapFactory.decodeByteArray(base, 0, base.size)
                    b = b.rotate(180f)
                    var i: ZebraImageI = ZebraImageFactory.getImage(b)
                    zebraPrinter.printImage(i,200,200,220,220,false)

                    //CERRAR CONEXION
                    conAlt!!.close()
                }
            }
            catch (e:Exception)
            {
                result = e.message.toString()
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