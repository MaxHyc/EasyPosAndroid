package com.devhyc.easypos.impresion

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTCajaEstado
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.data.model.DTImpresionCPCL
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.integration.easyposkotlin.data.model.DTCaja
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.printer.*
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.graphics.ZebraImageFactory
import com.zebra.sdk.graphics.ZebraImageI
import com.zebra.sdk.printer.ZebraPrinterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.nio.charset.Charset


class ImpresionFiserv: AppCompatActivity() {

    var dialog: AlertDialog? = null

    fun Print(impresion: DTImpresion, Contexto: Context)
    {
        lifecycleScope.launch {
            try
            {
                if (Globales.fiserv.printer != null)
                {
                    if (Globales.fiserv.printer!!.status == PrinterError.SUCCESS)
                    {
                        Globales.fiserv.printer!!.setPrintFormat(PrintFormat.FORMAT_MOREDATAPROC,PrintFormat.VALUE_MOREDATAPROC_PRNTOEND)
                        //Globales.fiserv.printer!!.setAscScale(ASCScale.SC1x1)
                        //Globales.fiserv.printer!!.setAscSize(ASCSize.DOT24x12)
                        Globales.fiserv.printer!!.setAscScale(ASCScale.SC1x1)
                        Globales.fiserv.printer!!.setAscSize(ASCSize.DOT5x7)
                        Globales.fiserv.printer!!.setXSpace(0)
                        Globales.fiserv.printer!!.setYSpace(1)
                        //IMPRIMIR LOGOS Y TEXTO
                        ProcesarLogoyTextos(impresion, Contexto)
                        //IMPRIMIR QR
                        impresion.imagenes.forEach {
                            if (it.posy != 0)
                            {
                                //Es imagen que no es logo
                                val base = com.zebra.sdk.util.internal.Base64.decode(it.imagen)
                                Globales.fiserv.printer!!.addBmpImage(Globales.fiserv.printer!!.validWidth/2,FactorMode.BMP2X2,base)
                                Globales.fiserv.printer!!.feedLine(3)
                            }
                        }
                        //DEJO UN ESPACIO AL FINAL DE LA IMPRESION
                        Globales.fiserv.printer!!.feedLine(3)
                        //ENVIAR A IMPRIMIR
                        Globales.fiserv.printer!!.startPrint(object : OnPrintListener.Stub()
                        {
                            override fun onFinish() {
                                //AlertView.showOk("Impresión correcta","",Contexto)
                            }

                            override fun onError(p0: Int) {
                                //AlertView.showError("Error al imprimir",p0.toString(),Contexto)
                            }
                        })
                        //MAS PAGINAS IMPRIME VAUCHER
                        if (impresion.masPaginas.isNotEmpty())
                        {
                            Thread.sleep(Globales.TiempoEntreImpresion.toLong())
                            for (v:DTImpresion in impresion.masPaginas)
                            {
                                ProcesarLogoyTextos(v,Contexto)
                                Globales.fiserv.printer!!.feedLine(3)
                                Globales.fiserv.printer!!.startPrint(object :OnPrintListener.Stub()
                                {
                                    override fun onFinish() {
                                        //TODO("Not yet implemented")
                                    }

                                    override fun onError(p0: Int) {
                                        //TODO("Not yet implemented")
                                    }
                                })
                            }
                        }
                    }
                    else
                    {
                        var mensaje:String=""
                        when (Globales.fiserv.printer!!.status)
                        {
                            PrinterError.ERROR_OPENCOVER -> mensaje = "Tapa de impresora abierta"
                            PrinterError.ERROR_BUSY -> mensaje = "Impresora ocupada"
                            PrinterError.ERROR_TIMEOUT -> mensaje = "Timeout"
                            PrinterError.ERROR_PAPERENDED -> mensaje = "Sin papel"
                            PrinterError.ERROR_PAPERJAM -> mensaje = "Papel atascado"
                            PrinterError.ERROR_PRINTER_OPEN_FAILED -> mensaje = "Tapa de impresora abierta"
                            PrinterError.ERROR_OVERHEAT -> mensaje = "Cabezal recalentado"
                            PrinterError.ERROR_BUFOVERFLOW -> mensaje = "Buffer de impresora lleno"
                            PrinterError.ERROR_PRINT_NOT_SUPPORTED -> mensaje = "Impresión no soportada"
                        }
                        AlertView.showError("Error al imprimir",mensaje,Contexto)
                    }
                }
            }
            catch (e:Exception)
            {
                AlertView.showError("Error al imprimir",e.message,Contexto)
            }
        }
    }

    fun ProcesarLogoyTextos(impresion: DTImpresion,Contexto: Context)
    {
        try
        {
            //IMPRIMO EL LOGO
            if (impresion.imagenes.isNotEmpty())
            {
                if (impresion.imagenes[0].posy == 0)
                {
                    //Es el logo
                    val base = com.zebra.sdk.util.internal.Base64.decode(impresion.imagenes[0].imagen)
                    Globales.fiserv.printer!!.addImage(AlignMode.CENTER,base)
                }
            }
            //IMPRIMIR TEXTOS
            impresion.textos.forEach {
                if (it.fuenteSize <= 6)
                {
                    Globales.fiserv.printer!!.setAscSize(ASCSize.DOT24x8)
                }
                else
                {
                    Globales.fiserv.printer!!.setAscSize(ASCSize.DOT24x12)
                }
                when(it.alineacion)
                {
                    Globales.eAlineacionImpresion.CENTER.codigo ->
                    {
                        Globales.fiserv.printer!!.addText(AlignMode.CENTER,it.texto.replace("%","%%"))
                    }
                    Globales.eAlineacionImpresion.LEFT.codigo -> {
                        Globales.fiserv.printer!!.addText(AlignMode.LEFT,it.texto.replace("%","%%"))
                    }
                    Globales.eAlineacionImpresion.RIGHT.codigo -> {
                        Globales.fiserv.printer!!.addText(AlignMode.RIGHT,it.texto.replace("%","%%"))
                    }
                }
            }
            Globales.fiserv.printer!!.feedLine(2)
        }
        catch (e:Exception)
        {
            AlertView.showError("Error al imprimir logo o texto",e.message,Contexto)
        }
    }

    fun PrintPrueba(Contexto: Context)
    {
        lifecycleScope.launch {
            try {
                if (Globales.fiserv.printer != null)
                {
                    if (Globales.fiserv.printer!!.status == PrinterError.SUCCESS)
                    {
                        Globales.fiserv.printer!!.setPrintFormat(PrintFormat.FORMAT_MOREDATAPROC,PrintFormat.VALUE_MOREDATAPROC_PRNTOEND)
                        Globales.fiserv.printer!!.setAscScale(ASCScale.SC1x1)
                        Globales.fiserv.printer!!.setAscSize(ASCSize.DOT7x7)
                        Globales.fiserv.printer!!.setXSpace(0)
                        Globales.fiserv.printer!!.setYSpace(1)
                        Globales.fiserv.printer!!.addText(AlignMode.CENTER,"¡BIENVENIDO A EASY POS!")
                        Globales.fiserv.printer!!.addQrCode(AlignMode.CENTER,240,ECLevel.ECLEVEL_H,"https://www.hyc.uy")
                        Globales.fiserv.printer!!.addText(AlignMode.CENTER,"SI PUEDE LEER ESTO ES QUE SU IMPRESORA ESTA BIEN CONFIGURADA")
                        Globales.fiserv.printer!!.feedLine(5)
                        Globales.fiserv.printer!!.startPrint(object : OnPrintListener.Stub()
                        {
                            override fun onFinish() {
                                //AlertView.showOk("Impresión correcta","",Contexto)
                            }

                            override fun onError(p0: Int) {
                                //AlertView.showError("Error al imprimir",p0.toString(),Contexto)
                            }
                        })
                    }
                    else
                    {
                        AlertView.showAlert("Error al conectar con impresora",Globales.fiserv.printer!!.status.toString(),Contexto)
                    }
                }
            }
            catch (e:Exception)
            {
                AlertView.showError("Error al imprimir",e.message,Contexto)
            }
        }
    }

}