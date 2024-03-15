package com.devhyc.easypos.fiserv.presenter

import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import com.ingenico.fiservitdapi.transaction.data.TransactionResult

interface TransactionLauncherPresenter {

    fun onConfirmClicked(transactionInputData: TransactionInputData)
    fun onTransactionTypeSelected(selected: Int)
    fun onClearDataSelected()
    fun onTransactionDone(transactionResult: TransactionResult?)
    fun onExit()
}