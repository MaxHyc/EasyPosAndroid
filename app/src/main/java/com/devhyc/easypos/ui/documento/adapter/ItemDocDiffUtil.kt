package com.devhyc.easypos.ui.documento.adapter

import androidx.recyclerview.widget.DiffUtil
import com.devhyc.easypos.data.model.DTDocDetalle

class ItemDocDiffUtil(private val oldList:ArrayList<DTDocDetalle>, private val newList:ArrayList<DTDocDetalle>) :DiffUtil.Callback() {
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