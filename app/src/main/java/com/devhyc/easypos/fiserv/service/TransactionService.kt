package com.devhyc.easypos.fiserv.service

import com.ingenico.fiservitdapi.transaction.data.TransactionInputData
import com.ingenico.fiservitdapi.transaction.data.TransactionResult
import kotlin.coroutines.Continuation


interface TransactionService {
    fun doTransaction(transactionInputData: TransactionInputData)
    fun stopService()
    fun setCallback(callback: (t:TransactionResult?) -> Unit)
}