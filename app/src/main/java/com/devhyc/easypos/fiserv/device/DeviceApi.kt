package com.devhyc.easypos.fiserv.device

import android.util.Log
import com.devhyc.easypos.data.model.DTImpresion
import com.devhyc.easypos.utilidades.Globales
import com.usdk.apiservice.aidl.printer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
/**
 * Clase que funciona como abstraccion para utilizar el usdk, se encarga de utilizar la impresora
 * para imprimir el ticket*/
class DeviceApi() {

    /**
     * Primero debe conectarse al servicio con el metodo connect, para luego poder obtener una instancia
     * de la impresora, en este caso se setea un formato para el salto de linea, y luego se llama al metodo
     * addText para agregar los datos a imprimir. Despues se llama a startPrint para empezar la impresion, se debe
     * crear una implementacion de listener para saber cuando termino o si hubo un error.*/

    suspend fun printReceipt(impresion: DTImpresion): ResultCode =
        withContext(Dispatchers.IO) {
            Globales.deviceService.connect()
            return@withContext Globales.deviceService.getPrinter()?.let { printer ->
             return@let if(printer.status == ErrorCode.OK.value) {
                    if (printer != null)
                    {
                        if (printer.status == PrinterError.SUCCESS)
                        {
                            printer.setPrintFormat(PrintFormat.FORMAT_MOREDATAPROC,PrintFormat.VALUE_MOREDATAPROC_PRNTOEND)
                            printer.setAscScale(ASCScale.SC1x1)
                            printer.setAscSize(ASCSize.DOT24x12)
                            //IMPRIMO EL LOGO
                            if (impresion.imagenes.isNotEmpty())
                            {
                                if (impresion.imagenes[0].posy == 0)
                                {
                                    //Es el logo
                                    val base = com.zebra.sdk.util.internal.Base64.decode(impresion.imagenes[0].imagen)
                                    printer.addImage(AlignMode.CENTER,base)
                                }
                            }
                            //IMPRIMIR TEXTOS
                            impresion.textos.forEach {
                                if (it.fuenteSize <= 6)
                                {
                                    printer.setAscSize(ASCSize.DOT24x8)
                                }
                                else
                                {
                                    printer.setAscSize(ASCSize.DOT24x12)
                                }
                                when(it.alineacion)
                                {
                                    Globales.eAlineacionImpresion.CENTER.codigo ->
                                    {
                                        printer.addText(AlignMode.CENTER,it.texto.replace("%","%%"))
                                    }
                                    Globales.eAlineacionImpresion.LEFT.codigo -> {
                                        printer.addText(AlignMode.LEFT,it.texto.replace("%","%%"))
                                    }
                                    Globales.eAlineacionImpresion.RIGHT.codigo -> {
                                        printer.addText(AlignMode.RIGHT,it.texto.replace("%","%%"))
                                    }
                                }
                            }
                            //
                            printer.feedLine(2)
                            //IMPRIMIR QR
                            impresion.imagenes.forEach {
                                if (it.posy != 0)
                                {
                                    //Es imagen que no es logo
                                    val base = com.zebra.sdk.util.internal.Base64.decode(it.imagen)
                                    printer.addBmpImage(1,FactorMode.BMP2X2,base)
                                    printer.feedLine(2)
                                }
                            }
                            printer.feedLine(5)
                        }
                    }
                suspendCoroutine {
                    printer.startPrint(object : OnPrintListener.Stub() {
                        override fun onFinish() {
                            Log.d("Printer", "Finished printer")
                            it.resume(ResultCode.OK)
                        }

                        override fun onError(p0: Int) {
                            Log.d("Printer", "Error")
                            it.resume(ResultCode.FAILED)
                        }
                    })
                }
                }else {
                 ResultCode.FAILED
             }
            } ?: ResultCode.FAILED
        }
    }

enum class ResultCode{
    OK,
    FAILED
}