package com.devhyc.easypos.fiserv.device

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.usdk.apiservice.aidl.UDeviceService
import com.usdk.apiservice.aidl.printer.UPrinter
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private object Config {

    /*
     * Accion del intent.
     */
    const val ACTION_NAME = "com.usdk.apiservice"

    /*
     * Paquete del intent.
     */
    const val PACKAGE_NAME = "com.usdk.apiservice"


    /*
     * Tag para debug
     */
    const val COMMON_LOG = "commonLog"
}

/**
 * Clase para realizar la conexion al usdk para tener acceso a los servicios del dispositivo*/
class DeviceService constructor(private val context: Application) : IDeviceService {

    companion object {
        private val TAG = DeviceService::class.java.simpleName
    }

    private var uDeviceService: UDeviceService? = null

    private var continuationDeviceService: Continuation<ErrorCode>? = null

    /**
     * Callback para cuando el servicio se conecta o se disconecta, esto se llama luego del metodo connect*/
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "SDK service disconnected.")
            try {
                uDeviceService?.unregister(null)
                uDeviceService = null
                continuationDeviceService?.resume(ErrorCode.OK)
            } catch (e: RemoteException) {
                continuationDeviceService?.resume(ErrorCode.ERROR_SERVICE_UNBIND)
            }
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "SDK service connected.")

            try {
                uDeviceService = UDeviceService.Stub.asInterface(service)
                uDeviceService?.register(null, Binder())
                Log.d(TAG, "service registered")
                val bundle = Bundle()
                bundle.putBoolean(Config.COMMON_LOG, true)
                uDeviceService?.debugLog(bundle) //Se pone el bundle con un true para poder ver en consola los mensajes
                continuationDeviceService?.resume(ErrorCode.OK)
            } catch (e: RemoteException) {
                continuationDeviceService?.resume(ErrorCode.ERROR_SERVICE_BIND)
            }

            try {
                linkToDeath(service)
            } catch (e: RemoteException) {
                continuationDeviceService?.resume(ErrorCode.ERROR_SERVICE_BIND)
            }
        }

        @Throws(RemoteException::class)
        private fun linkToDeath(service: IBinder) {
            service.linkToDeath(
                {
                    Log.d(TAG, "SDK service is dead. Reconnecting...")
                    bindSdkDeviceService()
                },
                0
            )
        }
    }

    /**
     * Se hace el bind para iniciar el servicio para que se pueda utilizar el usdk, este debe ser el primer metodo
     * a llamar antes de querer usar los servicios como emv,impresora,etc.*/
    override suspend fun connect(): ErrorCode = suspendCoroutine { continuation ->
        continuationDeviceService = continuation
        if (uDeviceService == null) {
            bindSdkDeviceService()
        } else {
            continuation.resume(ErrorCode.OK)
        }
    }
    /**
     * Se utiliza para liberar el servicio y dejar de utilizarlo*/
    override suspend fun release(): ErrorCode = suspendCoroutine { continuation ->
        continuationDeviceService = continuation
        if (uDeviceService != null) {
            uDeviceService!!.unregister(null)
            unbindDeviceService()
            uDeviceService = null
        } else {
            continuation.resume(ErrorCode.OK)
        }
    }

    private fun bindSdkDeviceService() {
        val intent = Intent().apply {
            action = Config.ACTION_NAME
            `package` = Config.PACKAGE_NAME
        }

        Log.d(TAG, "Binding sdk device service...")
        val isBindService = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (!isBindService) {
            Log.d(TAG, "SDK service binding failed.")
        } else {
            Log.d(TAG, "SDK service binding successfully.")
        }
    }

    private fun unbindDeviceService() {
        context.unbindService(serviceConnection)
        Log.d(TAG, "unbind sdk device service...")
    }

    /**
     * Se debe haber generado la conexion al servicio para poder usar la impresora*/
    override fun getPrinter(): UPrinter? {

        return if (uDeviceService != null) UPrinter.Stub.asInterface(uDeviceService?.printer) else null
    }

    override fun getDeviceService(): UDeviceService? = uDeviceService

}
