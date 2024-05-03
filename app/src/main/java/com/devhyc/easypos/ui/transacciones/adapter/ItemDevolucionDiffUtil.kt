package com.devhyc.easypos.ui.transacciones.adapter

import androidx.recyclerview.widget.DiffUtil
import com.devhyc.easypos.fiserv.model.ITDTransaccionLista

class ItemDevolucionDiffUtil (private val oldList:ArrayList<ITDTransaccionLista>, private val newList:ArrayList<ITDTransaccionLista>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        //return oldList[oldItemPosition] == newList[newItemPosition]
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
        /* return when{
             oldList[oldItemPosition].articuloId != newList[newItemPosition].articuloId -> false
         }*/
    }
}