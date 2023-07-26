package com.devhyc.easypos.ui.articulos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.devhyc.easypos.R
import com.devhyc.easypos.data.model.DTFamiliaPadre

class customSpinnerAdapter (var context: Context, var familias:ArrayList<DTFamiliaPadre>): BaseAdapter() {
    override fun getCount(): Int {
        return familias.count()
    }

    override fun getItem(position: Int): DTFamiliaPadre {
        return familias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var  view= LayoutInflater.from(context).inflate(R.layout.item_lista_precio,parent,false)
        var item: TextView =view.findViewById<TextView>(R.id.tvListaPre)
        item.text = familias[position].nombre
        return view
    }
}