package com.devhyc.easypos.impresion

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import com.devhyc.easypos.R
import com.devhyc.easypos.impresion.util.sunmi.SunmiPrintHelper
import com.devhyc.easypos.utilidades.AlertView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImpresionSunMi: AppCompatActivity() {

    fun ImprimirPaginaDePrueba(Contexto: Context)
    {
        //for (i in 1..copias) {
            PrintPaginaDePrueba("",Contexto).execute()
        //}
    }

    @SuppressLint("StaticFieldLeak")
    class PrintPaginaDePrueba(private var codigoCPCL:String,private var cnt: Context) : AsyncTask<String, String, String>() {

        private val dialogo = ProgressDialog(cnt)

        override fun onPreExecute() {
            dialogo.setTitle("Imprimiendo")
            dialogo.setMessage("aguarde unos instantes")
            dialogo.setCancelable(false)
            dialogo.show()
        }

        override fun doInBackground(vararg p0: String?): String {
            var result:String = "OK"
            try
            {
                var LEFT = 0
                var CENTER = 1
                var RIGHT = 2

                //INSTANCIA DE CONEXION CON SUNMI
                SunmiPrintHelper.getInstance().initSunmiPrinterService(cnt)
                SunmiPrintHelper.getInstance().initPrinter()

                //DATOS DE LA EMPRESA
                //SunmiPrintHelper.getInstance().printBitmap(BitmapFactory.decodeResource(cnt.resources, R.drawable.atencion),1)
                SunmiPrintHelper.getInstance().setAlign(CENTER)
                SunmiPrintHelper.getInstance().printText(
                    "HyC\n",
                    25F,
                    true,
                    false
                )
                SunmiPrintHelper.getInstance().setAlign(CENTER)
                SunmiPrintHelper.getInstance().printText(
                    "HyC Hardware S.A. \n RUT: 214837580016 \n Nicaragua 2260 \n Sucursal: Central\n",
                    20F,
                    false,
                    false
                )
                SunmiPrintHelper.getInstance().setAlign(LEFT)
                SunmiPrintHelper.getInstance().printText(
                    "05/08/2022 \t 12:30\n",
                    15F,
                    false,
                    false
                )
                SunmiPrintHelper.getInstance().setAlign(CENTER)
                SunmiPrintHelper.getInstance().printText(
                    "CONSUMO FINAL\n ",
                    15F,
                    true,
                    true
                )
                SunmiPrintHelper.getInstance().setAlign(LEFT)
                SunmiPrintHelper.getInstance().printText(
                    "NOMBRE DEL ARTICULO IVA BASICO\n",
                    15F,
                    false,
                    false
                )
                //
                /*SunmiPrintHelper.getInstance()
                    .printTable(arrayOf("hola", "mundo"), intArrayOf(10, 15), intArrayOf(10, 20))
                SunmiPrintHelper.getInstance().printTable(
                    arrayOf("hola", "mundo", "programacion", "maximiliano"),
                    intArrayOf(5, 10, 12, 20),
                    intArrayOf(10, 100, 12, 20)
                )
                SunmiPrintHelper.getInstance().printText(
                    "Esto es un texto en tamaño 14 \n segunda linea \n tercera linea ",
                    14F,
                    true,
                    false
                )
                SunmiPrintHelper.getInstance()
                    .printText("Esto es un texto en tamaño 32", 32F, true, false)*/
                SunmiPrintHelper.getInstance().feedPaper()
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