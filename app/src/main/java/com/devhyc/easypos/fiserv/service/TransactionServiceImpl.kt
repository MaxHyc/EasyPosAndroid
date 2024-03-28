package com.devhyc.easypos.fiserv.service

import android.util.Log
import com.ingenico.fiservitdapi.transaction.ITransactionDoneListener
import com.ingenico.fiservitdapi.transaction.Transaction
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import com.ingenico.fiservitdapi.transaction.data.TransactionResult
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Esta clase es la encargada de con el objeto transaction de la libreria hacer la llamada a la aplicacion de pagos
 * y la encargada de recibir el resultado de la transaccion, para ello implementar la interfaz [ITransactionDoneListener]
 * @param transactionApi el objeto que conecta directamente con la libreria de fiserv
 * @param callback utilizado para avisar que ya llego el resultado a la capa de ui
 */
class TransactionServiceImpl(
    private val transactionApi: Transaction,
) : TransactionService, ITransactionDoneListener {

    private var callback: ((t:TransactionResult?) -> Unit)? = null

    /**
     * Preguntar siempre primero si esta conectado y antes de empezar la transaccion registrar la clase
     * que se encargara de recibir el resultado una vez se haya terminado el proceso en la aplicacion de pagos
     * */
    override fun doTransaction(
        transactionInputData: TransactionInputData
    ) {
        if (transactionApi.connected)
        {
            transactionApi.registerTransactionDoneListener(this@TransactionServiceImpl) // IMPORTANTE!!
            transactionApi.start(transactionInputData)
        }
        else
        {
            //throw ServiceIsNotConnected("Transaction Service no está conectado.")
            throw Exception("El servicio no está conectado, aguarde unos instantes y pruebe nuevamente")
        }

    }

    /**
     * Funcion a implementar obligatoriamente por la clase que se encarge de recibir el resultado
     * de la transaccion, en este caso para mantener la arquitectura se usa coroutinas
     * para avisar a la ui que ya llego el resultado
     */
    override fun onTransactionDone(transactionResult: TransactionResult?) {
        callback?.invoke(transactionResult) ?: throw TransactionError("Something went wrong")
    }

    /**
     * Metodo para setear el callbakc que avisara a la ui */
    override fun setCallback(callback: (t:TransactionResult?) -> Unit){
        this.callback =  callback
    }

    /**
     * Se llama a este metodo para eliminar al listener cuando ya no se usa y frenar el servicio
     */
    override fun stopService() {
        transactionApi.unregisterTransactionDoneListener()
        transactionApi.disconnectService()
    }


}

/**
 * Clases de errores personalizados, solo de demostracion
 */

data class TransactionError(val msg: String = "") : Throwable(message = msg)

data class ServiceIsNotConnected(val msg: String = "") : Throwable(message = msg)