package com.devhyc.easypos.fiserv

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.device.DeviceService
import com.devhyc.easypos.fiserv.device.IDeviceService
import com.devhyc.easypos.fiserv.presenter.TransactionLauncherPresenter
import com.devhyc.easypos.fiserv.presenter.TransactionPresenter
import com.devhyc.easypos.fiserv.service.TransactionServiceImpl
import com.devhyc.easypos.ui.mediospagos.MediosDePagoFragment
import com.devhyc.easypos.utilidades.AlertView
import com.devhyc.easypos.utilidades.Globales
import com.ingenico.fiservitdapi.transaction.ITransactionDoneListener
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import com.ingenico.fiservitdapi.transaction.data.TransactionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.printer.UPrinter
import java.math.BigDecimal
import java.math.RoundingMode

class FiservITD {

    private var transFiserv: Transaction? = null
    var deviceService:UDeviceService? = null
    private lateinit var service:Intent
    //IMPRESORA
    var printer: UPrinter? = null

    fun InstanciarDeviceService(cnt: Context)
    {
        try
        {
            service = Intent("com.usdk.apiservice")
            service.setPackage("com.usdk.apiservice")
            cnt.bindService(service,object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    deviceService = UDeviceService.Stub.asInterface(service)
                    RegistrarDeviceService(cnt,true)
                    //UTILIZO EL DEVICE SERVICES PARA INSTANCIAR LA IMPRESORA
                    var p = deviceService!!.printer
                    if (p.isBinderAlive)
                    {
                        printer = UPrinter.Stub.asInterface(Globales.fiserv.deviceService!!.printer)
                        Toast.makeText(cnt,"Impresora conectada",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(cnt,"No se pudo conectar la impresora",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onServiceDisconnected(name: ComponentName?) {
                    deviceService = null
                    RegistrarDeviceService(cnt,false)
                }

            },Context.BIND_AUTO_CREATE)
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al instanciar conexión DeviceService!","${e.message}",cnt)
        }
    }

    fun RegistrarDeviceService(cnt:Context,registrar:Boolean)
    {
        try {
            if (registrar)
            {
                deviceService!!.register(null, Binder())
                Toast.makeText(cnt,"Registrando uso del dispositivo",Toast.LENGTH_SHORT).show()
            }
            else
            {
                deviceService!!.unregister(null)
                Toast.makeText(cnt,"Desregistrando uso del dispositivo",Toast.LENGTH_SHORT).show()
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al registrar DeviceService!","${e.message}",cnt)
        }
    }

   /* fun ConectarServicioITD(cnt:Context)
    {
        try
        {
            //transFiserv =  Transaction(cnt)
            //transFiserv?.connectService()
            //ComprobarConexion(cnt)
           // transFiserv.registerTransactionDoneListener(this) // IMPORTANTE!
            //transFiserv.start(transactionInputData)
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al conectar con servicio de ITD","${e.message}",cnt)
        }
    }*/

    /*fun DesconectarServicioITD(cnt:Context)
    {
        try {
            transFiserv?.disconnectService()
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al desconectar con servicio de ITD","${e.message}",cnt)
        }
    }*/

    /*fun ComprobarConexion(cnt:Context)
    {
        try {
            var intentos:Int=0
            while(!transFiserv!!.connected)
            {
                if (intentos < 60)
                {
                    intentos++
                    Thread.sleep(1000)
                }
                else
                {
                    AlertView.showError("¡No se ha podido conectar con el servicio de FiservITD!","Ha superado el tiempo de espera",cnt)
                    break
                }
            }
            if (transFiserv!!.connected)
            {
                //transFiserv?.start(TransactionInputData(TransactionTypes.SALE, BigDecimal(10),null,858,null))
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al procesar pago ITD!","${e.message}",cnt)
        }
    }*/

   /* override fun onTransactionDone(transactionResult: com.ingenico.fiservitdapi.transaction.data.TransactionResult?) {
        GlobalScope.launch(Dispatchers.Main) {
            var hola = transactionResult.toString()
        }
    }*/
}