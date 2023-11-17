package com.devhyc.easypos.fiserv

import android.content.Context
import com.devhyc.easypos.utilidades.AlertView
import com.ingenico.fiservitdapi.transaction.ITransactionDoneListener
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.constants.TransactionTypes
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

class FiservITD: ITransactionDoneListener {

    private var libFiserv: Transaction? = null

    fun ConectarServicioITD(cnt:Context)
    {
        try
        {
            libFiserv = cnt.let { Transaction(cnt) }
            libFiserv?.connectService()
            Thread.sleep(2000)
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al conectar con servicio de ITD","${e.message}",cnt)
        }
    }

    fun DesconectarServicioITD(cnt: Context)
    {
        try {
            libFiserv?.disconnectService()
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al desconectar con servicio de ITD","${e.message}",cnt)
        }
    }

    fun ProcesarTransaccionITD(cnt: Context)
    {
        try {
            var intentos:Int=0
            while(!libFiserv!!.connected)
            {
                if (intentos < 10)
                {
                    libFiserv?.connectService()
                    intentos++
                    Thread.sleep(1000)
                }
                else
                {
                    AlertView.showError("¡No se ha podido conectar con el servicio de FiservITD!","Ha superado el tiempo de espera",cnt)
                    break
                }
            }
            if (libFiserv!!.connected)
            {
                libFiserv?.start(TransactionInputData(TransactionTypes.SALE, BigDecimal(10),null,858,null))
            }
        }
        catch (e:Exception)
        {
            AlertView.showAlert("¡Error al procesar pago ITD!","${e.message}",cnt)
        }
    }

    override fun onTransactionDone(transactionResult: com.ingenico.fiservitdapi.transaction.data.TransactionResult?) {
        GlobalScope.launch(Dispatchers.Main) {
            var hola = transactionResult.toString()
        }
    }
}