package com.devhyc.easypos.fiserv.presenter

import android.util.Log
import com.devhyc.easypos.fiserv.device.DeviceApi
import com.devhyc.easypos.fiserv.service.TransactionError
import com.devhyc.easypos.fiserv.service.TransactionService
import com.devhyc.easypos.fiserv.view.TransactionView
import com.devhyc.easypos.ui.mediospagos.MediosDePagoFragment
import com.devhyc.easypos.ui.mediospagoslite.MediosPagosLiteFragment
import com.devhyc.easypos.utilidades.Globales
import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import com.ingenico.fiservitdapi.transaction.data.TransactionResult
import kotlinx.coroutines.*
import java.math.BigDecimal
import kotlin.coroutines.suspendCoroutine

class TransactionPresenter(
    private val transactionView: MediosPagosLiteFragment,
    private val transactionService: TransactionService,
    private val scope: CoroutineScope,
    private val deviceApi: DeviceApi
) : TransactionLauncherPresenter {

    /**
     * Metodo para emepezar a la transaccion, se delega la repsonsabilidad a la clase [transactionService]
     * Se pregunta si el valor es menor o igual a cero para asegurarnos de que no haya problemas
     */

    override fun onConfirmClicked(transactionInputData: TransactionInputData) {
        try {
            if (transactionInputData.amount <= BigDecimal.ZERO) {
                transactionView.showErrorMessage("Invalid amount")
            } else {
                transactionService.setCallback { onTransactionDone(it) } //Se pone primero el callback para ser invocado una vez tenga el resultado
                transactionService.doTransaction(transactionInputData)
            }
        } catch (e: java.lang.Exception) {
            throw Exception(e.message)
            //Log.d("Exception", "Ocurrio un error")
        }
    }

    /**
     * Metodo que es llamado mediante el callback en la clase [TransactionService] cuando la transaccion
     * fue recibida.
     * Una vez llega el resultado se muestra en pantalla el resultado de la transaccion y
     * se manda a imprimir el mensaje */

    override fun onTransactionDone(transactionResult:TransactionResult?){
        transactionResult?.let {
            scope.launch {
                transactionView.showTransactionResult(
                    transactionResult.authorizedAmount.toString(),
                    transactionResult.descriptionCode ?: "",
                    transactionResult.approvalCode ?: ""
                )
                /*deviceApi.printReceipt(
                    "Codigo de aprobacion:${transactionResult.approvalCode?:""} \n " +
                            "Total: ${transactionResult.authorizedAmount?:""} \n " +
                            "Detalle: ${transactionResult.descriptionCode?:""} \n " +
                            getDemoMessage()
                )*/
            }
        }?:throw TransactionError("Algo salió mal al hacer la transacción")
    }

    override fun onExit() {
        transactionService.stopService()
    }


    override fun onTransactionTypeSelected(selected: Int) {
        if (selected != 0) {
            transactionView.showErrorMessage("Not Implemented for this demo")
        }
    }

    override fun onClearDataSelected() {
        //transactionView.clearData()
    }

    private fun getDemoMessage(): String {
        return "*** DEMO DE IMPRESION, SOLO PARA MOSTRAR EL FUNCIONAMIENTO DE LA LIBRERIA ***"
    }
}