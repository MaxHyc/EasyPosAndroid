package com.devhyc.easypos.fiserv.view

interface TransactionView {
    fun showTransactionResult(amount:String?,result:String?,code:String?)
    fun showErrorMessage(message: String)
    fun clearData()
}