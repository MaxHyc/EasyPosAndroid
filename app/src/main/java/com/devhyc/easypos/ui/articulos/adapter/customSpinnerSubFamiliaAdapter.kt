package com.devhyc.easypos.ui.articulos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTFamiliaHija

class customSpinnerSubFamiliaAdapter (var context: Context, var subfamilias:ArrayList<DTFamiliaHija>): BaseAdapter() {
    override fun getCount(): Int {
        return subfamilias.count()
    }

    override fun getItem(position: Int): DTFamiliaHija {
        return subfamilias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var  view= LayoutInflater.from(context).inflate(R.layout.item_lista_precio,parent,false)
        var item: TextView =view.findViewById<TextView>(R.id.tvListaPre)
        item.text = subfamilias[position].nombre
        return view
    }
}